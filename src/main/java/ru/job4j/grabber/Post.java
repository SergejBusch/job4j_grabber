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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Post post = (Post) o;

        if (!name.equals(post.name)) {
            return false;
        }
        if (!detail.equals(post.detail)) {
            return false;
        }
        if (!url.equals(post.url)) {
            return false;
        }
        return date.equals(post.date);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + detail.hashCode();
        result = 31 * result + url.hashCode();
        result = 31 * result + date.hashCode();
        return result;
    }
}


