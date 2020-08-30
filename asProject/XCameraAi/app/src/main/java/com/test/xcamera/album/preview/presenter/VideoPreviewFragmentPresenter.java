package com.test.xcamera.album.preview.presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.framwork.base.view.MOBaseActivity;
import com.test.xcamera.R;
import com.test.xcamera.accrssory.AccessoryManager;
import com.test.xcamera.album.TimerHanler;
import com.test.xcamera.album.preview.fragmentinterface.VideoPreviewFragmentInterface;
import com.test.xcamera.bean.BaseData;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.mointerface.MoRequestCallback;

/**
 * author zxc
 * create_time : 2019/10/9 13:55
 * description : playcameravideoactivity  presenter
 */
public class VideoPreviewFragmentPresenter implements AccessoryManager.PreviewDataCallback, TimerHanler.TimerListenre {
    private final Handler handler;
    private VideoPreviewFragmentInterface mVideoPlayInterface;
    private MOBaseActivity activity;
    private int videoStatus = 0;
    private VideoPlayerListener videoPlayerListener;

    public VideoPreviewFragmentPresenter(Context context, VideoPreviewFragmentInterface mVideoPlayInterface, Handler handler) {
        this.mVideoPlayInterface = mVideoPlayInterface;
        this.handler = handler;
        this.activity = (MOBaseActivity) context;
    }

    /**
     * set callback
     */
    public void setConnectionCallback() {
        AccessoryManager.getInstance().setPreviewDataCallback(this);
    }

    /**
     * cancel callback
     */
    public void cancelConnectionCallback() {
        AccessoryManager.getInstance().setPreviewDataCallback(null);
    }

    /**
     * start timer
     *
     * @param totalTime video total time
     */
    public void startTimer(int totalTime) {
        //timerHanler.start(totalTime);
    }

    /**
     * continue timer
     *
     * @param totalCount
     */
    public void continueTimer(int totalCount) {
        // timerHanler.resumeStart(totalCount);
    }

    @Override
    public void onTimerDoing(int count) {
        // mVideoPlayInterface.refreshVideoCurrent(count);
    }

    @Override
    public void onFinish() {
        videoStatus = 0;
        mVideoPlayInterface.onFinishVideo();
    }

    /**
     * cancel timer
     */
    public void cancelTimer() {
//        timerHanler.cancelTimer();
    }

    /**
     * stop preview
     */
    public void stopPreView() {
        videoStatus = 1;
    }

    /**
     * request camera start play video interface
     *
     * @param videoUrl
     */
    public void startPlayVideo(String videoUrl) {
        videoStatus = 1;
        ConnectionManager.getInstance().start_play(new MoRequestCallback() {
            @Override
            public void onSuccess() {
                System.out.println("start_paly success");
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mVideoPlayInterface.videoStartUi();

                    }
                });
            }

            @Override
            public void onFailed() {
                System.out.println("start_paly failed");
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "start_paly response failed",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }, videoUrl);
    }

    /**
     * request camera start play video interface
     *
     * @param videoUrl
     */
    public void startPlayVideo(String videoUrl, VideoPlayerListener videoPlayerListener) {
        this.videoPlayerListener = videoPlayerListener;
        timeOutHandler.sendEmptyMessageDelayed(1, 5 * 1000);
        videoStatus = 1;
        ConnectionManager.getInstance().start_play(new MoRequestCallback() {
            @Override
            public void onSuccess() {
                isPlay = true;
                System.out.println("start_paly success");
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mVideoPlayInterface.videoStartUi();

                    }
                });
            }

            @Override
            public void onFailed() {
                isPlay = false;
                timeOutHandler.sendEmptyMessage(2);
            }
        }, videoUrl);
    }

    private boolean isPlay = false;
    @SuppressLint("HandlerLeak")
    private Handler timeOutHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            String message = "";
            if (what == 1 && activity != null) {
                message = activity.getBaseContext().getResources().getString(R.string.camera_play_video_timeout);
            } else if (what == 2 && activity != null) {
                message = activity.getBaseContext().getResources().getString(R.string.camera_video_failed);
            }
            if (videoPlayerListener != null && !isPlay) {
                videoPlayerListener.videoPlayError(message);
            }
        }
    };

    public void destroy() {
        if (timeOutHandler != null) {
            timeOutHandler.removeCallbacksAndMessages(null);
            timeOutHandler = null;
        }
        activity = null;
        videoPlayerListener = null;
    }

    public interface VideoPlayerListener {
        void videoPlayError(String errorMsg);
    }

    /**
     * get current video play status
     * 1 playing
     * 2 stop
     * 3 pause
     * 4 continue
     *
     * @return
     */
    public int getVideoStatus() {
        return videoStatus;
    }

    /**
     * stop play video
     */
    public void stopVideo() {
        videoStatus = 2;
        ConnectionManager.getInstance().stopPlay(new MoRequestCallback() {
            @Override
            public void onSuccess() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mVideoPlayInterface.videoStopUi();
                    }
                });
            }

            @Override
            public void onFailed() {

            }
        });

    }

    /**
     * pause video
     *
     * @param isTouchSeekBar
     */
    public void pauseVideo(final boolean isTouchSeekBar) {
        videoStatus = 3;
        ConnectionManager.getInstance().pausePlay(new MoRequestCallback() {
            @Override
            public void onSuccess() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isTouchSeekBar)
                            mVideoPlayInterface.videoPauseUi();
                    }
                });
            }

            @Override
            public void onFailed() {

            }
        });
    }

    /**
     * continue play video
     */
    public void continueVideo() {
        videoStatus = 4;
        ConnectionManager.getInstance().resumePlay(new MoRequestCallback() {
            @Override
            public void onSuccess() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mVideoPlayInterface.videoResumetUi();
                    }
                });
            }

            @Override
            public void onFailed() {

            }
        });
    }

    /**
     * mark video
     *
     * @param videoUrl
     * @param markTime
     */
    public void markVideo(String videoUrl, long markTime) {
        ConnectionManager.getInstance().markVideo(videoUrl, markTime, new MoRequestCallback() {
            @Override
            public void onSuccess() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mVideoPlayInterface.videoMarkSuccess();
                    }
                });
            }

            @Override
            public void onFailed() {
                Log.i("MARK_TEST", "onSuccess: 失败");
            }
        });
    }

    /**
     * seek to current time play video
     *
     * @param seekTime
     */
    public void seekVideo(final long seekTime) {
        videoStatus = 4;
        ConnectionManager.getInstance().seekPlay(new MoRequestCallback() {
            @Override
            public void onSuccess() {
                mVideoPlayInterface.seek(seekTime);
            }

            @Override
            public void onFailed() {

            }
        }, seekTime);
    }

    @Override
    public void onVideoDataAvailable(final BaseData baseData) {
        mVideoPlayInterface.startVideoPreview(baseData);
    }

    @Override
    public void onAudioDataAvailable(BaseData baseData) {
        mVideoPlayInterface.startAudioPreview(baseData);
    }

}
