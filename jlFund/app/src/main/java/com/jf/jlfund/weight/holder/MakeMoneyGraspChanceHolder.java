package com.jf.jlfund.weight.holder;

import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jf.jlfund.R;
import com.jf.jlfund.bean.GraspChanceBean;
import com.jf.jlfund.utils.UIUtils;
import com.jf.jlfund.view.activity.GraspChanceDetailActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

/**
 * Created by 55 on 2017/12/7.
 */

public class MakeMoneyGraspChanceHolder extends BaseHolder<GraspChanceBean> {
    RelativeLayout rlLeft;
    TextView tvRate;
    TextView tvTitle;
    TextView tvDesc;
    TextView tvPeriod;

    private int index;

    public MakeMoneyGraspChanceHolder(int index) {
        this.index = index;
    }

    @Override
    protected void initView(View rootView) {
        rlLeft = rootView.findViewById(R.id.rl_left_content);
        tvRate = rootView.findViewById(R.id.tv_item_grasp_chance_rate);
        tvTitle = rootView.findViewById(R.id.tv_item_grasp_chance_title);
        tvDesc = rootView.findViewById(R.id.tv_item_grasp_chance_desc);
        tvPeriod = rootView.findViewById(R.id.tv_item_grasp_chance_period);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> map = new HashMap<>();
                map.put("fundCode", data.getFundscode());
                map.put("fundName", data.getFundsName());
                map.put("index", index + "");
                MobclickAgent.onEvent(mContext, "client_makeMoneyFragment_graspChanceItem", map);
                GraspChanceDetailActivity.open(mContext, data.getId(), data.getFundscode());
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_grasp_chance;
    }

    @Override
    protected void updateView() {
        if (!TextUtils.isEmpty(data.getProfit())) {
            double profit = Double.parseDouble(data.getProfit());
            if (profit == 0) {
                rlLeft.setBackgroundResource(R.drawable.border_circle_b9bbca);
            } else if (profit > 0) {
                rlLeft.setSelected(true);
                tvPeriod.setTextColor(ContextCompat.getColor(mContext, R.color.color_f35857));
            } else {
                rlLeft.setSelected(false);
                tvPeriod.setTextColor(ContextCompat.getColor(mContext, R.color.color_18b293));
            }
            UIUtils.setRate(tvRate, profit);
        } else {
            tvRate.setText("--");
            rlLeft.setBackgroundResource(R.drawable.border_circle_b9bbca);
        }

        UIUtils.setText(tvTitle, data.getTitle(), "--");
        UIUtils.setText(tvDesc, data.getFundsName() + " | " + data.getFundscode(), "--");
        UIUtils.setText(tvPeriod, data.getWealthTypeName(), "--");
    }
}
