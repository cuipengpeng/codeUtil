package com.test.xcamera.api.http;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.test.xcamera.BuildConfig;
import com.test.xcamera.api.ApiService;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.base.IBaseView;
import com.test.xcamera.upload.UploadFileRequestBody;
import com.test.xcamera.utils.Constants;
import com.test.xcamera.utils.LoggerUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class HttpRequest {
    //测试环境
    private static final String APP_INTERFACE_WEB_URL_ENV_TEST = Constants.base_url;
    //预发环境
    private static final String APP_INTERFACE_WEB_URL_ENV_PRE ="https://www.baidu.com/";
    // 正式环境
    private static final String APP_INTERFACE_WEB_URL_ENV_ONLINE = Constants.base_url;

    //图片上传       测试环境
    private static final String UPLOAD_IMAGE_TEST_ENV_WEB_URL = "https://www.baidu.com/";
    //预发环境
    private static final String UPLOAD_IMAGE_PRE_ENV_WEB_URL = UPLOAD_IMAGE_TEST_ENV_WEB_URL;
    //正式环境
    private static final String UPLOAD_IMAGE_ONLINE_ENV_WEB_URL = "https://www.baidu.com/";

    //人脸识别      测试环境
    private static final String FACE_DETECT_TEST_ENV_URL = "https://www.baidu.com/";
    //预发环境
    private static final String FACE_DETECT_PRE_ENV_URL = FACE_DETECT_TEST_ENV_URL;
    //线上环境
    private static final String FACE_DETECT_ONLINE_ENV_URL = "https://www.baidu.com/";



    private enum Environment {
        TEST, PRE, ONLINE
    }
    private static Environment currentEnv = null;
    public static String APP_INTERFACE_WEB_URL = APP_INTERFACE_WEB_URL_ENV_TEST;
    public static String UPLOAD_IMAGE_WEB_URL = UPLOAD_IMAGE_TEST_ENV_WEB_URL;
    public static String FACE_DETECT_WEB_URL = FACE_DETECT_TEST_ENV_URL;

    public static void initEnvironment() {
        if(BuildConfig.DEBUG){
            currentEnv = Environment.ONLINE;
        }else {
            currentEnv = Environment.ONLINE;
        }

        if (currentEnv == Environment.TEST) {
            APP_INTERFACE_WEB_URL = APP_INTERFACE_WEB_URL_ENV_TEST;
            FACE_DETECT_WEB_URL = FACE_DETECT_TEST_ENV_URL;
            UPLOAD_IMAGE_WEB_URL = UPLOAD_IMAGE_TEST_ENV_WEB_URL;
        } else if (currentEnv == Environment.PRE) {
            APP_INTERFACE_WEB_URL = APP_INTERFACE_WEB_URL_ENV_PRE;
            FACE_DETECT_WEB_URL = FACE_DETECT_PRE_ENV_URL;
            UPLOAD_IMAGE_WEB_URL = UPLOAD_IMAGE_PRE_ENV_WEB_URL;
        } else if (currentEnv == Environment.ONLINE) {
            APP_INTERFACE_WEB_URL = APP_INTERFACE_WEB_URL_ENV_ONLINE;
            FACE_DETECT_WEB_URL = FACE_DETECT_ONLINE_ENV_URL;
            UPLOAD_IMAGE_WEB_URL = UPLOAD_IMAGE_ONLINE_ENV_WEB_URL;
        }
    }


    private static long requestReadTime = 5;
    private static long requestWriteTime = 5;
    private static long requestConnectTime = 5;

    private static long uploadReadTime = 20;
    private static long uploadWriteTime = 20;
    private static long uploadConnectTime = 20;


    private static final boolean SHOW_LOADING = true;


    public enum RequestType {
        POST, GET, PUT, DELETE
    }
    /**
     * 发送普通文本请求接口
     * @param url
     * @param params
     *     基view， 可为null。网络请求数据正常时，用iBaseView.showContentView();显示页面内容
     * @param responseHandler
     * @return
     */
    public static Call<String> postSubUrl(RequestType requestType, String url, Map<String, String> params, HttpResponseCallBack responseHandler) {
        return post(requestType, null,HttpRequest.APP_INTERFACE_WEB_URL+url, params, requestReadTime, requestWriteTime, requestConnectTime, "", SHOW_LOADING, responseHandler);
    }
    public static Call<String> post(RequestType requestType, String url, Map<String, String> params, HttpResponseCallBack responseHandler) {
        return post(requestType, null,url, params, requestReadTime, requestWriteTime, requestConnectTime, "", SHOW_LOADING, responseHandler);
    }
    public static Call<String> post(RequestType requestType, IBaseView iBaseView, String url, Map<String, String> params, HttpResponseCallBack responseHandler) {
        return post(requestType, iBaseView, url, params, requestReadTime, requestWriteTime, requestConnectTime, "", SHOW_LOADING, responseHandler);
    }

    public static Call<String> post(RequestType requestType, IBaseView iBaseView, String url, Map<String, String> params, boolean showLoading, HttpResponseCallBack responseHandler) {
        return post(requestType, iBaseView, url, params, requestReadTime, requestWriteTime, requestConnectTime, "", showLoading, responseHandler);
    }

    public static Call<String> post(RequestType requestType, IBaseView iBaseView, String url, Map<String, String> params, String requestChannel, HttpResponseCallBack responseHandler) {
        return post(requestType, iBaseView, url, params, requestReadTime, requestWriteTime, requestConnectTime, requestChannel, SHOW_LOADING, responseHandler);
    }

    public static Call<String> post(RequestType requestType, IBaseView iBaseView, String url, Map<String, String> params, long readTime, long writeTime, long timeOut, String requestChannel, boolean showLoading, HttpResponseCallBack responseHandler) {
        return execute(requestType, iBaseView, url, new HashMap<String, String>(), params, false, null, responseHandler, readTime, writeTime, timeOut, requestChannel, showLoading);
    }

    /**
     * 文件上传接口
     *
     * @param url
     * @param params
     * @param filesMap
     * @param progressListener
     * @return
     */
    public static void uploadFiles(String url, Map<String, String> params, Map<String, File> filesMap, UploadFileRequestBody.ProgressListener progressListener) {
         uploadFiles(url, new HashMap<String, String>(), params, filesMap, progressListener, uploadReadTime, uploadWriteTime, uploadConnectTime, SHOW_LOADING);
    }

    public static void uploadFiles(String url, Map<String, String> headers, Map<String, String> params, Map<String, File> filesMap, UploadFileRequestBody.ProgressListener progressListener, long readTime, long writeTime, long timeOut, boolean showLoading) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                execute(null,null, url, headers, params, true, filesMap, progressListener, readTime, writeTime, timeOut, "", showLoading);
            }
        }).start();
    }

    /**
     * http请求回调
     */
    public interface HttpResponseCallBack {
        void onSuccess(String response);

        void onFailure(String response);
    }

    /**
     * 发送http请求， 或者上传文件
     *
     * @param url
     * @param headers
     * @param params
     * @param uploadFile
     * @param uploadFilesMap
     * @param responseHandler
     * @param readTime
     * @param writeTime
     * @param timeOut
     * @param iBaseView
     * @param requestChannel
     * @param showLoading
     * @return
     */
    private static Call<String> execute(RequestType requestType, IBaseView iBaseView, String url, Map<String, String> headers, Map<String, String> params, boolean uploadFile, Map<String, File> uploadFilesMap, HttpResponseCallBack responseHandler, long readTime, long writeTime, long timeOut, String requestChannel, boolean showLoading) {
//        params.put("version", DeviceUtil.getAppVersionName(BaseApplication.applicationContext));
//        params.put("equipment", DeviceUtil.getDevicecId());
//        params.put("mobileSystem", "android");
//        if(BaseApplication.userInfo!=null && BaseApplication.userInfo.getToken()!=null){
//            params.put("token", BaseApplication.userInfo.getToken());
//            params.put("userId", BaseApplication.userInfo.getMobile()+"");
//        }
        TreeMap<String, String> treeMap = new TreeMap<String, String>();
        treeMap.putAll(params);
//        String signedContent = Md5Util.sign(treeMap); // Des3.signContent(params.toString());
//        LoggerUtils.printLog("--已签名的内容：" + signedContent);
//        params.put("sign", signedContent);
        params = filterEmptyParams(params);
        LoggerUtils.printLog("url= " + url + "--请求参数：" + params.toString());

        Call<String> call = null;
        ApiService apiService = getApiService(readTime, writeTime, timeOut);
        if (uploadFile) {
            Map<String, RequestBody> filesMap = new HashMap<String, RequestBody>();
            List<MultipartBody.Part>  partList=new ArrayList<>();
            RequestBody requestBody = null;
            for (Map.Entry<String, File> entry : uploadFilesMap.entrySet()) {
//                requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), uploadFilesMap.get(keyStr));
                requestBody = new UploadFileRequestBody(entry.getValue(), partList.size(),uploadFilesMap.entrySet().size(), (UploadFileRequestBody.ProgressListener) responseHandler);
                filesMap.put(entry.getKey(), requestBody);
                MultipartBody.Part  part= MultipartBody.Part.createFormData(entry.getKey(), entry.getValue().getAbsolutePath(),requestBody);
                partList.add(part);
            }
//            call = apiService.uploadFileWithText(url, headers, filesMap);
            call = apiService.uploadOneFile(url,params,partList);
        } else {
            if(requestType==RequestType.GET){
                call = apiService.getRequestAPiString(url, headers, params);
            }else if(requestType==RequestType.POST){
                //application/x-www-form-urlencoded格式
//                    if(headers==null){
//                        headers = new HashMap<>();
//                    }
//                    headers.put("Content-Type","application/x-www-form-urlencoded; charset=utf-8");
//                call = apiService.postFormAPiString(url, headers, params);

                //application/json格式
                RequestBody body = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(params));
                call = apiService.postJsonAPiString(url, headers, body);
            }else if(requestType==RequestType.PUT){
                for(String value : params.values()){
                    url=url+"/"+value;
                }
                call = apiService.putRequestAPiString(url, headers, new HashMap<>());
            }else if(requestType==RequestType.DELETE){
                for(String value : params.values()){
                    url=url+"/"+value;
                }
                call = apiService.deleteRequestAPiString(url, headers, new HashMap<>());
            }
        }

        Dialog loadingAlertDialog = null;
//        LoadingAlertDialog loadingAlertDialog = null;
//        if (showLoading) {
//            if (iBaseView instanceof BaseUIActivity) {
//                loadingAlertDialog = new LoadingAlertDialog(((BaseUIActivity) iBaseView));
//                loadingAlertDialog.show();
//            } else if (iBaseView instanceof BaseUIFragment) {
//                loadingAlertDialog = new LoadingAlertDialog(((BaseUIFragment) iBaseView).getActivity());
//                loadingAlertDialog.show();
//            }
//        }

        call.enqueue(new AsyncResponseCallBack(responseHandler, iBaseView, requestChannel, showLoading, loadingAlertDialog));
//        addCall(context, call);
        return call;
    }

    private static Map<String, ApiService> mapAPIService = new HashMap<>();

    /**
     * * 获取APIService
     *
     * @param readTime  请求时间
     * @param writeTime 读取数据时间
     * @param timeOut   连接超时时间
     * @return
     */
    private static ApiService getApiService(long readTime, long writeTime, long timeOut) {
        String apiServiceKey = readTime + writeTime + timeOut + "";
        ApiService apiService = mapAPIService.get(apiServiceKey);
        if (apiService == null) {//为空，则创建
            synchronized (mapAPIService) { //锁定对象
                OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                        // 添加通用的Header
                        .addInterceptor(new Interceptor() {
                            @Override
                            public okhttp3.Response intercept(Chain chain) throws IOException {
                                Request.Builder builder = chain.request().newBuilder();
                                if(AiCameraApplication.userDetail!=null && AiCameraApplication.userDetail.getToken()!=null){
                                    builder.addHeader("X-token", AiCameraApplication.userDetail.getToken());
//                                            .addHeader("userId", AiCameraApplication.userDetail.getMobile()+"");
                                }
                                return chain.proceed(builder.build());
                            }
                        })
                        .connectTimeout(timeOut, TimeUnit.SECONDS)
                        .readTimeout(readTime, TimeUnit.SECONDS)
                        .writeTimeout(writeTime, TimeUnit.SECONDS);
                //设置日志拦截器
                if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//                    httpClientBuilder.addInterceptor(loggingInterceptor);
//                    httpClientBuilder.addInterceptor(new LogInterceptor());
                }

                Retrofit retrofit = new Retrofit.Builder().client(httpClientBuilder.build())
                        .baseUrl(HttpRequest.APP_INTERFACE_WEB_URL)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .build();
                apiService = retrofit.create(ApiService.class);
                ApiService mApiService = mapAPIService.get(apiServiceKey);//再次获取mapAPIService中的APIService
                if (mApiService != null) {//添加对象前判断：若已经添加，则新建的APIService不需要加入mapAPIService【同时创建多个APIService,只添加一个，并且使用】
                    return mApiService;
                } else {//添加apiService
                    mapAPIService.put(apiServiceKey, apiService);
                    return apiService;
                }
            }
        } else {
            return apiService;
        }
    }

    /**
     * retrofit参数为空处理转化。
     *
     * @param params
     * @return
     */
    public static Map<String, String> filterEmptyParams(Map<String, String> params) {
        for (String key : params.keySet()) {
            if (params.get(key) == null) {
                params.put(key, "");
            }
        }
        return params;
    }

    private static Map<Context, List<Call<String>>> callManager = new HashMap<>();

    /**
     * 添加标记网络请求。
     *
     * @param context
     * @param call
     */
    private static void addCall(Context context, Call<String> call) {
        if (context == null) {
            return;
        }
        List<Call<String>> list = callManager.get(context);
        if (list == null) {
            list = new ArrayList<>();
            callManager.put(context, list);
        }
        Log.e("addCallTag", call.request().url().toString());
        list.add(call);
    }

    /**
     * 取消网络请求
     * @param context
     */
//    public static void cancelRequestNet(Context context) {
//        if (context != null) {
//            List<Call<String>> list = callManager.get(context);
//            if (!CommonUtils.isEmptyList(list)) {
//                for (int i = 0; i < list.size(); i++) {
//                    Call<String> call = list.get(i);
//                    if (!call.isCanceled()) {
//                        call.cancel();
//                        Log.e("cancel", call.request().url().toString());
//                    }
//                }
//                callManager.remove(context);
//            }
//        }
//    }

    /**
     *feed流列表
     */
    public static final String GET_FEED_LIST = "api/feed/list";
    /**
     *作品点赞
     */
    public static final String LIKE_VIDEO = "api/opus/like";
    /**
     *作品分享
     */
    public static final String SHARE_VIDEO = "api/opus/share";
    /**
     *匹配模板
     */
    public static final String MATCH_TEMPLETE = "api/template/matchable";
    /**
     *模板详情
     */
    public static final String GET_TEMPLETE_DETAIL = "api/template/";
    /**
     *获取用户信息
     */
    public static final String GET_USER_INFO = "api/my/profile";
    /**
     *上传文件
     */
    public static final String UPLOAD_VIDEO_FILE = "api/file";

    public static final String LIKEANDSHAER = "api/opus/propagation";

}
