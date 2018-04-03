package com.jf.jlfund.weight.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jf.jlfund.R;
import com.jf.jlfund.view.activity.RiskPreferenceActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by 55 on 2018/1/8.
 */

public class RiskNoEvaluationHolder extends BaseHolder<String> {
    TextView tvBeginEvaluation;

    public RiskNoEvaluationHolder(OnClickRetestRiskListener onClickRetestRiskListener) {
        this.onClickRetestRiskListener = onClickRetestRiskListener;
    }

    @Override
    protected void initView(final View rootView) {
        tvBeginEvaluation = rootView.findViewById(R.id.tv_beganRiskEvaluation);
        tvBeginEvaluation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {       //开始风险测评
                MobclickAgent.onEvent(mContext, "click_btn_riskPreferenceActivity_beginTest");
                if(onClickRetestRiskListener!=null){
                    onClickRetestRiskListener.onClickRetestRisk();
                }
                if (rootView.getParent() != null) {
                    ViewGroup parent = (ViewGroup) rootView.getParent();
                    parent.setVisibility(View.GONE);
                }
            }
        });
    }

    private OnClickRetestRiskListener onClickRetestRiskListener;

    public interface OnClickRetestRiskListener {
        void onClickRetestRisk();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.holder_no_risk_evaluation;
    }

    @Override
    protected void updateView() {

    }
}
