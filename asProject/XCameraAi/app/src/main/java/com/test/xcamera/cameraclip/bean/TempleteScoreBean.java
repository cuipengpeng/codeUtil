package com.test.xcamera.cameraclip.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TempleteScoreBean implements Serializable {

    public TempleteScoreBean() {
    }

    public TempleteScoreBean(int templetePosition, int count, float score, long duration, List<String> videoSegmentList) {
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
    public List<String> videoSegmentList = new ArrayList<>();


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
