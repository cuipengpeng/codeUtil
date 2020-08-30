package com.test.xcamera.phonealbum.presenter;

import com.editvideo.MediaData;
import com.test.xcamera.bean.MoAlbumItem;
import com.test.xcamera.bean.MoImage;

import java.util.ArrayList;
import java.util.List;

public class AlbumEditContract {
    public interface View{
        /**
         * 打开视频编辑
         * @param isOpen
         */
        void openVideoEdit(boolean isOpen);

        /**
         * 转码已下载的视频
         */
        void checkVideoIsConvert();

        /**
         * 显示相机素材数量
         */
        void showCameraAlbumCount(int count, MoImage thumbnail);
        /**
         * 列表获取成功
         * @param items
         */
        void showCameraMediaListSuccess(boolean refresh,ArrayList<MoAlbumItem> items);
        /**
         * 列表获取失败
         */
        void showCameraMediaListFailed();

        /**
         * 取消下载
         */
        void cancelDownload();

    }
    public interface Presenter{
        /**
         * 获取相机素材数量
         */
        void  getCameraAlbumCount();
        /**
         * 获取相机相册数据列表
         * @param refresh
         * @param pageIndex
         */
        void getRemoteSDcardCameraMediaList(final boolean refresh, int pageIndex) ;
        /**
         * 下载视频编辑素材
         * @param mediaDataList
         */
        void downloadSDCardFilesAndEditVideo(List<MediaData> mediaDataList);

        /**
         * 下载资源销毁
         */
        void downloadDestroy();

        /**
         * 取消下载handel
         */
        void clearDownloadHandleMsg();
        /**
         * 下载没有完成的资源
         */
        void downloadVideoDel();

        /**
         * 获取下载状态
         * @return
         */
        boolean getDownloadStatus();

        /**
         * 重置下载
         * @return
         */
        boolean resetDownloadStatus();

        boolean isDownloadStatus();
        /**
         * 销毁
         */
        void destroy();
    }
}
