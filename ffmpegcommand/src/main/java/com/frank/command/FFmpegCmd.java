package com.frank.command;

import android.util.Log;

public class FFmpegCmd {
    /*裁剪类型 - 只读裁剪区内的视频，把所有帧转换成关键帧*/
    public static final int BUSINESS_TYPE_CUT = 1;
    /*裁剪类型 - 对整个视频重新编码*/
    public static final int BUSINESS_TYPE_CUT2 = 2;
    /**
     * 回调
     */
    private static OnHandleListener onHandleListener;

    static {
        System.loadLibrary("media-command");
    }

    // 开子线程调用native方法进行音视频处理
    public static void execute(final String[] commands, final int type, final OnHandleListener onHandleListener) {
        FFmpegCmd.onHandleListener = onHandleListener;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (onHandleListener != null) {
                    onHandleListener.onBegin();
                }
                // 调用ffmpeg进行处理
                int result = handle(commands, type);
                if (onHandleListener != null) {
                    onHandleListener.onEnd(result);
                }
            }
        }).start();
    }

    private native static int handle(String[] commands, int type);

    public static void onCallback(String log, int type) {
        if (log != null) {
            Log.d("====onCallback ", "" + log + "====type " + type);

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

                    onProgress(timeLog, type);
                }
            }
        }
    }

    public static void onProgress(String dts, int type) {
        if (onHandleListener != null && dts != null) {
            try {
                onHandleListener.onProgress(Integer.parseInt(dts.trim()), type);
            } catch (Exception e) {
            }
        }
    }

    public interface OnHandleListener {
        void onBegin();

        void onProgress(int dts, int type);

        void onEnd(int result);
    }

}