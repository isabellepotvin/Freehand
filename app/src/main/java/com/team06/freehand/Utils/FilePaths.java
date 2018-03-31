package com.team06.freehand.Utils;

import android.os.Environment;

/**
 * Created by isabellepotvin on 2018-03-14.
 */

public class FilePaths {

    // "storage/emulated/0"
    public String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();

    public String PICTURES = ROOT_DIR + "/Pictures";

    public String CAMERA = ROOT_DIR + "/DCIM/camera";

    public String FIREBASE_IMAGE_STORAGE = "photos/users/";

    public String FIREBASE_MESSAGE_STORAGE = "chats/";




}
