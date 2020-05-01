package com.jfbank.qualitymarket.base;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.jfbank.qualitymarket.util.JumpUtil;
import com.jph.takephoto.app.TakePhoto;

import java.util.ArrayList;

/**
 * View基类
 */
public interface BaseView extends JumpUtil.JumpInterface {
    void showDialog(String msg);

    /**
     * 显示对话框
     *
     * @param isCancel 是否可以取消
     * @param msg      显示信息
     */
    void showDialog(boolean isCancel, String msg);

    void showDialog(boolean isCancel);

    void showDialog();

    /**
     * 关闭对话框
     */
    void disMissDialog();

    /**
     * 请求结束
     */
    void onViewDestory();

    /**
     * Toast提示
     *
     * @param msg
     */
    void msgToast(String msg);

    /**
     * 抢登活token失效
     *
     * @param msg
     */
    void onTokenFailure(final String msg);

    /**
     * 下拉刷新结束
     */
    void onFinishReFreshView();

    /**
     * 上啦加载更多
     */
    void onFinishLoadMoreView();

    /**
     * 网络失败显示
     *
     * @param msg
     */
    void onNetFailure(String url, String msg);

    void showContent();//显示主界面

    void setError(int resId, String msg, boolean isRetry, String retry);//显示错误界面

    void showError();

    void showEmpty();//显示空界面

    void setEmpty(int resId, String msg, boolean isRetry, String retry);//显示空界面

    void showLoading();//显示加载中界面

    TakePhoto getTakePhoto();//获取相册拍照对象

    void setLoading(String msg);//显示加载中界面

    void showPhotoList(ViewGroup parent, @NonNull ArrayList<String> photoAddress, int curSelectedPos,int picType);//查看相册

     void showPhotoOne(View view, String photoAddress, int picType);//查看相册
}
