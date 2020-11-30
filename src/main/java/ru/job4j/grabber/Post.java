package ru.job4j.grabber;

import java.util.Date;

public class Post {
    private String name;
    private String detail;
    private String url;
    private Date date;

    public Post() {
    }

    public Post(String name, String detail, String url, Date date) {
        this.name = name;
        this.detail = detail;
        this.url = url;
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }

    public String getDetail() {
        return detail;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
