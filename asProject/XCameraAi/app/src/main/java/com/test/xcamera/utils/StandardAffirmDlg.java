package com.test.xcamera.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.xcamera.R;

/**
 * Created by smz on 2020/2/12.
 * <p>
 * 标准一个按钮的弹框
 */

public class StandardAffirmDlg {
    public DlgInterface listener;
    public Dialog dialog;
    protected View view;
    protected Context context;

    /**
     * 旋转弹框
     *
     * @param rotate 旋转的度数
     */
    public void rotate(int rotate) {
        if (dialog != null && view != null) {
            view.setRotation(rotate);
        }
    }

    protected void rotateDlg(int width) {
        if (dialog == null) return;
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = lp.height = ViewUitls.dp2px(context, width);
        int size[] = ViewUitls.getScreenSize();
        lp.x = (size[0] - lp.width) / 2;
        lp.y = (size[1] - lp.height) / 2;
        dialogWindow.setAttributes(lp);
    }


    /**
     * 弹出对话框
     *
     * @param info 提示信息
     * @param icon 提示图标
     */
    public void show(Context context, String info, int icon) {
        this.show(context, info, icon, 0, null);
    }

    public void show(Context context, String info, int icon, int rotate) {
        this.show(context, info, icon, rotate, null);
    }

    public void clear() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
            listener = null;
        }
    }

    /**
     * 弹出对话框
     *
     * @param info   提示信息
     * @param icon   提示图标
     * @param rotate 旋转的度数 默认0度
     */
    public void show(Context context, String info, int icon, int rotate, DlgInterface listener) {
        this.context = context;
        if (dialog != null) {
            dialog.dismiss();
        }

        dialog = new Dialog(context, R.style.dialog);
        dialog.setCancelable(false);
        dialog.show();
        rotateDlg(240);

        View wrap = View.inflate(context, R.layout.dlg_normal, null);
        view = wrap.findViewById(R.id.content);
        dialog.setContentView(wrap);

        rotate(rotate);

        ((TextView) wrap.findViewById(R.id.info)).setText(info);
        ((ImageView) wrap.findViewById(R.id.icon)).setImageResource(icon);
        wrap.findViewById(R.id.affirm).setOnClickListener((v) -> {
            dialog.dismiss();
            dialog = null;
            if (listener != null)
                listener.onAffirm();
        });
    }

    /**
     * 点击按钮的回调
     */
    public void setDlgInterface(DlgInterface listener) {
        this.listener = listener;
    }

    public interface DlgInterface {
        void onAffirm();
    }
}
