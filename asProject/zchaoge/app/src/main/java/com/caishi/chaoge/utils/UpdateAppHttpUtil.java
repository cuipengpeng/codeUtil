package com.caishi.chaoge.utils;

import android.support.annotation.NonNull;

import com.caishi.chaoge.http.Account;
import com.caishi.chaoge.http.UserAgent;
import com.vector.update_app.HttpManager;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.DownloadProgressCallBack;
import com.zhouyou.http.exception.ApiException;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UpdateAppHttpUtil implements HttpManager {


    /**
     * 异步get
     *
     * @param url      get请求地址
     * @param params   get参数
     * @param callback 回调
     */
    @Override
    public void asyncGet(@NonNull String url, @NonNull Map<String, String> params, @NonNull final Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient();
//        okHttpClient.newBuilder().addInterceptor(new Interceptor() {
//            @Override
//            public Response intercept(Chain chain) throws IOException {
//                String host = chain.request().url().host();
//                String path = null;
//                if (TextUtils.equals(LOGIN_DOMAIN, host)) {
//                    path = chain.request().url().uri().getPath();
//                }
//                Request.Builder builder = chain.request().newBuilder();
//                builder.
//                return chain.proceed(builder.build());
//            }
//        }).build();

        final Request request = new Request.Builder()
                .url(url)
        .addHeader("User-Agent", UserAgent.formatAgent(""))
                .addHeader("userId", Account.sUserId)
                .addHeader("Source", "ChaoGe")
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onResponse(response.body().string());
            }
        });


    }

    /**
     * 异步post
     *
     * @param url      post请求地址
     * @param params   post请求参数
     * @param callBack 回调
     */
    @Override
    public void asyncPost(@NonNull String url, @NonNull Map<String, String> params, @NonNull final Callback callBack) {

    }

    /**
     * 下载
     *
     * @param url      下载地址
     * @param path     文件保存路径
     * @param fileName 文件名称
     * @param callback 回调
     */
    @Override
    public void download(@NonNull String url, @NonNull String path, @NonNull String fileName, @NonNull final FileCallback callback) {

        EasyHttp.downLoad(url)
                .savePath(path)
                .saveName(fileName)//不设置默认名字是时间戳生成的
                .execute(new DownloadProgressCallBack<String>() {
                    @Override
                    public void update(long bytesRead, long contentLength, boolean done) {
                        float progress =  ( (float)bytesRead  / contentLength);
                        callback.onProgress(progress, contentLength);
                    }

                    @Override
                    public void onStart() {
                        callback.onBefore();
                        //开始下载
                    }

                    @Override
                    public void onComplete(String path) {
                        callback.onResponse(new File(path));
                    }

                    @Override
                    public void onError(ApiException e) {
                        callback.onError(e.getMessage());
                        //下载失败
                    }
                });
    }


}