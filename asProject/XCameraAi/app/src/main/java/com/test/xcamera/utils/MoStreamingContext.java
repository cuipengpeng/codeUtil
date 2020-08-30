package com.test.xcamera.utils;

import com.editvideo.TimelineUtil;
import com.meicam.sdk.NvsStreamingContext;

/**
 * 防止美摄上下文为空
 */
public class MoStreamingContext {
    public static MoStreamingContext getInstance() {
        return new MoStreamingContext();
    }
    /**
     * 获取 NvsStreamingContext
     * @return NvsStreamingContext
     */
    public NvsStreamingContext getNvsStreamingContext(){
        if(NvsStreamingContext.getInstance()==null){
            TimelineUtil.initStreamingContext();
        }
        return NvsStreamingContext.getInstance();
    }
}
