package com.jfbank.qualitymarket.activity;

import java.util.List;

import org.apache.http.Header;

import com.alibaba.fastjson.JSON;
import com.jfbank.qualitymarket.ActivitysManage;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.adapter.AddressAdapter;
import com.jfbank.qualitymarket.base.BaseActivity;
import com.jfbank.qualitymarket.dao.StoreService;
import com.jfbank.qualitymarket.fragment.PopDialogFragment;
import com.jfbank.qualitymarket.model.AddressBean;
import com.jfbank.qualitymarket.model.AddressBean.DataBean;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
import com.jfbank.qualitymarket.util.UserUtils;

import java.util.HashMap; import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
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

import qiu.niorgai.StatusBarCompat;

/**
 * 地址选择区对应的页面
 * 
 * @author 彭爱军
 * @date 2016年8月16日
 */
public class AreaAddressActivity extends BaseActivity {
	private ListView mAddress_listvie;
	/** 父编码。获取省即传0 */
	private String mCode;

	private AddressAdapter mAdapter;
	private List<DataBean> mData;
	RelativeLayout rlTitle;
	/**
	 * 显示标题内容
	 */
	TextView tvTitle;
	/**
	 * 返回
	 */
	ImageView ivBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_address);
		Intent intent = getIntent();
		if (null != intent) {
			mCode = intent.getStringExtra("CODE");

		}
		bindViews();
		ActivitysManage.getActivitysManager().addActivity(this);
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
		tvTitle.setText("地址选择");

		ivBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mAddress_listvie.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				AppContext.getAddressInfo().setAddCounty(mData.get(position).getAreaname()); // 存储省和省编码
				AppContext.getAddressInfo().setAddCountyCode(mData.get(position).getAreaid());

				requestSetReceivingAddress(mData.get(position).getAreaid(), false);
			}
		});
		requestSetReceivingAddress(mCode, true);
	}

	/**
	 * 网络请求获取数据
	 * 
	 * @param code
	 * @param isNext
	 */
	private void requestSetReceivingAddress(final String code, final boolean isNext) {
		Map<String,String> params = new HashMap<>();

		params.put("ver", AppContext.getAppVersionName(this));
		params.put("Plat", ConstantsUtil.PLAT);
		
		params.put("uid", AppContext.user.getUid());
		params.put("token", AppContext.user.getToken());
		
		params.put("superareaid", code);

		Log.e("TAG", params.toString());

		HttpRequest.setReceivingAddress(mContext,params, new AsyncResponseCallBack() {

			@Override
			public void onResult(String arg2) {
				if (null != arg2 && arg2.length()> 0) {
					Log.e("TAG", new String(arg2));
					explainJson(new String(arg2), isNext, code);
				}

			}

			@Override
			public void onFailed(String path, String msg) {
				Toast.makeText(AreaAddressActivity.this, ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER, Toast.LENGTH_SHORT)
						.show();

			}
		});
	}

	/**
	 * 解释json
	 * 
	 * @param isNext
	 * @param code
	 * @param json
	 *            {"statusDetail":"查询无数据","status":11,"data":[],"function":
	 *            "setreceivingaddress"}
	 */
	protected void explainJson(String json, boolean isNext, String code) {
		AddressBean bean = JSON.parseObject(json, AddressBean.class);
		if (null != bean && ConstantsUtil.RESPONSE_SUCCEED ==Integer.parseInt(bean.getStatus())) {
			if (null == bean.getData() || bean.getData().size() < 1) {
				if (!isNext) {
					clossActivity();
				}
			} else {
				if (!isNext) {
					// 进入下一级别
					Intent intent = new Intent(AreaAddressActivity.this, TownAddressActivity.class);
					intent.putExtra("CODE", code);
					startActivity(intent);
				} else {
					setData(bean.getData());
				}
			}
		} else if ("11".equals(bean.getStatus())) {
			clossActivity();
		}else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == Integer.parseInt(bean.getStatus())) {
			UserUtils.tokenFailDialog(mContext, bean.getStatusDetail(), null);
		} else {
			Toast.makeText(this, bean.getStatusDetail(), Toast.LENGTH_SHORT).show();

		}
	}

	/**
	 * 用来关闭activity。 即没有第四级数据的时候
	 */
	private void clossActivity() {
		// TODO 说明没有数据了。 即没有下层了。 即要关闭地址选择了。
		AppContext.getAddressInfo().setOK(true);
		ActivitysManage.getActivitysManager().beforeOneActivity();
		ActivitysManage.getActivitysManager().beforeOneActivity();
		finish();
	}

	/**
	 * 设置数据
	 * 
	 * @param data
	 */
	private void setData(List<DataBean> data) {
		mData = data;
		if (null == mAdapter) {
			mAdapter = new AddressAdapter(this, mData, null, false);
			mAddress_listvie.setAdapter(mAdapter);
		} else {
			mAdapter.setData(mData);
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivitysManage.getActivitysManager().finishActivity(this);
	}
	@Override
	protected String getPageName() {
		String titleName=tvTitle.getText().toString();
		if (TextUtils.isEmpty(titleName))
			titleName=getClass().getName();
		return titleName;
	}
}
