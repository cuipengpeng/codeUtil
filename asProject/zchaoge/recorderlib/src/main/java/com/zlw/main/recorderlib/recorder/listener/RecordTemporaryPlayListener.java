package com.zlw.main.recorderlib.recorder.listener;

import java.io.File;

/**
 * 临时录音完成回调
 */
public interface RecordTemporaryPlayListener {

    /**
     * 临时录音文件
     *
     * @param result 录音文件
     */
    void onResult(File result);
}
