package com.team06.freehand.Models;

/**
 * Created by isabellepotvin on 2018-03-07.
 */

public class User {

    private String user_id;
    private String email;
    private String name;
    private long age;
    private String location;
    private String description;
    private long pictures_number;
    private String profile_photo;



    //Constructors

    public User(String user_id, String email, String name, long age, String location, String description, long pictures_number, String profile_photo) {
        this.user_id = user_id;
        this.email = email;
        this.name = name;
        this.age = age;
        this.location = location;
        this.description = description;
        this.pictures_number = pictures_number;
        this.profile_photo = profile_photo;
    }

    public User(){

    }


    //GETTERS AND SETTERS

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getAge() {
        return age;
    }

    public void setAge(long age) {
        this.age = age;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getPictures_number() {
        return pictures_number;
    }

    public void setPictures_number(long pictures_number) {
        this.pictures_number = pictures_number;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }


    //toString

    @Override
    public String toString() {
        return "User{" +
                "user_id='" + user_id + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                ", pictures_number=" + pictures_number +
                ", profile_photo='" + profile_photo + '\'' +
                '}';
    }
}
