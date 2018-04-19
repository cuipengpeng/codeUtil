/**
 * 文件名：CouponBeqan.java
 * 全路径：com.jfbank.qualitymarket.model.CouponBeqan
 */
package com.jfbank.qualitymarket.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 功能：红包对象<br>
 * 作者：赵海<br>
 * 时间：2016年10月20日 上午10:14:56 <br>
 * 版本：1.1.0<br>
 * 
 */
public class CouponBeqan implements Parcelable {

	/**
	 * 
	 */
	String couponNo;// 优惠券编号
	String subjectNo;// 主题编号 tNo
	String uid;// Uid
	String minOrderMoney;// 适用订单金额(最小
	String startTime;// 有效时间起
	String endTime;// 有效时间止
	String productScope;// 适用商品范围
	String parValue;// 面值
	String submitTitle;// 主题名
	long id;// 主键
	String couponName; // 优惠券名称
	String showName; // 前端显示名称
	String maxOrderMoney; // 适用订单金额(最大)
	String otherRuleDesc;// 其他规则说明
	int getStatus; // 领用状态 1已领用 2 未领用
	String getString; // 领用时间
	int useStatus;// 使用状态 1已用 2未使用 3已过期
	String useString; // 使用时间
	int realStatus; // 状态 0:未发放 1:未领用 2:未使用 3:已使用 4:已过期
	String createString;// 创建时间
    String uname;
    
	/**
	 * @return the uname
	 */
	public String getUname() {
		return uname;
	}

	/**
	 * @param uname the uname to set
	 */
	public void setUname(String uname) {
		this.uname = uname;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the couponName
	 */
	public String getCouponName() {
		return couponName;
	}

	/**
	 * @param couponName the couponName to set
	 */
	public void setCouponName(String couponName) {
		this.couponName = couponName;
	}

	/**
	 * @return the showName
	 */
	public String getShowName() {
		return showName;
	}

	/**
	 * @param showName the showName to set
	 */
	public void setShowName(String showName) {
		this.showName = showName;
	}

	/**
	 * @return the maxOrderMoney
	 */
	public String getMaxOrderMoney() {
		return maxOrderMoney;
	}

	/**
	 * @param maxOrderMoney the maxOrderMoney to set
	 */
	public void setMaxOrderMoney(String maxOrderMoney) {
		this.maxOrderMoney = maxOrderMoney;
	}

	/**
	 * @return the otherRuleDesc
	 */
	public String getOtherRuleDesc() {
		return otherRuleDesc;
	}

	/**
	 * @param otherRuleDesc the otherRuleDesc to set
	 */
	public void setOtherRuleDesc(String otherRuleDesc) {
		this.otherRuleDesc = otherRuleDesc;
	}

	/**
	 * @return the getStatus
	 */
	public int getGetStatus() {
		return getStatus;
	}

	/**
	 * @param getStatus the getStatus to set
	 */
	public void setGetStatus(int getStatus) {
		this.getStatus = getStatus;
	}

	/**
	 * @return the getString
	 */
	public String getGetString() {
		return getString;
	}

	/**
	 * @param getString the getString to set
	 */
	public void setGetString(String getString) {
		this.getString = getString;
	}

	/**
	 * @return the useStatus
	 */
	public int getUseStatus() {
		return useStatus;
	}

	/**
	 * @param useStatus the useStatus to set
	 */
	public void setUseStatus(int useStatus) {
		this.useStatus = useStatus;
	}

	/**
	 * @return the useString
	 */
	public String getUseString() {
		return useString;
	}

	/**
	 * @param useString the useString to set
	 */
	public void setUseString(String useString) {
		this.useString = useString;
	}

	/**
	 * @return the realStatus
	 */
	public int getRealStatus() {
		return realStatus;
	}

	/**
	 * @param realStatus the realStatus to set
	 */
	public void setRealStatus(int realStatus) {
		this.realStatus = realStatus;
	}

	/**
	 * @return the createString
	 */
	public String getCreateString() {
		return createString;
	}

	/**
	 * @param createString the createString to set
	 */
	public void setCreateString(String createString) {
		this.createString = createString;
	}

	
	
	/**
	 * @return the couponNo
	 */
	public String getCouponNo() {
		return couponNo;
	}

	/**
	 * @param couponNo
	 *            the couponNo to set
	 */
	public void setCouponNo(String couponNo) {
		this.couponNo = couponNo;
	}

	/**
	 * @return the subjectNo
	 */
	public String getSubjectNo() {
		return subjectNo;
	}

	/**
	 * @param subjectNo
	 *            the subjectNo to set
	 */
	public void setSubjectNo(String subjectNo) {
		this.subjectNo = subjectNo;
	}

	/**
	 * @return the uid
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * @param uid
	 *            the uid to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}

	/**
	 * @return the minOrderMoney
	 */
	public String getMinOrderMoney() {
		return minOrderMoney;
	}

	/**
	 * @param minOrderMoney
	 *            the minOrderMoney to set
	 */
	public void setMinOrderMoney(String minOrderMoney) {
		this.minOrderMoney = minOrderMoney;
	}

	/**
	 * @return the startTime
	 */
	public String getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime
	 *            the startTime to set
	 */
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the endTime
	 */
	public String getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime
	 *            the endTime to set
	 */
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	/**
	 * @return the productScope
	 */
	public String getProductScope() {
		return productScope;
	}

	/**
	 * @param productScope
	 *            the productScope to set
	 */
	public void setProductScope(String productScope) {
		this.productScope = productScope;
	}

	/**
	 * @return the parValue
	 */
	public String getParValue() {
		return parValue;
	}

	/**
	 * @param parValue
	 *            the parValue to set
	 */
	public void setParValue(String parValue) {
		this.parValue = parValue;
	}

	/**
	 * @return the submitTitle
	 */
	public String getSubmitTitle() {
		return submitTitle;
	}

	/**
	 * @param submitTitle
	 *            the submitTitle to set
	 */
	public void setSubmitTitle(String submitTitle) {
		this.submitTitle = submitTitle;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.couponNo);
		dest.writeString(this.subjectNo);
		dest.writeString(this.uid);
		dest.writeString(this.minOrderMoney);
		dest.writeString(this.startTime);
		dest.writeString(this.endTime);
		dest.writeString(this.productScope);
		dest.writeString(this.parValue);
		dest.writeString(this.submitTitle);
		dest.writeLong(this.id);
		dest.writeString(this.couponName);
		dest.writeString(this.showName);
		dest.writeString(this.maxOrderMoney);
		dest.writeString(this.otherRuleDesc);
		dest.writeInt(this.getStatus);
		dest.writeString(this.getString);
		dest.writeInt(this.useStatus);
		dest.writeString(this.useString);
		dest.writeInt(this.realStatus);
		dest.writeString(this.createString);
		dest.writeString(this.uname);
	}

	public CouponBeqan() {
	}

	protected CouponBeqan(Parcel in) {
		this.couponNo = in.readString();
		this.subjectNo = in.readString();
		this.uid = in.readString();
		this.minOrderMoney = in.readString();
		this.startTime = in.readString();
		this.endTime = in.readString();
		this.productScope = in.readString();
		this.parValue = in.readString();
		this.submitTitle = in.readString();
		this.id = in.readLong();
		this.couponName = in.readString();
		this.showName = in.readString();
		this.maxOrderMoney = in.readString();
		this.otherRuleDesc = in.readString();
		this.getStatus = in.readInt();
		this.getString = in.readString();
		this.useStatus = in.readInt();
		this.useString = in.readString();
		this.realStatus = in.readInt();
		this.createString = in.readString();
		this.uname = in.readString();
	}

	public static final Parcelable.Creator<CouponBeqan> CREATOR = new Parcelable.Creator<CouponBeqan>() {
		@Override
		public CouponBeqan createFromParcel(Parcel source) {
			return new CouponBeqan(source);
		}

		@Override
		public CouponBeqan[] newArray(int size) {
			return new CouponBeqan[size];
		}
	};
}
