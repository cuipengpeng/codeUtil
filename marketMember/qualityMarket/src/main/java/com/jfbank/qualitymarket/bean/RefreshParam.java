package com.jfbank.qualitymarket.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 刷新用参数
 * @author hanbingcheng 
 * @version 创建时间：2016年9月20日 下午9:27:20 
 */
public class RefreshParam implements Parcelable {

	private String key;
	
	private String value;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.key);
		dest.writeString(this.value);
	}

	public RefreshParam() {
	}

	protected RefreshParam(Parcel in) {
		this.key = in.readString();
		this.value = in.readString();
	}

	public static final Parcelable.Creator<RefreshParam> CREATOR = new Parcelable.Creator<RefreshParam>() {
		@Override
		public RefreshParam createFromParcel(Parcel source) {
			return new RefreshParam(source);
		}

		@Override
		public RefreshParam[] newArray(int size) {
			return new RefreshParam[size];
		}
	};
}
