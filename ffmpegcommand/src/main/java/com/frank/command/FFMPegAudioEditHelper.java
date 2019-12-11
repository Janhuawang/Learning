package com.frank.command;

import com.frank.callback.AudioEditImpl;

/**
 * 音频编辑
 * 作者：wjh on 2019-12-06 20:47
 */
public class FFMPegAudioEditHelper {

    /**
     * 音频转码
     *
     * @param srcFile    源文件
     * @param targetFile 目标文件（后缀指定转码格式）
     * @return 转码后的文件
     */
    public static void transformAudio(String srcFile, String targetFile, final AudioEditImpl handleCallback) {
        String cmd = "ffmpeg -d -i %s %s";
        cmd = String.format(cmd, srcFile, targetFile);
        executeFFMPegCmd(cmd.split(" "), FFMPegType.BUSINESS_TYPE_AUDIO_EDIT_FADE_IN, handleCallback);
    }

    /**
     * 音频剪切
     *
     * @param srcFile    源文件
     * @param startTime  剪切的开始时间(单位为秒)
     * @param duration   剪切时长(单位为秒)
     * @param targetFile 目标文件
     * @return 剪切后的文件
     */
    public static void cutAudio(String srcFile, int startTime, int duration, String targetFile, final AudioEditImpl handleCallback) {
        String cmd = "ffmpeg -d -i %s -ss %d -t %d %s";
        cmd = String.format(cmd, srcFile, startTime, duration, targetFile);
        executeFFMPegCmd(cmd.split(" "), FFMPegType.BUSINESS_TYPE_AUDIO_EDIT_FADE_IN, handleCallback);
    }

    /**
     * 音频合并
     *
     * @param srcFile    源文件
     * @param appendFile 待追加的文件
     * @param targetFile 目标文件
     * @return 合并后的文件
     */
    public static void concatAudio(String srcFile, String appendFile, String targetFile, final AudioEditImpl handleCallback) {
        String cmd = "ffmpeg -d -i concat:%s|%s -acodec copy %s";
        cmd = String.format(cmd, srcFile, appendFile, targetFile);
        executeFFMPegCmd(cmd.split(" "), FFMPegType.BUSINESS_TYPE_AUDIO_EDIT_FADE_IN, handleCallback);
    }
    //混音公式：value = sample1 + sample2 - (sample1 * sample2 / (pow(2, 16-1) - 1))

    /**
     * 音频混合
     *
     * @param srcFile    源文件
     * @param mixFile    待混合文件
     * @param targetFile 目标文件
     * @return 混合后的文件
     */
    public static void mixAudio(String srcFile, String mixFile, String targetFile, final AudioEditImpl handleCallback) {
        String cmd = "ffmpeg -d -i %s -i %s -filter_complex amix=inputs=2:duration=first -strict -2 %s";
        cmd = String.format(cmd, srcFile, mixFile, targetFile);
        executeFFMPegCmd(cmd.split(" "), FFMPegType.BUSINESS_TYPE_AUDIO_EDIT_FADE_IN, handleCallback);
    }

    /**
     * 音频淡入
     *
     * @param srcFile
     * @param targetFile
     * @return
     */
    public static void audioFadeIn(String srcFile, String targetFile, final AudioEditImpl handleCallback) {
        String cmd = "ffmpeg -d -i %s -af \"volume='if(lt(t,10),0.3,min(0.3+(t-10)/3*(1-0.3),1))':eval=frame\" %s";
        cmd = String.format(cmd, srcFile, targetFile);
        executeFFMPegCmd(cmd.split(" "), FFMPegType.BUSINESS_TYPE_AUDIO_EDIT_FADE_IN, handleCallback);
    }

    /**
     * 执行命令行
     *
     * @param commandLine
     * @param handleCallback
     */
    private static void executeFFMPegCmd(final String[] commandLine, final int type, final AudioEditImpl handleCallback) {
        FFMPegMain.execute(commandLine, type, handleCallback);
    }
}
