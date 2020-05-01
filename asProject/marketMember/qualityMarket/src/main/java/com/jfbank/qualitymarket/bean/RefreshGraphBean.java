package com.jfbank.qualitymarket.bean;
/**
 * 刷新图片对应的实体类
 * @author 彭爱军
 * @date 2016年10月24日
 */
public class RefreshGraphBean {
	private String statusDetail;
	private String status;
	private String function;
	private RefreshData data;
	
	public RefreshGraphBean(String statusDetail, String status, String function, RefreshData data) {
		super();
		this.statusDetail = statusDetail;
		this.status = status;
		this.function = function;
		this.data = data;
	}

	public RefreshGraphBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "RefreshGraphBean [statusDetail=" + statusDetail + ", status=" + status + ", function=" + function
				+ ", data=" + data + "]";
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

	public RefreshData getData() {
		return data;
	}

	public void setData(RefreshData data) {
		this.data = data;
	}

	public class RefreshData{
		private String pic_code;
		private boolean hasNext;
		private String taskNo;
		private String opt;
		private String step;
		
		public RefreshData(String pic_code, boolean hasNext, String taskNo, String opt, String step) {
			super();
			this.pic_code = pic_code;
			this.hasNext = hasNext;
			this.taskNo = taskNo;
			this.opt = opt;
			this.step = step;
		}

		public RefreshData() {
			super();
			// TODO Auto-generated constructor stub
		}

		public String getPic_code() {
			return pic_code;
		}

		public void setPic_code(String pic_code) {
			this.pic_code = pic_code;
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

		public String getStep() {
			return step;
		}

		public void setStep(String step) {
			this.step = step;
		}

		@Override
		public String toString() {
			return "RefreshData [pic_code=" + pic_code + ", hasNext=" + hasNext + ", taskNo=" + taskNo + ", opt=" + opt
					+ ", step=" + step + "]";
		}
		
	}
}
