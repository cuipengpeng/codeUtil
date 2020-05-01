package com.jfbank.qualitymarket.activity;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.base.BaseActivity;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.CountDownTask;
import com.jfbank.qualitymarket.util.IDCard;
import com.jfbank.qualitymarket.util.LogUtil;
import com.jfbank.qualitymarket.util.StringUtil;
import com.jfbank.qualitymarket.util.UserUtils;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
 import java.util.HashMap; import java.util.Map;

import org.apache.http.Header;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 绑定银行卡页面
 *
 * @author 崔朋朋
 */
public class AddBankCardActivity extends BaseActivity implements TextWatcher {
    public static final String TAG = AddBankCardActivity.class.getName();
    @InjectView(R.id.rl_title)
    RelativeLayout rlTitle;
    @InjectView(R.id.tv_title)
    TextView tvTitle;
    @InjectView(R.id.tv_addBankCardActivity_viewSupportedBankCard)
    TextView viewSupportedBankCardTextView;
    @InjectView(R.id.et_addBankCardActivity_name)
    EditText etAddBankCardActivityName;
    @InjectView(R.id.et_addBankCardActivity_idcard)
    EditText etAddBankCardActivityIdcard;
    @InjectView(R.id.et_addBankCardActivity_inputBankCardNum)
    EditText inputBankCardNumEditText;
    @InjectView(R.id.btn_addBankCardActivity_bindBankCard)
    Button bindBankCardButton;
    @InjectView(R.id.et_addBankCardActivity_phoneNumber)
    EditText phoneNumberEditText;
    @InjectView(R.id.et_addBankCardActivity_verifyCode)
    EditText verifyCodeEditText;
    @InjectView(R.id.btn_addBankCardActivity_getVerifyCode)
    Button getVerifyCodeButton;

    public static final String ADD_BANK_CARD_TITLE = "addBankCardActivityTitle";
    private String phoneNumber;
    private String bankCardNum;
    private String nameStr;
    boolean isGetID = false;
    private String idCard;
    private final String hiddenSymble = "***********";
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case CountDownTask.COUNT_DOWN_TASK:
                    //点击获取验证码后的倒计时任务
                    getVerifyCodeButton.setText("剩余" + msg.arg1 + "s");
                    CountDownTask.countDwon(msg.arg1, handler, "");
                    break;
                case CountDownTask.COUNT_DOWN_OVER:
                    //倒计时结束，启用获取验证码按钮
                    enableGetVerifyButton();
                    break;

            }
        }

        ;
    };

    @OnClick({R.id.iv_back, R.id.btn_addBankCardActivity_bindBankCard,
            R.id.tv_addBankCardActivity_viewSupportedBankCard, R.id.btn_addBankCardActivity_getVerifyCode})
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_addBankCardActivity_bindBankCard:
                disableBindBankCardButton();
                bindBankCard();
                break;
            case R.id.tv_addBankCardActivity_viewSupportedBankCard:
                //查看支持银行卡
                String supportedBankCard = HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.H5_PAGE_SUB_URL + HttpRequest.H5_PAGE_SUPPORT_BANK_CARD;
                CommonUtils.startWebViewActivity(this, supportedBankCard, false, false);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank_card);
        ButterKnife.inject(this);
        tvTitle.setText(R.string.str_pagename_addbankcard);
        CommonUtils.setTitle(this, rlTitle);
        disableBindBankCardButton();
        setRealNameInfo();
        phoneNumberEditText.addTextChangedListener(this);
        verifyCodeEditText.addTextChangedListener(this);
        inputBankCardNumEditText.addTextChangedListener(this);
        etAddBankCardActivityIdcard.addTextChangedListener(this);
        etAddBankCardActivityName.addTextChangedListener(this);

        String title = getIntent().getStringExtra(ADD_BANK_CARD_TITLE);
        if (StringUtil.notEmpty(title)) {
            tvTitle.setText(title);
        }

        String bankCardNum = getIntent().getStringExtra(PayActivity.KEY_OF_BANK_CARD_NUM);
        if (StringUtil.notEmpty(bankCardNum)) {
            inputBankCardNumEditText.setText(bankCardNum);
            phoneNumberEditText.setText(AppContext.user.getMobile());
        }

        etAddBankCardActivityName.setText(AppContext.user.getIdName());
        String idNumber = AppContext.user.getIdNumber();
        if (StringUtil.notEmpty(idNumber)) {
            idNumber = idNumber.substring(0, 3) + hiddenSymble + idNumber.substring(idNumber.length() - 4, idNumber.length());
        }
        etAddBankCardActivityIdcard.setText(idNumber);

    }

    @Override
    protected String getPageName() {
        return getString(R.string.str_pagename_addbankcard);
    }

    public void bindBankCard() {

        boolean wrongName = false;
        if (!CommonUtils.isChineseByREG(nameStr)) {
            wrongName = true;
        }
        if (null == nameStr || "".equals(nameStr) || nameStr.length() < 2 || wrongName) {
            Toast.makeText(this, "请输入正确的姓名", Toast.LENGTH_SHORT).show();
            enableBindBankCardButton();
            return;
        }

        if ("".equals(idCard) || 18 != idCard.length() || !"".equals(IDCard.IDCardValidate(idCard)) || !CommonUtils.isCorrectIdcard(idCard)) {
            Toast.makeText(this, "请输入正确的身份证号", Toast.LENGTH_SHORT).show();
            enableBindBankCardButton();
            return;
        }

        if (!CommonUtils.isMobilePhoneVerify(phoneNumber)) {
            Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            enableBindBankCardButton();
            return;
        }

        Map<String,String> params = new HashMap<>();
        params.put("uid", AppContext.user.getUid());
        params.put("token", AppContext.user.getToken());
        params.put("bankcardno", inputBankCardNumEditText.getText().toString().trim());
        params.put("bankphone", phoneNumberEditText.getText().toString().trim());
        String paramIdNumber = etAddBankCardActivityIdcard.getText().toString().trim();
        if (paramIdNumber.contains(hiddenSymble)) {
            params.put("idNumber", AppContext.user.getIdNumber());  //不必填
        } else {
            params.put("idNumber", paramIdNumber);  //不必填
        }
        params.put("idName", etAddBankCardActivityName.getText().toString().trim());    //不必填

        HttpRequest.post(mContext, HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.WEIZHIFU_BIND_BANK_CARD, params,
                new AsyncResponseCallBack() {

                    @Override
                    public void onResult(String arg2) {
                        String jsonStr = new String(arg2);
                        LogUtil.printLog("绑定银行卡: " + jsonStr);

                        JSONObject jsonObject = JSON.parseObject(jsonStr);

                        if (ConstantsUtil.RESPONSE_SUCCEED == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                            Toast.makeText(AddBankCardActivity.this, "绑定银行卡成功", Toast.LENGTH_SHORT).show();
                            JSONArray orderJsonArray = jsonObject
                                    .getJSONArray(ConstantsUtil.RESPONSE_DATA_JSON_ARRAY_FIELD_NAME);

                            finish();
                        } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == jsonObject.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                            UserUtils.tokenFailDialog(AddBankCardActivity.this, jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), TAG);
                        } else {
                            String errorMsg = jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME);
                            Toast.makeText(AddBankCardActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        }
                        enableBindBankCardButton();
                    }

                    @Override
                    public void onFailed(String path, String msg) {
                        Toast.makeText(AddBankCardActivity.this, "当前网络不稳定，请稍后再试", Toast.LENGTH_SHORT).show();
                        enableBindBankCardButton();
                    }
                });
    }

    /**
     * 设置实名认证身份的信息
     */
    private void setRealNameInfo() {
        isGetID = AppContext.isLogin && AppContext.user != null && !TextUtils.isEmpty(AppContext.user.getIdName()) && !TextUtils.isEmpty(AppContext.user.getIdNumber());
        if (!isGetID) { // 为true需要手动输入身份相关信息
            etAddBankCardActivityName.setEnabled(true);
            etAddBankCardActivityIdcard.setEnabled(true);
        } else {
            etAddBankCardActivityName.setText(AppContext.user.getIdName());
            etAddBankCardActivityIdcard.setText(AppContext.user.getIdNumber().subSequence(0, 3) + "***********" + AppContext.user.getIdNumber().substring(14, AppContext.user.getIdNumber().length()));
            etAddBankCardActivityName.setEnabled(false);
            idCard = AppContext.user.getIdNumber().toLowerCase();
            nameStr = AppContext.user.getIdName();
            etAddBankCardActivityIdcard.setEnabled(false);
        }
    }

    /**
     * 启用获取短信验证码button
     */
    private void enableGetVerifyButton() {
        getVerifyCodeButton.setEnabled(true);
        getVerifyCodeButton.setBackgroundResource(R.drawable.button_selector);
        getVerifyCodeButton.setText("获取验证码");
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        phoneNumber = phoneNumberEditText.getText().toString();
        bankCardNum = inputBankCardNumEditText.getText().toString();
        boolean isReadNameCard = true;
        if (!isGetID) {
            nameStr = etAddBankCardActivityName.getText().toString();
            idCard = etAddBankCardActivityIdcard.getText().toString().trim().toLowerCase();
            isReadNameCard = !("".equals(idCard) || 18 != idCard.length()) && !(null == nameStr || "".equals(nameStr) || nameStr.length() < 2 || nameStr.length() > 20);
        }
//        if (isReadNameCard&& CommonUtils.isMobilePhoneVerify(phoneNumber) && bankCardNum.length() >= 15) {
        if (isReadNameCard && (bankCardNum.length() >= 16 && bankCardNum.length() <= 19) && (phoneNumber.length() == 11)) {
            enableBindBankCardButton();
        } else {
            disableBindBankCardButton();
        }
    }


    /**
     * 启用绑定银行卡按钮
     */
    private void enableBindBankCardButton() {
        bindBankCardButton.setEnabled(true);
        bindBankCardButton.setBackgroundResource(R.drawable.button_selector);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /**
     * 禁用启用绑定银行卡button
     */
    private void disableBindBankCardButton() {
        bindBankCardButton.setEnabled(false);
        bindBankCardButton.setBackgroundResource(R.drawable.login_page_button_disabled);
    }

}
