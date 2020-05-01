package com.jfbank.qualitymarket.model;
/**
 * 手机验证这一块。给服务器端传的数据对象
 * @author 彭爱军
 * @date 2016年10月22日
 */
public class PhoneLoginBean {
	private String cellphone;			//手机号
	private String password;			//服务密码
	
	public PhoneLoginBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PhoneLoginBean(String cellphone, String password) {
		super();
		this.cellphone = cellphone;
		this.password = password;
	}

	public String getCellphone() {
		return cellphone;
	}

	public void setCellphone(String cellphone) {
		this.cellphone = cellphone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
