package com.jfbank.qualitymarket.net;


import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * 功能：retrofit接口API<br>
 * 作者：赵海<br>
 * 时间： 2017/4/10 0010<br>.
 * 版本：1.2.0
 */

public interface APIService {
    /**
     * 纯文本接口
     *
     * @param url    请求URl
     * @param params 请求参数
     * @return
     */
    @POST
    Call<String> getAPiString(@Url String url, @QueryMap Map<String, String> params);

    /**
     * 、
     * * 纯文本接口
     *
     * @param url     请求URl
     * @param headers 请求header
     * @param params  请求参数
     * @return
     */
    @POST
    Call<String> getAPiString(@Url String url, @HeaderMap Map<String, String> headers, @QueryMap Map<String, String> params);

    /**
     * 上传图片接口
     *
     * @param url    请求URl
     * @param params 请求参数
     * @param files  请求文件
     * @return
     */
    @Multipart
    @POST
    Call<String> uploadFileWithText(@Url String url,
                                    @QueryMap() Map<String, String> params,
                                    @PartMap() Map<String, RequestBody> files);

    @Multipart
    @POST
    Call<String> uploadOneFileWithText(@Url String url,
                                       @QueryMap() Map<String, String> params,
                                       @Part MultipartBody.Part files);

    /**
     * 上传图片接口
     *
     * @param url     请求URl
     * @param headers 请求header
     * @param params  请求参数
     *                = * @param files  请求文件
     * @return
     */
    @Multipart
    @POST
    Call<String> uploadFileWithText(@Url String url,
                                    @HeaderMap Map<String, String> headers, @QueryMap Map<String, String> params, @PartMap() Map<String, RequestBody> files);

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
    Call<String> uploadFileWithHeader(@Url String url, @HeaderMap Map<String, String> headers, @PartMap() Map<String, RequestBody> files);

}
