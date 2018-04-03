package com.jf.jlfund.weight.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jf.jlfund.R;
import com.jf.jlfund.utils.UIUtils;
import com.jf.jlfund.view.activity.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 55 on 2018/1/8.
 */

public class RiskEvaluationResultHolder extends BaseHolder<String> {
    @BindView(R.id.tv_riskResultHolder_type)
    TextView tvResult;
    @BindView(R.id.iv_riskResultHolder_result)
    ImageView ivResult;
    @BindView(R.id.tv_riskResultHolder_desc1)
    TextView tvDesc1;
    @BindView(R.id.tv_riskResultHolder_desc2)
    TextView tvDesc2;
    @BindView(R.id.tv_riskResult_buyFundNow)
    TextView tvBuyFundNow;

    @Override
    protected void initView(View rootView) {
        ButterKnife.bind(this, rootView);
        tvBuyFundNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //马上买基金跳转到赚钱页面
                MainActivity.open(mContext, 1);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.holder_risk_result;
    }

    @Override
    protected void updateView() {
        inflateRiskResult(data);
    }

    private void inflateRiskResult(String riskLevel) {
        tvResult.setText(riskLevel);
        if ("激进型".equals(riskLevel)) {
            ivResult.setImageResource(R.drawable.icon_risk_radical);
            UIUtils.setText(tvDesc1, "适合您的有高风险、中高风险、中风险、中低风险、低风险所有基金");
            tvDesc2.setVisibility(View.GONE);
            return;
        }
        if ("进取型".equals(riskLevel)) {
            ivResult.setImageResource(R.drawable.icon_risk_enterprising);
            UIUtils.setText(tvDesc1, "适合您的有高风险、中高风险、中风险、中低风险、低风险基金");
            tvDesc2.setVisibility(View.GONE);
            return;
        }
        if ("积极型".equals(riskLevel)) {
            ivResult.setImageResource(R.drawable.icon_risk_positive);
            UIUtils.setText(tvDesc1, "适合您的有高风险、中高风险、中风险、中低风险、低风险基金");
            tvDesc2.setVisibility(View.GONE);
            return;
        }
        if ("稳健型".equals(riskLevel)) {
            ivResult.setImageResource(R.drawable.icon_risk_steady);
            UIUtils.setText(tvDesc1, "适合您的有中风险、中低风险、低风险基金");
            tvDesc2.setVisibility(View.GONE);
            return;
        }
        if ("谨慎型".equals(riskLevel)) {
            ivResult.setImageResource(R.drawable.icon_risk_cautious);
            UIUtils.setText(tvDesc1, "适合您的基金类型是：货币型、理财型");
            tvDesc2.setVisibility(View.VISIBLE);
            UIUtils.setText(tvDesc2, "风险收益特征：历史未亏损，天天正收益");
            return;
        }
        if ("保守型".equals(riskLevel)) {
            ivResult.setImageResource(R.drawable.icon_risk_conservative);
            UIUtils.setText(tvDesc1, "适合您的基金类型是：货币型、理财型");
            tvDesc2.setVisibility(View.VISIBLE);
            UIUtils.setText(tvDesc2, "风险收益特征：历史未亏损，天天正收益");
            return;
        }
    }
}
