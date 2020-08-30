package com.test.xcamera.phonealbum.player.mvp;

public interface Player {
    void setVideoIndex(int index);

    void start();

    void playVideo(long startTime, long endTime);

    void pause();

    long getDuration();
    /**
     * @return current playback position in milliseconds.
     */
    long getCurrentPosition();


    void seekTimeline(long timestamp, int seekShowMode);


    boolean isPlaying();

    void onDestroy();
}
