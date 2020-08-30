package com.test.xcamera.statistic;

import com.test.xcamera.application.AppContext;
import com.test.xcamera.base.StatisticHelper;

/**
 * 浮层页数据埋点
 */
public class StatisticFloatLayer {
    /*浮层-侧面键一键成片*/
    public static final String FloatLayer_SideKey_OneClickVideo="FloatLayer_SideKey_OneClickVideo";
    /*浮层-今日精彩*/
    public static final String FloatLayer_TodayBrilliant="FloatLayer_TodayBrilliant";
    /*浮层-抖音拍摄*/
    public static final String FloatLayer_DYCapture="FloatLayer_DYCapture";
    /*浮层-相机拍摄*/
    public static final String FloatLayer_Capture="FloatLayer_Capture";
    /*浮层-我的相册*/
    public static final String FloatLayer_MyAlbum="FloatLayer_MyAlbum";
    /*浮层-编辑*/
    public static final String FloatLayer_MaterialEdit="FloatLayer_MaterialEdit";
    /*首页-我的页面*/
    public static final String Home_PersonalCenter="Home_PersonalCenter";
    private static StatisticFloatLayer instance;
    private StatisticFloatLayer(){}
    public static StatisticFloatLayer getInstance() {
        if(instance!=null){
            return instance;
        }
        instance=new StatisticFloatLayer();
        return instance;
    }
    public void setOnEvent(String action){
        StatisticHelper.onEvent(AppContext.getInstance(),action);
    }
    public void setOnEvent(String action,String value){
        StatisticHelper.onEvent(AppContext.getInstance(),action,value);
    }
}
