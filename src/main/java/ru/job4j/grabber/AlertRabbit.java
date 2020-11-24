package ru.job4j.grabber;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class AlertRabbit {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        var ar = new AlertRabbit();
        var properties = ar.readProperties();
        try (var connection = ar.getConnection(properties)) {
            int interval = Integer.parseInt((String) properties.get("rabbit.interval"));
            scheduler(connection, interval);
        }
    }

    private static void scheduler(Connection connection, int interval) {
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("connection", connection);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(interval)
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(3000);
            scheduler.shutdown();
        } catch (SchedulerException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Properties readProperties() {
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

    private Connection getConnection(Properties config)
            throws ClassNotFoundException, SQLException {
        Class.forName(config.getProperty("driver-class-name"));
        return DriverManager.getConnection(
                config.getProperty("url"),
                config.getProperty("user"),
                config.getProperty("password"));
    }

    public static class Rabbit implements Job {

        @Override
        public void execute(JobExecutionContext context) {
            Connection cn = (Connection) context.getJobDetail().getJobDataMap().get("connection");
            try (var statement = cn.prepareStatement(
                    "INSERT INTO dbrabbit.public.rabbit (created) VALUES (?)")) {
                statement.setDate(1, Date.valueOf(LocalDate.now()));
                statement.executeUpdate();
                System.out.println("test");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

        }
    }
}
