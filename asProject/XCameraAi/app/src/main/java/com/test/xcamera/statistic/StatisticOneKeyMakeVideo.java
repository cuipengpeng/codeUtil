package com.test.xcamera.statistic;

import com.test.xcamera.application.AppContext;
import com.test.xcamera.base.StatisticHelper;

/**
 * 浮层页数据埋点
 */
public class StatisticOneKeyMakeVideo {
//    浮层-侧面键一键成片 已埋点
    public static final String SIDE_KEY="FloatLayer_SideKey_OneClickVideo";
//    浮层-侧面键一键成片-生成
    public static final String SIDE_KEY_GENERATE="FloatLayer_SideKey_OneClickVideo_compose";
//    浮层-侧面键一键成片-编辑
    public static final String SIDE_KEY_EDIT="FloatLayer_SideKey_OneClickVideo_edit";
//    浮层-侧面键一键成片-选择模板id
    public static final String SIDE_KEY_SELECT_TEMPLETE_ID="SideKey_OneClickVideo_templateIdSelect";
//    浮层-今日精彩 已埋点
    public static final String TODAY_WONDERFUL_="FloatLayer_TodayBrilliant";
//    浮层-今日精彩-下一步
    public static final String TODAY_WONDERFUL_NEXT="FloatLayer_TodayBrilliant_next";
//    浮层-今日精彩-长模板
    public static final String TODAY_WONDERFUL_LONG_TEMPLETE="FloatLayer_TodayBrilliant_LongTemplatel";
//    浮层-今日精彩-短模板
    public static final String TODAY_WONDERFUL_SHORT_TEMPLETE="FloatLayer_TodayBrilliant_ShortTemplatel";

    private static StatisticOneKeyMakeVideo instance;
    private StatisticOneKeyMakeVideo(){}
    public static StatisticOneKeyMakeVideo getInstance() {
        if(instance!=null){
            return instance;
        }
        instance=new StatisticOneKeyMakeVideo();
        return instance;
    }
    public void setOnEvent(String action){
        StatisticHelper.onEvent(AppContext.getInstance(),action);
    }
    public void setOnEvent(String action,String value){
        StatisticHelper.onEvent(AppContext.getInstance(),action,value);
    }
}
