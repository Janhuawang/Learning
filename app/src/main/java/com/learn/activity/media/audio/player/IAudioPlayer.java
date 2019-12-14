package com.learn.activity.media.audio.player;


/**
 * 音频播放器
 * 作者：wjh on 2019-12-13 11:34
 */
public abstract class IAudioPlayer {

    /**
     * 双音混合播放器，可设置音量、文件格式(pcm或wav)
     *
     * @return
     */
    public static IAudioPlayer createMixer() {
        return new AudioPlayerMixer();
    }

    public abstract void setMixPath(String srcFilePath, String coverFilePath);

    /**
     * 设置音量大小0~1之间比例
     *
     * @param srcVolume
     * @param coverVolume
     */
    public abstract void setVolume(float srcVolume, float coverVolume);

    public abstract void setWave(boolean isWave);

    /**
     * 设置背景乐循环状态
     *
     * @param isLoop
     */
    public abstract void setLoopToCover(boolean isLoop);

    public abstract boolean prepare();

    public abstract boolean release();

    public abstract boolean play();

    public abstract boolean pause();

    public abstract boolean stop();

    public abstract void seek(int seekTime); // 单位秒

    public abstract void addPlayerCallback(AudioPlayerCallback audioPlayerCallback);

    public interface AudioPlayerCallback {
        void playProgress(int totalTime, int playTime);// 播放时间回调
    }
}




