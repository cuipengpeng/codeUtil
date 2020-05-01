package com.jfbank.qualitymarket.helper;

import com.jfbank.qualitymarket.util.CommonUtils;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 功能：<br>
 * 作者：赵海<br>
 * 时间： 2017/8/9 0009<br>.
 * 版本：1.2.0
 */

public class CacheInterceptor  implements Interceptor
{
    @Override
    public Response intercept(Chain chain) throws IOException
    {
        Request request = chain.request();
        //如果没有网络，则启用 FORCE_CACHE
        if (!CommonUtils.isNetworkConnected())
        {
            request = request
                    .newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
        }
        Response originalResponse = chain.proceed(request);
        return originalResponse
                .newBuilder()
                //在这里生成新的响应并修改它的响应头
                .header("Cache-Control", "public,max-age=3600")
                .removeHeader("Pragma").build();
    }
}
