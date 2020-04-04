package com.caishi.chaoge.base;

/**
 * 网络请求回调接口基类
 */

public interface BaseRequestInterface<T> {

    void success(int state, String msg, T t);

    void error(int state, String msg);
}
