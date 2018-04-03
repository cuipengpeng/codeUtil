package com.jfbank.qualitymarket.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huoyanzhengxin.zhengxin.print.FingerPrint;
import com.huoyanzhengxin.zhengxin.print.FingerPrintHelp;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.base.BaseActivity;
import com.jfbank.qualitymarket.util.ActivityManager;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.TDUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
*	支付成功页面
* @author 崔朋朋
*/

public class PaySuccessActivity extends BaseActivity {
	public static final String TAG  = PaySuccessActivity.class.getName();
	@InjectView(R.id.rl_title)
	RelativeLayout rlTitle;
	@InjectView(R.id.tv_title)
	TextView tvTitle;
	@InjectView(R.id.btn_paySuccessActivity_viewOrder)
	Button viewOrderButton;

	private String orderId;
	private String comeFrom = "";
	private FingerPrint fingerPrint;
	
	@OnClick({R.id.btn_paySuccessActivity_viewOrder, R.id.iv_back})
	public void OnViewClick(View v){

		switch (v.getId()) {
		case R.id.btn_paySuccessActivity_viewOrder:
			fingerPrint.sendEventInfo("android支付成功页面--商品ID："+PayActivity.productNo, AppContext.user.getUname());

			if(ConfirmOrderActivity.TAG.equals(comeFrom)){
				//从“商品详情”页面过来，则直接调到我的订单，并关闭“商品详情”等界面
				ActivityManager.getInstance().finishOrderAllActivity();
				launchMyOrderActivity();
			}else {
				ActivityManager.getInstance().finishPayllActivity();
				//从“我的订单”或者“订单详情”页面过来，则直接返回“我的订单”
				finish();
			}
			break;
			case R.id.iv_back:
				if(!ConfirmOrderActivity.TAG.equals(comeFrom)) {
					//清除extra列表中的所有的多余的activity：如“待首付订单状态下的订单详情页面”
					ActivityManager.getInstance().finishPayllActivity();
				}else{
					ActivityManager.getInstance().finishOrderAllActivity();
				}
				finish();
				break;
		}
	}

	/**
	 * 启动我的订单页面
	 */
	private void launchMyOrderActivity() {
		Intent intent = new Intent(this, MyOrderActivity.class);
		intent.putExtra(MyOrderActivity.KEY_OF_ORDER_ID, PayActivity.orderNo);
		intent.putExtra(MyOrderActivity.KEY_OF_QUERY_ORDER, MyOrderActivity.ORDER_STATUS_ALL_ORDERS);
		startActivity(intent);

		finish();
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay_success);
		TDUtils.onEvent(this, "100015", "支付成功",TDUtils.getInstance().putUserid().buildParams());
		ButterKnife.inject(this);

		fingerPrint = FingerPrintHelp.getFingerPrint(getApplicationContext());
		fingerPrint.startTime();

		Intent intent = getIntent();
		comeFrom = intent.getStringExtra(LoginActivity.KEY_OF_COME_FROM);
		tvTitle.setText(R.string.str_pagename_paysuccess);
		CommonUtils.setTitle(this, rlTitle);
	}

	@Override
	protected String getPageName() {
		return getString(R.string.str_pagename_paysuccess);
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if(!ConfirmOrderActivity.TAG.equals(comeFrom)) {
				//清除extra列表中的所有的多余的activity：如“待首付订单状态下的订单详情页面”
				ActivityManager.getInstance().finishPayllActivity();
			}else{
				ActivityManager.getInstance().finishOrderAllActivity();
			}
			finish();
			return true;
		} else{
			return super.onKeyDown(keyCode, event);
		}
	}
	
}
