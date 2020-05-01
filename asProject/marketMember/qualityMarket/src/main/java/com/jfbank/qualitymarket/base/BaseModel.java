package com.jfbank.qualitymarket.base;

import android.content.Context;

import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.net.NetUtil;

import java.io.File;
import java.util.Map;

import retrofit2.Call;

/**
 * Model基类
 */
public class BaseModel {
    public Context mContext;
    Call<String> requestHandle;

    public void onStart(Context context) {
        this.mContext = context;
    }

    public void onDestory() {
        //TODO 取消所有网络请求
    }

    /**
     * 取消最新網絡請求
     */
    public void cancel() {
        if (requestHandle != null && !requestHandle.isCanceled()) {
            requestHandle.cancel();
        }
    }

    /**
     * 请求数据
     *
     * @param url      地址url
     * @param params   参数
     * @param callBack 回调
     * @return
     */
    public Call<String> requestNetData(String url, Map<String, String> params, AsyncResponseCallBack callBack) {
        requestHandle = HttpRequest.post(mContext, HttpRequest.QUALITY_MARKET_WEB_URL + url, params, callBack);
        return requestHandle;
    }

    /**
     * 上传文件(默认content-type:text/plain)
     *
     * @param url      地址url
     * @param params   参数
     * @param files    文件
     * @param callBack 回调
     * @return
     */
    public Call<String> uploadOneFileWithText(String url, Map<String, String> params, File files, String paramsKey, AsyncResponseCallBack callBack) {
        requestHandle = uploadOneFileWithText(url, params, files, paramsKey, "multipart/form-data", callBack);//multipart/form-data//application / json   "text/plain"
        return requestHandle;
    }

    /**
     * 上传文件
     *
     * @param url      地址url
     * @param params   参数
     * @param files    文件
     * @param type     content-type
     * @param callBack 回调
     * @return
     */
    public Call<String> uploadOneFileWithText(String url, Map<String, String> params, File files, String paramsKey, String type, AsyncResponseCallBack callBack) {
        requestHandle = HttpRequest.uploadOneFileWithText(mContext, HttpRequest.QUALITY_MARKET_WEB_URL + url, params, NetUtil.toParamsFile(files, paramsKey, type), callBack);
        return requestHandle;
    }
}
