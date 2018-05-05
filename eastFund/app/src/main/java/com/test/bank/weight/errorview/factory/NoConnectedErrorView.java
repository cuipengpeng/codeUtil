package com.test.bank.weight.errorview.factory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.test.bank.R;
import com.test.bank.weight.errorview.ErrorBean;


/**
 * Created by 55 on 2017/10/10.
 * 无网络连接的错误布局
 */

public class NoConnectedErrorView implements ErrorView {

    private Context mContext;
    private OnErrorPageRefreshListener refreshListener;
    private ErrorBean errorPageBean;    //用来定制布局的文案

    public NoConnectedErrorView(Context context, ErrorBean errorPageBean, OnErrorPageRefreshListener refreshListener) {
        mContext = context;
        this.refreshListener = refreshListener;
        this.errorPageBean = errorPageBean;
    }

    @Override
    public View productErrorView() {
        View errorView = LayoutInflater.from(mContext).inflate(R.layout.layout_error_no_connect, null, false);
        errorView.findViewById(R.id.tv_reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (refreshListener != null) {
                    refreshListener.onRefresh();
                }
            }
        });
        return errorView;
    }
}
