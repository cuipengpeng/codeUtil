package com.test.xcamera.view.basedialog.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;

import com.test.xcamera.view.basedialog.BaseDialog;

public class PreviewCameraDisconnectDialog extends BaseDialog {
    private SureClick sureClick;

    public PreviewCameraDisconnectDialog(Activity context) {
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
        diaLogContent.setTextSize(contentTextSize);
        diaLogContent.setText(contentText);
    }

    @Override
    public void showGoneTitleDialog() {
        super.showGoneTitleDialog();
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
