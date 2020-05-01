package com.jfbank.qualitymarket.model;

/**
 * 提交手机验证码对应的实体类
 * 
 * @author 彭爱军
 * @date 2016年8月22日
 */
public class SubmitMobileCodeBean {
	private String nextOpt;			/** 共有3种值： Resend：重新发送 relogin：重新登录 nextStep:执行下一步 */
	private String statusDetail;
	private String status;
	private String step;
	private String function;
	
	public SubmitMobileCodeBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SubmitMobileCodeBean(String nextOpt, String statusDetail, String status, String step, String function) {
		super();
		this.nextOpt = nextOpt;
		this.statusDetail = statusDetail;
		this.status = status;
		this.step = step;
		this.function = function;
	}

	public String getNextOpt() {
		return nextOpt;
	}

	public void setNextOpt(String nextOpt) {
		this.nextOpt = nextOpt;
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

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	@Override
	public String toString() {
		return "SubmitMobileCodeBean [nextOpt=" + nextOpt + ", statusDetail=" + statusDetail + ", status=" + status
				+ ", step=" + step + ", function=" + function + "]";
	}
	
}
