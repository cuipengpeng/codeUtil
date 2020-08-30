package com.test.xcamera.utils;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.test.xcamera.R;

/**
 * Created by smz on 2020/2/12.
 * <p>
 * 标准一个按钮的弹框
 */

public class StandardLoadingDlg extends StandardAffirmDlg {

    /**
     * 弹出对话框
     *
     * @param rotate 旋转的度数 默认0度
     */
    public void show(Context context, int rotate) {
        super.context = context;
        if (dialog != null) {
            dialog.show();
            return;
        }

        dialog = new Dialog(context, R.style.dialog);
        dialog.setCancelable(true);
        dialog.show();
        super.rotateDlg(136);

        View wrap = View.inflate(context, R.layout.dlg_loading, null);

        View view = wrap.findViewById(R.id.progress);
        dialog.setContentView(wrap);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "rotation", 359f);
        objectAnimator.setDuration(1000);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.start();

        rotate(rotate);
    }
}
