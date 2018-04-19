package com.jfbank.qualitymarket.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;

import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.adapter.SalesProgressAdapter;
import com.jfbank.qualitymarket.base.BaseMvpFragment;
import com.jfbank.qualitymarket.constants.EventBusConstants;
import com.jfbank.qualitymarket.model.SalesProgressBean;
import com.jfbank.qualitymarket.mvp.SalesProgressMVP;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.net.Response;
import com.jfbank.qualitymarket.widget.FeedRootRecyclerView;
import com.jfbank.qualitymarket.widget.TwinklingRefreshLayoutView;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.List;

import butterknife.InjectView;

/**
 * 功能：售后进度查询<br>
 * 作者：赵海<br>
 * 时间： 2017/4/20 0020<br>.
 * 版本：1.4.2
 */

public class SalesProgressFragment extends BaseMvpFragment<SalesProgressMVP.Presenter, SalesProgressMVP.Model> implements SalesProgressMVP.View {


    @InjectView(R.id.recycler_view)
    FeedRootRecyclerView recyclerView;
    @InjectView(R.id.refreshLayout)
    TwinklingRefreshLayoutView refreshLayout;
    SalesProgressAdapter salesProgressAdapter; //适配器
    Response<List<SalesProgressBean>> response; //列表数据
    private boolean isInit = true; //是否初始化请求

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_sales_progress;
    }

    @Override
    protected void initView() {

        //设置数据布局格式--线性布局
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        salesProgressAdapter = new SalesProgressAdapter(mContext, mPresenter);
        //设置适配器
        recyclerView.setAdapter(salesProgressAdapter);
        recyclerView.setHasFixedSize(true);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setAutoLoadMore(true);
        refreshLayout.setOverScrollTopShow(false);
        refreshLayout.setOverScrollBottomShow(true);
        refreshLayout.setEnableOverScroll(true);
        //下拉刷新和上啦加载更多
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {//下拉刷新
                refreshData(false, true, 1);
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {//加载更多
                if (response != null)
                    refreshData(false, false, response.getPageNo() + 1);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        setInidData(false);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData(this.isInit, true, 1);
    }


    @Override
    public void onNetFailure(String url, String msg) {
        if (TextUtils.equals(HttpRequest.GET_QUERYSCHEDULE, url)) {
            if (isInit) {
                setError(-1, msg, true, null);
                showError();
            } else {
                super.onNetFailure(url, msg);
            }
        } else {
            super.onNetFailure(url, msg);
        }
    }

    /**
     * 更新数据
     */
    private void refreshData(boolean isInit, boolean isRefresh, int pageNum) {
        this.isInit = isInit;
        mPresenter.getListData(isInit, isRefresh, pageNum);
    }

    @Override
    protected void initData() {
        refreshData(true, false, 1);
    }

    @Override
    public String getPageName() {
        return getString(R.string.str_pagenanme_salesprogress);
    }

    @Override
    public void onFinishReFreshView() {
        refreshLayout.finishRefreshing();
    }

    @Override
    public void onFinishLoadMoreView() {
        refreshLayout.finishLoadmore();
    }


    @Override
    public void updateListView(Response<List<SalesProgressBean>> response) {
        this.isInit = false;
        this.response = response;
        salesProgressAdapter.upateData(!(response.getPageNo() > 1), response.getData());
        if (response.getPageNo() < response.getPageCount()) {//是否需要继续分页加载
            refreshLayout.setEnableLoadmore(true);
        } else {
            refreshLayout.setEnableLoadmore(false);
        }
    }

    @Override
    public void cancelOrderSuceess(int position) {
        salesProgressAdapter.updateCancelOrderSuccess(position);
    }
}