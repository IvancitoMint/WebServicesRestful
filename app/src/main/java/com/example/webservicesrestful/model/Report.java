package com.example.webservicesrestful.model;

import com.google.gson.annotations.SerializedName;

public class Report {
    @SerializedName("id")
    private int id;

    @SerializedName("userId")
    private int userId;

    @SerializedName("title")
    private String title;

    @SerializedName("body")
    private String body;

    // Constructor for creating new records (POST)
    public Report(int userId, String title, String body) {
        this.userId = userId;
        this.title = title;
        this.body = body;
    }

    // Getter
    public int getId() {
        return id;
    }
    public int getUserId() {
        return userId;
    }
    public String getTitle() {
        return title;
    }
    public String getBody() {
        return body;
    }
}
