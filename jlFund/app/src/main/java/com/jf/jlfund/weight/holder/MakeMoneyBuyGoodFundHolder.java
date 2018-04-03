package com.jf.jlfund.weight.holder;

import android.view.View;
import android.widget.TextView;

import com.jf.jlfund.R;
import com.jf.jlfund.bean.BuyGoodFundBean;
import com.jf.jlfund.utils.ActivityManager;
import com.jf.jlfund.utils.UIUtils;
import com.jf.jlfund.view.activity.BuyGoodFundDetailActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 55 on 2017/12/6.
 */

public class MakeMoneyBuyGoodFundHolder extends BaseHolder<BuyGoodFundBean> {
    @BindView(R.id.tv_item_buy_good_fund_rate)
    TextView tvRate;
    @BindView(R.id.tv_item_buy_good_fund_period)
    TextView tvPeriod;
    @BindView(R.id.tv_item_buy_good_fund_title)
    TextView tvTitle;
    @BindView(R.id.tv_item_buy_good_fund_desc)
    TextView tvDesc;

    private int index;

    public MakeMoneyBuyGoodFundHolder(int index) {
        this.index = index;
    }

    @Override
    protected void initView(final View rootView) {
        ButterKnife.bind(this, rootView);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> map = new HashMap<>();
                map.put("fundCode", data.getFundscode());
                map.put("clickIndex", index + "");
                MobclickAgent.onEvent(mContext, "click_makeMoneyFragment_buyGoodFundItem", map);
                BuyGoodFundDetailActivity.open(ActivityManager.getInstance().currentActivity(), data.getId(), true);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_make_money_buy_good_fund;
    }

    @Override
    protected void updateView() {
        UIUtils.setRate(tvRate, data.getProfit());
        UIUtils.setText(tvTitle, data.getTitle(), "--");
        UIUtils.setText(tvPeriod, data.getWealthTypeName(), "--");
        UIUtils.setText(tvDesc, data.getSecondTitle(), "--");
    }
}
