package com.test.xcamera.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.test.xcamera.bean.EventMessage;
import com.test.xcamera.enumbean.WorkState;
import com.test.xcamera.managers.WorkStateManager;
import com.test.xcamera.mointerface.MoErrorCallback;
import com.test.xcamera.utils.FileUtils;
import com.test.xcamera.utils.ViewUitls;
import com.test.xcamera.viewcontrol.MoFPVShotSettingControl;
import com.test.xcamera.MoPresenters.MoFPVPresenter;
import com.test.xcamera.R;
import com.test.xcamera.bean.MoShotSetting;
import com.test.xcamera.bean.MoSystemInfo;
import com.test.xcamera.enumbean.ScreenOrientationType;
import com.test.xcamera.enumbean.ShootMode;
import com.test.xcamera.managers.ShotModeManager;
import com.test.xcamera.mointerface.MoCountDownListener;
import com.test.xcamera.mointerface.MoFPVCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * FPV页面设置拍摄参数的view  显示在fpv页面头部
 * Created by zll on 2019/10/23.
 */

public class MoFPVShotSettingView extends RelativeLayout implements View.OnClickListener,
        MoFPVShotSettingControl.MoFPVShotSettingControlListener {
    public static final String TAG = "MoFPVShotSettingView";
    private static final int SET_BATTERY_SDCARD = 200001;
    private static final int SYNC_SHOT_SETTING = 200002;
    private static final int START_RECORD = 200003;
    private static final int STOP_RECORD = 200004;
    private static final int LONG_EXPLORE_START = 200005;
    private static final int LONG_EXPLORE_STOP = 200006;

    private View mRootView;

    public TextView mBatteryText;
    public BatteryView mBatteryView;
    //    @BindView(R.id.fragment_preview_control_sd_card_text)
    public TextView mSdCardText;
    public ImageView mSdCardIV;
    public TextView mRectCount;
    //    @BindView(R.id.fragment_preview_control_countdown_layout)
    PreviewCountDownView mPreviewCountDownView;
    //    @BindView(R.id.fragment_preview_control_resolution_layout)
    PreviewResolutionView mPreviewResolutionView;
    PreviewBeautyView mPreviewBeautyView;
    //    @BindView(R.id.fragment_preview_scale_layout)
    PreviewScaleView mScaleView;
    //    @BindView(R.id.fragment_preview_lapse_layout)
    PreviewLapseView mLapseView;
    //    @BindView(R.id.fragment_preview_slowmotion_layout)
    PreviewSlowmotionView mSlowMotionView;
    //    @BindView(R.id.fragment_preview_control_battery_sd_layout)
    LinearLayout mBatterySDCardLayout;
    //    @BindView(R.id.fragment_preview_control_battery_layout)
    LinearLayout mBatteryLayout;
    //    @BindView(R.id.fragment_preview_control_sd_layout)
    LinearLayout mSDCardLayout;
    RelativeLayout mParentLayout;
    public String countDown = "";

    PreviewLongExploreView mLongExploreView;

    private View mExploreLayout;
    private ImageView mExploreImage;
    private TextView mExploreText;

    private View mLandView, mPortraitView;

    private MoFPVCallback mFPVCallback;
    private MoFPVPresenter mFPVPresenter;
    private Context mContext;
    private ScreenOrientationType mOrientationType = ScreenOrientationType.LANDSCAPE;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SET_BATTERY_SDCARD:
                    MoSystemInfo systemInfo = (MoSystemInfo) msg.obj;
                    mBatteryText.setText(systemInfo.getmBattery() + "%");
                    mBatteryView.setProgress(systemInfo.getmBattery() / 100f);

                    mSdCardText.setText(FileUtils.formatSize(systemInfo.getmSDCardCapacity()));
                    switch (systemInfo.getSdStatus()) {
                        case MoErrorCallback.SD_IN:
                            mSdCardIV.setImageResource(R.mipmap.icon_fpv_mode_sdcard);
                            break;
                        case MoErrorCallback.SD_OUT:
                            mSdCardIV.setImageResource(R.mipmap.icon_sdcard_out);
                            return;
                        case MoErrorCallback.SD_IN_FAIL:
                            mSdCardIV.setImageResource(R.mipmap.icon_sdcard_out);
                            return;
                        case MoErrorCallback.SD_FULL:
                            mSdCardIV.setImageResource(R.mipmap.icon_sdcard_out);
                            return;
                        default:
                            mSdCardIV.setImageResource(R.mipmap.icon_fpv_mode_sdcard);
                            break;
                    }

                    break;
                case SYNC_SHOT_SETTING:
                    MoShotSetting shotSetting = (MoShotSetting) msg.obj;
                    setShotState(shotSetting);
                    ShotModeManager.getInstance().syncShotMode(shotSetting);
                    mPreviewResolutionView.syncSetting(shotSetting);
                    mPreviewBeautyView.syncSetting(shotSetting);
                    mLongExploreView.syncSetting(shotSetting);
                    mPreviewCountDownView.syncSetting(shotSetting);
                    mSlowMotionView.syncSetting(shotSetting);
                    mLapseView.syncSetting(shotSetting);
                    mScaleView.syncSetting(shotSetting);
                    break;
                case START_RECORD:
                    setRecordViewState(false);
                    break;
                case STOP_RECORD:
                    setRecordViewState(true);
                    break;
                case LONG_EXPLORE_START:
                    setLongExploreViewState(View.GONE);
                    break;
                case LONG_EXPLORE_STOP:
                    setLongExploreViewState(View.VISIBLE);
                    break;
            }
        }
    };

    public MoFPVShotSettingView(Context context) {
        super(context);

        initView(context);
    }

    public MoFPVShotSettingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView(context);
    }

    public void setMoFPVCallback(MoFPVCallback callback) {
        mFPVCallback = callback;
        MoFPVShotSettingControl.getInstance().setFPVCallback(callback);
    }

    public void setMoFPVPresenter(MoFPVPresenter presenter) {
        mFPVPresenter = presenter;
        MoFPVShotSettingControl.getInstance().setPresenter(presenter);
        mPreviewResolutionView.setFPVPresenter(presenter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(EventMessage msg) {
        if (msg.code == EventMessage.SCREEN_CLICKED) {
            if (WorkStateManager.getInstance().getmWorkState() == WorkState.STANDBY)
                hideSecondView();
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

    private void initView(Context context) {
        mContext = context;

        mRootView = LayoutInflater.from(context).inflate(R.layout.fpv_shot_setting_layout, this, true);

        mLandView = mRootView.findViewById(R.id.fpv_shot_setting_top_land_view);
        mPortraitView = mRootView.findViewById(R.id.fpv_shot_setting_top_portrait_view);
        MoFPVShotSettingControl.getInstance().setParentView(mLandView, mPortraitView);

        mBatteryText = mRootView.findViewById(R.id.fragment_preview_control_battery_text);
        mBatteryView = mRootView.findViewById(R.id.fragment_preview_control_battery);
        mSdCardText = mRootView.findViewById(R.id.fragment_preview_control_sd_card_text);
        mSdCardIV = mRootView.findViewById(R.id.fragment_preview_control_sd_card);
        mRectCount = mRootView.findViewById(R.id.todo_rect_count);
        mPreviewCountDownView = mRootView.findViewById(R.id.fragment_preview_control_countdown_layout);
        mPreviewResolutionView = mRootView.findViewById(R.id.fragment_preview_control_resolution_layout);
        mPreviewBeautyView = mRootView.findViewById(R.id.fragment_preview_control_beauty_layout);
        mScaleView = mRootView.findViewById(R.id.fragment_preview_scale_layout);
        mLapseView = mRootView.findViewById(R.id.fragment_preview_lapse_layout);
        mSlowMotionView = mRootView.findViewById(R.id.fragment_preview_slowmotion_layout);
        mBatterySDCardLayout = mRootView.findViewById(R.id.fragment_preview_control_battery_sd_layout);
        mBatteryLayout = mRootView.findViewById(R.id.fragment_preview_control_battery_layout);
        mSDCardLayout = mRootView.findViewById(R.id.fragment_preview_control_sd_layout);
        mParentLayout = mRootView.findViewById(R.id.fpv_shot_setting_layout);
        mLongExploreView = mRootView.findViewById(R.id.fragment_preview_longexplore_layout);
        mExploreLayout = mRootView.findViewById(R.id.fragment_preview_control_explore_layout);
        mExploreImage = mRootView.findViewById(R.id.fragment_preview_control_explore_image);
        mExploreText = mRootView.findViewById(R.id.fragment_preview_control_explore_text);

        MoFPVShotSettingControl.getInstance().initViewControl(mContext, mLandView, this);

        mPreviewCountDownView.setFPVPresenter(mFPVPresenter);
        mPreviewCountDownView.setCountDownListener(new MoCountDownListener() {
            @Override
            public void countDownChange(int seconds) {
                if (seconds == 3) {
                    mFPVCallback.changeDelayTime(3);
                    countDown = "animation/3_seconds.json";
                } else if (seconds == 5) {
                    mFPVCallback.changeDelayTime(5);
                    countDown = "animation/5_seconds.json";
                } else if (seconds == 7) {
                    mFPVCallback.changeDelayTime(7);
                    countDown = "animation/7_seconds.json";
                } else
                    countDown = "";
            }
        });

        mLapseView.setLapseValueChange((value -> {
            mFPVCallback.setLapseValue(value);
        }));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragment_preview_control_top_fps_layout:
                setPublicView(View.GONE);
                mPreviewResolutionView.setVisibility(View.VISIBLE);
                break;
            case R.id.fragment_preview_control_top_count_down_layout:
                setPublicView(View.GONE);
                mPreviewCountDownView.setVisibility(View.VISIBLE);
                break;
            case R.id.fragment_preview_control_lapse_layout:
                setPublicView(View.GONE);
                mLapseView.setVisibility(View.VISIBLE);
                break;
            case R.id.fragment_preview_control_slowmotion_layout:
                setPublicView(View.GONE);
                mSlowMotionView.setVisibility(View.VISIBLE);
                break;
            case R.id.fragment_preview_control_scale_layout:
                setPublicView(View.GONE);
                mScaleView.setVisibility(View.VISIBLE);
                break;
            case R.id.fragment_preview_control_top_setting_layout:
                setPublicView(View.GONE);
                if (mFPVCallback != null)
                    mFPVCallback.showMoreSetting();
                break;
            case R.id.fragment_preview_control_longexplore_layout:
                setPublicView(GONE);
                mLongExploreView.setVisibility(VISIBLE);
                break;
            case R.id.fragment_preview_beauty_view:
                setPublicView(GONE);
                mPreviewBeautyView.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void setPublicView(int visibility) {
        if (mOrientationType == ScreenOrientationType.LANDSCAPE) {
            mLandView.setVisibility(visibility);
        } else if (mOrientationType == ScreenOrientationType.PORTRAIT) {
            mPortraitView.setVisibility(visibility);
        }
        mBatterySDCardLayout.setVisibility(visibility);
    }

    public boolean isSecondShown(MotionEvent event) {
        return (mPreviewCountDownView.getVisibility() == VISIBLE ||
                mPreviewResolutionView.getVisibility() == VISIBLE ||
                mLapseView.getVisibility() == VISIBLE ||
                mSlowMotionView.getVisibility() == VISIBLE ||
                mScaleView.getVisibility() == VISIBLE ||
                mLongExploreView.getVisibility() == VISIBLE);
    }

    public void hideSecondView() {
        if (mPreviewResolutionView.getVisibility() == View.VISIBLE) {
            mPreviewResolutionView.setVisibility(View.GONE);
        } else if (mScaleView.getVisibility() == View.VISIBLE) {
            mScaleView.setVisibility(View.GONE);
        } else if (mLapseView.getVisibility() == View.VISIBLE) {
            mLapseView.setVisibility(View.GONE);
        } else if (mSlowMotionView.getVisibility() == View.VISIBLE) {
            mSlowMotionView.setVisibility(View.GONE);
        } else if (mPreviewCountDownView.getVisibility() == View.VISIBLE) {
            mPreviewCountDownView.setVisibility(View.GONE);
        } else if (mLongExploreView.getVisibility() == View.VISIBLE) {
            mLongExploreView.setVisibility(View.GONE);
        } else if (mPreviewBeautyView.getVisibility() == View.VISIBLE) {
            mPreviewBeautyView.setVisibility(View.GONE);
        }
        setPublicView(View.VISIBLE);
    }

    public void setLapseMode() {
        hideSecondView();
        ShotModeManager.getInstance().setmShootMode(ShootMode.LAPSE_VIDEO);
        MoFPVShotSettingControl.getInstance().setLapseMode();
    }

    public void setSlowMotionMode() {
        hideSecondView();
        ShotModeManager.getInstance().setmShootMode(ShootMode.SLOW_MOTION);
        MoFPVShotSettingControl.getInstance().setSlowMotionMode();
    }

    public void setVideoMode() {
        hideSecondView();
        ShotModeManager.getInstance().setmShootMode(ShootMode.VIDEO);
        MoFPVShotSettingControl.getInstance().setVideoMode();
    }

    public void setStoryMode() {
//        ShotModeManager.getInstance().setmShootMode(ShootMode.STORY);
//        MoFPVShotSettingControl.getInstance().setStoryMode();
//        mScaleView.changeOrientation(mOrientationType);
    }

    public void setPhotoMode() {
        hideSecondView();
        ShotModeManager.getInstance().setmShootMode(ShootMode.PHOTO);
        MoFPVShotSettingControl.getInstance().setPhotoMode();
    }

    public void setLongExploreMode() {
        hideSecondView();
        ShotModeManager.getInstance().setmShootMode(ShootMode.LONG_EXPLORE);
        MoFPVShotSettingControl.getInstance().setLongExploreMode();
    }

    public void setBeautyMode() {
    }

    public void setSystemInfo(MoSystemInfo systemInfo) {
        sendMessage(SET_BATTERY_SDCARD, systemInfo);
    }

    public void syncShotSetting(MoShotSetting shotSetting) {
//        sendMessage(SYNC_SHOT_SETTING, shotSetting);
        setShotState(shotSetting);
        ShotModeManager.getInstance().syncShotMode(shotSetting);
        if (shotSetting.getmMode() == ShootMode.SLOW_MOTION) {
            mSlowMotionView.syncSetting(shotSetting);
        } else if (shotSetting.getmMode() == ShootMode.LONG_EXPLORE) {
            mLongExploreView.syncSetting(shotSetting);
        } else if (shotSetting.getmMode() == ShootMode.PHOTO) {
            mPreviewCountDownView.syncSetting(shotSetting);
            mPreviewBeautyView.syncSetting(shotSetting);
        } else if (shotSetting.getmMode() == ShootMode.LAPSE_VIDEO) {
            mLapseView.syncSetting(shotSetting);
        } else if (shotSetting.getmMode() == ShootMode.VIDEO) {
            mPreviewResolutionView.syncSetting(shotSetting);
            mPreviewBeautyView.syncSetting(shotSetting);
        }
        mScaleView.syncSetting(shotSetting);
    }

    private void sendMessage(int what, Object object) {
        Message message = Message.obtain();
        message.what = what;
        message.obj = object;
        mHandler.sendMessage(message);
    }

    private void setShotState(MoShotSetting shotSetting) {
        switch (shotSetting.getmMode()) {
            case PHOTO:
                mFPVCallback.selectPhotoMode(false);
                break;
            case VIDEO:
                mFPVCallback.selectVideoMode(shotSetting.needCmd);
                break;
            case LONG_EXPLORE:
                mFPVCallback.selectLongExploreMode(false);
                break;
            case SLOW_MOTION:
                mFPVCallback.selectSlowMotionMode(false);
                break;
            case LAPSE_VIDEO:
                mFPVCallback.selectLapseMode(false);
                break;
            case PHOTO_BEAUTY:
                mFPVCallback.selectBeauty(false);
                break;
            case VIDEO_BEAUTY:
                mFPVCallback.selectBeauty(false);
                break;
        }
    }

    public void startTakeVideo() {
        sendMessage(START_RECORD, null);
    }

    public void stopTakeVideo() {
        sendMessage(STOP_RECORD, null);
    }

    public void startLongExplore() {
        sendMessage(LONG_EXPLORE_START, null);
    }

    public void stopLongExplore() {
        sendMessage(LONG_EXPLORE_STOP, null);
    }

    public void setRecordViewState(boolean visibility) {
        if (visibility) {
            if (mOrientationType == ScreenOrientationType.LANDSCAPE) {
                mLandView.setVisibility(View.VISIBLE);
                mPortraitView.setVisibility(View.INVISIBLE);
            } else {
                mLandView.setVisibility(View.INVISIBLE);
                mPortraitView.setVisibility(View.VISIBLE);
            }
        } else {
            mLandView.setVisibility(View.INVISIBLE);
            mPortraitView.setVisibility(View.INVISIBLE);
        }
    }

    public void setLongExploreViewState(int viewState) {
        mRootView.setVisibility(viewState);
    }

    public void takeDelayPhoto() {
        mFPVPresenter.takePhoto();
    }

    public void changeOrientation(ScreenOrientationType orientationType) {
        mOrientationType = orientationType;
        if (orientationType == ScreenOrientationType.LANDSCAPE) {
            if (mPortraitView.getVisibility() == View.VISIBLE || mLandView.getVisibility() == View.VISIBLE) {
                mPortraitView.setVisibility(View.INVISIBLE);
                mLandView.setVisibility(View.VISIBLE);
            }
            setViewOrientation(0, mLandView);
            mPreviewCountDownView.changeOrientation(ScreenOrientationType.LANDSCAPE);
            mSlowMotionView.changeOrientation(ScreenOrientationType.LANDSCAPE);
            mLapseView.changeOrientation(ScreenOrientationType.LANDSCAPE);
            mPreviewResolutionView.changeOrientation(ScreenOrientationType.LANDSCAPE);
            mLongExploreView.changeOrientation(ScreenOrientationType.LANDSCAPE);
            mScaleView.changeOrientation(ScreenOrientationType.LANDSCAPE);
            mPreviewBeautyView.changeOrientation(ScreenOrientationType.LANDSCAPE);

            RelativeLayout.LayoutParams layoutParams = (LayoutParams) mBatterySDCardLayout.getLayoutParams();
            layoutParams.setMargins(ViewUitls.dp2px(getContext(), 60), ViewUitls.dp2px(getContext(), 18), 0, 0);
            mBatterySDCardLayout.setLayoutParams(layoutParams);
        } else if (orientationType == ScreenOrientationType.PORTRAIT) {
            if (mPortraitView.getVisibility() == View.VISIBLE || mLandView.getVisibility() == View.VISIBLE) {
                mLandView.setVisibility(View.INVISIBLE);
                mPortraitView.setVisibility(View.VISIBLE);
            }
            setViewOrientation(-90, mPortraitView);
            mPreviewCountDownView.changeOrientation(ScreenOrientationType.PORTRAIT);
            mSlowMotionView.changeOrientation(ScreenOrientationType.PORTRAIT);
            mLapseView.changeOrientation(ScreenOrientationType.PORTRAIT);
            mPreviewResolutionView.changeOrientation(ScreenOrientationType.PORTRAIT);
            mLongExploreView.changeOrientation(ScreenOrientationType.PORTRAIT);
            mScaleView.changeOrientation(ScreenOrientationType.PORTRAIT);
            mPreviewBeautyView.changeOrientation(ScreenOrientationType.PORTRAIT);
            //对齐图标
            RelativeLayout.LayoutParams layoutParams = (LayoutParams) mBatterySDCardLayout.getLayoutParams();
            int off = ViewUitls.px2dp(getContext(), mBatteryText.getWidth()) - 24;
            int margin = 21 - 5 - (off < 0 ? 0 : off / 2);
            layoutParams.setMargins(ViewUitls.dp2px(getContext(), 60), ViewUitls.dp2px(getContext(), margin), 0, 0);
            mBatterySDCardLayout.setLayoutParams(layoutParams);
        }
    }

    public void setViewOrientation(int orientation, View view) {
        MoFPVShotSettingControl.getInstance().initViewControl(mContext, view, this);
        MoFPVShotSettingControl.getInstance().rotateView(orientation);
        mBatteryLayout.animate().rotation(orientation);
        mSDCardLayout.animate().rotation(orientation);
        mExploreLayout.animate().rotation(orientation);
    }

    @Override
    public void clickBack() {
        mFPVCallback.onFinish("");
    }

    @Override
    public void clickFPS() {
        setPublicView(View.GONE);
        mPreviewResolutionView.setVisibility(View.VISIBLE);
    }

    @Override
    public void clickCountDown() {
        setPublicView(View.GONE);
        mPreviewCountDownView.setVisibility(View.VISIBLE);
    }

    @Override
    public void clickLapse() {
        setPublicView(View.GONE);
        mLapseView.setVisibility(View.VISIBLE);
    }

    @Override
    public void clickSlowMotion() {
        setPublicView(View.GONE);
        mSlowMotionView.setVisibility(View.VISIBLE);
    }

    @Override
    public void clickScale() {
        setPublicView(View.GONE);
        mScaleView.setVisibility(View.VISIBLE);
    }

    @Override
    public void clickSetting() {
        if (mFPVCallback != null)
            mFPVCallback.showMoreSetting();
    }

    @Override
    public void clickLongExplore() {
        setPublicView(GONE);
        mLongExploreView.setVisibility(VISIBLE);
    }

    @Override
    public void clickBeauty() {
        setPublicView(GONE);
        mPreviewBeautyView.setVisibility(VISIBLE);
    }

    public void setExploreVisibility(int visibility) {
        mExploreLayout.setVisibility(visibility);
    }

    public void setExploreValue(String value) {
        mExploreText.setText(value + "");
    }
}
