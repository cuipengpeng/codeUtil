package com.test.xcamera.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dueeeke.videoplayer.controller.BaseVideoController;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.util.L;
import com.test.xcamera.R;

/**
 * Created by hackest on 2018-04-16.
 */

public class LocalVideoController extends BaseVideoController {

    private ImageView thumb;
    private View mRootview;
    public ImageView mPauseImageView;

    private boolean isSelected = false;
    private boolean remoteVideoUrl = false;  //是否是相机视频
    private RelativeLayout bacid;


    public LocalVideoController(@NonNull Context context) {
        super(context);
    }

    public LocalVideoController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LocalVideoController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_douyin_controller;
    }

    @Override
    protected void initView() {
        controllerView = LayoutInflater.from(getContext()).inflate(getLayoutId(), this);
        thumb = controllerView.findViewById(R.id.iv_thumb);
        mRootview = controllerView.findViewById(R.id.rootview);
        mPauseImageView = controllerView.findViewById(R.id.iv_play);
        bacid = findViewById(R.id.bacId);
    }

    public void videoPause() {
        if (mediaPlayer != null)
            mediaPlayer.pause();
    }

    public void videoStart() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    @Override
    public void setPlayState(int playState) {
        super.setPlayState(playState);

        switch (playState) {
            case IjkVideoView.STATE_IDLE:
                L.e("STATE_IDLE");
                thumb.setVisibility(VISIBLE);
                mPauseImageView.setVisibility(View.GONE);
                break;
            case IjkVideoView.STATE_PLAYING:
                L.e("STATE_PLAYING");
                thumb.setVisibility(GONE);
                mPauseImageView.setVisibility(View.GONE);
                break;
            case IjkVideoView.STATE_PREPARED:
                L.e("STATE_PREPARED");
                break;
            case IjkVideoView.STATE_PAUSED:
                mPauseImageView.setVisibility(View.VISIBLE);
                mPauseImageView.setSelected(false);
                break;
            case IjkVideoView.STATE_ERROR:
                thumb.setVisibility(VISIBLE);
                mPauseImageView.setVisibility(View.GONE);
                Log.i("DouYinController", "播放错误");
                break;
        }
    }

    public ImageView getThumb() {
        return thumb;
    }

    public void setBac(int w, int h) {
        if (bacid == null) {
            return;
        }
        LayoutParams layoutParams = (LayoutParams) bacid.getLayoutParams();
        layoutParams.width = w;
        layoutParams.height = h;
        bacid.setLayoutParams(layoutParams);
        bacid.requestLayout();
    }

    public View getRootview() {
        return mRootview;
    }

    public ImageView getIvPlay() {
        return mPauseImageView;
    }


    public void setSelect(boolean selected) {
        isSelected = selected;
    }

    public void setRemoteVideoUrl(boolean remoteVideoUrl) {
        this.remoteVideoUrl = remoteVideoUrl;
    }

}
