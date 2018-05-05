package com.test.bank.utils;

/**
 */
public class ConstantsUtil {

    //*/ 手势密码点的状态
    public static final int POINT_STATE_NORMAL = 0; // 正常状态
    public static final int POINT_STATE_SELECTED = 1; // 按下状态
    public static final int POINT_STATE_WRONG = 2; // 错误状态
    public static final int POINT_STATE_CORRECT = 3; // 正确状态

    public static final String CHANNEL_CODE  = "app"; //渠道标志(app,h5)
    public static final String MOBILE_SYSTEM  = "android"; //系统(ios,android)

    public static final String SYMBOL_EQUAL = "=";
    public static final String SIGNED_SEPETATOR = "|";
    public static final String SIGNED_KEY = "7758521";
    public static final int PAGE_SIZE = 20;
    public static final String REPLACE_BLANK_SYMBOL = "--";
    //
    public static final String CONFIG_FILE_NAME = "settingsConfig";
    public static final int ACCOUNT_MAX_VALID_TIME = 1000*60*5;

    public static class JS {
        public static final String JS_INTERFACE_OBJECT = "jsInterfaceObject";
    }

    public static class Config {
        public static final String USER_SETTING_CONFIG= "userSettingConfig";
    }

    //通用的一些常量
    public static class Common {
        public static final String VERSION = "1.7.0";//app版本号
        public static final String DEVICETYPE = "android";//设备渠道
        public static final String GUIDE_VERSION = "1";//引导页版本号
        public static final String SUBVERSION_PARAM = "subversion";//用来控制是否使x5内核的小版本
        public static final String SUBVERSION_VALUE = "x5"; //使用x5内核
    }

    //短信验证码类型
    public static class VerifyPayPwd {
        public static final String CODE_PAY = "1";//借款
        public static final String CODE_REPAY = "2";//还款
        public static final String CODE_PAY_PASSWORD = "3";//支付密码修改
    }

    /**
     * http响应常量
     */
    public static class Response {
        public static final String KEY_OF_RESPONSE_CODE = "resCode";//借款
        public static final String KEY_OF_RESPONSE_MESSAGE = "resMsg";//借款
        public static final String KEY_OF_RESPONSE_DATA = "data";//借款

        public static final int RESPONSE_CODE_TOKEN_FAIL_3004 =3004;
        public static final int RESPONSE_CODE_TRADE_PASSWORD_ERROR_3011 =3011;
        public static final int RESPONSE_CODE_TRADE_PASSWORD_ERROR_3013 =3013;
        public static final int RESPONSE_CODE_TRADE_PASSWORD_ERROR_3014 =3014;
    }

    /**
     * http响应常量
     */
    public static class Dialog {
        public static final int FINGERPRINT_VERIFY_FIRST_TIP = 501;
        public static final int FINGERPRINT_VERIFY_FIRST_FAIL = 502;
        public static final int GESTURE_VERIFY_FAIL = 503;
    }


}
