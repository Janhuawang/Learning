package com.esay.ffmtool;

/**
 * 作者：wjh on 2019-10-21 15:34
 */
public class FfmpegUtil {

    /**
     * 使用ffmpeg命令行进行视频文件合并
     *
     * @param listText
     * @param outPut
     * @return
     */
    public static String[] concatVideo(String listText, String outPut) {
        String mixAudioCmd = "ffmpeg -f concat -i %s -c copy %s";
        mixAudioCmd = String.format(mixAudioCmd, listText, outPut);
        return mixAudioCmd.split(" ");//以空格分割为字符串数组
    }

    /**
     * 多画面拼接视频
     *
     * @param input1      输入文件1
     * @param input2      输入文件2
     * @param videoLayout 视频布局
     * @param targetFile  画面拼接文件
     * @return 画面拼接的命令行
     * <p>
     * 水平拼接:1 垂直拼接:2
     */
    public static String[] multiVideo(String input1, String input2, String targetFile, int videoLayout) {
//        String multiVideo = "ffmpeg -i %s -i %s -i %s -i %s -filter_complex " +
//                "\"[0:v]pad=iw*2:ih*2[a];[a][1:v]overlay=w[b];[b][2:v]overlay=0:h[c];[c][3:v]overlay=w:h\" %s";
        String multiVideo = "ffmpeg -i %s -i %s -filter_complex hstack %s";//hstack:水平拼接，默认
        if (videoLayout == 2) {//vstack:垂直拼接
            multiVideo = multiVideo.replace("hstack", "vstack");
        }
        multiVideo = String.format(multiVideo, input1, input2, targetFile);
        return multiVideo.split(" ");
    }
}
