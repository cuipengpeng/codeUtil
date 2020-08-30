package com.test.xcamera.view.basedialog.dialog;

import android.app.Activity;
import android.app.Dialog;

import com.test.xcamera.view.basedialog.BaseDialog;

public class Net4GDialog extends BaseDialog {
    private SureClick sureClick;

    public Net4GDialog(Activity context) {
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
        if (sureClick != null) {
            sureClick.onSure(mDialog);
        }
    }

    @Override
    public void cancelClick(Dialog mDialog) {
        if (sureClick != null) {
            sureClick.onCancel(mDialog);
        }
    }

    public void setSureClick(SureClick sureClick) {
        this.sureClick = sureClick;
    }

    public interface SureClick {
        void onSure(Dialog mDialog);

        void onCancel(Dialog mDialog);
    }
}
