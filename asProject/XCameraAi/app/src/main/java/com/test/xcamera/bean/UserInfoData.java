package com.test.xcamera.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 周 on 2020/4/22.
 */

public class UserInfoData implements Parcelable {

    private String uid;
    private String phone;
    private String avatarFileId;
    private String avatarFileUrl;
    private String nickname;
    private String description;
    private int reviewStatus; // 0审核中 1审核通过 2审核失败


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
        return avatarFileId;
    }

    public void setAvatarFileId(String avatarFileId) {
        this.avatarFileId = avatarFileId;
    }

    public String getAvatarFileUrl() {
        return avatarFileUrl;
    }

    public void setAvatarFileUrl(String avatarFileUrl) {
        this.avatarFileUrl = avatarFileUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getDescription() {
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

    public UserInfoData() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uid);
        dest.writeString(this.phone);
        dest.writeString(this.avatarFileId);
        dest.writeString(this.avatarFileUrl);
        dest.writeString(this.nickname);
        dest.writeString(this.description);
        dest.writeInt(this.reviewStatus);
    }

    protected UserInfoData(Parcel in) {
        this.uid = in.readString();
        this.phone = in.readString();
        this.avatarFileId = in.readString();
        this.avatarFileUrl = in.readString();
        this.nickname = in.readString();
        this.description = in.readString();
        this.reviewStatus = in.readInt();
    }

    public static final Creator<UserInfoData> CREATOR = new Creator<UserInfoData>() {
        @Override
        public UserInfoData createFromParcel(Parcel source) {
            return new UserInfoData(source);
        }

        @Override
        public UserInfoData[] newArray(int size) {
            return new UserInfoData[size];
        }
    };
}
