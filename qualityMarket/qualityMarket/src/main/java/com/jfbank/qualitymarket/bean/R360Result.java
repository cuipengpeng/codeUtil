package com.jfbank.qualitymarket.bean;

import com.alibaba.fastjson.JSON;

/** 
 * @author hanbingcheng 
 * @version 创建时间：2016年9月20日 下午8:15:35 
 */
public class R360Result {

	private long spendTime;//消耗时间
	
	private long startTime;//开始时间
	
	private int status;//状态	1：成功	-1：异常
	
	private String taskNo;//操作任务编号
	
	private String transNo;//唯一流水号
	
	private String errorCode;//错误编码
	
	private String responseData;//r360返回参数
	
	private ResponseData responseDataObj;//r360返回参数对象（json转换后）

	public long getSpendTime() {
		return spendTime;
	}

	public void setSpendTime(long spendTime) {
		this.spendTime = spendTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getTaskNo() {
		return taskNo;
	}

	public void setTaskNo(String taskNo) {
		this.taskNo = taskNo;
	}

	public String getTransNo() {
		return transNo;
	}

	public void setTransNo(String transNo) {
		this.transNo = transNo;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public ResponseData getResponseData() {
		
		if(responseDataObj == null){
			responseDataObj = JSON.parseObject(responseData, ResponseData.class);
		}
		return responseDataObj;
	}

	public void setResponseData(String responseData) {
		this.responseData = responseData;
	}
	
}
