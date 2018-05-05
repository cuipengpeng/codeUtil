package com.test.bank.http;

import android.app.Dialog;
import android.content.Context;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.test.bank.base.BaseApplication;
import com.test.bank.base.BaseUIActivity;
import com.test.bank.base.BaseUIFragment;
import com.test.bank.base.IBaseView;
import com.test.bank.utils.ConstantsUtil;
import com.test.bank.utils.LogUtils;
import com.test.bank.view.activity.GetOutActivity;
import com.test.bank.view.activity.PutInActivity;
import com.test.bank.view.fragment.MyaccountFragment;
import com.test.bank.view.fragment.OptionalFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AsyncResponseCallBack implements Callback<String> {
    private HttpRequest.HttpResponseCallBank onResultFailLisenter = null;
    private IBaseView iBaseView = null;
    private String requestChannel = null;
    private boolean showLoading = true;
    private Dialog mLoadingDialog;
    Context context = BaseApplication.applicationContext;

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
            LogUtils.printLog("HTTP responseCode = " + response.code() + "--" + call.request().url().encodedPath() + "--响应数据：" + resultString);
//            统一返回值json格式为{"resCode":"","resMsg":"","data":{}}
//            resCode:返回码  0000成功,  21000参数不正确, 50000系统错误
//            resMsg:消息
//            data:数据
//            msgStatus: : 是否和用户展示提示语   true 需要向用户展示resMsg   false 不需要

            JSONObject responseJsonObject = JSON.parseObject(resultString);

            //TODO just for test
//            responseJsonObject.put("msgStatus",  true);
//            responseJsonObject.put("resMsg",  "11111111111111");
//            responseJsonObject.put("resCode",  "0");

            iBaseView.hideProgressDialog();
//            if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
//                mLoadingDialog.dismiss();
//            }

            if (response.code() == 200 && "0000".equals(responseJsonObject.getString("resCode"))) {
                showPageContentView();
                onResultFailLisenter.onResponse(call, response.success(responseJsonObject.getString(ConstantsUtil.Response.KEY_OF_RESPONSE_DATA)));
            } else if (ConstantsUtil.Response.RESPONSE_CODE_TOKEN_FAIL_3004 == responseJsonObject.getIntValue("resCode")) {
                //3004  token失效
                LogUtils.printLog(responseJsonObject.getString("resMsg"));

                iBaseView.onTokenInvalid();

                if (iBaseView != null && iBaseView instanceof MyaccountFragment) {
                    ((MyaccountFragment) iBaseView).showUserLoginOrNologinView(false);
                }
            } else if ((call.request().url().encodedPath().contains(HttpRequest.CURRENT_PLUS_FUND_BUY) || call.request().url().encodedPath().contains(HttpRequest.CURRENT_PLUS_FUND_REDEMPTION)
                    && responseJsonObject.getIntValue("resCode") != ConstantsUtil.Response.RESPONSE_CODE_TRADE_PASSWORD_ERROR_3011
                    && responseJsonObject.getIntValue("resCode") != ConstantsUtil.Response.RESPONSE_CODE_TRADE_PASSWORD_ERROR_3013
                    && responseJsonObject.getIntValue("resCode") != ConstantsUtil.Response.RESPONSE_CODE_TRADE_PASSWORD_ERROR_3014)) {
                Toast.makeText(BaseApplication.getContext(), "服务异常，请重试~", Toast.LENGTH_SHORT).show();
            } else if (responseJsonObject.getBoolean("msgStatus") && iBaseView != null) {
                //显示服务器端返回的消息
                showPageContentView();
                LogUtils.printLog("AsyncResponse_onResponse--" + response.code() + "--" + response.message());


                if ("com.jf.jlfund.view.activity.PutInActivity".equals(requestChannel)) {
                    PutInActivity.popupDialog(((PutInActivity) iBaseView), responseJsonObject.getString("resMsg"), Integer.valueOf(responseJsonObject.getString("resCode")), ((PutInActivity) iBaseView).payPasswordView, true);
                    return;
                } else if ("com.jf.jlfund.view.activity.GetOutActivity".equals(requestChannel)) {
                    PutInActivity.popupDialog(((BaseUIActivity) iBaseView), responseJsonObject.getString("resMsg"), Integer.valueOf(responseJsonObject.getString("resCode")), ((GetOutActivity) iBaseView).payPasswordView, true);
                    return;
                }

                Toast.makeText(BaseApplication.applicationContext, responseJsonObject.getString("resMsg"), Toast.LENGTH_SHORT).show();
            } else {
                LogUtils.printLog("AsyncResponse_unHandle--" + response.code() + "--" + response.message());
                if (iBaseView != null) {
                    iBaseView.showNoDataView();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.printLog("AsyncResponse_Exception--" + e.getMessage());
        }

        showMyaccounFragmentContentView();
    }

    private void showPageContentView() {
        if (iBaseView != null) {
            if (iBaseView instanceof BaseUIFragment && !(iBaseView instanceof MyaccountFragment) && !(iBaseView instanceof OptionalFragment)) {
                ((BaseUIActivity) ((BaseUIFragment) iBaseView).getActivity()).showContentView();
                ((BaseUIFragment) iBaseView).showContentView();
            }
            iBaseView.showContentView();
        }
    }

    private void showMyaccounFragmentContentView() {
        //myaccountfragment页面始终显示，不管请求成功还是失败。
        if (iBaseView != null && (iBaseView instanceof MyaccountFragment || iBaseView instanceof OptionalFragment)) {
            iBaseView.showContentView();
        }
    }

    @Override
    public void onFailure(Call<String> call, Throwable t) {
        LogUtils.printLog("AsyncResponse_onFailure--" + t.getMessage() + "--" + call.request().method());
        onResultFailLisenter.onFailure(call, t);
        showMyaccounFragmentContentView();
    }
}
