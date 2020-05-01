package com.jfbank.qualitymarket.net;

import android.content.Context;
import android.util.Log;

import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.Des3;
import com.jfbank.qualitymarket.util.LogUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

/**
 * POST　GET 请求分装
 */
public class HttpRequest {
    public enum Environment {
        TEST, PRE, ONLINE
    }

    public static Environment currentEnv = Environment.ONLINE;


    //测试环境
    public static final String MARKET_TEST_ENV_WEB_URL = "https://www.baidu.com/";
    //预发环境
    public static final String MARKET_PRE_ENV_WEB_URL = "https://www.baidu.com/";
    // 正式环境
    public static final String MARKET_ONLINE_ENV_WEB_URL = "https://www.baidu.com/";

    //测试环境
    public static final String CAR_TEST_ENV_WEB_URL = "https://www.baidu.com/";
    //预发环境
    public static final String CAR_PRE_ENV_WEB_URL = CAR_TEST_ENV_WEB_URL;
    // 正式环境
    public static final String CAR_ONLINE_ENV_WEB_URL = "https://www.baidu.com/";

    public static final String NE_TEST_ENV_WEB_URL = "https://www.baidu.com/";
    public static final String NE_PRE_ENV_WEB_URL = NE_TEST_ENV_WEB_URL;
    public static final String NE_ONLINE_ENV_WEB_URL = "https://www.baidu.com/";

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

    public static final String H5_PAGE_SUB_URL = "";
    public static final String H5_CAR_PAGE_SUB_URL = "";


    public static String QUALITY_MARKET_WEB_URL = MARKET_TEST_ENV_WEB_URL;
    public static String CAR_WEB_URL = CAR_TEST_ENV_WEB_URL;
    public static String CAR_API_URL = CAR_TEST_ENV_WEB_URL;
    public static String UPLOAD_IMAGE_WEB_URL = UPLOAD_IMAGE_TEST_ENV_WEB_URL;
    public static String FACE_DETECT_WEB_URL = FACE_DETECT_TEST_ENV_URL;

    public static void initEnvironment() {
        if (currentEnv == Environment.TEST) {
            QUALITY_MARKET_WEB_URL = MARKET_TEST_ENV_WEB_URL;
            CAR_WEB_URL = CAR_TEST_ENV_WEB_URL;
            FACE_DETECT_WEB_URL = FACE_DETECT_TEST_ENV_URL;
            CAR_API_URL = NE_TEST_ENV_WEB_URL;
            UPLOAD_IMAGE_WEB_URL = UPLOAD_IMAGE_TEST_ENV_WEB_URL;
        } else if (currentEnv == Environment.PRE) {
            QUALITY_MARKET_WEB_URL = MARKET_PRE_ENV_WEB_URL;
            CAR_WEB_URL = CAR_PRE_ENV_WEB_URL;
            FACE_DETECT_WEB_URL = FACE_DETECT_PRE_ENV_URL;
            CAR_API_URL = NE_PRE_ENV_WEB_URL;
            UPLOAD_IMAGE_WEB_URL = UPLOAD_IMAGE_PRE_ENV_WEB_URL;
        } else if (currentEnv == Environment.ONLINE) {
            QUALITY_MARKET_WEB_URL = MARKET_ONLINE_ENV_WEB_URL;
            CAR_WEB_URL = CAR_ONLINE_ENV_WEB_URL;
            CAR_API_URL = NE_ONLINE_ENV_WEB_URL;
            FACE_DETECT_WEB_URL = FACE_DETECT_ONLINE_ENV_URL;
            UPLOAD_IMAGE_WEB_URL = UPLOAD_IMAGE_ONLINE_ENV_WEB_URL;
        }
    }

    /**
     * 网络请求接口
     *
     * @param context         上下文
     * @param url             请求地址
     * @param params          请求参数
     * @param responseHandler 请求返回回调
     * @return
     */
    public static Call<String> post(Context context, String url, Map<String, String> params, AsyncResponseCallBack responseHandler) {
        return post(context, 3, 3, 3, url, params, responseHandler);
    }

    /**
     * 网络请求接口
     *
     * @param context         上下文
     * @param url             请求地址
     * @param params          请求参数
     * @param responseHandler 请求返回回调
     * @return
     */
    public static Call<String> post(Context context, long readTime, long writeTime, long timeOut, String url, Map<String, String> params, AsyncResponseCallBack responseHandler) {
        params.put("ver", AppContext.getAppVersionName(AppContext.mContext));
        params.put("Plat", ConstantsUtil.PLAT);
        params.put("timestamp", new Date().getTime() + "");
        params.put("key", ConstantsUtil.SIGNED_KEY);
        String signedContent = Des3.signContent(params.toString());
        LogUtil.printLog("--已签名的内容：" + signedContent);
        params.put("signed", signedContent);
        params = NetUtil.filterEmptyParams(params);
        LogUtil.printLog("url:" + url + "--请求参数：" + params.toString());
        Call<String> call = NetUtil.getApiService(readTime, writeTime, timeOut).getAPiString(url, params);
        call.enqueue(responseHandler);
        addCall(context, call);
        return call;
    }

    private static Map<Context, List<Call<String>>> callManager = new HashMap<>();

    /**
     * 添加标记网络请求。
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
    public static void cancelRequestNet(Context context) {
        if (context != null) {
            List<Call<String>> list = callManager.get(context);
            if (!CommonUtils.isEmptyList(list)) {
                for (int i = 0; i < list.size(); i++) {
                    Call<String> call = list.get(i);
                    if (!call.isCanceled()) {
                        call.cancel();
                        Log.e("cancel", call.request().url().toString());
                    }
                }
                callManager.remove(context);
            }
        }
    }

    /**
     * 上传图片（header和Files）
     *
     * @param context
     * @param url             地址
     * @param headerParams    header參數
     * @param filesParams     文件路徑
     * @param responseHandler 请求回调
     * @return
     */
    public static Call<String> uploadFileWithHeader(Context context, String url, Map<String, String> headerParams, Map<String, RequestBody> filesParams, AsyncResponseCallBack responseHandler) {
        headerParams = NetUtil.filterEmptyParams(headerParams);
        Call<String> call = NetUtil.getApiService(60, 60, 60).uploadFileWithHeader(url, headerParams, filesParams);
        call.enqueue(responseHandler);
        addCall(context, call);
        return call;
    }

    /**
     * 上传文件接口
     *
     * @param context         上下文
     * @param url             请求地址
     * @param params          参数
     * @param files           文件
     * @param responseHandler 回调函数
     * @return
     */
    public static Call<String> uploadFileWithText(Context context, String url, Map<String, String> params, Map<String, RequestBody> files, AsyncResponseCallBack responseHandler) {
        return uploadFileWithText(context, 60, 60, 60, url, params, files, responseHandler);
    }

    /**
     * 上传文件接口
     *
     * @param context         上下文
     * @param readTime
     * @param writeTime
     * @param timeOut         超時
     * @param url             请求地址
     * @param params          参数
     * @param files           文件
     * @param responseHandler 回调函数
     * @return
     */
    public static Call<String> uploadFileWithText(Context context, long readTime, long writeTime, long timeOut, String url, Map<String, String> params, Map<String, RequestBody> files, AsyncResponseCallBack responseHandler) {
        params.put("ver", AppContext.getAppVersionName(AppContext.mContext));
        params.put("Plat", ConstantsUtil.PLAT);
        params.put("timestamp", new Date().getTime() + "");
        params.put("key", ConstantsUtil.SIGNED_KEY);
        String signedContent = Des3.signContent(params.toString());
        LogUtil.printLog("--已签名的内容：" + signedContent);
        params.put("signed", signedContent);
        LogUtil.printLog("url:" + url + "--请求参数：" + params.toString());
        params = NetUtil.filterEmptyParams(params);
        Call<String> call = NetUtil.getApiService(readTime, writeTime, timeOut).uploadFileWithText(url, params, files);
        call.enqueue(responseHandler);
        addCall(context, call);
        return call;
    }

    public static Call<String> uploadOneFileWithText(Context context, String url, Map<String, String> params, MultipartBody.Part file, AsyncResponseCallBack responseHandler) {
        params.put("ver", AppContext.getAppVersionName(AppContext.mContext));
        params.put("Plat", ConstantsUtil.PLAT);
        params.put("timestamp", new Date().getTime() + "");
        params.put("key", ConstantsUtil.SIGNED_KEY);
        String signedContent = Des3.signContent(params.toString());
        LogUtil.printLog("--已签名的内容：" + signedContent);
        params.put("signed", signedContent);
        LogUtil.printLog("url:" + url + "--请求参数：" + params.toString());
        params = NetUtil.filterEmptyParams(params);
        Call<String> call = NetUtil.getApiService(60, 60, 60).uploadOneFileWithText(url, params, file);
        call.enqueue(responseHandler);
        addCall(context, call);
        return call;
    }

    public static final String ONCARD_API_FACE_IDENTIFY = "api/face/identify?type=sampleimg";//万卡人脸识别
    /**
     * 用户登录
     */
    public static final String USER_LOGIN = "mall/LoginDes";

    /**
     * 启动页广告
     */
    public static final String APP_START_AD = "mall/QueryLogHome";

    /**
     * 第三期首页地址
     */
    public static final String HOME_PAGE = "mall/index";
    /**
     * 获取验证码
     */
    public static final String SEND_VERIFY_CODE = "mall/SendValidCode";
    /**
     * 用户注册
     */
    public static final String REGISTER_USER = "mall/Regist";
    /**
     * 用户退出
     */
    public static final String USER_LOGOUT = "mall/Logout";
    /**
     * 获取图片验证码
     */
    public static final String GET_IMAGE_VERIFY_CODE = "oss/downloadverifyCode.do";
    /**
     * 找回密码
     */
    public static final String FIND_PASSWORD = "mall/FindPwd";
    /**
     * 查询订单列表
     */
    public static final String GET_ALL_ORDERS = "mall/QueryOrderList";
    /**
     * 查询发现列表
     */
    public static final String GET_DISCOVER_LIST = "mall/QueryDiscoveryBanner";
    /**
     * 查询订单详情
     */
    public static final String GET_ORDER_DETAIL = "mall/QueryOrderDetails";
    /**
     * 取消订单
     */
    public static final String CANCEL_ORDER = "mall/cancelorder";
    /**
     * 设置账单日
     */
    public static final String SET_BILLING_DATE = "mall/setbilldate";
    /**
     * 验证万卡交易密码
     */
    public static final String VALIDATE_ONE_CARD_TRADE_PASSWORD = "mall/VerifyPwd";
    /**
     * 老授信接口
     * 查询信用额度--品质
     */
//    public static final String GET_CREDIT_LINES = "mall/Creditline4MyPage";
    public static final String GET_CREDIT_LINES = "mall/Creditline4MyPageNew";
    /**
     * 查询用户信息接口
     */
    public static final String QUALITYUSERSHOW = "mall/QualityUserShow";

    /**
     * 查询红包个数，账单日，本期待还
     */
    public static final String GET_RED_PACKET = "mall/qualityuserextendshow";
    /**
     * 获取个人中心底部功能菜单
     */
    public static final String GET_BOTTOM_FUNCTION_MENU = "mall/PersonalCenterApi";
    /**
     * 添加订单
     */
    public static final String ADD_ORDER = "mall/QualityOrderAdd";
    /**
     * 手机充值下单接口
     */
    public static final String ADD_VIRTUAL_ORDER = "mall/QualityVirtualOrderAdd";
    /**
     * 会员充值下单接口
     */
    public static final String ADD_HOTMEMBER_ORDER = "mall/QualityHotMemberOrderAdd";
    /**
     * 查询各类订单数
     */
    public static final String GET_ORDER_COUNT = "mall/querycountorder";
    /**
     * 支付订单
     */
    public static final String PAY_ORDER = "mall/qualitychargeorderdetailadd";
    /**
     * 立即支付分期账单
     */
    public static final String PAY_INSTALMENT_BILL = "mall/ImmediateRepayment";
    /**
     * 搜索商品
     */
    public static final String SEARCH_GOODS = "mall/productsearch";
    /**
     * 品类查看
     */
    public static final String PRODUCT_CATEGORY = "mall/CategoryProduct";
    /**
     * 查询信用额度是否够用
     */
//    public static final String CHECK_CREDIT_LINES = "mall/isornotcredit";
    public static final String CHECK_CREDIT_LINES = "mall/IsOrNotCreditNew";
    /**
     * 查询银行卡列表
     */
//    public static final String GET_BANK_CARD_LIST = "mall/bankcardshow";
    public static final String GET_BANK_CARD_LIST = "mall/BankCardShowNew";
    /**
     * 获取产品详情
     */
    public static final String GET_PRODUCT_DETAIL = "mall/productdetailshow";
    /**
     * 虚拟商品详情展示接口
     */
    public static final String GET_VIRTUALPRODUCT_DETAIL_SHOW = "mall/VirtualProductDetailShow";
    /**
     * 搜索热词自动补全
     */
    public static final String GET_AUTOMATIC_COMPLETION = "mall/AutomaticCompletion";
    /**
     * 热门搜索词汇
     */
    public static final String GET_SEARCH_HOT_WORD = "mall/SearchHotWord";
    /**
     * 我的红包
     */
    public static final String GET_MYCOUPONSHOW = "mall/MyCouponShow";
    /**
     * 7.1查询未使用红包（下单）
     */
    public static final String GET_NOTUSEREDBAG = "mall/QualityQueryNotUseRedBag";
    /**
     * 添加银行卡
     */
    public static final String ADD_BANK_CARD = "mall/QualityOrderAdd";
    /**
     * 是否显示借钱花
     */
    public static final String BORROWMONEYSHOW = "mall/borrowmoneyshow";
    /**
     * 检查版本更新
     */
    public static final String CHECK_VERSION_UPDATE = "mall/GetVersion";
    /**
     * 易宝发送短信验证码
     */
    public static final String YIBAO_SMS_VERIFY_CODE = "mall/YiBaobindCard";
    /**
     * 易宝绑定银行卡
     */
    public static final String YIBAO_BIND_BANK_CARD = "mall/YiBaoConfirmCard";
    /**
     * 微支付绑定银行卡
     */
    public static final String WEIZHIFU_BIND_BANK_CARD = "mall/WeiPayBindCard";


    /**
     * 查询收货地址接口
     */
    public static final String QUERY_RECEIPT_ADDRESS = "mall/QueryReceiptAddress";
    /**
     * 保存基本信息
     */
    public static final String SAVE_BASE_INFO = "mall/SavePersonalInfo";
    /**
     * 删除收货地址接口
     */
    public static final String DEL_RECEIPT_ADDRESS = "mall/DelReceiptAddress";
    /**
     * 6.1	为填写基本信息准备数据
     */
    public static final String PREPARE_DATA_CREDITLINE = "mall/PreData4PersonalInfo";
    /**
     * 新增收货地址接口
     */
    public static final String ADD_RECEIPT_ADDRESS = "mall/AddReceiptAddress";
    /**
     * 4.16	收货地址地区查询
     */
    public static final String SET_RECEIVING_ADDRESS = "mall/SetReceivingAddress";
    /**
     * 6.2	根据父区域Code查询区域列表
     */
    public static final String QUERY_AREAS_BY_PARENT_CODE = "mall/QueryAreasByParentCode";
    /**
     * 6.3	根据省、市Code获得工作单位列表
     */
    public static final String GET_CONSUMER_COM4CREDIT_LINE = "mall/GetConsumerCom4creditline";
    /**
     * 6.7	手机验证
     */
    public static final String CHECK_MOBILE = "mall/CheckMobile4creditline";
    /**
     * 实名认证提交接口
     */
    public static final String CHECK_IDENTIFY4CREDIT = "mall/SaveRealNameInfoFace";  //人脸识别SaveRealNameInfoFace  SaveRealNameInfo
    /**
     * 实名信息获取接口
     */
    public static final String GET_REAL_NAME_INFO = "mall/GetRealNameInfo";  //
    /**
     * 查询账单详情
     **/
    public static final String QUERYINSTALLMENT_DETAIL = "mall/QueryInstallmentDetail";
    /**
     * 查询分期还款账单接口
     */
    public static final String QUERY_BILL_INSTALMENT = "mall/QueryBillInstallmentList";
    /**
     * 查询全部分期还款账单接口
     */
    public static final String QUERY_ALL_BILL_INSTALMENT = "mall/QueryRepayRecord";
    /**
     * 6.5	社保验证—为社保验证准备数据
     */
    public static final String PRE_CHECK_SECURITY = "mall/PreCheckSecurity4creditline";
    /**
     * 6.6	社保验证—提交验证
     */
    public static final String CHECK_SECURITY = "mall/CheckSecurity4creditline";
    /**
     * 6.8	手机验证—重新获取验证码
     */
    public static final String CHECK_MOBILE_GET_CODE = "mall/CheckMobileGetCode4creditline";
    /**
     * 6.9	手机验证—提交验证码
     */
    public static final String CHECK_MOBILE_SUBMIT_CODE = "mall/CheckMobileSubmitCode4creditline";
    /**
     * 设置默认地址
     */
    public static final String UPDATE_ADD_DEFAULT = "mall/UpdateAddDefault";
    /**
     * 查询物流
     */
    public static final String QUALITYORDER_TRACK = "mall/qualityordertrack";
    /**
     * 通用参数接口
     */
    public static final String QUALITY_PARAMETER_SHOW = "mall/QualityParameterShow";
    /**
     * 我的认证接口
     */
    public static final String MY_AUTHENTICATION = "mall/MyAuthentication";
    /**
     * 首页接口
     */
    public static final String HOME_PAGE_INDEX = "mall/IndexHome";//IndexHome  //index
    /**
     * 修改未读消息状态接口
     */
    public static final String UPDATEQUALITYUSERMESSAGE = "mall/UpdateQualityUserMessage";
    /**
     * 未读消息条数接口
     */
    public static final String USERMESSAGEUNREAD = "mall/UserMessageUnRead";
    /**
     * 首页秒杀接口
     */
    public static final String ACTIVITYPRODUCTSHOW = "mall/ActivityProductShow";
    /**
     * 退换货进度查询详情
     */
    public static final String GOODS_REJECTED_QUERY_SCHEDULE_DETAIL = "mall/QueryScheduleDetail";
    /**
     * 修改退货申请
     */
    public static final String MODIFY_GOODS_REJECTED_APPLY = "mall/ModifyReturns";
    /**
     * 取消退货申请
     */
    public static final String CANCEL_GOODS_REJECTED_APPLY = "mall/CancelApply";
    /**
     * 查询消息条数接口
     */
    public static final String QUALITYUSERMESSAGESHOW = "mall/QualityUserMessageShow";
    /**
     * 查询社保省份
     */
    public static final String QUERY_SOCIAL_SECURITY_PROVINCE = "mall/PreCheckSecurity4creditline";
    /**
     * 查询社保城市
     */
    public static final String QUERY_SOCIAL_SECURITY_CITY = "mall/QueryAreasByParentCode";
    /**
     * 查询芝麻授信
     */
    public static final String QUERY_ZHIMA_CREDIT = "mall/GetZhimaUrl";
    /**
     * 芝麻授信状态查询接口
     */
    public static final String QUERY_ZHIMA_CREDIT_STATUS = "mall/QueryZhimaNewAuthStatus";

    public static final String UPLOAD_ATTACT_URL = "oss/upload.do"; // 上传图片值OSS/oss/upload.do  ossFile/uploadAD.do
    public static final String CONTACT = "mall/Contact"; // 上传图片值OSS/oss/upload.do  ossFile/uploadAD.do

    public static final String DOWNLOAD_URL = "oss/download.do?uuid="; // 下载显示

    public static final String UPLOAD_FACE_URL = "img/verify/upload"; // 上传活体检测图片
    // 活体采集图片与公安部图片对比
    public static final String COMPAREMPS_URL = "img/verify/comparemps";
    // 获取活体检测对比结果分值
    public static final String GET_SCORT_URL = "img/verify/getConfidence";
    /**
     * 上传对比分值
     */
    public static final String UPLOAD_SCORE_URL = "mall/QualityFaceRecognitionAdd";
    /**
     * 品类
     */
    public static final String GET_CATEGORY_URL = "mall/QualityCategoryShowTag";

    /**
     * 申请售后服务-售后申请
     */
    public static final String GET_QUERYAFTERSALES = "mall/QueryAfterSales";
    /**
     * 申请售后服务-进度查询列表
     */
    public static final String GET_QUERYSCHEDULE = "mall/QuerySchedule";
    /**
     * ：申请售后服务-申请售后服务校验
     */
    public static final String RETURN_GOODSAPPLYCHECK = "mall/ReturnGoodsApplyCheck";
    /**
     * ：申请售后服务-取消售后申请
     */
    public static final String CANCELAPPLY = "mall/CancelApply";
    /**
     * ：申请售后服务-退换货提交申请
     */
    public static final String RETURNED_GOODS_SUMITAPPLY = "mall/ReturnedGoodsSubmitApply";
    /**
     * ：申请售后服务-14.7提交物流申请
     */
    public static final String RETURNED_SUBMITLOGISTICS = "mall/SubmitLogistics";

    //    商品详情页：
    public static final String H5_PAGE_GOODS_DETAIL = "#/detail";
    //    支持银行卡列表：
    public static final String H5_PAGE_SUPPORT_BANK_CARD = "#/supportBankList";
    //    分期账单：
    public static final String H5_PAGE_INSTALLMENT_BILL = "#/installmentbill";
    //    还款计划：
    public static final String H5_PAGE_REPAYMENT_PLAN = "#/repaymentplan";
    //    常见问题：
    public static final String H5_PAGE_COMMON_PROBLEM = "#/problem";
    //    红包说明：
    public static final String H5_PAGE_RED_PACKET_DESC = "#/totalactivityRule";
    //    查看合同：
    public static final String H5_PAGE_VIEW_AGREEMENT = "#/contractlist";
    //    客服：
    public static final String H5_PAGE_CUSTOMER_SERVICE = "#/contactCS";
    //    订单协议：
    public static final String H5_PAGE_ORDER_PROTOCOL = "#/protocolList";
    //    关于：
    public static final String H5_PAGE_ABOUT = "#/about";
    //    充值中心：
    public static final String H5_PAGE_CHARGE_CENTER = "#/virtualDetail";

    //    1、实名 （用户中心未实名，万卡也未实名） ---新用户
    public static final String H5_ONE_CARD_PAGE_REAL_NAME = "#/realName";
    //    2、身份校验 （用户中心实名，万卡没有实名）---存量用户
    public static final String H5_ONE_CARD_PAGE_IDENTITY_CHECK = "#/validateIdentity";
    //    3、已经实名 但是没有设置交易密码
    public static final String H5_ONE_CARD_PAGE_SET_TRATE_PASSWORD = "#/setDealPw";
    //    4、已经实名设置过交易密码，即已经开卡成功，去提额：
    public static final String H5_ONE_CARD_PAGE_PROMOT_QUOTA = "#/quota";
    //            5、忘记交易密码
    public static final String H5_ONE_CARD_PAGE_FORGET_TRATE_PASSWORD = "#/resetDealPw";

    /**
     * 查询收货地址
     */
    public static void queryReceiptAddress(Context mContext, Map<String, String> params, AsyncResponseCallBack responseHandler) {
        post(mContext, QUALITY_MARKET_WEB_URL + QUERY_RECEIPT_ADDRESS, params, responseHandler);
    }

    /**
     * 保存基本信息
     */
    public static void saveBaseInfo(Context mContext, Map<String, String> params, AsyncResponseCallBack responseHandler) {
        post(mContext, QUALITY_MARKET_WEB_URL + SAVE_BASE_INFO, params, responseHandler);
    }

    /**
     * 删除收货地址接口
     */
    public static void deleteReceiptAddress(Context mContext, Map<String, String> params, AsyncResponseCallBack responseHandler) {
        post(mContext, QUALITY_MARKET_WEB_URL + DEL_RECEIPT_ADDRESS, params, responseHandler);
    }

    /**
     * 6.1	为填写基本信息准备数据
     */
    public static void PrepareData4base4creditline(Context mContext, Map<String, String> params, AsyncResponseCallBack responseHandler) {
        post(mContext, QUALITY_MARKET_WEB_URL + PREPARE_DATA_CREDITLINE, params, responseHandler);
    }

    /**
     * 新增收货地址接口
     */
    public static void addReceiptAddress(Context mContext, Map<String, String> params, AsyncResponseCallBack responseHandler) {
        post(mContext, QUALITY_MARKET_WEB_URL + ADD_RECEIPT_ADDRESS, params, responseHandler);
    }

    /**
     * 4.16	收货地址地区查询
     */
    public static void setReceivingAddress(Context mContext, Map<String, String> params, AsyncResponseCallBack responseHandler) {
        post(mContext, QUALITY_MARKET_WEB_URL + SET_RECEIVING_ADDRESS, params, responseHandler);
    }

    /**
     * 6.2	根据父区域Code查询区域列表
     */
    public static void queryAreasByParentCode(Context mContext, Map<String, String> params, AsyncResponseCallBack responseHandler) {
        post(mContext, QUALITY_MARKET_WEB_URL + QUERY_AREAS_BY_PARENT_CODE, params, responseHandler);
    }

    /**
     * 6.3	根据省、市Code获得工作单位列表
     */
    public static void getConsumerCom4creditline(Context mContext, Map<String, String> params, AsyncResponseCallBack responseHandler) {
        post(mContext, QUALITY_MARKET_WEB_URL + GET_CONSUMER_COM4CREDIT_LINE, params, responseHandler);
    }

    /**
     * 6.7	手机验证
     */
    public static void checkPhone(Context mContext, Map<String, String> params, AsyncResponseCallBack responseHandler) {
        post(mContext, 60, 60, 60, QUALITY_MARKET_WEB_URL + CHECK_MOBILE, params, responseHandler);
    }

    /**
     * 实名认证提交接口
     */
    public static void checkIdentify4creditline(Context mContext, Map<String, String> params, AsyncResponseCallBack responseHandler) {

        post(mContext, QUALITY_MARKET_WEB_URL + CHECK_IDENTIFY4CREDIT, params, responseHandler);
    }

    /**
     * 实名信息获取接口
     */
    public static void getRealNameInfo(Context mContext, Map<String, String> params, AsyncResponseCallBack responseHandler) {
        post(mContext, QUALITY_MARKET_WEB_URL + GET_REAL_NAME_INFO, params, responseHandler);
    }

    /**
     * 6.5	社保验证—为社保验证准备数据
     */
    public static void preCheckSecurity4creditline(Context mContext, Map<String, String> params, AsyncResponseCallBack responseHandler) {
        post(mContext, QUALITY_MARKET_WEB_URL + PRE_CHECK_SECURITY, params, responseHandler);
    }

    /**
     * 6.6	社保验证—提交验证
     */
    public static void checkSecurity4creditline(Context mContext, Map<String, String> params, AsyncResponseCallBack responseHandler) {
        post(mContext, QUALITY_MARKET_WEB_URL + CHECK_SECURITY, params, responseHandler);
    }

    /**
     * 6.8	手机验证—重新获取验证码
     */
    public static void getMobileCode(Context mContext, Map<String, String> params, AsyncResponseCallBack responseHandler) {
        post(mContext, 60, 60, 60, QUALITY_MARKET_WEB_URL + CHECK_MOBILE, params, responseHandler);
    }

    /**
     * 6.9	手机验证—提交验证码
     */
    public static void submitMobileCode(Context mContext, Map<String, String> params, AsyncResponseCallBack responseHandler) {
        post(mContext, QUALITY_MARKET_WEB_URL + CHECK_MOBILE_SUBMIT_CODE, params, responseHandler);
    }

    /**
     * 设置默认地址
     */
    public static void updateAddDefault(Context mContext, Map<String, String> params, AsyncResponseCallBack responseHandler) {
        post(mContext, QUALITY_MARKET_WEB_URL + UPDATE_ADD_DEFAULT, params, responseHandler);
    }

    /**
     * 上传图片
     */
    public static void updateBitmap(Context mContext, Map<String, String> params, AsyncResponseCallBack responseHandler) {
        post(mContext, QUALITY_MARKET_WEB_URL + UPLOAD_ATTACT_URL, params, responseHandler);//UPLOAD_IMAGE_TEST_ENV_WEB_URL
    }

    /**
     * 查询物流
     */
    public static void qualityordertrack(Context mContext, Map<String, String> params, AsyncResponseCallBack responseHandler) {
        post(mContext, QUALITY_MARKET_WEB_URL + QUALITYORDER_TRACK, params, responseHandler);//UPLOAD_IMAGE_TEST_ENV_WEB_URL
    }

    /**
     * 上传通讯录
     */
    public static void uploadContacts(Context mContext, Map<String, String> params, AsyncResponseCallBack responseHandler) {
        post(mContext, QUALITY_MARKET_WEB_URL + CONTACT, params, responseHandler);//UPLOAD_IMAGE_TEST_ENV_WEB_URL
    }

    /**
     * 通用参数接口
     */
    public static void qualityParameterShow(Context mContext, Map<String, String> params, AsyncResponseCallBack responseHandler) {
        post(mContext, QUALITY_MARKET_WEB_URL + QUALITY_PARAMETER_SHOW, params, responseHandler);//UPLOAD_IMAGE_TEST_ENV_WEB_URL
    }

    /**
     * 我的认证接口
     */
    public static void myAuthentication(Context mContext, Map<String, String> params, AsyncResponseCallBack responseHandler) {
        post(mContext, QUALITY_MARKET_WEB_URL + MY_AUTHENTICATION, params, responseHandler);//UPLOAD_IMAGE_TEST_ENV_WEB_URL
    }

    /**
     * 首页证接口
     */
    public static void getHomePage(Context mContext, Map<String, String> params, AsyncResponseCallBack responseHandler) {
        post(mContext, QUALITY_MARKET_WEB_URL + HOME_PAGE_INDEX, params, responseHandler);//UPLOAD_IMAGE_TEST_ENV_WEB_URL
    }
}
