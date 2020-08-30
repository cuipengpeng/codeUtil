package com.framwork.http.retrofit;

import com.framwork.http.okhttp.HttpClient;
import com.test.xcamera.utils.Constants;

import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * creat by mz  2019.9.24
 */
public final class RetrofitUtil {

    private static Converter.Factory gsonConverterFactory = GsonConverterFactory.create();
    private static CallAdapter.Factory rxJavaCallAdapterFactory = RxJava2CallAdapterFactory.create();
    private HttpClient httpClient;
    private volatile static Retrofit.Builder retrofit_builder;
    private volatile static RetrofitUtil retrofitUtil;

    private RetrofitUtil() {
        creatRetrofitBuilder();
    }

    public static RetrofitUtil getInstance() {
        if (retrofitUtil == null) {
            retrofitUtil = new RetrofitUtil();
        }

        return retrofitUtil;
    }

    public void setRetrofitNull() {
        retrofitUtil = null;
    }

    public Retrofit.Builder creatRetrofitBuilder() {
        if (retrofit_builder == null) {
            retrofit_builder = new Retrofit.Builder()
                    .client(HttpClient.getInstance().build()) //添加自己的client
                    .baseUrl(Constants.base_url)
                    .addCallAdapterFactory(rxJavaCallAdapterFactory)
                    .addConverterFactory(gsonConverterFactory);
        }
        return retrofit_builder;
    }

    public Retrofit build() {
        creatRetrofitBuilder();
        return retrofit_builder.build();
    }


}
