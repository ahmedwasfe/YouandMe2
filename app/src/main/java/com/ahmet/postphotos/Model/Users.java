package com.ahmet.postphotos.Model;

/**
 * Created by ahmet on 2/5/2018.
 */

public class Users {

    private String UID;
    private String name;
    private String status;
    private String image;
    private String thumbImage;
    private String phone;
    private String age;
    private String gender;

    public Users() {
    }

    public Users(String name,String phonr,String age,String gender, String status, String image, String thumbImage) {
        this.name = name;
        this.phone = phonr;
        this.age = age;
        this.gender = gender;
        this.status = status;
        this.image = image;
        this.thumbImage = thumbImage;
    }

    public String getUID() {
        return UID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public String getThumbImage() {
        return thumbImage;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setThumbImage(String thumbImage) {
        this.thumbImage = thumbImage;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
