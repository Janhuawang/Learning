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
    public static AudioPlayerMixer createAudioPlayerMixer() {
        return new AudioPlayerMixer();
    }

    public abstract void setSrcPath(String srcFilePath);

    public abstract void setCoverPath(String coverFilePath);

    public abstract void setInsertTime(int insertTime);

    /**
     * 设置音量大小0~1之间比例
     *
     * @param coverVolume
     */
    public abstract void setBgmVolume(float coverVolume);

    public abstract void setWave(boolean isWave);

    public abstract boolean prepare();

    public abstract boolean release();

    public abstract boolean play();

    public abstract boolean pause();

    public abstract boolean stop();

    public abstract void seek(int seekTime); // 单位毫秒

    public abstract void updateConfig(boolean isFadeIn, int fadeInTime, boolean isFadeOut, int fadeOutTime, boolean isLoopWithCover); // 更新配置参数

    /**
     * 获取播放器当前状态
     *
     * @return
     * @see PlayState
     */
    public abstract int getPlayState();

    public abstract void addPlayerCallback(AudioPlayerCallback audioPlayerCallback);

    public interface AudioPlayerCallback {
        void playProgress(int totalTime, int playTime);// 播放时间回调  单位为毫秒
    }
}




