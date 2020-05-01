package com.jfbank.qualitymarket.callback;

import android.util.Log;

import com.jfbank.qualitymarket.util.ConstantsUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 功能：网络回调类<br>
 * 作者：赵海<br>
 * 时间： 2017/4/14 0014<br>.
 * 版本：1.2.0
 */

public abstract class AsyncResponseCallBack implements Callback<String> {
    OnResultCommonLisenter onResultFailLisenter;

    @Override
    public void onResponse(Call<String> call, Response<String> response) {
        try {
            if (response.code() == 200) {
                String resultString = response.body();
                onResult(resultString);
                if (this.onResultFailLisenter != null)
                    this.onResultFailLisenter.onAsyNetResult(call.request().url().encodedPath().substring(1), resultString);
                Log.e("AsyncResponse_Success", call.request().url().encodedPath() + ":" + resultString + "");
            } else {
                Log.e("AsyncResponse_onFailure", response.code() + response.message() + "");
                onFailed(call.request().url().encodedPath().substring(1), response.message());
            }
        } catch (Exception e) {
            Log.e("AsyncResponse_Exception", e.getMessage() + "");
            onFailed(call.request().url().encodedPath().substring(1), "数据格式错误,请稍后重试");
        }
        try {
            onFinish();
        } catch (Exception e) {
            Log.e("onFinish_Exception", e.getMessage() + "");
        }
    }

    public void onFailed(String path, String msg) {
        if (this.onResultFailLisenter != null)
            this.onResultFailLisenter.onAsyNetFailure(path, msg);
    }

    @Override
    public void onFailure(Call<String> call, Throwable t) {
        Log.e("AsyncResponse_onFailure", t.getMessage() + call.request().method() + "");
        try {
            onFailed(call.request().url().encodedPath().substring(1), ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER);
            onFinish();
        } catch (Exception e) {
            Log.e("onFinish_Exception", e.getMessage() + "");
        }
    }

    public interface OnResultCommonLisenter {
        void onAsyNetStart();

        void onAsyNetFailure(String url, String msg);

        void onAsyNetFinish();

        void onAsyNetCancel();

        void onAsyNetResult(String url, String responseStr);
    }

    public abstract void onResult(String responseStr);

    public AsyncResponseCallBack(OnResultCommonLisenter onResultFailLisenter) {
        onStart();
        this.onResultFailLisenter = onResultFailLisenter;
    }

    public AsyncResponseCallBack() {
        onStart();
    }

    public void onFinish() {
        if (this.onResultFailLisenter != null)
            this.onResultFailLisenter.onAsyNetFinish();
    }

    public void onStart() {
        if (this.onResultFailLisenter != null)
            this.onResultFailLisenter.onAsyNetStart();
    }
}
