package com.pepe.aplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pepe.aplayer.test.MyGlSurfaceView;
import com.pepe.aplayer.util.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class TestCamera2Fragment extends Fragment implements View.OnClickListener{

    //    private AutoFitTextureView mTextureView;
    private Button mButtonVideo;
    private CameraDevice mCameraDevice;
    private CameraCaptureSession mPreviewCameraCaptureSession;
    private Size mPreviewSize;
    private Size mVideoSize;
    private MediaRecorder mMediaRecorder;
    private boolean mIsRecordingVideo;
    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;
    private Integer mSensorOrientation;
    private String mNextVideoAbsolutePath;
    private CaptureRequest.Builder mPreviewBuilder;
    private MyGlSurfaceView mGlsurfaceView;
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);
    private Integer mCurrentCameraId = CameraCharacteristics.LENS_FACING_BACK;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test_camera2_video, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
//        mTextureView = (AutoFitTextureView) view.findViewById(R.id.texture);

        mButtonVideo = (Button) view.findViewById(R.id.video);
        mButtonVideo.setOnClickListener(this);
        view.findViewById(R.id.info).setOnClickListener(this);
        view.findViewById(R.id.switchCamera).setOnClickListener(this);
        mGlsurfaceView= (MyGlSurfaceView) view.findViewById(R.id.glsurfaceView);
        startBackgroundThread();
        mGlsurfaceView.setOnSurfacePrepared(new MyGlSurfaceView.OnSurfacePreparedCallBack() {
            @Override
            public void onSurfacePrepared() {
//                openCamera();
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void openCamera() {
        closeCamera();
        CameraManager cameraManager = (CameraManager) getActivity().getApplication().getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] cameraIdList = cameraManager.getCameraIdList();
            CameraCharacteristics cameraCharacteristics = null;
            String cameraIdStr=null;
            for(int i=0; i<cameraIdList.length; i++){
                 cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraIdList[i]);
                 if(mCurrentCameraId == cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)){
                     cameraIdStr = cameraIdList[i];
                     break;
                 }
            }
            if(cameraCharacteristics ==null || cameraIdStr==null){
                return;
            }
             StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
             int hardwareLevel = cameraCharacteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
             int[] availableCapabilitiesArray = cameraCharacteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);
            LogUtil.printLog("hardwareLevel="+hardwareLevel);

             Size[] previewSizeArray = streamConfigurationMap.getOutputSizes(SurfaceTexture.class);

            cameraManager.openCamera(cameraIdStr, new CameraDevice.StateCallback(){

                @Override
                public void onOpened(CameraDevice camera) {
                    closeCaptureSession();
                    mCameraDevice = camera;
                    try {
                        mPreviewBuilder= mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
//                        mPreviewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                        mPreviewBuilder.set(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        mPreviewBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON_AUTO_FLASH);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                    //List of metering areas to use for auto-exposure adjustment. 自动曝光
//                    mPreviewBuilder.set(CaptureRequest.CONTROL_AE_REGIONS, mAERegions);
//                    //传感器所需要捕获的区域
//                    mPreviewBuilder.set(CaptureRequest.SCALER_CROP_REGION, mCropRegion);
//        float minimumLens = mCameraCharacteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);
//        float num = (((float) 1) * minimumLens / 100);
//        mPreviewBuilder.set(CaptureRequest.LENS_FOCUS_DISTANCE, 0.0f);
//                    mPreviewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
//                    mPreviewBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);
//                    mPreviewBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
//                    mPreviewBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, CameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_START);//                    mPreviewBuilder.set();
                    mGlsurfaceView.surfaceTexture.setDefaultBufferSize(1080, 1711);
                    Surface surface = new Surface(mGlsurfaceView.surfaceTexture);
                    mPreviewBuilder.addTarget(surface);
                    List<Surface> surfaceList = new ArrayList<>();
                    surfaceList.add(surface);
                    try {
                        mCameraDevice.createCaptureSession(surfaceList, new CameraCaptureSession.StateCallback(){
                            @Override
                            public void onConfigured( CameraCaptureSession session) {
                                mPreviewCameraCaptureSession = session;
//                                List<CaptureRequest> captureRequestList = new ArrayList<>();
//                                captureRequestList.add(mPreviewBuilder.build());
                                try {
//                                    mPreviewCameraCaptureSession.setRepeatingBurst(captureRequestList,null, mBackgroundHandler);
                                    mPreviewCameraCaptureSession.setRepeatingRequest(mPreviewBuilder.build(),null, mBackgroundHandler);
                                } catch (CameraAccessException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onConfigureFailed( CameraCaptureSession session) {

                            }
                        }, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.switchCamera:
                if(mCurrentCameraId ==CameraCharacteristics.LENS_FACING_BACK){
                    mCurrentCameraId = CameraCharacteristics.LENS_FACING_FRONT;
                }else {
                    mCurrentCameraId = CameraCharacteristics.LENS_FACING_BACK;
                }
                openCamera();
                break;
            case R.id.info:
                break;
            case R.id.video:
                mPreviewBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_START);
                try {
                    mPreviewCameraCaptureSession.capture(mPreviewBuilder.build(), new CameraCaptureSession.CaptureCallback() {
                        @Override
                        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                            super.onCaptureCompleted(session, request, result);
                        }
                    }, mBackgroundHandler);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
                break;
                
        }
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
        closeCaptureSession();
        if(mCameraDevice!=null){
            mCameraDevice.close();
            mCameraDevice=null;
        }
        if (mMediaRecorder!=null){
            mMediaRecorder.release();
            mMediaRecorder=null;
        }
}

    private void closeCaptureSession() {
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
}
