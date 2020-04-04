package com.caishi.chaoge.manager;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.caishi.chaoge.utils.LogUtil;

public class MediaPlayerManager implements MediaPlayer.OnErrorListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener {
    public MediaPlayer mediaPlayer;//播放音频控件
    public String url;//播放地址
    private Context activity;//上下文
    public boolean isPrepare;//资源是否准备完成
    public boolean isPlayCompletion = false;//资源是否播放完成
    public boolean mPauseByHand = false;//是否手动停止
    private OnPlayMusicListener onPlayMusicListener;


    public static MediaPlayerManager newInstance(Context context) {
        return new MediaPlayerManager(context);
    }

    public MediaPlayerManager(Context context) {
        init(context);
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    /**
     * 切换播放资源
     *
     * @param url
     */
    public void changeUrl(String url) {
        isPrepare = false;//进入资源为准备状态
        mPauseByHand = false;
        if (mediaPlayer != null) {
            mediaPlayer.reset();//初始化
        } else {
            mediaPlayer = new MediaPlayer();
        }
        setUrl(url);//设置新的链接
        start();//开始播放
    }

    /**
     * 切换播放资源
     *
     * @param url
     */
    public void changeUrl(String url, boolean isLooping) {
        isPrepare = false;//进入资源为准备状态
        mPauseByHand = false;
        if (mediaPlayer != null) {
            mediaPlayer.reset();//初始化
        } else {
            mediaPlayer = new MediaPlayer();
        }
        setUrl(url);//设置新的链接
        start(isLooping);//开始播放
        LogUtil.i("开始播放");
    }

    /**
     * 开始播放，开始是在准备结束完成后才播放的
     */
    public void start(boolean isLooping) {
        try {
            mediaPlayer.setOnErrorListener(this);//设置错误监听
            mediaPlayer.setOnPreparedListener(this);//资源准备完成监听
            mediaPlayer.setOnCompletionListener(this);//播放完成监听
            // 设置循环播放getDuration
            mediaPlayer.setLooping(isLooping);
            // 设置音频流的类型
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(activity, Uri.parse(url));

            //因为是在线播放，所以采用异步的方式进行资源准备，
            // 准备完成后会执行OnPreparedListener的onPrepared方法
            mediaPlayer.prepareAsync();
//            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化操作，
     *
     * @param context
     */
    public void init(Context context) {
        this.activity = context;
        destoryMediaPlayer();
        mediaPlayer = new MediaPlayer();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 开始播放，开始是在准备结束完成后才播放的
     */
    public void start() {
        try {
            mediaPlayer.setOnErrorListener(this);//设置错误监听
            mediaPlayer.setOnPreparedListener(this);//资源准备完成监听
            mediaPlayer.setOnCompletionListener(this);//播放完成监听
            mediaPlayer.setOnSeekCompleteListener(this);
            // 设置循环播放
            mediaPlayer.setLooping(true);
            // 设置音频流的类型
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(activity, Uri.parse(url));

            //因为是在线播放，所以采用异步的方式进行资源准备，
            // 准备完成后会执行OnPreparedListener的onPrepared方法
            mediaPlayer.prepareAsync();
//            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            continuePlay();
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
    public void continuePlay() {
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
            LogUtil.e("播放音乐===isPlay==" + e.toString());
            return false;
        }
    }


    /**
     * 资源释放，在退出界面是调用
     */
    public void release() {
        destoryMediaPlayer();
    }

    public void destoryMediaPlayer() {
        try {
            if (mediaPlayer != null) {
//                mediaPlayer.setOnCompletionListener(null);
//                mediaPlayer.setOnPreparedListener(null);
//                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
                url = null;
            }
        } catch (Exception e) {
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d("mp", "播放失败: " + what + "--" + extra);
        Toast.makeText(activity, "播放失败", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //准备完成，开始播放，并修改状态
        isPrepare = true;
        isPlayCompletion = false;
        mp.start();
        mp.setVolume(1.0f, 1.0f);
        if (onPlayMusicListener != null)
            onPlayMusicListener.onPrepared(mp);
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        LogUtil.i("播放完成");
        //播放完成
        isPrepare = false;
        isPlayCompletion = true;
//        mp.reset();
        if (onPlayMusicListener != null)
            onPlayMusicListener.onCompletion(mp);
    }

    public void setOnPlayMusicListener(OnPlayMusicListener onPlayMusicListener) {
        this.onPlayMusicListener = onPlayMusicListener;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

        if (onPlayMusicListener != null)
            onPlayMusicListener.onSeekComplete(mp);

    }


    public interface OnPlayMusicListener {
        /**
         * 准备完成，开始播放
         *
         * @param mp MediaPlayer
         */
        void onPrepared(MediaPlayer mp);

        /**
         * 播放完成
         *
         * @param mp MediaPlayer
         */
        void onCompletion(MediaPlayer mp);


        void onSeekComplete(MediaPlayer mp);


    }


}
