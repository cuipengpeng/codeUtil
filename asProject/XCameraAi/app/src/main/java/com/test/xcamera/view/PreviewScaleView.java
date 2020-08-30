package com.test.xcamera.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.test.xcamera.R;
import com.test.xcamera.bean.MoShotSetting;
import com.test.xcamera.enumbean.ScreenOrientationType;
import com.test.xcamera.viewcontrol.MoFPVScaleControl;

/**
 * Created by zll on 2019/9/29.
 */

public class PreviewScaleView extends RelativeLayout implements View.OnClickListener {
    private Context mContext;
    private View mLandView, mPortraitView;
    private ScreenOrientationType mType = ScreenOrientationType.LANDSCAPE;
    private MoShotSetting shotSetting;

    public PreviewScaleView(Context context) {
        super(context);
        initView(context);
    }

    public PreviewScaleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.preview_scale_layout, this, true);
        mLandView = findViewById(R.id.preview_scale_land_layout);
        mPortraitView = findViewById(R.id.preview_scale_portrait_layout);

        mLandView.setOnClickListener(this);
        mPortraitView.setOnClickListener(this);

        if (mType == ScreenOrientationType.LANDSCAPE) {
            MoFPVScaleControl.getInstance().initControl(context, mLandView, true);
        } else if (mType == ScreenOrientationType.PORTRAIT) {
            MoFPVScaleControl.getInstance().initControl(mContext, mPortraitView, true);
        }
    }

    public void changeOrientation(ScreenOrientationType orientationType) {
        mType = orientationType;
        if (orientationType == ScreenOrientationType.LANDSCAPE) {
            mPortraitView.setVisibility(View.GONE);
            mLandView.setVisibility(View.VISIBLE);
            MoFPVScaleControl.getInstance().initControl(mContext, mLandView, false);
        } else if (orientationType == ScreenOrientationType.PORTRAIT) {
            mLandView.setVisibility(View.GONE);
            mPortraitView.setVisibility(View.VISIBLE);
            MoFPVScaleControl.getInstance().initControl(mContext, mPortraitView, false);
        }
    }

    public void syncSetting(MoShotSetting shotSetting) {
        this.shotSetting = shotSetting;
        MoFPVScaleControl.getInstance().syncSetting(shotSetting);
    }

    @Override
    public void onClick(View view) {
    }
}
