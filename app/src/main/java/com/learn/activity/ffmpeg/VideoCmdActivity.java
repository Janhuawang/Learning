package com.learn.activity.ffmpeg;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.frank.command.FFmpegCmd;
import com.frank.command.FFmpegUtil;
import com.learn.R;
import com.learn.activity.ffmpeg.formate.VideoLayout;
import com.learn.util.FileUtil;
import com.learn.util.TimeUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * 命令行
 */
public class VideoCmdActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = VideoCmdActivity.class.getSimpleName();
    private static final int MSG_BEGIN = 101;
    private static final int MSG_PROGRESS = 102;
    private static final int MSG_FINISH = 103;

    private static final String PATH = Environment.getExternalStorageDirectory().getPath() + File.separator + "AVideoCmd";
    private static final String OutDir = PATH + File.separator + "out";
    private ProgressBar progress_video;
    private TextView tv_log;
    private StringBuilder stringBuilder = new StringBuilder();

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_BEGIN:
                    stringBuilder.append("开始时间: " + TimeUtil.getNowTimeStr("yyyy-MM-dd HH:mm:ss")).append("\n");
                    updateLog();
                    progress_video.setProgress(0);
                    progress_video.setVisibility(View.VISIBLE);
                    setGone();
                    break;

                case MSG_PROGRESS:
                    int progress = progress_video.getProgress();
                    int o = (Integer) msg.obj;
                    if (o > progress) {
                        progress_video.setProgress(o);
                    }
                    break;

                case MSG_FINISH:
                    stringBuilder.append("结束时间: " + TimeUtil.getNowTimeStr("yyyy-MM-dd HH:mm:ss")).append("\n").append("------------------------").append("\n");
                    updateLog();

                    progress_video.setVisibility(View.GONE);
                    setVisible();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_video_handle);

        intView();

        initPath();
    }

    /**
     * 初始化路径
     */
    private void initPath() {
        File outFile = new File(OutDir);
        if (!outFile.exists()) {
            outFile.mkdirs();
        }

        File af = new File(PATH + File.separator + "a.mp4");
        if (!af.isFile()) {
            try {
                //InputStream is = this.getResources().openRawResource(R.raw.bjbj);
                InputStream is = this.getResources().getAssets().open("a.mp4");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                FileOutputStream fos = new FileOutputStream(af);
                fos.write(buffer);
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        File bf = new File(PATH + File.separator + "b.mp4");
        if (!bf.isFile()) {
            try {
                //InputStream is = this.getResources().openRawResource(R.raw.bjbj);
                InputStream is = this.getResources().getAssets().open("a.mp4");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                FileOutputStream fos = new FileOutputStream(bf);
                fos.write(buffer);
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Toast.makeText(VideoCmdActivity.this, "文件初始完成！", Toast.LENGTH_SHORT).show();
    }

    private void intView() {
        progress_video = findViewById(R.id.progress_video);
        progress_video.setMax(100);
        tv_log = findViewById(R.id.tv_log);
        findViewById(R.id.btn_multi_video).setOnClickListener(this);
        findViewById(R.id.btn_multi_video_splice).setOnClickListener(this);
        findViewById(R.id.btn_video_cut).setOnClickListener(this);
        findViewById(R.id.btn_video_frame).setOnClickListener(this);
    }

    private void setVisible() {
        findViewById(R.id.btn_multi_video).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_multi_video_splice).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_video_cut).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_video_frame).setVisibility(View.VISIBLE);
    }

    private void setGone() {
        findViewById(R.id.btn_multi_video).setVisibility(View.GONE);
        findViewById(R.id.btn_multi_video_splice).setVisibility(View.GONE);
        findViewById(R.id.btn_video_cut).setVisibility(View.GONE);
        findViewById(R.id.btn_video_frame).setVisibility(View.GONE);
    }

    /**
     * 更新日志
     */
    private void updateLog() {
        if (tv_log != null && stringBuilder != null) {
            tv_log.setText(stringBuilder.toString());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_multi_video: {
                /**
                 * 视频画面拼接:分辨率、时长、封装格式不一致时，先把视频源转为一致.
                 * a视频与b视频画面合并后的效果是:c视频中有a+b两个画面。
                 */
                String input1 = PATH + File.separator + "a.mp4";
                String input2 = PATH + File.separator + "b.mp4";
                String outputFile = OutDir + File.separator + "ab.mp4";
                if (!FileUtil.checkFileExist(input1) || !FileUtil.checkFileExist(input2)) {
                    Toast.makeText(VideoCmdActivity.this, "有文件不存在！", Toast.LENGTH_SHORT).show();
                    return;
                }
                String[] commandLine = FFmpegUtil.multiVideo(input1, input2, outputFile, VideoLayout.LAYOUT_HORIZONTAL);
                executeFFmpegCmd(commandLine, 0, null);
                break;
            }

            case R.id.btn_video_frame: { //视频转图片
                String srcFile = PATH + File.separator + "a.mp4";
                if (!FileUtil.checkFileExist(srcFile)) {
                    return;
                }
                String imagePath = OutDir + File.separator + "Video2Image/"; // 图片保存路径
                File imageFile = new File(imagePath);
                if (!imageFile.exists()) {
                    boolean result = imageFile.mkdir();
                    if (!result) {
                        return;
                    }
                }
                int mStartTime = 10;//开始时间
                int mDuration = 20;//持续时间（注意开始时间+持续时间之和不能大于视频总时长）
                int mFrameRate = 10;//帧率（从视频中每秒抽多少帧）
                String[] commandLine = FFmpegUtil.videoToImage(srcFile, mStartTime, mDuration, mFrameRate, imagePath);
                executeFFmpegCmd(commandLine, 0, null);
                break;
            }

            /**
             * 视频拼接
             */
            case R.id.btn_multi_video_splice: {
                String input1 = PATH + File.separator + "a.mp4";
                String input2 = PATH + File.separator + "b.mp4";
                String output = OutDir + File.separator + "abConcat.mp4";

                File concatFile = new File(OutDir + File.separator + "fileList.txt");
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(concatFile);
                    fileOutputStream.write(("file \'" + input1 + "\'").getBytes());
                    fileOutputStream.write("\n".getBytes());
                    fileOutputStream.write(("file \'" + input2 + "\'").getBytes());
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String[] commandLine = FFmpegUtil.concatVideo(concatFile.getAbsolutePath(), output);

//                List<String> vList = new ArrayList<>();
//                vList.add(input1);
//                vList.add(input2);
//
//                String[] commandLine = FFmpegUtil.concatVideo(vList, output);
                executeFFmpegCmd(commandLine, 0, null);
                break;
            }

            /**
             * 视频剪切
             */
            case R.id.btn_video_cut:
//                String srcFile = PATH + File.separator + "a.mp4";
//                String srcFile = "/storage/emulated/0/DCIM/Camera/VID_20190717_220948176.mp4";
//                String srcFile = "/storage/emulated/0/DCIM/Camera/VID_20191107_213834608.mp4";
                String srcFile = "/storage/emulated/0/aaaa/a.mp4";

                if (!FileUtil.checkFileExist(srcFile)) {
                    return;
                }
                String output = OutDir + File.separator + "cutA.mp4";
                int startTime = 1;
                int duration = 59;
//                String[] commandLine = FFmpegUtil.cutVideoCopyts(srcFile, TimeUtil.secToTime(startTime), TimeUtil.secToTime(startTime+duration), output);
                cutVideo(srcFile, startTime, duration, output);
                break;
        }
    }

    /**
     * 视频剪辑
     *
     * @param srcFile
     * @param startTime
     * @param duration
     * @param output
     */
    private void cutVideo(String srcFile, final int startTime, final int duration, String output) {
        String[] commandLine = FFmpegUtil.cutVideoS6(srcFile, startTime, duration, output);

        FFmpegCmd.execute(commandLine, FFmpegCmd.BUSINESS_TYPE_CUT, new FFmpegCmd.OnHandleListener() {
            @Override
            public void onBegin() {
                Log.i(TAG, "handle video onBegin...");
                mHandler.obtainMessage(MSG_BEGIN).sendToTarget();
            }

            @Override
            public void onProgress(int time, int type) {
                if (time > 0) {
                    float seconds = time / 1000f / 1000;
                    float progress = seconds - startTime;
                    float max = duration - startTime;
                    int percentage = (int) (progress * 100 / max);
                    mHandler.obtainMessage(MSG_PROGRESS, percentage).sendToTarget();
                }

            }

            @Override
            public void onEnd(int result) {
                Log.i(TAG, "handle video onEnd...");
                mHandler.obtainMessage(MSG_FINISH).sendToTarget();
            }
        });
    }

    /**
     * 执行ffmpeg命令行
     *
     * @param commandLine
     * @param type
     * @param onHandleListener
     */
    private void executeFFmpegCmd(final String[] commandLine, final int type, final FFmpegCmd.OnHandleListener onHandleListener) {
        FFmpegCmd.execute(commandLine, type, onHandleListener);
    }
}
