package com.caishi.chaoge.bean;

import java.io.Serializable;
import java.util.List;

public class ModuleMaterialBean implements Serializable {

    public float duration = 0;//视频总时
    public float leftLocation = 0;//单行横排竖排默认开始位置
    public float topLocation = 0;//单行横排竖排默认Top位置
    public boolean isVideoToText = false;//是否是识别视频文件转文字
    public String bgPath;//背景图片或者视频的地址
    public String bgMusicPath;//背景音乐的地址
    public String personMusicPath;//录制音乐的地址
    public String fontFilePath;//文字字体包地址
    public List<String> colorList;//文字颜色
    public List<LrcBean> lrcList;//内容
    public String aeModulePath;//AE模板的路径
    public int specialFlag = 0;//特效类别 0自由旋转   "1多行横排", "2多行竖排", "3单行横排", "4单行竖排
}
