package com.zlw.main.recorderlib;


import android.annotation.SuppressLint;
import android.app.Application;

import com.zlw.main.recorderlib.recorder.RecordConfig;
import com.zlw.main.recorderlib.recorder.RecordHelper;
import com.zlw.main.recorderlib.recorder.RecordService;
import com.zlw.main.recorderlib.recorder.listener.RecordDataListener;
import com.zlw.main.recorderlib.recorder.listener.RecordResultListener;
import com.zlw.main.recorderlib.recorder.listener.RecordSoundSizeListener;
import com.zlw.main.recorderlib.recorder.listener.RecordStateListener;
import com.zlw.main.recorderlib.recorder.listener.RecordTemporaryPlayListener;
import com.zlw.main.recorderlib.utils.Logger;

/**
 * @author zhaolewei on 2018/7/10.
 */
public class RecordManager {
    private static final String TAG = RecordManager.class.getSimpleName();
    @SuppressLint("StaticFieldLeak")
    private volatile static RecordManager instance;
    private Application context;

    private RecordManager() {
    }

    public static RecordManager getInstance() {
        if (instance == null) {
            synchronized (RecordManager.class) {
                if (instance == null) {
                    instance = new RecordManager();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化
     *
     * @param application Application
     * @param showLog     是否开启日志
     */
    public void init(Application application, boolean showLog) {
        this.context = application;
        Logger.IsDebug = showLog;
    }

    public void start() {
        if (context == null) {
            Logger.e(TAG, "未进行初始化");
            return;
        }
        Logger.i(TAG, "start...");
        RecordService.startRecording(context);
    }

    public void stop() {
        if (context == null) {
            return;
        }
        RecordService.stopRecording(context);
    }

    public void resume() {
        if (context == null) {
            return;
        }
        RecordService.resumeRecording(context);
    }

    public void pause() {
        if (context == null) {
            return;
        }
        RecordService.pauseRecording(context);
    }

    /**
     * 临时播放文件
     */
    public void makeTemporaryPlay() {
        if (context == null) {
            return;
        }
        RecordService.makeTemporaryPlay(context);
    }


    /**
     * 清除缓存文件
     */
    public void clearFile() {
        if (context == null) {
            return;
        }
        RecordService.clearFile(context);
    }




    /**
     * 录音状态监听回调
     */
    public void setRecordStateListener(RecordStateListener listener) {
        RecordService.setRecordStateListener(listener);
    }

    /**
     * 录音数据监听回调
     */
    public void setRecordDataListener(RecordDataListener listener) {
        RecordService.setRecordDataListener(listener);
    }

    /**
     * 录音音量监听回调
     */
    public void setRecordSoundSizeListener(RecordSoundSizeListener listener) {
        RecordService.setRecordSoundSizeListener(listener);
    }

    /**
     * 录音完成回调
     */
    public void setRecordResultListener(RecordResultListener listener) {
        RecordService.setRecordResultListener(listener);
    }
 /**
     * 录音完成回调
     */
    public void setRecordTemporaryPlayListener(RecordTemporaryPlayListener listener) {
        RecordService.setRecordTemporaryPlayListener(listener);
    }

    public boolean changeFormat(RecordConfig.RecordFormat recordFormat) {
        return RecordService.changeFormat(recordFormat);
    }

    /**
     * 修改录音文件存放路径
     */
    public void changeRecordDir(String recordDir) {
        RecordService.changeRecordDir(recordDir);
    }

    /**
     * 获取当前的录音状态
     *
     * @return 状态
     */
    public RecordHelper.RecordState getState() {
        return RecordService.getState();
    }

}
