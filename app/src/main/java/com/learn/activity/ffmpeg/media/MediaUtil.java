package com.learn.activity.ffmpeg.media;

import android.media.MediaPlayer;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;

/**
 * 作者：wjh on 2019-07-17 18:14
 */
public class MediaUtil {

    /**
     * //获得媒体文件的时长（以毫秒为单位）
     *
     * @param videoPath
     * @return
     */
    public static long getVideoTime(String videoPath) {
        if (!TextUtils.isEmpty(videoPath)) {
            File file = new File(videoPath);
            if (file.isFile()) {
                MediaPlayer mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(file.getPath());
                    mediaPlayer.prepare();
                    return mediaPlayer.getDuration();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        return 0;
    }
}
