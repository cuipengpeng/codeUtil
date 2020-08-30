package com.test.xcamera.utils;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AsyncCopyAssertRunnable implements Runnable {

    private Context mContext;
    private String mAssertRelativePath;
    public AsyncCopyAssertRunnable(Context context, String assertRelativePath){
        this.mContext = context;
        this.mAssertRelativePath = assertRelativePath;
    }

    @Override
    public void run() {
        InputStream inStream = null;
        OutputStream outStream = null;
        try {
            String sufferFix = mAssertRelativePath.substring(mAssertRelativePath.lastIndexOf("."));
            File cacheDir = new File(Constants.sampleVideoLocalPath);
            if(!cacheDir.exists()){
                cacheDir.mkdirs();
            }
            File desFile = new File(cacheDir,Md5Util.getMD5(mAssertRelativePath)+sufferFix);
            if(!desFile.exists()){
                inStream = mContext.getResources().getAssets().open(mAssertRelativePath);
                outStream = new FileOutputStream(desFile);
                byte[] buffer = new byte[1024*100];
                int length = inStream.read(buffer);
                while(length !=-1){
                    outStream.write(buffer, 0, length);
                    length=inStream.read(buffer);
                }
                outStream.flush();
                inStream.close();
                outStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(inStream!=null){
                    inStream.close();
                    inStream=null;
                }

                if(outStream!=null){
                    outStream.close();
                    outStream=null;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
