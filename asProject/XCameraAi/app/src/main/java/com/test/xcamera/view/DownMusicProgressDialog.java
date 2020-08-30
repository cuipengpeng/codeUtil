package com.test.xcamera.view;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.test.xcamera.R;


public class DownMusicProgressDialog {
    private View deleteMediaProgressLayout;
    private Context context;
    protected Dialog mDialog;
    private ArcProgress downloadMediaFilePg;
    private TextView mTvMsg;
    private DialogInterface.OnDismissListener mOnDismissListener;

    public void setOnDismissListener(DialogInterface.OnDismissListener mOnDismissListener) {
        this.mOnDismissListener = mOnDismissListener;
    }

    public DownMusicProgressDialog(final Context context) {
        if (context == null) {
            return;
        }
        this.context = context;
        deleteMediaProgressLayout = View.inflate(context, R.layout.down_music_progress_dialog_layout, null);
        downloadMediaFilePg = deleteMediaProgressLayout.findViewById(R.id.downloadMediaFilePg);

//        downloadMediaFilePg.setOnCenterDraw(new OnImageCenter(context, 0));
        mTvMsg = deleteMediaProgressLayout.findViewById(R.id.tv_msg);

        mDialog = new Dialog(context);
        mDialog.setContentView(deleteMediaProgressLayout);
        mDialog.setCanceledOnTouchOutside(false);

        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
        params.gravity = Gravity.CENTER;

        mDialog.getWindow().setAttributes(params);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialogInterface) {
//                if(mOnDismissListener!=null){
//                    mOnDismissListener.onDismiss(dialogInterface);
//                }
//            }
//        });
    }

    public void setDialogRotation(float mRotation) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(deleteMediaProgressLayout, "rotation", 0f, mRotation);
        anim.setDuration(1);
        anim.start();
    }

    public void showDialog(int progressMax, String msg) {
        showDialog(progressMax);
        if (mTvMsg != null) {
            mTvMsg.setText(msg);
        }
    }

    public void setTvMsg(String msg) {
        if (mTvMsg != null) {
            mTvMsg.setText(msg);
        }
    }

    public void setCancelable(boolean isCance) {
        this.mDialog.setCancelable(isCance);
    }

    public void showDialog(int progressMax) {
        downloadMediaFilePg.setMax(progressMax);
        Log.i("DOWN_TESTSS", "showDialog: " + progressMax);
        if (mDialog != null && !mDialog.isShowing()) {
            mDialog.show();
        }
    }

    public void refreshProgress(double mProgeress, double downCount) {
        String format = String.format(context.getResources().getString(R.string.down_count), (int) mProgeress);
//        currentDeleteCountTextView.setText(format + "%");
        downloadMediaFilePg.setProgress((int) downCount);
        downloadMediaFilePg.setDownloadProgress(format + "%");

    }

    public void dismissDialog() {
        downloadMediaFilePg.setProgress(0);
        downloadMediaFilePg.setMax(0);
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }

    }

    public void destroy() {
        mDialog = null;
        context = null;
    }

    public boolean isShowing() {
        if (mDialog != null) {
            return mDialog.isShowing();
        }
        return false;
    }
}
