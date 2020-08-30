//package com.meetvr.aicamera.widget;
//
//import android.app.Activity;
//import android.app.Dialog;
//import android.content.Context;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.meetvr.aicamera.utils.DisplayUtils;
//import com.meetvr.aicamera.R;
//
//import java.util.List;
//
///**
// * Created by mz on 2019/7/2.
// */
//
//public class BottomDialog  extends  Dialog{
//
//
//
//
//    public BottomDialog( @android.support.annotation.NonNull Context context, int themeResId) {
//        super(context,themeResId );
//    }
//
//    public static Dialog showBottomDialog(BottomDialog customDialog,Context context,
//                                          List<DialogItem> items) {
//        LinearLayout dialogView = (LinearLayout) LayoutInflater.from(context)
//                .inflate(R.layout.bottom_dialog_layout, null);
//
////        final Dialog customDialog = new Dialog(context, R.style.bottom_dialog_style);
//        RelativeLayout itemView;
//
//        //todo
//        TextView textView,tv_cancle;
//        ImageView imageView;
//        RelativeLayout.LayoutParams  params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, DisplayUtils.dpInt2px(context,65));
//        int margin=DisplayUtils.dpInt2px(context,15);
//
//        int i=0;
//        for (DialogItem item : items) {
//            itemView = (RelativeLayout) LayoutInflater.from(context).inflate(
//                    item.getViewId(), null);
//            itemView.setLayoutParams(params);
//
//            if(i==0){
//                params.setMargins(margin,margin,margin,margin);
//            }else {
//                params.setMargins(margin,0,margin,margin);
//            }
//            textView =  itemView.findViewById(R.id.bottom_item_text);
//            tv_cancle =itemView.findViewById(R.id.tv_cancle);
//            imageView=itemView.findViewById(R.id.iv_right);
//            int imageResource=item.getResourceId();
//            if(imageResource!=0){
//                textView.setVisibility(View.VISIBLE);
//                imageView.setVisibility(View.VISIBLE);
//                tv_cancle.setVisibility(View.GONE);
//                textView.setText(item.getText());
//                imageView.setImageResource(item.getResourceId());
//            }else{
//                textView.setVisibility(View.GONE);
//                imageView.setVisibility(View.GONE);
//                tv_cancle.setVisibility(View.VISIBLE);
//                //todo
//                tv_cancle.setText("取消");
//            }
//
//
//            itemView.setOnClickListener(new OnDialogItemClick(item, customDialog));
//            dialogView.addView(itemView);
//            i++;
//        }
//
//
//        Window  window=customDialog.getWindow();
//        WindowManager.LayoutParams localLayoutParams = window
//                .getAttributes();
//        window.getDecorView().setPadding(0,0,0,0);
//        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
//        localLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
//        localLayoutParams.x = 0;
//        localLayoutParams.y = -1000;
//        localLayoutParams.gravity = 80;
//        dialogView.setMinimumWidth(1000);
//        customDialog.onWindowAttributesChanged(localLayoutParams);
//        customDialog.setCanceledOnTouchOutside(true);
//        customDialog.setCancelable(true);
//        customDialog.setContentView(dialogView);
//
//        if (context instanceof Activity) {
//            Activity activity = (Activity) context;
//            if (!activity.isFinishing()) {
//                customDialog.show();
//            }
//        }
//
//        return customDialog;
//    }
//
//    public interface DialogOnKeyDownListener {
//        void onKeyDownListener(int keyCode, KeyEvent event);
//    }
//
//    private DialogOnKeyDownListener dialogOnKeyDownListener;
//    public void setDialogOnKeyDownListener(DialogOnKeyDownListener dialogOnKeyDownListener) {
//        this.dialogOnKeyDownListener=dialogOnKeyDownListener;
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(dialogOnKeyDownListener!=null) {
//            dialogOnKeyDownListener.onKeyDownListener(keyCode, event);
//
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//}
