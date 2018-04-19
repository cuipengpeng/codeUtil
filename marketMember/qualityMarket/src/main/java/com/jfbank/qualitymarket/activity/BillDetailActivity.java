package com.jfbank.qualitymarket.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.adapter.BillDetailAdapter;
import com.jfbank.qualitymarket.base.BaseActivity;
import com.jfbank.qualitymarket.callback.IBillPendingCallBack;
import com.jfbank.qualitymarket.constants.EventBusConstants;
import com.jfbank.qualitymarket.fragment.MyAccountFragment;
import com.jfbank.qualitymarket.helper.BillHelper;
import com.jfbank.qualitymarket.listener.IMeterialClickLisenter;
import com.jfbank.qualitymarket.model.BillAgingBean;
import com.jfbank.qualitymarket.model.BillBean;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.LogUtil;
import com.jfbank.qualitymarket.util.UserUtils;
import com.jfbank.qualitymarket.widget.NoScrollListView;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
 import java.util.HashMap; import java.util.Map;

import org.apache.http.Header;
import org.simple.eventbus.EventBus;

/**
 * 功能：分期详情<br>
 * 作者：赵海<br>
 * 时间： 2016/12/30 0030<br>.
 * 版本：1.2.0
 */

public class BillDetailActivity extends BaseActivity implements IMeterialClickLisenter, IBillPendingCallBack.IAdapterCallBack {
    private static final String TAG = "BillDetailActivity";
    private RelativeLayout rlTitle;
    private TextView tvTitle;
    private ImageView ivBack;
    private ScrollView svBilldetail;
    private TextView tvBillTitle;
    private TextView tvBillTime;
    private TextView tvBillId;
    private TextView tvBillCapital;
    private TextView tvBillServicemoney;
    private TextView tvBillTotal;
    private TextView tvBilldetailPaydes;
    private NoScrollListView nslvBilldetail;
    private LinearLayout llFooterPay;
    private ImageView ivBillpendingCheck;
    private TextView tvBillpendingDes;
    private TextView tvBillpendingMoney;
    private ImageView ivBillpendingToast;
    private TextView tvPayInstant;
    private BillDetailAdapter billDetailAdapter;
    private String orderId;
    private String totalMoney = "0.0";

    @Override
    protected String getPageName() {
        return getString(R.string.str_pagename_billdetail);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billdetail);
        initView();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    /**
     * 组件初始化
     */
    private void initView() {
        rlTitle = (RelativeLayout) findViewById(R.id.rl_title);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        tvBillTitle = (TextView) findViewById(R.id.tv_bill_title);
        tvBillTime = (TextView) findViewById(R.id.tv_bill_time);
        tvBillId = (TextView) findViewById(R.id.tv_bill_id);
        tvBillCapital = (TextView) findViewById(R.id.tv_bill_capital);
        svBilldetail = (ScrollView) findViewById(R.id.sv_billdetail);

        tvBillServicemoney = (TextView) findViewById(R.id.tv_bill_servicemoney);
        tvBillTotal = (TextView) findViewById(R.id.tv_bill_total);
        tvBilldetailPaydes = (TextView) findViewById(R.id.tv_billdetail_paydes);
        nslvBilldetail = (NoScrollListView) findViewById(R.id.nslv_billdetail);
        llFooterPay = (LinearLayout) findViewById(R.id.ll_footer_pay);
        ivBillpendingCheck = (ImageView) findViewById(R.id.iv_billpending_check);
        tvBillpendingDes = (TextView) findViewById(R.id.tv_billpending_des);
        tvBillpendingMoney = (TextView) findViewById(R.id.tv_billpending_money);
        ivBillpendingToast = (ImageView) findViewById(R.id.iv_billpending_toast);
        tvPayInstant = (TextView) findViewById(R.id.tv_pay_instant);
        billDetailAdapter = new BillDetailAdapter(this, this);
        nslvBilldetail.setAdapter(billDetailAdapter);
        tvTitle.setText(R.string.str_pagename_billdetail);
        CommonUtils.makeMeterial(ivBack, this);
        CommonUtils.makeMeterial(ivBillpendingCheck, this);
        CommonUtils.makeMeterial(tvPayInstant, this);
        CommonUtils.makeMeterial(ivBillpendingToast, this);
        CommonUtils.setTitle(this, rlTitle);
        orderId = getIntent().getStringExtra("orderId");
        getData();
    }

    @Override
    public void onMetrialClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back://返回按钮
                finish();
                break;
            case R.id.iv_billpending_check://选择所有账单
                selectAll();
                break;
            case R.id.tv_pay_instant://立即支付
                payBill();
                break;
            case R.id.iv_billpending_toast://提示
                float[] toastMoney = billDetailAdapter.getToast();
                String toastStr = "本金：" + MyAccountFragment.moneyDecimalFormat.format(toastMoney[0]) + "\n" + "服务费：" + MyAccountFragment.moneyDecimalFormat.format(toastMoney[1]) + ((billDetailAdapter.isAllSY && toastMoney[2] != 0.0f) ? ("\n违约金：" + MyAccountFragment.moneyDecimalFormat.format(toastMoney[2])) : "");
                Toast.makeText(this, toastStr, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * 选择所有分期账单
     */
    private void selectAll() {
        boolean isCheck = ivBillpendingCheck.isSelected();
        for (int i = 0; i < billDetailAdapter.getData().size(); i++) {
            if (billDetailAdapter.getData().get(i).isCheckEnable()) {
                if (isCheck) {
                    billDetailAdapter.getData().get(i).setCheckAll(false);
                } else {
                    billDetailAdapter.getData().get(i).setCheckAll(true);
                }
                billDetailAdapter.getData().get(i).setCheck(!isCheck);
            }

        }
        billDetailAdapter.notifyDataSetChanged();
    }

    /**
     * 获取首页数据
     */
    private void getData() {
        Map<String,String> params = new HashMap<>();
        params.put("orderId", orderId);
        params.put("uid", AppContext.user.getUid());
        params.put("token", AppContext.user.getToken());
        HttpRequest.post(mContext,HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.QUERYINSTALLMENT_DETAIL, params, new AsyncResponseCallBack() {
            @Override
            public void onResult(String responseStr)  {
                String jsonStr = new String(responseStr);
                JSONObject jsonObject = JSON.parseObject(jsonStr);
                Log.e(TAG, jsonStr);
                //
                if ((ConstantsUtil.RESPONSE_SUCCEED == jsonObject.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME))) {
                    try {
                        BillAgingBean billAgingBean = JSON.parseObject(jsonObject.toJSONString(), BillAgingBean.class);
                        updateView(billAgingBean);
                    } catch (Exception e) {
                        LogUtil.printLog(e.getMessage() + "");
                        Log.e(TAG, e.getMessage() + "");
                    }
                } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == jsonObject.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                    UserUtils.tokenFailDialog(mContext, jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), TAG);
                } else {
                    Toast.makeText(BillDetailActivity.this, jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME) + "", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed(String path, String msg){
                Toast.makeText(BillDetailActivity.this, R.string.error_net, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 更新分期详情UI
     *
     * @param data
     */
    private void updateView(BillAgingBean data) {
        tvBillTitle.setText(CommonUtils.toDBC(data.getProductName()));
        tvBillTime.setText(data.getOrderTime());
        tvBillId.setText(data.getOrderId());
        tvBillCapital.setText(data.getFenqibenjin());
        tvBillServicemoney.setText(data.getFenqifuwufei());
        tvBillTotal.setText(data.getHejiyinghuan());
        tvBilldetailPaydes.setText("已还" + data.getYihuan() + "元， 未还" + data.getWeihuan() + "元");
        billDetailAdapter.updateData(data.getData());
    }

    /**
     * 账单支付
     */
    public void payBill() {
        if (billDetailAdapter.hasFormless()) {
            Toast.makeText(this, R.string.str_billpay_isformless, Toast.LENGTH_LONG).show();
            return;
        }
        String billIds = "";
        for (int i = 0; i < billDetailAdapter.getData().size(); i++) {
            if (billDetailAdapter.getData().get(i).isCheck()) {
                if (!TextUtils.isEmpty(billIds)) {
                    billIds = billIds + ",";
                } else {
                    billIds = billIds + "";
                }
                billIds = billIds + billDetailAdapter.getData().get(i).getBillIds();
            }
        }
        BillHelper.payBillInstalmentDialog(this, this.totalMoney, billIds, new IBillPendingCallBack.IPayCallBack() {
            @Override
            public void onPayResult(int payResult, String msg) {
                EventBus.getDefault().post(new Object(), EventBusConstants.EVENTT_UPDATE_BILLPAYRESULT_STATUS);
                finish();
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
                        svBilldetail.setPadding(0, 0, 0, CommonUtils.dipToPx(BillDetailActivity.this, 56));
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
                        svBilldetail.setPadding(0, 0, 0, 0);
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
}
