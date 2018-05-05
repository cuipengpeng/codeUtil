package com.test.bank.base;

import java.io.Serializable;

/**
 * Created by 55 on 2017/11/6.
 */

public class BaseBean<T> implements Serializable {

    public static final String CODE_SUCCESS = "0000";
    public static final String CODE_FAILED = "0";
    public static final String CODE_FORCE_UPDATE = "-1";

    public static String CODE_LOGIN_USERNAME_OR_PASSWORD_INCORRECT = "3000";//用户名密码错误
    public static String CODE_REGISTER_USER_EXISTS = "3001";//用户名已存在
    public static String CODE_LOGIN_TRY_TIME_OUT_LOAD = "3002";//您输入的密码错误次数过多，请稍后重试
    public static String CODE_TOKEN_INVALID = "3004";//token失效
    public static String CODE_LOGIN_USERNAME_NOT_EXIST = "4004";    //该手机号码不存在，请注册后登录

    //    public static String[] CODE_ARR_POP_DIALOG = new String[]{CODE_LOGIN_USERNAME_OR_PASSWORD_INCORRECT, CODE_LOGIN_TRY_TIME_OUT_LOAD, CODE_LOGIN_USERNAME_NOT_EXIST,CODE_REGISTER_USER_EXISTS};
    public static String[] CODE_ARR_POP_DIALOG = new String[]{CODE_LOGIN_TRY_TIME_OUT_LOAD, CODE_REGISTER_USER_EXISTS};


    private String resCode;
    private String resMsg;
    private Boolean msgStatus;
    private T data;

    public boolean isPopDialog() {
        for (String code : CODE_ARR_POP_DIALOG) {
            if (code.equals(resCode)) {
                return true;
            }
        }
        return false;
    }

    public boolean isTokenInValid() {
        return resCode.equals(CODE_TOKEN_INVALID);
    }

    public boolean isSuccess() {
        return this.resCode.equals(CODE_SUCCESS);
    }

    public boolean isFailed() {
        return this.resCode.equals(CODE_FAILED);
    }

    public boolean isTokenInvalid() {
        return this.resCode.equals(CODE_TOKEN_INVALID);
    }

    public boolean isForceUpdate() {
        return this.resCode.equals(CODE_FORCE_UPDATE);
    }

    public String getResCode() {
        return resCode;
    }

    public void setResCode(String resCode) {
        this.resCode = resCode;
    }

    public String getResMsg() {
        return resMsg;
    }

    public void setResMsg(String resMsg) {
        this.resMsg = resMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Boolean getMsgStatus() {
        return msgStatus;
    }

    public void setMsgStatus(Boolean msgStatus) {
        this.msgStatus = msgStatus;
    }

    @Override
    public String toString() {
        return "BaseBean{" +
                "resCode='" + resCode + '\'' +
                ", resMsg='" + resMsg + '\'' +
                ", msgStatus='" + msgStatus + '\'' +
                ", data=" + data +
                '}';
    }
}
