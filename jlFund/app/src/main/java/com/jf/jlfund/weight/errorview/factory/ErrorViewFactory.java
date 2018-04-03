package com.jf.jlfund.weight.errorview.factory;

import android.content.Context;
import android.view.View;

import com.jf.jlfund.http.HttpConfig;
import com.jf.jlfund.weight.errorview.ErrorBean;

/**
 * Created by 55 on 2017/10/10.
 */

public class ErrorViewFactory {
    /**
     * 获取ErrorView
     *
     * @param context
     * @param httpCode        根据不同状态码获取不同的ErrorView。
     * @param errorPageBean   封装有该错误页面的文案提示，可以针对不同页面显示不同的文案。
     * @param refreshListener 点击刷新的回调
     * @return
     */
    public static View productErrorView(Context context, int httpCode, ErrorBean errorPageBean, OnErrorPageRefreshListener refreshListener) {
        ErrorView errorView = null;
        switch (httpCode) {
            case HttpConfig.REQUEST_TIMEOUT:
            case HttpConfig.CONNECT_EXCEPTION:
                errorView = new NoConnectedErrorView(context, errorPageBean, refreshListener);
                break;
            case HttpConfig.EMPTY_DATA:
                errorView = new NoConnectedErrorView(context, errorPageBean, refreshListener);
                break;
            case HttpConfig.SERVER_EXCEPTION:
                errorView = new NoConnectedErrorView(context, errorPageBean, refreshListener);
                break;
            default:
                errorView = new NoConnectedErrorView(context, errorPageBean, refreshListener);
                break;
        }
        return errorView.productErrorView();
    }
}