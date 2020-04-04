package com.caishi.chaoge.bean;

public class BackgroundBean {
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
