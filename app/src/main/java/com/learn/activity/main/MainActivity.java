package com.learn.activity.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.learn.R;
import com.learn.activity.dialog.rotating.DialogRotatingActivity;
import com.learn.activity.ffmpeg.FFPlayerActivity;
import com.learn.activity.ffmpeg.VideoClipActivity;
import com.learn.activity.ffmpeg.VideoCmdActivity;
import com.learn.activity.ffmpeg.VideoFrameActivity;
import com.learn.activity.glidedemo.sample.ZoomActivity;
import com.learn.activity.loadfile.FileListActivity;
import com.learn.activity.thread.AsyncTaskActivity;
import com.learn.activity.thread.LockActivity;
import com.learn.activity.thread.SynchronizedActivity;
import com.learn.activity.thread.VolatileActivity;
import com.learn.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1009;

    private List<ItemBean> lists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
    }

    private void initData() {
        /**
         * Android6.0（API 级别 23）开始，所有的权限都必须弹窗向用户索要，用户中间还可以手动关闭权限，
         * 所以首次打开app时需要申请全部权限，有以写重要的权限，可以在每次用到时检查并索要。
         */
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) { // 如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true
                ToastUtil.showText("如果用户拒绝过一次，那就给一个申请权限的解释吧！");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
            return;
        }

        lists.add(new ItemBean("线程Lock", LockActivity.class.getName()));
        lists.add(new ItemBean("线程Synchronized", SynchronizedActivity.class.getName()));
        lists.add(new ItemBean("线程volatile", VolatileActivity.class.getName()));
        lists.add(new ItemBean("弹框Rotating", DialogRotatingActivity.class.getName()));
        lists.add(new ItemBean("AsyncTask", AsyncTaskActivity.class.getName()));
        lists.add(new ItemBean("Zoom", ZoomActivity.class.getName()));
        lists.add(new ItemBean("视频剪辑库", VideoClipActivity.class.getName()));
        lists.add(new ItemBean("ffmpeg命令行", VideoCmdActivity.class.getName()));
        lists.add(new ItemBean("ffmpeg播放器", FFPlayerActivity.class.getName()));
        lists.add(new ItemBean("遍历本地文件", FileListActivity.class.getName()));
        lists.add(new ItemBean("获取视频帧", VideoFrameActivity.class.getName()));

        initView();
    }

    private void initView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ItemAdapter itemAdapter = new ItemAdapter(this, lists);
        recyclerView.addItemDecoration(new android.support.v7.widget.DividerItemDecoration(this, android.support.v7.widget.DividerItemDecoration.VERTICAL));   // 设置分割线使用的divider
        recyclerView.setAdapter(itemAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: // 权限回来了
                if (grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initData();
                }
                break;
        }
    }
}
