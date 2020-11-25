package ru.job4j.grabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class SqlRuParse {
    public static void main(String[] args) throws Exception {
        var sqlRuParse = new SqlRuParse();
        for (int siteNumber = 1; siteNumber < 6; siteNumber++) {
            String adress = "https://www.sql.ru/forum/job-offers";
            adress = siteNumber > 1 ? adress + "/" + siteNumber : adress;
            Document doc = Jsoup.connect(adress).get();
            Elements row = doc.select(".postslisttopic");
            Elements dates = doc.select(".altCol");
            int i = 1;
            for (Element td : row) {
                Element href = td.child(0);
                System.out.println(href.attr("href"));
                System.out.println(href.text());
                System.out.println(dates.get(i).text());
                System.out.println(Date.valueOf(sqlRuParse
                        .convertDate(dates.get(i).text())));
                i += 2;
            }
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
}
