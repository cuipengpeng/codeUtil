package com.test.bank.weight.holder;

import android.view.View;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.bean.FundHomeBean;
import com.test.bank.utils.StringUtil;
import com.test.bank.utils.UIUtils;
import com.test.bank.view.activity.FundPositionsActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

/**
 * Created by 55 on 2018/1/16.
 * 基金持有holder
 */

public class FundHoldHolder extends BaseHolder<FundHomeBean.HoldFundBean> {

    TextView tvFundName;
    TextView tvAmount;
    TextView tvYesterdayIncome;
    TextView tvTotalIncome;
    TextView tvBottom;

    @Override
    protected void initView(View rootView) {

        tvFundName = rootView.findViewById(R.id.tv_holderFundHold_fundName);
        tvAmount = rootView.findViewById(R.id.tv_holderFundHold_amount);
        tvYesterdayIncome = rootView.findViewById(R.id.tv_holderFundHold_yesterdayIncome);
        tvTotalIncome = rootView.findViewById(R.id.tv_holderFundHold_totalIncome);
        tvBottom = rootView.findViewById(R.id.tv_holderFundHold_bottomTip);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> map = new HashMap<>();
                map.put("fundCode", data.getFundcode());
                map.put("fundName", data.getFundname());
                MobclickAgent.onEvent(mContext, "click_fundActivity_itemHold", map);
                FundPositionsActivity.open(mContext, data.getFundcode(), data.getFundname(), StringUtil.isNormalFund(data.getFundstate()));
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.holder_fund_hold;
    }

    @Override
    protected void updateView() {
        UIUtils.setText(tvFundName, data.getFundname());
        UIUtils.setIncome(tvAmount, data.getHavingAmount(), "--", 2, false, false);
        UIUtils.setIncome(tvYesterdayIncome, data.getYestodayProfit());
        UIUtils.setIncome(tvTotalIncome, data.getTotalProfit());
        if (data.getTradeInway() > 0) {
            tvBottom.setVisibility(View.VISIBLE);
            tvBottom.setText(data.getTradeInway() + "笔交易确认中");
        } else {
            tvBottom.setVisibility(View.GONE);
        }
    }
}
