package com.test.xcamera.view;

import android.app.Activity;
import android.app.Dialog;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.test.xcamera.R;


public class DeleteDialog {
    private final View mMenologyView;
    protected Dialog mDialog;
    private Activity mActivity;
    private SureOnClick onClick;
    private final TextView dialogText;
    private final TextView dialog_title;
    private final TextView dialog_content;

    public DeleteDialog(final Activity context) {
        this.mActivity = context;
        mMenologyView = View.inflate(context, R.layout.delete_dialog_layout, null);

        Button cancle_dialog = mMenologyView.findViewById(R.id.cancle_dialog);
        Button sure_dialog = mMenologyView.findViewById(R.id.sure_dialog);
        dialog_title = mMenologyView.findViewById(R.id.dialog_title);
        dialog_content = mMenologyView.findViewById(R.id.dialog_content);
        dialogText = mMenologyView.findViewById(R.id.dialogText);

        mDialog = new Dialog(context);
        mDialog.setContentView(mMenologyView);
        mDialog.setCanceledOnTouchOutside(false);

        WindowManager m = context.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用

        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
//        params.width = (int) (d.getWidth() * 0.9);
//
//        double v = d.getHeight() * 0.3 / 1920;
//
//        params.height = (int) (d.getHeight() * 0.3);
        mDialog.getWindow().setAttributes(params);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        sure_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClick != null) {
                    onClick.sure_button();
                    mDialog.dismiss();
                }
            }
        });

        cancle_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
    }

    public void showTitleAndContent(String title, String content, int titleVisable, int contentVisable) {
        dialog_title.setVisibility(titleVisable);
        dialog_content.setVisibility(contentVisable);
        dialog_title.setText(title);
        dialog_content.setText(content);
    }

    public void showDialog(SureOnClick sureOnClick) {
        this.onClick = sureOnClick;
        if (mDialog != null && !mDialog.isShowing() && !this.mActivity.isFinishing()) {
            mDialog.show();
        }
    }

    public void showDialog(SureOnClick sureOnClick, String context) {
        this.onClick = sureOnClick;
        if (mDialog != null && !mDialog.isShowing() && !this.mActivity.isFinishing()) {
            mDialog.show();
        }
        if (dialogText != null)
            dialogText.setText(context);
    }

    public void hideDlg() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    public interface SureOnClick {
        void sure_button();
    }
}
