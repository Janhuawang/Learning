package com.medialib.audioedit.util;

import android.widget.Toast;

import com.base.XApplication;
import com.medialib.system.App;

public class ToastUtil {

    /**
     * 系统Toast
     */
    public static void showToast(final String text) {
        XApplication.getXApplication().xHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(App.getApplication(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 系统Toast
     */
    public static void showToast(final int textId) {
        XApplication.getXApplication().xHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(App.getApplication(), App.getApplication().getResources().getString(textId), Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }
}
