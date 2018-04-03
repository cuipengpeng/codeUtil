package com.jf.jlfund.view.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.jf.jlfund.R;
import com.jf.jlfund.adapter.MessageAdapter;
import com.jf.jlfund.base.BaseActivity;
import com.jf.jlfund.base.BaseBean;
import com.jf.jlfund.bean.MessageBean;
import com.jf.jlfund.http.NetService;
import com.jf.jlfund.http.ParamMap;
import com.jf.jlfund.inter.OnResponseListener;
import com.jf.jlfund.utils.LogUtils;
import com.jf.jlfund.utils.SPUtil;
import com.jf.jlfund.weight.CommonTitleBar;
import com.jf.jlfund.weight.refreshlayout.AutoLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observable;

public class MessageActivity extends BaseActivity {

    @BindView(R.id.commonTitleBar_message)
    CommonTitleBar commonTitleBar;
    @BindView(R.id.recyclerView_message)
    AutoLoadMoreRecyclerView recyclerView;
    @BindView(R.id.tv_empty)
    TextView tvEmpty;

    MessageAdapter adapter;

    List<MessageBean> messageList;

    @Override
    protected void init() {
        initData();
        initListener();
        requestData(true);
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
        if (messageList == null)
            messageList = new ArrayList<>();

        for (int i = 0; i < 15; i++) {
            messageList.add(new MessageBean());
        }
        if (adapter == null) {
            adapter = new MessageAdapter(this, messageList);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_message;
    }

    private int pageNo = 1;
    private int pageSize = 10;

    @Override
    protected void doBusiness() {

    }

    private void requestData(final boolean isRefresh) {
        pageNo = isRefresh ? 1 : pageNo + 1;
        postRequest(new OnResponseListener<List<MessageBean>>() {
            @Override
            public Observable<BaseBean<List<MessageBean>>> createObservalbe() {
                ParamMap paramMap = new ParamMap();
                paramMap.put("token", SPUtil.getInstance().getToken());
                paramMap.put("rowsSize", pageSize + "");
                paramMap.putLast("pageNo", pageNo + "");
                return NetService.getNetService().getMessageList(paramMap);
            }

            @Override
            public void onResponse(BaseBean<List<MessageBean>> result) {
                if (result.isSuccess() && result.getData() != null && !result.getData().isEmpty()) {
                    recyclerView.refreshComplete();
                    if (isRefresh)
                        messageList.clear();
                    messageList.addAll(result.getData());
                    adapter.notifyDataSetChanged();
                    if (result.getData().size() < pageSize) {
                        recyclerView.noMoreLoading();
                    }
                } else {
                    if (isRefresh || pageNo == 1) {
                        recyclerView.setVisibility(View.GONE);
                        tvEmpty.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.noMoreLoading();
                    }
                }
            }

            @Override
            public void onError(String errorMsg) {
                LogUtils.e("onError: " + errorMsg);
            }
        });
    }

    public static void open(Context context) {
        context.startActivity(new Intent(context, MessageActivity.class));
    }
}
