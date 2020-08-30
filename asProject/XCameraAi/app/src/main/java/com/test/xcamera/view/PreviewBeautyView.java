package com.test.xcamera.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.test.xcamera.R;
import com.test.xcamera.bean.MoShotSetting;
import com.test.xcamera.enumbean.ScreenOrientationType;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.managers.ShotModeManager;
import com.test.xcamera.mointerface.MoRequestCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * 磨皮、增亮使用tag做标记
 * 增亮的tag+10 与磨皮区分
 */

public class PreviewBeautyView extends RelativeLayout implements View.OnClickListener {
    private View mLandView, mPortraitView;
    private List<TextView> textViews = new ArrayList<>();
    private static final int[] wrapIDs = {
            R.id.beauty_smooth_off, R.id.beauty_smooth_1, R.id.beauty_smooth_2,
            R.id.beauty_light_off, R.id.beauty_light_1/*, R.id.beauty_light_2, R.id.beauty_light_3*/
    };
    private static final int[] textIDs = {
            R.id.beauty_smooth_off_tv, R.id.beauty_smooth_1_tv, R.id.beauty_smooth_2_tv,
            R.id.beauty_light_off_tv, R.id.beauty_light_1_tv/*, R.id.beauty_light_2_tv, R.id.beauty_light_3_tv*/
    };
    private int lightIndex, smoothIndex;

    public PreviewBeautyView(Context context) {
        super(context);
        init(context);
    }

    public PreviewBeautyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.beauty_layout, this, true);

        mLandView = findViewById(R.id.preview_beauty_land_layout);
        mPortraitView = findViewById(R.id.preview_beauty_portrait_layout);

        initView(mLandView);
    }

    public void changeOrientation(ScreenOrientationType orientationType) {
        boolean landVisible = mLandView.getVisibility() == View.VISIBLE;
        boolean portVisible = mPortraitView.getVisibility() == View.VISIBLE;

        if (orientationType == ScreenOrientationType.LANDSCAPE && (!landVisible || portVisible)) {
            mLandView.setVisibility(View.VISIBLE);
            mPortraitView.setVisibility(View.GONE);
            initView(mLandView);
            setState(smoothIndex);
            setState(lightIndex);
        } else if (orientationType == ScreenOrientationType.PORTRAIT && (!portVisible || landVisible)) {
            mPortraitView.setVisibility(View.VISIBLE);
            mLandView.setVisibility(View.GONE);
            initView(mPortraitView);
            setState(smoothIndex);
            setState(lightIndex);
        }
    }

    @Override
    public void onClick(View view) {
        int tag = Integer.parseInt((String) ((RelativeLayout) view).getChildAt(0).getTag());
        //增亮
        if (tag >= 10) {
            int light = tag - 10;
            ConnectionManager.getInstance().setBeautyLight(light, new MoRequestCallback() {
                @Override
                public void onSuccess() {
                    post(() -> {
                        lightIndex = tag;
                        setState(tag);
                    });
                }
            });
        } else {    //磨皮
            ConnectionManager.getInstance().setBeautySmooth(tag, new MoRequestCallback() {
                @Override
                public void onSuccess() {
                    post(() -> {
                        smoothIndex = tag;
                        setState(tag);
                    });
                }
            });
        }
    }

    public void syncSetting(MoShotSetting shotSetting) {
        if (shotSetting != null) {
            if (ShotModeManager.getInstance().isVideo()) {
                smoothIndex = shotSetting.getmMoRecordSetting().getBeauty_smooth();
                lightIndex = shotSetting.getmMoRecordSetting().getBeauty_light() + 10;
            } else {
                smoothIndex = shotSetting.getmMoSnapShotSetting().getBeauty_smooth();
                lightIndex = shotSetting.getmMoSnapShotSetting().getBeauty_light() + 10;
            }
        }

        setState(smoothIndex);
        setState(lightIndex);
    }

    private void setState(int index) {
        for (TextView v : textViews) {
            int tag = Integer.parseInt((String) v.getTag());
            if (tag >= 10 && index >= 10)
                v.setSelected(tag == index);
            else if (tag < 10 && index < 10)
                v.setSelected(tag == index);
        }
    }

    private void initView(View contentView) {
        textViews.clear();

        for (int id : wrapIDs) {
            contentView.findViewById(id).setOnClickListener(this);
        }
        for (int id : textIDs)
            textViews.add(contentView.findViewById(id));
    }
}
