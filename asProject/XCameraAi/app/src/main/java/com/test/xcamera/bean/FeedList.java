package com.test.xcamera.bean;

import com.framwork.base.BaseResponse;

import java.io.Serializable;
import java.util.List;

/**
 * Created by smz on 2019/11/13.
 */

public class FeedList extends BaseResponse {

    private List<Feed> data;

    public List<Feed> getData() {
        return data;
    }

    public void setData(List<Feed> data) {
        this.data = data;
    }

    public static class Feed implements Serializable{
        /**
         * opusId : 作品id
         * videoFileId : 视频id
         * coverFileId : 封面Id
         * description : 视频描述
         * templateId : 模板ID
         * authorName : 作者名称
         */

        private long opusId;
        private long videoFileId;
        private long coverFileId;
        private String description;
        private long templateId;
        private String authorName;

        private int  shareNum;  //分享数量
        private int  likeNum;  //喜欢数量

        private boolean  isLike;//是否被喜欢过

        private long duration;//时长 秒  个人我的作品的返回

        private int bgmId;
        private int activityId;
        private int official;
        private String coverUrl;
        private String videoUrl;

        private String iconUrl;
        private String content;
        private String link;

        public String getIconUrl() {
            return iconUrl;
        }

        public void setIconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getCoverUrl() {
            return coverUrl;
        }

        public void setCoverUrl(String coverUrl) {
            this.coverUrl = coverUrl;
        }

        public String getVideoUrl() {
            return videoUrl;
        }

        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }

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

        @Override
        public String toString() {
            return "Feed{" +
                    "opusId=" + opusId +
                    ", videoFileId=" + videoFileId +
                    ", coverFileId=" + coverFileId +
                    ", description='" + description + '\'' +
                    ", templateId=" + templateId +
                    ", authorName='" + authorName + '\'' +
                    ", shareNum=" + shareNum +
                    ", likeNum=" + likeNum +
                    ", isLike=" + isLike +
                    ", duration=" + duration +
                    ", bgmId=" + bgmId +
                    ", activityId=" + activityId +
                    ", official=" + official +
                    '}';
        }
    }
}
