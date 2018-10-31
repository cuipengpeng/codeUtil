package com.test.bank.view.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.test.bank.R;
import com.test.bank.adapter.TradeRecordListAdapter;
import com.test.bank.base.BaseUIFragment;
import com.test.bank.bean.BaobaoBuyRecordBean;
import com.test.bank.http.HttpRequest;
import com.test.bank.utils.ConstantsUtil;
import com.test.bank.utils.SPUtil;
import com.test.bank.weight.refreshlayout.AutoLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Response;

/**
 *
 */
public class TradeRecordListFragment extends BaseUIFragment {

    @BindView(R.id.rv_tradeRecordListFragment_tradeRecordList)
    AutoLoadMoreRecyclerView tradeRecordRecyclerView;
    @BindView(R.id.ll_list_no_data_view)
    LinearLayout noDataViewLinearLayout;


    private int pageNo = 1;

    private String mTradeRecordStatus = "";
    private TradeRecordListAdapter tradeRecordListAdapter;
    private List<BaobaoBuyRecordBean.TradeHistory> mIndustryDistributionList = new ArrayList<BaobaoBuyRecordBean.TradeHistory>();

    /**
     * @param tradeRecordStatus 记录类型(1-存入，2-取出, 3-全部)
     * @return
     */
    public static TradeRecordListFragment newInstance(String tradeRecordStatus) {
        TradeRecordListFragment tradeRecordListFragment = new TradeRecordListFragment();
        tradeRecordListFragment.mIndustryDistributionList.clear();
        tradeRecordListFragment.mTradeRecordStatus = tradeRecordStatus;
        return tradeRecordListFragment;
    }

    @Override
    protected String getPageTitle() {
        return null;
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.fragment_trade_record_list;
    }

    @Override
    protected void initPageData() {
        tradeRecordListAdapter = new TradeRecordListAdapter(getActivity(), mIndustryDistributionList);
        tradeRecordRecyclerView.setAdapter(tradeRecordListAdapter);
        tradeRecordRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        tradeRecordRecyclerView.setOnRefreshListener(new AutoLoadMoreRecyclerView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFundCompanyInfo(true);
            }
        });
        tradeRecordRecyclerView.setOnLoadMoreListener(new AutoLoadMoreRecyclerView.OnLoadMoreListener() {
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
        params.put("token", SPUtil.getInstance().getToken());
        params.put("type", mTradeRecordStatus);//记录类型(1-存入，2-取出, 3-全部)
        params.put("pageNo", pageNo + "");
        params.put("rowsSize", ConstantsUtil.PAGE_SIZE + "");

        HttpRequest.post(HttpRequest.APP_INTERFACE_WEB_URL + HttpRequest.BAOBAO_BUY_RECORD_LIST, params, this, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                BaobaoBuyRecordBean baobaoBuyRecordBean = JSON.parseObject(response.body(), BaobaoBuyRecordBean.class);

                if (baobaoBuyRecordBean.getTradeHistory() != null && !baobaoBuyRecordBean.getTradeHistory().isEmpty()) {
                    tradeRecordRecyclerView.refreshComplete();
                    tradeRecordListAdapter.upateData(initRequest, baobaoBuyRecordBean.getTradeHistory());

                    if (baobaoBuyRecordBean.getTradeHistory().size() < ConstantsUtil.PAGE_SIZE) {
                        tradeRecordRecyclerView.noMoreLoading();
                    }
                } else {
                    tradeRecordRecyclerView.noMoreLoading();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
//                netValueListRecyclerView.refreshComplete();
            }
        });
    }

    @Override
    protected boolean isCountPage() {
        return true;
    }
}
