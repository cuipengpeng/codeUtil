package com.test.xcamera.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.test.xcamera.R;
import com.test.xcamera.bean.MoShotSetting;
import com.test.xcamera.enumbean.ScreenOrientationType;
import com.test.xcamera.viewcontrol.MoFPVSlowMotionControl;

/**
 * Created by zll on 2019/9/29.
 */

public class PreviewSlowmotionView extends RelativeLayout implements View.OnClickListener {
    //    private TextView mSpeed1, mSpeed2, mSpeed3;
//    private ArrayList<TextView> mTextViews;
    private Context mContext;
    private View mLandView, mPortraitView;

    public PreviewSlowmotionView(Context context) {
        super(context);
        mContext = context;
        initView(context);
    }

    public PreviewSlowmotionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;

        LayoutInflater.from(context).inflate(R.layout.preview_slowmotion_layout, this, true);

        mLandView = findViewById(R.id.preview_slowmotion_land_layout);
        mPortraitView = findViewById(R.id.preview_slowmotion_portrait_layout);

        mLandView.setOnClickListener(this);
        mPortraitView.setOnClickListener(this);

        MoFPVSlowMotionControl.getInstance().initControl(context, mLandView, true);
    }

    public void changeOrientation(ScreenOrientationType orientationType) {
        if (orientationType == ScreenOrientationType.LANDSCAPE) {
            mLandView.setVisibility(View.VISIBLE);
            mPortraitView.setVisibility(View.GONE);
            MoFPVSlowMotionControl.getInstance().initControl(mContext, mLandView, false);
        } else if (orientationType == ScreenOrientationType.PORTRAIT) {
            mPortraitView.setVisibility(View.VISIBLE);
            mLandView.setVisibility(View.GONE);
            MoFPVSlowMotionControl.getInstance().initControl(mContext, mPortraitView, false);
        }
    }

    public void syncSetting(MoShotSetting shotSetting) {
        MoFPVSlowMotionControl.getInstance().syncSettings(shotSetting);
    }

    @Override
    public void onClick(View view) {

    }
}
