package com.ahmet.postphotos.Model;

/**
 * Created by ahmet on 2/9/2018.
 */

public class Friends {

    private String date;
    private String name;
    private String image;
    private String status;

    public Friends() {
    }

    public Friends(String date, String name, String image, String status) {
        this.date = date;
        this.name = name;
        this.image = image;
        this.status = status;
    }

    public Friends(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getStatus() {
        return status;
    }
}
