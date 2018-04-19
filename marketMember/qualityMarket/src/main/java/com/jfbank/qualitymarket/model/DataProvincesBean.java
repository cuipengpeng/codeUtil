package com.jfbank.qualitymarket.model;

public class DataProvincesBean {
	private String id;
	private String code;
	private String parentCode;
	private String name;
	private String isZxs;

	private boolean needPwd;			/**是否需要社保密码*/
	private boolean needSecurityNo;	/**是否需要安全帐号*/
	private boolean needPersonNo;		/**是否需要个人编号*/
	
	public boolean isNeedPwd() {
		return needPwd;
	}

	public void setNeedPwd(boolean needPwd) {
		this.needPwd = needPwd;
	}

	public boolean isNeedSecurityNo() {
		return needSecurityNo;
	}

	public void setNeedSecurityNo(boolean needSecurityNo) {
		this.needSecurityNo = needSecurityNo;
	}

	public boolean isNeedPersonNo() {
		return needPersonNo;
	}

	public void setNeedPersonNo(boolean needPersonNo) {
		this.needPersonNo = needPersonNo;
	}

	public DataProvincesBean(String id, String code, String parentCode, String name, String isZxs, boolean needPwd,
			boolean needSecurityNo, boolean needPersonNo) {
		super();
		this.id = id;
		this.code = code;
		this.parentCode = parentCode;
		this.name = name;
		this.isZxs = isZxs;
		this.needPwd = needPwd;
		this.needSecurityNo = needSecurityNo;
		this.needPersonNo = needPersonNo;
	}

	public DataProvincesBean(String id, String code, String parentCode, String name, String isZxs) {
		super();
		this.id = id;
		this.code = code;
		this.parentCode = parentCode;
		this.name = name;
		this.isZxs = isZxs;
	}

	public DataProvincesBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIsZxs() {
		return isZxs;
	}

	public void setIsZxs(String isZxs) {
		this.isZxs = isZxs;
	}

	@Override
	public String toString() {
		return "DataProvincesBean [id=" + id + ", code=" + code + ", parentCode=" + parentCode + ", name=" + name
				+ ", isZxs=" + isZxs + "]";
	}
	
	
}
