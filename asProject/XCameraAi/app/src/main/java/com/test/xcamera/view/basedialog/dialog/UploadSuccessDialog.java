package com.test.xcamera.view.basedialog.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;

import com.test.xcamera.view.basedialog.BaseDialog;

public class UploadSuccessDialog extends BaseDialog {
    private SureClick sureClick;

    public UploadSuccessDialog(Activity context) {
        super(context);

        dialogCancel.setVisibility(View.GONE);
        zhongJian.setVisibility(View.GONE);
    }

    @Override
    protected void back(Dialog mDialog) {
        super.back(mDialog);
        //设置点击屏幕不消失
        mDialog.setCanceledOnTouchOutside(false);
        //设置点击返回键不消失
        mDialog.setCancelable(false);
    }

    @Override
    public void setDialogContent(int contentTextSize, String contentText) {
        super.setDialogContent(contentTextSize, contentText);
        diaLogContent.setText(contentText);
        diaLogContent.setTextSize(contentTextSize);
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

    }

    public void setSureClick(SureClick sureClick) {
        this.sureClick = sureClick;
    }

    public interface SureClick {
        void onSure(Dialog mDialog);
    }
}
