package com.jfbank.qualitymarket.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.base.BaseActivity;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
import com.jfbank.qualitymarket.model.BankCard;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.net.MobileSecurePayer;
import com.jfbank.qualitymarket.util.ActivityManager;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.LogUtil;
import com.jfbank.qualitymarket.util.StringUtil;
import com.jfbank.qualitymarket.util.TDUtils;
import com.jfbank.qualitymarket.util.UserUtils;
import com.jfbank.qualitymarket.widget.LoadingAlertDialog;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 支付页面
 *
 * @author 崔朋朋
 */
public class PayActivity extends BaseActivity {
    public static final String TAG = PayActivity.class.getName();
    @InjectView(R.id.rl_title)
    RelativeLayout rlTitle;
    @InjectView(R.id.tv_title)
    TextView tvTitle;
    @InjectView(R.id.tv_payActivity_downPayment)
    TextView downPaymentTextView;
    //首付金额
    @InjectView(R.id.tv_payActivity_downPaymentPrice)
    TextView downPaymentPriceTextView;
    //月供金额
    @InjectView(R.id.tv_payActivity_monthlyPaymentPrice)
    TextView monthlyPaymentPriceTextView;
    //产品名称
    @InjectView(R.id.tv_payActivity_productName)
    TextView productNameTextView;
    @InjectView(R.id.tv_payActivity_productPrice)
    TextView productPriceTextView;
    @InjectView(R.id.iv_payActivity_productImage)
    ImageView productImageView;

    @InjectView(R.id.rl_payActivity_bankCard)
    RelativeLayout bankCardRelativeLayout;
    @InjectView(R.id.tv_payActivity_bankName)
    TextView bankNameTextView;
    @InjectView(R.id.tv_payActivity_bankCardNum)
    TextView bankCardNumTextView;
    @InjectView(R.id.tv_payActivity_addBankCard)
    TextView addBankCardTextView;
    @InjectView(R.id.rl_payActivity_addBankCard)
    RelativeLayout addBankCardRelativeLayout;
    @InjectView(R.id.btn_payActivity_confirmPay)
    Button confirmPayButton;

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
    @InjectView(R.id.tv_goods_spu)
    TextView tvGoodsSpu;

    private BankCard defaultBankCard = new BankCard();
    private String productName;
    private String productPrice;
    private String skuParameters;
    private String downPayment;
    private String monthOfInstallment;
    private String productImageUrl;
    private String couponMoney;
    public static String orderNo = "";
    public static String productNo = "";
    private Intent intent;
    //首付比例
    private int rate = -1;
    private String bankCardNum = "";

    private String payJsonString;
    private String identityid = "";

    public static final String KEY_OF_BANK_NAME = "bankNameKey";
    public static final String KEY_OF_BANK_CARD_NUM = "bankCardNumKey";
    private final int REQUEST_CODE_OF_BANK_CARD = 1001;
    private static String comeFrom = "";

    @OnClick({R.id.rl_payActivity_bankCard, R.id.iv_back, R.id.btn_payActivity_confirmPay,
            R.id.rl_payActivity_addBankCard})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case R.id.rl_payActivity_bankCard:
                //本期没有这个功能，放在第二期做
//			Intent intent = new Intent(this, BankCardListActivity.class);
//			intent.putExtra(BankCardListActivity.KEY_OF_BANK_CARD_COME_FROM, TAG);
//			startActivityForResult(intent, REQUEST_CODE_OF_BANK_CARD);
                break;
            case R.id.iv_back:
                back();
                break;
            case R.id.rl_payActivity_addBankCard:
                Intent intent = new Intent(this, AddBankCardActivity.class);
                intent.putExtra(PayActivity.KEY_OF_BANK_CARD_NUM, defaultBankCard.getBankCardNum());
                startActivity(intent);
                break;
            case R.id.btn_payActivity_confirmPay:
                if (bankCardRelativeLayout.getVisibility() == View.VISIBLE
                        && StringUtil.notEmpty(defaultBankCard.getBankCardNum())) {
                    bankCardNum = defaultBankCard.getBankCardNum();
                }

                if (StringUtil.isNull(bankCardNum)) {
                    Toast.makeText(this, "请输入银行卡号", Toast.LENGTH_SHORT).show();
                    return;
                }
                disableConfirmPayButton();
                TDUtils.onEvent(this, "100016", "点击确认支付",TDUtils.getInstance().putUserid().buildParams());
                payOrder(orderNo, bankCardNum, mHandler, this);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        ButterKnife.inject(this);

        intent = getIntent();
        comeFrom = intent.getStringExtra(LoginActivity.KEY_OF_COME_FROM);
        orderNo = intent.getStringExtra(ConfirmOrderActivity.KEY_OF_ORDER_NO);
        productNo = intent.getStringExtra(ConfirmOrderActivity.KEY_OF_PRODUCT_NO);
        productName = intent.getStringExtra(ConfirmOrderActivity.KEY_OF_PRODUCT_NAME);
        productPrice = intent.getStringExtra(ConfirmOrderActivity.KEY_OF_PRODUCT_PRICE);
        skuParameters = intent.getStringExtra(ConfirmOrderActivity.KEY_OF_PRODUCT_SKUPARAM);
        downPayment = intent.getStringExtra(ConfirmOrderActivity.KEY_OF_DOWNPAYMENT_PRICE);
        monthOfInstallment = intent.getStringExtra(ConfirmOrderActivity.KEY_OF_INSTALLMENT);
        productImageUrl = intent.getStringExtra(ConfirmOrderActivity.KEY_OF_PRODUCT_IMAGE_URL);
        couponMoney = intent.getStringExtra(ConfirmOrderActivity.KEY_OF_COUPON_MOENY);
        rate = intent.getIntExtra(ConfirmOrderActivity.KEY_OF_DOWNPAYMENT_RATE, -1);
        if (rate == 0) {
            downPaymentTextView.setVisibility(View.GONE);
            downPaymentPriceTextView.setVisibility(View.GONE);
        }
        //隐藏 “红包金额”
        borderredMoneyDayView.setVisibility(View.GONE);
        redMoneyTextView.setVisibility(View.GONE);
        setRedMoneyTextView.setVisibility(View.GONE);
        tvTitle.setText(R.string.str_pagename_pay);
        CommonUtils.setTitle(this, rlTitle);
        disableConfirmPayButton();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBankCardList();
        productNameTextView.setText(productName);
        productPriceTextView.setText(productPrice);
        tvGoodsSpu.setText(skuParameters);
        setOrderMoneyTextView.setText(intent.getStringExtra(ConfirmOrderActivity.KEY_OF_ORDER_MOENY));
        if (StringUtil.notEmpty(couponMoney)) {
            //显示 “红包金额”
            borderredMoneyDayView.setVisibility(View.VISIBLE);
            redMoneyTextView.setVisibility(View.VISIBLE);
            setRedMoneyTextView.setVisibility(View.VISIBLE);
            setRedMoneyTextView.setText(couponMoney);
        }
        setActualMoneyTextView.setText(intent.getStringExtra(ConfirmOrderActivity.KEY_OF_ACTUAL_MOENY));
        downPaymentPriceTextView.setText(downPayment);
        monthlyPaymentPriceTextView.setText(monthOfInstallment);
        Picasso.with(this).load(productImageUrl).placeholder(R.drawable.ic_launcher)
                .into(productImageView);
    }

    @Override
    protected String getPageName() {
        return getString(R.string.str_pagename_pay);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case REQUEST_CODE_OF_BANK_CARD:
                    defaultBankCard.setBankName(data.getStringExtra(KEY_OF_BANK_NAME));
                    defaultBankCard.setBankCardNum(data.getStringExtra(KEY_OF_BANK_CARD_NUM));
                    bankNameTextView.setText(data.getStringExtra(KEY_OF_BANK_NAME));
                    String bankCardNum = data.getStringExtra(KEY_OF_BANK_CARD_NUM);
                    bankCardNum = "**** **** **** " + bankCardNum.substring(bankCardNum.length() - 4);
                    bankCardNumTextView.setText(bankCardNum);
                    break;
            }
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            back();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * 点击back键
     */
    private void back() {
        final Dialog dialog = new Dialog(this, R.style.protocalDialog);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        View view = View.inflate(this, R.layout.pay_activity_back_dialog, null);
        view.findViewById(R.id.tv_payActivity_goOnPay).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }

        });
        view.findViewById(R.id.tv_payActivity_leave).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (ConfirmOrderActivity.TAG.equals(comeFrom)) {
                    Intent intent = new Intent(PayActivity.this, MyOrderActivity.class);
                    intent.putExtra(MyOrderActivity.KEY_OF_QUERY_ORDER, MyOrderActivity.ORDER_STATUS_WAIT_FOR_PAY);
                    startActivity(intent);
                    ActivityManager.getInstance().finishOrderAllActivity();
                }
                finish();
            }

        });

        dialog.setContentView(view);
        dialog.setTitle("");
        dialog.show();
    }

    /**
     * 获取第一个银行卡号
     */
    public void getBankCardList() {
        final LoadingAlertDialog mDialog = new LoadingAlertDialog(this);
        mDialog.show(ConstantsUtil.NETWORK_REQUEST_IN);

        Map<String,String> params = new HashMap<>();
        params.put("uid", AppContext.user.getUid());
        params.put("token", AppContext.user.getToken());
        params.put("rates", rate+"");

        HttpRequest.post(mContext, HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.GET_BANK_CARD_LIST, params,
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
                                defaultBankCard = JSON.parseObject(orderJsonArray.get(0).toString(), BankCard.class);
                                bankCardNum = defaultBankCard.getBankCardNum();
                            }

                            if (orderJsonArray.size() > 0 && !BankCardListActivity.BANK_CARD_CHANNEL_WAN_KA.equals(defaultBankCard.getChannel())) {
                                enableConfirmPayButton();
                                bankCardRelativeLayout.setVisibility(View.VISIBLE);
                                addBankCardRelativeLayout.setVisibility(View.GONE);

                                bankNameTextView.setText(defaultBankCard.getBankName());
                                identityid = defaultBankCard.getYbIdentityid();
                                String bankCardNum = defaultBankCard.getBankCardNum();
                                bankCardNum = "**** **** **** " + bankCardNum.substring(bankCardNum.length() - 4);
                                bankCardNumTextView.setText(bankCardNum);
                            } else {
                                showInputBankCardNum();
                            }

                        } else if (ConstantsUtil.RESPONSE_NO_DATA == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                            showInputBankCardNum();
                        } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == jsonObject.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                            UserUtils.tokenFailDialog(PayActivity.this, jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), TAG);
                        } else {
                            showInputBankCardNum();
                            Toast.makeText(PayActivity.this, jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailed(String path, String msg) {
                        if (mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        Toast.makeText(PayActivity.this, ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER + "获取银行卡列表失败",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 显示输入银行卡号的edittext
     */
    private void showInputBankCardNum() {
        addBankCardRelativeLayout.setVisibility(View.VISIBLE);
        bankCardRelativeLayout.setVisibility(View.GONE);
    }

    /**
     * 支付订单
     *
     * @param orderNo     订单编号
     * @param bankCardNum 银行卡号
     */
    public void payOrder(String orderNo, String bankCardNum, final Handler handler, final Activity activity) {
        Map<String,String> params = new HashMap<>();
        params.put("uid", AppContext.user.getUid());
        params.put("token", AppContext.user.getToken());
        params.put("orderId", orderNo);
        params.put("name", "");
        params.put("idCard", "");
        params.put("bankcardno", bankCardNum);
        params.put("identityid", identityid);

        HttpRequest.post(mContext, HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.PAY_ORDER, params,
                new AsyncResponseCallBack() {

                    @Override
                    public void onFailed(String path, String msg) {
                        Toast.makeText(activity, ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER + "支付失败",
                                Toast.LENGTH_SHORT).show();
                        enableConfirmPayButton();

                    }

                    @Override
                    public void onResult(String arg2) {
                        String jsonStr = new String(arg2);
                        LogUtil.printLog("支付：" + jsonStr);

                        JSONObject jsonObject = JSON.parseObject(jsonStr);

                        if (ConstantsUtil.RESPONSE_SUCCEED == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                            String paymentInfoJsonObject = jsonObject.getString("PaymentInfo");

                            MobileSecurePayer msp = new MobileSecurePayer();
                            LogUtil.printLog("连连支付请求参数：" + JSON.toJSONString(paymentInfoJsonObject));
                            boolean bRet = msp.payAuth(paymentInfoJsonObject, handler, ConstantsUtil.RQF_PAY,
                                    activity, false);
                        } else if (25 == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                            //25 零首付支付成功
                            paySuccess(PayActivity.this, comeFrom);

                        } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == jsonObject.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                            UserUtils.tokenFailDialog(activity, jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), TAG);
                        } else {
                            String errorMsg = "支付失败";
                            if (StringUtil.notEmpty(jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME))) {
                                errorMsg = jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME);
                            }
                            Toast.makeText(activity, errorMsg, Toast.LENGTH_SHORT).show();
                        }
                        enableConfirmPayButton();
                    }
                });
    }
//
//	private PayOrder constructPreCardPayOrder() {
//
//		SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMddHHmmss");
//		Date date = new Date();
//		String timeString = dataFormat.format(date);
//
//		PayOrder order = new PayOrder();
//		order.setBusi_partner("101001");
//		order.setNo_order(timeString);
//		order.setDt_order(timeString);
//		order.setName_goods("龙禧大酒店中餐厅：2-3人浪漫套餐X1");
//		order.setNotify_url(ConstantsUtil.NOTIFY_URL);
//		// MD5 签名方式
//		// order.setSign_type(PayOrder.SIGN_TYPE_MD5);
//		// RSA 签名方式
//		order.setSign_type(PayOrder.SIGN_TYPE_RSA);
//
//		order.setValid_order("100");
//
//		// 银行卡卡号，该卡首次支付时必填
////		order.setCard_no(((EditText) findViewById(R.id.et_payActivity_inputBankCardNum)).getText().toString());
//		order.setId_no("410181199007074530");
//		order.setAcct_name("崔朋朋");
//		order.setMoney_order("0.01");
//
//		// 银行卡历次支付时填写，可以查询得到，协议号匹配会进入SDK，
//		// order.setNo_agree(((EditText) findViewById(R.id.agree_no)).getText()
//		// .toString());
//		// order.setUser_id(((EditText) findViewById(R.id.userid))
//		// .getText().toString());
//
//		// 风险控制参数
//		// order.setRisk_item(constructRiskItem());
//
//		String sign = "";
//		order.setOid_partner(ConstantsUtil.PARTNER);
//		String content = BaseHelper.sortParam(order);
//		// MD5 签名方式
//		// sign = Md5Algorithm.getParamsInstance().sign(content,
//		// EnvConstants.MD5_KEY);
//		// RSA 签名方式
//		sign = Rsa.sign(content, ConstantsUtil.RSA_PRIVATE);
//		order.setSign(sign);
//		return order;
//	}

    // 支付验证方式 0：标准版；1：卡前置方式；2：单独签约
    private static int pay_type_flag = 0;
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            String strRet = (String) msg.obj;
            switch (msg.what) {
                case ConstantsUtil.RQF_PAY: {

                    JSONObject objContent = JSON.parseObject(strRet);
                    String retCode = objContent.getString("ret_code");
                    String retMsg = objContent.getString("ret_msg");
                    if ("1004".equals(retCode)) {
                        retMsg = "身份证号和姓名不能为空";
                    }

                    // 成功
                    if (ConstantsUtil.RET_CODE_SUCCESS.equals(retCode)) {
                        // TODO 卡前置模式返回的银行卡绑定协议号，用来下次支付时使用，此处仅作为示例使用。正式接入时去掉
                        if (2 == pay_type_flag) {
                            LogUtil.printLog("签约成功，交易状态码：" + retCode + " 返回报文:" + strRet);
                            break;
                        } else {
                            if (pay_type_flag == 1) {
                            }

                            paySuccess(PayActivity.this, comeFrom);
                            LogUtil.printLog("支付成功，交易状态码：" + retCode + " 返回报文:" + strRet);
                        }

                    } else if (ConstantsUtil.RET_CODE_PROCESS.equals(retCode)) {
                        // TODO 处理中，掉单的情形
                        String resulPay = objContent.getString("result_pay");
                        failToPay(PayActivity.this, retMsg);
                        if (ConstantsUtil.RESULT_PAY_PROCESSING.equalsIgnoreCase(resulPay)) {
                            LogUtil.printLog("交易状态码：" + retCode + " 返回报文:" + strRet);
                        }

                    } else {
                        // TODO 失败

                        failToPay(PayActivity.this, retMsg);
                        LogUtil.printLog("，交易状态码:" + retCode + " 返回报文:" + strRet);
                    }
                }
                break;
            }
            super.handleMessage(msg);
        }

    };

    /**
     * 支付成功跳转
     *
     * @param activity 当前activity
     * @param comeFrom 从哪个页面跳转到支付成功页面
     */
    public void paySuccess(Activity activity, String comeFrom) {
        Intent intent = new Intent(activity, PaySuccessActivity.class);
        intent.putExtra(LoginActivity.KEY_OF_COME_FROM, comeFrom);
        activity.startActivity(intent);
        finish();
    }

    /**
     * 支付失败显示dialog
     *
     * @param activity     当前activity
     * @param errorMessage 支付失败信息
     */
    public static void failToPay(Activity activity, String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        builder.setMessage(errorMessage).setPositiveButton("确定",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.dismiss();
                    }
                }).show();
    }


    /**
     * 启用确认支付按钮
     */
    private void enableConfirmPayButton() {
        confirmPayButton.setEnabled(true);
        confirmPayButton.setBackgroundResource(R.drawable.button_selector);
    }

    /**
     * 禁用确认支付button
     */
    private void disableConfirmPayButton() {
        confirmPayButton.setEnabled(false);
        confirmPayButton.setBackgroundResource(R.drawable.login_page_button_disabled);
    }
}
