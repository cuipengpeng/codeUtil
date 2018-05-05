package com.test.bank.weight.holder;

import android.view.View;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.bean.BuyGoodFundDetailBean;
import com.test.bank.utils.CommonUtil;
import com.test.bank.utils.UIUtils;
import com.test.bank.view.activity.FundBuyInActivity;
import com.test.bank.view.activity.SingleFundDetailActivity;

/**
 * Created by 55 on 2017/12/6.
 */

public class BuyGoodFundDetailHolder extends BaseHolder<BuyGoodFundDetailBean.BuyGoodFundDetailInnerFundBean> {
    TextView tvRate;
    TextView tvPeriod;
    TextView tvTitle;
    TextView tvDesc;
    TextView tvBuyNow;

    private String wealthTypeName;

    public BuyGoodFundDetailHolder(String wealthTypeName) {
        super();
        this.wealthTypeName = wealthTypeName;
    }

    @Override
    protected void initView(final View rootView) {
        tvRate = rootView.findViewById(R.id.tv_item_buy_good_fund_rate);
        tvTitle = rootView.findViewById(R.id.tv_item_buy_good_fund_title);
        tvDesc = rootView.findViewById(R.id.tv_item_buy_good_fund_desc);
        tvPeriod = rootView.findViewById(R.id.tv_item_buy_good_fund_period);
        tvBuyNow = rootView.findViewById(R.id.tv_item_bgfd_buy_now);
        tvBuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FundBuyInActivity.open(mContext, data.getFund_code(), data.getFundsname());
            }
        });
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtil.startActivity(mContext, SingleFundDetailActivity.class, data.getFund_code());
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_buy_good_fund;
    }

    @Override
    protected void updateView() {
        UIUtils.setRate(tvRate, data.getProfit());
        UIUtils.setText(tvPeriod, wealthTypeName, "--");
        UIUtils.setText(tvTitle, data.getFundsname(), "--");
        UIUtils.setText(tvDesc, data.getFund_code(), "--");
        tvBuyNow.setEnabled((data.getDeclarestate() != null && data.getDeclarestate() == 1) ? true : false);
    }
}
