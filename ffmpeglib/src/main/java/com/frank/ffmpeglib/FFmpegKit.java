package com.frank.ffmpeglib;

/**
 * 作者：wjh on 2019-10-24 00:06
 */
public class FFmpegKit {
    static {
        System.loadLibrary("ffmpeg");
        System.loadLibrary("media-handle");
    }

    /**
     * 开子线程调用本地方法进行音视频处理
     *
     * @param commands
     * @param onHandleListener
     */
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

    public interface OnHandleListener {
        void onBegin();

        void onEnd(int result);
    }
}
