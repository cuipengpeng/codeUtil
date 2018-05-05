package com.test.bank.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.test.bank.http.HttpConfig;
import com.test.bank.inter.OnResponseListener;
import com.test.bank.receiver.ConnectionStatusChangeReceiver;
import com.test.bank.utils.LogUtils;
import com.test.bank.weight.errorview.ErrorBean;
import com.test.bank.weight.errorview.factory.OnErrorPageRefreshListener;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by 55 on 2017/11/3.
 */

public abstract class BaseFragment extends BaseLazyFragment implements IBaseView, ConnectionStatusChangeReceiver.OnNetworkReconnectedListener {
    private String mClassName;
    protected View rootView;
    private Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mClassName = getClass().getName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(getLayoutId(), container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        errorBean = getErrorBean();
        init();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {      //网络重新链接的回调
            ((BaseActivity) context).setOnNetworkReconnectedListener(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isCountPage() && !TextUtils.isEmpty(mClassName)) {
            MobclickAgent.onPageStart(mClassName);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isCountPage() && !TextUtils.isEmpty(mClassName)) {
            MobclickAgent.onPageEnd(mClassName);
        }
    }

    /**
     * 是否统计该Fragment
     *
     * @return
     */
    protected boolean isCountPage() {
        return false;
    }

    /**
     * 初始化
     * initBundle()
     * initWeight()
     * initListener()
     */
    protected abstract void init();


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != unbinder) {
            unbinder.unbind();
        }
    }

    protected abstract int getLayoutId();


    //重试网络请求集合
    public List<OnResponseListener> retryResponseListenerList = new ArrayList<>();

    /**
     * 请求网络的方法
     *
     * @param onResponseListener
     * @param <T>
     */
    public <T> void postRequest(OnResponseListener<T> onResponseListener) {
        postRequest(onResponseListener, true);
    }

    public <T> void postRequest(final OnResponseListener<T> onResponseListener, final boolean isShowProgressDialog) {
        postRequest(onResponseListener, isShowProgressDialog, true);
    }

    /**
     * 发起网络请求
     *
     * @param onResponseListener
     * @param isShowProgressDialog
     * @param isAddRetryList       是否添加到重试集合，默认false
     * @param <T>
     */
    public <T> void postRequest(final OnResponseListener<T> onResponseListener, final boolean isShowProgressDialog, boolean isAddRetryList) {
//        if (getErrorBean() != null && isAddRetryList) {
        if (isAddRetryList) {
            addToRefreshList(onResponseListener);
        }
        BaseBusiness.postRequest(onResponseListener, this, isShowProgressDialog);
    }

    private void addToRefreshList(OnResponseListener onResponseListener) {
        if (retryResponseListenerList == null) {
            return;
        }

        if (retryResponseListenerList.isEmpty()) {
            retryResponseListenerList.add(onResponseListener);
            return;
        }

        boolean isContains = false;
        for (int i = 0; i < retryResponseListenerList.size(); i++) {
            if (retryResponseListenerList.get(i).UUID.equals(onResponseListener.UUID)) {
                isContains = true;
            }
        }
        if (!isContains) {
            retryResponseListenerList.add(onResponseListener);
        }
    }

    @Override
    public void onRequestError(int errorCode, OnResponseListener onResponseListener) {
        if (null != errorBean) {
            errorBean.show(errorCode, generateErrorPageRefreshListener());
        }
        switch (errorCode) {
            case HttpConfig.EMPTY_DATA:
                addToRefreshList(onResponseListener);
                break;
            case HttpConfig.FORCE_UPDATE:
                onForceUpdate();
                break;
            case HttpConfig.TOKEN_INVALID:
                onTokenInvalid();
                break;
            case HttpConfig.CONNECT_EXCEPTION:  //无网络
            case HttpConfig.REQUEST_TIMEOUT:
                break;
            case HttpConfig.SERVER_EXCEPTION:
                break;
            case HttpConfig.UNKNOWN_ERROR:  //未知异常
                break;
        }
    }

    private OnErrorPageRefreshListener generateErrorPageRefreshListener() {
        return new OnErrorPageRefreshListener() {
            @Override
            public void onRefresh() {
                refreshRetryResponseListenerList();
            }
        };
    }

    /**
     * 刷新重试接口列表
     */
    private void refreshRetryResponseListenerList() {
        if (retryResponseListenerList == null) {
            return;
        }
        for (int i = 0; i < retryResponseListenerList.size(); i++) {
            postRequest(retryResponseListenerList.get(i), i == 0 ? true : false);   //点击异常页面的刷新按钮的回调
        }
    }

    @Override
    public void onRefreshSuccess(OnResponseListener onResponseListener) {
        if (retryResponseListenerList == null || retryResponseListenerList.isEmpty()) {
            return;
        }
        for (int i = 0; i < retryResponseListenerList.size(); i++) {
            if (onResponseListener.UUID.endsWith(retryResponseListenerList.get(i).UUID)) {
                retryResponseListenerList.remove(i);
            }
        }
    }


    @Override
    public void showProgressDialog() {
        ((BaseActivity) getActivity()).showProgressDialog();
    }

    @Override
    public void hideProgressDialog() {
        ((BaseActivity) getActivity()).hideProgressDialog();
    }

    @Override
    public void onForceUpdate() {
        if (getActivity() instanceof BaseActivity)
            ((BaseActivity) getActivity()).onForceUpdate();
    }

    @Override
    public void onTokenInvalid() {
        if (getActivity() instanceof BaseActivity)
            ((BaseActivity) getActivity()).onTokenInvalid();
    }


    @Override
    public void onNetworkReconnected() {
        LogUtils.e("onNetworkReconnected in BaseFragment.....");
        refreshRetryResponseListenerList();
    }

    protected ErrorBean errorBean;

    /**
     * 子类重写该方法返回错误布局
     *
     * @return
     */
    protected ErrorBean getErrorBean() {
        return null;
    }

    @Override
    public void showNoDataView() {
    }

    @Override
    public void showContentView() {
    }

    @Override
    public void showNetworkErrorView() {
    }
}
