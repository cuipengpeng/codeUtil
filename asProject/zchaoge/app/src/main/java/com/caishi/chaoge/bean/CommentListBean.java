package com.caishi.chaoge.bean;

public class CommentListBean {

        /**
         * 评论Id
         */
        public String commentId;

        /**
         * 动态Id
         */
        public String momentId;

        /**
         * 评论者Id
         */
        public String userId;

        /**
         * 评论者nickname
         */
        public String nickname;

        /**
         * 评论者头像
         */
        public String avatar;

        /**
         * 性别（可以为空）
         */
        public Character sex = 'U';

        /**
         * 评论者内容
         */
        public String content;
        /**
         * 点赞数
         */
        public int likeCount;
        /**
         * 是否热门评论 0否 1是
         */
        public int isHot;
        /**
         * 是否点赞 0否 1 是
         */
        public int isLike;
        /**
         * 评论回复的userId
         */
        public String desUserId;

        /**
         * 评论回复的昵称
         */
        public String desNickname;

        /**
         * 评论的创建时间
         */
        public String createTime;

        /**
         * 评论的targetTime
         */
        public long targetTime;
}