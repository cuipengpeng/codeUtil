package com.jfbank.qualitymarket.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;

import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.adapter.SalesApplyAdapter;
import com.jfbank.qualitymarket.base.BaseMvpFragment;
import com.jfbank.qualitymarket.model.SaleApplyOrderBean;
import com.jfbank.qualitymarket.mvp.SalesApplyMVP;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.net.Response;
import com.jfbank.qualitymarket.widget.FeedRootRecyclerView;
import com.jfbank.qualitymarket.widget.TwinklingRefreshLayoutView;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.util.List;

import butterknife.InjectView;

/**
 * 功能：售后申请<br>
 * 作者：赵海<br>
 * 时间： 2017/4/20 0020<br>.
 * 版本：1.2.0
 */

public class SalesApplyFragment extends BaseMvpFragment<SalesApplyMVP.Presenter, SalesApplyMVP.Model> implements SalesApplyMVP.View {


    @InjectView(R.id.recycler_view)
    FeedRootRecyclerView recyclerView;
    @InjectView(R.id.refreshLayout)
    TwinklingRefreshLayoutView refreshLayout;
    SalesApplyAdapter salesApplyAdapter;
    Response<List<SaleApplyOrderBean>> response;
    private boolean isInit=true;

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_sales_apply;
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
    protected void initView() {

        //设置数据布局格式--线性布局
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        salesApplyAdapter = new SalesApplyAdapter(mContext, mPresenter);
        //设置适配器
        recyclerView.setAdapter(salesApplyAdapter);
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
    public void onNetFailure(String url, String msg) {
        if (TextUtils.equals(HttpRequest.GET_QUERYAFTERSALES, url)) {
            if (isInit) {//
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
    public void onFinishReFreshView() {
        refreshLayout.finishRefreshing();
    }

    @Override
    public void onFinishLoadMoreView() {
        refreshLayout.finishLoadmore();
    }

    @Override
    public String getPageName() {
        return getString(R.string.str_pagename_salesapply);
    }

    @Override
    public void updateListView(Response<List<SaleApplyOrderBean>> response) {
        this.isInit=false;
        this.response = response;
        salesApplyAdapter.upateData(!(response.getPageNo() > 1), response.getData());
        if (response.getPageNo() < response.getPageCount()) {//是否需要继续分页加载
            refreshLayout.setEnableLoadmore(true);
        } else {
            refreshLayout.setEnableLoadmore(false);
        }
    }
}