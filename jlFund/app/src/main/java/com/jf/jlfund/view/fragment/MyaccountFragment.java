package com.jf.jlfund.view.fragment;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jf.jlfund.R;
import com.jf.jlfund.base.BaseApplication;
import com.jf.jlfund.base.BaseUIFragment;
import com.jf.jlfund.bean.AccountInfoBean;
import com.jf.jlfund.bean.UserInfo;
import com.jf.jlfund.http.HttpRequest;
import com.jf.jlfund.utils.CommonUtil;
import com.jf.jlfund.utils.DensityUtil;
import com.jf.jlfund.utils.LogUtils;
import com.jf.jlfund.utils.SPUtil;
import com.jf.jlfund.utils.StatusBarUtil;
import com.jf.jlfund.utils.StringUtil;
import com.jf.jlfund.view.activity.CurrentPlusActivity;
import com.jf.jlfund.view.activity.FundActivity;
import com.jf.jlfund.view.activity.GestureVerifyActivity;
import com.jf.jlfund.view.activity.LoginActivity;
import com.jf.jlfund.view.activity.MessageActivity;
import com.jf.jlfund.view.activity.TestActivity;
import com.jf.jlfund.view.activity.UserInfoActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 *
 */
public class MyaccountFragment extends BaseUIFragment {

    @BindView(R.id.iv_myaccountFragment_hiddenMoney)
    ImageView hiddenMoneyImageView;
    @BindView(R.id.tv_myaccountFragment_totalAmountValue)
    TextView totalAmountValueTextView;
    @BindView(R.id.tv_myaccountFragment_yesterdayIncomeValue)
    TextView yesterdayIncomeValueTextView;
    @BindView(R.id.tv_myaccountFragment_currentPlusValue)
    TextView currentPlusValueTextView;
    @BindView(R.id.tv_myaccountFragment_currentPlusTotalValue)
    TextView currentPlusTotalValueTextView;
    @BindView(R.id.tv_myaccountFragment_selfFundValue)
    TextView selfFundValueTextView;
    @BindView(R.id.tv_myaccountFragment_selfFundTotalValue)
    TextView selfFundTotalValueTextView;

    @BindView(R.id.rl_myaccountFragment_buyFund)
    RelativeLayout buyFundRelativeLayout;
    @BindView(R.id.rl_myaccountFragment_test)
    RelativeLayout testRelativeLayout;
    @BindView(R.id.rl_myaccountFragment_longTimeInvest)
    RelativeLayout longTimeInvestRelativeLayout;
    @BindView(R.id.rl_myaccountFragment_makeMoneyOversea)
    RelativeLayout makeMoneyOverseaRelativeLayout;

    @BindView(R.id.ll_myaccountFragment_currentPlus)
    LinearLayout currentPlusLinearLayout;
    @BindView(R.id.ll_myaccountFragment_fund)
    LinearLayout fundLinearLayout;

    @BindView(R.id.ll_myaccountFragment_userNoLogin)
    LinearLayout userNoLoginLinearLayout;
    @BindView(R.id.ll_myaccountFragment_userLogin)
    LinearLayout userLoginLinearLayout;

    private boolean currentHiddenMoney = false;
    public int mFromTabIndex = 1;
    public static final String HIDDEN_MONEY_SYMBOL = "****";
    private AccountInfoBean accountInfoBean = new AccountInfoBean();


    @OnClick({R.id.iv_myaccountFragment_hiddenMoney, R.id.rl_myaccountFragment_buyFund,
            R.id.rl_myaccountFragment_test, R.id.rl_myaccountFragment_longTimeInvest, R.id.rl_myaccountFragment_makeMoneyOversea,
            R.id.ll_myaccountFragment_currentPlus, R.id.ll_myaccountFragment_fund})
    public void onViewClick(View v) {
        if (!SPUtil.getInstance().isLogin()) {
            LoginActivity.open(getActivity());
            return;
        }

        switch (v.getId()) {
            case R.id.iv_myaccountFragment_hiddenMoney:
                if (currentHiddenMoney) {
                    showMoneyNumber();
                    currentHiddenMoney = false;
                } else {
                    hiddenMoneyNumber();
                    currentHiddenMoney = true;
                }
                UserInfo userInfo = SPUtil.getInstance().getUserInfo();
                userInfo.setHiddenAccountMoney(currentHiddenMoney);
                SPUtil.writeUserConfigInfo(getActivity(), userInfo);
                break;
            case R.id.ll_myaccountFragment_currentPlus:
                CommonUtil.startActivity(getActivity(), CurrentPlusActivity.class, "000000");
                break;
            case R.id.ll_myaccountFragment_fund:
                FundActivity.open(getContext());
                break;
            case R.id.rl_myaccountFragment_buyFund:
                MobclickAgent.onEvent(getContext(), "click_btn_accountFragment_buyFundSecrct");
                break;
            case R.id.rl_myaccountFragment_test:
                MobclickAgent.onEvent(getContext(), "click_btn_accountFragment_test");
                TestActivity.open(getContext());
                break;
            case R.id.rl_myaccountFragment_longTimeInvest:
                MobclickAgent.onEvent(getContext(), "click_btn_accountFragment_stableInvest");
                break;
            case R.id.rl_myaccountFragment_makeMoneyOversea:
                MobclickAgent.onEvent(getContext(), "click_btn_accountFragment_investOverseas");
                break;
        }
    }

    @OnClick({R.id.ll_myaccountFragment_userNoLogin})
    public void onViewClick2(View v) {
        switch (v.getId()) {
            case R.id.ll_myaccountFragment_userNoLogin:
                LoginActivity.open(getActivity());
                break;
        }
    }

    public static MyaccountFragment newInstance() {
        MyaccountFragment myaccountFragment = new MyaccountFragment();
        return myaccountFragment;
    }

    @Override
    protected String getPageTitle() {
        return null;
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.fragment_myaccount;
    }

    @Override
    protected void initPageData() {
        showBaseUITitle = true;
        currentHiddenMoney = SPUtil.getInstance().getUserInfo().isHiddenAccountMoney();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LogUtils.printLog("");
        GestureVerifyActivity.initAccountFragment = true;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        LogUtils.printLog("isVisibleToUser=" + isVisibleToUser + "SPUtil.getInstance().isLogin()=" + SPUtil.getInstance().isLogin()
                + "--BaseApplication.goIntoAccountSecurity=" + BaseApplication.goIntoAccountSecurity + "--GestureVerifyActivity.initAccountFragment=" + GestureVerifyActivity.initAccountFragment);
        if (isVisibleToUser && SPUtil.getInstance().isLogin() && !BaseApplication.goIntoAccountSecurity && !GestureVerifyActivity.initAccountFragment
                && (GestureVerifyActivity.supportFingerprint(getActivity()) || StringUtil.notEmpty(SPUtil.getInstance().getUserInfo().getGesturePassword()))) {
            LogUtils.printLog("");
            ((BaseApplication) BaseApplication.applicationContext).lockScreenOrBackgroundMinuteSecurityCheck(getActivity(), false, mFromTabIndex);
        }

        if(isVisibleToUser && !GestureVerifyActivity.initAccountFragment){
            getFundCompanyInfo();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }


    @Override
    public void onStart() {
        showContentView();
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        GestureVerifyActivity.initAccountFragment = false;

        showUserLoginOrNologinView(SPUtil.getInstance().isLogin());

        getFundCompanyInfo();
    }

    public void showUserLoginOrNologinView(boolean userLogin) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) titleBarRelativeLayout.getLayoutParams();
        if (userLogin) {
            layoutParams.topMargin = StatusBarUtil.getStatusBarHeight(getActivity());
            titleBarRelativeLayout.setLayoutParams(layoutParams);
            userLoginLinearLayout.setVisibility(View.VISIBLE);
            baseBackRelativeLayout.setVisibility(View.VISIBLE);
            baseRightMenuImageView.setVisibility(View.VISIBLE);
            userNoLoginLinearLayout.setVisibility(View.GONE);
        } else {
            layoutParams.topMargin = 0;
            titleBarRelativeLayout.setLayoutParams(layoutParams);
            userLoginLinearLayout.setVisibility(View.GONE);
            baseBackRelativeLayout.setVisibility(View.GONE);
            baseRightMenuImageView.setVisibility(View.GONE);
            userNoLoginLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void setBackListener() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) baseBackImageView.getLayoutParams();
        layoutParams.width = DensityUtil.dip2px(32);
        layoutParams.height = DensityUtil.dip2px(32);
        baseBackImageView.setLayoutParams(layoutParams);
        baseBackImageView.setBackgroundResource(R.mipmap.headportrait);
        baseBackRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(getContext(), "click_btn_accountFragment_headIcon");
                if (SPUtil.getInstance().isLogin())
                    UserInfoActivity.open(getActivity());
                else
                    LoginActivity.open(getActivity());
            }
        });
    }

    @Override
    protected void setRightMenu() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) baseRightMenuImageView.getLayoutParams();
        layoutParams.width = DensityUtil.dip2px(23);
        layoutParams.height = DensityUtil.dip2px(20);
        baseRightMenuImageView.setLayoutParams(layoutParams);
        baseRightMenuImageView.setBackgroundResource(R.mipmap.myaccount_fragment_message);
        baseRightMenuImageView.setVisibility(View.VISIBLE);
        baseRightMenuImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(getContext(), "click_btn_accountFragment_message");
                if (SPUtil.getInstance().isLogin())
                    MessageActivity.open(getContext());
                else
                    LoginActivity.open(getActivity());
            }
        });
    }

    private void hiddenMoneyNumber() {
        totalAmountValueTextView.setText(HIDDEN_MONEY_SYMBOL);
        yesterdayIncomeValueTextView.setText(HIDDEN_MONEY_SYMBOL);
        yesterdayIncomeValueTextView.setTextColor(getResources().getColor(R.color.appContentColor));
        if (StringUtil.doubleValue(accountInfoBean.getFundvolbalance()) > 0) {
            currentPlusValueTextView.setText(HIDDEN_MONEY_SYMBOL);
            currentPlusValueTextView.setTextColor(getResources().getColor(R.color.appContentColor));
            currentPlusTotalValueTextView.setText("总金额   " + HIDDEN_MONEY_SYMBOL);
        }
        if (StringUtil.doubleValue(accountInfoBean.getTotalAmoney()) > 0) {
            selfFundValueTextView.setText(HIDDEN_MONEY_SYMBOL);
            selfFundValueTextView.setTextColor(getResources().getColor(R.color.appContentColor));
            selfFundTotalValueTextView.setText("总金额   " + HIDDEN_MONEY_SYMBOL);
        }
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) hiddenMoneyImageView.getLayoutParams();
//        layoutParams.width = DensityUtil.dip2px(40);
//        layoutParams.height = DensityUtil.dip2px(19);
        layoutParams.width = DensityUtil.dip2px(16);
        layoutParams.height = DensityUtil.dip2px(8);
        hiddenMoneyImageView.setLayoutParams(layoutParams);
        hiddenMoneyImageView.setBackgroundResource(R.mipmap.myaccount_fragment_hidden_money_number);
    }

    private void showMoneyNumber() {
        totalAmountValueTextView.setText(StringUtil.moneyDecimalFormat2(accountInfoBean.getTotalfundmarketvalue_mode1()));
        StringUtil.setMoneyTextView(getActivity(), accountInfoBean.getTotaldailyincome(), yesterdayIncomeValueTextView);
//        if (StringUtil.doubleValue(accountInfoBean.getFundvolbalance()) > 0) {
            currentPlusTotalValueTextView.setText("总金额   " + StringUtil.moneyDecimalFormat2(accountInfoBean.getFundvolbalance()));
            StringUtil.setMoneyTextView(getActivity(), accountInfoBean.getTotalyestincome(), currentPlusValueTextView);
//        }

//        if (StringUtil.doubleValue(accountInfoBean.getTotalAmoney()) > 0) {
            selfFundTotalValueTextView.setText("总金额   " + StringUtil.moneyDecimalFormat2(accountInfoBean.getTotalAmoney()));
            StringUtil.setMoneyTextView(getActivity(), accountInfoBean.getYestodayIncome(), selfFundValueTextView);
//        }
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) hiddenMoneyImageView.getLayoutParams();
        layoutParams.width = DensityUtil.dip2px(16);
        layoutParams.height = DensityUtil.dip2px(12);
        hiddenMoneyImageView.setLayoutParams(layoutParams);
        hiddenMoneyImageView.setBackgroundResource(R.mipmap.myaccount_fragment_show_money_number);
    }

    private void getFundCompanyInfo() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", SPUtil.getInstance().getToken());

        HttpRequest.post(HttpRequest.JL_FUND_TEST_ENV_WEB_URL + HttpRequest.GET_ACCOUNT_INFO, params, this, false, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                accountInfoBean = JSON.parseObject(response.body(), AccountInfoBean.class);

                boolean userHasTraded = true;
                if (userHasTraded) {
                    if (currentHiddenMoney) {
                        hiddenMoneyNumber();
                    } else {
                        showMoneyNumber();
                    }
                } else {
                    totalAmountValueTextView.setText("0.00");
                    yesterdayIncomeValueTextView.setText("0.00");
                    yesterdayIncomeValueTextView.setTextColor(getResources().getColor(R.color.appTitleColor));
                    currentPlusValueTextView.setText("");
                    currentPlusTotalValueTextView.setText("存闲钱，赚收益");
                    selfFundValueTextView.setText("");
                    selfFundTotalValueTextView.setText("买好基，赚复利");
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }

    @Override
    protected boolean isCountPage() {
        return true;
    }
}
