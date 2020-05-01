package com.jfbank.qualitymarket.model;
/**
 * 实名认证初始化数据对应的实体类
 * @author 彭爱军
 * @date 2016年10月29日
 */
public class IdentityInitBean {
	private String statusDetail;
	private String status;
	private String function;
	private DataBean data;
	
	public class DataBean{
		private boolean needEnterNumber;		//是否需要填写身份信息。true需要。false不需要
		private String idName;					//姓名
		private String idNumber;				//身份证号
		
		public DataBean() {
			super();
			// TODO Auto-generated constructor stub
		}

		public DataBean(boolean needEnterNumber, String idName, String idNumber) {
			super();
			this.needEnterNumber = needEnterNumber;
			this.idName = idName;
			this.idNumber = idNumber;
		}

		public boolean isNeedEnterNumber() {
			return needEnterNumber;
		}

		public void setNeedEnterNumber(boolean needEnterNumber) {
			this.needEnterNumber = needEnterNumber;
		}

		public String getIdName() {
			return idName;
		}

		public void setIdName(String idName) {
			this.idName = idName;
		}

		public String getIdNumber() {
			return idNumber;
		}

		public void setIdNumber(String idNumber) {
			this.idNumber = idNumber;
		}

		@Override
		public String toString() {
			return "DataBean [needEnterNumber=" + needEnterNumber + ", idName=" + idName + ", idNumber=" + idNumber
					+ "]";
		}
		
	}

	public IdentityInitBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public IdentityInitBean(String statusDetail, String status, String function, DataBean data) {
		super();
		this.statusDetail = statusDetail;
		this.status = status;
		this.function = function;
		this.data = data;
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

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public DataBean getData() {
		return data;
	}

	public void setData(DataBean data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "IdentityInitBean [statusDetail=" + statusDetail + ", status=" + status + ", function=" + function
				+ ", data=" + data + "]";
	}
	
	
}
