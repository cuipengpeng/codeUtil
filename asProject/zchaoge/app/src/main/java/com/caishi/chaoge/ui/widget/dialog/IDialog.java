package com.caishi.chaoge.ui.widget.dialog;

import android.support.v4.app.FragmentManager;
import android.view.View;


public interface IDialog {

    void dismiss();

    interface OnBuildListener {
        /**
         * @param dialog    IDialog
         * @param view      DialogView
         * @param layoutRes Dialog的资源文件 如果一个Activity里有多个dialog 可以通过layoutRes来区分
         */
        void onBuildChildView(IDialog dialog, View view, int layoutRes, FragmentManager fragmentManager);
    }

    interface OnClickListener {
        void onClick(IDialog dialog);
    }
}
