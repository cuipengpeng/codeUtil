package com.test.xcamera.phonealbum.usecase;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.editvideo.MediaData;
import com.editvideo.ToastUtil;
import com.editvideo.dataInfo.ClipInfo;
import com.editvideo.dataInfo.RecordClipsInfo;
import com.editvideo.dataInfo.TimelineData;
import com.test.xcamera.R;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.utils.FileUtils;
import com.test.xcamera.utils.LogAccessory;
import com.test.xcamera.utils.LoggerUtils;
import com.test.xcamera.view.basedialog.dialog.CommonDownloadDialog;
import com.meicam.sdk.NvsAVFileInfo;
import com.meicam.sdk.NvsStreamingContext;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class VideoImportCheck {
    public static VideoImportCheck  mVideoImportCheck;
    private CommonDownloadDialog downProgressDialog;
    private RecordClipsInfo mRecordClipsInfo;
    private VideoMediaFileConvert mVideoMediaFileConvert;
    public static VideoImportCheck getInstance() {
        if(mVideoImportCheck!=null){
            return mVideoImportCheck;
        }else {
            mVideoImportCheck=new VideoImportCheck();
            return mVideoImportCheck;

        }
//        return new VideoImportCheck();
    }
    public static void reset(){
        mVideoImportCheck=null;
    }

    /**
     * 验证视频是否可以导入
     *
     * @param mediaData
     * @return
     */
    public boolean checkVideoImport(MediaData mediaData, int curSize) {
        LoggerUtils.printLog("club:checkVideoImport:Duration=" + mediaData.getDuration() + " size=" + (curSize + TimelineData.instance().getClipCount()));
        //视频时长大于15分钟禁止导入
        if (mediaData.getDuration() > 15 * 60 * 1000) {
            ToastUtil.showToast(AiCameraApplication.getContext(), AiCameraApplication.getContext().getResources().getString(R.string.video_edit_check_clip_duration));
            return false;
        }
        //视频素材最多有50 片段
        return  checkVideoClipCount(curSize);
    }
    public boolean checkVideoClipCount(){
           return checkVideoClipCount(0);
    }
    private boolean checkVideoClipCount(int size){
        if (size + TimelineData.instance().getClipCount() >= 50) {
            ToastUtil.showToast(AiCameraApplication.getContext(),  AiCameraApplication.getContext().getResources().getString(R.string.video_edit_check_clip_count));
            return false;
        }
        return true;
    }

    /**
     *检查是否需要转码
     * @param clipInfoList
     * @param context
     * @return
     */
    public boolean checkVideoIsConvertImport(List<ClipInfo> clipInfoList, WeakReference<Context> context, OnImportCheckCallBack mOnImportCheckCallBack) {
        if(mRecordClipsInfo==null){
            mRecordClipsInfo=new RecordClipsInfo();
        }
        setOnImportCheckCallBack(mOnImportCheckCallBack);
        for(ClipInfo info:clipInfoList){
            if(isVideoConvert(info.getFilePath())){
                int index=isExicRecordClipsInfo(info.getFilePath());
                info.setM_fileOldPath(info.getFilePath());
                if(index==-1){
                    mRecordClipsInfo.addClip(info);

                }else {
                    mRecordClipsInfo.setClip(index,info);

                }
            }
        }
        LogAccessory.getInstance().showLog("是否有需转码:checkVideoIsConvertImport：size" +mRecordClipsInfo.getClipList().size()
        );
        if(mRecordClipsInfo.getClipList().size()<=0){
//        if(true){//暂时不转码
            if(mOnImportCheckCallBack!=null){
                mOnImportCheckCallBack.onCheckFinish();
            }
            return true;
        }
        if (downProgressDialog == null) {
            downProgressDialog = new CommonDownloadDialog(context.get());
        }
        downProgressDialog.setCancelable(false);
        downProgressDialog.showDialog(false);
        if(downProgressDialog!=null){
            downProgressDialog.setProgress(AiCameraApplication.getContext().getResources().getString(R.string.video_edit_check_ing)+"0%");
        }
        downProgressDialog.setViewWidth(160);
        downProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                sendCancelMediaFileConvert();
            }
        });
        startVideoMediaFileConvert(mRecordClipsInfo);
        return false;
    }
    public void sendCancelMediaFileConvert(){
        if(mVideoMediaFileConvert!=null){
            mVideoMediaFileConvert.sendFinishConvertMsg();
            mVideoMediaFileConvert.sendCancelConvertMsg();
        }
        if(downProgressDialog!=null&&downProgressDialog.isShowing()){
            downProgressDialog.dismissDialog();
        }
        mVideoMediaFileConvert=null;
    }
    public void startVideoMediaFileConvert(RecordClipsInfo recordClipsInfo){
        //没有正在转码的视频
        if(mVideoMediaFileConvert==null||!mVideoMediaFileConvert.isIsConvertIng()){
            mVideoMediaFileConvert=new VideoMediaFileConvert(recordClipsInfo);
            mVideoMediaFileConvert.sendConvertFileMsg();
            mVideoMediaFileConvert.setOnMediaConvertCallBack(new VideoMediaFileConvert.OnMediaConvertCallBack() {
                @Override
                public void onProgress(ClipInfo clip, int index, int progress) {
                    if(downProgressDialog!=null&&downProgressDialog.isShowing()){
                        float clipValue=100f/mRecordClipsInfo.getClipList().size();
                        int p=(int)((clipValue)*(progress/100f)+(index*clipValue));
                        downProgressDialog.setProgress(AiCameraApplication.getContext().getResources().getString(R.string.video_edit_check_ing)+p+"%");
/*
                        downProgressDialog.setTvMsg("处理中...");//+(index+1)+"/"+mRecordClipsInfo.getClipList().size()
*/
                    }
                }

                @Override
                public void onClipFinish(ClipInfo clipInfo, boolean isSuccess) {
                    if(isSuccess){
                        clipInfo.setIsConvert(true);
                        resetMediaDataList(clipInfo);
                    }
                }

                @Override
                public void onFinish(List<ClipInfo> clipInfoList, boolean isSuccess) {
                    if(downProgressDialog!=null&&downProgressDialog.isShowing()){
                        downProgressDialog.dismissDialog();
                    }
                    if(mOnImportCheckCallBack!=null){
                        mOnImportCheckCallBack.onCheckFinish();
                        LogAccessory.getInstance().showLog("转码成功:" +"打开视频编辑-----"
                        );
                        reset();
                    }
                }

                @Override
                public void onError() {
                    if(downProgressDialog!=null&&downProgressDialog.isShowing()){
                        downProgressDialog.dismissDialog();
                    }
                    mVideoMediaFileConvert=null;
                    ToastUtil.showToast(AiCameraApplication.getContext(),  AiCameraApplication.getContext().getResources().getString(R.string.video_edit_check_error));
                    reset();
                }
            });
        }else {//如果有专门中直接添加
            mVideoMediaFileConvert.addClipList(recordClipsInfo.getClipList());
        }


    }
   private List<MediaData> mMediaDataList=new ArrayList<>();
    /**
     * 检查是否有需要转码的视频
     * @param mediaDataList
     * @return
     */
    public boolean checkVideoIsConvert(List<MediaData> mediaDataList){
        if(mRecordClipsInfo==null){
            mRecordClipsInfo=new RecordClipsInfo();
        }
        mMediaDataList=mediaDataList;
        for (MediaData mediaData : mMediaDataList) {
            if(isVideoConvert(mediaData.getPath())&&isExicRecordClipsInfo(mediaData.getPath())==-1){
                ClipInfo clipInfo = new ClipInfo();
                clipInfo.setFilePath(mediaData.getPath());
                clipInfo.setM_fileOldPath(mediaData.getPath());
                clipInfo.setDuration(mediaData.getDuration());
                clipInfo.setRemoteData(mediaData.isRemoteData());
                mRecordClipsInfo.getClipList().add(clipInfo);
            }
        }
        if(mRecordClipsInfo.getClipList().size()>0){
            startVideoMediaFileConvert(mRecordClipsInfo);
        }
        return false;
    }
    private int  isExicRecordClipsInfo(String path){
        int i=-1;
        for (ClipInfo info:mRecordClipsInfo.getClipList()){
            i++;
            if (info.getM_fileOldPath().equals(path)){
                return i;
            }
        }
        return -1;
    }
    private boolean resetMediaDataList(ClipInfo info){
        if(mMediaDataList==null){
            return false;
        }
        for (MediaData mediaData:mMediaDataList){
            if (info.getM_fileOldPath().equals(mediaData.getPath())){
                if(mediaData.isRemoteData()){
                    mediaData.setPath(info.getFilePath());
                }else {
                    mediaData.setPath(info.getFilePath());
                }
                FileUtils.deleteFile(info.getM_fileOldPath());
                return true;
            }
        }
        return false;
    }
    private  OnImportCheckCallBack mOnImportCheckCallBack;

    public void setOnImportCheckCallBack(OnImportCheckCallBack mOnImportCheckCallBack) {
        this.mOnImportCheckCallBack = mOnImportCheckCallBack;
    }

    public interface OnImportCheckCallBack{
        void onCheckFinish();
    }
    public boolean isVideoConvert(String  clipFilePath) {
        /*NvsSize nvsSize =VideoMediaFileConvert.getVideoNvsSize(clipFilePath);
        if(nvsSize!=null){
            Log.i("club","club_ClipInfo :视频宽高 w:"+nvsSize.width+" h:"+nvsSize.height);
        }*/
        return getVideoBitRate(clipFilePath) > 70 * 1000000;
    }
    public long getVideoBitRate(String  clipFilePath) {
        NvsAVFileInfo info = null;
        long bitRate = 0;
        if (NvsStreamingContext.getInstance() != null) {
            info = NvsStreamingContext.getInstance().getAVFileInfo(clipFilePath);
        }
        if (info != null) {
            bitRate = info.getDataRate();
        }

        Log.i("club","club_ClipInfo :视频码率 "+bitRate);
        return bitRate;
    }

}