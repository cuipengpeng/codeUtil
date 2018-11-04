package com.test.bank.view.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.base.BaseUIActivity;
import com.test.bank.bean.FundArchivesWithAnnouncementBean;
import com.test.bank.view.fragment.FundBuyRuleFragment;

import butterknife.BindView;
import butterknife.OnClick;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
* 时    间：2017/12/11<br>
*/
public class TradeNoticeActivity extends BaseUIActivity {

    @BindView(R.id.tv_positionAllocationActivity_positionAllocation)
    TextView positionAllocationTextView;
    @BindView(R.id.v_positionAllocationActivity_positionAllocationRedLine)
    View positionAllocationRedLineView;
    @BindView(R.id.tv_positionAllocationActivity_industryDistribution)
    TextView industryDistributionTextView;
    @BindView(R.id.v_positionAllocationActivity_industryDistributionRedLine)
    View industryDistributionRedLineView;

    @OnClick({R.id.ll_positionAllocationActivity_positionAllocation, R.id.ll_positionAllocationActivity_industryDistribution})
    public void onItemClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.ll_positionAllocationActivity_industryDistribution:
                showIndustryDistributionFragment();
                break;
            case R.id.ll_positionAllocationActivity_positionAllocation:
                showPositionAllocationFragment();
                break;
        }
    }

    private FragmentManager mFragmentManager;
    private Fragment industryDistributionFragment;
    private Fragment positionAllocationFragment;

    public FundArchivesWithAnnouncementBean mFundArchivesWithAnnouncementBean = new FundArchivesWithAnnouncementBean();

    public String fundCode;

    @Override
    protected String getPageTitle() {
        return "交易规则";
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_trade_notice;
    }

    @Override
    protected void initPageData() {
        fundCode = getIntent().getStringExtra(SingleFundDetailActivity.KEY_OF_FUND_CODE);
        mFundArchivesWithAnnouncementBean = (FundArchivesWithAnnouncementBean) getIntent().getSerializableExtra(SingleFundDetailActivity.KEY_OF_FUND_ARCHIVES_WITH_ANNOUNCEMENT_MODEL);

        mFragmentManager = getSupportFragmentManager();
        showPositionAllocationFragment();
    }

    /**
     * 显示行业分布
     */
    private void showIndustryDistributionFragment() {
        industryDistributionTextView.setTextColor(getResources().getColor(R.color.appRedColor));
        industryDistributionRedLineView.setVisibility(View.VISIBLE);

        positionAllocationTextView.setTextColor(getResources().getColor(R.color.appContentColor));
        positionAllocationRedLineView.setVisibility(View.INVISIBLE);
        showFragent(1);

    }

    /**
     * 显示持仓配置
     */
    private void showPositionAllocationFragment() {
        positionAllocationTextView.setTextColor(getResources().getColor(R.color.appRedColor));
        positionAllocationRedLineView.setVisibility(View.VISIBLE);

        industryDistributionTextView.setTextColor(getResources().getColor(R.color.appContentColor));
        industryDistributionRedLineView.setVisibility(View.INVISIBLE);

        showFragent(0);

    }

    /**
     * 选择切换fragment
     */
    private void showFragent(int position) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        if (industryDistributionFragment == null) {
            industryDistributionFragment = FundBuyRuleFragment.newInstance(FundBuyRuleFragment.RULE_TYPE_SELL);
            fragmentTransaction.add(R.id.ll_positionAllocationActivity_contentView, industryDistributionFragment);
        }
        if (positionAllocationFragment == null) {
            positionAllocationFragment = FundBuyRuleFragment.newInstance(FundBuyRuleFragment.RULE_TYPE_BUY);
            fragmentTransaction.add(R.id.ll_positionAllocationActivity_contentView, positionAllocationFragment);
        }
                /*
         *隐藏所有Fragment
         */
        fragmentTransaction.hide(positionAllocationFragment);
        fragmentTransaction.hide(industryDistributionFragment);

        switch (position) {
            case 0:
                fragmentTransaction.show(positionAllocationFragment).commit();
                break;
            case 1:
                fragmentTransaction.show(industryDistributionFragment).commit();
                break;
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }
}
