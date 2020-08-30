package com.test.xcamera.go_up;

import com.test.xcamera.api.ApiImpl;
import com.test.xcamera.api.CallBack;
import com.test.xcamera.bean.CommunityBean;

/**
 * Created by zhouxuecheng on
 * Create Time 2020/7/22
 * e-mail zhouxuecheng1991@163.com
 * <p>
 * 对数据的处理,包括网络请求的部分
 */

public class GoUpModel {
    private final GoUpInterface.ModelToPresenter modelToPresenter;

    public GoUpModel(GoUpInterface.ModelToPresenter modelToPresenter) {
        this.modelToPresenter = modelToPresenter;
    }

    /**
     * 获取社区帮助是否显示
     */
    public void communityHelp() {
        ApiImpl.getInstance().community(new CallBack<CommunityBean>() {
            @Override
            public void onSuccess(CommunityBean communityBean) {
                if (modelToPresenter != null && communityBean != null) {
                    modelToPresenter.communityHelp(communityBean);
                } else {
                    modelToPresenter.interfaceError("communityBean is null");
                }
            }

            @Override
            public void onFailure(Throwable e) {
                if (modelToPresenter != null) {
                    modelToPresenter.interfaceError(e.toString());
                }
            }

            @Override
            public void onCompleted() {

            }
        });
    }
}
