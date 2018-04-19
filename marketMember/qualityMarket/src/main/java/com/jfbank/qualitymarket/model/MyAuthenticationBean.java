package com.jfbank.qualitymarket.model;

/**
 * 我的认证页面对应的实体类
 * @author 彭爱军
 * @date 2016年10月21日
 */
public class MyAuthenticationBean {
	private String function;
	private String step;				/**基本信息的当前步数*/
	private String statusDetail;
	private String status;
	private AuthenticationData data;
	
	
	public MyAuthenticationBean() {
		super();
		// TODO Auto-generated constructor stub
	}


	public MyAuthenticationBean(String function, String step, String statusDetail, String status,
			AuthenticationData data) {
		super();
		this.function = function;
		this.step = step;
		this.statusDetail = statusDetail;
		this.status = status;
		this.data = data;
	}


	public String getFunction() {
		return function;
	}


	public void setFunction(String function) {
		this.function = function;
	}


	public String getStep() {
		return step;
	}


	public void setStep(String step) {
		this.step = step;
	}


	public String getStatusDetail() {
		return statusDetail;
	}


	public void setStatusDetail(String statusDetail) {
		this.statusDetail = statusDetail;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public AuthenticationData getData() {
		return data;
	}


	public void setData(AuthenticationData data) {
		this.data = data;
	}


	public class AuthenticationData{
		private String status;				/**状态为editing时，芝麻信用、社保不可点	editing:未提交；submited:已提交；scorePassed/auditPassed已通过；auditRefused：认证失败*/
		private String sesameStatus	;		/**芝麻信用状态*/
		private String shebaoStatus	;		/**社保授信状态*/
		private String step;				/**基本信息的当前步数*/
		
		public AuthenticationData(String status, String sesameStatus, String shebaoStatus, String step) {
			super();
			this.status = status;
			this.sesameStatus = sesameStatus;
			this.shebaoStatus = shebaoStatus;
			this.step = step;
		}

		public AuthenticationData() {
			super();
			// TODO Auto-generated constructor stub
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getSesameStatus() {
			return sesameStatus;
		}

		public void setSesameStatus(String sesameStatus) {
			this.sesameStatus = sesameStatus;
		}

		public String getShebaoStatus() {
			return shebaoStatus;
		}

		public void setShebaoStatus(String shebaoStatus) {
			this.shebaoStatus = shebaoStatus;
		}

		public String getStep() {
			return step;
		}

		public void setStep(String step) {
			this.step = step;
		}

		@Override
		public String toString() {
			return "AuthenticationData [status=" + status + ", sesameStatus=" + sesameStatus + ", shebaoStatus="
					+ shebaoStatus + ", step=" + step + "]";
		}
		
	}


	@Override
	public String toString() {
		return "MyAuthenticationBean [function=" + function + ", step=" + step + ", statusDetail=" + statusDetail
				+ ", status=" + status + ", data=" + data + "]";
	}
	
	
}
