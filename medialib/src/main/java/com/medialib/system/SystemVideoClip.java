package com.medialib.system;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.medialib.impl.NativeClipVideoCallback;
import com.medialib.system.mediacodec.VideoClipper;

import java.io.IOException;

/**
 *
 */
public class SystemVideoClip {

    /**
     * 部分视频可能会加速，无音频的视频需要特殊处理
     *
     * @param srcFile
     * @param startTime           微秒
     * @param duration            微妙
     * @param outFile
     * @param nativeVideoCallback
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void cutVideo(final String srcFile, final long startTime, final long duration, final String outFile, final NativeClipVideoCallback nativeVideoCallback) {
        if (nativeVideoCallback != null) {
            try {
                VideoClipper videoClipper = new VideoClipper();
                videoClipper.setInputVideoPath(srcFile);
                videoClipper.setOutputVideoPath(outFile);
                videoClipper.setOnVideoCutFinishListener(new VideoClipper.OnVideoCutFinishListener() {
                    @Override
                    public void onFinish() {
                        nativeVideoCallback.done(1);
                    }
                });
                videoClipper.clipVideo(startTime, duration);

            } catch (IOException e) {
                e.printStackTrace();
                nativeVideoCallback.done(0);
            }
        }
    }


}
