package com.test.xcamera.phonealbum.usecase;

import com.editvideo.dataInfo.StickerInfo;
import com.editvideo.dataInfo.TimelineData;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.phonealbum.widget.VideoEditManger;
import com.test.xcamera.utils.AppExecutors;
import com.test.xcamera.utils.ParseJsonFile;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineAnimatedSticker;
import com.meicam.sdk.NvsTimelineCompoundCaption;

import java.util.ArrayList;
import java.util.List;

public class VideoCompoundSticker {

    private VideoStickerCallaBack mVideoCompoundCallaBack;



    public static VideoCompoundSticker getInstance() {
        return new VideoCompoundSticker();
    }
    public interface VideoStickerCallaBack{
        void OnStickerArrayList(ArrayList<StickerInfo> list);
    }
    public VideoCompoundSticker(){

    }
    public void getComStickerList(VideoStickerCallaBack mVideoCompoundCallaBack){
        AppExecutors.getInstance().diskIO().execute(() -> {
            ArrayList<StickerInfo> list=getComStickerList();
            AppExecutors.getInstance().mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    if(mVideoCompoundCallaBack!=null){
                        mVideoCompoundCallaBack.OnStickerArrayList(list);
                    }
                }
            });
        });
    }
    /**
     * 获取组合字幕列表
     * @return
     */
    public ArrayList<StickerInfo> getComStickerList(){
        ArrayList<StickerInfo> comList = new ArrayList<>();
        String jsonBundlePath = "sticker/info.json";
        ArrayList<ParseJsonFile.FxJsonFileInfo.JsonFileInfo> infoLists = ParseJsonFile.readBundleFxJsonFile(AiCameraApplication.getContext(), jsonBundlePath);
        if(infoLists==null){
            return null;
        }
        String coverPath = "file:///android_asset/sticker/";
        for (ParseJsonFile.FxJsonFileInfo.JsonFileInfo info:infoLists){

            StickerInfo stickerInfo=new StickerInfo();
            stickerInfo.setId(info.getFxPackageId());
            stickerInfo.setCustomSticker(false);
            stickerInfo.setImagePath(coverPath+info.getImageName());
            stickerInfo.setInPoint(0);
            stickerInfo.setOutPoint(3*VideoEditManger.VIDEO_microsecond_TIME);
            stickerInfo.setVolumeGain(1);
            comList.add(stickerInfo);
        }


        return comList;

    }

    public  float getCurStickerZVal(NvsTimeline timeline) {
        float zVal = 0.0f;
        NvsTimelineCompoundCaption caption = timeline.getFirstCompoundCaption();
        while (caption != null) {
            float tmpZVal = caption.getZValue();
            if (tmpZVal > zVal)
                zVal = tmpZVal;
            caption = timeline.getNextCaption(caption);
        }
        zVal += 1.0;
        return zVal;
    }
    public void removeAllSticker(NvsTimeline timeline){
        List<StickerInfo> mediaData = TimelineData.instance().getStickerData();
        if(mediaData==null){
            return;
        }
        for(StickerInfo info:mediaData){
            NvsTimelineAnimatedSticker compoundCaption=VideoEditManger.getNvsNvsTimelineSticker(timeline,info);
            if(compoundCaption!=null){
                VideoEditManger.delComSticker(timeline,compoundCaption);
            }
        }
        TimelineData.instance().getStickerData().clear();

    }
}
