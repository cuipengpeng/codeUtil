package com.caishi.chaoge.base;

import com.google.gson.annotations.SerializedName;

/**
 * 服务器通用返回数据格式
 */
public class BaseBean<E> {

    @SerializedName("code")
    private int code;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private E data;

    public boolean isSuccess() {
        return code == 10000;
    }

    public int getState() {
        return code;
    }

    public String getMsg() {
        return message;
    }

    public E getData() {
        return data;
    }
}
