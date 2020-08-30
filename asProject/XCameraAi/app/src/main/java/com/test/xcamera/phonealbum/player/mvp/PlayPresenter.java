package com.test.xcamera.phonealbum.player.mvp;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import static android.support.v4.util.Preconditions.checkNotNull;

public class PlayPresenter implements PlayContract.Presenter {
    private PlayContract.View mView;

    public static PlayPresenter create(PlayContract.View view) {
        return new PlayPresenter(view);
    }

    @SuppressLint("RestrictedApi")
    private PlayPresenter(@NonNull PlayContract.View view) {
        mView = checkNotNull(view);
        mView.setPresenter(this);
    }

    @Override
    public void play() {
        mView.doPlay();
    }

    @Override
    public void seek(float position) {
        mView.doSeek(position);
    }

    @Override
    public void pause() {
        mView.doPause();
    }

    @Override
    public void enableDragVideoView(boolean enable) {

    }

    @Override
    public void playClip(int playIndex) {
        mView.doPlayClip(playIndex);
    }
}
