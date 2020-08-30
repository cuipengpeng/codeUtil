package com.test.xcamera.view.basedialog.dialog;

import android.app.Activity;
import android.app.Dialog;

import com.test.xcamera.view.basedialog.BaseDialog;

/**
 * Created by zhouxuecheng on
 * Create Time 2020/3/31
 * e-mail zhouxuecheng1991@163.com
 */

public class CancelDownDialog extends BaseDialog {
    private CancelListener cancelListener;

    public CancelDownDialog(Activity context) {
        super(context);
    }

    @Override
    public void showGoneTitleDialog() {
        super.showGoneTitleDialog();
    }

    @Override
    public void setDialogContent(int contentTextSize, String contentText) {
        super.setDialogContent(contentTextSize, contentText);
        diaLogContent.setText(contentText);
        diaLogContent.setTextSize(contentTextSize);
    }

    @Override
    public void sureClick(Dialog mDialog) {
        if (cancelListener != null) {
            cancelListener.cancel(mDialog);
        }
    }

    @Override
    public void cancelClick(Dialog mDialog) {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    public void dismissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    public void setCancelListener(CancelListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    public interface CancelListener {
        void cancel(Dialog mDialog);
    }
}
