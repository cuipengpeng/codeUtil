package com.jf.jlfund.weight.errorview;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.jf.jlfund.weight.errorview.factory.ErrorViewFactory;
import com.jf.jlfund.weight.errorview.factory.OnErrorPageRefreshListener;

/**
 * Created by 55 on 2017/10/10.
 * <p>
 * 局部替换，用于带头布局的RecycleView
 */

public class PartReplaceErrorPageBean extends ErrorBean {

    private static final String TAG = "HalfReplaceErrorPageBea";

    public PartReplaceErrorPageBean(View replacedView) {
        super(replacedView);
    }

    public PartReplaceErrorPageBean(View replacedView, String hintText) {
        super(replacedView, hintText);
    }

    public PartReplaceErrorPageBean(View replacedView, int imgId) {
        super(replacedView, imgId);
    }

    public PartReplaceErrorPageBean(View replacedView, String hintText, int imgId) {
        super(replacedView, hintText, imgId);
    }


    @Override
    public void transferToErrorPage(int errorCode, OnErrorPageRefreshListener onErrorPageRefreshListener) {
        Log.d(TAG, "transferToErrorPage in partReplaceErrorPageBean......");
        if (!(replacedView instanceof RecyclerView)) {
            Log.e(TAG, "transferToErrorPage: !(currErrorView instanceof RecyclerView) ");
            return;
        }

        RecyclerView.Adapter adapter = ((RecyclerView) replacedView).getAdapter();
        if (!(adapter instanceof RecyclerViewWithHeaderAdapter)) {
            Log.e(TAG, "transferToErrorPage: !(adapter instanceof RecyclerViewWithHeaderAdapter)");
            return;
        }

        currErrorView = ErrorViewFactory.productErrorView(replacedView.getContext(), errorCode, this, onErrorPageRefreshListener);    //根据code重新生成对应的errorView

        ((RecyclerViewWithHeaderAdapter) adapter).notifyPageChanged(currErrorView);
    }

    @Override
    public void transferToOriginalPage() {
        Log.d(TAG, "transferToOriginalPage: in partReplaceErrorPageBean");
        if (!(replacedView instanceof RecyclerView)) {
            Log.e(TAG, "transferToErrorPage: !(currErrorView instanceof RecyclerView) ");
            return;
        }

        RecyclerView.Adapter adapter = ((RecyclerView) replacedView).getAdapter();
        if (!(adapter instanceof RecyclerViewWithHeaderAdapter)) {
            Log.e(TAG, "transferToErrorPage: !(adapter instanceof RecyclerViewWithHeaderAdapter)");
            return;
        }

        ((RecyclerViewWithHeaderAdapter) adapter).notifyPageChanged(null);   //传null表示替换为原来的view
    }
}
