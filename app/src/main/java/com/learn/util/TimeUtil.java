package com.learn.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 作者：wjh on 2019-10-10 21:39
 */
public class TimeUtil {

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

}
