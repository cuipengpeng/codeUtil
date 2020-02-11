package com.test.bank.bean;

import java.util.ArrayList;
import java.util.List;

public class TempleteScoreBean {

    public TempleteScoreBean() {
    }

    public TempleteScoreBean(String scene_class, int templetePosition, int count, float score, long duration, List<String> videoSegmentList) {
        this.scene_class = scene_class;
        this.templetePosition = templetePosition;
        this.count = count;
        this.score = score;
        this.duration = duration;
        this.videoSegmentList = videoSegmentList;
    }

    public int templetePosition;
    public int count;
    public float score;
    public long duration;
    public String scene_class;
    public List<String> videoSegmentList = new ArrayList<>();


    public String getScene_class() {
        return scene_class;
    }

    public void setScene_class(String scene_class) {
        this.scene_class = scene_class;
    }


    public List<String> getVideoSegmentList() {
        return videoSegmentList;
    }

    public void setVideoSegmentList(List<String> videoSegmentList) {
        this.videoSegmentList = videoSegmentList;
    }

    public int getTempletePosition() {
        return templetePosition;
    }

    public void setTempletePosition(int templetePosition) {
        this.templetePosition = templetePosition;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
