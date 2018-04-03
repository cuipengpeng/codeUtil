package com.jf.jlfund.base;

import com.jf.jlfund.inter.OnResponseListener;

/**
 * Created by 55 on 2017/12/25.
 */

public interface IBaseUIListener extends IBaseView {

    void onRequestError(int errorCode, OnResponseListener onResponseListener);

    void onRefreshSuccess(OnResponseListener onResponseListener);

    void onForceUpdate();

    void onTokenInvalid();
}
