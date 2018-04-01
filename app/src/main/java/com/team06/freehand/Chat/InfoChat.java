package com.team06.freehand.Chat;

/**
 * Created by isabellepotvin on 2018-03-27.
 */

public class InfoChat {

    private String name;
    private String imgUrl;
    private String last_timestamp;


    public InfoChat(String name, String imgUrl, String last_timestamp) {
        this.name = name;
        this.imgUrl = imgUrl;
        this.last_timestamp = last_timestamp;
    }

    public InfoChat() {
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getLast_timestamp() {
        return last_timestamp;
    }

    public void setLast_timestamp(String last_timestamp) {
        this.last_timestamp = last_timestamp;
    }
}
