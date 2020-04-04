package com.caishi.chaoge.http;


import com.caishi.chaoge.base.BaseBean;
import com.caishi.chaoge.bean.AliStsTokenBean;
import com.caishi.chaoge.bean.ClassListBean;
import com.caishi.chaoge.bean.FontBean;
import com.caishi.chaoge.bean.HomeDataBean;
import com.caishi.chaoge.bean.LoginBean;
import com.caishi.chaoge.bean.MainFragmentBean;
import com.caishi.chaoge.bean.MessageNumBean;
import com.caishi.chaoge.bean.MineDataBean;
import com.caishi.chaoge.bean.NewMessageBean;
import com.caishi.chaoge.bean.RecommendBean;
import com.caishi.chaoge.bean.ScenarioBean;
import com.caishi.chaoge.bean.TemplateBean;
import com.caishi.chaoge.bean.VoiceTranslateBean;

import java.util.ArrayList;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * 请求接口
 */
public interface RequestApi {
    /**
     * * 纯文本接口
     *
     * @param url     请求URl
     * @param headers 请求header
     * @param params  请求参数
     * @return
     */
    @GET
    Call<String> getAPiString(@Url String url, @HeaderMap Map<String, String> headers, @QueryMap Map<String, String> params);

    /**
     * * 纯文本接口
     *
     * @param url     请求URl
     * @param headers 请求header
     * @param params  请求参数
     * @return
     */
    @Headers({"Content-Type:application/x-www-form-urlencoded; charset=utf-8"})
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
     * @param params  请求参数
     *                = * @param files  请求文件
     * @return
     */
    @Multipart
    @POST
    Call<String> uploadFileWithText(@Url String url,
                                    @HeaderMap Map<String, String> headers, @FieldMap Map<String, String> params, @PartMap() Map<String, RequestBody> files);

    /**
     * 登录
     *
     * @return Observable
     */
    @Headers({"version:" + RequestURL.VERSION})
    @FormUrlEncoded
    @POST(RequestURL.LOGIN)
    Observable<BaseBean<LoginBean>> login(@FieldMap Map<String, Object> map);

    /**
     * 自动登录
     *
     * @return Observable
     */
    @Headers({"version:" + RequestURL.VERSION})
    @FormUrlEncoded
    @POST(RequestURL.AUTO_LOGIN)
    Observable<BaseBean<LoginBean>> autoLogin(@Field("") String s);

    /**
     * 虚拟登录
     *
     * @return Observable
     */
    @Headers({"version:" + RequestURL.VERSION})
    @FormUrlEncoded
    @POST(RequestURL.VIRTUAL_LOGIN)
    Observable<BaseBean<LoginBean>> virtualLogin(@Field("") String s);

    /**
     * 第三方登录
     *
     * @return Observable
     */
    @Headers({"version:" + RequestURL.VERSION, "Content-Type:application/x-www-form-urlencoded; charset=utf-8"})
    @FormUrlEncoded
    @POST(RequestURL.OTHER_LOGIN)
    Observable<BaseBean<LoginBean>> otherLogin(
            @FieldMap Map<String, Object> map);


    /**
     * 绑定手机号
     *
     * @return Observable
     */
    @Headers({"version:" + RequestURL.VERSION})
    @FormUrlEncoded
    @POST(RequestURL.BOUND_MOBILD)
    Observable<BaseBean<MineDataBean>> boundMobile(@FieldMap Map<String, Object> map);

    /**
     * 首页基础数据
     *
     * @return Observable  music/find
     */
    @Headers({"version:" + RequestURL.VERSION})
    @GET(RequestURL.BASIC_DATA)
    Observable<BaseBean<MainFragmentBean>> basicData();

    /**
     * 获取字体信息
     *
     * @return Observable  music/find
     */
    @Headers({"version:" + RequestURL.VERSION})
    @GET(RequestURL.FONT_LIST)
    Observable<BaseBean<ArrayList<FontBean>>> getFontList();

    /**
     * 获取分类信息
     *
     * @param type 那个类别 音乐 music 剧本 script 背景图 backGround 模板 model
     * @return Observable
     */
    @Headers({"version:" + RequestURL.VERSION})
    @GET(RequestURL.GET_CLASS_LIST)
    Observable<BaseBean<ArrayList<ClassListBean>>> getClassList(@Query("type") String type);

    /**
     * 获取颜色信息
     *
     * @return Observable  music/find
     */
    @Headers({"version:" + RequestURL.VERSION})
    @GET(RequestURL.COLOR_LIST)
    Observable<BaseBean<ArrayList<String>>> getColorList();

    /**
     * 随机剧本
     *
     * @return Observable
     */
    @Headers({"version:" + RequestURL.VERSION})
    @GET(RequestURL.RANDOM)
    Observable<BaseBean<ArrayList<ScenarioBean>>> getRandomList(@Query("pageSize") int pageSize);


    /**
     * 获取验证码
     *
     * @param mobile 手机号
     * @return Observable
     */
    @Headers({"version:" + com.caishi.chaoge.http.RequestURL.VERSION})
    @GET(RequestURL.GET_VALIDATE_AUTHCODE)
    Observable<BaseBean<Boolean>> getValidateAuthCode(
            @Query("mobile") String mobile);

    /**
     * 获取阿里OSStoken
     *
     * @return Observable
     */
    @Headers({"version:" + RequestURL.VERSION})
    @GET(RequestURL.GET_TOKEN)
    Observable<BaseBean<AliStsTokenBean>> getToken();


    /**
     * 发布视频
     *
     * @param map map集
     * @return Observable
     */
    @Headers({"version:" + RequestURL.VERSION2, "Content-Type:application/x-www-form-urlencoded; charset=utf-8"})
    @FormUrlEncoded
    @POST(RequestURL.PUBLISH)
    Observable<BaseBean<String>> publish(@FieldMap Map<String, Object> map);


    /**
     * 获取推荐模板
     *
     * @param params map参数集
     * @return Observable
     */
    @Headers({"version:" + RequestURL.VERSION})
    @GET(RequestURL.RECOMMEND)
    Observable<BaseBean<ArrayList<RecommendBean>>> recommend(@QueryMap Map<String, Object> params);

    /**
     * 获取模板列表
     *
     * @param params map参数集
     * @return Observable
     */
    @Headers({"version:" + RequestURL.VERSION})
    @GET(RequestURL.GET_MODEL_LIST)
    Observable<BaseBean<ArrayList<RecommendBean>>> getModelList(@QueryMap Map<String, Object> params);


    //GET /v1/


    /**
     * 获取模板详情
     *
     * @param modelId 模板id
     * @return Observable
     */
    @Headers({"version:" + RequestURL.VERSION})
    @GET(RequestURL.GET_MODEL_BY_ID)
    Observable<BaseBean<TemplateBean>> getModelById(
            @Query("modelId") String modelId);

    /**
     * 语音识别（阿里）
     *
     * @param fileLink 语音文件在阿里云服务器的地址
     * @return Observable
     */
    @Headers({"version:" + RequestURL.VERSION})
    @GET(RequestURL.GET_RECOGNITION)
    Observable<BaseBean<ArrayList<VoiceTranslateBean>>> getRecognition(
            @Query("fileLink") String fileLink);

    /**
     * 获取新粉丝
     *
     * @return Observable
     */
    @Headers({"version:" + RequestURL.VERSION})
    @GET(RequestURL.GET_NEW_FANS)
    Observable<BaseBean<ArrayList<NewMessageBean>>> getNewFans(@QueryMap Map<String, Object> params);

    /**
     * 获取新粉丝
     *
     * @return Observable
     */
    @Headers({"version:" + RequestURL.VERSION})
    @GET(RequestURL.GET_NEW_LIKES)
    Observable<BaseBean<ArrayList<NewMessageBean>>> getNewLikes(@QueryMap Map<String, Object> params);

    /**
     * 获取新粉丝
     *
     * @return Observable
     */
    @Headers({"version:" + RequestURL.VERSION})
    @GET(RequestURL.GET_NEW_MOMENT_COMMENTS)
    Observable<BaseBean<ArrayList<NewMessageBean>>> getNewMomentComments(@QueryMap Map<String, Object> params);

    /**
     * 获取消息数
     *
     * @return Observable
     */
    @Headers({"version:" + RequestURL.VERSION})
    @GET(RequestURL.GET_MESSAGE_NUM)
    Observable<BaseBean<MessageNumBean>> getMessageNum(@Query("userId") String userId);

    /**
     * 根据动态Id查询动态 GET /v1/findMoment4App
     *
     * @return Observable
     */
    @Headers({"version:" + RequestURL.VERSION})
    @GET(RequestURL.FIND_MOMENT)
    Observable<BaseBean<HomeDataBean>> findMoment(@Query("momentId") String momentId);


    /**
     * 删除粉丝消息
     *
     * @return Observable
     */
    @FormUrlEncoded
    @Headers({"version:" + RequestURL.VERSION})
    @POST(RequestURL.DEL_NEW_FANS)
    Observable<BaseBean<Boolean>> delNewFans(@Field("targetTime") String targetTime);

    /**
     * 删除点赞消息
     *
     * @return Observable
     */
    @FormUrlEncoded
    @Headers({"version:" + RequestURL.VERSION})
    @POST(RequestURL.DE_NEW_LIKE)
    Observable<BaseBean<Boolean>> delNewLikes(@Field("targetTime") String targetTime);

    /**
     * 删除评论消息
     *
     * @return Observable
     */
    @FormUrlEncoded
    @Headers({"version:" + RequestURL.VERSION})
    @POST(RequestURL.DEL_NEW_MOMENT_COMMENTS)
    Observable<BaseBean<Boolean>> delNewMomentComments(@Field("targetTime") String targetTime);

}
