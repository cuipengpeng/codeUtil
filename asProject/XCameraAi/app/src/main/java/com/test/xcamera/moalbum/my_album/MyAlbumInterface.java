package com.test.xcamera.moalbum.my_album;

import com.test.xcamera.moalbum.bean.MoAlbumFolder;

/**
 * Created by zhouxuecheng on
 * Create Time 2020/7/23
 * e-mail zhouxuecheng1991@163.com
 */

public class MyAlbumInterface {
    public interface ModelToPresenter {

        /**
         * 获取相机数据成功
         *
         * @param items
         * @param page
         */
        void setCameraAlbumData(MoAlbumFolder items, int page);

        void interfaceError(String s);

        void stopFpvPreviewSuccess();

        void stopFpvPreviewFailed();

        void stopFpvModeSuccess();

        void stopFpvModeFailed();

        void setMobileAlbumData(MoAlbumFolder requestAlbumFolders);
    }

    public interface PresenterToView {

        void refreshMobileAlbumData(MoAlbumFolder requestAlbumFolders);

        void refreshCameraAlbumData(MoAlbumFolder requestAlbumFolders, int page);

        void stopFpvModeFailed();

        void stopFpvModeSuccess();

        void stopFpvPreviewFailed();

        void stopFpvPreviewSuccess();

        void interfaceError();
    }
}
