package com.jfbank.qualitymarket.activity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.StringUtil;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
*	分类tab页面
* @author 崔朋朋
*/
public class CategoryFragment extends Fragment {
	public static final String TAG = CategoryFragment.class.getName();

	@InjectView(R.id.ll_categoryFragment_search)
	LinearLayout searchLinearLayout;
	@InjectView(R.id.rl_categoryFragment_error)
	RelativeLayout errorRelativeLayout;

	Map<String, String> cacheHeaderMap = new HashMap<String, String>();

	String listUrl = "views/list.html";
	private String categoryUrl = HttpRequest.QUALITY_MARKET_WEB_URL + listUrl;

	@InjectView(R.id.wv_categoryFragment_loadCategoryUrl)
	WebView webView;

	private String categoryType = "";

	@OnClick({ R.id.ll_categoryFragment_search, R.id.rl_categoryFragment_error })
	public void OnViewClick(View v) {
		switch (v.getId()) {
		case R.id.ll_categoryFragment_search:
			startActivity(new Intent(getActivity(), SearchGoodsActivity.class));
			break;
		case R.id.rl_categoryFragment_error:
			setWebViewVisible();
			webView.loadUrl(categoryUrl, cacheHeaderMap);
			break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!MainActivity.isNetworkAvailable(getActivity())) {
			Toast.makeText(getActivity(), ConstantsUtil.H5_PAGE_FAIL_TO_CONNECT_SERVER, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_category, container, false);
		ButterKnife.inject(this, view);
		
		setWebViewVisible();
		webView.getSettings().setLoadWithOverviewMode(true);
		// 设置 缓存模式--- 无论是否有网络，只要本地有缓存，都使用缓存。本地没有缓存时才从网络上获取。
		 webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		// 设置WebView对JavaScript的支持
		webView.getSettings().setJavaScriptEnabled(true);
		// 开启 DOM storage API 功能
		webView.getSettings().setDomStorageEnabled(true);
		// 开启 database storage API 功能
		webView.getSettings().setDatabaseEnabled(true);
		// 添加JS调用Android(Java)的方法接口
		JsInterface jsInterface = new JsInterface(getActivity(), webView);
		webView.addJavascriptInterface(jsInterface, ConstantsUtil.JS_INTERFACE_OBJECT);
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				webView.setVisibility(View.GONE);
				errorRelativeLayout.setVisibility(View.VISIBLE);
			}
		});

		webView.setBackgroundColor(Color.TRANSPARENT);// 先设置背景色为transparent
		ProductFragment.sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00")); // 设置时区为GMT+8:00
		long currentDate = new Date().getTime();
		currentDate = currentDate + ConstantsUtil.WEBVIEW_CACHE_TIME * 1000;
		String cacheTime = ProductFragment.sdf.format(currentDate);
		cacheHeaderMap.put("Expires", cacheTime);
		cacheHeaderMap.put("Pragma", ProductFragment.WEBVIEW_CACHE_STRING);
		cacheHeaderMap.put("Cache-Control", ProductFragment.WEBVIEW_CACHE_STRING);
		if (StringUtil.notEmpty(categoryType)) {
			categoryUrl = HttpRequest.QUALITY_MARKET_WEB_URL + "views/list.html#categoryType=" + categoryType;
		}
		webView.loadUrl(categoryUrl, cacheHeaderMap);

		return view;
	}
	
	 @Override
	  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
	      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	      if (requestCode == ConstantsUtil.PERMISSION_WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
	    	  if(grantResults.length>0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
	              // Permission Granted
	          } else {
	              // Permission Denied
	          }
	      }
	  }
	
	private void setWebViewVisible() {
		webView.setVisibility(View.VISIBLE);
		errorRelativeLayout.setVisibility(View.GONE);
	}

}
