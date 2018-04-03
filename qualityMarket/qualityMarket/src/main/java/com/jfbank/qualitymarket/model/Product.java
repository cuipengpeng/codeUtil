package com.jfbank.qualitymarket.model;

import java.util.ArrayList;

/**
*
* @author 崔朋朋
*/
public class Product extends ResponseBean {
	//商品详情
	private String productNo;//产品编号
	private String categoryType;//产品类型
	private String productName;//产品名称
	private String saleUnit;
	private String weight;
	private String brandname;
	private String productArea;
	private String param;
	private String state;
	private String jdPrice;//京东价格
	private String downpaymentRatio;//首付比例
	private String jdState;//京东状态
	private String type;
	private String sku;
	private String wareqd;
	private String upc;
	private String category;
	private ArrayList<ImageList> imageList;
	private ArrayList<MonthnumList> monthnumList;
	private String mainImagePath;
	private String saleState;
	private String introduction;
	private String isCanVAT;
	//商品搜索
	private String price;//价格
	private float monthAmount;//月供金额
	private float firstPayment;//首付金额
	private String proImage;
	private String upCategoryType;
	private String monthnum;//最大分期数
	private int productStock; //产品库存 0标识抢光了

	private String limitCount;
	private String activityPrice;//活动价格
	private String isActivity;//是否是活动
	private String firstPayPrice;
	private String	skuParameters;//颜色:双屏爆款智能点验钞机（C级） 重量:100g

	public int getProductStock() {
		return productStock;
	}

	public void setProductStock(int productStock) {
		this.productStock = productStock;
	}
	public String getSkuParameters() {
		return skuParameters;
	}

	public void setSkuParameters(String skuParameters) {
		this.skuParameters = skuParameters;
	}

	public Product() {
		super();
	}

	public String getLimitCount() {
		return limitCount;
	}

	public void setLimitCount(String limitCount) {
		this.limitCount = limitCount;
	}

	public String getActivityPrice() {
		return activityPrice;
	}

	public void setActivityPrice(String activityPrice) {
		this.activityPrice = activityPrice;
	}

	public String getIsActivity() {
		return isActivity;
	}

	public void setIsActivity(String isActivity) {
		this.isActivity = isActivity;
	}

	public String getFirstPayPrice() {
		return firstPayPrice;
	}

	public void setFirstPayPrice(String firstPayPrice) {
		this.firstPayPrice = firstPayPrice;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public float getMonthAmount() {
		return monthAmount;
	}

	public void setMonthAmount(float monthAmount) {
		this.monthAmount = monthAmount;
	}

	public float getFirstPayment() {
		return firstPayment;
	}

	public void setFirstPayment(float firstPayment) {
		this.firstPayment = firstPayment;
	}

	public String getProImage() {
		return proImage;
	}

	public void setProImage(String proImage) {
		this.proImage = proImage;
	}


	public String getUpCategoryType() {
		return upCategoryType;
	}

	public void setUpCategoryType(String upCategoryType) {
		this.upCategoryType = upCategoryType;
	}

	public String getMonthnum() {
		return monthnum;
	}

	public void setMonthnum(String monthnum) {
		this.monthnum = monthnum;
	}
	
	public static class ImageList {
		private String position;
		private int ordersort;
		private String imagePath;
		private String isprimary;

		public String getPosition() {
			return position;
		}

		public void setPosition(String position) {
			this.position = position;
		}

		public int getOrdersort() {
			return ordersort;
		}

		public void setOrdersort(int ordersort) {
			this.ordersort = ordersort;
		}

		public String getImagePath() {
			return imagePath;
		}

		public void setImagePath(String imagePath) {
			this.imagePath = imagePath;
		}

		public String getIsprimary() {
			return isprimary;
		}

		public void setIsprimary(String isprimary) {
			this.isprimary = isprimary;
		}
	}

	public static class MonthnumList {
		private String rates;
		private String monthnum;

		public String getRates() {
			return rates;
		}

		public void setRates(String rates) {
			this.rates = rates;
		}

		public String getMonthnum() {
			return monthnum;
		}

		public void setMonthnum(String monthnum) {
			this.monthnum = monthnum;
		}
	}

	public String getProductNo() {
		return productNo;
	}

	public void setProductNo(String productNo) {
		this.productNo = productNo;
	}

	public String getSaleUnit() {
		return saleUnit;
	}

	public void setSaleUnit(String saleUnit) {
		this.saleUnit = saleUnit;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getBrandname() {
		return brandname;
	}

	public void setBrandname(String brandname) {
		this.brandname = brandname;
	}

	public String getProductArea() {
		return productArea;
	}

	public void setProductArea(String productArea) {
		this.productArea = productArea;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getCategoryType() {
		return categoryType;
	}

	public void setCategoryType(String categoryType) {
		this.categoryType = categoryType;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getJdPrice() {
		return jdPrice;
	}

	public void setJdPrice(String jdPrice) {
		this.jdPrice = jdPrice;
	}

	public String getDownpaymentRatio() {
		return downpaymentRatio;
	}

	public void setDownpaymentRatio(String downpaymentRatio) {
		this.downpaymentRatio = downpaymentRatio;
	}

	public String getJdState() {
		return jdState;
	}

	public void setJdState(String jdState) {
		this.jdState = jdState;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getWareqd() {
		return wareqd;
	}

	public void setWareqd(String wareqd) {
		this.wareqd = wareqd;
	}

	public String getUpc() {
		return upc;
	}

	public void setUpc(String upc) {
		this.upc = upc;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public ArrayList<ImageList> getImageList() {
		return imageList;
	}

	public void setImageList(ArrayList<ImageList> imageList) {
		this.imageList = imageList;
	}

	public ArrayList<MonthnumList> getMonthnumList() {
		return monthnumList;
	}

	public void setMonthnumList(ArrayList<MonthnumList> monthnumList) {
		this.monthnumList = monthnumList;
	}

	public String getMainImagePath() {
		return mainImagePath;
	}

	public void setMainImagePath(String mainImagePath) {
		this.mainImagePath = mainImagePath;
	}

	public String getSaleState() {
		return saleState;
	}

	public void setSaleState(String saleState) {
		this.saleState = saleState;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getIsCanVAT() {
		return isCanVAT;
	}

	public void setIsCanVAT(String isCanVAT) {
		this.isCanVAT = isCanVAT;
	}
}
