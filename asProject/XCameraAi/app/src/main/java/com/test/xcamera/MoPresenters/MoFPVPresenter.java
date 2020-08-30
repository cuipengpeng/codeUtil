package com.test.xcamera.MoPresenters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import com.test.xcamera.R;
import com.test.xcamera.accrssory.AccessoryManager;
import com.test.xcamera.activity.DyFPVActivity;
import com.test.xcamera.activity.MoFPVSettingActivity;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.bean.MoImage;
import com.test.xcamera.bean.MoSettingModel;
import com.test.xcamera.bean.MoShotSetting;
import com.test.xcamera.bean.MoSystemInfo;
import com.test.xcamera.enumbean.WorkState;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.managers.WorkStateManager;
import com.test.xcamera.moalbum.activity.MyAlbumList;
import com.test.xcamera.mointerface.MoFPVCallback;
import com.test.xcamera.mointerface.MoGetBitmapFormGLCallback;
import com.test.xcamera.mointerface.MoRequestCallback;
import com.test.xcamera.mointerface.MoStartTakeVideoCallback;
import com.test.xcamera.mointerface.MoSyncSettingCallback;
import com.test.xcamera.mointerface.MoSynvShotSettingCallback;
import com.test.xcamera.mointerface.MoSystemInfoCallback;
import com.test.xcamera.mointerface.MoTakeVideoCallback;
import com.test.xcamera.mointerface.MoTaklePhotoCallback;
import com.test.xcamera.utils.BitmapUtils;
import com.test.xcamera.glview.PreviewGLSurfaceView;
import com.test.xcamera.utils.DlgUtils;
import com.test.xcamera.utils.StringUtil;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL10;

/**
 * FPV页面业务请求实现
 * Created by zll on 2019/10/23.
 */

public class MoFPVPresenter {
    private static final String TAG = "MoFPVPresenter";
    private MoFPVCallback mFPVCallback;
    private boolean isBeautyTakePhoto;
    private boolean mRefrushSDCardFlag = false;

    public MoFPVPresenter(MoFPVCallback listener) {
        mFPVCallback = listener;
    }

    /**
     * 开启预览
     */
    public void startPreview() {
        ConnectionManager.getInstance().startPreview(new MoRequestCallback() {
            @Override
            public void onSuccess() {
                mFPVCallback.startPreviewSuccess();
            }

            @Override
            public void onFailed() {
                mFPVCallback.requestFailed();
            }
        });
    }

    /**
     * 关闭预览
     */
    public void stoPreview() {
        ConnectionManager.getInstance().stopPreview(new MoRequestCallback() {
            @Override
            public void onSuccess() {
                mFPVCallback.requestSuccess();
            }

            @Override
            public void onFailed() {
                mFPVCallback.requestFailed();
            }
        });
    }

    /**
     * 切换拍摄模式
     *
     * @param mode 拍摄模式的字段
     */
    public void changeShotMode(final String mode) {
        if (AccessoryManager.getInstance().mIsRunning)
            WorkStateManager.getInstance().setSwitchMode(true);
        ConnectionManager.getInstance().stopPreview(new MoRequestCallback() {
            @Override
            public void onSuccess() {
                Log.d("BEAUTY_TEST_LOG", "onSuccess: stopPreview mode = " + mode);
                ConnectionManager.getInstance().switchMode(mode, new MoRequestCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("BEAUTY_TEST_LOG", "onSuccess:switchMode  mode = " + mode);

                        mFPVCallback.changeShotModeSuccess();
                    }

                    @Override
                    public void onFailed() {
                        Log.d("BEAUTY_TEST_LOG", "onSuccess:switchMode  mode = " + mode);
                        WorkStateManager.getInstance().setSwitchMode(false);
                        mFPVCallback.requestFailed();
                        AiCameraApplication.mApplication.mHandler.post(() -> {
                            DlgUtils.toast(AiCameraApplication.getContext(), StringUtil.getStr(R.string.change_mode_fail));
                        });
                    }
                });
            }

            @Override
            public void onFailed() {
                WorkStateManager.getInstance().setSwitchMode(false);
            }
        });

    }

    /**
     * 拍照
     */
    public void takePhoto() {
        WorkStateManager.getInstance().setmWorkState(WorkState.PHOTOING);
        ConnectionManager.getInstance().takePhoto(new MoTaklePhotoCallback() {
            @Override
            public void onSuccess(MoImage image) {
                mFPVCallback.takePhotoResult(image);
            }

            @Override
            public void onFailed(int reason) {
                mFPVCallback.requestReason(reason);
                mFPVCallback.requestFailed(MoFPVCallback.PHOTO_STOP_FAILED);
                WorkStateManager.getInstance().setmWorkState(WorkState.STANDBY);
            }
        });
    }

    private Runnable mRefrushSDCard = new Runnable() {
        @Override
        public void run() {
            if (mRefrushSDCardFlag) {
                syncSystemInfo();
                AiCameraApplication.mApplication.mHandler.postDelayed(mRefrushSDCard, 2500);
            }
        }
    };

    /**
     * 开始录视频
     */
    public void startTakeVideo() {
        ConnectionManager.getInstance().takeVideoStart(new MoStartTakeVideoCallback() {

            @Override
            public void onSuccess(String uri, int reason) {
                if (!mRefrushSDCardFlag) {
                    mRefrushSDCardFlag = true;
                    AiCameraApplication.mApplication.mHandler.postDelayed(mRefrushSDCard, 1500);
                }
                AiCameraApplication.mApplication.mHandler.post(() -> {
                    mFPVCallback.startTakeVideoSuccess(uri, reason);
                });
            }

            @Override
            public void onFailed(int reason) {
                mFPVCallback.requestReason(reason);
                AiCameraApplication.mApplication.mHandler.post(() -> {
                    mFPVCallback.takeVideoResult(null);
//                    mFPVCallback.requestFailed(MoFPVCallback.RECORD_START_FAILED);
                });
            }
        });
    }

    public void stopRefrushVideo() {
        mRefrushSDCardFlag = false;
        AiCameraApplication.mApplication.mHandler.removeCallbacks(mRefrushSDCard);
    }

    public void restartRefrushVideo() {
        if (!mRefrushSDCardFlag) {
            mRefrushSDCardFlag = true;
            AiCameraApplication.mApplication.mHandler.postDelayed(mRefrushSDCard, 1000);
        }
    }

    /**
     * 结束录视频
     */
    public void stopTakeVideo() {
        mFPVCallback.takeVideoResultImm();
        ConnectionManager.getInstance().takeVideoStop(new MoTakeVideoCallback() {
            @Override
            public void onSuccess(MoImage image) {
                mRefrushSDCardFlag = false;
                AiCameraApplication.mApplication.mHandler.removeCallbacks(mRefrushSDCard);
                AiCameraApplication.mApplication.mHandler.post(() -> {
                    mFPVCallback.takeVideoResult(image);
                });
            }

            @Override
            public void onFailed() {
                takeVideoFailed();
            }
        });
    }

    public void takeVideoFailed() {
        mRefrushSDCardFlag = false;
        AiCameraApplication.mApplication.mHandler.removeCallbacks(mRefrushSDCard);
        AiCameraApplication.mApplication.mHandler.post(() -> {
            mFPVCallback.takeVideoResult(null);
        });
    }

    /**
     * 数码变焦
     *
     * @param level 变焦倍率
     */
    public void digitalZoom(int level) {
        ConnectionManager.getInstance().digitalZoom(level, new MoRequestCallback() {
            @Override
            public void onSuccess() {
                mFPVCallback.requestSuccess();
            }

            @Override
            public void onFailed() {
                mFPVCallback.requestFailed();
            }
        });
    }

    /**
     * 开始自动跟踪
     */
    public void startAutoTrack() {
//        ConnectionManager.getInstance().startTrack(new MoRequestCallback() {
//            @Override
//            public void onSuccess() {
//                mFPVCallback.requestSuccess();
//                WorkStateManager.getInstance().setTrack(true);
//            }
//
//            @Override
//            public void onFailed() {
//                mFPVCallback.requestFailed();
//                WorkStateManager.getInstance().setTrack(false);
//            }
//        });
    }

    /**
     * 画面翻转
     *
     * @param type 0:后置拍照  1:自拍
     */
    public void rotateCamera(int type) {
        ConnectionManager.getInstance().setSelfie(type, new MoRequestCallback() {
            @Override
            public void onSuccess() {
                mFPVCallback.requestSuccess();
            }

            @Override
            public void onFailed() {
                mFPVCallback.requestFailed();
            }
        });
    }

    /**
     * 获取画框跟踪的帧数据
     *
     * @param previewTextureView
     * @return
     */
    public void getTrackBitmap(final PreviewGLSurfaceView previewTextureView, final MoGetBitmapFormGLCallback callback) {
        if (previewTextureView != null) {
//            final Bitmap bitmap = null;//previewTextureView.getBitmap();
            previewTextureView.queueEvent(new Runnable() {
                @Override
                public void run() {
                    EGL10 egl = (EGL10) EGLContext.getEGL();
                    GL10 gl = (GL10) egl.eglGetCurrentContext().getGL();
                    final Bitmap bitmap = BitmapUtils.createBitmapFromGLSurface(0, 0,
                            previewTextureView.getWidth(), previewTextureView.getHeight(), gl);
                    // 640 360
                    Bitmap newBitmap = BitmapUtils.scaleBitmap(bitmap, 640, 360);

                    byte[] bgr = BitmapUtils.bitmap2BGR(newBitmap);
                    if (callback != null) {
                        callback.onBGRDataReady(bgr);
                    }
                }
            });
        }
    }

    /**
     * 开始画框跟踪
     *
     * @param values 框的位置信息
     * @param width  预览宽
     * @param height 预览高
     * @param data   bgr数据
     */
//    public void startFingerTrack(int[] values, int width, int height, byte[] data) {
//        ConnectionManager.getInstance().trackRect(values, width, height, data);
//        WorkStateManager.getInstance().setTrack(true);
//    }

    /**
     * 跳转到相册页
     *
     * @param context
     */
    public void startAlbumActivity(Context context) {
        if (isBeautyTakePhoto) {
            //如果是在beauty模式下,拍了照片,这个时候跳转到我的相册页面,进入的是app相册
            MyAlbumList.openThis(context, "beauty");
        } else {
            MyAlbumList.openThis(context, "fpv");
        }


//        ShootMode shootMode = ShotModeManager.getInstance().getmShootMode();
//        Intent intent = null;
//        if (shootMode == ShootMode.PHOTO_BEAUTY || shootMode == ShootMode.VIDEO_BEAUTY) {
//            intent = new Intent(context, LocalFilterMediaListActivity.class);
//        } else {
//            intent = new Intent(context, CameraPhotoListActivity.class);
//        }
//        intent.putExtra("jump_flag", "mo_preview");
//        context.startActivity(intent);
//
        //  startAct(AlbumListActivity.class);
    }

    public void startMoreSettingActivity(Context context) {
        Intent intent = new Intent(context, MoFPVSettingActivity.class);
//        Intent intent = new Intent(context, LocalFilterMediaListActivity.class);
        context.startActivity(intent);
    }

    /**
     * 获取系统信息
     */
    public void syncSystemInfo() {
        ConnectionManager.getInstance().getSystemInfo(new MoSystemInfoCallback() {
            @Override
            public void onSuccess(MoSystemInfo systemInfo) {
                mFPVCallback.getSystemInfo(systemInfo);
            }

            @Override
            public void onFailed() {
                mFPVCallback.requestFailed();
            }
        });
    }

    /**
     * 同步拍摄设置
     * <p>
     * 会调用两次 以后优化
     */
    public void syncShotSettings() {
        ConnectionManager.getInstance().syncShotSetting(new MoSynvShotSettingCallback() {
            @Override
            public void onSuccess(MoShotSetting shotSetting) {
                mFPVCallback.syncShotSettings(shotSetting);
            }

            @Override
            public void onFailed() {
                WorkStateManager.getInstance().setSwitchMode(false);
            }
        }, new MoSyncSettingCallback() {
            @Override
            public void onSuccess(MoSettingModel model) {
                AiCameraApplication.mApplication.mHandler.post(() -> {
                    mFPVCallback.syncShotSettingsEx(model);
                });
            }
        });
    }

    /**
     * 用于同步更多设置中的数据
     */
    public void syncParamSetting() {
        ConnectionManager.getInstance().syncSetting(new MoSyncSettingCallback() {
            @Override
            public void onSuccess(MoSettingModel model) {
                mFPVCallback.syncShotSettingsEx(model);
            }
        });
    }

    /**
     * 控制云台转动
     *
     * @param x
     * @param y
     */
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
//
//        if (angle >= 0 && angle <= 90) {
//            director_x = 3;
//            director_y = 2;
//        } else if (angle > 90 && angle <= 180) {
//            director_x = 1;
//            director_y = 2;
//        } else if (angle > 180 && angle <= 270) {
//            director_x = 1;
//            director_y = 0;
//        } else {
//            director_x = 3;
//            director_y = 0;
//        }
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

    /**
     * 录制中添加mark
     *
     * @param uri
     * @param time
     */
    public void markVideo(String uri, long time) {
        ConnectionManager.getInstance().markVideo(uri, time, new MoRequestCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailed() {

            }
        });
    }

    /**
     * 设置拍照倒计时
     *
     * @param time
     */
    public void setDelayPhotoTime(int time) {
        ConnectionManager.getInstance().setDelayTime(time, new MoRequestCallback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailed() {
            }
        });
    }

    /**
     * 专业模式设置--色彩
     *
     * @param value
     */
    public void setColor(int value) {
        ConnectionManager.getInstance().setColor(value, new MoRequestCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailed() {

            }
        });
    }

    /**
     * 专业模式设置--云台速度
     *
     * @param value
     */
    public void setTrackSpeed(int value) {
        ConnectionManager.getInstance().setSpeed(value, new MoRequestCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailed() {

            }
        });
    }

    /**
     * 专业模式设置--电子防抖
     *
     * @param value
     */
    public void setDis(int value) {
        ConnectionManager.getInstance().setDis(value, new MoRequestCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailed() {

            }
        });
    }

    /**
     * 专业模式设置--宽动态
     *
     * @param value
     */
    public void setDynamicRange(int value) {
        ConnectionManager.getInstance().setDynamicRange(value, new MoRequestCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailed() {

            }
        });
    }

    /**
     * 专业模式设置--超高画质
     *
     * @param value
     */
    public void setSuperHighQuality(int value) {
        ConnectionManager.getInstance().setSuperHighQuality(value, new MoRequestCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailed() {

            }
        });
    }

    /**
     * 专业模式设置--格式
     *
     * @param value
     */
    public void setFormat(int value) {
        ConnectionManager.getInstance().setFormat(value, new MoRequestCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailed() {

            }
        });
    }

    /**
     * 专业模式设置--ISO
     *
     * @param value
     */
    public void setISO(int value) {
        ConnectionManager.getInstance().setISOValue(value, new MoRequestCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailed() {

            }
        });
    }

    /**
     * 专业模式设置--EV
     *
     * @param value
     */
    public void setEV(int value) {
        ConnectionManager.getInstance().setEV(value, new MoRequestCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailed() {

            }
        });
    }

    /**
     * 专业模式设置--Shutter
     *
     * @param value
     */
    public void setShutter(int value) {
        ConnectionManager.getInstance().setShutter(value, new MoRequestCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailed() {

            }
        });
    }

    /**
     * 专业模式设置--AWB
     *
     * @param value
     */
    public void setAWB(int value) {
        ConnectionManager.getInstance().setAwb(value, new MoRequestCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailed() {

            }
        });
    }

    /**
     * 设置录视频的帧率，分辨率
     *
     * @param resolution
     * @param fps
     */
    public void takeVideoSetting(final int resolution, final int fps) {
        mFPVCallback.setResolFps(resolution, fps);
    }

    public void setIsBeauty(boolean isBeautyTakePhoto) {
        this.isBeautyTakePhoto = isBeautyTakePhoto;
    }

    public void startDyFPV(Context context) {
        Intent intent = new Intent(context, DyFPVActivity.class);
        context.startActivity(intent);
    }
}
