package com.jfbank.qualitymarket.model;

import java.util.List;

/**
 * 地址对应的实体类
 * @author 彭爱军
 * @date 2016年8月16日
 */
public class AddressBean {
	private String status;
	private String statusDetail;
	private List<DataBean> data;
	
	
	public AddressBean(String status, String statusDetail, List<DataBean> data) {
		super();
		this.status = status;
		this.statusDetail = statusDetail;
		this.data = data;
	}


	public AddressBean() {
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


	public List<DataBean> getData() {
		return data;
	}


	public void setData(List<DataBean> data) {
		this.data = data;
	}


	public class DataBean{
		private String id;
		private String areaid;			/**地区编号*/
		private String areaname;		/**地区名称*/
		private String superareaid;		/**上级地址编号*/
		
		public String getId() {
			return id;
		}
		
		public DataBean() {
			super();
			// TODO Auto-generated constructor stub
		}

		public DataBean(String id, String areaid, String areaname, String superareaid) {
			super();
			this.id = id;
			this.areaid = areaid;
			this.areaname = areaname;
			this.superareaid = superareaid;
		}

		public void setId(String id) {
			this.id = id;
		}
		
		public String getAreaid() {
			return areaid;
		}
		
		public void setAreaid(String areaid) {
			this.areaid = areaid;
		}
		
		public String getAreaname() {
			return areaname;
		}
		
		public void setAreaname(String areaname) {
			this.areaname = areaname;
		}
		
		public String getSuperareaid() {
			return superareaid;
		}
		
		public void setSuperareaid(String superareaid) {
			this.superareaid = superareaid;
		}
		
		
	}
}
