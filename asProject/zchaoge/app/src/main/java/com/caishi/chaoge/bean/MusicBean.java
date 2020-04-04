package com.caishi.chaoge.bean;

import java.util.List;

public class MusicBean {
        //"音频Id")
        public String musicId;
        //"音频地址")
        public String musicUrl;
        //"音频名称")
        public String title;
        //"封面图")
        public String imageUrl;
        //"音频作者")
        public String author;
        //"分类")
        public List<Integer> musicType;
        //"时长 单位 s")
        public long duration;
        //"话题")
        public List<String> topic;
        //"动态入队列的时间")
        public long targetTime;
        public boolean isClick;
        public boolean isDownloading = false;
        public String absolutePath;
}
