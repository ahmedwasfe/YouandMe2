package com.ahmet.postphotos.Model;

import java.io.Serializable;

/**
 * Created by ahmet on 2/13/2018.
 */

public class UserData implements Serializable {

    private String uid;
    private String name;
    private String phone;
    private String age;
    private String gender;

    public UserData() {}

    public UserData(String name, String phone, String age, String gender) {
        this.name = name;
        this.phone = phone;
        this.age = age;
        this.gender = gender;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }
}
