package com.test.bank.http;


import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;


public interface APIService {
    /**
     * * 纯文本接口
     *
     * @param url     请求URl
     * @param headers 请求header
     * @param params  请求参数
     * @return
     */
    @GET
    Call<String> getRequestAPiString(@Url String url, @HeaderMap Map<String, String> headers, @QueryMap Map<String, String> params);

    /**
     * * 纯文本接口
     *
     * @param url     请求URl
     * @param headers 请求header
     * @param params  请求参数
     * @return
     */
    @FormUrlEncoded
    @POST
    Call<String> postFormAPiString(@Url String url, @HeaderMap Map<String, String> headers, @FieldMap Map<String, String> params);

    /**
     * * 纯文本接口
     *
     * @param url     请求URl
     * @param headers 请求header
     * @param params  请求参数
     * @return
     */
    @POST
    Call<String> postJsonAPiString(@Url String url, @HeaderMap Map<String, String> headers, @Body RequestBody  params);


    /**
     * 上传图片接口
     *
     * @param url     请求URl
     * @param headers 请求header
     *                = * @param files  请求文件
     * @return
     */
    @Multipart
    @POST
    Call<String> uploadFileWithText(@Url String url,
                                    @HeaderMap Map<String, String> headers, @PartMap() Map<String, RequestBody> files);
    /**
     * 上传图片接口
     *
     * @param url     请求URl
     * @param params  请求参数
     *                = * @param files  请求文件
     * @return
     */
    @Multipart
    @POST
    Call<String> uploadMultiFiles(@Url String url,
                               @QueryMap() Map<String, String> params,
                               @Part List<MultipartBody.Part> files);
}
