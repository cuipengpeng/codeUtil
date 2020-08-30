package com.test.xcamera.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.test.xcamera.R;

/**
 * Created by smz on 2019/11/8.
 */

public class UploadDialog  {

    private TextView tv_dialog_title;
    private TextView tv_dialog_content;
    private TextView tv_cancale;
    private View view_middle;
    private TextView tv_confirm;
    private final Dialog mDialog;


    public UploadDialog(Context context, int layout, int style) {
//        super(context, style);
        View view = View.inflate(context, layout, null);
        mDialog = new Dialog(context);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(false);
//        setContentView(layout);
//        Window window = context.getWindow();
//        WindowManager.LayoutParams params = window.getAttributes();
//        params.gravity = Gravity.CENTER;
//        window.setAttributes(params);
        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        mDialog.getWindow().setAttributes(params);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        initView(view);
        initClick();
    }

    private void initClick() {
        tv_cancale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               callback.onCancleListener(v);
            }
        });

        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              callback.onConfirmListener(v);
            }
        });
    }

    private void initView(View view) {
        tv_dialog_title = view.findViewById(R.id.tv_dialog_title);
        tv_dialog_content = view.findViewById(R.id.tv_dialog_content);
        tv_cancale = view.findViewById(R.id.tv_cancale);
        view_middle = view.findViewById(R.id.view_middle);
        tv_confirm = view.findViewById(R.id.tv_confirm);
    }

    public  void  setTitle(String str){
        tv_dialog_title.setText(str);
    }

    public  void  setContent(String str){
        tv_dialog_content.setText(str);
    }

    public void setMiddleLineIsVisbis(int visbis){
        view_middle.setVisibility(visbis);
    }


    private DialogClickListener callback;
    public void setDialogListener(DialogClickListener callback){
        this.callback=callback;
    }

    public void   show(){
        if(mDialog!=null){
            mDialog.show();
        }
    }
    public void dismiss(){
        if(mDialog!=null&&mDialog.isShowing()){
            mDialog.dismiss();
        }
    }

    public boolean   isShowing(){
        return mDialog!=null&&mDialog.isShowing();
    }
    public interface DialogClickListener{
        void  onCancleListener(View v);
        void onConfirmListener(View v);
    }

}
