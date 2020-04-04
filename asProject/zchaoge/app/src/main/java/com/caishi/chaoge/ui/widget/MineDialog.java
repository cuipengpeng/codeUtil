package com.caishi.chaoge.ui.widget;

import android.content.Context;
import android.view.Gravity;

import com.caishi.chaoge.R;
import com.caishi.chaoge.ui.widget.dialog.IDialog;
import com.caishi.chaoge.ui.widget.dialog.SYDialog;

public class MineDialog {

    private SYDialog dialog;
    private static MineDialog mineDialog;

    public static MineDialog newInstance() {
        if (mineDialog == null) {
            mineDialog = new MineDialog();
        }
        return mineDialog;
    }


    public void showSelectDialog(Context context,IDialog.OnBuildListener onBuildListener) {

        dialog = new SYDialog.Builder(context)
                .setDialogView(R.layout.dialog_select)
                .setWindowBackgroundP(0.5f)
                .setAnimStyle(R.style.AnimUp)
                .setCancelableOutSide(true)
                .setCancelable(true)
                .setBuildChildListener(onBuildListener)
                .setScreenWidthP(1.0f)
                .setGravity(Gravity.BOTTOM)
                .show();
    }



    public void showDateSelectDialog(Context context){


    }


    /**
     * 关闭弹窗 注意dialog=null;防止内存泄漏
     */
    private void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }


}
