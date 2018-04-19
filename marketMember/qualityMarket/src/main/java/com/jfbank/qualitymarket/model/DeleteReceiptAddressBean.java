package com.jfbank.qualitymarket.model;

/**
 * 删除收货地址对应的实体类
 * 
 * @author 彭爱军
 * @date 2016年8月12日
 */
public class DeleteReceiptAddressBean {
	/**
	 * 08-1215: 05: 47.196: E/TAG(25906): { "status": 1, "statusDetail": "操作成功",
	 * "function": "delreceiptaddress" }
	 */
	private String status;
	private String statusDetail;
	private String function;
	
	
	public DeleteReceiptAddressBean(String status, String statusDetail, String function) {
		super();
		this.status = status;
		this.statusDetail = statusDetail;
		this.function = function;
	}

	public DeleteReceiptAddressBean() {
		super();
		// TODO Auto-generated constructor stub
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

	
}
