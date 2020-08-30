package com.test.xcamera.phonealbum.player;


import com.test.xcamera.phonealbum.player.mvp.Player;

public interface PlayController {
    /** 完成初始化及视频路径设置，视频可以开始播放 */
    void onPrepared();

    /** 视频片段发生变化，可能增加、删除、分割视频片段, 播放单个或播放连续多个视频，切换时的回调*/
    void onClipChanged();

    /** 视频开始播放 */
    void onPlayVideo();

    /** 视频正在播放，显示进度 */
    void onPlayProgress(float progress);

    /** 暂停播放 */
    void onPauseVideo();

    /** 拖动 */
    void onSeek(float position);

    /** 完成播放 */
    void onComplete();

    /** 视频播放页面销毁 */
    void onDestroy();

    /** 视频播放错误 */
    void onError(Exception exception);

    void onShowCoverIcon(boolean show);

    /** 设置播放器控制器 */
    void setPlayer(Player player);
    interface OnSeekBarChangedListener {

        void onProgressChangedByUserTouch(float position);

        void onStopTrackingTouch(float position);

        void onPlayProgress(float position);

        void onPlayPause();
    }

    interface OnPlayCompletedListener {
        void onPlayCompleted(float startOffset);
    }

    interface OnPlayPreparedListener {
        void onPrepared();
    }
}
