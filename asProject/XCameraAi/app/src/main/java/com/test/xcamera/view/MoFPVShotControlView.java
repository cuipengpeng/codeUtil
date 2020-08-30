package com.test.xcamera.view;

import android.animation.Animator;
import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.test.xcamera.MoPresenters.MoFPVPresenter;
import com.test.xcamera.R;
import com.test.xcamera.accrssory.AccessoryManager;
import com.test.xcamera.activity.MoFPVActivity;
import com.test.xcamera.bean.EventMessage;
import com.test.xcamera.bean.MoImage;
import com.test.xcamera.constants.LogcatConstants;
import com.test.xcamera.enumbean.ScreenOrientationType;
import com.test.xcamera.enumbean.ShootMode;
import com.test.xcamera.enumbean.WorkState;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.managers.MoMarkVideoManager;
import com.test.xcamera.managers.ShotModeManager;
import com.test.xcamera.managers.WorkStateManager;
import com.test.xcamera.mointerface.MoErrorCallback;
import com.test.xcamera.mointerface.MoFPVCallback;
import com.test.xcamera.statistic.StatisticFPVLayer;
import com.test.xcamera.utils.CustomGlideUtils;
import com.test.xcamera.utils.DlgUtils;
import com.test.xcamera.utils.RecordTimeUtil;
import com.test.xcamera.viewcontrol.MoFPVShotSettingControl;
import com.moxiang.common.logging.Logcat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * FPV页面控制拍摄的view  fpv页面的底部
 * Created by zll on 2019/10/23.
 */

public class MoFPVShotControlView extends RelativeLayout implements CurrentShotModeListener {
    private static final int SET_THUMBNAIL = 300001;
    private static final int START_RECORD = 300002;
    private static final int STOP_RECORD = 300003;
    private static final int STOP_RECORD_IMM = 300013;
    private static final int START_PHOTO = 300004;
    private static final int STOP_PHOTO = 300005;
    private static final int STOP_RECORD_TIME = 300009;


    @BindView(R.id.digital_zoom_text)
    public TextView mZoomText;
    @BindView(R.id.fragment_preview_control_zoom_layout)
    public RelativeLayout mZoomLayout;
    @BindView(R.id.fragment_preview_control_thumbnail)
    ImageView mThumbnailImg;
    @BindView(R.id.fragment_preview_control_thumbnail_layout)
    RelativeLayout mThumbnailLayout;
    @BindView(R.id.fragment_preview_control_mark_layout)
    RelativeLayout mMarkLayout;
    @BindView(R.id.fragment_preview_control_mark_image)
    ImageView mMarkImage;
    @BindView(R.id.lottie_mark)
    LottieAnimationView mLottieMark;
    @BindView(R.id.fragment_preview_control_rotate_camera_layout)
    RelativeLayout mRotateCameraLayout;
    @BindView(R.id.fragment_preview_control_shot_image)
    ImageView mShotImage;
    @BindView(R.id.shot_lottie)
    LottieAnimationView mShotLottie;
    @BindView(R.id.fragment_preview_control_shot_image_layout)
    RelativeLayout mShotImageLayout;
    @BindView(R.id.fragment_preview_control_track_image)
    public ImageView mTrackImage;
    @BindView(R.id.fragment_preview_control_track_layout)
    public RelativeLayout mTrackLayout;
    @BindView(R.id.fragment_preview_control_guiji_layout)
    RelativeLayout mGuiji;
    @BindView(R.id.fragment_preview_control_view_beauty_layout)
    RelativeLayout mBeautyLayout;
    @BindView(R.id.fragment_preview_control_shot_layout)
    LinearLayout mShotLayout;
    @BindView(R.id.fragment_preview_control_bottom_selector)
    MoSelectShotModeView mBottomSelector;
    @BindView(R.id.fragment_preview_control_rotate_camera_image)
    ImageView mRotateImage;
    @BindView(R.id.fragment_preview_control_view_beauty_image)
    ImageView mBeautyImage;
    @BindView(R.id.fragment_preview_control_guiji_image)
    ImageView mGuiJiImage;
    @BindView(R.id.fragment_preview_control_dy_btn)
    ImageView mDyBtn;

    @BindView(R.id.fragment_preview_control_record_time_text)
    TextView mRecordTimeText;
    @BindView(R.id.fragment_preview_control_record_time_text_top)
    VerticalTextView mRecordTimeTextTop;

    @BindView(R.id.preview_record_time)
    View mLapseRecordTime;
    @BindView(R.id.real_time)
    TextView mLapseRealTime;
    @BindView(R.id.lapse_time)
    TextView mLapseTime;

    @BindView(R.id.preview_record_time_top)
    View mLapseRecordTimeTop;
    @BindView(R.id.real_time_top)
    TextView mLapseRealTimeTop;
    @BindView(R.id.lapse_time_top)
    TextView mLapseTimeTop;

    private MoFPVShotSettingView mShotSettingLayout;

    private Context mContext;
    private Unbinder mUnbinder;
    private MoFPVCallback mFPVCallback;
    private MoFPVPresenter mFPVPresenter;
    private RotateAnimation mRotateAnimation;
    private RecordTimeUtil mRecordTimeUtil;
    //    private ObjectAnimator mPhotoAnimator;
    private boolean mEnablePTZ = true;
    public int mZoomLevel = 10;
    private float mOldDist = 1f;
    private int mZoom = 10;
    private float mMaxZoom = 50;
    private int mDelayTime = 0;
    private String mRecordUri;
    public float mLapseValue;
    public int recordViewStatus = -1;
    public long sdStatus = Integer.MAX_VALUE;
    private long lastShotTime = 0;
    private long mLastToastTime;
    public int mViewRotate = 0;
    private ScreenOrientationType orient = ScreenOrientationType.LANDSCAPE;
    /**
     * 慢动作 倍率
     */
    public int speed = 1;

    private static final int FAILED = -1;
    private static final int CANCEL = -1;
    private static final int RECORDING_NORMAL = 0;
    private static final int RECORD_NORMAL_STOP_IMM = 1;
    private static final int RECORD_NORMAL_STOP = 2;
    private static final int RECORDING_LAPSE = 3;
    private static final int RECORD_LAPSE_STOP_IMM = 4;
    private static final int RECORD_LAPSE_STOP = 5;
    private static final int RECORDING_SLOW = 6;
    private static final int RECORD_SLOW_STOP_IMM = 7;
    private static final int RECORD_SLOW_STOP = 8;
    private static final int PHOTO_NORMAL = 9;
    private static final int LONG_EXPLORE = 10;
    private static final int PHOTO_NORMAL_STOP = 11;
    private static final int IDE = 100;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case CANCEL:
                    if (ShotModeManager.getInstance().getmShootMode() == ShootMode.PHOTO
                            || ShotModeManager.getInstance().getmShootMode() == ShootMode.LONG_EXPLORE)
                        setShotState(PHOTO_NORMAL_STOP);
                    else
                        setShotState(CANCEL);
                    //确保模式切换能点击 即使会有其他问题
                    WorkStateManager.getInstance().setmWorkState(WorkState.STANDBY);
                    break;
                case RecordTimeUtil.RECORD_TIME:
                    long time = (long) msg.obj;
                    String t = RecordTimeUtil.formatTime(time);
                    if (isLapse()) {
                        if (mLapseRealTime != null)
                            mLapseRealTime.setText(t);
                        if (mLapseRealTimeTop != null)
                            mLapseRealTimeTop.setText(t);

                        long lapseTime = (long) (time * 1000 / (mLapseValue * 1000f) / 30f);
                        String lapseTimeStr = RecordTimeUtil.formatTime(lapseTime);
                        if (mLapseTime != null)
                            mLapseTime.setText(lapseTimeStr);
                        if (mLapseTimeTop != null)
                            mLapseTimeTop.setText(lapseTimeStr);
                    } else {
                        if (mRecordTimeText != null)
                            mRecordTimeText.setText(t);
                        if (mRecordTimeTextTop != null)
                            mRecordTimeTextTop.setText(t);
                    }
                    break;
                case SET_THUMBNAIL:
                    MoImage image = msg.obj == null ? null : (MoImage) msg.obj;
                    String path = image == null ? null : image.getmUri();
                    if (mContext != null || mThumbnailImg != null) {
                        float angle = 0;
                        if (image != null) {
                            int rotate = image.getRotate();
                            if (rotate == 0) {
                                angle = 0;
                            } else if (rotate == 1) {
                                angle = -90;
                            } else if (rotate == 2) {
                                angle = 180;
                            } else if (rotate == 3) {
                                angle = 90;
                            }
                        }

                        CustomGlideUtils.loadAlbumPhotoThumbnailRotate(mContext, path, mThumbnailImg, 0L, angle, R.mipmap.icon_thumbnail2);
                    }
                    stopAnim();
                    break;
                case START_PHOTO:
                    WorkStateManager.getInstance().setmWorkState(WorkState.PHOTOING);
                    mZoomLayout.setVisibility(View.INVISIBLE);
                    mThumbnailLayout.setVisibility(View.INVISIBLE);
                    mGuiji.setVisibility(View.INVISIBLE);
                    mRotateCameraLayout.setVisibility(View.INVISIBLE);
                    mBottomSelector.setAlpha(0f);
                    mMarkLayout.setVisibility(View.GONE);
                    mTrackLayout.setVisibility(View.INVISIBLE);

                    CountDownTimer timer = new CountDownTimer((long) (MoFPVShotSettingControl.getInstance().mLongExploreTime * 1000 + 500), 1000) {
                        public void onTick(long millisUntilFinished) {
                            int second = (int) (millisUntilFinished / 1000);
                            if (mRecordTimeTextTop != null)
                                mRecordTimeTextTop.setText(String.format("%02d:%02d", second / 60, second % 60));
                            if (mRecordTimeText != null)
                                mRecordTimeText.setText(String.format("%02d:%02d", second / 60, second % 60));
                        }

                        public void onFinish() {
                            mRecordTimeTextTop.setVisibility(View.GONE);
                            mRecordTimeText.setVisibility(View.GONE);
                        }
                    };

                    if (ShotModeManager.getInstance().getmShootMode() == ShootMode.LONG_EXPLORE)
                        //长曝光模式 3秒后才开始计时
                        postDelayed(() -> {
                            showTimeView();
                            timer.start();
                        }, 3000);
                    else {
                        showTimeView();
                        timer.start();
                    }

                    break;
                case STOP_PHOTO:
                    WorkStateManager.getInstance().setmWorkState(WorkState.STANDBY);
                    mZoomLayout.setVisibility(View.VISIBLE);
                    mThumbnailLayout.setVisibility(View.VISIBLE);
                    mGuiji.setVisibility(View.VISIBLE);
                    mRotateCameraLayout.setVisibility(View.VISIBLE);
                    mBottomSelector.setVisibility(View.VISIBLE);
                    mBottomSelector.setAlpha(1f);
                    mMarkLayout.setVisibility(View.GONE);
                    mTrackLayout.setVisibility(View.VISIBLE);
                    mRecordTimeText.setVisibility(View.GONE);
                    mRecordTimeTextTop.setVisibility(View.GONE);
                    break;
                case START_RECORD:

//                    if (ShotModeManager.getInstance().getmShootMode() == ShootMode.SLOW_MOTION) {
//                        mRecordTimeUtil.statTimerMs(mHandler, speed, mRecordTimeText, mRecordTimeTextTop);
//                    } else

                    break;
                case STOP_RECORD_IMM:
//                    if (ShotModeManager.getInstance().getmShootMode() == ShootMode.SLOW_MOTION) {
//                        mRecordTimeUtil.stopTimer();
//                        mRecordTimeText.setVisibility(View.GONE);
//                        mRecordTimeTextTop.setVisibility(View.GONE);
//                    }
                    break;
                case STOP_RECORD_TIME:
                    mRecordTimeUtil.stopTimer();
                    mHandler.removeMessages(RecordTimeUtil.RECORD_TIME);
                    break;
                case STOP_RECORD:

                    break;
            }
        }
    };

    public MoFPVShotControlView(Context context) {
        super(context);

        initView(context);
    }

    public MoFPVShotControlView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView(context);
    }

    public void setMoFPVCallback(MoFPVCallback callback) {
        mFPVCallback = callback;
    }

    public void setMoFPVPresenter(MoFPVPresenter presenter) {
        mFPVPresenter = presenter;
    }

    private void initView(Context context) {
        mContext = context;

        LayoutInflater.from(context).inflate(R.layout.fpv_shot_control_layout, this, true);
        mUnbinder = ButterKnife.bind(this);

        mTrackLayout.setTag(true);
        mBottomSelector.setBottomSelectorCurrentIndexListener(this);

        mRecordTimeUtil = new RecordTimeUtil();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void resetZoom(EventMessage msg) {
        if (msg.code == EventMessage.RESET_ZOOM) {
            if (mZoomText != null) {
                mZoomLayout.setAlpha(1f);
                mZoomText.setText("1.0X");
                mZoomLevel = 10;
                mZoomText.setTag(10);
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.fragment_preview_control_zoom_layout, R.id.fragment_preview_control_thumbnail_layout,
            R.id.fragment_preview_control_mark_layout, R.id.fragment_preview_control_rotate_camera_layout,
            R.id.fragment_preview_control_shot_image_layout, R.id.fragment_preview_control_track_layout,
            R.id.fragment_preview_control_guiji_layout, R.id.fragment_preview_control_shot_layout,
            R.id.fragment_preview_control_view_beauty_layout, R.id.fragment_preview_control_dy_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fragment_preview_control_zoom_layout:
                if (WorkStateManager.getInstance().isSetting())
                    return;

                if (mZoomLayout.getTag() != null && !(boolean) mZoomLayout.getTag()) {
                    if (System.currentTimeMillis() - mLastToastTime > 3000) {
                        mLastToastTime = System.currentTimeMillis();
                        DlgUtils.toast(getContext(), getResources().getString(R.string.zoom_err_mode), mViewRotate);
                    }
                    break;
                }

                if (mZoomLevel < 10 || mZoomLevel >= 50) {
                    mFPVPresenter.digitalZoom(10);
                    mZoomText.setText("1.0X");
                    mZoomText.setTag(10);
                    mZoomLevel = 10;
                } else if (mZoomLevel < 20) {
                    mFPVPresenter.digitalZoom(20);
                    mZoomText.setText("2.0X");
                    mZoomLevel = 20;
                    mZoomText.setTag(20);
                } else if (mZoomLevel < 30) {
                    mFPVPresenter.digitalZoom(30);
                    mZoomText.setText("3.0X");
                    mZoomLevel = 30;
                    mZoomText.setTag(30);
                } else if (mZoomLevel < 40) {
                    mFPVPresenter.digitalZoom(40);
                    mZoomText.setText("4.0X");
                    mZoomLevel = 40;
                    mZoomText.setTag(40);
                } else if (mZoomLevel < 50) {
                    mFPVPresenter.digitalZoom(50);
                    mZoomText.setText("5.0X");
                    mZoomLevel = 0;
                    mZoomText.setTag(50);
                }
                break;
            case R.id.fragment_preview_control_thumbnail_layout:
                if (WorkStateManager.getInstance().getmWorkState() != WorkState.STANDBY)
                    return;

                //如果正在切模式 退出会造成指令混乱
                if (WorkStateManager.getInstance().enableSwitch()) {
//                    DlgUtils.toast(getContext(), getResources().getString(R.string.change_moding));
                    return;
                }

                StatisticFPVLayer.getInstance().setOnEvent(StatisticFPVLayer.FloatLayer_Capture_ViewAlbum);
                mFPVCallback.onFinish(MoFPVActivity.FINISH_ALBUM);
                break;
            case R.id.fragment_preview_control_mark_layout:
                if (WorkStateManager.getInstance().getmWorkState() != WorkState.RECORDING)
                    return;

                mFPVPresenter.markVideo(mRecordUri, MoMarkVideoManager.getInstance().getCurrentTime());

                mLottieMark.setVisibility(View.VISIBLE);
                mLottieMark.setSpeed(2f);
                mMarkImage.setVisibility(View.GONE);

                mLottieMark.playAnimation();
                mMarkLayout.setClickable(false);
                mLottieMark.addAnimatorListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLottieMark.setVisibility(View.GONE);
                        mMarkImage.setVisibility(View.VISIBLE);

                        mMarkLayout.setClickable(true);
                        mLottieMark.removeAllAnimatorListeners();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        mMarkLayout.setClickable(true);
                        mLottieMark.removeAllAnimatorListeners();
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                break;
            case R.id.fragment_preview_control_rotate_camera_layout:
                if (!this.mEnablePTZ)
                    break;

                StatisticFPVLayer.getInstance().setOnEvent(StatisticFPVLayer.FloatLayer_Capture_LensFlip);

                mFPVPresenter.rotateCamera(0);
                break;
            case R.id.fragment_preview_control_shot_image_layout:
                shot();
                break;
            case R.id.fragment_preview_control_track_layout:
                if (!(boolean) mTrackLayout.getTag()) {
                    DlgUtils.toast(getContext(), getResources().getString(R.string.trace_unsupport_fpv), mViewRotate);
                    return;
                }
                if (mTrackImage.isSelected()) {
                    mFPVCallback.stopAutoTrack();
                } else {
                    mFPVCallback.startAutoTrack();
                }

                StatisticFPVLayer.getInstance().setOnEvent(StatisticFPVLayer.FloatLayer_Capture_Track, mTrackImage.isSelected() ? "stop track" : "start track");

                break;
            case R.id.fragment_preview_control_guiji_layout:
                if (!this.mEnablePTZ)
                    break;

                StatisticFPVLayer.getInstance().setOnEvent(StatisticFPVLayer.FloatLayer_Capture_MidPosition);

                ConnectionManager.getInstance().setPtzBack(null);
                break;
            case R.id.fragment_preview_control_shot_layout:
                break;
            case R.id.fragment_preview_control_view_beauty_layout:
                mFPVCallback.chooseBeautyType();
                break;
        }
    }

    /**
     * 跟踪时 禁用按钮
     *
     * @param enable false 禁用回中和翻转按钮
     */
    public void enablePTZ(boolean enable) {
        this.mEnablePTZ = enable;

        if (enable) {
            mRotateImage.setImageResource(R.mipmap.icon_fpv_cloud_reversal);
            mGuiJiImage.setImageResource(R.mipmap.icon_guiji);
        } else {
            mRotateImage.setImageResource(R.mipmap.icon_fpv_cloud_reversal_disable);
            mGuiJiImage.setImageResource(R.mipmap.icon_guiji_disable);
        }
    }

    private void reset() {
        mZoomText.setText("1.0X");
        mZoomLevel = 10;
        mZoomText.setTag(10);
    }

    /**
     * 获取当前拍摄模式
     *
     * @param index
     */
    @Override
    public void currentShotMode(int index) {
        reset();
        switch (index) {
            case MoSelectShotModeView.LAPSE:
                ShotModeManager.getInstance().setmShootMode(ShootMode.LAPSE_VIDEO);
                if (mShotSettingLayout != null)
                    mShotSettingLayout.setLapseMode();
                mFPVCallback.selectLapseMode(true);
                if (mBeautyLayout != null)
                    mBeautyLayout.setVisibility(GONE);
                setShotIcon(R.mipmap.icon_lapse_shot);
                break;
            case MoSelectShotModeView.SLOW_MOTION:
                ShotModeManager.getInstance().setmShootMode(ShootMode.SLOW_MOTION);
                if (mShotSettingLayout != null)
                    mShotSettingLayout.setSlowMotionMode();
                mFPVCallback.selectSlowMotionMode(true);
                if (mBeautyLayout != null)
                    mBeautyLayout.setVisibility(GONE);
                setShotIcon(R.mipmap.icon_slowmotion_shot);
                break;
            case MoSelectShotModeView.VIDEO:
                ShotModeManager.getInstance().setmShootMode(ShootMode.VIDEO);
                if (mShotSettingLayout != null)
                    mShotSettingLayout.setVideoMode();
                mFPVCallback.selectVideoMode(true);
                if (mBeautyLayout != null)
                    mBeautyLayout.setVisibility(GONE);
                setShotIcon(R.mipmap.icon_record);
                break;
            case MoSelectShotModeView.PHOTO:
                ShotModeManager.getInstance().setmShootMode(ShootMode.PHOTO);
                if (mShotSettingLayout != null)
                    mShotSettingLayout.setPhotoMode();
                mFPVCallback.selectPhotoMode(true);
                if (mBeautyLayout != null)
                    mBeautyLayout.setVisibility(GONE);
                setShotIcon(R.mipmap.icon_shot);
                break;
            case MoSelectShotModeView.LONG_EXPLORE:
                ShotModeManager.getInstance().setmShootMode(ShootMode.LONG_EXPLORE);
                if (mShotSettingLayout != null)
                    mShotSettingLayout.setLongExploreMode();
                mFPVCallback.selectLongExploreMode(true);
                if (mBeautyLayout != null)
                    mBeautyLayout.setVisibility(GONE);
                setShotIcon(R.mipmap.icon_shot);
                break;
            case MoSelectShotModeView.BEAUTY:
                ShotModeManager.getInstance().setmShootMode(ShootMode.VIDEO_BEAUTY);
                if (mShotSettingLayout != null)
                    mShotSettingLayout.setBeautyMode();
                mFPVCallback.selectBeauty(true);
                if (mBeautyLayout != null)
                    mBeautyLayout.setVisibility(VISIBLE);
                setShotIcon(R.mipmap.icon_record);
                break;
        }
    }

    /**
     * 执行拍照、录像
     */
    private void shot() {
        //防止点击过于频繁
        if (System.currentTimeMillis() - lastShotTime < 800)
            return;
        lastShotTime = System.currentTimeMillis();

        //如果正在切换相机参数
        if (WorkStateManager.getInstance().isSetting())
            return;

        //拍摄中
        if (WorkStateManager.getInstance().getmWorkState() == WorkState.PHOTOING
                || WorkStateManager.getInstance().getmWorkState() == WorkState.STOPPING)
            return;

        //未插相机
        if (!AccessoryManager.getInstance().mIsRunning)
            return;

        if (WorkStateManager.getInstance().enableSwitch()) {
            DlgUtils.toast(getContext(), getResources().getString(R.string.err_change_moding), mViewRotate);
            return;
        }

        //sd卡未插入
        if (WorkStateManager.getInstance().getmWorkState() == WorkState.STANDBY) {
            switch (recordViewStatus) {
                case MoErrorCallback.SD_IN:
                    break;
                case MoErrorCallback.SD_OUT:
                    DlgUtils.toast(getContext(), getResources().getString(R.string.sdcard_out), mViewRotate);
                    return;
                case MoErrorCallback.SD_IN_FAIL:
                    DlgUtils.toast(getContext(), getResources().getString(R.string.sdcard_abnormal), mViewRotate);
                    return;
                case MoErrorCallback.SD_FULL:
                    DlgUtils.toast(getContext(), getResources().getString(R.string.sdcard_status_full), mViewRotate);
                    return;
                case MoErrorCallback.SD_LOW:
                    DlgUtils.toast(getContext(), getResources().getString(R.string.sdcard_low), mViewRotate);
                    break;
                default:
                    DlgUtils.toast(getContext(), getResources().getString(R.string.sdcard_abnormal), mViewRotate);
                    break;
            }
        }

        if (WorkStateManager.getInstance().getmWorkState() != WorkState.RECORDING)
            EventBus.getDefault().post(new EventMessage(EventMessage.SCREEN_CLICKED));

        if (!ShotModeManager.getInstance().isVideo()) {
            if (ShotModeManager.getInstance().getmShootMode() == ShootMode.PHOTO) {
                mFPVCallback.takeDelayPhoto();
            } else {
                mFPVPresenter.takePhoto();
            }

            StatisticFPVLayer.getInstance().setOnEvent(StatisticFPVLayer.FloatLayer_Capture_Photo);

            AccessoryManager.mConnectCount = 0;
            AccessoryManager.mMoFPVActCreateCount = 0;

            mFPVCallback.takePhotoStart();
            if (ShotModeManager.getInstance().getmShootMode() == ShootMode.LONG_EXPLORE)
                setShotState(LONG_EXPLORE);
            else
                setShotState(PHOTO_NORMAL);
        } else {
            if (WorkStateManager.getInstance().getmWorkState() == WorkState.RECORDING) {
                StatisticFPVLayer.getInstance().setOnEvent(StatisticFPVLayer.FloatLayer_Capture_Video_Stop);
                WorkStateManager.getInstance().setmWorkState(WorkState.STOPPING);
                mFPVPresenter.stopTakeVideo();

                switch (ShotModeManager.getInstance().getmShootMode()) {
                    case SLOW_MOTION:
                        setShotState(RECORD_SLOW_STOP_IMM);
                        break;
                    case LAPSE_VIDEO:
                        setShotState(RECORD_LAPSE_STOP_IMM);
                        break;
                    default:
                        setShotState(RECORD_NORMAL_STOP_IMM);
                        break;
                }
            } else if (WorkStateManager.getInstance().getmWorkState() == WorkState.STANDBY) {
                mFPVPresenter.startTakeVideo();
                StatisticFPVLayer.getInstance().setOnEvent(StatisticFPVLayer.FloatLayer_Capture_Video_Start);
                switch (ShotModeManager.getInstance().getmShootMode()) {
                    case SLOW_MOTION:
                        setShotState(RECORDING_SLOW);
                        break;
                    case LAPSE_VIDEO:
                        setShotState(RECORDING_LAPSE);
                        break;
                    default:
                        setShotState(RECORDING_NORMAL);
                        break;
                }
            }
        }
    }

    /**
     * 开始录视频
     */
    public void takeVideoStart(String uri) {
        WorkStateManager.getInstance().setmWorkState(WorkState.RECORDING);
        mRecordTimeUtil.statTimer(mHandler);
        setStartRecordingState();
        mRecordUri = uri;
    }

    /**
     * 结束录视频
     */
    public void takeVideoStop(MoImage image) {
        mRecordTimeUtil.stopTimer();
        mHandler.removeMessages(RecordTimeUtil.RECORD_TIME);
        setThumbnail(image);
        setStopRecordingState();
        switch (ShotModeManager.getInstance().getmShootMode()) {
            case SLOW_MOTION:
                setShotState(RECORD_SLOW_STOP);
                break;
            case LAPSE_VIDEO:
                setShotState(RECORD_LAPSE_STOP);
                break;
            default:
                setShotState(RECORD_NORMAL_STOP);
                break;
        }
    }

    public void videoStopTimeImm() {
        sendHandlerMsg(STOP_RECORD_TIME, null);
    }

    public void takeVideoStopImm() {
        sendHandlerMsg(STOP_RECORD_IMM, null);
    }

    public void cancelPhoto(int state) {
        sendHandlerMsg(CANCEL, state);
    }

    public void takePhotoStart() {
        sendHandlerMsg(START_PHOTO, null);
    }

    public void takePhotoStop() {
        sendHandlerMsg(STOP_PHOTO, null);
    }

    /**
     * 设置拍摄按钮icon
     *
     * @param resID
     */
    public void setShotIcon(int resID) {
        if (mShotImage != null) {
            mShotImage.clearAnimation();
            mShotImage.setVisibility(View.VISIBLE);
            mShotImage.setImageResource(resID);
        }

        if (mShotLottie != null)
            mShotLottie.setVisibility(View.GONE);
    }

    /**
     * 设置缩略图
     *
     * @param image
     */
    public void setThumbnail(MoImage image) {
        sendHandlerMsg(SET_THUMBNAIL, image);
    }

    /**
     * 结束动画
     */
    private void stopAnim() {
        if (mShotImage != null) {
            mShotImage.clearAnimation();
        }
    }

    public void changeMode(int type) {
        if (mBottomSelector != null)
            mBottomSelector.changeMode(type);
    }

    public void setStartRecordingState() {
        mBottomSelector.setAlpha(0f);
        Logcat.v().tag(LogcatConstants.FPV_PREVIEW).msg("setStartRecordingState==>" + (ShotModeManager.getInstance().getmShootMode() == ShootMode.LAPSE_VIDEO)).out();
        mMarkLayout.setVisibility(ShotModeManager.getInstance().getmShootMode() == ShootMode.LAPSE_VIDEO ? View.INVISIBLE : View.VISIBLE);
        mThumbnailLayout.setVisibility(View.GONE);

        showTimeView();
    }

    private void showTimeView() {
        if (this.orient == ScreenOrientationType.LANDSCAPE) {
            if (isLapse())
                mLapseRecordTime.setVisibility(View.VISIBLE);
            else
                mRecordTimeText.setVisibility(View.VISIBLE);
        } else {
            if (isLapse())
                mLapseRecordTimeTop.setVisibility(View.VISIBLE);
            else
                mRecordTimeTextTop.setVisibility(View.VISIBLE);
        }
    }

    public void setStopRecordingState() {
        mThumbnailLayout.setVisibility(View.VISIBLE);
        mBottomSelector.setAlpha(1f);
        mMarkLayout.setVisibility(View.GONE);
        mRecordTimeText.setVisibility(View.GONE);
        mRecordTimeTextTop.setVisibility(View.GONE);
        mLapseRecordTime.setVisibility(View.GONE);
        mLapseRecordTimeTop.setVisibility(View.GONE);
        mRecordTimeUtil.stopTimer();
        mHandler.removeMessages(RecordTimeUtil.RECORD_TIME);
    }

    private void sendHandlerMsg(int what, Object o) {
        Message message = Message.obtain();
        message.what = what;
        message.obj = o;
        mHandler.sendMessage(message);
    }

    public void setDelayTime(int time) {
        mDelayTime = time;
    }

    public void changeOrientation(ScreenOrientationType orientationType) {
        this.orient = orientationType;
        if (orientationType == ScreenOrientationType.LANDSCAPE) {
            if (isShowTimeView()) {
                if (isLapse()) {
                    if (mLapseRecordTime.getVisibility() == View.VISIBLE || mLapseRecordTimeTop.getVisibility() == View.VISIBLE) {
                        mLapseRecordTime.setVisibility(View.VISIBLE);
                        mLapseRecordTimeTop.setVisibility(View.GONE);
                        mRecordTimeText.setVisibility(View.GONE);
                        mRecordTimeTextTop.setVisibility(View.GONE);
                    }
                } else {
                    if (mRecordTimeText.getVisibility() == View.VISIBLE || mRecordTimeTextTop.getVisibility() == View.VISIBLE) {
                        mLapseRecordTime.setVisibility(View.GONE);
                        mLapseRecordTimeTop.setVisibility(View.GONE);
                        mRecordTimeText.setVisibility(View.VISIBLE);
                        mRecordTimeTextTop.setVisibility(View.GONE);
                    }
                }
            }
            setViewOrientation(0);
        } else if (orientationType == ScreenOrientationType.PORTRAIT) {
            if (isShowTimeView()) {
                if (isLapse()) {
                    if (mLapseRecordTime.getVisibility() == View.VISIBLE || mLapseRecordTimeTop.getVisibility() == View.VISIBLE) {
                        mLapseRecordTime.setVisibility(View.GONE);
                        mLapseRecordTimeTop.setVisibility(View.VISIBLE);
                        mRecordTimeText.setVisibility(View.GONE);
                        mRecordTimeTextTop.setVisibility(View.GONE);
                    }
                } else {
                    if (mRecordTimeText.getVisibility() == View.VISIBLE || mRecordTimeTextTop.getVisibility() == View.VISIBLE) {
                        mRecordTimeText.setVisibility(View.GONE);
                        mRecordTimeTextTop.setVisibility(View.VISIBLE);
                        mLapseRecordTime.setVisibility(View.GONE);
                        mLapseRecordTimeTop.setVisibility(View.GONE);
                    }
                }

            }
            setViewOrientation(-90);
        }
    }

    /**
     * 判断是否需要展示计时
     */
    private boolean isShowTimeView() {
        return ShotModeManager.getInstance().getmShootMode() == ShootMode.VIDEO
                || ShotModeManager.getInstance().getmShootMode() == ShootMode.SLOW_MOTION
                || ShotModeManager.getInstance().getmShootMode() == ShootMode.LONG_EXPLORE
                || ShotModeManager.getInstance().getmShootMode() == ShootMode.VIDEO_BEAUTY
                || ShotModeManager.getInstance().getmShootMode() == ShootMode.LAPSE_VIDEO;
    }

    private void setViewOrientation(int orientation) {
        mThumbnailImg.animate().rotation(orientation);
        mRotateImage.animate().rotation(orientation);
        mShotImage.animate().rotation(orientation);
        mTrackImage.animate().rotation(orientation);
        mGuiJiImage.animate().rotation(orientation);
        mZoomLayout.animate().rotation(orientation);
        mBeautyImage.animate().rotation(orientation);
        mMarkImage.animate().rotation(orientation);
    }

    /**
     * 拍照及摄影时 按钮的各种动画状态
     */
    private void setShotState(int state) {
        switch (state) {
            case RECORDING_NORMAL:
                stateStart("animation/animation_video_click.json", "animation/animation_video_time_cycle.json");
                break;
            case RECORD_NORMAL_STOP_IMM:
                stateStore("animation/animation_video_stop_click.json", "animation/animation_video_save_loading.json");
                break;
            case RECORD_NORMAL_STOP:
                stateStop("animation/animation_video_restore.json");
                break;

            case RECORDING_LAPSE:
                float speed = mLapseValue > 0 ? 0.5f / mLapseValue : 1f;
                stateStart("animation/animation_lapse_click.json", "animation/animation_lapse_time_cycle.json", speed);
                break;
            case RECORD_LAPSE_STOP_IMM:
                stateStore("animation/animation_lapse_stop_click.json", "animation/animation_lapse_loading.json");
                break;
            case RECORD_LAPSE_STOP:
                stateStop("animation/animation_lapse_restore.json");
                break;

            case RECORDING_SLOW:
                if (mShotLottie != null) {
                    mShotLottie.cancelAnimation();
                    mShotLottie.setVisibility(View.VISIBLE);
                    mShotLottie.loop(false);
                    mShotLottie.setAnimation("animation/animation_slowmotion_click.json");

                    mShotLottie.removeAllAnimatorListeners();
                    mShotLottie.addAnimatorListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            if (mShotImage != null) {
                                mShotImage.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {

                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                }
                mShotLottie.playAnimation();
                break;
            case RECORD_SLOW_STOP_IMM:
                stateStore("animation/animation_slowmotion_stop_click.json", "animation/animation_slowmotion_save_loading.json");
                break;
            case RECORD_SLOW_STOP:
                stateStop("animation/animation_slowmotion_restore.json");
                break;

            case LONG_EXPLORE:
            case PHOTO_NORMAL:
                mShotImage.setImageResource(R.mipmap.icon_shot_loading);
                mShotImage.setVisibility(View.VISIBLE);
                mShotLottie.setVisibility(View.GONE);
                mShotImage.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.rotate_anim));
                break;
            case PHOTO_NORMAL_STOP:
                setShotIcon(R.mipmap.icon_shot);
                break;
            case CANCEL:
                if (mShotLottie != null) {
                    mShotLottie.setVisibility(View.GONE);
                    mShotLottie.removeAllAnimatorListeners();
                    mShotLottie.cancelAnimation();
                }
                if (mShotImage != null) {
                    mShotImage.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void stateStart(String anim1, String anim2) {
        this.stateStart(anim1, anim2, -1);
    }

    private void stateStartSmiple(String anim1, String anim2, float extra) {
        if (mShotLottie != null) {
            if (mShotLottie.isAnimating())
                mShotLottie.cancelAnimation();
            mShotLottie.removeAllAnimatorListeners();
            mShotLottie.setVisibility(View.VISIBLE);
            mShotLottie.loop(true);
            mShotLottie.setSpeed(extra > 0 ? extra : 1f);
            mShotLottie.setAnimation(anim2);

            mShotLottie.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (mShotImage != null) {
                        mShotImage.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {

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
    }

    private void stateStart(String anim1, String anim2, float extra) {
        this.stateStartSmiple(anim1, anim2, extra);
    }

    private void stateStore(String anim1, String anim2) {
        if (mShotLottie != null) {
            if (mShotLottie.isAnimating())
                mShotLottie.cancelAnimation();
            mShotLottie.removeAllAnimatorListeners();

            mShotLottie.setVisibility(View.VISIBLE);
            mShotLottie.loop(false);
            mShotLottie.setSpeed(1f);
            mShotLottie.setAnimation(anim1);

            mShotLottie.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (mShotImage != null)
                        mShotImage.setVisibility(View.GONE);

                    mShotLottie.removeAllAnimatorListeners();
                    mShotLottie.loop(true);
                    mShotLottie.setSpeed(1f);
                    mShotLottie.setAnimation(anim2);
                    mShotLottie.playAnimation();
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
    }

    private void stateStop(String anim) {
        if (mShotImage != null)
            mShotImage.clearAnimation();
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
                if (mShotImage != null) {
                    mShotImage.setVisibility(View.VISIBLE);
                }
                WorkStateManager.getInstance().setmWorkState(WorkState.STANDBY);
                mShotLottie.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                WorkStateManager.getInstance().setmWorkState(WorkState.STANDBY);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mShotLottie.playAnimation();
    }

    public void setShotSettingLayout(MoFPVShotSettingView shotSettingLayout) {
        this.mShotSettingLayout = shotSettingLayout;
    }

    private boolean isLapse() {
        //TODO XMYTXJ-663已完成 目前先隐藏
        return false;
//        return ShotModeManager.getInstance().getmShootMode() == ShootMode.LAPSE_VIDEO;
    }

    public void release() {
        if (mHandler != null)
            mHandler.removeCallbacksAndMessages(null);
    }
}
