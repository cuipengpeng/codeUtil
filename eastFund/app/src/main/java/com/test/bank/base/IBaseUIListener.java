package com.test.bank.base;

import com.test.bank.inter.OnResponseListener;


public interface IBaseUIListener extends IBaseView {

    void onRequestError(int errorCode, OnResponseListener onResponseListener);

    void onRefreshSuccess(OnResponseListener onResponseListener);

    void onForceUpdate();

    void onTokenInvalid();
}
