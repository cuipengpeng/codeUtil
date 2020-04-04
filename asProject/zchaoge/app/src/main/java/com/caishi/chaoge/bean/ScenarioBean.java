package com.caishi.chaoge.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class ScenarioBean implements Serializable {

    /**
     * 剧本Id
     */
    //剧本Id")
    public String scriptId;
    /**
     * 标题
     */
    //标题")
    public String title;
    /**
     * 内容慢 lrc
     */
    //内容慢 lrc")
    public ArrayList<String> slowContent;
    /**
     * 内容超慢 lrc
     */
    //内容超慢 lrc")
    public ArrayList<String> superSlowContent;
    /**
     * 内容快 lrc
     */
    //内容快 lrc")
    public ArrayList<String> fastContent;
    /**
     * 内容超快 lrc
     */
    //内容超快 lrc")
    public ArrayList<String> superFastContent;
    /**
     * 内容正常 lrc
     */
    //内容正常 lrc")
    public ArrayList<String> content;
    /**
     * 内容 UI显示
     */
    //内容 UI显示")
    public String substance;
    /**
     * 作者
     */
    //作者")
    public String author;
    /**
     * 音频Id
     */
    //音频Id")
    public String musicId;
    /**
     * 音频地址
     */
    //音频地址")
    public String musicUrl;
    /**
     * 音频作者
     */
    //音频作者")
    public String musicAuthor;
    /**
     * 音频名称
     */
    //音频名称")
    public String musicTitle;
    /**
     * 图集
     */
    //图集")
    public String imageUrl;
    /**
     * 剧本时长 由最慢到最快
     */
    //剧本时长 由最慢到最快")
    public ArrayList<Long> duration;
    /**
     * 动态入队列的时间 createTime + 0000(4位) 队列中score值
     */
    //动态入队列的时间")
    public long targetTime;
}
