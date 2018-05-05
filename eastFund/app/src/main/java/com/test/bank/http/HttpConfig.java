package com.test.bank.http;

import java.net.HttpURLConnection;

/**
 * Created by 55 on 2017/11/3.
 */

public class HttpConfig {

    //--------------------------服务器地址------------------------------------//
    public static final String BASE_URL = "http://47.95.165.12:8181/jlapp-web/";//服务器地址【外网】

    public static final int SUCCESS = 200;  //请求数据成功
    public static final int EMPTY_DATA = -11;  //数据为空
    public static final int TOKEN_INVALID = 2;  //用户token失效状态码，增加可读性，避免魔法数
    public static final int FORCE_UPDATE = -1;  //强制更新状态码

    /**
     * 未知错误
     */
    public static final int UNKNOWN_ERROR = 1000;

    /**
     * 连接异常,对应 ConnectException 异常
     */
    public static final int CONNECT_EXCEPTION = 1002;

    /**
     * 连接被拒绝,服务器崩溃
     */
    public static final int SERVER_EXCEPTION = 1003;

    /**
     * HTTP Status-Code 408: Request Time-Out.
     * <p>
     * 注意：超时分两种情况
     * 一种是请求超时：对应该408状态码，如果请求失败，说明未能成功请求到服务器，可以允许用户再次提交。
     * 一种是读取超时：对应 SocketTimeoutException  如果是响应失败，就说明用户提交是成功了的，应该防止用户提交。
     */
    public static final int REQUEST_TIMEOUT = HttpURLConnection.HTTP_CLIENT_TIMEOUT;
}
