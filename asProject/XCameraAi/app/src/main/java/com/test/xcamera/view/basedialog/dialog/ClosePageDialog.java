package com.test.xcamera.view.basedialog.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;

import com.test.xcamera.view.basedialog.BaseDialog;

public class ClosePageDialog extends BaseDialog {
    private SureClick sureClick;

    public ClosePageDialog(Activity context) {
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
    protected void back(Dialog mDialog) {
        super.back(mDialog);
        //设置点击屏幕不消失
        mDialog.setCanceledOnTouchOutside(false);
        //设置点击返回键不消失
        mDialog.setCancelable(false);
    }

    @Override
    public void setDialogContent(int contentTextSize, String contentText) {
        diaLogContent.setText(contentText);
        diaLogContent.setTextSize(contentTextSize);
    }

    @Override
    public void showGoneTitleDialog() {
        super.showGoneTitleDialog();
    }

    public void setSureClick(SureClick sureClick) {
        this.sureClick = sureClick;
    }

    public interface SureClick {
        void onSure(Dialog mDialog);
    }
}
