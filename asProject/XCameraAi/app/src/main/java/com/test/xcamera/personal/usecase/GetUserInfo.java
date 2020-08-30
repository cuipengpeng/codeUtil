package com.test.xcamera.personal.usecase;

import com.google.gson.Gson;
import com.test.xcamera.api.ApiImpl;
import com.test.xcamera.api.CallBack;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.bean.User;
import com.test.xcamera.bean.UserInfo;
import com.test.xcamera.utils.SPUtils;

public class GetUserInfo {
    private OnGetUserInfoCallBack mCallBack;
    public static GetUserInfo getInstance() {
        return new GetUserInfo();
    }

    public GetUserInfo setCallBack(OnGetUserInfoCallBack mCallBack) {
        this.mCallBack = mCallBack;
        return this;
    }

    public void getUserInfo(){
        //已经登录过,需要校验token是否过期
        ApiImpl.getInstance().getUserInfo(new CallBack<UserInfo>() {
            @Override
            public void onSuccess(UserInfo userInfo) {

                if (userInfo.getCode() == 0) {

                    Gson gson = new Gson();
                    Object object = SPUtils.readObject(AiCameraApplication.getContext(), SPUtils.KEY_OF_USER_INFO, new User.UserDetail());
                    if (object instanceof User.UserDetail) {
                        AiCameraApplication.userDetail = (User.UserDetail) object;
                        AiCameraApplication.userDetail.setNickname(userInfo.getData().getNickname());
                        AiCameraApplication.userDetail.setDescription(userInfo.getData().getDescription());
                        AiCameraApplication.userDetail.setAvatarFileId(userInfo.getData().getAvatarFileId());
                        AiCameraApplication.userDetail.setAvatarFileUrl(userInfo.getData().getAvatarFileUrl());
                        AiCameraApplication.userDetail.setReviewStatus(userInfo.getData().getReviewStatus());
                        SPUtils.writeObject(AiCameraApplication.getContext(), SPUtils.KEY_OF_USER_INFO, AiCameraApplication.userDetail);
                    }
                    String detail = gson.toJson(AiCameraApplication.userDetail);
                    SPUtils.put(AiCameraApplication.getContext(), "user", detail);
                } else if (userInfo.getCode() == 20002) {
                    //token失效了直接登录即可,

                } else if (userInfo.getCode() == 20003) {
                    //token过期刷新token
                    extendToken();
                }
                if(mCallBack!=null){
                    mCallBack.onUserInfoCallBack(userInfo);
                }
            }

            @Override
            public void onFailure(Throwable e) {

            }

            @Override
            public void onCompleted() {

            }
        });
    }
    public void extendToken(){
        ApiImpl.getInstance().ExtendToken(new CallBack<User>() {
            @Override
            public void onSuccess(User user) {
                int code = user.getCode();
                if (code == 0) {
                    User.UserDetail userDetail = user.getData();
                    SPUtils.put(AiCameraApplication.getContext(), "token", userDetail.getToken());
                    //token 续期成功
                } else if (code == 20106) {
                    //token 续期失败.缺少参数
                }
            }

            @Override
            public void onFailure(Throwable e) {
            }

            @Override
            public void onCompleted() {

            }
        });
    }

}
