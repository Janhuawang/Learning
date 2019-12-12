package com.learn.activity.concurrent;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.learn.R;
import com.learn.activity.concurrent.sync.GetThread;
import com.learn.activity.concurrent.sync.PutThread;
import com.learn.activity.concurrent.sync.Queue;
import com.learn.base.BaseActivity;

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
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_start:
                Queue queue = new Queue(50);
                new Thread(new PutThread(queue)).start();
                new Thread(new GetThread(queue)).start();

                mHandler.obtainMessage(MSG_PROGRESS, "开始了").sendToTarget();
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


