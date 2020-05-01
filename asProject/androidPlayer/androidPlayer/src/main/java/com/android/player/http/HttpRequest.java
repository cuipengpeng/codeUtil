package com.android.player.http;

import android.content.Context;
import android.util.Log;

import com.android.player.base.BaseApplication;
import com.android.player.base.BaseUIActivity;
import com.android.player.base.BaseUIFragment;
import com.android.player.base.IBaseView;
import com.android.player.utils.DeviceUtil;
import com.android.player.utils.encrypt.LogUtils;
import com.android.player.utils.encrypt.MD5;
import com.android.player.widget.LoadingAlertDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class HttpRequest {
    public enum Environment {
        TEST, PRE, ONLINE
    }

    public static Environment currentEnv = Environment.TEST;


    //测试环境
    public static final String APP_INTERFACE_WEB_URL_ENV_TEST ="https://www.baidu.com/" ;
    //预发环境
    public static final String APP_INTERFACE_WEB_URL_ENV_PRE ="https://www.baidu.com/";
    // 正式环境
    public static final String APP_INTERFACE_WEB_URL_ENV_ONLINE ="https://www.baidu.com/";

    //图片上传       测试环境
    public static final String UPLOAD_IMAGE_TEST_ENV_WEB_URL = "https://www.baidu.com/";
    //预发环境
    public static final String UPLOAD_IMAGE_PRE_ENV_WEB_URL = UPLOAD_IMAGE_TEST_ENV_WEB_URL;
    //正式环境
    public static final String UPLOAD_IMAGE_ONLINE_ENV_WEB_URL = "https://www.baidu.com/";

    //人脸识别      测试环境
    public static final String FACE_DETECT_TEST_ENV_URL = "https://www.baidu.com/";
    //预发环境
    public static final String FACE_DETECT_PRE_ENV_URL = FACE_DETECT_TEST_ENV_URL;
    //线上环境
    public static final String FACE_DETECT_ONLINE_ENV_URL = "https://www.baidu.com/";


    public static String APP_INTERFACE_WEB_URL = APP_INTERFACE_WEB_URL_ENV_TEST;
    public static String UPLOAD_IMAGE_WEB_URL = UPLOAD_IMAGE_TEST_ENV_WEB_URL;
    public static String FACE_DETECT_WEB_URL = FACE_DETECT_TEST_ENV_URL;

    public static void initEnvironment() {
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

    /**
     * 发送普通文本请求接口
     * @param iBaseView 1.用于token失效，弹出对话框。2.跳转到登陆页面
     * @param url
     * @param params
     * @param responseHandler
     * @return
     */
    public static Call<String> post(IBaseView iBaseView, String url, Map<String, String> params, HttpResponseCallBank responseHandler) {
        return post(iBaseView, url, params, responseHandler, requestReadTime, requestWriteTime, requestConnectTime, "", SHOW_LOADING);
    }

    public static Call<String> post(String url, Map<String, String> params, IBaseView iBaseView, boolean showLoading, HttpResponseCallBank responseHandler) {
        return post(iBaseView, url, params, responseHandler, requestReadTime, requestWriteTime, requestConnectTime, "", showLoading);
    }

    public static Call<String> post(String url, Map<String, String> params, IBaseView iBaseView, String requestChannel, HttpResponseCallBank responseHandler) {
        return post(iBaseView, url, params, responseHandler, requestReadTime, requestWriteTime, requestConnectTime, requestChannel, SHOW_LOADING);
    }

    public static Call<String> post( IBaseView iBaseView, String url, Map<String, String> params, HttpResponseCallBank responseHandler, long readTime, long writeTime, long timeOut,String requestChannel, boolean showLoading) {
        return execute(iBaseView, url, new HashMap<String, String>(), params, false, null, responseHandler, readTime, writeTime, timeOut, requestChannel, showLoading);
    }

    /**
     * 文件上传接口
     *
     * @param iBaseView 1.用于token失效，弹出对话框。2.跳转到登陆页面
     * @param url
     * @param params
     * @param filesMap
     * @param responseHandler
     * @return
     */
    public static Call<String> uploadFiles(IBaseView iBaseView, String url, Map<String, String> params, Map<String, File> filesMap, HttpResponseCallBank responseHandler) {
        return uploadFiles(iBaseView, url, new HashMap<String, String>(), params, filesMap, responseHandler, uploadReadTime, uploadWriteTime, uploadConnectTime, SHOW_LOADING);
    }

    public static Call<String> uploadFiles(IBaseView iBaseView, String url, Map<String, String> headers, Map<String, String> params, Map<String, File> filesMap, HttpResponseCallBank responseHandler, long readTime, long writeTime, long timeOut, boolean showLoading) {
        return execute(iBaseView, url, headers, params, true, filesMap, responseHandler, readTime, writeTime, timeOut, "", showLoading);
    }

    /**
     * http请求回调
     */
    public interface HttpResponseCallBank {
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
    private static Call<String> execute(IBaseView iBaseView, String url, Map<String, String> headers, Map<String, String> params, boolean uploadFile, Map<String, File> uploadFilesMap, HttpResponseCallBank responseHandler, long readTime, long writeTime, long timeOut, String requestChannel, boolean showLoading) {
        params.put("version", DeviceUtil.getAppVersionName(BaseApplication.applicationContext));
        params.put("channelCode", "android");
        params.put("equipment", DeviceUtil.getDevicecId(BaseApplication.applicationContext));
        params.put("mobileSystem", "mobileSystem");
        TreeMap<String, String> treeMap = new TreeMap<String, String>();
        treeMap.putAll(params);
        String signedContent = MD5.sign(treeMap); // Des3.signContent(params.toString());
        LogUtils.printLog("--已签名的内容：" + signedContent);
        params.put("sign", signedContent);
        //retrofit参数为空处理
        for (String key : params.keySet()) {
            if (params.get(key) == null) {
                params.put(key, "");
            }
        }
        LogUtils.printLog("url:" + url + "--请求参数：" + params.toString());

        Call<String> call = null;
        APIService apiService = getApiService(readTime, writeTime, timeOut);
        if (uploadFile) {
            Map<String, RequestBody> filesMap = new HashMap<String, RequestBody>();
            RequestBody requestBody = null;
            for (String keyStr : uploadFilesMap.keySet()) {
                requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), uploadFilesMap.get(keyStr));
                filesMap.put(keyStr, requestBody);
            }
            call = apiService.uploadFileWithText(url, headers, params, filesMap);
        } else {
            call = apiService.getAPiString(url, headers, params);
        }

        LoadingAlertDialog loadingAlertDialog = null;
        if (showLoading) {
            if (iBaseView instanceof BaseUIActivity) {
                loadingAlertDialog = new LoadingAlertDialog(((BaseUIActivity) iBaseView));
            } else if (iBaseView instanceof BaseUIFragment) {
                loadingAlertDialog = new LoadingAlertDialog(((BaseUIFragment) iBaseView).getActivity());
            }
            loadingAlertDialog.show();
        }

        call.enqueue(new AsyncResponseCallBack(responseHandler, iBaseView, requestChannel, loadingAlertDialog));
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
        String baseUrl = HttpRequest.APP_INTERFACE_WEB_URL;
//        String apiServiceKey = baseUrl + readTime + writeTime + timeOut;
        String apiServiceKey = readTime + writeTime + timeOut + "";
        APIService apiService = mapAPIService.get(apiServiceKey);
        if (apiService == null) {//为空，则创建
            synchronized (mapAPIService) { //锁定对象
                OkHttpClient httpClient = new OkHttpClient.Builder()
                        .connectTimeout(timeOut, TimeUnit.SECONDS)
                        .readTimeout(readTime, TimeUnit.SECONDS)
                        .writeTimeout(writeTime, TimeUnit.SECONDS).build();
                //设置日志拦截器
                Retrofit retrofit = new Retrofit.Builder().client(httpClient)
                        .baseUrl(baseUrl)
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
            list = new ArrayList<Call<String>>();
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
     *
     */
    public static final String CURRENT_PLUS = "v1/security/fund_buy.do";

}
