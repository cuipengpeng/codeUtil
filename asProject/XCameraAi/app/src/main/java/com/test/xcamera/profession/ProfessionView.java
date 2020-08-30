package com.test.xcamera.profession;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.test.xcamera.R;
import com.test.xcamera.activity.CameraMode;
import com.test.xcamera.bean.MoSettingModel;
import com.test.xcamera.enumbean.ScreenOrientationType;
import com.test.xcamera.view.AutoTrackingRectViewFix;

/**
 * Created by smz on 2020/1/15.
 * <p>
 * 专业模式总设置
 * GalleryLayoutManager
 */

public class ProfessionView extends FrameLayout {
    private ProfessionSettingView mProfessionSettingView;
    public ParamCustomView mParamCustomView;
    public ParamAWBView mParamAWBView;

    public ProfessionView(@NonNull Context context) {
        super(context);
        init();
    }

    public ProfessionView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        this.setClickable(true);
        this.setFocusable(true);
        View.inflate(getContext(), R.layout.layout_profession_view, this);
        mProfessionSettingView = findViewById(R.id.setting_view);
        mProfessionSettingView.setProfessionView(this);

        mParamCustomView = findViewById(R.id.custom_view);
        mParamAWBView = findViewById(R.id.awb_view);
    }

    /**
     * 重置页面
     */
    public void reset() {
    }

    public void syncMode(MoSettingModel model) {
        if (model == null)
            return;
        mProfessionSettingView.syncMode(model);
        mParamCustomView.syncMode(model);
        mParamAWBView.syncMode(model);
    }

    public void syncPtzMode(int ptzMode, int ptzSensitivity) {
        if (mProfessionSettingView != null)
            mProfessionSettingView.syncPtzMode(ptzMode, ptzSensitivity);
    }

    public void syncTrackMode(AutoTrackingRectViewFix trackView) {
        if (mProfessionSettingView != null)
            mProfessionSettingView.mTrackView = trackView;
    }

    public void orientation(ScreenOrientationType type) {
        mProfessionSettingView.orientation(type);
        mParamCustomView.orientation(type);
        mParamAWBView.orientation(type);
    }

    public void setMode(CameraMode mode) {
        mProfessionSettingView.setMode(mode);
    }

    public void setViewRotate(int viewRotate) {
        if (mProfessionSettingView != null)
            this.mProfessionSettingView.mViewRotate = viewRotate;
    }
}
