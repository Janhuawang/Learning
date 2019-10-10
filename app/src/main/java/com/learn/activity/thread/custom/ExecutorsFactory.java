package com.learn.activity.thread.custom;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 作者：wjh on 2019-08-12 21:50
 */
public class ExecutorsFactory {

    public static ExecutorService buildDefaultExecutor(){
        return Executors.newCachedThreadPool();
    }
}
