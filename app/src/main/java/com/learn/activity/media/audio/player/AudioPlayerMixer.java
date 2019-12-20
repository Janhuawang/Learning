package com.learn.activity.media.audio.player;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.medialib.audioedit.util.FileUtils;
import com.medialib.audioedit.util.MultiAudioMixer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.locks.LockSupport;

/**
 * 流播放器
 * 作者：wjh on 2019-12-13 13:05
 */
public class AudioPlayerMixer extends IAudioPlayer {
    /* wave头文件大小 */
    private static final int WAVE_HEAD_SIZE = 44;

    /**
     * 当前音频流文件
     */
    private RandomAccessFile srcFis;
    /**
     * 当前背景音频流文件
     */
    private volatile RandomAccessFile coverFis;
    /**
     * 音频播放时间回调
     */
    private AudioPlayerCallback audioPlayerCallback;


    /**
     * 音频采样大小
     */
    private int sampleRate;
    /**
     * 音频通道数 1：单声道 2：双声道立体声
     */
    private int channel;
    /**
     * 音频位 8、16、32
     */
    private int bitNum;
    /**
     * 一秒的字节大小
     */
    private volatile int sampleSize;
    /**
     * 总时长
     */
    private int timeMillis;
    /**
     * 是否为wave格式
     */
    private boolean isWave;


    /**
     * 音频轨道
     */
    private AudioTrack mAudioTrack;
    /**
     * 播放线程
     */
    private PlayAudioThread mPlayAudioThread;
    /**
     * 线程退出态
     */
    private boolean mThreadExitFlag;
    /**
     * 线程暂停态
     */
    private boolean mThreadWaitState;
    /**
     * 准备态
     */
    private boolean mBReady;
    /**
     * 当前播放状态
     */
    private int mPlayState = PlayState.MPS_UNINIT;


    /**
     * 背景乐路径
     */
    private String coverFilePath;
    /**
     * 当前是否有背景音乐
     */
    private volatile boolean hasCoverFis;
    /**
     * 背景乐数据大小 如果是wav格式的话会把头数据减掉
     */
    private volatile int coverAudioDataSize;


    /**
     * 初始位置的时间
     */
    private volatile int initialSeekTime;
    /**
     * 当前读取文件的大小
     */
    private volatile int currentSeekSize;
    /**
     * 当前Seek到位置时间
     */
    private volatile int currentSeekTime;


    /**
     * 当前音量值 默认是1
     */
    private float srcVolume = 1f, coverVolume = 1f;
    /**
     * 淡出淡出音量值
     */
    private float tempVolume = this.coverVolume;


    /**
     * 插入时间点
     */
    private volatile int insertPoint;
    /**
     * 是否设置背景乐为循环模式
     */
    private volatile boolean isLoopWithCover = true;
    /**
     * 淡入状态
     */
    private volatile boolean isFadeIn;
    /**
     * 淡出状态
     */
    private volatile boolean isFadeOut;


    /**
     * 淡入开始时间点
     */
    private volatile int fadeInStartTime;
    /**
     * 淡入结束时间点
     */
    private volatile int fadeInEndTime;
    /**
     * 淡出开始时间点
     */
    private volatile int fadeOutStartTime;
    /**
     * 淡出结束时间点
     */
    private volatile int fadeOutEndTime;
    /**
     * 淡入音量渐变大小
     */
    private volatile float fadeInGradientVolume;
    /**
     * 淡出音量渐变大小
     */
    private volatile float fadeOutGradientVolume;


    @Override
    public void setSrcPath(String srcFilePath) {
        if (!FileUtils.checkFileExist(srcFilePath)) {
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setCoverPath(String coverFilePath) {
        if (!FileUtils.checkFileExist(coverFilePath)) {
            this.hasCoverFis = false;
            this.coverAudioDataSize = 0;
            return;
        }

        // 创建输入流
        try {
            if (coverFis != null && !TextUtils.isEmpty(this.coverFilePath) && this.coverFilePath.equals(coverFilePath)) {
                return;
            }

            coverFis = new RandomAccessFile(coverFilePath, "rw");
            this.hasCoverFis = true;
            this.coverAudioDataSize = getFileDataSize(coverFis);
            this.coverFilePath = coverFilePath;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setInsertTime(int insertTime) {
        this.insertPoint = AudioMixUtil.timeToPosition(insertTime, sampleRate, channel, bitNum);
    }

    @Override
    public void setBgmVolume(float coverVolume) {
        this.coverVolume = coverVolume;
        this.tempVolume = coverVolume;
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

        seekFile(0, srcFis);
        seekFile(0, coverFis);

        return true;
    }

    @Override
    public boolean release() {
        if (mBReady == false) {
            return false;
        }

        mThreadExitFlag = true;

        stop();

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
        if (mPlayState != PlayState.MPS_PLAYING) {
            setPlayState(PlayState.MPS_PLAYING);
            seek(AudioPlayerMixer.this.currentSeekTime);
            startThread();
        }
        return true;
    }

    @Override
    public boolean pause() {
        if (mBReady == false) {
            return false;
        }
        if (mPlayState == PlayState.MPS_PLAYING) {
            setPlayState(PlayState.MPS_PAUSE);
            pauseThread();
        }
        return true;
    }

    @Override
    public boolean stop() {
        if (mBReady == false) {
            return false;
        }

        setPlayState(PlayState.MPS_PREPARE);

        AudioPlayerMixer.this.currentSeekTime = 0; // seek到0

        stopThread();

        if (audioPlayerCallback != null) { // seek到0
            audioPlayerCallback.playProgress(timeMillis, 0);
        }

        return true;
    }

    @Override
    public void seek(final int seekTime) {
        if (mBReady == false) {
            return;
        }

        AudioPlayerMixer.this.initialSeekTime = seekTime;
        mThreadWaitState = true;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                seekFile(seekTime, srcFis);
                seekFile(seekTime, coverFis);

                mThreadWaitState = false;
                LockSupport.unpark(mPlayAudioThread);
            }
        }, 150);  // delayMillis > 1000/DENOTE = 100毫秒读取数据的时间

    }

    @Override
    public void updateConfig(boolean isFadeIn, int fadeInTime, boolean isFadeOut, int fadeOutTime, boolean isLoopWithCover) {
        this.isFadeIn = isFadeIn;
        this.fadeInStartTime = 0;
        this.fadeInEndTime = fadeInStartTime + fadeInTime;

        this.isFadeOut = isFadeOut;
        this.fadeOutStartTime = timeMillis - fadeOutTime;
        this.fadeOutEndTime = timeMillis;

        this.isLoopWithCover = isLoopWithCover;

        fadeInGradientVolume = tempVolume / (fadeInEndTime - fadeInStartTime);
        fadeOutGradientVolume = tempVolume / (fadeOutEndTime - fadeOutStartTime);

        if (fadeInGradientVolume <= 0) {
            this.isFadeIn = false;
        }
        if (fadeOutGradientVolume <= 0) {
            this.isFadeOut = false;
        }
    }

    @Override
    public int getPlayState() {
        return mPlayState;
    }

    /**
     * 当前播放状态
     *
     * @param state
     */
    private void setPlayState(int state) {
        this.mPlayState = state;
    }

    @Override
    public void addPlayerCallback(AudioPlayerCallback audioPlayerCallback) {
        this.audioPlayerCallback = audioPlayerCallback;
    }

    /**
     * 播放结束
     */
    private void playFinish() {
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

        // 获得构建对象的最小缓冲区大小 - 按照数字音频的知识，这个算出来的是一秒钟buffer的大小。
        int minBufSize = AudioTrack.getMinBufferSize(sampleRate,
                channelConfig,
                audioFormat);

        // 初始化
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate,
                channelConfig,
                audioFormat,
                minBufSize,
                AudioTrack.MODE_STREAM);

        mAudioTrack.setPositionNotificationPeriod(minBufSize);
        mAudioTrack.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
            @Override
            public void onMarkerReached(AudioTrack track) {
            }

            @Override
            public void onPeriodicNotification(AudioTrack track) {
                if (audioPlayerCallback != null && currentSeekSize > 0) { // 因为获取的流是上一个已经完成的数据，所以需要增加1秒。
                    AudioPlayerMixer.this.currentSeekTime = (int) (currentSeekSize * 1f / sampleSize) + 1;
                    audioPlayerCallback.playProgress(timeMillis, Math.min(timeMillis, AudioPlayerMixer.this.currentSeekTime));
                }
            }
        });
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
            sampleSize = sampleRate * channel * bitNum / 8;
            timeMillis = (int) (audioParam.getTimeMillis() / 1000f);
        }
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
     * 暂停线程
     */
    private void pauseThread() {
        if (mPlayAudioThread != null) {
            mThreadExitFlag = true;
            mPlayAudioThread = null;
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

    /**
     * 根据时间指定流位置
     *
     * @param seekTime
     * @param file
     */
    private void seekFile(int seekTime, RandomAccessFile file) {
        if (file != null && seekTime >= 0) {

            final int seekPos = getSeekPos(seekTime, file);
            try {
                file.seek(this.isWave ? WAVE_HEAD_SIZE + seekPos : seekPos);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据时间计算流位置
     *
     * @param seekTime
     * @param file
     * @return
     */
    private int getSeekPos(int seekTime, RandomAccessFile file) {
        final int seekPos = AudioMixUtil.timeToPosition(seekTime, sampleRate, channel, bitNum);
        int maxPos = getFileDataSize(file);
        return seekPos / maxPos == 1 ? maxPos : seekPos % maxPos;
    }

    /**
     * 获取文件数据大小
     *
     * @param file
     * @return
     */
    private int getFileDataSize(RandomAccessFile file) {
        if (file != null) {
            try {
                return (int) (this.isWave ? file.length() - WAVE_HEAD_SIZE : file.length());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    interface PlayAudioCallback {
        void writeMixData(byte[] mixData, int srcSeekPoint);

        void resetMixData();

        void writeDone();
    }

    private class PlayAudioThread extends Thread {
        @Override
        public void run() {
            mAudioTrack.play();

            try {
                mixData(srcFis, new PlayAudioCallback() {
                    @Override
                    public void writeMixData(byte[] mixData, int srcSeekPoint) {
                        AudioPlayerMixer.this.currentSeekSize = srcSeekPoint;
                        mAudioTrack.write(mixData, 0, mixData.length);
                    }

                    @Override
                    public void resetMixData() {
                        mAudioTrack.flush();
                    }

                    @Override
                    public void writeDone() {
                        mAudioTrack.stop();
                        AudioPlayerMixer.this.playFinish();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                mAudioTrack.stop();
                AudioPlayerMixer.this.playFinish();
            }
        }

        /**
         * 合成音频
         */
        private void mixData(RandomAccessFile srcFis,
                             PlayAudioCallback playAudioCallback) {
            MultiAudioMixer mix = AudioMixUtil.getAudioMixer();

            final int DENOTE = 10; // 1000/DENOTE = 100毫秒读取一次数据
            final int BUFFER_SIZE = sampleSize / DENOTE;
            byte[] srcBuffer = new byte[BUFFER_SIZE];
            byte[] coverBuffer = new byte[BUFFER_SIZE];

            int length;

            int srcSeekPoint = AudioPlayerMixer.this.initialSeekTime * sampleSize; // 原音当前读取流位置
            int coverSeekPoint; // 背景乐当前读流位置

            int timeCounter = 0; // 时间计数器 一秒记一次
            boolean isTail = false; // 背景乐是否已经读到了尾部

            try {
                while ((length = srcFis.read(srcBuffer)) != -1) {
                    if (mThreadExitFlag == true) { // 退出
                        break;
                    }
                    if (mThreadWaitState) { // 暂停态
                        srcSeekPoint = AudioPlayerMixer.this.initialSeekTime * sampleSize;
                        timeCounter = 0;
                        length = 0;
                        isTail = false;

                        playAudioCallback.resetMixData();
                        LockSupport.park(); // 等待Seek完后继续执行。
                    }

                    srcSeekPoint += length;
                    coverSeekPoint = srcSeekPoint - insertPoint;

                    boolean isCanReadCover = hasCoverFis && coverAudioDataSize > 0 && coverSeekPoint >= 0; // 是否可以读取背景乐
                    isCanReadCover = isCanReadCover && coverSeekPoint > insertPoint && isLoopWithCover; // 是否继续读取背景乐
                    if (!isCanReadCover) {
                        srcBuffer = AudioMixUtil.changeDataWithVolume(srcBuffer, srcVolume);
                        if (playAudioCallback != null) {
                            playAudioCallback.writeMixData(srcBuffer, srcSeekPoint);
                        }
                        continue;
                    }

                    coverFis.read(coverBuffer);

                    /**
                     * 计算淡入淡出的音量值
                     */
                    if ((coverSeekPoint - sampleSize * timeCounter) >= sampleSize) {
                        ++timeCounter;

                        int coverSeekTime = coverSeekPoint / sampleSize;
                        if (isFadeIn && coverSeekTime <= fadeInEndTime && coverSeekTime >= fadeInStartTime) {
                            tempVolume = fadeInGradientVolume * coverSeekTime;
                        } else if (isFadeOut && coverSeekTime >= fadeOutStartTime && coverSeekTime <= fadeOutEndTime) {
                            tempVolume = fadeOutGradientVolume * (fadeOutEndTime - coverSeekTime);
                        } else {
                            tempVolume = coverVolume;
                        }
                    }

                    srcBuffer = AudioMixUtil.changeDataWithVolume(srcBuffer, srcVolume);
                    coverBuffer = AudioMixUtil.changeDataWithVolume(coverBuffer, tempVolume);
                    byte[] mixData = mix.mixRawAudioBytes(new byte[][]{srcBuffer, coverBuffer});
                    if (playAudioCallback != null) {
                        playAudioCallback.writeMixData(mixData, srcSeekPoint);
                    }

                    /**
                     * 过尾部了 还原buffer大小
                     */
                    if (isTail) {
                        isTail = false;

                        coverBuffer = new byte[BUFFER_SIZE];
                        srcBuffer = new byte[BUFFER_SIZE];
                        seekFile(0, coverFis);
                        Log.e("Mix", "背景乐完成循环一次！");
                        continue;
                    }

                    /**
                     * 是否快到尾部了
                     */
                    int remainSize = coverAudioDataSize - srcSeekPoint % coverAudioDataSize;
                    if (remainSize <= coverBuffer.length) {
                        coverBuffer = new byte[remainSize];
                        srcBuffer = new byte[remainSize];
                        isTail = true;
                        Log.e("Mix", "背景乐准备循环一次！");
                    }
                }

                playAudioCallback.writeDone(); // 播放结束
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
