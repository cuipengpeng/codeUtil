package com.test.xcamera.go_up;

import android.text.TextUtils;

import com.test.xcamera.bean.CommunityBean;

/**
 * Created by zhouxuecheng on
 * Create Time 2020/7/22
 * e-mail zhouxuecheng1991@163.com
 */

public class GoUpPresenter implements GoUpInterface.ModelToPresenter {
    private final GoUpInterface.PresenterToView presenterToView;
    private final GoUpModel goUpModel;

    public GoUpPresenter(GoUpInterface.PresenterToView presenterToView) {
        this.presenterToView = presenterToView;
        goUpModel = new GoUpModel(this);
    }

    public void getCommunityIsHide() {
        goUpModel.communityHelp();
    }

    @Override
    public void communityHelp(CommunityBean communityBean) {
        presenterToView.communityIconIsHide(communityBean.getData().getStatus() == 1 &&
                !TextUtils.isEmpty(communityBean.getData().getTargetUrl()),communityBean);
    }

    @Override
    public void interfaceError(String errorInfo) {
        presenterToView.showErrorView(errorInfo);
    }
}
