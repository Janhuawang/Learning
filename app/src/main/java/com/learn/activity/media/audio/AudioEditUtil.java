package com.learn.activity.media.audio;

import android.content.Context;

import com.frank.callback.AudioEditImpl;
import com.frank.command.FFMPegAudioEditHelper;
import com.medialib.audioedit.service.AudioTaskCreator;

import java.io.File;

/**
 * 音频处理
 * 作者：wjh on 2019-12-11 20:57
 */
public class AudioEditUtil {


    /**
     * 音频 混合
     *
     * @param context
     * @param path1
     * @param path2
     * @param volume1 0~1
     * @param volume2 0~1
     */
    public static void onMixAudio(Context context, String path1, String path2, float volume1, float volume2) {
        AudioTaskCreator.createMixAudioTask(context, path1, path2, 1f, 0.2f);
    }

    /**
     * 音频 插入、拼接
     *
     * @param context
     * @param path1
     * @param path2
     * @param insertPointTime 插入到path1音频的时间入口 单位秒
     */
    public static void onInsertAudio(Context context, String path1, String path2, float insertPointTime) {
        AudioTaskCreator.createInsertAudioTask(context, path1, path2, insertPointTime);
    }

    /**
     * 音频 裁剪
     *
     * @param context
     * @param path1
     * @param startTime 开始时间 单位秒
     * @param endTime   结束时间 单位秒
     */
    public static void onCutAudio(Context context, String path1, float startTime, float endTime, final AudioEditImpl handleCallback) {
        AudioTaskCreator.createCutAudioTask(context, path1, startTime, endTime);

//        File file = new File(path1);
//        if (file.isFile()) {
//            int index = path1.lastIndexOf(".");
//            if (index > -1) {
//                String before = path1.substring(0, index);
//                String after = path1.substring(index, path1.length());
//                String targetFile = before + "_" + System.currentTimeMillis() + after;
//
//                FFMPegAudioEditHelper.cutAudio(path1, (int) startTime, (int) endTime, targetFile, handleCallback);
//            }
//        }
    }

    /**
     * 音频淡入
     *
     * @param path1
     * @param handleCallback
     */
    public static void audioFadeInTest(String path1, final AudioEditImpl handleCallback) {
        File file = new File(path1);
        if (file.isFile()) {
            int index = path1.lastIndexOf(".");
            if (index > -1) {
                String before = path1.substring(0, index);
                String after = path1.substring(index, path1.length());
                String targetFile = before + System.currentTimeMillis() + after;

                FFMPegAudioEditHelper.audioFadeIn(path1, targetFile, handleCallback);
            }
        }

    }
}
