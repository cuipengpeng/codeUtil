package com.jf.jlfund.view.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;

import com.jf.jlfund.R;
import com.jf.jlfund.adapter.GraspChanceAdapter;
import com.jf.jlfund.base.BaseActivity;
import com.jf.jlfund.base.BaseBean;
import com.jf.jlfund.bean.GraspChanceBean;
import com.jf.jlfund.http.NetService;
import com.jf.jlfund.http.ParamMap;
import com.jf.jlfund.inter.OnResponseListener;
import com.jf.jlfund.utils.MD5;
import com.jf.jlfund.weight.errorview.DefaultErrorPageBean;
import com.jf.jlfund.weight.errorview.ErrorBean;
import com.jf.jlfund.weight.refreshlayout.AutoLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observable;

public class GraspChanceActivity extends BaseActivity {
    private static final String TAG = "GraspChanceActivity";
    @BindView(R.id.recyclerView_graspChance)
    AutoLoadMoreRecyclerView recyclerView;

    List<GraspChanceBean> graspChanceList;
    GraspChanceAdapter adapter;

    @Override
    protected void init() {
        initData();
        initListener();

        requestData(true);
    }

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
        if (graspChanceList == null) {
            graspChanceList = new ArrayList<>();
        }

        if (adapter == null) {
            adapter = new GraspChanceAdapter(this, graspChanceList);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_grasp_chance;
    }

    @Override
    protected void doBusiness() {
    }


    private int pageNo = 1;
    private int pageSize = 10;

    private void requestData(final boolean isRefresh) {
        pageNo = isRefresh ? 1 : pageNo + 1;
        postRequest(new OnResponseListener<List<GraspChanceBean>>() {
            @Override
            public Observable<BaseBean<List<GraspChanceBean>>> createObservalbe() {
                ParamMap paramMap = new ParamMap();
                paramMap.put("pageNo", pageNo + "");
                paramMap.put("rowsSize", pageSize + "");
                paramMap.put("showModule", "2");
                paramMap.put("sign", MD5.sign(paramMap));
                return NetService.getNetService().getGraspChanceList(paramMap);
            }

            @Override
            public void onResponse(BaseBean<List<GraspChanceBean>> result) {
                if (result.getData() == null || result.getData().isEmpty()) {
                    recyclerView.noMoreLoading();
                } else {
                    recyclerView.refreshComplete();
                    if (isRefresh) {
                        graspChanceList.clear();
                    }
                    graspChanceList.addAll(result.getData());
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
        context.startActivity(new Intent(context, GraspChanceActivity.class));
    }
}
