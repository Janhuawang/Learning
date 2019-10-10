package com.learn.activity.thread;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.learn.R;

/**
 * volatile
 * 作者：wjh on 2019-07-22 19:31
 * <p>
 * volatile可修饰成员变量，能保证变量的可见性，但是不能保证原子性，
 * 也就是说并发的时候多个线程对变量进行计算的话，结果是会出错的，保证可见性只是能保证每个线程拿到的东西是最新的。
 * 内存可见性：通俗来说就是，线程A对一个volatile变量的修改，对于其它线程来说是可见的，即线程每次获取volatile变量的值都是最新的。
 */
public class VolatileActivity extends AppCompatActivity {


    //  private volatile int c = 0;
    private Bean bean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item);

        findViewById(R.id.tv_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bean = new Bean();
                initData();
            }
        });
        findViewById(R.id.tv_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /***** volatile ************************************************************/
    public void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                addHandle("one");
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i < 50; i++) {
                    reduceHandle("tow");
                }
            }
        }).start();
    }

    private void addHandle(String it) {
        Log.e("Learn", "-add");
        for (int i = 1; i <= 9999; i++) {
            ++bean.c;
        }
        Log.e("Learn", "+++++++++++++++++++++++++-add:" + bean.c);
    }

    /**
     * 可以保证拿到数据是最新更改的数据  即线程每次获取volatile变量的值都是最新的
     *
     * @param it
     */
    private void reduceHandle(String it) {
        Log.e("Learn", "-reduce");
        --bean.c;
        Log.e("Learn", "-reduce:" + bean.c);
    }

    public static class Bean {
        public volatile int c;
    }

}