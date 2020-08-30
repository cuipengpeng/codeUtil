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
import com.test.xcamera.managers.ConnectionManager;

/**
 * 抖音
 * Created by hackest on 2018-04-16.
 */

public class AlbumPlayController extends BaseVideoController {

    public ImageView thumb;
    private View mRootview;
    public ImageView mPauseImageView;

    private boolean isSelected = false;
    private boolean remoteVideoUrl = false;  //是否是相机视频
    private boolean showCoverImageView = true;  //是否是相机视频
    private RelativeLayout bacid;
    private String currentUrl;


    public AlbumPlayController(@NonNull Context context) {
        super(context);
    }

    public AlbumPlayController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AlbumPlayController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_douyin_controller;
    }

    @Override
    protected void initView() {
        showCoverImageView = true;
        controllerView = LayoutInflater.from(getContext()).inflate(getLayoutId(), this);
        thumb = controllerView.findViewById(R.id.iv_thumb);
        mRootview = controllerView.findViewById(R.id.rootview);
        mPauseImageView = controllerView.findViewById(R.id.iv_play);
        bacid = findViewById(R.id.bacId);
        mRootview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUrl == null) {
                    return;
                }
                if (isSelected) {
                    mPauseImageView.setVisibility(View.GONE);
                    if (remoteVideoUrl) {
                        ConnectionManager.getInstance().resumePlay(null);
                    } else {
                        mediaPlayer.start();
                    }
                } else {
                    mPauseImageView.setVisibility(View.VISIBLE);
                    if (remoteVideoUrl) {
                        ConnectionManager.getInstance().pausePlay(null);
                    } else {
                        mediaPlayer.pause();
                    }
                }
                isSelected = !isSelected;
            }
        });
    }

    public void pause(){
        mPauseImageView.setVisibility(View.VISIBLE);
        if (remoteVideoUrl) {
            ConnectionManager.getInstance().pausePlay(null);
        } else {
            mediaPlayer.pause();
        }
        isSelected = !isSelected;
    }

    @Override
    public void setPlayState(int playState) {
        super.setPlayState(playState);

        switch (playState) {
            case IjkVideoView.STATE_IDLE:
                L.e("STATE_IDLE");
                if (showCoverImageView) {
                    thumb.setVisibility(VISIBLE);
                }
                mPauseImageView.setVisibility(View.GONE);
                break;
            case IjkVideoView.STATE_PLAYING:
                if (onInfoListener != null) {
                    onInfoListener.onInfo();
                }
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
                if (showCoverImageView) {
                    thumb.setVisibility(VISIBLE);
                }
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

    private OnInfoListener onInfoListener;

    public void setOnInfoListener(OnInfoListener onInfoListener) {
        this.onInfoListener = onInfoListener;
    }

    public void setCurrentUrl(String currentUrl) {
        this.currentUrl = currentUrl;
        if (currentUrl == null) {
            if (mPauseImageView.getVisibility() == VISIBLE) {
                mPauseImageView.setVisibility(View.GONE);
            }
        }
    }

    public interface OnInfoListener {
        void onInfo();
    }
}
