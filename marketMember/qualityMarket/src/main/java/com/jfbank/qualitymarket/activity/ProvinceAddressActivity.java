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
import com.jfbank.qualitymarket.model.AddressInfoBean;
import com.jfbank.qualitymarket.model.BasicInfoBean;
import com.jfbank.qualitymarket.model.DataProvincesBean;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.LogUtil;
import com.jfbank.qualitymarket.util.StringUtil;
import com.jfbank.qualitymarket.util.UserUtils;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
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

/**
 * 地址选择省对应的页面
 *
 * @author 彭爱军
 * @date 2016年8月16日
 */
public class ProvinceAddressActivity extends BaseActivity {
    protected String TAG = ProvinceAddressActivity.class.getName();

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
    /**
     * 父编码。获取省即传0
     */
    private String mCode;

    private AddressAdapter mAdapter;
    private List<DataBean> mData;
    /**
     * 是否为基本信息中选择的地址
     */
    private boolean mIsBaseInfo;
    private String comeFrom = "";        //社保标识

    /**
     * 基本信息中的省
     */
    private List<DataProvincesBean> mDataProvinces = new ArrayList<DataProvincesBean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_address);
        Intent intent = getIntent();

        if (null != intent) {
            mCode = intent.getStringExtra("CODE");
            comeFrom = intent.getStringExtra("SOCIAL_INSURANCE");
            mIsBaseInfo = intent.getBooleanExtra("IS_BASE_INFO", false);
        }
        bindViews();
        ActivitysManage.getActivitysManager().addActivity(this);

    }

    @Override
    protected String getPageName() {
        return getString(R.string.str_pagename_provinceaddress);
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
        //mAdapter = new AddressAdapter(this, null,mDataProvinces,true);
        //mAddress_listvie.setAdapter(mAdapter);

        tvTitle.setText(R.string.str_pagename_provinceaddress);

        ivBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mAddress_listvie.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppContext.setAddressInfo(new AddressInfoBean());
                Intent intent = new Intent(ProvinceAddressActivity.this, CityAddressActivity.class);
                intent.putExtra("IS_BASE_INFO", mIsBaseInfo);
                intent.putExtra(LoginActivity.KEY_OF_COME_FROM, StringUtil.notEmpty(comeFrom) ? true : false);    //true为社保,false基本城市
                if (mIsBaseInfo) {
                    AppContext.getAddressInfo().setAddProvince(mDataProvinces.get(position).getName());        //存储省和省编码
                    AppContext.getAddressInfo().setAddProvinceCode(mDataProvinces.get(position).getCode());
                    intent.putExtra("CODE", mDataProvinces.get(position).getCode());
                } else {
                    AppendAddressActivity.isShow = false;
                    AppContext.getAddressInfo().setAddProvince(mData.get(position).getAreaname());        //存储省和省编码
                    AppContext.getAddressInfo().setAddProvinceCode(mData.get(position).getAreaid());
                    intent.putExtra("CODE", mData.get(position).getAreaid());
                }
                startActivity(intent);
            }
        });


        if (StringUtil.notEmpty(comeFrom)) {
            querySocialSecurityProvince();
        } else if (mIsBaseInfo) {
            requestPrepareData4base4creditline();
        } else {
            requestSetReceivingAddress();
        }

    }

    /**
     * 查询社保省份
     */
    private void querySocialSecurityProvince() {
        Map<String, String> params = new HashMap<>();
        params.put("uid", AppContext.user.getUid());
        params.put("token", AppContext.user.getToken());
        params.put("creditlineApplyId", AppContext.user.getCreditlineApplyId4show());

        HttpRequest.post(mContext, HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.QUERY_SOCIAL_SECURITY_PROVINCE, params, new AsyncResponseCallBack() {

            @Override
            public void onFailed(String path, String msg) {
                Toast.makeText(ProvinceAddressActivity.this, ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER + "查询社保省份失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(String arg2) {
                String jsonStr = new String(arg2);
                LogUtil.printLog("查询社保省份：" + jsonStr);

                JSONObject jsonObject = JSON.parseObject(jsonStr);

                if (ConstantsUtil.RESPONSE_SUCCEED == jsonObject.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                    //Toast.makeText(ProvinceAddressActivity.this, "查询社保省份成功", Toast.LENGTH_SHORT).show();

                    mDataProvinces = jsonObject.parseArray(jsonObject.getJSONObject(ConstantsUtil.RESPONSE_DATA_JSON_ARRAY_FIELD_NAME).getJSONArray("provinces").toString(), DataProvincesBean.class);
                    AppContext.user.setIdName(jsonObject.getJSONObject(ConstantsUtil.RESPONSE_DATA_JSON_ARRAY_FIELD_NAME).getString("name"));        //社保返回的需要提交的数据
                    AppContext.user.setIdNumber(jsonObject.getJSONObject(ConstantsUtil.RESPONSE_DATA_JSON_ARRAY_FIELD_NAME).getString("idNumber"));
                    if (mDataProvinces.size() > 0) {
                        setData(null, mDataProvinces);
                    }

                } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == jsonObject.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                    UserUtils.tokenFailDialog(ProvinceAddressActivity.this, jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), TAG);
                } else {
                    Toast.makeText(ProvinceAddressActivity.this, jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), Toast.LENGTH_SHORT).show();
                }
            }

        });
    }


    /**
     * 网络请求初始化基本信息
     */
    private void requestPrepareData4base4creditline() {

        Map<String, String> params = new HashMap<>();

        params.put("ver", AppContext.getAppVersionName(this));
        params.put("Plat", ConstantsUtil.PLAT);

        params.put("uid", AppContext.user.getUid());
        params.put("token", AppContext.user.getToken());
        //params.put("mobile", AppContext.user.getMobile()); // 手机
        //params.put("defaultAdd", "0"); // 是否为默认地址

        HttpRequest.PrepareData4base4creditline(mContext, params, new AsyncResponseCallBack() {

            @Override
            public void onResult(String arg2) {
                if (null != arg2 && arg2.length() > 0) {
                    Log.e("TAG", new String(arg2));
                    explainJsonBaseInfo(new String(arg2));
                }

            }

            @Override
            public void onFailed(String path, String msg) {
                Toast.makeText(ProvinceAddressActivity.this, "获取基本信息填写数据失败", Toast.LENGTH_SHORT).show();

            }
        });

    }

    /**
     * 解释基本的json
     *
     * @param json
     */
    protected void explainJsonBaseInfo(String json) {
        BasicInfoBean bean = JSON.parseObject(json, BasicInfoBean.class);
        if (null != bean && ConstantsUtil.RESPONSE_SUCCEED == Integer.parseInt(bean.getStatus())) {
            mDataProvinces = bean.getData().getProvinces();
            setData(null, mDataProvinces);
        } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == Integer.parseInt(bean.getStatus())) {
            UserUtils.tokenFailDialog(mContext, bean.getStatusDetail(), null);
        } else {
            Toast.makeText(this, bean.getStatusDetail(), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 网络请求获取数据
     */
    private void requestSetReceivingAddress() {
        Map<String, String> params = new HashMap<>();

        params.put("ver", AppContext.getAppVersionName(this));
        params.put("Plat", ConstantsUtil.PLAT);

        params.put("uid", AppContext.user.getUid());
        params.put("token", AppContext.user.getToken());

        params.put("superareaid", mCode);

        HttpRequest.setReceivingAddress(mContext, params, new AsyncResponseCallBack() {

            @Override
            public void onResult(String arg2) {
                if (null != arg2 && arg2.length() > 0) {
                    Log.e("TAG", new String(arg2));
                    explainJson(new String(arg2));
                }

            }

            @Override
            public void onFailed(String path, String msg) {
                Toast.makeText(ProvinceAddressActivity.this, "保存基本信息失败", Toast.LENGTH_SHORT).show();

            }
        });
    }

    /**
     * 解释json
     *
     * @param json
     */
    protected void explainJson(String json) {
        AddressBean bean = JSON.parseObject(json, AddressBean.class);
        if (null != bean && ConstantsUtil.RESPONSE_SUCCEED == Integer.parseInt(bean.getStatus())) {
            if (null == bean.getData() || bean.getData().size() < 1) {
                //TODO 	如果是多层嵌套，则此时没有数据了。即需要存储数据。并且关闭页面。
            } else {
                setData(bean.getData(), null);
            }
        } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == Integer.parseInt(bean.getStatus())) {
            UserUtils.tokenFailDialog(mContext, bean.getStatusDetail(), null);
        } else {
            Toast.makeText(this, bean.getStatusDetail(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 设置数据
     *
     * @param data
     */
    private void setData(List<DataBean> data, List<DataProvincesBean> mDataProvinces) {
        mData = data;
        if (null == mAdapter) {
            mAdapter = new AddressAdapter(this, mData, mDataProvinces, mIsBaseInfo);
            mAddress_listvie.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        } else {
            if (mIsBaseInfo) {
                mAdapter.setDataProvinces(mDataProvinces);
            } else {
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
