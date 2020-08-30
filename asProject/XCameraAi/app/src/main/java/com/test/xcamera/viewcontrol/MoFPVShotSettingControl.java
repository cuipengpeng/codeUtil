package com.test.xcamera.viewcontrol;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.test.xcamera.MoPresenters.MoFPVPresenter;
import com.test.xcamera.R;
import com.test.xcamera.activity.MoFPVActivity;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.enumbean.ShootMode;
import com.test.xcamera.enumbean.WorkState;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.managers.ShotModeManager;
import com.test.xcamera.managers.WorkStateManager;
import com.test.xcamera.mointerface.MoFPVCallback;
import com.test.xcamera.mointerface.MoRequestCallback;
import com.test.xcamera.utils.DlgUtils;
import com.test.xcamera.utils.StringUtil;

import java.lang.ref.WeakReference;

/**
 * Created by zll on 2019/11/14.
 * <p>
 * FPV页面设置拍摄参数view的帮助类 同MoFPVShotSettingView
 */

public class MoFPVShotSettingControl implements View.OnClickListener {
    private static final String TAG = "MoFPVShotSettingControl";
    private static MoFPVShotSettingControl instance = null;
    private static Object lock = new Object();
    private MoFPVShotSettingControlListener mControlListener;
    private MoFPVPresenter mPresenter;
    private WeakReference<MoFPVCallback> mFPVCallback;
    private volatile boolean mBeautyImageAble, mBeautyStatus;

    private View mBackBtn;
    //    private ImageView mFpsImg;
    private LinearLayout mFpsLayout;
    private RelativeLayout mCountDownLayout;
    private ImageView mLapseImage;
    private RelativeLayout mLapseLayout;
    private ImageView mSlowMotionImage;
    private RelativeLayout mSlowMotionLayout;
    private RelativeLayout mScaleLayout;
    private View mSettingLayout;
    private RelativeLayout mBeautyLayout;
    private RelativeLayout mLongExploreLayout;
    private ImageView mLongExploreImage;

    private ImageView mSettingImage;
    private ImageView mScaleImage, mBeautyImage;
    private ImageView mCountDownImg;
    private boolean mEnableTakePhoto = true;

    private TextView mCountDownValue;
    private TextView mLongExploreValue;
    private TextView mSlowMotionValue;
    private TextView mLapseValue;
    private TextView mFpsValue;
    private TextView mResolutionValue;
    private String mFpsData, mResolutionData;
    public float mLongExploreTime;
    private View mParentView[] = new View[2];

    public static MoFPVShotSettingControl getInstance() {
        synchronized (lock) {
            if (instance == null) {
                instance = new MoFPVShotSettingControl();
            }
        }
        return instance;
    }

    public void release() {
        instance = null;
    }

    public void initViewControl(Context context, View view, MoFPVShotSettingControlListener listener) {
        mControlListener = listener;

        findView(view);
        setViewState();
    }

    public void setPresenter(MoFPVPresenter presenter) {
        mPresenter = presenter;
    }

    public void setFPVCallback(MoFPVCallback callback) {
        mFPVCallback = new WeakReference<>(callback);
    }

    /**
     * 设置首页面倒计时时间
     */
    public void setCountDownValue(int mCountDown) {
        if (mCountDownValue != null) {
            mCountDownValue.setVisibility(mCountDown == 0 ? View.GONE : View.VISIBLE);
            mCountDownValue.setText(mCountDown + StringUtil.getStr(R.string.second));
        }
    }

    /**
     * 设置首页面长曝光时间
     */
    public void setLongExploreValue(float duration) {
        if (mLongExploreValue != null) {
            mLongExploreTime = duration;
            mLongExploreValue.setVisibility(duration == 0 ? View.GONE : View.VISIBLE);

            if (duration - (int) duration < 0.01)
                mLongExploreValue.setText(((int) duration) + StringUtil.getStr(R.string.second));
            else
                mLongExploreValue.setText(duration + StringUtil.getStr(R.string.second));
        }
    }

    public void setSlowMotionValue(int mSpeed, boolean cb) {
        if (mSlowMotionValue != null) {
            mSlowMotionValue.setVisibility(mSpeed == 0 ? View.GONE : View.VISIBLE);
            mSlowMotionValue.setText(mSpeed + "X");
        }

        if (mFPVCallback != null && mFPVCallback.get() != null && cb) {
            mFPVCallback.get().setSlowMotionValue(mSpeed, true);
        } else {
            MoFPVSlowMotionControl.getInstance().refresh();
        }
    }

    public void setLapseValue(String mDuration) {
        if (mLapseValue != null) {
            mLapseValue.setText(mDuration);
            mLapseValue.setVisibility(TextUtils.isEmpty(mDuration) ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * 设置首页面视频模式下帧率及分辨率
     */
    public void setFpsValue(String fps, String resolution) {
        if (mFpsValue != null && mResolutionValue != null) {
            mFpsValue.setText(fps);
            mResolutionValue.setText("/" + resolution);
        } else {
            mFpsData = fps;
            mResolutionData = resolution;
        }
    }

    /**
     * 设置超宽屏图标
     *
     * @param normal true 正常 false 超宽
     */
    public void setScaleIcon(boolean normal) {
        if (mScaleImage != null && mEnableTakePhoto) {
            for (View view : mParentView) {
                ImageView scaleImage = view.findViewById(R.id.fragment_preview_control_scale_image);
                if (normal)
                    scaleImage.setImageResource(R.mipmap.icon_fpv_mode_ratio_normal);
                else
                    scaleImage.setImageResource(R.mipmap.icon_19_5_9_width_p);
                scaleImage.setTag(normal);
            }
        }
    }

    private void findView(View view) {
        mBackBtn = view.findViewById(R.id.fragment_preview_control_top_back);
        mFpsLayout = view.findViewById(R.id.fragment_preview_control_top_fps_layout);
//        mFpsImg = view.findViewById(R.id.fragment_preview_control_top_fps_image);
        mCountDownLayout = view.findViewById(R.id.fragment_preview_control_top_count_down_layout);
        mCountDownImg = view.findViewById(R.id.fragment_preview_control_countdown_image);
        mLapseLayout = view.findViewById(R.id.fragment_preview_control_lapse_layout);
        mLapseImage = view.findViewById(R.id.fragment_preview_control_lapse_image);
        mSlowMotionLayout = view.findViewById(R.id.fragment_preview_control_slowmotion_layout);
        mSlowMotionImage = view.findViewById(R.id.fragment_preview_control_slowmotion_image);
        mScaleLayout = view.findViewById(R.id.fragment_preview_control_scale_layout);
        mScaleImage = view.findViewById(R.id.fragment_preview_control_scale_image);
        mSettingLayout = view.findViewById(R.id.fragment_preview_control_top_setting_layout);
        mSettingImage = view.findViewById(R.id.fragment_preview_control_top_setting_image);
        mLongExploreLayout = view.findViewById(R.id.fragment_preview_control_longexplore_layout);
        mLongExploreImage = view.findViewById(R.id.fragment_preview_control_longexplore_image);
        mBeautyLayout = view.findViewById(R.id.fragment_preview_beauty_view);
        mBeautyImage = view.findViewById(R.id.fragment_preview_beauty_button);
        setBeautyStatus(mBeautyStatus);

        mCountDownValue = view.findViewById(R.id.fragment_preview_control_countdown_value);
        mLongExploreValue = view.findViewById(R.id.fragment_preview_control_longexplore_value);
        mSlowMotionValue = view.findViewById(R.id.fragment_preview_control_slowmotion_value);
        mLapseValue = view.findViewById(R.id.fragment_preview_control_lapse_value);
        mFpsValue = view.findViewById(R.id.fragment_preview_control_top_fps_text);
        mResolutionValue = view.findViewById(R.id.fragment_preview_control_top_resolution_text);
        //首次加载时 mFpsValue/mResolutionValue为null 只能先保存再赋值
        if (!TextUtils.isEmpty(mFpsData))
            mFpsValue.setText(mFpsData);
        if (!TextUtils.isEmpty(mResolutionData))
            mResolutionValue.setText("/" + mResolutionData);

        mBackBtn.setOnClickListener(this);
        mBeautyLayout.setOnClickListener(this);
        mFpsLayout.setOnClickListener(this);
        mCountDownLayout.setOnClickListener(this);
        mLapseLayout.setOnClickListener(this);
        mSlowMotionLayout.setOnClickListener(this);
        mScaleLayout.setOnClickListener(this);
        mSettingLayout.setOnClickListener(this);
        mLongExploreLayout.setOnClickListener(this);
    }

    public void setParentView(View landView, View portraitView) {
        this.mParentView[0] = landView;
        this.mParentView[1] = portraitView;
    }

    public void setBeautyAble(boolean enable) {
        mBeautyImageAble = enable;
        setBeautyIcon();
    }

    public void setBeautyStatus(boolean status) {
        mBeautyStatus = status;
        setBeautyIcon();
    }

    public void setBeautyIcon() {
        if (mBeautyImage != null) {
            if (mBeautyImageAble && mBeautyStatus) {
                mBeautyImage.setBackgroundResource(R.mipmap.icon_beauty_on);
            } else if (mBeautyImageAble && !mBeautyStatus) {
                mBeautyImage.setBackgroundResource(R.mipmap.icon_beauty_off);
            } else if (!mBeautyImageAble && mBeautyStatus) {
                mBeautyImage.setBackgroundResource(R.mipmap.icon_beauty_on_dis);
            } else if (!mBeautyImageAble && !mBeautyStatus) {
                mBeautyImage.setBackgroundResource(R.mipmap.icon_beauty_off_dis);
            }
        }
    }

    public void setBeautyEnable() {
        if (mBeautyImage != null)
            mBeautyImage.setBackgroundResource(mBeautyStatus ? R.mipmap.icon_beauty_on_dis : R.mipmap.icon_beauty_off_dis);
    }

    public void rotateView(int orientation) {
//        mBackBtn.animate().rotation(orientation);
        mBeautyImage.animate().rotation(orientation);
        mScaleImage.animate().rotation(orientation);
        mSettingImage.animate().rotation(orientation);
        mSlowMotionImage.animate().rotation(orientation);
        mLapseImage.animate().rotation(orientation);
        mCountDownImg.animate().rotation(orientation);
        mFpsLayout.animate().rotation(orientation);
    }

    /**
     * 拍照时 禁用按钮
     */
    public void takePhotoIcon(boolean enable) {
        this.mEnableTakePhoto = enable;
        for (View view : mParentView) {
            ImageView settingImage = view.findViewById(R.id.fragment_preview_control_top_setting_image);
            ImageView scaleImage = view.findViewById(R.id.fragment_preview_control_scale_image);
            TextView scaleValue = view.findViewById(R.id.fragment_preview_control_countdown_value);
            View beautyBtn = view.findViewById(R.id.fragment_preview_beauty_button);
            ImageView countDownImg = view.findViewById(R.id.fragment_preview_control_countdown_image);
            ImageView back = view.findViewById(R.id.control_back);
            boolean scaleFlag = scaleImage.getTag() != null && (boolean) scaleImage.getTag();

            if (enable) {
                settingImage.setImageResource(R.mipmap.icon_fpv_setting);
                scaleImage.setImageResource(scaleFlag ? R.mipmap.icon_fpv_mode_ratio_normal : R.mipmap.icon_19_5_9_width_p);
                countDownImg.setImageResource(R.mipmap.icon_count_down);
                scaleValue.setTextColor(Color.WHITE);
                back.setImageResource(R.mipmap.icon_mo_back);
                beautyBtn.setSelected(true);
            } else {
                settingImage.setImageResource(R.mipmap.icon_setting_disable);
                scaleImage.setImageResource(scaleFlag ? R.mipmap.icon_fpv_mode_ratio_normal_disable : R.mipmap.icon_19_5_9_width_p_disable);
                countDownImg.setImageResource(R.mipmap.icon_count_down_disable);
                scaleValue.setTextColor(ContextCompat.getColor(scaleValue.getContext(), R.color.color_666666));
                back.setImageResource(R.mipmap.back_disable);
                beautyBtn.setSelected(false);
            }
        }
    }

    public void setLapseMode() {
        //清空文字
        MoFPVShotSettingControl.getInstance().setLapseValue("");
        mBeautyLayout.setVisibility(View.GONE);
        mFpsLayout.setVisibility(View.GONE);
        mSlowMotionLayout.setVisibility(View.GONE);
        mCountDownLayout.setVisibility(View.GONE);
        mLongExploreLayout.setVisibility(View.GONE);
        mLapseLayout.setVisibility(View.VISIBLE);
        mScaleLayout.setVisibility(View.GONE);
    }

    public void setSlowMotionMode() {
        mBeautyLayout.setVisibility(View.GONE);
        mFpsLayout.setVisibility(View.GONE);
        mCountDownLayout.setVisibility(View.GONE);
        mSlowMotionLayout.setVisibility(View.VISIBLE);
        mLapseLayout.setVisibility(View.GONE);
        mLongExploreLayout.setVisibility(View.GONE);
        mScaleLayout.setVisibility(View.GONE);
    }

    public void setVideoMode() {
        mBeautyLayout.setVisibility(View.VISIBLE);
        mCountDownLayout.setVisibility(View.GONE);
        mLapseLayout.setVisibility(View.GONE);
        mSlowMotionLayout.setVisibility(View.GONE);
        mLongExploreLayout.setVisibility(View.GONE);
        mFpsLayout.setVisibility(View.VISIBLE);
        mScaleLayout.setVisibility(View.GONE);
    }

    public void setPhotoMode() {
        mBeautyLayout.setVisibility(View.VISIBLE);
        setBeautyAble(true);

        mCountDownLayout.setVisibility(View.VISIBLE);
        mFpsLayout.setVisibility(View.GONE);
        mLapseLayout.setVisibility(View.GONE);
        mSlowMotionLayout.setVisibility(View.GONE);
        mLongExploreLayout.setVisibility(View.GONE);
        mScaleLayout.setVisibility(View.VISIBLE);
    }

    public void setLongExploreMode() {
        mBeautyLayout.setVisibility(View.GONE);
        mCountDownLayout.setVisibility(View.GONE);
        mLapseLayout.setVisibility(View.GONE);
        mSlowMotionLayout.setVisibility(View.GONE);
        mLongExploreLayout.setVisibility(View.VISIBLE);
        mFpsLayout.setVisibility(View.GONE);
        mScaleLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragment_preview_control_top_back:
                if (!this.mEnableTakePhoto)
                    break;
                if (WorkStateManager.getInstance().enableSwitch())
                    break;
                if (mFPVCallback != null && mFPVCallback.get() != null)
                    mFPVCallback.get().onFinish(MoFPVActivity.FINISH_FPV);
                break;
            case R.id.fragment_preview_control_top_fps_layout:
                mControlListener.clickFPS();
                break;
            case R.id.fragment_preview_control_top_count_down_layout:
                if (!this.mEnableTakePhoto)
                    break;

                mControlListener.clickCountDown();
                break;
            case R.id.fragment_preview_control_lapse_layout:
                mControlListener.clickLapse();
                break;
            case R.id.fragment_preview_control_slowmotion_layout:
                mControlListener.clickSlowMotion();
                break;
            case R.id.fragment_preview_control_scale_layout:
                if (!this.mEnableTakePhoto)
                    break;

                mControlListener.clickScale();
                break;
            case R.id.fragment_preview_control_top_setting_layout:
                if (!this.mEnableTakePhoto)
                    break;

                if (mControlListener != null)
                    mControlListener.clickSetting();
                break;
            case R.id.fragment_preview_control_longexplore_layout:
                mControlListener.clickLongExplore();
                break;
            case R.id.fragment_preview_beauty_view:
                if (WorkStateManager.getInstance().getmWorkState() == WorkState.PHOTOING)
                    break;

                if (mBeautyImageAble) {
//                    mControlListener.clickBeauty();
                    ConnectionManager.getInstance().setAbleBeauty(mBeautyStatus ? 0 : 1, new MoRequestCallback() {
                        @Override
                        public void onSuccess() {
                            mBeautyImage.post(() -> {
                                setBeautyStatus(!mBeautyStatus);
                            });
                        }
                    });
                } else if (ShotModeManager.getInstance().getmShootMode() == ShootMode.VIDEO) {
                    DlgUtils.toast(AiCameraApplication.getContext(), mBeautyImage.getResources().getString(R.string.fpv_beauty_unsupport), 0);
                }
                break;
        }
    }

    private void setViewState() {
        switch (ShotModeManager.getInstance().getmShootMode()) {
            case VIDEO:
                setVideoMode();
                break;
            case PHOTO:
                setPhotoMode();
                break;
            case SLOW_MOTION:
                setSlowMotionMode();
                break;
            case LAPSE_VIDEO:
                setLapseMode();
                break;
            case LONG_EXPLORE:
                setLongExploreMode();
                break;
        }
    }

    public interface MoFPVShotSettingControlListener {
        void clickBack();

        void clickFPS();

        void clickCountDown();

        void clickLapse();

        void clickSlowMotion();

        void clickScale();

        void clickSetting();

        void clickLongExplore();

        void clickBeauty();
    }
}
