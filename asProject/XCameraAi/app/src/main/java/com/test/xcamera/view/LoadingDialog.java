package com.test.xcamera.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.test.xcamera.R;


/**
 * Created by zll on 2019/5/30.
 */
public class LoadingDialog {

    private final Context context;
    //    private AlertDialog mDeleteDialog;
    private final View mMenologyView;
    protected Dialog mDialog;

    public LoadingDialog(final Context context, Activity activity) {
        this.context = context;
        mMenologyView = View.inflate(context, R.layout.loading_dialog_layout, null);
        mMenologyView.measure(0, 0);

        mDialog = new Dialog(context);
        mDialog.setContentView(mMenologyView);
        mDialog.setCanceledOnTouchOutside(false);

        WindowManager m = activity.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用

        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.width = mMenologyView.getMeasuredWidth();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mDialog.getWindow().setAttributes(params);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

    }

    public void showDialog() {

        Window window = mDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.dimAmount = 0f;
        window.setAttributes(lp);

        if (mDialog != null && !mDialog.isShowing()) {
            try {
                mDialog.show();
            } catch (Exception e) {
            }
        }
    }

    public void dimss() {
        if (mDialog != null && mDialog.isShowing()) {
            try {
                mDialog.dismiss();
            } catch (Exception e) {
            }
        }
    }
}
