package com.caishi.chaoge.bean;

import com.caishi.chaoge.base.BaseBean;

import java.util.List;

public class TemplateBean extends BaseBean {
    public int backGroundDuration;
    public String backGroundId;
    public String backGroundImage;
    public String backGroundVideo;
    public List<String> contentList;
    public String fontColorId;
    public String fontColorValue;
    public String fontId;
    public float fontScaleAndroid;
    public int fontSpecial;
    public String fontUrl;
    public int loop;
    public String modelCover;
    public String modelId;
    public String modelName;
    public String modelVideo;
    public String musicId;
    public String musicName;
    public String musicUrl;
    public String specialPosAndroid;
    public String scriptId;
    public long targetTime;
    public List<ScriptTimeInfo> scriptTimeInfoList;

    public class ScriptTimeInfo {
        public String andPos;
        public long endTime;
        public long startTime;
        public String fontColorId;
        public String fontColorValue;
    }


}
