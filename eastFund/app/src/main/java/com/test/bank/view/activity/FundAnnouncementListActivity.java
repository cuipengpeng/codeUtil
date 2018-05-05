package com.test.bank.view.activity;

import android.support.v7.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.test.bank.R;
import com.test.bank.adapter.FundAnnouncementListAdapter;
import com.test.bank.base.BaseUIActivity;
import com.test.bank.bean.FundAnnouncementListBean;
import com.test.bank.http.HttpRequest;
import com.test.bank.utils.ConstantsUtil;
import com.test.bank.weight.refreshlayout.AutoLoadMoreRecyclerView;

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
public class FundAnnouncementListActivity extends BaseUIActivity {

    @BindView(R.id.rv_fundAnnouncementListActivity_fundAnnouncementList)
    AutoLoadMoreRecyclerView fundAnnouncementListRecyclerView;

    private FundAnnouncementListAdapter fundAnnouncementAdapter;
    private List<FundAnnouncementListBean.NoticeList> fundAnnouncementList = new ArrayList<FundAnnouncementListBean.NoticeList>();
    private String fundCode;

    private int pageNo = 1;

    @Override
    protected String getPageTitle() {
        return "基金公告";
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_fund_announcement_list;
    }

    @Override
    protected void initPageData() {
        fundCode = getIntent().getStringExtra(SingleFundDetailActivity.KEY_OF_FUND_CODE);

        fundAnnouncementAdapter = new FundAnnouncementListAdapter(this, fundAnnouncementList);
        fundAnnouncementListRecyclerView.setAdapter(fundAnnouncementAdapter);
        fundAnnouncementListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        fundAnnouncementListRecyclerView.setOnRefreshListener(new AutoLoadMoreRecyclerView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFundAnnouncementList(true);
            }
        });
        fundAnnouncementListRecyclerView.setOnLoadMoreListener(new AutoLoadMoreRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getFundAnnouncementList(false);
            }
        });


        getFundAnnouncementList(true);

    }


    private void getFundAnnouncementList(final boolean initRequest) {
        if(initRequest){
            pageNo = 1;
        }else {
            pageNo+=1;
        }

        Map<String, String> params =  new HashMap<String, String>();
        params.put("fundcode", fundCode);
        params.put("pageNo", pageNo+"");
        params.put("pageSize", ConstantsUtil.PAGE_SIZE+"");

        HttpRequest.post(HttpRequest.APP_INTERFACE_WEB_URL_ENV_TEST +HttpRequest.FUND_ANNOUNCEMENT_LIST, params, this, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                FundAnnouncementListBean  fundAnnouncementListBean = JSON.parseObject(response.body(), FundAnnouncementListBean.class);

                if (fundAnnouncementListBean.getNoticeList() != null && !fundAnnouncementListBean.getNoticeList().isEmpty()) {
                    fundAnnouncementListRecyclerView.refreshComplete();
                    fundAnnouncementAdapter.upateData(initRequest, fundAnnouncementListBean.getNoticeList());

                    if (fundAnnouncementListBean.getNoticeList().size()< ConstantsUtil.PAGE_SIZE) {
                        fundAnnouncementListRecyclerView.noMoreLoading();
                    }
                } else {
                    fundAnnouncementListRecyclerView.noMoreLoading();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                fundAnnouncementListRecyclerView.refreshComplete();
            }
        });
    }
}
