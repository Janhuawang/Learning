package com.frank.command;

import android.util.Log;

public class FFmpegCmd {
    /*裁剪类型*/
    private static final int BUSINESS_TYPE_CUT = 1;
    /**
     * 回调
     */
    private static OnHandleListener onHandleListener;

    static {
        System.loadLibrary("media-command");
    }

    // 开子线程调用native方法进行音视频处理
    public static void execute(final String[] commands, final OnHandleListener onHandleListener) {
        FFmpegCmd.onHandleListener = onHandleListener;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (onHandleListener != null) {
                    onHandleListener.onBegin();
                }
                // 调用ffmpeg进行处理
                int result = handle(commands, BUSINESS_TYPE_CUT);
                if (onHandleListener != null) {
                    onHandleListener.onEnd(result);
                }
            }
        }).start();
    }

    private native static int handle(String[] commands, int type);

    public static void onCallback(String log, int type) {
        if (log != null) {
            if (type == BUSINESS_TYPE_CUT) { // 裁剪模块
                Log.d("====onCallback", "" + log);

                int dtsIndex = log.indexOf("dts ");
                if (dtsIndex > -1) {
                    String dtsLog = log.substring(dtsIndex, log.length() - 1);
                    String[] sepLogArr = dtsLog.split(",");
                    if (sepLogArr.length > 0) {
                        String timeLog;
                        String[] dtsArr = sepLogArr[0].split("dts ");
                        if (dtsArr.length > 1) {
                            timeLog = dtsArr[1];
                        } else {
                            timeLog = dtsArr[0];
                        }

                        onProgress(timeLog);
                    }
                }
            }
        }
    }

    public static void onProgress(String dts) {
        if (onHandleListener != null && dts != null) {
            try {
                onHandleListener.onProgress(Integer.parseInt(dts.trim()));
            } catch (Exception e) {
            }
        }
    }

    public interface OnHandleListener {
        void onBegin();

        void onProgress(int dts);

        void onEnd(int result);
    }

}