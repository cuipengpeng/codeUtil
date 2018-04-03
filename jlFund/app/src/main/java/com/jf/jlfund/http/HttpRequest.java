package com.jf.jlfund.http;

import android.content.Context;
import android.util.Log;

import com.jf.jlfund.base.IBaseView;
import com.jf.jlfund.utils.ConstantsUtil;
import com.jf.jlfund.utils.DeviceUtil;
import com.jf.jlfund.utils.LogUtils;
import com.jf.jlfund.utils.MD5;
import com.jf.jlfund.weight.dialog.LoadingAlertDialog;

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


    //玖理web服务器      测试环境
//    public static final String JL_FUND_TEST_ENV_WEB_URL = "http://123.57.15.239:8181/jlapp-web/";
//    public static final String JL_FUND_TEST_ENV_WEB_URL = "http://47.95.165.12:8181/jlapp-web/";
//    public static final String JL_FUND_TEST_ENV_WEB_URL = "http://192.168.20.249:8181/jlapp-web/";
//    public static final String JL_FUND_TEST_ENV_WEB_URL = "http://192.168.20.80:8080/jlapp-web/";
    public static final String JL_FUND_TEST_ENV_WEB_URL = HttpConfig.BASE_URL;

    //预发环境
    public static final String JL_FUND_PRE_ENV_WEB_URL = "https://123.56.167.11:8102/app/";
    // 正式环境
    public static final String JL_FUND_ONLINE_ENV_WEB_URL = "https://sc.9fbank.com/";

    //图片上传       测试环境
    public static final String UPLOAD_IMAGE_TEST_ENV_WEB_URL = "http://123.57.48.237:7006/";
    //预发环境
    public static final String UPLOAD_IMAGE_PRE_ENV_WEB_URL = UPLOAD_IMAGE_TEST_ENV_WEB_URL;
    //正式环境
    public static final String UPLOAD_IMAGE_ONLINE_ENV_WEB_URL = "http://ecm.9fbank.com:9006/";

    //人脸识别      测试环境
    public static final String FACE_DETECT_TEST_ENV_URL = "http://112.126.78.148:8098/";
    //预发环境
    public static final String FACE_DETECT_PRE_ENV_URL = FACE_DETECT_TEST_ENV_URL;
    //线上环境
    public static final String FACE_DETECT_ONLINE_ENV_URL = "https://imgverify.9fbank.com/";


    public static String JL_FUND_WEB_URL = JL_FUND_TEST_ENV_WEB_URL;
    public static String UPLOAD_IMAGE_WEB_URL = UPLOAD_IMAGE_TEST_ENV_WEB_URL;
    public static String FACE_DETECT_WEB_URL = FACE_DETECT_TEST_ENV_URL;

    public static void initEnvironment() {
        if (currentEnv == Environment.TEST) {
            JL_FUND_WEB_URL = JL_FUND_TEST_ENV_WEB_URL;
            FACE_DETECT_WEB_URL = FACE_DETECT_TEST_ENV_URL;
            UPLOAD_IMAGE_WEB_URL = UPLOAD_IMAGE_TEST_ENV_WEB_URL;
        } else if (currentEnv == Environment.PRE) {
            JL_FUND_WEB_URL = JL_FUND_PRE_ENV_WEB_URL;
            FACE_DETECT_WEB_URL = FACE_DETECT_PRE_ENV_URL;
            UPLOAD_IMAGE_WEB_URL = UPLOAD_IMAGE_PRE_ENV_WEB_URL;
        } else if (currentEnv == Environment.ONLINE) {
            JL_FUND_WEB_URL = JL_FUND_ONLINE_ENV_WEB_URL;
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
     *
     * @param url
     * @param params
     * @param responseHandler
     * @return
     */
    public static Call<String> post(String url, Map<String, String> params, HttpResponseCallBank responseHandler) {
        return post(url, params, responseHandler, requestReadTime, requestWriteTime, requestConnectTime, null, "", SHOW_LOADING);
    }

    public static Call<String> post(String url, Map<String, String> params, IBaseView iBaseView, HttpResponseCallBank responseHandler) {
        return post(url, params, responseHandler, requestReadTime, requestWriteTime, requestConnectTime, iBaseView, "", SHOW_LOADING);
    }

    public static Call<String> post(String url, Map<String, String> params, IBaseView iBaseView, boolean showLoading, HttpResponseCallBank responseHandler) {
        return post(url, params, responseHandler, requestReadTime, requestWriteTime, requestConnectTime, iBaseView, "", showLoading);
    }

    public static Call<String> post(String url, Map<String, String> params, IBaseView iBaseView, String requestChannel, HttpResponseCallBank responseHandler) {
        return post(url, params, responseHandler, requestReadTime, requestWriteTime, requestConnectTime, iBaseView, requestChannel, SHOW_LOADING);
    }

    public static Call<String> post(String url, Map<String, String> params, HttpResponseCallBank responseHandler, long readTime, long writeTime, long timeOut, IBaseView iBaseView, String requestChannel, boolean showLoading) {
        return execute(url, new HashMap<String, String>(), params, false, null, responseHandler, readTime, writeTime, timeOut, iBaseView, requestChannel, showLoading);
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
    public static Call<String> uploadFiles(String url, Map<String, String> params, Map<String, File> filesMap, HttpResponseCallBank responseHandler) {
        return uploadFiles(url, new HashMap<String, String>(), params, filesMap, responseHandler, uploadReadTime, uploadWriteTime, uploadConnectTime, SHOW_LOADING);
    }

    public static Call<String> uploadFiles(String url, Map<String, String> headers, Map<String, String> params, Map<String, File> filesMap, HttpResponseCallBank responseHandler, long readTime, long writeTime, long timeOut, boolean showLoading) {
        return execute(url, headers, params, true, filesMap, responseHandler, readTime, writeTime, timeOut, null, "", showLoading);
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
    private static Call<String> execute(String url, Map<String, String> headers, Map<String, String> params, boolean uploadFile, Map<String, File> uploadFilesMap, HttpResponseCallBank responseHandler, long readTime, long writeTime, long timeOut, IBaseView iBaseView, String requestChannel, boolean showLoading) {
//        params.put("version", DeviceUtil.getAppVersionName(BaseApplication.applicationContext));
        params.put("version", "1");
        params.put("channelCode", ConstantsUtil.CHANNEL_CODE);
        params.put("equipment", DeviceUtil.getDevicecId());
        params.put("mobileSystem", ConstantsUtil.MOBILE_SYSTEM);
        TreeMap<String, String> treeMap = new TreeMap<String, String>();
        treeMap.putAll(params);
        String signedContent = MD5.sign(treeMap); // Des3.signContent(params.toString());
        LogUtils.printLog("--已签名的内容：" + signedContent);
        params.put("sign", signedContent);
        params = filterEmptyParams(params);
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
            iBaseView.showProgressDialog();
//            if (iBaseView instanceof BaseUIActivity) {
//                loadingAlertDialog = new LoadingAlertDialog(((BaseUIActivity) iBaseView));
//            } else if (iBaseView instanceof BaseUIFragment) {
//                loadingAlertDialog = new LoadingAlertDialog(((BaseUIFragment) iBaseView).getActivity());
//            }
//            loadingAlertDialog.show();
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
        String baseUrl = HttpRequest.JL_FUND_WEB_URL;
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
     * 基金公司信息及旗下基金
     */
    public static final String FUND_COMPANY_INFO = "v1/fund_company.do";

    /**
     * 基金档案附带公告
     */
    public static final String FUND_ARCHIVES_WITH_ANNOUNCEMENT = "v1/fund_record.do";
    /**
     * 单只基金详情
     */
    public static final String SINGLE_FUND_DETAIL = "v1/fund_detail.do";
    /**
     * 单只基金收益率走势
     */
    public static final String SINGLE_FUND_YIELD_TREND = "v1/fund_trend.do";
    /**
     * 基金经理信息
     */
    public static final String FUND_MANAGER_DETAIL = "v1/fund_manager.do";
    /**
     * 基金信息
     */
    public static final String FUND_INFO_DETAIL = "v1/fund_info.do";
    /**
     * 基金公告详情
     */
    public static final String FUND_ANNOUNCEMENT_DETAIL = "v1/fund_notice_detail.do";
    /**
     * 基金费率明细（交易须知）
     */
    public static final String TRADE_NOTICE = "v1/fund_feedetail.do";
    /**
     * 持仓配置
     */
    public static final String POSITION_ALLOCATION = "v1/fund_position_conf.do";
    /**
     * 3.6.2行业配置
     */
    public static final String INDUSTRY_DISTRIBUTION = "v1/fund_industry_conf.do";

    /**
     * 查看单只基金历史净值
     */
    public static final String SINGLE_FUND_NET_VALUE = "v1//fund_nav.do";
    /**
     * 3.3.7基金公告（查看更多）
     */
    public static final String FUND_ANNOUNCEMENT_LIST = "v1/fund_notice.do";
    /**
     * 获取宝宝类资产信息
     */
    public static final String BAOBAO_ASSERT_INFO = "v1/security/acct_baobao.do";
    /**
     * 3.2.11宝宝资产七日年化收益走势
     */
    public static final String SEVEN_DAY_YIELD_LIST = "v1/growthrate_trend.do";
    /**
     * 宝宝类购买记录列表
     */
    public static final String BAOBAO_BUY_RECORD_LIST = "v1/security/baobao_buy_records.do";
    /**
     * 宝宝类购买记录详情
     */
    public static final String BAOBAO_BUY_RECORD_DETAIL = "v1/security/records_detail.do";
    /**
     * 3.3.13获取购买或赎回信息
     */
    public static final String PUT_IN_AND_GET_OUT_BANK_CARD_INFO = "v1/security/get_buy_or_redemption_info.do";
    /**
     * 购买基金
     */
    public static final String CURRENT_PLUS_FUND_BUY = "v1/security/fund_buy.do";
    /**
     * 赎回基金
     */
    public static final String CURRENT_PLUS_FUND_REDEMPTION = "v1/security/fund_redemption.do";
    /**
     * 查看"货期+"七日年化
     */
    public static final String BAOBAO_SEVEN_DAY_YIELD = "v1/baobao_year_yld.do";
    /**
     * 查看"货期+"万份收益
     */
    public static final String BAOBAO_WAN_FEN_INCOME = "v1/baobao_tenthou_unit_incm.do";
    /**
     * 查看"货期+"历史收益
     */
    public static final String BAOBAO_ACCUMULATED_INCOME = "v1/security/baobao_history_incm.do";
    /**
     * 风险评测问题
     */
    public static final String RISK_TEST_QUESTIONS = "v1/security/queryrisk.do";
    /**
     * 提交风险测试答案
     */
    public static final String SUBMIT_RISK_TEST_ANSWERS = "v1/security/modifyrisk.do";
    /**
     *
     */
    public static final String GET_ACCOUNT_INFO = "v1/security/user_acct_info.do";
    /**
     * 获取自选基金列表
     */
    public static final String GET_OPTIONAL_FUND_LIST = "v1/security/personCollection/getCollections.do";
    /**
     * 添加自选
     */
    public static final String ADD_TO_OPTIONAL_FUND = "v1/security/personCollection/addCollection.do";
    /**
     * 删除自选
     */
    public static final String DELETE_OPTIONAL_FUND = "v1/security/personCollection/deleteCollection.do";
    /**
     *
     */
    public static final String FUND_LOGIN = "v1/login.do";
//    /**
//     *
//     */
//    public static final String FUND_ = "v1/";
//    /**
//     *
//     */
//    public static final String FUND_ = "v1/";
//    /**
//     *
//     */
//    public static final String FUND_ = "v1/";
//    /**
//     *
//     */
//    public static final String FUND_ = "v1/";

}
