package com.team06.freehand.Chat;

/**
 * Created by isabellepotvin on 2018-03-27.
 */

public class UserChatInfo {

    private String name;
    private String imgUrl;


    public UserChatInfo(String name, String imgUrl) {
        this.name = name;
        this.imgUrl = imgUrl;
    }

    public UserChatInfo() {
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


}
