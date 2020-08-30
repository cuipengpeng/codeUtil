package com.test.xcamera.download;

import com.test.xcamera.bean.FeedList;
import com.test.xcamera.utils.Constants;
import com.moxiang.common.mvp.BasePresenter;

import java.io.File;

/**
 * Created by admin on 2020/2/12.
 */

public class DownloadPresenter extends BasePresenter<DownloadContract.DownloadModel, DownloadContract.DownloadView> {
    @Override
    protected DownloadContract.DownloadModel createModel() {
        return new DownloadModel();
    }

    /**
     * 下载当前Feed 视频
     *
     * @param item
     */
    public void downloadFeedFlie(final FeedList.Feed item, final int index) {
        String path = Constants.mFileDirector + System.currentTimeMillis() + ".MP4";
        DownloadUtil.downLoadFile(item.getVideoFileId(), path, new DownloadListener() {
            @Override
            public void onStart() {


//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        ((RecommendFragmentView) viewThis).getRelativeLayout().setVisibility(View.VISIBLE);
//                    }
//                });
                getView().onStart();
            }

            @Override
            public void onProgress(final int progress) {
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        ((RecommendFragmentView) viewThis).getProgressBar().setProgress(progress);
//                    }
//                });
                getView().onProgress(progress);
            }

            @Override
            public void onFinish(final File path) {
                //todo   通知相册也  发送下载数量变化接口

//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        ((RecommendFragmentView) viewThis).getRelativeLayout().setVisibility(View.GONE);
//                        FileUtil.sendScanBroadcast(AiCameraApplication.mApplication, path.getAbsolutePath());
//                        CameraToastUtil.show("已下载到相册中", AiCameraApplication.mApplication);
//                        clickShare(index, item, textView);
//                    }
//                });
                getView().onFinish(path);
            }

            @Override
            public void onFail(String errorInfo) {
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        textView.setClickable(true);
//                        CameraToastUtil.show("下载视频失败", AiCameraApplication.mApplication);
//                        ((RecommendFragmentView) viewThis).getRelativeLayout().setVisibility(View.GONE);
//                    }
//                });
                getView().onFail(errorInfo);
            }
        });
    }
}
