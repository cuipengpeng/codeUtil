package com.caishi.chaoge.bean;

public class MyFansBean {

        /**
         * userId : ef2f72c818488c2e4b8e883590dce2e4
         * nickname : word天
         * sex : 0
         * avatar : http://video.chaogevideo.com/image/201812/03/702ee999f3d659fad1391daadafaed2c.jpg
         * followStatus : 1
         * remark : 好像没有什么想说的
         * targetTime : 1550458301705719
         */

        private String userId;
        private String nickname;
        private String sex;
        private String avatar;
        private int followStatus;
        private int hasFollow; //只在关注列表使用 // 1 关注 0 取消关注
        private String remark;
        private long targetTime;


        public int getHasFollow() {
            return hasFollow;
        }

        public void setHasFollow(int hasFollow) {
            this.hasFollow = hasFollow;
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

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
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

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public long getTargetTime() {
            return targetTime;
        }

        public void setTargetTime(long targetTime) {
            this.targetTime = targetTime;
        }
}
