package com.test.xcamera.view.basedialog.dialog;

import android.app.Activity;
import android.app.Dialog;

import com.test.xcamera.view.basedialog.BaseDialog;

public class AlbumDeleteDialog extends BaseDialog {
    private ButtonClickListener buttonClickListener;

    public AlbumDeleteDialog(Activity context) {
        super(context);
    }

    @Override
    public void setDialogContent(int contentTextSize, String contentText) {
        diaLogContent.setTextSize(contentTextSize);
        diaLogContent.setText(contentText);
    }

    @Override
    public void showGoneTitleDialog() {
        super.showGoneTitleDialog();
    }

    @Override
    public void sureClick(Dialog mDialog) {
        if (buttonClickListener != null) {
            buttonClickListener.sureButton(mDialog);
        }
    }

    @Override
    public void cancelClick(Dialog mDialog) {
        if (buttonClickListener != null) {
            buttonClickListener.cancelButton(mDialog);
        }
    }

    public void setButtonClickListener(ButtonClickListener buttonClickListener) {
        this.buttonClickListener = buttonClickListener;
    }

    public interface ButtonClickListener {
        void sureButton(Dialog mDialog);

        void cancelButton(Dialog mDialog);
    }
}
