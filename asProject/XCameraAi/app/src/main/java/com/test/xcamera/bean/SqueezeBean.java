package com.test.xcamera.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by zhouxuecheng on
 * Create Time 2020/7/22
 * e-mail zhouxuecheng1991@163.com
 */

public class SqueezeBean implements Parcelable {

    private int code;
    private String message;
    private ArrayList<Squeeze> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<Squeeze> getData() {
        return data;
    }

    public void setData(ArrayList<Squeeze> data) {
        this.data = data;
    }

    public static Creator<SqueezeBean> getCREATOR() {
        return CREATOR;
    }

    public class Squeeze {
        private int opusId;
        private String iconUrl;
        private String content;
        private String link;

        public int getOpusId() {
            return opusId;
        }

        public void setOpusId(int opusId) {
            this.opusId = opusId;
        }

        public String getIconUrl() {
            return iconUrl;
        }

        public void setIconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.code);
        dest.writeString(this.message);
        dest.writeList(this.data);
    }

    public SqueezeBean() {
    }

    protected SqueezeBean(Parcel in) {
        this.code = in.readInt();
        this.message = in.readString();
        this.data = new ArrayList<Squeeze>();
        in.readList(this.data, Squeeze.class.getClassLoader());
    }

    public static final Parcelable.Creator<SqueezeBean> CREATOR = new Parcelable.Creator<SqueezeBean>() {
        @Override
        public SqueezeBean createFromParcel(Parcel source) {
            return new SqueezeBean(source);
        }

        @Override
        public SqueezeBean[] newArray(int size) {
            return new SqueezeBean[size];
        }
    };
}
