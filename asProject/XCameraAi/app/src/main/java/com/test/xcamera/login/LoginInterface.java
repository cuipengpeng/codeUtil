package com.test.xcamera.login;


import com.framwork.base.view.BaseViewInterface;

/**
 * Author: mz
 * Time:  2019/10/8
 */
public interface LoginInterface extends BaseViewInterface {

    String getPhone();
    String getCode();
    void  Finish();
    void   setLoginClickable(boolean isClickable);
    String getisActivation();

    /**
     * 判断获取验证码是否成功
     * @param isSuccess
     */
    void codeCallBack(boolean isSuccess);
}
