package com.learn.activity.concurrent.sync;

/**
 * 消费者线程
 * 作者：wjh on 2019-12-12 20:25
 */
public class GetThread implements Runnable {
    Queue queue;

    public GetThread(Queue queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        for (; ; ) {
            for (int i = 0; i < queue.getSize(); i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String value = queue.get(i);

            }
        }
    }
}
