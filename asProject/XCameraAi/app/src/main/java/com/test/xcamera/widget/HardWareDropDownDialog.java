package com.test.xcamera.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.test.xcamera.utils.DisplayUtils;
import com.test.xcamera.R;

import java.util.List;

/**
 * Created by DELL on 2019/7/5.
 */

public class HardWareDropDownDialog {

    public static Dialog showDropDialog(Context context,
                                          List<DialogItem> items) {
        LinearLayout dialogView = (LinearLayout) LayoutInflater.from(context)
                .inflate(R.layout.top_dialog_layout, null);

        final Dialog customDialog = new Dialog(context, R.style.top_dialog_style);


         ImageView iv_close=dialogView.findViewById(R.id.iv_close);
         iv_close.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                if(customDialog!=null&&customDialog.isShowing()) {
                    customDialog.dismiss();
                }
             }
         });



        RelativeLayout itemView;
        ImageView imageView;
        TextView title,tv_count;
        RelativeLayout.LayoutParams  params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, DisplayUtils.dpInt2px(context,100));
        int margin=DisplayUtils.dpInt2px(context,15);

//        for (DialogItem item : items) {

        for(int i=0;i<2;i++){
//            itemView = (RelativeLayout) LayoutInflater.from(context).inflate(
//                    item.getViewId(), null);
            itemView = (RelativeLayout) LayoutInflater.from(context).inflate(
                    items.get(i).getViewId(), null);
            itemView.setLayoutParams(params);

            if(i==0){
                params.setMargins(margin,margin,margin,margin);
            }else {
                params.setMargins(margin,0,margin,margin);
            }
            imageView=itemView.findViewById(R.id.iv_image);
            title =  itemView.findViewById(R.id.tv_ablum_name);
            tv_count =itemView.findViewById(R.id.tv_ablum_count);


//            Glide.with(context).load("").into(imageView);

            if(i==0){
                title.setText("SD卡相册");
                tv_count.setText(15+"");
            }
            if(i==1){
                title.setText("APP相册");
                tv_count.setText(20+"");
            }

            itemView.setOnClickListener(new OnDialogItemClick( items.get(i), customDialog));
            dialogView.addView(itemView);
        }


        Window window=customDialog.getWindow();
        WindowManager.LayoutParams localLayoutParams = window
                .getAttributes();
        window.getDecorView().setPadding(0,0,0,0);
        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        localLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        localLayoutParams.x = 0;
        localLayoutParams.y = 0;
        dialogView.setMinimumWidth(1000);
        customDialog.onWindowAttributesChanged(localLayoutParams);
        customDialog.setCanceledOnTouchOutside(true);
        customDialog.setCancelable(true);
        customDialog.setContentView(dialogView);

        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (!activity.isFinishing()) {
                customDialog.show();
            }
        }

        return customDialog;
    }
}
