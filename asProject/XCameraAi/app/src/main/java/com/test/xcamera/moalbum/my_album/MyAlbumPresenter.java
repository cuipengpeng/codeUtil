package com.test.xcamera.moalbum.my_album;

import android.content.Context;
import android.os.Handler;

import com.test.xcamera.moalbum.bean.MoAlbumFolder;

/**
 * Created by zhouxuecheng on
 * Create Time 2020/7/23
 * e-mail zhouxuecheng1991@163.com
 */

public class MyAlbumPresenter implements MyAlbumInterface.ModelToPresenter {
    private final MyAlbumInterface.PresenterToView presenterToView;
    private final MyAlbumModel myAlbumModel;
    private Handler handler;

    public MyAlbumPresenter(Context context, MyAlbumInterface.PresenterToView presenterToView) {
        this.presenterToView = presenterToView;
        myAlbumModel = new MyAlbumModel(context, this);
        handler = new Handler();
    }


    public void stopPreview() {
        myAlbumModel.stopFpvPreview();
    }

    public void stopFpvModel() {
        myAlbumModel.stopFpvMode();
    }

    /**
     * 获取相机数据
     *
     * @param page
     */
    public void getCameraAlbumList(int page) {
        myAlbumModel.getCameraAlbumListData(page);
    }

    /**
     * 获取手机数据
     */
    public void getMobileAlbumData() {
        myAlbumModel.getMobileAlbumListData();
    }

    @Override
    public void setCameraAlbumData(MoAlbumFolder items, int page) {
        if (handler != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    presenterToView.refreshCameraAlbumData(items, page);
                }
            });
        }
    }

    @Override
    public void setMobileAlbumData(MoAlbumFolder requestAlbumFolders) {
        if (handler != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    presenterToView.refreshMobileAlbumData(requestAlbumFolders);
                }
            });
        }
    }

    @Override
    public void interfaceError(String s) {
        presenterToView.interfaceError();
    }

    @Override
    public void stopFpvPreviewSuccess() {
        presenterToView.stopFpvPreviewSuccess();
    }

    @Override
    public void stopFpvPreviewFailed() {
        presenterToView.stopFpvPreviewFailed();
    }

    @Override
    public void stopFpvModeSuccess() {
        presenterToView.stopFpvModeSuccess();
    }

    @Override
    public void stopFpvModeFailed() {
        presenterToView.stopFpvModeFailed();
    }
}
