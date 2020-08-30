package com.test.xcamera.personal.presenter;


import android.content.Context;


import com.editvideo.ToastUtil;
import com.framwork.base.presenter.BasePresenter;
import com.framwork.base.view.BaseViewInterface;
import com.google.gson.Gson;
import com.test.xcamera.R;
import com.test.xcamera.api.ApiImpl;
import com.test.xcamera.api.CallBack;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.bean.UploadBean;
import com.test.xcamera.bean.User;

import com.test.xcamera.personal.PersonUserInterface;
import com.test.xcamera.personal.usecase.GetUserInfo;
import com.test.xcamera.personal.usecase.OnGetUserInfoCallBack;
import com.test.xcamera.upload.UploadListener;
import com.test.xcamera.upload.UploadUtil;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.FileUtils;
import com.test.xcamera.utils.NetworkUtil;
import com.test.xcamera.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

public class UserPresenterImpl extends BasePresenter implements UserPresenterInterface {
    private Context mContext;

    public UserPresenterImpl(BaseViewInterface login, Context context) {
        super(login);
        mContext = context;
    }

    @Override
    public void uploadFile(String path) {
        if (!NetworkUtil.isNetworkConnected(mContext)) {
            ToastUtil.showToast(AiCameraApplication.getContext(), AiCameraApplication.getContext().getString(R.string.net_work_Available));
            return;
        }

        List<String> pathList = new ArrayList<>();
        pathList.add(path);
        UploadUtil.uploadFiles(pathList, new UploadListener<UploadBean>() {
            @Override
            public void onProgress(int progress) {
            }

            @Override
            public void onFail(Throwable errorInfo) {
            }

            @Override
            public void onSucess(UploadBean bean) {
                if (bean.getData() != null && bean.getData().size() > 0) {
                    long imageId = bean.getData().get(0).getFileId();
                    userProfile(String.valueOf(imageId));
                } else {
                    userProfile(String.valueOf(-1));
                }
                FileUtils.deleteFile(path);
            }
        });
    }

    @Override
    public void unLogin() {
        ApiImpl.getInstance().unLogin(new CallBack<User>() {
            @Override
            public void onSuccess(User user) {

            }

            @Override
            public void onFailure(Throwable e) {

            }

            @Override
            public void onCompleted() {

            }
        });
    }

    @Override
    public void getUserInfo(OnGetUserInfoCallBack callBack) {
        GetUserInfo userInfo = GetUserInfo.getInstance();
        userInfo.setCallBack(callBack);
        userInfo.getUserInfo();
    }

    @Override
    public void userProfile(String imageId) {
        User.UserDetail userDetail = ((PersonUserInterface) viewThis).getUserMassage();
        if (!"-1".equals(imageId)) {
            userDetail.setAvatarFileId(imageId);
        }
        ApiImpl.getInstance().userProfile(userDetail.getAvatarFileId(), userDetail.getNickname(), userDetail.getDescription(), new CallBack<User>() {
            @Override
            public void onSuccess(User user) {
                if (user.isSucess()) {
                    User.UserDetail userDetail = user.getData();
                    Gson gson = new Gson();
                    Object object = SPUtils.readObject(mContext, SPUtils.KEY_OF_USER_INFO, new User.UserDetail());
                    if (object instanceof User.UserDetail) {
                        AiCameraApplication.userDetail = (User.UserDetail) object;
                        AiCameraApplication.userDetail.setNickname(userDetail.getNickname());
                        AiCameraApplication.userDetail.setDescription(userDetail.getDescription());
                        AiCameraApplication.userDetail.setAvatarFileId(userDetail.getAvatarFileId());
                        AiCameraApplication.userDetail.setAvatarFileUrl(userDetail.getAvatarFileUrl());
                        AiCameraApplication.userDetail.setReviewStatus(userDetail.getReviewStatus());
                        SPUtils.writeObject(mContext, SPUtils.KEY_OF_USER_INFO, AiCameraApplication.userDetail);
                    }
                    String detail = gson.toJson(userDetail);
                    SPUtils.put(mContext, "user", detail);
                    showMsg(R.string.person_edit_introduce_success, mContext);
                    if (viewThis != null) {
                        ((PersonUserInterface) viewThis).editUserCallBack(user.getMessage());
                    }
                } else {
                    if (mContext != null) {
                        if (user.getCode() == 20001) {
                            ((PersonUserInterface) viewThis).unTokenCallBack(user.getMessage());
                            showMsg(R.string.person_token_failure, mContext);

                        } else if (user.getCode() == 20112) {
                            showMsg(R.string.person_edit_introduce_error_name, mContext);

                        } else if (user.getCode() == 20113) {
                            showMsg(R.string.person_edit_introduce_error_des, mContext);

                        } else if (user.getCode() == 20114) {
                            showMsg(R.string.person_edit_introduce_error_no, mContext);
                        } else {
                            if(viewThis!=null){
                                ((PersonUserInterface) viewThis). editUserCallBack(user.getMessage());
                            }
                        }
                    }

                }
            }

            @Override
            public void onFailure(Throwable e) {
                showMsg(R.string.person_edit_introduce_failure, mContext);
            }

            @Override
            public void onCompleted() {
            }
        });
    }

    private void showMsg(int id, Context mContext) {
        if (mContext != null) {
            CameraToastUtil.show(mContext.getString(id), mContext);
        }
    }
}
