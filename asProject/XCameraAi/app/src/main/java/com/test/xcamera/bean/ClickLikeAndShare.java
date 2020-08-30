package com.test.xcamera.bean;

import com.framwork.base.BaseResponse;

import java.io.Serializable;

/**
 * Created by smz on 2019/11/13.
 */

public class ClickLikeAndShare extends BaseResponse {


    /**
     * data : {"opusId":"作品id","likeNum":2341}
     */

    private LikeAndShareDetail data;

    public LikeAndShareDetail getData() {
        return data;
    }

    public void setData(LikeAndShareDetail data) {
        this.data = data;
    }

    public static class LikeAndShareDetail implements Serializable{
        /**
         * opusId : 作品id
         * likeNum : 2341
         */

        private String opusId;
        private int likeNum;

        private int shareNum;

        public String getOpusId() {
            return opusId;
        }

        public void setOpusId(String opusId) {
            this.opusId = opusId;
        }

        public int getLikeNum() {
            return likeNum;
        }

        public void setLikeNum(int likeNum) {
            this.likeNum = likeNum;
        }

        public int getShareNum() {
            return shareNum;
        }

        public void setShareNum(int shareNum) {
            this.shareNum = shareNum;
        }
    }
}
