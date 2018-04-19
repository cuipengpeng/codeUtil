package com.jfbank.qualitymarket.util;

public class ConstantsUtil {
    public static final int BASE_ID = 0;
    public static final int RQF_PAY = BASE_ID + 1;
    //"待支付", 1,
    //	"待发货", 2,
    //	"待收货", 3,
    //	"已完成", 4,
    //	"已取消, 5,
    //	"已取消, 6,
    //	"已支付",7,
    //	"已取消",8
    public static final String[] ORDER_STATUS = {"", "待支付", "待发货", "待收货", "已完成", "已取消", "已取消", "已支付", "已取消"};
    public static final String RET_CODE_SUCCESS = "0000";// 0000 交易成功
    public static final String RET_CODE_PROCESS = "2008";// 2008 支付处理中
    public static final String RESULT_PAY_PROCESSING = "PROCESSING";
    //异步通知回调地址
    public static final String NOTIFY_URL = "http://test.yintong.com.cn:80/apidemo/API_DEMO/notifyUrl.htm";
    public static final String INTENT_CLASSIFY = "CLASSIFY";//跳转到品类界面
    public static final String INTENT_WEBONLY = "WEBONLY";//跳转到网页


    public static final String SYMBOL_EQUAL = "=";
    public static final String SIGNED_SEPETATOR = "|";
    public static final String SIGNED_KEY = "7758521";

    public static final int PERMISSION_WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 201;
    public static final int PERMISSION_CAMERA_REQUEST_CODE = 203;
    public static final int PERMISSION_CAMERA_STATUS = 203;
    public static final int PERMISSION_LOCATION_REQUEST_CODE = 205;

    public static final String SDCARD_STORAGE_DIR = "/qualityMarket_001";
    public static final String APP_ICON_FILE_NAME = "icon.jpg";

    //全局配置文件名
    public static final String GLOBAL_CONFIG_FILE_NAME = "qualityMarketGlobalConfig";
    public static final String KEY_OF_APP_START_AD = "appStartAdKey";
    public static final String KEY_OF_APP_START_AD_CLICK_URL = "appStartAdClickUrlKey";
    public static final String KEY_OF_APP_START_AD_ONLINE_TIME = "appStartAdOnlineTimeKey";
    public static final String KEY_OF_APP_START_AD_OFFLINE_TIME = "appStartAdOfflineTimeKey";
    public static final String KEY_OF_APP_START_AD_OFFLINE = "appStartAdOfflineKey";
    public static final String APP_ICON_LOCAL_STORE_KEY = "appIconLocalStoreKey";

    public static final String SHARED_PREFERENCE_FILE_NAME = "qualityMarket";
    public static final String JS_INTERFACE_OBJECT = "callBackModel";
    public static final String JS_INTERFACE_FILE_NAME_ACHIEVE = "achieve";
    public static final String JS_INTERFACE_FILE_NAME_AUTHORITY = "authority";

    public static final int WEBVIEW_CACHE_TIME = 60 * 60 * 24 * 7;//s
    public static final int COUNT_DOWN = 60;//s
    public static final int VERIFY_CODE_LENGTH = 6;
    public static final int PRODUCT_IMAGE_WIDTH = 300;


    public static final int RESPONSE_SUCCEED = 1;
    public static final int RESPONSE_NO_DATA = 11;
    public static final int RESPONSE_UPDATE_VERSION = 13;
    public static final int RESPONSE_TOKEN_FAIL = 10;
    public static final String RESPONSE_STATUS_FIELD_NAME = "status";
    public static final String RESPONSE_DATA_JSON_ARRAY_FIELD_NAME = "data";
    public static final String RESPONSE_MESSAGE_FIELD_NAME = "statusDetail";
    public static final String RESPONSE_DETAIL_FAILED_REASON_FIELD_NAME = "statusMsg";
    public static final String RESPONSE_ERROR_CODE_FIELD_NAME = "errorCode";
    public static final String RESPONSE_ERROR_PROP_FIELD_NAME = "errorProp";
    public static final String RESPONSE_ERROR_MESSAGE_FIELD_NAME = "errorMsg";

    public static final String ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER = "亲，您的网络异常，请检查后重试";
    public static final String H5_PAGE_FAIL_TO_CONNECT_SERVER = "网络异常，请检查网络后重新加载!";
    public static final String PULL_TO_REFRESH_PROMPT = "上拉加载更多...";
    public static final String NO_MORE_DATA_PROMPT = "没有更多数据了";
    public static final String NETWORK_REQUEST_IN = "网络请求中...";

    public static final String CHANNEL_OF_PINZHI_IN_ONE_CARD = "pzscAPP";//万卡app中万卡商城的渠道号
    public static final String CHANNEL_CODE = "android";
    public static final String PLAT = "android";            //平台

    public static final String EXTRA_AFTERSALES_TYPEOFSUPPORT = "typeOfSupport";//支持退换货类型
    public static final String EXTRA_AFTERSALES_TYPE = "aftersales_type";//退换货类型
    public static final String EXTRA_AFTERSALESREASONBEAN = "AfterSalesReasonBean";//退换货原因
    public static final String EXTRA_GOODS_REJECTED_BEAN = "goodsRejectedBean";//退换货原因
    public static final String EXTRA_FIRSTPAYMENT = "FirstPayment";//退款金额（首付金额）
    public static final String EXTRA_ORDERID = "orderId";//订单id 商品订单订单编号
    public static final String EXTRA_IDENTIFICATION = "identification";//退换货标识：1退货;2换货
    public static final String EXTRA_RETURNEDGOODSORDERID = "returnedGoodsOrderId";//退换货订单编号
    public static final String KEY_OF_DES = "201408071000001543";
    public static final String PARTNER = "201408071000001543";
    public static final String MD5_KEY = "201408071000001546_test_20140815";

    // 商户（RSA）私钥 TODO 强烈建议将私钥配置到服务器上，否则有安全隐患
    public static final String RSA_PRIVATE =
            "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAOilN4tR7HpNYvSBra/DzebemoAiGtGeaxa+qebx/O2YAdUFPI+xTKTX2ETyqSzGfbxXpmSax7tXOdoa3uyaFnhKRGRvLdq1kTSTu7q5s6gTryxVH2m62Py8Pw0sKcuuV0CxtxkrxUzGQN+QSxf+TyNAv5rYi/ayvsDgWdB3cRqbAgMBAAECgYEAj02d/jqTcO6UQspSY484GLsL7luTq4Vqr5L4cyKiSvQ0RLQ6DsUG0g+Gz0muPb9ymf5fp17UIyjioN+ma5WquncHGm6ElIuRv2jYbGOnl9q2cMyNsAZCiSWfR++op+6UZbzpoNDiYzeKbNUz6L1fJjzCt52w/RbkDncJd2mVDRkCQQD/Uz3QnrWfCeWmBbsAZVoM57n01k7hyLWmDMYoKh8vnzKjrWScDkaQ6qGTbPVL3x0EBoxgb/smnT6/A5XyB9bvAkEA6UKhP1KLi/ImaLFUgLvEvmbUrpzY2I1+jgdsoj9Bm4a8K+KROsnNAIvRsKNgJPWd64uuQntUFPKkcyfBV1MXFQJBAJGs3Mf6xYVIEE75VgiTyx0x2VdoLvmDmqBzCVxBLCnvmuToOU8QlhJ4zFdhA1OWqOdzFQSw34rYjMRPN24wKuECQEqpYhVzpWkA9BxUjli6QUo0feT6HUqLV7O8WqBAIQ7X/IkLdzLa/vwqxM6GLLMHzylixz9OXGZsGAkn83GxDdUCQA9+pQOitY0WranUHeZFKWAHZszSjtbe6wDAdiKdXCfig0/rOdxAODCbQrQs7PYy1ed8DuVQlHPwRGtokVGHATU=";
//    public static final String RSA_PRIVATE =
//            "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJJL8OLB0J/9pmzHFxpwOeigamHd3Yk6PkZdaL6reDOdlq5mOQ0/xIqXcnaWI/Q7qtT9j/b34hR74ZMyEw4Um5mbWG0C0qK7l6RbQaUExbF/gU+RiVCQ8TQW1qgw/eBh+H47Aj58hGulbfJKfeZJydzpnvTSdT9VitGR9xIJtKdHAgMBAAECgYBMmbzATnE5RGu+qyP6sOZxWoU5Rx03PCrdVw2AQHIIvKvoFxgqSshTNOc3Fngu6osRSM73pmVXCmJbWy3FAp9Rqg2FZfQoX+ds4cnj3QVpeILw6b2Sr0rI2OBkbXGFre/crM+JcjYBAkV7pnwcWRH3EyOvzLUqKs5qEkOycxTi8QJBAOUFVS8ipCnp7Qaynig6PcfJC0JP4GxpFmQu0w1OrmlzP/zezUfRwihTx1NPssJm9HD7KNiBDlgFj0PQJkGbB18CQQCjh90kBAoloAsCxe/qD4w7lbre75P16Kicb+K0FCeJsZrdXpApFhlDo60zPNUJEPph9HFptZfNBE8I8dIesHEZAkEAxe4V8Oa/ennxoBg/GAU936yhTm46R3eLIopVXOrjUb+JTcJBKBDg/Hlri1UV6W2RVRO7+WGQRAKKDtGWPpz9gQJAImZAFIVtBQEnj8vHbfsbSqVyi9blzwLEBTRcAfmDX6mmpA5yUNI/OkVB99dCEQgrQ1PCT7RNXGkdnwoPYzlGcQJBAJQQrWM8SxovyqcN7Md2wRvIjA1Ny7OJGSR8y+0eu/D0GydQbUj1rNdPX5CLNFVwvcgMwkLNUD+u+JSol5+PQHk=";

    // 银通支付（RSA）公钥
    public static final String RSA_YT_PUBLIC =
            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCSS/DiwdCf/aZsxxcacDnooGph3d2JOj5GXWi+q3gznZauZjkNP8SKl3J2liP0O6rU/Y/29+IUe+GTMhMOFJuZm1htAtKiu5ekW0GlBMWxf4FPkYlQkPE0FtaoMP3gYfh+OwI+fIRrpW3ySn3mScnc6Z700nU/VYrRkfcSCbSnRwIDAQAB";

}
