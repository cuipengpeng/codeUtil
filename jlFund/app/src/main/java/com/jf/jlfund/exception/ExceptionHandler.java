package com.jf.jlfund.exception;

import android.util.Log;

import com.jf.jlfund.http.HttpConfig;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

/**
 * Created by 55 on 2017/11/6.
 */

public class ExceptionHandler {
    private static final String TAG = "ExceptionHandler";

    /**
     * 异常处理
     *
     * @param exception
     * @return
     */
    public static int handleException(Throwable exception) {        //EOFException
        Log.e(TAG, "handleException: " + exception.toString());
        if (exception instanceof ForceUpdateException) {        //强制更新
            return HttpConfig.FORCE_UPDATE;
        } else if (exception instanceof TokenInvalidException) {    //Token失效
            return HttpConfig.TOKEN_INVALID;
        } else if (exception instanceof ConnectException) {  //连接异常
            return HttpConfig.CONNECT_EXCEPTION;
        } else if (exception instanceof SocketTimeoutException || exception instanceof ServerException) {  //服务器异常
            return HttpConfig.SERVER_EXCEPTION;
        }
        return HttpConfig.UNKNOWN_ERROR;    //未知异常
    }
}
