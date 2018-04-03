package com.jf.jlfund.inter;

import com.jf.jlfund.base.BaseBean;

import io.reactivex.Observable;

/**
 * Created by 55 on 2017/12/25.
 */

public interface SuperResponseListener<T> {
    Observable<BaseBean<T>> createObservalbe();

    void onResponse(BaseBean<T> result);

    void onError(String errorMsg);
}
