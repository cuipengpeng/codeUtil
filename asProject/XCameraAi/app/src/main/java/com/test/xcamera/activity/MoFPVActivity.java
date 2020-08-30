package com.test.xcamera.activity;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.framwork.base.view.MOBaseActivity;
import com.test.xcamera.MoPresenters.MoFPVPresenter;
import com.test.xcamera.R;
import com.test.xcamera.accrssory.AccessoryManager;
import com.effect_opengl.EffectManager;
import com.test.xcamera.bean.BaseData;
import com.test.xcamera.bean.EventMessage;
import com.test.xcamera.bean.FrameRectInfo;
import com.test.xcamera.bean.MoImage;
import com.test.xcamera.bean.MoSettingModel;
import com.test.xcamera.bean.MoShotSetting;
import com.test.xcamera.bean.MoSystemInfo;
import com.test.xcamera.constants.LogcatConstants;
import com.test.xcamera.enumbean.ScreenOrientationType;
import com.test.xcamera.enumbean.ShootMode;
import com.test.xcamera.enumbean.WorkState;
import com.test.xcamera.home.GoUpActivity;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.managers.MoMarkVideoManager;
import com.test.xcamera.managers.ShotModeManager;
import com.test.xcamera.managers.WorkStateManager;
import com.test.xcamera.moalbum.activity.MyAlbumList;
import com.test.xcamera.mointerface.MoErrorCallback;
import com.test.xcamera.mointerface.MoFPVCallback;
import com.test.xcamera.mointerface.MoRequestCallback;
import com.test.xcamera.mointerface.MoSyncSettingCallback;
import com.test.xcamera.mointerface.MoSynvShotSettingCallback;
import com.test.xcamera.mointerface.MoTakeVideoCallback;
import com.test.xcamera.player.AVFrame;
import com.test.xcamera.profession.ProfessionSettingView;
import com.test.xcamera.profession.ProfessionView;
import com.test.xcamera.statistic.StatisticFPVLayer;
import com.test.xcamera.util.ScreenOrientationListener;

import com.test.xcamera.utils.DlgUtils;
import com.test.xcamera.utils.ViewUitls;
import com.test.xcamera.view.AutoTrackingRectViewFix;
import com.test.xcamera.view.MoFPVRockerView;
import com.test.xcamera.view.MoFPVShotControlView;
import com.test.xcamera.view.MoFPVShotSettingView;
import com.test.xcamera.glview.PreviewGLSurfaceView;
import com.test.xcamera.view.MoSelectShotModeView;
import com.test.xcamera.view.OnDecodeListener;
import com.test.xcamera.viewcontrol.MoFPVCountDownControl;
import com.test.xcamera.viewcontrol.MoFPVRsolutionControl;
import com.test.xcamera.viewcontrol.MoFPVScaleControl;
import com.test.xcamera.viewcontrol.MoFPVShotSettingControl;
import com.test.xcamera.viewcontrol.MoFPVSlowMotionControl;
import com.test.xcamera.widget.ActivityContainerHome;
import com.moxiang.common.logging.Logcat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;

import butterknife.BindView;

/**
 * Created by zll on 2019/10/23.
 * <p>
 * FPV主页面 关于页面的参数基本都有回调
 */

public class MoFPVActivity extends MOBaseActivity implements AccessoryManager.PreviewDataCallback, MoFPVCallback,
        ScreenOrientationListener.ChangeOrientationListener {
    public static final String TAG = "MoFPVActivity";
    public static final int START_RECORD_RESP = 100005;
    public static final int STOP_RECORD_RESP = 100007;
    public static final int LONG_EXPLORE_START = 100010;
    public static final int LONG_EXPLORE_STOP = 100011;

    public static final String FINISH_ALBUM = "AlbumListActivity";
    public static final String FINISH_FPV = "back_btn";

    @BindView(R.id.activity_mo_fpv_texture_view)
    PreviewGLSurfaceView mPreviewTextureView;
    @BindView(R.id.activity_mo_fpv_track_rect_view)
    AutoTrackingRectViewFix mAutoTrackRectView;
    @BindView(R.id.activity_mo_fpv_shot_setting_layout)
    MoFPVShotSettingView mShotSettingLayout;
    @BindView(R.id.activity_mo_fpv_shot_control_layout)
    MoFPVShotControlView mShotControlLayout;
    @BindView(R.id.activity_mo_fpv_setting_layout)
    ProfessionView mFPVSettingView;
    @BindView(R.id.rockerView_center)
    MoFPVRockerView mRockerView;
    @BindView(R.id.fragment_preview_count_down_animation)
    LottieAnimationView mAnimationView;

    private View mDlgContent, mDlgLayer;
    private TextView mDlgAffirm, mDlgInfo;
    private ImageView mDlgImg;
    private View mContentView;

    private MoFPVPresenter mFPVPresenter;
    private volatile boolean mIsPause = false;
    private SendNaviControlTread mSendNaviControlRunnable;
    private View mDecorView;
    private boolean mIsFirstSync = false;
    //如果指令开始执行了 则需要执行完
    private long mCompleteFlag;
    private PowerManager.WakeLock mWakeLock;
    private boolean mBatteryState = true;
    private boolean connFlag = true;
    private int mPtzAction = -1;
    private long mRockerTimeFlag;

    //如果fpv页面退到后台时拔掉相机 则finish
    private boolean mStopedFlag = false;
    private boolean mRestartLongExplore = false;
    //判断是否需要调用appFpvMode(0) 接口
    private boolean mIsNeedFpvMode = true;
    //云台的fpv模式时 需要禁止识别 如果再返回其他模式 需要恢复识别状态
    private boolean mIsTracePtzMode = false;

    private boolean mIsFirstShowImg = true;
    private int mRotate = 0;
    private ScreenOrientationListener mOrientationListener;
    private MoSettingModel mShotSettingsEx;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LONG_EXPLORE_START:
                    mAutoTrackRectView.setEnableZoom(false);
                    mShotSettingLayout.startLongExplore();
                    mShotControlLayout.takePhotoStart();

                    if (mAnimationView != null) {
                        mAnimationView.setVisibility(View.VISIBLE);
                        mAnimationView.setAnimation("animation/3_seconds.json");
                        mAnimationView.playAnimation();
                    }
                    break;
                case LONG_EXPLORE_STOP:
                    if (mAutoTrackRectView != null)
                        mAutoTrackRectView.setEnableZoom(true);
                    if (mShotSettingLayout != null)
                        mShotSettingLayout.stopLongExplore();
                    if (mShotControlLayout != null)
                        mShotControlLayout.takePhotoStop();
                    if (mShotControlLayout != null)
                        mShotControlLayout.setThumbnail(msg.obj == null ? null : (MoImage) msg.obj);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initValue();
        init();
        Logcat.e().tag(LogcatConstants.FPV_PREVIEW).msg("==>MoFPVActivity onCreate").out();
        AccessoryManager.mMoFPVActCreateCount++;
        ActivityContainerHome.getInstance().setFPVActivty(new WeakReference<>(this));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initValue();
        init();
        Logcat.e().tag(LogcatConstants.FPV_PREVIEW).msg("==>MoFPVActivity onNewIntent").out();
        AccessoryManager.mMoFPVActCreateCount++;
    }

    private void initValue() {
        mStopedFlag = false;
        WorkStateManager.getInstance().setmWorkState(WorkState.STANDBY);
        WorkStateManager.getInstance().setSwitchMode(false);
    }

    @SuppressLint("InvalidWakeLockTag")
    private void init() {
        mBatteryState = true;
        connFlag = true;
        mAutoTrackRectView.setShotControlView(this.mShotControlLayout);
        mPreviewTextureView.setTrackingRectView(mAutoTrackRectView);
        mFPVSettingView.syncTrackMode(mAutoTrackRectView);
        mPreviewTextureView.setDecodeListener(new OnDecodeListener() {
            @Override
            public void decodeResult(AVFrame avFrame) {
                if (avFrame != null
                        && avFrame.getBaseData() != null
                        && avFrame.getBaseData().getmVideoFrameInfo() != null) {
                    if (mAutoTrackRectView != null) {
                        mAutoTrackRectView.mRotate = avFrame.getBaseData().getmVideoFrameInfo().getRotate();
                        if (avFrame.getBaseData().getmVideoFrameInfo().getmRectCount() > 0)
                            mAutoTrackRectView.setData(avFrame.getBaseData().getmVideoFrameInfo());
                    }
                }
            }
        });

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        if (powerManager != null) {
            mWakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "WakeLock");
        }

        mDecorView = getWindow().getDecorView();
        mAutoTrackRectView.setGLSurfaceView(mPreviewTextureView);
        mRockerView.setOnLocationListener(new MoFPVRockerView.OnLocationListener() {
            @Override
            public void onStart() {
                Log.d(TAG, "onStart: ");
                if (mSendNaviControlRunnable != null) {
                    mSendNaviControlRunnable.startThread();
                }
            }

            @Override
            public void getLocation(int x, int y) {
                Log.d(TAG, "getLocation x: " + x + "  y: " + y);
                if (mSendNaviControlRunnable != null) {
                    mSendNaviControlRunnable.setData(x, y);
                }
            }

            @Override
            public void onFinish() {
                Log.d(TAG, "onFinish: ");
                if (mSendNaviControlRunnable != null) {
                    mSendNaviControlRunnable.stopThread();
                }
            }
        });

        mAnimationView.loop(false);
        mAnimationView.setScale(0.5f);
        mAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mAnimationView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        if (mSendNaviControlRunnable == null) {
            mSendNaviControlRunnable = new SendNaviControlTread();
            mSendNaviControlRunnable.isStop = false;
            mSendNaviControlRunnable.start();
        }

        mContentView = findViewById(R.id.fpv_content);
        mDlgContent = findViewById(R.id.fpv_dlg);
        mDlgLayer = findViewById(R.id.dlg_layer);
        mDlgInfo = findViewById(R.id.info);
        mDlgImg = findViewById(R.id.icon);
        mDlgAffirm = findViewById(R.id.affirm);
        //拦截所有的点击事件
        mDlgLayer.setOnTouchListener((view, event) -> {
            return true;
        });
        clearDlg();

        ActivityContainerHome.getInstance().setFPVClose(() -> {
            clearDlg();
        });
    }

    /**
     * 异常中止拍摄
     */
    private void abortRecording(MoImage image) {
        if (WorkStateManager.getInstance().getmWorkState() == WorkState.RECORDING)
            takeVideoResult(image);
        else if (WorkStateManager.getInstance().getmWorkState() == WorkState.PHOTOING) {
            runOnUiThread(() -> {
                if (mAnimationView != null && mAnimationView.getVisibility() == View.VISIBLE) {
                    mAnimationView.cancelAnimation();
                    mAnimationView.setVisibility(View.GONE);
                }
            });

            takePhotoResult(image);
        }
    }

    private void syncThumbnail() {
        ConnectionManager.getInstance().albumCount(new ConnectionManager.AlbumCountListener() {
            @Override
            public void onSuccess(int count, MoImage thumbnail) {
                Log.e("=====", "thumbnail==>" + thumbnail.toString());
                runOnUiThread(() -> {
                    if (mShotControlLayout != null)
                        mShotControlLayout.setThumbnail(thumbnail);
                });
            }

            @Override
            public void onFailed() {
                runOnUiThread(() -> {
                    if (mShotControlLayout != null)
                        mShotControlLayout.setThumbnail(null);
                });
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //onRestart时 需要重置所有状态
        init();
    }

    @Override
    protected void onStart() {
        setDecorViewState();
        super.onStart();

        mOrientationListener = new ScreenOrientationListener(this, this);
        if (mOrientationListener.canDetectOrientation()) {
            Log.v(TAG, "Can detect orientation");
            mOrientationListener.enable();
        } else {
            Log.v(TAG, "Cannot detect orientation");
            mOrientationListener.disable();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Logcat.e().tag(LogcatConstants.FPV_PREVIEW).msg("==>MoFPVActivity onStop").out();
        mIsPause = true;
        mStopedFlag = true;
        mIsFirstShowImg = true;
        mPreviewTextureView.destoryOpengl();
        mPreviewTextureView.release();

        clearDlg();

        if (mSendNaviControlRunnable != null) {
            mSendNaviControlRunnable.isStop = true;
            mSendNaviControlRunnable = null;
        }

        if (mOrientationListener != null) {
            mOrientationListener.disable();
        }

        if (WorkStateManager.getInstance().getmWorkState() == WorkState.STANDBY && mIsNeedFpvMode) {
            ConnectionManager.getInstance().appFpvMode(0, new MoRequestCallback() {
                @Override
                public void onSuccess() {

                }
            });
        }
        //退出即取消 防止发送多余的消息
        mFPVPresenter.stopRefrushVideo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityContainerHome.getInstance().setFPVActivty(null);
        MoFPVRsolutionControl.getInstance().release();
        MoFPVCountDownControl.getInstance().release();
        MoFPVShotSettingControl.getInstance().release();
        MoFPVSlowMotionControl.getInstance().release();
        MoFPVScaleControl.getInstance().release();
        if (mShotControlLayout != null)
            mShotControlLayout.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWakeLock != null) {
            mWakeLock.acquire();
        }
        EventBus.getDefault().register(this);
        DlgUtils.mRotate = mRotate;

        mStopedFlag = false;
        mIsPause = false;
        mIsFirstSync = false;
        mIsNeedFpvMode = true;

        errUpload();
        Log.e("=====", "onResume  mAoaActFlag==>fpv");
        if (AccessoryManager.getInstance().mIsRunning) {
            mCompleteFlag = System.currentTimeMillis();
            ConnectionManager.getInstance().appFpvMode(1, new MoRequestCallback() {
                @Override
                public void onSuccess() {
                    mFPVPresenter.syncShotSettings();
                }

                @Override
                public void onFailed() {
                    mFPVPresenter.syncShotSettings();
                }
            });
        }

        if (!AccessoryManager.getInstance().mIsRunning) {
            showDlg(R.string.dlg_disconnect, R.mipmap.icon_discon_camera, () -> {
                wakeUpGoUp();
            });
        }

        Logcat.e().tag(LogcatConstants.FPV_PREVIEW).msg("==>MoFPVActivity onResume").out();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mPreviewTextureView.getLayoutParams();
            lp.setMargins(0, 0, 0, -ViewUitls.dp2px(this, 5));
            mPreviewTextureView.setLayoutParams(lp);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWakeLock != null) {
            mWakeLock.release();
        }
        EventBus.getDefault().unregister(this);
        Logcat.e().tag(LogcatConstants.FPV_PREVIEW).msg("==>MoFPVActivity onPause").out();
        mIsPause = true;
        mFPVPresenter.stoPreview();

        DlgUtils.mRotate = 0;
    }

    @Override
    public int initView() {
        return R.layout.activity_mo_fpv;
    }

    @Override
    public void initData() {
        AccessoryManager.getInstance().setPreviewDataCallback(this);

        mFPVPresenter = new MoFPVPresenter(this);

        mShotSettingLayout.setMoFPVCallback(this);
        mShotSettingLayout.setMoFPVPresenter(mFPVPresenter);
        mShotControlLayout.setShotSettingLayout(mShotSettingLayout);
        mShotControlLayout.setMoFPVCallback(this);
        mShotControlLayout.setMoFPVPresenter(mFPVPresenter);

        //跟踪框回调
        mAutoTrackRectView.setTrackInterface(new AutoTrackingRectViewFix.TrackInterface() {

            @Override
            public void onStartAutoTrack() {
                if (mShotControlLayout != null && mShotControlLayout.mTrackImage != null && longExporeTrace()) {
                    mShotControlLayout.mTrackImage.setSelected(true);
                    mIsTracePtzMode = true;
                }

                reactFlag = true;
                reactCount = -1;
            }

            @Override
            public void onStartTrackSingle() {
                if (mShotControlLayout != null && mShotControlLayout.mTrackImage != null && longExporeTrace()) {
                    mShotControlLayout.mTrackImage.setSelected(true);
                    mIsTracePtzMode = true;
                }
                reactFlag = true;
                reactCount = -1;
            }

            @Override
            public void onStopTrackSingle() {
                mRockerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStopTrack() {
                if (mShotControlLayout != null && mShotControlLayout.mTrackImage != null)
                    mShotControlLayout.mTrackImage.setSelected(false);
                reactFlag = false;
                mIsTracePtzMode = false;
            }

            @Override
            public void onStartAreaTrack() {
                if (mShotControlLayout != null && mShotControlLayout.mTrackImage != null && longExporeTrace()) {
                    mShotControlLayout.mTrackImage.setSelected(true);
                    mIsTracePtzMode = true;
                }

                reactFlag = true;
                reactCount = -1;
            }

            @Override
            public void onFailed(AutoTrackingRectViewFix.Mode mode) {
            }
        });

//        //初始化,美摄管理类
        EffectManager.instance().initEffect(this);
    }

    private void setDecorViewState() {
        int flag = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        if (Build.VERSION.SDK_INT >= 19)
            mDecorView.setSystemUiVisibility(flag);
    }


    @Override
    public void requestSuccess() {

    }

    @Override
    public void requestFailed() {

    }

    @Override
    public void requestFailed(int state) {
        mShotControlLayout.cancelPhoto(state);
        takePhotoIcon(true);
        if (ShotModeManager.getInstance().getmShootMode() == ShootMode.LONG_EXPLORE) {
            mHandler.removeMessages(LONG_EXPLORE_START);
            mHandler.sendEmptyMessage(LONG_EXPLORE_STOP);
            if (mRestartLongExplore) {
                mRestartLongExplore = false;
                mFPVPresenter.startPreview();
            }
        }
        if (state == MoFPVCallback.PHOTO_STOP_FAILED) {
            runOnUiThread(() -> {
                if (mAnimationView != null) {
                    mAnimationView.setTag(false);
                    mAnimationView.cancelAnimation();
                    mAnimationView.setVisibility(View.GONE);
                }
            });

            takePhotoResult(null);
        }
    }

    @Override
    public void requestReason(int reason) {
        runOnUiThread(() -> {
            switch (reason) {
                case MoErrorCallback.SD_OUT:
                    DlgUtils.toast(this, getStr(R.string.sdcard_out), mRotate);
                    return;
                case MoErrorCallback.SD_LOW:
                    DlgUtils.toast(MoFPVActivity.this, getResources().getString(R.string.sdcard_low), mRotate);
                    return;
                case MoErrorCallback.SD_FULL:
                    DlgUtils.toast(MoFPVActivity.this, getResources().getString(R.string.sdcard_status_full), mRotate);
                    return;
            }
        });
    }

    @Override
    public void startPreviewSuccess() {
        mFPVPresenter.syncSystemInfo();

        if (mIsFirstShowImg) {
            mIsFirstShowImg = false;
            ConnectionManager.getInstance().albumCount(new ConnectionManager.AlbumCountListener() {
                @Override
                public void onSuccess(int count, MoImage thumbnail) {
                    if (mShotControlLayout != null)
                        mShotControlLayout.setThumbnail(thumbnail);
                }

                @Override
                public void onFailed() {
                    if (mShotControlLayout != null)
                        mShotControlLayout.setThumbnail(null);
                }
            });
        }
    }

    @Override
    public void syncShotSettings(MoShotSetting shotSetting) {
        runOnUiThread(() -> {
            if (!mStopedFlag)
                syncSetting(shotSetting);
        });
    }

    private void syncSetting(MoShotSetting shotSetting) {
        mRestartLongExplore = false;
        boolean isRecording = (ShotModeManager.getInstance().isVideo()
                && shotSetting.getmMoRecordSetting().getProgress() == 1);
        boolean isPhotoing = (ShotModeManager.getInstance().isPhoto(shotSetting.getmMode())
                && shotSetting.getmMoSnapShotSetting().getProgress() == 1);

        //1、如果相机正在录像中连接app 则先停止录像
        //2、mConnectCount用于判断 如果只是息屏 则不用停止录像
        //3、mMoFPVActCreateCount用于判断 如果按home键返回桌面 计时会被销毁 需要停录像
        if (isRecording || isPhotoing) {
            if (AccessoryManager.mConnectCount > 0
                    || AccessoryManager.mMoFPVActCreateCount > 0) {
                if (isRecording) {
                    ConnectionManager.getInstance().takeVideoStop(new MoTakeVideoCallback() {
                        @Override
                        public void onSuccess(MoImage image) {
                            runOnUiThread(() -> {
                                if (mShotControlLayout != null)
                                    mShotControlLayout.setThumbnail(image);
                                syncSetting(shotSetting, true);
                            });
                        }

                        @Override
                        public void onFailed() {

                        }
                    });
                }
            } else {
                if (isRecording)
                    mFPVPresenter.restartRefrushVideo();
                if (isPhotoing && shotSetting.getmMode() == ShootMode.LONG_EXPLORE)
                    mRestartLongExplore = true;

                syncSetting(shotSetting, false);
            }
        } else {
            syncSetting(shotSetting, true);
        }
    }

    private void syncSetting(MoShotSetting shotSetting, boolean reset) {
        if (mShotSettingLayout == null) {
            return;
        }

        if (reset)
            mShotSettingLayout.syncShotSetting(shotSetting);
        mShotControlLayout.mZoomLayout.setTag(true);
        mAutoTrackRectView.setEnable(true);
        mAutoTrackRectView.setEnableZoom(true);
        mShotControlLayout.mZoomLayout.setAlpha(1f);

        //同步变焦
        int zoom = ShotModeManager.getInstance().isVideo()
                ? shotSetting.getmMoRecordSetting().getZoom()
                : shotSetting.getmMoSnapShotSetting().getZoom();
        mShotControlLayout.mZoomLevel = zoom;
        mShotControlLayout.mZoomText.setText((zoom / 10f) + "X");
        mShotControlLayout.mZoomText.setTag(zoom);

        //录像模式 4k 60fps 变焦不可用
        int resol = ShotModeManager.getInstance().isVideo()
                ? shotSetting.getmMoRecordSetting().getmResolution()
                : shotSetting.getmMoSnapShotSetting().getmResolution();
        int rate = ShotModeManager.getInstance().isVideo()
                ? shotSetting.getmMoRecordSetting().getmFrameRate()
                : shotSetting.getmMoSnapShotSetting().getmFrameRate();
        enableMode(resol, rate);

        //慢动作8x模式不支持跟踪
        if (ShotModeManager.getInstance().getmShootMode() == ShootMode.SLOW_MOTION) {
            setSlowMotionValue(shotSetting.getmMoRecordSetting().getmSpeed(), false);
            mShotControlLayout.speed = shotSetting.getmMoRecordSetting().getmSpeed();
        }

        //同步自动跟踪
        int track = ShotModeManager.getInstance().isVideo()
                ? shotSetting.getmMoRecordSetting().getTrackStatus()
                : shotSetting.getmMoSnapShotSetting().getTrackStatus();
        mShotControlLayout.mTrackImage.setSelected(longExporeTrace() && track == 1);
        mIsTracePtzMode = longExporeTrace() && track == 1;
        if (track == 1) {
            mAutoTrackRectView.isStopFlag = false;
            mAutoTrackRectView.setNormal();
            Logcat.v().tag(LogcatConstants.FPV_PREVIEW).msg("mAutoTrackRectView.setNormal()").out();
            reactFlag = true;
            reactCount = -1;
        } else {
            mAutoTrackRectView.isStopFlag = true;
        }

        if (!mIsPause && mPreviewTextureView != null && !mIsFirstSync) {
            mIsFirstSync = true;
            AccessoryManager.getInstance().setPreviewDataCallback(this);
            Log.e("=====", "syncShotSettings  ==>restartCodec");
            mPreviewTextureView.reset();
        }

        //同步美颜按钮
        int beauty = ShotModeManager.getInstance().isVideo()
                ? shotSetting.getmMoRecordSetting().getBeauty()
                : shotSetting.getmMoSnapShotSetting().getBeauty();
        MoFPVShotSettingControl.getInstance().setBeautyStatus(beauty == 1);
        //拍照模式 录像模式1080x30/24 可用
        boolean beautyAble = ShotModeManager.getInstance().getmShootMode() == ShootMode.PHOTO
                || (ShotModeManager.getInstance().getmShootMode() == ShootMode.VIDEO
                && (rate == 1 || rate == 2) && resol == 4);
        MoFPVShotSettingControl.getInstance().setBeautyAble(beautyAble);

        mFPVPresenter.startPreview();

        //切换模式完成
        WorkStateManager.getInstance().setSwitchMode(false);

        //获取画幅比 用于双击跟踪时 改变上传图片的大小
        MoFPVScaleControl.getInstance().mScale = ShotModeManager.getInstance().isVideo()
                ? shotSetting.getmMoRecordSetting().getmProportion()
                : shotSetting.getmMoSnapShotSetting().getmProportion();
    }

    private void syncFps(MoShotSetting shotSetting) {
        mShotControlLayout.mZoomLayout.setTag(true);
        mAutoTrackRectView.setEnable(true);
        mAutoTrackRectView.setEnableZoom(true);
        mShotControlLayout.mZoomLayout.setAlpha(1f);

        //同步变焦
        int zoom = ShotModeManager.getInstance().isVideo()
                ? shotSetting.getmMoRecordSetting().getZoom()
                : shotSetting.getmMoSnapShotSetting().getZoom();
        mShotControlLayout.mZoomLevel = zoom;
        mShotControlLayout.mZoomText.setText((zoom / 10f) + "X");
        mShotControlLayout.mZoomText.setTag(zoom);

        //录像模式 4k 60fps 变焦不可用
        int resol = ShotModeManager.getInstance().isVideo()
                ? shotSetting.getmMoRecordSetting().getmResolution()
                : shotSetting.getmMoSnapShotSetting().getmResolution();
        int rate = ShotModeManager.getInstance().isVideo()
                ? shotSetting.getmMoRecordSetting().getmFrameRate()
                : shotSetting.getmMoSnapShotSetting().getmFrameRate();
        enableMode(resol, rate);

        //同步自动跟踪
        int track = ShotModeManager.getInstance().isVideo()
                ? shotSetting.getmMoRecordSetting().getTrackStatus()
                : shotSetting.getmMoSnapShotSetting().getTrackStatus();
        mShotControlLayout.mTrackImage.setSelected(longExporeTrace() && track == 1);
        mIsTracePtzMode = longExporeTrace() && track == 1;
        if (track == 1) {
            mAutoTrackRectView.isStopFlag = false;
            mAutoTrackRectView.setNormal();
            Logcat.v().tag(LogcatConstants.FPV_PREVIEW).msg("mAutoTrackRectView.setNormal()").out();
            reactFlag = true;
            reactCount = -1;
        }
    }

    @Override
    public void syncShotSettingsEx(MoSettingModel shotSetting) {
        this.mShotSettingsEx = shotSetting;
        if (mFPVSettingView != null)
            mFPVSettingView.syncMode(shotSetting);
    }

    private boolean longExporeTrace() {
        return ShotModeManager.getInstance().getmShootMode() != ShootMode.LONG_EXPLORE;
    }

    @Override
    public void restartMediaCodec() {

    }

    @Override
    public void showMoreSetting() {
        Log.d(TAG, "chooseBeautyType: ");
        StatisticFPVLayer.getInstance().setOnEvent(StatisticFPVLayer.FloatLayer_Capture_Setting);

        if (mFPVSettingView.getVisibility() == View.VISIBLE) {
            mShotControlLayout.setVisibility(View.VISIBLE);
            mFPVSettingView.setVisibility(View.GONE);
        } else {
            int mode = 1;
            switch (ShotModeManager.getInstance().getmShootMode()) {
                case PHOTO:
                    mode = 1;
                    break;
                case LONG_EXPLORE:
                    mode = 2;
                    break;
                case VIDEO:
                    mode = 3;
                    break;
                case SLOW_MOTION:
                    mode = 4;
                    break;
                case TRACKLASPEVIDEO:
                    mode = 5;
                    break;
                case LAPSE_VIDEO:
                    mode = 6;
                    break;
                case PHOTO_BEAUTY:
                    mode = 7;
                    break;
                case VIDEO_BEAUTY:
                    mode = 8;
                    break;
                default:
                    mode = 3;
                    break;
            }

            mFPVSettingView.setMode(CameraMode.getMode(mode));
            mShotControlLayout.setVisibility(View.VISIBLE);
            mFPVSettingView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void selectLapseMode(boolean needSendCmd) {
        MoFPVShotSettingControl.getInstance().setLapseMode();
        if (mShotControlLayout != null) {
            mShotControlLayout.setShotIcon(R.mipmap.icon_lapse_shot);
            mShotControlLayout.changeMode(MoSelectShotModeView.LAPSE);
        }

        if (needSendCmd) {
            mFPVPresenter.changeShotMode("lapsevideo");
        }

        StatisticFPVLayer.getInstance().setOnEvent(StatisticFPVLayer.FloatLayer_Capture_Lapse);
    }

    @Override
    public void selectSlowMotionMode(boolean needSendCmd) {
        MoFPVShotSettingControl.getInstance().setSlowMotionMode();
        if (mShotControlLayout != null) {
            mShotControlLayout.setShotIcon(R.mipmap.icon_slowmotion_shot);
            mShotControlLayout.changeMode(MoSelectShotModeView.SLOW_MOTION);
        }

        if (needSendCmd) {
            mFPVPresenter.changeShotMode("slowmotionvideo");
        }
        StatisticFPVLayer.getInstance().setOnEvent(StatisticFPVLayer.FloatLayer_Capture_SlowMotion);
    }

    @Override
    public void selectVideoMode(boolean needSendCmd) {
        MoFPVShotSettingControl.getInstance().setVideoMode();
        if (mShotControlLayout != null) {
            mShotControlLayout.setShotIcon(R.mipmap.icon_record);
            mShotControlLayout.changeMode(MoSelectShotModeView.VIDEO);
        }

        if (needSendCmd) {
            mFPVPresenter.changeShotMode("video");
        }
        StatisticFPVLayer.getInstance().setOnEvent(StatisticFPVLayer.FloatLayer_Capture_Video);
    }

    @Override
    public void selectStoryMode(boolean needSendCmd) {
    }

    @Override
    public void selectPhotoMode(boolean needSendCmd) {
        MoFPVShotSettingControl.getInstance().setPhotoMode();
        if (mShotControlLayout != null) {
            mShotControlLayout.setShotIcon(R.mipmap.icon_shot);
            mShotControlLayout.changeMode(MoSelectShotModeView.PHOTO);
        }

        if (needSendCmd) {
            mFPVPresenter.changeShotMode("photo");
        }
        StatisticFPVLayer.getInstance().setOnEvent(StatisticFPVLayer.FloatLayer_Capture_Picture);
    }

    @Override
    public void selectLongExploreMode(boolean needSendCmd) {
        MoFPVShotSettingControl.getInstance().setLongExploreMode();
        if (mShotControlLayout != null) {
            mShotControlLayout.setShotIcon(R.mipmap.icon_shot);
            mShotControlLayout.changeMode(MoSelectShotModeView.LONG_EXPLORE);
        }

        if (needSendCmd) {
            mFPVPresenter.changeShotMode("longexplorephoto");
        }
        StatisticFPVLayer.getInstance().setOnEvent(StatisticFPVLayer.FloatLayer_Capture_LongExposure);
    }

    @Override
    public void selectBeauty(boolean needSendCmd) {

    }

    @Override
    public void chooseBeautyType() {

    }

    @Override
    public void changeShotModeSuccess() {
        mFPVPresenter.syncShotSettings();
    }

    @Override
    public void setLapseValue(String value) {
        if (!TextUtils.isEmpty(value))
            mShotControlLayout.mLapseValue = Float.parseFloat(value);
    }

    @Override
    public void disconnectedUSB() {
        super.disconnectedUSB();
        if (mStopedFlag)
            this.finish();

        if (connFlag) { //防止多次调用
            connFlag = false;
            runOnUiThread(() -> {
                abortRecording(null);
                if (!mIsPause)
                    showDlg(R.string.dlg_disconnect, R.mipmap.icon_discon_camera, () -> {
                        wakeUpGoUp();
                    });
            });
        }
    }

    @Override
    public void startTakeVideoSuccess(String uri, int reason) {
        if (mShotControlLayout != null)
            mShotControlLayout.takeVideoStart(uri);
        if (mShotSettingLayout != null)
            mShotSettingLayout.startTakeVideo();
        AccessoryManager.mConnectCount = 0;
        AccessoryManager.mMoFPVActCreateCount = 0;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    public void takePhotoResult(final MoImage image) {
        if (!mRestartLongExplore)
            mFPVPresenter.syncSystemInfo();

        ShootMode shootMode = ShotModeManager.getInstance().getmShootMode();
        if (shootMode == ShootMode.LONG_EXPLORE) {
            Message msg = Message.obtain();
            msg.what = LONG_EXPLORE_STOP;
            msg.obj = image;
            mHandler.sendMessage(msg);

            if (mRestartLongExplore) {
                mRestartLongExplore = false;
                if (mFPVPresenter != null)
                    mFPVPresenter.startPreview();
            }
        } else {
            runOnUiThread(() -> {
                if (mShotControlLayout != null)
                    mShotControlLayout.setThumbnail(image);
            });
        }
        takePhotoIcon(true);
        if (mShotControlLayout != null)
            mShotControlLayout.cancelPhoto(MoFPVCallback.PHOTO_STOP);
    }

    public void takePhotoIcon(boolean enable) {
        runOnUiThread(() -> {
            if (ShotModeManager.getInstance().getmShootMode() == ShootMode.PHOTO) {
                MoFPVShotSettingControl.getInstance().takePhotoIcon(enable);
                mAutoTrackRectView.setEnable(enable);
                mAutoTrackRectView.setEnableZoom(enable);
                mShotControlLayout.mZoomLayout.setAlpha(enable ? 1f : 0.5f);
                mShotControlLayout.enablePTZ(enable);
                //变焦操作被禁 如果不拦截点击  多次点击会造成双击跟踪
                if (enable) {
                    MoFPVShotSettingControl.getInstance().setBeautyIcon();
                    mShotControlLayout.mZoomLayout.setOnTouchListener(null);
                } else {
                    MoFPVShotSettingControl.getInstance().setBeautyEnable();
                    mShotControlLayout.mZoomLayout.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return true;
                        }
                    });
                }
            }
        });
    }

    @Override
    public void takeVideoResultImm() {
        mShotControlLayout.takeVideoStopImm();
    }

    @Override
    public void takeVideoResult(MoImage image) {
        if (mShotControlLayout != null)
            mShotControlLayout.takeVideoStop(image);
        if (mShotSettingLayout != null)
            mShotSettingLayout.stopTakeVideo();
        //同步内存信息
        if (mFPVPresenter != null)
            mFPVPresenter.syncSystemInfo();
    }

    @Override
    public void startFingerTrack(final int[] rectInfo) {
    }

    @Override
    public void getSystemInfo(MoSystemInfo systemInfo) {
        if (mShotSettingLayout != null)
            mShotSettingLayout.setSystemInfo(systemInfo);
        if (mShotControlLayout != null) {
            mShotControlLayout.recordViewStatus = systemInfo.getSdStatus();
            mShotControlLayout.sdStatus = systemInfo.getmSDCardCapacity();
        }
        if (mFPVSettingView != null)
            mFPVSettingView.syncPtzMode(systemInfo.getPtzMode(), systemInfo.getPtzSensitivity());

        enableTrack(systemInfo.getPtzMode());
        if (mAutoTrackRectView != null)
            mAutoTrackRectView.enableTracingMode = systemInfo.getPtzMode();
    }

    @Override
    public void onFinish(String type) {
        switch (type) {
            case FINISH_ALBUM:
                mIsNeedFpvMode = false;
//                Intent intent = new Intent(this, AlbumListActivity.class);
//                intent.putExtra("jump_flag", "fpv");
//                startActivity(intent);
                MyAlbumList.openThis(this, "fpv");
                this.finish();
                break;
            //返回按钮时 会进入Goup页面 会调用fpvmode=0 防止冲突
            case FINISH_FPV:
                //防止指令未执行完成就退出
                if (System.currentTimeMillis() - mCompleteFlag > 1500) {
                    mIsNeedFpvMode = false;
                    wakeUpGoUp();
                }
                break;
            default:
                wakeUpGoUp();
                break;
        }
    }

    @Override
    public void onVideoDataAvailable(BaseData baseData) {
        if (mIsPause || mPreviewTextureView == null)
            return;

        AVFrame avFrame = new AVFrame(baseData, baseData.getmDataSize(), false, false);
        mPreviewTextureView.onReceived(avFrame);
        long time = baseData.getmVideoFrameInfo().getmTimeTs();
        MoMarkVideoManager.getInstance().setCurrentTime(time);

        if (mAutoTrackRectView != null) {
            if (videoWidth != avFrame.getVideoWidth() || videoHeight != avFrame.getVideoHeight()
                    || mAutoTrackRectView.videoWidthFix == 0) {
                videoWidth = avFrame.getVideoWidth();
                videoHeight = avFrame.getVideoHeight();
                runOnUiThread(() -> {
                    mAutoTrackRectView.changeSize(avFrame.getVideoWidth(), avFrame.getVideoHeight());
                });
            }
        }

        //如果有跟踪框或者正在拍照 则隐藏万向键 否则显示
        boolean isTracing = false;
        if (avFrame.getBaseData().getmVideoFrameInfo().getmRectCount() > 0)
            for (FrameRectInfo info : avFrame.getBaseData().getmVideoFrameInfo().getmFrameRectInfos())
                if (info.getStatus() == AutoTrackingRectViewFix.TRACE
                        || info.getStatus() == AutoTrackingRectViewFix.TRACE_FIND) {
                    isTracing = true;
                    break;
                }
        if (mRockerView != null)
            if ((isTracing
                    || (WorkStateManager.getInstance().getmWorkState() == WorkState.PHOTOING))
                    && (mRockerView.getVisibility() == View.VISIBLE))
                runOnUiThread(() -> {
                    tracingCount = 0;
                    mRockerView.setVisibility(View.GONE);
                });
            else if (((!isTracing)
                    && (WorkStateManager.getInstance().getmWorkState() != WorkState.PHOTOING))
                    && (mRockerView.getVisibility() != View.VISIBLE)) {
                //如果跟踪框消失8帧 300毫秒以上 再显示跟踪框 防止闪烁
                if (tracingCount++ > 10)
                    runOnUiThread(() -> {
                        mRockerView.setVisibility(View.VISIBLE);
                    });
            } else
                tracingCount = 0;

        if (mAutoTrackRectView != null)
            mAutoTrackRectView.setMirror(avFrame.getMirror() == 1);

    }


    private volatile int videoWidth, videoHeight;
    private volatile int reactCount = -1;
    private volatile int reactCountOld = -1;
    private boolean reactFlag = false;
    private int rectSelect = 0, reactCountReal;
    private int tracingCount = 0;

    //测试跟踪框问题 临时用
    private void checkTrack(AVFrame avFrame) {
        rectSelect = 0;
        reactCountReal = 0;
        //添加跟踪框 防止延时过大
        if (avFrame.getBaseData() != null
                && avFrame.getBaseData().getmVideoFrameInfo() != null
                && avFrame.getBaseData().getmVideoFrameInfo().getmRectCount() > 0) {
            reactCountOld = avFrame.getBaseData().getmVideoFrameInfo().getmRectCount();
            Log.e("=====", "-->" + avFrame.getBaseData().getmVideoFrameInfo().getmFrameRectInfos().toString());
            //计算橙色框个数
            for (FrameRectInfo info : avFrame.getBaseData().getmVideoFrameInfo().getmFrameRectInfos()) {
                reactCountReal++;
                if (info.getStatus() == 1)
                    rectSelect++;
            }
        } else {
            reactCountOld = 0;
        }

        runOnUiThread(() -> {
//            reactFlag = false;
            String info = "";
            if (reactFlag) {
                reactCount = reactCountOld;
                if (mShotSettingLayout.mRectCount != null) {
                    mShotSettingLayout.mRectCount.setVisibility(View.VISIBLE);

                    info = "跟踪框个数:" + reactCount + "/" + reactCountReal + "  \n橙色跟踪框:" + rectSelect;
                }
            }
            mShotSettingLayout.mRectCount.setText(info + " \n状态:" + (mAutoTrackRectView.isStopFlag ? "停止" : "开启"));
        });
    }

    @Override
    public void onAudioDataAvailable(BaseData baseData) {
        if (mIsPause || mPreviewTextureView == null) {
            return;
        }
        mPreviewTextureView.onReceivedAudio(baseData.getmAudioFrameInfo());
    }

    @Override
    public void changeDelayTime(int delayTime) {
        mShotControlLayout.setDelayTime(delayTime);
    }

    @Override
    public void takeDelayPhoto() {
        mShotSettingLayout.takeDelayPhoto();
        if (!TextUtils.isEmpty(mShotSettingLayout.countDown)) {
            mAnimationView.setTag(true);
            mAnimationView.setAnimation(mShotSettingLayout.countDown);
            mAnimationView.playAnimation();
            handler.postDelayed(() -> {
                if ((boolean) mAnimationView.getTag())
                    mAnimationView.setVisibility(View.VISIBLE);
            }, 200);
        }
        takePhotoIcon(false);
    }

    @Override
    public void takePhotoStart() {
        if (ShotModeManager.getInstance().getmShootMode() == ShootMode.LONG_EXPLORE) {
            mHandler.sendEmptyMessageDelayed(LONG_EXPLORE_START, 500);
        }
    }

    @Override
    public void hideControlNavi(int visibility) {
    }

    @Override
    public void startMoreSettingActivity() {
        mFPVPresenter.startMoreSettingActivity(this);
    }

    @Override
    public void changeExploreVisibility(int visibility) {
        mShotSettingLayout.setExploreVisibility(visibility);
    }

    @Override
    public void setExploreValue(String value) {
        mShotSettingLayout.setExploreValue(value);
    }

    @Override
    public void stopAutoTrack() {
        mAutoTrackRectView.stopDraw();
    }

    @Override
    public void startAutoTrack() {
        if (mShotControlLayout.mZoomLayout.getTag() != null
                && !(boolean) mShotControlLayout.mZoomLayout.getTag()) {
            if (ShotModeManager.getInstance().getmShootMode() == ShootMode.SLOW_MOTION)
                DlgUtils.toast(this, getStr(R.string.trace_err_8x), mRotate);
            else if (ShotModeManager.getInstance().getmShootMode() == ShootMode.VIDEO)
                DlgUtils.toast(this, getStr(R.string.trace_err_4k60fps), mRotate);
            else
                DlgUtils.toast(this, getStr(R.string.trace_err_mode), mRotate);
            return;
        }
        mAutoTrackRectView.startDraw();
    }

    @Override
    public void setResolFps(int resolution, int fps) {
        enableMode(resolution, fps);

        if (ShotModeManager.getInstance().getmShootMode() == ShootMode.VIDEO) {
            ConnectionManager.getInstance().syncShotSetting(new MoSynvShotSettingCallback() {

                @Override
                public void onSuccess(MoShotSetting shotSetting) {
                    runOnUiThread(() -> {
                        syncFps(shotSetting);
                    });

                    ConnectionManager.getInstance().startPreview(new MoRequestCallback() {
                        @Override
                        public void onSuccess() {
                            MoFPVRsolutionControl.getInstance().refresh();
                        }

                        @Override
                        public void onFailed() {
                            MoFPVRsolutionControl.getInstance().refresh();
                        }
                    });
                }

                @Override
                public void onFailed() {
                    ConnectionManager.getInstance().startPreview(new MoRequestCallback() {
                        @Override
                        public void onSuccess() {
                            MoFPVRsolutionControl.getInstance().refresh();
                        }

                        @Override
                        public void onFailed() {
                            MoFPVRsolutionControl.getInstance().refresh();
                        }
                    });
                }
            }, new MoSyncSettingCallback() {
                @Override
                public void onSuccess(MoSettingModel model) {
                    runOnUiThread(() -> {
                        mFPVSettingView.syncMode(model);
                    });
                }
            });
        }

    }

    @Override
    public void setSlowMotionValue(int mSpeed, boolean refresh) {
        if (ShotModeManager.getInstance().getmShootMode() == ShootMode.SLOW_MOTION) {
            //慢动作 8X 不支持跟踪、缩放
            mAutoTrackRectView.setEnable(mSpeed != 8);
            mShotControlLayout.mZoomLayout.setTag(mSpeed != 8);
            mAutoTrackRectView.setEnableZoom(mSpeed != 8);
            mShotControlLayout.speed = mSpeed;
            //如果从8X切换为其他模式 需要同步跟踪框
            Log.e("=====", "setSlowMotionValue==>" + mSpeed);
            if (refresh) {
                ConnectionManager.getInstance().syncShotSetting(new MoSynvShotSettingCallback() {

                    @Override
                    public void onSuccess(MoShotSetting shotSetting) {
                        runOnUiThread(() -> {
                            syncFps(shotSetting);

                            if (mSpeed == 8) {
                                mAutoTrackRectView.stopTrack();
                                mShotControlLayout.mZoomLayout.setTag(false);
                                mShotControlLayout.mZoomLayout.setAlpha(0.5f);
                                mAutoTrackRectView.setEnableZoom(false);
                                resetZoom();
                            }
                        });

                        MoFPVSlowMotionControl.getInstance().startPrew();
                    }

                    @Override
                    public void onFailed() {
                        MoFPVSlowMotionControl.getInstance().startPrew();
                    }
                });
                //不同的慢动作参数对应不同的快门
            }
            if (mFPVSettingView != null && mShotSettingsEx != null && mShotSettingsEx.videoModel != null) {
                ConnectionManager.getInstance().syncSetting(new MoSyncSettingCallback() {
                    @Override
                    public void onSuccess(MoSettingModel model) {
                        runOnUiThread(() -> {
                            mShotSettingsEx = model;
                            mFPVSettingView.syncMode(mShotSettingsEx);
                        });
                    }
                });
            }

            if (mSpeed == 8) {
                mAutoTrackRectView.stopTrack();
                mShotControlLayout.mZoomLayout.setAlpha(0.5f);
                mShotControlLayout.mZoomLayout.setTag(false);
                mAutoTrackRectView.setEnableZoom(false);
                resetZoom();
            }
        }
    }

    private void enableMode(int resolution, int fps) {
        //长曝光模式不支持跟踪
        if (ShotModeManager.getInstance().getmShootMode() == ShootMode.LONG_EXPLORE) {
            mAutoTrackRectView.setEnable(false);
            return;
        }
        //4k 60fps 不支持跟踪、变焦
        if (ShotModeManager.getInstance().getmShootMode() == ShootMode.VIDEO) {
            boolean enable = !(resolution == 2/*4k*/ && fps == 4/*60帧*/);
            mShotControlLayout.mZoomLayout.setTag(enable);
            mAutoTrackRectView.setEnable(enable);
            mAutoTrackRectView.setEnableZoom(enable);
            if (!enable)
                resetZoom();

            if (!enable) {
                mAutoTrackRectView.stopTrack();
                mShotControlLayout.mZoomLayout.setAlpha(0.5f);
            }
        }
    }

    private void resetZoom() {
        if (mShotControlLayout != null) {
            mShotControlLayout.mZoomLevel = 10;
            mShotControlLayout.setTag(10);
            mShotControlLayout.mZoomText.setText("1.0X");
        }
    }

    @Override
    public void orientationChanged(ScreenOrientationType type) {
        Log.d(TAG, "portrait: ");
        if (type == ScreenOrientationType.PORTRAIT)
            mRotate = -90;
        else if (type == ScreenOrientationType.LANDSCAPE)
            mRotate = 0;
        else if (type == ScreenOrientationType.REVERSE_PORTRAIT)
            mRotate = 90;
        DlgUtils.mRotate = mRotate;

        //增加了页面倒置的UI
        if ((type == ScreenOrientationType.PORTRAIT || type == ScreenOrientationType.LANDSCAPE)
                && mContentView.getRotation() != 0) {
            mContentView.setRotation(0);
            mAutoTrackRectView.setOrien(0);
        } else if (type == ScreenOrientationType.REVERSE_PORTRAIT && mContentView.getRotation() != 180) {
            mContentView.setRotation(180);
            mAutoTrackRectView.setOrien(180);
        }

        if (ScreenOrientationListener.isPortrait(type))
            type = ScreenOrientationType.PORTRAIT;

        if (mShotSettingLayout != null) {
            mShotSettingLayout.changeOrientation(type);
        }
        if (mShotControlLayout != null) {
            mShotControlLayout.changeOrientation(type);
        }
        if (mFPVSettingView != null) {
            mFPVSettingView.orientation(type);
        }

        setRockerOrientation(type);

        int rotate = 0;
        if (type == ScreenOrientationType.LANDSCAPE) {
            if (mAnimationView != null)
                mAnimationView.animate().rotation(0);

        } else if (type == ScreenOrientationType.PORTRAIT) {
            rotate = -90;
            if (mAnimationView != null)
                mAnimationView.animate().rotation(-90);
        }

        if (mShotControlLayout != null)
            mShotControlLayout.mViewRotate = rotate;
        if (mAutoTrackRectView != null)
            mAutoTrackRectView.mViewRotate = rotate;
        if (mFPVSettingView != null)
            mFPVSettingView.setViewRotate(rotate);
        if (mDlgContent != null) {
            mDlgContent.setRotation(rotate);
        }
    }

    private void setRockerOrientation(ScreenOrientationType type) {
        if (mRockerView == null)
            return;

        if (type == ScreenOrientationType.LANDSCAPE) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mRockerView.getLayoutParams();
            lp.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lp.setMargins(ViewUitls.dp2px(this, 85), 0, 0, ViewUitls.dp2px(this, 20));
            mRockerView.setLayoutParams(lp);
        } else if (type == ScreenOrientationType.PORTRAIT) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mRockerView.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lp.setMargins(0, 0, ViewUitls.dp2px(this, 122), ViewUitls.dp2px(this, 20));
            mRockerView.setLayoutParams(lp);
        }
    }

    private static class SendNaviControlTread extends Thread {
        private float mX, mY;
        private boolean isRunning = false;
        public volatile boolean isStop = false;

        public void startThread() {
            isRunning = true;
        }

        public void stopThread() {
            isRunning = false;
        }

        public void setData(float x, float y) {
            isRunning = true;
            this.mX = x;
            this.mY = y;
        }

        @Override
        public void run() {
            while (!isStop) {
                if (isRunning) {
                    sendDirector(mX, mY);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void sendDirector(float x, float y) {
            int director_x;
            int director_y;
            if (x >= 0) {
                director_x = 3;
            } else {
                director_x = 1;
            }

            if (y >= 0) {
                director_y = 0;
            } else {
                director_y = 2;
            }

            int[] arr_x = {director_x, (int) Math.abs(x)};
            int[] arr_y = {director_y, (int) Math.abs(y)};

            ConnectionManager.getInstance().send_director(arr_x, arr_y, new MoRequestCallback() {
                @Override
                public void onSuccess() {
                    System.out.println("send_director success");
                }

                @Override
                public void onFailed() {
                    System.out.println("send_director failed");
                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(EventMessage msg) {
        if (msg.code == EventMessage.ENABLE_TRACING) {
            enableTrack(msg.extra);
            mAutoTrackRectView.enableTracingMode = msg.extra;
        }
    }

    //ptz的fpv模式关闭识别 其他模式恢复为之前的识别状态
    private void enableTrack(int ptzMode) {
        if (mShotControlLayout != null) {
            mShotControlLayout.mTrackImage.setSelected(mIsTracePtzMode && ptzMode != ProfessionSettingView.FPV);
            mShotControlLayout.mTrackLayout.setTag(ptzMode != ProfessionSettingView.FPV);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (WorkStateManager.getInstance().getmWorkState() != WorkState.STANDBY) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private String getStr(@StringRes int string) {
        if (getResources() != null)
            return getResources().getString(string);
        return "";
    }

    @Override
    public void connectedUSB() {
        super.connectedUSB();
        GoUpActivity.startGoUpActivity(this);
    }

    /**
     * 异常上报
     */
    private void errUpload() {
        ConnectionManager.getInstance().setErrorI((data) -> {
            if (data == null || mIsPause || getResources() == null)
                return;
            switch (data.event) {
                case MoErrorCallback.SD_EVENT:
                    if (mShotSettingLayout == null || mShotSettingLayout.mSdCardText == null)
                        break;

                    switch (data.status) {
                        case MoErrorCallback.SD_OUT:
                            mShotSettingLayout.mSdCardText.setText("0M");
                            mShotSettingLayout.mSdCardIV.setImageResource(R.mipmap.icon_sdcard_out);
                            DlgUtils.toast(MoFPVActivity.this, getResources().getString(R.string.sdcard_out), mRotate);
                            abortRecording(null);
                            break;
                        case MoErrorCallback.SD_IN:
                            mShotSettingLayout.mSdCardText.setText(data.size);
                            mShotSettingLayout.mSdCardIV.setImageResource(R.mipmap.icon_fpv_mode_sdcard);
                            syncThumbnail();
                            break;
                        case MoErrorCallback.SD_IN_FAIL:
                            mShotSettingLayout.mSdCardIV.setImageResource(R.mipmap.icon_sdcard_out);
                            DlgUtils.toast(MoFPVActivity.this, getResources().getString(R.string.sdcard_abnormal), mRotate);
                            break;
                        case MoErrorCallback.SD_FULL:
                            mShotSettingLayout.mSdCardIV.setImageResource(R.mipmap.icon_sdcard_out);
                            DlgUtils.toast(MoFPVActivity.this, getResources().getString(R.string.sdcard_status_full), mRotate);
                            ConnectionManager.getInstance().albumCount(new ConnectionManager.AlbumCountListener() {
                                @Override
                                public void onSuccess(int count, MoImage thumbnail) {
                                    abortRecording(thumbnail);
                                }

                                @Override
                                public void onFailed() {
                                    abortRecording(null);
                                }
                            });

                            break;
                        case MoErrorCallback.SD_LOW:
                            mShotSettingLayout.mSdCardIV.setImageResource(R.mipmap.icon_fpv_mode_sdcard);
                            DlgUtils.toast(MoFPVActivity.this, getResources().getString(R.string.sdcard_low), mRotate);
                            break;
                    }

                    mShotControlLayout.recordViewStatus = data.status;
                    break;
                case MoErrorCallback.BAT_EVENT:
                    if (data.value >= 0) {
                        if (data.value <= 1) {
                            DlgUtils.toast(MoFPVActivity.this, getResources().getString(R.string.bat_low_shutdown), mRotate);
                        } else if (data.value < 15) {
                            if (mBatteryState) {
                                mBatteryState = false;
                                DlgUtils.toast(MoFPVActivity.this, getResources().getString(R.string.bat_low_15), mRotate);
                            }
                        } else
                            mBatteryState = true;
                        if (mShotSettingLayout == null)
                            return;
                        mShotSettingLayout.mBatteryView.setProgress(data.value / 100f);
                        mShotSettingLayout.mBatteryText.setText(data.value + "%");
                    }
                    break;
                case MoErrorCallback.PHOTO_ERR_EVENT:
                    if (mFPVPresenter != null)
                        mFPVPresenter.takeVideoFailed();
                    break;
                case MoErrorCallback.PTZ_EVENT:
                    if (mFPVSettingView != null && this.mShotSettingsEx != null && data.ptz_mode > 0) {
                        if (this.mShotSettingsEx.isVideo) {
                            this.mShotSettingsEx.videoModel.ptz_mode = data.ptz_mode;
                        } else {
                            this.mShotSettingsEx.snapshotModel.ptz_mode = data.ptz_mode;
                        }
                        mFPVSettingView.syncPtzMode(data.ptz_mode, -1);
                    }

                    if (this.mShotSettingsEx != null && data.ptz_sensitivity > 0) {
                        if (this.mShotSettingsEx.isVideo) {
                            this.mShotSettingsEx.videoModel.ptz_speed = data.ptz_sensitivity;
                        } else {
                            this.mShotSettingsEx.snapshotModel.ptz_speed = data.ptz_sensitivity;
                        }
                        mFPVSettingView.syncPtzMode(-1, data.ptz_sensitivity);
                    }

                    String err_ptz = "";
                    switch (data.ptz_action) {
                        case 1:
                            err_ptz = getStr(R.string.err_ptz_rever);
                            break;
                        case 2:
                            err_ptz = getStr(R.string.err_ptz_center);
                            break;
                        case 3:
                            err_ptz = getStr(R.string.err_ptz_astrict);
                            break;
                        case 4:
                            showDlg(R.string.err_ptz_protect, R.mipmap.pic_protect_ptz, () -> {
                                ConnectionManager.getInstance().protectRecover(new MoRequestCallback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onFailed() {

                                    }
                                });
                            });
                            break;
                        case 5:
                            //跟踪状态不支持万向键
                            break;
                        case 10:
                            //云台恢复
                            break;
                        case 11:
                            //云台恢复成功
                            break;
                        case 12:
                            //云台恢复失败
                            break;
                    }
                    //同一个提示 限制五秒以上
                    if (!err_ptz.isEmpty() && (mPtzAction != data.ptz_action
                            || (System.currentTimeMillis() - mRockerTimeFlag > 5000))) {
                        mPtzAction = data.ptz_action;
                        mRockerTimeFlag = System.currentTimeMillis();
                        DlgUtils.toast(MoFPVActivity.this, err_ptz, mRotate);
                    }
                    break;
                case MoErrorCallback.TRACK_EVENT:
                    if (data.track_status == 1) {
                        DlgUtils.toast(MoFPVActivity.this, getStr(R.string.trace_miss), mRotate);
                        mAutoTrackRectView.setNormal();
                        mRockerView.setVisibility(View.VISIBLE);
                    } else if (data.track_status == 2) {
                        DlgUtils.toast(MoFPVActivity.this, getStr(R.string.trace_unsupport), mRotate);
                        mRockerView.setVisibility(View.VISIBLE);
                    } else if (data.track_status == 3) {//开始跟踪
                        mAutoTrackRectView.setNormal();
                        mIsTracePtzMode = true;
                        if (mShotControlLayout != null && mShotControlLayout.mTrackImage != null) {
                            mShotControlLayout.mTrackImage.setSelected(true);
                        }
                    }
                    break;
                case MoErrorCallback.TEMPER_EVENT:
                    String err = "";
                    switch (data.temperature) {
                        case 1:
                            err = getStr(R.string.err_temper_bat_hei);
                            break;
                        case 2:
                            showDlg(R.string.err_temper_bat_hei_5, R.mipmap.icon_baterr_hot, null);
                            break;
                        case 3:
                            showDlg(R.string.err_temper_bat_low_5, R.mipmap.icon_baterr_cold, null);
                            break;
                        case 4:
                            err = getStr(R.string.err_temper_camera_hei);
                            break;
                        case 5:
                            showDlg(R.string.err_temper_camera_hei_5, R.mipmap.icon_camerr_hot, null);
                            break;
                        case 6:
                            err = getStr(R.string.err_temper_camera_low);
                            break;
                    }
                    if (!err.isEmpty())
                        DlgUtils.toast(MoFPVActivity.this, err, mRotate);
                    break;
                case MoErrorCallback.VIDEO_PRE_FINISH:
                    if (mShotControlLayout != null) {
                        mShotControlLayout.videoStopTimeImm();
                    }

                    if (mFPVPresenter != null)
                        mFPVPresenter.stopRefrushVideo();
                    break;
            }
        });
    }

    private void wakeUpGoUp() {
        startActivity(new Intent(this, GoUpActivity.class));
        this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        wakeUpGoUp();
    }

    private void showDlg(@StringRes int info, @DrawableRes int icon, OnDlgDismiss listener) {
        if (mDlgContent != null) {
            mDlgInfo.setText(info);
            mDlgImg.setImageResource(icon);

            mDlgAffirm.setOnClickListener((view) -> {
                mDlgContent.setVisibility(View.GONE);
                mDlgLayer.setVisibility(View.GONE);
                if (listener != null)
                    listener.onDismiss();
            });
            mDlgContent.setVisibility(View.VISIBLE);
            mDlgLayer.setVisibility(View.VISIBLE);
        }
    }

    private void clearDlg() {
        if (mDlgContent != null) {
            mDlgContent.setVisibility(View.GONE);
            mDlgLayer.setVisibility(View.GONE);
        }
    }

    interface OnDlgDismiss {
        void onDismiss();
    }
}
