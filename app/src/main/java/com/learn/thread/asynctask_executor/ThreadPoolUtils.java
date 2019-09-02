package com.learn.thread.asynctask_executor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 作者：wjh on 2019-08-08 21:09
 */
public class ThreadPoolUtils {
    //核心线程数
    private static int CORE_POOL_SIZE = 8;
    //最大线程数
    private static int MAX_POOL_SIZE = 64;
    //线程池中超过corePoolSize数目的空闲线程最大存活时间；可以allowCoreThreadTimeOut(true)使得核心线程有效时间
    private static int KEEP_ALIVE_TIME = 5;
    //任务队列
    private static BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(64);
    public static ThreadPoolExecutor threadpool;

    static {
        threadpool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue);
    }

    private ThreadPoolUtils() {
    }

    public static void execute(Runnable runnable) {
        threadpool.execute(runnable);
    }
}