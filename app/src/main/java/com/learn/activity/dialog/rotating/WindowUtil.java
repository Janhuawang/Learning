package com.learn.activity.dialog.rotating;

import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 */
public class WindowUtil {
    /**
     * 获取屏幕大小
     */
    public static Size getWindowSize(){
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        return new Size(displayMetrics.widthPixels,displayMetrics.heightPixels);
    }
}
