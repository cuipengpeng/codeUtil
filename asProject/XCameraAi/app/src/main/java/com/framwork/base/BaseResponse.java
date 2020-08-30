package com.framwork.base;

import java.io.Serializable;

/**
 * creat by mz  2019.9.24
 */
public class BaseResponse implements Serializable{

    private  int  code;
    private   String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        code = this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public  boolean isSucess(){
        return this.code == 0;
    }


}
