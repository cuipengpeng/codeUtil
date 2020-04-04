package com.caishi.chaoge.bean;

import com.caishi.chaoge.base.BaseBean;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelBean extends BaseBean implements Serializable {
//    public Result result;
//
//    public Result getResult() {
//        return new Result();
//    }
//
//    public class Result implements Serializable {

        // "模板Id")
        public String modelId;
        /* 剧本*/
        // "剧本Id")
        public String scriptId;
        // "标题")
        public String scriptTitle;
        // "内容慢 lrc")
        public ArrayList<String> slowContent;
        // "内容超慢 lrc")
        public ArrayList<String> superSlowContent;
        // "内容快 lrc")
        public ArrayList<String> fastContent;
        // "内容超快 lrc")
        public ArrayList<String> superFastContent;
        // "内容正常 lrc")
        public ArrayList<String> content;
        // "内容 UI显示")
        public String substance;
        // "剧本作者")
        public String scriptAuthor;
        // "剧本时长 由最慢到最快")
        public ArrayList<Long> scriptDuration;
        /* 音乐*/
        // "音频Id")
        public String musicId;
        // "音频地址")
        public String musicUrl;
        // "音频名称")
        public String musicTitle;
        // "音频作者")
        public String musicAuthor;
        // "时长 单位 s")
        public long musicDuration;
        /*背景图*/
        // "背景Id")
        public String backGroundId;
        // "背景图")
        public String bgImageUrl;
        // "幻灯片图")
        public ArrayList<String> bgSlideUrl;
        // "视频URL")
        public String bgVideoUrl;
        // "视频封面")
        public String bgCover;
        // "类别 0 图片  1 幻灯片 2 视频")
        public int backGroundClass;
        // "模板封面图")
        public String modelImage;
        // "字特效")
        public Integer fontSpecial = 0; /*字特效*/
        // "字体名字")
        public String fontTypeName;
        // "字体下载Url")
        public String fontTypeUrl;
        // "字颜色")
        public String fontColor = "000000"; /*字颜色*/
        // "动态入队列的时间")
        public long targetTime;
        public String pngPath;//下载图片保存路径
        public String mp3Path;//下载背景音乐保存路径
        public String fontPath;//下载字体保存路径
        public ArrayList<String> lrcList;//选择的内容
        public long duration;//时长
        public String wavFilePath;//录音文件
//    }
}
