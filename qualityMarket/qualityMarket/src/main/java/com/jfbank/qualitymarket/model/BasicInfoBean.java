package com.jfbank.qualitymarket.model;

import java.util.List;

/**
 * 申请额度中。基本信息对应的实体类
 * @author 彭爱军
 * @date 2016年8月16日
 */
public class BasicInfoBean {
	/**
	 * {
    "statusDetail": "操作成功",
    "status": 1,
    "relations": [
        {
            "value": "夫妻",
            "key": "conjugal"
        },
        {
            "value": "朋友",
            "key": "friends"
        }
    ],
    "provinces": [
        {
            "id": 1,
            "code": "01",
            "parentCode": "0",
            "name": "北京",
            "isZxs": "1"
        },
        {
            "id": 2,
            "code": "02",
            "parentCode": "0",
            "name": "山东",
            "isZxs": "0"
        },
        {
            "id": 5,
            "code": "03",
            "parentCode": "0",
            "name": "安徽",
            "isZxs": "0"
        }
    ],
    "function": "preparedata4base4creditline"
}
	 */
	
	private String statusDetail;
	private String status;
	private String function;
	private BasicInfoData data;
	
	public BasicInfoBean(String statusDetail, String status, String function, BasicInfoData data) {
		super();
		this.statusDetail = statusDetail;
		this.status = status;
		this.function = function;
		this.data = data;
	}

	public BasicInfoBean() {
		super();
		// TODO Auto-generated constructor stub
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

	public BasicInfoData getData() {
		return data;
	}

	public void setData(BasicInfoData data) {
		this.data = data;
	}

	public class BasicInfoData{
		private List<DataRelations> relations;
		private List<DataProvincesBean> provinces;
		
		public List<DataRelations> getRelations() {
			return relations;
		}

		public void setRelations(List<DataRelations> relations) {
			this.relations = relations;
		}

		public List<DataProvincesBean> getProvinces() {
			return provinces;
		}

		public void setProvinces(List<DataProvincesBean> provinces) {
			this.provinces = provinces;
		}

		public BasicInfoData() {
			super();
			// TODO Auto-generated constructor stub
		}

		public BasicInfoData(List<DataRelations> relations, List<DataProvincesBean> provinces) {
			super();
			this.relations = relations;
			this.provinces = provinces;
		}

		/**
		 * 关系
		 * @author 彭爱军
		 * @date 2016年8月16日
		 */
		public class DataRelations{
			private String value;
			private String key;
			
			public DataRelations() {
				super();
				// TODO Auto-generated constructor stub
			}

			public DataRelations(String value, String key) {
				super();
				this.value = value;
				this.key = key;
			}

			public String getValue() {
				return value;
			}

			public void setValue(String value) {
				this.value = value;
			}

			public String getKey() {
				return key;
			}

			public void setKey(String key) {
				this.key = key;
			}
			
		
	}
	
	
	
		
	}
	
}
