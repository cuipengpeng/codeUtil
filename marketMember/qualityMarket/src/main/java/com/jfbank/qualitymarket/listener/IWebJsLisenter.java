package com.jfbank.qualitymarket.listener;

/**
 * 功能：web H5回调处理<br>
 * 作者：赵海<br>
 * 时间： 2017/1/18 0018<br>.
 * 版本：1.2.0
 */

public interface IWebJsLisenter {
    /**
     * 设置显示分享按钮
     */
    void setShare(String shareStr);

    void openContacts(String requestName);

    void requstAllContacts();

    void onRefreshWebUrl();

    void setH5Title(String title);

    void openCamera();

    void setCallBack(int statusClose);

    void onH5Back(int statusClose);
}
