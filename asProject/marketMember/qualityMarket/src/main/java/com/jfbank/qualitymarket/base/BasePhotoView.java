package com.jfbank.qualitymarket.base;

import com.jph.takephoto.model.TResult;

/**
 * 功能：相册和拍照<br>
 * 作者：赵海<br>
 * 时间： 2017/4/25 0025<br>.
 * 版本：1.2.0
 */

public interface BasePhotoView {
    void takeSuccess(TResult result);

    void takeFail(TResult result, String msg);

    void takeCancel();
}
