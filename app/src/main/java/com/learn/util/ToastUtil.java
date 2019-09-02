package com.learn.util;

import android.content.Context;
import android.widget.Toast;

import com.learn.base.IApplication;


/**
 * Toast弹出工具
 *
 * @author wjh
 */
public class ToastUtil {

    private static Context activity = IApplication.getIApplication();
    private static Toast toast = null;

    private static long lasTimeMillis;
    private static Object lastText;

    /**
     * 吐司显示信息
     *
     * @param text
     */
    public static void showText(Object text) {
        show(text, Toast.LENGTH_SHORT);
    }

    /**
     * 长时间显示吐司
     *
     * @param text
     */
    public static void showTextLong(Object text) {
        show(text, Toast.LENGTH_LONG);
    }

    private static void show(Object text, int duration) {
        if (text == null) {
            return;
        }

        // 屏蔽1秒内重复内容的吐司
        long timeMillis = System.currentTimeMillis();
        if (text.equals(lastText) && timeMillis - lasTimeMillis < 1000) {
            return;
        }
        lasTimeMillis = timeMillis;
        lastText = text;

        if (toast != null) {
            toast.cancel();
        }
        if (text instanceof Integer) {
            toast = Toast.makeText(activity, (Integer) text, duration);
        } else {
            String s = text.toString();
            if (s.trim().length() > 0) {
                toast = Toast.makeText(activity, s, duration);
            }
        }
        if (toast != null) {
            toast.show();
        }
    }

}
