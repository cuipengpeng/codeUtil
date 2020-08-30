package com.framwork.http.okhttp;

import com.test.xcamera.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * creat by mz  2019.9.24
 */
public class HttpClient extends OkHttpClient {

    //single
    private volatile static HttpClient  client;
    // 请求超时时间
    private static final int DEFAULT_TIMEOUT = 10000;

    private  OkHttpClient.Builder builder ;

    private  HttpClient() {
        build();
        builder.readTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);  //全局的读取超时时间
        builder.writeTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);  //全局的写入超时时间
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);// 全局的连接超时时间
        if (BuildConfig.DEBUG) {
            // Log信息拦截器
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            //设置 Debug Log 模式
            builder.addInterceptor(loggingInterceptor);
          }
         builder.addInterceptor(new HeaderInterceptor());//增加请求头信息
    }

    public   static HttpClient getInstance(){
        if(client==null){
            client=new HttpClient();
        }
        return  client;
    }

    public   OkHttpClient.Builder CreatBuilder(){
        if(builder==null){
            builder=new OkHttpClient.Builder();
        }

        return  builder;
    }

    public OkHttpClient build(){
        if(builder==null){
            CreatBuilder();
        }
      return   builder.build();
    }

    /**
     * 重新初始化一下okhttp的信息
     */
    public   void  setClientNull(){
        client=null;
   }

}
