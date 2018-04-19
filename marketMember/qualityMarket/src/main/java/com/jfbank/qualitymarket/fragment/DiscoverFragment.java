package com.jfbank.qualitymarket.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.adapter.DiscoverAdapter;
import com.jfbank.qualitymarket.base.BaseFragment;
import com.jfbank.qualitymarket.config.CacheKeyConfig;
import com.jfbank.qualitymarket.model.DiscoverBean;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.helper.DiskLruCacheHelper;
import com.jfbank.qualitymarket.util.LogUtil;
import com.jfbank.qualitymarket.util.UserUtils;
import com.jfbank.qualitymarket.widget.TwinklingRefreshLayoutView;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;

import java.util.HashMap;
import java.util.Map;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 我的订单fragment
 *
 * @author 崔朋朋
 */

public class DiscoverFragment extends BaseFragment {
    public static final String TAG = DiscoverFragment.class.getName();
    @InjectView(R.id.iv_discoverFragment_noData)
    ImageView noDataImageView;
    @InjectView(R.id.iv_back)
    ImageView backImageView;
    @InjectView(R.id.tv_title)
    TextView titleTextView;
    @InjectView(R.id.rl_title)
    RelativeLayout titleRelativeLayout;
    @InjectView(R.id.lv_discoverFragment_discoverList)
    ListView myOrderListView;
    @InjectView(R.id.refreshLayout)
    TwinklingRefreshLayoutView refreshLayout;
    private DiscoverAdapter discoverAdapter;

    private boolean initFragment = false;

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        ButterKnife.inject(this, view);

        initFragment = true;
        discoverAdapter = new DiscoverAdapter(getActivity());
        myOrderListView.setAdapter(discoverAdapter);
        refreshLayout.setAutoLoadMore(true);
        refreshLayout.setEnableLoadmore(false, false);
        refreshLayout.setOverScrollBottomShow(false);

        //下拉刷新和上啦加载更多
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {//下拉刷新
                getDiscoverList();
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {//加载更多
                refreshLayout.finishLoadmore();
            }
        });

        titleTextView.setText("发现");
        backImageView.setVisibility(View.GONE);
        CommonUtils.setStatusBarTitle(getActivity(), titleRelativeLayout);
        noDataImageView.setVisibility(View.GONE);
        List<DiscoverBean> discoverList = DiskLruCacheHelper.getAsSerializableList(CacheKeyConfig.CACHE_DISCOVERFRAGMENT, DiscoverBean[].class);
        if (!CommonUtils.isEmptyList(discoverList)) {
            discoverAdapter.updateData(discoverList);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDiscoverList();
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {// 重新显示到最前端中
            getDiscoverList();
        }
    }

    @Override
    public String getPageName() {
        return getString(R.string.str_pagename_discover);
    }

    /**
     * 获取订单列表
     */
    private void getDiscoverList() {
        if (initFragment) {
            initFragment = false;
        }

        Map<String, String> params = new HashMap<>();

        HttpRequest.post(mContext, HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.GET_DISCOVER_LIST, params, new AsyncResponseCallBack() {

            @Override
            public void onFailed(String path, String msg) {
                refreshLayout.finishRefreshing();
            }

            @Override
            public void onResult(String arg2) {
                String jsonStr = new String(arg2);
                LogUtil.printLog("获取发现：" + jsonStr);

                JSONObject jsonObject = JSON.parseObject(jsonStr);

                if (ConstantsUtil.RESPONSE_TOKEN_FAIL == jsonObject.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                    UserUtils.tokenFailDialog(DiscoverFragment.this.getActivity(), jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), TAG);
                } else if ((ConstantsUtil.RESPONSE_SUCCEED == jsonObject.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME))) {
                    if (jsonObject.containsKey(ConstantsUtil.RESPONSE_DATA_JSON_ARRAY_FIELD_NAME)) {
                        JSONArray orderJsonArray = jsonObject.getJSONArray(ConstantsUtil.RESPONSE_DATA_JSON_ARRAY_FIELD_NAME);
                        List<DiscoverBean> discoverBeanList = JSON.parseArray(orderJsonArray.toString(), DiscoverBean.class);
                        discoverAdapter.updateData(discoverBeanList);
                    } else {
                        Toast.makeText(getActivity(), ConstantsUtil.NO_MORE_DATA_PROMPT, Toast.LENGTH_SHORT).show();
                    }
                } else if ((ConstantsUtil.RESPONSE_NO_DATA == jsonObject.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME))) {
                    noDataImageView.setVisibility(View.GONE);
                    refreshLayout.setVisibility(View.GONE);
                } else {
                    Toast.makeText(DiscoverFragment.this.getActivity(), "查询发现失败", Toast.LENGTH_SHORT).show();
                }
                refreshLayout.finishRefreshing();
            }
        });
    }

}