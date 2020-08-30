package com.test.xcamera.picasso;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by admin on 2018/6/12.
 */

public class MInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        okhttp3.Request request = chain.request();
        //如果没有网络，则启用 FORCE_CACHE
//        if (!NetUtils.isNetworkConnected(AppContext.getInstance())) {
        request = request
                .newBuilder()
                .cacheControl(CacheControl.FORCE_CACHE)
                .build();
//        }
        Response originalResponse = chain.proceed(request);
        return originalResponse
                .newBuilder()
                //在这里生成新的响应并修改它的响应头
                .header("Cache-Control", "public,max-age=3600")
                .removeHeader("Pragma").build();
    }
}