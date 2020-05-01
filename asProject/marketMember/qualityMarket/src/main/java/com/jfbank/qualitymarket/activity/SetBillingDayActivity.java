package com.jfbank.qualitymarket.activity;

import org.apache.http.Header;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.base.BaseActivity;
import com.jfbank.qualitymarket.model.ReceivingAddressBean;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.LogUtil;
import com.jfbank.qualitymarket.util.StringUtil;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
 import java.util.HashMap; import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import qiu.niorgai.StatusBarCompat;


/**
 *	设置账单日页面
 * @author 崔朋朋
 */
public class SetBillingDayActivity extends BaseActivity {
	@InjectView(R.id.rl_title)
	RelativeLayout rlTitle;
	@InjectView(R.id.tv_title)
	TextView tvTitle;
	@InjectView(R.id.tv_setBillingDayActivity_billingDay1)
	TextView billingDay1TextView;
	@InjectView(R.id.tv_setBillingDayActivity_billingDay2)
	TextView billingDay2TextView;
	@InjectView(R.id.tv_setBillingDayActivity_billingDay3)
	TextView billingDay3TextView;
	@InjectView(R.id.tv_setBillingDayActivity_billingDay4)
	TextView billingDay4TextView;
	@InjectView(R.id.tv_setBillingDayActivity_billingDay5)
	TextView billingDay5TextView;
	
	@InjectView(R.id.tv_setBillingDayActivity_bottomHintBillingDay1)
	TextView bottomHintBillingDay1TextView;
	@InjectView(R.id.tv_setBillingDayActivity_bottomHintBillingDay2)
	TextView bottomHintBillingDay2TextView;
	@InjectView(R.id.tv_setBillingDayActivity_bottomHintLastMonth)
	TextView bottomHintLastMonthTextView;
	@InjectView(R.id.tv_setBillingDayActivity_bottomHintRepaymentDay)
	TextView bottomHintRepaymentDayTextView;
	
	@InjectView(R.id.tv_setBillingDayActivity_item_billingDay)
	TextView itemBillingDayTextView;
	@InjectView(R.id.tv_setBillingDayActivity_item_repaymentDate)
	TextView itemRepaymentDateTextView;
	
	@InjectView(R.id.btn_setBillingDayActivity_confirm)
	Button Button;
	
	//账单日和还款日的间隔
	private final int PERIOD_BETWEEN_BILLING_DAY_AND_REPAYMENT_DAY = 5;
	private final String BILLING_DAY_1 = "2";
	private final String BILLING_DAY_2 = "5";
	private final String BILLING_DAY_3 = "10";
	private final String BILLING_DAY_4 = "15";
	private final String BILLING_DAY_5 = "20";
	private Resources resources;
	private String billingDay = "";
	
	@OnClick({R.id.btn_setBillingDayActivity_confirm,R.id.tv_setBillingDayActivity_billingDay1,
		R.id.tv_setBillingDayActivity_billingDay2,R.id.tv_setBillingDayActivity_billingDay3,
		R.id.tv_setBillingDayActivity_billingDay4,R.id.tv_setBillingDayActivity_billingDay5,
		R.id.iv_back})
	public void onViewClick(View v){
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.btn_setBillingDayActivity_confirm:
//			setBillingDate();
			
//			AppContext.user.setBillDate(billingDay);
			Intent intent = new Intent();
			intent.putExtra(ConfirmOrderActivity.KEY_OF_BILLING_DAY, billingDay+"");
			setResult(100, intent);
			finish();
			break;
		case R.id.tv_setBillingDayActivity_billingDay1:
			setDefaultTextColor();
			billingDay1TextView.setTextColor(resources.getColor(R.color.themeRed));
			billingDay = billingDay1TextView.getText().toString();
			setRepaymentDay(billingDay);
			break;
		case R.id.tv_setBillingDayActivity_billingDay2:
			setDefaultTextColor();
			billingDay2TextView.setTextColor(resources.getColor(R.color.themeRed));
			billingDay = billingDay2TextView.getText().toString();
			setRepaymentDay(billingDay);
			break;
		case R.id.tv_setBillingDayActivity_billingDay3:
			setDefaultTextColor();
			billingDay3TextView.setTextColor(resources.getColor(R.color.themeRed));
			billingDay = billingDay3TextView.getText().toString();
			setRepaymentDay(billingDay);
			
			break;
		case R.id.tv_setBillingDayActivity_billingDay4:
			setDefaultTextColor();
			billingDay4TextView.setTextColor(resources.getColor(R.color.themeRed));
			billingDay = billingDay4TextView.getText().toString();
			setRepaymentDay(billingDay);
			
			break;
		case R.id.tv_setBillingDayActivity_billingDay5:
			setDefaultTextColor();
			billingDay5TextView.setTextColor(resources.getColor(R.color.themeRed));
			billingDay = billingDay5TextView.getText().toString();
			setRepaymentDay(billingDay);
			
			break;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_billing_day);
		ButterKnife.inject(this);
		resources = getResources();
		billingDay1TextView.setText(BILLING_DAY_1);
		billingDay2TextView.setText(BILLING_DAY_2);
		billingDay3TextView.setText(BILLING_DAY_3);
		billingDay4TextView.setText(BILLING_DAY_4);
		billingDay5TextView.setText(BILLING_DAY_5);
		itemBillingDayTextView.setText("0");
		itemRepaymentDateTextView.setText("0");

		setDefaultTextColor();
		billingDay2TextView.setTextColor(resources.getColor(R.color.themeRed));
		billingDay = billingDay2TextView.getText().toString();
		setRepaymentDay(billingDay);
		
		Intent intent = getIntent();
		String billingDayStr = intent.getStringExtra(ConfirmOrderActivity.KEY_OF_BILLING_DAY);
		if(StringUtil.notEmpty(billingDayStr) && !ConfirmOrderActivity.NO_BILLING_DAY.equals(billingDayStr)){
			billingDay = billingDayStr.replace(ConfirmOrderActivity.DAY, "").substring(2);
			
			setDefaultTextColor();
			if(BILLING_DAY_1.equals(billingDay)){
				billingDay1TextView.setTextColor(resources.getColor(R.color.themeRed));
			}else if(BILLING_DAY_2.equals(billingDay)){
				billingDay2TextView.setTextColor(resources.getColor(R.color.themeRed));
			}else if(BILLING_DAY_3.equals(billingDay)){
				billingDay3TextView.setTextColor(resources.getColor(R.color.themeRed));
			}else if(BILLING_DAY_4.equals(billingDay)){
				billingDay4TextView.setTextColor(resources.getColor(R.color.themeRed));
			}else if(BILLING_DAY_5.equals(billingDay)){
				billingDay5TextView.setTextColor(resources.getColor(R.color.themeRed));
			} 
			setRepaymentDay(billingDay);
		}
		tvTitle.setText(R.string.str_pagename_setbillingday);
		CommonUtils.setTitle(this,rlTitle);
	}

	@Override
	protected String getPageName() {
		return getString(R.string.str_pagename_setbillingday);
	}

	/**
	 * 设置默认的文本颜色
	 */
	public void setDefaultTextColor(){
		int defaultTextColor = resources.getColor(R.color.app_editText_hint);
		billingDay1TextView.setTextColor(defaultTextColor);
		billingDay2TextView.setTextColor(defaultTextColor);
		billingDay3TextView.setTextColor(defaultTextColor);
		billingDay4TextView.setTextColor(defaultTextColor);
		billingDay5TextView.setTextColor(defaultTextColor);
	}
	
	/**
	 * 设置还款日
	 * @param billingDay 账单日
	 */
	public void setRepaymentDay(String billingDay){
		int repaymentDay = Integer.valueOf(billingDay)+PERIOD_BETWEEN_BILLING_DAY_AND_REPAYMENT_DAY;
		itemBillingDayTextView.setText(billingDay);
		itemRepaymentDateTextView.setText(repaymentDay+"");
		
		bottomHintBillingDay1TextView.setText(billingDay);
		bottomHintBillingDay2TextView.setText(billingDay);
		bottomHintLastMonthTextView.setText((Integer.valueOf(billingDay)+1)+"");
		bottomHintRepaymentDayTextView.setText(repaymentDay+"");
	}
}
