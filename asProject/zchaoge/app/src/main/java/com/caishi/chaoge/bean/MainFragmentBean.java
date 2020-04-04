package com.caishi.chaoge.bean;

import com.caishi.chaoge.base.BaseBean;

import java.util.ArrayList;

public class MainFragmentBean extends BaseBean {
    public ArrayList<BackGroundList> backGroundList;
    public ArrayList<FontList> fontList;
    public ArrayList<ModelList> modelList;
    public ArrayList<MusicList> musicList;
    public ArrayList<String> colorList;


    public class BackGroundList {
        public boolean isSelect;
        public boolean isDownload;//是否下载
        public int itemType = 1; //itemType
        public int imgRes; //图片drawable
        public int backGroundClass;//(integer, optional): 类别 0 图片 1 幻灯片 2 视频 ,
        public String backGroundId;//(string, optional): 背景Id ,
        public String cover;//(string, optional): 视频封面 ,
        public String imageUrl;//(string, optional): 背景图
        public int slideUrl;//(Array[string], optional): 幻灯片图 ,
        public long targetTime;//(string, optional): 动态入redis队列的时间 ,
        public String videoUrl;//(string, optional): 视频URL
    }


    public class FontList {
        public boolean isDownload;
        public boolean isSelect;
        public int itemType; //itemType
        public String fontId;//(string, optional):字体Id ,,
        public String fontUrl;//(string, optional):字体下载地址 ,
        public String selectUrl;//(string, optional): 已选图
        public String unSelectUrl;//(string, optional): 未选图
    }

    public class ModelList {
        public boolean isSelect;
        public String id;//(string, optional):字体Id ,,
        public long targetTime;//(int, optional):targetTime ,
        public String value;//(string, optional): value
    }

    public class MusicList {
        public boolean isSelect;
        public boolean isDownload;//是否下载
        public int itemType = 1; //itemType
        public int imgRes; //图片drawable
        public String author;//(string, optional): 音频作者 ,
        public int duration;//(integer, optional): 时长 单位 s ,
        public String imageUrl;//(string, optional): 封面图 ,
        public String musicId;//(string, optional): 音频Id ,
        public String musicUrl;//(string, optional): 音频地址 ,
        public long targetTime;//(integer, optional): 动态入队列的时间 ,
        public String title;//(string, optional): 音频名称 ,
    }


}
