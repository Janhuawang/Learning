package com.learn.thread;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.learn.R;

/**
 * 并发线程安全问题
 * 作者：wjh on 2019-07-22 19:31
 * <p>
 * 原理：可以这么理解，每个实例化的对象都有一个公共的锁，该锁被该实例共享。
 * 因此对于该对象的所有被synchronized修饰的实例方法，是共享的同一个对象锁。
 * 同理，类锁也是一样的，伴随Class对象的生成，也会有一个类监视器，也就有一个默认的类锁了，被synchronized修饰的所有静态方法都共享一个类锁。
 */
public class SynchronizedActivity extends AppCompatActivity {


    private static int d;
    private int c = 0;

    /**
     * 类锁 - 修饰静态方法
     * SynchronizedActivity.class
     *
     * @param it
     */
    public static synchronized void onHandle2(String it) {
        Log.e("Learn", "我来了-" + it);
        ++d;
        Log.e("Learn", "d-" + d);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item);

        findViewById(R.id.tv_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initSynchronized();
            }
        });
        findViewById(R.id.tv_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    /***** synchronized ************************************************************/
    public void initSynchronized() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i < 5; i++) {
                    onHandle3("one");
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i < 5; i++) {
                    onHandle3("tow");
                }
            }
        }).start();
    }

    /**
     * 对象锁 - 修饰实例方法
     * 增加synchronized关键字后 可以让多个线程排队执行这个方法，可以保证当前时间里只有一个线程在执行。
     *
     * @param it
     */
    private synchronized void onHandle(String it) {
        Log.e("Learn", "我来了-" + it);
        ++c;
        Log.e("Learn", "c-" + c);
    }

    /**
     * 对象锁 - 修饰代码块
     * 增加synchronized关键字后 可以让多个线程排队执行这个方法，可以保证当前时间里只有一个线程在执行。
     *
     * @param it
     */
    private void onHandle3(String it) {
        synchronized (this) {
            Log.e("Learn", "我来了-" + it);
            ++c;
            Log.e("Learn", "c-" + c);
        }
    }
}