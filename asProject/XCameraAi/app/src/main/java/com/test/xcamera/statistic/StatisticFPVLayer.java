package com.test.xcamera.statistic;

import com.test.xcamera.application.AppContext;
import com.test.xcamera.base.StatisticHelper;

/**
 * fpv页面数据埋点
 */
public class StatisticFPVLayer {
    public static final String FloatLayer_Capture_Picture = "FloatLayer_Capture_Picture";
    public static final String FloatLayer_Capture_LongExposure = "FloatLayer_Capture_LongExposure";
    public static final String FloatLayer_Capture_Video = "FloatLayer_Capture_Video";
    public static final String FloatLayer_Capture_SlowMotion = "FloatLayer_Capture_SlowMotion";
    public static final String FloatLayer_Capture_Lapse = "FloatLayer_Capture_Lapse";

    /**
     * 开始录像
     * */
    public static final String FloatLayer_Capture_Video_Start = "FloatLayer_Capture_Video_Start";
    /**
     * 结束录像
     * */
    public static final String FloatLayer_Capture_Video_Stop = "FloatLayer_Capture_Video_Stop";
    /**
     * 拍照
     * */
    public static final String FloatLayer_Capture_Photo = "FloatLayer_Capture_Photo";
    public static final String FloatLayer_Capture_Track = "FloatLayer_Capture_Track";
    public static final String FloatLayer_Capture_LensFlip = "FloatLayer_Capture_LensFlip";
    public static final String FloatLayer_Capture_MidPosition = "FloatLayer_Capture_MidPosition";
    public static final String FloatLayer_Capture_resolution = "FloatLayer_Capture_resolution";
    public static final String FloatLayer_Capture_ViewAlbum = "FloatLayer_Capture_ViewAlbum";
    public static final String FloatLayer_Capture_Scale = "FloatLayer_Capture_Scale";
    public static final String FloatLayer_Capture_CountDown = "FloatLayer_Capture_CountDown";
    public static final String FloatLayer_Capture_ExposureParameterSelection = "FloatLayer_Capture_ExposureParameterSelection";
    public static final String FloatLayer_Capture_Setting = "FloatLayer_Capture_Setting";


    private static StatisticFPVLayer instance;

    private StatisticFPVLayer() {
    }

    public static StatisticFPVLayer getInstance() {
        if (instance != null) {
            return instance;
        }
        instance = new StatisticFPVLayer();
        return instance;
    }

    public void setOnEvent(String action) {
        StatisticHelper.onEvent(AppContext.getInstance(), action);
    }

    public void setOnEvent(String action, String value) {
        StatisticHelper.onEvent(AppContext.getInstance(), action, value);
    }
}
