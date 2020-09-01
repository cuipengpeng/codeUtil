package com.hospital.checkup.utils;

public class ConstantsUtil {
    public static final int PERMISSION_WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 201;
    public static final int PERMISSION_CAMERA_REQUEST_CODE = 203;
    public static final int PERMISSION_CAMERA_STATUS = 203;
    public static final int PERMISSION_LOCATION_REQUEST_CODE = 205;

    public static final String JS_INTERFACE_OBJECT = "androidJs";
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
}
