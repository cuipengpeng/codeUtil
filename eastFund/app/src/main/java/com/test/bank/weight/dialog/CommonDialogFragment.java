package com.test.bank.weight.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TextView;

import com.test.bank.R;

/**
 * Created by 55 on 2017/12/19.
 */

public class CommonDialogFragment extends DialogFragment {

    TextView tvTitle;
    TextView tvLeft;
    TextView tvRight;

    View centerSplitView;

    String content;
    String leftTxt;
    String rightTxt;

    boolean isCanceledOnTouchOutside = false;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.CommonDialogFragment);
        dialog.setContentView(R.layout.layout_common_dialog);
        initView(dialog);
        return dialog;
    }

    private void initView(Dialog rootView) {
        rootView.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
        tvTitle = rootView.findViewById(R.id.tv_common_dialog_title);
        tvLeft = rootView.findViewById(R.id.tv_common_dialog_left);
        tvRight = rootView.findViewById(R.id.tv_common_dialog_right);
        centerSplitView = rootView.findViewById(R.id.view_split_common_dialog);
        tvTitle.setText(content);
        tvLeft.setText(leftTxt);
        tvRight.setText(rightTxt);
        tvRight.setVisibility(isOneButton ? View.GONE : View.VISIBLE);
        centerSplitView.setVisibility(isOneButton ? View.GONE : View.VISIBLE);
        tvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onLeftClickListener != null) {
                    onLeftClickListener.onClickLeft();
                }
                dismiss();
            }
        });

        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRightClickListener != null) {
                    onRightClickListener.onClickRight();
                }
                dismiss();
            }
        });
    }

    public void setContent(String title) {
        content = title;
    }

    public void setLeftTxt(String leftTxt) {
        this.leftTxt = leftTxt;
    }

    public void setRightTxt(String rightTxt) {
        this.rightTxt = rightTxt;
    }

    public void setIsCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        isCanceledOnTouchOutside = canceledOnTouchOutside;
    }

    private OnRightClickListener onRightClickListener;
    private OnLeftClickListener onLeftClickListener;

    public void setOnRightClickListener(OnRightClickListener onRightClickListener) {
        this.onRightClickListener = onRightClickListener;
    }

    public void setOnLeftClickListener(OnLeftClickListener onLeftClickListener) {
        this.onLeftClickListener = onLeftClickListener;
    }

    private boolean isOneButton = false;

    public void setOneButtonEnable(boolean isOneButton) {
        this.isOneButton = isOneButton;
    }

    public interface OnRightClickListener {
        void onClickRight();
    }

    public interface OnLeftClickListener {
        void onClickLeft();
    }
}
