package com.test.xcamera.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;


import com.test.xcamera.MoPresenters.MoFPVPresenter;
import com.test.xcamera.R;
import com.test.xcamera.bean.MoShotSetting;
import com.test.xcamera.enumbean.ScreenOrientationType;
import com.test.xcamera.viewcontrol.MoFPVRsolutionControl;

/**
 * 分辨率，帧率选择控件
 * Created by zll on 2019/7/11.
 */

public class PreviewResolutionView extends RelativeLayout implements View.OnClickListener {
    private Context mContext;
    private View mLandView, mPortraitView;

    public PreviewResolutionView(Context context) {
        super(context);
        init(context);
    }

    public PreviewResolutionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void setFPVPresenter(MoFPVPresenter presenter) {
        MoFPVRsolutionControl.getInstance().setFPVPresenter(presenter);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.resolution_layout, this, true);

        mLandView = findViewById(R.id.preview_resolution_land_layout);
        mPortraitView = findViewById(R.id.preview_resolution_portrait_layout);

        mLandView.setOnClickListener(this);
        mPortraitView.setOnClickListener(this);

        MoFPVRsolutionControl.getInstance().initControl(mContext, mLandView, true);
    }

    public void changeOrientation(ScreenOrientationType orientationType) {
        if (orientationType == ScreenOrientationType.LANDSCAPE) {
            mLandView.setVisibility(View.VISIBLE);
            mPortraitView.setVisibility(View.GONE);
            MoFPVRsolutionControl.getInstance().initControl(mContext, mLandView, false);
        } else if (orientationType == ScreenOrientationType.PORTRAIT) {
            mPortraitView.setVisibility(View.VISIBLE);
            mLandView.setVisibility(View.GONE);
            MoFPVRsolutionControl.getInstance().initControl(mContext, mPortraitView, false);
        }
    }

    @Override
    public void onClick(View view) {
    }

    public void syncSetting(MoShotSetting shotSetting) {
        MoFPVRsolutionControl.getInstance().syncSetting(shotSetting);
    }
}
