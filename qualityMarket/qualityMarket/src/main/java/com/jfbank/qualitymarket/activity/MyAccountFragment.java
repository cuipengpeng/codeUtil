package com.jfbank.qualitymarket.activity;

import java.text.DecimalFormat;

import org.apache.http.Header;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.model.User;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.LogUtil;
import com.jfbank.qualitymarket.util.StringUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;

/**
*	我的tab页面
* @author 崔朋朋
*/

public class MyAccountFragment extends Fragment {
	public static final String TAG = MyAccountFragment.class.getName();

	@InjectView(R.id.iv_myaccountFragment_contactSustomerService)
	ImageView contactSustomerServiceImageView;
	@InjectView(R.id.iv_myaccountFragment_profile)
	ImageView profileImageView;
	
	@InjectView(R.id.rl_myAccountFragment_myOrder)
	RelativeLayout myOrderRelativeLayout;
	@InjectView(R.id.rl_myaccountFragment_receiveGoods)
	RelativeLayout receiveGoodsRelativeLayout;
	@InjectView(R.id.rl_myaccountFragment_waitForPay)
	RelativeLayout waitForPayRelativeLayout;
	@InjectView(R.id.rl_myaccountFragment_waitForSend)
	RelativeLayout waitForSendRelativeLayout;
	@InjectView(R.id.rl_myaccountFragment_afterSale)
	RelativeLayout afterSaleRelativeLayout;

	//激活白条
	@InjectView(R.id.rl_myaccountFragment_activiteBorrow)
	RelativeLayout activiteBorrowRelativeLayout;
	@InjectView(R.id.btn_myAccountFragment_activeBorrow)
	Button activeBorrowButton;
	//白条额度
	@InjectView(R.id.rl_myaccountFragment_borrowLine)
	RelativeLayout borrowLineRelativeLayout;
	@InjectView(R.id.tv_myaccountFragment_borrowLine)
	TextView borrowLineTextView;
	@InjectView(R.id.tv_myaccountFragment_viewDetail)
	TextView viewDetailTextView;
	
	//红包个数
	@InjectView(R.id.rl_myaccountFragment_redPacket)
	RelativeLayout redPacketRelativeLayout;
	@InjectView(R.id.tv_myaccountFragment_redPacket)
	TextView redPacketTextView;
	@InjectView(R.id.tv_myaccountFragment_avaliableCredit)
	TextView avaliableCreditTextView;
	//本期待还
	@InjectView(R.id.rl_myaccountFragment_waitForRepaymentInDate)
	RelativeLayout waitForRepaymentInDateRelativeLayout;
	@InjectView(R.id.tv_myaccountFragment_waitForRepayment)
	TextView waitForRepaymentTextView;
	@InjectView(R.id.tv_myaccountFragment_repaymentDay)
	TextView repaymentDayTextView;
	@InjectView(R.id.tv_myaccountFragment_repaymentDayText)
	TextView repaymentDayTextTextView;
	
	@InjectView(R.id.rl_myaccountFragment_instalment)
	RelativeLayout instalmentRelativeLayout;
	@InjectView(R.id.ll_myaccountFragment_instalment)
	LinearLayout instalmentLinearLayout;
	@InjectView(R.id.rl_myaccountFragment_bankCard)
	RelativeLayout bankCardRelativeLayout;
	@InjectView(R.id.rl_myaccountFragment_deliveryAddress)
	RelativeLayout deliveryAddressRelativeLayout;
	@InjectView(R.id.rl_myaccountFragment_coupon)
	RelativeLayout couponRelativeLayout;
	@InjectView(R.id.v_muaccountFragment_border03_coupon)
	View borderCouponView;
	@InjectView(R.id.rl_myaccountFragment_about)
	RelativeLayout aboutRelativeLayout;
	@InjectView(R.id.rl_myaccountFragment_inviteFriend)
	RelativeLayout inviteFriendRelativeLayout;


	@InjectView(R.id.tv_myaccountFragment_waitForPayAmount)
	TextView waitForPayAmountTextView;
	@InjectView(R.id.tv_myaccountFragment_waitForSendAmount)
	TextView waitForSendAmountTextView;
	@InjectView(R.id.tv_myaccountFragment_waitForReceiveGoodsAmount)
	TextView waitForReceiveGoodsAmountTextView;
	@InjectView(R.id.tv_myaccountFragment_afterSaleAmount)
	TextView afterSaleAmountTextView;

	private int step = -1;

	
	public static final String KEY_OF_CREDIT_LINES = "STEP";
	public static final String KEY_OF_HTML_URL = "htmlKey";
	public static final String KEY_OF_HTML_TITLE = "htmlTitleKey";
	public static final String KEY_OF_ZHIMA_URL = "zhimaUrlKey";
	public static final String KEY_OF_WEB_VIEW_GO_BACK = "webViewGoBackKey";
	public static DecimalFormat moneyDecimalFormat = new DecimalFormat("#0.00");

	/**
	 * 不需要登录
	 * @param v
	 */
	@OnClick({R.id.rl_myaccountFragment_commonIssue, R.id.rl_myaccountFragment_about,
		R.id.iv_myaccountFragment_contactSustomerService,  R.id.rl_myaccountFragment_inviteFriend})
	public void OnViewClick2(View v) {
		switch (v.getId()) {
		case R.id.rl_myaccountFragment_commonIssue:
			//常见问题
			String commonIssueUrl = HttpRequest.QUALITY_MARKET_WEB_URL + "views/problem.html";
			launchWebViewActivity(getActivity(), commonIssueUrl, "常见问题", false);
			break;
		case R.id.rl_myaccountFragment_about:
			//关于
			String aboutUrl = HttpRequest.QUALITY_MARKET_WEB_URL + "views/about.html";
			launchWebViewActivity(getActivity(), aboutUrl, "关于", false);
			break;
		case R.id.rl_myaccountFragment_inviteFriend:
			//邀请朋友
			oneKeyShare(getActivity(), "", "", "", "", false);
			break;
		case R.id.iv_myaccountFragment_contactSustomerService:
			//联系客服
			startActivity(new Intent(getActivity(), ContactServiceActivity.class));
			break;
		}
	}
	
	/**
	 * 需验证登录
	 * @param v
	 */
	@OnClick({R.id.rl_myaccountFragment_bankCard, R.id.rl_myaccountFragment_coupon,
			R.id.rl_myaccountFragment_deliveryAddress, R.id.rl_myaccountFragment_instalment,
			R.id.ll_myaccountFragment_instalment,
			R.id.rl_myAccountFragment_myOrder, R.id.rl_myaccountFragment_redPacket,
			R.id.rl_myaccountFragment_receiveGoods, R.id.rl_myaccountFragment_waitForPay,
			R.id.rl_myaccountFragment_waitForSend, R.id.rl_myaccountFragment_afterSale,
			R.id.btn_myAccountFragment_activeBorrow,R.id.tv_myaccountFragment_borrowLine,
			R.id.tv_myaccountFragment_viewDetail, R.id.iv_myaccountFragment_profile,
			R.id.rl_myaccountFragment_waitForRepaymentInDate})
	public void OnViewClick(View v) {
		if (!AppContext.isLogin) {
			launchLoginActivity(); 
			return;
		}
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.tv_myaccountFragment_borrowLine:
			//白条额度
			break;
		case R.id.tv_myaccountFragment_viewDetail:
			//查看详情
			intent.setClass(getActivity(), MyAuthenticationActivity.class);
			intent.putExtra(KEY_OF_CREDIT_LINES, true);//true 进入我的认证页面，false进入激活白条页面
			startActivity(intent); 			
			break;
		case R.id.btn_myAccountFragment_activeBorrow:
			//激活白条
			if(StringUtil.notEmpty(AppContext.user.getCreditlineApplyId4show())){
				//我的认证页面
				intent.setClass(getActivity(), MyAuthenticationActivity.class);
				intent.putExtra(KEY_OF_CREDIT_LINES, true);//true 进入我的认证页面，false进入激活白条页面
			}else{
				//基本资料第一步
				intent.setClass(getActivity(), ApplyAmountActivity.class);
				intent.putExtra(KEY_OF_CREDIT_LINES, 1);
			} 
			startActivity(intent);
			break;
		case R.id.rl_myaccountFragment_bankCard:
			//我的银行卡
			intent.setClass(getActivity(), BankCardListActivity.class);
			intent.putExtra(BankCardListActivity.KEY_OF_BANK_CARD_COME_FROM, TAG);
			startActivity(intent);
			break;
		case R.id.rl_myaccountFragment_redPacket:
			//红包个数
			intent.setClass(getActivity(), MyRedPacketActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_myaccountFragment_coupon:
			break;
		case R.id.ll_myaccountFragment_instalment:
			//分期账单
			viewInstallmentBill();
			break;
		case R.id.rl_myaccountFragment_instalment:
			//分期账单
			viewInstallmentBill();
			break;
		case R.id.rl_myaccountFragment_waitForRepaymentInDate:
			viewInstallmentBill();
			break;
		case R.id.rl_myaccountFragment_deliveryAddress:
			//收货地址
			intent.setClass(getActivity(), MyReceivingAddressActivity.class);
			intent.putExtra(ConfirmOrderActivity.KEY_OF_SET_CONSIGNEE_ADDRESS, true);
			startActivity(intent); // 测试
			break;
		case R.id.rl_myaccountFragment_receiveGoods:
			//待收货订单
			intent.setClass(getActivity(), MyOrderActivity.class);
			intent.putExtra(MyOrderActivity.KEY_OF_QUERY_ORDER, MyOrderActivity.ORDER_STATUS_WAIT_FOR_TAKE_OVER_GOODS);
			startActivity(intent);
			break;
		case R.id.rl_myaccountFragment_afterSale:
			//申请售后
			OrderDetailActivity.applyAfterSale(getActivity());
			// intent.setClass(getActivity(), MyOrderActivity.class);
			// intent.putExtra(MyOrderActivity.KEY_OF_QUERY_ORDER,
			// MyOrderActivity.ORDER_STATUS_FINISHED);
			// startActivity(intent);
			break;
		case R.id.rl_myaccountFragment_waitForPay:
			//待支付订单
			intent.setClass(getActivity(), MyOrderActivity.class);
			intent.putExtra(MyOrderActivity.KEY_OF_QUERY_ORDER, MyOrderActivity.ORDER_STATUS_WAIT_FOR_PAY);
			startActivity(intent);
			break;
		case R.id.rl_myaccountFragment_waitForSend:
			//待发货订单
			intent.setClass(getActivity(), MyOrderActivity.class);
			intent.putExtra(MyOrderActivity.KEY_OF_QUERY_ORDER, MyOrderActivity.ORDER_STATUS_WAIT_FOR_SEND_GOODS);
			startActivity(intent);
			break;
		case R.id.rl_myAccountFragment_myOrder:
			//我的订单
			startActivity(new Intent(getActivity(), MyOrderActivity.class));
			break;
		case R.id.iv_myaccountFragment_profile:
			//账户信息
			startActivity(new Intent(getActivity(), AccountInfoActivity.class));
			break;
		}

		AppContext.extraActivityList.add(getActivity());
	}

	/**
	 * 查看分期账单
	 */
	private void viewInstallmentBill() {
		String instalmentUrl = HttpRequest.QUALITY_MARKET_WEB_URL + "views/installmentBill.html?uid="
				+ AppContext.user.getUid() + "&token=" + AppContext.user.getToken();
		launchWebViewActivity(getActivity(), instalmentUrl, "分期账单", true);
	}
	
	/**
	 * 启动webview页面
	 * @param context 当前activity
	 * @param url  要加载的url
	 * @param title 
	 * @param webViewGoBack webview是否可以返回
	 */
	public static void launchWebViewActivity(Context context, String url, String title, boolean webViewGoBack) {
		Intent intent = new Intent(context, WebViewActivity.class);
		intent.putExtra(KEY_OF_HTML_URL, url);
		intent.putExtra(KEY_OF_HTML_TITLE, title);
		intent.putExtra(KEY_OF_WEB_VIEW_GO_BACK, webViewGoBack);
		context.startActivity(intent);
		AppContext.extraActivityList.add(((Activity) context));
	}

	/**
	 * 启动登录页面
	 */
	public void launchLoginActivity() {
		if (!AppContext.isLogin) {
			Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
			loginIntent.putExtra(LoginActivity.KEY_OF_COME_FROM, TAG);
			startActivity(loginIntent);
			getActivity().finish();
			return;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!MainActivity.isNetworkAvailable(getActivity())) {
			Toast.makeText(getActivity(), ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_myaccount, container, false);
		ButterKnife.inject(this, view);

		viewDetailTextView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
		//一期暂时没有优惠券功能，二期添加
		couponRelativeLayout.setVisibility(View.GONE);
		borderCouponView.setVisibility(View.GONE);

		redPacketTextView.setText(getWhiteAvaliableLineTextStyle(redPacketTextView.getText().toString()), TextView.BufferType.SPANNABLE);
		avaliableCreditTextView.setText(getWhiteAvaliableLineTextStyle(avaliableCreditTextView.getText().toString()), TextView.BufferType.SPANNABLE);
		waitForRepaymentTextView.setText(getRedRepaymentTextStyle(waitForRepaymentTextView.getText().toString()), TextView.BufferType.SPANNABLE);
		
		String money = "¥ 5000";
		borrowLineTextView.setText(getBorrowLineTextStyle(borrowLineTextView.getText().toString()), TextView.BufferType.SPANNABLE);
		
		repaymentDayTextView.setText(getRepaymentDayTextStyle(repaymentDayTextView.getText().toString()), TextView.BufferType.SPANNABLE);
		showActiviteBorrow();

		
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		requestData();
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
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {// 重新显示到最前端中
			requestData();
		}
	}

	/**
	 * 请求首页数据
	 */
	private void requestData() {
		if (AppContext.isLogin) {
			getRedPacketCount();
			getCreditLines(TAG);
			getOrderCount();
			
			String imageUrl = "http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg";
			// Picasso.with(getActivity()).load("http://f1.sharesdk.cn/imgs").placeholder(R.drawable.ic_launcher).resize(100,
			// 100).into(profileImageView);
			profileImageView.setImageResource(R.drawable.default_head_logined);
		} else {
			profileImageView.setImageResource(R.drawable.default_head_nologin);
		}
	}

	/**
	 * 显示激活白条
	 */
	private void showActiviteBorrow() {
		activiteBorrowRelativeLayout.setVisibility(View.VISIBLE);
		borrowLineRelativeLayout.setVisibility(View.GONE);
	}

	/**
	 * 显示我的额度
	 */
	private void showMyLine() {
		activiteBorrowRelativeLayout.setVisibility(View.GONE);
		borrowLineRelativeLayout.setVisibility(View.VISIBLE);
	}

	/**
	 * 查询信用额度
	 */
	public void getCreditLines(final String comeFrom) {
		RequestParams params = new RequestParams();
		params.put("uid", AppContext.user.getUid());
		params.put("token", AppContext.user.getToken());

		HttpRequest.post(HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.GET_CREDIT_LINES, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						String jsonStr = new String(arg2);
						LogUtil.printLog("查询信用额度：" + jsonStr);
                        Log.e("查询信用额度：" , jsonStr);
						JSONObject jsonObject = JSON.parseObject(jsonStr);
						
						if (ConstantsUtil.RESPONSE_SUCCEED == jsonObject
								.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
							User user = JSON.parseObject(jsonObject.getJSONObject(ConstantsUtil.RESPONSE_DATA_JSON_ARRAY_FIELD_NAME).toString(), User.class);
							AppContext.user.setCreditlineApplyId4show(user.getCreditlineApplyId4show());
							AppContext.user.setCreditLine(user.getCreditLine());
							AppContext.user.setUsableLine(user.getUsableLine());
							AppContext.user.setShowDetail(user.isShowDetail());

							if(MyAccountFragment.TAG.equals(comeFrom)){
								if(user.isShowDetail()){
									SpannableString styledText = getWhiteAvaliableLineTextStyle(moneyDecimalFormat.format(Double.valueOf(user.getUsableLine()))+ "元");
									avaliableCreditTextView.setText(styledText, TextView.BufferType.SPANNABLE);
									SpannableString styledText2 = getBorrowLineTextStyle("¥ "+moneyDecimalFormat.format(Double.valueOf(user.getCreditLine())));
									borrowLineTextView.setText(styledText2, TextView.BufferType.SPANNABLE);
									showMyLine();
								}else {
									showActiviteBorrow();
								}
							}

							
						} else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == jsonObject
								.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
							// LoginActivity.tokenFailDialog(getActivity(),
							// jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME),
							// TAG);
						} else {
							if(MyAccountFragment.TAG.equals(comeFrom)){
							Toast.makeText(getActivity(), "查询信用额度失败", Toast.LENGTH_SHORT).show();
							}
						}
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
						// Toast.makeText(getActivity(),
						// ConstantsUtil.FAIL_TO_CONNECT_SERVER+"查询信用额度失败",
						// Toast.LENGTH_SHORT).show();
					}
				});
	}
	/**
	 * 查询红包个数，账单日，本期待还
	 */
	private void getRedPacketCount() {
		RequestParams params = new RequestParams();
		params.put("uid", AppContext.user.getUid());
		params.put("mobile", AppContext.user.getMobile());
		params.put("token", AppContext.user.getToken());
		
		HttpRequest.post(HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.GET_RED_PACKET, params,
				new AsyncHttpResponseHandler() {
			
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				String jsonStr = new String(arg2);
				LogUtil.printLog("查询红包个数，账单日，本期待还：" + jsonStr);
				
				JSONObject jsonObject = JSON.parseObject(jsonStr);
				
				//0:未生成账单        
				//1:未还款
				//2：逾期
				if (ConstantsUtil.RESPONSE_SUCCEED == jsonObject
						.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
					
					User user = JSON.parseObject(jsonObject.toString(), User.class);
					redPacketTextView.setText(getWhiteAvaliableLineTextStyle(user.getCouponCount()+"个"), TextView.BufferType.SPANNABLE);
//					avaliableCreditTextView.setText(getWhiteAvaliableLineTextStyle(), TextView.BufferType.SPANNABLE);
					if(StringUtil.notEmpty(user.getBillCount())){
						waitForRepaymentTextView.setText(getRedRepaymentTextStyle(user.getBillCount()+"元"), TextView.BufferType.SPANNABLE);
					}
					if(StringUtil.notEmpty(user.getSettledDay())){
						//0:未生成账单        
						//1:未还款
						//2：逾期
						if("1".equals(user.getStatus1())){
							repaymentDayTextView.setText(getRepaymentDayTextStyle("剩余 "+user.getSettledDay()+"天"), TextView.BufferType.SPANNABLE);
						}else if("2".equals(user.getStatus1())){
							repaymentDayTextView.setText(getRepaymentDayTextStyle("已逾期"+user.getSettledDay()+"天"), TextView.BufferType.SPANNABLE);
						}
					}
					
					AppContext.user.setCouponCount(user.getCouponCount());
					AppContext.user.setBillCount(user.getBillCount());
					AppContext.user.setSettledDay(user.getSettledDay());
					AppContext.user.setStatus1(user.getStatus1());
				} else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == jsonObject
						.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
					// LoginActivity.tokenFailDialog(getActivity(),
					// jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME),
					// TAG);
				} else {
					Toast.makeText(getActivity(), "查询红包个数失败", Toast.LENGTH_SHORT).show();
				}
			}
			
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				// Toast.makeText(getActivity(),
				// ConstantsUtil.FAIL_TO_CONNECT_SERVER+"查询信用额度失败",
				// Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * 获取各类订单数
	 */
	private void getOrderCount() {
		RequestParams params = new RequestParams();
		params.put("uid", AppContext.user.getUid());
		params.put("token", AppContext.user.getToken());
		params.put("version", AppContext.getAppVersionName(getActivity()));
		params.put("channel", ConstantsUtil.CHANNEL_CODE);

		HttpRequest.post(HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.GET_ORDER_COUNT, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						String jsonStr = new String(arg2);
						LogUtil.printLog("获取各类订单数：" + jsonStr);

						JSONObject jsonObject = JSON.parseObject(jsonStr);

						if (ConstantsUtil.RESPONSE_SUCCEED == jsonObject
								.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
							waitForPayAmountTextView.setText(jsonObject.getString("dzf") + "笔");
							waitForSendAmountTextView.setText(jsonObject.getString("dfh") + "笔");
							waitForReceiveGoodsAmountTextView.setText(jsonObject.getString("dsh") + "笔");
							// afterSaleAmountTextView.setText(jsonObject.getString("ywc")+"笔");
							afterSaleAmountTextView.setText("0笔");
						} else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == jsonObject
								.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
							LoginActivity.tokenFailDialog(getActivity(),
									jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), TAG);
						} else {
							Toast.makeText(getActivity(), "获取各类订单数失败", Toast.LENGTH_SHORT).show();
						}
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
						// Toast.makeText(getActivity(),
						// ConstantsUtil.FAIL_TO_CONNECT_SERVER+"获取各类订单数失败",
						// Toast.LENGTH_SHORT).show();
					}
				});
	}

	/**
	 * 本期待还的text style
	 * @param day
	 * @return
	 */
	private SpannableString getRedRepaymentTextStyle(String money) {
		SpannableString styledText = new SpannableString(money);
		styledText.setSpan(new TextAppearanceSpan(getActivity(), R.style.money_style0), 0, (money.length() - 1),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		styledText.setSpan(new TextAppearanceSpan(getActivity(), R.style.money_yuan_style1), (money.length() - 1),
				money.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return styledText;
	}
	
	/**
	 * 红包的text style
	 * @param day
	 * @return
	 */
	private SpannableString getWhiteAvaliableLineTextStyle(String money) {
		SpannableString styledText = new SpannableString(money);
		styledText.setSpan(new TextAppearanceSpan(getActivity(), R.style.money_white_style0), 0, (money.length() - 1),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		styledText.setSpan(new TextAppearanceSpan(getActivity(), R.style.money_white_yuan_style1), (money.length() - 1),
				money.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return styledText;
	}


	/**
	 * 白条额度的text style
	 * @param day
	 * @return
	 */
	private SpannableString getBorrowLineTextStyle(String money) {
		SpannableString styledText2 = new SpannableString(money);
		styledText2.setSpan(new TextAppearanceSpan(getActivity(), R.style.money_white_yuan_style1), 0,
				2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		styledText2.setSpan(new TextAppearanceSpan(getActivity(), R.style.money_white_style_40sp), 2, money.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return styledText2;
	}

	/**
	 * 还款日的text style
	 * @param day
	 * @return
	 */
	private SpannableString getRepaymentDayTextStyle(String day) {
		SpannableString styledText3 = new SpannableString(day);
		styledText3.setSpan(new TextAppearanceSpan(getActivity(), R.style.money_yuan_style1), 0,
				3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		styledText3.setSpan(new TextAppearanceSpan(getActivity(), R.style.money_style0), 3, (day.length() - 1),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		styledText3.setSpan(new TextAppearanceSpan(getActivity(), R.style.money_yuan_style1), (day.length() - 1),
				day.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return styledText3;
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
	 * @param localShare
	 *            根据该参数取值判断分享内容是本地的还是服务器端传过来的
	 */
	public static void oneKeyShare(final Context context, final String title, final String content,
			final String imagePathOrImageUrl, final String urlAfterClick, boolean shareRecommendCode) {
		LogUtil.printLog("title=" + title + " ; content=" + content + " ; imagePathOrImageUrl = " + imagePathOrImageUrl
				+ " ; urlAfterClick = " + urlAfterClick);

		ShareSDK.initSDK(context);
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();

		// 显示推荐码
//		oks.setShareRecommendCode(shareRecommendCode);
		oks.setSilent(false);
		
		oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
			
			@Override
			public void onShare(Platform platform, ShareParams paramsToShare) {

				User user = AppContext.user;
				// 本地分享数据
				String localTitle = "分期购物上品质商城";
				String localContent = "玖富品质商城，京东品质保证，正品行货，爆款直降，白条购物，想分就分！";
				String localUrl = HttpRequest.QUALITY_MARKET_WEB_URL+"views/share.html";
				SharedPreferences sharedPreferences = context
						.getSharedPreferences(ConstantsUtil.GLOBAL_CONFIG_FILE_NAME, Context.MODE_PRIVATE);
				String localImagePath = sharedPreferences.getString(ConstantsUtil.APP_ICON_LOCAL_STORE_KEY, null);
				if (StringUtil.notEmpty(title)) {
					localTitle = title;
				}
				
				if (StringUtil.notEmpty(content)) {
					localContent = content;
				}
				
				if (StringUtil.notEmpty(urlAfterClick)) {
					localUrl = urlAfterClick;
				}

				paramsToShare.setTitle(localTitle);
				// 微信收藏和微信朋友圈该属性不显示
				paramsToShare.setText(localContent);
				if (imagePathOrImageUrl != null && !"".equals(imagePathOrImageUrl)) {
					paramsToShare.setImageUrl(imagePathOrImageUrl);
					// paramsToShare.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
				} else {
					paramsToShare.setImagePath(localImagePath);
				}
				// url仅在微信（包括好友和朋友圈）中使用
				paramsToShare.setUrl(localUrl);
				paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
				// titleUrl是标题的网络链接，site是分享此内容的网站名称，siteUrl是分享此内容的网站地址,仅在人人网和QQ空间使用
				paramsToShare.setTitleUrl(localUrl);
				paramsToShare.setSite(context.getString(R.string.app_name));
				paramsToShare.setSiteUrl(localUrl);
				// 发短信用到的参数
				paramsToShare.setAddress("");

//				paramsToShare.setTitle("分享标题--Title");
//				paramsToShare.setTitleUrl("http://mob.com");
//				paramsToShare.setText("分享测试文--Text");
//				paramsToShare.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
			}
		});
		// 启动分享GUI
		oks.show(context);
	}
	
	
}

