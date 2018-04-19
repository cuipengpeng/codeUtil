package com.jfbank.qualitymarket.model;

/**
*
* @author 崔朋朋
*/
public class BankCard extends ResponseBean {
	private int id;//银行卡id
	private int uid;//用户id
	private String uname; //用户姓名
	private String bankName; //开户银行
	private String branchName;//开户银行支行
	private String bankCardNum;//银行卡号
	private String bankCode;//开户银行代码
	private String areaCode;//银行地区编码
	private String areaName;//银行地区名称
	private String provCode;//银行地区编码（省）
	private String provName;//银行地区名称（省）
	private String channel;//"wanka"表明该银行卡来自万卡app
	private Object useTime;
	private int delFlag;
	private int creditline ;//是否实名  0 未实名,1实名
	private String ybBindStatus;//
	private String ybRequestid;//易宝请求的id号
	private String ybIdentityid;//易宝的唯一标识



	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public BankCard() {
		super();
	}

	public int getCreditline() {
		return creditline;
	}

	public void setCreditline(int creditline) {
		this.creditline = creditline;
	}

	public String getYbBindStatus() {
		return ybBindStatus;
	}

	public void setYbBindStatus(String ybBindStatus) {
		this.ybBindStatus = ybBindStatus;
	}


	public String getYbRequestid() {
		return ybRequestid;
	}


	public void setYbRequestid(String ybRequestid) {
		this.ybRequestid = ybRequestid;
	}


	public String getYbIdentityid() {
		return ybIdentityid;
	}


	public void setYbIdentityid(String ybIdentityid) {
		this.ybIdentityid = ybIdentityid;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}



	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getBankCardNum() {
		return bankCardNum;
	}

	public void setBankCardNum(String bankCardNum) {
		this.bankCardNum = bankCardNum;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getProvCode() {
		return provCode;
	}

	public void setProvCode(String provCode) {
		this.provCode = provCode;
	}

	public String getProvName() {
		return provName;
	}

	public void setProvName(String provName) {
		this.provName = provName;
	}

	public Object getUseTime() {
		return useTime;
	}

	public void setUseTime(Object useTime) {
		this.useTime = useTime;
	}

	public int getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(int delFlag) {
		this.delFlag = delFlag;
	}


}
