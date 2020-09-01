package com.hospital.checkup.listener;


public interface IWebJsLisenter {

    void setShare(String shareStr);

    void openContacts(String requestName);

    void requstAllContacts();

    void onRefreshWebUrl();

    void setH5Title(String title);

    void openCamera();

    void setCallBack(int statusClose);

    void onH5Back(int statusClose);
}
