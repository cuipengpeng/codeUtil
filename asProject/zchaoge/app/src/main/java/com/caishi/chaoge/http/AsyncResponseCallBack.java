package com.caishi.chaoge.http;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.caishi.chaoge.base.BaseApplication;
import com.caishi.chaoge.base.IBaseView;
import com.caishi.chaoge.ui.activity.LoginActivity;
import com.caishi.chaoge.utils.ConstantUtils;
import com.caishi.chaoge.utils.LogUtil;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AsyncResponseCallBack implements Callback<String> {
    private HttpRequest.HttpResponseCallBank onResultFailLisenter = null;
    private IBaseView iBaseView = null;
    private String requestChannel = null;
    private boolean showLoading = true;
    private Dialog mLoadingDialog;

    public AsyncResponseCallBack(HttpRequest.HttpResponseCallBank onResultFailLisenter) {
        this.onResultFailLisenter = onResultFailLisenter;
    }

    public AsyncResponseCallBack(HttpRequest.HttpResponseCallBank onResultFailLisenter, IBaseView context, String requestChannel, boolean showLoading, Dialog loadingDialog) {
        this.onResultFailLisenter = onResultFailLisenter;
        this.iBaseView = context;
        this.requestChannel = requestChannel;
        this.showLoading = showLoading;
        this.mLoadingDialog = loadingDialog;
    }

    @Override
    public void onResponse(Call<String> call, Response<String> response) {
        try {
            String resultString = response.body();
            LogUtil.printLog("HTTP responseCode = " + response.code() + "--" + call.request().url().encodedPath() + "--响应数据：" + resultString);
            JSONObject responseJsonObject = new JSONObject(resultString);
//            iBaseView.hideProgressDialog();
            hideLoadingDialog();

            if(call.request().url().encodedPath().endsWith(RequestURL.LOGIN)
               || call.request().url().encodedPath().endsWith(RequestURL.BOUND_MOBILD)){
                onResultFailLisenter.onSuccess(LoginActivity.KEY_OF_ENABLE_LOGIN_BUTTON);
            }

            if (response.code() == 200 && responseJsonObject.getInt("code") == 10000) {
                showPageContentView();
                if(call.request().url().encodedPath().endsWith(RequestURL.FOLLOW)){
                    onResultFailLisenter.onSuccess(resultString);
                }else {
                    onResultFailLisenter.onSuccess(responseJsonObject.getString("data"));
                }
            } else if (responseJsonObject.getInt("code") == 20008 ) {
                LoginActivity.open(BaseApplication.getContext(), -1);
                Toast.makeText(BaseApplication.getContext(), "请重新登陆", Toast.LENGTH_SHORT).show();
            } else if (responseJsonObject.getInt("code") > 20000 && responseJsonObject.getInt("code") < 40000) {
                LogUtil.printLog(responseJsonObject.getString("message"));
                Toast.makeText(BaseApplication.getContext(), "服务器繁忙：" + responseJsonObject.getInt("code"), Toast.LENGTH_SHORT).show();
//                iBaseView.onTokenInvalid();
            } else if (responseJsonObject.getInt("code") >= 40000) {
                Toast.makeText(BaseApplication.getContext(), responseJsonObject.getString("message"), Toast.LENGTH_SHORT).show();
            } else {
                LogUtil.printLog("AsyncResponse_unHandle--" + response.code() + "--" + response.message());
                if (iBaseView != null) {
                    iBaseView.showNoDataView();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.printLog("AsyncResponse_Exception--" + e.getMessage());
        }
    }

    private void showPageContentView() {
        if (iBaseView != null) {
//            if (iBaseView instanceof BaseUIFragment && !(iBaseView instanceof MyaccountFragment) && !(iBaseView instanceof OptionalFragment)) {
//                ((BaseUIActivity) ((BaseUIFragment) iBaseView).getActivity()).showContentView();
//                ((BaseUIFragment) iBaseView).showContentView();
//            }
            iBaseView.showContentView();
        }
    }

    @Override
    public void onFailure(Call<String> call, Throwable t) {
        LogUtil.printLog("AsyncResponse_onFailure--" + t.getMessage() + "--" + call.request().url().encodedPath());
        hideLoadingDialog();
        if (null!=onResultFailLisenter)
        onResultFailLisenter.onFailure(t.getMessage());
    }

    private void hideLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }
}
