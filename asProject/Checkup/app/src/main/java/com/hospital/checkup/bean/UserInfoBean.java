package com.hospital.checkup.bean;

import java.io.Serializable;

public class UserInfoBean implements Serializable {

    /**
     * sessionId : 1
     * userAvatar : https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1599306030848&di=c36e1691645c888d3817027fa88523dc&imgtype=0&src=http%3A%2F%2Fimg.qqzhi.com%2Fuploads%2F2018-11-29%2F053827577.jpg
     * userId : 1
     * username : 张医生
     */

    private String sessionId;
    private String userAvatar;
    private String userId;
    private String username;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
