package com.jfbank.qualitymarket.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.base.BaseMvpActivity;
import com.jfbank.qualitymarket.js.JsRequstInterface;
import com.jfbank.qualitymarket.listener.DialogListener;
import com.jfbank.qualitymarket.mvp.CameraPhotoMVP;
import com.jfbank.qualitymarket.util.PermissionUtils;
import com.jfbank.qualitymarket.util.PhotoUtils;
import com.jfbank.qualitymarket.util.ToastUtil;
import com.jph.takephoto.model.TResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 功能：自定义相机功能<br>
 * 作者：赵海<br>
 * 时间： 2017/7/14 0014<br>.
 * 版本：1.2.0
 */

public class CameraPhotoActivity extends BaseMvpActivity<CameraPhotoMVP.Presenter, CameraPhotoMVP.Model> implements CameraPhotoMVP.View {
    @InjectView(R.id.tv_sys_takepic)
    TextView tvSysTakepic;
    @InjectView(R.id.iv_take_pic)
    ImageView ivTakePic;
    @InjectView(R.id.tv_cancel_takepic)
    TextView tvCancelTakepic;
    @InjectView(R.id.iv_overlayout)
    ImageView ivOverlayout;
    private static final int EXTRA_REQUST_CAMERA = 0X11;
    public String idCardType = "J1201";
    @InjectView(R.id.camera_preview)
    SurfaceView cameraView;
    Camera camera;
    byte[] picByte = null;
    public static boolean isStartCameraAct = false;
    private boolean isPageDestory = false;
    private long time = 0;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_camera_photo;
    }

    @Override
    protected void initView() {
        time = System.currentTimeMillis();
        llTitle.setVisibility(View.GONE);
        cameraView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        cameraView.getHolder().setFixedSize(176, 144);
        //屏幕常亮
        cameraView.getHolder().setKeepScreenOn(true);
        //为SurfaceView的句柄添加一个回调函数
        cameraView.getHolder().addCallback(new SurfaceCallback());
        idCardType = getIntent().getStringExtra(JsRequstInterface.EXTRA_TAKE_IDCARD_TYPE);
        //添加浮层
        if ("J1201".equals(idCardType)) {//身份证正面
            ivOverlayout.setBackgroundResource(R.mipmap.ic_idcard_front);
        } else if ("J1202".equals(idCardType)) {//身份证反面
            ivOverlayout.setBackgroundResource(R.mipmap.ic_idcard_opposite);
        } else if ("J1205".equals(idCardType)) {//身份证手持
            ivOverlayout.setBackgroundResource(R.mipmap.ic_idcard_hand);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isPageDestory) {
            cameraView.setVisibility(View.VISIBLE);
        }
        try {
            if (camera == null) {
                initCameraView();
            } else {
                camera.startPreview();
            }
        } catch (Exception e) {
            setResultPic(null);
            Log.e("mCameraView", e.getMessage() + "");
        }


    }


    @Override
    protected void onPause() {
        if (!isPageDestory) {
            //处理特殊机型无法刷新问题
            cameraView.setVisibility(View.GONE);
        }
        cameraOnPause();
        super.onPause();
    }

    SurfaceCallback surfaceCallback = null;


    private void initCameraView() {
        cameraView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        cameraView.getHolder().setFixedSize(176, 144);
        //屏幕常亮
        cameraView.getHolder().setKeepScreenOn(true);
        //为SurfaceView的句柄添加一个回调函数
        surfaceCallback = new SurfaceCallback();
        cameraView.getHolder().addCallback(surfaceCallback);
    }

    /**
     * 相机OnPause
     */
    private void cameraOnPause() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void finish() {
        isStartCameraAct = false;
        super.finish();
    }

    /**
     * 拍照回调
     */
    private final class MyPictureCallback implements Camera.PictureCallback {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
                picByte = data;
                File file = saveImage(data);
                if (file != null && file.exists()) {
                    disMissDialog();
                    if (camera != null) {//camera不为空
                        camera.stopPreview();
                        setResultPic(file.getPath());
                    } else {
                        setResultPic(null, "拍照出错啦，稍后重试");
                    }
                } else {
                    disMissDialog();
                    Toast.makeText(CameraPhotoActivity.this, "照片保存失败，请重试", Toast.LENGTH_SHORT).show();
                    ivTakePic.setEnabled(true);
                    tvCancelTakepic.setEnabled(true);
                }
            } catch (Exception e) {
                disMissDialog();
                ivTakePic.setEnabled(true);
                tvCancelTakepic.setEnabled(true);
                Toast.makeText(CameraPhotoActivity.this, "照片保存失败，请重试", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * 相机渲染回调
     */
    private final class SurfaceCallback implements SurfaceHolder.Callback {

        // 拍照状态变化时调用该方法
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, final int width,
                                   final int height) {
            if (camera == null) {
                setResultPic(null, "请开启相机权限");
            } else {
                camera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        if (success) {
                            initCamera();
                        }
                    }
                });
            }

        }

        // 开始拍照时调用该方法
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                if (camera == null) {
                    camera = Camera.open(); // 打开摄像头
                }
                camera.setPreviewDisplay(holder); // 设置用于显示拍照影像的SurfaceHolder对象
                camera.setDisplayOrientation(getPreviewDegree(CameraPhotoActivity.this));
                camera.startPreview();
            } catch (Exception e) {
                setResultPic(null, "拍照出错啦，稍后重试");
            }

        }

        // 停止拍照时调用该方法
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            cameraOnPause();
        }
    }


    /**
     * 初始化各项参数
     */
    private void initCamera() {
        if (camera != null) {
            // 获取各项参数
            Camera.Parameters parameters = camera.getParameters();
            parameters.setPictureFormat(PixelFormat.JPEG); // 设置图片格式
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            List picSize = parameters.getSupportedPictureSizes();
            Camera.Size size = null;
            if (null != picSize && 0 < picSize.size()) {
                if (picSize.size() / 2 != 0) {
                    size = (Camera.Size) picSize.get(picSize.size() / 2);
                }
            }
            if (size != null) {
                // 设置保存的图片尺寸
                parameters.setPictureSize(size.width, size.height);
            } else {
                // 设置保存的图片尺寸
                parameters.setPictureSize(
                        parameters.getSupportedPictureSizes().get(0).width,
                        parameters.getSupportedPictureSizes().get(0).height);
            }
            parameters.setJpegQuality(80); // 设置照片质量
            setDispaly(parameters, camera);
            try {
                camera.setParameters(parameters);
                camera.startPreview();
                camera.cancelAutoFocus();
            } catch (Exception e) {
                setResultPic(null, "拍照出错啦，稍后重试");
            }
        }
    }

    //控制图像的正确显示方向
    private void setDispaly(Camera.Parameters parameters, Camera camera) {
        if (Integer.parseInt(Build.VERSION.SDK) >= 8) {
            setDisplayOrientation(camera, getPreviewDegree(CameraPhotoActivity.this));
        } else {
            parameters.setRotation(getPreviewDegree(CameraPhotoActivity.this));
        }
    }

    //实现的图像的正确显示
    private void setDisplayOrientation(Camera camera, int i) {
        Method downPolymorphic;
        try {
            downPolymorphic = camera.getClass().getMethod("setDisplayOrientation", int.class);
            if (downPolymorphic != null) {
                downPolymorphic.invoke(camera, i);
            }
        } catch (Exception e) {
            Toast.makeText(CameraPhotoActivity.this, "相机转动故障，请检查后重试", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    *提供一个静态方法，用于根据手机方向获得相机预览画面旋转的角度
     */
    public static int getPreviewDegree(Activity activity) {
        // 获得手机的方向
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degree = 0;
        // 根据手机的方向计算相机预览画面应该选择的角度
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 90;
                break;
            case Surface.ROTATION_90:
                degree = 0;
                break;
            case Surface.ROTATION_180:
                degree = 270;
                break;
            case Surface.ROTATION_270:
                degree = 180;
                break;

        }
        Log.e("jiaodu", degree + "");
        return degree;
    }

    /**
     * 保存图片
     *
     * @param data
     * @return
     * @throws IOException
     */
    private File saveImage(byte[] data) throws IOException {
        Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
        File file;

        file = PhotoUtils.getFile(this);

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            return file;
        } catch (IOException e) {
            Toast.makeText(CameraPhotoActivity.this, "照片保存失败，请重试", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    protected void initData() {

    }


    @Override
    protected String getPageName() {
        return getString(R.string.str_pagename_cameraphoto);
    }


    @OnClick({R.id.tv_sys_takepic, R.id.iv_take_pic, R.id.tv_cancel_takepic})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_sys_takepic://系统相机
                getTakePhoto().onPickFromCapture(PhotoUtils.getOutputUri(mContext));
                break;
            case R.id.iv_take_pic:
                if (camera != null && System.currentTimeMillis() - time > 900) {
                    showDialog(false, "拍照中...");
                    ivTakePic.setEnabled(false);
                    tvCancelTakepic.setEnabled(false);
                    camera.takePicture(null, null, new CameraPhotoActivity.MyPictureCallback());
                }
                break;
            case R.id.tv_cancel_takepic:
                if (System.currentTimeMillis() - time > 900) {
                    isPageDestory = true;
                    tvCancelTakepic.setEnabled(false);
                    ivTakePic.setEnabled(false);
                    setResultPic(null);
                }
                break;
        }
    }

    @Override
    public void takeCancel() {
        super.takeCancel();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setResultPic(null);
            }
        });
    }

    @Override
    public void takeFail(TResult result, final String msg) {
        super.takeFail(result, msg);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setResultPic(null);
            }
        });
    }

    @Override
    public void takeSuccess(final TResult result) {
        super.takeSuccess(result);
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String picUrl = result.getImage().getOriginalPath();
                setResultPic(picUrl);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - time > 900) {//两秒内初始化，不允许退出。
            setResultPic(null);
        }
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
        super.setContentView(layoutResID);
    }

    /**
     * 回调
     *
     * @param path
     */
    public void setResultPic(final String path, String msg) {
        isPageDestory = true;
        if (!TextUtils.isEmpty(path)) {
            final Intent intent = getIntent();
            intent.putExtra("path", path);
            setResult(Activity.RESULT_OK, intent);
        } else {
            ToastUtil.show(msg);
        }
        finish();
    }

    public void setResultPic(final String path) {
        setResultPic(path, "取消拍照");
    }
}