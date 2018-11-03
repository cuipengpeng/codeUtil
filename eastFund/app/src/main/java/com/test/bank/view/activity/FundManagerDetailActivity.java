package com.test.bank.view.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Layout;
import android.view.View;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.adapter.FundManagerListAdapter;
import com.test.bank.adapter.FundManagerManageFundAdapter;
import com.test.bank.base.BaseUIActivity;
import com.test.bank.bean.FundManagerDetailBean;
import com.test.bank.weight.NoScrollRecycleView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
* 时    间：2017/12/11<br>
*/
public class FundManagerDetailActivity extends BaseUIActivity {


    @BindView(R.id.tv_fundManagerDetailActivity_fundManagerName)
    TextView fundManagerNameTextView;
    @BindView(R.id.tv_fundManagerDetailActivity_workingDate)
    TextView workingDateTextView;
    @BindView(R.id.tv_fundManagerDetailActivity_workingExperience)
    TextView workingExperienceTextView;
    @BindView(R.id.tv_fundManagerDetailActivity_viewFullText)
    TextView viewFullTextTextView;
    @BindView(R.id.rv_fundManagerDetailActivity_manageFundList)
    NoScrollRecycleView manageFundListRecyclerView;

    private FundManagerDetailBean.FundManager mFundManager = new FundManagerDetailBean.FundManager();
    private String fundCode;
    private FundManagerManageFundAdapter fundCompanyAdapter;
    private List<FundManagerDetailBean.Fundlist> fundCompanyList = new ArrayList<FundManagerDetailBean.Fundlist>();

    @OnClick({R.id.tv_fundManagerDetailActivity_viewFullText})
    public void onItemClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.tv_fundManagerDetailActivity_viewFullText:
                workingExperienceTextView.setMaxLines(100000);
                viewFullTextTextView.setVisibility(View.GONE);
                break;
        }
    }
    @Override
    protected String getPageTitle() {
        return "基金经理";
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_fund_manager_detail;
    }

    @Override
    protected void initPageData() {
        //无网络请求的页面需要先调用该方法
        showContentView();

        mFundManager = (FundManagerDetailBean.FundManager) getIntent().getSerializableExtra(FundManagerListAdapter.KEY_OF_FUND_MANAGER_MODEL);
        fundCompanyAdapter = new FundManagerManageFundAdapter(this);
        manageFundListRecyclerView.setAdapter(fundCompanyAdapter);
        manageFundListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        fundManagerNameTextView.setText(mFundManager.getIndi_name());
        workingDateTextView.setText(mFundManager.getDeclaredate());
        workingExperienceTextView.setText("   "+mFundManager.getResume().trim());
    }


    @Override
    protected void onResume() {
        super.onResume();
        workingExperienceTextView.post(new Runnable() {
            @Override
            public void run() {
                Layout l = workingExperienceTextView.getLayout();
                if (l != null) {
                    int lines = l.getLineCount();
                    if (lines > 0) {
                        if (l.getEllipsisCount(lines - 1) <= 0) {
                            viewFullTextTextView.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });

    }
}
