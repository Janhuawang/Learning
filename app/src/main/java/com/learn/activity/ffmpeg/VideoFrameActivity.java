package com.learn.activity.ffmpeg;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.learn.R;
import com.learn.activity.ffmpeg.media.VideoExtractObj;
import com.learn.base.BaseActivity;
import com.learn.util.NumberUtil;
import com.learn.util.TimeUtil;

import java.io.File;

/**
 * 获取视频帧
 * 作者：wjh on 2019-11-06 11:11
 */
public class VideoFrameActivity extends BaseActivity {
    private static final int MSG_BEGIN = 101;
    private static final int MSG_FINISH = 102;
    private static final String PATH = Environment.getExternalStorageDirectory().getPath() + File.separator + "AVideoFrame";
    private static final String OutDir = PATH + File.separator + "out";
    /*视频地址*/
    private final String VIDEO_OUT_PUT_PATH = "https://umu-dev.bj.bcebos.com/lss-givnbfvdga4y7veh%2Frecording_20160920120343.mp4";
    /*每次拉取帧的个数*/
    private final int VIDEO_COVER_COUNT = 30;

    /**
     * 本地视频路径
     */
    private final String localPath = Environment.getExternalStorageDirectory().getPath() + File.separator + "ar" + File.separator + "aaaTest.mp4";

    /**
     * 日志
     */
    private StringBuilder builderLog = new StringBuilder();

    private ProgressBar progress_video;
    private TextView tv_log;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_BEGIN:
                    progress_video.setVisibility(View.VISIBLE);
                    break;
                case MSG_FINISH:
                    tv_log.setText("log= \n" + builderLog.toString());
                    progress_video.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_frame;
    }

    @Override
    protected void initView() {
        progress_video = findViewById(R.id.progress_video);
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
            case R.id.btn_start: // 开始
                File outFile = new File(OutDir);
                if (!outFile.exists()) {
                    outFile.mkdirs();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getVideoFrame();
                    }
                }).start();
                break;
        }
    }

    /**
     * 获取视频帧
     */
    private void getVideoFrame() {
        mHandler.obtainMessage(MSG_BEGIN).sendToTarget();
        VideoExtractObj videoExtractObj = new VideoExtractObj(VIDEO_OUT_PUT_PATH); // localPath VIDEO_OUT_PUT_PATH
        long endPosition = NumberUtil.parseLong(videoExtractObj.getVideoTimeMs());
        float scale = 0.2f;
        int w = (int) (videoExtractObj.getVideoWidth() * scale);
        int h = (int) (videoExtractObj.getVideoHeight() * scale);

        builderLog.append("start: " + TimeUtil.getNowTimeStr("yyyy-MM-dd HH:mm:ss SSS")).append("\n");

        videoExtractObj.extractFrameList(0, endPosition, VIDEO_COVER_COUNT, w, h, new VideoExtractObj.VideoExtractImp() {
            @Override
            public void callbackFrame(Bitmap bitmap) {
                if (bitmap == null) {
                    return;
                }

                builderLog.append(TimeUtil.getNowTimeStr("yyyy-MM-dd HH:mm:ss SSS")).append("\n");

              /*  String filePath = getCacheFilePath();
                File f = new File(filePath);
                if (!f.isFile()) {
                    try {
                        f.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    FileOutputStream out = new FileOutputStream(f);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                    bitmap.recycle();

                    builderLog.append(filePath);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }

            @Override
            public void done() {
                mHandler.obtainMessage(MSG_FINISH).sendToTarget();
            }
        });
    }

    /**
     * 短时间保存的缓存目录地址
     *
     * @return
     */
    private String getCacheFilePath() {
        return new StringBuilder().append(OutDir).append(File.separator).append("video_").append(String.valueOf(System.currentTimeMillis())).append(".jpg").toString();
    }
}


