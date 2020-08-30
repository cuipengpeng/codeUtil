package com.test.xcamera.statistic;

import com.test.xcamera.application.AppContext;
import com.test.xcamera.base.StatisticHelper;

/**
 * 抖音拍摄模板
 */
public class StatisticDYCapture {
    /*浮层-抖音拍摄-镜头翻转*/
    public static final String FloatLayer_DYCapture_LensFlip="FloatLayer_DYCapture_LensFlip";
    /*浮层-抖音拍摄-快慢速*/
    public static final String FloatLayer_DYCapture_FastSlow="FloatLayer_DYCapture_FastSlow";
    /*浮层-抖音拍摄-滤镜*/
    public static final String FloatLayer_DYCapture_Filter="FloatLayer_DYCapture_Filter";
    /*浮层-抖音拍摄-美化*/
    public static final String FloatLayer_DYCapture_Beautify="FloatLayer_DYCapture_Beautify";
    /*浮层-抖音拍摄-道具*/
    public static final String FloatLayer_DYCapture_Prop="FloatLayer_DYCapture_Prop";
    /*浮层-抖音拍摄-选择音乐*/
    public static final String FloatLayer_DYCapture_SelectMusic="FloatLayer_DYCapture_SelectMusic";
    /*浮层-抖音拍摄-回中*/
    public static final String FloatLayer_Capture_MidPosition="FloatLayer_DYCapture_MidPositionmid-position";
    /*浮层-抖音拍摄-跟踪*/
    public static final String FloatLayer_DYCapture_Track="FloatLayer_DYCapture_Track";
    /*浮层-抖音拍摄-拍摄*/
    public static final String FloatLayer_DYCapture_capture	="FloatLayer_DYCapture_capture";
    /*浮层-抖音拍摄-合成*/
    public static final String FloatLayer_DYCapture_compose="FloatLayer_DYCapture_compose";
    /*浮层-抖音拍摄-变焦*/
    public static final String FloatLayer_DYCapture_Zoom="FloatLayer_DYCapture_Zoom";

    private static StatisticDYCapture instance;
    private StatisticDYCapture(){}
    public static StatisticDYCapture getInstance() {
        if(instance!=null){
            return instance;
        }
        instance=new StatisticDYCapture();
        return instance;
    }
    public void setOnEvent(String action){
        StatisticHelper.onEvent(AppContext.getInstance(),action);
    }
    public void setOnEvent(String action,String value){
        StatisticHelper.onEvent(AppContext.getInstance(),action,value);
    }
}
