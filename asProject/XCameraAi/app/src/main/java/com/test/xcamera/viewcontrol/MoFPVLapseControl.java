//package com.meetvr.aicamera.viewcontrol;
//
//import android.content.Context;
//import android.os.Handler;
//import android.os.Message;
//import android.view.View;
//import android.widget.TextView;
//
//import com.meetvr.aicamera.R;
//import com.meetvr.aicamera.bean.EventMessage;
//import com.meetvr.aicamera.bean.MoShotSetting;
//import com.meetvr.aicamera.managers.ConnectionManager;
//import com.meetvr.aicamera.mointerface.MoRequestCallback;
//
//import org.greenrobot.eventbus.EventBus;
//
//import java.util.ArrayList;
//
///**
// * Created by zll on 2019/11/14.
// *
// * 以迁至PreviewLapseView
// */
//
//public class MoFPVLapseControl implements View.OnClickListener {
//    private static final String TAG = "MoFPVSlowMotionControl";
//    private static final int REFRESH_UI = 700001;
//    private static MoFPVLapseControl instance = null;
//    private static Object lock = new Object();
//    private Context mContext;
//
//    private TextView mLapse1, mLapse2, mLapse3, mLapse4, mLapse5;
//    private ArrayList<TextView> mTextViews;
//    private int mDuration;
//
//    private Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//
//            switch (msg.what) {
//                case REFRESH_UI:
//                    for (TextView textView : mTextViews) {
//                        if (Integer.parseInt((String) textView.getTag()) == mDuration) {
//                            textView.setSelected(true);
////                            textView.setTextColor(0XFF00D6B4);
//                        } else {
//                            textView.setSelected(false);
////                            textView.setTextColor(0XFFFFFFFF);
//                        }
//                    }
//
////                    MoFPVShotSettingControl.getInstance().setLapseValue(mDuration);
//                    break;
//            }
//        }
//    };
//
//    public static MoFPVLapseControl getInstance() {
//        synchronized (lock) {
//            if (instance == null) {
//                instance = new MoFPVLapseControl();
//            }
//        }
//        return instance;
//    }
//
//    public void initControl(Context context, View view) {
//        mContext = context;
//
//        findView(view);
//        initData();
//
//        setViewState();
////        MoFPVShotSettingControl.getInstance().setLapseValue(mDuration);
//    }
//
//    private void findView(View view) {
//        mLapse1 = view.findViewById(R.id.preview_lapse_1);
//        mLapse2 = view.findViewById(R.id.preview_lapse_2);
//        mLapse3 = view.findViewById(R.id.preview_lapse_3);
//        mLapse4 = view.findViewById(R.id.preview_lapse_4);
//        mLapse5 = view.findViewById(R.id.preview_lapse_5);
//
//        mLapse1.setOnClickListener(this);
//        mLapse2.setOnClickListener(this);
//        mLapse3.setOnClickListener(this);
//        mLapse4.setOnClickListener(this);
//        mLapse5.setOnClickListener(this);
//    }
//
//    private void initData() {
//        mTextViews = new ArrayList<>();
//        mTextViews.add(mLapse1);
//        mTextViews.add(mLapse2);
//        mTextViews.add(mLapse3);
//        mTextViews.add(mLapse4);
//        mTextViews.add(mLapse5);
//    }
//
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.preview_lapse_1:
//                setSelectState(R.id.preview_lapse_1);
//                mDuration = (int) (0.1 * 1000);
//                setLapseValue(mDuration);
//                break;
//            case R.id.preview_lapse_2:
//                setSelectState(R.id.preview_lapse_2);
//                mDuration = (int) (0.2 * 1000);
//                setLapseValue(mDuration);
//                break;
//            case R.id.preview_lapse_3:
//                setSelectState(R.id.preview_lapse_3);
//                mDuration = (int) (0.3 * 1000);
//                setLapseValue(mDuration);
//                break;
//            case R.id.preview_lapse_4:
//                setSelectState(R.id.preview_lapse_4);
//                mDuration = (int) (0.5 * 1000);
//                setLapseValue(mDuration);
//                break;
//            case R.id.preview_lapse_5:
//                setSelectState(R.id.preview_lapse_5);
//                mDuration = (int) (1.0 * 1000);
//                setLapseValue(mDuration);
//                break;
//        }
//
//        EventBus.getDefault().post(new EventMessage(EventMessage.HIDE_PARAMS_VIEW));
////        MoFPVShotSettingControl.getInstance().setLapseValue(mDuration);
//    }
//
//    private void setSelectState(int viewID) {
//        if (mTextViews != null) {
//            for (TextView textView : mTextViews) {
//                if (textView.getId() == viewID) {
//                    textView.setSelected(true);
////                    textView.setTextColor(0XFF00D6B4);
//                } else {
//                    textView.setSelected(false);
////                    textView.setTextColor(0XFFFFFFFF);
//                }
//            }
//        }
//    }
//
//    private void setLapseValue(final int value) {
//        ConnectionManager.getInstance().stopPreview(new MoRequestCallback() {
//            @Override
//            public void onSuccess() {
//                ConnectionManager.getInstance().laspeRecordSetting(value, new MoRequestCallback() {
//                    @Override
//                    public void onSuccess() {
//                        ConnectionManager.getInstance().startPreview(null);
//                    }
//
//                    @Override
//                    public void onFailed() {
//                        ConnectionManager.getInstance().startPreview(null);
//                    }
//                });
//            }
//
//            @Override
//            public void onFailed() {
//
//            }
//        });
//
//    }
//
//    private void setViewState() {
//        for (TextView textView : mTextViews) {
//            if (Integer.parseInt((String) textView.getTag()) == mDuration) {
//                textView.setSelected(true);
////                textView.setTextColor(0XFF00D6B4);
//            } else {
//                textView.setSelected(false);
////                textView.setTextColor(0XFFFFFFFF);
//            }
//        }
//    }
//
//    public void syncSettings(MoShotSetting shotSetting) {
//        if (shotSetting.getmMoRecordSetting() != null) {
//            int duration = shotSetting.getmMoRecordSetting().getmSpeed();
//            mDuration = duration;
//            mHandler.sendEmptyMessage(REFRESH_UI);
//        }
//    }
//}
