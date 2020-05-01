package com.jfbank.qualitymarket.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.jfbank.qualitymarket.bean.*;
import com.jfbank.qualitymarket.bean.RefreshParam;

/**
 * @author hanbingcheng 
 * @version 创建时间：2016年9月20日 下午9:25:10 
 */
public class Param implements Parcelable {

	private String title;
	
	private String hint;//提示
	
	private String detail_hint;//特殊提示
	
	private Integer type;//类型
	
	private String key;
	
	private String value;
	
	private String refresh_method;//刷新方法
	
	private RefreshParam[] refresh_param;//刷新用参数

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

	public String getDetail_hint() {
		return detail_hint;
	}

	public void setDetail_hint(String detail_hint) {
		this.detail_hint = detail_hint;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

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

	public String getRefresh_method() {
		return refresh_method;
	}

	public void setRefresh_method(String refresh_method) {
		this.refresh_method = refresh_method;
	}

	public com.jfbank.qualitymarket.bean.RefreshParam[] getRefresh_param() {
		return refresh_param;
	}

	public void setRefresh_param(com.jfbank.qualitymarket.bean.RefreshParam[] refresh_param) {
		this.refresh_param = refresh_param;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.title);
		dest.writeString(this.hint);
		dest.writeString(this.detail_hint);
		dest.writeValue(this.type);
		dest.writeString(this.key);
		dest.writeString(this.value);
		dest.writeString(this.refresh_method);
		dest.writeParcelableArray(this.refresh_param, flags);
	}

	public Param() {
	}

	protected Param(Parcel in) {
		this.title = in.readString();
		this.hint = in.readString();
		this.detail_hint = in.readString();
		this.type = (Integer) in.readValue(Integer.class.getClassLoader());
		this.key = in.readString();
		this.value = in.readString();
		this.refresh_method = in.readString();
		this.refresh_param = (RefreshParam[]) in.readParcelableArray(RefreshParam.class.getClassLoader());
	}

	public static final Parcelable.Creator<Param> CREATOR = new Parcelable.Creator<Param>() {
		@Override
		public Param createFromParcel(Parcel source) {
			return new Param(source);
		}

		@Override
		public Param[] newArray(int size) {
			return new Param[size];
		}
	};
}
