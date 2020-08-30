package com.framwork.http;


import com.framwork.http.retrofit.RetrofitUtil;
import com.test.xcamera.api.ApiService;
import com.test.xcamera.api.CallBack;
import com.test.xcamera.application.AppContext;
import com.test.xcamera.utils.NetworkUtil;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

/**
 * Author: mz
 * Time:  2019/10/8
 */
public class HttpFactory {
    public static ApiService CreatHttp() {
        ApiService service = RetrofitUtil.getInstance().build().create(ApiService.class);
        return service;
    }

    /**
     * 初始化observable信息
     */
    public static <T> void init(Observable<T> observable, final CallBack<T> callBack) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<T>() {
                               @Override
                               public void onSubscribe(Disposable d) {

                               }

                               @Override
                               public void onNext(T t) {
                                   callBack.onSuccess(t);
                               }

                               @Override
                               public void onError(Throwable e) {
                                   String errMsg = "";

                                   if (!NetworkUtil.isNetworkConnected(AppContext.getInstance())) {
                                       errMsg = "网络连接出错,";
                                   } else if (e instanceof HttpException) {
                                       errMsg = "网络请求出错,";
                                   } else if (e instanceof IOException) {
                                       errMsg = "网络出错,";
                                   } else {
                                       errMsg = e.getMessage();
                                   }
                                   callBack.onFailure(e);
                                   e.printStackTrace();

                                   //CameraToastUtil.show(errMsg, AppContext.getInstance());
                               }

                               @Override
                               public void onComplete() {
                                   callBack.onCompleted();
                               }
                           }
//                        new Subscriber<T>() {

//                    @Override
//                    public void onCompleted() {
//                        callBack.onCompleted();
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        callBack.onFailure(e);
//                        e.printStackTrace();
//                        CameraToastUtil.show(e.getMessage(), AppContext.getInstance());
//                    }
//
//                    @Override
//                    public void onNext(T t) {
//                        callBack.onSuccess(t);
//                    }
//                }
                );

    }
}
