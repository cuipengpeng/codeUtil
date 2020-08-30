package com.test.xcamera.album.preview.fragmentinterface;

import com.test.xcamera.bean.BaseData;

public interface VideoPreviewFragmentInterface {
    /**
     * start play video UI setting
     */
    void videoStartUi();

    /**
     * video stop play UI setting
     */
    void videoStopUi();

    /**
     * restart video
     */
    void videoResumetUi();

    /**
     * pause video
     */
    void videoPauseUi();

    /**
     * 开始播放视频
     */
    void startPlayVideo();

    /**
     * video data callback
     * start preview
     *
     * @param baseData
     */
    void startVideoPreview(BaseData baseData);

    /**
     * audio data callback
     *
     * @param baseData
     */
    void startAudioPreview(BaseData baseData);

    /**
     * refresh video current time
     *
     * @param currentTime
     */
    void refreshVideoCurrent(int currentTime);

    /**
     * finish video
     */
    void onFinishVideo();

    /**
     * seek video
     *
     * @param seekTime
     */
    void seek(long seekTime);

    /**
     * mark success
     */
    void videoMarkSuccess();
}
