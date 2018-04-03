package com.jf.jlfund.weight.errorview.factory;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jf.jlfund.R;
import com.jf.jlfund.weight.errorview.ErrorBean;


/**
 * Created by 55 on 2017/10/10.
 */

public class EmptyErrorView implements ErrorView {

    private Context mContext;
    private OnErrorPageRefreshListener refreshListener;    //点击刷新回调  ,空数据布局无刷新按钮
    private ErrorBean errorPageBean;

    public EmptyErrorView(Context context, ErrorBean errorPageBean, OnErrorPageRefreshListener refreshListener) {
        mContext = context;
        this.refreshListener = refreshListener;
        this.errorPageBean = errorPageBean;
    }

    @Override
    public View productErrorView() {
        View errorView = LayoutInflater.from(mContext).inflate(R.layout.layout_error_no_connect, null, false);
//        TextView tvTip1 = (TextView) currErrorView.findViewById(R.id.tv_errorPage_tip1);
//        if (tvTip1 != null && !TextUtils.isEmpty(errorPageBean.hintText)) {
//            tvTip1.setQuestionText(errorPageBean.hintText);
//        }
//        ImageView iv = (ImageView) currErrorView.findViewById(R.id.iv_errorPage);
//        if (iv != null && errorPageBean.imgId != -1) {
//            iv.setImageResource(errorPageBean.imgId);
//        }
        return errorView;
    }
}
