package com.test.xcamera.api;


import com.framwork.base.BaseResponse;
import com.test.xcamera.bean.ActivationCode;
import com.test.xcamera.bean.AppVersion;
import com.test.xcamera.bean.ClickLikeAndShare;
import com.test.xcamera.bean.CommunityBean;
import com.test.xcamera.bean.FeedList;
import com.test.xcamera.bean.SplashScreenBean;
import com.test.xcamera.bean.SqueezeBean;
import com.test.xcamera.bean.UploadBean;
import com.test.xcamera.bean.UploadWorksBean;
import com.test.xcamera.bean.User;
import com.test.xcamera.bean.UserInfo;
import com.test.xcamera.phonealbum.bean.MusicResult;
import com.test.xcamera.phonealbum.bean.MusicTypeResult;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * creat  by mz  2019.9.23
 */
public interface ApiService {

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

    @PUT
    Call<String> putRequestAPiString(@Url String url, @HeaderMap Map<String, String> headers, @QueryMap Map<String, String> params);

    @DELETE
    Call<String> deleteRequestAPiString(@Url String url, @HeaderMap Map<String, String> headers, @QueryMap Map<String, String> params);

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
    Call<String> postJsonAPiString(@Url String url, @HeaderMap Map<String, String> headers, @Body RequestBody params);


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
     * @param url 请求URl
     *            = * @param files  请求文件
     * @return
     */
    @Multipart
    @POST
    Call<String> uploadOneFile(@Url String url,
                               @QueryMap() Map<String, String> params,
                               @Part List<MultipartBody.Part> files);

    @GET("api/token/captcha")
    Observable<BaseResponse> VerificationCode(@Query("phone") String phone);

    @PUT("api/token")
    Observable<User> ExtendToken();

    @GET("api/bbs/entrance")
    Observable<CommunityBean> community();

    @DELETE("api/token")
    Observable<User> unLogin();

    @POST("api/token")
    Observable<User> login(@Query("phone") String phone, @Query("captcha") String captcha);


    //    获取用户信息
    @GET("api/my/profile")
    Observable<UserInfo> getUserInfo();

    //    用户信息修改
    @PUT("api/my/profile")
    Observable<User> userProfile(@Query("avatarFileId") String avatarFileId, @Query("nickname") String nickname, @Query("description") String description);


    @FormUrlEncoded
    @POST("api/device/activation")
    Observable<ActivationCode> ActivationCode(@Field("did") String did, @Field("nonce") String nonce);

    @PUT("api/device/activation/status")
    Observable<BaseResponse> PushActivationCode(@Query("activationId") String activationId);

    @GET("api/app/version/{version}")
    Observable<AppVersion> PushAppVersion(@Path("version") String version, @Query("platform") int platform);

    @Streaming
    @GET("api/app/package/{version}")
    Call<ResponseBody> UpgradeApp(@Path("version") String version);

    @Streaming
    @GET("api/file/{id}")
    Call<ResponseBody> downLoadFile(@Path("id") long version);

    @Streaming
    @GET
    Call<ResponseBody> downLoad(@Url String url);

    @GET("/api/firmware/version/{version}")
    Observable<AppVersion> PushHardWareVersion(@Path("version") String version, @Query("did") String did);

    @Streaming
    @GET("api/firmware/package/{version}")
    Call<ResponseBody> UpgradeHardWareFile(@Path("version") String version, @Query("did") String did);

    @Multipart
    @POST("api/file")
    Call<UploadBean> UploadFile(@Part List<MultipartBody.Part> files);


    @FormUrlEncoded
    @POST("api/my/opus")
    Observable<UploadWorksBean> UploadWorks(@Field("videoFileId") long videoFileId, @Field("description") String description, @Field("duration") long duration, @Field("coverFileId") long coverFileId, @Field("templateId") long templateId, @Field("bgmId") long bgmId, @Field("videoSize") long videoSize);

    @GET("api/feed/list")
    Observable<FeedList> getFeedList(@Query("pn") int pn, @Query("ps") int ps);

    @GET("api/my/opus/list")
    Observable<FeedList> getMyOPusList(@Query("pn") int pn, @Query("ps") int ps);


    @GET("api/opus/propagation/{opusId}")
    Observable<FeedList> getLikeAndShareNum(@Path("opusId") String opusId);

    @PUT("api/opus/like/{opusId}")
    Observable<ClickLikeAndShare> clickLike(@Path("opusId") long opusId);

    @DELETE("api/opus/like/{opusId}")
    Observable<ClickLikeAndShare> cancelLike(@Path("opusId") long opusId);

    @PUT("api/opus/share/{opusId}")
    Observable<ClickLikeAndShare> clickShare(@Path("opusId") long opusId);

    @GET("api/bgm/list")
    Observable<MusicResult> getMusicResult(@Query("pn") int pn, @Query("ps") int ps, @Query("typeId") int typeId);

    @GET("api/bgm/search")
    Observable<MusicResult> getSearchMusicResult(@Query("pn") int pn, @Query("ps") int ps, @Query("keywords") String keyWord);

    @GET("api/bgm/type/list")
    Observable<MusicTypeResult> getMusicTypeResult(@Query("pn") int pn, @Query("ps") int ps);

    @GET("api/splashScreen/list")
    Observable<SplashScreenBean> getSplashScreenData();

    @GET("api/opus/link/{opusId}")
    Observable<SqueezeBean> getFeedSqueeze(@Path("opusId") String opusId);
}


