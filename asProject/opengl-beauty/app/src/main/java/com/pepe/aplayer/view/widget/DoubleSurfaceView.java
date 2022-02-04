package com.pepe.aplayer.view.widget;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.PermissionChecker;

import com.pepe.aplayer.R;
import com.pepe.aplayer.opengl.egl.EGLUtils;
import com.pepe.aplayer.opengl.egl.GLFramebuffer;
import com.pepe.aplayer.opengl.egl.GLRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DoubleSurfaceView extends LinearLayout {

    private SurfaceView mSurfaceView1;
    private SurfaceView mSurfaceView2;

    private EGLUtils mEglUtils;
    private GLFramebuffer mFramebuffer;
    private SurfaceTexture mSurfaceTexture;
    private Surface mSurface;

    private final Object mObject1 = new Object();
    private final Object mObject2 = new Object();

    private String mCameraId;
    private CameraManager mCameraManager;
    private CameraCaptureSession mCameraCaptureSession;
    private CameraDevice mCameraDevice;
    private Handler mHandler;

    private int screenWidth1, screenHeight1,screenWidth2, screenHeight2;

    public DoubleSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public DoubleSurfaceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }



    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.double_surface_view,this);
        mSurfaceView1 = findViewById(R.id.sv_camera1);
        mSurfaceView2 = findViewById(R.id.sv_camera2);
        initCamera2();
        mSurfaceView1.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {

            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                screenWidth1 = i1;
                screenHeight1 = i2;
                Thread thread = new Thread(new SurfaceRunnable1());
                thread.start();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                if(mCameraCaptureSession != null){
                    mCameraCaptureSession.getDevice().close();
                    mCameraCaptureSession.close();
                    mCameraCaptureSession = null;
                }
                if(mSurface != null){
                    mSurface.release();
                    mSurface = null;
                }
                if(mSurfaceTexture != null){
                    mSurfaceTexture.release();
                    mSurfaceTexture = null;
                    synchronized (mObject1) {
                        mObject1.notifyAll();
                    }
                }
            }
        });

        mSurfaceView2.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {

            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                screenWidth2 = i1;
                screenHeight2 = i2;
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });

    }

    public class SurfaceRunnable1 implements Runnable{
        @Override
        public void run() {
            mFramebuffer = new GLFramebuffer(getContext());
            GLRenderer renderer = new GLRenderer(getContext());

            mEglUtils = new EGLUtils();
            mEglUtils.initEGL(EGL14.EGL_NO_CONTEXT,mSurfaceView1.getHolder().getSurface());
            renderer.initShader();


            Size mPreviewSize =  getPreferredPreviewSize(mSizes, screenWidth1, screenHeight1);
            int previewWidth = mPreviewSize.getHeight();
            int previewHeight = mPreviewSize.getWidth();
            int left,top,viewWidth,viewHeight;
            if(previewHeight > previewWidth){
                left = 0;
                viewWidth = screenWidth1;
                viewHeight = (int)(previewHeight*1.0f/previewWidth*viewWidth);
                top = (screenHeight1 - viewHeight)/2;
            }else{
                top = 0;
                viewHeight = screenHeight1;
                viewWidth = (int)(previewWidth*1.0f/previewHeight*viewHeight);
                left = (screenWidth1 - viewWidth)/2;
            }
            Rect rect = new Rect();
            rect.left = left;
            rect.top = top;
            rect.right = left + viewWidth;
            rect.bottom = top + viewHeight;

            mFramebuffer.initFramebuffer(previewWidth,previewHeight);

            mSurfaceTexture = mFramebuffer.getSurfaceTexture();
            mSurfaceTexture.setDefaultBufferSize(previewWidth, previewHeight);
            mSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                @Override
                public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                    synchronized (mObject1) {
                        mObject1.notifyAll();
                    }
                }
            });
            post(new Runnable() {
                @Override
                public void run() {
                    Thread thread = new Thread(new SurfaceRunnable2());
                    thread.start();
                }
            });

            openCamera2();
            while (true){
                synchronized (mObject1) {
                    try {
                        mObject1.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if(mSurfaceTexture == null){
                    break;
                }
                mFramebuffer.drawFrameBuffer(previewWidth,previewHeight);
                synchronized (mObject2) {
                    mObject2.notifyAll();
                }

                GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

                GLES20.glViewport(rect.left,rect.top,rect.width(),rect.height());

                mFramebuffer.bindTexture();

                renderer.drawFrame();
                mEglUtils.swap();

            }
            mEglUtils.release();
        }
    }
    public class SurfaceRunnable2 implements Runnable{
        @Override
        public void run() {
            EGLUtils eglUtils = new EGLUtils();
            eglUtils.initEGL(mEglUtils.getContext(),mSurfaceView2.getHolder().getSurface());
            GLRenderer renderer = new GLRenderer(getContext());
            renderer.initShader();

            Size mPreviewSize =  getPreferredPreviewSize(mSizes, screenWidth2, screenHeight2);
            int previewWidth = mPreviewSize.getHeight();
            int previewHeight = mPreviewSize.getWidth();
            int left,top,viewWidth,viewHeight;
            if(previewHeight > previewWidth){
                left = 0;
                viewWidth = screenWidth2;
                viewHeight = (int)(previewHeight*1.0f/previewWidth*viewWidth);
                top = (screenHeight2 - viewHeight)/2;
            }else{
                top = 0;
                viewHeight = screenHeight2;
                viewWidth = (int)(previewWidth*1.0f/previewHeight*viewHeight);
                left = (screenWidth2 - viewWidth)/2;
            }
            Rect rect = new Rect();
            rect.left = left;
            rect.top = top;
            rect.right = left + viewWidth;
            rect.bottom = top + viewHeight;

            while (true){
                synchronized (mObject2) {
                    try {
                        mObject2.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if(mSurfaceTexture == null){
                    break;
                }
                GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

                GLES20.glViewport(rect.left,rect.top,rect.width(),rect.height());

                mFramebuffer.bindTexture();
                renderer.drawFrame();
                eglUtils.swap();

            }
            eglUtils.release();
        }
    }

    private Size[] mSizes;
    private void initCamera2() {
        HandlerThread handlerThread = new HandlerThread("Camera2");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
        mCameraManager = (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] CameraIdList = mCameraManager.getCameraIdList();
            mCameraId = CameraIdList[0];
            CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(mCameraId);
            characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            if(map != null){
                mSizes = map.getOutputSizes(SurfaceTexture.class);
            }

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private void openCamera2(){
        if (PermissionChecker.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            try {
                mCameraManager.openCamera(mCameraId, stateCallback, mHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }
    private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            mCameraDevice = cameraDevice;
            takePreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            if (mCameraDevice != null) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {

        }
    };

    private void takePreview() {
        try {
            mSurface = new Surface(mSurfaceTexture);
            final CaptureRequest.Builder builder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            builder.addTarget(mSurface);
            mCameraDevice.createCaptureSession(Arrays.asList(mSurface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    if (null == mCameraDevice) return;
                    mCameraCaptureSession = cameraCaptureSession;
                    builder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                    builder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                    CaptureRequest previewRequest = builder.build();
                    try {
                        mCameraCaptureSession.setRepeatingRequest(previewRequest, null, mHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                }
            }, mHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private Size getPreferredPreviewSize(Size[] sizes, int width, int height) {
        List<Size> collectorSizes = new ArrayList<>();
        for (Size option : sizes) {
            if (width > height) {
                if (option.getWidth() > width && option.getHeight() > height) {
                    collectorSizes.add(option);
                }
            } else {
                if (option.getHeight() > width && option.getWidth() > height) {
                    collectorSizes.add(option);
                }
            }
        }
        if (collectorSizes.size() > 0) {
            return Collections.min(collectorSizes, new Comparator<Size>() {
                @Override
                public int compare(Size s1, Size s2) {
                    return Long.signum(s1.getWidth() * s1.getHeight() - s2.getWidth() * s2.getHeight());
                }
            });
        }
        return sizes[0];
    }

}