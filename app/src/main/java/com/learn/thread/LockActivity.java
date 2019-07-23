package com.learn.thread;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.learn.R;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 加锁
 * 作者：wjh on 2019-07-22 19:31
 * <p>
 * 使用 ReentrantLock , lock()加锁 unlock()解锁
 * a线程lock()后，其它线程都会在lock()之前等候，
 * a线程unlock()后，其它线程会有一个b线程拿到锁,
 * b线程lock()后，其它线程会在继续等候。
 */
public class LockActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item);

        findViewById(R.id.tv_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockTest();
                //        lockReadWriteTest();
            }
        });
    }

    /***** ReentrantLock ************************************************************/
    private void lockHandle(String it, ReentrantLock reentrantLock) {
        Log.e("Learn", "我来了-" + it);
        reentrantLock.lock();
        Log.e("Learn", "拿到锁了-" + it);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            reentrantLock.unlock();
            Log.e("Learn", "释放锁了-" + it);
        }
    }

    private void lockTest() {
        final ReentrantLock reentrantLock = new ReentrantLock();
        Thread oneTread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i < 2; i++) {
                    lockHandle("One-" + i, reentrantLock);
                }
            }
        });

        Thread towTread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i < 2; i++) {
                    lockHandle("Tow-" + i, reentrantLock);
                }
            }
        });

        oneTread.start();
        towTread.start();

        try {
            Thread.sleep(500);
            oneTread.interrupt(); // 打断线程后会把锁释放掉的。
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /***** ReentrantReadWriteLock 目前测试没太大区别 ************************************************************/
    private void lockReadWriteTest() {
        ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();

        ReentrantReadWriteLock.WriteLock writeLock = reentrantReadWriteLock.writeLock();
        writeLock.lock();
        writeEvent();

        ReentrantReadWriteLock.ReadLock readLock = reentrantReadWriteLock.readLock();
        readLock.lock();
        readEvent();

    }

    private void readEvent() {
        Log.e("Learn", "readEvent-");
    }

    private void writeEvent() {
        Log.e("Learn", "writeEvent-");
    }

}