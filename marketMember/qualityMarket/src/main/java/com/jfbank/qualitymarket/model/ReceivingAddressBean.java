package com.jfbank.qualitymarket.model;

import java.util.List;

/**
 * 收货地址对应的实体类
 * @author 彭爱军
 * @date 2016年8月9日
 */
public class ReceivingAddressBean {

	private String count;
	private String status;
	private String statusDetail;
	private String function;
	private String pageCount;			//新增字段。需要进行确认  TODO
	private String pageNo;
	private List<DataBean> data;
	
	
	public ReceivingAddressBean() {
		super();
		// TODO Auto-generated constructor stub
	}


	public ReceivingAddressBean(String count, String status, String statusDetail, String function,String pageCount,String pageNo, List<DataBean> data) {
		super();
		this.count = count;
		this.status = status;
		this.statusDetail = statusDetail;
		this.function = function;
		this.pageCount = pageCount;
		this.pageNo = pageNo;
		this.data = data;
	}

	public String getPageCount() {
		return pageCount;
	}


	public void setPageCount(String pageCount) {
		this.pageCount = pageCount;
	}


	public String getPageNo() {
		return pageNo;
	}


	public void setPageNo(String pageNo) {
		this.pageNo = pageNo;
	}


	public String getCount() {
		return count;
	}


	public void setCount(String count) {
		this.count = count;
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


	public class DataBean{
		private String addCounty;
		private String addCityCode;
		private String addDefault;			/**默认收货地址*/
		private String addProvinceCode;
		private String addCountyCode;
		private String addTown;
		private String addCity;
		private String addAreaCode;			/**收货地址-区域（城区或城区以外，几环内几环外）编码*/
		private String addTownCode;
		private String addProvince;
		private String addArea;
		private String userId;
		private String userName;			/**用户名*/
		private String addDetail;			/**收货地址-详细地址*/
		private String mobile;				/**登录手机号*/
		private String addressNo;			/**收货地址ID*/
		private String consignee;			/**收货人姓名*/
		private String consigneeMobile;		/**收货人手机号*/
		private String addresslabel;		/**收货人关系标签*/
		
		
		public DataBean() {
			super();
			// TODO Auto-generated constructor stub
		}
		public DataBean(String addCounty, String addCityCode, String addDefault, String addProvinceCode,
				String addCountyCode, String addTown, String addCity, String addAreaCode, String addTownCode,
				String addProvince, String addArea, String userId, String userName, String addDetail, String mobile,
				String addressNo, String consignee, String consigneeMobile,String addresslabel) {
			super();
			this.addCounty = addCounty;
			this.addCityCode = addCityCode;
			this.addDefault = addDefault;
			this.addProvinceCode = addProvinceCode;
			this.addCountyCode = addCountyCode;
			this.addTown = addTown;
			this.addCity = addCity;
			this.addAreaCode = addAreaCode;
			this.addTownCode = addTownCode;
			this.addProvince = addProvince;
			this.addArea = addArea;
			this.userId = userId;
			this.userName = userName;
			this.addDetail = addDetail;
			this.mobile = mobile;
			this.addressNo = addressNo;
			this.consignee = consignee;
			this.consigneeMobile = consigneeMobile;
			this.addresslabel = addresslabel;
		}
		
		public String getAddresslabel() {
			return addresslabel;
		}
		public void setAddresslabel(String addresslabel) {
			this.addresslabel = addresslabel;
		}
		public String getConsignee() {
			return consignee;
		}
		public void setConsignee(String consignee) {
			this.consignee = consignee;
		}
		public String getConsigneeMobile() {
			return consigneeMobile;
		}
		public void setConsigneeMobile(String consigneeMobile) {
			this.consigneeMobile = consigneeMobile;
		}
		public String getAddressNo() {
			return addressNo;
		}
		public void setAddressNo(String addressNo) {
			this.addressNo = addressNo;
		}
		public String getAddCounty() {
			return addCounty;
		}
		public void setAddCounty(String addCounty) {
			this.addCounty = addCounty;
		}
		public String getAddCityCode() {
			return addCityCode;
		}
		public void setAddCityCode(String addCityCode) {
			this.addCityCode = addCityCode;
		}
		public String getAddDefault() {
			return addDefault;
		}
		public void setAddDefault(String addDefault) {
			this.addDefault = addDefault;
		}
		public String getAddProvinceCode() {
			return addProvinceCode;
		}
		public void setAddProvinceCode(String addProvinceCode) {
			this.addProvinceCode = addProvinceCode;
		}
		public String getAddCountyCode() {
			return addCountyCode;
		}
		public void setAddCountyCode(String addCountyCode) {
			this.addCountyCode = addCountyCode;
		}
		public String getAddTown() {
			return addTown;
		}
		public void setAddTown(String addTown) {
			this.addTown = addTown;
		}
		public String getAddCity() {
			return addCity;
		}
		public void setAddCity(String addCity) {
			this.addCity = addCity;
		}
		public String getAddAreaCode() {
			return addAreaCode;
		}
		public void setAddAreaCode(String addAreaCode) {
			this.addAreaCode = addAreaCode;
		}
		public String getAddTownCode() {
			return addTownCode;
		}
		public void setAddTownCode(String addTownCode) {
			this.addTownCode = addTownCode;
		}
		public String getAddProvince() {
			return addProvince;
		}
		public void setAddProvince(String addProvince) {
			this.addProvince = addProvince;
		}
		public String getAddArea() {
			return addArea;
		}
		public void setAddArea(String addArea) {
			this.addArea = addArea;
		}
		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}
		public String getUserName() {
			return userName;
		}
		public void setUserName(String userName) {
			this.userName = userName;
		}
		public String getAddDetail() {
			return addDetail;
		}
		public void setAddDetail(String addDetail) {
			this.addDetail = addDetail;
		}
		public String getMobile() {
			return mobile;
		}
		public void setMobile(String mobile) {
			this.mobile = mobile;
		}
		
		
	}
}
