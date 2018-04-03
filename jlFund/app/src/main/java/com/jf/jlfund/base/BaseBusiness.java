package com.jf.jlfund.base;

import android.util.Log;

import com.jf.jlfund.exception.ExceptionHandler;
import com.jf.jlfund.http.HttpConfig;
import com.jf.jlfund.inter.NestList;
import com.jf.jlfund.inter.OnResponseListener;
import com.jf.jlfund.utils.NetUtil;
import com.jf.jlfund.utils.ToastUtils;
import com.jf.jlfund.weight.refreshlayout.AutoLoadMoreRecyclerView;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by 55 on 2017/12/4.
 */

public class BaseBusiness {
    private static final String TAG = "BaseBusiness";

    public static <T> void postRequest(final OnResponseListener<T> onResponseListener) {
        postRequest(onResponseListener, null, false);
    }

    public static <T> void postRequest(final OnResponseListener<T> onResponseListener, final IBaseView iBaseView, final boolean isShowProgressDialog) {
        Observer<BaseBean<T>> observer = new Observer<BaseBean<T>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                if (!NetUtil.isNetworkAvailable()) {
                    ToastUtils.showShort("网络连接失败");
                    d.dispose();
                    return;
                }
                if (isShowProgressDialog && iBaseView != null) {
                    iBaseView.showProgressDialog();
                }
                if (onResponseListener != null) {
                    onResponseListener.onStartRequest();
                }
            }

            @Override
            public void onNext(@NonNull BaseBean<T> tBaseBean) {
                if (onResponseListener == null) {
                    return;
                }
                if (!tBaseBean.isPopDialog() && tBaseBean.getMsgStatus()) {
                    ToastUtils.showShort(tBaseBean.getResMsg());
                }
                onResponseListener.onResponse(tBaseBean);
                //处理空数据与token失效等
                if (isEmptyList(tBaseBean) && AutoLoadMoreRecyclerView.isRefreshing && iBaseView != null) {
                    iBaseView.onRequestError(HttpConfig.EMPTY_DATA, onResponseListener);
                } else if (iBaseView != null) {
                    if (tBaseBean.isSuccess()) {
                        iBaseView.onRefreshSuccess(onResponseListener);
                        iBaseView.onRequestError(HttpConfig.SUCCESS, onResponseListener);
                    } else {
                        if (tBaseBean.isTokenInvalid() && iBaseView != null) {
                            iBaseView.onTokenInvalid();
                        } else if (tBaseBean.isForceUpdate() && iBaseView != null) {
                            iBaseView.onForceUpdate();
                        }
                    }
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e(TAG, "onError: " + e.toString());
                if (iBaseView == null || onResponseListener == null) {
                    return;
                }
                if (onResponseListener != null)
                    onResponseListener.onError(e.getMessage());
                if (isShowProgressDialog) {
                    iBaseView.hideProgressDialog();
                }
                iBaseView.onRequestError(ExceptionHandler.handleException(e), onResponseListener);
            }

            @Override
            public void onComplete() {
                if (isShowProgressDialog && iBaseView != null) {
                    iBaseView.hideProgressDialog();
                }
            }
        };

        onResponseListener.createObservalbe()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(observer);
    }

    /**
     * 判断数据集合是否为空
     *
     * @param tBaseBean
     * @return
     */
    private static boolean isEmptyList(BaseBean tBaseBean) {
        return (tBaseBean.getData() instanceof List && ((List) tBaseBean.getData()).isEmpty() || tBaseBean.getData() instanceof NestList && ((NestList) tBaseBean.getData()).isNestListEmpty());
    }
}
