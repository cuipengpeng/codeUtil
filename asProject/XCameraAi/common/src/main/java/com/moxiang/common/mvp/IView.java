package com.moxiang.common.mvp;

/**
 * Created by admin on 2019/10/10.
 */

public interface IView {
    /**
     * 显示加载
     */
    void showLoading();
    /**
     * 隐藏加载
     */
    void hideLoading();

    void showNetWorkErrorView();
    void showNoDataView();

}
