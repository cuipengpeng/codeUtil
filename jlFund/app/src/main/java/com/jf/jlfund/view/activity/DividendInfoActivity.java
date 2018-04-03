package com.jf.jlfund.view.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.jf.jlfund.R;
import com.jf.jlfund.adapter.DividendInfoAdapter;
import com.jf.jlfund.base.BaseLocalDataActivity;
import com.jf.jlfund.bean.FundInfoBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
* 时    间：2017/12/11<br>
*/
public class DividendInfoActivity extends BaseLocalDataActivity{

    @BindView(R.id.rv_dividendInfoActivity_dividendInfoList)
    RecyclerView dividendInfoListRecyclerView;
    @BindView(R.id.ll_list_no_data_view)
    LinearLayout noDataLinearLayout;

    private FundInfoBean mFundInfoBean =   new FundInfoBean();
    private DividendInfoAdapter dividendInfoAdapter;
    private List<FundInfoBean.Divlist> dividendInfoList = new ArrayList<FundInfoBean.Divlist>();


    @Override
    protected String getPageTitle() {
        return "分红信息";
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_dividend_info;
    }

    @Override
    protected void initPageData() {
        mFundInfoBean = (FundInfoBean) getIntent().getSerializableExtra(FundInfoActivity.KEY_OF_FUND_INFO_MODEL);
        dividendInfoAdapter = new DividendInfoAdapter(this, mFundInfoBean.getDivlist());
        dividendInfoListRecyclerView.setAdapter(dividendInfoAdapter);
        dividendInfoListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(mFundInfoBean.getDivlist()==null  || mFundInfoBean.getDivlist().size()==0){
            dividendInfoListRecyclerView.setVisibility(View.GONE);
            noDataLinearLayout.setVisibility(View.VISIBLE);
        }
    }


}
