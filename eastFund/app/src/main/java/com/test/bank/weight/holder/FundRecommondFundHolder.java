package com.test.bank.weight.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.bean.GraspChanceBean;
import com.test.bank.utils.UIUtils;
import com.test.bank.view.activity.FundBuyInActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

/**
 * Created by 55 on 2018/1/16.
 */

public class FundRecommondFundHolder extends BaseHolder<GraspChanceBean> {

    ImageView ivNo;
    TextView tvFundName;
    LinearLayout llStar;
    TextView tvProfit;
    TextView tvPeriod;

    @Override
    protected void initView(View rootView) {
        ivNo = rootView.findViewById(R.id.iv_holderFundRecommand_no);
        tvFundName = rootView.findViewById(R.id.tv_holderFundRecommand_fundName);
        llStar = rootView.findViewById(R.id.ll_holderFundRecommand_star);
        tvProfit = rootView.findViewById(R.id.tv_holderFundRecommand_profit);
        tvPeriod = rootView.findViewById(R.id.tv_holderFundRecommand_period);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> map = new HashMap<>();
                map.put("fundCode", data.getFundscode());
                map.put("fundName", data.getFundsName());
                MobclickAgent.onEvent(mContext, "click_fundActivity_itemRecommand", map);
                FundBuyInActivity.open(mContext, data.getFundscode(), data.getFundsName());
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.holder_fund_recommand;
    }

    @Override
    protected void updateView() {
        UIUtils.setText(tvFundName, data.getFundsName());
        UIUtils.setRate(tvProfit, data.getProfit());
        UIUtils.setText(tvPeriod, data.getWealthTypeName());
        int start = data.getStars();
        for (int i = 0; i < start; i++) {
            llStar.addView(getStar(true));
        }
        for (int i = 1; i < 5 - start; i++) {
            llStar.addView(getStar(false));
        }
        ivNo.setImageResource(getImageResourceByIndex(data.getEveryBuyNo()));
    }

    private int getImageResourceByIndex(int start) {
        int rs = R.drawable.icon_fund_recommand_1;
        if (start == 0) {
            rs = R.drawable.icon_fund_recommand_1;
        } else if (start == 1) {
            rs = R.drawable.icon_fund_recommand_2;
        } else if (start == 2) {
            rs = R.drawable.icon_fund_recommand_3;
        } else if (start == 3) {
            rs = R.drawable.icon_fund_recommand_4;
        } else if (start == 4) {
            rs = R.drawable.icon_fund_recommand_5;
        } else if (start == 5) {
            rs = R.drawable.icon_fund_recommand_6;
        }
        return rs;
    }

    private ImageView getStar(boolean isSelected) {
        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(isSelected ? R.drawable.icon_star_selected : R.drawable.icon_star_unselected);
        return imageView;
    }
}
