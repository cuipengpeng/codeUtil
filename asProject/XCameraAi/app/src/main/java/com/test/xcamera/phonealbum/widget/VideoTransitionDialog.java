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

public class VideoTransitionDialog extends Dialog {
    public VideoTransitionDialog(@NonNull Context context,int type) {
        super(context,R.style.ActionSheetDialogStyle);
        View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_video_transition_layout, null);
        //初始化控件
        TextView cancel = inflate.findViewById(R.id.tv_myVideoEditActivity_dialog_cancel);
        TextView exitEdit = inflate.findViewById(R.id.tv_myVideoEditActivity_dialog_exitEdit);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        exitEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnTransitionDialog!=null){
                    mOnTransitionDialog.onTransitionConfirm();
                }
                dismiss();
            }
        });
        //将布局设置给Dialog
        setContentView(inflate);
        //获取当前Activity所在的窗体
        Window dialogWindow = getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity( Gravity.BOTTOM);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = 0;//设置Dialog距离底部的距离
        lp.width = ScreenUtils.getScreenWidth(context);
//       将属性设置给窗体
        dialogWindow.setAttributes(lp);
        if(type==1){
            exitEdit.setText("删除");
            exitEdit.setBackgroundResource(R.drawable.circle_corner_white_bg_normal_2dp);
            exitEdit.setTextColor(context.getResources().getColorStateList(R.color.appThemeColor));
            cancel.setTextColor(context.getResources().getColorStateList(R.color.wifi_hint_color_dialog));
        }
    }
    OnTransitionDialog mOnTransitionDialog;

    public void setOnTransitionDialog(OnTransitionDialog mOnTransitionDialog) {
        this.mOnTransitionDialog = mOnTransitionDialog;
    }

    public interface OnTransitionDialog{
        void onTransitionConfirm();
    }
}
