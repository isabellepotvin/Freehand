package com.team06.freehand.Models;

import java.util.Date;

/**
 * Created by isabellepotvin on 2018-03-27.
 */

public class ChatMessage {

    private String image_path;
    private String sender_user_id;
    private String receiver_user_id;
    private String timestamp;


    //CONSTRUCTORS

    public ChatMessage(String image_path, String sender_user_id, String receiver_user_id, String timestamp) {
        this.image_path = image_path;
        this.sender_user_id = sender_user_id;
        this.receiver_user_id = receiver_user_id;
        this.timestamp = timestamp;
    }

    public ChatMessage() {
    }



    //GETTERS AND SETTERS

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getSender_user_id() {
        return sender_user_id;
    }

    public void setSender_user_id(String sender_user_id) {
        this.sender_user_id = sender_user_id;
    }

    public String getReceiver_user_id() {
        return receiver_user_id;
    }

    public void setReceiver_user_id(String receiver_user_id) {
        this.receiver_user_id = receiver_user_id;
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
        return "ChatMessage{" +
                "image_path='" + image_path + '\'' +
                ", sender_user_id='" + sender_user_id + '\'' +
                ", receiver_user_id='" + receiver_user_id + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }

}

