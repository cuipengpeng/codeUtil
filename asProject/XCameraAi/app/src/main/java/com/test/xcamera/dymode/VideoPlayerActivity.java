package com.test.xcamera.dymode;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.test.xcamera.R;

import java.io.File;

public class VideoPlayerActivity extends Activity {
    private VideoView mVideoPlayer;
    private MediaController mController;
    private String mPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mVideoPlayer = findViewById(R.id.video_player_view);
        mController = new MediaController(this);
        mPath = getIntent().getStringExtra("video_path");
        File file = new File(mPath);
        if (file.exists()) {
            Toast.makeText(this, "视频路径：" + mPath, Toast.LENGTH_LONG).show();
            mVideoPlayer.setVideoPath(file.getAbsolutePath());
            mVideoPlayer.setMediaController(mController);
            mController.setMediaPlayer(mVideoPlayer);
            mVideoPlayer.start();

        } else {
            Toast.makeText(this, "视频文件不存在", Toast.LENGTH_SHORT).show();
        }
    }
}
