package com.hospital.checkup.http;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.hospital.checkup.BuildConfig;
import com.hospital.checkup.base.BaseApplication;
import com.hospital.checkup.base.BaseUIActivity;
import com.hospital.checkup.base.BaseUIFragment;
import com.hospital.checkup.base.IBaseView;
import com.hospital.checkup.utils.DeviceUtil;
import com.hospital.checkup.utils.LogUtils;
import com.hospital.checkup.utils.MD5;
import com.hospital.checkup.widget.LoadingAlertDialog;

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
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class HttpRequest {
    //测试环境
    private static final String APP_INTERFACE_WEB_URL_ENV_TEST = "http://www.perfecservice.com/";
    //预发环境
    private static final String APP_INTERFACE_WEB_URL_ENV_PRE ="https://www.baidu.com/";
    // 正式环境
    private static final String APP_INTERFACE_WEB_URL_ENV_ONLINE ="https://www.baidu.com/";

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

    private static final String MOCK_DATA = "mock/14/";
    private static final String H5_SUB_PATH = "dist/#/";
    private static final String H5_URL = APP_INTERFACE_WEB_URL+H5_SUB_PATH;

    public static void initEnvironment() {
        if(BuildConfig.DEBUG){
            currentEnv = Environment.TEST;
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
     * @param iBaseView  基view， 可为null。网络请求数据正常时，用iBaseView.showContentView();显示页面内容
     * @param responseHandler
     * @return
     */
    public static Call<String> post(boolean getRequest, IBaseView iBaseView, String url, Map<String, String> params, HttpResponseCallBack responseHandler) {
        if(getRequest){
            return post(RequestType.GET, iBaseView, url, params, requestReadTime, requestWriteTime, requestConnectTime, "", SHOW_LOADING, responseHandler);
        }else {
            return post(RequestType.POST, iBaseView, url, params, requestReadTime, requestWriteTime, requestConnectTime, "", SHOW_LOADING, responseHandler);
        }
    }

    public static Call<String> post(RequestType requestType, IBaseView iBaseView, String url, Map<String, String> params, HttpResponseCallBack responseHandler) {
        return post(requestType, iBaseView, url, params, requestReadTime, requestWriteTime, requestConnectTime, "", SHOW_LOADING, responseHandler);
    }

    public static Call<String> post(boolean getRequest, IBaseView iBaseView, String url, Map<String, String> params, boolean showLoading, HttpResponseCallBack responseHandler) {
        if(getRequest){
            return post(RequestType.GET, iBaseView, url, params, requestReadTime, requestWriteTime, requestConnectTime, "", showLoading, responseHandler);
        }else {
            return post(RequestType.POST, iBaseView, url, params, requestReadTime, requestWriteTime, requestConnectTime, "", showLoading, responseHandler);
        }
    }

    public static Call<String> post(boolean getRequest, IBaseView iBaseView, String url, Map<String, String> params, String requestTag, HttpResponseCallBack responseHandler) {
        if(getRequest){
            return post(RequestType.GET, iBaseView, url, params, requestReadTime, requestWriteTime, requestConnectTime, requestTag, SHOW_LOADING, responseHandler);
        }else {
            return post(RequestType.POST, iBaseView, url, params, requestReadTime, requestWriteTime, requestConnectTime, requestTag, SHOW_LOADING, responseHandler);
        }
    }

    public static Call<String> post(RequestType requestType, IBaseView iBaseView, String url, Map<String, String> params, long readTime, long writeTime, long timeOut, String requestTag, boolean showLoading, HttpResponseCallBack responseHandler) {
        return execute(requestType, iBaseView, url, new HashMap<String, String>(), params, false, null, responseHandler, readTime, writeTime, timeOut, requestTag, showLoading);
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

    public static void uploadFiles(final String url, final Map<String, String> headers, final Map<String, String> params, final Map<String, File> filesMap, final UploadFileRequestBody.ProgressListener progressListener, final long readTime, final long writeTime, final long timeOut, final boolean showLoading) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                execute(RequestType.POST, null, url, headers, params, true, filesMap, progressListener, readTime, writeTime, timeOut, "", showLoading);
            }
        }).start();

    }

    /**
     * http请求回调
     */
    public interface HttpResponseCallBack {
        void onResponse(Call<String> call, Response<String> response);

        void onFailure(Call<String> call, Throwable t);
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
        params.put("version", DeviceUtil.getAppVersionName(BaseApplication.applicationContext));
        params.put("equipment", DeviceUtil.getDevicecId());
        params.put("mobileSystem", "android");
//        if(BaseApplication.userInfo!=null && BaseApplication.userInfo.getToken()!=null){
//            params.put("token", BaseApplication.userInfo.getToken());
//            params.put("userId", BaseApplication.userInfo.getMobile()+"");
//        }
        TreeMap<String, String> treeMap = new TreeMap<String, String>();
        treeMap.putAll(params);
        String signedContent = MD5.sign(treeMap); // Des3.signContent(params.toString());
        LogUtils.printLog("--已签名的内容：" + signedContent);
        params.put("sign", signedContent);
        params = filterEmptyParams(params);
        LogUtils.printLog("url= " + url + "--请求参数：" + params.toString());

        Call<String> call = null;
        APIService apiService = getApiService(readTime, writeTime, timeOut);
        if (uploadFile) {
            RequestBody requestBody = null;
            List<MultipartBody.Part>  partList=new ArrayList<>();
            for (Map.Entry<String, File> entry : uploadFilesMap.entrySet()) {
//                requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), entry.getValue());
                requestBody = new UploadFileRequestBody(entry.getValue(), partList.size(),uploadFilesMap.entrySet().size(), (UploadFileRequestBody.ProgressListener) responseHandler);
                MultipartBody.Part  part= MultipartBody.Part.createFormData(entry.getKey(), entry.getValue().getAbsolutePath(),requestBody);
                partList.add(part);
            }
            call = apiService.uploadMultiFiles(url, params, partList);

//            Map<String, RequestBody> filesMap = new HashMap<String, RequestBody>();
//             for (Map.Entry<String, File> entry : uploadFilesMap.entrySet()) {
//                requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), entry.getValue());
//                filesMap.put(entry.getKey(), requestBody);
//            }
//            call = apiService.uploadFileWithText(url, headers, filesMap);
        } else {
            if(requestType== RequestType.GET){
                call = apiService.getRequestAPiString(url, headers, params);
            }else if(requestType== RequestType.POST){
                //格式: application/x-www-form-urlencoded
//                    if(headers==null){
//                        headers = new HashMap<>();
//                    }
//                    headers.put("Content-Type","application/x-www-form-urlencoded; charset=utf-8");
//                call = apiService.postFormAPiString(url, headers, params);

                //格式: application/json
                RequestBody body = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(params));
                call = apiService.postJsonAPiString(url, headers, body);
            }else if(requestType== RequestType.PUT){
                for(String value : params.values()){
                    url=url+"/"+value;
                }
                call = apiService.putRequestAPiString(url, headers, new HashMap<String, String>());
            }else if(requestType== RequestType.DELETE){
                for(String value : params.values()){
                    url=url+"/"+value;
                }
                call = apiService.deleteRequestAPiString(url, headers, new HashMap<String, String>());
            }
        }

        LoadingAlertDialog loadingAlertDialog = null;
        if (showLoading) {
            if (iBaseView instanceof BaseUIActivity) {
                loadingAlertDialog = new LoadingAlertDialog(((BaseUIActivity) iBaseView));
                loadingAlertDialog.show();
            } else if (iBaseView instanceof BaseUIFragment) {
                loadingAlertDialog = new LoadingAlertDialog(((BaseUIFragment) iBaseView).getActivity());
                loadingAlertDialog.show();
            }
        }

        call.enqueue(new AsyncResponseCallBack(responseHandler, iBaseView, requestChannel, showLoading, loadingAlertDialog));
//        addCall(context, call);
        return call;
    }

    private static Map<String, APIService> mapAPIService = new HashMap<>();

    /**
     * * 获取APIService
     *
     * @param readTime  请求时间
     * @param writeTime 读取数据时间
     * @param timeOut   连接超时时间
     * @return
     */
    private static APIService getApiService(long readTime, long writeTime, long timeOut) {
        String apiServiceKey = readTime + writeTime + timeOut + "";
        APIService apiService = mapAPIService.get(apiServiceKey);
        if (apiService == null) {//为空，则创建
            synchronized (mapAPIService) { //锁定对象
                OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                        // 添加通用的Header
                        .addInterceptor(new Interceptor() {
                            @Override
                            public okhttp3.Response intercept(Chain chain) throws IOException {
                                Request.Builder builder = chain.request().newBuilder();
//                                if(BaseApplication.userInfo!=null && BaseApplication.userInfo.getToken()!=null){
//                                    builder.addHeader("token", BaseApplication.userInfo.getToken())
//                                            .addHeader("userId", BaseApplication.userInfo.getMobile()+"");
//                                }
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
                    httpClientBuilder.addInterceptor(new LogInterceptor());
                }

                Retrofit retrofit = new Retrofit.Builder().client(httpClientBuilder.build())
                        .baseUrl(HttpRequest.APP_INTERFACE_WEB_URL)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .build();
                apiService = retrofit.create(APIService.class);
                APIService mApiService = mapAPIService.get(apiServiceKey);//再次获取mapAPIService中的APIService
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
     * 登录
     */
    public static final String CHECKUP_LOGIN = MOCK_DATA+"m/api/login";
    /**
     * 保存试者详情数据
     */
    public static final String SAVE_MEASURER_DETAIL_INFO = MOCK_DATA+"m/api/test-management/objs";
    /**
     * 获取测试模型列表
     */
    public static final String TEST_MODEL_LIST = MOCK_DATA+"m/api/test-management/models";
    /**
     * 设置中心
     */
    public static final String H5_SETTING = H5_URL+"settingCenter";
    /**
     * 搜索测量
     */
    public static final String H5_SEARCH_MEASURE = H5_URL+"searchMeasurer";
    /**
     * 选择用户信息
     */
    public static final String H5_SELECT_USER_INFO = H5_URL+"selectedUserInfo";
    /**
     * 设置参数
     */
    public static final String H5_SETTING_PARAMS = H5_URL+"settingParams";
    /**
     * 设置账户
     */
    public static final String H5_SETTING_ACCOUNT = H5_URL+"settingAccount";
    /**
     * 新增测试者
     */
    public static final String H5_ADD_TESTER = H5_URL+"searchMeasurer?from=home";
    /**
     * 新增医生
     */
    public static final String H5_ADD_DOCTOR = H5_URL+"searchDoctor?from=home";
    /**
     * 记录
     */
    public static final String H5_RECORD = H5_URL+"record";
    /**
     * 注册
     */
    public static final String H5_REGISTER = H5_URL+"registerAccount";
    /**
     * 重置密码
     */
    public static final String H5_RESET_PASSWORD = H5_URL+"resetPassword";




}
