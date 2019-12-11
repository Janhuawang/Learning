package com.frank.callback;

import android.util.Log;

/**
 * 音频回调实现
 * 作者：wjh on 2019-12-11 22:01
 */
public abstract class AudioEditImpl implements HandleCallback {

    public abstract void onProgress(int dts, int type);

    @Override
    public void onCallback(String log, int type) {
        if (log != null) {
            Log.d("AudioEditImpl-callback", "log:" + log + "====type:" + type);
        }
    }
}
