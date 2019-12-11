package com.medialib.system;

import android.app.Application;

import com.base.XApplication;

/**
 * 作者：wjh on 2019-12-10 18:39
 */
public class App {

    public static Application getApplication() {
        return XApplication.getXApplication();
    }

}
