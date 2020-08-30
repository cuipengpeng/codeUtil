package com.test.xcamera.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.test.xcamera.R;
import com.test.xcamera.accrssory.AccessoryManager;
import com.effect_opengl.EffectManager;
import com.test.xcamera.bean.BaseData;
import com.test.xcamera.enumbean.ScreenOrientationType;
import com.test.xcamera.glview.PreviewGLSurfaceView;
//import com.meetvr.aicamera.glview.RenderCoreManager;
import com.test.xcamera.player.AVFrame;
import com.test.xcamera.util.ScreenOrientationListener;
import com.test.xcamera.view.AutoTrackingRectViewFix;
import com.test.xcamera.view.MoFPVModeView;

/**
 * Created by smz on 2019/12/18.
 * <p>
 * 测试新UI用
 */

public class FPVActivity extends Activity {
    private ScreenOrientationType mScreenOrientationType = null;
    private ScreenOrientationListener mListener;

    private PreviewGLSurfaceView mPreviewTextureView;
    private AutoTrackingRectViewFix mTrackingRectView;
    private MoFPVModeView mMoFPVModeView;
    private int mVideoWidth, mVideoHeight;

    //在预览页面 插上usb时延时发送指令
    private Runnable mConnRunnable = () -> {
        mMoFPVModeView.syncMode(true);
    };
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        init();
    }

    private void init() {
        setContentView(R.layout.activity_fpv);

        mPreviewTextureView = findViewById(R.id.preview);
        mTrackingRectView = findViewById(R.id.track_view);
        mMoFPVModeView = findViewById(R.id.mode_view);

        mTrackingRectView.setGLSurfaceView(mPreviewTextureView);
        mTrackingRectView.setFPVModeView(mMoFPVModeView);
        mMoFPVModeView.setFinishCB(() -> {
            this.finish();
        });

        //初始化预览数据
        AccessoryManager.getInstance().setPreviewDataCallback((BaseData baseData) -> {
            AVFrame avFrame = new AVFrame(baseData, baseData.getmDataSize(), false, false);
            mPreviewTextureView.onReceived(avFrame);

            if (mVideoWidth != avFrame.getVideoWidth() || mVideoHeight != avFrame.getVideoHeight()) {
                mVideoWidth = avFrame.getVideoWidth();
                mVideoHeight = avFrame.getVideoHeight();
                runOnUiThread(() -> {
                    mTrackingRectView.changeSize(avFrame.getVideoWidth(), avFrame.getVideoHeight());
                });
            }

            if (baseData.getmVideoFrameInfo() != null && baseData.getmVideoFrameInfo().getmRectCount() > 0)
                mTrackingRectView.setData(baseData.getmVideoFrameInfo());
        });

        mListener = new ScreenOrientationListener(this, ((ScreenOrientationType type) -> {
            if ((type == ScreenOrientationType.PORTRAIT || type == ScreenOrientationType.LANDSCAPE)
                    && mScreenOrientationType != type) {
                mScreenOrientationType = type;
                mMoFPVModeView.orientation(type);
            }
        }));
        mListener.enable();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AccessoryManager.getInstance().mIsRunning)
            mMoFPVModeView.syncMode(true);
        else
            AccessoryManager.getInstance().setConnectStateListener(new AccessoryManager.ConnectStateListener() {
                @Override
                public void connectedUSB() {
                    mHandler.postDelayed(mConnRunnable, 1000);
                }

                @Override
                public void disconnectedUSB() {
                    mHandler.removeCallbacks(mConnRunnable);
                }
            }, "FPVActivity");
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
        mPreviewTextureView.destoryOpengl();
        EffectManager.instance().destory();

        mListener.disable();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
