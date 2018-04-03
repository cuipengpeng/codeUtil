package com.jfbank.qualitymarket.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.DownloadFileUtil;
import com.jfbank.qualitymarket.util.LogUtil;
import com.jfbank.qualitymarket.util.StringUtil;
import com.jfbank.qualitymarket.widget.LoadingAlertDialog;
import com.jfbank.qualitymarket.widget.MyPopupDialog;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;

/**
 * webview的js接口类
 * 
 * @author 崔朋朋
 */
public class JsInterface {
	public static final String TAG = JsInterface.class.getName();

	private List<Activity> newHeadActivityList = new ArrayList<Activity>();
	public static final String PRODUCT_ID = "productId";
	public static final String CATEGORY_ID = "categoryId";
	public static final String ACTIVITY_ID = "activityId";
	public static final String CLASSIFY_ID = "classifyId";
	public static final String ORDER_JSON_STRING = "orderStr";

	// 页面加载完成js才会传递分享的参数
	public static String shareForProductDetail = "";
	private WebView webView;
	Activity activity;
	private String downloadUrl;
	private String downloadFileName;
	private static MyPopupDialog dialog = null;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				// 点击获取验证码后的倒计时任务
				String pdfPath = DownloadFileUtil.getFile(downloadUrl, downloadFileName).getAbsolutePath();
				Toast.makeText(activity, "下载文件成功：" + pdfPath, Toast.LENGTH_SHORT).show();

				Intent intent = new Intent("android.intent.action.VIEW");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				Uri uri = Uri.fromFile(DownloadFileUtil.getFile(downloadUrl, downloadFileName));
				intent.setDataAndType(uri, "application/pdf");
				activity.startActivity(intent);
				break;
			case 2:
				// 发送取消订单请求
				Toast.makeText(activity, "下载文件失败", Toast.LENGTH_SHORT).show();
				break;

			}
		};
	};

	public JsInterface() {
		super();
	}

	public JsInterface(Activity activity, WebView webView) {
		super();
		this.activity = activity;
		this.webView = webView;
	}

	/**
	 * 搜索列表
	 * 
	 * @param param
	 */
	@android.webkit.JavascriptInterface
	public void goProductList(String param) {
		JSONObject jsonObject = JSON.parseObject(param);
		// String productID = jsonObject.getString("productID");
		String achieve = jsonObject.getString(ConstantsUtil.JS_INTERFACE_FILE_NAME_ACHIEVE);
		String authority = jsonObject.getString(ConstantsUtil.JS_INTERFACE_FILE_NAME_AUTHORITY);
		LogUtil.printLog("goProductList---" + param);

		Intent intent = new Intent(activity, SearchGoodsActivity.class);
		intent.putExtra(CLASSIFY_ID, jsonObject.getString("classifyID"));
		activity.startActivity(intent);
	}

	@android.webkit.JavascriptInterface
	public void goMyAuthentication(String param) {
		activity.finish();
	}

	/**
	 * 商品分类
	 * 
	 * @param param
	 */
	@android.webkit.JavascriptInterface
	public void goClassification(String param) {
		LogUtil.printLog("goClassification---" + param);

		JSONObject jsonObject = JSON.parseObject(param);

		String classifyID = jsonObject.getString("classifyID");
		String achieve = jsonObject.getString(ConstantsUtil.JS_INTERFACE_FILE_NAME_ACHIEVE);
		String authority = jsonObject.getString(ConstantsUtil.JS_INTERFACE_FILE_NAME_AUTHORITY);

		Intent intent = new Intent(activity, WebViewActivity.class);
		intent.putExtra(CATEGORY_ID, param);
		activity.startActivity(intent);
	}

	/**
	 * 活动页面banner
	 * 
	 * @param param
	 */
	@android.webkit.JavascriptInterface
	public void goActivity(String param) {
		LogUtil.printLog("goActivity---" + param);
		JSONObject jsonObject = JSON.parseObject(param);
		String achieve = jsonObject.getString(ConstantsUtil.JS_INTERFACE_FILE_NAME_ACHIEVE);
		String authority = jsonObject.getString(ConstantsUtil.JS_INTERFACE_FILE_NAME_AUTHORITY);
		Intent intent = new Intent(activity, WebViewActivity.class);
		intent.putExtra(ACTIVITY_ID, param);
		intent.putExtra(MyAccountFragment.KEY_OF_WEB_VIEW_GO_BACK, true);
		activity.startActivity(intent);
		AppContext.extraActivityList.add(activity);
	}

	/**
	 * 快去购物吧
	 * 
	 * @param param
	 */
	@android.webkit.JavascriptInterface
	public void goIndex(String param) {
		LogUtil.printLog("goIndex---" + param);
		JSONObject jsonObject = JSON.parseObject(param);
		Intent intent = new Intent(activity, MainActivity.class);
		intent.putExtra(MainActivity.KEY_OF_BOTTOM_MENU, ProductFragment.TAG);
		activity.startActivity(intent);
		AppContext.extraActivityList.add(activity);
	}

	/**
	 * token失效
	 * 
	 * @param param
	 */
	@android.webkit.JavascriptInterface
	public void tokenExpired(String param) {
		LogUtil.printLog("tokenExpired---" + param);
		JSONObject jsonObject = JSON.parseObject(param);
		String message = jsonObject.getString("message");
		jsonObject = JSON.parseObject(message);
		String errorMsg = jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME);
		LoginActivity.tokenFailDialog(activity, errorMsg, TAG);
		AppContext.extraActivityList.add(activity);
	}

	/**
	 * 下载合同页面的pdf文件
	 * 
	 * @param param
	 */
	@android.webkit.JavascriptInterface
	public void downloadContract(String param) {
		LogUtil.printLog("downloadContract---" + param);
		JSONObject jsonObject = JSON.parseObject(param);
		downloadUrl = jsonObject.getString("url");
		downloadFileName = jsonObject.getString("text");
		DownloadFileUtil.downFile(handler, downloadUrl, downloadFileName);
		AppContext.extraActivityList.add(activity);
	}

	/**
	 * 商品详情
	 * 
	 * @param param
	 */
	@android.webkit.JavascriptInterface
	public void goProductDetail(String param) {
		JSONObject jsonObject = JSON.parseObject(param);
		String achieve = jsonObject.getString(ConstantsUtil.JS_INTERFACE_FILE_NAME_ACHIEVE);
		String authority = jsonObject.getString(ConstantsUtil.JS_INTERFACE_FILE_NAME_AUTHORITY);
		LogUtil.printLog("goProductDetail---" + param);

		Intent intent = new Intent(activity, WebViewActivity.class);
		intent.putExtra(PRODUCT_ID, param);
		activity.startActivity(intent);
		AppContext.extraActivityList.add(activity);
	}

	@android.webkit.JavascriptInterface
	public void goJhbt(String param) {// 去激活白条
		Intent intent = new Intent();
		intent.setClass(activity, MyAuthenticationActivity.class);
		intent.putExtra(MyAccountFragment.KEY_OF_CREDIT_LINES, false);// true入我的认证页面，false进入激活白条页面
		activity.startActivity(intent);
		activity.finish();
	}

	@android.webkit.JavascriptInterface
	public void goMy(String param) {// 去我的界面
		Intent intent = new Intent(activity, MainActivity.class);
		intent.putExtra(MainActivity.KEY_OF_BOTTOM_MENU, MyAccountFragment.TAG);
		activity.startActivity(intent);
		activity.finish();
	}

	/**
	 * 立即订单
	 * 
	 * @param param
	 */
	@android.webkit.JavascriptInterface
	public void goOrder(String param) {
		JSONObject jsonObject = JSON.parseObject(param);
		String achieve = jsonObject.getString(ConstantsUtil.JS_INTERFACE_FILE_NAME_ACHIEVE);
		String authority = jsonObject.getString(ConstantsUtil.JS_INTERFACE_FILE_NAME_AUTHORITY);
		LogUtil.printLog("goOrder---" + param);

		Intent intent = new Intent();
		if (AppContext.isLogin) {
			// intent.setClass(context, ConfirmOrderActivity.class);
			// intent.putExtra(ORDER_JSON_STRING, param);
			// context.startActivity(intent);

			Double productTotal = jsonObject.getDouble("monthMoney") * jsonObject.getDouble("curMonthNum")
					+ jsonObject.getDouble("downpaymentMonth");
			checkCreditLines(activity, productTotal, jsonObject.getDouble("downpaymentMonth"), param);

		} else {
			intent.setClass(activity, LoginActivity.class);
			intent.putExtra(LoginActivity.KEY_OF_COME_FROM, TAG);
			activity.startActivity(intent);
			AppContext.extraActivityList.add(activity);
		}

	}

	/**
	 * 是否授信及额度是否够用
	 * 
	 * @param activity
	 * @param productTotal
	 * @param firstPayment
	 */
	public void checkCreditLines(final Activity activity, Double productTotal, Double firstPayment,
			final String param) {
		final LoadingAlertDialog mDialog = new LoadingAlertDialog(activity);
		mDialog.show(ConstantsUtil.NETWORK_REQUEST_IN);

		RequestParams params = new RequestParams();
		params.put("uid", AppContext.user.getUid());
		params.put("token", AppContext.user.getToken());
		params.put("productTotal", MyAccountFragment.moneyDecimalFormat.format(productTotal));
		params.put("firstPayment", firstPayment);

		HttpRequest.post(HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.CHECK_CREDIT_LINES, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						if (mDialog.isShowing()) {
							mDialog.dismiss();
						}
						String jsonStr = new String(arg2);
						LogUtil.printLog("查询授信及额度是否够用: " + jsonStr);

						JSONObject jsonObject = JSON.parseObject(jsonStr);
						if (jsonObject.containsKey("creditApplyId")) {
							AppContext.user.setCreditlineApplyId4show(jsonObject.getString("creditApplyId"));
						}

						if (ConstantsUtil.RESPONSE_SUCCEED == jsonObject
								.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
							AppContext.generateNextCacheIndex();
							newHeadActivityList.add(activity);
							if (AppContext.currentCacheIndex != -1) {
								AppContext.activityMap.put(AppContext.currentCacheIndex, newHeadActivityList);
							}
							Intent intent = new Intent(activity, ConfirmOrderActivity.class);
							intent.putExtra(ORDER_JSON_STRING, param);
							activity.startActivity(intent);
						} else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == jsonObject
								.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
							LoginActivity.tokenFailDialog(activity,
									jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), TAG);
						} else {
							String errorMessage = jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME);
							popupDialog(activity, errorMessage,
									jsonObject.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME));
						}
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
						if (mDialog.isShowing()) {
							mDialog.dismiss();
						}
						System.out.println(arg0 + new String(arg2) + "----");
						arg3.printStackTrace();
						Toast.makeText(activity, ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER + "是否授信及额度是否够用",
								Toast.LENGTH_SHORT).show();
					}
				});

	}

	/**
	 * 弹出对话框
	 * 
	 * @param activity
	 *            当前上下文
	 * @param mTitle
	 *            对话框标题
	 * @param mContent
	 *            对话框内容
	 * @param mLeftBtn
	 *            左侧按钮文本
	 * @param mRightBtn
	 *            右侧按钮文本
	 * @param mOneBtn
	 *            只有一个按钮时的文本
	 * @param isTwoButton
	 *            是否有两个按钮
	 * @param dialogEvent
	 *            对话框事件
	 */
	public static void popupDialog(final Activity activity, String mContent, final int dialogEvent) {
		// 2 查询为空您尚未获取额度 如果返回码为2 跳到第一步
		// 14 信用额度不足

		// 15 您的额度正在审批中
		// 16 认证失败，请重新认证
		// 17 认证过期，请重新认证
		// 18 资料认证中您尚未获取额度
		// 23您尚未获取额度
		// 如果返回码为18、15、16、17 跳到我的认证

		// 21 您当前的可用白条额度不足
		// 22 当前额度不足，请修改首付比例
		// 1 成功

		switch (dialogEvent) {
		case 2:
		case 18:
			dialog = new MyPopupDialog(activity, null, mContent, "取消", "获取额度", null, true);
			break;
		case 21:
			dialog = new MyPopupDialog(activity, null, mContent, "取消", "去提额", null, true);
			break;
		case 14:
		case 15:
		case 16:
		case 17:
		case 22:
		case 23:
			dialog = new MyPopupDialog(activity, null, mContent, null, null, "确定", false);
			break;
		case ConstantsUtil.PERMISSION_WRITE_EXTERNAL_STORAGE_REQUEST_CODE:
		case ConstantsUtil.PERMISSION_CAMERA_REQUEST_CODE:
		case ConstantsUtil.PERMISSION_LOCATION_REQUEST_CODE:
			dialog = new MyPopupDialog(activity, null, mContent, null, null, "确定", false);
			break;
		default:
			dialog = new MyPopupDialog(activity, null, mContent, null, null, "确定", false);
			break;
		}

		dialog.setOnClickListen(new MyPopupDialog.OnClickListen() {

			@Override
			public void rightClick() {
				dialog.dismiss();
				Intent intent = new Intent();
				switch (dialogEvent) {
				case 2:
					intent.setClass(activity, ApplyAmountActivity.class);
					intent.putExtra(MyAccountFragment.KEY_OF_CREDIT_LINES, 1);
					activity.startActivity(intent);
					break;
				case 18:
				case 21:
					intent.setClass(activity, MyAuthenticationActivity.class);
					intent.putExtra(MyAccountFragment.KEY_OF_CREDIT_LINES, true);// true
																					// 进入我的认证页面，false进入激活白条页面
					activity.startActivity(intent);
					break;
				case 14:
				case 15:
				case 16:
				case 17:
				case 23:
					break;
				case ConstantsUtil.PERMISSION_WRITE_EXTERNAL_STORAGE_REQUEST_CODE:
					MainActivity.checkPermission(activity, ConstantsUtil.PERMISSION_WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
					break;
				case ConstantsUtil.PERMISSION_CAMERA_REQUEST_CODE:
					MainActivity.checkPermission(activity, ConstantsUtil.PERMISSION_CAMERA_REQUEST_CODE);
					break;
				case ConstantsUtil.PERMISSION_LOCATION_REQUEST_CODE:
					MainActivity.checkPermission(activity, ConstantsUtil.PERMISSION_LOCATION_REQUEST_CODE);
					break;
				}
				AppContext.extraActivityList.add(activity);
			}

			@Override
			public void leftClick() {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	/**
	 * 双十一红包 抢红包登录判断
	 * 
	 * @param param
	 *            登录参数
	 */
	@android.webkit.JavascriptInterface
	public void login(String param) {
		Log.d("双十一红包 抢红包登录返回参数", param);
		if (AppContext.isLogin) {// 登录

			try {
				final org.json.JSONObject redPacketJson = new org.json.JSONObject(param);
				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						webView.loadUrl(redPacketJson.optString("url", "") + "?uid=" + AppContext.user.getUid()
								+ "&token=" + AppContext.user.getToken() + "&mobile=" + AppContext.user.getMobile());

					}
				});

			} catch (JSONException e) {
				Log.e(TAG, e.getMessage() + "");
			}

		} else {// 跳转到等级界面
			Intent intent = new Intent();
			intent.setClass(activity, LoginActivity.class);
			intent.putExtra(LoginActivity.KEY_OF_COME_FROM, TAG);
			activity.startActivity(intent);
			AppContext.extraActivityList.add(activity);
		}

	}

	@android.webkit.JavascriptInterface
	public void showShare(String shareStr) {
		LogUtil.printLog("showShare---" + shareStr);
		shareForProductDetail = shareStr;
	}

	/**
	 * 
	 * @param context
	 *            上下文
	 * @param title
	 *            分享的标题
	 * @param content
	 *            分享的内容
	 * @param imagePathOrImageUrl
	 *            分享的图标
	 * @param urlAfterClick
	 *            点击分享的item后响应的url地址
	 * @param shareRecommendCode
	 *            直接传值false
	 */
	@android.webkit.JavascriptInterface
	public void share(final String shareStr) {
		org.json.JSONObject shareJson;
		try {
			shareJson = new org.json.JSONObject(shareStr);
			LogUtil.printLog("红包分享:" + shareStr);
			final String localTitle = shareJson.optString("title", "红包分享");
			final String localContent = shareJson.optString("content", "红包分享");
			final String urlAfterClick = shareJson.optString("url", "");
			ShareSDK.initSDK(activity);
			OnekeyShare oks = new OnekeyShare();
			// 关闭sso授权
			oks.disableSSOWhenAuthorize();

			// 显示推荐码
			// oks.setShareRecommendCode(shareRecommendCode);
			oks.setSilent(false);

			oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {

				@Override
				public void onShare(Platform platform, ShareParams paramsToShare) {

					// // 本地分享数据
					// // String localTitle =
					// // context.getResources().getString(R.string.app_name);
					// String localTitle = "分期购物上品质商城";
					// // String localContent = "亲，我是" + user.getNickName() +
					// //
					// ",我刚投的，看看这收益，相当不赖吧！十年大品牌，玖富真靠谱~新注册还送9999元体验金！~注册请填推荐码:";
					// String localContent =
					// "玖富品质商城，京东品质保证，正品行货，爆款直降，白条购物，想分就分！";
					// // String localUrl = "https://www.9fbank.com/";
					String localUrl = HttpRequest.QUALITY_MARKET_WEB_URL + "views/share.html";

					SharedPreferences sharedPreferences = activity
							.getSharedPreferences(ConstantsUtil.GLOBAL_CONFIG_FILE_NAME, Context.MODE_PRIVATE);
					String localImagePath = sharedPreferences.getString(ConstantsUtil.APP_ICON_LOCAL_STORE_KEY, null);

					if (StringUtil.notEmpty(urlAfterClick)) {
						localUrl = urlAfterClick;
					}

					paramsToShare.setTitle(localTitle);
					// 微信收藏和微信朋友圈该属性不显示
					paramsToShare.setText(localContent);
					// if (imagePathOrImageUrl != null &&
					// !"".equals(imagePathOrImageUrl)) {
					// paramsToShare.setImageUrl(imagePathOrImageUrl);
					// //
					// paramsToShare.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
					// } else {
					paramsToShare.setImagePath(localImagePath);
					// }
					// url仅在微信（包括好友和朋友圈）中使用
					paramsToShare.setUrl(localUrl);
					paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
					// titleUrl是标题的网络链接，site是分享此内容的网站名称，siteUrl是分享此内容的网站地址,仅在人人网和QQ空间使用
					paramsToShare.setTitleUrl(localUrl);
					paramsToShare.setSite(activity.getString(R.string.app_name));
					paramsToShare.setSiteUrl(localUrl);
					// 发短信用到的参数
					paramsToShare.setAddress("");

					// paramsToShare.setTitle("分享标题--Title");
					// paramsToShare.setTitleUrl("http://mob.com");
					// paramsToShare.setText("分享测试文--Text");
					// paramsToShare.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
				}
			});
			// 启动分享GUI
			oks.show(activity);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}