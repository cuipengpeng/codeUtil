package com.jfbank.qualitymarket.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.adapter.MyRedPacketAdapter;
import com.jfbank.qualitymarket.base.BaseActivity;
import com.jfbank.qualitymarket.model.CouponBeqan;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.UserUtils;
import com.jfbank.qualitymarket.widget.LoadingAlertDialog;
import com.jfbank.qualitymarket.widget.TwinklingRefreshLayoutView;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 功能：我的红包 <br>
 * 作者：赵海 <br>
 * 时间：2016年10月19日 下午3:38:27 <br>
 * 版本：1.1.0<br>
 */
public class MyRedPacketActivity extends BaseActivity {
    @InjectView(R.id.rl_title)
    RelativeLayout rlTitle;
    @InjectView(R.id.tv_title)
    TextView tvTitle;
    @InjectView(R.id.tv_right_menu)
    TextView tvRightMmenu;
    @InjectView(R.id.btn_redpacket_nouse)
    Button btn_redpacket_nouse;
    @InjectView(R.id.plv_myredpacket)
    ListView plvMyPacket;
    @InjectView(R.id.refreshLayout)
    TwinklingRefreshLayoutView refreshLayout;
    @InjectView(R.id.iv_empty)
    ImageView iv_empty;
    MyRedPacketAdapter adapter;
    int PageCount = 1; // 总页数
    int PageNo = 1; // 页数
    Intent intent = null;
    String fromType = null;
    /**
     * 网编请求时加载框
     */
    private LoadingAlertDialog mDialog;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_myredpacket);
        ButterKnife.inject(this);
        adapter = new MyRedPacketAdapter(this);
        tvRightMmenu.setText("红包说明");
        tvRightMmenu.setTextSize(14f);
        tvRightMmenu.setVisibility(View.VISIBLE);
        tvRightMmenu.setCompoundDrawables(null, null, null, null);
        CommonUtils.setTitle(this, rlTitle);
        refreshLayout.setAutoLoadMore(true);
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setOverScrollBottomShow(true);
        refreshLayout.setEnableOverScroll(true);

        //下拉刷新和上啦加载更多
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {//下拉刷新
                getRedPacket(1);
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {//加载更多
                getRedPacket(PageNo + 1);
            }
        });
        plvMyPacket.setAdapter(adapter);
        intent = getIntent();
        fromType = intent.getStringExtra(ConfirmOrderActivity.KEY_OF_RED_PACKET);// ConfirmOrderActivity.COUPON_NO_DATA;//
        if (TextUtils.equals(fromType, ConfirmOrderActivity.COUPON_NO_DATA)) {
            btn_redpacket_nouse.setVisibility(View.VISIBLE);
            tvTitle.setText(R.string.str_pagename_redpacket);
        } else {
            btn_redpacket_nouse.setVisibility(View.GONE);
            tvTitle.setText(R.string.str_pagename_myredpacket);
        }
        if (null == mDialog) {
            mDialog = new LoadingAlertDialog(this);
        }
        mDialog.show("网络请求中...");
        getRedPacket(1);

    }

    @Override
    protected String getPageName() {
        if (TextUtils.equals(fromType, ConfirmOrderActivity.COUPON_NO_DATA)) {
            return getString(R.string.str_pagename_redpacket);
        } else {
            return getString(R.string.str_pagename_myredpacket);
        }
    }

    /**
     * 获取红包
     */
    private void getRedPacket(final int pageNo) {
        Map<String, String> params = new HashMap<>();
        params.put("uid", AppContext.user.getUid());
        if (TextUtils.equals(fromType, ConfirmOrderActivity.COUPON_NO_DATA)) {
            params.put("productNo", intent.getStringExtra(ConfirmOrderActivity.KEY_OF_UPCATEGORY_TYPE));
            params.put("token", AppContext.user.getToken());
            params.put("isActivity", intent.getStringExtra(ConfirmOrderActivity.KEY_OF_IS_ACTIVITY));
            params.put("firstPayment", intent.getStringExtra(ConfirmOrderActivity.KEY_OF_FIRSTPAYMENT));
            params.put("rates", intent.getIntExtra(ConfirmOrderActivity.KEY_OF_RATE, 100) + "");
        } else {
            params.put("token", AppContext.user.getToken());
            params.put("PageNo", pageNo + "");
        }
        params.put("mobile", AppContext.user.getMobile());
        HttpRequest.post(mContext,
                HttpRequest.QUALITY_MARKET_WEB_URL + (TextUtils.equals(fromType, ConfirmOrderActivity.COUPON_NO_DATA)
                        ? HttpRequest.GET_NOTUSEREDBAG : HttpRequest.GET_MYCOUPONSHOW),
                params, new AsyncResponseCallBack() {

                    @Override
                    public void onResult(String arg2) {
                        if (mDialog != null && mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        String jsonStr = new String(arg2);
                        if (TextUtils.isEmpty(jsonStr)) {
                            Toast.makeText(MyRedPacketActivity.this, "加载失败，下拉重试", Toast.LENGTH_SHORT).show();
                            plvMyPacket.setEmptyView(iv_empty);
                        } else {
                            JSONObject jsonObject = JSON.parseObject(jsonStr);
                            if (ConstantsUtil.RESPONSE_SUCCEED == jsonObject
                                    .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                                com.alibaba.fastjson.JSONArray productJsonObject = jsonObject
                                        .getJSONArray(ConstantsUtil.RESPONSE_DATA_JSON_ARRAY_FIELD_NAME);
                                List<CouponBeqan> couponList = JSON.parseArray(productJsonObject.toString(),
                                        CouponBeqan.class);
                                if (!TextUtils.equals(fromType, ConfirmOrderActivity.COUPON_NO_DATA)) {// 我的红包需要分页
                                    PageCount = jsonObject.getIntValue("pageCount");
                                    PageNo = jsonObject.getIntValue("pageNo");
                                    if (PageNo < PageCount) {
                                        refreshLayout.setEnableLoadmore(true);
                                    } else {
                                        refreshLayout.setEnableLoadmore(false);
                                    }
                                }
                                adapter.updateData(couponList, PageNo == 1 ? true : false);
                            } else if (ConstantsUtil.RESPONSE_NO_DATA == jsonObject
                                    .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                                plvMyPacket.setEmptyView(iv_empty);
                            } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == jsonObject
                                    .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                                UserUtils.tokenFailDialog(mContext, jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), null);
                            } else {
                                plvMyPacket.setEmptyView(iv_empty);
                                Toast.makeText(MyRedPacketActivity.this,
                                        jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (pageNo == 1) {
                            refreshLayout.finishRefreshing();
                        } else {
                            refreshLayout.finishLoadmore();
                        }
                    }

                    @Override
                    public void onFailed(String path, String msg) {
                        if (mDialog != null && mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        if (pageNo == 1) {
                            refreshLayout.finishRefreshing();
                        } else {
                            refreshLayout.finishLoadmore();
                        }
                        plvMyPacket.setEmptyView(iv_empty);
                        Toast.makeText(MyRedPacketActivity.this, "加载失败，下拉重试", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @OnClick({R.id.iv_back, R.id.tv_right_menu, R.id.btn_redpacket_nouse})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:// 返回
                finish();
                break;
            case R.id.tv_right_menu:// 红包说明
                CommonUtils.startWebViewActivity(this, HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.H5_PAGE_SUB_URL + HttpRequest.H5_PAGE_RED_PACKET_DESC, false, false);
                break;
            case R.id.btn_redpacket_nouse:// 不适用红包
                intent.putExtra(ConfirmOrderActivity.KEY_OF_RED_PACKET, ConfirmOrderActivity.COUPON_NO_DATA);
                setResult(1, intent);
                finish();
                break;
            default:
                break;
        }

    }

    /**
     * 点击选择红包
     *
     * @param position
     */
    public void onItemClick(int position) {
        if (TextUtils.equals(fromType, ConfirmOrderActivity.COUPON_NO_DATA)) {
            if (adapter.getData().get(position).getUseStatus() == 2) {
                intent.putExtra(ConfirmOrderActivity.KEY_OF_RED_PACKET, "");
                intent.putExtra(ConfirmOrderActivity.KEY_OF_RED_PACKET_MODEL, adapter.getData().get(position));
                setResult(1, intent);
                finish();
            } else {
                Toast.makeText(MyRedPacketActivity.this, "请选择可用红包", Toast.LENGTH_SHORT).show();
            }

        }
    }

}
