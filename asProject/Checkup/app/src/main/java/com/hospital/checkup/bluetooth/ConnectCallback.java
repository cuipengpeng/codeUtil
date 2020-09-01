package com.hospital.checkup.bluetooth;


public interface ConnectCallback {
    /**
     *  获得通知之后
     */

    void onConnSuccess();

    /**
     * 断开或连接失败
     */
    void onConnFailed();
}
