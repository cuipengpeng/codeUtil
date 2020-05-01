//package com.jfbank.qualitymarket.activity;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.http.Header;
//
//import com.alibaba.fastjson.JSON;
//import com.jfbank.qualitymarket.ActivitysManage;
//import com.jfbank.qualitymarket.AppContext;
//import com.jfbank.qualitymarket.R;
//import com.jfbank.qualitymarket.adapter.SelectionUnitAdapter;
//import com.jfbank.qualitymarket.base.BaseActivity;
//import com.jfbank.qualitymarket.dao.StoreService;
//import com.jfbank.qualitymarket.fragment.PopDialogFragment;
//import com.jfbank.qualitymarket.model.BasicInfoJobUnitBean;
//import com.jfbank.qualitymarket.model.BasicInfoJobUnitBean.JobUnitDataBean;
//import com.jfbank.qualitymarket.net.HttpRequest;
//import com.jfbank.qualitymarket.util.CommonUtils;
//import com.jfbank.qualitymarket.util.ConstantsUtil;
//import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
// import java.util.HashMap; import java.util.Map;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import qiu.niorgai.StatusBarCompat;
//
///**
// * 选择单位名称页面
// *
// * @author 彭爱军
// * @date 2016年8月18日
// */
//public class SelectionUnitActivity extends BaseActivity {
//	RelativeLayout rlTitle;
//	/**
//	 * 显示标题内容
//	 */
//	TextView tvTitle;
//	/**
//	 * 返回
//	 */
//	ImageView ivBack;
//    /**搜索内容*/
//	private EditText mSelection_unit_et_search;
//	private ListView mSelection_unit_listview;
//	private String mProvinceCode;			//省编码
//	private String mCityCode;				//市编码
//	private List<JobUnitDataBean> mDataJobUnit;
//	private List<JobUnitDataBean> mData;
//	private SelectionUnitAdapter mAdapter;
//	/**没有数据时显示的图片*/
//	private ImageView mSelection_unit_iv_no_data;
//
//	private class MyTextWatcher implements TextWatcher {
//		private String search;
//		@Override
//		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//		}
//
//		@Override
//		public void onTextChanged(CharSequence s, int start, int before, int count) {
//			if (null == mData || mData.size() < 1) {
//				mSelection_unit_iv_no_data.setVisibility(View.VISIBLE);
//				mSelection_unit_listview.setVisibility(View.GONE);
//				return;
//			}
//			if (mDataJobUnit != null && mDataJobUnit.size() > 0) {
//				mDataJobUnit.clear();
//			}
//			 search = mSelection_unit_et_search.getText().toString().trim();
//			for (int i = 0; i < mData.size(); i++) {
//				if (mData.get(i).getName().contains(search)) {
//					mDataJobUnit.add(mData.get(i));
//				}
//			}
//			if (null == mDataJobUnit || mDataJobUnit.size() < 1) {
//				mSelection_unit_iv_no_data.setVisibility(View.VISIBLE);
//				mSelection_unit_listview.setVisibility(View.GONE);
//				return;
//			}
//			mSelection_unit_iv_no_data.setVisibility(View.GONE);
//			mSelection_unit_listview.setVisibility(View.VISIBLE);
//			if (null != mAdapter) {
//				mAdapter.setData(mDataJobUnit);
//				mAdapter.notifyDataSetChanged();
//			}
//
//		}
//
//		@Override
//		public void afterTextChanged(Editable s) {
//
//		}
//
//	}
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_LEFT_ICON);
//		ActivitysManage.getActivitysManager().addActivity(this);
//		setContentView(R.layout.activity_selection_unit);
//		bindViews();
//	}
//
//	/**
//	 * 初始化View以及设置监听
//	 */
//	private void bindViews() {
//		rlTitle = (RelativeLayout) findViewById(R.id.rl_title);
//		ivBack = (ImageView) findViewById(R.id.iv_back);
//		tvTitle = (TextView) findViewById(R.id.tv_title);
//		CommonUtils.setTitle(this, rlTitle);
//
//		mSelection_unit_et_search = (EditText) findViewById(R.id.selection_unit_et_search);
//		mSelection_unit_listview = (ListView) findViewById(R.id.selection_unit_listview);
//		mSelection_unit_iv_no_data = (ImageView) findViewById(R.id.selection_unit_iv_no_data);
//
//		tvTitle.setText(R.string.str_pagename_selectionunit);
//		ivBack.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				finish();
//			}
//		});
//
//		Intent intent = getIntent();
//		if (null != intent) {
//			mProvinceCode = intent.getStringExtra("PROVINCE_CODE");
//			mCityCode = intent.getStringExtra("CITY_CODE");
//		}
//
//		mSelection_unit_listview.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				Intent data = new Intent(SelectionUnitActivity.this, ApplyAmountActivity.class);
//				data.putExtra("JOB_UNIT_NAME", mDataJobUnit.get(position).getName());
//				data.putExtra("JOB_UNIT_ID", mDataJobUnit.get(position).getId());
//				data.putExtra("COMPANY_AREA_CODE", mDataJobUnit.get(position).getAreaCode());
//				data.putExtra("COMPANY_NO", mDataJobUnit.get(position).getCompanyNo());
//				data.putExtra("ADDRESS", mDataJobUnit.get(position).getAddress());
//				setResult(200, data );
//				finish();
//			}
//		});
//		mDataJobUnit = new ArrayList<BasicInfoJobUnitBean.JobUnitDataBean>();
//		requestGetConsumerCom4creditline();
//	}
//	/**
//	 * 根据当前的省市获得当前的工作单位
//	 */
//	private void requestGetConsumerCom4creditline() {
//		Map<String,String> params = new HashMap<>();
//
//		params.put("ver", AppContext.getAppVersionName(this));
//		params.put("Plat", ConstantsUtil.PLAT);
//
//		params.put("provinceCode", mProvinceCode);
//		params.put("cityCode", mCityCode);
//
//		params.put("uid", AppContext.user.getUid());
//		params.put("token", AppContext.user.getToken());
//
//		Log.e("TAG", params.toString());
//
//		HttpRequest.getConsumerCom4creditline(mContext,params, new AsyncResponseCallBack() {
//
//			@Override
//			public void onResult(String arg2) {
//				if (null != arg2 && arg2.length() > 0) {
//					Log.e("TAG", new String(arg2));
//					explainJsonJobUnit(new String(arg2));
//				}
//
//			}
//
//			@Override
//			public void onFailed(String path, String msg) {
//				Toast.makeText(SelectionUnitActivity.this, ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER, Toast.LENGTH_SHORT).show();
//
//			}
//		});
//	}
//
//
//	/**
//	 * 解释工作单位对应的json
//	 * @param json
//	 */
//	protected void explainJsonJobUnit(String json) {
//		BasicInfoJobUnitBean bean = JSON.parseObject(json, BasicInfoJobUnitBean.class);
//		if (null != bean && ConstantsUtil.RESPONSE_SUCCEED ==Integer.parseInt(bean.getStatus())) {
//			 mDataJobUnit.addAll(bean.getConsumerCompanies());
//			 mData = bean.getConsumerCompanies();
//			 if (null != mDataJobUnit && mDataJobUnit.size() > 0) {
//				 mSelection_unit_iv_no_data.setVisibility(View.GONE);
//					mSelection_unit_listview.setVisibility(View.VISIBLE);
//				if (null == mAdapter) {
//					mAdapter = new SelectionUnitAdapter(this, mDataJobUnit);
//					mSelection_unit_listview.setAdapter(mAdapter);
//				}else{
//					mAdapter.setData(mDataJobUnit);
//				}
//				mSelection_unit_et_search.addTextChangedListener(new MyTextWatcher());
//			}else{
//				mSelection_unit_iv_no_data.setVisibility(View.VISIBLE);
//				mSelection_unit_listview.setVisibility(View.GONE);
//				//Toast.makeText(this, "没有查询到数据", Toast.LENGTH_SHORT).show();
//			}
//		}else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == Integer.parseInt(bean.getStatus())) {
//			showDialog(bean.getStatusDetail());
//		}else{
//			Toast.makeText(this, bean.getStatusDetail(), Toast.LENGTH_SHORT).show();
//		}
//	}
//
//	/**
//	 * token失效时弹出对话框
//	 * @param content
//	 */
//	private void showDialog(String content) {
//		final PopDialogFragment dialog = PopDialogFragment.newDialog(false, false,null, content, null,null, "确定");
//		dialog.setOnClickListen(new PopDialogFragment.OnClickListen() {
//
//			@Override
//			public void rightClick() {
//				new StoreService(SelectionUnitActivity.this).clearUserInfo();
//				Intent loginIntent = new Intent(getApplication(), LoginActivity.class);
//				loginIntent.putExtra(LoginActivity.KEY_OF_COME_FROM, LoginActivity.TOKEN_FAIL_TAG);
//				startActivity(loginIntent);
//				dialog.dismiss();
//			}
//
//			@Override
//			public void leftClick() {
//				dialog.dismiss();
//			}
//		});
//		dialog.show(getSupportFragmentManager(), "TAG");
//	}
//
//	@Override
//	protected void onResume() {
//		// TODO Auto-generated method stub
//		super.onResume();
//	}
//
//	@Override
//	protected String getPageName() {
//		return getString(R.string.str_pagename_selectionunit) ;
//	}
//
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		ActivitysManage.getActivitysManager().finishActivity(this);
//	}
//
//}
