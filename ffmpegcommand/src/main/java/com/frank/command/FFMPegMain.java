package com.frank.command;

import com.frank.callback.HandleCallback;

/**
 * 命令执行入口
 */
public class FFMPegMain {
    /**
     * 业务层的回调
     */
    private static HandleCallback handleCallback;

    static {
        System.loadLibrary("media-command");
    }

    private native static int handle(String[] commands, int type);

    /**
     * 执行指令 运行在子线程中
     *
     * @param commands
     * @param type
     * @param handleCallback
     */
    public static void execute(final String[] commands, final int type, final HandleCallback handleCallback) {
        FFMPegMain.handleCallback = handleCallback;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (handleCallback != null) {
                    handleCallback.onBegin();
                }

                int result = handle(commands, type);// 执行成功后返回0

                if (handleCallback != null) {
                    handleCallback.onEnd(result);
                }
            }
        }).start();
    }

    /**
     * 回调 c - native
     *
     * @param log
     * @param type
     */
    public static void onCallback(String log, int type) {
        if (handleCallback != null) {
            handleCallback.onCallback(log, type);
        }
    }

}