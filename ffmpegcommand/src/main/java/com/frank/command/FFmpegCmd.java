package com.frank.command;

import android.text.TextUtils;
import android.util.Log;

import java.math.BigDecimal;
import java.math.BigInteger;

public class FFmpegCmd {
    /*裁剪类型*/
    public static final int BUSINESS_TYPE_CUT = 1;
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

    public static void onProgress(String time, int type) {
        Log.e("====onProgress", "" + time + "  type：" + type);
        if (onHandleListener != null && time != null) {
            onHandleListener.onProgress(parseInt(time.trim()), type);
        }
    }

    public static int parseInt(Object obj) {
        int count = 0;
        if (obj != null) {
            if (obj instanceof Integer) {
                count = ((Integer) obj).intValue();
            } else if (obj instanceof Long) {
                try {
                    count = ((Long) obj).intValue();
                } catch (Exception e) {
                }
            } else if (obj instanceof BigInteger) {
                count = ((BigInteger) obj).intValue();
            } else if (obj instanceof Float) {
//                try {
//                    count = ((Float) obj).intValue();
//                } catch (Exception e) {
//                }
                count = (int) ((Float) obj + 0.5);
            } else if (obj instanceof Double) {
//                try {
//                    count = ((Double) obj).intValue();
//                } catch (Exception e) {
//                }
                count = (int) ((Double) obj + 0.5);
            } else if (obj instanceof BigDecimal) {
                count = ((BigDecimal) obj).intValue();
            } else if (obj instanceof Byte) {
                count = ((Byte) obj).intValue();
            } else if (obj instanceof Boolean) {
                count = ((Boolean) obj) ? 1 : 0;
            } else if (obj instanceof String) {
                if (TextUtils.isEmpty((String) obj)) {
                    return count;
                }
                try {
                    count = Integer.parseInt(((String) obj).trim());
                } catch (Exception e) {
                    return count;
                }
            }
        }
        return count;
    }

    public interface OnHandleListener {
        void onBegin();

        void onProgress(int time, int type);

        void onEnd(int result);
    }

}