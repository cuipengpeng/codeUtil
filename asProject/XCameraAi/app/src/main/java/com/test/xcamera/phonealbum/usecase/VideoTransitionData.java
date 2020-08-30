package com.test.xcamera.phonealbum.usecase;

import com.editvideo.dataInfo.TransitionInfo;
import com.test.xcamera.R;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.phonealbum.bean.MusicBean;
import com.test.xcamera.utils.ParseJsonFile;

import java.util.ArrayList;
import java.util.List;

public class VideoTransitionData {
    public static VideoTransitionData getInstance() {
        return new VideoTransitionData();
    }

    public List<MusicBean> getTransitionDataList(TransitionInfo info) {
        List<MusicBean> transitionBeanList = new ArrayList<>();
        String jsonBundlePath = "transition/info.json";
        ArrayList<ParseJsonFile.FxJsonFileInfo.JsonFileInfo> infoLists = ParseJsonFile.readBundleFxJsonFile(AiCameraApplication.getContext(), jsonBundlePath);
        if (infoLists != null) {
            for (ParseJsonFile.FxJsonFileInfo.JsonFileInfo jsonFileInfo : infoLists) {
                if(jsonFileInfo==null){
                    continue;
                }
                StringBuilder coverPath = new StringBuilder("file:///android_asset/transition/");
                coverPath.append(jsonFileInfo.getImageName());
                MusicBean musicBean=new MusicBean(coverPath.toString(), R.mipmap.filter_shengxia, jsonFileInfo.getName(), jsonFileInfo.getFxPackageId());
                transitionBeanList.add(musicBean);
            }
        }

        boolean tag = true;
        if (info != null && info.getTransitionId() != null) {
            for (MusicBean bean : transitionBeanList) {
                if (info.getTransitionId().equals(bean.getId())) {
                    bean.setSelected(true);
                    tag = false;
                    break;
                }
            }
        }
        if (tag) {
            transitionBeanList.get(0).setSelected(true);
        }
        return transitionBeanList;
    }
}
