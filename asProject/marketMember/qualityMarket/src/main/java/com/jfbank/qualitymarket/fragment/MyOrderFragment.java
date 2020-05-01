package com.jfbank.qualitymarket.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.activity.ConfirmOrderActivity;
import com.jfbank.qualitymarket.activity.LogisticsDetailsActivity;
import com.jfbank.qualitymarket.activity.MyOrderActivity;
import com.jfbank.qualitymarket.activity.OrderDetailActivity;
import com.jfbank.qualitymarket.activity.PayActivity;
import com.jfbank.qualitymarket.base.BaseFragment;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
import com.jfbank.qualitymarket.model.Order;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.LogUtil;
import com.jfbank.qualitymarket.util.StringUtil;
import com.jfbank.qualitymarket.util.UserUtils;
import com.jfbank.qualitymarket.widget.LoadingAlertDialog;
import com.jfbank.qualitymarket.widget.TwinklingRefreshLayoutView;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 我的订单fragment
 *
 * @author 崔朋朋
 */

public class MyOrderFragment extends BaseFragment {
    public static final String TAG = MyAccountFragment.class.getName();
    @InjectView(R.id.iv_myOrderFragment_noData)
    ImageView noDataImageView;

    private List<Order> allOrderList = new ArrayList<Order>();
    private MyOrderAdapter allOrdersAdapter;
    private String orderStatus;
    private int pageNo = 1;
    private int pageSize = 10;
    private int pageCount = -1;
    //防止当前页码在中间，用户上拉、下拉刷新时重复加载数据
    private int headPageNo = 1;//记录当前orderList中的顶部页码
    private int bottomPageNo = 1;//记录当前orderList中的底部页码
    //上拉刷新的基页码
    private final int BASE_PAGE_NO = 1;
    /**
     * 网编请求时加载框
     */
    private LoadingAlertDialog mDialog;
    /**
     * list
     */
    private ListView myOrderListView;
    private TwinklingRefreshLayoutView refreshLayout;
    public static final String KEY_OF_ORDER_ID = "orderIdKey";

    public static MyOrderFragment newInstance(String content) {
        MyOrderFragment fragment = new MyOrderFragment();
        fragment.allOrderList.clear();
        fragment.orderStatus = content;
        return fragment;
    }


    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_order, container, false);
        ButterKnife.inject(this, view);

        allOrderList.clear();
        myOrderListView = (ListView) view.findViewById(R.id.lv_myOrderFragment_myOrder);
        allOrdersAdapter = new MyOrderAdapter(getActivity(), allOrderList);
        myOrderListView.setAdapter(allOrdersAdapter);
        myOrderListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
                intent.putExtra(KEY_OF_ORDER_ID, allOrderList.get(position).getOrderId());
                startActivity(intent);
            }
        });
        refreshLayout =(TwinklingRefreshLayoutView)view.findViewById(R.id.refreshLayout);
        refreshLayout.setAutoLoadMore(true);
        refreshLayout.setEnableLoadmore(true);
        refreshLayout.setOverScrollBottomShow(true);

        //下拉刷新和上啦加载更多
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {//下拉刷新
                pageNo = 1;
                headPageNo = pageNo;
                getOrderList(true, true,  false);
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {//加载更多
                if (pageNo < pageCount && (pageCount * pageSize) > allOrderList.size() && bottomPageNo < pageCount) {
                    pageNo += 1;
                    bottomPageNo = pageNo;
                    getOrderList(false, false, false);
                } else {
                    cancelRefreshView( false);
                }
            }
        });
        noDataImageView.setVisibility(View.GONE);
        refreshLayout.setVisibility(View.VISIBLE);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
//    	pageNo = currentMaxPageNo;
//    	pageNo = 1;
        headPageNo = pageNo;
        bottomPageNo = pageNo;
        getOrderList(true, false,true);
    }

    @Override
    public String getPageName() {
        return getString(R.string.str_pagename_myorder_frag);
    }

    /**
     * 取消下拉刷新的view
     *
     */
    private void cancelRefreshView( final boolean pullDownToRefresh) {
        if (!pullDownToRefresh) {
            Toast.makeText(getActivity(), ConstantsUtil.NO_MORE_DATA_PROMPT, Toast.LENGTH_SHORT).show();
        }
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                allOrdersAdapter.notifyDataSetChanged();
                refreshLayout.finishLoadmore();
            }
        }, 500);
    }


    /**
     * 获取订单列表
     *
     * @param initRequest         是否是初始化请求
     * @param pullDownToRefresh   是否是下拉刷新
     * @param dialog              是否显示网络加载框
     */
    private void getOrderList(final boolean initRequest, final boolean pullDownToRefresh, boolean dialog) {
        if (null == mDialog) {
            mDialog = new LoadingAlertDialog(getActivity());
        }
        if (dialog)
            mDialog.show(ConstantsUtil.NETWORK_REQUEST_IN);

        Map<String,String> params = new HashMap<>();
        params.put("uid", AppContext.user.getUid());
        params.put("token", AppContext.user.getToken());
        params.put("orderStatus", orderStatus);
        params.put("pageNo", pageNo+"");

        HttpRequest.post(mContext, HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.GET_ALL_ORDERS, params, new AsyncResponseCallBack() {

            @Override
            public void onFailed(String path, String msg) {
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                if (pullDownToRefresh||initRequest){
                    refreshLayout.finishRefreshing();
                }else{
                    refreshLayout.finishLoadmore();
                }
                if (!((MyOrderActivity) getActivity()).init) {
                    Toast.makeText(MyOrderFragment.this.getActivity(), ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER, Toast.LENGTH_SHORT).show();
                }
                ((MyOrderActivity) getActivity()).init = false;
            }

            @Override
            public void onResult(String arg2) {
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }

                String jsonStr = new String(arg2);
                LogUtil.printLog("获取订单：" + jsonStr);

                JSONObject jsonObject = JSON.parseObject(jsonStr);

                if (initRequest) {
                    allOrderList.clear();
                }
                if (ConstantsUtil.RESPONSE_TOKEN_FAIL == jsonObject.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                    if (!((MyOrderActivity) getActivity()).init) {
                        ((MyOrderActivity) getActivity()).init = false;
                        UserUtils.tokenFailDialog(MyOrderFragment.this.getActivity(), jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), TAG);
                    }
                } else if ((ConstantsUtil.RESPONSE_SUCCEED == jsonObject.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) || allOrderList.size() > 0) {
                    pageCount = jsonObject.getIntValue("pageCount");
                    if (jsonObject.containsKey(ConstantsUtil.RESPONSE_DATA_JSON_ARRAY_FIELD_NAME)) {
                        JSONArray orderJsonArray = jsonObject.getJSONArray(ConstantsUtil.RESPONSE_DATA_JSON_ARRAY_FIELD_NAME);
                        Order order = null;
                        for (int i = 0; i < orderJsonArray.size(); i++) {
                            order = JSON.parseObject(orderJsonArray.get(i).toString(), Order.class);
                            if (pullDownToRefresh) {
                                allOrderList.add(i, order);
                            } else {
                                allOrderList.add(order);
                            }
                        }
                    } else {
                        if (!pullDownToRefresh) {
                            Toast.makeText(getActivity(), ConstantsUtil.NO_MORE_DATA_PROMPT, Toast.LENGTH_SHORT).show();
                        }
                    }
                    allOrdersAdapter.notifyDataSetChanged();
                } else if ((ConstantsUtil.RESPONSE_NO_DATA == jsonObject.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) && allOrderList.size() <= 0) {
                    noDataImageView.setVisibility(View.VISIBLE);
                    refreshLayout.setVisibility(View.GONE);
                } else {
                    Toast.makeText(MyOrderFragment.this.getActivity(), "查询订单失败", Toast.LENGTH_SHORT).show();
                }

                ((MyOrderActivity) getActivity()).init = false;
                if (pullDownToRefresh||initRequest){
                    refreshLayout.finishRefreshing();
                }else{
                    refreshLayout.finishLoadmore();
                }
            }
        });
    }


    class MyOrderAdapter extends BaseAdapter {
        private List<Order> orderList;
        private Activity activity;

        public MyOrderAdapter(Activity activity, List<Order> orderList) {
            super();
            this.orderList = orderList;
            this.activity = activity;
        }

        @Override
        public int getCount() {
            return orderList.size();
        }

        @Override
        public Object getItem(int position) {
            return orderList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.order_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final Order order = orderList.get(position);
            Picasso.with(getActivity()).load(order.getProductImage()).placeholder(R.drawable.ic_launcher).into(viewHolder.productImageView);

            viewHolder.orderStatusTextView.setText(MyOrderActivity.getOrderStatus(order.getOrderStatus()));
            if (MyOrderActivity.ORDER_STATUS_WAIT_FOR_PAY.equals(order.getOrderStatus())) {
                viewHolder.paidDownPaymentTextView.setVisibility(View.VISIBLE);
                viewHolder.viewLogisticsTextView.setVisibility(View.GONE);
                viewHolder.applyAfterSaleTextView.setVisibility(View.GONE);
            } else if (MyOrderActivity.ORDER_STATUS_WAIT_FOR_TAKE_OVER_GOODS.equals(order.getOrderStatus())) {
                viewHolder.paidDownPaymentTextView.setVisibility(View.GONE);
                viewHolder.viewLogisticsTextView.setVisibility(View.VISIBLE);
                viewHolder.applyAfterSaleTextView.setVisibility(View.GONE);
                //101为话费充值，102为流量充值 虚拟商品没有查看物流
                if (StringUtil.notEmpty(order.getProductType()) && (Integer.valueOf(order.getProductType()) == 101 || Integer.valueOf(order.getProductType()) == 102||Integer.valueOf(order.getProductType()) == 106)) {
                    viewHolder.viewLogisticsTextView.setVisibility(View.GONE);
                }
            } else if (MyOrderActivity.ORDER_STATUS_FINISHED.equals(order.getOrderStatus())) {
                viewHolder.paidDownPaymentTextView.setVisibility(View.GONE);
                viewHolder.viewLogisticsTextView.setVisibility(View.GONE);
                //按产品要求，隐藏"订单列表"和"订单详情"页面的"申请售后"按钮,  退换货仅有1个入口
                viewHolder.applyAfterSaleTextView.setVisibility(View.GONE);
            } else {
                viewHolder.paidDownPaymentTextView.setVisibility(View.GONE);
                viewHolder.viewLogisticsTextView.setVisibility(View.GONE);
                viewHolder.applyAfterSaleTextView.setVisibility(View.GONE);
            }

            viewHolder.productPriceTextView.setText("一次性付: " + MyAccountFragment.moneyDecimalFormat.format(order.getProductPrice()) + "元");
            viewHolder.productNameTextView.setText(order.getProductName());
            viewHolder.spuparamsTextView.setText(order.getSkuParameters());
            String productAmount = "共1件商品";
            String downPayment = "首付:" + MyAccountFragment.moneyDecimalFormat.format(order.getFirstPayment()) + "元";
            String installment = "月付:" + MyAccountFragment.moneyDecimalFormat.format(order.getMonthPay()) + "*" + order.getMonthNum() + "期";
            SpannableString productAmountStyledText = new SpannableString(productAmount);
            productAmountStyledText.setSpan(new TextAppearanceSpan(getActivity(), R.style.my_order_style_gray), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            productAmountStyledText.setSpan(new TextAppearanceSpan(getActivity(), R.style.my_order_style_black), 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            productAmountStyledText.setSpan(new TextAppearanceSpan(getActivity(), R.style.my_order_style_gray), 2, productAmount.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            SpannableString downPaymentStyledText = new SpannableString(downPayment);
            downPaymentStyledText.setSpan(new TextAppearanceSpan(getActivity(), R.style.my_order_style_gray), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            downPaymentStyledText.setSpan(new TextAppearanceSpan(getActivity(), R.style.my_order_style_black), 3, downPayment.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            downPaymentStyledText.setSpan(new TextAppearanceSpan(getActivity(), R.style.my_order_style_gray), downPayment.length() - 1, downPayment.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            SpannableString installmentStyledText = new SpannableString(installment);
            installmentStyledText.setSpan(new TextAppearanceSpan(getActivity(), R.style.my_order_style_gray), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            installmentStyledText.setSpan(new TextAppearanceSpan(getActivity(), R.style.my_order_style_black), 3, installment.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            installmentStyledText.setSpan(new TextAppearanceSpan(getActivity(), R.style.my_order_style_gray), installment.length() - 1, installment.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder.productAmountTextView.setText(productAmountStyledText, TextView.BufferType.SPANNABLE);
            viewHolder.downPaymentTextView.setText(downPaymentStyledText, TextView.BufferType.SPANNABLE);
            viewHolder.installmentTextView.setText(installmentStyledText, TextView.BufferType.SPANNABLE);
            viewHolder.paidDownPaymentTextView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(activity, PayActivity.class);
                    String productPrice = MyAccountFragment.moneyDecimalFormat.format(order.getProductPrice()) + "元";
                    intent.putExtra(ConfirmOrderActivity.KEY_OF_PRODUCT_NAME, order.getProductName());
                    intent.putExtra(ConfirmOrderActivity.KEY_OF_PRODUCT_IMAGE_URL, order.getProductImage());
                    intent.putExtra(ConfirmOrderActivity.KEY_OF_PRODUCT_PRICE, "一次性付: " + productPrice);
                    intent.putExtra(ConfirmOrderActivity.KEY_OF_PRODUCT_SKUPARAM, order.getSkuParameters());
                    intent.putExtra(ConfirmOrderActivity.KEY_OF_DOWNPAYMENT_PRICE, MyAccountFragment.moneyDecimalFormat.format(order.getFirstPayment()) + "元");
                    intent.putExtra(ConfirmOrderActivity.KEY_OF_DOWNPAYMENT_RATE, Integer.valueOf(order.getRates()));
                    intent.putExtra(ConfirmOrderActivity.KEY_OF_INSTALLMENT, MyAccountFragment.moneyDecimalFormat.format(order.getMonthPay()) + "元");
                    intent.putExtra(ConfirmOrderActivity.KEY_OF_ORDER_NO, order.getOrderId());
                    intent.putExtra(ConfirmOrderActivity.KEY_OF_PRODUCT_NO, order.getProductNo());
                    intent.putExtra(ConfirmOrderActivity.KEY_OF_ORDER_MOENY, productPrice);
                    if (StringUtil.isNull(order.getRedbagValue()) || Double.valueOf(order.getRedbagValue()) == 0) {
                        intent.putExtra(ConfirmOrderActivity.KEY_OF_COUPON_MOENY, "");
                        intent.putExtra(ConfirmOrderActivity.KEY_OF_ACTUAL_MOENY, productPrice);
                    } else {
                        //使用红包
                        intent.putExtra(ConfirmOrderActivity.KEY_OF_COUPON_MOENY, order.getRedbagValue() + "元");
                        intent.putExtra(ConfirmOrderActivity.KEY_OF_ACTUAL_MOENY, order.getActualAmount() + "元");
                    }
                    activity.startActivity(intent);
                }
            });
            viewHolder.viewLogisticsTextView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(activity, LogisticsDetailsActivity.class);
                    intent.putExtra(KEY_OF_ORDER_ID, order.getOrderId());
                    activity.startActivity(intent);
                }
            });
            viewHolder.applyAfterSaleTextView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    OrderDetailActivity.applyAfterSale(activity);
                }
            });


            return convertView;
        }

    }

    static class ViewHolder {
        @InjectView(R.id.tv_myOrderActivity_orderItem_orderStatus)
        TextView orderStatusTextView;
        @InjectView(R.id.iv_myOrderActivity_orderItem_productImage)
        ImageView productImageView;
        @InjectView(R.id.tv_myOrderActivity_orderItem_productName)
        TextView productNameTextView;
        @InjectView(R.id.tv_myOrderActivity_orderItem_productPrice)
        TextView productPriceTextView;
        @InjectView(R.id.tv_myOrderActivity_orderItem_productAmount)
        TextView productAmountTextView;
        @InjectView(R.id.tv_myOrderActivity_orderItem_downPaymentPrice)
        TextView downPaymentTextView;
        @InjectView(R.id.tv_myOrderActivity_orderItem_installment)
        TextView installmentTextView;
        @InjectView(R.id.tv_myOrderActivity_orderItem_paidDownPayment)
        TextView paidDownPaymentTextView;
        @InjectView(R.id.tv_myOrderActivity_orderItem_viewLogistics)
        TextView viewLogisticsTextView;
        @InjectView(R.id.tv_myOrderActivity_orderItem_applyAfterSale)
        TextView applyAfterSaleTextView;
        @InjectView(R.id.tv_myOrderActivity_orderItem_spuparams)
        TextView spuparamsTextView;

        public ViewHolder(View v) {
            super();
            ButterKnife.inject(this, v);
        }

    }

    /**
     * 获取待支付订单的剩余时间
     *
     * @param order
     * @return
     */
    public static int getOrderRemainTime(Order order) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long usageTime = 0;
        long totalTime = 0;
        //秒杀商品倒计时15分钟      非秒杀商品倒计时24小时
        //1为秒杀商品  0为非秒杀商品
        if ("0".equals(order.getMiaosha())) {
            totalTime = 60 * 60 * 24 * 1000;
        } else if ("1".equals(order.getMiaosha())) {
            totalTime = 60 * 15 * 1000;
        }

//		usageTime = Long.valueOf(order.getCurrentTime()) - Long.valueOf(order.getOrderTime());

        try {
            usageTime = simpleDateFormat.parse(order.getCurrentTime()).getTime() - simpleDateFormat.parse(order.getOrderTime()).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //倒计时时间  单位：秒
        int countDownTime = (int) ((totalTime - usageTime) / 1000);
        return countDownTime;
    }

}
