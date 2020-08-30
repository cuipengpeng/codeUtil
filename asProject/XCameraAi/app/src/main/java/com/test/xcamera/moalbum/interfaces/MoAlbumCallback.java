package com.test.xcamera.moalbum.interfaces;

import com.test.xcamera.bean.MoAlbumItem;

import java.util.ArrayList;

/**
 * Created by zll on 2019/11/21.
 */

public interface MoAlbumCallback {
    /**
     * 下载资源成功
     */
    void downLoadFileSuccess();

    /**
     * 删除资源成功
     */
    void deleteLocalFileSuccess();

    void deleteCameraFileSuccess();

    /**
     * 刷新列表
     *
     * @param albumItems
     */
    void refreshList(ArrayList<MoAlbumItem> albumItems);

    /**
     * 刷新失败
     */
    void refreshListFailed();

    /**
     * 获取相机媒体资源列表
     */
    void getCameraMediaList();

    /**
     * 收藏
     *
     * @param selectedPhotoList
     */
    void likeSuccess(ArrayList<MoAlbumItem> selectedPhotoList);
}
