package com.jfbank.qualitymarket.model;

/**
 * 
 * @author 崔朋朋
 *
 */
public class Order extends ResponseBean {
	private String productType; //产品类型 如：手机充值、普通商品。 0-100为普通商品，101以后为虚拟商品，其中101为话费充值，102为流量充值
	private String miaosha; //是否为秒杀商品   1为秒杀商品 0为非秒杀商品
	private String mobile; //充值手机号
	private String orderId;//订单ID
	private String jdOrderId;//对应的京东的订单ID	
	private String orderStatus;//订单状态		
	private String productNo;//商品ID		
	private String productName;//商品标题名称	
	private String productImage;//商品图片		

	private String rates; //首付比例
	private Double firstPayment;//首付款
	private Double monthPay;//月供
	private int monthNum; //分期月数
	private Double productTotal;//商品总额
	private Double productPrice;//商品售价

	private String consigneeNo;//收货人编码	
	private String consignee;//收货人姓名	
	private String consigneeMobile;//收货人手机号码
	private String addProvince;     //收货地址-省	
	private String addCity;         //收货地址-市	
	private String addCounty;       //收货地址-县	
	private String addTown;         //收货地址-镇	
	private String addArea;	//收货地址-区域（城区或城区以外，几环内几环外）	
	private String addDetail; //收货地址-详细地址	

	private String currentTime; //当前服务器时间
	private String orderTime;  //下单时间
	private String remark; //备注信息
	private String billDate;//账单日
	
	private String  invoiceContent; //1不开发票   2明细
	private String invoiceTitle;//1 个人 2 单位
	private String invoiceType;//1 纸质发票2 增值税发票
	private String invoiceUnit;//发票单位名称

	private String isActivity ;//是否是活动商品  1是活动商品 0是非活动商品
	private String activityNo;//活动编号
	private String redbagId;//红包id
	private String redbagValue;//红包面额
	private String actualAmount;//实际金额
	private String skuParameters;//

	public String getSkuParameters() {
		return skuParameters;
	}

	public void setSkuParameters(String skuParameters) {
		this.skuParameters = skuParameters;
	}

	public Order() {
		super();
	}

	public String getMiaosha() {
		return miaosha;
	}

	public void setMiaosha(String miaosha) {
		this.miaosha = miaosha;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}


	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getRedbagId() {
		return redbagId;
	}

	public void setRedbagId(String redbagId) {
		this.redbagId = redbagId;
	}


	public String getRedbagValue() {
		return redbagValue;
	}


	public void setRedbagValue(String redbagValue) {
		this.redbagValue = redbagValue;
	}


	public String getActualAmount() {
		return actualAmount;
	}


	public void setActualAmount(String actualAmount) {
		this.actualAmount = actualAmount;
	}


	public String getIsActivity() {
		return isActivity;
	}

	public void setIsActivity(String isActivity) {
		this.isActivity = isActivity;
	}

	public String getActivityNo() {
		return activityNo;
	}

	public void setActivityNo(String activityNo) {
		this.activityNo = activityNo;
	}

	public String getJdOrderId() {
		return jdOrderId;
	}

	public void setJdOrderId(String jdOrderId) {
		this.jdOrderId = jdOrderId;
	}

	public String getBillDate() {
		return billDate;
	}

	public void setBillDate(String billDate) {
		this.billDate = billDate;
	}

	public String getInvoiceTitle() {
		return invoiceTitle;
	}

	public void setInvoiceTitle(String invoiceTitle) {
		this.invoiceTitle = invoiceTitle;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}

	public String getInvoiceUnit() {
		return invoiceUnit;
	}

	public void setInvoiceUnit(String invoiceUnit) {
		this.invoiceUnit = invoiceUnit;
	}

	public String getRemark() {
		return remark;
	}
	
	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getRates() {
		return rates;
	}

	public void setRates(String rates) {
		this.rates = rates;
	}
	
	public String getInvoiceContent() {
		return invoiceContent;
	}
	
	public void setInvoiceContent(String invoiceContent) {
		this.invoiceContent = invoiceContent;
	}
	
	public String getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}

	public String getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(String currentTime) {
		this.currentTime = currentTime;
	}

	public int getMonthNum() {
		return monthNum;
	}

	public void setMonthNum(int monthNum) {
		this.monthNum = monthNum;
	}

	
	public String getConsigneeNo() {
		return consigneeNo;
	}

	public void setConsigneeNo(String consigneeNo) {
		this.consigneeNo = consigneeNo;
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

	public String getAddProvince() {
		return addProvince;
	}

	public void setAddProvince(String addProvince) {
		this.addProvince = addProvince;
	}

	public String getAddCity() {
		return addCity;
	}

	public void setAddCity(String addCity) {
		this.addCity = addCity;
	}

	public String getAddCounty() {
		return addCounty;
	}

	public void setAddCounty(String addCounty) {
		this.addCounty = addCounty;
	}

	public String getAddTown() {
		return addTown;
	}

	public void setAddTown(String addTown) {
		this.addTown = addTown;
	}

	public String getAddArea() {
		return addArea;
	}

	public void setAddArea(String addArea) {
		this.addArea = addArea;
	}

	public String getAddDetail() {
		return addDetail;
	}

	public void setAddDetail(String addDetail) {
		this.addDetail = addDetail;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getProductNo() {
		return productNo;
	}

	public void setProductNo(String productNo) {
		this.productNo = productNo;
	}

	public String getProductImage() {
		return productImage;
	}

	public void setProductImage(String productImage) {
		this.productImage = productImage;
	}

	public Double getMonthPay() {
		return monthPay;
	}

	public void setMonthPay(Double monthPay) {
		this.monthPay = monthPay;
	}

	public Double getFirstPayment() {
		return firstPayment;
	}

	public void setFirstPayment(Double firstPayment) {
		this.firstPayment = firstPayment;
	}

	public Double getProductTotal() {
		return productTotal;
	}

	public void setProductTotal(Double productTotal) {
		this.productTotal = productTotal;
	}

	public Double getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(Double productPrice) {
		this.productPrice = productPrice;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

}
