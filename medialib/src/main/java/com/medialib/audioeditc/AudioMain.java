package com.medialib.audioeditc;

/**
 * 作者：wjh on 2019-12-16 15:57
 */
public class AudioMain {

    static {
        System.loadLibrary("media-audio-edit");
    }

    private native static int mix(String srcPath, String coverPath, String outputPath, MixParam mixParam);

    private native static int pcm16leToWav(String pcmPath, String wavPath, int byteType); // 1:小端字节 0：大端字节

    private native static int getWavHeadSize(String wavPath);

    public static void mixAudio(final String srcPath, final String coverPath, final String outputPath, final MixParam mixParam, final HandleCallback handleCallback) {
        if (handleCallback != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    handleCallback.onBegin();
                    int result = mix(srcPath, coverPath, outputPath, mixParam);
                    handleCallback.onEnd(result);
                }
            }).start();
        }
    }

    public static void pcmToWav(final String pcmPath, final String wavPath, final HandleCallback handleCallback) {
        if (handleCallback != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    handleCallback.onBegin();
                    int result = pcm16leToWav(pcmPath, wavPath, 1);
                    handleCallback.onEnd(result);
                }
            }).start();
        }
    }

    public static int getWavFileHeadSize(final String wavPath) {
        return getWavHeadSize(wavPath);
    }

}
