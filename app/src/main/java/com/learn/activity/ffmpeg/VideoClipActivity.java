package com.learn.activity.ffmpeg;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

//import com.esay.ffmtool.FfmpegTool;
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
    private final int IMAGE_NUM = 10;// 每一屏图片的数量
    private final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1009;
    private String videoPath = "/storage/emulated/0/DCIM/Camera/VID_20191010_185014.mp4";
    private String outPutClipDir;
    private String outPutImgDir;
    private int startTime = 0;
    private int endTime = 10;
    /**
     * 执行线程
     */
    private ExecutorService executorService = Executors.newFixedThreadPool(3);
    /**
     * ffmpeg工具类
     */
//    private FfmpegTool ffmpegTool;
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

    @Override
    protected void initData() {
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
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
            return;
        }
        if (!new File(videoPath).exists()) {
            Toast.makeText(this, "视频文件不存在", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        outPutClipDir = FileUtil.getDCIMPath(this, "clip");
        File file = new File(outPutClipDir);
        if (!file.exists()) {
            file.mkdirs();
        }

        outPutImgDir = FileUtil.getDCIMPath(this, "img");
        File fileImg = new File(outPutImgDir);
        if (!fileImg.exists()) {
            fileImg.mkdirs();
        }

        /*ffmpegTool = FfmpegTool.getInstance(this);
        ffmpegTool.setImageDecodeing(new FfmpegTool.ImageDecodeing() {
            @Override
            public void sucessOne(String s, int i) {
                ToastUtil.showText("s---" + s);
            }
        });*/

        runImagDecodTask(0, 5 * IMAGE_NUM);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (R.id.tv_clip == v.getId()) {
            tv_state.setText("裁剪中。。。");
           /* executorService.execute(new Runnable() {
                @Override
                public void run() {
                    String video = outPutClipDir + File.separator + "clip" + System.currentTimeMillis() / 1000 + ".mp4";
                    //参数说明 视频源  输出结果地址 开始时间单位s  视频时长单位s  标志位  回调
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
            });*/
        }
    }

    /**
     * 运行一个图片的解码任务
     *
     * @param start 解码开始的视频时间 秒
     * @param count 一共解析多少张
     */
    private void runImagDecodTask(final int start, final int count) {
        /*executorService.execute(new Runnable() {
            @Override
            public void run() {
                ffmpegTool.decodToImageWithCall(videoPath, outPutImgDir + "/", start, count);
            }
        });*/
    }
}
