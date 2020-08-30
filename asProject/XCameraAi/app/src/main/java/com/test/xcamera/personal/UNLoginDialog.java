package com.test.xcamera.personal;

import android.app.Activity;
import android.app.Dialog;

import com.test.xcamera.view.basedialog.BaseDialog;

public class UNLoginDialog extends BaseDialog {
    private ButtonClickListener buttonClickListener;

    public UNLoginDialog(Activity context) {
        super(context);
    }

    @Override
    public void setDialogContent(int contentTextSize, String contentText) {
        diaLogContent.setTextSize(contentTextSize);
        diaLogContent.setText(contentText);
    }

    @Override
    public void showVisibilityTitleDialog(int titleSize, String titleContext) {
        dialogTitle.setTextSize(titleSize);
        dialogTitle.setText(titleContext);
        super.showVisibilityTitleDialog(titleSize,titleContext);
    }

    @Override
    public void showGoneTitleDialog() {
        super.showGoneTitleDialog();
    }
    @Override
    public void showGoneDialogCancel() {
        super.showGoneDialogCancel();
    }

    @Override
    public void sureClick(Dialog mDialog) {
        if (buttonClickListener != null) {
            buttonClickListener.sureButton(mDialog);
        }
        mDialog.dismiss();
    }

    @Override
    public void cancelClick(Dialog mDialog) {
        if (buttonClickListener != null) {
            buttonClickListener.cancelButton(mDialog);
        }
        mDialog.dismiss();
    }

    public void setButtonClickListener(ButtonClickListener buttonClickListener) {
        this.buttonClickListener = buttonClickListener;
    }

    public interface ButtonClickListener {
        void sureButton(Dialog mDialog);

        void cancelButton(Dialog mDialog);
    }
}
