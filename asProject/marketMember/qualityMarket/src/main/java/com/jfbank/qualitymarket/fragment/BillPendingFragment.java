package com.jfbank.qualitymarket.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.adapter.BillPendingAdapter;
import com.jfbank.qualitymarket.base.BaseFragment;
import com.jfbank.qualitymarket.callback.IBillPendingCallBack;
import com.jfbank.qualitymarket.constants.EventBusConstants;
import com.jfbank.qualitymarket.helper.BillHelper;
import com.jfbank.qualitymarket.listener.IMeterialClickLisenter;
import com.jfbank.qualitymarket.model.BillBean;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.LogUtil;
import com.jfbank.qualitymarket.util.UserUtils;
import com.jfbank.qualitymarket.widget.TwinklingRefreshLayoutView;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
 import java.util.HashMap; import java.util.Map;

import org.apache.http.Header;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.List;

/**
 * 功能：待还款<br>
 * 作者：赵海<br>
 * 时间： 2016/12/29 0029<br>.
 * 版本：1.2.0
 */

public class BillPendingFragment extends BaseFragment implements IBillPendingCallBack.IAdapterCallBack, IMeterialClickLisenter {
    private static final String TAG = "BillPendingFragment";
    TwinklingRefreshLayoutView refreshLayout;
    private ListView plvBillpending;
    private BillPendingAdapter billPendingAdapter;
    private LinearLayout llFooterPay;
    private ImageView ivBillpendingCheck;
    private TextView tvBillpendingDes;
    private TextView tvBillpendingMoney;
    private ImageView ivBillpendingToast;
    private ImageView ivEmpty;

    private TextView tvPayInstant;
    private String totalMoney = "0.0";

    @Override
    public String getPageName() {
        return getString(R.string.str_pagename_billpending);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_billpending, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refreshLayout = (TwinklingRefreshLayoutView) getView().findViewById(R.id.refreshLayout);
        plvBillpending = (ListView) getView().findViewById(R.id.plv_billpending);
        llFooterPay = (LinearLayout) getView().findViewById(R.id.ll_footer_pay);
        ivBillpendingCheck = (ImageView) getView().findViewById(R.id.iv_billpending_check);
        tvBillpendingDes = (TextView) getView().findViewById(R.id.tv_billpending_des);
        tvBillpendingMoney = (TextView) getView().findViewById(R.id.tv_billpending_money);
        ivBillpendingToast = (ImageView) getView().findViewById(R.id.iv_billpending_toast);
        tvPayInstant = (TextView) getView().findViewById(R.id.tv_pay_instant);
        ivEmpty = (ImageView) getView().findViewById(R.id.iv_empty);
        billPendingAdapter = new BillPendingAdapter(this, this);
        plvBillpending.setAdapter(billPendingAdapter);
        CommonUtils.makeMeterial(ivBillpendingCheck, this);
        CommonUtils.makeMeterial(tvPayInstant, this);
        CommonUtils.makeMeterial(ivBillpendingToast, this);
        refreshLayout.setAutoLoadMore(true);
        refreshLayout.setEnableLoadmore(false, false);
        refreshLayout.setOverScrollBottomShow(false);

        //下拉刷新和上啦加载更多
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {//下拉刷新
                getData();
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {//加载更多
                refreshLayout.finishLoadmore();
            }
        });
        refreshLayout.startRefresh();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 选择所有分期账单
     */
    private void selectAll() {
        boolean isCheck = ivBillpendingCheck.isSelected();
        for (int i = 0; i < billPendingAdapter.getData().size(); i++) {
            if (billPendingAdapter.getData().get(i).isCheckEnable()) {
                if (isCheck) {
                    billPendingAdapter.getData().get(i).setCheckAll(false);
                } else {
                    billPendingAdapter.getData().get(i).setCheckAll(true);
                }
                billPendingAdapter.getData().get(i).setCheck(!isCheck);
            }
        }
        billPendingAdapter.notifyDataSetChanged();
    }

    /**
     * 获取首页数据
     */
    private void getData() {
        Map<String,String> params = new HashMap<>();
        params.put("uid", AppContext.user.getUid());
        params.put("token", AppContext.user.getToken());
        params.put("billStatus", "NO_REPAYMENT");
//        params.put("pageNo", "1");
        HttpRequest.post(mContext, HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.QUERY_BILL_INSTALMENT, params, new AsyncResponseCallBack() {
            @Override
            public void onResult(String bytes) {
                refreshLayout.finishRefreshing();
                String jsonStr = new String(bytes);
                JSONObject jsonObject = JSON.parseObject(jsonStr);
                Log.e(TAG, jsonStr);

                if ((ConstantsUtil.RESPONSE_SUCCEED == jsonObject.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME))) {
                    plvBillpending.setEmptyView(ivEmpty);
                    try {
                        List<BillBean> billList = JSON.parseArray(jsonObject.getJSONArray(ConstantsUtil.RESPONSE_DATA_JSON_ARRAY_FIELD_NAME).toJSONString(), BillBean.class);
                        billPendingAdapter.updateData(billList);
                    } catch (Exception e) {
                        LogUtil.printLog(e.getMessage() + "");
                        Log.e(TAG, e.getMessage() + "");
                    }
                } else if (ConstantsUtil.RESPONSE_NO_DATA == jsonObject.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {//没有数据，显示暂无数据
                    plvBillpending.setEmptyView(ivEmpty);
                    billPendingAdapter.updateData(null);
                } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == jsonObject.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                    UserUtils.tokenFailDialog(mContext, jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), null);
                } else {//提示数据出错
                    Toast.makeText(getActivity(), jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME) + "", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailed(String path, String msg) {
                refreshLayout.finishRefreshing();
                Toast.makeText(getActivity(), R.string.error_net, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onShowPay(boolean isshowPay, boolean isChecked, float totalMoney) {
        this.totalMoney = MyAccountFragment.moneyDecimalFormat.format(totalMoney);
        tvBillpendingMoney.setText(this.totalMoney + "元");
        if (isshowPay) {
            if (llFooterPay.getVisibility() == View.GONE) {
                BillHelper.setBottomViewVisible(llFooterPay, View.VISIBLE, new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        refreshLayout.setPadding(0, 0, 0, CommonUtils.dipToPx(getActivity(), 56));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        } else {
            if (llFooterPay.getVisibility() == View.VISIBLE) {
                BillHelper.setBottomViewVisible(llFooterPay, View.GONE, new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        refreshLayout.setPadding(0, 0, 0, 0);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        }
        ivBillpendingCheck.setSelected(isChecked);
    }


    @Override
    public void onMetrialClick(View v) {
        switch (v.getId()) {
            case R.id.iv_billpending_check://选择所有账单
                selectAll();
                break;
            case R.id.tv_pay_instant://立即支付
                payBill();
                break;
            case R.id.iv_billpending_toast://提示
                float[] toastMoney = billPendingAdapter.getToast();
                String toastStr = "本金：" + MyAccountFragment.moneyDecimalFormat.format(toastMoney[0]) + "\n" + "服务费：" + MyAccountFragment.moneyDecimalFormat.format(toastMoney[1]) + ((billPendingAdapter.isAllSY && toastMoney[2] != 0.0f) ? ("\n违约金：" + MyAccountFragment.moneyDecimalFormat.format(toastMoney[2])) : "");
                Toast.makeText(mContext, toastStr, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Subscriber(tag = EventBusConstants.EVENTT_UPDATE_BILLPAYRESULT_STATUS, mode = ThreadMode.MAIN)
    public void updatePay(Object params) {
        refreshLayout.startRefresh();
    }

    /**
     * 账单支付
     */
    public void payBill() {
        if (billPendingAdapter.isFormless()) {
            Toast.makeText(mContext, R.string.str_billpay_isformless, Toast.LENGTH_LONG).show();
            return;
        }
        String billIds = "";
        for (int i = 0; i < billPendingAdapter.getData().size(); i++) {
            if (billPendingAdapter.getData().get(i).isCheck()) {
                if (!TextUtils.isEmpty(billIds)) {
                    billIds = billIds + ",";
                } else {
                    billIds = billIds + "";
                }
                billIds = billIds + billPendingAdapter.getData().get(i).getBillIds();
            }
        }
        BillHelper.payBillInstalmentDialog(getActivity(), this.totalMoney, billIds, new IBillPendingCallBack.IPayCallBack() {
            @Override
            public void onPayResult(int payResult, String msg) {
                EventBus.getDefault().post(new Object(), EventBusConstants.EVENTT_UPDATE_BILLPAYRESULT_STATUS);
            }
        });
    }
}
