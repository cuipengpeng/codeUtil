package com.test.xcamera.cameraclip.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VideoFile implements Serializable {


    private String name;
    private long create_time;
    private long close_time;
    private boolean selected=false;
    private String rec_mode;
    private float speedx;

    public String getRec_mode() {
        return rec_mode;
    }

    public void setRec_mode(String rec_mode) {
        this.rec_mode = rec_mode;
    }

    public float getSpeedx() {
        return speedx;
    }

    public void setSpeedx(float speedx) {
        this.speedx = speedx;
    }

    private List<VideoSegment> videoSegmentList =new ArrayList<>();

    private List<VideoScoreType> videoScoreTypeList =new ArrayList<>();

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public long getClose_time() {
        return close_time;
    }

    public void setClose_time(long close_time) {
        this.close_time = close_time;
    }


    public List<VideoScoreType> getVideoScoreTypeList() {
        return videoScoreTypeList;
    }

    public void setVideoScoreTypeList(List<VideoScoreType> videoScoreTypeList) {
        this.videoScoreTypeList = videoScoreTypeList;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public List<VideoSegment> getVideoSegmentList() {
        return videoSegmentList;
    }

    public void setVideoSegmentList(List<VideoSegment> videoSegmentList) {
        this.videoSegmentList = videoSegmentList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
