package com.test.xcamera.utils;

import android.text.TextUtils;
import android.widget.TextView;

import com.test.xcamera.BuildConfig;
import com.moxiang.common.logging.Logcat;

public class LogAccessory {
    private static LogAccessory instance;

    private TextView mText;
    private boolean mIsShowViewLog=false ;



    public static LogAccessory getInstance() {
        if(instance==null){
            instance=new LogAccessory();
        }
        return instance;
    }
    private LogAccessory(){
        showView();
    }

    private void showView(){

    }
    public void setLogOpen(String tag){
        mIsShowViewLog = !TextUtils.isEmpty(tag) && "1+1=0".equals(tag);
    }
    public void setIsShowViewLog(boolean mIsShowViewLog) {
        this.mIsShowViewLog = mIsShowViewLog;
    }

    public boolean isIsShowViewLog() {
        if(BuildConfig.DEBUG){
            return mIsShowViewLog;

        }
        return false;
    }

    public void setText(TextView mText) {
        this.mText = mText;
    }
    public void setClearText() {
        mLogMsg="";
        if(mText!=null){
            mText.setText("");
        }

    }

    public void showLog(int type, String msg){
        showLog(msg);
    }
    String mLogMsg;
    long time=0;
    public void showLog(final String msg){
        if(!TextUtils.isEmpty(mLogMsg)&&mLogMsg.length()>10000){
            mLogMsg=mLogMsg.substring(0,1000);
        }
        String em="";
        if(msg!=null&&(msg.contains("断开")||msg.contains("连接 "))){
            em="\n\n\n";

        }

        mLogMsg=em+"club_usb:"+msg+"\n"+mLogMsg;

        if(mText!=null&&mIsShowViewLog ){
            time=System.currentTimeMillis();
            AppExecutors.getInstance().mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    mText.setText(mLogMsg);
                }
            });
        }
        Logcat.i("club_usb:"+msg);
    }


}
