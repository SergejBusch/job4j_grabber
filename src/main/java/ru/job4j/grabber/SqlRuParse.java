package ru.job4j.grabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlRuParse implements Parse {
    private static final Map<String, LocalDate> MOTHS_AND_DAYS = new HashMap<>();

    public SqlRuParse() {
        fillMap();
    }

    public static void main(String[] args) {
        var sqlRuParse = new SqlRuParse();
        List<Post> list = sqlRuParse.list("https://www.sql.ru/forum/job-offers");
        for (Post post : list) {
            System.out.println(post.getName());
            System.out.println(post.getUrl());
            System.out.println(post.getDate());
            System.out.println(post.getDetail());
            System.out.println("------------------------------");
        }
    }

    private Date convertDate(String date) {
        String[] dateParts = date.split(",")[0].split(" ");
        if (dateParts.length > 1) {
            var tempDate = MOTHS_AND_DAYS.get(dateParts[1]);
            Year year = Year.parse(dateParts[2], DateTimeFormatter.ofPattern("yy"));
            tempDate = tempDate
                    .withYear(year.getValue())
                    .withDayOfMonth(Integer.parseInt(dateParts[0]));
            return Date.valueOf(tempDate);
        } else {
            return Date.valueOf(MOTHS_AND_DAYS.get(dateParts[0]));
        }
    }

    private void fillMap() {
        MOTHS_AND_DAYS.put("сегодня", LocalDate.now());
        MOTHS_AND_DAYS.put("вчера", LocalDate.now().minusDays(1));
        MOTHS_AND_DAYS.put("янв", LocalDate.now().withMonth(1));
        MOTHS_AND_DAYS.put("фев", LocalDate.now().withMonth(2));
        MOTHS_AND_DAYS.put("мар", LocalDate.now().withMonth(3));
        MOTHS_AND_DAYS.put("апр", LocalDate.now().withMonth(4));
        MOTHS_AND_DAYS.put("май", LocalDate.now().withMonth(5));
        MOTHS_AND_DAYS.put("июн", LocalDate.now().withMonth(6));
        MOTHS_AND_DAYS.put("июл", LocalDate.now().withMonth(7));
        MOTHS_AND_DAYS.put("авг", LocalDate.now().withMonth(8));
        MOTHS_AND_DAYS.put("сен", LocalDate.now().withMonth(9));
        MOTHS_AND_DAYS.put("окт", LocalDate.now().withMonth(10));
        MOTHS_AND_DAYS.put("ноя", LocalDate.now().withMonth(11));
        MOTHS_AND_DAYS.put("дек", LocalDate.now().withMonth(12));
    }

    @Override
    public List<Post> list(String link) {
        var list = new ArrayList<Post>();
        for (int siteNumber = 1; siteNumber < 6; siteNumber++) {
            String address = link;
            address += "/" + siteNumber;
            try {
                Document doc = Jsoup.connect(address).get();
                Elements row = doc.select(".postslisttopic");
                Elements dates = doc.select(".altCol");
                int i = 1;
                for (Element td : row) {
                    Element href = td.child(0);
                    Post post = detail(href.attr("href"));
                    post.setUrl(href.attr("href"));
                    post.setName(href.text());
                    post.setDate(convertDate(dates.get(i).text()));
                    i += 2;
                    list.add(post);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    @Override
    public Post detail(String link) {
        var post = new Post();
        try {
            Document doc = Jsoup.connect(link).get();
            Elements row = doc.select(".msgBody");
            post.setDetail(row.get(1).text());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return post;
    }

}
