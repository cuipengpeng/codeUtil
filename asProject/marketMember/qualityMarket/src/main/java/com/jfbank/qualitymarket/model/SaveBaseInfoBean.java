package com.jfbank.qualitymarket.model;
/**
 * 申请额度中保存基本信息对应的实体类
 * @author 彭爱军
 * @date 2016年8月21日
 */
public class SaveBaseInfoBean {
	/**
	 * 08-21 11:53:25.419: E/TAG(19695): {"function":"savebase4creditline","step":2,"statusDetail":"操作成功","status":1}

	 */
	private String function;
	private String statusDetail;
	private String status;
	private String isContact;
	private SaveBaseData data;
	
	public class SaveBaseData{
		private String step;
		private boolean hasNext;
		private boolean finishedUploadContracts;			//表示通讯录是否上传完毕。 true上传完毕
		private String taskNo;
		private String opt;
		
		public SaveBaseData(String step, boolean hasNext,  boolean finishedUploadContracts, String taskNo, String opt) {
			super();
			this.step = step;
			this.hasNext = hasNext;
			this.finishedUploadContracts = finishedUploadContracts;
			this.taskNo = taskNo;
			this.opt = opt;
		}

		public SaveBaseData() {
			super();
			// TODO Auto-generated constructor stub
		}

		public boolean isFinishedUploadContracts() {
			return finishedUploadContracts;
		}

		public void setFinishedUploadContracts(boolean finishedUploadContracts) {
			this.finishedUploadContracts = finishedUploadContracts;
		}

		public String getStep() {
			return step;
		}

		public void setStep(String step) {
			this.step = step;
		}

		public boolean isHasNext() {
			return hasNext;
		}

		public void setHasNext(boolean hasNext) {
			this.hasNext = hasNext;
		}

		public String getTaskNo() {
			return taskNo;
		}

		public void setTaskNo(String taskNo) {
			this.taskNo = taskNo;
		}

		public String getOpt() {
			return opt;
		}

		public void setOpt(String opt) {
			this.opt = opt;
		}

	}
	public SaveBaseInfoBean() {
		super();
		// TODO Auto-generated constructor stub
	}
	public SaveBaseInfoBean(String function, SaveBaseData data, String statusDetail, String status, String isContact) {
		super();
		this.function = function;
		this.data = data;
		this.statusDetail = statusDetail;
		this.status = status;
		this.isContact = isContact;
	}
	
	public String getIsContact() {
		return isContact;
	}
	public void setIsContact(String isContact) {
		this.isContact = isContact;
	}
	public String getFunction() {
		return function;
	}
	public void setFunction(String function) {
		this.function = function;
	}
	public SaveBaseData getStep() {
		return data;
	}
	public void setStep(SaveBaseData step) {
		this.data = step;
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
	public SaveBaseData getData() {
		return data;
	}
	public void setData(SaveBaseData data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "SaveBaseInfoBean [function=" + function + ", data=" + data + ", statusDetail=" + statusDetail
				+ ", status=" + status + "]";
	}
	
}
