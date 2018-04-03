package com.jf.jlfund.view.fragment;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jf.jlfund.R;
import com.jf.jlfund.base.BaseFragment;
import com.jf.jlfund.bean.TestResultBean;
import com.jf.jlfund.utils.DensityUtil;
import com.jf.jlfund.utils.UIUtils;

import butterknife.BindView;

/**
 * 测一测结果页
 */
public class TestResultFragment extends BaseFragment {
    @BindView(R.id.tv_fragmentTestResult_fundName)
    TextView tvFundName;
    @BindView(R.id.tv_fragmentTestResult_profit)
    TextView tvProfit;
    @BindView(R.id.ll_fragmentTestResult_star)
    LinearLayout llStar;
    @BindView(R.id.iv_fragmentTestResult_star1)
    ImageView ivStar1;
    @BindView(R.id.iv_fragmentTestResult_star2)
    ImageView ivStar2;
    @BindView(R.id.iv_fragmentTestResult_star3)
    ImageView ivStar3;
    @BindView(R.id.iv_fragmentTestResult_star4)
    ImageView ivStar4;
    @BindView(R.id.iv_fragmentTestResult_star5)
    ImageView ivStar5;
    @BindView(R.id.tv_fragmentTestResult_antiRiskAbility)
    TextView tvAntiRiskAbility;
    @BindView(R.id.tv_fragmentTestResult_stable)
    TextView tvStableAbility;
    @BindView(R.id.tv_fragmentTestResult_orgPos)
    TextView tvOrgPos;  //机构持仓
    @BindView(R.id.ll_fragmentTestResult_tag)
    LinearLayout llTags;

    private TestResultBean resultBean;

    public static TestResultFragment newInstance(TestResultBean testResultBean) {
        TestResultFragment fragment = new TestResultFragment();
        Bundle args = new Bundle();
        args.putSerializable("resultBean", testResultBean);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void init() {
        if (!getArguments().isEmpty()) {
            resultBean = (TestResultBean) getArguments().getSerializable("resultBean");
        }
        if (resultBean == null) {
            return;
        }
        inflateData();
    }

    private void inflateData() {
        UIUtils.setText(tvFundName, resultBean.getFundname(), "--");
        UIUtils.setIncome(tvProfit, resultBean.getProfit(), "--", 2, true, true);
        UIUtils.setText(tvAntiRiskAbility, resultBean.getWithstandRisk(), "--");
        UIUtils.setText(tvStableAbility, resultBean.getStability(), "--");
        UIUtils.setText(tvOrgPos, resultBean.getInstitutionHold(), "--");

        if (!TextUtils.isEmpty(resultBean.getInstitutionGrade())) {
            Integer star = Integer.parseInt(resultBean.getInstitutionGrade());
            ivStar1.setSelected(star >= 1);
            ivStar2.setSelected(star >= 2);
            ivStar3.setSelected(star >= 3);
            ivStar4.setSelected(star >= 4);
            ivStar5.setSelected(star >= 5);
        }

        if (!TextUtils.isEmpty(resultBean.getTag()) && resultBean.getTag().contains(",")) {
            llTags.removeAllViews();
            String[] arr = resultBean.getTag().split(",");
            for (int i = 0; i < arr.length; i++) {
                TextView textView = new TextView(getContext());

                textView.setText(arr[i]);
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.color_7e819b));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                textView.setPadding(DensityUtil.dip2px(15), DensityUtil.dip2px(5), DensityUtil.dip2px(15), DensityUtil.dip2px(5));
                textView.setBackgroundResource(R.drawable.bg_radius30_f5f5f5);

                if (i != 0) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.leftMargin = DensityUtil.dip2px(10);
                    textView.setLayoutParams(layoutParams);
                }

                llTags.addView(textView);
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_test_result;
    }
}
