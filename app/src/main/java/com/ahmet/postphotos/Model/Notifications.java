package com.ahmet.postphotos.Model;

public class Notifications {

    private String notifcation;
    private String from;
    private String type;
    private String name;
    private String image;

    public Notifications() {
    }

    public Notifications(String notifcation, String from, String type, String name, String image) {
        this.notifcation = notifcation;
        this.from = from;
        this.type = type;
        this.name = name;
        this.image = image;
    }

    public String getNotifcation() {
        return notifcation;
    }

    public String getFrom() {
        return from;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }
}
