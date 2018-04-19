package com.jfbank.qualitymarket.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author hanbingcheng 
 * @version 创建时间：2016年9月20日 下午9:31:13 
 */
public class Hidden implements Parcelable {

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

	public Hidden() {
	}

	protected Hidden(Parcel in) {
		this.key = in.readString();
		this.value = in.readString();
	}

	public static final Parcelable.Creator<Hidden> CREATOR = new Parcelable.Creator<Hidden>() {
		@Override
		public Hidden createFromParcel(Parcel source) {
			return new Hidden(source);
		}

		@Override
		public Hidden[] newArray(int size) {
			return new Hidden[size];
		}
	};
}
