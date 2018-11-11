package com.test.bank.view.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.test.bank.R;
import com.test.bank.adapter.IndustryDistributionAdapter;
import com.test.bank.base.BaseUIFragment;
import com.test.bank.bean.IndustryDistributionBean;
import com.test.bank.http.HttpRequest;
import com.test.bank.utils.ConstantsUtil;
import com.test.bank.utils.StringUtil;
import com.test.bank.view.activity.PositionAllocationActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Response;

/**
 * 收益率走势图Fragment
 */
public class IndustryDistributionFragment extends BaseUIFragment {

    @BindView(R.id.tv_industryDistributionFragment_investPeriod)
    TextView investPeriodTextView;
    @BindView(R.id.rv_industryDistributionFragment_industryDistribution)
    RecyclerView industryDistributionRecyclerView;
    @BindView(R.id.ll_list_no_data_view)
    LinearLayout noDataLinearLayout;

    IndustryDistributionAdapter industryDistributionAdapter;
    List<IndustryDistributionBean.IndustryConf> mIndustryDistributionList = new ArrayList<>();

    @Override
    protected String getPageTitle() {
        return null;
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.fragment_industry_distribution;
    }

    @Override
    protected void initPageData() {
        industryDistributionAdapter = new IndustryDistributionAdapter(getActivity());
        industryDistributionRecyclerView.setAdapter(industryDistributionAdapter);
        industryDistributionRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        getFundCompanyInfo();
    }
    private void getFundCompanyInfo(){
        Map<String, String> params =  new HashMap<String, String>();
        params.put("fundcode", ((PositionAllocationActivity)getActivity()).mFundCode);

        HttpRequest.post(this, HttpRequest.APP_INTERFACE_WEB_URL +HttpRequest.INDUSTRY_DISTRIBUTION, params, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                IndustryDistributionBean industryDistributionBean = JSON.parseObject(response.body(), IndustryDistributionBean.class);
                if(StringUtil.isNull(industryDistributionBean.getPerioddate()) && StringUtil.isNull(industryDistributionBean.getRpt_cls())){
                    investPeriodTextView.setText(ConstantsUtil.REPLACE_BLANK_SYMBOL);
                }else {
                    investPeriodTextView.setText(industryDistributionBean.getPerioddate()+industryDistributionBean.getRpt_cls());
                }

                if(industryDistributionBean.getIndustryConf().size()>0){
                    industryDistributionRecyclerView.setVisibility(View.VISIBLE);
                    noDataLinearLayout.setVisibility(View.GONE);
                    industryDistributionAdapter.upateData(true, industryDistributionBean.getIndustryConf());

                }else {
                    industryDistributionRecyclerView.setVisibility(View.GONE);
                    noDataLinearLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

}
