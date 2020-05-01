package com.jfbank.qualitymarket.model;
/**
 * 地址信息对应的实体类。 即省、省编码、市、市编码等
 * @author 彭爱军
 * @date 2016年8月16日
 */
public class AddressInfoBean {
	private String addProvince;			/**收货地址-省*/
	private String addProvinceCode;		/**收货地址-省编码*/
	
	private String addCity;				/**收货地址-市*/
	private String addCityCode;			/**收货地址-市编码*/
	
	private String addCounty;			/**收货地址-县*/
	private String addCountyCode;		/**收货地址-县编码*/
	
	private String addTown;				/**收货地址-镇*/
	private String addTownCode;			/**收货地址-镇编码*/
	
	private String addArea;				/**收货地址-区域（城区或城区以外，几环内几环外）*/
	private String addAreaCode;			/**收货地址-区域（城区或城区以外，几环内几环外）编码*/
	
	private boolean isOK;				/**必须保证选择到三级后才能显示或者是保存基本信息中选择两级后才能显示*/	
	private boolean isCleanJobUnit;	/**是否要清空基本信息中的工作单位地址*/	
	
	private boolean needPwd;			/**是否需要社保密码*/	
	private boolean needSecurityNo;	/**是否需要安全帐号*/	
	private boolean needPersonNo;		/**是否需要个人编号*/	
	
	
	
	public String getAddProvince() {
		return addProvince;
	}
	public AddressInfoBean(String addProvince, String addProvinceCode, String addCity, String addCityCode,
			String addCounty, String addCountyCode, String addTown, String addTownCode, String addArea,
			String addAreaCode) {
		super();
		this.addProvince = addProvince;
		this.addProvinceCode = addProvinceCode;
		this.addCity = addCity;
		this.addCityCode = addCityCode;
		this.addCounty = addCounty;
		this.addCountyCode = addCountyCode;
		this.addTown = addTown;
		this.addTownCode = addTownCode;
		this.addArea = addArea;
		this.addAreaCode = addAreaCode;
	}
	
	
	public boolean isNeedPwd() {
		return needPwd;
	}
	public void setNeedPwd(boolean needPwd) {
		this.needPwd = needPwd;
	}
	public boolean isNeedSecurityNo() {
		return needSecurityNo;
	}
	public void setNeedSecurityNo(boolean needSecurityNo) {
		this.needSecurityNo = needSecurityNo;
	}
	public boolean isNeedPersonNo() {
		return needPersonNo;
	}
	public void setNeedPersonNo(boolean needPersonNo) {
		this.needPersonNo = needPersonNo;
	}
	public AddressInfoBean() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public boolean isCleanJobUnit() {
		return isCleanJobUnit;
	}
	public void setCleanJobUnit(boolean isCleanJobUnit) {
		this.isCleanJobUnit = isCleanJobUnit;
	}
	public boolean isOK() {
		return isOK;
	}
	public void setOK(boolean isOK) {
		this.isOK = isOK;
	}
	public void setAddProvince(String addProvince) {
		this.addProvince = addProvince;
	}
	public String getAddProvinceCode() {
		return addProvinceCode;
	}
	public void setAddProvinceCode(String addProvinceCode) {
		this.addProvinceCode = addProvinceCode;
	}
	public String getAddCity() {
		return addCity;
	}
	public void setAddCity(String addCity) {
		this.addCity = addCity;
	}
	public String getAddCityCode() {
		return addCityCode;
	}
	public void setAddCityCode(String addCityCode) {
		this.addCityCode = addCityCode;
	}
	public String getAddCounty() {
		return addCounty;
	}
	public void setAddCounty(String addCounty) {
		this.addCounty = addCounty;
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
	public String getAddTownCode() {
		return addTownCode;
	}
	public void setAddTownCode(String addTownCode) {
		this.addTownCode = addTownCode;
	}
	public String getAddArea() {
		return addArea;
	}
	public void setAddArea(String addArea) {
		this.addArea = addArea;
	}
	public String getAddAreaCode() {
		return addAreaCode;
	}
	public void setAddAreaCode(String addAreaCode) {
		this.addAreaCode = addAreaCode;
	}
	@Override
	public String toString() {
		return "AddressInfoBean [addProvince=" + addProvince + ", addProvinceCode=" + addProvinceCode + ", addCity="
				+ addCity + ", addCityCode=" + addCityCode + ", addCounty=" + addCounty + ", addCountyCode="
				+ addCountyCode + ", addTown=" + addTown + ", addTownCode=" + addTownCode + ", addArea=" + addArea
				+ ", addAreaCode=" + addAreaCode + "]";
	}
	
}
