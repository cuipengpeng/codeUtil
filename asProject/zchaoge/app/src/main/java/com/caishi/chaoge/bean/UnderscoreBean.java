package com.caishi.chaoge.bean;

public class UnderscoreBean {
    public boolean isSelect;
    public boolean isDownload;//是否下载
    public int isPlay;//是否播放  -1 不显示   0 播放  1暂停
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
