package com.caishi.chaoge.bean;

public class FollowBean {


    /**
     * code : 10000
     * data : true
     * message : ok
     * attached : {"isFriend":false}
     */

    private String code;
    private boolean data;
    private String message;
    private AttachedBean attached;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isData() {
        return data;
    }

    public void setData(boolean data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AttachedBean getAttached() {
        return attached;
    }

    public void setAttached(AttachedBean attached) {
        this.attached = attached;
    }

    public static class AttachedBean {
        /**
         * isFriend : false
         */

        private boolean isFriend;

        public boolean isIsFriend() {
            return isFriend;
        }

        public void setIsFriend(boolean isFriend) {
            this.isFriend = isFriend;
        }
    }
}
