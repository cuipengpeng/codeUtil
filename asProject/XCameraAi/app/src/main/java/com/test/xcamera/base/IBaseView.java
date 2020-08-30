package com.test.xcamera.base;


public interface IBaseView {
    //显示进度加载框
    void showProgressDialog();

    //隐藏进度加载框
    void hideProgressDialog();

    void showNoDataView();

    void showContentView();

    void showNetworkErrorView();

    void onForceUpdate();

    void onTokenInvalid();
}
