package com.learn.activity.media.audio.player;

import com.medialib.audioedit.util.ByteUtil;
import com.medialib.audioedit.util.MultiAudioMixer;

/**
 * 作者：wjh on 2019-12-13 14:35
 */
public class AudioMixUtil {

    /**
     * 改变音量 0.1 - 1
     */
    public static byte[] changeDataWithVolume(byte[] buffer, float volumeValue) {
        if (volumeValue <= 0) {
            volumeValue = 0.1f;
        }
        if (volumeValue >= 1) {
            return buffer;
        }
        for (int i = 0; i < buffer.length; i = i + 2) {
            int value = ByteUtil.byte2Short(buffer[i + 1], buffer[i]);
            int tempValue = value;
            value *= volumeValue;
            value = value > 0x7fff ? 0x7fff : value; // 32767
            value = value < -0x8000 ? -0x8000 : value; // 32768

            short newValue = (short) value;

            byte[] array = ByteUtil.short2Byte(newValue);
            buffer[i + 1] = array[0];
            buffer[i] = array[1];
        }
        return buffer;
    }

    /**
     * 音频混合器
     *
     * @return
     */
    public static MultiAudioMixer getAudioMixer() {
        return MultiAudioMixer.createDefaultAudioMixer();
    }

    /**
     * 时间转数据位置
     *
     * @param time       时间
     * @param sampleRate 采样率
     * @param channels   声道数
     * @param bitNum     采样位数
     * @return
     */
    public static int timeToPosition(float time, int sampleRate, int channels, int bitNum) {
        int byteNum = bitNum / 8;
        int position = (int) (time * sampleRate * channels * byteNum);

        position = position / (byteNum * channels) * (byteNum * channels);

        return position;
    }

}
