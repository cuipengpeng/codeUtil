package com.test.xcamera.phonealbum.player.mvp;


import android.support.annotation.NonNull;
import android.widget.RelativeLayout;

import com.editvideo.NvAsset;
import com.editvideo.ScreenUtils;
import com.editvideo.dataInfo.TimelineData;
import com.test.xcamera.application.AiCameraApplication;


public class VideoPlayPresenter extends VideoPlayContract.Presenter {
    private VideoPlayContract.View mView;
    public static VideoPlayPresenter create(VideoPlayContract.View view) {
        return new VideoPlayPresenter(view);
    }

    private VideoPlayPresenter(@NonNull VideoPlayContract.View view) {
        mView = view;
    }

    @Override
    public void setLiveWindowRatio(RelativeLayout.LayoutParams layoutParams,
                                   int ratio, int width, int height) {

        int titleHeight = 0;
        int bottomHeight = 0;
        int statusHeight = ScreenUtils.getStatusBarHeight(AiCameraApplication.getContext());//状态栏高度
        int screenWidth = ScreenUtils.getScreenWidth(AiCameraApplication.getContext());//屏宽
        int screenHeight = ScreenUtils.getScreenHeight(AiCameraApplication.getContext());//屏高
        int newHeight = screenHeight - titleHeight - bottomHeight - statusHeight;
        if (height > 0) {
            newHeight = height;
        }
        switch (ratio) {
            case NvAsset.AspectRatio_16v9: // 16:9
                layoutParams.width = screenWidth;
                layoutParams.height = (int) (screenWidth * 9.0 / 16);
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

                break;
            case NvAsset.AspectRatio_1v1: //1:1
                layoutParams.width = screenWidth;
                layoutParams.height = screenWidth;
                if (newHeight < screenWidth) {
                    layoutParams.width = newHeight;
                    layoutParams.height = newHeight;
                }
                break;
            case NvAsset.AspectRatio_9v16: //9:16
                layoutParams.width = (int) (newHeight * 9.0f / 16f);
                layoutParams.height = newHeight;
                if (height == 0) {
                    layoutParams.width = screenWidth;
                    layoutParams.height = (int) (screenWidth * 16.0 / 9);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                }
                break;
            case NvAsset.AspectRatio_3v4: // 3:4
                layoutParams.width = (int) (newHeight * 3.0 / 4);
                layoutParams.height = newHeight;
                break;
            case NvAsset.AspectRatio_4v3: //4:3
                layoutParams.width = screenWidth;
                layoutParams.height = (int) (screenWidth * 3.0 / 4);
                break;
            default: // 16:9
                layoutParams.width = (int) (newHeight * 9.0f / 16);
                layoutParams.height = newHeight;
                break;
        }
        mView.doLiveWindowRatio(layoutParams);
        TimelineData.instance().setVideoCanvasWidth(layoutParams.width);
        TimelineData.instance().setVideoCanvasHeight(layoutParams.height);
    }

    @Override
    public void setSticker(long time, boolean Locked) {
//        List<? extends Mark> newOWLPackageList = OWLPackageManage.getHookedOWLPackageList(time / Config.DEFAULT_MS_TO_SECOND);
//        for (OWLPackage owlPackage : newOWLPackageList) {
//            OWLPackageManage.replaceOWLPackage(OWLVideoEffectUtils.resetOWLEffectFace(owlPackage, info, false));
//        }
//        mView.showRunes(newOWLPackageList, Locked, time);
    }
}
