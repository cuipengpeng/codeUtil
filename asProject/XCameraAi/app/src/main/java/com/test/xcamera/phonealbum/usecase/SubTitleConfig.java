package com.test.xcamera.phonealbum.usecase;

import com.editvideo.NvAsset;
import com.editvideo.dataInfo.TimelineData;

public class SubTitleConfig {
    /**
     * 获取视频字数
     * * @return
     */
    public static int getSubTitleNumber(){
        int number;
        int ratio= TimelineData.instance().getMakeRatio();
        if (ratio == NvAsset.AspectRatio_16v9) {
            number=28;
        } else if (ratio == NvAsset.AspectRatio_1v1) {
            number=16;
        } else if (ratio == NvAsset.AspectRatio_9v16) {
            number=16;
        } else if (ratio == NvAsset.AspectRatio_3v4) {
            number=16;
        } else if (ratio == NvAsset.AspectRatio_4v3) {
            number=16;
        } else {
            number=28;
        }
        return number;
    }
}
