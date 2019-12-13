package com.medialib.audioedit.bean;

import android.text.TextUtils;

/**
 *
 */

public class AudioMsg {

    public String type;
    public String path;
    public String msg;

    public AudioMsg(String type, String path, String msg) {
        this.type = type;
        this.path = path;
        this.msg = msg;
    }

    public AudioMsg(String type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public boolean isDone() {
        return !TextUtils.isEmpty(path);
    }

}