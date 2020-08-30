package com.test.xcamera.view;

import android.app.Activity;
import android.app.Dialog;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.test.xcamera.R;


public class DeleteAlbumDialog {
    private final View deleteMediaProgressLayout;
    private final Activity context;
    protected Dialog mDialog;
    private final TextView deleteTextView;
    private final TextView cancelTextView;

    public DeleteAlbumDialog(final Activity context) {
        this.context = context;
        deleteMediaProgressLayout = View.inflate(context, R.layout.delete_progress_dialog_layout_new, null);
        deleteTextView = deleteMediaProgressLayout.findViewById(R.id.delete);
        cancelTextView = deleteMediaProgressLayout.findViewById(R.id.cancel);

        mDialog = new Dialog(context);
        mDialog.setContentView(deleteMediaProgressLayout);
        mDialog.setCanceledOnTouchOutside(false);

        WindowManager m = context.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用

        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
        params.x = 10;
        params.y = 180;
        params.gravity = Gravity.CENTER;
        mDialog.getWindow().setGravity(Gravity.BOTTOM);

        mDialog.getWindow().setAttributes(params);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        });
    }

    public void showDialog(int deleteCount, Delete delete) {
        if (mDialog != null && !mDialog.isShowing()) {
            mDialog.show();
        }
        String deleteCountStr = context.getString(R.string.delete_count, deleteCount);
        deleteTextView.setText(deleteCountStr);
        deleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (delete != null) {
                    delete.delete();
                }
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        });
    }

    public interface Delete {
        void delete();
    }

    public void refreshProgress(int currentCount) {
//        String format = String.format(context.getResources().getString(R.string.current_delete_count), currentCount);
//        currentDeleteCountTextView.setText(format);
//        deleteMediaFileProgess.setProgress(currentCount);
    }

    public void dismissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }
}
