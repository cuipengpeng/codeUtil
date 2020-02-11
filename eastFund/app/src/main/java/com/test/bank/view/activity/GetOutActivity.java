package com.test.bank.view.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.test.bank.R;
import com.test.bank.adapter.TradeRecordListAdapter;
import com.test.bank.base.BaseUIActivity;
import com.test.bank.bean.BaobaoAssertInfoBean;
import com.test.bank.bean.GetOutBankCardInfoBean;
import com.test.bank.bean.PutInAndGetOutResultBean;
import com.test.bank.http.HttpRequest;
import com.test.bank.utils.Aes;
import com.test.bank.utils.LogUtils;
import com.test.bank.utils.SPUtil;
import com.test.bank.utils.StringUtil;
import com.test.bank.weight.ClearEditText;
import com.test.bank.weight.PayPasswordView;
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
public class GetOutActivity extends BaseUIActivity {

    @BindView(R.id.tv_getOutActivity_bankIcon)
    ImageView bankIconImageView;
    @BindView(R.id.tv_getOutActivity_bankName)
    TextView bankNameTextView;
    @BindView(R.id.tv_getOutActivity_allMoney)
    TextView allMoneyTextView;
    @BindView(R.id.tv_getOutActivity_getOutTips)
    TextView getOutTipsTextView;
    @BindView(R.id.ll_getOutActivity_getOutTips)
    LinearLayout getOutTipsLinearLayout;
    @BindView(R.id.et_getOutActivity_inputMoney)
    ClearEditText inputMoneyEditText;

    @BindView(R.id.tv_getOutActivity_quickGetOutContent)
    TextView quickGetOutContentTextView;
    @BindView(R.id.tv_getOutActivity_quickGetOutIcon)
    ImageView quickGetOutIconImageView;
    @BindView(R.id.rl_getOutActivity_quickGetOut)
    RelativeLayout QuickGetOutRelativeLayout;
    @BindView(R.id.tv_getOutActivity_commonGetOutIcon)
    ImageView commonGetOutIconImageView;
    @BindView(R.id.tv_getOutActivity_commonGetOutContent)
    TextView commonGetOutContentTextView;
    @BindView(R.id.rl_getOutActivity_commonGetOut)
    RelativeLayout CommonGetOutRelativeLayout;

    @BindView(R.id.tv_getOutActivity_confirmGetOut)
    TextView confirmGetOutTextView;

    private boolean userLogin = true;
    public static final String QUICK_GET_OUT = "3";
    public static final String COMMON_GET_OUT = "0";
    private String currentGetOutType = QUICK_GET_OUT;//赎回类型（宝宝赎回时专用字段，0-普通赎回，3-T+0快速赎回）
    private GetOutBankCardInfoBean getOutBankCardInfoBean = new GetOutBankCardInfoBean();
    private String maxWithdraw = "";

    public static final String KEY_OF_BAOBAO_ASSERT_INFO_BEAN = "bobaoAssertInfoBeanKey";
    private BaobaoAssertInfoBean baobaoAssertInfoBean = new BaobaoAssertInfoBean();
    public PayPasswordView payPasswordView;

    @OnClick({R.id.tv_getOutActivity_confirmGetOut, R.id.rl_getOutActivity_quickGetOut, R.id.rl_getOutActivity_commonGetOut,
                R.id.tv_getOutActivity_allMoney})
    public void onItemClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.tv_getOutActivity_confirmGetOut:
                MobclickAgent.onEvent(this, "click_btn_currPlusGetOutActivity_confirmGetOut");  //统计点击事件
                disableGetOutButton();
                inputPayPasswd(this, new PayPasswordView.OnPayListener() {
                    @Override
                    public void onCancelPay() {

                    }

                    @Override
                    public void onSurePay(String password, PayPasswordView payPasswordView) {
                        confirmGetOut(password);
                    }
                });
                enableGetOutButton();
                break;
            case R.id.tv_getOutActivity_allMoney:
                inputMoneyEditText.setText(maxWithdraw);
                inputMoneyEditText.setSelection(maxWithdraw.length());
                break;
            case R.id.rl_getOutActivity_quickGetOut:
                MobclickAgent.onEvent(this, "click_btn_currPlusGetOutActivity_quickGetOut");  //统计点击事件
                selectQuickGetOut();
                break;
            case R.id.rl_getOutActivity_commonGetOut:
                MobclickAgent.onEvent(this, "click_btn_currPlusGetOutActivity_commonGetOut");  //统计点击事件
                selectCommonGetOut() ;
                break;
        }
    }

    private void disableQuickGetOutOption() {
        quickGetOutIconImageView.setBackgroundResource(R.mipmap.get_out_activity_unselected);
        QuickGetOutRelativeLayout.setEnabled(false);
    }

    private void enableQuickGetOutOption() {
        QuickGetOutRelativeLayout.setEnabled(true);
    }

    private void selectQuickGetOut() {
        currentGetOutType = QUICK_GET_OUT;
        hideQuickAndCommonImageView();
        quickGetOutIconImageView.setBackgroundResource(R.mipmap.put_in_result_activity_flow_step1_selected);
        enableQuickGetOutOption();
    }

    private void selectCommonGetOut() {
        currentGetOutType = COMMON_GET_OUT;
        hideQuickAndCommonImageView();
        commonGetOutIconImageView.setBackgroundResource(R.mipmap.put_in_result_activity_flow_step1_selected);
    }

    private void hideQuickAndCommonImageView() {
        quickGetOutIconImageView.setBackgroundResource(R.mipmap.get_out_activity_unselected);
        commonGetOutIconImageView.setBackgroundResource(R.mipmap.get_out_activity_unselected);
    }

    private void enableGetOutButton() {
        confirmGetOutTextView.setEnabled(true);
        confirmGetOutTextView.setBackgroundResource(R.mipmap.current_plus_activity_begin_put_in_enable);
    }

    private void disableGetOutButton() {
        confirmGetOutTextView.setEnabled(false);
        confirmGetOutTextView.setBackgroundResource(R.mipmap.current_plus_activity_begin_put_in_disabled);
    }


    @Override
    protected String getPageTitle() {
        return "取出";
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_get_out;
    }

    @Override
    protected void initPageData() {
        baobaoAssertInfoBean = (BaobaoAssertInfoBean) getIntent().getSerializableExtra(KEY_OF_BAOBAO_ASSERT_INFO_BEAN);

        payPasswordView = new PayPasswordView(this, new Dialog(this), null);
        disableGetOutButton();
        if(baobaoAssertInfoBean.getIsStopQuickRedeem()){
            disableQuickGetOutOption();
            selectCommonGetOut();
        }else {
            selectQuickGetOut();
        }
        getOutTipsLinearLayout.setVisibility(View.GONE);
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
                str = StringUtil.checkInputMoney(str, inputMoneyEditText);
                LogUtils.printLog(str);

                disableGetOutButton();
                getOutTipsLinearLayout.setVisibility(View.GONE);
                if(!baobaoAssertInfoBean.getIsStopQuickRedeem()){
                    enableQuickGetOutOption();
                }

                if(StringUtil.notEmpty(str)) {

                    if (Double.valueOf(str) < StringUtil.doubleValue(getOutBankCardInfoBean.getPer_min())) {
                        getOutTipsLinearLayout.setVisibility(View.VISIBLE);
                        getOutTipsTextView.setText("最低取出金额"+StringUtil.moneyDecimalFormat2(getOutBankCardInfoBean.getPer_min()) +"元");
                        getOutTipsTextView.setTextColor(getResources().getColor(R.color.appRedColor));
                    }

                    if(StringUtil.doubleValue(str)>StringUtil.doubleValue(getOutBankCardInfoBean.getMax_redemption_stare())){
                        String  maxRedemptionStare = getOutBankCardInfoBean.getMax_redemption_stare();
                        inputMoneyEditText.setText(maxRedemptionStare);
                        inputMoneyEditText.setSelection(maxRedemptionStare.length());
                        enableGetOutButton();
                    }else {
                        enableGetOutButton();
                        if(StringUtil.doubleValue(str)>StringUtil.doubleValue(getOutBankCardInfoBean.getSingleLimit())
                                || (StringUtil.doubleValue(str))>StringUtil.doubleValue(getOutBankCardInfoBean.getSingleDayLimit())) {
                            disableQuickGetOutOption();
                            selectCommonGetOut();
                        }
                    }

                    if(StringUtil.doubleValue(str)>=StringUtil.doubleValue(getOutBankCardInfoBean.getMax_redemption_stare()) && StringUtil.doubleValue(getOutBankCardInfoBean.getUnpaidincome()+"")>0){
                        getOutTipsLinearLayout.setVisibility(View.VISIBLE);
                        getOutTipsTextView.setText("剩余未结转收益"+StringUtil.moneyDecimalFormat2(getOutBankCardInfoBean.getUnpaidincome())+"元在下个工作日自动到账");
                        getOutTipsTextView.setTextColor(getResources().getColor(R.color.appRedColor));
                    }

                    double leftMoney = StringUtil.doubleValue(getOutBankCardInfoBean.getMax_redemption_stare())-StringUtil.doubleValue(str);
                    if(leftMoney >0 && leftMoney<StringUtil.doubleValue(getOutBankCardInfoBean.getHoldmin())){
                        getOutTipsLinearLayout.setVisibility(View.VISIBLE);
                        getOutTipsTextView.setText("剩余金额不可小于"+StringUtil.moneyDecimalFormat2(getOutBankCardInfoBean.getHoldmin())+"元，请全部取出");
                        getOutTipsTextView.setTextColor(getResources().getColor(R.color.appRedColor));
                        disableGetOutButton();
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        getFundCompanyInfo();
    }

    private void getFundCompanyInfo() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", SPUtil.getInstance().getToken());
        params. put("fundcode", baobaoAssertInfoBean.getFundCode());
        params.put("type", "1"); //type为0时必须传入fundcode，为1时无需传入默认fundcode为宝宝代码: 按接口要求"活期+"页面type写死传1

        HttpRequest.post(false, this, HttpRequest.APP_INTERFACE_WEB_URL + HttpRequest.PUT_IN_AND_GET_OUT_BANK_CARD_INFO, params, new HttpRequest.HttpResponseCallBack() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                getOutBankCardInfoBean = JSON.parseObject(response.body(), GetOutBankCardInfoBean.class);
                Glide.with(GetOutActivity.this).load(getOutBankCardInfoBean.getBank_logo_url()).into(bankIconImageView);
                bankNameTextView.setText(getOutBankCardInfoBean.getBank_name()+"(尾号"+getOutBankCardInfoBean.getBank_card()+")");
                maxWithdraw = StringUtil.moneyDecimalFormat2(getOutBankCardInfoBean.getMax_redemption_stare());
                inputMoneyEditText.setHint("最多可取现"+maxWithdraw+"元");

                commonGetOutContentTextView.setText("预计"+getOutBankCardInfoBean.getRedeemrefunddate()+"前到账，无限额，到账前一日仍有收益");
                int onceLimit = StringUtil.intValue(getOutBankCardInfoBean.getSingleLimit())/10000;
                int singleDayLimit = StringUtil.intValue(getOutBankCardInfoBean.getSingleDayLimit())/10000;
                quickGetOutContentTextView.setText("预计2小时内到账，每笔限额"+onceLimit+"万，单日限额"+singleDayLimit+"万");
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }


    private void confirmGetOut(String tradePassword) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", SPUtil.getInstance().getToken());
        params.put("buytype", "1");//购买类型（0基金购买，1宝宝购买）, 活期+即宝宝
        //按接口要求，buytype=1时，该字段不传
//        params.put("fundcode", baobaoAssertInfoBean.getFundCode());//基金代码（buytype为0时必须传入fundcode，为1时无需传入）
        params.put("buyShare", inputMoneyEditText.getText().toString().trim());
        params.put("pwd", Aes.encryptAES(tradePassword));
        params.put("redemptionType", currentGetOutType);//赎回类型（宝宝赎回时专用字段，0-普通赎回，3-T+0快速赎回）
        params.put("fundType", getOutBankCardInfoBean.getFundType()+"");

        HttpRequest.post(false, this, HttpRequest.APP_INTERFACE_WEB_URL + HttpRequest.CURRENT_PLUS_FUND_REDEMPTION, params, TAG, new HttpRequest.HttpResponseCallBack() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                PutInAndGetOutResultBean putInAndGetOutResultBean = JSON.parseObject(response.body(), PutInAndGetOutResultBean.class);
                Intent intent = new Intent(GetOutActivity.this, PutInResultActivity.class);
                intent.putExtra(TradeRecordListAdapter.KEY_OF_TRADE_RECORD_NUMBER, putInAndGetOutResultBean.getTradeId());
                if(COMMON_GET_OUT.equals(currentGetOutType)){
                    intent.putExtra(PutInResultActivity.KEY_OF_TRADE_TYPE_AND_STATUS, PutInResultActivity.GET_OUT_RESULT_COMMON_GET_OUT);
                }else {
                    intent.putExtra(PutInResultActivity.KEY_OF_TRADE_TYPE_AND_STATUS, PutInResultActivity.GET_OUT_RESULT_QUICK_GET_OUT);
                }
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

}

