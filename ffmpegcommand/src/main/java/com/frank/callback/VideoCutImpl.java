package com.frank.callback;

import android.util.Log;

import com.frank.command.FFMPegType;

/**
 * 视频裁剪回调实现
 * 作者：wjh on 2019-12-11 21:46
 */
public abstract class VideoCutImpl implements HandleCallback {
    public abstract void onProgress(int dts, int type);

    @Override
    public void onCallback(String log, int type) {
        if (log != null) {
            Log.d("VideoCutImpl-callback", "log:" + log + "====type:" + type);

            if (type == FFMPegType.BUSINESS_TYPE_VIDEO_CUT1 || type == FFMPegType.BUSINESS_TYPE_VIDEO_CUT2) {
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

                        if (timeLog != null) {
                            try {
                                onProgress(Integer.parseInt(timeLog.trim()), type);
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            }
        }
    }
}
