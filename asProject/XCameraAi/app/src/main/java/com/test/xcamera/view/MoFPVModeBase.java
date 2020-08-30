package com.test.xcamera.view;

import android.animation.Animator;
import android.content.Context;
import android.view.View;

import com.test.xcamera.accrssory.AccessoryManager;
import com.test.xcamera.bean.MoSettingModel;
import com.test.xcamera.enumbean.ScreenOrientationType;

import java.util.List;

/**
 * Created by smz on 2019/12/19.
 * 相机模式基类
 */
public abstract class MoFPVModeBase {
    public static final int CANCEL = 0x8000;
    public static final int RECORDING_NORMAL = 0x4001;
    public static final int RECORD_NORMAL_STOP_IMM = 0x4002;
    public static final int RECORD_NORMAL_STOP = 0x8003;
    public static final int RECORDING_LAPSE = 0x4004;
    public static final int RECORD_LAPSE_STOP_IMM = 0x4005;
    public static final int RECORD_LAPSE_STOP = 0x8006;
    public static final int RECORDING_SLOW = 0x4007;
    public static final int RECORD_SLOW_STOP_IMM = 0x4008;
    public static final int RECORD_SLOW_STOP = 0x8009;
    public static final int PHOTO_NORMAL = 0x400A;
    public static final int LONG_EXPLORE = 0x400B;
    public static final int PHOTO_NORMAL_STOP = 0x800C;

    protected Context mContext;
    MoFPVModeView mFpvModeView;

    void initMode(Context context, MoFPVModeView modeViews) {
        mContext = context;
        mFpvModeView = modeViews;
    }

    /**
     * 头部进行转换时 使用两套布局 需要同步UI
     */
    void syncHeaderView(MoFPVModeView.ModeViews modeViews) {
        modeViews.customView24.setVisibility(View.INVISIBLE);
        modeViews.customView32.setVisibility(View.GONE);
    }

    /**
     * 点击参数按钮时 弹出选择框
     */
    abstract void showParamView();

    abstract void hideParamView();

    /**
     * 同步相机属性数据
     */
    abstract void syncParams(MoSettingModel settingModel);

    /**
     * 横竖屏切换
     */
    abstract void orientation(ScreenOrientationType type);

    /**
     * 判断相机的连接状态 控制点击按钮
     *
     * @return true 已经连接
     */
    protected boolean checkConn() {
        return AccessoryManager.getInstance().mIsRunning;
    }

    /**
     * 单行参数列表view
     */
    protected SingleParamsView getSingleLineParams(Context context, List<Entity> data, String info) {
        SingleParamsView view = new SingleParamsView(context);
        view.setData(info, data);
        return view;
    }

    /**
     * 拍照及摄影时 按钮的各种动画状态
     */
    protected void setShotState(int state) {
        setShotState(state, -1f);
    }

    protected void setShotState(int state, float value) {
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
                float speed = value > 0 ? 0.5f / value : 1f;
                stateStart("animation/animation_lapse_click.json", "animation/animation_lapse_time_cycle.json", speed);
                break;
            case RECORD_LAPSE_STOP_IMM:
                stateStore("animation/animation_lapse_stop_click.json", "animation/animation_lapse_loading.json");
                break;
            case RECORD_LAPSE_STOP:
                stateStop("animation/animation_lapse_restore.json");
                break;

            case RECORDING_SLOW:
                if (mFpvModeView.mModeViews.recordLottie != null) {
                    mFpvModeView.mModeViews.recordLottie.cancelAnimation();
                    mFpvModeView.mModeViews.recordLottie.setVisibility(View.VISIBLE);
                    mFpvModeView.mModeViews.recordLottie.loop(false);
                    mFpvModeView.mModeViews.recordLottie.setAnimation("animation/animation_slowmotion_click.json");
                    mFpvModeView.mModeViews.recordLottie.playAnimation();

                    mFpvModeView.mModeViews.recordLottie.removeAllAnimatorListeners();
                    mFpvModeView.mModeViews.recordLottie.addAnimatorListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            if (mFpvModeView.mModeViews.recordView != null) {
                                mFpvModeView.mModeViews.recordView.setVisibility(View.GONE);
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
                break;
            case RECORD_SLOW_STOP_IMM:
                stateStore("animation/animation_slowmotion_stop_click.json", "animation/animation_slowmotion_save_loading.json");
                break;
            case RECORD_SLOW_STOP:
                stateStop("animation/animation_slowmotion_restore.json");
                break;

            case PHOTO_NORMAL:
                mFpvModeView.setPhotoViews(false);
                stateStart("animation/animation_shot_click.json", "animation/animation_shot_loading.json");
                break;
            case LONG_EXPLORE:
                stateStart("animation/animation_video_click.json", "animation/animation_video_time_cycle.json");
                break;
            case PHOTO_NORMAL_STOP:
                mFpvModeView.setPhotoViews(true);
                stateStop("animation/animation_shot_restore.json");
                break;
            case CANCEL:
                if (mFpvModeView.mModeViews.recordLottie != null) {
                    mFpvModeView.mModeViews.recordLottie.setVisibility(View.GONE);
                    mFpvModeView.mModeViews.recordLottie.removeAllAnimatorListeners();
                    mFpvModeView.mModeViews.recordLottie.cancelAnimation();
                }
                if (mFpvModeView.mModeViews.recordView != null) {
                    mFpvModeView.mModeViews.recordView.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void stateStart(String anim1, String anim2) {
        this.stateStart(anim1, anim2, -1);
    }

    private void stateStart(String anim1, String anim2, float extra) {
        if (mFpvModeView.mModeViews.recordLottie != null) {
            if (mFpvModeView.mModeViews.recordLottie.isAnimating())
                mFpvModeView.mModeViews.recordLottie.cancelAnimation();
            mFpvModeView.mModeViews.recordLottie.removeAllAnimatorListeners();
            mFpvModeView.mModeViews.recordLottie.setVisibility(View.VISIBLE);
            mFpvModeView.mModeViews.recordLottie.loop(false);
            mFpvModeView.mModeViews.recordLottie.setSpeed(1f);
            mFpvModeView.mModeViews.recordLottie.setAnimation(anim1);

            mFpvModeView.mModeViews.recordLottie.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (mFpvModeView.mModeViews.recordView != null) {
                        mFpvModeView.mModeViews.recordView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (mFpvModeView.mModeViews.recordLottie.isAnimating())
                        mFpvModeView.mModeViews.recordLottie.cancelAnimation();
                    mFpvModeView.mModeViews.recordLottie.removeAllAnimatorListeners();

                    mFpvModeView.mModeViews.recordLottie.setAnimation(anim2);
                    mFpvModeView.mModeViews.recordLottie.loop(true);
                    if (extra > 0)
                        mFpvModeView.mModeViews.recordLottie.setSpeed(extra);
                    mFpvModeView.mModeViews.recordLottie.playAnimation();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            mFpvModeView.mModeViews.recordLottie.playAnimation();
        }
    }

    private void stateStore(String anim1, String anim2) {
        if (mFpvModeView.mModeViews.recordLottie != null) {
            if (mFpvModeView.mModeViews.recordLottie.isAnimating())
                mFpvModeView.mModeViews.recordLottie.cancelAnimation();
            mFpvModeView.mModeViews.recordLottie.removeAllAnimatorListeners();

            mFpvModeView.mModeViews.recordLottie.setVisibility(View.VISIBLE);
            mFpvModeView.mModeViews.recordLottie.loop(false);
            mFpvModeView.mModeViews.recordLottie.setSpeed(1f);
            mFpvModeView.mModeViews.recordLottie.setAnimation(anim1);

            mFpvModeView.mModeViews.recordLottie.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (mFpvModeView.mModeViews.recordView != null)
                        mFpvModeView.mModeViews.recordView.setVisibility(View.GONE);

                    mFpvModeView.mModeViews.recordLottie.removeAllAnimatorListeners();
                    if (mFpvModeView.mModeViews.recordLottie.isAnimating())
                        mFpvModeView.mModeViews.recordLottie.cancelAnimation();
                    mFpvModeView.mModeViews.recordLottie.loop(true);
                    mFpvModeView.mModeViews.recordLottie.setSpeed(1f);
                    mFpvModeView.mModeViews.recordLottie.setAnimation(anim2);
                    mFpvModeView.mModeViews.recordLottie.playAnimation();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            mFpvModeView.mModeViews.recordLottie.playAnimation();
        }
    }

    private void stateStop(String anim) {
        if (mFpvModeView.mModeViews.recordLottie.isAnimating())
            mFpvModeView.mModeViews.recordLottie.cancelAnimation();
        mFpvModeView.mModeViews.recordLottie.removeAllAnimatorListeners();
        mFpvModeView.mModeViews.recordLottie.setVisibility(View.VISIBLE);
        mFpvModeView.mModeViews.recordLottie.loop(false);
        mFpvModeView.mModeViews.recordLottie.setSpeed(1f);
        mFpvModeView.mModeViews.recordLottie.setAnimation(anim);

        mFpvModeView.mModeViews.recordLottie.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mFpvModeView.mModeViews.recordView != null)
                    mFpvModeView.mModeViews.recordView.setVisibility(View.VISIBLE);

                mFpvModeView.mModeViews.recordLottie.cancelAnimation();
                mFpvModeView.mModeViews.recordLottie.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mFpvModeView.mModeViews.recordLottie.playAnimation();
    }

}