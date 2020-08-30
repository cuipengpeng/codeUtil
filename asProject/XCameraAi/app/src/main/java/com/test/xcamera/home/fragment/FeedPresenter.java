package com.test.xcamera.home.fragment;

import com.test.xcamera.bean.CommunityBean;
import com.test.xcamera.bean.FeedList;

import java.util.List;

/**
 * Created by zhouxuecheng on
 * Create Time 2020/7/22
 * e-mail zhouxuecheng1991@163.com
 * 接口流向
 */

public class FeedPresenter implements FeedInterface.ModelToPresenter {
    private final FeedInterface.PresenterToView presenterToView;
    private final FeedModel feedModel;

    public FeedPresenter(FeedInterface.PresenterToView presenterToView) {
        this.presenterToView = presenterToView;
        feedModel = new FeedModel(this);
    }

    public void getCommunity() {
        feedModel.getCommunity();
    }

    /**
     * 获取feed流数据的入口
     *
     * @param pageNumber
     * @param pageSize
     */
    public void getFeedList(int pageNumber, int pageSize) {
        feedModel.getFeedVideoListData(pageNumber, pageSize);
    }

    @Override
    public void feedVideoList(List<FeedList.Feed> feeds, int pageNumber) {
        presenterToView.feedVideoData(feeds, pageNumber);
    }

    @Override
    public void refreshFeed(List<FeedList.Feed> feeds) {
        presenterToView.refreshView(feeds);
    }

    @Override
    public void interfaceError(String errorInfo) {
        presenterToView.dataError(errorInfo);
    }

    @Override
    public void community(CommunityBean communityBean) {
        presenterToView.setCommunityBean(communityBean);
    }
}
