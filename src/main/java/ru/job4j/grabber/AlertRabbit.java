package ru.job4j.grabber;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class AlertRabbit {
    private static Properties properties;

    public static void main(String[] args) {
        properties = readProperties();
        int interval = properties != null
                ? Integer.parseInt((String) properties.get("rabbit.properties"))
                : 2;


        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDetail job = newJob(Rabbit.class).build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(interval)
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    private static Properties readProperties() {
        try (var inputStream = AlertRabbit.class.getClassLoader()
                .getResourceAsStream("rabbit.properties")) {
            var properties = new Properties();
            properties.load(inputStream);
            return properties;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static class Rabbit implements Job {

        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            System.out.println("Follow white rabbit");
        }
    }
}
