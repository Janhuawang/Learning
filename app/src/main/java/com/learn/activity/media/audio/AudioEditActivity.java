package com.learn.activity.media.audio;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.frank.callback.AudioEditImpl;
import com.learn.R;
import com.learn.activity.media.audio.player.AudioPlayerMixer;
import com.learn.activity.media.audio.player.IAudioPlayer;
import com.learn.base.BaseActivity;
import com.learn.util.TimeUtil;
import com.medialib.audioedit.bean.AudioMsg;
import com.medialib.audioedit.util.FileUtils;
import com.medialib.audioedit.util.ToastUtil;
import com.medialib.audioeditc.AudioMain;
import com.medialib.audioeditc.HandleCallback;
import com.medialib.audioeditc.MixParam;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

/**
 * 音频编辑页
 * 作者：wjh on 2019-12-10 16:43
 */
public class AudioEditActivity extends BaseActivity {
    private static final int REQUEST_AUDIO_CODE = 1;
    private final int MSG_BEGIN = 11;
    private final int MSG_PROGRESS = 12;
    private final int MSG_FINISH = 13;
    private final int MSG_PLAYER_TIME = 21;

    private TextView tv_path_1, tv_path_2, tv_path_3, tv_log;
    private SeekBar seek_bar_time, seek_bar_volume;
    private TextView tv_start_time, tv_end_time;
    private int mCurPickBtnId;

    private String mCurPath = "/storage/emulated/0/AudioEdit/audio/out.wav";
    private StringBuilder logBuilder = new StringBuilder();

    /**
     * 音频播放器
     */
    private AudioPlayerMixer mAudioPlayer;

    /**
     * UI线程处理
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_BEGIN:
                    showProgress(true);
                    updateLog(true);
                    break;

                case MSG_PROGRESS:
                    break;

                case MSG_FINISH:
                    showProgress(false);
                    updateLog(false);
                    break;

                case MSG_PLAYER_TIME:
                    int[] times = (int[]) msg.obj;
                    tv_start_time.setText(TimeUtil.secToTime(times[0]));
                    tv_end_time.setText(TimeUtil.secToTime(times[1]));
                    break;

                default:
                    break;
            }
        }
    };
    /**
     * 回调
     */
    private AudioEditImpl audioEdit = new AudioEditImpl() {
        @Override
        public void onProgress(int dts, int type) {

        }

        @Override
        public void onBegin() {
            mHandler.obtainMessage(MSG_BEGIN).sendToTarget();
        }

        @Override
        public void onEnd(int result) {
            mHandler.obtainMessage(MSG_FINISH).sendToTarget();
        }
    };
    /**
     * 临时变量：总时长
     */
    private int tempTotalTime;

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
        seek_bar_time = findViewById(R.id.seek_bar_time);
        seek_bar_volume = findViewById(R.id.seek_bar_volume);
        tv_start_time = findViewById(R.id.tv_start_time);
        tv_end_time = findViewById(R.id.tv_end_time);
    }

    @Override
    protected void initListener() {
        findViewById(R.id.btn_mix).setOnClickListener(this);
        findViewById(R.id.btn_cut).setOnClickListener(this);
        findViewById(R.id.btn_insert).setOnClickListener(this);
        findViewById(R.id.tv_play).setOnClickListener(this);
        findViewById(R.id.tv_pause).setOnClickListener(this);
        findViewById(R.id.tv_stop).setOnClickListener(this);

        findViewById(R.id.tv_done).setOnClickListener(this);
        findViewById(R.id.btn_fade_in).setOnClickListener(this);
        findViewById(R.id.btn_fade_out).setOnClickListener(this);
        findViewById(R.id.btn_config).setOnClickListener(this);

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

        seek_bar_time.setMax(100);
        seek_bar_time.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //SeekBar 滑动时的回调函数，其中 fromUser 为 true 时是手动调节
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //SeekBar 开始滑动的的回调函数
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //SeekBar 停止滑动的回调函数
                mAudioPlayer.seek((int) (seekBar.getProgress() * tempTotalTime / 100f));
            }
        });

        seek_bar_volume.setMax(100);
        seek_bar_volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //SeekBar 滑动时的回调函数，其中 fromUser 为 true 时是手动调节
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //SeekBar 开始滑动的的回调函数
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //SeekBar 停止滑动的回调函数
                mAudioPlayer.setBgmVolume(seekBar.getProgress() / 100f);
            }
        });
    }

    @Override
    protected void initData() {
        tv_path_1.setText("/storage/emulated/0/aavv/戴青塔娜-寂静的天空.wav");
        tv_path_2.setText("/storage/emulated/0/aavv/卡奇社-别来无恙.wav");
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_mix: // 混合
                mixAudio();
                break;

            case R.id.btn_cut: // 裁剪
                cutAudio();
                break;

            case R.id.btn_insert: // 插入拼接
                insertAudio();
                break;

            case R.id.btn_fade_in: // 淡入
                if (mAudioPlayer != null) {
                    mAudioPlayer.updateConfig(false, 60, false, 60, false);
                }
                break;

            case R.id.btn_fade_out:// 淡出
                if (mAudioPlayer != null) {
                    mAudioPlayer.updateConfig(false, 60, false, 60, false);
                }
                break;

            case R.id.btn_config:// 配置
                if (mAudioPlayer != null) {
                    mAudioPlayer.updateConfig(true, 60, true, 60, true);
                }
                break;

            case R.id.tv_play: // 播放
                if (mAudioPlayer != null) {
                    mAudioPlayer.play();
                } else {
                    playAudio();
                }
                break;

            case R.id.tv_pause: // 暂停
                if (mAudioPlayer != null) {
                    mAudioPlayer.pause();
                }
                break;

            case R.id.tv_stop: // 停止
                if (mAudioPlayer != null) {
                    mAudioPlayer.stop();
                }
                break;

            case R.id.tv_done: // 合并
                done();
                break;

        }
    }

    @Override
    protected void onDestroy() {
        if (mAudioPlayer != null) {
            mAudioPlayer.release();
        }
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

        mHandler.obtainMessage(MSG_BEGIN).sendToTarget();

        AudioEditUtil.onMixAudio(AudioEditActivity.this, path1, path2, 1f, 0.2f);
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

        mHandler.obtainMessage(MSG_BEGIN).sendToTarget();

        AudioEditUtil.onInsertAudio(AudioEditActivity.this, path1, path2, insertPointTime);
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
        float startTime = 20;
        float endTime = 40;

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

        AudioEditUtil.onCutAudio(AudioEditActivity.this, path1, startTime, endTime, audioEdit);
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
     * 完成操作
     */
    private void done() {
        String path1 = tv_path_1.getText().toString();
        String path2 = tv_path_2.getText().toString();

        if (TextUtils.isEmpty(path1) || TextUtils.isEmpty(path2)) {
            ToastUtil.showToast("音频路径为空");
            return;
        }

        String path3 = null;
        File file = new File(path1);
        if (file.isFile()) {
            int index = path1.lastIndexOf(".");
            if (index > -1) {
                String before = path1.substring(0, index);
                String after = path1.substring(index, path1.length());
                path3 = before + "_Mix" + System.currentTimeMillis() + after;
            }

            MixParam mixParam = new MixParam();
            mixParam.testStr = "hello!!!";
            mixParam.fadeIn = true;
            mixParam.fadeInSec = 10;
            mixParam.fadeOut = true;
            mixParam.fadeOutSec = 20;
            mixParam.loop = true;
            mixParam.startSec = 10;
            mixParam.volumeRate = 1.0f;
            AudioMain.mixAudio(path1, path2, path3, mixParam, new HandleCallback() {
                @Override
                public void onBegin() {
                    mHandler.obtainMessage(MSG_BEGIN).sendToTarget();
                }

                @Override
                public void onCallback(String log, int type) {

                }

                @Override
                public void onEnd(int result) {
                    mHandler.obtainMessage(MSG_FINISH).sendToTarget();
                }
            });
        }
    }

    /**
     * 播放声音
     */
    private void playAudio() {
        String path1 = tv_path_1.getText().toString();
        String path2 = tv_path_2.getText().toString();

        if (TextUtils.isEmpty(path1) || TextUtils.isEmpty(path2)) {
            ToastUtil.showToast("音频路径为空");
            return;
        }

        if (mAudioPlayer == null) {
            mAudioPlayer = new AudioPlayerMixer();
            mAudioPlayer.addPlayerCallback(new IAudioPlayer.AudioPlayerCallback() {
                @Override
                public void playProgress(int totalTime, int playTime) {
                    tempTotalTime = totalTime;
                    seek_bar_time.setProgress((int) (playTime * 100f / totalTime));
                    mHandler.obtainMessage(MSG_PLAYER_TIME, new int[]{playTime, totalTime}).sendToTarget();
                    Log.d("onPeriodicNotification", "totalTime:" + totalTime + "  playTime：" + playTime);
                }
            });
        }
        mAudioPlayer.setWave(true);
        mAudioPlayer.setSrcPath(path1);
        mAudioPlayer.setCoverPath(path2);
        mAudioPlayer.setInsertTime(20);
        mAudioPlayer.prepare();
        mAudioPlayer.play();
    }

    /**
     * 打印日志
     *
     * @param isStart
     */
    private void updateLog(boolean isStart) {
        if (tv_log != null && logBuilder != null) {
            logBuilder.append((isStart ? "开始时间: " : "结束时间: ") + TimeUtil.getNowTimeStr("yyyy-MM-dd HH:mm:ss")).append("\n");
            if (!isStart) {
                logBuilder.append("\n\n");
            }
            tv_log.setText(logBuilder.toString());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveAudioMsg(AudioMsg msg) {
        if (msg != null && !TextUtils.isEmpty(msg.msg)) {
            tv_path_3.setText(msg.msg);
            mCurPath = msg.path;

            if (msg.isDone()) {
                mHandler.obtainMessage(MSG_FINISH).sendToTarget();
            }
        }
    }
}
