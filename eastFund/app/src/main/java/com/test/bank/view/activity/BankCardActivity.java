package com.test.bank.view.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.base.BaseActivity;
import com.test.bank.utils.ImageUtils;
import com.test.bank.utils.UIUtils;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.OnClick;

public class BankCardActivity extends BaseActivity {

    @BindView(R.id.iv_bindBankCard_bg)
    ImageView ivBankBg;

    @BindView(R.id.iv_bindBankCard_icon)
    ImageView ivBankIcon;

    @BindView(R.id.tv_bindBankCard_bankName)
    TextView tvBankName;
    @BindView(R.id.tv_bindBankCard_bankNo)
    TextView tvBankNo;

    private String bankCardNo;
    private String bankName;
    private String bankBgUrl;
    private String bankIconUrl;

    @Override
    protected void init() {
        if (getIntent() != null) {
            bankCardNo = getIntent().getStringExtra(PARAM_BANK_CARD_NO);
            bankName = getIntent().getStringExtra(PARAM_BANK_CARD_NAME);
            bankBgUrl = getIntent().getStringExtra(PARAM_BANK_BG_URL);
            bankIconUrl = getIntent().getStringExtra(PARAM_BANK_ICON_URL);
        }
        if (!TextUtils.isEmpty(bankBgUrl)) {
            ImageUtils.displayImage(this, bankBgUrl, ivBankBg);
        }
        if (!TextUtils.isEmpty(bankIconUrl)) {
            ImageUtils.displayImage(this, bankIconUrl, ivBankIcon);
        }
        UIUtils.setText(tvBankName, bankName);
        UIUtils.setText(tvBankNo, bankCardNo);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bank_card;
    }

    @Override
    protected void doBusiness() {

    }

    @OnClick({R.id.tv_bankCard_hotline})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_bankCard_hotline:
                MobclickAgent.onEvent(this, "client_bankCardActivity_hotline");
                UIUtils.showDialDialog(this);
                break;
        }
    }

    public static String PARAM_BANK_CARD_NO = "bankCardNo";
    public static String PARAM_BANK_CARD_NAME = "bankCardName";
    public static String PARAM_BANK_BG_URL = "bankBgUrl";
    public static String PARAM_BANK_ICON_URL = "bankIconUrl";

    public static void open(Context context, String bankCardNo, String bankName, String bankBgUrl, String bankIconUrl) {
        Intent intent = new Intent(context, BankCardActivity.class);
        intent.putExtra(PARAM_BANK_CARD_NO, bankCardNo);
        intent.putExtra(PARAM_BANK_CARD_NAME, bankName);
        intent.putExtra(PARAM_BANK_BG_URL, bankBgUrl);
        intent.putExtra(PARAM_BANK_ICON_URL, bankIconUrl);
        context.startActivity(intent);
    }
}
