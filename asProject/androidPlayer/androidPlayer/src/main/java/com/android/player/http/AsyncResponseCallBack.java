package com.android.player.http;

import android.app.Dialog;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.player.base.BaseApplication;
import com.android.player.base.BaseUIActivity;
import com.android.player.base.BaseUIFragment;
import com.android.player.base.IBaseView;
import com.android.player.fragment.UserInfoFragment;
import com.android.player.utils.encrypt.LogUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AsyncResponseCallBack implements Callback<String> {
    private HttpRequest.HttpResponseCallBank onResultFailLisenter = null;
    private IBaseView iBaseView = null;
    private String requestChannel = null;
    private Dialog mLoadingDialog;

    public AsyncResponseCallBack(HttpRequest.HttpResponseCallBank onResultFailLisenter) {
        this.onResultFailLisenter = onResultFailLisenter;
    }

    public AsyncResponseCallBack(HttpRequest.HttpResponseCallBank onResultFailLisenter, IBaseView context, String requestChannel, Dialog loadingDialog) {
        this.onResultFailLisenter = onResultFailLisenter;
        this.iBaseView = context;
        this.requestChannel = requestChannel;
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

            if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }

            if (response.code() == 200 && "0000".equals(responseJsonObject.getString("resCode"))) {
                showPageContentView();
                onResultFailLisenter.onResponse(call, response.success(responseJsonObject.getString("")));
            } else if (3004 == responseJsonObject.getIntValue("resCode")) {
                //3004  token失效
                LogUtils.printLog(responseJsonObject.getString("resMsg"));

                iBaseView.onTokenInvalid();

                if (iBaseView != null && iBaseView instanceof UserInfoFragment) {

                }
            } else if ((call.request().url().encodedPath().contains(HttpRequest.CURRENT_PLUS)
                    && responseJsonObject.getIntValue("resCode") != 3011
                    && responseJsonObject.getIntValue("resCode") != 3013
                    && responseJsonObject.getIntValue("resCode") != 3014)) {
                Toast.makeText(BaseApplication.applicationContext, "服务异常，请重试~", Toast.LENGTH_SHORT).show();
            } else if (responseJsonObject.getBoolean("msgStatus") && iBaseView != null) {
                //显示服务器端返回的消息
                showPageContentView();
                LogUtils.printLog("AsyncResponse_onResponse--" + response.code() + "--" + response.message());


                if ("com.jf.jlfund.view.activity.PutInActivity".equals(requestChannel)) {
                    return;
                } else if ("com.jf.jlfund.view.activity.GetOutActivity".equals(requestChannel)) {
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
            if (iBaseView instanceof BaseUIFragment && !(iBaseView instanceof UserInfoFragment)) {
                ((BaseUIActivity) ((BaseUIFragment) iBaseView).getActivity()).showContentView();
                ((BaseUIFragment) iBaseView).showContentView();
            }
            iBaseView.showContentView();
        }
    }

    private void showMyaccounFragmentContentView() {
        //myaccountfragment页面始终显示，不管请求成功还是失败。
        if (iBaseView != null && (iBaseView instanceof UserInfoFragment)) {
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
