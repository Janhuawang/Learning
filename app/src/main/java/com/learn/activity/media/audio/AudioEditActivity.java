package com.learn.activity.media.audio;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.learn.R;
import com.learn.base.BaseActivity;
import com.learn.util.TimeUtil;
import com.medialib.audioedit.bean.AudioMsg;
import com.medialib.audioedit.service.AudioTaskCreator;
import com.medialib.audioedit.util.FileUtils;
import com.medialib.audioedit.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

/**
 * 作者：wjh on 2019-12-10 16:43
 */
public class AudioEditActivity extends BaseActivity {
    private static final int REQUEST_AUDIO_CODE = 1;

    private TextView tv_path_1, tv_path_2, tv_path_3, tv_log;

    private int mCurPickBtnId;
    private String mCurPath = "/storage/emulated/0/AudioEdit/audio/out.wav";
    private StringBuilder stringBuilder = new StringBuilder();

    public static Uri fromFile(Context context, File file) {
        if (context == null || file == null) {
            return null;
        }
        Uri fileUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // 对应的是Manifest定义的 authorities
            fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
        } else {
            fileUri = Uri.fromFile(file);
        }
        context.grantUriPermission(context.getPackageName(), fileUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        context.grantUriPermission(context.getPackageName(), fileUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return fileUri;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_audio_edit;
    }

    @Override
    protected void initView() {
        tv_path_1 = findViewById(R.id.tv_path_1);
        tv_path_2 = findViewById(R.id.tv_path_2);
        tv_path_3 = findViewById(R.id.tv_path_3);
        tv_log = findViewById(R.id.tv_log);
    }

    @Override
    protected void initListener() {
        findViewById(R.id.btn_mix).setOnClickListener(this);
        findViewById(R.id.btn_cut).setOnClickListener(this);
        findViewById(R.id.btn_insert).setOnClickListener(this);
        findViewById(R.id.tv_start).setOnClickListener(this);
        tv_path_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickAudioFile(v.getId());
            }
        });
        tv_path_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickAudioFile(v.getId());
            }
        });
    }

    @Override
    protected void initData() {
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_mix:
                mixAudio();
                break;

            case R.id.btn_cut:
                cutAudio();
                break;

            case R.id.btn_insert:
                insertAudio();
                break;

            case R.id.tv_start:
                playAudio(mCurPath);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_AUDIO_CODE) {
                String path = FileUtils.queryFilePath(AudioEditActivity.this, data.getData());

                switch (mCurPickBtnId) {
                    case R.id.tv_path_1:
                        tv_path_1.setText(path);
                        break;
                    case R.id.tv_path_2:
                        tv_path_2.setText(path);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 混合
     */
    private void mixAudio() {
        String path1 = tv_path_1.getText().toString();
        String path2 = tv_path_2.getText().toString();

        if (TextUtils.isEmpty(path1) || TextUtils.isEmpty(path2)) {
            ToastUtil.showToast("音频路径为空");
            return;
        }

        showProgress(true);
        updateLog(true);

        AudioTaskCreator.createMixAudioTask(AudioEditActivity.this, path1, path2, 1f, 0.2f);
    }

    /**
     * 插入
     */
    private void insertAudio() {
        String path1 = tv_path_1.getText().toString();
        String path2 = tv_path_2.getText().toString();

        if (TextUtils.isEmpty(path1) || TextUtils.isEmpty(path2)) {
            ToastUtil.showToast("音频路径为空");
            return;
        }

        float path1Time = FileUtils.getFilePlayTime(AudioEditActivity.this, new File(path1)) / 1000f;
        float insertPointTime = path1Time; // 目前设置插入前为音频尾

        if (insertPointTime < 0 || insertPointTime > path1Time) {
            ToastUtil.showToast("开始时间不正确");
            return;
        }

        showProgress(true);
        updateLog(true);

        AudioTaskCreator.createInsertAudioTask(AudioEditActivity.this, path1, path2, insertPointTime);
    }

    /**
     * 裁剪音频
     */
    private void cutAudio() {
        String path1 = tv_path_1.getText().toString();

        if (TextUtils.isEmpty(path1)) {
            ToastUtil.showToast("音频路径为空");
            return;
        }
        float startTime = 1;
        float endTime = 1 * 60;

        if (startTime <= 0) {
            ToastUtil.showToast("时间不对");
            return;
        }
        if (endTime <= 0) {
            ToastUtil.showToast("时间不对");
            return;
        }
        if (startTime >= endTime) {
            ToastUtil.showToast("时间不对");
            return;
        }

        showProgress(true);
        updateLog(true);

        AudioTaskCreator.createCutAudioTask(AudioEditActivity.this, path1, startTime, endTime);
    }

    /**
     * 是否展示进度
     *
     * @param isShow
     */
    private void showProgress(boolean isShow) {
        findViewById(R.id.progress_video).setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    /**
     * 选取音频文件
     */
    private void pickAudioFile(int viewId) {
        mCurPickBtnId = viewId;

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_AUDIO_CODE);
    }

    /**
     * 播放声音
     *
     * @param path
     */
    private void playAudio(String path) {
        if (TextUtils.isEmpty(path)) {
            ToastUtil.showToast("播放路径为空");
            return;
        }

        try {
            Intent it = new Intent(Intent.ACTION_VIEW);
            it.setDataAndType(fromFile(AudioEditActivity.this, new File(path)), "audio/*");
            startActivity(it);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 打印日志
     *
     * @param isStart
     */
    private void updateLog(boolean isStart) {
        if (tv_log != null && stringBuilder != null) {
            stringBuilder.append((isStart ? "开始时间: " : "结束时间: ") + TimeUtil.getNowTimeStr("yyyy-MM-dd HH:mm:ss")).append("\n");
            if (!isStart) {
                stringBuilder.append("\n\n");
            }
            tv_log.setText(stringBuilder.toString());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveAudioMsg(AudioMsg msg) {
        if (msg != null && !TextUtils.isEmpty(msg.msg)) {
            tv_path_3.setText(msg.msg);
            mCurPath = msg.path;

            if (msg.isDone()) {
                showProgress(false);
                updateLog(false);
            }
        }
    }
}
