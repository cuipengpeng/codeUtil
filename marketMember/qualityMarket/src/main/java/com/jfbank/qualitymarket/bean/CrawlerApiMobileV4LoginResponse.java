package com.jfbank.qualitymarket.bean;
/** 
 * 登陆/提交图片验证码响应
 * @author hanbingcheng 
 * @version 创建时间：2016年9月20日 下午8:23:17 
 */
public class CrawlerApiMobileV4LoginResponse {

	private int status_id;
	
	private String user_id;
	
	private Next next;

	public int getStatus_id() {
		return status_id;
	}

	public void setStatus_id(int status_id) {
		this.status_id = status_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public Next getNext() {
		return next;
	}

	public void setNext(Next next) {
		this.next = next;
	}
	
}
