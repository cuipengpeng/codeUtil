package com.test.xcamera.moalbum.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dueeeke.videoplayer.player.IjkVideoView;
import com.framwork.base.view.MOBaseFragment;
import com.test.xcamera.R;
import com.test.xcamera.moalbum.activity.MyAlbumPreview;
import com.test.xcamera.album.preview.fragmentinterface.VideoPreviewFragmentInterface;
import com.test.xcamera.album.preview.presenter.VideoPreviewFragmentPresenter;
import com.test.xcamera.bean.BaseData;
import com.test.xcamera.bean.MoAlbumItem;
import com.test.xcamera.player.AVFrame;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.DateUtils;
import com.test.xcamera.utils.GlideUtils;
import com.xiaoyi.yicamera.mp4recorder2.Mp4V2Native;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * author zxc
 * createTime 2019/10/12  下午11:49
 */
public class LocalVideoPreviewFragment extends MOBaseFragment implements VideoPreviewFragmentInterface, MyAlbumPreview.PagerChange, SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    @BindView(R.id.videoPreViewBg)
    ImageView videoPreViewBg;
    @BindView(R.id.ijk_albumctivity_ijkPlayer)
    IjkVideoView ijk_albumctivity_ijkPlayer;
    @BindView(R.id.startPlay)
    ImageView startPlay;
    @BindView(R.id.videoContorlLLayout)
    RelativeLayout videoContorlLLayout;
    @BindView(R.id.videoTotalTimeTextView)
    TextView videoTotalTimeTextView;
    @BindView(R.id.videoPlay)
    ImageView videoPlay;
    @BindView(R.id.videoPlaySeekBar)
    SeekBar videoPlaySeekBar;

    private MyAlbumPreview activity;
    private VideoPreviewFragmentPresenter videoPreviewFragmentPresenter;
    private MoAlbumItem item;

    private int currentProgress = 0;
    private int maxProgress;
    private boolean isHideSeekBar = false;
    private boolean isHidePlay = false;
    private boolean isPlay = false;
    private final int PROGRESS_BAR_FLAG = 200;
    private final int PROGRESS_BAR_DELAY_TIME = 1000;
    private final int COUNTDOWN_FLAG = 201;
    private final int COUNTDOWN_TIME = 3000;
    @SuppressLint("HandlerLeak")
    private Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == PROGRESS_BAR_FLAG) {
                if (ijk_albumctivity_ijkPlayer != null &&
                        ijk_albumctivity_ijkPlayer.isPlaying()) {
                    isPlay = true;
                    currentProgress += 1000;
                    videoPlaySeekBar.setProgress(currentProgress);
                    videoTotalTimeTextView.setText(DateUtils.timeFormat_(currentProgress));
                    this.sendEmptyMessageDelayed(PROGRESS_BAR_FLAG, PROGRESS_BAR_DELAY_TIME);
                } else {
                    currentProgress = 0;
                    layoutReset();
                }
            } else if (msg.what == COUNTDOWN_FLAG && videoContorlLLayout != null && activity != null) {
                //时间到了,如果这个时候是显示的需要隐藏掉
                isHideSeekBar = true;
                activity.hideOrVisibilityTitle(View.GONE);
                videoContorlLLayout.setVisibility(View.GONE);
            }
        }
    };

    public static LocalVideoPreviewFragment newInstanceFragment(MoAlbumItem moAlbumItem) {
        LocalVideoPreviewFragment videoPreviewFragment = new LocalVideoPreviewFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("moalbumitem", moAlbumItem);
        videoPreviewFragment.setArguments(bundle);
        return videoPreviewFragment;
    }

    @OnClick({R.id.startPlay, R.id.videoPlay, R.id.videoPreViewBg})
    public void onViewClicked(View view) {
        File file = new File(item.getmThumbnail().getmUri());
        if (file != null && !file.exists()) {
            CameraToastUtil.show(getString(R.string.file_exists), getContext());
            return;
        }
        switch (view.getId()) {
            case R.id.startPlay:
//                seekBarHandler.sendEmptyMessage(HANDLER_FLAG);
                startPlay.setVisibility(View.GONE);
                videoPreViewBg.setVisibility(View.GONE);
                startPlay(item.getmThumbnail().getmUri());
                break;
            case R.id.videoPlay:
                mainHandler.removeMessages(COUNTDOWN_FLAG);
                mainHandler.removeMessages(PROGRESS_BAR_FLAG);
//                vc_albumctivity_videoControler.videoPause();
                activity.controlLayoutVisible();
                ijk_albumctivity_ijkPlayer.pause();
                activity.hideOrVisibilityTitle(View.VISIBLE);
                videoContorlLLayout.setVisibility(View.GONE);
                startPlay.setVisibility(View.VISIBLE);
                break;
            case R.id.videoPreViewBg:
                controlTitleHideOrVisibility();
                break;
        }
    }

    @Override
    public int initView() {
        return R.layout.local_video_preview_fragment_layout;
    }

    @Override
    public void initClick() {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (!isVisibleToUser && ijk_albumctivity_ijkPlayer != null) {
            mainHandler.removeMessages(PROGRESS_BAR_FLAG);
            mainHandler.removeMessages(COUNTDOWN_FLAG);
            currentProgress = 0;
            isPlay = false;
            ijk_albumctivity_ijkPlayer.release();
//            ijk_albumctivity_ijkPlayer.pause(); 如果页面切换的时候,只需要暂停播放,打开这两行代码
//            startPlay.setVisibility(View.VISIBLE);
            layoutReset();
        }
    }

    private void layoutReset() {
        //重置所有的状态
        if (mainHandler != null)
            mainHandler.removeMessages(COUNTDOWN_FLAG);
        if (videoPreViewBg != null && !isPlay)
            videoPreViewBg.setVisibility(View.VISIBLE);
        if (startPlay != null)
            startPlay.setVisibility(View.VISIBLE);
        if (videoContorlLLayout != null)
            videoContorlLLayout.setVisibility(View.GONE);
        if (activity != null) {
            activity.controlLayoutVisible();
            activity.hideOrVisibilityTitle(View.VISIBLE);
        }
        if (videoPlaySeekBar != null)
            videoPlaySeekBar.setProgress(0);
        if (videoTotalTimeTextView != null)
            videoTotalTimeTextView.setText(DateUtils.timeFormat_(item.getmVideo().getmDuration()));
    }

    @Override
    public void onStop() {
        super.onStop();
        if (ijk_albumctivity_ijkPlayer != null)
            ijk_albumctivity_ijkPlayer.release();
        if (videoPreViewBg != null) {
            videoPreViewBg.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void initData() {
        item = (MoAlbumItem) this.getArguments().getSerializable("moalbumitem");
        GlideUtils.GlideLoader(getContext(), item.getmThumbnail().getmUri(), videoPreViewBg);

        activity = (MyAlbumPreview) getActivity();
        activity.setPagerChange(this);
        videoPreviewFragmentPresenter = new VideoPreviewFragmentPresenter(getActivity(), this, new Handler());

//        ijk_albumctivity_ijkPlayer.setPlayerConfig(config);
        //vc_albumctivity_videoControler.setMediaPlayer(ijk_albumctivity_ijkPlayer);
        videoTotalTimeTextView.setText(DateUtils.timeFormat_(item.getmVideo().getmDuration()));
        Log.i("TIME_TEST", "initData: " + item.getmVideo().getmDuration());
        maxProgress = (int) (item.getmVideo().getmDuration() / 1000) * 1000;
        videoPlaySeekBar.setMax(maxProgress);
        videoPlaySeekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (videoPreviewFragmentPresenter != null) {
            videoPreviewFragmentPresenter.destroy();
            videoPreviewFragmentPresenter = null;
        }
    }

    private void startPlay(String video) {
        // vc_albumctivity_videoControler.setSelect(false);
        ijk_albumctivity_ijkPlayer.setUrl(video);
        ijk_albumctivity_ijkPlayer.setOnClickListener(this);
        ijk_albumctivity_ijkPlayer.setScreenScale(IjkVideoView.SCREEN_SCALE_DEFAULT);
        ijk_albumctivity_ijkPlayer.start();
        videoContorlLLayout.setVisibility(View.VISIBLE);
        activity.controlLayoutGone();

        mainHandler.sendEmptyMessageDelayed(PROGRESS_BAR_FLAG, PROGRESS_BAR_DELAY_TIME);
        startCountdown();
    }

    private void startCountdown() {
        mainHandler.sendEmptyMessageDelayed(COUNTDOWN_FLAG, COUNTDOWN_TIME);
    }

    @Override
    public void videoStartUi() {
    }

    @Override
    public void videoStopUi() {
    }

    @Override
    public void videoResumetUi() {
    }

    @Override
    public void videoPauseUi() {
    }

    @Override
    public void startPlayVideo() {
    }

    @Override
    public void startVideoPreview(BaseData baseData) {
        AVFrame avFrame = new AVFrame(baseData, baseData.getmDataSize(), false, true);
    }

    @Override
    public void startAudioPreview(BaseData baseData) {
        Log.d("", "startAudioPreview: " + baseData.toString());
    }

    @Override
    public void refreshVideoCurrent(int currentTime) {
    }

    @Override
    public void onFinishVideo() {

        //todo  结束下载的逻辑添加一下
        CameraToastUtil.show("结束下载 数据", getActivity());
        Mp4V2Native.getInstance().stopRecord();

    }

    @Override
    public void seek(long seekTime) {
        videoResumetUi();
    }

    @Override
    public void onBackPressed() {
        activity.finish();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mainHandler.removeMessages(COUNTDOWN_FLAG);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mainHandler.sendEmptyMessageDelayed(COUNTDOWN_FLAG, COUNTDOWN_TIME);
        seekBarChange(seekBar);
    }

    private void seekBarChange(SeekBar seekBar) {

        int seekBarProgress = seekBar.getProgress();
        seekBarProgress = (seekBarProgress / 1000) * 1000;
        currentProgress = seekBarProgress;
        ijk_albumctivity_ijkPlayer.seekTo(currentProgress);

        Log.i("SEEK_TO", "max progress = " + maxProgress + " current progress = " + seekBarProgress);

//        videoPlaySeekBar.setProgress(currentProgress);
//        videoTotalTimeTextView.setText(DateUtils.timeFormat_(currentProgress));
    }

    @Override
    public void videoMarkSuccess() {
        CameraToastUtil.show("mark Success", getContext());
    }

    public boolean isPlayVideo() {
        return ijk_albumctivity_ijkPlayer.isPlaying();
    }

    @Override
    public void onClick(View v) {
        if (startPlay.getVisibility() == View.VISIBLE) {
            controlTitleHideOrVisibility();
            return;
        }
        if (isHideSeekBar) {
            //显示,开始计时
            isHideSeekBar = false;
            startCountdown();
            videoContorlLLayout.setVisibility(View.VISIBLE);
            if (activity != null)
                activity.hideOrVisibilityTitle(View.VISIBLE);
        } else {
            //隐藏,取消计时
            isHideSeekBar = true;
            mainHandler.removeMessages(COUNTDOWN_FLAG);
            if (activity != null)
                activity.hideOrVisibilityTitle(View.GONE);
            videoContorlLLayout.setVisibility(View.GONE);
        }
    }

    private void controlTitleHideOrVisibility() {
        if (isHidePlay) {
            isHidePlay = false;
            activity.controlLayoutVisible();
            activity.hideOrVisibilityTitle(View.VISIBLE);
        } else {
            isHidePlay = true;
            activity.controlLayoutGone();
            activity.hideOrVisibilityTitle(View.GONE);
        }
    }
}
