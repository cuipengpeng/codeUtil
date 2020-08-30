package com.test.xcamera.picasso;

import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import com.test.xcamera.bean.MoRange;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.mointerface.MoDownloadCallback;

/**
 * Created by wangchunhui on 2019/7/31.
 */

public class MoCameraRequest
{
    private static final int PACKAGE_SIZE = 256*1024;

    private  byte[] mData;
    private int mRequestCount = 0;
    private int mRetryCount = 0;

   private Handler handler;
    public MoCameraRequest() {

    }

    public MoCameraRequest(Handler handler) {
        this.handler=handler;
    }

    public byte[] request(Uri uri, int size) {
        mData = new byte[size];
        dividePackages(size, uri, mData);

        while (true) {
            if (mRequestCount == 0) {
                return mData;
            }
            try {
//                wait(1000);
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ++mRetryCount;
            if (mRetryCount > 30) {
                break;
            }
        }

        return null;

    }

    private void dividePackages(int size, Uri uri, byte[] data) {
        int offset = 0;
        int len = 0;

        // send multi requests
        while (size > 0) {

            len = Math.min(size, PACKAGE_SIZE);
            sendRequest(uri, offset, len, data);

            offset += len;
            size -= len;

            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendRequest(Uri uri, final int offset, int len, final byte[] data) {
        MoRange range = new MoRange();
        range.setmOffset(offset);
        range.setmLength(len);
        ++mRequestCount;

        System.out.println("sendRequest...MoRange==="+range+"uri======"+uri.toString());
        ConnectionManager.getInstance().downloadFile(uri.toString(), range, new MoDownloadCallback() {

            @Override
            public void callback(byte[] data) {
//                System.out.println("----------------" +data+"offset==="+offset);
                if (data != null) {

                        System.arraycopy(data, 0, mData, offset, data.length);

                     if(handler!=null){
//                         System.out.println("发送了多少次数据");
                         Message  msg=Message.obtain();
                         msg.what=10000;
                         msg.obj=data.length;
                         handler.sendMessage(msg);
                    }
                }

                --mRequestCount;
//                System.out.println("----------------mRequestCount" +mRequestCount);
            }

        });
    }

    public  String type;
    private  String fileName ;
    private String  filePath;
    public void setType(String type,String fileName ,String  filePath){
        this.type=type;
        this.fileName=fileName;
        this.filePath=filePath;
    }
}
