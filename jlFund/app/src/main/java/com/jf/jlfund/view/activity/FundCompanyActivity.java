package com.jf.jlfund.view.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jf.jlfund.R;
import com.jf.jlfund.adapter.FundCompanyManageFundAdapter;
import com.jf.jlfund.base.BaseUIActivity;
import com.jf.jlfund.bean.FundCompanyBean;
import com.jf.jlfund.http.HttpRequest;
import com.jf.jlfund.utils.StringUtil;
import com.jf.jlfund.weight.NoScrollRecycleView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Response;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
* 时    间：2017/12/11<br>
*/
public class FundCompanyActivity extends BaseUIActivity {

    @BindView(R.id.tv_fundCompanyActivity_fundCompanyNameValue)
    TextView fundCompanyNameTextView;
    @BindView(R.id.tv_fundCompanyActivity_setupTimeValue)
    TextView setupTimeTextView;
    @BindView(R.id.tv_fundCompanyActivity_manageMoneyValue)
    TextView manageMoneyTextView;
    @BindView(R.id.tv_fundCompanyActivity_rankValue)
    TextView rankTextView;
    @BindView(R.id.tv_fundCompanyActivity_managerPersonValue)
    TextView managerPersonTextView;
    @BindView(R.id.rv_fundCompanyActivity_manageFundList)
    NoScrollRecycleView manageFundListRecyclerView;

    private String fundCode;
    private FundCompanyManageFundAdapter fundCompanyAdapter;
    private List<FundCompanyBean.Fundlist> fundCompanyList = new ArrayList<FundCompanyBean.Fundlist>();

    @Override
    protected String getPageTitle() {
        return "基金公司";
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_fund_company;
    }

    @Override
    protected void initPageData() {
        fundCode = getIntent().getStringExtra(SingleFundDetailActivity.KEY_OF_FUND_CODE);

//        manageFundListRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        manageFundListRecyclerView.setHasFixedSize(true);
        fundCompanyAdapter = new FundCompanyManageFundAdapter(this, fundCompanyList);
        manageFundListRecyclerView.setAdapter(fundCompanyAdapter);
        manageFundListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        getFundCompanyInfo();
    }

    private void getFundCompanyInfo(){
        Map<String, String> params =  new HashMap<String, String>();
        params.put("fundcode", fundCode);

        HttpRequest.post(HttpRequest.JL_FUND_TEST_ENV_WEB_URL+HttpRequest.FUND_COMPANY_INFO, params, this, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                FundCompanyBean fundCompanyBean = JSON.parseObject(response.body(), FundCompanyBean.class);

                fundCompanyNameTextView.setText(fundCompanyBean.getFnd_org_name());
                setupTimeTextView.setText(fundCompanyBean.getBuild_date());
                manageMoneyTextView.setText(StringUtil.moneyDecimalFormat2(fundCompanyBean.getMana_scale()+"")+"亿元");
                rankTextView.setText(fundCompanyBean.getMana_ranking());
                managerPersonTextView.setText(fundCompanyBean.getIndi_num()+"位");
                fundCompanyAdapter.upateData(true, fundCompanyBean.getFundlist());

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }
}
