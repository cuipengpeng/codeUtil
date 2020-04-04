package com.caishi.chaoge.bean;

import com.caishi.chaoge.base.BaseBean;

import java.util.ArrayList;

public class GetBackGroundBean extends BaseBean {
        public boolean isDownload = false;
        public boolean isSelect = false;
        public int backGroundClass;//类别 0 图片 1 幻灯片 2 视频 ,
        public String backGroundId;//背景Id ,
        public String cover;//视频封面 ,
        public String imageUrl;// 背景图 ,
        public ArrayList<String> slideUrl;// 幻灯片图 ,
        public long targetTime;//动态入redis队列的时间 ,
        public String videoUrl;//视频URL
//        backGroundTypes (Array[integer], optional): 分类 ,
}
