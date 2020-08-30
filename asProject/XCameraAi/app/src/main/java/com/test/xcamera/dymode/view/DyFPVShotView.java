package com.test.xcamera.dymode.view;

import android.Manifest;
import android.animation.Animator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.editvideo.ScreenUtils;
import com.test.xcamera.R;
import com.test.xcamera.activity.DyFPVActivity;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.dymode.callbacks.DyFPVCallback;
import com.test.xcamera.dymode.managers.EffectPlatform;
import com.test.xcamera.dymode.presenter.DyFPVPresenter;
import com.test.xcamera.permission.RxPermissions;
import com.test.xcamera.phonealbum.MyVideoEditActivity;
import com.test.xcamera.phonealbum.SelectMusicActivity;
import com.test.xcamera.phonealbum.bean.MusicBean;
import com.test.xcamera.statistic.StatisticDYCapture;
import com.test.xcamera.utils.AppExecutors;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.FileUtils;
import com.test.xcamera.utils.Md5Util;
import com.test.xcamera.utils.NetworkUtil;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zll on 2020/1/19.
 */

public class DyFPVShotView extends RelativeLayout implements CountDownLayout.ThouchTrimListener {
    private static final String TAG = "DyFPVShotView";
    private static final int RECORD_MIN_TIME = 5 * 1000;
    private static final int VIDEO_MAX_TIME = 60 * 1000;
    private static final int START_RECORD = 1;
    private static final int STOP_RECORD = 2;
    private static final int SET_DELETE_STATE = 3;
    private static final int SET_RIGHT_STATE = 4;
    private String[] DY_PERMISSIONS = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
            , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};

    @BindView(R.id.dy_fpv_shot_confirm)
    ImageView mShotConfirm;
    @BindView(R.id.dy_fpv_shot_confirm_layout)
    RelativeLayout mShotConfirmLayout;
    @BindView(R.id.dy_fpv_shot_delete_video_image)
    ImageView mShotDeleteVideoImage;
    @BindView(R.id.dy_fpv_shot_delete_video_layout)
    RelativeLayout mShotDeleteVideoLayout;
    @BindView(R.id.dy_fpv_shot_shot_image)
    ImageView mShotImage;
    @BindView(R.id.dy_fpv_shot_lottie)
    LottieAnimationView mShotLottie;
    @BindView(R.id.dy_fpv_shot_shot_image_layout)
    RelativeLayout mShotImageLayout;
    @BindView(R.id.dy_fpv_shot_track_image)
    public ImageView mShotTrackImage;
    @BindView(R.id.dy_fpv_shot_track_layout)
    RelativeLayout mShotTrackLayout;
    @BindView(R.id.dy_fpv_shot_center_image)
    ImageView mShotCenterImage;
    @BindView(R.id.dy_fpv_shot_center_layout)
    RelativeLayout mShotCenterLayout;
    @BindView(R.id.dy_fpv_shot_layout)
    LinearLayout mShotLayout;
    @BindView(R.id.dy_fpv_shot_zoom_text)
    public TextView mShotZoomText;
    @BindView(R.id.dy_fpv_shot_zoom_layout)
    public RelativeLayout mShotZoomLayout;
    @BindView(R.id.dy_fpv_right_layout)
    LinearLayout mRightLayout;
    @BindView(R.id.dy_fpv_shot_rotate_camera)
    LinearLayout mShotRotateCamera;
    @BindView(R.id.dy_fpv_shot_record_speed_image)
    ImageView mShotRecordSpeedImage;
    @BindView(R.id.dy_fpv_shot_record_speed_text)
    TextView mShotRecordSpeedText;
    @BindView(R.id.dy_fpv_shot_record_speed)
    LinearLayout mShotRecordSpeed;
    @BindView(R.id.dy_fpv_shot_filter)
    LinearLayout mShotFilter;
    @BindView(R.id.dy_fpv_shot_beauty)
    LinearLayout mShotBeauty;
    @BindView(R.id.dy_fpv_count_down)
    LinearLayout mCountDown;
    @BindView(R.id.dy_fpv_count_down_img)
    ImageView mCountDownImg;
    @BindView(R.id.dy_fpv_prop_layout)
    LinearLayout mPropLayout;
    @BindView(R.id.dy_fpv_cut_music)
    LinearLayout mCutMusic;
    @BindView(R.id.dy_fpv_back_btn)
    ImageView mBackBtn;
    @BindView(R.id.dy_fpv_choose_speed_view)
    DyFPVSpeedView mChooseSpeedView;
    @BindView(R.id.dy_fpv_record_line_layout)
    FrameLayout mFrameLayout;
    @BindView(R.id.dy_fpv_beauty_layout)
    DyFPVBeautyView mBeautyView;
    @BindView(R.id.dy_fpv_props_layout)
    DyFPVPropsView mPropsView;
    @BindView(R.id.dy_fpv_filter_layout)
    DyFilterView mFilterView;
    @BindView(R.id.dy_fpv_choose_music_layout)
    LinearLayout mChooseMusicLayout;
    @BindView(R.id.dy_fpv_choose_music_name)
    TextView mChooseMusicName;
    @BindView(R.id.dy_fpv_choose_music_img)
    ImageView mChooseMusicImg;
    @BindView(R.id.dy_fpv_count_down_layout)
    CountDownLayout mCountDownView;
    @BindView(R.id.dy_fpv_count_down_animation)
    LottieAnimationView mCountDownAnimation;
    @BindView(R.id.dy_fpv_record_delete_dialog)
    DyCommonDialog mDeleteDialog;
    @BindView(R.id.dy_fpv_record_cut_music_view)
    MaskViewLayout mCutMusicView;

    private RecordTimelineView mRecorderProgressView;
    private WeakReference<Context> mContext;
    private DyFPVPresenter mDyFPVPresenter;
    private DyFPVCallback mFPVCallback;
    private boolean mIsRecord = false;
    private int mCameraOrientation = 0;
    public int mZoomLevel = 10;
    private WeakReference<DyFPVActivity> mDyFPVActivity;
    private String countDown;
    private String mCutMusicPath = "";
    private MusicBean mMusicBean;
    public boolean mIsSpeedViewShow = false;
    private boolean mIsAnimation = false;
    private boolean mIsCancelAniamtion = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case START_RECORD:
                    mChooseSpeedView.setVisibility(GONE);
                    mShotRecordSpeedImage.setBackgroundResource(R.mipmap.icon_dy_speed_off);
                    stateStart("animation/animation_video_click.json", "animation/animation_video_time_cycle.json");
                    break;
                case STOP_RECORD:
                    stateStop("animation/animation_video_restore.json");
                    break;
                case SET_DELETE_STATE:
                    int state = (int) msg.obj;
                    mShotDeleteVideoLayout.setVisibility(state);
                    break;
                case SET_RIGHT_STATE:
                    int state1 = (int) msg.obj;
                    mBackBtn.setVisibility(state1);
                    setRightLayout(state1);
                    mChooseMusicLayout.setVisibility(state1);
                    break;
            }
        }
    };

    private void setRightLayout(int state) {
        mShotRecordSpeed.setVisibility(state);
        mShotFilter.setVisibility(state);
        mShotBeauty.setVisibility(state);
        mCountDown.setVisibility(state);
        mPropLayout.setVisibility(state);
        mCutMusic.setVisibility(state);
    }

    public DyFPVShotView(Context context) {
        super(context);
    }

    public DyFPVShotView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDyFPVPresenter(DyFPVPresenter presenter) {
        mDyFPVPresenter = presenter;
    }

    public void setDyFPVCallback(DyFPVCallback callback) {
        mFPVCallback = callback;
    }

    public void attachActivity(DyFPVActivity activity) {
        mDyFPVActivity = new WeakReference<>(activity);
        mPropsView.attachActivity(mDyFPVActivity.get());
        mFilterView.attachActivity(mDyFPVActivity.get());
        mBeautyView.attachActivity(mDyFPVActivity.get());

        mDyFPVActivity.get().mCountDownTime = RecordTimelineView.RECORD_MAX_TIME;
    }

    public void init(Context context) {
        mContext = new WeakReference<>(context);
        LayoutInflater.from(context).inflate(R.layout.dy_fpv_shot, this, true);

        ButterKnife.bind(this);

        setBackEnable(false);
        resetBackEnable();
        mChooseSpeedView.init(mContext.get(), this);
        mFilterView.init(mContext.get(), this);
        mBeautyView.init(mContext.get(), this);
        mPropsView.init(mContext.get(), this);
        mCountDownView.init(this);
        mCountDownView.setThouchTrimListener(this);
//
        initRecordProgress();

        mCountDownAnimation.loop(false);
        mCountDownAnimation.setScale(0.5f);
        mCountDownAnimation.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                DyFPVActivity.testLog("count down start");
                mIsAnimation = true;
                mCountDownView.setVisibility(GONE);
                setRightLayout(GONE);
                mShotLayout.setVisibility(GONE);
                mShotZoomLayout.setVisibility(GONE);
                mRecorderProgressView.setVisibility(GONE);
                setRockerViewState(GONE, 22);
                mChooseMusicLayout.setVisibility(GONE);
                mBackBtn.setVisibility(GONE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                DyFPVActivity.testLog("count down end");
                if (mIsCancelAniamtion) {
                    mIsCancelAniamtion = false;
                    return;
                }
                mIsAnimation = false;
                mCountDownAnimation.setVisibility(View.GONE);
                mShotLayout.setVisibility(VISIBLE);
                mRecorderProgressView.setVisibility(VISIBLE);
                mShotRotateCamera.setVisibility(VISIBLE);
                if (mDyFPVActivity.get().mIsDisConnect) {
                    setRightLayout(VISIBLE);
                    mShotLayout.setVisibility(VISIBLE);
                    setRockerViewState(VISIBLE, 22);
                    mChooseMusicLayout.setVisibility(VISIBLE);
                    mBackBtn.setVisibility(VISIBLE);
                } else {
                    performShotClick();
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        countDown = "animation/3_seconds.json";

        mDeleteDialog.setAllowCancel(true);
        mDeleteDialog.setDialogCallback(new DyCommonDialog.DyCommonDialogCallback() {
            @Override
            public void confirm() {
                if (mDeleteDialog.getDialogState() == DyCommonDialog.DialogState.DELETE) {
                    // 删除分段
                    mDyFPVPresenter.deleteLastFrag();
                    mDeleteDialog.dismiss();
                    if (mDyFPVPresenter.mCameraDisplay.getSegmentSize() <= 0) {
                        if (TextUtils.isEmpty(mCutMusicPath)) {
                            setCutmusicEnable(false);
                        } else {
                            setCutmusicEnable(true);
                        }
                        mShotDeleteVideoLayout.setVisibility(INVISIBLE);
                        mShotConfirmLayout.setVisibility(INVISIBLE);
                        mChooseMusicLayout.setEnabled(true);
                        mChooseMusicName.setTextColor(0XFFFFFFFF);
                        mChooseMusicImg.setBackgroundResource(R.mipmap.icon_choose_music);
                        if (checkActivity()) {
                            mDyFPVActivity.get().mCurrentMills = 0;
                            mDyFPVActivity.get().mRecordTime = 0;
                        }
                    }
                    if (checkActivity()) {
                        if (mDyFPVActivity.get().getTotalRecordingTime() < 3000) {
                            mShotConfirmLayout.setVisibility(INVISIBLE);
                        }
                        setCountDown(false, mDyFPVActivity.get().getTotalRecordingTime() / 1000);
                    }
                } else if (mDeleteDialog.getDialogState() == DyCommonDialog.DialogState.COMPRESS) {
                    StatisticDYCapture.getInstance().setOnEvent(StatisticDYCapture.FloatLayer_DYCapture_compose);
                    mDyFPVPresenter.concatVideo(mDeleteDialog);
                }
            }

            @Override
            public void cancel() {
                if (checkActivity()) {
                    if (mDyFPVActivity.get().mIsDisConnect) {
                        mDyFPVActivity.get().finishFPV();
                    }
                }

                mDeleteDialog.dismiss();
            }
        });

        mCutMusic.setEnabled(false);
        mCutMusic.setAlpha(0.5f);

        int width = ScreenUtils.getScreenWidth(AiCameraApplication.getContext());
        int height = ScreenUtils.getScreenHeight(AiCameraApplication.getContext());
        int margin;
        if (width <= 1920) margin = 10;
        else margin = (width - 1920) / 2 + 10;
        Log.d(TAG, "init: screen width: " + width + ", height: " + height +
                ", center: " + margin);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mShotLayout.getLayoutParams();
        params.leftMargin = margin;
        mShotLayout.setLayoutParams(params);
    }

    private boolean checkActivity() {
        return mDyFPVActivity != null && mDyFPVActivity.get() != null;
    }

    private void setRockerViewState(int state, int tag) {
        if (checkActivity()) {
            mDyFPVActivity.get().setRockerViewState(state, tag);
        }
    }


    private void initRecordProgress() {
        if (mContext == null && mContext.get() == null) {
            return;
        }
        mRecorderProgressView = new RecordTimelineView(mContext.get());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ScreenUtils.dip2px(AiCameraApplication.getContext(), 6), FrameLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(0,
                ScreenUtils.dip2px(AiCameraApplication.getContext(), 12),
                ScreenUtils.dip2px(AiCameraApplication.getContext(), 6),
                ScreenUtils.dip2px(AiCameraApplication.getContext(), 12));
        mRecorderProgressView.setColor(R.color.dy_record_progress_duraton, R.color.dy_record_progress_selected, R.color.dy_record_progress_white,
                R.color.dy_record_progress_timeview);
        mRecorderProgressView.setMaxDuration(RecordTimelineView.RECORD_MAX_TIME);
        mRecorderProgressView.setMinDuration(RecordTimelineView.RECORD_MIN_TIME);
        mFrameLayout.addView(mRecorderProgressView, params);
    }

    private void startAnimation() {
        if (!TextUtils.isEmpty(countDown)) {
            mCountDownAnimation.setAnimation(countDown);
            mCountDownAnimation.setVisibility(View.VISIBLE);
            mCountDownAnimation.playAnimation();
        }
    }

    @OnClick({R.id.dy_fpv_shot_confirm_layout, R.id.dy_fpv_shot_delete_video_layout,
            R.id.dy_fpv_shot_shot_image_layout, R.id.dy_fpv_shot_track_layout,
            R.id.dy_fpv_shot_center_layout,
            R.id.dy_fpv_shot_zoom_layout, R.id.dy_fpv_shot_rotate_camera,
            R.id.dy_fpv_shot_record_speed, R.id.dy_fpv_shot_filter,
            R.id.dy_fpv_shot_beauty, R.id.dy_fpv_count_down,
            R.id.dy_fpv_prop_layout, R.id.dy_fpv_cut_music, R.id.dy_fpv_back_btn,
            R.id.dy_fpv_choose_music_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dy_fpv_back_btn:
                mDyFPVPresenter.finish();
                break;
            case R.id.dy_fpv_shot_confirm_layout:
                if (mDyFPVPresenter.mCameraDisplay.isRecording()) {
                    clickRecord();
                }
                mDeleteDialog.showCompressLayout();
                break;
            case R.id.dy_fpv_shot_delete_video_layout:
                mDeleteDialog.showDeleteLayout();
                break;
            case R.id.dy_fpv_shot_shot_image_layout:
                if (checkActivity()) {
                    if (mDyFPVActivity.get().isOverTime()) {
                        CameraToastUtil.show90(getResources().getString(R.string.time_max), getContext());
                        return;
                    }
                }

                clickRecord();
                break;
            case R.id.dy_fpv_shot_track_layout:
                if (mShotTrackImage.isSelected()) {
                    mFPVCallback.stopTrack();
                } else {
                    StatisticDYCapture.getInstance().setOnEvent(StatisticDYCapture.FloatLayer_DYCapture_Track);
                    mFPVCallback.startTrack();
                }
                break;
            case R.id.dy_fpv_shot_center_layout:
                StatisticDYCapture.getInstance().setOnEvent(StatisticDYCapture.FloatLayer_Capture_MidPosition);
                mDyFPVPresenter.backCenter();
                break;
            case R.id.dy_fpv_shot_zoom_layout:
                StatisticDYCapture.getInstance().setOnEvent(StatisticDYCapture.FloatLayer_DYCapture_Zoom);
                digitalZoom();
                break;
            case R.id.dy_fpv_shot_rotate_camera:
                StatisticDYCapture.getInstance().setOnEvent(StatisticDYCapture.FloatLayer_DYCapture_LensFlip);
                clickRotateCamera();
                break;
            case R.id.dy_fpv_shot_record_speed:
                StatisticDYCapture.getInstance().setOnEvent(StatisticDYCapture.FloatLayer_DYCapture_FastSlow);
                clickSpeedLayout();
                break;
            case R.id.dy_fpv_shot_filter:
                StatisticDYCapture.getInstance().setOnEvent(StatisticDYCapture.FloatLayer_DYCapture_Filter);
                clickFilterLayout();
                break;
            case R.id.dy_fpv_shot_beauty:
                StatisticDYCapture.getInstance().setOnEvent(StatisticDYCapture.FloatLayer_DYCapture_Beautify);
                clickBeautyLayout();
                break;
            case R.id.dy_fpv_count_down:

                clickCountDownLayout();
                break;
            case R.id.dy_fpv_prop_layout:
                StatisticDYCapture.getInstance().setOnEvent(StatisticDYCapture.FloatLayer_DYCapture_Prop);
                clickPropLayout();
                break;
            case R.id.dy_fpv_cut_music:
                clickCutMusicLayout();
//                mDyFPVPresenter.authLogin();
                break;
            case R.id.dy_fpv_choose_music_layout:
                StatisticDYCapture.getInstance().setOnEvent(StatisticDYCapture.FloatLayer_DYCapture_SelectMusic);
                if (checkActivity()) {
                    SelectMusicActivity.openForResult(mDyFPVActivity.get(), MyVideoEditActivity.REQUEST_CODE, -180, mMusicBean);

                }
//                mDyFPVActivity.startActivity(new Intent(mDyFPVActivity, TestAct.class));
                Log.d(TAG, "跳转-------------");
                break;
        }
    }

    public void clickRecord() {
        StatisticDYCapture.getInstance().setOnEvent(StatisticDYCapture.FloatLayer_DYCapture_capture);
        if (!checkActivity()) {
            return;
        }
        new RxPermissions(mDyFPVActivity.get())
                .requestEachCombined(DY_PERMISSIONS)
                .subscribe(permission -> {
                    if (permission.granted) {
                        mIsRecord = !mIsRecord;
//                        FileUtil.writeFileToSDCard(FileUtil.path, "click record 111".getBytes(), "countdown.txt", true, true, false);
                        mDyFPVPresenter.record();
                        if (mDyFPVPresenter.hasRecordVideo()) {
                            mChooseMusicLayout.setEnabled(false);
                            mChooseMusicName.setTextColor(0XFFB8BCBE);
                            mChooseMusicImg.setBackgroundResource(R.mipmap.icon_choosed_music);
                        }
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        CameraToastUtil.show90(getResources().getString(R.string.need_open_permission), getContext());
                    } else {
                        CameraToastUtil.show90(getResources().getString(R.string.need_open_permission), getContext());
                    }
                });

    }

    private void clickRotateCamera() {
        if (mCameraOrientation == 0) {
            mCameraOrientation = 1;
        } else {
            mCameraOrientation = 0;
        }
        mDyFPVPresenter.rotateCamera(mCameraOrientation);
    }

    public void clickPropLayout() {
        if (mPropsView.isShown()) {
            mPropsView.setVisibility(GONE);
            mShotZoomLayout.setVisibility(VISIBLE);
            setRockerViewState(VISIBLE, 21);
            mShotLayout.setVisibility(VISIBLE);
            mRightLayout.setVisibility(VISIBLE);
            mBackBtn.setVisibility(VISIBLE);
            mChooseMusicLayout.setVisibility(VISIBLE);
            setSpeedView();
        } else {
            Log.d(TAG, "clickPropLayout hasFiltersData : " + EffectPlatform.getInstance().hasPropsData());
            if (!NetworkUtil.isNetworkConnected(AiCameraApplication.getContext()) && !EffectPlatform.getInstance().hasPropsData()) {
                CameraToastUtil.show(AiCameraApplication.getContext().getString(R.string.no_network), AiCameraApplication.getContext());
                return;
            }
            if (!EffectPlatform.getInstance().hasPropsData()) {
                EffectPlatform.getInstance().fetchEffectList(EffectPlatform.PANEL_DEFAULT, new EffectPlatform.EffectRequestCallback() {
                    @Override
                    public void onSuccess() {
                        mPropsView.notifyPropsAdapter();
                    }

                    @Override
                    public void onFailed() {

                    }
                });
            }
            mPropsView.setVisibility(VISIBLE);
            mShotZoomLayout.setVisibility(GONE);
            setRockerViewState(GONE, 20);
            mShotLayout.setVisibility(GONE);
            mRightLayout.setVisibility(GONE);
            mBackBtn.setVisibility(GONE);
            mChooseMusicLayout.setVisibility(GONE);
            mChooseSpeedView.setVisibility(GONE);
        }
        mBeautyView.setVisibility(GONE);
        mFilterView.setVisibility(GONE);
        mCountDownView.setVisibility(GONE);
        mCutMusicView.setVisibility(GONE);
    }

    public void clickBeautyLayout() {
        if (mBeautyView.isShown()) {
            mBeautyView.setVisibility(GONE);
            mShotZoomLayout.setVisibility(VISIBLE);
            setRockerViewState(VISIBLE, 18);
            mShotLayout.setVisibility(VISIBLE);
            mRightLayout.setVisibility(VISIBLE);
            mBackBtn.setVisibility(VISIBLE);
            mChooseMusicLayout.setVisibility(VISIBLE);
            setSpeedView();
        } else {
            mBeautyView.setVisibility(VISIBLE);
            mShotZoomLayout.setVisibility(GONE);
            setRockerViewState(GONE, 19);
            mShotLayout.setVisibility(GONE);
            mRightLayout.setVisibility(GONE);
            mBackBtn.setVisibility(GONE);
            mChooseMusicLayout.setVisibility(GONE);
            mChooseSpeedView.setVisibility(GONE);
        }
        mFilterView.setVisibility(GONE);
        mPropsView.setVisibility(GONE);
        mCountDownView.setVisibility(GONE);
        mCutMusicView.setVisibility(GONE);
    }

    public void clickFilterLayout() {
        if (mFilterView.isShown()) {
            mFilterView.setVisibility(GONE);
            mShotZoomLayout.setVisibility(VISIBLE);
            setRockerViewState(VISIBLE, 17);
            mShotLayout.setVisibility(VISIBLE);
            mRightLayout.setVisibility(VISIBLE);
            mBackBtn.setVisibility(VISIBLE);
            mChooseMusicLayout.setVisibility(VISIBLE);
            setSpeedView();
        } else {
            if (!NetworkUtil.isNetworkConnected(AiCameraApplication.getContext()) && !EffectPlatform.getInstance().hasFiltersData()) {
                CameraToastUtil.show(AiCameraApplication.getContext().getString(R.string.no_network), AiCameraApplication.getContext());
                return;
            }
            if (!EffectPlatform.getInstance().hasFiltersData()) {
                Log.d(TAG, "clickFilterLayout: no filter data");
                EffectPlatform.getInstance().fetchEffectList(EffectPlatform.PANEL_FILTER, new EffectPlatform.EffectRequestCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "onSuccess: fetchEffectList");
                        AppExecutors.getInstance().mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                if (mFilterView != null)
                                    mFilterView.notifyFilterAdapter();
                            }
                        });
                    }

                    @Override
                    public void onFailed() {
                        Log.d(TAG, "onFailed: fetchEffectList");
                    }
                });
            }
            mFilterView.setVisibility(VISIBLE);
            mShotZoomLayout.setVisibility(GONE);
            setRockerViewState(GONE, 16);
            mShotLayout.setVisibility(GONE);
            mRightLayout.setVisibility(GONE);
            mBackBtn.setVisibility(GONE);
            mChooseMusicLayout.setVisibility(GONE);
            mChooseSpeedView.setVisibility(GONE);
        }
        mBeautyView.setVisibility(GONE);
        mPropsView.setVisibility(GONE);
        mCountDownView.setVisibility(GONE);
        mCutMusicView.setVisibility(GONE);
    }

    public void clickSpeedLayout() {
//        String c = "clickSpeedLayout: " + mChooseSpeedView.isShown();
//        FileUtil.writeFileToSDCard(FileUtil.path, c.getBytes(), "setRockerViewState.txt", true, true, false);
        if (mChooseSpeedView.isShown()) {
            mIsSpeedViewShow = false;
            mChooseSpeedView.setVisibility(GONE);
            mShotZoomLayout.setVisibility(VISIBLE);
            setRockerViewState(VISIBLE, 14);
            mShotRecordSpeedImage.setBackgroundResource(R.mipmap.icon_dy_speed_off);
        } else {
            mIsSpeedViewShow = true;
            mChooseSpeedView.setVisibility(VISIBLE);
            mShotZoomLayout.setVisibility(GONE);
            setRockerViewState(GONE, 15);
            mShotRecordSpeedImage.setBackgroundResource(R.mipmap.icon_dy_speed_on);
        }
        mFilterView.setVisibility(GONE);
        mBeautyView.setVisibility(GONE);
        mPropsView.setVisibility(GONE);
        mCountDownView.setVisibility(GONE);
        mCutMusicView.setVisibility(GONE);
    }

    public void clickCountDownLayout() {
        if (mCountDownView.isShown()) {
            pauseCountDown();
            mCountDownView.setVisibility(GONE);
            mShotZoomLayout.setVisibility(VISIBLE);
            setRockerViewState(VISIBLE, 12);
            mShotLayout.setVisibility(VISIBLE);
            setRightLayout(VISIBLE);
            mShotRotateCamera.setVisibility(VISIBLE);
            mBackBtn.setVisibility(VISIBLE);
            mChooseMusicLayout.setVisibility(VISIBLE);
            setSpeedView();
        } else {
            mCountDownView.setVisibility(VISIBLE);
            mShotZoomLayout.setVisibility(GONE);
            setRockerViewState(GONE, 13);
            mShotLayout.setVisibility(GONE);
            setRightLayout(GONE);
            mShotRotateCamera.setVisibility(GONE);
            mBackBtn.setVisibility(GONE);
            mChooseMusicLayout.setVisibility(GONE);
            if (checkActivity()) {
                float recordTime = mDyFPVActivity.get().mRecordTime;
                setCountDown(recordTime >= 6000 ? false : true, recordTime / 1000);
            }
            mChooseSpeedView.setVisibility(GONE);
        }
        mFilterView.setVisibility(GONE);
        mBeautyView.setVisibility(GONE);
        mPropsView.setVisibility(GONE);
        mCutMusicView.setVisibility(GONE);
    }

    private void clickCutMusicLayout() {
        if (mCutMusicView.isShown()) {
            mCutMusicView.pausePlay();
            mCutMusicView.setVisibility(GONE);
            mShotZoomLayout.setVisibility(VISIBLE);
            setRockerViewState(VISIBLE, 11);
            mShotLayout.setVisibility(VISIBLE);
            mRightLayout.setVisibility(VISIBLE);
            mBackBtn.setVisibility(VISIBLE);
            mChooseMusicLayout.setVisibility(VISIBLE);
            setSpeedView();
        } else {
            if (TextUtils.isEmpty(mCutMusicPath)) {
                CameraToastUtil.show90(getResources().getString(R.string.dy_no_music), AiCameraApplication.getContext());
                return;
            }
            mCutMusicView.startPlay();
            mCutMusicView.setVisibility(VISIBLE);
            mShotZoomLayout.setVisibility(GONE);
            setRockerViewState(GONE, 10);
            mShotLayout.setVisibility(GONE);
            mRightLayout.setVisibility(GONE);
            mBackBtn.setVisibility(GONE);
            mChooseMusicLayout.setVisibility(GONE);
            mChooseSpeedView.setVisibility(GONE);
        }
        mFilterView.setVisibility(GONE);
        mPropsView.setVisibility(GONE);
        mCountDownView.setVisibility(GONE);
        mBeautyView.setVisibility(GONE);
    }

    public void record(boolean isRecord) {
        if (isRecord) {
            mHandler.sendEmptyMessage(START_RECORD);
        } else {
            mHandler.sendEmptyMessage(STOP_RECORD);
        }
    }

    private void stateStart(String anim1, String anim2) {
        this.stateStart(anim1, anim2, -1);
    }

    private void stateStart(String anim1, String anim2, float extra) {
        if (mShotImage != null) {
            mShotImage.setVisibility(View.GONE);
        }
        if (mShotLottie != null) {
            if (mShotLottie.isAnimating())
                mShotLottie.cancelAnimation();
            mShotLottie.removeAllAnimatorListeners();
            mShotLottie.setVisibility(View.VISIBLE);
            mShotLottie.loop(true);
            mShotLottie.setAnimation(anim2);
            mShotLottie.playAnimation();
        }
    }

    private void stateStop(String anim) {
        if (mShotLottie.isAnimating())
            mShotLottie.cancelAnimation();
        mShotLottie.removeAllAnimatorListeners();
        mShotLottie.setVisibility(View.VISIBLE);
        mShotLottie.loop(false);
        mShotLottie.setSpeed(1f);
        mShotLottie.setAnimation(anim);

        mShotLottie.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mShotLottie.cancelAnimation();
                mShotLottie.removeAllAnimatorListeners();
                mShotLottie.setVisibility(View.GONE);
                if (mShotImage != null)
                    mShotImage.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mShotLottie.playAnimation();
    }

    private void digitalZoom() {
        if (mZoomLevel < 10 || mZoomLevel >= 50) {
            digital(10, "1.0X");
            mZoomLevel = 10;
        } else if (mZoomLevel < 20) {
            digital(20, "2.0X");
            mZoomLevel = 20;
        } else if (mZoomLevel < 30) {
            digital(30, "3.0X");
            mZoomLevel = 30;
        } else if (mZoomLevel < 40) {
            digital(40, "4.0X");
            mZoomLevel = 40;
        } else if (mZoomLevel < 50) {
            digital(50, "5.0X");
            mZoomLevel = 0;
        }
    }

    private void digital(int zoomLevel, String content) {
        mDyFPVPresenter.digitalZoom(zoomLevel);
        mShotZoomText.setText(content);
        mShotZoomText.setTag(zoomLevel);
    }

    public float getRecordSpeed() {
        return mChooseSpeedView.getRecordSpeed();
    }

    public void setDeleteState(int state) {
        Message message = Message.obtain();
        message.what = SET_DELETE_STATE;
        message.obj = state;
        mHandler.sendMessage(message);
    }

    public void setRightLayoutState(int state) {
//        FileUtil.writeFileToSDCard(FileUtil.path, ("setRightLayoutState: state: " + state).getBytes(), "countdown.txt", true, true, false);
        Message message = Message.obtain();
        message.what = SET_RIGHT_STATE;
        message.obj = state;
        mHandler.sendMessage(message);
    }

    public void performShotClick() {
        mShotImageLayout.performClick();
    }

    public void setShotBtnEnable(boolean enable) {
        if (mShotImageLayout != null)
            mShotImageLayout.setEnabled(enable);
    }

    public boolean isShotBtnEnabled() {
        if (mShotImageLayout != null)
            return mShotImageLayout.isEnabled();
        return false;
    }

    public void performConfirmClick() {
        mShotConfirmLayout.performClick();
    }

    public void deleteLastFlag() {
        if (mRecorderProgressView != null)
            mRecorderProgressView.deleteLast();
    }

    public void clipComplete() {
        if (mRecorderProgressView != null)
            mRecorderProgressView.clipComplete();
    }

    public void setDuration(float duration) {
        Log.d("test record", "setDuration: 11111");
        if (mRecorderProgressView != null)
            mRecorderProgressView.setDuration(duration);
    }

    public int getMaxDuration() {
        if (mRecorderProgressView != null)
            return mRecorderProgressView.getMaxDuration();
        return 0;
    }

    public float getTimeLineDuration() {
        if (mRecorderProgressView != null)
            return mRecorderProgressView.getTimelineDuration();
        return 0;
    }

    public void setRecordProgressStateCallback(RecordTimelineView.RecordProgressStateCallback callback) {
        DyFPVActivity.testLog("shot view set delete callback");
        if (mRecorderProgressView != null)
            mRecorderProgressView.setRecordProgressStateCallback(callback);
    }

    public void resetMaxDuration(int max) {
        if (mRecorderProgressView != null)
            mRecorderProgressView.resetMaxDuration(max);
    }

    public float getCurrentSpeed() {
        if (mChooseSpeedView != null)
            return mChooseSpeedView.getRecordSpeed();
        return 0;
    }

    public void setMusicInfo(MusicBean musicBean) {
        this.mMusicBean = musicBean;
        if (musicBean != null) {
            mChooseMusicName.setText(musicBean.getName());
            mChooseMusicImg.setBackgroundResource(R.mipmap.icon_choosed_music);

            int duration = musicBean.getDuration();
            if (duration <= 60) {
                mCutMusicPath = "";
                mCutMusic.setEnabled(false);
                mCutMusic.setAlpha(0.5f);
            } else {
                mCutMusicPath = musicBean.getPath();
                mCutMusic.setEnabled(true);
                mCutMusic.setAlpha(1f);

                String newPath = FileUtils.getMusicSelectPath(Md5Util.getMD5("cut_" + musicBean.getName()), AiCameraApplication.getContext()).getAbsolutePath();
                mCutMusicView.setPlayResources(musicBean.getPath(), newPath, 60, new MaskViewLayout.ClipListener() {
                    @Override
                    public void clipOk(String outputPath) {
                        mCutMusicPath = outputPath;
                        resetMaxDuration(60 * 1000);
                        mDyFPVPresenter.mCameraDisplay.setMusic(mCutMusicPath);
                        clickCutMusicLayout();
                        Log.d(TAG, "setMusicInfo: path: " + outputPath);
                    }

                    @Override
                    public void clipError() {

                    }
                });
            }
        } else {
            mCutMusicPath = "";
            mChooseMusicName.setText(AiCameraApplication.getContext().getString(R.string.dy_choose_music));
            mChooseMusicImg.setBackgroundResource(R.mipmap.icon_choose_music);
            mCutMusic.setEnabled(false);
            mCutMusic.setAlpha(0.5f);
        }
    }

    @Override
    public void thouchTime(float time) {
        Log.d(TAG, "thouchTime: " + time);
        if (!checkActivity()) {

        }
        if (mDyFPVActivity.get().isOverTime()) {
            CameraToastUtil.show90(getResources().getString(R.string.time_max), getContext());
            return;
        }
        startAnimation();
        mDyFPVActivity.get().setCountDownTime(time);
    }

    @Override
    public void countDownChanged(int count) {
        if (count == 3) {
            countDown = "animation/3_seconds.json";
            mCountDownImg.setBackgroundResource(R.mipmap.icon_dy_countdown3_n);
            CameraToastUtil.show90(getResources().getString(R.string.dy_count_down_3s), mContext.get());
            DyFPVActivity.testLog("change count down anim time 3s");
        } else if (count == 10) {
            countDown = "animation/10_seconds.json";
            mCountDownImg.setBackgroundResource(R.mipmap.icon_countdown10_n);
            CameraToastUtil.show90(getResources().getString(R.string.dy_count_down_10s), mContext.get());
            DyFPVActivity.testLog("change count down anim time 10s");
        }
    }

    public boolean allowCancel() {
        if (mDeleteDialog != null) {
            return mDeleteDialog.allowCancel();
        }
        return true;
    }

    public void setCountDown(boolean needPlay, float time) {
//        String s = com.meetvr.aicamera.dymode.utils.FileUtils.getCurrentTime() + ": stop record set count down: " + time;
//        FileUtil.writeFileToSDCard(FileUtil.path, s.getBytes(), "countdown.txt", true, true, false);
        if (mCountDownView != null) {
            mCountDownView.setCountDownTime(time, mRecorderProgressView.getMaxDuration() / 1000, mCutMusicPath);
            if (needPlay)
                mCountDownView.startPlay(time, 0);
        }
    }

    public void pauseCountDown() {
        if (mCountDownView != null)
            mCountDownView.pausePlay();
    }

    public void release() {
        if (mCountDownView != null)
            mCountDownView.destory();
        if (mCutMusicView != null)
            mCutMusicView.destory();
    }

    public void pauseCutmusic() {
        if (mCutMusicView != null && mCutMusicView.isShown()) {
            mCutMusicView.pausePlay();
        }
    }

    public void setCutmusicEnable(boolean enable) {
        mCutMusic.setEnabled(enable);
        if (enable) {
            mCutMusic.setAlpha(1f);
        } else {
            mCutMusic.setAlpha(0.5f);
        }
    }

    public void showConfirmLayout() {
        if (!mShotConfirmLayout.isShown()) {
            mShotConfirmLayout.setVisibility(VISIBLE);
        }
    }

    public void setZoom(float value, int zoom) {
        AppExecutors.getInstance().mainThread().execute(new Runnable() {
            @Override
            public void run() {
                mShotZoomText.setText(value + "X");
                mShotZoomText.setTag(zoom);
                mZoomLevel = zoom;
            }
        });
    }

    public void setZoomLayout(int state) {
        mShotZoomLayout.setVisibility(state);
    }

    public void showCompressLayout() {
        mDeleteDialog.showCompressLayout();
    }

    public void setDialogEnable(boolean enable) {
        mDeleteDialog.setBtnEnable(enable);
    }

    public boolean isSecondViewShow() {
        return mChooseSpeedView.isShown() || mFilterView.isShown() || mPropsView.isShown()
                || mCutMusicView.isShown() || mBeautyView.isShown() || mCountDownView.isShown()
                || isCountDownAnimation();
    }

    public void setBackEnable(boolean enable) {
        if (mBackBtn != null)
            mBackBtn.setEnabled(enable);
    }

    public boolean canGoBack() {
        if (mBackBtn != null)
            return mBackBtn.isEnabled();
        return true;
    }

    private void resetBackEnable() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setBackEnable(true);
            }
        }, 4000);
    }

    public void onTouch(float x, float y) {
        if (mDyFPVActivity.get() != null)
            mDyFPVActivity.get().setTouchEvent(x, y);
    }

    public void setSpeedViewState(int state) {
        if (mChooseSpeedView != null)
            mChooseSpeedView.setVisibility(state);
    }

    public void setSpeedView() {
        if (mIsSpeedViewShow) {
            mChooseSpeedView.setVisibility(VISIBLE);
            mShotZoomLayout.setVisibility(GONE);
            setRockerViewState(GONE, 15);
            mShotRecordSpeedImage.setBackgroundResource(R.mipmap.icon_dy_speed_on);
        }
    }

    public boolean isCountDownAnimation() {
        return mIsAnimation;
    }

    public void resetCountDownState() {
        mIsCancelAniamtion = true;
        mIsAnimation = false;
        mCountDownAnimation.setVisibility(View.GONE);
        mShotRotateCamera.setVisibility(VISIBLE);
        setRightLayout(VISIBLE);
        mShotLayout.setVisibility(VISIBLE);
        mShotZoomLayout.setVisibility(VISIBLE);
        mRecorderProgressView.setVisibility(VISIBLE);
        setRockerViewState(VISIBLE, 22);
        mChooseMusicLayout.setVisibility(VISIBLE);
        mBackBtn.setVisibility(VISIBLE);
    }
}