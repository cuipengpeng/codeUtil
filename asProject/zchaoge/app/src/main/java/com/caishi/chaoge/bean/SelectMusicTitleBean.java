package com.caishi.chaoge.bean;

public class SelectMusicTitleBean {
    private String normalDrawable;
    private String selectedDrawable;
    private boolean selected;

    /**
     *     0  古风
     *     1  网络热门
     *     2  情感
     *     3  欢快
     *     4  安静
     *     5  电音
     */
    private int musicType;

    public String getNormalDrawable() {
        return normalDrawable;
    }

    public void setNormalDrawable(String normalDrawable) {
        this.normalDrawable = normalDrawable;
    }

    public String getSelectedDrawable() {
        return selectedDrawable;
    }

    public void setSelectedDrawable(String selectedDrawable) {
        this.selectedDrawable = selectedDrawable;
    }

    public int getMusicType() {
        return musicType;
    }

    public void setMusicType(int musicType) {
        this.musicType = musicType;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
