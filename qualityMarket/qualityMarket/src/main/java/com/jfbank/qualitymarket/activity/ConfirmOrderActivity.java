package com.jfbank.qualitymarket.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.balysv.materialripple.MaterialRippleLayout;
import com.huoyanzhengxin.zhengxin.print.FingerPrint;
import com.huoyanzhengxin.zhengxin.print.FingerPrintHelp;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.base.BaseActivity;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
import com.jfbank.qualitymarket.dao.StoreService;
import com.jfbank.qualitymarket.fragment.MyAccountFragment;
import com.jfbank.qualitymarket.js.JsRequstInterface;
import com.jfbank.qualitymarket.listener.IMeterialClickLisenter;
import com.jfbank.qualitymarket.model.CouponBeqan;
import com.jfbank.qualitymarket.model.Product;
import com.jfbank.qualitymarket.model.ReceivingAddressBean;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.util.Base64;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.LogUtil;
import com.jfbank.qualitymarket.util.StringUtil;
import com.jfbank.qualitymarket.util.TDUtils;
import com.jfbank.qualitymarket.util.UserUtils;
import com.jfbank.qualitymarket.widget.LoadingAlertDialog;
import com.jfbank.qualitymarket.widget.PayPasswordView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit2.Call;

/**
 * 功能：手机充值确认订单<br>
 * 作者：赵海<br>
 * 时间： 2017/2/17 0017<br>.
 * 版本：1.2.0
 */

public class ConfirmOrderActivity extends BaseActivity implements IMeterialClickLisenter {
    public static final String TAG = ConfirmOrderActivity.class.getName();
    ;
    public final static int INVOICE_INFO_REQUEST_CODE = 101;
    public final static int COMMENT_REQUEST_CODE = 201;
    public final static int ADD_CONSIGNEE_ADDRESS_REQUEST_CODE = 301;
    public final static int SET_CONSIGNEE_ADDRESS_REQUEST_CODE = 401;
    public final static int SET_BILLING_DAY_REQUEST_CODE = 501;
    public final static int RED_PACKET_REQUEST_CODE = 601;

    public static final String KEY_OF_PRODUCT_NAME = "productName";
    public static final String KEY_OF_PRODUCT_PRICE = "productPrice";
    public static final String KEY_OF_PRODUCT_SKUPARAM = "skuParameters";
    public static final String KEY_OF_PRODUCT_IMAGE_URL = "productImageUrl";
    public static final String KEY_OF_DOWNPAYMENT_PRICE = "downpaymentPrice";
    public static final String KEY_OF_DOWNPAYMENT_RATE = "downpaymentRate";
    public static final String KEY_OF_INSTALLMENT = "installment";
    public static final String KEY_OF_BILLING_DAY = "billingday";
    public static final String KEY_OF_INVOICE_INFO = "invoiceInfoKey";
    public static final String KEY_OF_COMMENT = "commentKey";
    public static final String KEY_OF_ORDER_MOENY = "orderMoneyKey";
    public static final String KEY_OF_ACTUAL_MOENY = "actualMoneyKey";
    public static final String KEY_OF_COUPON_MOENY = "couponKey";
    public static final String KEY_OF_RED_PACKET = "redPacketKey";
    public static final String KEY_OF_UPCATEGORY_TYPE = "upCategoryTypeKey";
    public static final String KEY_OF_RED_PACKET_MODEL = "redPacketModelKey";
    public static final String KEY_OF_ADD_CONSIGNEE_ADDRESS = "addConsigneeAddressKey";
    public static final String KEY_OF_CONSIGNEE_NAME = "consigneeNameKey";
    public static final String KEY_OF_CONSIGNEE_MOBILE = "consigneeMobileKey";
    public static final String KEY_OF_CONSIGNEE_ADDRESS = "consigneeAddressKey";
    public static final String KEY_OF_CONSIGNEE_ADDRESS_CODE = "consigneeAddressCodeKey";
    public static final String KEY_OF_SET_CONSIGNEE_ADDRESS = "setConsigneeAddressKey";
    public static final String KEY_OF_ORDER_NO = "orderNoKey";
    public static final String KEY_OF_PRODUCT_NO = "productNoKey";
    public static final String KEY_OF_IS_ACTIVITY = "isActivity";
    public static final String KEY_OF_RATE = "rates";
    public static final String KEY_OF_FIRSTPAYMENT = "firstpayment";
    public static final String COMMENT_NO_INFO = "无备注";
    public static final String COUPON_NO_DATA = "不使用";
    // 发票内容 invoiceContent String 1 不开发票 2 明细
    // 发票类型 invoiceType String 1 纸质发票2 增值税发票
    // 发票抬头 invoiceTitle String 1 个人 2 单位
    // 发票单位名称 invoiceUnit String
    public static final String CODE_OF_NO_INVOICE_INFO = "0";
    public static final String CODE_OF_PAGER_INVOICE_INFO = "1";//纸质发票
    public static final String CODE_OF_VALUE_ADDED_TAX_INVOICE_INFO = "2"; //增值税发票
    public static final String NO_INVOICE_INFO = "不开发票";
    public static final String PAGER_INVOICE_INFO = "纸质发票";
    public static final String NO_BILLING_DAY = "设置";
    public static final String DAY = "号";

    private RelativeLayout rlTitle;
    private TextView tvTitle;
    private ImageView ivBack;
    private TextView tvRechargePhonenum;
    private LinearLayout llOrderAddress;
    private MaterialRippleLayout rlConfirmOrderActivitySetConsigneeAddress;
    private TextView tvConfirmOrderActivityRecipient;
    private TextView tvConfirmOrderActivityRecipientMobile;
    private TextView tvConfirmOrderActivityConsignee;
    private TextView tvConfirmOrderActivityConsigneeAddress;
    private MaterialRippleLayout rlConfirmOrderActivityAddConsigneeAddress;
    private ImageView ivPhonenumOperator;
    private TextView tvRechargeSum;
    private TextView tvRechargePaynum;
    private TextView tvGoodsSpu;
    private LinearLayout llRechargePayway;
    private TextView tvRechargePayway;
    private LinearLayout llRechargeBillinfo;
    private TextView tvRechargeBillinfo;
    private LinearLayout llRechargeSetBillingDay;
    private TextView tvRechargeSetBillingDay;
    //首付比例
    private TextView tvRechargeProportion;
    private TextView tvRechargeStagenum;
    private LinearLayout llRechargeSetCoupon;
    private TextView tvRechargeSetCoupon;
    private LinearLayout llRechargeRemark;
    private TextView tvRechargeRemark;
    private TextView tvOrderDetailActivityOrderMoney;
    private TextView tvOrderDetailActivitySetOrderMoney;
    private View vOrderDetailActivityBorderRedMoney;
    private TextView tvOrderDetailActivityRedMoney;
    private TextView tvOrderDetailActivitySetRedMoney;
    private View vOrderDetailActivityBorderActualMoney;
    private TextView tvOrderDetailActivityActualMoney;
    private TextView tvOrderDetailActivitySetActualMoney;
    private TextView tvConfirmOrderActivityDownPayment;
    private TextView tvConfirmOrderActivityDownPaymentPrice;
    @InjectView(R.id.v_confirmOrderActivity_border_downPayment)
    View borderDownpaymentTextView;
    private TextView tvConfirmOrderActivityMonthlyPayment;
    private TextView tvConfirmOrderActivityMonthlyPaymentPrice;
    private TextView tvConfirmOrderActivityQualityMarketProtocal;
    private Button btnConformcharge;

    private JSONObject orderJsonObject;
    private Product product;
    private String firstPayment;
    private String firstPaymentOne;
    private String monthPay;// 月付金额
    private String phoneNum;    //充值手机号
    private int productType; //商品类型
    private String operatorName = null;//商户名称
    private String billingDay = "";//账单日
    private String addressNo = "";
    private ReceivingAddressBean.DataBean defaultAddress;
    private List<ReceivingAddressBean.DataBean> addressList = new ArrayList<ReceivingAddressBean.DataBean>();
    private CouponBeqan couponBeqan = new CouponBeqan();//红包
    String picImages = null;
    private String actualAmount;// 商品实际支付价格
    private int rateInt = -1;
    private String invoiceInfo = ConfirmOrderActivity.NO_INVOICE_INFO;
    /**
     * 网编请求时加载框
     */
    private LoadingAlertDialog mDialog;
    private FingerPrint fingerPrint;
    private String fingerPrintEventType = "";// 火眼设备指纹sdk事件类型
    private String fingerPrintTimeTag;// 火眼设备指sdk纹时间

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);
        ButterKnife.inject(this);

        fingerPrint = FingerPrintHelp.getFingerPrint(getApplicationContext());
        fingerPrint.startTime();

        initView();
    }

    /**
     * 初始化组件
     */
    private void initView() {
        rlTitle = (RelativeLayout) findViewById(R.id.rl_title);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        tvRechargePhonenum = (TextView) findViewById(R.id.tv_recharge_phonenum);
        llOrderAddress = (LinearLayout) findViewById(R.id.ll_order_address);
        tvConfirmOrderActivityRecipient = (TextView) findViewById(R.id.tv_confirmOrderActivity_recipient);
        tvConfirmOrderActivityRecipientMobile = (TextView) findViewById(R.id.tv_confirmOrderActivity_recipientMobile);
        tvConfirmOrderActivityConsignee = (TextView) findViewById(R.id.tv_confirmOrderActivity_consignee);
        tvConfirmOrderActivityConsigneeAddress = (TextView) findViewById(R.id.tv_confirmOrderActivity_consigneeAddress);
        ivPhonenumOperator = (ImageView) findViewById(R.id.iv_phonenum_operator);
        tvRechargeSum = (TextView) findViewById(R.id.tv_recharge_sum);
        tvRechargePaynum = (TextView) findViewById(R.id.tv_recharge_paynum);
        tvGoodsSpu = (TextView) findViewById(R.id.tv_goods_spu);
        llRechargePayway = (LinearLayout) findViewById(R.id.ll_recharge_payway);
        tvRechargePayway = (TextView) findViewById(R.id.tv_recharge_payway);
        llRechargeBillinfo = (LinearLayout) findViewById(R.id.ll_recharge_billinfo);
        tvRechargeBillinfo = (TextView) findViewById(R.id.tv_recharge_billinfo);
        llRechargeSetBillingDay = (LinearLayout) findViewById(R.id.ll_recharge_setBillingDay);
        tvRechargeSetBillingDay = (TextView) findViewById(R.id.tv_recharge_setBillingDay);
        tvRechargeProportion = (TextView) findViewById(R.id.tv_recharge_proportion);
        tvRechargeStagenum = (TextView) findViewById(R.id.tv_recharge_stagenum);
        llRechargeSetCoupon = (LinearLayout) findViewById(R.id.ll_recharge_setCoupon);
        tvRechargeSetCoupon = (TextView) findViewById(R.id.tv_recharge_setCoupon);
        llRechargeRemark = (LinearLayout) findViewById(R.id.ll_recharge_remark);
        tvRechargeRemark = (TextView) findViewById(R.id.tv_recharge_remark);
        tvOrderDetailActivityOrderMoney = (TextView) findViewById(R.id.tv_orderDetailActivity_orderMoney);
        tvOrderDetailActivitySetOrderMoney = (TextView) findViewById(R.id.tv_orderDetailActivity_setOrderMoney);
        vOrderDetailActivityBorderRedMoney = (View) findViewById(R.id.v_orderDetailActivity_border_redMoney);
        tvOrderDetailActivityRedMoney = (TextView) findViewById(R.id.tv_orderDetailActivity_redMoney);
        tvOrderDetailActivitySetRedMoney = (TextView) findViewById(R.id.tv_orderDetailActivity_setRedMoney);
        vOrderDetailActivityBorderActualMoney = (View) findViewById(R.id.v_orderDetailActivity_border_actualMoney);
        tvOrderDetailActivityActualMoney = (TextView) findViewById(R.id.tv_orderDetailActivity_actualMoney);
        tvOrderDetailActivitySetActualMoney = (TextView) findViewById(R.id.tv_orderDetailActivity_setActualMoney);
        tvConfirmOrderActivityDownPayment = (TextView) findViewById(R.id.tv_confirmOrderActivity_downPayment);
        tvConfirmOrderActivityDownPaymentPrice = (TextView) findViewById(R.id.tv_confirmOrderActivity_downPaymentPrice);
        tvConfirmOrderActivityMonthlyPayment = (TextView) findViewById(R.id.tv_confirmOrderActivity_monthlyPayment);
        tvConfirmOrderActivityMonthlyPaymentPrice = (TextView) findViewById(R.id.tv_confirmOrderActivity_monthlyPaymentPrice);
        tvConfirmOrderActivityQualityMarketProtocal = (TextView) findViewById(R.id.tv_confirmOrderActivity_qualityMarketProtocal);
        btnConformcharge = (Button) findViewById(R.id.btn_conformcharge);
        tvTitle.setText(R.string.str_title_sureorder);
        CommonUtils.makeMeterial(ivBack, this);
        CommonUtils.makeMeterial(btnConformcharge, this);
        CommonUtils.makeMeterial(llRechargeRemark, this);
        CommonUtils.makeMeterial(llRechargeSetCoupon, this);
        CommonUtils.makeMeterial(llRechargeSetBillingDay, this);
        CommonUtils.makeMeterial(llRechargeBillinfo, this);
        rlConfirmOrderActivitySetConsigneeAddress = CommonUtils.makeMeterial(findViewById(R.id.rl_confirmOrderActivity_setConsigneeAddress), this);
        rlConfirmOrderActivityAddConsigneeAddress = CommonUtils.makeMeterial(findViewById(R.id.rl_confirmOrderActivity_addConsigneeAddress), this);
        CommonUtils.makeMeterial(tvConfirmOrderActivityQualityMarketProtocal, this);
        CommonUtils.setTitle(this, rlTitle);
        String orderStr = getIntent().getStringExtra(JsRequstInterface.ORDER_JSON_STRING);
        orderJsonObject = JSON.parseObject(orderStr);
        setData();
    }

    @Override
    protected String getPageName() {
        return getString(R.string.str_pagename_rechargeorder);
    }

    private void setData() {
        //首付金额
        firstPaymentOne = firstPayment = MyAccountFragment.moneyDecimalFormat.format(orderJsonObject.getDouble("downpaymentMonth"));
        // 月付金额
        monthPay = orderJsonObject.getString("monthMoney");
        //充值手机号
        phoneNum = orderJsonObject.getString("phone");
        //商品类型
        try {
            productType = Integer.parseInt(orderJsonObject.getString("notEmpty"));
        } catch (Exception e) {
            productType = 0;
        }
//  商品实际支付价格
        actualAmount = orderJsonObject.getString("jdPrice");
        operatorName = orderJsonObject.getString("attribution");
        // 首付比例
        try {
            rateInt = Integer.parseInt(orderJsonObject.getString("curDownPaymentRatio"));// {"productNo":"pzsc1471857319003","jdPrice":"130.00","curMonthNum":"12","downpaymentMonth":"39.00","monthMoney":"10.01","isActivity":"1","activityNo":"hd2016101013565955tbbw","achieve":"order","authority":"ID"}
        } catch (Exception e) {
            rateInt = 0;
        }
        // 首付比例
        if (rateInt == 0) {
            tvConfirmOrderActivityDownPayment.setVisibility(View.GONE);
            tvConfirmOrderActivityDownPaymentPrice.setVisibility(View.GONE);
            borderDownpaymentTextView.setVisibility(View.GONE);
        }
        tvRechargeProportion.setText(rateInt + "%");
        // 分期
        tvRechargeStagenum.setText(orderJsonObject.getString("curMonthNum") + "个月");
        //首付金额
        tvConfirmOrderActivityDownPaymentPrice.setText(firstPayment + "元");
        //月供金额
        tvConfirmOrderActivityMonthlyPaymentPrice.setText(monthPay + "元");
//        订单金额
        tvOrderDetailActivitySetOrderMoney.setText(actualAmount + "元");
//        发票类型
        tvRechargeBillinfo.setText(ConfirmOrderActivity.NO_INVOICE_INFO);
//        账单日设置
        tvRechargeSetBillingDay.setText(ConfirmOrderActivity.NO_BILLING_DAY);
//        不适用红包
        tvRechargeSetCoupon.setText(ConfirmOrderActivity.COUPON_NO_DATA);
        // 已设置过账单日，隐藏账单日视图
        if (StringUtil.notEmpty(AppContext.user.getBillDate())) {
            llRechargeSetBillingDay.setVisibility(View.GONE);
        }
        // 暂无红包功能可选
        llRechargeSetCoupon.setVisibility(View.GONE);

        //隐藏 “红包金额”
        vOrderDetailActivityBorderRedMoney.setVisibility(View.GONE);
        tvOrderDetailActivityRedMoney.setVisibility(View.GONE);
        tvOrderDetailActivitySetRedMoney.setVisibility(View.GONE);
        getProductDetail();
        getRedPacket();
        if (productType == 101 || productType == 102 || productType == 106) {
            picImages = orderJsonObject.getString("productImages");
            if (TextUtils.isEmpty(picImages)) {
                Picasso.with(this).load(R.drawable.ic_launcher).into(ivPhonenumOperator);
            } else {
                Picasso.with(this).load(picImages).placeholder(R.drawable.ic_launcher).into(ivPhonenumOperator);
            }
            if (productType == 101 || productType == 102) { //充值号码
                tvRechargePhonenum.setText("充值号码: " + phoneNum);
            } else {
                //充值号码
                tvRechargePhonenum.setText("接收短信号码: " + phoneNum);
            }

            tvRechargePhonenum.setVisibility(View.VISIBLE);
            llOrderAddress.setVisibility(View.GONE);
        } else {
            tvRechargePhonenum.setVisibility(View.GONE);
            llOrderAddress.setVisibility(View.VISIBLE);
            showAddConsigneeAddress();
            getDefaultReceiptAddress();
        }
    }


    /**
     * 显示修改收货地址
     */
    private void showSetConsigneeAddressView() {
        rlConfirmOrderActivitySetConsigneeAddress.setVisibility(View.VISIBLE);
        rlConfirmOrderActivityAddConsigneeAddress.setVisibility(View.GONE);
    }

    /**
     * 显示添加收货地址
     */
    private void showAddConsigneeAddress() {
        rlConfirmOrderActivitySetConsigneeAddress.setVisibility(View.GONE);
        rlConfirmOrderActivityAddConsigneeAddress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onMetrialClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.iv_back://返回按钮
                finish();
                break;
            case R.id.btn_conformcharge://确认充值
                TDUtils.onEvent(this, "100014", "点击订单确认", TDUtils.getInstance().putUserid().buildParams());
                addOrder();
                break;
            case R.id.tv_confirmOrderActivity_qualityMarketProtocal: // 万卡商城协议
                String qualityMarketProtocalUrl = HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.H5_PAGE_SUB_URL + HttpRequest.H5_PAGE_ORDER_PROTOCOL;
                CommonUtils.startWebViewActivity(this, qualityMarketProtocalUrl, true, false);
                break;
            case R.id.ll_recharge_billinfo:  // 发票信息
                if (productType == 101 || productType == 102 || productType == 106) {
                    Toast.makeText(this, "此类商品不支持发票", Toast.LENGTH_SHORT).show();
                } else {
                    intent.setClass(this, InvoiceInfoActivity.class);
                    intent.putExtra(ConfirmOrderActivity.KEY_OF_INVOICE_INFO, invoiceInfo);
                    startActivityForResult(intent, ConfirmOrderActivity.INVOICE_INFO_REQUEST_CODE);
                }
                break;
            case R.id.ll_recharge_remark:// 备注
                intent.setClass(this, RemarkActivity.class);
                intent.putExtra(ConfirmOrderActivity.KEY_OF_COMMENT, tvRechargeRemark.getText().toString());
                startActivityForResult(intent, ConfirmOrderActivity.COMMENT_REQUEST_CODE);
                break;
            case R.id.rl_confirmOrderActivity_setConsigneeAddress:  // 设置收货地址

                launchMyReceivingAddressActivity(intent);
                break;
            case R.id.rl_confirmOrderActivity_addConsigneeAddress: // 添加收货地址
                intent.setClass(this, AppendAddressActivity.class);
                intent.putExtra(ConfirmOrderActivity.KEY_OF_ADD_CONSIGNEE_ADDRESS, true);
                startActivityForResult(intent, ConfirmOrderActivity.ADD_CONSIGNEE_ADDRESS_REQUEST_CODE);
                break;
            case R.id.ll_recharge_setCoupon: //  // 优惠券.红包
                intent.setClass(this, MyRedPacketActivity.class);
                intent.putExtra(ConfirmOrderActivity.KEY_OF_RED_PACKET, ConfirmOrderActivity.COUPON_NO_DATA);
                intent.putExtra(ConfirmOrderActivity.KEY_OF_IS_ACTIVITY, orderJsonObject.getString("isActivity"));
                intent.putExtra(ConfirmOrderActivity.KEY_OF_UPCATEGORY_TYPE, orderJsonObject.getString("productNo"));
                intent.putExtra(ConfirmOrderActivity.KEY_OF_RATE, rateInt);
                intent.putExtra(ConfirmOrderActivity.KEY_OF_FIRSTPAYMENT, firstPaymentOne);
                startActivityForResult(intent, ConfirmOrderActivity.RED_PACKET_REQUEST_CODE);
                break;
            case R.id.ll_recharge_setBillingDay: // 设置账单日
                intent.setClass(this, SetBillingDayActivity.class);
                intent.putExtra(ConfirmOrderActivity.KEY_OF_BILLING_DAY, tvRechargeSetBillingDay.getText().toString());
                startActivityForResult(intent, ConfirmOrderActivity.SET_BILLING_DAY_REQUEST_CODE);
                break;
        }
    }

    /**
     * 弹出万卡交易密码对话框
     *
     * @param activity
     */
    public void inputWankaPayPasswd(final Activity activity) {
        final Dialog dialog = new Dialog(activity, R.style.payBillInstalmentDialog);
        final PayPasswordView payPasswordView = new PayPasswordView(activity, dialog, new PayPasswordView.OnPayListener() {

            @Override
            public void onSurePay(final String password, PayPasswordView passwordView) {// 这里调用验证密码是否正确的请求
                //TODO
                //校验交易密码成功后，发送提交订单请求
                //提交订单请求嵌入到校验交易密码请求中
                fingerPrintEventType = "android确认订单页面--验证交易密码--商品ID：" + orderJsonObject.getString("productNo");
                fingerPrintTimeTag = fingerPrint.sendEventInfo(fingerPrintEventType, AppContext.user.getUname());
                validateOneCardTradePassword(password, passwordView);
            }

            @Override
            public void onCancelPay() {
            }
        });

        final View dialogView = payPasswordView.getView();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(dialogView);
        dialog.setTitle("");

        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
//		dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        dialogWindow.setBackgroundDrawable(activity.getResources().getDrawable(R.color.transparent));
        lp.x = 0; // 新位置X坐标
        lp.y = -20; // 新位置Y坐标
        lp.width = (int) activity.getResources().getDisplayMetrics().widthPixels; // 宽度
//		lp.height = WindowManager.LayoutParams.WRAP_CONTENT; // 高度
//		dialogView.measure(0,0);
        lp.height = (int) activity.getResources().getDisplayMetrics().heightPixels;
        lp.alpha = 1f; // 透明度
        dialogWindow.setAttributes(lp);
        dialog.show();
    }


    /**
     * 启动我的收货地址页面
     *
     * @param intent
     */
    private void launchMyReceivingAddressActivity(Intent intent) {
        intent.setClass(this, MyReceivingAddressActivity.class);
        intent.putExtra(ConfirmOrderActivity.KEY_OF_SET_CONSIGNEE_ADDRESS, false);
        startActivityForResult(intent, ConfirmOrderActivity.SET_CONSIGNEE_ADDRESS_REQUEST_CODE);
    }

    /**
     * 15.6验证万卡交易密码
     */
    private void validateOneCardTradePassword(String tradePassword, final PayPasswordView payPasswordView) {
        Map<String, String> params = new HashMap<>();
        params.put("uid", AppContext.user.getUid());
        params.put("token", AppContext.user.getToken());
        params.put("tradePwd", Base64.encode(tradePassword.getBytes()));
        HttpRequest.post(mContext, HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.VALIDATE_ONE_CARD_TRADE_PASSWORD, params,
                new AsyncResponseCallBack() {

                    @Override
                    public void onResult(String arg2) {
                        String jsonStr = new String(arg2);
                        LogUtil.printLog("验证万卡交易密码：" + jsonStr);

                        JSONObject jsonObject = JSON.parseObject(jsonStr);
                        if (ConstantsUtil.RESPONSE_SUCCEED == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                            fingerPrint.sendEventResult(fingerPrintEventType, fingerPrintTimeTag, AppContext.user.getUname(), true);
                            submitOrder();
                        } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                            fingerPrint.sendEventResult(fingerPrintEventType, fingerPrintTimeTag, AppContext.user.getUname(), false);
                            UserUtils.tokenFailDialog(ConfirmOrderActivity.this,
                                    jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), TAG);
                        } else {
                            payPasswordView.mList.clear();
                            payPasswordView.updateUi();
                            Toast.makeText(ConfirmOrderActivity.this, jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), Toast.LENGTH_SHORT).show();
                            fingerPrint.sendEventResult(fingerPrintEventType, fingerPrintTimeTag, AppContext.user.getUname(), false);
                        }
                    }

                    @Override
                    public void onFailed(String path, String msg) {
                        Toast.makeText(ConfirmOrderActivity.this,
                                ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER + "验证万卡交易密码失败", Toast.LENGTH_SHORT)
                                .show();
                        fingerPrint.sendEventResult(fingerPrintEventType, fingerPrintTimeTag, AppContext.user.getUname(), false);
                    }
                });

    }

    /**
     * 设置账单日
     */
    private void setBillingDate() {
        if (StringUtil.isNull(billingDay)) {
            Toast.makeText(ConfirmOrderActivity.this, "请选择账单日", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("uid", AppContext.user.getUid());
        params.put("token", AppContext.user.getToken());
        params.put("billDate", billingDay);
        HttpRequest.post(mContext, HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.SET_BILLING_DATE, params,
                new AsyncResponseCallBack() {

                    @Override
                    public void onResult(String arg2) {
                        String jsonStr = new String(arg2);
                        LogUtil.printLog("设置账单日：" + jsonStr);

                        JSONObject jsonObject = JSON.parseObject(jsonStr);
                        if (ConstantsUtil.RESPONSE_SUCCEED == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                            AppContext.user.setBillDate(billingDay);
                            new StoreService(ConfirmOrderActivity.this).saveUserInfo(AppContext.user);
                            if ("100".equals(orderJsonObject.getString("curDownPaymentRatio"))) {
                                //首付比例100%时，直接跳转到支付页面，不用验证万卡交易密码
                                submitOrder();
                            } else {
                                inputWankaPayPasswd(ConfirmOrderActivity.this);
                            }
                        } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                            UserUtils.tokenFailDialog(ConfirmOrderActivity.this,
                                    jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), TAG);
                        } else {
                            Toast.makeText(ConfirmOrderActivity.this, "设置账单日失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailed(String path, String msg) {
                        Toast.makeText(ConfirmOrderActivity.this,
                                ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER + "设置账单日失败", Toast.LENGTH_SHORT)
                                .show();
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            switch (requestCode) {
                case ConfirmOrderActivity.INVOICE_INFO_REQUEST_CODE://发票选择
                    invoiceInfo = data.getStringExtra(ConfirmOrderActivity.KEY_OF_INVOICE_INFO);
                    if (invoiceInfo == null || "".equals(invoiceInfo) || ConfirmOrderActivity.NO_INVOICE_INFO.equals(invoiceInfo)) {
                        tvRechargeBillinfo.setText(ConfirmOrderActivity.NO_INVOICE_INFO);
                    } else {
                        String[] invoiceArray = invoiceInfo.split("\\" + InvoiceInfoActivity.INVOICE_INFO_SEPARATOR);
                        tvRechargeBillinfo.setText(invoiceArray[0]);
                    }
                    break;
                case ConfirmOrderActivity.COMMENT_REQUEST_CODE://备注
                    String comment = data.getStringExtra(ConfirmOrderActivity.KEY_OF_COMMENT);
                    if (comment == null || "".equals(comment) || ConfirmOrderActivity.COMMENT_NO_INFO.equals(comment)) {
                        tvRechargeRemark.setText("");
                    } else {
                        tvRechargeRemark.setText(comment);
                    }
                    break;
                case ConfirmOrderActivity.ADD_CONSIGNEE_ADDRESS_REQUEST_CODE:
                    getDefaultReceiptAddress();
                    break;
                case ConfirmOrderActivity.SET_CONSIGNEE_ADDRESS_REQUEST_CODE:
                    showSetConsigneeAddressView();
                    tvConfirmOrderActivityRecipient.setText(data.getStringExtra(ConfirmOrderActivity.KEY_OF_CONSIGNEE_NAME) + "  ");
                    tvConfirmOrderActivityRecipientMobile.setText(data.getStringExtra(ConfirmOrderActivity.KEY_OF_CONSIGNEE_MOBILE));
                    tvConfirmOrderActivityConsigneeAddress.setText(data.getStringExtra(ConfirmOrderActivity.KEY_OF_CONSIGNEE_ADDRESS));
                    addressNo = data.getStringExtra(ConfirmOrderActivity.KEY_OF_CONSIGNEE_ADDRESS_CODE);
                    break;
                case ConfirmOrderActivity.SET_BILLING_DAY_REQUEST_CODE://账单日选择
                    billingDay = data.getStringExtra(ConfirmOrderActivity.KEY_OF_BILLING_DAY);
                    if (billingDay == null || "".equals(billingDay)) {
                        tvRechargeSetBillingDay.setText(ConfirmOrderActivity.NO_BILLING_DAY);
                    } else {
                        tvRechargeSetBillingDay.setText("每月" + billingDay + ConfirmOrderActivity.DAY);
                    }
                    break;

                case ConfirmOrderActivity.RED_PACKET_REQUEST_CODE://红包选择
                    //只要显示了活动价格的商品无法使用红包，不管是不是活动商品。
                    //活动商品从活动页进是活动商品，从搜索页进是普通商品，但显示活动价格。
                    String redPacket = data.getStringExtra(ConfirmOrderActivity.KEY_OF_RED_PACKET);
                    Double redPacketPrice = 0.0d;
                    if (ConfirmOrderActivity.COUPON_NO_DATA.equals(redPacket)) {
                        tvRechargeSetCoupon.setText(ConfirmOrderActivity.COUPON_NO_DATA);
                        vOrderDetailActivityBorderRedMoney.setVisibility(View.GONE);
                        tvOrderDetailActivityRedMoney.setVisibility(View.GONE);
                        tvOrderDetailActivitySetRedMoney.setVisibility(View.GONE);
                        couponBeqan = new CouponBeqan();
                    } else {
                        vOrderDetailActivityBorderRedMoney.setVisibility(View.VISIBLE);
                        tvOrderDetailActivityRedMoney.setVisibility(View.VISIBLE);
                        tvOrderDetailActivitySetRedMoney.setVisibility(View.VISIBLE);
                        couponBeqan = data.getParcelableExtra(ConfirmOrderActivity.KEY_OF_RED_PACKET_MODEL);
                        tvOrderDetailActivitySetRedMoney.setText(MyAccountFragment.moneyDecimalFormat.format(Double.valueOf(couponBeqan.getParValue())) + "元");
                        tvRechargeSetCoupon.setText(couponBeqan.getShowName() + "  " + couponBeqan.getParValue() + "元");        //需要显示名称 + 面值
                        redPacketPrice = Double.valueOf(couponBeqan.getParValue());
                    }

                    // 计算物品总价格和首付金额
                    //商品价格
                    Double jdPrice = orderJsonObject.getDouble("jdPrice");
                    tvOrderDetailActivitySetOrderMoney.setText(MyAccountFragment.moneyDecimalFormat.format(jdPrice) + "元");
                    //实际价格
                    Double actualPrice = jdPrice - redPacketPrice;
                    if (actualPrice < 0) {
                        actualPrice = 0.0d;
                    }
                    actualAmount = MyAccountFragment.moneyDecimalFormat.format(actualPrice);
                    tvOrderDetailActivitySetActualMoney.setText(actualAmount + "元");

                    Double downPaymentPrice = Double.valueOf(actualAmount) * orderJsonObject.getInteger("curDownPaymentRatio") / 100;
                    Double monthlyPaymentPrice = 0.0d;
                    if (Double.valueOf(actualAmount) - downPaymentPrice > 0) {
                        monthlyPaymentPrice = (Double.valueOf(actualAmount) - downPaymentPrice) / orderJsonObject.getInteger("curMonthNum") + (Double.valueOf(actualAmount) - downPaymentPrice) * orderJsonObject.getDouble("rate") / 100;
                    }

                    //月供金额保留两位小数，向上取整
                    String monthPayment = MyAccountFragment.moneyDecimalFormat.format(monthlyPaymentPrice);
                    if (monthlyPaymentPrice > Double.valueOf(monthPayment)) {
                        monthPayment = MyAccountFragment.moneyDecimalFormat.format(Double.valueOf(monthPayment) + 0.01);
                    }

                    //首付金额保留两位小数，向上取整
                    firstPayment = MyAccountFragment.moneyDecimalFormat.format(downPaymentPrice);
                    monthPay = monthPayment + "";
                    if (downPaymentPrice > Double.valueOf(firstPayment)) {
                        firstPayment = MyAccountFragment.moneyDecimalFormat.format(Double.valueOf(firstPayment) + 0.01);
                    }

                    monthPay = monthPayment;
                    //首付金额
                    tvConfirmOrderActivityDownPaymentPrice.setText(firstPayment + "元");
                    //月供金额
                    tvConfirmOrderActivityMonthlyPaymentPrice.setText(monthPayment + "元");
                    break;
            }
        }
    }

    /**
     * 查询默认收货地址
     */
    private void getDefaultReceiptAddress() {
        final LoadingAlertDialog mDialog = new LoadingAlertDialog(this);
        mDialog.show("网络请求中...");
        Map<String, String> params = new HashMap<>();

        params.put("uid", AppContext.user.getUid());
        params.put("token", AppContext.user.getToken());
        // params.put("mobile", AppContext.user.getMobile()); // 手机
        params.put("defaultAdd", "0"); // 查询所有地址
        params.put("pageNo", 1 + ""); // 当前页码
        params.put("ver", AppContext.getAppVersionName(AppContext.mContext));
        params.put("Plat", ConstantsUtil.PLAT);

        LogUtil.printLog("默认收货地址请求参数：" + params.toString());

        HttpRequest.queryReceiptAddress(mContext, params, new AsyncResponseCallBack() {

            @Override
            public void onResult(String arg2) {
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }

                String jsonStr = new String(arg2);
                LogUtil.printLog("默认收货地址：" + jsonStr);

                if (null != arg2 && arg2.length() > 0) {

                    ReceivingAddressBean bean = JSON.parseObject(jsonStr, ReceivingAddressBean.class);
                    if (null != bean && "1".equals(bean.getStatus())) {
                        addressList = bean.getData();
                        if (addressList.size() > 0) {
                            defaultAddress = addressList.get(0);
                        }
                        for (int i = 0; i < addressList.size(); i++) {
                            if ("1".equals(addressList.get(i).getAddDefault())) {
                                defaultAddress = addressList.get(i);
                                break;
                            }
                        }

                        showSetConsigneeAddressView();
                        tvConfirmOrderActivityRecipient.setText(defaultAddress.getConsignee() + "   ");
                        tvConfirmOrderActivityRecipientMobile.setText(defaultAddress.getConsigneeMobile());
                        tvConfirmOrderActivityConsigneeAddress.setText(MyReceivingAddressActivity.getAddDetail(defaultAddress));
                        addressNo = defaultAddress.getAddressNo();

                    } else if ("11".equals(bean.getStatus())) { // 无任何提示。 即没有数据时
                        showAddConsigneeAddress();
                    } else if ("10".equals(bean.getStatus())) {
                        UserUtils.tokenFailDialog(mContext, bean.getStatusDetail(), null);
                    } else {
                        showAddConsigneeAddress();

                    }

                }

            }

            @Override
            public void onFailed(String path, String msg) {
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                Toast.makeText(ConfirmOrderActivity.this, ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER + "获取收货地址失败",
                        Toast.LENGTH_SHORT).show();

            }
        });

    }

    /**
     * 获取可用的红包
     */
    private void getRedPacket() {
        Map<String, String> params = new HashMap<>();
        params.put("mobile", AppContext.user.getMobile());
        params.put("productNo", orderJsonObject.getString("productNo"));
        params.put("uid", AppContext.user.getUid());
        params.put("token", AppContext.user.getToken());
        params.put("IsActivity", orderJsonObject.getString("isActivity"));
        params.put("firstPayment", firstPaymentOne);
        params.put("rates", rateInt + "");
        HttpRequest.post(mContext, HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.GET_NOTUSEREDBAG,
                params, new AsyncResponseCallBack() {

                    @Override
                    public void onResult(String arg2) {
                        String jsonStr = new String(arg2);
                        LogUtil.printLog("红包列表：" + jsonStr);
                        JSONObject jsonObject = JSON.parseObject(jsonStr);

                        if (ConstantsUtil.RESPONSE_SUCCEED == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                            com.alibaba.fastjson.JSONArray productJsonObject = jsonObject
                                    .getJSONArray(ConstantsUtil.RESPONSE_DATA_JSON_ARRAY_FIELD_NAME);
                            List<CouponBeqan> couponList = JSON.parseArray(productJsonObject.toString(),
                                    CouponBeqan.class);
                            // 1是活动商品 0是非活动商品
                            //活动商品不使用红包
//                            if (couponList.size() > 0 && !"1".equals(orderJsonObject.getString("isActivity"))) {
                            if (!CommonUtils.isEmptyList(couponList)) {//红包显示不为空
                                // 显示红包功能
                                llRechargeSetCoupon.setVisibility(View.VISIBLE);
                            } else {
                                llRechargeSetCoupon.setVisibility(View.GONE);
                            }
                        } else if (ConstantsUtil.RESPONSE_NO_DATA == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                            //无可用红包不显示
                            llRechargeSetCoupon.setVisibility(View.GONE);
                        } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == jsonObject.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                            UserUtils.tokenFailDialog(mContext, jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), null);
                        } else {
                            Toast.makeText(ConfirmOrderActivity.this, jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailed(String path, String msg) {
                        Toast.makeText(ConfirmOrderActivity.this, "加载失败，下拉重试", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    /**
     * 获取产品详情
     */
    private void getProductDetail() {
        Map<String, String> params = new HashMap<>();
        params.put("productType", productType + "");
        params.put("mobile", AppContext.user.getMobile());
        params.put("productNo", orderJsonObject.getString("productNo"));
        params.put("isActivity", orderJsonObject.getString("isActivity"));
        if (orderJsonObject.containsKey("activityNo")) {
            params.put("activityNo", orderJsonObject.getString("activityNo"));
        }

        HttpRequest.post(mContext, HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.GET_PRODUCT_DETAIL, params,
                new AsyncResponseCallBack() {

                    @Override
                    public void onResult(String arg2) {
                        String jsonStr = new String(arg2);
                        LogUtil.printLog("商品详情：" + jsonStr);

                        JSONObject jsonObject = JSON.parseObject(jsonStr);
                        if (ConstantsUtil.RESPONSE_SUCCEED == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                            try {
                                JSONObject productJsonObject = jsonObject
                                        .getJSONObject(ConstantsUtil.RESPONSE_DATA_JSON_ARRAY_FIELD_NAME);
                                product = JSON.parseObject(productJsonObject.toString(), Product.class);
                                updateGoodsDetail();
                            } catch (Exception e) {
                                LogUtil.printLog("产品详情：" + e.getMessage());
                            }

                        } else if (ConstantsUtil.RESPONSE_NO_DATA == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                            Toast.makeText(ConfirmOrderActivity.this, "暂无该商品详情详细数据", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(ConfirmOrderActivity.this, "获取商品详情失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailed(String path, String msg) {
                        Toast.makeText(ConfirmOrderActivity.this,
                                ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER + "获取商品详情失败", Toast.LENGTH_SHORT)
                                .show();

                    }
                });
    }

    /**
     * 商品详情
     */
    private void updateGoodsDetail() {
        //只要显示了活动价格的商品无法使用红包，不管是不是活动商品。
        //活动商品从活动页进是活动商品，从搜索页进是普通商品，但显示活动价格。
        if (productType == 101) {
            tvRechargeSum.setText(operatorName + " 话费充值: " + product.getProductName());
        } else if (productType == 102) {
            tvRechargeSum.setText(operatorName + " 流量充值: " + product.getProductName());
        } else if (productType == 106) {
            tvRechargeSum.setText(product.getProductName());
        } else {
            //活动商品从活动页进是活动商品，从搜索页进是普通商品，但显示活动价格。
            tvRechargeSum.setText(product.getProductName());
            Picasso.with(ConfirmOrderActivity.this).load(product.getMainImagePath())
                    .placeholder(R.drawable.ic_launcher)
                    .resize(ConstantsUtil.PRODUCT_IMAGE_WIDTH, ConstantsUtil.PRODUCT_IMAGE_WIDTH)
                    .into(ivPhonenumOperator);
        }
        String productPrice = "";
        if (StringUtil.notEmpty(product.getActivityPrice())) {
            // 活动价不为空显示活动价，活动价为空显示京东价
            productPrice = MyAccountFragment.moneyDecimalFormat.format(Double.valueOf(product.getActivityPrice())) + "元";
        } else {
            productPrice = MyAccountFragment.moneyDecimalFormat.format(Double.valueOf(product.getJdPrice())) + "元";
        }
        tvRechargePaynum.setText("一次性付：" + productPrice);
        tvGoodsSpu.setText(product.getSkuParameters());
        tvOrderDetailActivitySetOrderMoney.setText(productPrice);
        tvOrderDetailActivitySetActualMoney.setText(productPrice);
    }

    /**
     * 新增提交订单
     */
    private void addOrder() {
        if ((productType != 101 && productType != 102 && productType != 106) && (rlConfirmOrderActivityAddConsigneeAddress.getVisibility() == View.VISIBLE
                || rlConfirmOrderActivitySetConsigneeAddress.getVisibility() == View.GONE)) {
            Toast.makeText(ConfirmOrderActivity.this, "请先设置收货地址", Toast.LENGTH_SHORT).show();
            return;
        }
        if (product == null) {
            Toast.makeText(ConfirmOrderActivity.this, "产品详情数据为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (llRechargeSetBillingDay.getVisibility() == View.VISIBLE
                && (ConfirmOrderActivity.NO_BILLING_DAY.equals(tvRechargeSetBillingDay.getText().toString())
                || tvRechargeSetBillingDay.getText().toString() == null)) {
            Toast.makeText(ConfirmOrderActivity.this, "请先设置账单日", Toast.LENGTH_SHORT).show();
            return;
        }
        if (StringUtil.isNull(AppContext.user.getBillDate())) {
            setBillingDate();
        } else if ("100".equals(orderJsonObject.getString("curDownPaymentRatio"))) {
            //首付比例100%时，直接跳转到支付页面，不用验证万卡交易密码
            submitOrder();
        } else {
            inputWankaPayPasswd(this);
        }
    }

    /**
     * 提交订单
     */

    private void submitOrder() {
        if (null == mDialog) {
            mDialog = new LoadingAlertDialog(this);
        }
        mDialog.show("正在确认订单，请稍候...");

        Map<String, String> params = new HashMap<>();
        params.put("uid", AppContext.user.getUid());
        params.put("token", AppContext.user.getToken());
        String URL = null;
        if (productType == 101 || productType == 102||productType == 106) {
            params.put("productImage", picImages);//商品图片
            params.put("mobile", phoneNum);//充值手机号
            URL = HttpRequest.ADD_VIRTUAL_ORDER;
        } else {
            URL = HttpRequest.ADD_ORDER;
            params.put("addressNo", addressNo);
            params.put("productImage", product.getMainImagePath());
            params.put("buycount", 1 + "");
        }
        params.put("productTotal", product.getJdPrice());//商品总额
        params.put("productNo", orderJsonObject.getString("productNo"));//商品Id
        params.put("productName", product.getProductName());//商品名称
        params.put("productPrice", product.getJdPrice());//商品售价
        params.put("firstPayment", firstPayment);//首付款
        params.put("monthPay", monthPay);//月供
        if (StringUtil.isNull(tvRechargeRemark.getText().toString())) {
            params.put("remark", "");//备注
        } else {
            params.put("remark", tvRechargeRemark.getText().toString());//备注
        }
        if (ConfirmOrderActivity.NO_INVOICE_INFO.equals(tvRechargeBillinfo.getText().toString())
                || tvRechargeBillinfo.getText().toString() == null) {
            // 发票类型 invoiceType String 0不开发票1 纸质发票2 增值税发票
            // 发票抬头 invoiceTitle String 1 个人2 单位
            // 发票内容 invoiceContent String 1 明细2 耗材3 办公用品4 电脑配件
            // 发票单位名称 invoiceUnit String
            params.put("invoiceType", "0");
            params.put("invoiceTitle", "");
            params.put("invoiceContent", "");
            params.put("invoiceUnit", "");
        } else if (invoiceInfo.contains(InvoiceInfoActivity.INVOICE_INFO_SEPARATOR)) {
            String[] invoiceInfoArray = invoiceInfo.split("\\" + InvoiceInfoActivity.INVOICE_INFO_SEPARATOR);
            params.put("invoiceType", 1 + "");

            if (InvoiceInfoActivity.INVOICE_CONTENT_DETAIL.equals(invoiceInfoArray[invoiceInfoArray.length - 1])) {
                params.put("invoiceContent", 1 + "");
            } else if (InvoiceInfoActivity.INVOICE_CONTENT_CONSUMER_GOODS
                    .equals(invoiceInfoArray[invoiceInfoArray.length - 1])) {
                params.put("invoiceContent", 2 + "");
            } else if (InvoiceInfoActivity.INVOICE_CONTENT_OFFICE_SUPPLY
                    .equals(invoiceInfoArray[invoiceInfoArray.length - 1])) {
                params.put("invoiceContent", 3 + "");
            } else if (InvoiceInfoActivity.INVOICE_CONTENT_PC_COMPONENTS
                    .equals(invoiceInfoArray[invoiceInfoArray.length - 1])) {
                params.put("invoiceContent", 4 + "");
            }

            if (InvoiceInfoActivity.INVOICE_TITLE_PERSON.equals(invoiceInfoArray[0])) {
                params.put("invoiceTitle", 1 + "");
                params.put("invoiceUnit", "");
            } else {
                params.put("invoiceTitle", 2 + "");
                params.put("invoiceUnit", invoiceInfoArray[1]);
            }
        }
        if (orderJsonObject.containsKey("curDownPaymentRatio")) {
            params.put("rates", orderJsonObject.getString("curDownPaymentRatio"));// 首付比例
        } else {
            params.put("rates", 0 + "");// 首付比例
        }
        if (StringUtil.notEmpty(AppContext.user.getBillDate())) {
            params.put("billDate", AppContext.user.getBillDate());
        } else {
            params.put("billDate", tvRechargeSetBillingDay.getText().toString().replace(ConfirmOrderActivity.DAY, "").substring(2));
        }
        params.put("monthNum", orderJsonObject.getString("curMonthNum"));// 分期月数
        params.put("isActivity", orderJsonObject.getString("isActivity"));//活动商品标识
        if (orderJsonObject.containsKey("activityNo")) {//活动编号No
            params.put("activityNo", orderJsonObject.getString("activityNo"));
        } else {
            params.put("activityNo", "");
        }
        if (View.VISIBLE == tvOrderDetailActivitySetRedMoney.getVisibility() && couponBeqan != null && StringUtil.notEmpty(couponBeqan.getCouponNo() + "")) {
            params.put("redBagId", couponBeqan.getId() + "");//红包ID
            params.put("redBagValue", couponBeqan.getParValue());//红包优惠金额
            params.put("actualAmount", (orderJsonObject.getDouble("jdPrice") - Double.valueOf(couponBeqan.getParValue())) + "");//实际金额
        } else {
            params.put("actualAmount", actualAmount);//实际金额
        }

        btnConformcharge.setEnabled(false);
        /*
         * (1)  h5wk    H5下单使用万卡额度
         (2)androidwk   安卓下单使用万卡额度
         (3)ioswk    ios下单使用万卡额度
         (4)newh5wk       其他引流渠道使用H5下单使用万卡额度
         (5)h5dd   叮当下单
         (6)android   android下单使用万卡商城额度
         (7)ios   ios下单使用万卡商城额度
         */
        params.put("orderChannel", "android");//下单渠道
        /*
         *商品类型（0-100为普通商品，101以后为虚拟商品）（暂定101为手机充话费、102为冲流量）
         */
        params.put("productType", String.valueOf(productType));//商品类型

        final Call<String> requestHandle = HttpRequest.post(mContext, HttpRequest.QUALITY_MARKET_WEB_URL + URL, params,
                // HttpRequest.post(mContext,"http://101.200.87.124:8080/quality-mall-api/mall/qualityorderadd",
                // params,
                new AsyncResponseCallBack() {

                    @Override
                    public void onFailed(String path, String msg) {
                        if (mDialog != null && mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        Toast.makeText(ConfirmOrderActivity.this,
                                ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER + "订单提交失败", Toast.LENGTH_SHORT).show();
                        btnConformcharge.setEnabled(true);
                    }

                    @Override
                    public void onResult(String arg2) {
                        if (mDialog != null && mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        String jsonStr = new String(arg2);
                        LogUtil.printLog("确认订单：" + jsonStr);
                        Log.e("确认订单：", jsonStr);
                        btnConformcharge.setEnabled(true);
                        JSONObject jsonObject = JSON.parseObject(jsonStr);

                        if (ConstantsUtil.RESPONSE_SUCCEED == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                            Intent intent = new Intent(ConfirmOrderActivity.this, PayActivity.class);
                            intent.putExtra(ConfirmOrderActivity.KEY_OF_PRODUCT_NAME, tvRechargeSum.getText().toString());
                            intent.putExtra(ConfirmOrderActivity.KEY_OF_PRODUCT_PRICE, tvRechargePaynum.getText().toString());
                            intent.putExtra(ConfirmOrderActivity.KEY_OF_PRODUCT_SKUPARAM, tvGoodsSpu.getText().toString());
                            if (productType == 101 || productType == 102|| productType == 106) {//手机充值
                                intent.putExtra(ConfirmOrderActivity.KEY_OF_PRODUCT_IMAGE_URL, picImages);
                            } else {//普通商品
                                intent.putExtra(ConfirmOrderActivity.KEY_OF_PRODUCT_IMAGE_URL, product.getMainImagePath());

                            }

                            intent.putExtra(ConfirmOrderActivity.KEY_OF_DOWNPAYMENT_PRICE, tvConfirmOrderActivityDownPaymentPrice.getText().toString());
                            intent.putExtra(ConfirmOrderActivity.KEY_OF_DOWNPAYMENT_RATE, rateInt);
                            intent.putExtra(ConfirmOrderActivity.KEY_OF_INSTALLMENT, tvConfirmOrderActivityMonthlyPaymentPrice.getText().toString());
                            intent.putExtra(ConfirmOrderActivity.KEY_OF_ORDER_NO, jsonObject.getString("orderId"));
                            intent.putExtra(ConfirmOrderActivity.KEY_OF_PRODUCT_NO, orderJsonObject.getString("productNo"));
                            intent.putExtra(ConfirmOrderActivity.KEY_OF_ORDER_MOENY, tvOrderDetailActivitySetOrderMoney.getText().toString());
                            intent.putExtra(ConfirmOrderActivity.KEY_OF_ACTUAL_MOENY, tvOrderDetailActivitySetActualMoney.getText().toString());
                            intent.putExtra(LoginActivity.KEY_OF_COME_FROM, TAG);
                            if (View.VISIBLE == tvOrderDetailActivitySetRedMoney.getVisibility()) {
                                //使用红包
                                intent.putExtra(ConfirmOrderActivity.KEY_OF_COUPON_MOENY, tvOrderDetailActivitySetRedMoney.getText().toString());
                            } else {
                                //未使用红包
                                intent.putExtra(ConfirmOrderActivity.KEY_OF_COUPON_MOENY, "");

                            }
                            startActivity(intent);
                            //启动收银台页面后，无法返回当前页面了。故finish掉即可。
                            ConfirmOrderActivity.this.finish();
                        } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                            UserUtils.tokenFailDialog(ConfirmOrderActivity.this,
                                    jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), TAG);
                        } else {
                            String errorMsg = "订单提交失败";
                            if (StringUtil.notEmpty(jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME))) {
                                errorMsg = jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME);
                            }
                            Toast.makeText(ConfirmOrderActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                btnConformcharge.setEnabled(true);
                if (requestHandle != null && !requestHandle.isCanceled()) {
                    requestHandle.cancel();
                }
            }
        });
    }
}
