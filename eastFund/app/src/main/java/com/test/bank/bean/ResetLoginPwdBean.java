package com.test.bank.bean;

/**
 * Created by 55 on 2017/12/19.
 */

public class ResetLoginPwdBean {
    private Boolean state;

    public ResetLoginPwdBean() {
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "ResetLoginPwdBean{" +
                "state=" + state +
                '}';
    }
}
