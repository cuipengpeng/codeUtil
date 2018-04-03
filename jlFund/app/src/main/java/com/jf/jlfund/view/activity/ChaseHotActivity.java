package com.jf.jlfund.view.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;

import com.jf.jlfund.R;
import com.jf.jlfund.adapter.ChaseHotAdapter;
import com.jf.jlfund.base.BaseActivity;
import com.jf.jlfund.base.BaseBean;
import com.jf.jlfund.bean.ChaseHotBean;
import com.jf.jlfund.http.NetService;
import com.jf.jlfund.http.ParamMap;
import com.jf.jlfund.inter.OnResponseListener;
import com.jf.jlfund.weight.errorview.DefaultErrorPageBean;
import com.jf.jlfund.weight.errorview.ErrorBean;
import com.jf.jlfund.weight.refreshlayout.AutoLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observable;

public class ChaseHotActivity extends BaseActivity {
    @BindView(R.id.recyclerView_chaseHot)
    AutoLoadMoreRecyclerView recyclerView;

    List<ChaseHotBean> chaseHotList;
    ChaseHotAdapter adapter;

    @Override
    protected void init() {
        initListener();
        initData();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void initListener() {
        recyclerView.setOnLoadMoreListener(new AutoLoadMoreRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                requestData(false);
            }
        });
    }

    private void initData() {
        if (chaseHotList == null) {
            chaseHotList = new ArrayList<>();
        }

        if (adapter == null) {
            adapter = new ChaseHotAdapter(this, chaseHotList);
        }
        requestData(true);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chase_hot;
    }

    @Override
    protected void doBusiness() {
    }

    private int pageNo = 1;
    private int pageSize = 10;

    private void requestData(final boolean isRefresh) {
        pageNo = isRefresh ? 1 : pageNo + 1;
        postRequest(new OnResponseListener<List<ChaseHotBean>>() {
            @Override
            public Observable<BaseBean<List<ChaseHotBean>>> createObservalbe() {
                ParamMap paramMap = new ParamMap();
                paramMap.put("pageNo", pageNo + "");
                paramMap.putLast("rowsSize", pageSize + "");
                return NetService.getNetService().getChaseHotList(paramMap);
            }

            @Override
            public void onResponse(BaseBean<List<ChaseHotBean>> result) {
                if (result == null || result.getData().isEmpty()) {
                    recyclerView.noMoreLoading();
                } else {
                    recyclerView.refreshComplete();
                    if (isRefresh) {
                        chaseHotList.clear();
                    }
                    chaseHotList.addAll(result.getData());
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
        context.startActivity(new Intent(context, ChaseHotActivity.class));
    }
}
