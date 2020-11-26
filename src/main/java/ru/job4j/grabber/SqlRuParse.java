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

public class SqlRuParse implements Parse {
    public static void main(String[] args) {
        var sqlRuParse = new SqlRuParse();
        List<Post> list = sqlRuParse.list("https://www.sql.ru/forum/job-offers");
        for (Post post : list) {
            System.out.println(post.getTitle());
            System.out.println(post.getUrl());
            System.out.println(post.getDate());
            System.out.println(post.getDetail());
            System.out.println("------------------------------");
        }
    }

    private LocalDate convertDate(String date) {
        String[] dateParts = date.split(",")[0].split(" ");
        if (dateParts.length > 1) {
            var tempDate = dateMap(dateParts[1]);
            Year year = Year.parse(dateParts[2], DateTimeFormatter.ofPattern("yy"));
            tempDate = tempDate
                    .withYear(year.getValue())
                    .withDayOfMonth(Integer.parseInt(dateParts[0]));
            return tempDate;
        } else {
            return dateMap(dateParts[0]);
        }
    }

    private LocalDate dateMap(String monthOrDay) {
        var map = new HashMap<String, LocalDate>();
        map.put("сегодня", LocalDate.now());
        map.put("вчера", LocalDate.now().minusDays(1));
        map.put("янв", LocalDate.now().withMonth(1));
        map.put("фев", LocalDate.now().withMonth(2));
        map.put("мар", LocalDate.now().withMonth(3));
        map.put("апр", LocalDate.now().withMonth(4));
        map.put("май", LocalDate.now().withMonth(5));
        map.put("июн", LocalDate.now().withMonth(6));
        map.put("июл", LocalDate.now().withMonth(7));
        map.put("авг", LocalDate.now().withMonth(8));
        map.put("сен", LocalDate.now().withMonth(9));
        map.put("окт", LocalDate.now().withMonth(10));
        map.put("ноя", LocalDate.now().withMonth(11));
        map.put("дек", LocalDate.now().withMonth(12));
        return map.get(monthOrDay);
    }

    @Override
    public List<Post> list(String link) {
        var list = new ArrayList<Post>();
        for (int siteNumber = 1; siteNumber < 6; siteNumber++) {
            String address = link;
            address = siteNumber > 1 ? address + "/" + siteNumber : address;
            try {
                Document doc = Jsoup.connect(address).get();
                Elements row = doc.select(".postslisttopic");
                Elements dates = doc.select(".altCol");
                int i = 1;
                for (Element td : row) {
                    Element href = td.child(0);
                    Post post = detail(href.attr("href"));
                    post.setUrl(href.attr("href"));
                    post.setTitle(href.text());
                    post.setDate(Date.valueOf(convertDate(dates.get(i).text())));
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
            StringBuilder lines = new StringBuilder();
            String strRegEx = "<[^>]*>";
            for (var e : row.get(1).childNodes()) {
                final String trim = e.toString().replaceAll(strRegEx, "").trim();
                if (e.toString().startsWith("<b>")) {
                    lines.append(trim);
                } else if (e.toString().startsWith("<br")) {
                    lines.append(System.lineSeparator());
                } else {
                    lines.append(trim);
                }
            }
            post.setDetail(lines.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return post;
    }

}
