package com.test.xcamera.bean;

import java.util.List;

/**
 * Created by DELL on 2019/7/6.
 */

public class MyBean {

    private String message;
    private  int code;
    private List<MyTestBean> result;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<MyTestBean> getResult() {
        return result;
    }

    public void setResult(List<MyTestBean> result) {
        this.result = result;
    }
}
