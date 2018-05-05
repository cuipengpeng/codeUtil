package com.test.bank.view.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.adapter.TradeRecordAdapter;
import com.test.bank.base.BaseActivity;
import com.test.bank.base.BaseBean;
import com.test.bank.bean.FundTradeRecordBean;
import com.test.bank.http.NetService;
import com.test.bank.http.ParamMap;
import com.test.bank.inter.OnResponseListener;
import com.test.bank.utils.SPUtil;
import com.test.bank.utils.UIUtils;
import com.test.bank.weight.errorview.DefaultErrorPageBean;
import com.test.bank.weight.errorview.ErrorBean;
import com.test.bank.weight.refreshlayout.AutoLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observable;

/**
 * 单只基金交易记录
 */
public class SingleFundTradeRecordActivity extends BaseActivity {

    @BindView(R.id.tv_tradeRecord_title)
    TextView tvTitle;
    @BindView(R.id.ll_sftr_root)
    LinearLayout llRootView;
    @BindView(R.id.recyclerView_tradeFund)
    AutoLoadMoreRecyclerView recyclerView;

    String fundName;
    String fundCode;

    List<FundTradeRecordBean> tradeRecordList;
    TradeRecordAdapter adapter;

    @Override
    protected void init() {
        if (getIntent() != null) {
            fundName = getIntent().getStringExtra(PARAM_FUND_NAME);
            fundCode = getIntent().getStringExtra(PARAM_FUND_CODE);
        }
        UIUtils.setText(tvTitle, fundName + "(" + fundCode + ")");
        initData();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setOnLoadMoreListener(new AutoLoadMoreRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                requestData(false);
            }
        });
    }

    private void initData() {
        if (tradeRecordList == null)
            tradeRecordList = new ArrayList<>();
        adapter = new TradeRecordAdapter(this, tradeRecordList);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_fund_trade_record;
    }

    @Override
    protected void doBusiness() {
        requestData(true);
    }

    private int pageNo = 1;
    private int pageSize = 15;

    private void requestData(final boolean isRefresh) {
        pageNo = isRefresh ? 1 : pageNo + 1;
        postRequest(new OnResponseListener<List<FundTradeRecordBean>>() {
            @Override
            public Observable<BaseBean<List<FundTradeRecordBean>>> createObservalbe() {
                ParamMap paramMap = new ParamMap();
                paramMap.put("token", SPUtil.getInstance().getToken());
                paramMap.put("type", "3");    //记录类型(3全部，0 进行中)
                paramMap.put("fundcode", fundCode);    //记录类型(3全部，0 进行中)
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
                        tradeRecordList.clear();
                    }
                    tradeRecordList.addAll(result.getData());
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
    protected ErrorBean getErrorBean() {
        return new DefaultErrorPageBean(llRootView, "没有更多了~");
    }

    private static String PARAM_FUND_NAME = "fundName";
    private static String PARAM_FUND_CODE = "fundCode";

    public static void open(Context context, String fundName, String fundCode) {
        Intent intent = new Intent(context, SingleFundTradeRecordActivity.class);
        intent.putExtra(PARAM_FUND_NAME, fundName);
        intent.putExtra(PARAM_FUND_CODE, fundCode);
        context.startActivity(intent);
    }
}
