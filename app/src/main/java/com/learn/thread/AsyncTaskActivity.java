package com.learn.thread;


import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.learn.R;
import com.learn.base.BaseActivity;
import com.learn.util.LogUtil;
import com.learn.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.LockSupport;

/**
 * 一个简单的异步类，实现：2个线程池 + handler
 * 优势：1、节省资源 可复用线程池缓存，频繁创建及销毁时可减少资源开销。
 * 2、使用比较方便，不需要使用"任务线程" + "handler" 组合。
 * 3、过程可以控制，可以调动 cancel 取消任务。
 * 缺点：
 * 1、执行多个异步任务并刷新UI时用起来比较复杂。
 * 作者：wjh on 2019-08-06 17:45
 */
public class AsyncTaskActivity extends BaseActivity {
    /**
     * 缓存线程池 无限个执行线程
     * 创建一个可缓存线程池，线程池的最大长度无限制，但如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
     */
    private static final ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
    /**
     * 单线程跑 ：线程池中只有一个执行的线程。
     * 创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行。
     */
    private static final ExecutorService singleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    /**
     * 预定的 有排程的，可指定核心线程数
     * 创建一个定长线程池，支持定时及周期性任务执行。
     */
    private static final ExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);
    /**
     * 创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待。
     */
    private static final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
    private int threadIndex = 1;

    @Override
    protected void initView() {
    }

    @Override
    protected void initListener() {
        findViewById(R.id.tv_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                testCallable();
//                testFutureTaskAndThread();
//                testLockSupport();

                testAsyncTaskRunHandler();
            }
        });
        findViewById(R.id.tv_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                testFutureTask();
                ToastUtil.showText("哈哈哈！！");
            }
        });
    }

    @Override
    protected void initData() {

    }

    /**
     * 测试一个带返回结果的线程 - Callable和Future
     */
    private void testCallable() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<String> future = executorService.submit(new Callable<String>() {

            @Override
            public String call() throws Exception {
                LogUtil.log("Callable-任务开始了!");
                Thread.sleep(3000);
                LogUtil.log("Callable-任务结束了!");
                return "哈哈哈我来啦！！！";
            }
        });

        executorService.shutdown();

        try {
            LogUtil.log(future.get());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试一个带返回结果的线程 - Callable和FutureTask
     */
    private void testFutureTask() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        FutureTask<String> futureTask = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                LogUtil.log("FutureTask-任务开始了!");
                Thread.sleep(3000);
                LogUtil.log("FutureTask-任务结束了!");
                return "哈哈哈我来啦！！！";
            }
        });

        executorService.execute(futureTask);
        executorService.shutdown();

        try {
            LogUtil.log(futureTask.get());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试一个带返回结果的线程 - Callable和FutureTask和Thread
     */
    private void testFutureTaskAndThread() {
        FutureTask<String> futureTask = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                LogUtil.log("FutureTaskAndThread-任务开始了!");
                Thread.sleep(5000);
                LogUtil.log("FutureTaskAndThread-任务结束了!");
                return "哈哈哈我来啦！！！";
            }
        });

        new Thread(futureTask).start();

        try {
            LogUtil.log(futureTask.get());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 控制线程阻塞与唤醒
     */
    private void testLockSupport() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                LockSupport.park();
                LogUtil.log("testLockSupport-任务开始了!");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                LogUtil.log("testLockSupport-任务结束了!");
            }
        });
        thread.start();

        LogUtil.log("testLockSupport-开始啦!");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LockSupport.unpark(thread);
    }

    /**
     * 测试AsyncTask运行机制
     */
    private void testAsyncTaskRunHandler() {
        for (int i = 0; i < 400; i++) {
            threadIndex++;
            new MyAsyncTask("" + threadIndex).executeOnExecutor(fixedThreadPool);
        }
    }


    private static class MyAsyncTask extends AsyncTask<String, Integer, String> {
        private String asyncTaskName;

        public MyAsyncTask(String asyncTaskName) {
            this.asyncTaskName = asyncTaskName;
        }

        @Override
        protected String doInBackground(String... strings) {
            LogUtil.log("doInBackground-   " + asyncTaskName);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "{\"title\": \"c\"}";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            for(int j=0;j<200;j++){
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    LogUtil.log("onPostExecute-   " + asyncTaskName + "-   "+j + jsonObject.optString("title"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }

}
