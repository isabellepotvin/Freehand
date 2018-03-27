package com.team06.freehand.Models;

import java.util.Date;

/**
 * Created by isabellepotvin on 2018-03-27.
 */

public class UserChats {

    private String chat_id;
    private long last_timestamp;

    //CONSTRUCTORS

    public UserChats(String chat_id) {
        this.chat_id = chat_id;
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


    //TO STRING

    @Override
    public String toString() {
        return "UserChats{" +
                "chat_id='" + chat_id + '\'' +
                ", last_timestamp=" + last_timestamp +
                '}';
    }


}
