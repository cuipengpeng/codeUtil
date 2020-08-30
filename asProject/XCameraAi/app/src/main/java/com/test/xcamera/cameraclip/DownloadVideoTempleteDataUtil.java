package com.test.xcamera.cameraclip;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.editvideo.ToastUtil;
import com.test.xcamera.R;
import com.test.xcamera.accrssory.AccessoryManager;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.bean.BaseData;
import com.test.xcamera.bean.VideoTemplete;
import com.test.xcamera.cameraclip.bean.VideoSegment;
import com.test.xcamera.constants.LogcatConstants;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.mointerface.MoRequestCallback;
import com.test.xcamera.player.AVFrame;
import com.test.xcamera.utils.Constants;
import com.test.xcamera.utils.DateUtils;
import com.test.xcamera.utils.LoggerUtils;
import com.test.xcamera.utils.Md5Util;
import com.test.xcamera.utils.SPUtils;
import com.test.xcamera.utils.StorageUtils;
import com.test.xcamera.utils.StringUtil;
import com.moxiang.common.logging.Logcat;
import com.xiaoyi.yicamera.mp4recorder2.Mp4V2Native;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Author: mz
 * Time:  2019/10/29
 */
public class DownloadVideoTempleteDataUtil implements AccessoryManager.PreviewDataCallback {

    private String Tag = "DownloadVideoTempleteDataUtil";
    private int downloadVideoIndex = 0;
    //验证问题使用，查看下载片段的总时长
    private long downloadDuration = 0;
    private int timeOutCount = 0;
    private VideoSegment segment;
    public static long selectedDateForCache;//必须是static  用于多对象
    private VideoTemplete selectedVideoTemplate = new VideoTemplete();//默认模板的数据
    private Timer timeoutTimer = null;
    private boolean startDownload = false;
    private boolean netTemplete = false;
    public static final float NET_TEMPLETE_PERCENT = 0.1f;
    public static final int MIN_STORAGE = 100 * 1024 * 1024;
    public int DEFAULT_RESOLUTION_4K = 3840*2160;
    private final float localTempletePercent = 1 - NET_TEMPLETE_PERCENT;
    private Handler mHandler;
    private long segmentStartTime = 0;

    public DownloadVideoTempleteDataUtil() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    public void startPlayDownload(boolean newDownload, VideoTemplete videoTemplete, boolean readCache) {
        if (StorageUtils.getAvailableExternalMemorySize() < MIN_STORAGE) {
            ToastUtil.showToast(AiCameraApplication.mApplication, AiCameraApplication.mApplication.getResources().getString(R.string.stor_no));
            return ;
        }
        netTemplete = videoTemplete.isNetTemplete();
        boolean hasCache = true;
        if (newDownload && readCache) {
            Object object = SPUtils.readObject(AiCameraApplication.mApplication, getVideoSegmentCachePath(videoTemplete), new VideoTemplete());
            if (object instanceof VideoTemplete) {
                selectedVideoTemplate = (VideoTemplete) object;
            }else {
                selectedVideoTemplate = videoTemplete;
            }
            List<VideoSegment> videoSegmentList = selectedVideoTemplate.getVideoSegmentList();
            Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("download file cache size  :" + videoSegmentList.size()).out();
            if (videoSegmentList.size() > 0) {
                for (int i = 0; i < videoSegmentList.size(); i++) {
                    LoggerUtils.printLog("cache--" + "--position=" + i + "--filepath=" + videoSegmentList.get(i).getVideoSegmentFilePath());
                    Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg(" cache download file name  :" + videoSegmentList.get(i).getVideoSegmentFilePath() + " camera filePath: " + videoSegmentList.get(i).getFilePath() + " createTime: " + DateUtils.DateFormat(videoSegmentList.get(i).getCreate_time())
                            + " closeTime " + DateUtils.DateFormat(videoSegmentList.get(i).getClose_time()) + " startTime :" + DateUtils.DateFormat(videoSegmentList.get(i).getStart_time()) + " endTime: " + DateUtils.DateFormat(videoSegmentList.get(i).getEnd_time())
                            + "  score: " + videoSegmentList.get(i).getScore()).out();
                    String segmentFilePath = videoSegmentList.get(i).getVideoSegmentFilePath();
                    if (StringUtil.isNull(segmentFilePath)) {
                        hasCache = false;
                        break;
                    } else {
                        if (!new File(segmentFilePath).exists()) {
                            Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("file not exit ").out();
                            hasCache = false;
                            break;
                        }
                    }
                }
            } else {
                hasCache = false;
            }
        }

        if (newDownload && readCache && hasCache) {
            Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("load cache finish this activity :").out();
            mOnFinishDownloadVideoCallback.onFinish(selectedVideoTemplate);
            return;
        } else {
            if (newDownload) {
                mOnFinishDownloadVideoCallback.onStart();
                downloadVideoIndex = 0;
                //切记：有且只能赋值一次
                selectedVideoTemplate = videoTemplete;
            }

            segment = videoTemplete.getVideoSegmentList().get(downloadVideoIndex);
            String filePath = segment.getFilePath();
            if (!segment.getFilePath().startsWith("mox")) {
                filePath = "mox://resourceitem?uri=" + segment.getFilePath() + "&size=0&type=video";
            }
            LoggerUtils.printLog("actualSegmentCount=" + videoTemplete.getVideoSegmentList().size() + "--needSegmentCount=" + videoTemplete.getVideo_segment().size() + "start back play " + filePath + "--segment=" + segment);

            Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("start  download file name: " + segment.getVideoSegmentFilePath() + " camera filePath: " + segment.getFilePath() + " createTime: " + DateUtils.DateFormat(segment.getCreate_time())
                    + " closeTime: " + DateUtils.DateFormat(segment.getClose_time()) + " startTime: " + DateUtils.DateFormat(segment.getStart_time()) + " endTime: " + DateUtils.DateFormat(segment.getEnd_time())
                    + " score: " + segment.getScore() + "      actualSegmentCount=" + videoTemplete.getVideoSegmentList().size() + "--needSegmentCount=" + videoTemplete.getVideo_segment().size()).out();
            AccessoryManager.getInstance().setPreviewDataCallback(this);
            //需要的时间是第几秒,相对文件的下载时间点
            long start_time = segment.getStart_time() - segment.getCreate_time();
            segmentStartTime = segment.getStart_time() - segment.getCreate_time();
            ConnectionManager.getInstance().start_play_download(new MoRequestCallback() {
                @Override
                public void onSuccess() {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            long mp4Duration = ((segment.getEnd_time() - segment.getStart_time()));
                            LoggerUtils.d(Tag, " mp4Duration time = " + mp4Duration);
                            startRecording(mp4Duration);// 开始录制
                        }
                    });
                }

                @Override
                public void onFailed() {
                    Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("fail to startPlayDown()  downloadVideoIndex=" + downloadVideoIndex + "    needSegmentSize=" + selectedVideoTemplate.getVideo_segment().size()).out();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AiCameraApplication.mApplication, AiCameraApplication.mApplication.getResources().getString(R.string.videoDownloadException), Toast.LENGTH_SHORT).show();
                            mOnFinishDownloadVideoCallback.onFail(AiCameraApplication.mApplication.getResources().getString(R.string.videoDownloadException));
                            downloadVideoIndex = 0;
                        }
                    });
                }
            }, filePath, start_time);
        }
    }

    public void stopPlayDown() {
        startDownload = false;
        timeOutCount = 0;
        Mp4V2Native.getInstance().stopRecord();
        if (!AccessoryManager.getInstance().mIsRunning) {
            cancelTimer();
        }
        ConnectionManager.getInstance().stopPlay(new MoRequestCallback() {
            @Override
            public void onSuccess() {
                cancelTimer();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //结束录制
//                        Mp4V2Native.getInstance().stopRecord();
                        String fileName = Mp4V2Native.getInstance().getFilePath();
                        VideoSegment videoSegment = selectedVideoTemplate.getVideoSegmentList().get(downloadVideoIndex);
                        videoSegment.setDownloadCount(videoSegment.getDownloadCount() + 1);
                        if (new File(fileName).exists()) {
                            videoSegment.setVideoSegmentFilePath(fileName);
                            videoSegment.setVideoSegmentDuration((segment.getEnd_time() - segment.getStart_time()));
                            //测试专用，发版去掉。
                            downloadDuration = downloadDuration + (segment.getEnd_time() - segment.getStart_time());
                            Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("download  ok  file  position :" + " position: " + downloadVideoIndex + " localpath: " + videoSegment.getVideoSegmentFilePath() + " camera filePath: " + videoSegment.getFilePath() + " createTime:" + DateUtils.DateFormat(videoSegment.getCreate_time())
                                    + " closeTime： " + DateUtils.DateFormat(videoSegment.getClose_time()) + " startTime: " + DateUtils.DateFormat(videoSegment.getStart_time()) + " endTime: " + DateUtils.DateFormat(videoSegment.getEnd_time())
                                    + "--score=" + videoSegment.getScore() + " download duration " + downloadDuration + " current segment length " + (segment.getEnd_time() - segment.getStart_time())).out();
                            LoggerUtils.printLog("download  finish  --exist" + "--position=" + downloadVideoIndex + "--filepath=" + fileName);

                        } else {
                            Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("download  file  not exist").out();
                            LoggerUtils.printLog("download  finish  --not exist" + "--position=" + downloadVideoIndex + "--filepath=" + fileName);
                        }

                        if (downloadVideoIndex == (selectedVideoTemplate.getVideo_segment().size() - 1)) {
//                            cancelTimer();
                            Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("download success stop        needSegmentSize="+selectedVideoTemplate.getVideo_segment().size()).out();
                            LoggerUtils.printLog( "download success stop ----");
                            // todo 下载完成 后 跳转到 下一个页面
                            mOnFinishDownloadVideoCallback.onProgress(100);
                            SPUtils.writeObject(AiCameraApplication.mApplication, getVideoSegmentCachePath(selectedVideoTemplate), selectedVideoTemplate);
                            downloadVideoIndex = 0;
                            //测试专用，发版去掉
                            downloadDuration = 0;

                            mOnFinishDownloadVideoCallback.onProgress(0);
                            mOnFinishDownloadVideoCallback.onFinish(selectedVideoTemplate);
                            return;
                        }
                        if (new File(fileName).exists() || videoSegment.getDownloadCount() > 2) {
                            float progess = (downloadVideoIndex + 1) * 1.0f / selectedVideoTemplate.getVideo_segment().size() * 100;
                            if (netTemplete) {
                                progess = progess * localTempletePercent + NET_TEMPLETE_PERCENT * 100;
                            }
                            mOnFinishDownloadVideoCallback.onProgress((int) progess);
                            downloadVideoIndex++;
                        }
                        startPlayDownload(false, selectedVideoTemplate, true);
                    }
                });
            }

            @Override
            public void onFailed() {
                Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("fail to stopPlayDown()  downloadVideoIndex=" + downloadVideoIndex + "      needSegmentSize=" + selectedVideoTemplate.getVideo_segment().size()).out();
            }
        });
    }

    private long chaTime = 0;

    @Override
    public void onVideoDataAvailable(BaseData baseData) {

        AVFrame avFrame = new AVFrame(baseData, baseData.getmDataSize(), false, false);

        if (avFrame.getFrmNo() == 1) {
            long frameStamp = avFrame.getTimeStamp();
            chaTime = segmentStartTime - frameStamp;
//            Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("segmentStartTime segmentStartTime  " + segmentStartTime + " frameStamp "
//                    + frameStamp + " chaTime " + chaTime + " ").out();
        }
        if (!startDownload) {
            return;
        }

        if (downloadVideoIndex < selectedVideoTemplate.getVideoSegmentList().size()) {
            VideoSegment segment = selectedVideoTemplate.getVideoSegmentList().get(downloadVideoIndex);
            long time = segment.getEnd_time() - segment.getCreate_time();

            long downTime = baseData.getmVideoFrameInfo().getmTimeMs() - (segment.getStart_time() - segment.getCreate_time());
            long totalTime = segment.getEnd_time() - segment.getStart_time();
            if(chaTime>0){
                downTime=chaTime+downTime;
            }

            if (time >= baseData.getmVideoFrameInfo().getmTimeMs()) {
                float singleSegmentPercent = downTime * 1.0f / totalTime * (1.0f / selectedVideoTemplate.getVideo_segment().size());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        float progess = ((downloadVideoIndex) * 1.0f / selectedVideoTemplate.getVideo_segment().size() + singleSegmentPercent) * 100;
                        if (netTemplete) {
                            progess = progess * localTempletePercent + NET_TEMPLETE_PERCENT * 100;
                        }
                        mOnFinishDownloadVideoCallback.onProgress((int) progess);
                    }
                });
            }
            LoggerUtils.d(Tag, "---Video data time " + time + " Frame Time = " + baseData.getmVideoFrameInfo().getmTimeMs());
            checkTime(time - chaTime, baseData, null);
        }
        if (startDownload) {
            if(avFrame.getVideoWidth()*avFrame.getVideoHeight()<= DEFAULT_RESOLUTION_4K){
                selectedVideoTemplate.setVideoWidth(avFrame.getVideoWidth());
                selectedVideoTemplate.setVideoHeight(avFrame.getVideoHeight());
                DEFAULT_RESOLUTION_4K =avFrame.getVideoHeight()*avFrame.getVideoWidth();
            }
            Mp4V2Native.getInstance().recordVideoFrame(avFrame, 4);
//            Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("write video to  mp4  " + avFrame.getTimeStamp() + " seq id "
//                    + avFrame.getFrmNo() + " chaTime " + chaTime + " ").out();
        }

    }

    @Override
    public void onAudioDataAvailable(BaseData baseData) {
//        AVFrame avFrame = new AVFrame(baseData, false);
//        Mp4V2Native.getInstance().recordAudioFrame(avFrame, "");
//        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("on audio data available template size " + selectedVideoTemplate.getVideoSegmentList().size() + " current position " + downloadVideoIndex).out();
//        VideoSegment segment = selectedVideoTemplate.getVideoSegmentList().get(downloadVideoIndex);
//        long time = segment.getEnd_time() - segment.getCreate_time();
//        checkTime(time, null, baseData);
    }

    private void checkTime(long time, BaseData videoBaseData, BaseData audioBaseData) {
        timeOutCount = 0;
        if (timeoutTimer == null) {
            createTimeoutTimer();
        }
        if (videoBaseData != null) {
            if (startDownload && time <= videoBaseData.getmVideoFrameInfo().getmTimeMs()) {
                chaTime = 0;
                Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("video Data  stopPlayDown()--" + "timeLength=" + time + "--timestamp=" + videoBaseData.getmVideoFrameInfo().getmTimeMs()).out();
                stopPlayDown();
            }
        } else {
            if (startDownload && time <= audioBaseData.getmAudioFrameInfo().getmTimeMs()) {
                chaTime = 0;
                Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("audio Data  stopPlayDown()--" + "timeLength=" + time + "--timestamp=" + audioBaseData.getmAudioFrameInfo().getmTimeMs()).out();
                stopPlayDown();
            }
        }
    }

    private void cancelTimer() {
        if (timeoutTimer != null) {
            timeoutTimer.cancel();
            timeoutTimer = null;
        }
        System.gc();
        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("cancelTimer()  downloadVideoIndex=" + downloadVideoIndex + "----needSegmentSize=" + selectedVideoTemplate.getVideo_segment().size()).out();
    }

    private void createTimeoutTimer() {
        cancelTimer();
        timeoutTimer = new Timer();
        timeoutTimer.schedule(new TimeoutTimerTask(), new Date(), 1000);
        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("createTimeoutTimer()  downloadVideoIndex=" + downloadVideoIndex + "----needSegmentSize=" + selectedVideoTemplate.getVideo_segment().size()).out();
    }

    private class TimeoutTimerTask extends TimerTask {
        @Override
        public void run() {
            timeOutCount++;
            if (timeOutCount >= 3) {
                Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("timeout  stopPlayDown()--").out();
                stopPlayDown();
            }
        }
    }

    /**
     * 开始录制
     */
    private void startRecording(final long time) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                startDownload = true;
                String videoSegmentCachePath = getVideoSegmentCachePath(selectedVideoTemplate);
                File file = new File(videoSegmentCachePath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                String fileName = videoSegmentCachePath + "/" + System.currentTimeMillis() + ".mp4";
//                LoggerUtils.d(Tag, " file name is what = " + fileName);
                LoggerUtils.printLog("download" + "--position=" + downloadVideoIndex + "--filepath=" + fileName);
                Mp4V2Native.getInstance().startRecord(fileName);
            }
        });
    }

    @NonNull
    private String getVideoSegmentCachePath(VideoTemplete videoTemplete) {
        Set<String> strSet = new HashSet<>();
        for (int i = 0; i < videoTemplete.getVideoSegmentList().size(); i++) {
            strSet.add(videoTemplete.getVideoSegmentList().get(i).getFilePath());
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (String string : strSet) {
            stringBuilder.append(string);
        }
        for (int i = 0; i < videoTemplete.getVideo_segment().size(); i++) {
            stringBuilder.append(videoTemplete.getVideo_segment().get(i).getVideo_segment_length());
        }

        return Constants.cacheLocalPath + "/" + Md5Util.md5(stringBuilder.toString() + android.os.Process.myPid() + OneKeyMakeVideoHelper.makeVideoDir + selectedDateForCache + videoTemplete.getPackageFileId() + videoTemplete.getId() + videoTemplete.getIcon() + videoTemplete.getContent());
    }

    private OnFinishDownloadVideoCallback mOnFinishDownloadVideoCallback;

    public void setOnFinishDownloadVideoCallback(OnFinishDownloadVideoCallback onFinishDownloadVideoCallback) {
        mOnFinishDownloadVideoCallback = onFinishDownloadVideoCallback;
    }

    public interface OnFinishDownloadVideoCallback {
        void onStart();

        void onProgress(int progress);

        void onFinish(VideoTemplete videoTemplete);

        void onFail(String errorInfo);
    }

}
