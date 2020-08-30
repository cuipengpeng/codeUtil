package com.test.xcamera.viewcontrol;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.test.xcamera.MoPresenters.MoFPVPresenter;
import com.test.xcamera.R;
import com.test.xcamera.bean.EventMessage;
import com.test.xcamera.bean.MoShotSetting;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.statistic.StatisticFPVLayer;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by zll on 2019/11/14.
 */

public class MoFPVCountDownControl implements View.OnClickListener {
    private static final String TAG = "MoFPVCountDownControl";
    private static final int REFRESH_UI = 600001;
    private static MoFPVCountDownControl instance = null;
    private static Object lock = new Object();
    private MoFPVPresenter mPresenter;
    private ArrayList<TextView> mTextList = new ArrayList<>();
    private int mCountDown = 0;
    private MoFPVCountDownListener mCountDownListener;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REFRESH_UI:
                    for (TextView textView : mTextList) {
                        if (Integer.parseInt((String) textView.getTag()) == mCountDown) {
                            textView.setSelected(true);
                        } else {
                            textView.setSelected(false);
                        }
                    }

                    changeSeconds();
                    MoFPVShotSettingControl.getInstance().setCountDownValue(mCountDown);
                    break;
            }
        }
    };

    public static MoFPVCountDownControl getInstance() {
        synchronized (lock) {
            if (instance == null) {
                instance = new MoFPVCountDownControl();
            }
        }
        return instance;
    }

    public void release() {
        mTextList.clear();
        mCountDownListener = null;
        mHandler.removeCallbacksAndMessages(null);
    }

    public void initControl(Context context, View view, MoFPVPresenter presenter,
                            MoFPVCountDownListener listener) {
        mPresenter = presenter;
        mCountDownListener = listener;

        findView(view);
        initData();
        setViewState();

        changeSeconds();
        MoFPVShotSettingControl.getInstance().setCountDownValue(mCountDown);
    }

    private void findView(View view) {
        mTextList.clear();
        mTextList.add(view.findViewById(R.id.fragment_preview_control_photo_child_off));
        mTextList.add(view.findViewById(R.id.fragment_preview_control_photo_child_3s));
        mTextList.add(view.findViewById(R.id.fragment_preview_control_photo_child_5s));
        mTextList.add(view.findViewById(R.id.fragment_preview_control_photo_child_7s));

        for (TextView tv : mTextList)
            tv.setOnClickListener(this);
    }

    private void initData() {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragment_preview_control_photo_child_off:
                setCountDownSelect(R.id.fragment_preview_control_photo_child_off);
                mCountDown = 0;
                setCountDown(mCountDown);
                changeSeconds();
                break;
            case R.id.fragment_preview_control_photo_child_3s:
                setCountDownSelect(R.id.fragment_preview_control_photo_child_3s);
                mCountDown = 3;
                setCountDown(mCountDown);
                changeSeconds();
                break;
            case R.id.fragment_preview_control_photo_child_5s:
                setCountDownSelect(R.id.fragment_preview_control_photo_child_5s);
                mCountDown = 5;
                setCountDown(mCountDown);
                changeSeconds();
                break;
            case R.id.fragment_preview_control_photo_child_7s:
                setCountDownSelect(R.id.fragment_preview_control_photo_child_7s);
                mCountDown = 7;
                setCountDown(mCountDown);
                changeSeconds();
                break;
        }

        EventBus.getDefault().post(new EventMessage(EventMessage.HIDE_PARAMS_VIEW));
        MoFPVShotSettingControl.getInstance().setCountDownValue(mCountDown);
        StatisticFPVLayer.getInstance().setOnEvent(StatisticFPVLayer.FloatLayer_Capture_CountDown, "countDown:" + mCountDown);
    }

    private void changeSeconds() {
        if (mCountDownListener != null) {
            mCountDownListener.change(mCountDown);
        }
    }

    private void setCountDownSelect(int viewID) {
        for (TextView textView : mTextList) {
            if (textView.getId() == viewID) {
                textView.setSelected(true);
//                textView.setTextColor(0XFF00D6B4);
            } else {
                textView.setSelected(false);
//                textView.setTextColor(0XFFFFFFFF);
            }
        }
    }

    private void setViewState() {
        for (TextView textView : mTextList) {
            if (Integer.parseInt((String) textView.getTag()) == mCountDown) {
                textView.setSelected(true);
            } else {
                textView.setSelected(false);
            }
        }
    }

    public int getmCountDown() {
        return mCountDown;
    }

    public void syncSetting(MoShotSetting shotSetting) {
        if (shotSetting.getmMoSnapShotSetting() != null) {
            int countDown = shotSetting.getmMoSnapShotSetting().getmDelayTime();
            mCountDown = countDown;
            mHandler.sendEmptyMessage(REFRESH_UI);
        }
    }

    private void setCountDown(int time) {
//        mPresenter.setDelayPhotoTime(time);
        ConnectionManager.getInstance().setDelayTime(time, null);
    }

    public interface MoFPVCountDownListener {
        void change(int time);
    }
}
