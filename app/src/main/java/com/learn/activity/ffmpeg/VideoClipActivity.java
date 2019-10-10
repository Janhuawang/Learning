package com.learn.activity.ffmpeg;

import android.view.View;
import android.widget.TextView;

import com.esay.ffmtool.FfmpegTool;
import com.learn.R;
import com.learn.base.BaseActivity;
import com.learn.util.FileUtil;
import com.learn.util.ToastUtil;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 视频剪裁
 * 作者：wjh on 2019-10-10 21:25
 */
public class VideoClipActivity extends BaseActivity {

    private final String videoPath = "/storage/emulated/0/DCIM/Camera/VID_20191010_185014.mp4";
    private String outPutDir;
    private int startTime = 0;
    private int endTime = 5 * 1000;

    /**
     * 执行线程
     */
    private ExecutorService executorService = Executors.newFixedThreadPool(3);
    /**
     * ffmpeg工具类
     */
    private FfmpegTool ffmpegTool;
    /**
     * 剪切状态
     */
    private TextView tv_state;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_clip;
    }

    @Override
    protected void initView() {
        tv_state = findViewById(R.id.tv_clip);
        findViewById(R.id.tv_clip).setOnClickListener(this);
    }

    @Override
    protected void initListener() {
    }

    @Override
    protected void initData() {
        outPutDir = FileUtil.getDCIMPath(this, "clip");
        ffmpegTool = FfmpegTool.getInstance(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (R.id.tv_clip == v.getId()) {
            tv_state.setText("裁剪中。。。");
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    String video = outPutDir + File.separator + "clip" + System.currentTimeMillis() / 1000 + ".mp4";
                    ffmpegTool.clipVideo(videoPath, video, startTime, endTime - startTime, 2, new FfmpegTool.VideoResult() {
                        @Override
                        public void clipResult(int i, String s, String s1, boolean b, int i1) {
                            if (b) {
                                ToastUtil.showText("裁剪视频完成!");
                                tv_state.setText("裁剪视频完成!");
                            }
                        }
                    });
                }
            });
        }
    }
}
