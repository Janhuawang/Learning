package com.base;

import android.app.Application;
import android.os.Handler;

/**
 * 作者：wjh on 2019-12-10 20:22
 */
public class XApplication extends Application {

    public Handler xHandler;

    /**
     * 全局上下文对象
     */
    private static XApplication xApplication;

    /**
     * 上下文对象
     *
     * @return
     */
    public static XApplication getXApplication() {
        return xApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        xHandler = new Handler();
        xApplication = this;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        xHandler = null;
        xApplication = null;
    }
}
