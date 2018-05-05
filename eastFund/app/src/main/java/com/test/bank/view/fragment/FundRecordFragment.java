package com.test.bank.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.adapter.FundRecordAdapter;
import com.test.bank.base.BaseBean;
import com.test.bank.base.BaseFragment;
import com.test.bank.bean.FundTradeRecordBean;
import com.test.bank.http.NetService;
import com.test.bank.http.ParamMap;
import com.test.bank.inter.OnResponseListener;
import com.test.bank.utils.LogUtils;
import com.test.bank.utils.SPUtil;
import com.test.bank.utils.ToastUtils;
import com.test.bank.weight.refreshlayout.AutoLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observable;

public class FundRecordFragment extends BaseFragment {
    private static final String ARG_PARAM1 = "isAll";

    private Boolean isAll = true;

    @BindView(R.id.recyclerView_fundRecord)
    AutoLoadMoreRecyclerView recyclerView;
    @BindView(R.id.tv_fragmentFundRecord_empty)
    TextView tvEmpty;

    FundRecordAdapter adapter;

    List<FundTradeRecordBean> fundList;

    public FundRecordFragment() {
    }

    public static FundRecordFragment newInstance(Boolean isAll) {
        FundRecordFragment fragment = new FundRecordFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM1, isAll);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void init() {
        if (getArguments() == null) {
            ToastUtils.showShort("初始化参数为空");
        }
        isAll = getArguments().getBoolean(ARG_PARAM1);
        LogUtils.e("isAll: " + isAll);

        initData();

        adapter = new FundRecordAdapter(getContext(), fundList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        recyclerView.setOnLoadMoreListener(new AutoLoadMoreRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                requestData(false);
            }
        });
    }

    private void initData() {
        fundList = new ArrayList<>();
    }

    private int pageNo = 1;
    private int pageSize = 15;

    @Override
    protected void doBusiness() {
        requestData(true);

    }

    @Override
    protected boolean isCountPage() {
        return true;
    }

    private void requestData(final boolean isRefresh) {
        pageNo = isRefresh ? 1 : pageNo + 1;
        postRequest(new OnResponseListener<List<FundTradeRecordBean>>() {
            @Override
            public Observable<BaseBean<List<FundTradeRecordBean>>> createObservalbe() {
                ParamMap paramMap = new ParamMap();
                paramMap.put("token", SPUtil.getInstance().getToken());
                paramMap.put("type", isAll ? "3" : "0");    //记录类型(3全部，0 进行中)
                paramMap.put("pageNo", pageNo + "");
                paramMap.putLast("rowsSize", "15");
                return NetService.getNetService().getFundTradeRecordList(paramMap);
            }

            @Override
            public void onResponse(BaseBean<List<FundTradeRecordBean>> result) {
                if (!result.isSuccess()) {
                    return;
                }
                if (result.getData() == null || result.getData().isEmpty()) {
                    recyclerView.noMoreLoading();
                } else {
                    recyclerView.refreshComplete();
                    if (isRefresh) {
                        fundList.clear();
                    }
                    fundList.addAll(result.getData());
                    adapter.notifyDataSetChanged();
                    if (result.getData().size() < pageSize) {
                        recyclerView.noMoreLoading();
                    }
                }
            }

            @Override
            public void onError(String errorMsg) {
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_fund_record;
    }

}
