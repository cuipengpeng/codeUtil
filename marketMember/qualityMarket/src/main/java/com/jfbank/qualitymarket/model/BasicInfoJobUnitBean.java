package com.jfbank.qualitymarket.model;

import java.util.List;

/**
 * 申请额度中工作单位对应的实体类
 * @author 彭爱军
 * @date 2016年8月17日
 */
public class BasicInfoJobUnitBean {
	/**
	 * 08-17 14:23:10.271: E/TAG(12078): {"consumerCompanies":[{"id":3,"name":"青岛啤酒","address":"山东省青岛市市南区香港中路五四广场青啤大厦","status":"online","provinceCode":"02","province":"山东","cityCode":"0202","city":"青岛"},{"id":4,"name":"青岛海尔","address":"青岛海尔路1号海尔工业园","status":"online","provinceCode":"02","province":"山东","cityCode":"0202","city":"青岛"}],"statusDetail":"操作成功","status":1,"function":"getconsumercom4creditline"}

	 */
	private String statusDetail;
	private String status;
	private String function;
	private List<JobUnitDataBean> consumerCompanies;
	
	public BasicInfoJobUnitBean(String statusDetail, String status, String function,
			List<JobUnitDataBean> consumerCompanies) {
		super();
		this.statusDetail = statusDetail;
		this.status = status;
		this.function = function;
		this.consumerCompanies = consumerCompanies;
	}

	public BasicInfoJobUnitBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "BasicInfoJobUnitBean [statusDetail=" + statusDetail + ", status=" + status + ", function=" + function
				+ ", consumerCompanies=" + consumerCompanies + "]";
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

	public List<JobUnitDataBean> getConsumerCompanies() {
		return consumerCompanies;
	}

	public void setConsumerCompanies(List<JobUnitDataBean> consumerCompanies) {
		this.consumerCompanies = consumerCompanies;
	}

	public class JobUnitDataBean{
		private String id;
		private String name;
		private String address;
		private String status;
		private String provinceCode;
		private String province;
		private String cityCode;
		private String city;
		private String areaCode;
		private String companyNo;
		
		public JobUnitDataBean() {
			super();
			// TODO Auto-generated constructor stub
		}

		public JobUnitDataBean(String id, String name, String address, String status, String provinceCode,
				String province, String cityCode, String city, String areaCode, String companyNo) {
			super();
			this.id = id;
			this.name = name;
			this.address = address;
			this.status = status;
			this.provinceCode = provinceCode;
			this.province = province;
			this.cityCode = cityCode;
			this.city = city;
			this.areaCode = areaCode;
			this.companyNo = companyNo;
		}

		public String getCompanyNo() {
			return companyNo;
		}

		public void setCompanyNo(String companyNo) {
			this.companyNo = companyNo;
		}

		public String getAreaCode() {
			return areaCode;
		}

		public void setAreaCode(String areaCode) {
			this.areaCode = areaCode;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getProvinceCode() {
			return provinceCode;
		}

		public void setProvinceCode(String provinceCode) {
			this.provinceCode = provinceCode;
		}

		public String getProvince() {
			return province;
		}

		public void setProvince(String province) {
			this.province = province;
		}

		public String getCityCode() {
			return cityCode;
		}

		public void setCityCode(String cityCode) {
			this.cityCode = cityCode;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		@Override
		public String toString() {
			return "JobUnitDataBean [id=" + id + ", name=" + name + ", address=" + address + ", status=" + status
					+ ", provinceCode=" + provinceCode + ", province=" + province + ", cityCode=" + cityCode + ", city="
					+ city + "]";
		}
		
	}
}
