package com.example.walterademiluyi.washingtonposttest.models;

/**
 * Created by walterademiluyi on 2/8/15.
 */
public class Post {
    String id;
    String title;
    String content;
    String date;
    String excerpt;

    public String getContent() {
        return content;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public String toString() {
        return content;
    }
}
