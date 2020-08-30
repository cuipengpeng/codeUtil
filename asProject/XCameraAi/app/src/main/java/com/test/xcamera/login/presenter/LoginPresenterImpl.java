package com.test.xcamera.login.presenter;


import com.framwork.base.presenter.BasePresenter;
import com.framwork.base.BaseResponse;
import com.framwork.base.view.BaseViewInterface;
import com.google.gson.Gson;
import com.test.xcamera.R;
import com.test.xcamera.api.ApiImpl;
import com.test.xcamera.api.CallBack;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.bean.User;
import com.framwork.http.okhttp.HttpClient;
import com.test.xcamera.h5_page.CommunityActivity;
import com.test.xcamera.home.activation.ConfirmActivationPhone;
import com.test.xcamera.login.LoginActivty;
import com.test.xcamera.login.LoginInterface;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.SPUtils;

/**
 * Author: mz
 * Time:  2019/10/8
 */
public class LoginPresenterImpl extends BasePresenter implements LoginPresenterInterface {
    public LoginPresenterImpl(BaseViewInterface login) {
        super(login);
    }

    @Override
    public void getcode() {
        ApiImpl.getInstance().VerificationCode(((LoginInterface) viewThis).getPhone(), new CallBack<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {
                if (baseResponse.isSucess()) {
                    if (viewThis != null) {
                        ((LoginInterface) viewThis).codeCallBack(true);
                        CameraToastUtil.show(((LoginActivty) viewThis).getResourceToString(R.string.get_code_sucess), (LoginActivty) viewThis);
                    }
                } else {
                    if (viewThis != null) {
                        ((LoginInterface) viewThis).codeCallBack(false);
                        CameraToastUtil.show(baseResponse.getMessage(), (LoginActivty) viewThis);
                    }
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

    @Override
    public void login() {
        String phone = ((LoginInterface) viewThis).getPhone();
        String code = ((LoginInterface) viewThis).getCode();
        ApiImpl.getInstance().login(phone, code, new CallBack<User>() {
            @Override
            public void onSuccess(User user) {
                if (user.isSucess()) {
                    String isActivation = ((LoginInterface) viewThis).getisActivation();
                    User.UserDetail userDetail = user.getData();
                    AiCameraApplication.userDetail = user.getData();
                    SPUtils.writeObject(AiCameraApplication.getContext(), SPUtils.KEY_OF_USER_INFO, AiCameraApplication.userDetail);
                    CameraToastUtil.show(((LoginActivty) viewThis).getResourceToString(R.string.login_sucess), (LoginActivty) viewThis);
                    SPUtils.put((LoginActivty) viewThis, "token", userDetail.getToken());
                    Gson gson = new Gson();
                    String detail = gson.toJson(userDetail);
                    SPUtils.put((LoginActivty) viewThis, "user", detail);
                    HttpClient.getInstance().setClientNull();//重新加载一下头信息
                    if ("1".equals(isActivation)) {
                        ((LoginActivty) viewThis).startAct(ConfirmActivationPhone.class);
                    }
                    (((LoginInterface) viewThis)).Finish();
                    if (CommunityActivity.logingCallback != null) {
                        CommunityActivity.logingCallback.loginCallback(1, userDetail.getToken());
                    }
                } else {
                    CameraToastUtil.show(user.getMessage(), (LoginActivty) viewThis);
                }
            }

            @Override
            public void onFailure(Throwable e) {
                if (CommunityActivity.logingCallback != null) {
                    CommunityActivity.logingCallback.loginCallback(0, "login error");
                }
            }

            @Override
            public void onCompleted() {
                ((LoginInterface) viewThis).setLoginClickable(false);
            }
        });
    }


}
