package com.jfbank.qualitymarket.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.adapter.PrepaymentAdapter;
import com.jfbank.qualitymarket.base.BaseActivity;
import com.jfbank.qualitymarket.constants.EventBusConstants;
import com.jfbank.qualitymarket.listener.IMeterialClickLisenter;
import com.jfbank.qualitymarket.model.BankCard;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.LogUtil;
import com.jfbank.qualitymarket.util.UserUtils;
import com.jfbank.qualitymarket.widget.LoadingAlertDialog;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
 import java.util.HashMap; import java.util.Map;

import org.apache.http.Header;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

/**
 * 功能：PrepaymentActivity 提前还款<br>
 * 作者：赵海<br>
 * 时间： 2016/12/29 0029<br>.
 * 版本：1.2.3
 */

public class PrepaymentActivity extends BaseActivity implements View.OnClickListener, IMeterialClickLisenter {
    public static final String TAG = PrepaymentActivity.class.getName();
    public static BankCard bankCard=null;
    private TextView tvTitle;
    private ImageView ivBack;
    private TabLayout tbsPrepayment;
    public ViewPager vpPrepayment;
    PrepaymentAdapter prepaymentAdapter = null;
    RelativeLayout rlTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepayment);
        initView();
        bankCard= new BankCard();
        rlTitle = (RelativeLayout) findViewById(R.id.rl_title);
        CommonUtils.setTitle(this, rlTitle);
        getBankCardList(this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bankCard=null;
        EventBus.getDefault().unregister(this);
    }

    @Subscriber(tag = EventBusConstants.EVENTT_UPDATE_BILLPAYRESULT_STATUS, mode = ThreadMode.MAIN)
    public void updatePay(Object params) {
        vpPrepayment.setCurrentItem(0);
    }

    /**
     * 获取第一个银行卡号
     */
    public void getBankCardList(final Activity activity) {
        final LoadingAlertDialog mDialog = new LoadingAlertDialog(this);
        mDialog.show(ConstantsUtil.NETWORK_REQUEST_IN);
        Map<String,String> params = new HashMap<>();
        params.put("uid", AppContext.user.getUid());
        params.put("token", AppContext.user.getToken());
        params.put("rates", "");

        HttpRequest.post(mContext,HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.GET_BANK_CARD_LIST, params,
                new AsyncResponseCallBack() {

                    @Override
                    public void onResult(String arg2) {
                        if (mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        String jsonStr = new String(arg2);
                        LogUtil.printLog("获取银行卡列表: " + jsonStr);

                        JSONObject jsonObject = JSON.parseObject(jsonStr);

                        if (ConstantsUtil.RESPONSE_SUCCEED == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                            JSONArray orderJsonArray = jsonObject
                                    .getJSONArray(ConstantsUtil.RESPONSE_DATA_JSON_ARRAY_FIELD_NAME);
                            if (orderJsonArray.size() > 0) {
                                //获取第一个银行卡号
                                bankCard = JSON.parseObject(orderJsonArray.get(0).toString(), BankCard.class);
                            }

                        } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == jsonObject.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                            UserUtils.tokenFailDialog(activity, jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), TAG);
                        } else {
                            Toast.makeText(activity, "获取银行卡列表失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailed(String path, String msg) {
                        if (mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        Toast.makeText(activity, ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER + "获取银行卡列表失败",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected String getPageName() {
        return getString(R.string.str_pagename_prepayment);
    }

    /**
     * 初始化组件
     */
    private void initView() {
        prepaymentAdapter = new PrepaymentAdapter(this, getSupportFragmentManager());
        tvTitle = (TextView) findViewById(R.id.tv_title);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        CommonUtils.makeMeterial(ivBack, this);
        tbsPrepayment = (TabLayout) findViewById(R.id.tbs_prepayment);
        vpPrepayment = (ViewPager) findViewById(R.id.vp_prepayment);
        vpPrepayment.setAdapter(prepaymentAdapter);
        vpPrepayment.setOffscreenPageLimit(3);
        //初始化tab
        tbsPrepayment.setupWithViewPager(vpPrepayment);
        for (int i = 0; i < 3; i++) {
            TabLayout.Tab tab = tbsPrepayment.getTabAt(i);
            tab.setCustomView(prepaymentAdapter.getTabView(i));
        }

        tvTitle.setText(R.string.str_pagename_prepayment);
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onMetrialClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back://返回按钮
                finish();
                break;
        }
    }
}
