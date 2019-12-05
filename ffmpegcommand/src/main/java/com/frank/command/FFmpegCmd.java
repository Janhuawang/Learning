package com.frank.command;

import android.util.Log;

public class FFmpegCmd {

    private static OnProgressListener onProgressListener;

    static {
        System.loadLibrary("media-command");
    }

    //开子线程调用native方法进行音视频处理
    public static void execute(final String[] commands, final OnHandleListener onHandleListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (onHandleListener != null) {
                    onHandleListener.onBegin();
                }
                //调用ffmpeg进行处理
                int result = handle(commands);
                if (onHandleListener != null) {
                    onHandleListener.onEnd(result);
                }
            }
        }).start();
    }

    private native static int handle(String[] commands);

    public static void onProgress(int second) {
        Log.e("====onProgress",""+second);
        if (onProgressListener != null && second >= 0) {
            onProgressListener.onProgress(second);
        }
    }

    public interface OnHandleListener {
        void onBegin();

        void onEnd(int result);
    }

    public interface OnProgressListener {
        void onProgress(int second);
    }

}