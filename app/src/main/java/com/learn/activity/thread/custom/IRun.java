package com.learn.activity.thread.custom;

import java.util.concurrent.Callable;

/**
 * 执行任务
 * 作者：wjh on 2019-08-12 21:52
 */
public abstract class IRun<Params, Progress, Result> implements Callable<Result> {

    private Params[] mParams;

    @Override
    public Result call() throws Exception {
        return doInBackground(mParams);
    }

    protected abstract Result doInBackground(Params... params);
}
