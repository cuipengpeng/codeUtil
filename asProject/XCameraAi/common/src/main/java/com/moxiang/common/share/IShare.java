package com.moxiang.common.share;

/**
 * Created by admin on 2019/11/28.
 */

import com.umeng.socialize.UMShareListener;

public interface  IShare extends UMShareListener{
    /**
     * 回到首页
     */
    void goHome();

    /**
     * 去发布页面
     */
    void goPublishPage();

    /**
     * 选择的分享渠道
     * @param shareChooser
     */
    void onItemClick(ShareManager.ShareChooser shareChooser);
}
