package com.jf.jlfund.bean;

/**
 * Created by 55 on 2017/12/19.
 */

public class SmsCheckCodeBean {
    private String code;    //Aes加密验证码

    public SmsCheckCodeBean() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "SmsCheckCodeBean{" +
                "code='" + code + '\'' +
                '}';
    }
}
