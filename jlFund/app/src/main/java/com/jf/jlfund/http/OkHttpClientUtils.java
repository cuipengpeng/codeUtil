package com.jf.jlfund.http;

import android.text.TextUtils;

import com.jf.jlfund.utils.DateUtils;
import com.jf.jlfund.utils.FileUtils;
import com.jf.jlfund.utils.LogUtils;
import com.jf.jlfund.utils.PackageUtils;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Dispatcher;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by 55 on 2017/11/3.
 */

public class OkHttpClientUtils {

    private static final int CACHE_SIZE = 10 * 1024 * 1024;//缓存大小
    private static final int DEFAULT_CONNECT_TIMEOUT = 10000;   //请求超时时间    ConnectException
    private static final int DEFAULT_WRITE_TIMEOUT = 20000;     //请求成功，读取响应时间 -- SocketTimeoutException
    private static final int DEFAULT_READ_TIMEOUT = 20000;

    private static Dispatcher dispatcher;

    /**
     * 获取OkHttpClient.
     *
     * @return okHttpClient 【单例，所有的api都是通过该client发送请求】
     */
    public static OkHttpClient getOkHttpClient() {
        return getOkHttpClientBudiler(null, null).build();
    }

    /**
     * 获取下载进度进度
     *
     * @param progressListener
     * @return
     */
    public static OkHttpClient getOkHttpClient(final ProgressResponseBody.ProgressListener progressListener) {
        return getOkHttpClientBudiler(progressListener, null).build();
    }

    public static OkHttpClient getOkHttpClient(OnParsedServerDataFromHttpHeaderCallback onParsedServerDataFromHttpHeaderCallback) {
        return getOkHttpClientBudiler(null, onParsedServerDataFromHttpHeaderCallback).build();
    }

    /**
     * 构建builder
     *
     * @param progressListener 下载进度监听者
     * @return
     */
    private static OkHttpClient.Builder getOkHttpClientBudiler(ProgressResponseBody.ProgressListener progressListener, OnParsedServerDataFromHttpHeaderCallback onParsedServerDataFromHttpHeaderCallback) {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)//连接超时时间【毫秒】
                .addInterceptor(generateHttpLoggingInterceptor())
//                .addInterceptor(generateHeaderInterceptor())  //请求头不添加参数
                .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)//写入时间
                .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.MILLISECONDS)//读取时间
                .cache(new Cache(FileUtils.getHttpCacheDir(), CACHE_SIZE));
        if (null != progressListener) {
            builder.addInterceptor(generateProgressListenerInterceptor(progressListener));
        }
        if (null != onParsedServerDataFromHttpHeaderCallback) {
            builder.addInterceptor(generateFetchServerDataFromHttpHeader(onParsedServerDataFromHttpHeaderCallback));
        }
        dispatcher = builder.build().dispatcher();
        return builder;
    }

    private static Interceptor generateFetchServerDataFromHttpHeader(final OnParsedServerDataFromHttpHeaderCallback onParsedServerDataFromHttpHeaderCallback) {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());
                parseServerDateFromHttpHeader(response, onParsedServerDataFromHttpHeaderCallback);
                return response;
            }
        };
    }

    /**
     * 生成日志拦截器
     *
     * @return
     */
    private static HttpLoggingInterceptor generateHttpLoggingInterceptor() {
        //添加请求日志
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                //打印retrofit日志
                LogUtils.v("Retorfit=========================>" + message);
            }
        });
        //类型为BASIC，其实日志级别分为4类：NONE、BASIC、HEADERS、BODY
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);//日志级别
        return loggingInterceptor;
    }

    /**
     * 添加头部拦截器
     *
     * @return
     */
    private static Interceptor generateHeaderInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder()
                        .addHeader("vType", "1")
                        .addHeader("channelCode", "app")
                        .addHeader("mobileSystem", "android")
                        .addHeader("version", PackageUtils.getVersion())
                        .addHeader("equipment", PackageUtils.getVersion())
                        .build();
                return chain.proceed(request);
            }
        };
    }

    /**
     * 从响应头中解析服务器时间
     *
     * @param response
     * @param onParsedServerDataFromHttpHeaderCallback
     */
    private static void parseServerDateFromHttpHeader(Response response, OnParsedServerDataFromHttpHeaderCallback onParsedServerDataFromHttpHeaderCallback) {
        if (null == onParsedServerDataFromHttpHeaderCallback) {
            return;
        }
        Headers headers = response.headers();
        Date serverDate = headers.getDate("Date");
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String serverTime = DateUtils.getMonthFromDate(serverDate);
        LogUtils.e("zzzzzz", "parseServerDateFromHttpHeader >> " + serverTime);
        if (!TextUtils.isEmpty(serverTime)) {
            onParsedServerDataFromHttpHeaderCallback.onParsedServerDate(serverTime);
        } else {
            onParsedServerDataFromHttpHeaderCallback.onParsedFailed("The result parsed is empty.");
        }
    }

    /**
     * 添加下载进度拦截器
     *
     * @param progressListener
     * @return
     */
    private static Interceptor generateProgressListenerInterceptor(final ProgressResponseBody.ProgressListener progressListener) {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                okhttp3.Response orginalResponse = chain.proceed(chain.request());

                return orginalResponse.newBuilder()
                        .body(new ProgressResponseBody(orginalResponse.body(), progressListener)).build();
            }
        };
    }

    public static Dispatcher getDispatcher() {
        return dispatcher;
    }

    /**
     * 取消所有请求
     */
    public static void cancelAllRequest() {
        LogUtils.e("-----------beforeCancelAllRequest: " + dispatcher.queuedCallsCount() + dispatcher.runningCallsCount() + "-------------------");
        dispatcher.cancelAll();
        LogUtils.e("-----------afterCancelAllRequest: " + dispatcher.queuedCallsCount() + dispatcher.runningCallsCount() + "--------------------");
    }

    /**
     * 取消除url外所有的请求
     *
     * @param url ： 在BeeFinancingApi中定义的地址.   eg: 【"user/login.do"】
     */
    public static void cancelAllButCall(String url) {
        List<Call> runningCalls = getAllRunningCalls();
        if (runningCalls == null) {
            return;
        }
        for (int i = 0; i < runningCalls.size(); i++) {
            if (!runningCalls.get(i).request().url().toString().contains(url)) {
                runningCalls.get(i).cancel();
            }
        }
        List<Call> queuedCalls = getAllQueuedCalls();
        if (queuedCalls == null) {
            return;
        }
        for (int i = 0; i < queuedCalls.size(); i++) {
            if (!queuedCalls.get(i).request().url().toString().contains(url)) {
                queuedCalls.get(i).cancel();
            }
        }
    }

    /**
     * 获取所有等待的阻塞请求队列
     *
     * @return
     */
    public static List<Call> getAllQueuedCalls() {
        return dispatcher == null ? null : dispatcher.queuedCalls();
    }

    /**
     * 获取所有正在运行的请求列表
     *
     * @return
     */
    public static List<Call> getAllRunningCalls() {
        return dispatcher == null ? null : dispatcher.runningCalls();
    }

    /**
     * 获取等待请求的个数
     *
     * @return
     */
    public static int getQueuedCallsCount() {
        return dispatcher == null ? 0 : dispatcher.queuedCallsCount();
    }

    /**
     * 获取正在运行的请求的个数
     *
     * @return
     */
    public static int getRunningCallsCount() {
        return dispatcher == null ? 0 : dispatcher.runningCallsCount();
    }


    /**
     * 从响应头中解析出服务器日期的回调方法【之前是请求服务器的 getServerTime 接口】
     */
    public interface OnParsedServerDataFromHttpHeaderCallback {
        /**
         * 解析到服务器日期
         *
         * @param serverDate
         */
        void onParsedServerDate(String serverDate);

        /**
         * 解析失败
         *
         * @param errorMsg
         */
        void onParsedFailed(String errorMsg);
    }

}
