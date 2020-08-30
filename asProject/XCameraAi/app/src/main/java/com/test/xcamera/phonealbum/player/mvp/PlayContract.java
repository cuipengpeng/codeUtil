package com.test.xcamera.phonealbum.player.mvp;



public interface PlayContract {
    interface View  {

        void setPresenter(PlayPresenter presenter);

        /**
         * 播放
         */
        void doPlay();

        /**
         * 快进
         */
        void doSeek(float position);

        /**
         * 暂停
         */
        void doPause();

        void doPlayClip(int playIndex);
    }

    interface Presenter {

        /**
         * 播放
         */
        @SuppressWarnings("unused")
        void play();

        /**
         *
         */
        @SuppressWarnings("unused")
        void seek(float position);

        /**
         * 暂停
         */
        @SuppressWarnings("unused")
        void pause();

        /**
         * 允许拖拽视频
         */
        @SuppressWarnings("unused")
        void enableDragVideoView(boolean enable);

        /**
         * 设置播放的片段
         */
        @SuppressWarnings("unused")
        void playClip(int playIndex);
    }

}
