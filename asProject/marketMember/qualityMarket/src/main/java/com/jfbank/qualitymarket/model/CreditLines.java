package com.jfbank.qualitymarket.model;

/**
*
* @author 崔朋朋
*/
public class CreditLines extends ResponseBean {
	private String btnInfo;//申请额度的显示文本
	private int status;//响应状态码
	private String statusDetail;//响应详情
	private String enchashmentLine;//取现额度
	private String creditLine;//信用额度
	private String usableLine;//可用额度
	private boolean btnClickable;//申请额度是否可点击
	private int step;
	private int CurrentStep;//当前提交到第几步

	private String function;

	public CreditLines() {
		super();
	}

	public int getCurrentStep() {
		return CurrentStep;
	}
	
	public void setCurrentStep(int currentStep) {
		CurrentStep = currentStep;
	}
	public String getBtnInfo() {
		return btnInfo;
	}

	public void setBtnInfo(String btnInfo) {
		this.btnInfo = btnInfo;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getStatusDetail() {
		return statusDetail;
	}

	public void setStatusDetail(String statusDetail) {
		this.statusDetail = statusDetail;
	}

	public String getEnchashmentLine() {
		return enchashmentLine;
	}

	public void setEnchashmentLine(String enchashmentLine) {
		this.enchashmentLine = enchashmentLine;
	}

	public String getCreditLine() {
		return creditLine;
	}

	public void setCreditLine(String creditLine) {
		this.creditLine = creditLine;
	}

	public String getUsableLine() {
		return usableLine;
	}

	public void setUsableLine(String usableLine) {
		this.usableLine = usableLine;
	}

	
	public boolean getBtnClickable() {
		return btnClickable;
	}

	public void setBtnClickable(boolean btnClickable) {
		this.btnClickable = btnClickable;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}


}
