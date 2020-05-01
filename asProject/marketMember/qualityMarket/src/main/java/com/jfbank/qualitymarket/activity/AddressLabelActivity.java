package com.jfbank.qualitymarket.activity;

import java.util.List;

import org.apache.http.Header;

import com.alibaba.fastjson.JSON;
import com.jfbank.qualitymarket.ActivitysManage;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.adapter.LabelAdapter;
import com.jfbank.qualitymarket.base.BaseActivity;
import com.jfbank.qualitymarket.model.LabelBean;
import com.jfbank.qualitymarket.model.LabelBean.LabelData;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.UserUtils;
import com.jfbank.qualitymarket.widget.LoadingAlertDialog;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
 import java.util.HashMap; import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import qiu.niorgai.StatusBarCompat;

/**
 * 选择地址标签页面
 * @author 彭爱军
 * @date 2016年10月9日
 */
public class AddressLabelActivity extends BaseActivity {
	@InjectView(R.id.rl_title)
	RelativeLayout rlTitle;
	@InjectView(R.id.tv_title)
	TextView tvTitle;
	@InjectView(R.id.iv_back)
	ImageView ivBack;
	@InjectView(R.id.label_listview)
	 ListView mListView;
	/** 网编请求时加载框 */
	private LoadingAlertDialog mDialog;

	private List<LabelBean.LabelData> mData;
	private LabelAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_address_label);
		ButterKnife.inject(this);
		ActivitysManage.getActivitysManager().addActivity(this);
		bindViews();
	}

	/**
	 * 初始化View以及设置监听
	 */
	private void bindViews() {
		CommonUtils.setTitle(this, rlTitle);
		tvTitle.setText(R.string.str_pagename_addresslabel);
		ivBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent data = new Intent();
				data.putExtra("LABEL", mData.get(position).getParameterName());
				setResult(200, data );
				finish();
			}
		});
		CommonUtils.setTitle(this, rlTitle);
		requestAddReceiptAddress();

	}
	
	/**
	 * 网络请求添加地址
	 */
	private void requestAddReceiptAddress() {
		if (null == mDialog) {
			mDialog = new LoadingAlertDialog(this);
		}
		mDialog.show(ConstantsUtil.NETWORK_REQUEST_IN);
		
		Map<String,String> params = new HashMap<>();
		
		params.put("uid", AppContext.user.getUid());
		params.put("token", AppContext.user.getToken());
		
		params.put("ver", AppContext.getAppVersionName(this));
		params.put("Plat", ConstantsUtil.PLAT);
		
		params.put("parameterType", "addressLabel");	//parameterType传addressLabel

		Log.e("TAG", params.toString());
		
		HttpRequest.qualityParameterShow(mContext,params, new AsyncResponseCallBack() {

			@Override
			public void onResult(String arg2) {
				if (mDialog.isShowing()) {
					mDialog.dismiss();
				}
				if (null != arg2 && arg2.length() > 0) {
					Log.e("TAG", new String(arg2));
					explainJson(new String(arg2));
				}

			}

			@Override
			public void onFailed(String path, String msg) {
				if (mDialog.isShowing()) {
					mDialog.dismiss();
				}
				Log.e("TAG", "arg0:" + msg );
				Toast.makeText(AddressLabelActivity.this, ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER, Toast.LENGTH_SHORT).show();

			}
		});
	}
	
	/**
	 * 解释json
	 * @param json
	 */
	protected void explainJson(String json) {
		LabelBean bean = JSON.parseObject(json, LabelBean.class);
		if (null != bean && ConstantsUtil.RESPONSE_SUCCEED ==Integer.parseInt(bean.getStatus())) {
			if (null != bean.getData() && bean.getData().size() > 0) {
				setData(bean.getData());
			}else{
				Toast.makeText(this, "没有获取到数据", Toast.LENGTH_SHORT).show();
			}
		}else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == Integer.parseInt(bean.getStatus())) {
			UserUtils.tokenFailDialog(mContext, bean.getStatusDetail(), null);
		}else{
			Toast.makeText(this, bean.getStatusDetail(), Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 设置数据
	 * @param data 
	 */
	private void setData(List<LabelData> data) {
		mData = data;
		if (null == mAdapter) {
			mAdapter = new LabelAdapter(this, data);
			mListView.setAdapter(mAdapter);
		}else{
			mAdapter.setData(data);
		}
		mAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivitysManage.getActivitysManager().finishActivity(this);
	}

	@Override
	protected String getPageName() {
		return getString(R.string.str_pagename_addresslabel);
	}
}
