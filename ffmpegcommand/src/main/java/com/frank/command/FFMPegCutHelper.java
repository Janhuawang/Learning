package com.frank.command;

import com.frank.callback.HandleCallback;
import com.frank.callback.VideoCutImpl;

/**
 * 作者：wjh on 2019-12-06 20:47
 */
public class FFMPegCutHelper {

    /**
     * 精确截取视频，避免关键帧的丢失并精确截取时间，重新编码整个剪辑的视频 无损的
     *
     * @param srcFile
     * @param startTime
     * @param duration
     * @param output
     * @return
     */
    public static void cutVideoX264Number(String srcFile, int startTime, int duration, String output, final VideoCutImpl handleCallback) {
        String cutVideoCmd = "ffmpeg -d -i %s -ss %d -t %d -c:v libx264 -c:a aac -strict experimental %s";
        cutVideoCmd = String.format(cutVideoCmd, srcFile, startTime, duration, output);
        executeFFMPegCmd(cutVideoCmd.split(" "), FFMPegType.BUSINESS_TYPE_VIDEO_CUT2, handleCallback);
    }

    /*
     * 非常准确 无损的
     * 非常耗时
     * 画面清晰
     * 让每一帧都成为关键帧，即由原来的帧间编码转换为帧内编码再进行裁剪，这样可以裁剪任意时间点，不受关键帧影响
     * */
    public static void cutVideoQScale(String srcFile, int startTime, int duration, String output, final VideoCutImpl handleCallback) {
        String cutVideoCmd = "ffmpeg -d -i %s -ss %d -t %d -strict -2 -qscale 0 -intra %s";
        cutVideoCmd = String.format(cutVideoCmd, srcFile, startTime, duration, output);
        executeFFMPegCmd(cutVideoCmd.split(" "), FFMPegType.BUSINESS_TYPE_VIDEO_CUT1, handleCallback);
    }

    /**
     * 非常准确 快速的 无损的
     *
     * @param srcFile
     * @param startTime
     * @param duration
     * @param output
     * @return
     */
    public static void cutVideoX264Fast(String srcFile, int startTime, int duration, String output, final VideoCutImpl handleCallback) {
        String cutVideoCmd = "ffmpeg -d -i %s -ss %d -t %d -c:v libx264 -preset ultrafast -c:a aac -strict experimental %s";
        cutVideoCmd = String.format(cutVideoCmd, srcFile, startTime, duration, output);
        executeFFMPegCmd(cutVideoCmd.split(" "), FFMPegType.BUSINESS_TYPE_VIDEO_CUT2, handleCallback);
    }

    /**
     * 执行命令行
     *
     * @param commandLine
     * @param handleCallback
     */
    private static void executeFFMPegCmd(final String[] commandLine, final int type, final HandleCallback handleCallback) {
        FFMPegMain.execute(commandLine, type, handleCallback);
    }
}
