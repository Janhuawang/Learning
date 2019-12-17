package com.learn.activity.concurrent;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.learn.R;
import com.learn.activity.concurrent.sync.GetThread;
import com.learn.activity.concurrent.sync.PutThread;
import com.learn.activity.concurrent.sync.Queue;
import com.learn.base.BaseActivity;

import java.util.concurrent.locks.LockSupport;

/**
 * 队列测试
 * 1、生产者与消费者
 * 作者：wjh on 2019-12-12 20:14
 */
public class SyncQueueActivity extends BaseActivity {
    private final int MSG_BEGIN = 11;
    private final int MSG_PROGRESS = 12;
    private final int MSG_FINISH = 13;
    private StringBuilder logBuilder = new StringBuilder();

    private TextView tv_log;

    /**
     * UI线程处理
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_BEGIN:
                    break;

                case MSG_PROGRESS:
                    String txt = (String) msg.obj;
                    updateLog(txt);
                    break;

                case MSG_FINISH:
                    break;
                default:
                    break;
            }
        }
    };
    /**
     * 测试
     */
    private boolean isPark;
    private Thread thread;
    private boolean isInterrupted;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_concurrent_sync_queue;
    }

    @Override
    protected void initView() {
        tv_log = findViewById(R.id.tv_log);
    }

    @Override
    protected void initListener() {
        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_start2).setOnClickListener(this);
        findViewById(R.id.btn_un_park).setOnClickListener(this);
        findViewById(R.id.btn_part).setOnClickListener(this);
        findViewById(R.id.btn_interrupt).setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_start:
                Queue queue = new Queue(10);
                new Thread(new PutThread(queue)).start();
                new Thread(new GetThread(queue)).start();

                mHandler.obtainMessage(MSG_PROGRESS, "开始了").sendToTarget();
                break;

            case R.id.btn_un_park:
                isPark = false;
                LockSupport.unpark(thread);
                Log.e("concurrent", "MSG: un part");
                break;


            case R.id.btn_part:
                isInterrupted = true;
                Log.e("concurrent", "MSG: Interrupted");
//                isPark = true;
//                Log.e("concurrent", "MSG: part");
                break;

            case R.id.btn_interrupt:
                thread.interrupt(); // 一个标识而已，可以中断sleep、wait 线程会出于阻塞状态，如果该线程被其他线程将中断状态标识置为true，则该线程 会从阻塞状态醒来，抛出InterruptedException，并将该线程的中断状态标识置为false。
                Log.e("concurrent", "MSG: interrupt");
                break;

            case R.id.btn_start2:
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (isInterrupted) {
                            for (int i = 0; i <= 100; i++) {
                                if (isPark) {
                                    LockSupport.park(); // 必须放在Run运行体当中，比如while for 循环当中，不然不会被执行。被执行后才会暂停，之后打开才会继续。
                                }

                                Log.e("concurrent", "MSG:" + i);

                                if (Thread.currentThread().isInterrupted()) {
                                    isInterrupted = false;
                                    Log.e("concurrent", "MSG: 中断了");
                                    break;
                                }
                            }
                        }
                    }
                });
                thread.start();
                break;
        }
    }

    /**
     * 打印日志
     *
     * @param
     */
    private void updateLog(String txt) {
        if (txt != null && tv_log != null && logBuilder != null) {
            logBuilder.append(txt);
            tv_log.setText(logBuilder.toString());
        }
    }
}


