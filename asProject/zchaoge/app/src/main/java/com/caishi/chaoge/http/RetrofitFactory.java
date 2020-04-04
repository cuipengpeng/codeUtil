package com.caishi.chaoge.http;

import android.text.TextUtils;

import com.caishi.chaoge.utils.LogUtil;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.caishi.chaoge.utils.ConstantUtils.LOGIN_DOMAIN;
import static com.caishi.chaoge.utils.ConstantUtils.TIMEOUT;

/**
 * RetrofitFactory
 */
public class RetrofitFactory {
    // Retrofit是基于OkHttpClient的，可以创建一个OkHttpClient进行一些配置
    private static OkHttpClient httpClient = new OkHttpClient.Builder()
            // 添加通用的Header
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    String host = chain.request().url().host();
                    String path = null;
                    if (TextUtils.equals(LOGIN_DOMAIN, host)) {
                        path = chain.request().url().uri().getPath();
                    }
                    Request.Builder builder = chain.request().newBuilder();
                    builder.addHeader("User-Agent", UserAgent.formatAgent(path))
                            .addHeader("userId", Account.sUserId)
                            .addHeader("Source", "ChaoGe");
                    LogUtil.d(" Account.sUserId====" + Account.sUserId);
                    return chain.proceed(builder.build());
                }
            })
            //添加应用拦截器
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    //获取原始的originalRequest
                    Request originalRequest = chain.request();
                    //获取老的url
                    HttpUrl oldUrl = originalRequest.url();
                    //获取originalRequest的创建者builder
                    Request.Builder builder = originalRequest.newBuilder();
                    //获取头信息的集合如：manage,mdffx
                    List<String> urlNameList = originalRequest.headers("version");
                    if (urlNameList != null && urlNameList.size() > 0) {
                        //删除原有配置中的值,就是namesAndValues集合里的值
                        builder.removeHeader("version");
                        //获取头信息中配置的value,如：manage或者mdffx
                        String urlName = urlNameList.get(0);
                        HttpUrl baseURL = null;
                        //根据头信息中配置的value,来匹配新的base_url地址
                        if ("v1".equals(urlName)) {
                            baseURL = HttpUrl.parse(RequestURL.BASE_URL);
                        } else if ("v2".equals(urlName)) {
                            baseURL = HttpUrl.parse(RequestURL.BASE_URL_V2);
                        } else if ("download".equals(urlName)) {
                            baseURL = HttpUrl.parse(RequestURL.VIDEO_BASE_URL);
                        }
                        //重建新的HttpUrl，需要重新设置的url部分
                        HttpUrl.Builder builder1 = oldUrl.newBuilder()
                                .scheme(baseURL.scheme())//http协议如：http或者https
                                .host(baseURL.host())//主机地址
                                .port(baseURL.port());//端口
                        if (!"download".equals(urlName)) {
                            builder1.setPathSegment(0, urlName);
                        }
                        HttpUrl newHttpUrl = builder1.build();
                        //获取处理后的新newRequest
                        Request newRequest = builder.url(newHttpUrl).build();
                        return chain.proceed(newRequest);
                    } else {
                        return chain.proceed(originalRequest);
                    }

                }
            })
            /**
             *这里可以添加一个HttpLoggingInterceptor，因为Retrofit封装好了从Http请求到解析，
             *出了bug很难找出来问题，添加HttpLoggingInterceptor拦截器方便调试接口
             */
//            .addInterceptor(new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
//                @Override
//                public void log(String message) {
//                    Log.d("LogInterceptor", message);
//                }
//            }).setLevel(HttpLoggingInterceptor.Level.BASIC))

            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(new LogInterceptor())
            .build();

    private static RequestApi requestApi = new Retrofit.Builder()
            .baseUrl(RequestURL.BASE_URL + "/")
            // 添加Gson转换器
            .addConverterFactory(GsonConverterFactory.create())
            // 添加Retrofit到RxJava的转换器
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(httpClient)
            .build()
            .create(RequestApi.class);

    public static RequestApi getInstance() {
        return requestApi;
    }


}
