package com.test.bank.view.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.base.BaseActivity;
import com.test.bank.base.BaseBean;
import com.test.bank.bean.UserInfo;
import com.test.bank.bean.UserInfoBean;
import com.test.bank.http.NetService;
import com.test.bank.http.ParamMap;
import com.test.bank.inter.OnResponseListener;
import com.test.bank.utils.Aes;
import com.test.bank.utils.NetUtil;
import com.test.bank.utils.SPUtil;
import com.test.bank.utils.StringUtil;
import com.test.bank.utils.ToastUtils;
import com.test.bank.utils.UIUtils;
import com.test.bank.weight.dialog.CommonDialogFragment;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;

public class UserInfoActivity extends BaseActivity {

    @BindView(R.id.tv_userInfo_phoneNum)
    TextView tvPhoneNum;
    @BindView(R.id.tv_userInfo_bankCard)
    TextView tvBankStatus;
    @BindView(R.id.tv_userInfo_riskPreference)
    TextView tvRiskPreference;
    @BindView(R.id.tv_userInfo_logout)
    TextView tvLogout;

    String phoneNum;

    @Override
    protected void init() {
        phoneNum = SPUtil.getInstance().getMobile();
        if (!TextUtils.isEmpty(phoneNum)) {
            phoneNum = StringUtil.hidePhoneNumber(Aes.decryptAES(phoneNum));
            tvPhoneNum.setText(phoneNum);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_info;
    }

    @Override
    protected void doBusiness() {
        postRequest(new OnResponseListener<UserInfoBean>() {
            @Override
            public Observable<BaseBean<UserInfoBean>> createObservalbe() {
                ParamMap paramMap = new ParamMap();
                paramMap.putLast("token", SPUtil.getInstance().getToken());
                return NetService.getNetService().getUserInfo(paramMap);
            }

            @Override
            public void onResponse(BaseBean<UserInfoBean> result) {
                if (result.isSuccess() && result.getData() != null) {
                    data = result.getData();
                    inflateData(result.getData());
                }
            }

            @Override
            public void onError(String errorMsg) {

            }
        });
    }

    private boolean isBindBandCard;

    private UserInfoBean data;

    private void inflateData(UserInfoBean data) {
        UserInfo userInfo = SPUtil.getInstance().getUserInfo();
        userInfo.setRiskLevel(data.getRiskLevel());
        userInfo.setWhite(data.getIsWhite());
        userInfo.setBindBankCard(data.getBindBankCard());
        if (data.getBindBankCard()) {
            userInfo.setBankCard(data.getBankCard());
            userInfo.setBankName(data.getBankName());
        }
        SPUtil.getInstance().putUserInfo(userInfo);

        if (data.getBindBankCard() != null && tvBankStatus != null) {
            if (data.getBindBankCard() && !TextUtils.isEmpty(data.getBankName()) && !TextUtils.isEmpty(data.getBankCard())) {
                isBindBandCard = true;
                tvBankStatus.setText(data.getBankName() + "(" + data.getBankCard() + ")");
            } else {
                tvBankStatus.setText("未绑定");
            }
        }

        if (TextUtils.isEmpty(data.getRiskLevel())) {
            UIUtils.setText(tvRiskPreference, "未测评");
        } else {
            UIUtils.setText(tvRiskPreference, data.getRiskLevel());
        }
    }

    @OnClick({R.id.rl_userInfo_phone, R.id.rl_userInfo_bankCard, R.id.rl_userInfo_riskPreference, R.id.rl_userInfo_pwdManager, R.id.rl_userInfo_hotline, R.id.tv_userInfo_logout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_userInfo_phone:
                break;
            case R.id.rl_userInfo_bankCard:
                MobclickAgent.onEvent(this, "click_btn_userInfoActivity_bankCard");
                if (isBindBandCard) {
                    BankCardActivity.open(this, data.getBankCard(), data.getBankName(), data.getBankBg(), data.getBankImgx3());
                } else {
                    RapidBindCardActivity.open(this);
                }
                break;
            case R.id.rl_userInfo_riskPreference:
                MobclickAgent.onEvent(this, "click_btn_userInfoActivity_riskPreference");
                if (isBindBandCard) {
                    RiskPreferenceActivity.open(this, data.getRiskLevel());
                } else {
                    RapidBindCardActivity.open(this);
                }
                break;
            case R.id.rl_userInfo_pwdManager:
                MobclickAgent.onEvent(this, "click_btn_userInfoActivity_pwdManager");
                PwdManagerActivity.open(this);
                break;
            case R.id.rl_userInfo_hotline:
                MobclickAgent.onEvent(this, "click_btn_userInfoActivity_hotline");
                UIUtils.showDialDialog(this);
                break;
            case R.id.tv_userInfo_logout:
                showCommonDialog("您确定退出当前账户吗？", "取消", "确定", null, new CommonDialogFragment.OnRightClickListener() {
                    @Override
                    public void onClickRight() {
                        NetUtil.setJPushAliasAndTags("");
                        SPUtil.getInstance().clearUserInfo();
                        MobclickAgent.onProfileSignOff();
                        ToastUtils.showShort("已退出");
                        finish();
                    }
                });
                break;
        }
    }

    public static void open(Context context) {
        context.startActivity(new Intent(context, UserInfoActivity.class));
    }
}
