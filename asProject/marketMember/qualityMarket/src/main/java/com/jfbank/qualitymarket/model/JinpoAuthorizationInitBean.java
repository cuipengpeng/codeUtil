package com.jfbank.qualitymarket.model;

/**
 * 社保数据初始化对应的实体类
 * @author 彭爱军
 * @date 2016年8月21日
 */
public class JinpoAuthorizationInitBean {
	private String status;
	private String statusDetail;
	private String statusMsg;
	private JinpoBean data;
	
	public JinpoAuthorizationInitBean(String status, String statusDetail, String statusMsg, JinpoBean data) {
		super();
		this.status = status;
		this.statusDetail = statusDetail;
		this.statusMsg = statusMsg;
		this.data = data;
	}

	public JinpoAuthorizationInitBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "JinpoAuthorizationInitBean [status=" + status + ", statusDetail=" + statusDetail + ", statusMsg="
				+ statusMsg + ", data=" + data + "]";
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusDetail() {
		return statusDetail;
	}

	public void setStatusDetail(String statusDetail) {
		this.statusDetail = statusDetail;
	}

	public String getStatusMsg() {
		return statusMsg;
	}

	public void setStatusMsg(String statusMsg) {
		this.statusMsg = statusMsg;
	}

	public JinpoBean getData() {
		return data;
	}

	public void setData(JinpoBean data) {
		this.data = data;
	}

	public class JinpoBean{
		private String name;					/**用户姓名*/
		private String idNumber;				/**身份证号*/
		private String areaCode;				/**区域编码*/
		private boolean needPwd;				/**需要个人编号*/
		private boolean needSecurityNo;		/**需要社保号码*/
		private boolean needPersonNo;			/**需要个人编号*/
		
		public JinpoBean() {
			super();
			// TODO Auto-generated constructor stub
		}
		public JinpoBean(String name, String idNumber, String areaCode, boolean needPwd, boolean needSecurityNo,
				boolean needPersonNo) {
			super();
			this.name = name;
			this.idNumber = idNumber;
			this.areaCode = areaCode;
			this.needPwd = needPwd;
			this.needSecurityNo = needSecurityNo;
			this.needPersonNo = needPersonNo;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getIdNumber() {
			return idNumber;
		}
		public void setIdNumber(String idNumber) {
			this.idNumber = idNumber;
		}
		public String getAreaCode() {
			return areaCode;
		}
		public void setAreaCode(String areaCode) {
			this.areaCode = areaCode;
		}
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
		@Override
		public String toString() {
			return "JinpoBean [name=" + name + ", idNumber=" + idNumber + ", areaCode=" + areaCode + ", needPwd="
					+ needPwd + ", needSecurityNo=" + needSecurityNo + ", needPersonNo=" + needPersonNo + "]";
		}
		
	}
}
