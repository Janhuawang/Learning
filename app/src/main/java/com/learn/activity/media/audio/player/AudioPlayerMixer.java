package com.learn.activity.media.audio.player;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import com.medialib.audioedit.bean.Audio;
import com.medialib.audioedit.util.FileUtils;
import com.medialib.audioedit.util.MultiAudioMixer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 播放器
 * 作者：wjh on 2019-12-13 13:05
 */
public class AudioPlayerMixer extends IAudioPlayer {
    // wave头文件大小
    private static final int WAVE_HEAD_SIZE = 44;

    /**
     * 当前音频流文件
     */
    private RandomAccessFile srcFis, coverFis;

    /**
     * 音频格式数据
     */
    private int sampleRate;
    private int channel;
    private int bitNum;

    /**
     * 音频轨道
     */
    private AudioTrack mAudioTrack;
    /**
     * 播放线程
     */
    private PlayAudioThread mPlayAudioThread;

    /**
     * 准备态
     */
    private boolean mBReady;
    /**
     * 当前播放状态
     */
    private int mPlayState = PlayState.MPS_UNINIT;
    /**
     * 是否为wave格式
     */
    private boolean isWave;
    /**
     * 音量比例
     */
    private float srcVolume, coverVolume;
    /**
     * 线程退出态
     */
    private boolean mThreadExitFlag;

    @Override
    public void setMixPath(String srcFilePath, String coverFilePath) {
        if (!FileUtils.checkFileExist(srcFilePath) || !FileUtils.checkFileExist(coverFilePath)) {
            return;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            try {
                Audio audio = Audio.createAudioFromFile(new File(srcFilePath));
                setAudioParam(audio);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 创建输入流
        try {
            srcFis = new RandomAccessFile(srcFilePath, "rw");
            coverFis = new RandomAccessFile(coverFilePath, "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setVolume(float srcVolume, float coverVolume) {
        this.srcVolume = srcVolume;
        this.coverVolume = coverVolume;
    }

    @Override
    public void setWave(boolean isWave) {
        this.isWave = isWave;
    }

    @Override
    public boolean prepare() {
        if (mBReady == true) {
            return true;
        }

        createAudio();

        mBReady = true;

        setPlayState(PlayState.MPS_PREPARE);

        seek(1);

        return true;
    }

    @Override
    public boolean release() {
        if (mBReady == false) {
            return false;
        }

        mThreadExitFlag = true;

        releaseAudioTrack();

        closeFile();

        mBReady = false;
        setPlayState(PlayState.MPS_UNINIT);

        return true;
    }

    @Override
    public boolean play() {
        if (mBReady == false) {
            return false;
        }

        setPlayState(PlayState.MPS_PLAYING);
        startThread();

        return true;
    }

    @Override
    public boolean pause() {
        if (mBReady == false) {
            return false;
        }

        if (mPlayState == PlayState.MPS_PLAYING) {
            setPlayState(PlayState.MPS_PAUSE);
            stopThread();
        }

        return true;
    }

    @Override
    public boolean stop() {
        if (mBReady == false) {
            return false;
        }

        setPlayState(PlayState.MPS_PREPARE);
        stopThread();

        return true;
    }

    @Override
    public void seek(int seekTime) {
        if (mBReady == false) {
            return;
        }
        if (srcFis == null || coverFis == null || seekTime < 0) {
            return;
        }

        final int seekPos = AudioMixUtil.getPositionFromWave(seekTime, sampleRate, channel, bitNum);
        try {
            srcFis.seek(this.isWave ? WAVE_HEAD_SIZE + seekPos : seekPos);
            coverFis.seek(this.isWave ? WAVE_HEAD_SIZE + seekPos : seekPos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放结束
     */
    private void playFinish() {
        mPlayAudioThread = null;

        closeFile();

        if (mPlayState != PlayState.MPS_PAUSE) {
            setPlayState(PlayState.MPS_PREPARE);
        }
    }

    /**
     * 创建音频轨道
     */
    private void createAudio() {
        int audioFormat;
        int channelConfig;
        switch (bitNum) {
            case 8:
                audioFormat = AudioFormat.ENCODING_PCM_8BIT;
                break;

            case 16:
                audioFormat = AudioFormat.ENCODING_PCM_16BIT;
                break;

            case 32:
                audioFormat = AudioFormat.ENCODING_PCM_FLOAT;
                break;

            default:
                audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        }
        switch (channel) {
            case 1:
                channelConfig = AudioFormat.CHANNEL_IN_MONO;
                break;

            case 2:
                channelConfig = AudioFormat.CHANNEL_IN_STEREO;
                break;

            default:
                channelConfig = AudioFormat.CHANNEL_IN_STEREO;
        }

        // 获得构建对象的最小缓冲区大小
        int minBufSize = AudioTrack.getMinBufferSize(sampleRate,
                channelConfig,
                audioFormat);

        int mPrimePlaySize = minBufSize * 2;

        // 初始化
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate,
                channelConfig,
                audioFormat,
                minBufSize,
                AudioTrack.MODE_STREAM);
    }

    /**
     * 设置音频格式参数
     *
     * @param audioParam
     */
    private void setAudioParam(Audio audioParam) {
        if (audioParam != null) {
            sampleRate = audioParam.getSampleRate();
            channel = audioParam.getChannel();
            bitNum = audioParam.getBitNum();
        }
    }

    /**
     * 当前播放状态
     *
     * @param state
     */
    private void setPlayState(int state) {
        this.mPlayState = state;
    }

    /**
     * 开启线程
     */
    private void startThread() {
        if (mPlayAudioThread == null) {
            mThreadExitFlag = false;
            mPlayAudioThread = new PlayAudioThread();
            mPlayAudioThread.start();
        }
    }

    /**
     * 停止线程
     */
    private void stopThread() {
        if (mPlayAudioThread != null) {
            mThreadExitFlag = true;
            mPlayAudioThread = null;
        }
    }

    /**
     * 释放AudioTrack
     */
    private void releaseAudioTrack() {
        if (mAudioTrack != null) {
            mAudioTrack.stop();
            mAudioTrack.release();
            mAudioTrack = null;
        }
    }

    /**
     * 关闭文件流
     */
    private void closeFile() {
        if (srcFis != null) {
            try {
                srcFis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (coverFis != null) {
            try {
                coverFis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    interface PlayAudioCallback {
        void writeMixData(byte[] mixData);

        void writeDone();
    }

    private class PlayAudioThread extends Thread {
        @Override
        public void run() {
            mAudioTrack.play();

            while (true) {
                if (mThreadExitFlag == true) {
                    break;
                }

                try {
                    mixData(srcFis, coverFis, srcVolume, coverVolume, new PlayAudioCallback() {
                        @Override
                        public void writeMixData(byte[] mixData) {
                            mAudioTrack.write(mixData, 0, mixData.length);
                        }

                        @Override
                        public void writeDone() {
                            AudioPlayerMixer.this.playFinish();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    AudioPlayerMixer.this.playFinish();
                    break;
                }
            }

            mAudioTrack.stop();
        }

        /**
         * 合成音频
         */
        private void mixData(RandomAccessFile srcFis, RandomAccessFile coverFis,
                             float volumeAudio1, float volumeAudio2, PlayAudioCallback playAudioCallback) {
            MultiAudioMixer mix = AudioMixUtil.getAudioMixer();

            byte[] srcBuffer = new byte[2048];
            byte[] coverBuffer = new byte[2048];

            try {
                while (coverFis.read(coverBuffer) != -1) {
                    srcFis.read(srcBuffer);
                    srcBuffer = AudioMixUtil.changeDataWithVolume(srcBuffer, volumeAudio1);
                    coverBuffer = AudioMixUtil.changeDataWithVolume(coverBuffer, volumeAudio2);

                    byte[] mixData = mix.mixRawAudioBytes(new byte[][]{srcBuffer, coverBuffer});
                    if (playAudioCallback != null) {
                        playAudioCallback.writeMixData(mixData);
                    }
                }

                playAudioCallback.writeDone();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
