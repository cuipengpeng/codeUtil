package com.test.xcamera.home.adapter;


import java.io.Serializable;

public class FeedBean implements Serializable {

    private long opusId;
    private long videoFileId;
    private long coverFileId;
    private long templateId;
    private String description;
    private String authorName;
    private int  shareNum;  //分享数量
    private int  likeNum;  //喜欢数量
    private boolean  isLike;//是否被喜欢过
    private long duration;//时长 秒  个人我的作品的返回
    private int bgmId;
    private int activityId;
    private int official;

    public int getBgmId() {
        return bgmId;
    }

    public void setBgmId(int bgmId) {
        this.bgmId = bgmId;
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public int getOfficial() {
        return official;
    }

    public void setOfficial(int official) {
        this.official = official;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    public int getShareNum() {
        return shareNum;
    }

    public void setShareNum(int shareNum) {
        this.shareNum = shareNum;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public long getOpusId() {
        return opusId;
    }

    public void setOpusId(long opusId) {
        this.opusId = opusId;
    }

    public long getVideoFileId() {
        return videoFileId;
    }

    public void setVideoFileId(long videoFileId) {
        this.videoFileId = videoFileId;
    }

    public long getCoverFileId() {
        return coverFileId;
    }

    public void setCoverFileId(long coverFileId) {
        this.coverFileId = coverFileId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(long templateId) {
        this.templateId = templateId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
}
