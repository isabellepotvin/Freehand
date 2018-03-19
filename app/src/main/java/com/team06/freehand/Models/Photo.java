package com.team06.freehand.Models;

/**
 * Created by isabellepotvin on 2018-03-17.
 */

public class Photo {

    private String date_created;
    private String image_path;
    private String photo_id;
    private String user_id;


    //CONSTRUCTORS

    public Photo(String date_created, String image_path, String photo_id, String user_id) {
        this.date_created = date_created;
        this.image_path = image_path;
        this.photo_id = photo_id;
        this.user_id = user_id;
    }

    public Photo() {

    }




    //GETTERS AND SETTERS

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


    //TO STRING

    @Override
    public String toString() {
        return "Photo{" +
                "date_created='" + date_created + '\'' +
                ", image_path='" + image_path + '\'' +
                ", photo_id='" + photo_id + '\'' +
                ", user_id='" + user_id + '\'' +
                '}';
    }
}
