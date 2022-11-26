package com.pepe.aplayer.view.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pepe.aplayer.R;
import com.pepe.aplayer.base.BaseApplication;
import com.pepe.aplayer.opengl.BeautyImageActivity;
import com.pepe.aplayer.view.widget.MyGlSurfaceView;
import com.pepe.aplayer.util.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Camera2Fragment extends Fragment implements View.OnClickListener,SeekBar.OnSeekBarChangeListener {

    // global var
    private CameraDevice mCameraDevice;
    private Integer mCurrentCameraId = CameraCharacteristics.LENS_FACING_BACK;
    private CameraCharacteristics mCameraCharacteristics;
    private CameraCaptureSession mPreviewCameraCaptureSession;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private MyGlSurfaceView mGlsurfaceView;
    private Surface mPreviewSurface;
    private boolean mJpegMirror = false;

    private final float BASIC_ZOOM_RATIO = 1.0f;
    private float mZoomRatio = BASIC_ZOOM_RATIO;

    private final int MODE_CAPTURE = 1001;
    private final int MODE_VIDEO = 1002;
    private int mCurrentMode = MODE_CAPTURE;

    private final int FLASH_STATE_OFF=101;
    private final int FLASH_STATE_AUTO=102;
    private final int FLASH_STATE_ALWAYS=103;
    private final int FLASH_STATE_TORCH=104;
    private int mCurrentFlash =FLASH_STATE_OFF;

    private final int PRECAPTURE_STATE_PREVIEW = 0;
    private final int PRECAPTURE_STATE_AE_PRECAPTURE = 1;
    private final int PRECAPTURE_STATE_WAITING_FOCUS = 2;
    private final int PRECAPTURE_STATE_CAPTURE = 3;
    private int mPrecaptureState = PRECAPTURE_STATE_PREVIEW;

    private MediaRecorder mMediaRecorder;
    private boolean mIsRecordingVideo = false;
    private ImageReader mImageReader;
    private Size mCapturePreviewSize;
    private Size mJpegSize;
    private Size mVideoPreviewSize;
    private Size mVideoSize;

    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;
    private SeekBar zoomSeekBar;
    private SeekBar redSeekBar;
    private SeekBar smoothSeekBar;
    private SeekBar whiteSeekBar;
    private Button mCaptureButton;
    private Button mTakeVideoButton;
    private Button mFlashOffButton;
    private Button mFlashAutoButton;
    private Button mFlashAlwaysButton;
    private Button mFlashTorchButton;
    private Button videoMode;
    private Button captureModeButton;
    private String mNextVideoAbsolutePath;

    private int mSensorOrientation;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }
    private int getOrientation(int rotation) {
        // Sensor orientation is 90 for most devices, or 270 for some devices (eg. Nexus 5X)
        // We have to take that into account and rotate JPEG properly.
        // For devices with orientation of 90, we simply return our mapping from ORIENTATIONS.
        // For devices with orientation of 270, we need to rotate the JPEG 180 degrees.
        return (ORIENTATIONS.get(rotation) + mSensorOrientation + 270) % 360;
    }

    private static final int SENSOR_ORIENTATION_DEFAULT_DEGREES = 90;
    private static final int SENSOR_ORIENTATION_INVERSE_DEGREES = 270;
    private static final SparseIntArray DEFAULT_ORIENTATIONS = new SparseIntArray();
    private static final SparseIntArray INVERSE_ORIENTATIONS = new SparseIntArray();
    static {
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_0, 90);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_90, 0);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_180, 270);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }
    static {
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_0, 270);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_90, 180);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_180, 90);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_270, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_testFragment_beautyImage:
                BeautyImageActivity.open(getActivity());
                break;
            case R.id.btn_testFragment_captureMode:
                startPreview(true);
                break;
            case R.id.btn_testFragment_videoMode:
                startPreview(false);
                break;
            case R.id.btn_testFragment_flashOff:
                mCurrentFlash = FLASH_STATE_OFF;
                setRepeatingRequest();
                break;
            case R.id.btn_testFragment_flashAuto:
                mCurrentFlash = FLASH_STATE_AUTO;
                setRepeatingRequest();
                break;
            case R.id.btn_testFragment_flashAlways:
                mCurrentFlash = FLASH_STATE_ALWAYS;
                setRepeatingRequest();
                break;
            case R.id.btn_testFragment_flashTorch:
                mCurrentFlash = FLASH_STATE_TORCH;
                setRepeatingRequest();
                break;
            case R.id.btn_testFragment_capture:
                switch (mCurrentMode){
                    case MODE_CAPTURE:
                        if(mCurrentCameraId ==CameraCharacteristics.LENS_FACING_FRONT){
                            //前置摄像头 没有对焦功能？
                            capture();
                        }else {
//                            preCaptureExposure();
                            preCaptureLockFocus();
                        }
                        break;
                    case MODE_VIDEO:
                        if(mIsRecordingVideo){
                            stopRecordingVideo();
                        }else {
                            startRecordingVideo();
                        }
                        break;
                }
                break;
            case R.id.switchCamera:
                if(mCurrentCameraId ==CameraCharacteristics.LENS_FACING_BACK){
                    mCurrentCameraId = CameraCharacteristics.LENS_FACING_FRONT;
                }else {
                    mCurrentCameraId = CameraCharacteristics.LENS_FACING_BACK;
                }
                openCamera();
                break;
        }
        LogUtil.printLog("mCurrentFlash="+mCurrentFlash);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test_camera2_video, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        mFlashOffButton = view.findViewById(R.id.btn_testFragment_flashOff);
        mFlashOffButton.setOnClickListener(this);
        mFlashAutoButton = view.findViewById(R.id.btn_testFragment_flashAuto);
        mFlashAutoButton.setOnClickListener(this);
        mFlashAlwaysButton = view.findViewById(R.id.btn_testFragment_flashAlways);
        mFlashAlwaysButton.setOnClickListener(this);
        mFlashTorchButton = view.findViewById(R.id.btn_testFragment_flashTorch);
        mFlashTorchButton.setOnClickListener(this);
        mCaptureButton = view.findViewById(R.id.btn_testFragment_capture);
        mCaptureButton.setOnClickListener(this);
        videoMode = view.findViewById(R.id.btn_testFragment_videoMode);
        videoMode.setOnClickListener(this);
        captureModeButton = view.findViewById(R.id.btn_testFragment_captureMode);
        captureModeButton.setOnClickListener(this);
        redSeekBar = view.findViewById(R.id.sb_testFragment_beautyRed);
        redSeekBar.setOnSeekBarChangeListener(this);
        smoothSeekBar = view.findViewById(R.id.sb_testFragment_beautySmooth);
        smoothSeekBar.setOnSeekBarChangeListener(this);
        whiteSeekBar = view.findViewById(R.id.sb_testFragment_beautyWhite);
        whiteSeekBar.setOnSeekBarChangeListener(this);
        zoomSeekBar = view.findViewById(R.id.sb_testFragment_zoom);
        zoomSeekBar.setOnSeekBarChangeListener(this);
        view.findViewById(R.id.btn_testFragment_beautyImage).setOnClickListener(this);
        view.findViewById(R.id.switchCamera).setOnClickListener(this);
        mGlsurfaceView= view.findViewById(R.id.camera2Fragment_glsurfaceView);
        mGlsurfaceView.setOnSurfacePrepared(new MyGlSurfaceView.OnSurfacePreparedCallBack() {
            @Override
            public void onSurfacePrepared() {
//                openCamera();
            }
        });
    }


    @Override
    public void onResume() {
        startBackgroundThread();
        openCamera();
        super.onResume();
    }

    @Override
    public void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    private void closeCamera(){
        closePreviewSession();
        if(mCameraDevice!=null){
            mCameraDevice.close();
            mCameraDevice=null;
        }
        if (mMediaRecorder!=null){
            mMediaRecorder.release();
            mMediaRecorder=null;
        }
    }

    private void closePreviewSession() {
        if(mPreviewCameraCaptureSession != null){
            mPreviewCameraCaptureSession.close();
            mPreviewCameraCaptureSession=null;
        }
    }


    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }


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

    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    private Size chooseOptimizedPreviewSize(Size[] supportSizeArray, int targetWidth, int targetHeight, Size aspectRatio){
        Size selectedSize = supportSizeArray[0];
        List<Size> bigSizeList = new ArrayList<>();
        List<Size> smallSizeList = new ArrayList<>();

        for(int i=0; i< supportSizeArray.length ;i++){
            if(supportSizeArray[i].getWidth()*aspectRatio.getHeight() == supportSizeArray[i].getHeight()*aspectRatio.getWidth()){
                if(supportSizeArray[i].getWidth()>=targetWidth && supportSizeArray[i].getHeight()>=targetHeight){
                    bigSizeList.add(supportSizeArray[i]);
                }else {
                    smallSizeList.add(supportSizeArray[i]);
                }
            }
        }

        selectedSize = Collections.max(Arrays.asList(supportSizeArray), new CompareSizesByArea());
        if(bigSizeList.size()>0){
            selectedSize = Collections.min(bigSizeList, new CompareSizesByArea());
        }else if(smallSizeList.size()>0){
            selectedSize = Collections.max(smallSizeList, new CompareSizesByArea());
        }

        return selectedSize;
    }

    @SuppressLint("MissingPermission")
    private void openCamera() {
        closeCamera();
        CameraManager cameraManager = (CameraManager) getActivity().getApplication().getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] cameraIdList = cameraManager.getCameraIdList();
             mCameraCharacteristics = null;
            String cameraIdStr=null;
            for(int i=0; i<cameraIdList.length; i++){
                 mCameraCharacteristics = cameraManager.getCameraCharacteristics(cameraIdList[i]);
                 if(mCurrentCameraId == mCameraCharacteristics.get(CameraCharacteristics.LENS_FACING)){
                     cameraIdStr = cameraIdList[i];
                     break;
                 }
            }

            if(mCameraCharacteristics ==null || cameraIdStr==null){
                return;
            }
            if(mCurrentCameraId ==CameraCharacteristics.LENS_FACING_FRONT){
                mJpegMirror = true;
            }else {
                mJpegMirror = false;
            }

//            for(int i=0;i<mCameraCharacteristics.getAvailableCaptureRequestKeys().size();i++){
//                LogUtil.printLog("key = "+mCameraCharacteristics.getAvailableCaptureRequestKeys().get(i).getName());
//            }
//            LogUtil.printLog("MAX_REGIONS_AE="+mCameraCharacteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AE)+"");
//            LogUtil.printLog("MAX_REGIONS_AF="+mCameraCharacteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF)+"");

             StreamConfigurationMap streamConfigurationMap = mCameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
             int hardwareLevel = mCameraCharacteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
             int[] availableCapabilitiesArray = mCameraCharacteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);
            float maxZoom = mCameraCharacteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM);
            LogUtil.printLog("hardwareLevel="+hardwareLevel+"--maxZoom="+maxZoom);

             Size[] jpegSizeArray = streamConfigurationMap.getOutputSizes(ImageFormat.JPEG);
             mJpegSize = Collections.max(Arrays.asList(jpegSizeArray), new CompareSizesByArea());
            Size[] capturePreviewSizeArray = streamConfigurationMap.getOutputSizes(SurfaceTexture.class);
            mCapturePreviewSize = chooseOptimizedPreviewSize(capturePreviewSizeArray, 1920, 1080, mJpegSize);

            Size[] videoSizeArray = streamConfigurationMap.getOutputSizes(MediaRecorder.class);
            mVideoSize = chooseVideoSize(videoSizeArray);
            Size[] videoPreviewSizeArray = streamConfigurationMap.getOutputSizes(SurfaceTexture.class);
            mVideoPreviewSize = chooseOptimalSize(videoPreviewSizeArray,1080, 1920, mVideoSize);
            mMediaRecorder = new MediaRecorder();

            mImageReader = ImageReader.newInstance(mJpegSize.getWidth(), mJpegSize.getHeight(), ImageFormat.JPEG, 2);
            mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = reader.acquireNextImage();
                    if(image != null){
                        ByteBuffer byteBuffer  = image.getPlanes()[0].getBuffer();
                        byte[] imageByteArray = new byte[byteBuffer.remaining()];
                        byteBuffer.get(imageByteArray);
                        image.close();

                        //Image对象最好不要跨线程调用，而是传递image里的byteArray
                        mBackgroundHandler.post(new ImageSaveRunnable(imageByteArray, mJpegMirror));
                    }
                }
            },mBackgroundHandler);

            mSensorOrientation = mCameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
            cameraManager.openCamera(cameraIdStr, new CameraDevice.StateCallback(){

                @Override
                public void onOpened(CameraDevice camera) {
                    mCameraDevice = camera;
                    startPreview(true);
                }

                @Override
                public void onDisconnected(CameraDevice camera) {

                }

                @Override
                public void onError(CameraDevice camera, int error) {

                }
            },mBackgroundHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startPreview(boolean capturePreview) {
        if(capturePreview){
            mCurrentMode = MODE_CAPTURE;
        }else {
            mCurrentMode = MODE_VIDEO;
        }

        try {
            closePreviewSession();
            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            //List of metering areas to use for auto-exposure adjustment. 自动曝光
//                    mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_REGIONS, mAERegions);
//                    //传感器所需要捕获的区域
//                    mPreviewRequestBuilder.set(CaptureRequest.SCALER_CROP_REGION, mCropRegion);
//                   float minimumLens = mCameraCharacteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);
//                   float num = (((float) 1) * minimumLens / 100);
//                   mPreviewRequestBuilder.set(CaptureRequest.LENS_FOCUS_DISTANCE, 0.0f);

            mGlsurfaceView.surfaceTexture.setDefaultBufferSize(mCapturePreviewSize.getWidth(), mCapturePreviewSize.getHeight());
             mPreviewSurface = new Surface(mGlsurfaceView.surfaceTexture);
            mPreviewRequestBuilder.addTarget(mPreviewSurface);

            List<Surface> surfaceList = new ArrayList<>();
            surfaceList.add(mPreviewSurface);
            if(capturePreview){
                surfaceList.add(mImageReader.getSurface());
            }

            mCameraDevice.createCaptureSession(surfaceList, new CameraCaptureSession.StateCallback(){
                @Override
                public void onConfigured( CameraCaptureSession session) {
                    mPreviewCameraCaptureSession = session;
                    setRepeatingRequest();
                }

                @Override
                public void onConfigureFailed( CameraCaptureSession session) {

                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void setRepeatingRequest() {
        mPrecaptureState = PRECAPTURE_STATE_PREVIEW;
        try {
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            if(mCurrentMode==MODE_CAPTURE){
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            }else if(mCurrentMode==MODE_VIDEO){
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_VIDEO);
            }

            setFlash(mPreviewRequestBuilder);
            setZoomRatio(mPreviewRequestBuilder);

//                                    mPreviewCameraCaptureSession.setRepeatingBurst(captureRequestList,null, mBackgroundHandler);
            mPreviewCameraCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(),preCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * In this sample, we choose a video size with 3x4 aspect ratio. Also, we don't use sizes
     * larger than 1080p, since MediaRecorder cannot handle such a high-resolution video.
     *
     * @param choices The list of available sizes
     * @return The video size
     */
    private static Size chooseVideoSize(Size[] choices) {
        for (Size size : choices) {
            if (size.getWidth() == size.getHeight() * 4 / 3 && size.getWidth() <= 1080) {
                return size;
            }
        }
        Log.e("", "Couldn't find any suitable video size");
        return choices[choices.length - 1];
    }

    /**
     * Given {@code choices} of {@code Size}s supported by a camera, chooses the smallest one whose
     * width and height are at least as large as the respective requested values, and whose aspect
     * ratio matches with the specified value.
     *
     * @param choices     The list of sizes that the camera supports for the intended output class
     * @param width       The minimum desired width
     * @param height      The minimum desired height
     * @param aspectRatio The aspect ratio
     * @return The optimal {@code Size}, or an arbitrary one if none were big enough
     */
    private static Size chooseOptimalSize(Size[] choices, int width, int height, Size aspectRatio) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getHeight() == option.getWidth() * h / w &&
                    option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }

        // Pick the smallest of those, assuming we found any
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new Camera2VideoFragment.CompareSizesByArea());
        } else {
            Log.e("", "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    private static class ImageSaveRunnable implements  Runnable {

        private byte[] mImageByteArray;
        private boolean mMirrir;
        private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");

        public ImageSaveRunnable(byte[] image, boolean mirror) {
            mImageByteArray = image;
            mMirrir = mirror;
        }

        @Override
        public void run() {
            File saveFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), dateFormat.format(new Date()) + ".jpg");
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(saveFile);
                if(mMirrir){
                    Bitmap bitmap = BitmapFactory.decodeByteArray(mImageByteArray, 0, mImageByteArray.length);
                    Bitmap convertBitmap = convertBmp(bitmap);
                    convertBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                }else {
                    fileOutputStream.write(mImageByteArray);
                }

                fileOutputStream.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                        fileOutputStream = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public Bitmap convertBmp(Bitmap bmp) {
            int w = bmp.getWidth();
            int h = bmp.getHeight();
            Matrix matrix = new Matrix();
            // 镜像水平翻转
            matrix.postScale(-1, 1);
            Bitmap convertBmp = Bitmap.createBitmap(bmp, 0, 0, w, h, matrix, true);
            if(convertBmp!=bmp){
                bmp.recycle();
//                System.gc();
//                Runtime.getRuntime().gc();
            }
            return convertBmp;
        }
    }

    private void preCaptureExposure(){
        mPrecaptureState = PRECAPTURE_STATE_AE_PRECAPTURE;
        try {
            CaptureRequest.Builder builder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            builder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
            builder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            builder.addTarget(mPreviewSurface);
            mPreviewCameraCaptureSession.capture(builder.build(), preCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void preCaptureLockFocus() {
        mPrecaptureState = PRECAPTURE_STATE_WAITING_FOCUS;
        try {
            CaptureRequest.Builder builder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            builder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);
            builder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_START);
            builder.addTarget(mPreviewSurface);
            mPreviewCameraCaptureSession.capture(builder.build(), preCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private CameraCaptureSession.CaptureCallback preCaptureCallback = new CameraCaptureSession.CaptureCallback() {

        private void process(CaptureResult captureResult){
            Integer aeState = captureResult.get(CaptureResult.CONTROL_AE_STATE);
            Integer afState = captureResult.get(CaptureResult.CONTROL_AF_STATE);
            Integer awbState = captureResult.get(CaptureResult.CONTROL_AWB_STATE);
            LogUtil.printLog("afState="+afState+"--aeState="+aeState+"--awbState="+awbState+"--mPrecaptureState="+mPrecaptureState);
            switch (mPrecaptureState){
                case PRECAPTURE_STATE_AE_PRECAPTURE:
                    if(aeState==null || aeState==CaptureResult.CONTROL_AE_STATE_CONVERGED){
                        if(aeState!=null
                                &&(aeState==CaptureResult.CONTROL_AE_STATE_FLASH_REQUIRED || aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE)){
                            mCurrentFlash = FLASH_STATE_ALWAYS;
                        }
                        preCaptureLockFocus();
                    }
                    break;
                case PRECAPTURE_STATE_WAITING_FOCUS:
                   if(afState == null
                     || afState == CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED
                     || afState == CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED
                     || afState == CaptureResult.CONTROL_AF_STATE_PASSIVE_FOCUSED
                     || afState == CaptureResult.CONTROL_AF_STATE_PASSIVE_UNFOCUSED) {
                            capture();
                     }
//                     }else if(afState == CaptureResult.CONTROL_AF_STATE_PASSIVE_SCAN){
//                         mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);
//                         try {
//                             mPreviewCameraCaptureSession.capture(mPreviewRequestBuilder.build(), preCaptureCallback, mBackgroundHandler);
//                         } catch (CameraAccessException e) {
//                             e.printStackTrace();
//                         }
//                     }else if(afState == CaptureResult.CONTROL_AF_STATE_INACTIVE){
//                            preCaptureLockFocus();
//                     }
                    break;
                case PRECAPTURE_STATE_CAPTURE:
                    setRepeatingRequest();
                    break;
            }
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult totalCaptureResult) {
            process(totalCaptureResult);
        }
    };

    private void capture(){
        mGlsurfaceView.mCapturing = true;
        mPrecaptureState = PRECAPTURE_STATE_CAPTURE;
        try {
            CaptureRequest.Builder captureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
//            captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO);

            setFlash(captureRequestBuilder);
            setZoomRatio(captureRequestBuilder);

            int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
            LogUtil.printLog("rotation="+rotation);
            captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation(rotation));

            //拍照时将mPreviewSurface添加到captureRequestBuilder中，若不添加则会有拍照瞬间预览画面就会少了一帧,相当于卡了一下。
            captureRequestBuilder.addTarget(mPreviewSurface);
            captureRequestBuilder.addTarget(mImageReader.getSurface());
            mPreviewCameraCaptureSession.stopRepeating();
            mPreviewCameraCaptureSession.abortCaptures();
            mPreviewCameraCaptureSession.capture(captureRequestBuilder.build(), preCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void setFlash(CaptureRequest.Builder captureRequestBuilder) {
        switch (mCurrentFlash){
            case FLASH_STATE_AUTO:
                captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                captureRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_SINGLE);
                break;
            case FLASH_STATE_ALWAYS:
                captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH);
                captureRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_SINGLE);
                break;
            case FLASH_STATE_TORCH:
                captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
                captureRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
                break;
            case FLASH_STATE_OFF:
                default:
                    captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
                    captureRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);
                    break;
        }
    }

    private void setZoomRatio(CaptureRequest.Builder captureRequestBuilder) {
        Rect rect = mCameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
        Rect zoomRect= new Rect((int)(rect.right/2-rect.right/ mZoomRatio /2), (int)(rect.bottom/2-rect.bottom/ mZoomRatio /2),
                (int)(rect.right/2+rect.right/ mZoomRatio /2), (int)(rect.bottom/2+rect.bottom/ mZoomRatio /2));
        LogUtil.printLog("mZoomRatio="+ mZoomRatio +"--rect="+rect+"--right="+rect.right+"--bottom="+rect.bottom+"--zoomRect="+zoomRect);
        captureRequestBuilder.set(CaptureRequest.SCALER_CROP_REGION, zoomRect);
    }

    private void startRecordingVideo() {
//        if (null == mCameraDevice || !mTextureView.isAvailable() || null == mVideoPreviewSize) {
//            return;
//        }
        try {
            closePreviewSession();
            setUpMediaRecorder();
            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);

            // Set up Surface for the camera preview
            SurfaceTexture texture = mGlsurfaceView.surfaceTexture;
            texture.setDefaultBufferSize(mVideoPreviewSize.getWidth(), mVideoPreviewSize.getHeight());
            Surface previewSurface = new Surface(texture);
            Surface recorderSurface = mMediaRecorder.getSurface();
            mPreviewRequestBuilder.addTarget(previewSurface);
            mPreviewRequestBuilder.addTarget(recorderSurface);

            // Set up Surface for the MediaRecorder
            List<Surface> surfaceList = new ArrayList<>();
            surfaceList.add(previewSurface);
            surfaceList.add(recorderSurface);
            mCameraDevice.createCaptureSession(surfaceList, new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    mPreviewCameraCaptureSession = cameraCaptureSession;
                    setRepeatingRequest();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // UI
                            mCaptureButton.setText(R.string.stop);
                            mIsRecordingVideo = true;

                            // Start recording
                            mMediaRecorder.start();
                        }
                    });
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Activity activity = getActivity();
                    if (null != activity) {
                        Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException | IOException e) {
            e.printStackTrace();
        }

    }

    private void stopRecordingVideo() {
        //TODO:
        //前置摄像头录制视频完成后，需要执行ffmpeg的命令来翻转一下左右镜像的视频文件
        //翻转由MediaRecorder拍摄的视频，可以运行ffmpeg命令：
        // ffmpeg -i /pathtooriginalfile/originalfile.mp4 -vf hflip -c:a copy /pathtosave/flippedfile.mp4

        // UI
        mIsRecordingVideo = false;
        mCaptureButton.setText(R.string.record);
        // Stop recording
        mMediaRecorder.stop();
        mMediaRecorder.reset();

        Toast.makeText(BaseApplication.mContext, "Video saved: " + mNextVideoAbsolutePath, Toast.LENGTH_SHORT).show();
        mNextVideoAbsolutePath = null;
        startPreview(false);
    }

    private void setUpMediaRecorder() throws IOException {
        final Activity activity = getActivity();
        if (null == activity) {
            return;
        }
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        if (mNextVideoAbsolutePath == null || mNextVideoAbsolutePath.isEmpty()) {
            mNextVideoAbsolutePath = getVideoFilePath(getActivity());
        }
        mMediaRecorder.setOutputFile(mNextVideoAbsolutePath);
        mMediaRecorder.setVideoEncodingBitRate(10000000);
        mMediaRecorder.setVideoFrameRate(30);
        mMediaRecorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        switch (mSensorOrientation) {
            case SENSOR_ORIENTATION_DEFAULT_DEGREES:
                mMediaRecorder.setOrientationHint(DEFAULT_ORIENTATIONS.get(rotation));
                break;
            case SENSOR_ORIENTATION_INVERSE_DEGREES:
                mMediaRecorder.setOrientationHint(INVERSE_ORIENTATIONS.get(rotation));
                break;
        }
        mMediaRecorder.prepare();
    }

    private String getVideoFilePath(Context context) {
        final  File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        return (dir == null ? "" : (dir.getAbsolutePath() + "/")) + System.currentTimeMillis() + ".mp4";
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()){
            case R.id.sb_testFragment_beautyRed:
//                mRenderer.setTexelOffset(range(progress,minstepoffset,maxstepoffset));
                mGlsurfaceView.requestRender();
                break;
            case R.id.sb_testFragment_beautySmooth:
                mGlsurfaceView.requestRender();
                break;
            case R.id.sb_testFragment_beautyWhite:
                mGlsurfaceView.requestRender();
                break;
            case R.id.sb_testFragment_zoom:
                float maxZoomRatio = mCameraCharacteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM);
                mZoomRatio = progress/100f*(maxZoomRatio-1) + BASIC_ZOOM_RATIO;
                setRepeatingRequest();
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
