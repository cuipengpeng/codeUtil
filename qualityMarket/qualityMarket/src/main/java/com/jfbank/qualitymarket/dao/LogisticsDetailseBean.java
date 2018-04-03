package com.jfbank.qualitymarket.dao;

import java.util.List;

/**
 * 物流详情对应的实体类。  用来进行测试的类
 * @author 彭爱军
 * @date 2016年8月25日
 */
public class LogisticsDetailseBean {
	private String status;
	private String statusDetail;
	private String function;
	private String express; //物流公司
	private String logisticCode; //物流单号
	private String sendRemark; //发货备注

	public String getSendRemark() {
		return sendRemark;
	}

	public void setSendRemark(String sendRemark) {
		this.sendRemark = sendRemark;
	}

	private List<DataBean> data;
	
	@Override
	public String toString() {
		return "LogisticsDetailseBean [status=" + status + ", statusDetail=" + statusDetail + ", function=" + function
				+ ", data=" + data + "]";
	}


	public String getExpress() {
		return express;
	}

	public void setExpress(String express) {
		this.express = express;
	}

	public String getLogisticCode() {
		return logisticCode;
	}

	public void setLogisticCode(String logisticCode) {
		this.logisticCode = logisticCode;
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

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public List<DataBean> getData() {
		return data;
	}

	public void setData(List<DataBean> data) {
		this.data = data;
	}

	public LogisticsDetailseBean(String status, String statusDetail, String function, List<DataBean> data) {
		super();
		this.status = status;
		this.statusDetail = statusDetail;
		this.function = function;
		this.data = data;
	}

	public LogisticsDetailseBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public class DataBean{
		private String content;
		private String msgTime;
		private String operator;
		
		public DataBean() {
			super();
			// TODO Auto-generated constructor stub
		}

		public DataBean(String content, String msgTime, String operator) {
			super();
			this.content = content;
			this.msgTime = msgTime;
			this.operator = operator;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public String getMsgTime() {
			return msgTime;
		}

		public void setMsgTime(String msgTime) {
			this.msgTime = msgTime;
		}

		public String getOperator() {
			return operator;
		}

		public void setOperator(String operator) {
			this.operator = operator;
		}

		@Override
		public String toString() {
			return "DataBean [content=" + content + ", msgTime=" + msgTime + ", operator=" + operator + "]";
		}
		
	}
	
}
