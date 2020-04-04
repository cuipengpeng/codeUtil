package com.caishi.chaoge.http;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.caishi.chaoge.BuildConfig;
import com.caishi.chaoge.base.BaseUIActivity;
import com.caishi.chaoge.base.BaseUIFragment;
import com.caishi.chaoge.base.IBaseView;
import com.caishi.chaoge.ui.widget.LoadingAlertDialog;
import com.caishi.chaoge.utils.LogInterceptor;
import com.caishi.chaoge.utils.LogUtil;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.caishi.chaoge.utils.ConstantUtils.LOGIN_DOMAIN;


public class HttpRequest {
    //测试环境
    private static final String APP_INTERFACE_WEB_URL_ENV_TEST = RequestURL.BASE_URL + "/";
    //预发环境
    private static final String APP_INTERFACE_WEB_URL_ENV_PRE = RequestURL.BASE_URL +"/";
    // 正式环境
    private static final String APP_INTERFACE_WEB_URL_ENV_ONLINE = RequestURL.BASE_URL + "/";

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
        if (BuildConfig.DEBUG) {
            currentEnv = Environment.TEST;
        } else {
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
    private static long requestConnectTime = 8;

    private static long uploadReadTime = 20;
    private static long uploadWriteTime = 20;
    private static long uploadConnectTime = 20;


    private static final boolean SHOW_LOADING = true;

    /**
     * 发送普通文本请求接口
     *
     * @param url
     * @param params
     * @param responseHandler
     * @return
     */
    public static Call<String> post(boolean getRequest, String url, Map<String, String> params, HttpResponseCallBank responseHandler) {
        return post(getRequest, url, params, responseHandler, requestReadTime, requestWriteTime, requestConnectTime, null, "", SHOW_LOADING);
    }

    public static Call<String> post(boolean getRequest, String url, Map<String, String> params, IBaseView iBaseView, HttpResponseCallBank responseHandler) {
        return post(getRequest, url, params, responseHandler, requestReadTime, requestWriteTime, requestConnectTime, iBaseView, "", SHOW_LOADING);
    }

    public static Call<String> post(boolean getRequest, String url, Map<String, String> params, IBaseView iBaseView, boolean showLoading, HttpResponseCallBank responseHandler) {
        return post(getRequest, url, params, responseHandler, requestReadTime, requestWriteTime, requestConnectTime, iBaseView, "", showLoading);
    }

    public static Call<String> post(boolean getRequest, String url, Map<String, String> params, IBaseView iBaseView, String requestChannel, HttpResponseCallBank responseHandler) {
        return post(getRequest, url, params, responseHandler, requestReadTime, requestWriteTime, requestConnectTime, iBaseView, requestChannel, SHOW_LOADING);
    }

    public static Call<String> post(boolean getRequest, String url, Map<String, String> params, HttpResponseCallBank responseHandler, long readTime, long writeTime, long timeOut, IBaseView iBaseView, String requestChannel, boolean showLoading) {
        return execute(getRequest, url, new HashMap<String, String>(), params, false, null, responseHandler, readTime, writeTime, timeOut, iBaseView, requestChannel, showLoading);
    }

    /**
     * 文件上传接口
     *
     * @param url
     * @param params
     * @param filesMap
     * @param responseHandler
     * @return
     */
    public static void uploadFiles(String url, Map<String, String> params, Map<String, File> filesMap, HttpResponseCallBank responseHandler) {
        uploadFiles(url, new HashMap<String, String>(), params, filesMap, responseHandler, uploadReadTime, uploadWriteTime, uploadConnectTime, SHOW_LOADING);
    }

    public static void uploadFiles(final String url, final Map<String, String> headers, final Map<String, String> params, final Map<String, File> filesMap, final HttpResponseCallBank responseHandler, final long readTime, final long writeTime, final long timeOut, final boolean showLoading) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                execute(false, url, headers, params, true, filesMap, responseHandler, readTime, writeTime, timeOut, null, "", showLoading);
            }
        }).start();

    }

    /**
     * http请求回调
     */
    public interface HttpResponseCallBank {
        void onSuccess(String response);

        void onFailure(String t);
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
    private static Call<String> execute(boolean getRequest, String url, Map<String, String> headers, Map<String, String> params, boolean uploadFile, Map<String, File> uploadFilesMap, HttpResponseCallBank responseHandler, long readTime, long writeTime, long timeOut, IBaseView iBaseView, String requestChannel, boolean showLoading) {
//        params.put("version", DeviceUtil.getAppVersionName(BaseApplication.applicationContext));
//        params.put("channelCode", "android");
//        params.put("equipment", DeviceUtil.getDevicecId());
//        TreeMap<String, String> treeMap = new TreeMap<String, String>();
//        treeMap.putAll(params);
//        String signedContent = MD5Util.sign(treeMap); // Des3.signContent(params.toString());
//        LogUtil.printLog("--已签名的内容：" + signedContent);
//        params.put("sign", signedContent);
        params = filterEmptyParams(params);
        LogUtil.printLog("url= " + url + "--请求参数：" + params.toString());

        Call<String> call = null;
        RequestApi apiService = getApiService(readTime, writeTime, timeOut);
//        RequestApi apiService = RetrofitFactory.getInstance();
        if (uploadFile) {
            Map<String, RequestBody> filesMap = new HashMap<String, RequestBody>();
            RequestBody requestBody = null;
            for (String keyStr : uploadFilesMap.keySet()) {
                requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), uploadFilesMap.get(keyStr));
                filesMap.put(keyStr, requestBody);
            }
            call = apiService.uploadFileWithText(url, headers, params, filesMap);
        } else {
            if (getRequest) {
                call = apiService.getAPiString(url, headers, params);
            } else {
                //格式: application/x-www-form-urlencoded
//              if(headers==null){
//                 headers = new HashMap<>();
//              }
//              headers.put("Content-Type","application/x-www-form-urlencoded; charset=utf-8");
                call = apiService.postFormAPiString(url, headers, params);


                //格式: application/json
//                RequestBody body = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(params));
//                call = apiService.postJsonAPiString(url, headers, body);
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

    private static Map<String, RequestApi> mapAPIService = new HashMap<>();

    /**
     * * 获取APIService
     *
     * @param readTime  请求时间
     * @param writeTime 读取数据时间
     * @param timeOut   连接超时时间
     * @return
     */
    private static RequestApi getApiService(long readTime, long writeTime, long timeOut) {
        String baseUrl = HttpRequest.APP_INTERFACE_WEB_URL;
        String apiServiceKey = readTime + writeTime + timeOut + "";
        RequestApi apiService = mapAPIService.get(apiServiceKey);
        if (apiService == null) {//为空，则创建
            synchronized (mapAPIService) { //锁定对象
                OkHttpClient httpClient = new OkHttpClient.Builder()
                        // 添加通用的Header
                        .addInterceptor(new Interceptor() {
                            @Override
                            public okhttp3.Response intercept(Chain chain) throws IOException {
                                String host = chain.request().url().host();
                                String path = null;
                                if (TextUtils.equals(LOGIN_DOMAIN, host)) {
                                    path = chain.request().url().uri().getPath();
                                }
                                Request.Builder builder = chain.request().newBuilder();
                                builder.addHeader("User-Agent", UserAgent.formatAgent(path))
                                        .addHeader("userId", Account.sUserId)
                                        .addHeader("Source", "ChaoGe");
                                return chain.proceed(builder.build());
                            }
                        })
                        .connectTimeout(timeOut, TimeUnit.SECONDS)
                        .readTimeout(readTime, TimeUnit.SECONDS)
                        .writeTimeout(writeTime, TimeUnit.SECONDS)
                        .addInterceptor(new LogInterceptor())
                        .build();




                //设置日志拦截器
                Retrofit retrofit = new Retrofit.Builder().client(httpClient)
                        .baseUrl(baseUrl)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .build();
                apiService = retrofit.create(RequestApi.class);
                RequestApi mApiService = mapAPIService.get(apiServiceKey);//再次获取mapAPIService中的APIService
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

}
