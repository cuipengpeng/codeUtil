package com.test.bank.view.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.test.bank.R;
import com.test.bank.adapter.SupportBankAndAmountAdapter;
import com.test.bank.base.BaseActivity;
import com.test.bank.base.BaseBean;
import com.test.bank.bean.SupportBankAndAmountBean;
import com.test.bank.http.NetService;
import com.test.bank.http.ParamMap;
import com.test.bank.inter.OnResponseListener;

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
