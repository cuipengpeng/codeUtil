package com.test.xcamera.cameraclip;

import java.util.HashMap;
import java.util.Map;

public class ScoreItemConstants {

    public static final float SCORE_FOR_THEME = 3f;  //主体
    public static final float SCORE_FOR_SCENE = 8f;  //场景
    public static final float SCORE_FOR_MARK = 20f;
    public static final float SCORE_FOR_TRACE_VIDEO = 5f; //视频跟踪
    public static final float SCORE_FOR_SINGLE_PERSON_AND_PET = 5f; //单人+宠物
    public static final float SCORE_FOR_DOUBLE_PERSON_AND_PET = 5f; //双人+宠物
    public static final float SCORE_FOR_DOUBLE_PERSON_MAN_AND_WOMAN = 5f; //一男一女
    public static final float SCORE_FOR_PET_CLOSE_UP = 5f; //宠物特写
    public static final float SCORE_FOR_LPSE_REC = 10f;  //延时摄影
    public static final float SCORE_FOR_PIZ_STILL = 3f; //云台静止
    public static final float SCORE_FOR_FORWARD_FACE = 0f; //正脸
    public static final float SCORE_FOR_HAPPY_FACE = 8f; //笑脸
    public Map<String , Integer> sceneMap = new HashMap<>();

    public  ScoreItemConstants() {
        sceneMap.clear();
        sceneMap.put("51",8);
        sceneMap.put("98",8);
        sceneMap.put("26",8);
        sceneMap.put("32",8);
        sceneMap.put("37",8);
        sceneMap.put("42",8);
        sceneMap.put("50",8);
        sceneMap.put("57",8);
        sceneMap.put("82",8);
        sceneMap.put("96",8);
        sceneMap.put("116",8);
        sceneMap.put("122",8);
        sceneMap.put("126",8);
        sceneMap.put("132",8);
        sceneMap.put("6",8);
        sceneMap.put("17",8);
        sceneMap.put("36",8);
        sceneMap.put("137",8);
        sceneMap.put("100",8);
        sceneMap.put("1",8);
        sceneMap.put("45",8);
        sceneMap.put("128",8);
        sceneMap.put("59",8);
        sceneMap.put("84",8);
        sceneMap.put("78",8);
        sceneMap.put("41",8);
        sceneMap.put("91",8);
        sceneMap.put("107",8);
        sceneMap.put("20",8);
        sceneMap.put("4",8);
    }
}
