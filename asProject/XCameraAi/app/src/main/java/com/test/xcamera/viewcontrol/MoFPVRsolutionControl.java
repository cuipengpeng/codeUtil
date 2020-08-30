package com.test.xcamera.viewcontrol;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.test.xcamera.MoPresenters.MoFPVPresenter;
import com.test.xcamera.R;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.bean.EventMessage;
import com.test.xcamera.bean.MoRecordSetting;
import com.test.xcamera.bean.MoShotSetting;
import com.test.xcamera.enumbean.ShootMode;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.managers.ShotModeManager;
import com.test.xcamera.managers.WorkStateManager;
import com.test.xcamera.mointerface.MoRequestCallback;
import com.test.xcamera.statistic.StatisticFPVLayer;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zll on 2019/11/14.
 */

public class MoFPVRsolutionControl implements View.OnClickListener {
    private static final String TAG = "MoFPVSlowMotionControl";
    private static final int REFRESH_UI = 800001;
    private static final int REFRESH_STATUS = 800002;
    private static MoFPVRsolutionControl instance = null;
    private static Object lock = new Object();
    private volatile boolean swiching = false;
    private final static int DELAY = 1000;
    private long mLastTime;
    private final static Map<Integer, String> FPS_VALUES = new HashMap<>();
    private final static Map<Integer, String> RESOLUT_VALUES = new HashMap<>();

    {
        RESOLUT_VALUES.put(2, "4K");
        RESOLUT_VALUES.put(3, "2K");
        RESOLUT_VALUES.put(4, "1080P");
        FPS_VALUES.put(1, "24FPS");
        FPS_VALUES.put(2, "30FPS");
//        FPS_VALUES.put(3, "48FPS");
        FPS_VALUES.put(4, "60FPS");
    }

    private ArrayList<TextView> mResolutionList = new ArrayList<>();
    private ArrayList<TextView> mFpsList = new ArrayList<>();
    private ArrayList<View> mCallBackViews = new ArrayList<>();
    private int mResolution = 2, mFps = 2;
    private MoFPVPresenter mFPVPresenter;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case REFRESH_UI:
                    MoFPVShotSettingControl.getInstance().setBeautyAble((mFps == 1 || mFps == 2) && mResolution == 4);
                    setViewState(mResolution, mFps);
                    break;
                case REFRESH_STATUS:
                    swiching = false;
                    break;
            }
        }
    };

    public static MoFPVRsolutionControl getInstance() {
        synchronized (lock) {
            if (instance == null) {
                instance = new MoFPVRsolutionControl();
            }
        }
        return instance;
    }

    public void setFPVPresenter(MoFPVPresenter presenter) {
        mFPVPresenter = presenter;
    }

    public void initControl(Context context, View view, boolean refresh) {
        findView(view);
        initData();

        setViewState(mResolution, mFps);
        syncView();
        refresh();
    }

    public void release() {
        mResolutionList.clear();
        mFpsList.clear();
        mCallBackViews.clear();

        mFPVPresenter = null;
        mHandler.removeCallbacksAndMessages(null);
    }

    private void findView(View view) {
        mResolutionList.clear();
        mFpsList.clear();
        mResolutionList.add(view.findViewById(R.id.resolution_layout_4k));
        mResolutionList.add(view.findViewById(R.id.resolution_layout_2k));
        mResolutionList.add(view.findViewById(R.id.resolution_layout_1080p));
        mFpsList.add(view.findViewById(R.id.resolution_layout_24));
        mFpsList.add(view.findViewById(R.id.resolution_layout_30));
//        m48Text = view.findViewById(R.id.resolution_layout_48);
        mFpsList.add(view.findViewById(R.id.resolution_layout_60));
//        m120Text = view.findViewById(R.id.resolution_layout_120);
//        m240Text = view.findViewById(R.id.resolution_layout_240);

        mCallBackViews.clear();
        mCallBackViews.add(view.findViewById(R.id.resolution_layout_4k_wrap));
        mCallBackViews.add(view.findViewById(R.id.resolution_layout_2k_wrap));
        mCallBackViews.add(view.findViewById(R.id.resolution_layout_1080p_wrap));
        mCallBackViews.add(view.findViewById(R.id.resolution_layout_24_wrap));
        mCallBackViews.add(view.findViewById(R.id.resolution_layout_30_wrap));
        mCallBackViews.add(view.findViewById(R.id.resolution_layout_60_wrap));

        for (View v : mCallBackViews)
            v.setOnClickListener(this);
    }

    private void initData() {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.resolution_layout_4k_wrap:
                takeVideoSetting(2, mFps);
                break;
            case R.id.resolution_layout_2k_wrap:
                takeVideoSetting(3, mFps);
                break;
            case R.id.resolution_layout_1080p_wrap:
                takeVideoSetting(4, mFps);
                break;
            case R.id.resolution_layout_24_wrap:
                takeVideoSetting(mResolution, 1);
                break;
            case R.id.resolution_layout_30_wrap:
                takeVideoSetting(mResolution, 2);
                break;
//            case R.id.resolution_layout_48:
//                mFps = 3;
//                setFpsSelect(R.id.resolution_layout_48);
//                break;
            case R.id.resolution_layout_60_wrap:
                takeVideoSetting(mResolution, 4);
                break;
//            case R.id.resolution_layout_120:
//                mFps = 5;
//                setFpsSelect(R.id.resolution_layout_120);
//                break;
//            case R.id.resolution_layout_240:
//                mFps = 6;
//                setFpsSelect(R.id.resolution_layout_240);
//                break;
        }

        EventBus.getDefault().post(new EventMessage(EventMessage.HIDE_PARAMS_VIEW));
    }

    /**
     * 视频模式下 FPV页面分辨率、帧率使用TextView显示
     */
    private void syncView() {
        MoFPVShotSettingControl.getInstance().setFpsValue(FPS_VALUES.get(mFps), RESOLUT_VALUES.get(mResolution));
    }

    public void syncSetting(MoShotSetting shotSetting) {
        MoRecordSetting setting = shotSetting.getmMoRecordSetting();
        if (setting != null) {
            int resolution = setting.getmResolution();
            int fps = setting.getmFrameRate();
            mResolution = resolution;
            mFps = fps;
            mHandler.sendEmptyMessage(REFRESH_UI);
            syncView();
        }
    }

    private void setViewState(int resolution, int fps) {
        for (TextView textView : mResolutionList) {
            textView.setSelected(Integer.parseInt((String) textView.getTag()) == resolution);
        }

        for (TextView textView : mFpsList) {
            textView.setSelected(Integer.parseInt((String) textView.getTag()) == fps);
        }
    }

    public void takeVideoSetting(final int resolution, final int fps) {
        if (!swiching || !WorkStateManager.getInstance().enableSetting()) {
            StatisticFPVLayer.getInstance().setOnEvent(StatisticFPVLayer.FloatLayer_Capture_resolution, "resolution:" + resolution + "  fps:" + fps);

            swiching = true;
            mHandler.removeCallbacksAndMessages(REFRESH_STATUS);
            mHandler.sendEmptyMessageDelayed(REFRESH_STATUS, 3500);
            ConnectionManager.getInstance().stopPreview(new MoRequestCallback() {
                @Override
                public void onSuccess() {
                    ConnectionManager.getInstance().takeVideoSetting(resolution, fps, new MoRequestCallback() {
                        @Override
                        public void onSuccess() {
                            if (fps != -1)
                                mFps = fps;
                            if (resolution != -1)
                                mResolution = resolution;

                            AiCameraApplication.mApplication.mHandler.post(() -> {
                                syncView();
                                mFPVPresenter.takeVideoSetting(mResolution, mFps);
                                setViewState(mResolution, mFps);

                                MoFPVShotSettingControl.getInstance().setBeautyAble((mFps == 1 || mFps == 2) && mResolution == 4);
                            });
                        }

                        @Override
                        public void onFailed() {
                            refresh();
                            ConnectionManager.getInstance().startPreview(null);
                        }
                    });
                }

                @Override
                public void onFailed() {
                    ConnectionManager.getInstance().startPreview(new MoRequestCallback() {
                        @Override
                        public void onSuccess() {
                            refresh();
                        }

                        @Override
                        public void onFailed() {
                            refresh();
                        }
                    });
                }
            });
        }
    }

    public void refresh() {
        if (mHandler != null)
            mHandler.removeCallbacksAndMessages(REFRESH_STATUS);
        swiching = false;
    }

    /**
     * 是否正在切换分辨率
     */
    public boolean isSwiching() {
        return (ShotModeManager.getInstance().getmShootMode() == ShootMode.VIDEO && swiching);
    }
}
