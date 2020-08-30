package com.test.xcamera.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.test.xcamera.R;
import com.test.xcamera.utils.LogAccessory;

/**
 * 日志view
 */
public class LogDataLayout extends LinearLayout{
    private LogViewCallBack mCallBack;
    public TextView mUSBTextTest;
    public ImageView mBack;
    public ImageView mClear;
    public LogDataLayout(Context context) {
        this(context, null);
        init(context);
    }

    public LogDataLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private void init(Context context) {
        View root = inflate(context, R.layout.view_log_data, this);
        mUSBTextTest=findViewById(R.id.usb_text_tip);
        mBack=findViewById(R.id.usb_back);
        mBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCallBack!=null){
                    mCallBack.onCallBack();
                }
            }
        });
        mClear=findViewById(R.id.usb_clear);
        mClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LogAccessory.getInstance().setClearText();

            }
        });
        LogAccessory.getInstance().setText(mUSBTextTest);

    }
    public void showVideoEditPanel(int tag){

    }
    public void setCallBack(LogViewCallBack callBack) {
        this.mCallBack = callBack;
    }
    public interface LogViewCallBack{
        void onCallBack();
    }





}
