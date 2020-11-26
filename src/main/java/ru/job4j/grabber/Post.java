package ru.job4j.grabber;

import java.util.Date;

public class Post {
    private String url;
    private String title;
    private Date date;
    private String detail;

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
