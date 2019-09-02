package com.learn.util;

import android.util.Log;

/**
 * 作者：wjh on 2019-08-06 19:00
 */
public class LogUtil {
    private static final String TAG = "Learn";

    public static void log(String logStr) {
        Log.e(TAG, "##########:" + logStr);
    }
}
