package com.learn.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 作者：wjh on 2019-10-10 21:39
 */
public class TimeUtil {

    /**
     * 秒转时长 - 格式 xx:xx 或 xx:xx:xx
     *
     * @param time
     * @return
     */
    public static String secToTime(int time) {
        String timeStr;
        int hour;
        int minute;
        int second;

        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }

        return timeStr;
    }

    private static String unitFormat(int i) {
        String retStr;
        if (i >= 0 && i < 10)
            retStr = "0" + i;
        else
            retStr = "" + i;
        return retStr;
    }

    /**
     * 获取制定格式的当前时间
     *
     * @param dateType
     * @return
     */
    public static String getNowTimeStr(String dateType) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateType);
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    /**
     * 文件名中使用到的时间格式
     *
     * @param timeMillis
     * @return
     */
    public static String getSaveFileTime(long timeMillis) {
        return new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date(timeMillis));
    }


}
