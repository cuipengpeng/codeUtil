package com.test.xcamera.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;


import com.test.xcamera.MoPresenters.MoFPVPresenter;
import com.test.xcamera.R;
import com.test.xcamera.bean.MoShotSetting;
import com.test.xcamera.enumbean.ScreenOrientationType;
import com.test.xcamera.mointerface.MoCountDownListener;
import com.test.xcamera.viewcontrol.MoFPVCountDownControl;

/**
 * 拍照倒计时控件
 * Created by zll on 2019/7/11.
 */

public class PreviewCountDownView extends RelativeLayout implements MoFPVCountDownControl.MoFPVCountDownListener,
        View.OnClickListener {
    private Activity mContext;
    private MoCountDownListener mCountDownListener;
    private View mLandView, mPortraitView;
    private MoFPVPresenter mFPVPresenter;

    public PreviewCountDownView(Context context) {
        super(context);
        mContext = (Activity) context;
        init(context);
    }

    public PreviewCountDownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = (Activity) context;
        init(context);
    }

    public void setFPVPresenter(MoFPVPresenter presenter) {
        mFPVPresenter = presenter;
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.count_down_view, this, true);

        mLandView = findViewById(R.id.preview_count_down_land_layout);
        mPortraitView = findViewById(R.id.preview_count_down_portrait_layout);

        MoFPVCountDownControl.getInstance().initControl(mContext, mLandView, mFPVPresenter, this);

        mLandView.setOnClickListener(this);
        mPortraitView.setOnClickListener(this);
    }

    public void setCountDownListener(MoCountDownListener listener) {
        mCountDownListener = listener;
    }

    public void changeOrientation(ScreenOrientationType orientationType) {
        if (orientationType == ScreenOrientationType.LANDSCAPE) {
            mLandView.setVisibility(View.VISIBLE);
            mPortraitView.setVisibility(View.GONE);
            MoFPVCountDownControl.getInstance().initControl(mContext, mLandView, mFPVPresenter, this);
        } else if (orientationType == ScreenOrientationType.PORTRAIT) {
            mPortraitView.setVisibility(View.VISIBLE);
            mLandView.setVisibility(View.GONE);
            MoFPVCountDownControl.getInstance().initControl(mContext, mPortraitView, mFPVPresenter, this);
        }
    }

    public void syncSetting(MoShotSetting shotSetting) {
        MoFPVCountDownControl.getInstance().syncSetting(shotSetting);
    }

    @Override
    public void change(int time) {
        if (mCountDownListener != null) {
            mCountDownListener.countDownChange(time);
        }
    }

    @Override
    public void onClick(View view) {

    }
}
