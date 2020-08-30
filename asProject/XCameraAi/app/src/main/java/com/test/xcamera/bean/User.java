package com.test.xcamera.bean;


import android.text.TextUtils;

import com.framwork.base.BaseResponse;
import com.test.xcamera.utils.Constants;

import java.io.Serializable;

/**
 * creat by mz  2019.9.24
 */
public class User extends BaseResponse {
    /**
     * data : {"token":"","uid":"","phone":"177****1111","headImgUrl":"","nickname":"","description":""}
     */

    private UserDetail data;

    public UserDetail getData() {
        return data;
    }

    public void setData(UserDetail data) {
        this.data = data;
    }

    public static class UserDetail implements Serializable{
        /**
         * token :
         * uid :
         * phone : 177****1111
         * headImgUrl :
         * nickname :
         * description :
         */

        private String token;
        private String uid;
        private String phone;
        private String avatarFileId;
        private String nickname;
        private String description;
        private String avatarFileUrl;
        private int reviewStatus; // 0审核中 1审核通过 2审核失败


        public String getAvatarFileUrl() {
            return avatarFileUrl;
        }

        public void setAvatarFileUrl(String avatarFileUrl) {
            this.avatarFileUrl = avatarFileUrl;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getAvatarFileId() {
            /*if(TextUtils.isEmpty(avatarFileId)){
                avatarFileId="";
            }else{
                avatarFileId= Constants.getFileIdToUrl(avatarFileId);
            } 为解决头像地址修改暂时*/
            return avatarFileId;
        }
        public String getAvatarFilePath() {
            if(TextUtils.isEmpty(avatarFileId)){
                avatarFileId="";
            }else{
                avatarFileId= Constants.getFileIdToUrl(avatarFileId);
            }
            return avatarFileId;
        }

        public void setAvatarFileId(String avatarFileId) {
            this.avatarFileId = avatarFileId;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getDescription() {
            if(TextUtils.isEmpty(description)){
                description="";
            }
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getReviewStatus() {
            return reviewStatus;
        }

        public void setReviewStatus(int reviewStatus) {
            this.reviewStatus = reviewStatus;
        }
        public UserDetail copy(){
            UserDetail userDetail=new UserDetail();
            userDetail.setUid(this.uid);
            userDetail.setToken(this.token);
            userDetail.setPhone(this.phone);
            userDetail.setReviewStatus(this.reviewStatus);
            userDetail.setAvatarFileId(this.avatarFileId);
            userDetail.setDescription(this.description);
            userDetail.setAvatarFileUrl(this.avatarFileUrl);
            userDetail.setNickname(this.nickname);

            return userDetail;
        }
    }
}
