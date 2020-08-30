package com.test.xcamera.dymode.utils;

import android.accounts.NetworkErrorException;
import android.util.Log;

import com.google.gson.Gson;
import com.ss.android.ugc.effectmanager.common.EffectConstants;
import com.ss.android.ugc.effectmanager.common.EffectRequest;
import com.ss.android.ugc.effectmanager.common.listener.IEffectNetWorker;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ImplNetWorker implements IEffectNetWorker {
    private OkHttpClient.Builder mDownloadBuilder;

    @Override
    public InputStream execute(EffectRequest request) {
        try {
            return request(request);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private InputStream request(EffectRequest effectRequest) throws Exception {
        Request.Builder builder = new Request.Builder()
                .url(effectRequest.getUrl());
        if (!effectRequest.getHeaders().isEmpty()) {
            builder = builder.headers(setHeaders(effectRequest.getHeaders()));
        }
        if (effectRequest.getHttpMethod().equals(EffectConstants.GET)) {
            builder = builder.get();
        } else {
            if (!effectRequest.getParams().isEmpty()) {
                builder = builder.method(effectRequest.getHttpMethod(), setRequestBody(effectRequest.getContentType(), effectRequest.getParams()));
            }
        }


        Request request = builder.build();
        if (mDownloadBuilder == null) {
            mDownloadBuilder = new OkHttpClient.Builder().cookieJar(new CookieJar() {//这里可以做cookie传递，保存等操作
                @Override
                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {//可以做保存cookies操作
                    System.out.println("cookies url: " + url.toString());
                    for (Cookie cookie : cookies) {
                        System.out.println("cookies: " + cookie.toString());
                    }
                }

                @Override
                public List<Cookie> loadForRequest(HttpUrl url) {//加载新的cookies
                    ArrayList<Cookie> cookies = new ArrayList<>();
                    Cookie cookie = new Cookie.Builder()
                            .hostOnlyDomain(url.host())
                            .name("SESSION").value("fuck")
                            .build();
                    cookies.add(cookie);
                    return cookies;
                }
            });
        }
        Call call = mDownloadBuilder.build().newCall(request);
        final Response res = call.execute();
        if (res.code() == 200 && res.body() != null) {
            effectRequest.setContentLength(res.body().contentLength());
            return new FilterInputStream(res.body().byteStream()) {
                @Override
                public void close() throws IOException {
                    super.close();
                    res.close();
                }
            };
        }
        throw new NetworkErrorException("http response code:" + res.code());
    }


    /**
     * post的请求参数，构造RequestBody
     */
    private RequestBody setRequestBody(String contentType, Map<String, Object> bodyParams) {
        String json = new Gson().toJson(bodyParams);
        return RequestBody.create(MediaType.parse(contentType), json);

    }

    private Headers setHeaders(Map<String, String> HeaderParams) {
        Headers header = null;
        Headers.Builder formEncodingBuilder = new Headers.Builder();
        if (HeaderParams != null) {
            Iterator<String> iterator = HeaderParams.keySet().iterator();
            String key = "";
            while (iterator.hasNext()) {
                key = iterator.next();
                formEncodingBuilder.add(key, HeaderParams.get(key));
                Log.d("http", " header ===" + key + " ==== " + HeaderParams.get(key));
            }
        }
        header = formEncodingBuilder.build();
        return header;
    }

}
