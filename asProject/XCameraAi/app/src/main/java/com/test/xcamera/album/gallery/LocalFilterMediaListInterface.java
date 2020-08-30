package com.test.xcamera.album.gallery;

import com.test.xcamera.bean.MoAlbumItem;

import java.util.ArrayList;

/**
 * author : zxc
 * create_time : 2019/10/10 19:09
 * description
 */
public interface LocalFilterMediaListInterface {
    /**
     * 下载资源成功
     */
    void downLoadFileSuccess();

    /**
     * 删除资源成功
     */
    void deleteFileSuccess();

    /**
     * 刷新列表
     *
     * @param albumItems
     */
    void refreshList(ArrayList<MoAlbumItem> albumItems);

    /**
     * 获取相机媒体资源列表
     */
    void getCameraMediaList();
}
