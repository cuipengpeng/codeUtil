package com.jf.jlfund.weight.errorview;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.jf.jlfund.http.HttpConfig;
import com.jf.jlfund.weight.errorview.factory.ErrorViewFactory;
import com.jf.jlfund.weight.errorview.factory.OnErrorPageRefreshListener;

/**
 * Created by 55 on 2017/10/10.
 */

public abstract class ErrorBean {

    private static final String TAG = "ErrorBean";

    public View replacedView;   //将要被错误页面替换的view
    public ViewGroup parentOfReplacedView;  //replacedView的父布局
    public int indexOfReplacedView;     //replacedView在父布局中的index

    public View currErrorView;      //错误页面

    public String hintText;  //错误页面的提示文本
    public int imgId;    //错误页面的图片id

    public ErrorBean(View replacedView) {
        this.replacedView = replacedView;
        init();
    }

    public ErrorBean(View replacedView, String hintText) {
        this.replacedView = replacedView;
        this.hintText = hintText;
        init();
    }

    public ErrorBean(View replacedView, int imgId) {
        this.replacedView = replacedView;
        this.imgId = imgId;
        init();
    }

    public ErrorBean(View replacedView, String hintText, int imgId) {
        this.replacedView = replacedView;
        this.hintText = hintText;
        this.imgId = imgId;
        init();
    }

    private void init() {
        if (replacedView.getParent() != null) {
            parentOfReplacedView = (ViewGroup) replacedView.getParent();
        }
        for (int i = 0; i < parentOfReplacedView.getChildCount(); i++) {
            Log.v(TAG, "init: childAt:" + i + " -- " + parentOfReplacedView.getChildAt(i).toString());
            if (parentOfReplacedView.getChildAt(i) == replacedView) {   //RecyclerRefreshLayout的第一个子view是CircleImageView，即刷新的转动箭头
                indexOfReplacedView = i;
            }
        }
    }

    public void show(int errorCode) {
        Log.v(TAG, "show: errorCode: " + errorCode);
        if (HttpConfig.SUCCESS == errorCode) {
            transferToOriginalPage();
        } else {
            transferToErrorPage(errorCode, null);
        }
    }

    public void show(int errorCode, OnErrorPageRefreshListener onErrorPageRefreshListener) {
        if (HttpConfig.SUCCESS == errorCode) {
            transferToOriginalPage();
        } else {
            transferToErrorPage(errorCode, onErrorPageRefreshListener);
        }
    }

    /**
     * 转换为错误页面
     *
     * @param errorCode
     */
    public void transferToErrorPage(int errorCode, OnErrorPageRefreshListener onErrorPageRefreshListener) {
        Log.v(TAG, "transferToErrorPage: errorCode: " + errorCode);
        if (replacedView == null) {
            return;
        }

//        if (replacedView instanceof AutoLoadMoreRecyclerView && errorCode == HttpConfig.CONNECT_EXCEPTION) {
//            WrapAdapter adapter = ((AutoLoadMoreRecyclerView) replacedView).getWrapAdapter();
//            if (adapter != null && adapter.getItemCount() - adapter.getFootersCount() - adapter.getHeadersCount() > 0) {
//                ToastUtils.showShort("当前网络异常，请稍后重试。");
//                return;
//            }
//        }

        View errorView = ErrorViewFactory.productErrorView(replacedView.getContext(), errorCode, this, onErrorPageRefreshListener);    //根据code重新生成对应的errorView
        if (errorView == null) {
            Log.v(TAG, "transferToErrorPage: currErrorView is null......");
            return;
        }
        Log.v(TAG, "transferToErrorPage: currErrorView: " + errorView.toString());

        if (currErrorView == null) {
            currErrorView = errorView;

            errorView.setLayoutParams(replacedView.getLayoutParams());
            parentOfReplacedView.removeView(replacedView);
            parentOfReplacedView.addView(errorView);
        } else {
            errorView.setLayoutParams(replacedView.getLayoutParams());
            parentOfReplacedView.removeView(currErrorView);
            currErrorView = errorView;
            parentOfReplacedView.addView(currErrorView);
        }

    }


    /**
     * 转换为原始页面
     */
    public void transferToOriginalPage() {
        if (replacedView == null || parentOfReplacedView == null) {
            Log.v(TAG, "transferToOriginalView: one of the view is null...");
            return;
        }

        if (currErrorView == null) {
            Log.v(TAG, "transferToOriginalPage: not do uselesss work...");
            return;
        } else {
            parentOfReplacedView.removeView(currErrorView);
        }

        if (replacedView.getParent() != null) {
            ViewGroup parent = (ViewGroup) replacedView.getParent();
            parent.removeView(replacedView);
        }
//        parentOfReplacedView.addView(replacedView, replacedView.getLayoutParams());
        parentOfReplacedView.addView(replacedView, indexOfReplacedView);
    }

    @Override
    public String toString() {
        return "ErrorBean{" +
                "replacedView=" + replacedView +
                ", parentOfReplacedView=" + parentOfReplacedView +
                ", indexOfReplacedView=" + indexOfReplacedView +
                ", hintText='" + hintText + '\'' +
                ", imgId=" + imgId +
                '}';
    }
}
