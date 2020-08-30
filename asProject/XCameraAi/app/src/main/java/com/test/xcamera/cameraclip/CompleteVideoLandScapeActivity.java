package com.test.xcamera.cameraclip;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.dueeeke.videoplayer.listener.VideoListener;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.player.PlayerConfig;
import com.framwork.base.view.MOBaseActivity;
import com.jaeger.library.StatusBarUtil;
import com.test.xcamera.R;
import com.test.xcamera.phonealbum.widget.VideoEditManger;
import com.test.xcamera.widget.DouYinController;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by smz on 2019/11/5.
 */

public class CompleteVideoLandScapeActivity extends MOBaseActivity {
    @BindView(R.id.ijk_completeVideoLandscapeActivity_ijkPlayer)
    IjkVideoView ijkIjkPlayer;
    @BindView(R.id.vc_completeVideoLandscapeActivity_videoControler)
    DouYinController vcVideoControler;
    @BindView(R.id.iv_completeVideoLandscapeActivity_coverImg)
    ImageView coverImg;
    @BindView(R.id.iv_completeVideoLandscapeActivity_back)
    ImageView backImageView;
    private String path;
    private long videoDuration;

    public static final String KEY_OF_VIDEO_PATH = "videoPathKey";
    public static final String KEY_OF_CURRENT_POSITION = "videoDurationKey";

    @OnClick({R.id.iv_completeVideoLandscapeActivity_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_completeVideoLandscapeActivity_back:
                finish();
                break;
        }
    }

    @Override
    public int initView() {
        return R.layout.activity_complete_video_landscape;
    }

    @Override
    public void initData() {
        //隐藏虚拟按键
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        StatusBarUtil.setTransparent(this);
        path = getIntent().getStringExtra(KEY_OF_VIDEO_PATH);
        videoDuration = getIntent().getLongExtra(KEY_OF_CURRENT_POSITION, 0);
        ijkIjkPlayer.setVideoListener(new VideoListener() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onPrepared() {
                ijkIjkPlayer.seekTo((int) videoDuration);
            }

            @Override
            public void onError() {

            }

            @Override
            public void onInfo(int what, int extra) {

            }
        });
        initIjkPlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!ijkIjkPlayer.isPlaying() && vcVideoControler.getIvPlay().getVisibility() == View.GONE)
            restartVideo();
    }

    public void restartVideo() {
        ijkIjkPlayer.resume();
        if (vcVideoControler != null) {
            vcVideoControler.setSelect(false);
        }

    }

    public void initIjkPlayer() {
        PlayerConfig config = new PlayerConfig.Builder().setLooping().build();
        ijkIjkPlayer.setPlayerConfig(config);
        vcVideoControler.setMediaPlayer(ijkIjkPlayer);

        startPlay(path);
    }


    @Override
    public void onPause() {
        super.onPause();
        if (ijkIjkPlayer.isPlaying() && vcVideoControler.getIvPlay().getVisibility() == View.GONE) {
            pauseVideo();
        }
    }

    public void pauseVideo() {
        ijkIjkPlayer.pause();
        if (vcVideoControler != null) {
            vcVideoControler.getIvPlay().setVisibility(View.GONE);
        }
    }

    private void startPlay(String video) {
        vcVideoControler.setSelect(false);
        ijkIjkPlayer.setUrl(video);
        ijkIjkPlayer.setScreenScale(IjkVideoView.SCREEN_SCALE_DEFAULT);
        ijkIjkPlayer.start();
        ijkIjkPlayer.getDuration();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ijkIjkPlayer != null) {
            ijkIjkPlayer.release();
        }
        VideoEditManger.releaseNvsStreamingContext();
    }


//    @Override
//    public void onBackPressed() {
//        //禁用back键
//    }


    public static void open(Context context, String filePath, long videoDuration) {
        Intent intent = new Intent(context, CompleteVideoLandScapeActivity.class);
        intent.putExtra(CompleteVideoLandScapeActivity.KEY_OF_CURRENT_POSITION, videoDuration);
        intent.putExtra(CompleteVideoLandScapeActivity.KEY_OF_VIDEO_PATH, filePath);
        context.startActivity(intent);
    }

}
