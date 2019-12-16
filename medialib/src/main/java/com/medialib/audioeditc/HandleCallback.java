package com.medialib.audioeditc;

/**
 * 作者：wjh on 2019-12-11 21:44
 */
public interface HandleCallback {
    void onBegin();

    void onCallback(String log, int type);

    void onEnd(int result);
}
