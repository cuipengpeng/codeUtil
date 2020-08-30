package com.test.xcamera.statistic;

import com.test.xcamera.application.AppContext;
import com.test.xcamera.base.StatisticHelper;

/**
 * 素材编辑埋点
 */
public class StatisticMaterialEdit {
    /*编辑-导入*/
    public static final String MaterialEdit_Import="MaterialEdit_Import";
    /*编辑-生成*/
    public static final String MaterialEdit_Compose="MaterialEdit_Compose";
    /*编辑-标题*/
    public static final String MaterialEdit_Title="MaterialEdit_Title";
    /*编辑-转场*/
    public static final String MaterialEdit_Transition="MaterialEdit_Transition";
    /*编辑-滤镜美化*/
    public static final String MaterialEdit_Filter="MaterialEdit_Filter";
    private static StatisticMaterialEdit instance;
    private StatisticMaterialEdit(){}
    public static StatisticMaterialEdit getInstance() {
        if(instance!=null){
            return instance;
        }
        instance=new StatisticMaterialEdit();
        return instance;
    }
    public void setOnEvent(String action){
        StatisticHelper.onEvent(AppContext.getInstance(),action);
    }
    public void setOnEvent(String action,String value){
        StatisticHelper.onEvent(AppContext.getInstance(),action,value);
    }
}
