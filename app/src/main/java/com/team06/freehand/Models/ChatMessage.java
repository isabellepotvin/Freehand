package com.team06.freehand.Models;

import java.util.Date;

/**
 * Created by isabellepotvin on 2018-03-27.
 */

public class ChatMessage {

    private String message_text;
    private String sender_user_id;
    private String receiver_user_id;
    private long timestamp;


    //CONSTRUCTORS

    public ChatMessage(String message_text, String sender_user_id, String receiver_user_id, long timestamp) {
        this.message_text = message_text;
        this.sender_user_id = sender_user_id;
        this.receiver_user_id = receiver_user_id;
        this.timestamp = timestamp;
    }

    public ChatMessage() {
    }



    //GETTERS AND SETTERS

    public String getMessage_text() {
        return message_text;
    }

    public void setMessage_text(String message_text) {
        this.message_text = message_text;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    //TO STRING

    @Override
    public String toString() {
        return "ChatMessage{" +
                "message_text='" + message_text + '\'' +
                ", sender_user_id='" + sender_user_id + '\'' +
                ", receiver_user_id='" + receiver_user_id + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }


}
