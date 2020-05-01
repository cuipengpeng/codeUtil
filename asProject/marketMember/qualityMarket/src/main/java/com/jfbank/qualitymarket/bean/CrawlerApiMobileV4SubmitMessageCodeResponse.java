package com.jfbank.qualitymarket.bean;
/** 
 * 提交短信验证码响应
 * @author hanbingcheng 
 * @version 创建时间：2016年9月22日 上午11:28:13 
 */
public class CrawlerApiMobileV4SubmitMessageCodeResponse {

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
