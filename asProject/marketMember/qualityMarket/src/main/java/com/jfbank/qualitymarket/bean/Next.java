package com.jfbank.qualitymarket.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.jfbank.qualitymarket.bean.Hidden;
import com.jfbank.qualitymarket.bean.Param;

/**
 * @author hanbingcheng 
 * @version 创建时间：2016年9月20日 下午9:18:52 
 */
public class Next implements Parcelable {

	private String method;//下一步要调用的方法
	
	private Param[] param;//下一步要用到的参数
	
	private Hidden[] hidden;//下一步需要隐含的入参

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Param[] getParam() {
		return param;
	}

	public void setParam(Param[] param) {
		this.param = param;
	}

	public Hidden[] getHidden() {
		return hidden;
	}

	public void setHidden(Hidden[] hidden) {
		this.hidden = hidden;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.method);
		dest.writeParcelableArray(this.param, flags);
		dest.writeParcelableArray(this.hidden, flags);
	}

	public Next() {
	}

	protected Next(Parcel in) {
		this.method = in.readString();
		this.param = (Param[]) in.readParcelableArray(Param.class.getClassLoader());
		this.hidden = (Hidden[]) in.readParcelableArray(Hidden.class.getClassLoader());
	}

	public static final Parcelable.Creator<Next> CREATOR = new Parcelable.Creator<Next>() {
		@Override
		public Next createFromParcel(Parcel source) {
			return new Next(source);
		}

		@Override
		public Next[] newArray(int size) {
			return new Next[size];
		}
	};
}
