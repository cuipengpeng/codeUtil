package com.test.bank.inter;

import com.test.bank.base.BaseBean;

import io.reactivex.Observable;

/**
 * Created by 55 on 2017/12/25.
 */

public interface SuperResponseListener<T> {
    Observable<BaseBean<T>> createObservalbe();

    void onResponse(BaseBean<T> result);

    void onError(String errorMsg);
}
