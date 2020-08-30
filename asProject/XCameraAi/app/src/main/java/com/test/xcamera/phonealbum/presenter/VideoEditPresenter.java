package com.test.xcamera.phonealbum.presenter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.editvideo.Constants;
import com.editvideo.VideoClipFxInfo;
import com.editvideo.dataInfo.MusicInfo;
import com.editvideo.dataInfo.TimelineData;
import com.test.xcamera.R;
import com.test.xcamera.accrssory.UsbDispose;
import com.test.xcamera.bean.MoErrorData;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.mointerface.MoErrorCallback;
import com.test.xcamera.phonealbum.bean.MusicBean;
import com.test.xcamera.phonealbum.usecase.MediaClipDataTran;
import com.test.xcamera.phonealbum.usecase.VideoCompile;
import com.test.xcamera.phonealbum.usecase.VideoFilerData;
import com.test.xcamera.phonealbum.widget.VideoEditManger;
import com.test.xcamera.utils.AppExecutors;
import com.meicam.sdk.NvsAudioClip;
import com.meicam.sdk.NvsAudioTrack;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineCompoundCaption;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoFx;
import com.meicam.sdk.NvsVideoTrack;

import java.util.ArrayList;
import java.util.List;

public class VideoEditPresenter implements VideoEditContract.Presenter{
    public static final int LAYOUT_PARAM_DEF=-1;
    private VideoEditContract.View mView;
    private Context mContext;
    private VideoCompile mVideoCompile;

    public static VideoEditPresenter getInstance(VideoEditContract.View view, Context context) {
        return new VideoEditPresenter(view,context);
    }
    private VideoEditPresenter(VideoEditContract.View view, Context context){
        this.mView=view;
        this.mContext=context;
    }

    @Override
    public void setLayoutParams(View view, int w, int h) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if(w!=LAYOUT_PARAM_DEF){
            params.height=w;
        }
        if(h!=LAYOUT_PARAM_DEF){
            params.height=h;
        }
        view.setLayoutParams(params);
    }

    @Override
    public void getMusicBGBeanList() {
        AppExecutors.getInstance().diskIO().execute(() -> {
            List<MusicBean> musicBeanList = new ArrayList<>();
            musicBeanList.add(new MusicBean("", R.mipmap.select_music, "选择音乐", ""));
            musicBeanList.addAll(MediaClipDataTran.getMusicInfoListToMusicBean(TimelineData.instance().cloneMusicData()));
            if(mView!=null){
                AppExecutors.getInstance().mainThread().execute(() -> mView.showMusicBGBeanList(musicBeanList));
            }
        });

    }

    @Override
    public void getVideoFilerBeanList() {
        AppExecutors.getInstance().diskIO().execute(() -> {
            List<MusicBean> filerLis= VideoFilerData.getInstance().getVideoFilerList();
            if(mView!=null){
                AppExecutors.getInstance().mainThread().execute(() -> {
                    if(mView!=null){
                        mView.showFilerBeanList(filerLis);
                    }
                });
            }
        });
    }

    @Override
    public NvsTimelineCompoundCaption getCurCompoundCaption(NvsTimeline timeline,long curPos) {
        List<NvsTimelineCompoundCaption> captionList = timeline.getCompoundCaptionsByTimelinePosition(curPos);
        if(captionList==null){
            return null;
        }
        Log.i("club", "club_selectCurCompoundCaption:" + captionList.size());
        int captionCount = captionList.size();
        if (captionCount > 0) {
            float zVal = captionList.get(0).getZValue();
            int index = 0;
            for (int i = 0; i < captionCount; i++) {
                float tmpZVal = captionList.get(i).getZValue();
                if (tmpZVal > zVal) {
                    zVal = tmpZVal;
                    index = i;
                }
            }
            return captionList.get(index);
        } else {
            return  null;
        }
    }

    @Override
    public void setFilterIntensity(NvsTimeline timeline, VideoClipFxInfo videoClipFxInfo) {
        NvsVideoTrack videoTrack = timeline.getVideoTrackByIndex(0);
        for (int i = 0; i < videoTrack.getClipCount(); i++) {
            NvsVideoClip videoClip = videoTrack.getClipByIndex(i);
            int fxCount = videoClip.getFxCount();
            for (int j = 0; j < fxCount; j++) {
                NvsVideoFx fx = videoClip.getFxByIndex(j);
                String name = fx.getBuiltinVideoFxName();
                if (Constants.FX_COLOR_PROPERTY.equals(name) || Constants.FX_VIGNETTE.equals(name) ||
                        Constants.FX_SHARPEN.equals(name) || Constants.FX_TRANSFORM_2D.equals(name)) {
                    continue;
                }
                fx.setFilterIntensity(videoClipFxInfo.getFxIntensity());
            }
        }
    }

    @Override
    public void playPositionItemMusic(NvsTimeline timeline, NvsAudioTrack audioTrack, int pos, String musicPath) {
        audioTrack.removeAllClips();

        List<MusicInfo> musicInfoList = new ArrayList<>();
        if (pos != 0) {
            MusicInfo musicInfo = new MusicInfo();
            musicInfo.setFilePath(musicPath);
            musicInfo.setInPoint(0l);
            musicInfo.setTrimIn(0l);
            musicInfo.setTrimOut(timeline.getDuration());
            musicInfo.setExtraMusic(0);
            musicInfo.setExtraMusicLeft(0);
            musicInfoList.add(musicInfo);

            NvsAudioClip audioClip = audioTrack.addClip(musicInfo.getFilePath(), musicInfo.getInPoint(), musicInfo.getTrimIn(), musicInfo.getTrimOut());
            audioClip.setFadeInDuration(0);
            audioClip.setFadeOutDuration(0);
            audioClip.changeTrimInPoint(0, false);
            audioClip.changeTrimOutPoint(timeline.getDuration(), false);
            audioClip.setVolumeGain(Constants.VIDEOVOLUME_MUSIC_DEFAULTVALUE,Constants.VIDEOVOLUME_MUSIC_DEFAULTVALUE);
        }
        TimelineData.instance().setMusicList(musicInfoList);
        updateMusicFadeInStatus(timeline,audioTrack);
        updateMusicFadeOutStatus(timeline,audioTrack);
    }

    @Override
    public void updateMusicFadeInStatus(NvsTimeline timeline, NvsAudioTrack audioTrack) {
        if (audioTrack.getClipCount() > 0) {
            NvsAudioClip clip = audioTrack.getClipByIndex(0);
            if (clip.getFadeInDuration() > 0) {
                clip.setFadeInDuration(0);
                if(mView!=null){
                    mView.updateMusicFadeInStatus(false);
                }
            } else {
                if(mView!=null){
                    mView.updateMusicFadeInStatus(true);
                }
                clip.setFadeInDuration((long) (1.5f * VideoEditManger.VIDEO_microsecond_TIME));
            }
        } else {
            if(mView!=null){
                mView.updateMusicFadeInStatus(false);
            }
        }
    }

    @Override
    public void updateMusicFadeOutStatus(NvsTimeline timeline, NvsAudioTrack audioTrack) {
        if (audioTrack.getClipCount() > 0) {
            NvsAudioClip clip = audioTrack.getClipByIndex(0);
            clip.changeTrimOutPoint(timeline.getDuration(), false);
            if (clip.getFadeOutDuration() > 0) {
                clip.setFadeOutDuration(0);
                if(mView!=null){
                    mView.updateMusicFadeOutStatus(false);
                }
            } else {
                clip.setAttachment(Constants.MUSIC_EXTRA_AUDIOCLIP, 0);
                clip.setAttachment(Constants.MUSIC_EXTRA_LAST_AUDIOCLIP, 0);
                clip.setFadeOutDuration((long) (1.5f * VideoEditManger.VIDEO_microsecond_TIME));
                if(mView!=null){
                    mView.updateMusicFadeOutStatus(true);
                }
            }
        } else {
            if(mView!=null){
                mView.updateMusicFadeOutStatus(false);
            }
        }
    }
    UsbDispose mUsbDispose;
    @Override
    public void setConnectedUSBError() {
        ConnectionManager.getInstance().setErrorI(new MoErrorCallback() {
            @Override
            public void onError(MoErrorData data) {
                if (mUsbDispose != null) {
                    mUsbDispose.syncLE(data.event);
                }

            }
        });
    }

    @Override
    public void setUsbDispose() {
        mUsbDispose = new UsbDispose();
        mUsbDispose.dispose(new UsbDispose.SyncStatus() {
            @Override
            public void onSyncSucc() {

            }

            @Override
            public void onSyncFailed() {

            }

            @Override
            public void onSyncStart() {

            }
        });
    }

    @Override
    public int getCompoundCaptionIndex(int curZValue) {
        int index = -1;
        int count = TimelineData.instance().getCompoundCaptionArray().size();
        for (int i = 0; i < count; ++i) {
            int zVal = TimelineData.instance().getCompoundCaptionArray().get(i).getCaptionZVal();
            if (curZValue == zVal) {
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    public void compileVideo(Context context, NvsStreamingContext streamingContext, NvsTimeline timeline, String compilePath, long startTime, long endTime) {
        if(mVideoCompile==null){
            mVideoCompile=VideoCompile.getInstance(streamingContext,context);
        }
        mVideoCompile.setOnCompileCallBack(new VideoCompile.OnCompileCallBack() {
            @Override
            public void onCallBack(boolean isStatus) {
                if(mView!=null){
                    mView.videoCompileResult(isStatus);
                }
            }
        });
        mVideoCompile.compileVideo(timeline,compilePath,startTime,endTime);
    }

    @Override
    public void destroy() {
        if(mVideoCompile!=null){
            mVideoCompile.setOnCompileCallBack(null);
            mVideoCompile.destroy();
        }
    }
}
