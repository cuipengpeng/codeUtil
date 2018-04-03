package com.jf.jlfund.view.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.jf.jlfund.R;
import com.jf.jlfund.adapter.NetValueListAdapter;
import com.jf.jlfund.base.BaseUIActivity;
import com.jf.jlfund.bean.NetValueBean;
import com.jf.jlfund.http.HttpRequest;
import com.jf.jlfund.utils.ConstantsUtil;
import com.jf.jlfund.weight.refreshlayout.AutoLoadMoreRecyclerView;

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
public class NetValueListActivity extends BaseUIActivity {

    @BindView(R.id.rv_netValueListActivity_netValueList)
    AutoLoadMoreRecyclerView netValueListRecyclerView;
    @BindView(R.id.ll_list_no_data_view)
    LinearLayout noDataLinearLayout;


    private NetValueListAdapter netValueListAdapter;
    private List<NetValueBean.Navs> netValueListList = new ArrayList<NetValueBean.Navs>();
    private String fundCode;
    private String mTitle;

    private int pageNo = 1;


    @Override
    protected String getPageTitle() {
        return "";
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_net_value_list;
    }

    @Override
    protected void initPageData() {
        fundCode = getIntent().getStringExtra(SingleFundDetailActivity.KEY_OF_FUND_CODE);
        mTitle = getIntent().getStringExtra(SingleFundDetailActivity.KEY_OF_TITLE);
        String[] titleArray = mTitle.split(SingleFundDetailActivity.TITLE_SEPERATOR);
        if (titleArray.length > 1) {
            baseTitleTextView.setText(SingleFundDetailActivity.generateTitleText(this, titleArray[0], titleArray[1]));
        }

        netValueListAdapter = new NetValueListAdapter(this, netValueListList);
        netValueListRecyclerView.setAdapter(netValueListAdapter);
        netValueListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        netValueListRecyclerView.setOnRefreshListener(new AutoLoadMoreRecyclerView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFundCompanyInfo(true);
            }
        });
        netValueListRecyclerView.setOnLoadMoreListener(new AutoLoadMoreRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getFundCompanyInfo(false);
            }
        });

        getFundCompanyInfo(true);
    }

    private void getFundCompanyInfo(final boolean initRequest) {
        if (initRequest) {
            pageNo = 1;
        } else {
            pageNo += 1;
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("fundcode", fundCode);
        params.put("pageNo", pageNo + "");
        params.put("pageSize", ConstantsUtil.PAGE_SIZE + "");

        HttpRequest.post(HttpRequest.JL_FUND_TEST_ENV_WEB_URL + HttpRequest.SINGLE_FUND_NET_VALUE, params, this, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                NetValueBean netValueBean = JSON.parseObject(response.body(), NetValueBean.class);

                if (netValueBean.getNavs() != null && !netValueBean.getNavs().isEmpty()) {
                    netValueListRecyclerView.refreshComplete();
                    netValueListAdapter.upateData(initRequest, netValueBean.getNavs());

                    if (netValueBean.getNavs().size() < ConstantsUtil.PAGE_SIZE) {
                        netValueListRecyclerView.noMoreLoading();
                    }
                } else {
                    netValueListRecyclerView.setVisibility(View.GONE);
                    noDataLinearLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                netValueListRecyclerView.refreshComplete();
            }
        });
    }

}
