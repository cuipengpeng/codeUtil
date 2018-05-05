package com.test.bank.view.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.base.BaseUIActivity;
import com.test.bank.bean.FundInfoBean;
import com.test.bank.view.fragment.TradeRecordListFragment;

import butterknife.BindView;
import butterknife.OnClick;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
*/
public class TradeRecordListActivity extends BaseUIActivity {

    @BindView(R.id.tv_tradeRecordListActivity_all)
    TextView allTextView;
    @BindView(R.id.v_tradeRecordListActivity_allRedLine)
    View allRedLineView;
    @BindView(R.id.ll_tradeRecordListActivity_all)
    LinearLayout allLinearLayout;
    @BindView(R.id.tv_tradeRecordListActivity_putIn)
    TextView putInTextView;
    @BindView(R.id.v_tradeRecordListActivity_putInRedLine)
    View putInRedLineView;
    @BindView(R.id.ll_tradeRecordListActivity_putIn)
    LinearLayout PutIn;
    @BindView(R.id.tv_tradeRecordListActivity_getOut)
    TextView getOutTextView;
    @BindView(R.id.v_tradeRecordListActivity_getOutRedLine)
    View getOutRedLineView;
    @BindView(R.id.ll_tradeRecordListActivity_getOut)
    LinearLayout getOutLinearLayout;
    @BindView(R.id.ll_tradeRecordListActivity_contentView)
    LinearLayout contentViewLinearLayout;

    private FundInfoBean mFundInfoBean = new FundInfoBean();
    private FragmentManager mFragmentManager;
    private Fragment allFragment;
    private Fragment putInFragment;
    private Fragment getOutFragment;

    @OnClick({R.id.ll_tradeRecordListActivity_all,R.id.ll_tradeRecordListActivity_putIn, R.id.ll_tradeRecordListActivity_getOut})
    public void onItemClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.ll_tradeRecordListActivity_all:
                showAllTradeFragment();
                break;
            case R.id.ll_tradeRecordListActivity_putIn:
                showPutInFragment();
            break;
            case R.id.ll_tradeRecordListActivity_getOut:
                showGetOutFragment();
                break;
        }
    }

    @Override
    protected String getPageTitle() {
        return "交易记录";
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_trade_record_list;
    }

    @Override
    protected void initPageData() {
        mFundInfoBean = (FundInfoBean) getIntent().getSerializableExtra(FundInfoActivity.KEY_OF_FUND_INFO_MODEL);
        mFragmentManager = getSupportFragmentManager();

        showAllTradeFragment();
    }

    /**
     *
     */
    private void showAllTradeFragment(){
        allTextView.setTextColor(getResources().getColor(R.color.currentPlusActivityGold));
        allRedLineView.setVisibility(View.VISIBLE);

        putInTextView.setTextColor(getResources().getColor(R.color.appContentColor));
        putInRedLineView.setVisibility(View.INVISIBLE);
        getOutTextView.setTextColor(getResources().getColor(R.color.appContentColor));
        getOutRedLineView.setVisibility(View.INVISIBLE);

        showFragent(0);

    }
    /**
     *
     */
    private void showPutInFragment(){
        putInTextView.setTextColor(getResources().getColor(R.color.currentPlusActivityGold));
        putInRedLineView.setVisibility(View.VISIBLE);

        getOutTextView.setTextColor(getResources().getColor(R.color.appContentColor));
        getOutRedLineView.setVisibility(View.INVISIBLE);
        allTextView.setTextColor(getResources().getColor(R.color.appContentColor));
        allRedLineView.setVisibility(View.INVISIBLE);

        showFragent(1);

    }
    /**
     *
     */
    private void showGetOutFragment(){
        getOutTextView.setTextColor(getResources().getColor(R.color.currentPlusActivityGold));
        getOutRedLineView.setVisibility(View.VISIBLE);

        putInTextView.setTextColor(getResources().getColor(R.color.appContentColor));
        putInRedLineView.setVisibility(View.INVISIBLE);
        allTextView.setTextColor(getResources().getColor(R.color.appContentColor));
        allRedLineView.setVisibility(View.INVISIBLE);

        showFragent(2);

    }

    /**
     * 选择切换fragment
     */
    private void showFragent(int position) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        if (putInFragment == null){
            putInFragment = TradeRecordListFragment.newInstance("1");
            fragmentTransaction.add(R.id.ll_tradeRecordListActivity_contentView, putInFragment);
        }
        if (getOutFragment == null){
            getOutFragment = TradeRecordListFragment.newInstance("2");
            fragmentTransaction.add(R.id.ll_tradeRecordListActivity_contentView, getOutFragment);
        }
        if (allFragment == null){
            allFragment = TradeRecordListFragment.newInstance("3");
            fragmentTransaction.add(R.id.ll_tradeRecordListActivity_contentView, allFragment);
        }
        /*
         *隐藏所有Fragment
         */
        fragmentTransaction.hide(allFragment);
        fragmentTransaction.hide(putInFragment);
        fragmentTransaction.hide(getOutFragment);

        switch (position) {
            case 0:
                fragmentTransaction.show(allFragment).commit();
                break;
            case 1:
                fragmentTransaction.show(putInFragment).commit();
                break;
            case 2:
                fragmentTransaction.show(getOutFragment).commit();
                break;
        }
    }

    @Override
    protected boolean isCountPage() {
        return false;
    }
}
