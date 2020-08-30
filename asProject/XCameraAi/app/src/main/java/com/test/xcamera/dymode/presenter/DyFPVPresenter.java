package com.test.xcamera.dymode.presenter;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;

import com.bytedance.sdk.open.aweme.share.Share;
import com.editvideo.ToastUtil;
import com.test.xcamera.R;
import com.test.xcamera.activity.DyFPVActivity;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.bean.MoShotSetting;
import com.test.xcamera.constants.LogcatConstants;
import com.test.xcamera.dymode.managers.EffectPlatform;
import com.test.xcamera.dymode.utils.FileUtils;
import com.test.xcamera.dymode.utils.NewCameraDisplay;
import com.test.xcamera.dymode.view.DyCommonDialog;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.dymode.callbacks.DyFPVCallback;
import com.test.xcamera.managers.WorkStateManager;
import com.test.xcamera.mointerface.MoRequestCallback;
import com.test.xcamera.mointerface.MoSynvShotSettingCallback;
import com.test.xcamera.utils.AppExecutors;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.MediaScannerUtil;
import com.test.xcamera.watermark.WaterMarker;
import com.moxiang.common.logging.Logcat;
import com.moxiang.common.share.ShareManager;
import com.ss.android.ttve.common.TEDefine;
import com.ss.android.vesdk.TERecorder;
import com.ss.android.vesdk.VECommonCallback;
import com.ss.android.vesdk.VECommonCallbackInfo;
import com.ss.android.vesdk.VEEditor;
import com.ss.android.vesdk.VEResult;
import com.ss.android.vesdk.VEVideoEncodeSettings;

import java.lang.ref.WeakReference;

import static com.ss.android.vesdk.VEVideoEncodeSettings.COMPILE_TYPE.COMPILE_TYPE_MP4;
import static com.ss.android.vesdk.VEVideoEncodeSettings.USAGE_COMPILE;

/**
 * Created by zll on 2020/1/17.
 */

public class DyFPVPresenter {
    private static final String TAG = "DyFPVPresenter";
    private WeakReference<Context> mContext;
    private DyFPVCallback mDyFPVCallback;
    //    private ProgressDialog mSynthesisDialog;
    public NewCameraDisplay mCameraDisplay;
    private VEEditor mEditor;
    volatile boolean mIsConcatFinished = true;
    private boolean mNeedPrepare;
    private WeakReference<DyFPVActivity> mActivity;
    private DyCommonDialog mDialog;
    private int mTotalProgress = 0;
    private boolean mIsCompressing = false;
    private SendNaviControlRunnable mSendNaviControlRunnable;
    private Thread mPtzThread;

    public DyFPVPresenter(DyFPVActivity activity, Context context, DyFPVCallback callback, NewCameraDisplay cameraDisplay) {
        mActivity = new WeakReference<>(activity);
        mContext = new WeakReference<>(context);
        mDyFPVCallback = callback;
        mCameraDisplay = cameraDisplay;
    }

    public void enterDyAPP(int mode) {
        ConnectionManager.getInstance().enterDyApp(mode, new MoRequestCallback() {
            @Override
            public void onSuccess() {
                if (mode == 1)
//                    changeDyMode(mode);
                    syncShotSettings(true);
            }
        });
    }

    public void startPreview() {
        ConnectionManager.getInstance().startPreview(new MoRequestCallback() {
            @Override
            public void onSuccess() {
                if (mDyFPVCallback != null) {
                    if (checkActivity()) {
                        mActivity.get().mBackgroundHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setModel(3);
                            }
                        }, 500);
                    }
                }
            }

            @Override
            public void onFailed() {
            }
        });
    }

    public void stopPreview() {
        ConnectionManager.getInstance().stopPreview(new MoRequestCallback() {
            @Override
            public void onSuccess() {
                enterDyAPP(0);
            }

            @Override
            public void onFailed() {
            }
        });
    }

    /**
     * 云台回中
     */
    public void backCenter() {
        ConnectionManager.getInstance().ptzBackCenter(null);
    }

    /**
     * 画面翻转
     *
     * @param type
     */
    public void rotateCamera(int type) {
        boolean isSelfie;
        isSelfie = (type != 0);
        WorkStateManager.getInstance().setmIsSelfie(isSelfie);
        ConnectionManager.getInstance().setSelfie(type, null);
    }

    /**
     * 变焦
     *
     * @param scale
     */
    public void digitalZoom(int scale) {
        ConnectionManager.getInstance().digitalZoom(scale, null);
    }

    private CountDownTimer countDownTimer = new CountDownTimer(3000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            setModel(1);
            countDownTimer.cancel();
        }
    };

    public void startSetModeTimer() {
        if (countDownTimer != null)
            countDownTimer.start();
    }

    public void initNaviControl() {
        mSendNaviControlRunnable = new SendNaviControlRunnable();
        mSendNaviControlRunnable.isStop = false;
        mPtzThread = new Thread(mSendNaviControlRunnable);
        mPtzThread.start();
    }

    public void startNaviThread() {
        if (mSendNaviControlRunnable != null)
            mSendNaviControlRunnable.startThread();
    }

    public void stopNaviThread() {
        if (mSendNaviControlRunnable != null)
            mSendNaviControlRunnable.stopThread();
    }

    public void setNaviThradData(float x, float y) {
        if (mSendNaviControlRunnable != null)
            mSendNaviControlRunnable.setData(x, y);
    }

    private void releaseNaviThread() {
        if (mPtzThread != null && mPtzThread.isAlive()) {
            mSendNaviControlRunnable.isStop = true;
        }
    }

    /**
     * 控制云台转动
     */
    private static class SendNaviControlRunnable implements Runnable {
        //        private double mAngle;
        private float mX, mY;
        private boolean isRunning = false;
        public boolean isStop = false;

        public void startThread() {
            isRunning = true;
            Log.d(TAG, "startThread: 22222");
        }

        public void stopThread() {
            Log.d(TAG, "stopThread: 22222");
            isRunning = false;
        }

        public void setData(float x, float y) {
            Log.d(TAG, "setData: ");
            isRunning = true;
//            this.mAngle = angle;
            this.mX = x;
            this.mY = y;
        }

        @Override
        public void run() {
            while (!isStop) {
                if (isRunning) {
                    Log.d(TAG, "run: send command");
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

    public void syncShotSettings(boolean needStartPreview) {
        ConnectionManager.getInstance().syncShotSetting(new MoSynvShotSettingCallback() {
            @Override
            public void onSuccess(MoShotSetting shotSetting) {
                //指令回调统一放在子线程了
                AiCameraApplication.mApplication.mHandler.post(() -> {
                    if (mDyFPVCallback != null) {
                        mDyFPVCallback.syncShotSettings(shotSetting);
                    }
                });

                if (needStartPreview)
                    startPreview();
            }

            @Override
            public void onFailed() {
            }
        });
    }

    /**
     * 云台模式设置
     *
     * @param value 1: lock roll
     *              2: Lock Pitch&Roll
     *              3: FPV
     *              4: 全Lock
     */
    public void setModel(int value) {
        ConnectionManager.getInstance().setModel(value, new MoRequestCallback() {
            @Override
            public void onSuccess() {
                if (mDyFPVCallback != null) {
                    mDyFPVCallback.setModelSuccess(value);
                }
            }

            @Override
            public void onFailed() {
            }
        });
    }

    /**
     * 删除上一个片段
     */
    public void deleteLastFrag() {
        if (mDyFPVCallback != null) {
            mDyFPVCallback.deleteLastFrag();
        }
    }

    public void finish() {
        if (mDyFPVCallback != null) {
            mDyFPVCallback.finishActivity();
        }
    }

    public void record() {
        if (mDyFPVCallback == null || mCameraDisplay == null) {
            return;
        }
        mDyFPVCallback.record(mCameraDisplay.isRecording());
    }

    public boolean hasRecordVideo() {
        if (mCameraDisplay == null) {
            return false;
        }
        return mCameraDisplay.getSegmentSize() > 0;
    }

    public void concatVideo(DyCommonDialog dyCommonDialog) {
        Logcat.v().tag(LogcatConstants.DOUYIN).msg("start concat video").out();
        mIsCompressing = true;
        stopPreview();
        mDialog = dyCommonDialog;
        mDialog.setCompressText("合成中...");
        mDialog.setAllowCancel(false);
        mDialog.showCompressingLayout();
        if (!mIsConcatFinished) {
            Log.w(TAG, "Last concat hasn't finished");
            Logcat.v().tag(LogcatConstants.DOUYIN).msg("Last concat hasn't finished").out();
            return;
        }
        if (mCameraDisplay == null) {
            Logcat.v().tag(LogcatConstants.DOUYIN).msg("camera display is null").out();
            return;
        }
        if (mCameraDisplay.isRecording()) {
            mCameraDisplay.stopRecord();
            if (mDyFPVCallback != null) {
                mDyFPVCallback.record(mCameraDisplay.isRecording());
            }
        }
        if (mCameraDisplay.getSegmentSize() < 0) {
            Log.d(TAG, "nothing record ");
            Logcat.v().tag(LogcatConstants.DOUYIN).msg("nothing record").out();
            return;
        }
        mCameraDisplay.concat(new TERecorder.OnConcatFinishedListener() {
            @Override
            public void onConcatFinished(final int i) {
                AppExecutors.getInstance().mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        mIsConcatFinished = true;
                        Log.d(TAG, "concatVideo2: 合成返回码 " + i);
                        Logcat.v().tag(LogcatConstants.DOUYIN).msg("concat video 合成返回码 " + i).out();
//                        String s = "concat time 111: " + (System.currentTimeMillis() - time);
//                        FileUtil.writeFileToSDCard(FileUtil.path, s.getBytes(), "concat.txt", true, true, false);
                        initEditor(mCameraDisplay.getConcatVideoPath(), mCameraDisplay.getConcatWavPath(),
                                mCameraDisplay.getMusicPath() != null, 0, mCameraDisplay.getTotalRecordingTime(),
                                mCameraDisplay.getBGMType());
                    }
                });
            }
        });
        mIsConcatFinished = false;
    }

    String mVideoSavePath;

    private void initEditor(String videoPath, String audioPath, boolean isMusic,
                            int mBackgroundMusicTrimIn, long mBackgroundMusicTrimOut, int mBackgroundMusicLoopType) {
        Logcat.v().tag(LogcatConstants.DOUYIN).msg("initEditor").out();
//        mVideoSavePath = Constants.dyGalleryLocalPath + "/" + System.currentTimeMillis() + ".mp4";
        mVideoSavePath = FileUtils.getDyVideoPath(mContext.get()) + "/" + System.currentTimeMillis() + ".mp4";
        long time = System.currentTimeMillis();
        Log.d(TAG, "initEditor: start");
        Log.d(TAG, "initEditor: video: " + videoPath + ", audio: " + audioPath
                + ", is music: " + isMusic + ", mBackgroundMusicTrimIn: " + mBackgroundMusicTrimIn
                + ", mBackgroundMusicTrimOut: " + mBackgroundMusicTrimOut + ", mBackgroundMusicLoopType: " + mBackgroundMusicLoopType);
//        mSynthesisDialog.setMessage("保存中");
        if (mEditor == null)
            mEditor = new VEEditor(FileUtils.ROOT_DIR);
        String mBackgroundMusic = "";
        if (isMusic) {
            mBackgroundMusic = audioPath;
        }

        String[] audioPaths = isMusic ? null : new String[]{audioPath};
        String[] videoPaths = new String[]{videoPath};
        String[] transitions = new String[]{TEDefine.TETransition.FADE};

        int ret = mEditor.init(videoPaths, null, audioPaths, VEEditor.VIDEO_RATIO.VIDEO_OUT_RATIO_ORIGINAL);
        Log.d(TAG, "initEditor: 11111 ret: " + ret);
//        mActivity.showToast("开始合成--111111");

        if (ret != VEResult.TER_OK) {
            final int result = ret;
//            mActivity.showToast("所有文件都不好使啊！！！");
            Log.d(TAG, "initEditor: 所有文件都不好使啊！！！");
            Logcat.v().tag(LogcatConstants.DOUYIN).msg("initEditor: all files are bad!").out();
//            Toast.makeText(this,  + result, Toast.LENGTH_SHORT).show();
            if (mEditor != null) {
                mEditor.destroy();
                mEditor = null;
            }
            return;
        }

//        mEditor.setLoopPlay(true);
        mEditor.setScaleMode(VEEditor.SCALE_MODE.SCALE_MODE_CENTER_CROP);

        mEditor.prepare();

        mEditor.addAudioTrack(mBackgroundMusic, mBackgroundMusicTrimIn, (int) mBackgroundMusicTrimOut, mBackgroundMusicLoopType == 1);
//        mEditor.seek(0, VEEditor.SEEK_MODE.EDITOR_SEEK_FLAG_LastSeek);
//        mEditor.play();
        mNeedPrepare = false;

//        mActivity.showToast("开始合成--222222");
        Log.d(TAG, "initEditor: 222222");

        mEditor.setOnInfoListener(new VECommonCallback() {
            @Override
            public void onCallback(int what, int ext, float f, String s) {
                switch (what) {
                    case VECommonCallbackInfo.TE_INFO_COMPILE_PROGRESS:
                        mTotalProgress = (int) (f * 100 * 0.5);
                        AppExecutors.getInstance().mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                if (checkActivity()) {
                                    Logcat.v().tag(LogcatConstants.DOUYIN).msg("onCallback: 合成中 = " + mTotalProgress).out();
                                    String s = String.format(mActivity.get().getResourceToString(R.string.dy_compress_progress), mTotalProgress) + "%";
                                    mDialog.setCompressText(s);
                                }
                            }
                        });
                        Logcat.v().tag(LogcatConstants.DOUYIN).msg("concat progresss  " + mTotalProgress).out();
                        break;
                    case VECommonCallbackInfo.TE_INFO_COMPILE_DONE:
                        Logcat.v().tag(LogcatConstants.DOUYIN).msg("concat done start ").out();
                        mCameraDisplay.deleteFile();
                        mNeedPrepare = true;
                        mEditor.destroy();
                        mEditor = null;
                        startMark();
                }
            }
        });
        mEditor.setOnErrorListener(new VECommonCallback() {
            @Override
            public void onCallback(int ret, int ext, float f, String s) {
                ToastUtil.showToast(AiCameraApplication.getContext(), "error:" + s);
            }
        });

        compress();
    }

    private void startMark() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Logcat.v().tag(LogcatConstants.DOUYIN).msg("onCallback: 合成完成开始添加水印").out();
                WaterMarker.builder().setPath(mVideoSavePath).setShowLoading(false).
                        setResultListener(new WaterMarker.ResultListener() {
                            @Override
                            public void onSuccess(String path) {
                                AppExecutors.getInstance().mainThread().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!FileUtils.isAppInstalled()) {
                                            enterDyAPP(1);
                                            if (checkActivity())
                                                showNotInstall();
                                            mDialog.setAllowCancel(true);
                                            mDialog.dismiss();
                                            mIsCompressing = false;
                                            return;
                                        }
                                        if (checkActivity())
                                            mDialog.setCompressText(mActivity.get().getResourceToString(R.string.dy_compress_success));
                                        Logcat.v().tag(LogcatConstants.DOUYIN).msg("onCallback: 添加水印完成: " + path).out();
                                        ShareManager.ShareEntity entity = new ShareManager.ShareEntity();
                                        entity.setThumbUrl(mVideoSavePath);
                                        MediaScannerUtil.scanFileAsync(mActivity.get(), mVideoSavePath);
                                        ShareManager.getInstance().shareToDouyin(mActivity.get(), Share.VIDEO, entity);
                                        mDialog.setAllowCancel(true);
                                        mDialog.dismiss();
                                        mTotalProgress = 0;
                                        mIsCompressing = false;
                                        if (checkActivity() && mActivity.get().mIsDisConnect)
                                            mActivity.get().finishFPV();
                                    }
                                });
                            }

                            @Override
                            public void onFailure(String err) {
                                mIsCompressing = false;
                                Logcat.v().tag(LogcatConstants.DOUYIN).msg("onCallback: 添加水印失败" + err).out();
                            }

                            @Override
                            public void onProgress(int progress, int type) {
                                if (type == 1) {
                                    AppExecutors.getInstance().mainThread().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            mTotalProgress = (int) (progress * 0.25 + 50);
                                            if (checkActivity()) {
                                                String s = String.format(mActivity.get().getResourceToString(R.string.dy_compress_progress), mTotalProgress) + "%";
                                                mDialog.setCompressText(s);
                                            }
                                        }
                                    });
                                } else if (type == 2) {
                                    AppExecutors.getInstance().mainThread().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            mTotalProgress = (int) (progress * 0.25 + 75);
                                            if (checkActivity()) {
                                                String s = String.format(mActivity.get().getResourceToString(R.string.dy_compress_progress), mTotalProgress) + "%";
                                                mDialog.setCompressText(s);
                                            }
                                        }
                                    });
                                }
                            }
                        }).buildStart();
                Logcat.v().tag(LogcatConstants.DOUYIN).msg("onCallback: 合成完成添加水印").out();
            }
        });
    }

    public void compress() {
        VEVideoEncodeSettings setting = new VEVideoEncodeSettings.Builder(USAGE_COMPILE)
                .setCompileType(COMPILE_TYPE_MP4)
//                .setVideoRes(1920, 1080)
                .setVideoRes(1080, 1920)
                .setHwEnc(true)
                .setGopSize(30)
                .setVideoBitrate(VEVideoEncodeSettings.ENCODE_BITRATE_MODE.ENCODE_BITRATE_ABR, 4 * 1024 * 1024)
                .setFps(25)
                .build();
        mEditor.compile(mVideoSavePath, null, setting);
        Logcat.v().tag(LogcatConstants.DOUYIN).msg("start compress").out();
    }

    public void destroy() {
        releaseNaviThread();
        EffectPlatform.getInstance().setDownloadListener(null);
        if (mActivity != null) {
            mActivity = null;
        }
        if (mContext != null) {
            mContext = null;
        }
        if (mCameraDisplay != null) {
            mCameraDisplay = null;
        }
        if (mDyFPVCallback != null) {
            mDyFPVCallback = null;
        }
        if (mDialog != null) {
            mDialog = null;
        }
    }

    public boolean isCompressing() {
        return mIsCompressing;
    }

    private boolean checkActivity() {
        return mActivity != null && mActivity.get() != null;
    }

    public void showNotInstall() {
        if (checkActivity())
            CameraToastUtil.show90(mActivity.get().getResources().getString(R.string.not_install_dy), mActivity.get());
    }
}
