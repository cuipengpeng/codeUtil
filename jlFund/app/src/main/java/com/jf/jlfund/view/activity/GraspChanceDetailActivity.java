package com.jf.jlfund.view.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jf.jlfund.R;
import com.jf.jlfund.base.BaseActivity;
import com.jf.jlfund.base.BaseBean;
import com.jf.jlfund.bean.FundTrendBean;
import com.jf.jlfund.bean.GraspChanceDetailBean;
import com.jf.jlfund.http.HttpConfig;
import com.jf.jlfund.http.NetService;
import com.jf.jlfund.http.ParamMap;
import com.jf.jlfund.inter.OnResponseListener;
import com.jf.jlfund.utils.CommonUtil;
import com.jf.jlfund.utils.SPUtil;
import com.jf.jlfund.utils.UIUtils;
import com.jf.jlfund.weight.CommonTitleBar;
import com.jf.jlfund.weight.errorview.DefaultErrorPageBean;
import com.jf.jlfund.weight.errorview.ErrorBean;
import com.jf.jlfund.weight.holder.LineChartViewHolder;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;

public class GraspChanceDetailActivity extends BaseActivity {
    @BindView(R.id.commonTitleBar_graspChaceDetail)
    CommonTitleBar commonTitleBar;
    @BindView(R.id.scrollView_graspChanceDetail)
    ScrollView scrollView;
    @BindView(R.id.tv_yesterday_increase)
    TextView tvYesterDayIncrease;
    @BindView(R.id.tv_net_value_and_data)
    TextView tvYesterdayNetWorthAndData;
    @BindView(R.id.tv_net_worth)
    TextView tvNetWorth;
    @BindView(R.id.tv_grasp_chance_detail_fund_type)
    TextView tvFundType;
    @BindView(R.id.tv_grasp_chance_detail_fund_risk)
    TextView tvFundRisk;
    @BindView(R.id.tv_grasp_chance_detail_desc)
    TextView tvFundDesc;

    @BindView(R.id.btn_gcda_buy)
    TextView tvBuy;

    @BindView(R.id.ll_container_lineChart)
    LinearLayout llLineChart;

    private String fundId;
    private String fundCode;

    @Override
    protected void init() {
        if (getIntent() != null) {
            fundId = getIntent().getStringExtra(PARAM_FUND_ID);
            fundCode = getIntent().getStringExtra(PARAM_FUND_CODE);
        }
        commonTitleBar.setOnRightClickListener(new CommonTitleBar.onRightClickListener() {
            @Override
            public void onClickRight() {
                CommonUtil.startActivity(GraspChanceDetailActivity.this, SingleFundDetailActivity.class, fundCode);
            }
        });
        requestData();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_grasp_chance_detail;
    }

    @Override
    protected void doBusiness() {
    }

    private void requestData() {
        Log.e("zzzzzz", "doBusiness>>> fundId  " + fundId + "  fundCode: " + fundCode);
        postRequest(new OnResponseListener<GraspChanceDetailBean>() {
            @Override
            public Observable<BaseBean<GraspChanceDetailBean>> createObservalbe() {
                ParamMap paramMap = new ParamMap();
                paramMap.put("id", fundId);
                paramMap.putLast("fundcode", fundCode);
                return NetService.getNetService().getGraspChanceDetail(paramMap);
            }

            @Override
            public void onResponse(BaseBean<GraspChanceDetailBean> result) {
                inflateHeaderData(result);
            }

            @Override
            public void onError(String errorMsg) {

            }
        });

        postRequest(new OnResponseListener<FundTrendBean>() {
            @Override
            public Observable<BaseBean<FundTrendBean>> createObservalbe() {
                ParamMap paramMap = new ParamMap();
                if (SPUtil.getInstance().isLogin())
                    paramMap.put("token", SPUtil.getInstance().getToken());
                paramMap.putLast("fundcode", fundCode);
                return NetService.getNetService().getSingleFundTrend(paramMap);
            }

            @Override
            public void onResponse(BaseBean<FundTrendBean> result) {
                llLineChart.addView(new LineChartViewHolder().inflateData(result.getData()));
            }

            @Override
            public void onError(String errorMsg) {

            }
        }, false);
    }

    private String fundName = "";

    private void inflateHeaderData(BaseBean<GraspChanceDetailBean> result) {
        GraspChanceDetailBean bean = result.getData();
        if (bean == null) {
            return;
        }
        fundName = bean.getFundsname();
        tvBuy.setBackgroundResource(bean.getDeclarestate() == 1 ? R.drawable.bg_btn_buy_enable : R.drawable.bg_btn_buy_unenable);
        tvFundType.setVisibility(TextUtils.isEmpty(bean.getFundtype()) ? View.GONE : View.VISIBLE);
        tvFundRisk.setVisibility(TextUtils.isEmpty(bean.getCustrisk()) ? View.GONE : View.VISIBLE);
        commonTitleBar.setPrimaryTitle(bean.getFundsname());
        commonTitleBar.setSubTitle(bean.getFundcode());
//        UIUtils.setQuestionText(tvYesterDayIncrease, StringUtil.getRateWithSign(bean.getYestincome()));
        UIUtils.setRate(tvYesterDayIncrease, bean.getYestincome());
        UIUtils.setText(tvYesterdayNetWorthAndData, "净值(" + bean.getTradedate() + ")");
        UIUtils.setText(tvNetWorth, bean.getFundnav(), "---");
        UIUtils.setText(tvFundType, bean.getFundtype(), "---");

        UIUtils.setText(tvFundRisk, bean.getCustrisk(), "---");
        if (bean.getCustrisk().contains("高风险")) {
            tvFundRisk.setEnabled(true);
        } else if (bean.getCustrisk().contains("低风险")) {
            tvFundRisk.setBackgroundResource(R.drawable.border_rect_18b293);
        } else {
            tvFundRisk.setBackgroundResource(R.drawable.border_rect_ffa200);
        }

        UIUtils.setText(tvFundDesc, bean.getDescription(), "---");
        if (!bean.isShowLineChart()) {
            llLineChart.setVisibility(View.GONE);
        }
    }

//    float currLetterSpace = 0f;

    @OnClick({R.id.btn_gcda_buy, R.id.tv_net_worth})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.btn_gcda_buy:
                FundBuyInActivity.open(this, fundCode, fundName);
                break;
            case R.id.tv_net_worth:
                break;
        }
    }

    @Override
    protected ErrorBean getErrorBean() {
        return new DefaultErrorPageBean(scrollView);
    }

    @Override
    public void onRequestError(int errorCode, OnResponseListener onResponseListener) {
        super.onRequestError(errorCode, onResponseListener);
        if (errorCode != HttpConfig.SUCCESS) {
            tvBuy.setVisibility(View.GONE);
        } else {
            tvBuy.setVisibility(View.VISIBLE);
        }
    }

    public static String PARAM_FUND_ID = "FUND_ID";
    public static String PARAM_FUND_CODE = "FUND_CODE";

    public static void open(Context context, String fundId, String fundCode) {
        Intent intent = new Intent(context, GraspChanceDetailActivity.class);
        intent.putExtra(PARAM_FUND_ID, fundId);
        intent.putExtra(PARAM_FUND_CODE, fundCode);
        context.startActivity(intent);
    }
}
