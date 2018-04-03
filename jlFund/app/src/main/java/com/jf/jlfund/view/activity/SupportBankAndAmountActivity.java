package com.jf.jlfund.view.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.jf.jlfund.R;
import com.jf.jlfund.adapter.SupportBankAndAmountAdapter;
import com.jf.jlfund.base.BaseActivity;
import com.jf.jlfund.base.BaseBean;
import com.jf.jlfund.bean.SupportBankAndAmountBean;
import com.jf.jlfund.http.NetService;
import com.jf.jlfund.http.ParamMap;
import com.jf.jlfund.inter.OnResponseListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observable;

public class SupportBankAndAmountActivity extends BaseActivity {
    @BindView(R.id.recyclerView_supportBankAndAmount)
    RecyclerView recyclerView;

    SupportBankAndAmountAdapter adapter;
    List<SupportBankAndAmountBean> mList;

    @Override
    protected void init() {
        mList = new ArrayList<>();
        adapter = new SupportBankAndAmountAdapter(this, mList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        requestData();
    }

    private void requestData() {
        postRequest(new OnResponseListener<List<SupportBankAndAmountBean>>() {
            @Override
            public Observable<BaseBean<List<SupportBankAndAmountBean>>> createObservalbe() {
                return NetService.getNetService().checkSupportBankAndAmount(new ParamMap());
            }

            @Override
            public void onResponse(BaseBean<List<SupportBankAndAmountBean>> result) {
                if (result.isSuccess() && result.getData() != null) {
                    mList.addAll(result.getData());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(String errorMsg) {
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_support_bank_and_amount;
    }

    @Override
    protected void doBusiness() {

    }

    public static void open(Context context) {
        context.startActivity(new Intent(context, SupportBankAndAmountActivity.class));
    }
}
