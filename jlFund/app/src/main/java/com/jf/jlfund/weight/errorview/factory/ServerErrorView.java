package com.jf.jlfund.weight.errorview.factory;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.jf.jlfund.R;
import com.jf.jlfund.weight.errorview.ErrorBean;

/**
 * Created by 55 on 2017/10/10.
 */

public class ServerErrorView implements ErrorView {

    private Context mContext;
    private OnErrorPageRefreshListener refreshListener;
    private ErrorBean errorPageBean;    //用来定制布局的文案

    public ServerErrorView(Context context, ErrorBean errorPageBean, OnErrorPageRefreshListener refreshListener) {
        mContext = context;
        this.refreshListener = refreshListener;
        this.errorPageBean = errorPageBean;
    }

    @Override
    public View productErrorView() {
        View errorView = LayoutInflater.from(mContext).inflate(R.layout.layout_error_no_connect, null, false);
//        TextView tvHint = (TextView) currErrorView.findViewById(R.id.tv_404_tip1);
//        if (errorPageBean != null && !TextUtils.isEmpty(errorPageBean.hintText)) {
//            tvHint.setQuestionText(errorPageBean.hintText);
//        }
//
//        currErrorView.findViewById(R.id.btn_refresh).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (refreshListener != null) {
//                    refreshListener.onRefresh();
//                }
//            }
//        });
        return errorView;
    }
}
