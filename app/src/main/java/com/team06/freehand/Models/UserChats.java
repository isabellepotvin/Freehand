package com.team06.freehand.Models;

import java.util.Date;

/**
 * Created by isabellepotvin on 2018-03-27.
 */

public class UserChats {

    private String chat_id;
    private String other_user_id;
    private long last_timestamp;

    //CONSTRUCTORS

    public UserChats(String chat_id, String other_user_id) {
        this.chat_id = chat_id;
        this.other_user_id = other_user_id;
        this.last_timestamp = new Date().getTime();
    }

    public UserChats() {
    }

    //GETTERS AND SETTERS

    public String getChat_id() {
        return chat_id;
    }

    public void setChat_id(String chat_id) {
        this.chat_id = chat_id;
    }

    public long getLast_timestamp() {
        return last_timestamp;
    }

    public void setLast_timestamp(long last_timestamp) {
        this.last_timestamp = last_timestamp;
    }

    public String getOther_user_id() {
        return other_user_id;
    }

    public void setOther_user_id(String other_user_id) {
        this.other_user_id = other_user_id;
    }

    //TO STRING

    @Override
    public String toString() {
        return "UserChats{" +
                "chat_id='" + chat_id + '\'' +
                ", other_user_id='" + other_user_id + '\'' +
                ", last_timestamp=" + last_timestamp +
                '}';
    }


}
