package com.test.bank.bean;

import java.io.Serializable;

public class MusicBean implements Serializable {

    private String singer;
    private String path;
    private int duration;
    private long size;
    private String imgPath;
    private int imgRes;
    private int selectedImgRes;
    private String name;
    private String id;
    private boolean selected = false;

    public MusicBean() {
    }

    public MusicBean(String imgPath, int imgRes, String name, String id) {
        this.imgPath = imgPath;
        this.imgRes = imgRes;
        this.name = name;
        this.id = id;
    }
    public MusicBean(int imgRes, int selectedImgRes, String name) {
        this.imgRes = imgRes;
        this.selectedImgRes = selectedImgRes;
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getImgRes() {
        return imgRes;
    }

    public void setImgRes(int imgRes) {
        this.imgRes = imgRes;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getSelectedImgRes() {
        return selectedImgRes;
    }

    public void setSelectedImgRes(int selectedImgRes) {
        this.selectedImgRes = selectedImgRes;
    }
}
