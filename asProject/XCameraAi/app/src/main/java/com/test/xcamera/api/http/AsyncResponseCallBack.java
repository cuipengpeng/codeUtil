package com.test.xcamera.api.http;

import android.app.Dialog;

import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.base.IBaseView;
import com.test.xcamera.utils.LoggerUtils;
import com.test.xcamera.utils.SPUtils;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AsyncResponseCallBack implements Callback<String> {
    private HttpRequest.HttpResponseCallBack onResultFailLisenter = null;
    private IBaseView iBaseView = null;
    private String requestChannel = null;
    private boolean showLoading = true;
    private Dialog mLoadingDialog;
    private int retryCount =0;

    public AsyncResponseCallBack(HttpRequest.HttpResponseCallBack onResultFailLisenter) {
        this.onResultFailLisenter = onResultFailLisenter;
    }

    public AsyncResponseCallBack(HttpRequest.HttpResponseCallBack onResultFailLisenter, IBaseView context, String requestChannel, boolean showLoading, Dialog loadingDialog) {
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
            LoggerUtils.printLog("HTTP responseCode = " + response.code() + "--" + call.request().url().encodedPath() + "--响应数据：" + resultString);

            JSONObject responseJsonObject = new JSONObject(resultString);
//            iBaseView.hideProgressDialog();
            if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }

            if (response.code() == 200){

                if(responseJsonObject.getInt("code")==0) {
                    showPageContentView();
                    onResultFailLisenter.onSuccess(responseJsonObject.getString("data"));
                } else if (20002 == responseJsonObject.getInt("code")) {
                    //3004  token失效
                    SPUtils.unLogin(AiCameraApplication.getContext());
                    LoggerUtils.printLog(responseJsonObject.getString("message"));
                    onResultFailLisenter.onFailure("token失效");
                    iBaseView.onTokenInvalid();
                } else {
                    LoggerUtils.printLog("AsyncResponse_unHandle--" + responseJsonObject.getInt("code") + "--" + response.message());
                    onResultFailLisenter.onFailure("响应码异常，请稍后再试"+responseJsonObject.getInt("code")+" resMessage"+response.message());
                    if (iBaseView != null) {
                        iBaseView.showNoDataView();
                    }
                }
            }
        } catch (Exception e) {
            onResultFailLisenter.onFailure("解析异常，请稍后再试");
            e.printStackTrace();
            LoggerUtils.printLog("Exception--" + e.getMessage());
        }
    }

    private void showPageContentView() {
        if (iBaseView != null) {
//            if (iBaseView instanceof BaseUIFragment) {
//                ((BaseUIActivity) ((BaseUIFragment) iBaseView).getActivity()).showContentView();
//                ((BaseUIFragment) iBaseView).showContentView();
//            }
            iBaseView.showContentView();
        }
    }

    @Override
    public void onFailure(Call<String> call, Throwable t) {
        LoggerUtils.printLog("AsyncResponse_onFailure--" + t.getMessage() + "--" + call.request().method());
        //网络请求失败重试
//        if(retryCount < 3){
//            call.clone().enqueue(this);
//            retryCount++;
//        }else {
            onResultFailLisenter.onFailure(t.getMessage());
//        }
    }
}
