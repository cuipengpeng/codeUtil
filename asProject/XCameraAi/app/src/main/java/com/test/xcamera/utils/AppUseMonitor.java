package com.test.xcamera.utils;


import android.os.Handler;
import android.os.Message;

import com.test.xcamera.application.AiCameraApplication;
import com.moxiang.common.logging.Logcat;
/**
 * zjh
 */
public class AppUseMonitor {
    private static AppUseMonitor instance;
    public static AppUseMonitor getInstance() {
        if(instance==null){
            instance= new AppUseMonitor();
        }
        return instance;
    }
    private AppUseMonitor(){
        startAppMonitor();
    }

    /**
     * 启动内存监控
     */
    public void startAppMonitor(){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Logcat.i("moxiang_monitor:"
                            +" 当前Activity："+DeviceInfoManager.getTopActivityPackageName(AiCameraApplication.getContext())+"----------------------------------------------------------------------"
                    );
                    Logcat.i("moxiang_monitor:"
                            +"  总内存："+FileUtils.formatSize(DeviceInfoManager.getTotalMemory()*1024)
                            +"  可用内存："+FileUtils.formatSize(DeviceInfoManager.getAvailableMemory(AiCameraApplication.getContext()))
                            +"  当前应用占内存："+FileUtils.formatSize(DeviceInfoManager.getAppMemory())
                            +"  已使用比例："+DeviceInfoManager.getUsedPercentValue(AiCameraApplication.getContext())
                    );
                    /*Logcat.i("moxiang_monitor:"
                            +" CPU 前进程的使用率："+DeviceInfoManager.getCurProcessCpuRate()
                            +" CPU 总的使用率："+DeviceInfoManager.getTotalCpuRate()
                            +" CPU 使用时长："+DeviceInfoManager.getAppCpuTime()
                    );*/
                }catch (Exception e){
                    e.printStackTrace();
                }
                mHandler.removeCallbacksAndMessages(null);
                mHandler.sendEmptyMessageDelayed(0,2000);
            }
        });
    }
    private Handler mHandler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            startAppMonitor();
        }
    };

}
