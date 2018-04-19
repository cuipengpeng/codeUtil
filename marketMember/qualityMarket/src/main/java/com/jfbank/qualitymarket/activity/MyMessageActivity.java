package com.jfbank.qualitymarket.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;

import com.daimajia.swipe.util.Attributes;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.adapter.MyMessageAdapter;
import com.jfbank.qualitymarket.base.BaseMvpActivity;
import com.jfbank.qualitymarket.constants.EventBusConstants;
import com.jfbank.qualitymarket.model.MessageBean;
import com.jfbank.qualitymarket.mvp.MyMessageMVP;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.net.Response;
import com.jfbank.qualitymarket.widget.FeedRootRecyclerView;
import com.jfbank.qualitymarket.widget.TwinklingRefreshLayoutView;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import org.simple.eventbus.EventBus;

import java.util.List;

import butterknife.InjectView;

/**
 * 功能：我的消息<br>
 * 作者：赵海
 * 时间： 2016/12/2 0002<br>.
 * 版本：1.2.0
 */

public class MyMessageActivity extends BaseMvpActivity<MyMessageMVP.Presenter, MyMessageMVP.Model> implements MyMessageMVP.View {
    @InjectView(R.id.recycler_view)
    FeedRootRecyclerView recyclerView;
    @InjectView(R.id.refreshLayout)
    TwinklingRefreshLayoutView refreshLayout;
    MyMessageAdapter adapter;
    public static final String TAG = "MyMessageActivity";
    Response<List<MessageBean>> response;
    boolean isInit = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    /**
     * 初始化组件
     */
    @Override
    protected void initView() {
        //设置数据布局格式--线性布局
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new MyMessageAdapter(this, mPresenter);
        adapter.setMode(Attributes.Mode.Single);
        //设置适配器
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        refreshLayout.setAutoLoadMore(true);
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setOverScrollBottomShow(true);

        //下拉刷新和上啦加载更多
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {//下拉刷新
                refreshData(false, true, 1);
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {//加载更多
                refreshData(false, false, response.getPageNo() + 1);
            }
        });
        setEmpty(R.drawable.ic_no_msg, "暂无消息", false, null);
        tvTitle.setText(R.string.str_title_msg);
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_my_message;
    }

    @Override
    protected void initData() {
        refreshData(true, false, 1);
    }

    @Override
    protected String getPageName() {
        return getString(R.string.str_title_msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onFinishReFreshView() {
        refreshLayout.finishRefreshing();
    }

    @Override
    public void onFinishLoadMoreView() {
        super.onFinishLoadMoreView();
        refreshLayout.finishLoadmore();
    }

    /**
     * 更新数据
     */
    private void refreshData(boolean isInit, boolean isRefresh, int pageNum) {
        this.isInit = isInit;
        mPresenter.getData(isInit, isRefresh, pageNum);
    }


    @Override
    public void onNetFailure(String url, String msg) {
        if (TextUtils.equals(HttpRequest.QUALITYUSERMESSAGESHOW, url)) {
            if (isInit) {
                setError(-1, msg, true, null);
                showError();
            }
        } else {
            super.onNetFailure(url, msg);
        }
    }


    /**
     * 更新消息状态
     *
     * @param position
     */
    @Override
    public void updateMsgStatus(final int position) {
        EventBus.getDefault().post(new Object(), EventBusConstants.EVENTT_UPDATE_MESSAGE_NUM);
        adapter.getData().get(position).setReadStatus("1");
        adapter.notifyItemChanged(position);
    }

    @Override
    public void updateDeleteMsg(int position) {
        adapter.getData().remove(position);
        if (adapter.getData().size() > 0) {
            adapter.notifyItemRemoved(position);
        } else {
            setEmpty(R.drawable.ic_no_msg, "暂无消息", false, null);
            showEmpty();
        }
    }


    @Override
    public void updateListView(final boolean isRefresh, Response<List<MessageBean>> response) {
        this.response = response;
        adapter.updateData(isRefresh, response.getData());
        if (response.getPageNo() < response.getPageCount()) {//是否需要继续分页加载
            refreshLayout.setEnableLoadmore(true);
        } else {
            refreshLayout.setEnableLoadmore(false);
        }
    }
}