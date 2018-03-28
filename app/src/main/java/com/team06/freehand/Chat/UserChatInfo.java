package com.team06.freehand.Chat;

/**
 * Created by isabellepotvin on 2018-03-27.
 */

public class UserChatInfo {

    private String name;
    private String profile_photo;


    public UserChatInfo(String name, String profile_photo) {
        this.name = name;
        this.profile_photo = profile_photo;
    }

    public UserChatInfo() {
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }


    @Override
    public String toString() {
        return "UserChatInfo{" +
                "name='" + name + '\'' +
                ", profile_photo='" + profile_photo + '\'' +
                '}';
    }
}
