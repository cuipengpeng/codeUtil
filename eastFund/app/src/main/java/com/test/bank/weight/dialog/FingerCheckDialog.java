package com.test.bank.weight.dialog;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.StyleRes;
import android.view.View;
import android.widget.TextView;

import com.test.bank.R;

/**
 * Created by 55 on 2018/2/27.
 * 指纹验证的加载框
 */

public class FingerCheckDialog extends AlertDialog {

    public FingerCheckDialog(Context context) {
        this(context, R.style.CommonDialogFragment);
    }

    protected FingerCheckDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected FingerCheckDialog(Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    TextView tvResult;
    TextView tvCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_finger_check);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        tvResult = findViewById(R.id.tv_dialogFingerCheck_result);
        tvCancel = findViewById(R.id.tv_dialogFingerCheck_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void onCheckSuccess() {
        if (tvResult != null) {
            tvResult.setVisibility(View.GONE);
        }
    }

    public void onCheckFailed() {
        if (tvResult != null) {
            tvResult.setVisibility(View.VISIBLE);
            ObjectAnimator translationX = ObjectAnimator.ofFloat(tvResult, "translationX", 0, 25, -25, 25, -25, 15, -15, 6, -6, 0);
            translationX.setDuration(500);
            translationX.start();
        }
    }
}
