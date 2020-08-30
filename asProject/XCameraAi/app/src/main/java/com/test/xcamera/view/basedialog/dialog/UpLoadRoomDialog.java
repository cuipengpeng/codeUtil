package com.test.xcamera.view.basedialog.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;

import com.test.xcamera.view.basedialog.BaseDialog;

public class UpLoadRoomDialog extends BaseDialog {
    private SureClick sureClick;

    public UpLoadRoomDialog(Activity context) {
        super(context);
        dialogCancel.setVisibility(View.GONE);
        zhongJian.setVisibility(View.GONE);
    }

    @Override
    public void sureClick(Dialog mDialog) {
        if (sureClick != null) {
            sureClick.onSure(mDialog);
        }
    }

    @Override
    public void cancelClick(Dialog mDialog) {

    }

    @Override
    public void setDialogContent(int contentTextSize, String contentText) {
        diaLogContent.setText(contentText);
        diaLogContent.setTextSize(contentTextSize);
    }

    @Override
    public void showVisibilityTitleDialog(int titleSize, String titleContext) {
        super.showVisibilityTitleDialog(titleSize, titleContext);
        dialogTitle.setText(titleContext);
        dialogTitle.setTextSize(titleSize);
    }

    public void setSureClick(SureClick sureClick) {
        this.sureClick = sureClick;
    }

    public interface SureClick {
        void onSure(Dialog mDialog);
    }
}
