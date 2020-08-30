package com.test.xcamera.moalbum.fragment;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.framwork.base.view.MOBaseFragment;
import com.test.xcamera.R;
import com.test.xcamera.album.preview.fragmentinterface.VideoPreviewFragmentInterface;
import com.test.xcamera.album.preview.presenter.VideoPreviewFragmentPresenter;
import com.test.xcamera.bean.BaseData;
import com.test.xcamera.bean.MoAlbumItem;
import com.test.xcamera.moalbum.activity.MyAlbumPreview;
import com.test.xcamera.player.AVFrame;
import com.test.xcamera.util.AACDecoderUtil;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.CustomGlideUtils;
import com.test.xcamera.utils.DateUtils;
import com.test.xcamera.utils.PUtil;
import com.test.xcamera.utils.SPUtils;
import com.test.xcamera.view.MarkSeekBar;
import com.test.xcamera.view.VideoPreviewTextureView;
import com.test.xcamera.widget.ChrysanthemumView;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * author zxc
 * createTime 2019/10/12  下午11:49
 */
public class CameraVideoPreviewFragment extends MOBaseFragment implements VideoPreviewFragmentInterface, MyAlbumPreview.PagerChange, SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    private final String TAG = "VideoPreviewFragment";
    private final String markAnimationPath = "animation/mark_animation.json";
    @BindView(R.id.videoPreViewBg)
    ImageView videoPreViewBg;
    @BindView(R.id.media_codec_texture_view)
    VideoPreviewTextureView mediaCodecTextureView;
    @BindView(R.id.videoPlayB)
    ImageView videoPlayB;
    @BindView(R.id.videoPlaySeekBar)
    MarkSeekBar videoPlaySeekBar;
    @BindView(R.id.videoTotalTimeTextView)
    TextView videoTotalTimeTextView;
    @BindView(R.id.videoControlTime)
    LinearLayout videoControlTime;
    @BindView(R.id.videoPlay)
    ImageView videoPlay;
    @BindView(R.id.markImage)
    LottieAnimationView markImage;
    @BindView(R.id.videoContorlLLayout)
    RelativeLayout videoContorlLLayout;
    @BindView(R.id.videoContorlMarkLabel)
    ImageView videoContorlMarkLabel;
    @BindView(R.id.mChrysanthemumView)
    ChrysanthemumView mChrysanthemumView;
    @BindView(R.id.chrysanthemumViewLayout)
    RelativeLayout chrysanthemumViewLayout;

    private MyAlbumPreview activity;
    private VideoPreviewFragmentPresenter videoPreviewFragmentPresenter;
    private MoAlbumItem item;
    private ArrayList<Long> markArray = new ArrayList<>();
    private ArrayList<Long> copyMarkArray = new ArrayList<>();
    private AACDecoderUtil aacDecoderUtil;
    private long videoDuration;
    private int currentTouchProgress;
    private boolean isTouchSeekBar;
    private long[] mark_list;
    private float speed;
    private float frameMs = 0;
    private float videoFrameMs = 0;
    private boolean isStop = false;
    private int rotate;
    private String uriPhoto;
    private boolean isHideSeekBar = false;
    private boolean isHidePlay = false;
    private final int COUNTDOWN_FLAG = 201;
    private final int COUNTDOWN_DELAY_TIME = 3000;
    @SuppressLint("HandlerLeak")
    private Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 201 && videoContorlLLayout != null && activity != null) {
                isHideSeekBar = true;
//                videoContorlLLayout.setVisibility(View.GONE);
//                videoContorlMarkLabel.setVisibility(View.GONE);
                setMediaControlState(View.GONE);
                activity.hideOrVisibilityTitle(View.GONE);
            }
        }
    };

    public static CameraVideoPreviewFragment newInstanceFragment(MoAlbumItem moAlbumItem) {
        CameraVideoPreviewFragment videoPreviewFragment = new CameraVideoPreviewFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("moalbumitem", moAlbumItem);
        videoPreviewFragment.setArguments(bundle);
        return videoPreviewFragment;
    }

    @Override
    public int initView() {
        return R.layout.video_preview_fragment_layout;
    }

    @Override
    public void initClick() {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser && videoPreviewFragmentPresenter != null) {
            if (videoPreviewFragmentPresenter.getVideoStatus() != 2) {
                videoPreviewFragmentPresenter.stopVideo();
            }
            videoPreviewFragmentPresenter.cancelConnectionCallback();
            videoStopUi();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (videoPreviewFragmentPresenter != null) {
            videoPreviewFragmentPresenter.destroy();
            videoPreviewFragmentPresenter = null;
        }
    }

    @Override
    public void initData() {
        item = (MoAlbumItem) this.getArguments().getSerializable("moalbumitem");
        int width = item.getmVideo().getmWidth();
        int heigth = item.getmVideo().getmHeight();
        boolean lapsevideo = item.getmType().equals("lapsevideo");
        Log.i(TAG, "initData: " + item.getmType());
        if (lapsevideo) {
            markImage.setVisibility(View.GONE);
        } else {
            markImage.setVisibility(View.VISIBLE);
        }
        uriPhoto = item.getmThumbnail().getmUri();
        rotate = item.getRotate();
        activity = (MyAlbumPreview) getActivity();
        activity.setPagerChange(this);
        mediaCodecTextureView.setOnClickListener(this);

        if (getActivity() instanceof MyAlbumPreview) {
            aacDecoderUtil = ((MyAlbumPreview) getActivity()).mAacDecoderUtil;
        }
        videoPreviewFragmentPresenter = new VideoPreviewFragmentPresenter(activity, this, new Handler());
        videoDuration = item.getmVideo().getmDuration();
        videoTotalTimeTextView.setText(DateUtils.stringForTime(videoDuration));
        videoPlaySeekBar.setMax((int) videoDuration);

        videoPlaySeekBar.setOnSeekBarChangeListener(this);
        mark_list = item.getmVideo().getMarkList();
        speed = Float.parseFloat(item.getmVideo().getSpeed());
        initMarkArray();
        videoPlaySeekBar.setCurrentMarkPoint(videoPlaySeekBar.getMax(), markArray);
        rotate(rotate);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        rotate(rotate);
    }

    private void viewAnimation(int ratate) {
        Animation anim = new RotateAnimation(0f, ratate, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setFillAfter(true); // 设置保持动画最后的状态
        anim.setDuration(10); // 设置动画时间
        anim.setInterpolator(new AccelerateInterpolator()); // 设置插入器
        mediaCodecTextureView.startAnimation(anim);
    }

    @OnClick({R.id.videoPlayB, R.id.videoPlay, R.id.markImage, R.id.videoPreViewBg})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.videoPlayB:
            case R.id.videoPlay:
                int videoStatus = videoPreviewFragmentPresenter.getVideoStatus();
                videoStatusControl(videoStatus);
                break;
            case R.id.markImage:
                float temporaryFrameMs = videoPlaySeekBar.getProgress();//videoFrameMs;
                if (markArray.size() > 0) {
                    boolean markPointIsOk = markPointIsOk(temporaryFrameMs);
                    if (markPointIsOk) {
                        startImageAnimation(temporaryFrameMs);
                    } else {
                        CameraToastUtil.show(getResources().getString(R.string.current_mark), this.getContext());
                    }
                } else {
                    startImageAnimation(temporaryFrameMs);
                }
                break;
            case R.id.videoPreViewBg:
                controlTitleHideOrVisibility();
                break;
        }
    }

    private boolean markPointIsOk(float temporaryMs) {
        if (videoDuration - temporaryMs < 500) {
            return false;
        }
        boolean left = false;
        boolean right = false;
        long temporary = (long) temporaryMs;
        copyMarkArray.add(temporary);
        //排序
        Collections.sort(copyMarkArray);
        //比较,能不能打mark
        int indexOf = copyMarkArray.indexOf(temporary);
        long currentTempMs = temporary;
        long biJiaoTime = 2000;

        if (indexOf != 0) {
            long leftTime = copyMarkArray.get(indexOf - 1);
            left = (currentTempMs - leftTime) > biJiaoTime;
        } else {
            left = true;
        }

        if (!left) {
            copyMarkArray.remove(indexOf);
            return left;
        }

        if (indexOf != copyMarkArray.size() - 1) {
            long rightTime = copyMarkArray.get(indexOf + 1);
            right = (rightTime - currentTempMs) > biJiaoTime;
        } else {
            right = true;
        }
        copyMarkArray.remove(indexOf);
        return left && right;
    }

    private boolean markIsDelete(float temporaryMs) {
        boolean isDelete = false;
        for (int i = 0; i < markArray.size(); i++) {
            long aLong = markArray.get(i);
            if (temporaryMs >= aLong - 1000 && temporaryMs <= aLong + 1000) {
                isDelete = true;
            }
            if (isAnimation || markImage.isAnimating()) {
                //如果动画已经开始,或者是正在动画中,比较暂停退出循环
                break;
            }
        }
        return isDelete;
    }

    private boolean isAnimation = false;

    private void startImageAnimation(float frameMsTemporary) {
        if (markImage == null) {
            return;
        }
        if (markImage.isAnimating()) {
            //取消动画
            markImage.cancelAnimation();
            markImage.clearAnimation();
            markImage.setImageResource(R.mipmap.icon_preview_mark);
        } else {
            isAnimation = true;
            markImage.removeAllAnimatorListeners();
            markImage.setVisibility(View.VISIBLE);
            markImage.loop(false);
            markImage.setSpeed(1f);
            markImage.setAnimation(markAnimationPath);
            markImage.playAnimation();
        }

        markImage.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (markImage != null) {
                    markImage.clearAnimation();
                    isAnimation = false;
                    //2秒动画结束,开始添加mark点
//                markArray.add(frameTimeMarkTimeTemp - item.getmCreateTime());
                    markArray.add((long) frameMsTemporary);
                    copyMarkArray.add((long) frameMsTemporary);
                    long markTime = ((long) (frameMsTemporary * speed)) + item.getmCreateTime();
                    videoPreviewFragmentPresenter.markVideo(item.getmVideo().getmUri(), markTime);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                if (markImage != null) {
                    markImage.clearAnimation();
                    isAnimation = false;
                }
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }


    private void videoStatusControl(int status) {
        switch (status) {
            case 0:
            case 2:
                videoPlayB.setVisibility(View.GONE);
                chrysanthemumViewLayout.setVisibility(View.VISIBLE);
                mChrysanthemumView.startAnimation();
                videoPreviewFragmentPresenter.setConnectionCallback();
                videoPreviewFragmentPresenter.startPlayVideo(item.getmVideo().getmUri(), new VideoPreviewFragmentPresenter.VideoPlayerListener() {
                    @Override
                    public void videoPlayError(String errorMsg) {
                        if (getActivity() == null || isHidden() || isDetached()) {
                            return;
                        }
                        CameraToastUtil.show(errorMsg, getContext());
                        videoStopUi();
//                        videoPreviewFragmentPresenter.stopVideo();
                    }
                });
                startCountDown();
                break;
            case 4:
            case 1:
                videoPreviewFragmentPresenter.cancelTimer();
                videoPreviewFragmentPresenter.pauseVideo(false);
                break;
            case 3:
                int progress = videoPlaySeekBar.getProgress();
                videoPreviewFragmentPresenter.continueVideo();
                videoPreviewFragmentPresenter.continueTimer((int) (videoDuration - progress));
                break;
        }
    }

    private void startCountDown() {
        mainHandler.sendEmptyMessageDelayed(COUNTDOWN_FLAG, COUNTDOWN_DELAY_TIME);
    }

    @Override
    public void videoStartUi() {
        if (videoPlayB == null || isHidden() || isDetached()) {
            return;
        }
        chrysanthemumViewLayout.setVisibility(View.GONE);
        mChrysanthemumView.stopAnimation();
//        videoPlayB.setVisibility(View.GONE);
//        videoContorlLLayout.setVisibility(View.VISIBLE);
//        videoContorlMarkLabel.setVisibility(View.VISIBLE);
        setMediaControlState(View.VISIBLE);
        videoPreViewBg.setVisibility(View.GONE);
        activity.controlLayoutGone();
        videoPlay.setImageResource(R.mipmap.pause_playicon);
    }

    @Override
    public void videoStopUi() {
        mainHandler.removeMessages(COUNTDOWN_FLAG);
        if (videoPlayB == null || isHidden() || isDetached()) {
            return;
        }
        isStop = false;
        videoPlayB.setVisibility(View.VISIBLE);
//        videoContorlLLayout.setVisibility(View.GONE);
//        videoContorlMarkLabel.setVisibility(View.GONE);
        setMediaControlState(View.GONE);
//        videoPreViewBg.setVisibility(View.VISIBLE);
        if (activity != null) {
            activity.controlLayoutVisible();
            activity.hideOrVisibilityTitle(View.VISIBLE);
        }
        videoPlaySeekBar.setMax((int) videoDuration);
        videoPlaySeekBar.setProgress(0);
        videoTotalTimeTextView.setText(DateUtils.stringForTime(videoDuration));
    }

    @Override
    public void videoResumetUi() {
        if (videoPlayB == null || isHidden() || isDetached()) {
            return;
        }
        videoPlayB.setVisibility(View.GONE);
//        videoContorlLLayout.setVisibility(View.VISIBLE);
//        videoContorlMarkLabel.setVisibility(View.VISIBLE);
        setMediaControlState(View.VISIBLE);
        activity.controlLayoutGone();
        videoPlay.setImageResource(R.mipmap.pause_playicon);
        startCountDown();
    }

    @Override
    public void videoPauseUi() {
        if (isTouchSeekBar || videoPlayB == null || isHidden() || isDetached()) {
            return;
        }
        videoPlayB.setVisibility(View.VISIBLE);
//        videoContorlLLayout.setVisibility(View.GONE);
//        videoContorlMarkLabel.setVisibility(View.GONE);
        setMediaControlState(View.GONE);
        activity.controlLayoutVisible();
        mainHandler.removeMessages(COUNTDOWN_FLAG);
        if (activity != null) {
            activity.hideOrVisibilityTitle(View.VISIBLE);
        }
    }

    @Override
    public void startPlayVideo() {
    }

    @Override
    public void startVideoPreview(BaseData baseData) {
        if (isHidden() || isDetached()) {
            return;
        }
        AVFrame avFrame = new AVFrame(baseData, baseData.getmDataSize(), false, true);
        if (!getUserVisibleHint() || mediaCodecTextureView == null || avFrame == null) {
            return;
        }
        mediaCodecTextureView.onReceived(avFrame);
        refreshSeekBar(baseData, null);
    }

    @Override
    public void startAudioPreview(BaseData baseData) {
        if (isHidden() || isDetached()) {
            return;
        }
        if (aacDecoderUtil != null) {
            aacDecoderUtil.onReceivedAudioData(baseData.getmAudioFrameInfo());
        }
        refreshSeekBar(null, baseData);
    }


    private void refreshSeekBar(BaseData videoBaseData, BaseData audioBaseData) {
        if (videoBaseData != null) {
            frameMs = videoBaseData.getmVideoFrameInfo().getmTimeMs();
            videoFrameMs = videoBaseData.getmVideoFrameInfo().getmTimeMs();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (videoPlaySeekBar == null || videoTotalTimeTextView == null)
                        return;
                    Log.i("TEST_PLAY_B", "stopVideo: playback_stop 被调用了! " + videoFrameMs + " this = " + videoBaseData.getmVideoFrameInfo().getmSeq());
                    videoPlaySeekBar.setProgress((int) videoFrameMs);
                    videoTotalTimeTextView.setText(DateUtils.stringForTime((long) videoFrameMs));
                }
            });
            // showDeleteMarkIcon(frameMs);
        } else {
            /**
             * 这个是音频的时间戳  在mark 的时候只需要使用视频的就行
             */
//            frameTimeMarkTime = audioBaseData.getmAudioFrameInfo().getmTimeTs();
            frameMs = audioBaseData.getmAudioFrameInfo().getmTimeMs();
        }

        if (((videoDuration - frameMs) < 70) && !isStop) {
            isStop = true;
            if (videoPreviewFragmentPresenter != null)
                videoPreviewFragmentPresenter.stopVideo();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    videoStopUi();
                }
            });
        }
//        Log.i(TAG, frameMs + " refreshSeekBar:  frameTime = " + frameTimeMarkTime);
    }

    private boolean isProcess = false;

    private void showDeleteMarkIcon(float frameMsTemporary) {
        if (markImage == null || markImage.isAnimating() || isAnimation || isProcess || markArray.size() <= 0) {
            return;
        }
        isProcess = true;
        boolean markIsDelete = markIsDelete(frameMsTemporary);
        if (markIsDelete) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (markImage != null)
                        markImage.setImageResource(R.mipmap.btn_delete_mark);
                }
            });
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (markImage != null)
                        markImage.setImageResource(R.mipmap.icon_preview_mark);
                }
            });
        }
        isProcess = false;
    }

    @Override
    public void refreshVideoCurrent(int currentTime) {
    }

    @Override
    public void onStop() {
        if (videoPreviewFragmentPresenter != null && videoPreviewFragmentPresenter.getVideoStatus() != 2) {
            videoPreviewFragmentPresenter.stopVideo();
        }
        super.onStop();
    }

    @Override
    public void onFinishVideo() {
//        videoPreviewFragmentPresenter.stopVideo();
    }

    @Override
    public void seek(long seekTime) {
        videoResumetUi();
        videoPlaySeekBar.setProgress(currentTouchProgress);
    }

    @Override
    public void onBackPressed() {
        if (videoPreviewFragmentPresenter != null && videoPreviewFragmentPresenter.getVideoStatus() != 2) {
            videoPreviewFragmentPresenter.stopVideo();
        }
        activity.finish();
    }

    public int getvideoStatus() {
        return videoPreviewFragmentPresenter.getVideoStatus();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mainHandler.removeMessages(COUNTDOWN_FLAG);
        isTouchSeekBar = true;
        videoPreviewFragmentPresenter.pauseVideo(true);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isTouchSeekBar = false;
        currentTouchProgress = seekBar.getProgress();
        videoPreviewFragmentPresenter.seekVideo(currentTouchProgress);
        mainHandler.sendEmptyMessageDelayed(COUNTDOWN_FLAG, COUNTDOWN_DELAY_TIME);
    }

    @Override
    public void videoMarkSuccess() {
        int max = videoPlaySeekBar.getMax();
        videoPlaySeekBar.setCurrentMarkPoint(max, markArray);
        CameraToastUtil.show(this.getResources().getString(R.string.mark_success), getContext());
    }

    /**
     * mark point process
     *
     * @param
     */
    private void initMarkArray() {
        if (mark_list != null && mark_list.length > 0) {
            for (int i = 0; i < mark_list.length; i++) {
                long mark = 0;
                if (speed != 0) {
                    mark = (long) ((mark_list[i] - item.getmCreateTime()) / speed);
                    Log.i(TAG, "initMarkArray: mark = " + mark + " mark_list[i] = " + mark_list[i] + " item.getmCreateTime() " + item.getmCreateTime());
                }
                markArray.add(mark);
                copyMarkArray.add(mark);
            }
        }
    }

    private void rotate(int rotate) {
        int h = PUtil.getScreenH(getContext());
        int w = (int) (h * 1080 / 1920f);

        int range = 0;
        if (rotate == 0) {
            range = 0;
            mediaCodecTextureView.setRotate(false);
        } else if (rotate == 1) {
            range = 270;
            setVideoCoverParams(h, w);
            mediaCodecTextureView.setRotate(true);
        } else if (rotate == 2) {
            range = 180;
            setVideoCoverParams(h, w);

            mediaCodecTextureView.setRotate(true);
        } else if (rotate == 3) {
            range = 90;
            setVideoCoverParams(h, w);
            mediaCodecTextureView.setRotate(true);
        }
        mediaCodecTextureView.setHeightRotation(h);
        mediaCodecTextureView.setWidthRotation(w);

        CustomGlideUtils.loadAlbumPhotoThumbnailRotate(getActivity(), uriPhoto, videoPreViewBg, 0L, range);
        mediaCodecTextureView.setTextureListener(range);
        mediaCodecTextureView.requestLayout();
    }

    private void setVideoCoverParams(int h, int w) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) videoPreViewBg.getLayoutParams();
        layoutParams.width = w;
        layoutParams.height = h;
        videoPreViewBg.setLayoutParams(layoutParams);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.media_codec_texture_view) {
            if (videoPlayB.getVisibility() == View.VISIBLE) {
                controlTitleHideOrVisibility();
                return;
            }
            if (isHideSeekBar) {
                //显示,开始计时
                isHideSeekBar = false;
                startCountDown();
//                videoContorlLLayout.setVisibility(View.VISIBLE);
//                videoContorlMarkLabel.setVisibility(View.VISIBLE);
                setMediaControlState(View.VISIBLE);
                if (activity != null)
                    activity.hideOrVisibilityTitle(View.VISIBLE);
            } else {
                //隐藏,取消计时
                isHideSeekBar = true;
                mainHandler.removeMessages(COUNTDOWN_FLAG);
                if (activity != null)
                    activity.hideOrVisibilityTitle(View.GONE);
//                videoContorlLLayout.setVisibility(View.GONE);
//                videoContorlMarkLabel.setVisibility(View.GONE);
                setMediaControlState(View.GONE);
            }
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

    /**
     * 播放进度条显示控制，统计mark提示展示次数，超过3次不再显示
     *
     * @param state
     */
    private void setMediaControlState(int state) {
        if (state == View.VISIBLE) {
            int count = (int) SPUtils.get(getActivity(), "mark_label_count", 0);
            SPUtils.put(getActivity(), "mark_label_count", count++);
            if (count++ < 4) {
                videoContorlMarkLabel.setVisibility(View.VISIBLE);
            }
            videoContorlLLayout.setVisibility(View.VISIBLE);
        } else if (state == View.GONE){
            videoContorlLLayout.setVisibility(View.GONE);
            videoContorlMarkLabel.setVisibility(View.GONE);
        }
    }
}
