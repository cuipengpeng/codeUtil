package com.test.xcamera.bean;

import com.framwork.base.BaseResponse;

import java.io.Serializable;

/**
 * Created by smz on 2019/11/7.
 */

public class UploadWorksBean extends BaseResponse {

    /**
     * data : {"opusId":"作品id","videoFileId":"视频Id","coverFileId":"缩略图","description":"视频描述"}
     */

    private UploadWorkDetail data;

    public UploadWorkDetail getData() {
        return data;
    }

    public void setData(UploadWorkDetail data) {
        this.data = data;
    }

    public static class UploadWorkDetail implements Serializable{
        /**
         * opusId : 作品id
         * videoFileId : 视频Id
         * coverFileId : 缩略图
         * description : 视频描述
         */

        private String opusId;
        private String videoFileId;
        private String coverFileId;
        private String description;

        public String getOpusId() {
            return opusId;
        }

        public void setOpusId(String opusId) {
            this.opusId = opusId;
        }

        public String getVideoFileId() {
            return videoFileId;
        }

        public void setVideoFileId(String videoFileId) {
            this.videoFileId = videoFileId;
        }

        public String getCoverFileId() {
            return coverFileId;
        }

        public void setCoverFileId(String coverFileId) {
            this.coverFileId = coverFileId;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
