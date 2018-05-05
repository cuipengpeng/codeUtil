package com.test.bank.http;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Yan on 2017/2/23.
 */

public class NetService {

    private static Api commmonApi;  //通用api
    private static Api progressApi; //监听下载进度的api,，避免重复创建对象
    private static Api parseServerTimeApi;  //从响应头中解析服务器时间的api
    private static Api smartServiceApi;  //智能客服的api
    private static final String TAG = "NetService";

    /**
     * 普通
     * rxjava2
     *
     * @return
     */
    public static Api getNetService() {
        if (commmonApi == null) {
            commmonApi = new Retrofit.Builder().client(OkHttpClientUtils.getOkHttpClient())
                    .baseUrl(HttpConfig.BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build().create(Api.class);
        }
        return commmonApi;
    }

    /**
     * 从响应头中解析服务器时间的api
     * rxjava2
     *
     * @return
     */
    public static Api getNetService(OkHttpClientUtils.OnParsedServerDataFromHttpHeaderCallback callback) {
        if (parseServerTimeApi == null) {
            parseServerTimeApi = new Retrofit.Builder().client(OkHttpClientUtils.getOkHttpClient(callback))
                    .baseUrl(HttpConfig.BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build().create(Api.class);
        }
        return parseServerTimeApi;
    }

    /**
     * 下载专用的service【提供了监听下载进度的回调】
     *
     * @param progressListener 进度监听回调
     * @return
     */
    public static Api getNetService(final ProgressResponseBody.ProgressListener progressListener) {
        if (progressApi == null) {
            progressApi = new Retrofit.Builder().client(OkHttpClientUtils.getOkHttpClient(progressListener))
                    .baseUrl(HttpConfig.BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(Api.class);
        }
        return progressApi;
    }
}
