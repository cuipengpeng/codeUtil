package com.caishi.chaoge.ui.widget.dialog;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.caishi.chaoge.R;
import com.caishi.chaoge.ui.widget.RotateLoading;

public class LoadDialog {

    private SYDialog loadDialog;
    private RotateLoading rl_load;

    public static LoadDialog newInstance() {
        return new LoadDialog();
    }


    /**
     * 等待dialog
     */
    public void showLoadDialog(final Context context, final String hint) {
        loadDialog = new SYDialog.Builder(context)
                .setDialogView(R.layout.dialog_load)
                .setScreenWidthP(0.3f)
                .setScreenHeightP(0.3f)
                .setWindowBackgroundP(0.6f)
                .setGravity(Gravity.CENTER)
                .setCancelable(true)
                .setCancelableOutSide(false)
//                .setAnimStyle(R.style.AnimUp)
                .setBuildChildListener(new IDialog.OnBuildListener() {

                    @Override
                    public void onBuildChildView(final IDialog dialog, View view, int layoutRes, FragmentManager fragmentManager) {
                        rl_load = view.findViewById(R.id.rl_load);
                        TextView tv_load_hint = view.findViewById(R.id.tv_load_hint);
                        if (!TextUtils.isEmpty(hint))
                            tv_load_hint.setText(hint);
                        rl_load.start();
                    }
                }).show();
    }

    /**
     * 关闭弹窗 注意dialog=null;防止内存泄漏
     */
    public void dismissDialog() {
        if (rl_load != null)
            rl_load.stop();
        if (loadDialog != null) {
            loadDialog.dismiss();
            loadDialog = null;
        }
    }


}
