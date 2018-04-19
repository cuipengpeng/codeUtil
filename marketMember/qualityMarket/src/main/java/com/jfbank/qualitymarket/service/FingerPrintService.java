package com.jfbank.qualitymarket.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.huoyanzhengxin.zhengxin.print.FingerPrint;
import com.huoyanzhengxin.zhengxin.print.FingerPrintHelp;

public class FingerPrintService extends Service {

    private FingerPrint fingerPrint;


    public FingerPrintService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {//服务已开启，只执行一次
        fingerPrint = FingerPrintHelp.getFingerPrint(getApplicationContext());
        fingerPrint.startMySDK();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        fingerPrint.stopMySDK();
        super.onDestroy();
    }


}
