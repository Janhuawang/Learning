package com.learn.activity.thread.custom;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

/**
 * 作者：wjh on 2019-08-12 21:55
 */
public class ITask <Params, Progress, Result>{

    private Handler mHandler;

    public ITask(@Nullable Handler handler) {
        this.mHandler = handler;
    }

    public ITask() {
        this(new Handler(Looper.getMainLooper()));

    }


    public void execute(){
        new IRun<Params, Progress, Result>() {
            @Override
            protected Result doInBackground(Params... params) {
                return null;
            }
        };
    }

}
