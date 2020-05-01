package com.jfbank.qualitymarket.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.base.BaseActivity;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
import com.jfbank.qualitymarket.fragment.MyAccountFragment;
import com.jfbank.qualitymarket.fragment.MyOrderFragment;
import com.jfbank.qualitymarket.model.Order;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.util.ActivityManager;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.CountDownTask;
import com.jfbank.qualitymarket.util.LogUtil;
import com.jfbank.qualitymarket.util.StringUtil;
import com.jfbank.qualitymarket.util.ToastUtil;
import com.jfbank.qualitymarket.util.UserUtils;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 订单详情页面
 *
 * @author 崔朋朋
 */
public class OrderDetailActivity extends BaseActivity {
    public static final String TAG = OrderDetailActivity.class.getName();
    @InjectView(R.id.rl_title)
    RelativeLayout rlTitle;
    @InjectView(R.id.tv_title)
    TextView tvTitle;
    //手机充值
    @InjectView(R.id.rl_orderDetailActivity_topUp)
    RelativeLayout topUpRelativeLayout;
    @InjectView(R.id.tv_orderDetailActivity_topUpMobile)
    TextView topUpMobileTextView;
    //收货人地址
    @InjectView(R.id.rl_orderDetailActivity_setConsigneeAddress)
    RelativeLayout setConsigneeAddressRelativeLayout;
    @InjectView(R.id.tv_orderDetailActivity_recipientName)
    TextView recipientNameTextView;
    @InjectView(R.id.tv_orderDetailActivity_recipientMobile)
    TextView recipientMobileTextView;
    @InjectView(R.id.tv_orderDetailActivity_consigneeAddress)
    TextView consigneeAddressTextView;
    //订单状态
    @InjectView(R.id.tv_orderDetailActivity_orderStatus)
    TextView orderStatusTextView;
    @InjectView(R.id.tv_orderDetailActivity_orderNum)
    TextView orderNumTextView;
    //产品详情
    @InjectView(R.id.iv_orderDetailActivity_productImage)
    ImageView productImageView;
    @InjectView(R.id.tv_orderDetailActivity_productName)
    TextView productNameTextView;
    @InjectView(R.id.tv_orderDetailActivity_productPrice)
    TextView productPriceTextView;
    //联系客服
    @InjectView(R.id.rl_orderDetailActivity_contactCustomerService)
    RelativeLayout contactCustomerServiceRelativeLayout;

    //订单信息
    @InjectView(R.id.tv_orderDetailActivity_downPaymentPercent)
    TextView downPaymentPercentTextView;
    //支付方式
    @InjectView(R.id.tv_orderDetailActivity_modeOfPayment)
    TextView modeOfPaymentTextView;
    //发票信息
    @InjectView(R.id.tv_orderDetailActivity_InvoiceInfo)
    TextView invoiceInfoTextView;
    //备注
    @InjectView(R.id.tv_orderDetailActivity_comment)
    TextView commentTextView;

    // 分期月数
    @InjectView(R.id.v_orderDetailActivity_border4_monthOfInstallment)
    View borderMonthOfInstallmentView;
    @InjectView(R.id.tv_orderDetailActivity_textMonthOfInstallment)
    TextView textMonthOfInstallmentTextView;
    @InjectView(R.id.tv_orderDetailActivity_monthOfInstallment)
    TextView monthOfInstallmentTextView;

    //发票信息
    @InjectView(R.id.rl_orderDetailActivity_noInvoiceInfo)
    RelativeLayout noInvoiceInfoRelativeLayout;
    @InjectView(R.id.rl_orderDetailActivity_pagerInvoiceInfo)
    RelativeLayout pagerInvoiceInfoRelativeLayout;
    @InjectView(R.id.tv_orderDetailActivity_setInvoiceInfoType)
    TextView setInvoiceInfoTypeTextView;
    @InjectView(R.id.tv_orderDetailActivity_setInvoiceInfoContent)
    TextView setInvoiceInfoContentTextView;
    @InjectView(R.id.tv_orderDetailActivity_invoiceInfoCompanyName)
    TextView invoiceInfoCompanyNameNameTextView;
    @InjectView(R.id.tv_orderDetailActivity_setInvoiceInfoCompanyName)
    TextView setInvoiceInfoCompanyNameTextView;
    @InjectView(R.id.tv_orderDetailActivity_topUpName)
    TextView tvOrderDetailActivityTopUpName;
    //设置账单日
    @InjectView(R.id.v_orderDetailActivity_border_billingDay)
    View borderBillingDayView;
    @InjectView(R.id.tv_orderDetailActivity_billingDayText)
    TextView billingDayTextTextView;
    @InjectView(R.id.tv_orderDetailActivity_setBillingDay)
    TextView setBillingDayTextView;


    //设置优惠券
    @InjectView(R.id.v_orderDetailActivity_border_coupon)
    View borderCouponDayView;
    @InjectView(R.id.tv_orderDetailActivity_couponText)
    TextView couponTextView;
    @InjectView(R.id.tv_orderDetailActivity_setCoupon)
    TextView setCouponTextView;

    //订单金额  红包金额  实际金额
    @InjectView(R.id.tv_orderDetailActivity_setOrderMoney)
    TextView setOrderMoneyTextView;
    @InjectView(R.id.v_orderDetailActivity_border_redMoney)
    View borderredMoneyDayView;
    @InjectView(R.id.tv_orderDetailActivity_redMoney)
    TextView redMoneyTextView;
    @InjectView(R.id.tv_orderDetailActivity_setRedMoney)
    TextView setRedMoneyTextView;
    @InjectView(R.id.tv_orderDetailActivity_setActualMoney)
    TextView setActualMoneyTextView;

    //首付价格  月供金额  下单时间
    @InjectView(R.id.tv_orderDetailActivity_downPayment)
    TextView downPaymentTextView;
    @InjectView(R.id.tv_orderDetailActivity_downPaymentPrice)
    TextView downPaymentPriceTextView;
    @InjectView(R.id.tv_orderDetailActivity_monthlyPaymentPrice)
    TextView monthlyPaymentPriceTextView;
    @InjectView(R.id.tv_orderDetailActivity_setOrderTime)
    TextView setOrderTimeTextView;

    @InjectView(R.id.ll_orderDetailActivity_applyAfterSale)
    LinearLayout applyAfterSaleLinearLayout;
    @InjectView(R.id.tv_orderDetailActivity_applyAfterSale)
    TextView applyAfterSaleTextView;
    @InjectView(R.id.tv_orderDetailActivity_viewAgreement)
    TextView viewAgreementTextView;
    @InjectView(R.id.tv_orderDetailActivity_repaymentPlan)
    TextView repayMentPlanTextView;

    //倒计时view
    @InjectView(R.id.v_orderDetailActivity_border_countDown)
    View borderCountDownView;
    @InjectView(R.id.rl_orderDetailActivity_countDown)
    RelativeLayout countDownRelativeLayout;
    //支付首付 取消订单   查看物流
    @InjectView(R.id.tv_orderDetailActivity_orderRemainTime)
    TextView orderRemainTimeTextView;
    @InjectView(R.id.tv_orderDetailActivity_setOrderRemainTime)
    TextView setOrderRemainTimeTextView;
    @InjectView(R.id.tv_orderDetailActivity_cancelOrder)
    TextView cancelOrderTextView;
    @InjectView(R.id.tv_orderDetailActivity_payDownPayment)
    TextView payDownPaymentTextView;
    @InjectView(R.id.btn_orderDetailActivity_viewLogistics)
    Button viewLogisticsButton;

    public static final String KEY_OF_ORDER_ID = "orderIdKey";
    @InjectView(R.id.tv_orderDetailActivity_skuparams)
    TextView tvOrderDetailActivitySkuparams;
    private String orderId;
    private Order order;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CountDownTask.COUNT_DOWN_TASK:
                    setOrderRemainTimeTextView.setText(new CountDownTask().getRemainTimeString(msg.arg1 * 1000, (String) msg.obj));

                    CountDownTask.countDwon(msg.arg1, handler, (String) msg.obj);
                    break;
                case CountDownTask.COUNT_DOWN_OVER:
                    //发送取消订单请求
                    setOrderRemainTimeTextView.setText("发送取消订单请求");
                    cancelOrder();
                    break;

            }
        }

        ;
    };


    @OnClick({R.id.btn_orderDetailActivity_viewLogistics, R.id.rl_orderDetailActivity_contactCustomerService,
            R.id.iv_back, R.id.tv_orderDetailActivity_cancelOrder,
            R.id.tv_orderDetailActivity_payDownPayment, R.id.tv_orderDetailActivity_repaymentPlan,
            R.id.tv_orderDetailActivity_applyAfterSale, R.id.tv_orderDetailActivity_viewAgreement})
    public void onItemClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btn_orderDetailActivity_viewLogistics:
                //查看物流
                intent.setClass(this, LogisticsDetailsActivity.class);
                intent.putExtra(KEY_OF_ORDER_ID, orderId);
                startActivity(intent);
                break;
            case R.id.rl_orderDetailActivity_contactCustomerService:
                //查看客服
                String contactCsUrl = HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.H5_PAGE_SUB_URL + HttpRequest.H5_PAGE_CUSTOMER_SERVICE;
                CommonUtils.startWebViewActivity(this, contactCsUrl, false, false);
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_orderDetailActivity_cancelOrder:
                //取消订单
                cancelOrderDialog();
                break;
            case R.id.tv_orderDetailActivity_applyAfterSale:
                //申请售后
                applyAfterSale(this);
                break;
            case R.id.tv_orderDetailActivity_viewAgreement:
                //查看合同
                String protocolUrl = HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.H5_PAGE_SUB_URL + "#/contractList?" + "uid=" + AppContext.user.getUid() + "&orderId=" + orderId + "&token=" + AppContext.user.getToken();
                CommonUtils.startWebViewActivity(this, protocolUrl, true, false);
                break;
            case R.id.tv_orderDetailActivity_repaymentPlan:
                //还款计划
                String repaymentPlanUrl = HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.H5_PAGE_SUB_URL + "#/repaymentPlan?" + "uid=" + AppContext.user.getUid() + "&orderId=" + orderId + "&token=" + AppContext.user.getToken();
                CommonUtils.startWebViewActivity(this, repaymentPlanUrl, false, false);
                break;
            case R.id.tv_orderDetailActivity_payDownPayment:
                ActivityManager.getInstance().addPayActivity(this);
                //立即支付
                String productPrice = MyAccountFragment.moneyDecimalFormat.format(order.getProductPrice()) + "元";
                intent.setClass(this, PayActivity.class);
                intent.putExtra(ConfirmOrderActivity.KEY_OF_PRODUCT_NAME, order.getProductName());
                intent.putExtra(ConfirmOrderActivity.KEY_OF_PRODUCT_PRICE, "一次性付：" + productPrice);
                intent.putExtra(ConfirmOrderActivity.KEY_OF_PRODUCT_IMAGE_URL, order.getProductImage());
                intent.putExtra(ConfirmOrderActivity.KEY_OF_PRODUCT_SKUPARAM, tvOrderDetailActivitySkuparams.getText().toString());
                intent.putExtra(ConfirmOrderActivity.KEY_OF_DOWNPAYMENT_PRICE, MyAccountFragment.moneyDecimalFormat.format(order.getFirstPayment()) + "元");
                intent.putExtra(ConfirmOrderActivity.KEY_OF_DOWNPAYMENT_RATE, Integer.valueOf(order.getRates()));
                intent.putExtra(ConfirmOrderActivity.KEY_OF_INSTALLMENT, MyAccountFragment.moneyDecimalFormat.format(order.getMonthPay()) + "元");
                intent.putExtra(ConfirmOrderActivity.KEY_OF_ORDER_NO, order.getOrderId());
                intent.putExtra(ConfirmOrderActivity.KEY_OF_PRODUCT_NO, order.getProductNo());
                intent.putExtra(ConfirmOrderActivity.KEY_OF_ORDER_MOENY, productPrice);
                if (View.VISIBLE == setRedMoneyTextView.getVisibility()) {
                    //使用红包
                    intent.putExtra(ConfirmOrderActivity.KEY_OF_COUPON_MOENY, setRedMoneyTextView.getText().toString());
                } else {
                    //未使用红包
                    intent.putExtra(ConfirmOrderActivity.KEY_OF_COUPON_MOENY, "");
                }
                intent.putExtra(ConfirmOrderActivity.KEY_OF_ACTUAL_MOENY, setActualMoneyTextView.getText().toString());
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.inject(this);

        Intent intent = getIntent();
        orderId = intent.getStringExtra(MyOrderFragment.KEY_OF_ORDER_ID);
        setConsigneeAddressRelativeLayout.setVisibility(View.GONE);
        topUpRelativeLayout.setVisibility(View.GONE);
        //隐藏账单日
        borderBillingDayView.setVisibility(View.GONE);
        billingDayTextTextView.setVisibility(View.GONE);
        setBillingDayTextView.setVisibility(View.GONE);

        //隐藏“红包”功能
        borderCouponDayView.setVisibility(View.GONE);
        couponTextView.setVisibility(View.GONE);
        setCouponTextView.setVisibility(View.GONE);

//		//隐藏 “红包金额”
        borderredMoneyDayView.setVisibility(View.GONE);
        redMoneyTextView.setVisibility(View.GONE);
        setRedMoneyTextView.setVisibility(View.GONE);


        showPayDownPaymentViews();
        showNoInvoiceInfoView();
        applyAfterSaleLinearLayout.setVisibility(View.GONE);
        countDownRelativeLayout.setVisibility(View.GONE);
        tvTitle.setText(R.string.str_pagename_orderdetail);
        CommonUtils.setTitle(this, rlTitle);

    }

    /**
     * 显示"不开发票"view
     */
    private void showNoInvoiceInfoView() {
        noInvoiceInfoRelativeLayout.setVisibility(View.VISIBLE);
        pagerInvoiceInfoRelativeLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getOrderDetail();
    }

    @Override
    protected String getPageName() {
        return getString(R.string.str_pagename_orderdetail);
    }

    /**
     * 申请售后
     *
     * @param activity
     */
    public static void applyAfterSale(Activity activity) {
        new AlertDialog.Builder(activity)
                .setMessage("售后功能正紧张开发中，如需售后请联系客服处理")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
    }

    /**
     * 获取订单详情
     */
    private void getOrderDetail() {
        Map<String, String> params = new HashMap<>();
        params.put("uid", AppContext.user.getUid());
        params.put("token", AppContext.user.getToken());
        params.put("orderId", orderId);

        HttpRequest.post(mContext, HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.GET_ORDER_DETAIL, params, new AsyncResponseCallBack() {

            @Override
            public void onFailed(String path, String msg) {
                Toast.makeText(OrderDetailActivity.this, ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER + "获取订单详情失败", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onResult(String arg2) {
                String jsonStr = new String(arg2);
                LogUtil.printLog("订单详情：" + jsonStr);

                JSONObject jsonObject = JSON.parseObject(jsonStr);

                if (ConstantsUtil.RESPONSE_SUCCEED == jsonObject.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                    JSONObject orderJsonObject = jsonObject.getJSONObject(ConstantsUtil.RESPONSE_DATA_JSON_ARRAY_FIELD_NAME);

                    order = JSON.parseObject(orderJsonObject.toString(), Order.class);

                    // 空值""和0-100为普通商品，101以后为虚拟商品，其中101为话费充值，102为流量充值
                    if (StringUtil.isNull(order.getProductType()) || (Integer.valueOf(order.getProductType()) >= 0 && Integer.valueOf(order.getProductType()) <= 100)) {
                        //普通商品的收货地址
                        setConsigneeAddressRelativeLayout.setVisibility(View.VISIBLE);
                        topUpRelativeLayout.setVisibility(View.GONE);
                        recipientNameTextView.setText(order.getConsignee() + "   ");
                        recipientMobileTextView.setText(order.getConsigneeMobile());
                        String consigneeAddress = order.getAddProvince() + order.getAddCity() + order.getAddCounty() + order.getAddTown() + order.getAddArea() + order.getAddDetail();
                        consigneeAddressTextView.setText(consigneeAddress);
                    } else if (Integer.valueOf(order.getProductType()) == 101 || Integer.valueOf(order.getProductType()) == 102 || Integer.valueOf(order.getProductType()) == 106) {
                        if (Integer.valueOf(order.getProductType()) == 101 || Integer.valueOf(order.getProductType()) == 102) {
                            tvOrderDetailActivityTopUpName.setText("充值号码：");
                        } else {
                            tvOrderDetailActivityTopUpName.setText("接收短信号码：");
                        }
                        //手机充值 流量充值
                        setConsigneeAddressRelativeLayout.setVisibility(View.GONE);
                        topUpRelativeLayout.setVisibility(View.VISIBLE);
                        topUpMobileTextView.setText(order.getMobile());
                    }

                    //订单号及订单状态
                    orderNumTextView.setText(order.getOrderId());
                    orderNumTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ClipboardManager clipboardManager = (ClipboardManager) OrderDetailActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                            clipboardManager.setPrimaryClip(ClipData.newPlainText(null, orderNumTextView.getText().toString()));
                            ToastUtil.show("订单号已复制");
                        }
                    });
                    orderStatusTextView.setText(MyOrderActivity.getOrderStatus(order.getOrderStatus()));

                    int countDownTime = MyOrderFragment.getOrderRemainTime(order);

                    //确认订单状态
                    if (MyOrderActivity.ORDER_STATUS_WAIT_FOR_PAY.equals(order.getOrderStatus())) {
                        String format = "";
                        //秒杀商品倒计时15分钟      非秒杀商品倒计时24小时
                        //0为非秒杀商品 1为秒杀商品
                        if ("0".equals(order.getMiaosha())) {
                            format = CountDownTask.FORMAT_HOUR;
                        } else if ("1".equals(order.getMiaosha())) {
                            format = CountDownTask.FORMAT_MINUTE;
                        }

                        //待支付订单
                        if (countDownTime < 0) {
                            countDownTime = 0;
                        }
                        CountDownTask.countDwon(countDownTime, handler, format);
                        showPayDownPaymentViews();

                    } else if (MyOrderActivity.ORDER_STATUS_CANCELED.equals(order.getOrderStatus())
                            //已取消订单
                            || MyOrderActivity.ORDER_STATUS_REFUSED.equals(order.getOrderStatus())
                            || MyOrderActivity.ORDER_STATUS_AUTO_CANCELED.equals(order.getOrderStatus())) {
                        orderStatusCancelView();
                    } else if (MyOrderActivity.ORDER_STATUS_WAIT_FOR_SEND_GOODS.equals(order.getOrderStatus())) {
                        //待发货订单
                        countDownRelativeLayout.setVisibility(View.GONE);
                        borderCountDownView.setVisibility(View.GONE);
                    } else if (MyOrderActivity.ORDER_STATUS_WAIT_FOR_TAKE_OVER_GOODS.equals(order.getOrderStatus())) {
                        //待收货订单
                        countDownRelativeLayout.setVisibility(View.VISIBLE);
                        viewLogisticsButton.setVisibility(View.VISIBLE);
                        //101为话费充值，102为流量充值 虚拟商品没有查看物流
                        if (StringUtil.notEmpty(order.getProductType()) && (Integer.valueOf(order.getProductType()) == 101 || Integer.valueOf(order.getProductType()) == 102 || Integer.valueOf(order.getProductType()) == 106)) {
                            countDownRelativeLayout.setVisibility(View.GONE);
                        }
                        orderRemainTimeTextView.setVisibility(View.GONE);
                        setOrderRemainTimeTextView.setVisibility(View.GONE);
                        cancelOrderTextView.setVisibility(View.GONE);
                        payDownPaymentTextView.setVisibility(View.GONE);
                    } else if (MyOrderActivity.ORDER_STATUS_FINISHED.equals(order.getOrderStatus())) {
                        //已完成订单
                        applyAfterSaleLinearLayout.setVisibility(View.VISIBLE);
                        countDownRelativeLayout.setVisibility(View.GONE);
                    }

//					发票类型	invoiceType	String			0不开发票1 纸质发票2 增值税发票
//					发票抬头	invoiceTitle	String			1 个人2 单位
//					发票内容	invoiceContent	String			1 明细2 耗材3 办公用品4 电脑配件
//					发票单位名称	invoiceUnit	String
                    if (ConfirmOrderActivity.CODE_OF_NO_INVOICE_INFO.equals(order.getInvoiceType())) {
                        showNoInvoiceInfoView();

                    } else if (ConfirmOrderActivity.CODE_OF_PAGER_INVOICE_INFO.equals(order.getInvoiceType())
                            || ConfirmOrderActivity.CODE_OF_VALUE_ADDED_TAX_INVOICE_INFO.equals(order.getInvoiceType())) {
                        noInvoiceInfoRelativeLayout.setVisibility(View.GONE);
                        pagerInvoiceInfoRelativeLayout.setVisibility(View.VISIBLE);

                        if ("1".equals(order.getInvoiceTitle())) {
                            setInvoiceInfoTypeTextView.setText(InvoiceInfoActivity.INVOICE_TITLE_PERSON);
                            invoiceInfoCompanyNameNameTextView.setVisibility(View.GONE);
                            setInvoiceInfoCompanyNameTextView.setVisibility(View.GONE);
                        } else if ("2".equals(order.getInvoiceTitle())) {
                            setInvoiceInfoTypeTextView.setText(InvoiceInfoActivity.INVOICE_TITLE_COMPANY);
                            invoiceInfoCompanyNameNameTextView.setVisibility(View.VISIBLE);
                            setInvoiceInfoCompanyNameTextView.setVisibility(View.VISIBLE);
                            setInvoiceInfoCompanyNameTextView.setText(order.getInvoiceUnit());
                        }
                        setInvoiceInfoContentTextView.setText(getInvoiceContent(order.getInvoiceContent()));
                    }

                    String comment = "";
                    if (StringUtil.notEmpty(order.getRemark())) {
                        comment = order.getRemark();
                    }
                    commentTextView.setText(comment);

                    productNameTextView.setText(order.getProductName());
                    Picasso.with(OrderDetailActivity.this).load(order.getProductImage()).placeholder(R.drawable.ic_launcher).into(productImageView);
                    String productPrice = MyAccountFragment.moneyDecimalFormat.format(order.getProductPrice()) + "元";
                    productPriceTextView.setText("一次性付: " + productPrice);
                    tvOrderDetailActivitySkuparams.setText(order.getSkuParameters());
                    setOrderMoneyTextView.setText(productPrice);
                    setActualMoneyTextView.setText(productPrice);
                    if (StringUtil.notEmpty(order.getRedbagId())) {
//						//显示“红包金额”
                        borderredMoneyDayView.setVisibility(View.VISIBLE);
                        redMoneyTextView.setVisibility(View.VISIBLE);
                        setRedMoneyTextView.setVisibility(View.VISIBLE);

                        setRedMoneyTextView.setText(order.getRedbagValue() + "元");
                        setActualMoneyTextView.setText(order.getActualAmount() + "元");
                    }

                    //首付比例
                    double rate = 0.00d;
                    if (StringUtil.notEmpty(order.getRates())) {
                        rate = Double.valueOf(order.getRates());
                    }
                    int rateInt = (int) rate;
                    if (rateInt == 100) {
                        //首付比例100%时， 查看合同和还款计划隐藏
                        applyAfterSaleLinearLayout.setVisibility(View.GONE);
                    }
                    downPaymentPercentTextView.setText(rateInt + "%");
                    //分期月数
                    monthOfInstallmentTextView.setText(order.getMonthNum() + "个月");
                    //首付金额
                    if (Double.valueOf(order.getFirstPayment()) == 0) {
                        downPaymentPriceTextView.setVisibility(View.GONE);
                        downPaymentTextView.setVisibility(View.GONE);
                    } else {
                        downPaymentPriceTextView.setText(MyAccountFragment.moneyDecimalFormat.format(order.getFirstPayment()) + "元");
                    }
                    //月付金额
                    monthlyPaymentPriceTextView.setText(MyAccountFragment.moneyDecimalFormat.format(order.getMonthPay()) + "元");
                    setOrderTimeTextView.setText(order.getOrderTime());


                } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == jsonObject.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                    UserUtils.tokenFailDialog(OrderDetailActivity.this, jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), TAG);
                } else {
                    Toast.makeText(OrderDetailActivity.this, "获取订单详情失败", Toast.LENGTH_SHORT).show();
                }

            }

        });
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    /**
     * 发票内容转换
     *
     * @param contentInt 服务器端返回的发票内容代码
     * @return 要显示的文本
     */
    private String getInvoiceContent(String contentInt) {
        String contentStr = "";
        if ("1".equals(contentInt)) {
            contentStr = InvoiceInfoActivity.INVOICE_CONTENT_DETAIL;
        } else if ("2".equals(contentInt)) {
            contentStr = InvoiceInfoActivity.INVOICE_CONTENT_CONSUMER_GOODS;
        } else if ("3".equals(contentInt)) {
            contentStr = InvoiceInfoActivity.INVOICE_CONTENT_OFFICE_SUPPLY;
        } else if ("4".equals(contentInt)) {
            contentStr = InvoiceInfoActivity.INVOICE_CONTENT_PC_COMPONENTS;
        }

        return contentStr;
    }

    /**
     * 显示倒计时，取消订单，支付首付等view
     */
    private void showPayDownPaymentViews() {
        countDownRelativeLayout.setVisibility(View.VISIBLE);
        viewLogisticsButton.setVisibility(View.GONE);
        orderRemainTimeTextView.setVisibility(View.VISIBLE);
        setOrderRemainTimeTextView.setVisibility(View.VISIBLE);
        cancelOrderTextView.setVisibility(View.VISIBLE);
        payDownPaymentTextView.setVisibility(View.VISIBLE);
    }

    /**
     * 取消订单对话框
     */
    private void cancelOrderDialog() {
        new AlertDialog.Builder(this)
                .setMessage("是否取消订单?")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                                cancelOrder();
                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();

                            }
                        }).create().show();

    }

    /**
     * 取消订单
     */
    private void cancelOrder() {
        Map<String, String> params = new HashMap<>();
        params.put("uid", AppContext.user.getUid());
        params.put("token", AppContext.user.getToken());
        params.put("orderId", orderId);
        params.put("isActivity", order.getIsActivity());
        params.put("activityNo", order.getActivityNo());
        params.put("redBagId", order.getRedbagId());

        HttpRequest.post(mContext, HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.CANCEL_ORDER, params, new AsyncResponseCallBack() {

            @Override
            public void onFailed(String path, String msg) {
                Toast.makeText(OrderDetailActivity.this, ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER + "取消订单失败", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onResult(String arg2) {
                String jsonStr = new String(arg2);
                LogUtil.printLog("取消订单：" + jsonStr);

                JSONObject jsonObject = JSON.parseObject(jsonStr);

                if (ConstantsUtil.RESPONSE_SUCCEED == jsonObject.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                    Toast.makeText(OrderDetailActivity.this, "取消订单成功", Toast.LENGTH_SHORT).show();
                    orderStatusCancelView();
//					OrderDetailActivity.this.finish();

                } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == jsonObject.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                    UserUtils.tokenFailDialog(OrderDetailActivity.this, jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), TAG);
                } else {
                    Toast.makeText(OrderDetailActivity.this, "取消订单失败", Toast.LENGTH_SHORT).show();
                }

            }

        });
    }

    //订单已取消的view
    private void orderStatusCancelView() {
        countDownRelativeLayout.setVisibility(View.GONE);
        orderStatusTextView.setText("已取消");
    }
}
