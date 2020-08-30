package com.editvideo.dataInfo;

import android.util.Log;


import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class RecordClipsInfo implements Serializable {
    private static final String TAG = "RecordClipInfo";
    private boolean mIsConverted;//转码完成标志
    private List<ClipInfo> mClipList;
    private List<ClipInfo> mReverseClipList;
    private MusicInfo mMusicInfo;

    public RecordClipsInfo(){
        mClipList = Collections.synchronizedList(new ArrayList<ClipInfo>());
        mReverseClipList = Collections.synchronizedList(new ArrayList<ClipInfo>());
        mIsConverted = false;
    }

    public ClipInfo getClipByPath(String path){
        if(mClipList == null){
            return null;
        }

        for(int i = 0; i < mClipList.size(); i++){
            ClipInfo clip = mClipList.get(i);
            if(path.equals(clip.getFilePath())){
                return clip;
            }
        }

        return null;
    }

    public ClipInfo getReverseClipByPath(String path){
        if(mReverseClipList == null){
            return null;
        }
        for(int i = 0; i < mReverseClipList.size(); i++){
            ClipInfo clip = mReverseClipList.get(i);
            if(path.equals(clip.getFilePath())){
                return clip;
            }
        }
        return null;
    }

    public void setMusicInfo(MusicInfo musicInfo){
        mMusicInfo = musicInfo;
    }

    public MusicInfo getMusicInfo(){
        return mMusicInfo;
    }

    public void setIsConvert(boolean isConvert){
        mIsConverted = isConvert;
    }

    public boolean getIsConvert(){
        return mIsConverted;
    }

    public boolean addClip(ClipInfo clip){
        mIsConverted = false;
        return mClipList.add(clip);
    }
    public ClipInfo setClip(int index,ClipInfo clip){
        mIsConverted = false;
        return mClipList.set(index,clip);
    }

    public ClipInfo removeLastClip(){
        int size = mClipList.size();
        if( size > 0){
            final ClipInfo clip = mClipList.remove(size-1);

            new Thread(){
                @Override
                public void run() {
                    super.run();
                    File file = new File(clip.getFilePath());
                    if(file.exists()){
                        try {
                            file.delete();
                        }catch (Exception e){
                            e.printStackTrace();
                            Log.e(TAG, "删除视频失败!");
                        }
                    }
                }
            }.start();

            return clip;
        }
        return null;
    }

    public long getClipsDurationBySpeed(){
        long duration = 0;
        for(int i = 0; i < mClipList.size(); i++){
            ClipInfo clip = mClipList.get(i);
            duration += clip.getmDurationBySpeed();
        }
        return duration;
    }

    public List<ClipInfo> getClipList(){
        return mClipList;
    }

    public List<ClipInfo> getReverseClipList(){
        return mReverseClipList;
    }
}
