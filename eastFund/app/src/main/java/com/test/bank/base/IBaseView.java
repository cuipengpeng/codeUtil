package com.test.bank.base;

import com.test.bank.inter.OnResponseListener;


public interface IBaseView {
    //显示进度加载框
    void showProgressDialog();

    //隐藏进度加载框
    void hideProgressDialog();

    void showNoDataView();

    void showContentView();

    void showNetworkErrorView();

    void onRequestError(int errorCode, OnResponseListener onResponseListener);

    void onRefreshSuccess(OnResponseListener onResponseListener);

    void onForceUpdate();

    void onTokenInvalid();
}
