package com.test.xcamera.phonealbum.usecase;

import com.editvideo.VideoClipFxInfo;
import com.editvideo.dataInfo.TimelineData;
import com.test.xcamera.R;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.phonealbum.bean.MusicBean;
import com.test.xcamera.utils.ParseJsonFile;

import java.util.ArrayList;
import java.util.List;

public class VideoFilerData {
    public static VideoFilerData getInstance() {
        return new VideoFilerData();
    }
    public List<MusicBean> getVideoFilerList(){
        List<MusicBean> filterBeanList = new ArrayList<>();
        String jsonBundlePath = "filter/info.json";
        ArrayList<ParseJsonFile.FxJsonFileInfo.JsonFileInfo> infoLists = ParseJsonFile.readBundleFxJsonFile(AiCameraApplication.getContext(), jsonBundlePath);
        VideoClipFxInfo fxData=TimelineData.instance().getVideoClipFxData();
        if (infoLists != null) {
            for (ParseJsonFile.FxJsonFileInfo.JsonFileInfo jsonFileInfo : infoLists) {
                StringBuilder coverPath = new StringBuilder("file:///android_asset/filter/");
                coverPath.append(jsonFileInfo.getImageName());
                MusicBean musicBean=new MusicBean(coverPath.toString(), R.mipmap.filter_shengxia, jsonFileInfo.getName(), jsonFileInfo.getFxPackageId());
                filterBeanList.add(musicBean);
            }
        }
        filterBeanList.get(0).setSelected(true);
        if(fxData!=null){
            for(MusicBean musicBean:filterBeanList){
                if(fxData.getFxId()!=null&&fxData.getFxId().equals(musicBean.getId())){
                    musicBean.setSelected(true);
                }else {
                    musicBean.setSelected(false);
                }
            }
        }
        return filterBeanList;
    }
}
