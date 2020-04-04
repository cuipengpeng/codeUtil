package com.caishi.chaoge.ui.activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.caishi.chaoge.R;
import com.caishi.chaoge.base.BaseApplication;
import com.caishi.chaoge.base.BaseUILocalDataActivity;
import com.caishi.chaoge.bean.HomeDataBean;
import com.caishi.chaoge.bean.MyFansBean;
import com.caishi.chaoge.http.HttpRequest;
import com.caishi.chaoge.http.RequestURL;
import com.caishi.chaoge.ui.adapter.MyFollowsAdapter;
import com.caishi.chaoge.ui.widget.refreshlayout.AutoLoadMoreRecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class MyFollowsActivity extends BaseUILocalDataActivity{
    @BindView(R.id.rv_myFansActivity_attentionList)
    AutoLoadMoreRecyclerView rvMyFansActivityAttentionList;
    @BindView(R.id.srl_myFansActivity_attentionList)
    SwipeRefreshLayout srlMyFansActivityAttentionList;
    @BindView(R.id.srl_myFansActivity_noData)
    SwipeRefreshLayout noDataSwipeRefreshLayout;
    @BindView(R.id.iv_networkError)
    ImageView ivNetworkError;
    @BindView(R.id.tv_networkError)
    TextView tvNetworkError;
    @BindView(R.id.ll_myFansActivity_netwrokError)
    LinearLayout netwrokErrorLinearLayout;
    @BindView(R.id.btn_networkError_refresh)
    Button networkErrorRefreshButon;

    public static final int PAGE_SIZE=10;
    public static final String KEY_OF_USER_ID="userIdKey";
    private MyFollowsAdapter myFansAdapter;
    private String userId = "";

    @Override
    protected String getPageTitle() {
        return "我的关注";
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_my_fans;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        userId = intent.getStringExtra(KEY_OF_USER_ID);
    }

        @Override
    protected void initPageData() {
        userId = getIntent().getStringExtra(KEY_OF_USER_ID);
        noDataSwipeRefreshLayout.setVisibility(View.GONE);
        netwrokErrorLinearLayout.setVisibility(View.GONE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        rvMyFansActivityAttentionList.setLayoutManager(linearLayoutManager);
        myFansAdapter = new MyFollowsAdapter(mContext);
        rvMyFansActivityAttentionList.setAdapter(myFansAdapter);

        networkErrorRefreshButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMyFollowsList(true, -1, "UP");
            }
        });

        srlMyFansActivityAttentionList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMyFollowsList(true, -1, "DOWN");
            }
        });
        noDataSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMyFollowsList(true, -1, "DOWN");
            }
        });
        rvMyFansActivityAttentionList.setOnLoadMoreListener(new AutoLoadMoreRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                long targetTime = myFansAdapter.mDataList.get(myFansAdapter.mDataList.size() - 1).getTargetTime();
                getMyFollowsList(false, targetTime, "UP");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMyFollowsList(true, -1, "UP");
    }

    private void getMyFollowsList(final boolean refresh, long startTime, String direction) {
        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("userId", userId);
        paramsMap.put("pageSize", PAGE_SIZE+ "");
        paramsMap.put("since", startTime + "");
        paramsMap.put("slipType", direction);

        HttpRequest.post(true, HttpRequest.APP_INTERFACE_WEB_URL + RequestURL.MY_FOLLOW, paramsMap, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onSuccess(String response) {
                refreshComplete();
                if (refresh) {
                    rvMyFansActivityAttentionList.reset();
                }

                Gson gson = new Gson();
                List<MyFansBean> myFansBeanList = gson.fromJson(response, new TypeToken<List<MyFansBean>>(){}.getType());
                if (refresh && myFansBeanList.size() <= 0) {
                    noDataSwipeRefreshLayout.setVisibility(View.VISIBLE);
                    srlMyFansActivityAttentionList.setVisibility(View.GONE);
                    netwrokErrorLinearLayout.setVisibility(View.GONE);
                } else {
                    srlMyFansActivityAttentionList.setVisibility(View.VISIBLE);
                    noDataSwipeRefreshLayout.setVisibility(View.GONE);
                    netwrokErrorLinearLayout.setVisibility(View.GONE);
                    if (myFansBeanList.size() > 0) {
                        myFansAdapter.upateData(refresh, myFansBeanList);
                    }

                    if (myFansBeanList.size() < PAGE_SIZE ) {
                        rvMyFansActivityAttentionList.noMoreLoading();
                    }
                }

            }

            @Override
            public void onFailure(String t) {
                refreshComplete();
                if (myFansAdapter.mDataList.size() > 0) {
                    Toast.makeText(BaseApplication.getContext(), "请检查网络连接是否正常", Toast.LENGTH_SHORT).show();
                } else {
                    netwrokErrorLinearLayout.setVisibility(View.VISIBLE);
                    noDataSwipeRefreshLayout.setVisibility(View.GONE);
                    srlMyFansActivityAttentionList.setVisibility(View.GONE);
                }
                myFansAdapter.notifyDataSetChanged();
            }
        });
    }


    private void refreshComplete() {
        if (srlMyFansActivityAttentionList.isRefreshing()) {
            srlMyFansActivityAttentionList.setRefreshing(false);//取消刷新
        }
        if (noDataSwipeRefreshLayout.isRefreshing()) {
            noDataSwipeRefreshLayout.setRefreshing(false);//取消刷新
        }
        rvMyFansActivityAttentionList.loadMoreComplete();
    }

}
