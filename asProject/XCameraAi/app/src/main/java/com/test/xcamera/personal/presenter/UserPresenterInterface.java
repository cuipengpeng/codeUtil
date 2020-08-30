package com.test.xcamera.personal.presenter;


import com.framwork.base.presenter.BasePresenterInterface;
import com.test.xcamera.personal.usecase.OnGetUserInfoCallBack;


public interface UserPresenterInterface extends BasePresenterInterface {
    void userProfile(String imageId);
    void uploadFile(String path);
    void unLogin();
    void getUserInfo(OnGetUserInfoCallBack callBack);
}
