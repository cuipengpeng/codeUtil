package com.test.xcamera.view.basedialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.test.xcamera.R;


public abstract class BaseDialog implements View.OnClickListener {
    private View baseDialogRootView;
    protected Dialog mDialog;

    protected TextView dialogTitle;
    protected TextView diaLogContent;
    protected TextView dialogSure;
    protected TextView dialogCancel;
    protected final View zhongJian;

    public BaseDialog(final Activity context) {

        baseDialogRootView = View.inflate(context, R.layout.dialog_base_layout, null);
        baseDialogRootView.measure(0, 0);
        int measuredHeight = baseDialogRootView.getMeasuredHeight();
        int measuredWidth = baseDialogRootView.getMeasuredWidth();
        dialogTitle = baseDialogRootView.findViewById(R.id.dialogTitle);
        diaLogContent = baseDialogRootView.findViewById(R.id.diaLogContent);
        dialogSure = baseDialogRootView.findViewById(R.id.dialogSure);
        dialogCancel = baseDialogRootView.findViewById(R.id.dialogCancel);
        zhongJian = baseDialogRootView.findViewById(R.id.zhongJian);

        mDialog = new Dialog(context);
        mDialog.setContentView(baseDialogRootView);
        mDialog.setCanceledOnTouchOutside(false);

        WindowManager m = context.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用

        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
        if (d.getWidth() > d.getHeight()) {
            params.width = d.getHeight() - 150;
        } else {
            params.width = d.getWidth() - 150;
        }
        params.gravity = Gravity.CENTER;
        mDialog.getWindow().setAttributes(params);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogSure.setOnClickListener(this);
        dialogCancel.setOnClickListener(this);
        back(mDialog);
    }

    protected void back(Dialog mDialog) {

    }

    public void showVisibilityTitleDialog(int titleSize, String titleContext) {
        dialogTitle.setVisibility(View.VISIBLE);
        if (mDialog != null && !mDialog.isShowing()) {
            mDialog.show();
        }
    }

    public void showGoneTitleDialog() {
        dialogTitle.setVisibility(View.GONE);
        if (mDialog != null && !mDialog.isShowing()) {
            mDialog.show();
        }
    }
    public void showGoneDialogCancel() {
        if(dialogCancel!=null){
            dialogCancel.setVisibility(View.GONE);
            zhongJian.setVisibility(View.GONE);
        }

    }

    public void setSureButtonContent(String sureText, int sureTextSize, int sueTextColor) {

    }

    public void setCancelButtonContent(String sureText, int sureTextSize, int sueTextColor) {

    }

    public void setDialogContent(int contentTextSize, String contentText) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.dialogSure)
            sureClick(mDialog);
        else
            cancelClick(mDialog);
    }

    public abstract void sureClick(Dialog mDialog);

    public abstract void cancelClick(Dialog mDialog);


}
