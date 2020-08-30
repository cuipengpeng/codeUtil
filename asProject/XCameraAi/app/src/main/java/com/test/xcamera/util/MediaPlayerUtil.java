package com.test.xcamera.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.test.xcamera.utils.LoggerUtils;

public class MediaPlayerUtil {
    public static boolean mIsLoading=false;
    public MediaPlayer mediaPlayer;
    public String mUrl;
    private Context context;
    public boolean isPrepare;
    public boolean isPlayCompletion = false;
    public boolean mPauseByHand = false;//是否手动停止
    private boolean mLoop = true;//是否循环播放
    private OnPreparedCallBack mOnPreparedCallBack;

    public MediaPlayerUtil(Context context) {
        this.context = context;
        releaseMediaPlayer();
        mediaPlayer = new MediaPlayer();
    }

    public String getUrl() {
        return mUrl;
    }

    public boolean ismLoop() {
        return mLoop;
    }

    public void setmLoop(boolean mLoop) {
        this.mLoop = mLoop;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    /**
     * 开始播放，开始是在准备结束完成后才播放的
     */
    public void start(String url, boolean isLooping) {
        if(TextUtils.isEmpty(url)){
            return;
        }
        this.mUrl = url;
        LoggerUtils.printLog(url);
        isPrepare = false;//进入资源为准备状态
        mPauseByHand = false;
        if (mediaPlayer != null) {
            mediaPlayer.reset();//初始化
        } else {
            mediaPlayer = new MediaPlayer();
        }
        try {
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener(){

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.d("mp", "播放失败: " + what + "--" + extra);
                    Toast.makeText(context, "播放失败", Toast.LENGTH_SHORT).show();
                    if(mOnPreparedCallBack != null){
                        mOnPreparedCallBack.onError();
                    }
                    return false;
                }
            });//设置错误监听
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){

                @Override
                public void onPrepared(MediaPlayer mp) {
                    //准备完成，开始播放，并修改状态
                    isPrepare = true;
                    isPlayCompletion = false;
                    if(mOnPreparedCallBack != null){
                        mOnPreparedCallBack.onPrepared();
                    }
                    mp.start();
                    mp.setVolume(1.0f, 1.0f);
                }
            });//资源准备完成监听
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){

                @Override
                public void onCompletion(MediaPlayer mp) {
                    //播放完成
                    isPrepare = false;
                    isPlayCompletion = true;
            //        mp.reset();
                }
            });//播放完成监听
            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener(){

                @Override
                public void onSeekComplete(MediaPlayer mp) {
                }
            });
            // 设置循环播放
            mediaPlayer.setLooping(isLooping);
            // 设置音频流的类型
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(context, Uri.parse(url));

            //因为是在线播放，所以采用异步的方式进行资源准备，
            // 准备完成后会执行OnPreparedListener的onPrepared方法
            mediaPlayer.prepareAsync();
//            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getCurrentPosition() {
        return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0;
    }

    /**
     * 获取音频长度
     */
    public int getDuration() {
        if (mediaPlayer != null)
            return mediaPlayer.getDuration();
        else
            return 0;
    }

    /**
     * 快进
     *
     * @param msec
     */
    public void seekTo(int msec) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(msec);
            resumePlay();
        }

    }

    /**
     * 暂停
     */
    public void pause() {
        if (isPlay())
            mediaPlayer.pause();
    }

    /**
     * 暂停
     */
    public void pauseByHand() {
        if (isPlay()) {
            mediaPlayer.pause();
            mPauseByHand = true;
        }
    }

    /**
     * 继续播放
     */
    public void resumePlay() {
        if (mediaPlayer != null && !isPlay()) {
            mediaPlayer.start();
            mPauseByHand = false;
        }

    }


    /**
     * 当前是否在播放
     *
     * @return
     */
    public boolean isPlay() {
        try {
            return null != mediaPlayer && mediaPlayer.isPlaying();
        } catch (IllegalStateException e) {
            return false;
        }
    }

    /**
     * 资源释放，在退出界面是调用
     */
    public void releaseMediaPlayer() {
        try {
            if (mediaPlayer != null) {
//                mediaPlayer.setOnCompletionListener(null);
//                mediaPlayer.setOnPreparedListener(null);
//                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
                mUrl = null;
            }
        } catch (Exception e) {
        }
    }

    public void setOnPreparedCallBack(OnPreparedCallBack onPreparedCallBack) {
        this.mOnPreparedCallBack = onPreparedCallBack;
    }
    public interface OnPreparedCallBack {
        void onPrepared();
        void onError();
    }
}
