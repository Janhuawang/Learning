package com.frank.command;

/**
 * 作者：wjh on 2019-12-06 20:47
 */
public class FFmpegCutUtil {

    /**
     * 精确截取视频，避免关键帧的丢失并精确截取时间，重新编码整个剪辑的视频
     *
     * @param srcFile
     * @param startTime
     * @param duration
     * @param output
     * @return
     */
    public static String[] cutVideoX264Number(String srcFile, int startTime, int duration, String output) {
        String cutVideoCmd = "ffmpeg -d -i %s -ss %d -t %d -c:v libx264 -c:a aac -strict experimental %s";
        cutVideoCmd = String.format(cutVideoCmd, srcFile, startTime, duration, output);
        return cutVideoCmd.split(" ");
    }

    /*
     * 非常准确
     * 非常耗时
     * 画面清晰
     * 让每一帧都成为关键帧，即由原来的帧间编码转换为帧内编码再进行裁剪，这样可以裁剪任意时间点，不受关键帧影响
     * */
    public static String[] cutVideoQScale(String srcFile, int startTime, int duration, String output) {
        String cutVideoCmd = "ffmpeg -d -i %s -ss %d -t %d -strict -2 -qscale 0 -intra %s";
        cutVideoCmd = String.format(cutVideoCmd, srcFile, startTime, duration, output);
        return cutVideoCmd.split(" ");
    }

    /**
     * 非常准确 快速的
     *
     * @param srcFile
     * @param startTime
     * @param duration
     * @param output
     * @return
     */
    public static String[] cutVideoX264Fast(String srcFile, int startTime, int duration, String output) {
        String cutVideoCmd = "ffmpeg -d -i %s -ss %d -t %d -c:v libx264 -preset ultrafast -c:a aac -strict experimental %s";
        cutVideoCmd = String.format(cutVideoCmd, srcFile, startTime, duration, output);
//        String cutVideoCmd = "ffmpeg -d -ss %s -i %s -c:v libx264 -preset ultrafast -crf 0 -to %s %s";
//        cutVideoCmd = String.format(cutVideoCmd, startTime, srcFile, duration, output);
//        String cutVideoCmd = "ffmpeg -d -i %s -ss %s -to %s -c:v libx264 -c:a aac -strict experimental %s";
        return cutVideoCmd.split(" ");//以空格分割为字符串数组
    }
}
