package ru.job4j.grabber;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {
    private Connection connection;

    public PsqlStore(Properties cfg) {
        try {
            Class.forName(cfg.getProperty("driver-class-name"));
            connection = DriverManager.getConnection(
                    cfg.getProperty("url"),
                    cfg.getProperty("user"),
                    cfg.getProperty("password"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(Post post) {
        String sql = "INSERT INTO aggregator.post (name, text, link, created) VALUES (?, ?, ?, ?)";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, post.getName());
            statement.setString(2, post.getDetail());
            statement.setString(3, post.getUrl());
            statement.setDate(4, (Date) post.getDate());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        var list = new ArrayList<Post>();
        String sql = "SELECT * FROM aggregator.post";
        try (var ps = connection.prepareStatement(sql)) {
            var rs = ps.executeQuery();
            while (rs.next()) {
                var name = rs.getString(2);
                var text = rs.getString(3);
                var link = rs.getString(4);
                var date = rs.getDate(5);
                var post = new Post(name, text, link, date);
                list.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Post findBy(String id) {
        String sql = "SELECT * FROM aggregator.post WHERE id=?";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, Integer.parseInt(id));
            var rs = statement.executeQuery();
            while (rs.next()) {
                var name = rs.getString(2);
                var text = rs.getString(3);
                var link = rs.getString(4);
                var date = rs.getDate(5);
                return new Post(name, text, link, date);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    public static void main(String[] args) {
        try (var in = PsqlStore.class
                .getClassLoader().getResourceAsStream("rabbit.properties")) {
            var properties = new Properties();
            properties.load(in);
            var psqlStore = new PsqlStore(properties);

            psqlStore.save(new Post(
                    "Java",
                    "Text text",
                    "www.internet",
                    Date.valueOf(LocalDate.now())));

            Post post = psqlStore.findBy("1");
            System.out.println(post.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
