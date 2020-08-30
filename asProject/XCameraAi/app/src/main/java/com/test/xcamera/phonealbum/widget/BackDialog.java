package com.test.xcamera.phonealbum.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.editvideo.ScreenUtils;
import com.test.xcamera.R;


public class BackDialog extends Dialog {
    private View.OnClickListener mOnClickListener;
    public BackDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        initView(context);
    }

    public void setOnClickListener(View.OnClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }

    public void initView(Context context){
        View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_myideoedit_actvity, null);
        TextView cancel = inflate.findViewById(R.id.tv_myVideoEditActivity_dialog_cancel);
        TextView exitEdit = inflate.findViewById(R.id.tv_myVideoEditActivity_dialog_exitEdit);

        cancel.setOnClickListener(v -> this.dismiss());
        exitEdit.setOnClickListener(v -> {
            this.dismiss();
            if(mOnClickListener!=null){
                mOnClickListener.onClick(v);
            }
        });
        this.setContentView(inflate);
        Window dialogWindow = this.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = 20;
        lp.width = ScreenUtils.getScreenWidth(context);
        dialogWindow.setAttributes(lp);
    }

}
