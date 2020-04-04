package com.caishi.chaoge.bean;

import com.caishi.chaoge.base.BaseBean;

public class NewMessageBean extends BaseBean {

    //粉丝
    public int followStatus = -1;//粉丝 关注状态 0 未关 1已关 ,
    public String remark;//个人简介
    public String sex;//性别


    //赞  评论（共有参数）
    public String momentId;// 动态Id ,
    public String poster;// 动态封面图地址 ,

    //评论
    public String commentId;//评论Id ,
    public String content;// 评论内容 ,

    //（共有参数）
    public String avatar;//头像
    public String nickname;//昵称
    public long targetTime;//创建时间
    public String userId;
    public String createTime;//显示时间
    public int type;//1:给动态点赞、2:给评论点赞 ,
}
