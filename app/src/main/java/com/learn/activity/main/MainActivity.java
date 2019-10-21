package com.learn.activity.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.learn.R;
import com.learn.activity.dialog.rotating.DialogRotatingActivity;
import com.learn.activity.ffmpeg.VideoClipActivity;
import com.learn.activity.ffmpeg.VideoHandleActivity;
import com.learn.activity.glidedemo.sample.ZoomActivity;
import com.learn.activity.thread.AsyncTaskActivity;
import com.learn.activity.thread.LockActivity;
import com.learn.activity.thread.SynchronizedActivity;
import com.learn.activity.thread.VolatileActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<ItemBean> lists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initView();
    }

    private void initData() {
        lists.add(new ItemBean("线程Lock", LockActivity.class.getName()));
        lists.add(new ItemBean("线程Synchronized", SynchronizedActivity.class.getName()));
        lists.add(new ItemBean("线程volatile", VolatileActivity.class.getName()));
        lists.add(new ItemBean("弹框Rotating", DialogRotatingActivity.class.getName()));
        lists.add(new ItemBean("AsyncTask", AsyncTaskActivity.class.getName()));
        lists.add(new ItemBean("Zoom", ZoomActivity.class.getName()));
        lists.add(new ItemBean("视频剪辑", VideoClipActivity.class.getName()));
        lists.add(new ItemBean("视频拼接", VideoHandleActivity.class.getName()));
    }

    private void initView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ItemAdapter itemAdapter = new ItemAdapter(this, lists);
        recyclerView.addItemDecoration(new android.support.v7.widget.DividerItemDecoration(this, android.support.v7.widget.DividerItemDecoration.VERTICAL));   // 设置分割线使用的divider
        recyclerView.setAdapter(itemAdapter);
    }
}
