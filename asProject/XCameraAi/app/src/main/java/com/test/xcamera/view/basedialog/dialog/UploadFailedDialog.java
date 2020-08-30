package com.test.xcamera.view.basedialog.dialog;

import android.app.Activity;
import android.app.Dialog;

import com.test.xcamera.view.basedialog.BaseDialog;

public class UploadFailedDialog extends BaseDialog {
    private SureClick sureClick;

    public UploadFailedDialog(Activity context) {
        super(context);
    }

    @Override
    public void setDialogContent(int contentTextSize, String contentText) {
        super.setDialogContent(contentTextSize, contentText);
        diaLogContent.setText(contentText);
        diaLogContent.setTextSize(contentTextSize);
    }

    @Override
    public void setSureButtonContent(String sureText, int sureTextSize, int sueTextColor) {
        super.setSureButtonContent(sureText, sureTextSize, sueTextColor);
        dialogSure.setText(sureText);
    }

    @Override
    public void showVisibilityTitleDialog(int titleSize, String titleContext) {
        super.showVisibilityTitleDialog(titleSize, titleContext);
        dialogTitle.setTextSize(titleSize);
        dialogTitle.setText(titleContext);
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
