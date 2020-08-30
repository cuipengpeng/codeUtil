package com.framwork.http.okhttp;

import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.utils.LoggerUtils;
import com.test.xcamera.utils.SPUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Author: mz
 * Time:  2019/9/25
 */
public class HeaderInterceptor implements Interceptor {
    private String Tag = "Header";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder newBuilder = chain.request().newBuilder();
        String token = SPUtils.get(AiCameraApplication.mApplication, "token", "").toString();
        LoggerUtils.d(Tag, "获取的token 信息是。。" + token);
        newBuilder.addHeader("X-token", token);
        String decvideId = SPUtils.getUuid(AiCameraApplication.mApplication);
        LoggerUtils.d(Tag, "获取的decvideId 信息是。。" + token);
        newBuilder.addHeader("X-device-id", decvideId);
        newBuilder.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        return chain.proceed(newBuilder.build());
    }
}
