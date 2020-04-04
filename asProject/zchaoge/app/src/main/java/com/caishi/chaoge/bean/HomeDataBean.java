package com.caishi.chaoge.bean;


import com.caishi.chaoge.base.BaseBean;

import java.io.Serializable;
import java.util.List;

/**
 * Redis保存 用户信息内嵌
 */
public class HomeDataBean extends BaseBean implements Serializable {
    /**
     * momentId : 46a63804c0c78e425cdb366429df332a
     * userId : 777f37be97d48cf9f90257100bd8fbd9
     * nickname : 商业大佬
     * avatar : https://p1-dy.bytecdn.cn/aweme/1080x1080/fc1900071b50a83287f5.jpeg
     * followStatus : 0
     * content : 所有的问题都是人的问题#陈果老师 #世界
     * createTime : 1551059724480
     * musicId : null
     * musicTitle : null
     * musicImageUrl : null
     * musicAuthor : null
     * videoUrl : http://video.chaogevideo.com/video/20192/25/5f5433aa88404c6ab3f0dcfd446b5ba2.mp4
     * cover : http://video.chaogevideo.com/image/20192/25/5f5433aa88404c6ab3f0dcfd446b5ba2.jpg
     * scriptId : null
     * location : null
     * informUserId : null
     * likeStatus : 0
     * likeCount : 1
     * commentCount : 0
     * forwardCount : 0
     * targetTime : 1553157332224
     * topic : []
     * momentType : null
     */

    public String momentId;
    public String userId;
    public String nickname;
    public String avatar;
    public int followStatus;
    public String content;
    public long createTime;
    public Object musicId;
    public String musicTitle;
    public String musicImageUrl;
    public String musicAuthor;
    public String videoUrl;
    public String cover;
    public String poster;
    public Object scriptId;
    public Object location;
    public Object informUserId;
    public int likeStatus;
    public int likeCount;
    public int commentCount;
    public int forwardCount;
    public long targetTime;
    public Object momentType;
    public List<?> topic;
    public boolean playStatus;
    public String modelId;

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public boolean isFullScreen() {
        return fullScreen;
    }

    public void setFullScreen(boolean fullScreen) {
        this.fullScreen = fullScreen;
    }

    public boolean fullScreen;

    public boolean isPlayStatus() {
        return playStatus;
    }

    public void setPlayStatus(boolean playStatus) {
        this.playStatus = playStatus;
    }

    public String getMomentId() {
        return momentId;
    }

    public void setMomentId(String momentId) {
        this.momentId = momentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getFollowStatus() {
        return followStatus;
    }

    public void setFollowStatus(int followStatus) {
        this.followStatus = followStatus;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public Object getMusicId() {
        return musicId;
    }

    public void setMusicId(Object musicId) {
        this.musicId = musicId;
    }

    public String getMusicTitle() {
        return musicTitle;
    }

    public void setMusicTitle(String musicTitle) {
        this.musicTitle = musicTitle;
    }

    public String getMusicImageUrl() {
        return musicImageUrl;
    }

    public void setMusicImageUrl(String musicImageUrl) {
        this.musicImageUrl = musicImageUrl;
    }

    public String getMusicAuthor() {
        return musicAuthor;
    }

    public void setMusicAuthor(String musicAuthor) {
        this.musicAuthor = musicAuthor;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public Object getScriptId() {
        return scriptId;
    }

    public void setScriptId(Object scriptId) {
        this.scriptId = scriptId;
    }

    public Object getLocation() {
        return location;
    }

    public void setLocation(Object location) {
        this.location = location;
    }

    public Object getInformUserId() {
        return informUserId;
    }

    public void setInformUserId(Object informUserId) {
        this.informUserId = informUserId;
    }

    public int getLikeStatus() {
        return likeStatus;
    }

    public void setLikeStatus(int likeStatus) {
        this.likeStatus = likeStatus;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getForwardCount() {
        return forwardCount;
    }

    public void setForwardCount(int forwardCount) {
        this.forwardCount = forwardCount;
    }

    public long getTargetTime() {
        return targetTime;
    }

    public void setTargetTime(long targetTime) {
        this.targetTime = targetTime;
    }

    public Object getMomentType() {
        return momentType;
    }

    public void setMomentType(Object momentType) {
        this.momentType = momentType;
    }

    public List<?> getTopic() {
        return topic;
    }

    public void setTopic(List<?> topic) {
        this.topic = topic;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }
}
