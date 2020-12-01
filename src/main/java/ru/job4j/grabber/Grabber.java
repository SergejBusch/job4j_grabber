package ru.job4j.grabber;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class Grabber implements Grab {
    private final Properties cfg = new Properties();

    public Store store() {
        return new PsqlStore(cfg);
    }

    public Scheduler scheduler() throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        return scheduler;
    }

    public void cfg() throws IOException {
        try (var in = Grabber.class.getClassLoader()
                .getResourceAsStream("app.properties")) {
           cfg.load(in);
        }
    }

    @Override
    public void init(Parse parse, Store store, Scheduler scheduler)
            throws SchedulerException {
        JobDataMap data = new JobDataMap();
        data.put("store", store);
        data.put("parse", parse);
        JobDetail job = newJob(GrabJob.class)
                .usingJobData(data)
                .build();
        SimpleScheduleBuilder times = simpleSchedule()
                .withIntervalInSeconds(Integer.parseInt(cfg.getProperty("time")))
                .repeatForever();
        Trigger trigger = newTrigger()
                .startNow()
                .withSchedule(times)
                .build();
        scheduler.scheduleJob(job, trigger);
    }

    public static class GrabJob implements Job {

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
          JobDataMap map = context.getJobDetail().getJobDataMap();
          Store store = (Store) map.get("store");
          Parse parse = (Parse) map.get("parse");
          var parseList = parse.list("https://www.sql.ru/forum/job-offers");
          var storeList = store.getAll();
          for (Post parsePost : parseList) {
              if (!storeList.contains(parsePost)) {
                  store.save(parsePost);
              }
          }
        }
    }

    public static void main(String[] args) throws IOException, SchedulerException {
        var grabber = new Grabber();
        grabber.cfg();
        var scheduler = grabber.scheduler();
        var store = grabber.store();
        grabber.init(new SqlRuParse(), store, scheduler);
        grabber.web(store);
    }

    public void web(Store store) {
        new Thread(() -> {
            try (ServerSocket server = new ServerSocket(
                    Integer.parseInt(cfg.getProperty("port")))) {
                while (!server.isClosed()) {
                    Socket socket = server.accept();
                    try (OutputStream out = socket.getOutputStream()) {
                        out.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
                        for (Post post : store.getAll()) {
                            out.write(post.toString().getBytes(StandardCharsets.UTF_16));
                            out.write(System.lineSeparator().getBytes(StandardCharsets.UTF_16));
                        }
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
