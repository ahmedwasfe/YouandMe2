package com.ahmet.postphotos.Model;

/**
 * Created by ahmet on 3/19/2018.
 */

public class Blog {

    private String title;
    private String description;
    private String image;
    private String username;
    private String uid;
    private String imageUser;
    private String timePost;

    public Blog() {
    }

    public Blog(String imageUser) {
        this.imageUser = imageUser;
    }


    public Blog(String title, String description, String image, String username, String uid) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.username = username;
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }


    public String getDescription() {
        return description;
    }


    public String getImage() {
        return image;
    }


    public String getUsername() {
        return username;
    }

    public String getUid() {
        return uid;
    }

    public String getImageUser() {
        return imageUser;
    }

    public String getTimePost() {
        return timePost;
    }
}
