package com.test.xcamera.mointerface;

import com.test.xcamera.bean.MoAlbumItem;

import java.util.ArrayList;

/**
 * Created by zll on 2019/7/17.
 */

public interface MoAlbumListCallback{
    void onSuccess(ArrayList<MoAlbumItem> items);
    void onFailed();
}
