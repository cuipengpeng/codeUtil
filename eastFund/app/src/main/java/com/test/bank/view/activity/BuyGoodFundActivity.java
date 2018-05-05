package com.test.bank.view.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;

import com.test.bank.R;
import com.test.bank.adapter.BuyGoodFundAdapter;
import com.test.bank.base.BaseActivity;
import com.test.bank.base.BaseBean;
import com.test.bank.bean.BuyGoodFundBean;
import com.test.bank.http.NetService;
import com.test.bank.http.ParamMap;
import com.test.bank.inter.OnResponseListener;
import com.test.bank.weight.errorview.DefaultErrorPageBean;
import com.test.bank.weight.errorview.ErrorBean;
import com.test.bank.weight.refreshlayout.AutoLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observable;

public class BuyGoodFundActivity extends BaseActivity {
    @BindView(R.id.recyclerView_buyGoodFund)
    AutoLoadMoreRecyclerView recyclerView;

    BuyGoodFundAdapter adapter;

    List<BuyGoodFundBean> buyGoodFundBeanList;

    @Override
    protected void init() {
        initData();
        initListener();

        requestData(true);
    }

    private int pageNo = 1;
    private int pageSize = 10;

    private void initListener() {
        recyclerView.setOnRefreshListener(new AutoLoadMoreRecyclerView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData(true);
            }
        });
        recyclerView.setOnLoadMoreListener(new AutoLoadMoreRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                requestData(false);
            }
        });
    }

    private void initData() {
        if (buyGoodFundBeanList == null) {
            buyGoodFundBeanList = new ArrayList<>();
        }

        if (adapter == null) {
            adapter = new BuyGoodFundAdapter(this, buyGoodFundBeanList);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_buy_good_fund;
    }

    @Override
    protected void doBusiness() {
    }

    private void requestData(final boolean isRefresh) {
        pageNo = isRefresh ? 1 : pageNo + 1;
        postRequest(new OnResponseListener<List<BuyGoodFundBean>>() {
            @Override
            public Observable<BaseBean<List<BuyGoodFundBean>>> createObservalbe() {
                ParamMap paramMap = new ParamMap();
                paramMap.put("pageNo", pageNo + "");
                paramMap.putLast("rowsSize", pageSize + "");
                return NetService.getNetService().getBuyGoodFundList(paramMap);
            }

            @Override
            public void onResponse(BaseBean<List<BuyGoodFundBean>> result) {
                if (result.getData() == null || result.getData().isEmpty()) {
                    recyclerView.noMoreLoading();
                } else {
                    recyclerView.refreshComplete();
                    if (isRefresh) {
                        buyGoodFundBeanList.clear();
                    }
                    buyGoodFundBeanList.addAll(result.getData());
                    adapter.notifyDataSetChanged();
                    if (result.getData().size() < pageSize) {
                        recyclerView.noMoreLoading();
                    }
                }
            }

            @Override
            public void onError(String errorMsg) {
                recyclerView.refreshComplete();
            }
        });
    }

    @Override
    protected ErrorBean getErrorBean() {
        return new DefaultErrorPageBean(recyclerView);
    }

    public static void open(Context context) {
        context.startActivity(new Intent(context, BuyGoodFundActivity.class));
    }
}
