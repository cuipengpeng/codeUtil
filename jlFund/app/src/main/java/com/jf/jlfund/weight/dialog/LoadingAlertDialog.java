package com.jf.jlfund.weight.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jf.jlfund.R;


/**
 * 网络加载时弹出框
 *
 */
public class LoadingAlertDialog extends AlertDialog {
    private Context mContext;
    private ProgressBar mBar;
    private TextView mMessage;

    public LoadingAlertDialog(Context context, int theme) {
        super(context, theme);
    }

    public LoadingAlertDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public LoadingAlertDialog(Context context) {
        this(context, R.style.protocalDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading_layout);

        // 点击imageview外侧区域，动画不会消失
        setCanceledOnTouchOutside(false);

        mBar = (ProgressBar) findViewById(R.id.bar);
        mMessage = (TextView) findViewById(R.id.message);
    }

    /**
     * 设置文本内容,并且显示弹出框
     *
     * @param msg
     */
    public void show(String msg) {
        super.show();
        setText(msg);
    }

    public void setText(String msg) {
        if (mMessage != null) {
            mMessage.setText(msg);
        }
    }

    /**
     * 设置进度图片
     *
     * @param drawable
     */
    public void setIndeterminateDrawable(int drawable) {
        mBar.setIndeterminateDrawable(mContext.getResources().getDrawable(drawable));
    }

    /**
     * 设置字体颜色
     *
     * @param color
     */
    public void setTextColor(int color) {
        mMessage.setTextColor(color);
    }
}
