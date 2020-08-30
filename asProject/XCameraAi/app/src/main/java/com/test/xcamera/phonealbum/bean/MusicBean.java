package com.test.xcamera.phonealbum.bean;

import com.google.gson.annotations.SerializedName;
import com.test.xcamera.R;

import java.io.Serializable;

public class MusicBean implements Serializable {
    @SerializedName("singer")
    private String singer;
    @SerializedName("duration")
    private int duration;
    @SerializedName("name")
    private String name;
    @SerializedName("id")
    private String id;
    @SerializedName("musicFileUrl")
    private String musicFileURL;
    @SerializedName("coverFileUrl")
    private String coverFileURL;

    private int sourceType;// 来源 1:抖音  2:100Audio

    private boolean mIsLocal=false;
    private String path;
    private long size;
    private String imgPath;
    private int imgRes = R.mipmap.main_background;
    private int selectedImgRes = R.mipmap.main_background;

    private boolean selected = false;
    private float defaultValue;
    private String beautyKey;

    public MusicBean() {
    }
    public MusicBean(int imgRes, int selectedImgRes, String name) {
        this.imgRes = imgRes;
        this.selectedImgRes = selectedImgRes;
        this.name = name;
    }
    public MusicBean(String imgPath, int imgRes, String name, String id) {
        this.imgPath = imgPath;
        this.imgRes = imgRes;
        this.name = name;
        this.id = id;
    }
    public MusicBean(int imgRes, int selectedImgRes, String name, float defaultValue, String beautyKey) {
        this.imgRes = imgRes;
        this.selectedImgRes = selectedImgRes;
        this.name = name;
        this.defaultValue = defaultValue;
        this.beautyKey = beautyKey;
    }


    public String getBeautyKey() {
        return beautyKey;
    }
    public void setBeautyKey(String beautyKey) {
        this.beautyKey = beautyKey;
    }
    public float getDefaultValue() {
        return defaultValue;
    }
    public void setDefaultValue(float defaultValue) {
        this.defaultValue = defaultValue;
    }
    public boolean isSelected() {
        return selected;
    }

    public String getMusicFileURL() {
        return musicFileURL;
    }

    public void setMusicFileURL(String musicFileURL) {
        this.musicFileURL = musicFileURL;
    }

    public String getCoverFileURL() {
        return coverFileURL;
    }

    public void setCoverFileURL(String coverFileURL) {
        this.coverFileURL = coverFileURL;
    }

    public int getSourceType() {
        return sourceType;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
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

    public boolean ismIsLocal() {
        return mIsLocal;
    }

    public void setIsLocal(boolean mIsLocal) {
        this.mIsLocal = mIsLocal;
    }

    @Override
    public String toString() {
        return "MusicBean{" +
                "singer='" + singer + '\'' +
                ", duration=" + duration +
                ", name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", musicFileURL='" + musicFileURL + '\'' +
                ", coverFileURL='" + coverFileURL + '\'' +
                ", path='" + path + '\'' +
                ", size=" + size +
                ", imgPath='" + imgPath + '\'' +
                ", imgRes=" + imgRes +
                ", selectedImgRes=" + selectedImgRes +
                ", selected=" + selected +
                ", defaultValue=" + defaultValue +
                ", beautyKey='" + beautyKey + '\'' +
                '}';
    }
}
