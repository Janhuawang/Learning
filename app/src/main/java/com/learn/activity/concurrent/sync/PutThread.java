package com.learn.activity.concurrent.sync;

/**
 * 生产者线程
 * 作者：wjh on 2019-12-12 20:23
 */
public class PutThread implements Runnable{
    Queue queue;

    public PutThread(Queue queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        int i = 0;
        for (; ; ) {
            i++;
            queue.put(i + "号");

        }
    }
}
