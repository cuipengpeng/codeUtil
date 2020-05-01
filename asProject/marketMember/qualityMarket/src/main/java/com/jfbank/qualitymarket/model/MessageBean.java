package com.jfbank.qualitymarket.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * 功能：<br>
 * 作者：RaykerTeam
 * 时间： 2016/12/5 0005<br>.
 * 版本：1.0.0
 */

public class MessageBean implements Parcelable {

    /**
     * content : 亚杰说朋朋二货
     * uid : 243
     * id : 2
     * createTime : 2016-12-03 10:33:29
     * readStatus : 1
     * title : 未读的消息
     * h5Url : 1
     * appPage : 1
     * appParams : 1
     */

    private String content;           //     内容
    private String uid;               //     用户Id
    private String messageId;                //     id
    private String createTime;        //     创建时间
    private String readStatus;        //     是否已读(0 未读、1已读)
    private String title;             //     标题
    private String h5Url;             //     h5页面url
    private String appPage;           //     app页面标识
    private String appParams;         //     app页面参数

    @Override
    public boolean equals(Object o) {
        return TextUtils.equals(messageId, ((MessageBean) o).getMessageId());
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getId() {
        return messageId;
    }

    public void setId(String messageId) {
        this.messageId = messageId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(String readStatus) {
        this.readStatus = readStatus;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getH5Url() {
        return h5Url;
    }

    public void setH5Url(String h5Url) {
        this.h5Url = h5Url;
    }

    public String getAppPage() {
        return appPage;
    }

    public void setAppPage(String appPage) {
        this.appPage = appPage;
    }

    public String getAppParams() {
        return appParams;
    }

    public void setAppParams(String appParams) {
        this.appParams = appParams;
    }

    public MessageBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.content);
        dest.writeString(this.uid);
        dest.writeString(this.messageId);
        dest.writeString(this.createTime);
        dest.writeString(this.readStatus);
        dest.writeString(this.title);
        dest.writeString(this.h5Url);
        dest.writeString(this.appPage);
        dest.writeString(this.appParams);
    }

    protected MessageBean(Parcel in) {
        this.content = in.readString();
        this.uid = in.readString();
        this.messageId = in.readString();
        this.createTime = in.readString();
        this.readStatus = in.readString();
        this.title = in.readString();
        this.h5Url = in.readString();
        this.appPage = in.readString();
        this.appParams = in.readString();
    }

    public static final Creator<MessageBean> CREATOR = new Creator<MessageBean>() {
        public MessageBean createFromParcel(Parcel source) {
            return new MessageBean(source);
        }

        public MessageBean[] newArray(int size) {
            return new MessageBean[size];
        }
    };
}
