package com.caishi.chaoge.bean;


import com.caishi.chaoge.base.BaseApplication;
import com.caishi.chaoge.utils.DisplayMetricsUtil;

import java.io.Serializable;
import java.util.Random;

public class LrcBean implements Serializable {

    private String lrc;
    private long start;
    private long end;
    private int time;

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public LrcBean() {
    }

    public LrcBean(String text, long start, long end) {
        this.lrc = text;
        this.start = start;
        this.end = end;
        Random random = new Random();
        angle = random.nextInt(20);
        deltaX = random.nextInt(20)+DisplayMetricsUtil.getScreenWidth(BaseApplication.getContext())/6;
        deltaY = DisplayMetricsUtil.getScreenHeight(BaseApplication.getContext())/6;
    }

    private int angle;
    private int deltaX;
    private int deltaY;


    public void setDeltaY(int deltaY) {
        this.deltaY = deltaY;
    }

    public int getAngle() {
        return angle;
    }

    public int getDeltaX() {
        return deltaX;
    }

    public int getDeltaY() {
        return deltaY;
    }

    public String getLrc() {
        return lrc;
    }

    public void setLrc(String lrc) {
        this.lrc = lrc;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "LrcBean{" +
                "lrc='" + lrc + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", time=" + time +
                '}';
    }
}
