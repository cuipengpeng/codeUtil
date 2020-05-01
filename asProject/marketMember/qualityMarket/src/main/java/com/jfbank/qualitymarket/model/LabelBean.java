package com.jfbank.qualitymarket.model;

import java.util.List;

/**
 * 选择地址标签对应的实体类
 * @author 彭爱军
 * @date 2016年10月9日
 */
public class LabelBean {
	private String status;
	private String statusDetail;
	private List<LabelData> data;
	private String function;
	
	public class LabelData{
		private String id;
		private String parameterId;
		private String parameterName;
		private String parameterType;
		private String status;
		private String delFlag;
		
		public LabelData() {
			super();
			// TODO Auto-generated constructor stub
		}

		public LabelData(String id, String parameterId, String parameterName, String parameterType, String status,
				String delFlag) {
			super();
			this.id = id;
			this.parameterId = parameterId;
			this.parameterName = parameterName;
			this.parameterType = parameterType;
			this.status = status;
			this.delFlag = delFlag;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getParameterId() {
			return parameterId;
		}

		public void setParameterId(String parameterId) {
			this.parameterId = parameterId;
		}

		public String getParameterName() {
			return parameterName;
		}

		public void setParameterName(String parameterName) {
			this.parameterName = parameterName;
		}

		public String getParameterType() {
			return parameterType;
		}

		public void setParameterType(String parameterType) {
			this.parameterType = parameterType;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getDelFlag() {
			return delFlag;
		}

		public void setDelFlag(String delFlag) {
			this.delFlag = delFlag;
		}

		@Override
		public String toString() {
			return "LabelData [id=" + id + ", parameterId=" + parameterId + ", parameterName=" + parameterName
					+ ", parameterType=" + parameterType + ", status=" + status + ", delFlag=" + delFlag + "]";
		}
		
		
	}

	public LabelBean(String status, String statusDetail, List<LabelData> data, String function) {
		super();
		this.status = status;
		this.statusDetail = statusDetail;
		this.data = data;
		this.function = function;
	}

	public LabelBean() {
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

	public List<LabelData> getData() {
		return data;
	}

	public void setData(List<LabelData> data) {
		this.data = data;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	@Override
	public String toString() {
		return "LabelBean [status=" + status + ", statusDetail=" + statusDetail + ", data=" + data + ", function="
				+ function + "]";
	}
	
	
}
