package com.jfbank.qualitymarket.net;

import android.util.Log;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Dispatcher;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * 功能：网络请求工具类<br>
 * 作者：赵海<br>
 * 时间： 2017/4/10 0010<br>.
 * 版本：1.2.0
 * <p>
 * 描述：
 * 1.通过getAPISerVice获取对象  创建APIService 只有当BaseUrl、readTime、writeTime,Timeout 不一样，则新建APIService（OkHttpClient）。<br>
 * 2.項目部分接口连接时间有限制要求，则需要特殊处理。<br>
 * 3.若连接请求需要自定义参数则，调用getApiService(String baseUrl, OkHttpClient.Builder builder)方法，并新建builder--.<br>
 * 4.filterEmptyParams(Map<String, String> params)参数为空的处理
 * 5.> toParamsFiles(Map<String, File> files, String type)文件转化
 */

public class NetUtil {
    private static Map<String, APIService> mapAPIService = new HashMap<>();

    /**
     * 获取APIService
     *
     * @param readTime  请求时间
     * @param writeTime 读取数据时间
     * @param timeOut   连接超时时间
     * @return
     */
    public static APIService getApiService(long readTime, long writeTime, long timeOut) {
        return getApiService(HttpRequest.QUALITY_MARKET_WEB_URL, readTime, writeTime, timeOut);
    }

    /**
     * 获取APIService
     *
     * @return
     */
    public static APIService getApiService() {
        return getApiService(HttpRequest.QUALITY_MARKET_WEB_URL, 3, 3, 3);
    }

    public static APIService getApiService1() {
        //设置日志拦截器
        OkHttpClient httpClient = new OkHttpClient.Builder().build();
        Retrofit retrofit = new Retrofit.Builder().client(httpClient)
                .baseUrl(HttpRequest.QUALITY_MARKET_WEB_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        APIService apiService = retrofit.create(APIService.class);
        return apiService;
    }

    /**
     * 获取APIService
     *
     * @param baseUrl 基类地址
     * @return
     */
    public static APIService getApiService(String baseUrl) {
        return getApiService(baseUrl, 3, 3, 3);
    }

    /**
     * * 获取APIService
     *
     * @param baseUrl   基类地址
     * @param readTime  请求时间
     * @param writeTime 读取数据时间
     * @param timeOut   连接超时时间
     * @return
     */
    public static APIService getApiService(String baseUrl, long readTime, long writeTime, long timeOut) {
        String apiServiceKey = baseUrl + readTime + writeTime + timeOut;
        APIService apiService = mapAPIService.get(apiServiceKey);
        if (apiService == null) {//为空，则创建
            synchronized (mapAPIService) { //锁定对象
                apiService = getApiService(baseUrl, newBuilder(readTime, writeTime, timeOut));//创建APIService
                APIService mApiService = mapAPIService.get(apiServiceKey);//再次获取mapAPIService中的APIService
                if (mApiService != null) {//添加对象前判断：若已经添加，则新建的APIService不需要加入mapAPIService【同时创建多个APIService,只添加一个，并且使用】
                    return mApiService;
                } else {//添加apiService
                    mapAPIService.put(apiServiceKey, apiService);
                    return apiService;
                }
            }
        } else {
            return apiService;
        }
    }

    /**
     * 获取APIService
     *
     * @param baseUrl 基类地址
     * @param builder 参数封装
     * @return
     */
    public static APIService getApiService(String baseUrl, OkHttpClient.Builder builder) {
        //设置日志拦截器
        OkHttpClient httpClient = builder.build();
        Retrofit retrofit = new Retrofit.Builder().client(httpClient)
                .baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        APIService apiService = retrofit.create(APIService.class);
        return apiService;
    }

    /**
     * 新建 OkHttpClient.Builder
     *
     * @param readTime  请求时间
     * @param writeTime 读取数据时间
     * @param timeOut   连接超时时间
     * @return
     */
    public static OkHttpClient.Builder newBuilder(long readTime, long writeTime, long timeOut) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(timeOut, TimeUnit.SECONDS).readTimeout(readTime, TimeUnit.SECONDS).writeTimeout(writeTime, TimeUnit.SECONDS);
        return builder;
    }


    /**
     * 多文件参数转化
     *
     * @param files 文件map
     * @param type  上传content-type格式
     * @return
     */
    public static Map<String, RequestBody> toParamsFiles(Map<String, File> files, String type) {
        Map<String, RequestBody> mapFiles = new HashMap<>();
        for (Map.Entry<String, File> entry : files.entrySet()) {
            RequestBody requestBody = RequestBody.create(MediaType.parse(type), entry.getValue());
            mapFiles.put(entry.getKey(), requestBody);
        }
        return mapFiles;
    }

    /**
     * 单文件上传转化
     * @param file 文件
     * @param name key值名稱
     * @param type 上传content-type格式
     * @return
     */
    public static MultipartBody.Part toParamsFile(File file, String name, String type) {
        Map<String, RequestBody> mapFiles = new HashMap<>();
        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse(type), file);

        MultipartBody.Part body =
                MultipartBody.Part.createFormData(name, file.getName(), requestFile);
        return body;
    }

    /**
     * retrofit参数为空处理转化。
     *
     * @param params
     * @return
     */
    public static Map<String, String> filterEmptyParams(Map<String, String> params) {
        for (String key : params.keySet()) {
            if (params.get(key) == null) {
                params.put(key, "");
            }
        }
        return params;
    }
}
