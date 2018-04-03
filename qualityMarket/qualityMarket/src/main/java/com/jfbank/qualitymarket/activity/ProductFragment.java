package com.jfbank.qualitymarket.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshWebView;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.util.ConstantsUtil;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ProductFragment extends Fragment {
	public static final String TAG = ProductFragment.class.getName();
	
	@InjectView(R.id.ll_homePageFragment_search)
	LinearLayout searchLinearLayout;
	@InjectView(R.id.rl_homePageFragment_error)
	RelativeLayout errorRelativeLayout;
	/**返回*/

	@InjectView(R.id.webView_web)
	PullToRefreshWebView pullToRefreshWebView;
	WebView webView;
	Map<String, String> cacheHeaderMap = new HashMap<String, String>();
	public static SimpleDateFormat sdf = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
	public static String WEBVIEW_CACHE_STRING = "max-age="+ConstantsUtil.WEBVIEW_CACHE_TIME;
	
	private String homePageUrl = HttpRequest.QUALITY_MARKET_WEB_URL + "views/index.html";

	@OnClick({ R.id.ll_homePageFragment_search,R.id.rl_homePageFragment_error})
	public void OnViewClick(View v) {
		switch (v.getId()) {
		case R.id.ll_homePageFragment_search:
			startActivity(new Intent(getActivity(), SearchGoodsActivity.class));
			break;
		case R.id.rl_homePageFragment_error:
			setWebViewVisible();
			webView.loadUrl(homePageUrl, cacheHeaderMap);
		}
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	if(!MainActivity.isNetworkAvailable(getActivity())){
    		Toast.makeText(getActivity(), ConstantsUtil.H5_PAGE_FAIL_TO_CONNECT_SERVER,Toast.LENGTH_SHORT).show();
    	}
    }
	
	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_home_page, container, false);
		ButterKnife.inject(this, view);
		
		pullToRefreshWebView.setMode(Mode.PULL_FROM_START);
		pullToRefreshWebView.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				if(MainActivity.isNetworkAvailable(getActivity())){
					webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		    	}else {
		    		Toast.makeText(getActivity(), ConstantsUtil.H5_PAGE_FAIL_TO_CONNECT_SERVER,Toast.LENGTH_SHORT).show();
//		    		 设置 缓存模式--- 无论是否有网络，只要本地有缓存，都使用缓存。本地没有缓存时才从网络上获取。
		    		webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		    	}
				
				webView.loadUrl(homePageUrl);
				pullToRefreshWebView.onRefreshComplete();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				
			}
		});
		webView = pullToRefreshWebView.getRefreshableView();

		setWebViewVisible();
		
		webView.getSettings().setLoadWithOverviewMode(true);
//		 设置 缓存模式--- 无论是否有网络，只要本地有缓存，都使用缓存。本地没有缓存时才从网络上获取。
		webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		// 开启 DOM storage API 功能
		webView.getSettings().setDomStorageEnabled(true);
		// 开启 database storage API 功能
		webView.getSettings().setDatabaseEnabled(true);
		// 设置WebView对JavaScript的支持
		webView.getSettings().setJavaScriptEnabled(true);
		// 添加JS调用Android(Java)的方法接口
		JsInterface jsInterface = new JsInterface(getActivity(), webView);
		webView.addJavascriptInterface(jsInterface, ConstantsUtil.JS_INTERFACE_OBJECT);
		webView.setBackgroundColor(Color.TRANSPARENT);// 先设置背景色为transparent
		webView.setWebViewClient(new WebViewClient(){
			
            @Override  
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {  
            	System.out.println("----");
            	
            	pullToRefreshWebView.setVisibility(View.GONE);
//            	webView.setVisibility(View.GONE);
                errorRelativeLayout.setVisibility(View.VISIBLE);
            }
		});
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00")); // 设置时区为GMT+8:00
		long currentDate = new Date().getTime();
		currentDate = currentDate+ConstantsUtil.WEBVIEW_CACHE_TIME*1000;
	 	String cacheTime = sdf.format(currentDate);
		cacheHeaderMap.put("Expires", cacheTime);
		cacheHeaderMap.put("Pragma", WEBVIEW_CACHE_STRING);
		cacheHeaderMap.put("Cache-Control", WEBVIEW_CACHE_STRING);
		webView.loadUrl(homePageUrl, cacheHeaderMap);

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
		pullToRefreshWebView.setVisibility(View.VISIBLE);
		errorRelativeLayout.setVisibility(View.GONE);
	}

}
