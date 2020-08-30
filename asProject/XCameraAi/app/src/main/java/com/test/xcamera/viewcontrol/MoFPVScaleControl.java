package com.test.xcamera.viewcontrol;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.xcamera.R;
import com.test.xcamera.bean.EventMessage;
import com.test.xcamera.bean.MoShotSetting;
import com.test.xcamera.enumbean.ShootMode;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.managers.ShotModeManager;
import com.test.xcamera.managers.WorkStateManager;
import com.test.xcamera.mointerface.MoRequestCallback;
import com.test.xcamera.statistic.StatisticFPVLayer;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by zll on 2019/11/15.
 */

public class MoFPVScaleControl implements View.OnClickListener {
    public static final int SCALE_16_9 = 1;
    public static final int SCALE_4_3 = 2;
    public static final int SCALE_1_1 = 3;
    public static final int SCALE_195_9 = 4;
    public volatile int mScale = SCALE_16_9;

    private static final String TAG = "MoFPVScaleControl";
    private static final int REFRESH_UI = 500001;
    private static MoFPVScaleControl instance = null;
    private static Object lock = new Object();
    private Context mContext;

    private View mPhotoScaleLayout, mPictureNormalLayout, mPictureWidthLayout;
    private TextView mPictureNormalText, mPictureWidthText;
    private ImageView mPictureNormalImage, mPictureWidthImage;
    private ImageView mScale169Image, mScale1959Image, mScale11Image, mScale43Image;
    private int mPictureValue/*使用畸变校正*/, mScaleVaule, mLDC;
    private volatile boolean mSendSuccFlag = true;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case REFRESH_UI:
                    setViewState();
                    break;
            }
        }
    };

    public static MoFPVScaleControl getInstance() {
        synchronized (lock) {
            if (instance == null) {
                instance = new MoFPVScaleControl();
            }
        }
        return instance;
    }

    public void release() {
        if (mHandler != null)
            mHandler.removeCallbacksAndMessages(null);
        mSendSuccFlag = true;
        instance = null;
    }

    public void initControl(Context context, View view, boolean refresh) {
        if (refresh)
            mSendSuccFlag = true;

        initView(view);

        setViewState();
    }

    private void initView(View view) {
        mPictureNormalLayout = view.findViewById(R.id.preview_scale_layout_normal_layout);
        mPictureWidthLayout = view.findViewById(R.id.preview_scale_layout_width_layout);
        mPhotoScaleLayout = view.findViewById(R.id.preview_scale_layout_scale);
        mPictureNormalText = view.findViewById(R.id.preview_scale_layout_normal_text);
        mPictureWidthText = view.findViewById(R.id.preview_scale_layout_width_text);
        mPictureNormalImage = view.findViewById(R.id.preview_scale_layout_normal);
        mPictureWidthImage = view.findViewById(R.id.preview_scale_layout_width);
        mScale169Image = view.findViewById(R.id.preview_scale_layout_16_9);
        mScale1959Image = view.findViewById(R.id.preview_scale_layout_19_9);
        mScale11Image = view.findViewById(R.id.preview_scale_layout_1_1);
        mScale43Image = view.findViewById(R.id.preview_scale_layout_4_3);

        mPictureNormalLayout.setOnClickListener(this);
        mPictureWidthLayout.setOnClickListener(this);
        mScale169Image.setOnClickListener(this);
        mScale1959Image.setOnClickListener(this);
        mScale11Image.setOnClickListener(this);
        mScale43Image.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.preview_scale_layout_normal_layout:
//                if (!mPictureNormalLayout.isSelected()) {
//                    mPictureNormalLayout.setSelected(true);
//                    mPictureNormalImage.setSelected(true);
//                    mPictureNormalText.setSelected(true);
//                    MoFPVShotSettingControl.getInstance().setScaleIcon(true);
//
//                    mPictureWidthLayout.setSelected(false);
//                    mPictureWidthImage.setSelected(false);
//                    mPictureWidthText.setSelected(false);
//
//                    mLDC = 1;
////                    mPictureValue = 0;
//                }
                sendPicture();
                break;
            case R.id.preview_scale_layout_width_layout:
//                if (!mPictureWidthLayout.isSelected()) {
//                    mPictureWidthLayout.setSelected(true);
//                    mPictureWidthImage.setSelected(true);
//                    mPictureWidthText.setSelected(true);
//                    MoFPVShotSettingControl.getInstance().setScaleIcon(false);
//
//                    mPictureNormalLayout.setSelected(false);
//                    mPictureNormalImage.setSelected(false);
//                    mPictureNormalText.setSelected(false);
//
//                    mLDC = 0;
////                    mPictureValue = 1;
//                }
//                sendPicture();
                break;
            case R.id.preview_scale_layout_16_9:
                sendScale(1);
                break;
            case R.id.preview_scale_layout_19_9:
                sendScale(4);
                break;
            case R.id.preview_scale_layout_1_1:
                sendScale(3);
                break;
            case R.id.preview_scale_layout_4_3:
                sendScale(2);
                break;
        }

        EventBus.getDefault().post(new EventMessage(EventMessage.HIDE_PARAMS_VIEW));
    }

    private void sendScale(int scale) {
        if (!mSendSuccFlag || (mScale == scale) || WorkStateManager.getInstance().enableSetting())
            return;

        StatisticFPVLayer.getInstance().setOnEvent(StatisticFPVLayer.FloatLayer_Capture_Scale, "scale:" + scale);

        final int scaleValue = scale;
        mSendSuccFlag = false;
        ConnectionManager.getInstance().stopPreview(new MoRequestCallback() {
            @Override
            public void onSuccess() {
                ConnectionManager.getInstance().takePhotoSetting(scaleValue, new MoRequestCallback() {
                    @Override
                    public void onSuccess() {
                        mScaleVaule = mScale = scaleValue;
                        mHandler.post(() -> {
                            switch (scaleValue) {
                                case 1:
                                    mScale169Image.setSelected(true);
                                    mScale1959Image.setSelected(false);
                                    mScale11Image.setSelected(false);
                                    mScale43Image.setSelected(false);
                                    break;
                                case 2:
                                    mScale43Image.setSelected(true);
                                    mScale1959Image.setSelected(false);
                                    mScale169Image.setSelected(false);
                                    mScale11Image.setSelected(false);
                                    break;
                                case 3:
                                    mScale11Image.setSelected(true);
                                    mScale1959Image.setSelected(false);
                                    mScale169Image.setSelected(false);
                                    mScale43Image.setSelected(false);
                                    break;
                                case 4:
                                    mScale1959Image.setSelected(true);
                                    mScale169Image.setSelected(false);
                                    mScale11Image.setSelected(false);
                                    mScale43Image.setSelected(false);
                                    break;
                            }
                        });

//                        EventBus.getDefault().post(new EventMessage(EventMessage.RESET_ZOOM));
                        startPreView();
                    }

                    @Override
                    public void onFailed() {
                        startPreView();
                    }
                });
            }

            @Override
            public void onFailed() {

            }
        });
    }

    private void startPreView() {
        ConnectionManager.getInstance().startPreview(new MoRequestCallback() {
            @Override
            public void onSuccess() {
                mHandler.postDelayed(() -> {
                    mSendSuccFlag = true;
                }, 300);
            }

            @Override
            public void onFailed() {
                mHandler.postDelayed(() -> {
                    mSendSuccFlag = true;
                }, 300);
            }
        });
    }

    private void sendPicture() {
        ConnectionManager.getInstance().distortionCorrection(mLDC, null);
//        EventBus.getDefault().post(new EventMessage(EventMessage.RESET_ZOOM));
    }

    public void syncSetting(MoShotSetting shotSetting) {
        if (shotSetting.getmMoSnapShotSetting() != null) {
            mScaleVaule = shotSetting.getmMoSnapShotSetting().getmProportion();
            mLDC = shotSetting.getmMoSnapShotSetting().getmLDC();
//            mPictureValue = shotSetting.getmMoSnapShotSetting().getmPictureType();
        }
        if (shotSetting.getmMoRecordSetting() != null) {
            mScaleVaule = shotSetting.getmMoRecordSetting().getmProportion();
            mLDC = shotSetting.getmMoRecordSetting().getmLDC();
//            mPictureValue = shotSetting.getmMoRecordSetting().getmPictureType();
        }
        mHandler.sendEmptyMessage(REFRESH_UI);
    }

    private void setViewState() {
        //判断是否展示长宽比view
        if (ShotModeManager.getInstance().getmShootMode() == ShootMode.PHOTO
                || ShotModeManager.getInstance().getmShootMode() == ShootMode.LONG_EXPLORE
                || ShotModeManager.getInstance().getmShootMode() == ShootMode.PHOTO_BEAUTY) {
            if (mPhotoScaleLayout != null)
                mPhotoScaleLayout.setVisibility(View.VISIBLE);
        } else {
            if (mPhotoScaleLayout != null)
                mPhotoScaleLayout.setVisibility(View.GONE);
        }

        if (mLDC == 1) {
            mPictureNormalLayout.setSelected(true);
            mPictureNormalImage.setSelected(true);
            mPictureNormalText.setSelected(true);
            MoFPVShotSettingControl.getInstance().setScaleIcon(true);
            mPictureWidthLayout.setSelected(false);
            mPictureWidthImage.setSelected(false);
            mPictureWidthText.setSelected(false);
        } else if (mLDC == 0) {
            mPictureWidthLayout.setSelected(true);
            mPictureWidthLayout.setSelected(true);
            mPictureWidthLayout.setSelected(true);
            MoFPVShotSettingControl.getInstance().setScaleIcon(false);
            mPictureNormalLayout.setSelected(false);
            mPictureNormalImage.setSelected(false);
            mPictureNormalText.setSelected(false);
        }
        if (mScaleVaule == 1) {
            mScale169Image.setSelected(true);
            mScale1959Image.setSelected(false);
            mScale11Image.setSelected(false);
            mScale43Image.setSelected(false);
        } else if (mScaleVaule == 2) {
            mScale43Image.setSelected(true);
            mScale1959Image.setSelected(false);
            mScale169Image.setSelected(false);
            mScale11Image.setSelected(false);
        } else if (mScaleVaule == 3) {
            mScale11Image.setSelected(true);
            mScale1959Image.setSelected(false);
            mScale169Image.setSelected(false);
            mScale43Image.setSelected(false);
        } else if (mScaleVaule == 4) {
            mScale1959Image.setSelected(true);
            mScale169Image.setSelected(false);
            mScale11Image.setSelected(false);
            mScale43Image.setSelected(false);
        }
    }

    public boolean isScaleFlag() {
        return (!ShotModeManager.getInstance().isVideo() && !mSendSuccFlag);
    }
}
