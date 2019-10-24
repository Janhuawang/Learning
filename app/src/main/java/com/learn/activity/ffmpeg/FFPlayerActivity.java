package com.learn.activity.ffmpeg;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.frank.ffmpeglib.FFPlayer;
import com.learn.R;
import com.learn.activity.ffmpeg.util.FilePathUtil;
import com.learn.activity.ffmpeg.player.widget.FFVideoView;

import java.io.File;

/**
 * 视频播放器
 * 作者：wjh on 2019-10-24 22:04
 */
public class FFPlayerActivity extends AppCompatActivity {
    private final String PATH = FilePathUtil.PATH;

    private TextView mTextView;
    private FFVideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_palyer);

        mTextView = findViewById(R.id.sample_text);
        mVideoView = findViewById(R.id.videoView);

        FilePathUtil.initPath(this);
    }

    public void onButtonClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.button_protocol:
                setInfoText(FFPlayer.urlProtocolInfo());
                break;
            case R.id.button_codec:
                setInfoText(FFPlayer.avCodecInfo());
                break;
            case R.id.button_filter:
                setInfoText(FFPlayer.avFilterInfo());
                break;
            case R.id.button_format:
                setInfoText(FFPlayer.avFormatInfo());
                break;
            case R.id.button_play:
                String videoPath = PATH + File.separator + "a.mp4";
                mVideoView.playVideo(videoPath);
                break;
        }
    }

    private void setInfoText(String content) {
        if (mTextView != null) {
            mTextView.setText(content);
        }
    }
}
