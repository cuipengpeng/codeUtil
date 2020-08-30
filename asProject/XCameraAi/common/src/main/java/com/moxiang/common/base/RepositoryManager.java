package com.moxiang.common.base;

import android.support.annotation.NonNull;

import com.moxiang.common.http.RetrofitClient;

import java.lang.reflect.Proxy;

import retrofit2.Retrofit;

/**
 * Created by admin on 2019/10/25.
 */

public class RepositoryManager implements IRepositoryManager {
    RetrofitClient mRetrofitClient;
    public RepositoryManager() {
        mRetrofitClient = RetrofitClient.getInstance();

    }

    @NonNull
    @Override
    public <T> T obtainRetrofitService(@NonNull Class<T> service) {
        T retrofitService = mRetrofitClient.create(service);
        return retrofitService;
    }
}
