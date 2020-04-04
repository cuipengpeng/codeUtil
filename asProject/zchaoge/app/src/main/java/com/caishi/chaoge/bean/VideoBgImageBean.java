package com.caishi.chaoge.bean;

import java.util.List;

public class VideoBgImageBean {

    private List<ResultBean> result;

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * backGroundId : e5384a0c4c18951ffbd7e3eb60417901
         * imageUrl : http://video.chaogevideo.com/backGround/201811/28/8712398d11e044dbb632dd4ba1474b22.jpg
         * slideUrl : null
         * videoUrl : null
         * cover : null
         * character : []
         * backGroundTypes : [1]
         * backGroundClass : 0
         * targetTime : 1543393032682270
         */

        private String backGroundId;
        private String imageUrl;
        private Object slideUrl;
        private Object videoUrl;
        private Object cover;
        private int backGroundClass;
        private long targetTime;
        private List<?> character;
        private List<Integer> backGroundTypes;
        private boolean selected = false;
        private String absolutePath;


        public String getAbsolutePath() {
            return absolutePath;
        }

        public void setAbsolutePath(String absolutePath) {
            this.absolutePath = absolutePath;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public String getBackGroundId() {
            return backGroundId;
        }

        public void setBackGroundId(String backGroundId) {
            this.backGroundId = backGroundId;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public Object getSlideUrl() {
            return slideUrl;
        }

        public void setSlideUrl(Object slideUrl) {
            this.slideUrl = slideUrl;
        }

        public Object getVideoUrl() {
            return videoUrl;
        }

        public void setVideoUrl(Object videoUrl) {
            this.videoUrl = videoUrl;
        }

        public Object getCover() {
            return cover;
        }

        public void setCover(Object cover) {
            this.cover = cover;
        }

        public int getBackGroundClass() {
            return backGroundClass;
        }

        public void setBackGroundClass(int backGroundClass) {
            this.backGroundClass = backGroundClass;
        }

        public long getTargetTime() {
            return targetTime;
        }

        public void setTargetTime(long targetTime) {
            this.targetTime = targetTime;
        }

        public List<?> getCharacter() {
            return character;
        }

        public void setCharacter(List<?> character) {
            this.character = character;
        }

        public List<Integer> getBackGroundTypes() {
            return backGroundTypes;
        }

        public void setBackGroundTypes(List<Integer> backGroundTypes) {
            this.backGroundTypes = backGroundTypes;
        }
    }
}
