package com.jfbank.qualitymarket.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.jfbank.qualitymarket.bean.*;
import com.jfbank.qualitymarket.bean.Next;

public class PhoneData implements Parcelable {
	private boolean hasNext;		/**先判断opt.如果为空判断hasNest.false结束。true还有后续操作*/
	private String taskNo;			/**任务编码*/
	private String step;			/**下一步*/
	private String opt;			/**先判断opt，如果为空再判断*/
	private com.jfbank.qualitymarket.bean.Next next;
	
	public PhoneData() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PhoneData(boolean hasNext, String taskNo, String step, String opt, com.jfbank.qualitymarket.bean.Next next) {
		super();
		this.hasNext = hasNext;
		this.taskNo = taskNo;
		this.step = step;
		this.opt = opt;
		this.next = next;
	}

	public boolean isHasNext() {
		return hasNext;
	}

	public void setHasNext(boolean hasNext) {
		this.hasNext = hasNext;
	}

	public String getTaskNo() {
		return taskNo;
	}

	public void setTaskNo(String taskNo) {
		this.taskNo = taskNo;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	public String getOpt() {
		return opt;
	}

	public void setOpt(String opt) {
		this.opt = opt;
	}

	public com.jfbank.qualitymarket.bean.Next getNext() {
		return next;
	}

	public void setNext(com.jfbank.qualitymarket.bean.Next next) {
		this.next = next;
	}

	@Override
	public String toString() {
		return "PhoneData [hasNext=" + hasNext + ", taskNo=" + taskNo + ", step=" + step + ", opt=" + opt
				+ ", next=" + next + "]";
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeByte(this.hasNext ? (byte) 1 : (byte) 0);
		dest.writeString(this.taskNo);
		dest.writeString(this.step);
		dest.writeString(this.opt);
		dest.writeParcelable(this.next, flags);
	}

	protected PhoneData(Parcel in) {
		this.hasNext = in.readByte() != 0;
		this.taskNo = in.readString();
		this.step = in.readString();
		this.opt = in.readString();
		this.next = in.readParcelable(Next.class.getClassLoader());
	}

	public static final Parcelable.Creator<PhoneData> CREATOR = new Parcelable.Creator<PhoneData>() {
		@Override
		public PhoneData createFromParcel(Parcel source) {
			return new PhoneData(source);
		}

		@Override
		public PhoneData[] newArray(int size) {
			return new PhoneData[size];
		}
	};
}
