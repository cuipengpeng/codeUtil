package com.test.bank.base;

import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.Window;

import com.test.bank.http.HttpConfig;
import com.test.bank.inter.OnResponseListener;
import com.test.bank.receiver.ConnectionStatusChangeReceiver;
import com.test.bank.utils.LogUtils;
import com.test.bank.weight.dialog.CommonDialogFragment;
import com.test.bank.weight.errorview.ErrorBean;
import com.test.bank.weight.errorview.factory.OnErrorPageRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by 55 on 2017/11/3.
 */

public abstract class BaseActivity extends CommonActivity implements ConnectionStatusChangeReceiver.OnNetworkReconnectedListener {
    private static final String TAG = "BaseActivity";
    private Unbinder unbinder;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getLayoutId());
        unbinder = ButterKnife.bind(this);
        errorBean = getErrorBean();
        registerNetConnectionReceiver();    //注册网络监听广播
        init();
    }

    protected abstract void init();

    protected abstract int getLayoutId();

    /**
     * 显示一个button的dialog并且点击按钮只是隐藏dialog
     * 默认点击弹框外不消失
     *
     * @param content
     * @param btnText
     */
    public CommonDialogFragment showOneBtnDialog(String content, String btnText) {
        return showOneBtnDialog(content, btnText, null, false);
    }

    public CommonDialogFragment showOneBtnDialog(String content, String btnText, CommonDialogFragment.OnLeftClickListener onLeftClickListener) {
        return showOneBtnDialog(content, btnText, onLeftClickListener, false);
    }

    public CommonDialogFragment showOneBtnDialog(String content, String btnText, CommonDialogFragment.OnLeftClickListener onLeftClickListener, boolean isCanceledOnTouchOutside) {
        return showCommonDialog(content, btnText, "", onLeftClickListener, null, true, isCanceledOnTouchOutside);
    }

    public CommonDialogFragment showCommonDialog(String content, String leftTxt, String rightTxt, CommonDialogFragment.OnLeftClickListener onLeftClickListener, CommonDialogFragment.OnRightClickListener onRightClickListener) {
        return showCommonDialog(content, leftTxt, rightTxt, onLeftClickListener, onRightClickListener, false, false);
    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onForceUpdate() {
        //弹出强制更新框
    }

    @Override
    protected void onResume() {
        super.onResume();
        doBusiness();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * 处理业务逻辑。 eg:发起网络请求
     */
    protected abstract void doBusiness();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != unbinder) {
            unbinder.unbind();
        }
        unregisterNetConnectionReceiver();
    }


    /**
     * 网络请求
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

    //存放重试网络错误的请求
    public List<OnResponseListener> retryResponseListenerList = new ArrayList<>();

    /**
     * 发起网络请求
     *
     * @param onResponseListener
     * @param isShowProgressDialog
     * @param isAddRetryList       是否添加到重试集合，默认false
     * @param <T>
     */
    public <T> void postRequest(final OnResponseListener<T> onResponseListener, final boolean isShowProgressDialog, boolean isAddRetryList) {
        if (errorBean != null && isAddRetryList) {
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

    /**
     * 根据errorCode显示特定的错误布局
     *
     * @param errorCode
     */
    @Override
    public void onRequestError(int errorCode, OnResponseListener onResponseListener) {
        if (null != errorBean) {
            errorBean.show(errorCode, generateOnErrorPageRefreshListener());
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

    private OnErrorPageRefreshListener generateOnErrorPageRefreshListener() {
        return new OnErrorPageRefreshListener() {
            @Override
            public void onRefresh() {
                refreshRetryResponseListenerList();
            }
        };
    }

    /******************************  刷新重试集合  *****************************************/

    @Override
    public void onRefreshSuccess(OnResponseListener onResponseListener) {
//        this.retryResponseListenerList = retryList;
        if (retryResponseListenerList == null || retryResponseListenerList.isEmpty()) {
            return;
        }
        for (int i = 0; i < retryResponseListenerList.size(); i++) {
            if (onResponseListener.UUID.endsWith(retryResponseListenerList.get(i).UUID)) {
                retryResponseListenerList.remove(i);
            }
        }
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

    /******************************  网络状态监听  *****************************************/
    private ConnectionStatusChangeReceiver connectionStatusChangeReceiver;

    private void registerNetConnectionReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        connectionStatusChangeReceiver = new ConnectionStatusChangeReceiver();
        setOnNetworkReconnectedListener(this);
        registerReceiver(connectionStatusChangeReceiver, intentFilter);
    }

    private void unregisterNetConnectionReceiver() {
        unregisterReceiver(connectionStatusChangeReceiver);
    }

    /**
     * 抽出该方法是为了让BaseFragment复用
     *
     * @param listener
     */
    public void setOnNetworkReconnectedListener(ConnectionStatusChangeReceiver.OnNetworkReconnectedListener listener) {
        if (connectionStatusChangeReceiver != null) {
            connectionStatusChangeReceiver.setOnNetworkReconnectedListener(listener);
        }
    }

    @Override
    public void onNetworkReconnected() {
        LogUtils.e("onNetworkReconnected in BaseActivity.....");
        refreshRetryResponseListenerList();
    }

    /******************************  错误信息封装获取  *****************************************/

    protected ErrorBean errorBean;

    /**
     * 子类重写。返回该页面错误布局的UI【将要替换的view、错误提示文字、图片等】
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
