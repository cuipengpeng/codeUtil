package com.caishi.chaoge.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.caishi.chaoge.R;
import com.caishi.chaoge.base.BaseActivity;
import com.caishi.chaoge.base.BaseRequestInterface;
import com.caishi.chaoge.bean.NewMessageBean;
import com.caishi.chaoge.request.DeleteNewMessageRequest;
import com.caishi.chaoge.request.GetNewMessageRequest;
import com.caishi.chaoge.ui.adapter.MessageDetailsAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gyf.barlibrary.ImmersionBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;
import static com.caishi.chaoge.utils.ConstantUtils.DOWN;
import static com.caishi.chaoge.utils.ConstantUtils.UP;

public class MessageDetailsActivity extends BaseActivity {

    private String title;
    private int pageFlag;//0 粉丝  1 赞  2 评论  3 系统消息
    private SmartRefreshLayout srl_msgDetails_refresh;
    private RecyclerView rv_msgDetails_msg;
    private LinearLayout ll_data_null;
    private MessageDetailsAdapter messageDetailsAdapter;
    private int pageSize = 10;
    private long since = -1;
    private boolean isAddData = false;
    private ArrayList<NewMessageBean> newMessageList = new ArrayList<>();
    private PopupMenu popupMenu;

    @Override
    public void initBundle(Bundle bundle) {
        super.initBundle(bundle);
        title = bundle.getString("title");
        pageFlag = bundle.getInt("pageFlag");

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_message_details;
    }

    @Override
    public void initView(View view) {
        ImmersionBar.with(this).titleBar(R.id.view_msgDetails)
                .statusBarDarkFont(true)   //状态栏字体是深色，不写默认为亮色
                .init();
        setBaseTitle(title, false);
        srl_msgDetails_refresh = $(R.id.srl_msgDetails_refresh);
        rv_msgDetails_msg = $(R.id.rv_msgDetails_msg);
        ll_data_null = $(R.id.ll_data_null);
    }

    @Override
    public void doBusiness() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_msgDetails_msg.setLayoutManager(linearLayoutManager);
        messageDetailsAdapter = new MessageDetailsAdapter(pageFlag);
        rv_msgDetails_msg.setAdapter(messageDetailsAdapter);
        getData(-1, DOWN);

    }


    private void getData(long since, String slipType) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("pageSize", pageSize);
        paramsMap.put("since", since);
        paramsMap.put("slipType", slipType);
        paramsMap.put("userId", getCGUserId());

        GetNewMessageRequest.newInstance(mContext).getNewMessage(pageFlag, paramsMap, new BaseRequestInterface<ArrayList<NewMessageBean>>() {
            @Override
            public void success(int state, String msg, ArrayList<NewMessageBean> newFansBeans) {
                refreshData(newFansBeans);
            }

            @Override
            public void error(int state, String msg) {

            }
        });


    }


    private void refreshData(ArrayList<NewMessageBean> newFansBeans) {
        srl_msgDetails_refresh.finishRefresh();
        messageDetailsAdapter.setEnableLoadMore(true);
        newMessageList.addAll(newFansBeans);
        ll_data_null.setVisibility((newMessageList.size() == 0 && newFansBeans.size() == 0) ? View.VISIBLE : View.GONE);
        srl_msgDetails_refresh.setVisibility((newMessageList.size() == 0 && newFansBeans.size() == 0) ? View.GONE : View.VISIBLE);
        if (!isAddData) {
            messageDetailsAdapter.setNewData(newMessageList);
        } else {
            if (newFansBeans.size() > 0) {
                messageDetailsAdapter.addData(newFansBeans);
            }
        }
        if (newFansBeans.size() < pageSize) {
            //第一页如果不够一页就不显示没有更多数据布局
            messageDetailsAdapter.loadMoreEnd(false);
        } else {
            messageDetailsAdapter.loadMoreComplete();
        }
    }

    @Override
    public void setListener() {
        srl_msgDetails_refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                newMessageList.clear();
                isAddData = false;
                getData(-1, DOWN);
            }
        });
        messageDetailsAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                isAddData = true;
                getData(newMessageList.get(newMessageList.size() - 1).targetTime, UP);
            }
        }, rv_msgDetails_msg);
        messageDetailsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (pageFlag > 0 && newMessageList.size() > 0) {
                    Intent intent = new Intent(MessageDetailsActivity.this, MessageVideoActivity.class);
                    intent.putExtra("momentId", newMessageList.get(position).momentId);
                    intent.putExtra("pageFlag", pageFlag);
                    startActivity(intent);

                }
            }
        });
        messageDetailsAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                View viewByPosition = adapter.getViewByPosition(rv_msgDetails_msg, position, R.id.tv_msgDetails_msg);
                showPopupMenu(viewByPosition, position);
                return true;
            }
        });
        rv_msgDetails_msg.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (SCROLL_STATE_SETTLING == newState) {
                    if (null != popupMenu) {
                        popupMenu.dismiss();
                    }
                }

            }
        });
    }

    private void showPopupMenu(View view, final int position) {
        // 这里的view代表popupMenu需要依附的view
        popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.sample_menu, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // 控件每一个item的点击事件
                CharSequence title = item.getTitle();
                if (title.equals("删除")) {
                    deleteData(newMessageList.get(position).targetTime + "", position);
                } else {
                    popupMenu.dismiss();
                }


                return true;
            }
        });
    }

    private void deleteData(String targetTime, final int position) {

        DeleteNewMessageRequest.newInstance(mContext).deleteNewMessage(pageFlag, targetTime, new BaseRequestInterface<Boolean>() {
            @Override
            public void success(int state, String msg, Boolean isSuccess) {
                if (isSuccess)
                    messageDetailsAdapter.remove(position);
            }

            @Override
            public void error(int state, String msg) {

            }
        });


    }

    @Override
    public void widgetClick(View v) {

    }
}
