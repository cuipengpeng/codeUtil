package com.test.xcamera.mointerface;

import com.test.xcamera.bean.SideKeyBean;

import java.util.List;

/**
 * Created by zll on 2019/10/25.
 */

public interface MoGetSideKeyCallback {
    void onSuccess(List<SideKeyBean> sideKeyList);
    void onFailed();
}
