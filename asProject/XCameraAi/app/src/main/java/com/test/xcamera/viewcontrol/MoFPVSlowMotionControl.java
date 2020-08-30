package com.test.xcamera.viewcontrol;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.test.xcamera.R;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.bean.EventMessage;
import com.test.xcamera.bean.MoShotSetting;
import com.test.xcamera.enumbean.ShootMode;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.managers.ShotModeManager;
import com.test.xcamera.managers.WorkStateManager;
import com.test.xcamera.mointerface.MoRequestCallback;
import com.test.xcamera.utils.DlgUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by zll on 2019/11/14.
 */

public class MoFPVSlowMotionControl implements View.OnClickListener {
    private static final String TAG = "MoFPVSlowMotionControl";
    private static final int REFRESH_UI = 900001;
    private static MoFPVSlowMotionControl instance = null;
    private static Object lock = new Object();

    private TextView mSpeed1, mSpeed2, mSpeed3;
    private View mSpeed1W, mSpeed2W, mSpeed3W;
    private ArrayList<TextView> mTextViews;
    private volatile int mSpeed;
    private volatile boolean mValueSetting = false;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case REFRESH_UI:
                    if (mTextViews == null || mTextViews.size() == 0)
                        return;

                    for (TextView textView : mTextViews)
                        textView.setSelected((Integer.parseInt((String) textView.getTag()) == mSpeed));

                    switch (mSpeed) {
                        case 2:
                            setSelectState(R.id.preview_slowmotion_1);
                            break;
                        case 4:
                            setSelectState(R.id.preview_slowmotion_2);
                            break;
                        case 8:
                            setSelectState(R.id.preview_slowmotion_3);
                            break;
                    }

                    MoFPVShotSettingControl.getInstance().setSlowMotionValue(mSpeed, false);
                    break;
            }
        }
    };


    public static MoFPVSlowMotionControl getInstance() {
        synchronized (lock) {
            if (instance == null) {
                instance = new MoFPVSlowMotionControl();
            }
        }
        return instance;
    }

    public void release() {
        instance = null;
        mValueSetting = false;
    }

    public void initControl(Context context, View view, boolean refresh) {
        if (refresh)
            mValueSetting = false;

        findView(view);
        initData();

        setViewState();

        MoFPVShotSettingControl.getInstance().setSlowMotionValue(mSpeed, false);
    }

    private void findView(View view) {
        mSpeed1 = view.findViewById(R.id.preview_slowmotion_1);
        mSpeed2 = view.findViewById(R.id.preview_slowmotion_2);
        mSpeed3 = view.findViewById(R.id.preview_slowmotion_3);
        mSpeed1W = view.findViewById(R.id.preview_slowmotion_wrap_1);
        mSpeed2W = view.findViewById(R.id.preview_slowmotion_wrap_2);
        mSpeed3W = view.findViewById(R.id.preview_slowmotion_wrap_3);

        mSpeed1W.setOnClickListener(this);
        mSpeed2W.setOnClickListener(this);
        mSpeed3W.setOnClickListener(this);
    }

    private void initData() {
        mTextViews = new ArrayList<>();
        mTextViews.add(mSpeed1);
        mTextViews.add(mSpeed2);
        mTextViews.add(mSpeed3);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.preview_slowmotion_wrap_1:
//                setSelectState(R.id.preview_slowmotion_1);
                setSlowMotion(2);
//                mSpeed = 2;
                break;
            case R.id.preview_slowmotion_wrap_2:
//                setSelectState(R.id.preview_slowmotion_2);
                setSlowMotion(4);
//                mSpeed = 4;
                break;
            case R.id.preview_slowmotion_wrap_3:
//                setSelectState(R.id.preview_slowmotion_3);
                setSlowMotion(8);
//                mSpeed = 8;
                break;
        }
    }

    private void setSelectState(int viewID) {
        if (mTextViews != null)
            for (TextView textView : mTextViews)
                textView.setSelected(textView.getId() == viewID);
    }

    private void setViewState() {
        for (TextView textView : mTextViews) {
            textView.setSelected((Integer.parseInt((String) textView.getTag()) == mSpeed));
        }
    }

    public void syncSettings(MoShotSetting shotSetting) {
        if (shotSetting.getmMoRecordSetting() != null) {
            int speed = shotSetting.getmMoRecordSetting().getmSpeed();
            mSpeed = speed;
            mHandler.sendEmptyMessage(REFRESH_UI);
        }
    }

    private void setSlowMotion(final int speed) {
        if (mValueSetting || WorkStateManager.getInstance().enableSetting()) {
            DlgUtils.toast(AiCameraApplication.getContext(), AiCameraApplication.getContext().getResources().getString(R.string.param_setting), 0);
            return;
        }
        mValueSetting = true;
        ConnectionManager.getInstance().stopPreview(new MoRequestCallback() {
            @Override
            public void onSuccess() {
                ConnectionManager.getInstance().slowMotionSetting(speed, new MoRequestCallback() {

                    @Override
                    public void onSuccess() {
//                        EventBus.getDefault().post(new EventMessage(EventMessage.RESET_ZOOM));
                        EventBus.getDefault().post(new EventMessage(EventMessage.HIDE_PARAMS_VIEW));
                        mSpeed = speed;
                        mHandler.sendEmptyMessage(REFRESH_UI);
                        mHandler.post(() -> {
                            MoFPVShotSettingControl.getInstance().setSlowMotionValue(mSpeed, true);
                        });
//                        startPrew();
                    }

                    @Override
                    public void onFailed() {
                        startPrew();
                    }
                });
            }

            @Override
            public void onFailed() {
                mValueSetting = false;
            }
        });

    }

    public void startPrew() {
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

    public void refresh() {
        mHandler.postDelayed(() -> {
            mValueSetting = false;
        }, 300);
    }

    public boolean isSlowSetting() {
        return (ShotModeManager.getInstance().getmShootMode() == ShootMode.SLOW_MOTION && mValueSetting);
    }
}
