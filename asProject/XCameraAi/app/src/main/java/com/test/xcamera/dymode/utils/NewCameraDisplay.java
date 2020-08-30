package com.test.xcamera.dymode.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import com.bef.effectsdk.message.MessageCenter;
import com.test.xcamera.activity.DyFPVActivity;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.constants.LogcatConstants;
import com.test.xcamera.dymode.callbacks.H264DecodeListener;
import com.test.xcamera.dymode.model.TimeSpeedModel;
import com.test.xcamera.dymode.view.AutoFitTextureView;
import com.test.xcamera.player.AVFrame;
import com.test.xcamera.utils.AppExecutors;
import com.moxiang.common.logging.Logcat;
import com.ss.android.medialib.model.EnigmaResult;
import com.ss.android.vesdk.TERecorder;
import com.ss.android.vesdk.VEAudioEncodeSettings;
import com.ss.android.vesdk.VECameraSettings;
import com.ss.android.vesdk.VEListener;
import com.ss.android.vesdk.VEPreviewSettings;
import com.ss.android.vesdk.VESize;
import com.ss.android.vesdk.VEVideoEncodeSettings;
import com.ss.android.vesdk.faceinfo.VEFaceAttributeInfo;
import com.ss.android.vesdk.faceinfo.VEFaceDetectInfo;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.view.OrientationEventListener.ORIENTATION_UNKNOWN;
import static com.ss.android.vesdk.VEVideoEncodeSettings.USAGE_RECORD;

@TargetApi(21)
public class NewCameraDisplay implements TextureView.SurfaceTextureListener {
    private String TAG = "CameraDisplay";
    public static boolean mSurfaceDestoryed = true;
    private final int CORE_POOL_SIZE = 9;//核心线程数
    private final int MAX_POOL_SIZE = 10;//最大线程数
    private final long KEEP_ALIVE_TIME = 10;//空闲线程超时时间

    private final Handler mMainHandler;

    private AutoFitTextureView mTextureView;
    private SurfaceTexture mSurfaceTexture;
    private Surface mSurface;

    private boolean backCamera = true;

    private SensorManager mSensorManager;
    private Sensor mSensor;

    private SensorListener mSensorListener;

    private boolean mIsChangingCamera = false;

    private boolean mHasStoped = true;
    private boolean mIsRecording = false;

    private String mMusicPath;

    private String mConcatVideoPath;
    private String mConcatWavPath;

    private float mCurrentSpeed = 1.0f;
    private long mTotalRecordingTime = 0;

    private WeakReference<DyFPVActivity> mAttachedActivity;
    private VEVideoEncodeSettings mVideoEncodeSettings;
    private VEAudioEncodeSettings mAudioEncodeSettings;
    private VEPreviewSettings mPreviewSettings;
    private TERecorder mRecorder;

    private TERecorder.Texture mTexture;

    private int bgmType = 1;

    private boolean fromCreated = false;

    private boolean mIsCameraOpenedAndRecorderInited = false;

    private H264Decoder mH264Decoder;

//    private InitOkCallback mInitOkCallback;

    public ThreadPoolExecutor mExecutorPool;

    private boolean mIsDestoty = false;

//    private RecordmillsThread mRecordmillsThread;

    private VEListener.VERecorderStateExtListener mNativeInitListener = new VEListener.VERecorderStateExtListener() {

        @Override
        public void onInfo(int infoType, int ext, String msg) {

        }

        @Override
        public void onError(int ret, String msg) {

        }

        @Override
        public void onNativeInit(int ret, String msg) {
            mMainHandler.post(new Runnable() {          // 如果刚初始化recorder之后就立刻立刻销毁recorder（点亮屏幕、熄灭屏幕），初始化回调回来的时候就撞上recorder == null 的问题
                @Override
                public void run() {
                    if (mRecorder == null) {
                        return;
                    }
                    int result = mRecorder.slamDeviceConfig(
                            true
                            , true
                            , true
                            , true);
                    Log.e("tag", result + "");

                    /** 2019年03月31日 星期日
                     * 使用TERecorder.updateRotation 作用于所有贴纸。防止设备旋转90度手势贴纸不旋转, 旋转180度, 手势贴纸直接不生效的问题
                     */
                    initRotationListenerForNormalSticker();
                    mRecorder.setMessageListener(new MessageCenter.Listener() {
                        @Override
                        public void onMessageReceived(int messageType, int arg1, int arg2, String arg3) {
                            Log.e(TAG, "mess: " + messageType + "arg1 : " + arg1 + "arg2: " + arg2 + "arg3:" + arg3);
                            if (messageType == MessageCenter.MSG_TYPE_TYPE_ENIGMA && scanning) {
                                EnigmaResult result = mRecorder.getEnigmaResult();
                                if (result != null) {
                                    Log.e(TAG, "mess result: " + result.getResult()[0].getText());
                                }
                                scanning = false;
                                mRecorder.enableRequirement(false, TERecorder.Requirement.REQUIREMENT_ENIGMA_DETECT);
                            }
                        }
                    });
                    mRecorder.enableSceneRecognition(true);
                }
            });
        }

        @Override
        public void onHardEncoderInit(boolean success) {

        }
    };

    private void initRotationListenerForNormalSticker() {
        acceleratorSensorForNormalSticker = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(new SensorEventListener() {

            private static final int _DATA_X = 0;
            private static final int _DATA_Y = 1;
            private static final int _DATA_Z = 2;
            private int mOrientation = ORIENTATION_UNKNOWN;

            @Override
            public void onSensorChanged(SensorEvent event) {

                float[] values = event.values;
                int orientation = ORIENTATION_UNKNOWN;
                float X = -values[_DATA_X];
                float Y = -values[_DATA_Y];
                float Z = -values[_DATA_Z];
                float magnitude = X * X + Y * Y;
                // Don't trust the angle if the magnitude is small compared to the y value
                if (magnitude * 4 >= Z * Z) {
                    float OneEightyOverPi = 57.29577957855f;
                    float angle = (float) Math.atan2(-Y, X) * OneEightyOverPi;
                    orientation = 90 - Math.round(angle);
                    // normalize to 0 - 359 range
                    while (orientation >= 360) {
                        orientation -= 360;
                    }
                    while (orientation < 0) {
                        orientation += 360;
                    }
                }
                if (orientation != mOrientation) {
                    mOrientation = orientation;
                    if (orientation <= 45 || orientation > 315) {
                        orientation = 0;
//                        orientation = 90;
                    }
                    if (orientation > 45 && orientation <= 135) {
                        orientation = 90;
//                        orientation = 180;
                    }
                    if (orientation > 135 && orientation <= 225) {
                        orientation = 180;
//                        orientation = 270;
                    }
                    if (orientation > 225) {
                        orientation = 270;
//                        orientation = 0;
                    }
//                    Log.d("updateRotationTest", String.valueOf(orientation));
                    if (mRecorder != null) {
                        mRecorder.updateRotation(0, 0, orientation);
                    }
                }
            }


            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        }, acceleratorSensorForNormalSticker, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public int getBGMType() {
        return bgmType;
    }

    public void setMusic(String musicPath) {
        this.mMusicPath = musicPath;
        if (mRecorder == null) return;
        mRecorder.setRecordBGM(musicPath, 0, bgmType);
    }

    private Sensor acceleratorSeneor;
    private Sensor gravitySensor;
    private Sensor rotationVectorSensor;
    private Sensor gyroscopeSensor;

    private Sensor acceleratorSensorForNormalSticker;

    private List<TimeSpeedModel> mRecordSegmentTimeInfo = new ArrayList<>();

    private SensorEventListener acceleratorListener = new SensorEventListener() {

        private static final int _DATA_X = 0;
        private static final int _DATA_Y = 1;
        private static final int _DATA_Z = 2;

        private int mOrientation = ORIENTATION_UNKNOWN;

        @Override
        public void onSensorChanged(SensorEvent event) {
            double timestamp = getTimestamp(event);
            if (mRecorder != null) {
                mRecorder.slamProcessIngestAcc(event.values[0], event.values[1], event.values[2], timestamp);
            }
        }


        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private SensorEventListener gyroscopListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            double timeStamp = getTimestamp(sensorEvent);
//            Log.e("gyroscopListener: ", " " + sensorEvent.values[0] + "," +
//                    sensorEvent.values[1] + ", " + sensorEvent.values[2]);
            if (mRecorder != null) {
                mRecorder.slamProcessIngestGyr(sensorEvent.values[0],
                        sensorEvent.values[1], sensorEvent.values[2], timeStamp);
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private SensorEventListener gravityListenser = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            double timeStamp = getTimestamp(sensorEvent);
//            Log.e("gravityListenser: ", " " + sensorEvent.values[0] + "," +
//                    sensorEvent.values[1] + ", " + sensorEvent.values[2]);
            if (mRecorder != null) {
                mRecorder.slamProcessIngestGra(sensorEvent.values[0],
                        sensorEvent.values[1], sensorEvent.values[2], timeStamp);
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private SensorEventListener rotationVectorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            double timeStamp = getTimestamp(event);
            float[] rawWRbs = new float[9];
            SensorManager.getRotationMatrixFromVector(rawWRbs, event.values);
            double[] wRb = new double[9];
            for (int i = 0; i < rawWRbs.length; i++) {
                wRb[i] = (double) rawWRbs[i];
            }
//            Log.e("wRbs", " " + wRb[0] + "," + wRb[1] + ", " + wRb[2] + ", " + wRb[3] + ", " + wRb[4] + ", " + wRb[5] + ", " + wRb[6]
//                    + ", " + wRb[7] + ", " + wRb[8] + "," + (double)timeStamp);
            if (mRecorder != null) {
                mRecorder.slamProcessIngestOri(wRb, timeStamp);
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private double getTimestamp(SensorEvent sensorEvent) {
        long cur_time_nano = System.nanoTime();
        long delta_nano_time = Math.abs(cur_time_nano - sensorEvent.timestamp);
        long delta_elapsed_nano_time = Math.abs(SystemClock.elapsedRealtimeNanos() - sensorEvent.timestamp);
        double timeStamp = cur_time_nano - Math.min(delta_nano_time, delta_elapsed_nano_time);
        return timeStamp;
    }


    public boolean isRecording() {
        return mIsRecording;
    }

    public String getConcatVideoPath() {
        return mConcatVideoPath;
    }

    /**
     * 获取连接后的音频文件
     *
     * @return 返回音频文件路径
     */
    public String getConcatWavPath() {
        return TextUtils.isEmpty(mMusicPath) ? mConcatWavPath : mMusicPath;
    }

    public void deleteLastFrag() {
        Log.d(TAG, "delete last frag !!!");
        if (mRecorder == null) return;

        if (mRecordSegmentTimeInfo.size() > 0) {
            mRecordSegmentTimeInfo.remove(mRecordSegmentTimeInfo.size() - 1);
            mTotalRecordingTime = TimeSpeedModel.calculateRealTime(mRecordSegmentTimeInfo);
            if (mAttachedActivity != null && mAttachedActivity.get() != null) {
                mAttachedActivity.get().updateProgressSegment(mRecordSegmentTimeInfo, 0, false);
            }
        }
        mRecorder.deleteLastFrag();
    }

    public long getEndFrameTime() {
        return mRecorder != null ? mRecorder.getEndFrameTime() : 0;
    }

    public long getTotalRecordingTime() {
        return mTotalRecordingTime;
    }

    /**
     * 道具
     *
     * @param s
     */
    public void switchEffect(String s) {
        if (mRecorder == null) return;
        mRecorder.switchEffect(s);
    }

    /**
     * 磨皮
     *
     * @param open
     * @param fBeautyIntensity
     */
    public void setSmooth(boolean open, float fBeautyIntensity) {
        if (mRecorder == null) return;
        if (open) {
            mRecorder.setBeautyFace(3, FileUtils.getDyResourceFaceBeautyDir(AiCameraApplication.getContext()));
            mRecorder.setBeautyFaceIntensity(fBeautyIntensity, 0.35f);
        } else {
            mRecorder.setBeautyFace(0, "");
        }
    }

    /**
     * @param path           路径，如果为""，则取消大眼瘦脸
     * @param eyeIntensity
     * @param cheekIntensity
     */
    public void setFaceReshape(String path, float eyeIntensity, float cheekIntensity) {
        if (mRecorder == null) {
            return;
        }
        mRecorder.setFaceReshape(path, eyeIntensity, cheekIntensity);
    }

    public void setFilter(String path) {
        if (mRecorder == null) {
            return;
        }
        mRecorder.setFilter(path, 1.0f);
    }

    public void setFilter(String path, float intensity) {
        if (mRecorder == null) {
            return;
        }
        mRecorder.setFilter(path, intensity);
    }

    public void setIntensityByType(int type, float intensity) {
        if (mRecorder == null) {
            return;
        }
        mRecorder.setFaceMakeupResource(FileUtils.getDyResourceFaceMakeupDir(AiCameraApplication.getContext()));
        int code = mRecorder.setIntensityByType(type, intensity);
//        String content = "code = " + code + ", type: " + type + ", intensity：" + intensity + "";
//        FileUtil.writeFileToSDCard(testPath, content.getBytes(), testName, true, true, false);
    }

    private void registerSensor() {
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        gyroscopeSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        acceleratorSeneor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gravitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        rotationVectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);

        mSensorManager.registerListener(mSensorListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);

        if (acceleratorSeneor != null) {
            mSensorManager.registerListener(acceleratorListener, acceleratorSeneor, SensorManager.SENSOR_DELAY_FASTEST);
        }
        if (gyroscopeSensor != null) {
            mSensorManager.registerListener(gyroscopListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
        if (gravitySensor != null) {
            mSensorManager.registerListener(gravityListenser, gravitySensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
        if (rotationVectorSensor != null) {
            mSensorManager.registerListener(rotationVectorListener, rotationVectorSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    private void unregisterListener() {
        mSensorManager.unregisterListener(mSensorListener);
        mSensorManager.unregisterListener(acceleratorListener);
        mSensorManager.unregisterListener(gyroscopListener);
        mSensorManager.unregisterListener(gravityListenser);
        mSensorManager.unregisterListener(rotationVectorListener);
    }

    public NewCameraDisplay(DyFPVActivity attachedActivity, AutoFitTextureView surfaceView) {
        mMainHandler = new Handler(Looper.getMainLooper());
//        mInitOkCallback = callback;
        mTextureView = surfaceView;
        mTextureView.setSurfaceTextureListener(this);
//        mTextureView.getHolder().addCallback(this);
//        mH264Decoder = new H264Decoder();
//        mH264Decoder.setSurface(mTextureView.getHolder().getSurface());
        mAttachedActivity = new WeakReference<>(attachedActivity);
        Log.d(TAG, "NewCameraDisplay: init NewCameraDisplay");
        mSensorManager = (SensorManager) attachedActivity.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mSensorListener = new SensorListener();
        fromCreated = true;

//        mRecordmillsThread = new RecordmillsThread();
//        mRecordmillsThread.setCurrentMillsCallback(currentMillsCallback);
//        mRecordmillsThread.start();
        initThreadPool();
    }

    private void initThreadPool() {
        mExecutorPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
                Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
    }

    public void onVideoDataAvailable(AVFrame avFrame) {
        if (mH264Decoder != null) {
            mH264Decoder.onReceived(avFrame);
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
//        FileUtil.writeFileToSDCard(FileUtil.path, "---onSurfaceTextureAvailable".getBytes(), "textureview.txt", true, true, false);
        mSurfaceDestoryed = false;
        if (mAttachedActivity != null && mAttachedActivity.get() != null) {
            mAttachedActivity.get().enterDyApp();
        }
        mIsCameraOpenedAndRecorderInited = initCamera();
//        if (mInitOkCallback != null) mInitOkCallback.initOK();
        if (mRecorder != null) {
            mExecutorPool.execute(addSlamRunnable);
        }
        mTextureView.setLayoutParams(mTextureView.getLayoutParams());
        mTextureView.requestLayout();
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mSurfaceDestoryed = true;
        return false;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private class SensorListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.values == null || event.values.length < 4) return;
            float[] quaternion = new float[4];

            quaternion[0] = event.values[0];
            quaternion[1] = event.values[1];
            quaternion[2] = event.values[2];
            quaternion[3] = event.values[3];

            Log.v("FYF", "quaternion " + quaternion[0] + " " + quaternion[1] + " " + quaternion[2]
                    + " " + quaternion[3]);

            if (null != mRecorder) {
                mRecorder.setDeviceRotation(quaternion);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    /**
     * 将多短文件拼接成连续的文件，包含音频和视频
     */
    public void concat(TERecorder.OnConcatFinishedListener listener) {
        if (mRecorder == null) return;
        String parentPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        mConcatVideoPath = new File(parentPath, "demoVideo.mp4").getAbsolutePath();
        mConcatWavPath = new File(parentPath, "demoAudio.wav").getAbsolutePath();
        mRecorder.concatAsync(
                mConcatVideoPath,
                mConcatWavPath,
                listener
        );
    }

    public void deleteFile() {
        if (!TextUtils.isEmpty(mConcatVideoPath))
            com.test.xcamera.utils.FileUtils.deleteFile(mConcatVideoPath);
        if (!TextUtils.isEmpty(mConcatWavPath))
            com.test.xcamera.utils.FileUtils.deleteFile(mConcatWavPath);
    }

    private Runnable addSlamRunnable = new Runnable() {
        @Override
        public void run() {
            mRecorder.addSlamDetectListener(new TERecorder.OnSlamDetectListener() {
                @Override
                public void onSlam(boolean isOpen) {
                    if (isOpen) {
                        registerSensor();
                    }
                }
            });
            mSensorManager.registerListener(mSensorListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    };

    public void onPause() {
        if (mIsRecording) {
            if (mAttachedActivity != null && mAttachedActivity.get() != null) {
                mAttachedActivity.get().clickShot();
            }
        }
        unregisterListener();
        if (mH264Decoder != null) {
            mH264Decoder.unInit();
            mH264Decoder = null;
        }
        if (mRecorder != null) {
            mRecorder.stopPreview();
            mRecorder.setNativeInitListener(null);
            mRecorder.setRenderCallback(null);
            mRecorder.destroy();
        }
        mRecorder = null;
        if (!mIsDestoty) {
            mExecutorPool.execute(pauseRunnable);
        }
    }

    private Runnable pauseRunnable = new Runnable() {
        @Override
        public void run() {
            if (mRecorder != null) {
                mRecorder.stopPreview();
                mRecorder.setNativeInitListener(null);
                mRecorder.setRenderCallback(null);
                mRecorder.destroy();
            }
            mRecorder = null;
            mSensorManager.unregisterListener(mSensorListener);
        }
    };

    public void release() {
        mIsDestoty = true;
        if (mExecutorPool != null)
            mExecutorPool.shutdownNow();
    }

    private void genPreviewSettings() {
        mPreviewSettings = new VEPreviewSettings.Builder()
//                .setRenderSize(new VESize(1080, 1920))
                .setRenderSize(new VESize(1920, 1080))
                .build();
    }

    private boolean initCamera() {
        if (mH264Decoder != null) {
            mH264Decoder.clearVideoCache();
        }
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                mVideoEncodeSettings = new VEVideoEncodeSettings.Builder(USAGE_RECORD)
                        .setHwEnc(true)
                        .setEncodeProfile(VEVideoEncodeSettings.ENCODE_PROFILE.ENCODE_PROFILE_HIGH)
//                                        .setVideoRes(1080, 1920)
                        .setVideoRes(1920, 1080)
                        .setBps(12000000 * 4 / 3)
                        .setRotate(270)
                        .build();
                mAudioEncodeSettings = new VEAudioEncodeSettings.Builder().Build();

                genPreviewSettings();
                if (mRecorder == null)
                    mRecorder = new TERecorder(FileUtils.ROOT_DIR, AiCameraApplication.getContext());

                mRecorder.setNativeInitListener(mNativeInitListener);
                mRecorder.setRenderCallback(new TERecorder.IRenderCallback() {
                    @Override
                    public TERecorder.Texture onCreateTexture() {
                        return null;
                    }

                    @Override
                    public void onTextureCreated(TERecorder.Texture texture) {
                        mTexture = texture;
                        mH264Decoder = new H264Decoder();
                        mH264Decoder.setOnDecodeListener(mDecodeListener);
                        mH264Decoder.setSurface(new Surface(mTexture.getSurfaceTexture()));
                        mH264Decoder.init(mRecorder);
                    }

                    @Override
                    public boolean onDestroy() {
                        return false;
                    }
                });

                VECameraSettings.CAMERA_FACING_ID facingId = backCamera ? VECameraSettings.CAMERA_FACING_ID.FACING_BACK : VECameraSettings.CAMERA_FACING_ID.FACING_FRONT;
                VECameraSettings cameraSettings = new VECameraSettings(facingId,
                        90, new VESize(1920, 1080));
//                                        90, new VESize(1080, 1920));
                int code = mRecorder.init(mVideoEncodeSettings, mAudioEncodeSettings, mPreviewSettings, cameraSettings);
                mRecorder.setOnFaceInfoListener(new TERecorder.OnFaceInfoListener() {
                    @Override
                    public void onResult(@Nullable VEFaceAttributeInfo attributeInfo, @Nullable VEFaceDetectInfo detectInfo) {
                        Log.e(TAG, "attributeInfo: " + attributeInfo + " detectInfo: " + detectInfo);
                    }
                });

                mRecorder.tryRestore(mRecordSegmentTimeInfo.size());

                if (!fromCreated) {
                    mTotalRecordingTime = TimeSpeedModel.calculateRealTime(mRecordSegmentTimeInfo);
                } else {
                    mRecorder.clearEnv();
                }

                fromCreated = false;

                setMusicStart(0);

                mRecorder.startPreview(new Surface(mTextureView.getSurfaceTexture()));
            }
        });
        return true;
    }

    public Surface getSurface() {
        mSurfaceTexture = mTextureView.getSurfaceTexture();
        if (mSurface == null)
            mSurface = new Surface(mSurfaceTexture);
        return mSurface;
    }

    public void startRecord(float speed) {
        if (!mHasStoped) return;
        if (mRecorder == null) return;
        mIsRecording = true;
        mCurrentSpeed = speed;
        Log.e(TAG, "speed = " + mCurrentSpeed);

        int a = mRecorder.startRecord(speed, mTotalRecordingTime);
        Logcat.v().tag(LogcatConstants.DOUYIN_RECORD).msg("camera display startRecord code: " + a).out();
        Log.d("test record", "startRecord: ");

        if (a == 0) {
            mHasStoped = false;
            mMainHandler.post(new UpdateProgressSegment());
        } else {
            startRecord(speed);
        }
    }

    public void stopRecord() {
        if (mHasStoped) return;

        mIsRecording = false;
        mRecorder.stopRecord();
        mHasStoped = true;

        long currentDurationNano = mRecorder.getEndFrameTime();
        long currentDurationMilli = currentDurationNano / 1000;
        // if duration < 10ms, remove it, when clicking record button quickly
        if (currentDurationMilli <= 10 && currentDurationMilli >= 0) {
            realDeleteLast();
        } else {
            mTotalRecordingTime += currentDurationMilli * 1.0 / mCurrentSpeed;
            TimeSpeedModel currentSegment = new TimeSpeedModel(currentDurationMilli, mCurrentSpeed);
            mRecordSegmentTimeInfo.add(currentSegment);
        }
        AppExecutors.getInstance().mainThread().execute(new Runnable() {
            @Override
            public void run() {
                if (mAttachedActivity != null && mAttachedActivity.get() != null) {
                    mAttachedActivity.get().setDialogEnable(true);
//                    mAttachedActivity.get().updateProgressSegment(mRecordSegmentTimeInfo, 0, true);
                }
            }
        });
    }

    private void realDeleteLast() {
        if (mRecorder == null) return;
        mRecorder.deleteLastFrag();
    }

    /**
     * 设置背景音乐的起点与时长
     *
     * @param musicStart 音乐起点
     * @return 0为设置成功，负数为发生错误
     */
    public int setMusicStart(int musicStart) {
        if (mRecorder == null) return -1;
        return mRecorder.setRecordBGM(mMusicPath, musicStart, 1);
    }

    public class UpdateProgressSegment implements Runnable {
        @Override
        public void run() {
            DyFPVActivity.testLog("updateProgressSegment end time: " + getEndFrameTime() / 1000 + ", has stop: " + mHasStoped);
            if (mHasStoped) {
                if (mAttachedActivity != null && mAttachedActivity.get() != null) {
                    mAttachedActivity.get().updateProgressSegment(mRecordSegmentTimeInfo, getEndFrameTime() / 1000, true);
                }
                return;
            }
            long durationNano = getEndFrameTime();
//            Log.d("dongyanTime", "Update when getEndFrameTime return " + durationNano);
//            long timeElapsed = TimeSpeedModel.calculateRealTime(durationNano, mCurrentSpeed) + getTotalRecordingTime();
//
//            String content = "frame time:" + String.valueOf(durationNano) + ", time elapsed:" + timeElapsed;
//            Log.d("TAG", "Update when getEndFrameTime return " + durationNano + "time elapsed: " + timeElapsed);
            if (mAttachedActivity != null && mAttachedActivity.get() != null) {
                mAttachedActivity.get().updateProgressSegment(mRecordSegmentTimeInfo, durationNano / 1000, false);
            }

//            String content1 = "UpdateProgressSegment durationNano:" + durationNano + ", timeElapsed:" +
//                    timeElapsed + ", currentRecordTimeMilli :" + (durationNano / 1000) + content;
//            FileUtil.writeFileToSDCard(FileUtil.path, content1.getBytes(), fileName, true, true, false);

            mMainHandler.postDelayed(this, 20);
        }
    }

    /**
     * @return 分段数量
     */
    public int getSegmentSize() {
        return mRecordSegmentTimeInfo != null ? mRecordSegmentTimeInfo.size() : 0;
    }

    private boolean scanning = false;

    public void setDecodeListener(H264DecodeListener listener) {
        mDecodeListener = listener;
    }

    private H264DecodeListener mDecodeListener;

//    public void startDecode() {
//        if (mH264Decoder != null) {
//            mH264Decoder.clearVideoCache();
//        }
//    }
//
//    public interface InitOkCallback {
//        void initOK();
//
//        void initRecorderOk();
//    }

    public void execute(Runnable runnable) {
        mExecutorPool.execute(runnable);
    }

    public void setTouchEvent(float x, float y) {
        if (mRecorder != null)
            mRecorder.processTouchEvent(x, y);
    }

    public String getMusicPath() {
        return mMusicPath;
    }
}
