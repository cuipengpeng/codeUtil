package com.test.bank.view.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.base.BaseLocalDataActivity;
import com.test.bank.bean.FundInfoBean;

import butterknife.BindView;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
*/

public class TradeRecordDetailActivity extends BaseLocalDataActivity{

    @BindView(R.id.tv_putInResultActivity_tradeType)
    TextView tradeTypeTextView;
    @BindView(R.id.tv_putInResultActivity_tradeDate)
    TextView tradeDateTextView;
    @BindView(R.id.tv_putInResultActivity_tradeAmount)
    TextView tradeAmountTextView;

    @BindView(R.id.rl_putInResultActivity_flowStep1)
    RelativeLayout flowStep1RelativeLayout;
    @BindView(R.id.rl_putInResultActivity_flowStep2)
    RelativeLayout flowStep2RelativeLayout;
    @BindView(R.id.rl_putInResultActivity_flowStep3)
    RelativeLayout flowStep3RelativeLayout;

    @BindView(R.id.tv_putInResultActivity_flowLineUp1)
    View flowLineUp1View;
    @BindView(R.id.tv_putInResultActivity_flowIcon1)
    ImageView flowIcon1ImageView;
    @BindView(R.id.tv_putInResultActivity_flowLineDown1)
    View flowLineDown1View;
    @BindView(R.id.tv_putInResultActivity_flowDesc1)
    TextView flowDesc1TextView;
    @BindView(R.id.tv_putInResultActivity_flowDate1)
    TextView flowDate1TextView;
    @BindView(R.id.tv_putInResultActivity_flowLineUp2)
    View flowLineUp2View;
    @BindView(R.id.tv_putInResultActivity_flowIcon2)
    ImageView flowIcon2ImageView;
    @BindView(R.id.tv_putInResultActivity_flowLineDown2)
    View flowLineDown2View;
    @BindView(R.id.tv_putInResultActivity_flowDesc2)
    TextView flowDesc2TextView;
    @BindView(R.id.tv_putInResultActivity_flowDate2)
    TextView flowDate2TextView;
    @BindView(R.id.tv_putInResultActivity_flowLineUp3)
    View flowLineUp3View;
    @BindView(R.id.tv_putInResultActivity_flowIcon3)
    ImageView flowIcon3ImageView;
    @BindView(R.id.tv_putInResultActivity_flowLineDown3)
    View flowLineDown3View;
    @BindView(R.id.tv_putInResultActivity_flowDesc3)
    TextView flowDesc3TextView;
    @BindView(R.id.tv_putInResultActivity_flowDate3)
    TextView flowDate3TextView;

    private FundInfoBean mFundInfoBean =   new FundInfoBean();

    @Override
    protected String getPageTitle() {
        return "分红信息";
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_putin_result;
    }

    @Override
    protected void initPageData() {
        mFundInfoBean = (FundInfoBean) getIntent().getSerializableExtra(FundInfoActivity.KEY_OF_FUND_INFO_MODEL);
    }


}
