package com.test.bank.http;

import android.app.Dialog;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.test.bank.base.BaseApplication;
import com.test.bank.base.IBaseView;
import com.test.bank.utils.LogUtils;

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


//    使用官方提供的JSONObject来解析JSON数据，所以不需要导入第三方jar包。如下：
//
//    json字符串转json对象/json数组对象
//    JSONObject jsonObject = new JSONObject(jsonData);
//    jsonObject.getJSONObject("result")
//    jsonObject.getJSONArray("result")
//
//    JSONArray jsonArray = new JSONArray(jsonData);
//    JSONObject jsonObject = jsonArray.getJSONObject(0);
//
//for (int i=0; i < jsonArray.length(); i++) {
//        JSONObject jsonObject = jsonArray.getJSONObject(i);
//        String id = jsonObject.getString("id");
//        String name = jsonObject.getString("name");
//}
//    google gson库：
//1、从实体类到JSON字符串
//    Gson gson = new Gson();
//    String jsonBDID = gson.toJson(user);
//2、从JSON字符串到实体类
//    Gson gson = new Gson();
//    User user = gson.fromJson(userString, User.class);
//3、从JSON数组到ArrayList
//    Gson gson = new Gson();
//    List userList = gson.fromJson(result,new TypeToken<ArrayList<User>>() {}.getType());


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
//            iBaseView.hideProgressDialog();
            if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }

            if (response.code() == 200 && "00".equals(responseJsonObject.getString("resCode"))) {
                showPageContentView();
                onResultFailLisenter.onResponse(call, response.success(responseJsonObject.getString("data")));
            } else if (3004 == responseJsonObject.getIntValue("resCode")) {
                //3004  token失效
                LogUtils.printLog(responseJsonObject.getString("resMsg"));
                iBaseView.onTokenInvalid();
            } else if (responseJsonObject.getIntValue("resCode") == 3014) {
                //显示统一消息
                Toast.makeText(BaseApplication.applicationContext, "服务异常，请重试~", Toast.LENGTH_SHORT).show();
            } else if (responseJsonObject.getBoolean("msgStatus") && iBaseView != null) {
                //显示服务器端返回的消息
                showPageContentView();
                LogUtils.printLog("AsyncResponse_onResponse--" + response.code() + "--" + response.message());
                Toast.makeText(BaseApplication.applicationContext, responseJsonObject.getString("resMsg"), Toast.LENGTH_SHORT).show();
            } else {
                LogUtils.printLog("AsyncResponse_unHandle--" + response.code() + "--" + response.message());
                if (iBaseView != null) {
                    iBaseView.showNoDataView();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.printLog("Exception--" + e.getMessage());
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
        LogUtils.printLog("AsyncResponse_onFailure--" + t.getMessage() + "--" + call.request().method());
        //网络请求失败重试
//        if(retryCount < 3){
//            call.clone().enqueue(this);
//            retryCount++;
//        }else {
            onResultFailLisenter.onFailure(call, t);
//        }
    }



}
