package com.jf.jlfund.inter;

/**
 * Created by 55 on 2017/12/1.
 */

public abstract class OnResponseListener<T> implements SuperResponseListener<T> {
    public String UUID = java.util.UUID.randomUUID().toString();
    public void onStartRequest(){}
}