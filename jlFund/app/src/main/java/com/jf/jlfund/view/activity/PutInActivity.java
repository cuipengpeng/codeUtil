package com.jf.jlfund.view.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.jf.jlfund.R;
import com.jf.jlfund.adapter.TradeRecordListAdapter;
import com.jf.jlfund.base.BaseLocalDataActivity;
import com.jf.jlfund.bean.GetOutBankCardInfoBean;
import com.jf.jlfund.bean.PutInAndGetOutResultBean;
import com.jf.jlfund.http.HttpRequest;
import com.jf.jlfund.utils.Aes;
import com.jf.jlfund.utils.ConstantsUtil;
import com.jf.jlfund.utils.LogUtils;
import com.jf.jlfund.utils.SPUtil;
import com.jf.jlfund.utils.StringUtil;
import com.jf.jlfund.utils.UIUtils;
import com.jf.jlfund.weight.ClearEditText;
import com.jf.jlfund.weight.PayPasswordView;
import com.jf.jlfund.weight.dialog.MyPopupDialog;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
*/
public class PutInActivity extends BaseLocalDataActivity {

    @BindView(R.id.tv_putInActivity_bankIcon)
    ImageView bankIconImageView;
    @BindView(R.id.tv_putInActivity_bankName)
    TextView bankNameTextView;
    @BindView(R.id.tv_putInActivity_bankAmount)
    TextView bankAmountTextView;
    @BindView(R.id.et_putInActivity_inputMoney)
    ClearEditText inputMoneyEditText;
    @BindView(R.id.tv_putInActivity_incomeTips)
    TextView incomeTipsTextView;
    @BindView(R.id.tv_putInActivity_confirmPutIn)
    TextView confirmPutInTextView;

    private String fundCode;
    private boolean userLogin = true;
    private GetOutBankCardInfoBean getOutBankCardInfoBean = new GetOutBankCardInfoBean();
    private String incomeTips = "";
    public PayPasswordView payPasswordView;

    @OnClick({R.id.tv_putInActivity_confirmPutIn})
    public void onItemClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.tv_putInActivity_confirmPutIn:
                MobclickAgent.onEvent(this, "click_btn_putInActivity_confirmPutIn");  //统计点击事件
                disablePutInButton();
                inputPayPasswd(this, new PayPasswordView.OnPayListener() {
                    @Override
                    public void onCancelPay() {

                    }

                    @Override
                    public void onSurePay(String password, PayPasswordView payPasswordView) {
                        confirmPutIn(password);
                    }
                });
                enablePutInButton();
                break;
        }
    }


    private void enablePutInButton() {
        confirmPutInTextView.setEnabled(true);
        confirmPutInTextView.setBackgroundResource(R.mipmap.current_plus_activity_begin_put_in_enable);
    }

    private void disablePutInButton() {
        confirmPutInTextView.setEnabled(false);
        confirmPutInTextView.setBackgroundResource(R.mipmap.current_plus_activity_begin_put_in_disabled);
    }

    @Override
    protected String getPageTitle() {
        return "存入";
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_putin;
    }

    @Override
    protected void initPageData() {
        fundCode = getIntent().getStringExtra(SingleFundDetailActivity.KEY_OF_FUND_CODE);

        payPasswordView = new PayPasswordView(this, new Dialog(this), null);
        disablePutInButton();
        inputMoneyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if(StringUtil.notEmpty(s.toString())){
//                    inputMoneyEditText.setTextSize(DensityUtil.sp2px(30));
//                }else {
//                    inputMoneyEditText.setTextSize(DensityUtil.sp2px(24));
//                }

                String str = s.toString();
                LogUtils.printLog(str);
                str = StringUtil.checkInputMoney(str, inputMoneyEditText);

                double fundMaxBuyLimit = StringUtil.doubleValue(getOutBankCardInfoBean.getMax_buy_money());

                disablePutInButton();
                if (StringUtil.notEmpty(str)) {
                    //最多输入9位，大于10亿则截取
                    if (StringUtil.doubleValue(str) > 1000000000) {
                        str = str.substring(0, 9);
                        inputMoneyEditText.setText(str);
                        inputMoneyEditText.setSelection(str.length());
                    }

                    if (Double.valueOf(str) < StringUtil.doubleValue(getOutBankCardInfoBean.getPert_val_low_lim())) {
                        setDefaultIncomeTips("最低买入金额" + StringUtil.moneyDecimalFormat2(getOutBankCardInfoBean.getPert_val_low_lim()) + "元", getResources().getColor(R.color.appRedColor));
                    } else if (Double.valueOf(str) > getOutBankCardInfoBean.getLimit_each() && Double.valueOf(str) < fundMaxBuyLimit) {
                        incomeTipsTextView.setText("买入金额超过银行卡限额");
                        incomeTipsTextView.setTextColor(getResources().getColor(R.color.appRedColor));
                    } else if (Double.valueOf(str) > fundMaxBuyLimit) {
                        incomeTipsTextView.setText("买入金额超过基金限额");
                        incomeTipsTextView.setTextColor(getResources().getColor(R.color.appRedColor));
                    } else {
                        enablePutInButton();
                        setDefaultIncomeTips(incomeTips, getResources().getColor(R.color.appViewFullTextColor));
                    }
                } else {
                    setDefaultIncomeTips(incomeTips, getResources().getColor(R.color.appViewFullTextColor));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        getFundCompanyInfo();
    }

    private void setDefaultIncomeTips(String text, int color) {
        incomeTipsTextView.setText(text);
        incomeTipsTextView.setTextColor(color);
    }

    private void getFundCompanyInfo() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", SPUtil.getInstance().getToken());
        params.put("fundcode", fundCode);
        params.put("type", "1"); //type为0时必须传入fundcode，为1时无需传入默认fundcode为宝宝代码: 按接口要求"活期+"页面type写死传1

        HttpRequest.post(HttpRequest.JL_FUND_TEST_ENV_WEB_URL + HttpRequest.PUT_IN_AND_GET_OUT_BANK_CARD_INFO, params, this, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                getOutBankCardInfoBean = JSON.parseObject(response.body(), GetOutBankCardInfoBean.class);
                Glide.with(PutInActivity.this).load(getOutBankCardInfoBean.getBank_logo_url()).into(bankIconImageView);
                bankNameTextView.setText(getOutBankCardInfoBean.getBank_name() + "(尾号" + getOutBankCardInfoBean.getBank_card() + ")");
//                bankAmountTextView.setText("该卡本次最多支付"+StringUtil.moneyDecimalFormat2(getOutBankCardInfoBean.getLimit_each()+"")+"元");
                bankAmountTextView.setText("该卡本次最多支付" + UIUtils.fillUpZeroInAmountEnd(getOutBankCardInfoBean.getLimit_each() + "", 2, "--") + "元");
                incomeTips = "预计收益到账时间" + getOutBankCardInfoBean.getIncomedate();
                incomeTipsTextView.setText(incomeTips);


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void confirmPutIn(String tradePassword) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", SPUtil.getInstance().getToken());
        params.put("buytype", "1");//购买类型（0基金购买，1宝宝购买）
        //按接口要求，buytype=1时，该字段不传
//        params.put("fundcode", fundCode);//基金代码（buytype为0时必须传入fundcode，为1时无需传入）
        params.put("buyMoney", inputMoneyEditText.getText().toString().trim());
        params.put("pwd", Aes.encryptAES(tradePassword));
        params.put("fundType", getOutBankCardInfoBean.getFundType() + "");

        HttpRequest.post(HttpRequest.JL_FUND_TEST_ENV_WEB_URL + HttpRequest.CURRENT_PLUS_FUND_BUY, params, this, TAG, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                PutInAndGetOutResultBean putInAndGetOutResultBean = JSON.parseObject(response.body(), PutInAndGetOutResultBean.class);
                Intent intent = new Intent(PutInActivity.this, PutInResultActivity.class);
                intent.putExtra(TradeRecordListAdapter.KEY_OF_TRADE_RECORD_NUMBER, putInAndGetOutResultBean.getTradeId());
                intent.putExtra(PutInResultActivity.KEY_OF_TRADE_TYPE_AND_STATUS, PutInResultActivity.PUT_IN_RESULT_SUCCESS);
                startActivity(intent);
                finish();

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }


    /**
     * 弹出交易密码对话框
     *
     * @param activity
     */
    public void inputPayPasswd(final Activity activity, PayPasswordView.OnPayListener onPayListener) {
        Dialog tradePasswordDialog = new Dialog(activity, R.style.payBillInstalmentDialog);
        payPasswordView = new PayPasswordView(activity, tradePasswordDialog, onPayListener);
        final View dialogView = payPasswordView.getView();
        tradePasswordDialog.setCancelable(true);
        tradePasswordDialog.setCanceledOnTouchOutside(true);
        tradePasswordDialog.setContentView(dialogView);
        tradePasswordDialog.setTitle("");

        Window dialogWindow = tradePasswordDialog.getWindow();
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
        tradePasswordDialog.show();
    }


    public static MyPopupDialog popupDialog(final Activity activity, String mContent, final int dialogEvent, final PayPasswordView tradePasswordView, boolean isTwoButton) {
        final MyPopupDialog myAlertDialog = new MyPopupDialog(activity, "", mContent, "取消", "确定", "确定", isTwoButton);
//        3011交易密码错误
//        3013交易密码锁定30分钟
//        3014交易密码锁定


        myAlertDialog.setCancelable(true);
        myAlertDialog.setCanceledOnTouchOutside(false);
        myAlertDialog.mPop_dialog_tv_title.setVisibility(View.GONE);
        switch (dialogEvent) {
            case ConstantsUtil.Response.RESPONSE_CODE_TRADE_PASSWORD_ERROR_3011:
                myAlertDialog.mPop_dialog_tv_left_content.setText("重新输入");
                myAlertDialog.mPop_dialog_tv_right_content.setText("忘记密码");
                break;
            case ConstantsUtil.Response.RESPONSE_CODE_TRADE_PASSWORD_ERROR_3013:
            case ConstantsUtil.Response.RESPONSE_CODE_TRADE_PASSWORD_ERROR_3014:
                myAlertDialog.mPop_dialog_tv_left_content.setText("取消");
                myAlertDialog.mPop_dialog_tv_right_content.setText("找回密码");
                break;
            case ConstantsUtil.Dialog.FINGERPRINT_VERIFY_FIRST_FAIL:
                myAlertDialog.mPop_dialog_tv_content.setGravity(Gravity.CENTER);
                myAlertDialog.mPop_dialog_tv_left_content.setText("取消");
                myAlertDialog.mPop_dialog_tv_right_content.setText("验证手势密码");
                break;
            case ConstantsUtil.Dialog.FINGERPRINT_VERIFY_FIRST_TIP:
                myAlertDialog.mPop_dialog_tv_one_button.setText("取消");
                break;
            case ConstantsUtil.Dialog.GESTURE_VERIFY_FAIL:
                myAlertDialog.setCancelable(false);
                myAlertDialog.mPop_dialog_tv_one_button.setText("重新登录");
                break;
        }

        myAlertDialog.setOnClickListen(new MyPopupDialog.OnClickListen() {

            @Override
            public void rightClick() {
                myAlertDialog.dismiss();
                Intent intent = new Intent();
                switch (dialogEvent) {
                    case ConstantsUtil.Response.RESPONSE_CODE_TRADE_PASSWORD_ERROR_3011:
                    case ConstantsUtil.Response.RESPONSE_CODE_TRADE_PASSWORD_ERROR_3013:
                    case ConstantsUtil.Response.RESPONSE_CODE_TRADE_PASSWORD_ERROR_3014:
                        ResetTradePwdActivity.open(activity);
                        tradePasswordView.mDialog.dismiss();
                        break;
                    case ConstantsUtil.Dialog.FINGERPRINT_VERIFY_FIRST_FAIL:
                        ((GestureVerifyActivity) activity).showGesturePasswordView();
                        break;
                    case ConstantsUtil.Dialog.FINGERPRINT_VERIFY_FIRST_TIP:
                        myAlertDialog.dismiss();
                        break;
                    case ConstantsUtil.Dialog.GESTURE_VERIFY_FAIL:
                        //TODO 提示手势密码失效，点击重新登录跳转至登录页面，同时自动关闭用户的手势密码
                        GestureVerifyActivity.clearUserInfoAndStartLoginActivity(activity);
                        break;
                }
            }

            @Override
            public void leftClick() {
                if (tradePasswordView != null) {
                    tradePasswordView.showKeyBoardView();
                    tradePasswordView.mPasswordList.clear();
                    tradePasswordView.updateUi();
                }

                myAlertDialog.dismiss();
            }
        });
        myAlertDialog.show();

        return myAlertDialog;
    }


    public static void open(Context context, String fundCode) {
        Intent intent = new Intent(context, PutInActivity.class);
        intent.putExtra(SingleFundDetailActivity.KEY_OF_FUND_CODE, fundCode);
        context.startActivity(intent);
    }
}
