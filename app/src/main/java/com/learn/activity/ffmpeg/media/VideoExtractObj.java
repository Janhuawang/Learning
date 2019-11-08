package com.learn.activity.ffmpeg.media;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.text.TextUtils;

import com.learn.util.FileUtil;
import com.learn.util.UriUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 视频扩展类 只支持Mp4格式
 * 作者：wjh on 2018/11/2 11:19
 */
public class VideoExtractObj {

    private MediaMetadataRetriever mediaMetadataRetriever;
    private long fileLength = 0;// 单位毫秒
    private boolean isLoad;// 本地

    public VideoExtractObj(String path) {
        if (TextUtils.isEmpty(path)) {
            throw new RuntimeException("path must be not null !");
        }

        mediaMetadataRetriever = new MediaMetadataRetriever();
        if (isLoad = UriUtil.isLocalUrl(path)) {
            File file = new File(path);
            if (!file.isFile()) {
                throw new RuntimeException("path file not exists !");
            }

            mediaMetadataRetriever.setDataSource(path);
        } else {
            mediaMetadataRetriever.setDataSource(path, new HashMap<String, String>());
        }

        String len = getVideoTimeMs();
        fileLength = TextUtils.isEmpty(len) ? 0 : Long.valueOf(len);
    }

    /**
     * 视频第一帧
     *
     * @param activity
     * @param videoThumbnailImp
     */
    public void getVideoThumbnail(final Activity activity, final VideoThumbnailImp videoThumbnailImp) {
        if (videoThumbnailImp != null) {
            if (activity == null) {
                videoThumbnailImp.done();
                return;
            }

            if (isLoad) {
                videoThumbnailImp.callbackLoadFrame(mediaMetadataRetriever.getFrameAtTime());
                videoThumbnailImp.done();
                return;
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime();
                    FileOutputStream outStream = null;
                    final String filePath = FileUtil.getDefaultCacheImgPath(activity);
                    try {
                        outStream = new FileOutputStream(new File(filePath));
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, outStream);
                        outStream.close();
                        bitmap.recycle();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            videoThumbnailImp.callbackNetworkFrame(filePath);
                            videoThumbnailImp.done();
                        }
                    });
                }
            }).start();
        }
    }

    /**
     * 获取视频的宽度
     *
     * @return
     */
    public int getVideoWidth() {
        String w = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        int width = -1;
        if (!TextUtils.isEmpty(w)) {
            width = Integer.valueOf(w);
        }
        return width;
    }

    /**
     * 获取视频的高度
     *
     * @return
     */
    public int getVideoHeight() {
        String h = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        int height = -1;
        if (!TextUtils.isEmpty(h)) {
            height = Integer.valueOf(h);
        }
        return height;
    }

    /**
     * 获取视频的总时长
     *
     * @return 单位 毫秒
     */
    public String getVideoTimeMs() {
        return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
    }

    /**
     * 获取指定时间的一帧
     *
     * @param time 单位毫秒
     * @return
     */
    @TargetApi(Build.VERSION_CODES.O_MR1)
    public Bitmap extractFrame(long time, int dstWidth, int dstHeight) {
        // 第一个参数是传入时间，只能是us(微秒)
        // OPTION_CLOSEST ,在给定的时间，检索最近一个帧,这个帧不一定是关键帧。
        // OPTION_CLOSEST_SYNC   在给定的时间，检索最近一个同步与数据源相关联的的帧（关键帧）
        // OPTION_NEXT_SYNC 在给定时间之后检索一个同步与数据源相关联的关键帧。
        // OPTION_PREVIOUS_SYNC 在给定时间之前检索一个同步与数据源相关联的关键帧。
        Bitmap frameAtTime = null;
        try {
            frameAtTime = mediaMetadataRetriever.getScaledFrameAtTime(time * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC, dstWidth, dstHeight);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return frameAtTime;
    }

    /**
     * 获取时间段内多帧
     *
     * @param startPosition
     * @param endPosition
     * @param thumbnailsCount
     * @param videoExtractImp
     */
    public void extractFrameList(long startPosition, long endPosition, int thumbnailsCount, int dstWidth, int dstHeight, VideoExtractImp videoExtractImp) {
        if (videoExtractImp == null) {
            return;
        }
        if (endPosition == 0) {
            videoExtractImp.done();
        }

        startPosition = Math.max(startPosition, 0);
        if (fileLength != 0) {
            endPosition = Math.min(endPosition, fileLength);
        }
        long interval = (endPosition - startPosition) / (thumbnailsCount - 1);

        List<Long> timeList = new ArrayList<>();
        for (int i = 0; i < thumbnailsCount; i++) {
            long time = startPosition + interval * i;
            if (i == thumbnailsCount - 1) {
                if (interval > 1000) {
                    timeList.add(endPosition - 800);
                } else {
                    timeList.add(endPosition);
                }
            } else {
                timeList.add(time);
            }
        }
        extractFrameList(timeList, dstWidth, dstHeight, videoExtractImp);
    }

    /**
     * 获取一组视频帧
     *
     * @param timeList
     * @param videoExtractImp
     */
    public void extractFrameList(List<Long> timeList, int dstWidth, int dstHeight, VideoExtractImp videoExtractImp) {
        if (timeList != null && videoExtractImp != null) {
            for (Long time : timeList) {
                videoExtractImp.callbackFrame(extractFrame(time, dstWidth, dstHeight));
            }
            videoExtractImp.done();
        }
    }

    /**
     * 释放
     */
    public void release() {
        if (mediaMetadataRetriever != null) {
            mediaMetadataRetriever.release();
        }
    }

    public interface VideoExtractImp {
        void callbackFrame(Bitmap bitmap);

        void done();
    }

    public interface VideoThumbnailImp {
        void callbackNetworkFrame(String path);

        void callbackLoadFrame(Bitmap bitmap);

        void done();
    }
}
