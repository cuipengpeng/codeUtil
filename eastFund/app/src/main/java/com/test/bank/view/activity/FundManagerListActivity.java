package com.test.bank.view.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.test.bank.R;
import com.test.bank.adapter.FundManagerListAdapter;
import com.test.bank.base.BaseUIActivity;
import com.test.bank.bean.FundManagerDetailBean;
import com.test.bank.http.HttpRequest;

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
public class FundManagerListActivity extends BaseUIActivity {

    @BindView(R.id.rv_fundManagerListActivity_fundManagerList)
    RecyclerView fundManagerListRecyclerView;

    private FundManagerListAdapter fundManagerAdapter;
    private List<FundManagerDetailBean.FundManager> fundManagerList = new ArrayList<FundManagerDetailBean.FundManager>();

    private String mFundCode;

    @Override
    protected String getPageTitle() {
        return "基金经理";
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_fund_manager_list;
    }

    @Override
    protected void initPageData() {
        mFundCode = getIntent().getStringExtra(SingleFundDetailActivity.KEY_OF_FUND_CODE);
        fundManagerAdapter = new FundManagerListAdapter(this, fundManagerList);
        fundManagerListRecyclerView.setAdapter(fundManagerAdapter);
        fundManagerListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        getFundCompanyInfo();
    }

    private void getFundCompanyInfo() {
        Map<String, String> params =  new HashMap<String, String>();
        params.put("fundcode", mFundCode);

        HttpRequest.post(HttpRequest.APP_INTERFACE_WEB_URL_ENV_TEST +HttpRequest.FUND_MANAGER_DETAIL, params, this, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                FundManagerDetailBean fundManagerDetailBean = JSON.parseObject(response.body(), FundManagerDetailBean.class);

                if(fundManagerDetailBean.getFundManager()==null || fundManagerDetailBean.getFundManager().size() ==0){
                    fundManagerListRecyclerView.setVisibility(View.GONE);
                }
                fundManagerAdapter.upateData(true, fundManagerDetailBean.getFundManager());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }
}
