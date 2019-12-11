package com.learn.base;

import com.base.XApplication;

/**
 * 一级缓存Application
 *
 * @author wjh
 */
public class IApplication extends XApplication {
    /**
     * 全局上下文对象
     */
    private static IApplication iApplication;


    /**
     * 上下文对象
     *
     * @return
     */
    public static IApplication getIApplication() {
        return iApplication;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        iApplication = this;
    }


}
