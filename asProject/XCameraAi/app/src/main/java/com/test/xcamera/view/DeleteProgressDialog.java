package com.test.xcamera.view;

import android.app.Activity;
import android.app.Dialog;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.xcamera.R;


public class DeleteProgressDialog {
    private final View deleteMediaProgressLayout;
    private final Activity context;
    protected Dialog mDialog;
    private final ArcProgress deleteMediaFileProgess;
    private final TextView currentDeleteCountTextView;

    public DeleteProgressDialog(final Activity context) {
        this.context = context;
        deleteMediaProgressLayout = View.inflate(context, R.layout.delete_progress_dialog_layout, null);
        deleteMediaFileProgess = deleteMediaProgressLayout.findViewById(R.id.downloadMediaFilePg);
        ImageView dismissDialog = deleteMediaProgressLayout.findViewById(R.id.dismissDialog);

//        deleteMediaFileProgess.setOnCenterDraw(new OnImageCenter(context, R.mipmap.delete_progress));
        currentDeleteCountTextView = deleteMediaProgressLayout.findViewById(R.id.currentDeleteCount);


        mDialog = new Dialog(context);
        mDialog.setContentView(deleteMediaProgressLayout);
        mDialog.setCanceledOnTouchOutside(false);

        WindowManager m = context.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用

        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
        params.gravity = Gravity.CENTER;

        mDialog.getWindow().setAttributes(params);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dismissDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        });
    }

    public void showDialog(int progressMax) {
        deleteMediaFileProgess.setMax(progressMax);
        if (mDialog != null && !mDialog.isShowing()) {
            mDialog.show();
        }
    }

    public void refreshProgress(int currentCount) {
        String format = String.format(context.getResources().getString(R.string.current_delete_count), currentCount);
        currentDeleteCountTextView.setText(format);
        deleteMediaFileProgess.setProgress(currentCount);
    }

    public void dismissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }
}
