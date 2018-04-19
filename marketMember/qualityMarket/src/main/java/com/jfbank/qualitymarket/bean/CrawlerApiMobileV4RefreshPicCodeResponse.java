package com.jfbank.qualitymarket.bean;
/** 
 * 刷新图片验证码响应
 * @author hanbingcheng 
 * @version 创建时间：2016年9月21日 下午9:30:02 
 */
public class CrawlerApiMobileV4RefreshPicCodeResponse {

	private String pic_code;

	private Next next;
	
	public String getPic_code() {
		return pic_code;
	}

	public void setPic_code(String pic_code) {
		this.pic_code = pic_code;
	}

	public Next getNext() {
		return next;
	}

	public void setNext(Next next) {
		this.next = next;
	}
	
}
