package com.test.xcamera.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.editvideo.ToastUtil;
import com.framwork.base.view.MOBaseActivity;
import com.test.xcamera.R;
import com.test.xcamera.accrssory.AccessoryManager;
import com.test.xcamera.bean.BaseData;
import com.test.xcamera.bean.FrameRectInfo;
import com.test.xcamera.bean.MoShotSetting;
import com.test.xcamera.constants.LogcatConstants;
import com.test.xcamera.dymode.callbacks.DyFPVCallback;
import com.test.xcamera.dymode.callbacks.H264DecodeListener;
import com.test.xcamera.dymode.model.TimeSpeedModel;
import com.test.xcamera.dymode.presenter.DyFPVPresenter;
import com.test.xcamera.dymode.utils.FileUtils;
import com.test.xcamera.dymode.utils.NewCameraDisplay;
import com.test.xcamera.dymode.view.AutoFitTextureView;
import com.test.xcamera.dymode.view.DyFPVShotView;
import com.test.xcamera.dymode.view.RecordTimelineView;
import com.test.xcamera.home.GoUpActivity;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.managers.ShotModeManager;
import com.test.xcamera.mointerface.MoErrorCallback;
import com.test.xcamera.mointerface.MoRequestCallback;
import com.test.xcamera.phonealbum.MyVideoEditActivity;
import com.test.xcamera.phonealbum.SelectMusicActivity;
import com.test.xcamera.phonealbum.bean.MusicBean;
import com.test.xcamera.player.AVFrame;
import com.test.xcamera.utils.AppExecutors;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.DlgUtils;
import com.test.xcamera.utils.FileUtil;
import com.test.xcamera.utils.PUtil;
import com.test.xcamera.utils.StandardAffirmDlg;
import com.test.xcamera.view.AutoTrackingRectViewFix;
import com.test.xcamera.view.MoFPVRockerView;
import com.moxiang.common.logging.Logcat;

import java.math.BigDecimal;
import java.util.List;

import butterknife.BindView;

import static com.test.xcamera.dymode.view.RecordTimelineView.RECORD_MAX_TIME;

/**
 * 抖音拍摄
 * Created by zll on 2020/1/16.
 */

public class DyFPVActivity extends MOBaseActivity implements AccessoryManager.PreviewDataCallback,
        DyFPVCallback, H264DecodeListener, RecordTimelineView.RecordProgressStateCallback {
    private static final String TAG = "DyFPVActivity";
    private static final String ACCESS_KEY = "56351e7052ed11ea85f25bb3eaf08f69";
    private static final BigDecimal MAX_RECORD_TIME = new BigDecimal(60.0);

    @BindView(R.id.dy_fpv_frame_surface)
    AutoFitTextureView mSurfaceView;
    //    SurfaceView mSurfaceView;
    //    @BindView(R.id.dy_fpv_frame_layout)
//    ScreenSizeAspectFrameLayout mFrameLayout;
//    @BindView(R.id.dy_tv_frag_count)
//    TextView mFragCount;
//    @BindView(R.id.dy_tv_total_time)
//    TextView mTotalTime;
    @BindView(R.id.dy_fpv_shot_control_layout)
    DyFPVShotView mShotControlLayout;
    @BindView(R.id.dy_fpv_rocker_view)
    MoFPVRockerView mRockerView;
    @BindView(R.id.dy_fpv_track_view)
    AutoTrackingRectViewFix mAutoTrackRectView;

    //用于云台保护模式的弹框
    private View mDlgContent, mDlgLayer;
    private TextView mDlgAffirm, mDlgInfo;
    private ImageView mDlgImg;

    private DyFPVPresenter mPresenter;
    private View mDecorView;
    private NewCameraDisplay mCameraDisplay;
    private volatile int videoWidth, videoHeight;
    private boolean mIsPause = false;
    private boolean mBatteryState = true;
    private StandardAffirmDlg sdFullDlg = new StandardAffirmDlg();
    private boolean connFlag = true;

    public float mCurrentMills;
    private boolean isOverTime = false;
    private boolean isArriveCountDown = false;
    public boolean isCountDownRecord = false;
    public float mRecordTime = -1;
    public float mCountDownTime = -1;
    public boolean mIsDisConnect = false;
    public static boolean sizeFlag = false;
    private boolean mConnectState = true;
    public boolean mRecordStop = false;

    private HandlerThread mBackgroundThread;
    public Handler mBackgroundHandler;
    private MusicBean musicBean;

    @Override
    public int initView() {
        return R.layout.activity_dy_fpv;
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitleStatus(true);
        noStatusBar();
        super.onCreate(savedInstanceState);
        deleteCompile();
        init();
    }

    private void init() {
        connFlag = true;
        mDecorView = getWindow().getDecorView();
        mDecorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                Log.d(TAG, "onSystemUiVisibilityChange: " + visibility);
                if (visibility == 0)
                    setDecorViewState();
            }
        });
        AccessoryManager.getInstance().setPreviewDataCallback(this);

        sdFullDlg.clear();

        mCameraDisplay = new NewCameraDisplay(this, mSurfaceView);
        mPresenter = new DyFPVPresenter(this, this, this, mCameraDisplay);
        mCameraDisplay.setDecodeListener(this);
        mShotControlLayout.setDyFPVPresenter(mPresenter);

        mShotControlLayout.init(this);
        mShotControlLayout.setDyFPVCallback(this);
        mShotControlLayout.attachActivity(this);
        mShotControlLayout.setRecordProgressStateCallback(this);

        mRockerView.setOnLocationListener(new MoFPVRockerView.OnLocationListener() {
            @Override
            public void onStart() {
                Log.d(TAG, "onStart: ");
                if (mPresenter != null) {
                    mPresenter.startNaviThread();
                }
            }

            @Override
            public void getLocation(int x, int y) {
                Log.d(TAG, "getLocation x: " + x + "  y: " + y);
                if (mPresenter != null) {
                    mPresenter.setNaviThradData(x, y);
                }
            }

            @Override
            public void onFinish() {
                Log.d(TAG, "onFinish: ");
                if (mPresenter != null) {
                    mPresenter.stopNaviThread();
                }
            }
        });

        mPresenter.initNaviControl();
        initPtzDlg();

        mAutoTrackRectView.setDyMode(true);
        mAutoTrackRectView.setDyShotLayout(mShotControlLayout);
        mAutoTrackRectView.setTextureView(mSurfaceView);

        //跟踪框回调
        mAutoTrackRectView.setTrackInterface(new AutoTrackingRectViewFix.TrackInterface() {

            @Override
            public void onStartAutoTrack() {
//                testLog("onStartAutoTrack");
                if (mShotControlLayout != null && mShotControlLayout.mShotTrackImage != null)
                    mShotControlLayout.mShotTrackImage.setSelected(true);
            }

            @Override
            public void onStartTrackSingle() {
//                testLog("onStartTrackSingle");
                setRockerViewState(View.GONE, 1);

                if (mShotControlLayout != null && mShotControlLayout.mShotTrackImage != null)
                    mShotControlLayout.mShotTrackImage.setSelected(true);
            }

            @Override
            public void onStopTrackSingle() {
//                testLog("onStopTrackSingle");
                if (!mShotControlLayout.isSecondViewShow())
                    setRockerViewState(View.VISIBLE, 2);
            }

            @Override
            public void onStopTrack() {
//                testLog("onStopTrack");
                if (mShotControlLayout != null && mShotControlLayout.mShotTrackImage != null)
                    mShotControlLayout.mShotTrackImage.setSelected(false);
                if (mShotControlLayout == null) {
                    return;
                }
                if (!mShotControlLayout.isSecondViewShow())
                    setRockerViewState(View.VISIBLE, 3);
            }

            @Override
            public void onStartAreaTrack() {
//                testLog("onStartAreaTrack");
                if (mShotControlLayout != null && mShotControlLayout.mShotTrackImage != null)
                    mShotControlLayout.mShotTrackImage.setSelected(true);
            }

            @Override
            public void onFailed(AutoTrackingRectViewFix.Mode mode) {
//                testLog("onFailed");
                if (mShotControlLayout != null && mShotControlLayout.mShotTrackImage != null)
                    mShotControlLayout.mShotTrackImage.setSelected(false);
                if (!mShotControlLayout.isSecondViewShow())
                    setRockerViewState(View.VISIBLE, 4);
            }
        });

        ConnectionManager.getInstance().setErrorI((data) -> {
            if (data == null || mIsPause || getResources() == null) {
                return;
            }
            switch (data.event) {
                case MoErrorCallback.BAT_EVENT:
                    if (data.value >= 0) {
                        if (data.value <= 1) {
//                            DlgUtils.toast(DyFPVActivity.this, "相机即将关机");
                            CameraToastUtil.show90(getStr(R.string.bat_low_shutdown), DyFPVActivity.this);
                        } else if (data.value < 15) {
                            if (mBatteryState) {
                                mBatteryState = false;
                                DlgUtils.toast(DyFPVActivity.this, getResources().getString(R.string.bat_low_15));
                            }
                        } else
                            mBatteryState = true;
                    }
                    break;
                case MoErrorCallback.PHOTO_ERR_EVENT:

                    break;
                case MoErrorCallback.PTZ_EVENT:
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
//                            err_ptz = getStr(R.string.err_ptz_protect);
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
                    }

                    if (!err_ptz.isEmpty())
                        CameraToastUtil.show90(err_ptz, DyFPVActivity.this);
                    break;
                case MoErrorCallback.TRACK_EVENT:
                    if (data.track_status == 1) {
//                        DlgUtils.toast(DyFPVActivity.this, "跟踪目标丢失");
                        CameraToastUtil.show90(getStr(R.string.trace_miss), DyFPVActivity.this);
                        if (mAutoTrackRectView != null)
                            mAutoTrackRectView.setNormal();
                        setRockerViewState(View.VISIBLE, 9);
                    } else if (data.track_status == 2) {
                        DlgUtils.toast(DyFPVActivity.this, getStr(R.string.trace_unsupport));
                        setRockerViewState(View.VISIBLE, 111);
                    } else if (data.track_status == 3) {//开始跟踪
                        mAutoTrackRectView.setNormal();
                        if (mShotControlLayout != null && mShotControlLayout.mShotTrackImage != null) {
                            mShotControlLayout.mShotTrackImage.setSelected(true);
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
                            sdFullDlg.show(DyFPVActivity.this, getStr(R.string.err_temper_bat_hei_5), R.mipmap.icon_baterr_hot);
                            break;
                        case 3:
                            sdFullDlg.show(DyFPVActivity.this, getStr(R.string.err_temper_bat_low_5), R.mipmap.icon_baterr_cold);
                            break;
                        case 4:
                            err = getStr(R.string.err_temper_camera_hei);
                            break;
                        case 5:
                            sdFullDlg.show(DyFPVActivity.this, getStr(R.string.err_temper_camera_hei_5), R.mipmap.icon_camerr_hot);
                            break;
                        case 6:
                            err = getStr(R.string.err_temper_camera_low);

                            break;
                    }
                    if (!err.isEmpty())
                        CameraToastUtil.show90(err, DyFPVActivity.this);
                    break;
            }
        });
    }

    @Override
    protected void onStart() {
        setDecorViewState();
        super.onStart();
    }

    @Override
    public void onVideoDataAvailable(BaseData baseData) {
        AVFrame avFrame = new AVFrame(baseData, baseData.getmDataSize(), false, false);
        if (mAutoTrackRectView == null || avFrame == null) {
            return;
        }

        if (mCameraDisplay != null) {
            mCameraDisplay.onVideoDataAvailable(avFrame);
        }

        if (videoWidth != avFrame.getVideoWidth() || videoHeight != avFrame.getVideoHeight()
                || mAutoTrackRectView.videoWidthFix == 0) {
            videoWidth = avFrame.getVideoWidth();
            videoHeight = avFrame.getVideoHeight();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mAutoTrackRectView != null) {
                        mAutoTrackRectView.changeSize(avFrame.getVideoWidth(), avFrame.getVideoHeight());
                    }
                }
            });
        }

        boolean isTracing = false;
        if (avFrame.getBaseData().getmVideoFrameInfo().getmRectCount() > 0)
            for (FrameRectInfo info : avFrame.getBaseData().getmVideoFrameInfo().getmFrameRectInfos())
                if (info.getStatus() == AutoTrackingRectViewFix.TRACE
                        || info.getStatus() == AutoTrackingRectViewFix.TRACE_FIND) {
                    isTracing = true;
                    break;
                }
        if (mRockerView != null)
            if (isTracing && (mRockerView.getVisibility() == View.VISIBLE))
                runOnUiThread(() -> {
                    setRockerViewState(View.GONE, 7);
                });
            else if (!isTracing && (mRockerView.getVisibility() != View.VISIBLE))
                runOnUiThread(() -> {
                    if (!mShotControlLayout.isSecondViewShow())
                        setRockerViewState(View.VISIBLE, 8);
                });

        if (avFrame != null) {
            if (mAutoTrackRectView != null) {
                mAutoTrackRectView.setMirror(avFrame.getMirror() == 1);
            }
        }
    }

    @Override
    public void onAudioDataAvailable(BaseData baseData) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult: requestCode: " + requestCode + ", resultCode: " + resultCode);
        if (data != null) {
            mConnectState = data.getBooleanExtra("DisconnectedUSBStatus", false);
            boolean isCancel = data.getBooleanExtra("isCancelMusic", false);
            if (isCancel) {
                musicBean = null;
                if (mCameraDisplay != null) {
                    mCameraDisplay.setMusic("");
                }
                if (mShotControlLayout != null) {
                    mShotControlLayout.setMusicInfo(null);
                    mShotControlLayout.resetMaxDuration(RECORD_MAX_TIME);
                }
                return;
            }
            Log.d(TAG, "onActivityResult: connectedState: " + mConnectState);
            if (resultCode == MyVideoEditActivity.RESULT_OK_IN_MUSIC) {
                musicBean = (MusicBean) data.getSerializableExtra(SelectMusicActivity.KEY_OF_MUSIC_RESULT);
                if (musicBean != null) {
                    mCameraDisplay.setMusic(musicBean.getPath());
                    mShotControlLayout.setMusicInfo(musicBean);
                    if (musicBean.getDuration() <= 60) {
                        String msg = getResources().getString(R.string.dy_record_max_time, musicBean.getDuration());
                        CameraToastUtil.show90(msg, DyFPVActivity.this);
                        Log.d(TAG, "onActivityResult: msg: " + msg);
                        mShotControlLayout.resetMaxDuration(musicBean.getDuration() * 1000);
                    } else {
                        String msg = getResources().getString(R.string.dy_record_max_time, 60);
                        CameraToastUtil.show90(msg, DyFPVActivity.this);
                        mShotControlLayout.resetMaxDuration(RecordTimelineView.RECORD_MAX_TIME);
                        Log.d(TAG, "onActivityResult: msg: " + msg);
                    }
                    Log.d(TAG, "onActivityResult: music bean：" + musicBean.toString());
                }
            }
        }
    }

    @Override
    public void deleteSegment(float recordTime) {
        if (mShotControlLayout != null) {
            mRecordTime = mShotControlLayout.getTimeLineDuration();
            testLog("deleteSuccess: " + mShotControlLayout.getTimeLineDuration());
            Logcat.v().tag(LogcatConstants.DOUYIN_RECORD).msg("stop record delete total time: " + mShotControlLayout.getTimeLineDuration()).out();
        }
    }

    @Override
    public void clipSegment(float recordTime) {
        if (mShotControlLayout != null) {
            mRecordTime = mShotControlLayout.getTimeLineDuration();
            testLog("clip: " + mShotControlLayout.getTimeLineDuration());
            Logcat.v().tag(LogcatConstants.DOUYIN_RECORD).msg("stop record clip total time: " + mShotControlLayout.getTimeLineDuration()).out();
        }
    }

    /**
     * 更新拍摄进度
     *
     * @param durings                分段信息
     * @param currentRecordTimeMilli 当前正在拍摄分段的时长
     */
    public void updateProgressSegment(List<TimeSpeedModel> durings, long currentRecordTimeMilli, boolean isStop) {
        mCurrentMills = 1.0f * currentRecordTimeMilli / mShotControlLayout.getCurrentSpeed();
        if (isStop) {
            mShotControlLayout.setDuration(mCurrentMills);
            mShotControlLayout.clipComplete();
            testLog("updateProgressSegment stop: " + mCurrentMills + ", total record time: " + mRecordTime);
            return;
        }
        testLog("is over time: " + isOverTime + ", need thread: " + needThread + ", is arrive count down: " + isArriveCountDown);
        if (isOverTime) return;
        if (needThread) return;
        if (isArriveCountDown) return;
        String content = "recording mCurrentMills: " + mCurrentMills + ", currentRecordTimeMilli: " + currentRecordTimeMilli;
        content = content + ",  after mCurrentMills:" + mCurrentMills + ", currentRecordTimeMilli:" + currentRecordTimeMilli + ", speed: " + mShotControlLayout.getCurrentSpeed();
        if (currentRecordTimeMilli > 0) {
            if (!mShotControlLayout.isShotBtnEnabled())
                mShotControlLayout.setShotBtnEnable(true);
            mShotControlLayout.setDuration(mCurrentMills);
            mRecordTime = mShotControlLayout.getTimeLineDuration() + mCurrentMills;
            content = content + ", mRecordTime: " + mRecordTime;
            Logcat.v().tag(LogcatConstants.DOUYIN_RECORD).msg(content).out();
            testLog(content);
            if (mRecordTime >= 3 * 1000) {
                mShotControlLayout.showConfirmLayout();
            }
            if (mRecordTime >= mShotControlLayout.getMaxDuration()) {
                isOverTime = true;
                mShotControlLayout.clickRecord();
                mShotControlLayout.performConfirmClick();
                testLog("arrive max record time");
                testLog("show compress dialog");
            } else if (isCountDownRecord && mRecordTime >= mCountDownTime) {
                Log.d("TEST_COUNTDOWN", "updateProgressSegment: " + mCountDownTime);
                testLog("is count down record, arrive count down time, stop record");
                isArriveCountDown = true;
                mShotControlLayout.clickRecord();
                isCountDownRecord = false;
            }
        } else {
            Logcat.v().tag(LogcatConstants.DOUYIN_RECORD).msg(content).out();
        }
    }

    private void setDecorViewState() {
        int flag = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        //判断当前版本在4.0以上并且存在虚拟按键，否则不做操作
        if (Build.VERSION.SDK_INT < 19 || !PUtil.checkDeviceHasNavigationBar(this)) {
            //一定要判断是否存在按键，否则在没有按键的手机调用会影响别的功能。如之前没有考虑到，导致图传全屏变成小屏显示。
            return;
        } else {
            // 获取属性
            mDecorView.setSystemUiVisibility(flag);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();
        if (mIsPause && !mConnectState) {
            if (AccessoryManager.getInstance().mIsRunning)
                enterDyApp();
            else {
                ToastUtil.showToast(DyFPVActivity.this, R.string.camera_disconnect);
                finishFPV();
            }
            return;
        }

        if (mIsPause && AccessoryManager.getInstance().mIsRunning)
            enterDyApp();

        if (mIsPause && AccessoryManager.getInstance().mIsRunning)
            mPresenter.syncShotSettings(false);
        mIsPause = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        sizeFlag = true;
        mIsPause = true;

        exitDyApp();

        if (mShotControlLayout != null)
            mShotControlLayout.pauseCutmusic();
        if (mCameraDisplay.isRecording()) {
            mShotControlLayout.performShotClick();
        }
        stopBackgroundThread();
    }

    public void clickShot() {
        if (mShotControlLayout != null) {
            mShotControlLayout.pauseCutmusic();
            mShotControlLayout.performShotClick();
        }
    }

    public void enterDyApp() {
        if (mPresenter != null && mPresenter.isCompressing()) return;
        if (mPresenter != null)
            mPresenter.enterDyAPP(1);
    }

    public void exitDyApp() {
        if (mPresenter != null)
            mPresenter.stopPreview();
    }

    @Override
    public void finishActivity() {
        if (mRecordTime >= 3000) {
            mIsDisConnect = true;
            mHasRecord = true;
            mShotControlLayout.showCompressLayout();
            return;
        }
        finishFPV();
    }

    @Override
    public void deleteLastFrag() {
        mShotControlLayout.deleteLastFlag();
        mCameraDisplay.deleteLastFrag();
        isOverTime = false;
    }

    public long getTotalRecordingTime() {
        return mCameraDisplay.getTotalRecordingTime();
    }


    @Override
    public void record(boolean isRecord) {
        if (mCameraDisplay.isRecording()) {
            if (needThread) {
                setDialogEnable(false);
                stopRecord();
                mBackgroundHandler.postDelayed(stopRecordRunnable, 100);
            } else {
                testLog("stop record start");
                Logcat.v().tag(LogcatConstants.DOUYIN_RECORD).msg("stop record start").out();
                mCameraDisplay.stopRecord();
                stopRecord();
                mRecordStop = true;
                testLog("stop record end");
                Logcat.v().tag(LogcatConstants.DOUYIN_RECORD).msg("stop record end").out();
            }
        } else {
            testLog("start record");
            mRecordStop = false;
            startRecord();
        }
    }

    private void stopRecord() {
        int state;
        mShotControlLayout.record(false);
        if (mCameraDisplay != null && mCameraDisplay.getSegmentSize() > 0) {
            state = View.VISIBLE;
        } else {
            state = View.INVISIBLE;
        }
        isArriveCountDown = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mShotControlLayout.setDeleteState(state);
                mShotControlLayout.setRightLayoutState(View.VISIBLE);
                setRockerViewState(View.VISIBLE, 6);
                mShotControlLayout.setSpeedView();
            }
        });
    }

    private Runnable stopRecordRunnable = new Runnable() {
        @Override
        public void run() {
            mCameraDisplay.stopRecord();
            mRecordStop = true;
        }
    };

    private void startRecord() {
        testLog("start record");
        mShotControlLayout.setShotBtnEnable(false);
        mCameraDisplay.startRecord(mShotControlLayout.getRecordSpeed());
        mShotControlLayout.setCutmusicEnable(false);
        mShotControlLayout.record(true);
        mShotControlLayout.setDeleteState(View.INVISIBLE);
        mShotControlLayout.setRightLayoutState(View.INVISIBLE);
        setRockerViewState(View.VISIBLE, 5);
        mShotControlLayout.setZoomLayout(View.VISIBLE);
        if (mShotControlLayout.mIsSpeedViewShow) {
            mShotControlLayout.setSpeedViewState(View.GONE);
        }
    }

    @Override
    public void startTrack() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mAutoTrackRectView != null)
                    mAutoTrackRectView.startDraw();
            }
        });
    }

    @Override
    public void stopTrack() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mAutoTrackRectView != null)
                    mAutoTrackRectView.stopDraw();
            }
        });
    }

    @Override
    public void syncShotSettings(MoShotSetting shotSetting) {
        Logcat.v().tag("commandCallback").msg("syncShotSettings==>").out();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mAutoTrackRectView != null) {
                    mAutoTrackRectView.setEnable(true);
                    mAutoTrackRectView.setEnableZoom(true);
                }
                if (mShotControlLayout != null && mShotControlLayout.mShotZoomLayout != null) {
                    mShotControlLayout.mShotZoomLayout.setTag(true);
                    mShotControlLayout.mShotZoomLayout.setAlpha(1f);

                    //同步变焦
                    int zoom = ShotModeManager.getInstance().isVideo()
                            ? shotSetting.getmMoRecordSetting().getZoom()
                            : shotSetting.getmMoSnapShotSetting().getZoom();
                    mShotControlLayout.mZoomLevel = zoom;
                    mShotControlLayout.mShotZoomText.setText(Math.round(zoom / 10f) + ".0X");
                    mShotControlLayout.mShotZoomText.setTag(zoom);

                    //同步自动跟踪
                    int track = ShotModeManager.getInstance().isVideo()
                            ? shotSetting.getmMoRecordSetting().getTrackStatus()
                            : shotSetting.getmMoSnapShotSetting().getTrackStatus();
                    mShotControlLayout.mShotTrackImage.setSelected(track == 1);
                    if (mAutoTrackRectView != null) {
                        if (track == 1) {
                            mAutoTrackRectView.isStopFlag = false;
                            mAutoTrackRectView.setNormal();
                        } else {
                            mAutoTrackRectView.isStopFlag = true;
                        }
                    }
                }
            }
        });
    }

    @Override
    public void setModelSuccess(int mode) {
        if (mode == 1) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mShotControlLayout != null)
                        mShotControlLayout.setBackEnable(true);
                }
            });
        } else if (mode == 3) {
            if (mPresenter != null) {
                mPresenter.startSetModeTimer();
            }
        }
    }

    @Override
    public void decodeResult(AVFrame avFrame) {
        if (avFrame != null
                && avFrame.getBaseData() != null
                && avFrame.getBaseData().getmVideoFrameInfo() != null) {
            if (mAutoTrackRectView != null) {
                //实时获取屏幕角度 用于双击跟踪
                mAutoTrackRectView.mRotate = avFrame.getBaseData().getmVideoFrameInfo().getRotate();
                if (avFrame.getBaseData().getmVideoFrameInfo().getmRectCount() > 0)
                    mAutoTrackRectView.setData(avFrame.getBaseData().getmVideoFrameInfo());
            }
        }
    }

    @Override
    public void onSizeChanged(int width, int height) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mSurfaceView != null)
                    mSurfaceView.setAspectRatio(width, height);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mShotControlLayout != null && !mShotControlLayout.canGoBack()) return;
        if (mShotControlLayout != null && !mShotControlLayout.allowCancel()) return;
        if (mRecordTime >= 3000) {
            mIsDisConnect = true;
            mHasRecord = true;
            mShotControlLayout.performConfirmClick();
            return;
        }
        finishFPV();
    }

    public void finishFPV() {
        Log.d(TAG, "finishFPV: ");
        if (mCameraDisplay != null) mCameraDisplay.onPause();
        this.finish();
    }

    @Override
    protected void onStop() {
        super.onStop();

        clearDlg();

        Log.d(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        mHasRecord = false;
        if (mCameraDisplay != null)
            mCameraDisplay.release();
        Log.d(TAG, "onDestroy: ");
        mIsDisConnect = false;
        if (mAutoTrackRectView != null) {
            mAutoTrackRectView.setDyMode(false);
        }
        if (mShotControlLayout != null)
            mShotControlLayout.release();
        if (mPresenter != null) {
            mPresenter.destroy();
            mPresenter = null;
        }
        if (mAutoTrackRectView != null) {
            mAutoTrackRectView.setTrackInterface(null);
        }
        super.onDestroy();

    }

    public void showToast(String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(DyFPVActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setRockerViewState(int state, int tag) {
        testLog("setRockerViewState: " + state + ", tag: " + tag);
        if (mRockerView != null) {
            mRockerView.setVisibility(state);
        }
    }

    // 道具
    public void switchEffect(String path) {
        if (mCameraDisplay != null) {
            mCameraDisplay.switchEffect(path);
        }
    }

    // 腮红 口红
    public void setIntensityByType(int type, float intensity) {
        mCameraDisplay.setIntensityByType(type, intensity);
    }

    // 大眼 瘦脸
    public void setFaceReshape(String path, float eye, float face) {
        mCameraDisplay.setFaceReshape(path, eye, face);
    }

    // 美颜
    public void setBeauty(boolean isOpen, float value) {
        mCameraDisplay.setSmooth(isOpen, value);
    }

    // 滤镜
    public void setFilter(String path, float intensity) {
        if (mCameraDisplay != null) {
            mCameraDisplay.setFilter(path, intensity);
        }
    }

    public boolean isOverTime() {
        return isOverTime;
    }

    public void setCountDownTime(float time) {
        testLog("set count time: " + time);
        mCountDownTime = time * 1000;
        isCountDownRecord = true;
    }

    @Override
    public void connectedUSB() {
        super.connectedUSB();

        connFlag = true;
        mIsDisConnect = false;
        needThread = false;
        if (System.currentTimeMillis() - disconnectTime < 2000)
            return;
        if (!mHasRecord) {
            finishFPV();
            GoUpActivity.startGoUpActivity(this);
        } else {
            if (mPresenter != null && !mPresenter.isCompressing()) {
                enterDyApp();
            }
        }
    }

    private long disconnectTime;
    private boolean mHasRecord = false;
    private boolean needThread = false;

    @Override
    public void disconnectedUSB() {
        super.disconnectedUSB();

//        testLog("disconnect usb");

        disconnectTime = System.currentTimeMillis();

        mIsDisConnect = true;

        if (mShotControlLayout.isCountDownAnimation()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mShotControlLayout.resetCountDownState();
                }
            });
        }

        if (mIsPause) {
            finishFPV();
            return;
        }
        if (connFlag) { //防止多次调用
            connFlag = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    disConnect();
                }
            });
        }
    }

    private void disConnect() {
        if (mPresenter != null && mPresenter.isCompressing()) {
            mHasRecord = true;
            return;
        }
        if (mCameraDisplay.isRecording()) {
            if (mRecordTime >= 3000) {
                mHasRecord = true;
                needThread = true;
                mShotControlLayout.performConfirmClick();
//                            mHandler.sendEmptyMessage(DISMISSLOADING);
                testLog("disconnect usb clickConfirm 2");
            } else {
                mShotControlLayout.clickRecord();
                needThread = false;
//                            mHandler.sendEmptyMessage(DISMISSLOADING);
                CameraToastUtil.show(getResourceToString(R.string.disconenct_usb), DyFPVActivity.this);
                finishFPV();
            }
        } else {
            if (mRecordTime >= 3000) {
                needThread = true;
                mHasRecord = true;
                mShotControlLayout.performConfirmClick();
//                            mHandler.sendEmptyMessage(DISMISSLOADING);
            } else {
                needThread = false;
                CameraToastUtil.show(getResourceToString(R.string.disconenct_usb), DyFPVActivity.this);
//                            mHandler.sendEmptyMessage(DISMISSLOADING);
                finishFPV();
            }
        }
    }

    public void setTouchEvent(float x, float y) {
        mCameraDisplay.setTouchEvent(x, y);
    }

    private String getStr(@StringRes int string) {
        if (getResources() != null)
            return getResources().getString(string);
        return "";
    }

    public static void testLog(String event) {
        String s = FileUtils.getCurrentTime() + ": " + event;
        FileUtil.writeFileToSDCard(FileUtil.path, s.getBytes(), "mills.txt", true, true, false);
    }

    public void setDialogEnable(boolean enable) {
        mShotControlLayout.setDialogEnable(enable);
    }

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("DyFPVBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initPtzDlg() {
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
    }

    /**
     * 显示云台保护弹框
     */
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

    /**
     * 隐藏云台保护弹框
     */
    private void clearDlg() {
        if (mDlgContent != null) {
            mDlgContent.setVisibility(View.GONE);
            mDlgLayer.setVisibility(View.GONE);
        }
    }

    interface OnDlgDismiss {
        void onDismiss();
    }

    private void deleteCompile() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                FileUtils.delDir(FileUtils.getDyVideoPath(DyFPVActivity.this));
            }
        });
    }
}