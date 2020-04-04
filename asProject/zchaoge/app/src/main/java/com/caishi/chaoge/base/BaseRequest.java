package com.caishi.chaoge.base;

import com.caishi.chaoge.utils.NetConnectUtil;
import com.caishi.chaoge.utils.ToastUtils;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by 08124 on 2017/12/29.
 */

public class BaseRequest {
    private RxAppCompatActivity context;

    public BaseRequest(RxAppCompatActivity context) {
        this.context = context;
    }

    /**
     * 线程调度
     */
    public <T> ObservableTransformer<T, T> compose(final LifecycleTransformer<T> lifecycle) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(io.reactivex.Observable<T> observable) {
                return observable
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(Disposable disposable) throws Exception {
                                // 可添加网络连接判断等
                                if (!NetConnectUtil.isNetConnected(context)) {
                                    ToastUtils.showCentreToast(context, "请检查网络连接是否正常");
                                }
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(lifecycle);
            }
        };
    }


    public void errorType(RxAppCompatActivity context, int state) {
        String msg;
        if (state > 20000 && state < 40000) {
            msg = "服务器繁忙：" + state;
            ToastUtils.show(context, msg);
        }
//        switch (state) {
//            case -4:
//                msg = "请重新登陆";
//                ToastUtils.show(context, msg);
//                context.quitLogin();
//                return;
//            case -10:
//                msg = "在其他手机登录";
//                ToastUtils.show(context, msg);
//                context.quitLogin();
//                return;
//            case -101:
//                msg = "连接超时，请检查您的网络";
//                ToastUtils.show(context, msg);
//                context.clearLoginSP();
//                return;
////            case -500:
//                msg = "连接超时，请检查您的网络";
//                //  ToastUtils.show(context, msg);
//                context.clearLoginSP();
//                return;
//            default:
//                msg = "服务器繁忙：" + state;
//                ToastUtils.show(context, msg);
//                return;
//        }
    }

}
