package com.caishi.chaoge.bean;


import java.io.Serializable;
import java.util.Random;

public class MyLrcBean implements Serializable {

    private String lrc;
    private float start;
    private float end;
    private int time;

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public MyLrcBean() {
    }

    public MyLrcBean(String text, float start, float end) {
        this.lrc = text;
        this.start = start;
        this.end = end;

        angle = random.nextInt(20);
        deltaX = random.nextInt(20)+45;
        deltaY = random.nextInt(20)+100;
    }

    private Random random = new Random();
    private int angle;
    private int deltaX;
    private int deltaY;

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

    public float getStart() {
        return start;
    }

    public void setStart(float start) {
        this.start = start;
    }

    public float getEnd() {
        return end;
    }

    public void setEnd(float end) {
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
