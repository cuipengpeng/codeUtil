package com.framwork.base.presenter;

import com.framwork.base.view.BaseViewInterface;

/**
 * Author: mz
 * Time:  2019/10/8
 */
public abstract  class BasePresenter {
    public BaseViewInterface viewThis;
    public BasePresenter(BaseViewInterface viewThis) {
        this.viewThis =viewThis;
    }
}
