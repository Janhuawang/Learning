package com.frank.ffmpeglib;

import android.view.Surface;

/**
 * 播放器
 * 作者：wjh on 2019-10-24 22:00
 */
public class FFPlayer {

    static {
        System.loadLibrary("media-ffplayer");
    }

    public static native String urlProtocolInfo();

    public static native String avFormatInfo();

    public static native String avCodecInfo();

    public static native String avFilterInfo();

    public static native void playVideo(String videoPath, Surface surface);
}
