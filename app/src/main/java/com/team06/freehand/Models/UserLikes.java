package com.team06.freehand.Models;

/**
 * Created by isabellepotvin on 2018-03-27.
 */

public class UserLikes {

    private String other_user_id;
    private String timestamp;

    //CONSTRUCTORS


    public UserLikes(String other_user_id, String timestamp) {
        this.other_user_id = other_user_id;
        this.timestamp = timestamp;
    }

    public UserLikes() {
    }


    //GETTERS AND SETTERS

    public String getOther_user_id() {
        return other_user_id;
    }

    public void setOther_user_id(String other_user_id) {
        this.other_user_id = other_user_id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }


    //TO STRING

    @Override
    public String toString() {
        return "UserLikes{" +
                "other_user_id='" + other_user_id + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }


}
