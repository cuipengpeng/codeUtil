package com.jfbank.qualitymarket.bean;
/** 
 * @author hanbingcheng 
 * @version 创建时间：2016年9月20日 下午8:20:26 
 */
public class ResponseData {

	private int error;
	
	private String msg;
	
	private CrawlerApiMobileV4LoginResponse crawler_api_mobile_v4_login_response;//登陆/提交图片验证码响应
	
	private CrawlerApiMobileV4RefreshPicCodeResponse crawler_api_mobile_v4_refreshPicCode_response;//刷新图片验证码响应
	
	private CrawlerApiMobileV4SubmitMessageCodeResponse crawler_api_mobile_v4_submitMessageCode_response;//提交短信验证码响应
	
	private CrawlerApiMobileV4RefreshMessageCodeResponse crawler_api_mobile_v4_refreshMessageCode_response;//刷新短信验证码响应
	
	private CrawlerApiMobileV4GetPicCodeResponse crawler_api_mobile_v4_getPicCode_response;//请求图片验证码响应
	
	private CrawlerApiMobileV4GetMessageCodeResponse crawler_api_mobile_v4_getMessageCode_response;//请求短信验证码响应

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public CrawlerApiMobileV4LoginResponse getCrawler_api_mobile_v4_login_response() {
		return crawler_api_mobile_v4_login_response;
	}

	public void setCrawler_api_mobile_v4_login_response(
			CrawlerApiMobileV4LoginResponse crawler_api_mobile_v4_login_response) {
		this.crawler_api_mobile_v4_login_response = crawler_api_mobile_v4_login_response;
	}

	public CrawlerApiMobileV4RefreshPicCodeResponse getCrawler_api_mobile_v4_refreshPicCode_response() {
		return crawler_api_mobile_v4_refreshPicCode_response;
	}

	public void setCrawler_api_mobile_v4_refreshPicCode_response(
			CrawlerApiMobileV4RefreshPicCodeResponse crawler_api_mobile_v4_refreshPicCode_response) {
		this.crawler_api_mobile_v4_refreshPicCode_response = crawler_api_mobile_v4_refreshPicCode_response;
	}

	public CrawlerApiMobileV4SubmitMessageCodeResponse getCrawler_api_mobile_v4_submitMessageCode_response() {
		return crawler_api_mobile_v4_submitMessageCode_response;
	}

	public void setCrawler_api_mobile_v4_submitMessageCode_response(
			CrawlerApiMobileV4SubmitMessageCodeResponse crawler_api_mobile_v4_submitMessageCode_response) {
		this.crawler_api_mobile_v4_submitMessageCode_response = crawler_api_mobile_v4_submitMessageCode_response;
	}

	public CrawlerApiMobileV4RefreshMessageCodeResponse getCrawler_api_mobile_v4_refreshMessageCode_response() {
		return crawler_api_mobile_v4_refreshMessageCode_response;
	}

	public void setCrawler_api_mobile_v4_refreshMessageCode_response(
			CrawlerApiMobileV4RefreshMessageCodeResponse crawler_api_mobile_v4_refreshMessageCode_response) {
		this.crawler_api_mobile_v4_refreshMessageCode_response = crawler_api_mobile_v4_refreshMessageCode_response;
	}

	public CrawlerApiMobileV4GetPicCodeResponse getCrawler_api_mobile_v4_getPicCode_response() {
		return crawler_api_mobile_v4_getPicCode_response;
	}

	public void setCrawler_api_mobile_v4_getPicCode_response(
			CrawlerApiMobileV4GetPicCodeResponse crawler_api_mobile_v4_getPicCode_response) {
		this.crawler_api_mobile_v4_getPicCode_response = crawler_api_mobile_v4_getPicCode_response;
	}

	public CrawlerApiMobileV4GetMessageCodeResponse getCrawler_api_mobile_v4_getMessageCode_response() {
		return crawler_api_mobile_v4_getMessageCode_response;
	}

	public void setCrawler_api_mobile_v4_getMessageCode_response(
			CrawlerApiMobileV4GetMessageCodeResponse crawler_api_mobile_v4_getMessageCode_response) {
		this.crawler_api_mobile_v4_getMessageCode_response = crawler_api_mobile_v4_getMessageCode_response;
	}
	
}
