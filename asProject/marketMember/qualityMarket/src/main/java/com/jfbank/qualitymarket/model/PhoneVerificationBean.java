package com.jfbank.qualitymarket.model;

import com.jfbank.qualitymarket.bean.Next;
import com.jfbank.qualitymarket.bean.PhoneData;

/**
 * 申请额度中手机验证对应的实体类. 有服务密码的验证
 * @author 彭爱军
 * @date 2016年8月21日
 */
public class PhoneVerificationBean {
	/**
	 *   {
    "statusDetail": "操作成功",
    "status": "1",
    "next": {
        "method": "login",
        "param": [
            {
                "title": "图片验证码",
                "hint": "请输入图片验证码",
                "detail_hint": "",
                "type": "8",
                "key": "pic_code",
                "value": "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",
                "refresh_method": "refreshPicCode",
                "refresh_param": [
                    {
                        "key": "cellphone",
                        "value": "13714311443"
                    },
                    {
                        "key": "piccode_type",
                        "value": "1"
                    }
                ]
            }
        ],
        "hidden": [
            {
                "key": "cellphone",
                "value": "13714311443"
            },
            {
                "key": "password",
            }
        ]
    },
    "hasNextOpt": true,
    "taskNo": "161022rong3602gtLeLcdca",
    "step": "",
    "function": "checkmobile4creditline"
}
	 */
	private String status;
	private String statusDetail;
	private String function;
	private String statusMsg;		//status 返回2时{"statusDetail":"服务器出错啦~","status":"2","data":{"opt":"jump2LoginPage"},"statusMsg":"手机号与服务密码不匹配，请重新输入服务密码","function":"checkmobile4creditline"}
	
	private PhoneData data;
	
	
	public PhoneVerificationBean(String status, String statusDetail, String function, PhoneData data,String statusMsg) {
		super();
		this.status = status;
		this.statusDetail = statusDetail;
		this.function = function;
		this.data = data;
		this.statusMsg = statusMsg;
	}

	public PhoneVerificationBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getStatusMsg() {
		return statusMsg;
	}

	public void setStatusMsg(String statusMsg) {
		this.statusMsg = statusMsg;
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

	public PhoneData getData() {
		return data;
	}

	public void setData(PhoneData data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "PhoneVerificationBean [status=" + status + ", statusDetail=" + statusDetail + ", function=" + function
				+ ", data=" + data + "]";
	}
	
	
	
}
