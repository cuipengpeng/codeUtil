package com.jfbank.qualitymarket.model;

import java.util.List;


/**
 * 申请额度中基本信息页面中地址选择中市对应的实体类
 * @author 彭爱军
 * @date 2016年8月16日
 */
public class BaseInfoCityBean {
	/**
	 * {"statusDetail":"操作成功","status":1,"areas":[{"id":3,"code":"0201","parentCode":"02","name":"济南","isZxs":"0"},{"id":4,"code":"0202","parentCode":"02","name":"青岛","isZxs":"0"}],"function":"queryareasbyparentcode"}

	 */
	private String statusDetail;
	private String status;
	private List<DataProvincesBean> areas;
	private String function;
	
	public BaseInfoCityBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BaseInfoCityBean(String statusDetail, String status, String function, List<DataProvincesBean> areas) {
		super();
		this.statusDetail = statusDetail;
		this.status = status;
		this.function = function;
		this.areas = areas;
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

	public List<DataProvincesBean> getAreas() {
		return areas;
	}

	public void setAreas(List<DataProvincesBean> areas) {
		this.areas = areas;
	}

	@Override
	public String toString() {
		return "BaseInfoCityBean [statusDetail=" + statusDetail + ", status=" + status + ", function=" + function
				+ ", areas=" + areas + "]";
	}
	
	
	
}
