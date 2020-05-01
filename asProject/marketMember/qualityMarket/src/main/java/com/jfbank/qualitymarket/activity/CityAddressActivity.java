package com.jfbank.qualitymarket.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfbank.qualitymarket.ActivitysManage;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.adapter.AddressAdapter;
import com.jfbank.qualitymarket.base.BaseActivity;
import com.jfbank.qualitymarket.dao.StoreService;
import com.jfbank.qualitymarket.fragment.PopDialogFragment;
import com.jfbank.qualitymarket.model.AddressBean;
import com.jfbank.qualitymarket.model.AddressBean.DataBean;
import com.jfbank.qualitymarket.model.BaseInfoCityBean;
import com.jfbank.qualitymarket.model.DataProvincesBean;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.LogUtil;
import com.jfbank.qualitymarket.util.StringUtil;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
import com.jfbank.qualitymarket.util.UserUtils;

import java.util.HashMap; import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import qiu.niorgai.StatusBarCompat;

/**
 * 地址选择市对应的页面
 * @author 彭爱军
 * @date 2016年8月16日
 */
public class CityAddressActivity extends BaseActivity {
	protected String TAG = CityAddressActivity.class.getName();
	RelativeLayout rlTitle;
	/**
	 * 显示标题内容
	 */
	TextView tvTitle;
	/**
	 * 返回
	 */
	ImageView ivBack;
	private ListView mAddress_listvie;
	/** 没有数据显示的图标 */
	private ImageView mMy_receing_iv_no_data;
	/**父编码。获取省即传0*/
	private String mCode;
	
	private AddressAdapter mAdapter;
	private List<DataBean> mData;
	
	/**是否为基本信息中选择的地址*/
	private boolean mIsBaseInfo;
	/**基本信息中市的数据*/
	private List<DataProvincesBean> mDataCitys = new ArrayList<DataProvincesBean>();
	/**是否支持社保, true支持*/
	private boolean mSupportShebao;
	private String comeFrom = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_address);
		Intent intent = getIntent();
		if (null != intent) {
			 mCode = intent.getStringExtra("CODE");
			 mIsBaseInfo = intent.getBooleanExtra("IS_BASE_INFO", false);
			 mSupportShebao = intent.getBooleanExtra(LoginActivity.KEY_OF_COME_FROM,false);
		}
		bindViews();
		ActivitysManage.getActivitysManager().addActivity(this);
	}

	@Override
	protected String getPageName() {
		return getString(R.string.str_pagename_cityaddress) ;
	}

	/**
	 * 初始化View以及设置监听
	 */
    private void bindViews() {
		rlTitle = (RelativeLayout) findViewById(R.id.rl_title);
		ivBack = (ImageView) findViewById(R.id.iv_back);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		CommonUtils.setTitle(this, rlTitle);
        
        mAddress_listvie = (ListView) findViewById(R.id.address_listvie);
		
        mMy_receing_iv_no_data = (ImageView) findViewById(R.id.address_iv_no_data);

		tvTitle.setText(R.string.str_pagename_cityaddress);

		ivBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
        mAddress_listvie.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				if (mIsBaseInfo) {
					AppContext.getAddressInfo().setAddCity(mDataCitys.get(position).getName());		//存储省和省编码
					AppContext.getAddressInfo().setAddCityCode(mDataCitys.get(position).getCode());
					AppContext.getAddressInfo().setOK(true);
					AppContext.getAddressInfo().setCleanJobUnit(true);
					AppContext.getAddressInfo().setNeedPwd(mDataCitys.get(position).isNeedPwd());
					AppContext.getAddressInfo().setNeedSecurityNo(mDataCitys.get(position).isNeedSecurityNo());
					AppContext.getAddressInfo().setNeedPersonNo(mDataCitys.get(position).isNeedPersonNo());
					ActivitysManage.getActivitysManager().beforeOneActivity();
					finish();
				}else{
					AppContext.getAddressInfo().setAddCity(mData.get(position).getAreaname());		//存储省和省编码
					AppContext.getAddressInfo().setAddCityCode(mData.get(position).getAreaid());
					Intent intent = new Intent(CityAddressActivity.this, AreaAddressActivity.class);
					intent.putExtra("CODE", mData.get(position).getAreaid());
					startActivity(intent ); 
				}
			}
		});
        
       if (mIsBaseInfo) {
			requestQueryAreasByParentCode();
		}else{
	        requestSetReceivingAddress();
		}

    }
    
	
    
    /**
     * 网络请求6.2	根据父区域Code查询区域列表
     */
    private void requestQueryAreasByParentCode() {
    	Map<String,String> params = new HashMap<>();

		params.put("ver", AppContext.getAppVersionName(this));
		params.put("Plat", ConstantsUtil.PLAT);
		
		params.put("uid", AppContext.user.getUid());
		params.put("token", AppContext.user.getToken());
		
		params.put("parentCode", mCode);
		params.put("supportShebao", String.valueOf( mSupportShebao ));
		
		Log.e("TAG",params.toString());
		
		HttpRequest.queryAreasByParentCode(mContext,params, new AsyncResponseCallBack() {

			@Override
			public void onResult(String arg2) {
				if (null != arg2 && arg2.length() > 0) {
					Log.e("TAG", new String(arg2));
					explainJsonQueryParentCode(new String(arg2));
				}

			}

			@Override
			public void onFailed(String path, String msg) {
				Toast.makeText(CityAddressActivity.this, ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER, Toast.LENGTH_SHORT).show();

			}
		});
	}

    /**
     * 解释基本信息中地址查询市的json
     * @param json
     */
	protected void explainJsonQueryParentCode(String json) {
		BaseInfoCityBean bean = JSON.parseObject(json, BaseInfoCityBean.class);
		if (null != bean && ConstantsUtil.RESPONSE_SUCCEED ==Integer.parseInt(bean.getStatus())) {
			if (null == bean.getAreas() || bean.getAreas().size() < 1) {
				mMy_receing_iv_no_data.setVisibility(View.VISIBLE);
				mAddress_listvie.setVisibility(View.GONE);
				return;
			}
			 mDataCitys = bean.getAreas();
			setData(null,bean.getAreas());
		}else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == Integer.parseInt(bean.getStatus())) {
				UserUtils.tokenFailDialog(mContext, bean.getStatusDetail(), null);
		}else{
			Toast.makeText(this, bean.getStatusDetail(), Toast.LENGTH_SHORT).show();
		}
	}

	/**
     * 网络请求获取数据
     */
	private void requestSetReceivingAddress() {
		Map<String,String> params = new HashMap<>();

		params.put("ver", AppContext.getAppVersionName(this));
		params.put("Plat", ConstantsUtil.PLAT);
		
		params.put("uid", AppContext.user.getUid());
		params.put("token", AppContext.user.getToken());
		
		params.put("superareaid", mCode);
		
		HttpRequest.setReceivingAddress(mContext,params, new AsyncResponseCallBack() {

			@Override
			public void onResult(String arg2) {
				if (null != arg2 && arg2.length() > 0) {
					Log.e("TAG", new String(arg2));
					explainJson(new String(arg2));
				}

			}

			@Override
			public void onFailed(String path, String msg) {
				Toast.makeText(CityAddressActivity.this, ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER, Toast.LENGTH_SHORT).show();

			}
		});
	}
    
	/**
	 * 解释json
	 * @param json
	 */
	protected void explainJson(String json) {
		AddressBean bean= JSON.parseObject(json, AddressBean.class);
		if (null != bean && ConstantsUtil.RESPONSE_SUCCEED ==Integer.parseInt(bean.getStatus())) {
			if (null == bean.getData() || bean.getData().size() < 1) {
				//TODO 	如果是多层嵌套，则此时没有数据了。即需要存储数据。并且关闭页面。
			}else{
				if (null == bean.getData() || bean.getData().size() < 1) {
					mMy_receing_iv_no_data.setVisibility(View.VISIBLE);
					mAddress_listvie.setVisibility(View.GONE);
					return;
				}
				setData(bean.getData(),null);
			}
		}else if (ConstantsUtil.RESPONSE_TOKEN_FAIL==Integer.parseInt(bean.getStatus())) {
			UserUtils.tokenFailDialog(mContext, bean.getStatusDetail(), null);
		} else{
			Toast.makeText(this, bean.getStatusDetail(), Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * 设置数据
	 * @param data 
	 */
	private void setData(List<DataBean> data,List<DataProvincesBean> mDataCitys) {
		mData = data;
		if (null == mAdapter) {
			mAdapter = new AddressAdapter(this, mData,mDataCitys,mIsBaseInfo);
			mAddress_listvie.setAdapter(mAdapter);
			mAdapter.notifyDataSetChanged();
		}else{
			if (mIsBaseInfo) {
				mAdapter.setDataProvinces(mDataCitys);
			}else{
				mAdapter.setData(mData);
			}
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivitysManage.getActivitysManager().finishActivity(this);
	}
    
}
