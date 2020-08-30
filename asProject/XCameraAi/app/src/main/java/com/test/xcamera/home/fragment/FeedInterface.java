package com.test.xcamera.home.fragment;

import com.test.xcamera.bean.CommunityBean;
import com.test.xcamera.bean.FeedList;

import java.util.List;

/**
 * Created by zhouxuecheng on
 * Create Time 2020/7/22
 * e-mail zhouxuecheng1991@163.com
 */

public class FeedInterface {
    public interface ModelToPresenter {
        /**
         * feed流视频数据
         *
         * @param feeds
         */
        void feedVideoList(List<FeedList.Feed> feeds, int pageNumber);

        /**
         * 最终封装好的数据包括点赞分享落地页所有的数据
         *
         * @param feeds
         */
        void refreshFeed(List<FeedList.Feed> feeds);


        /**
         * 后台接口请求错误
         *
         * @param errorInfo
         */
        void interfaceError(String errorInfo);

        void community(CommunityBean communityBean);
    }

    public interface PresenterToView {
        /**
         * feed流视频数据
         *
         * @param feeds
         */
        void feedVideoData(List<FeedList.Feed> feeds, int pageNumber);

        /**
         * 刷新点赞和落地页,分享
         *
         * @param feeds
         */
        void refreshView(List<FeedList.Feed> feeds);

        /**
         * 数据出错
         *
         * @param errorInfo
         */
        void dataError(String errorInfo);

        /**
         * 获取社区状态
         *
         * @param communityBean
         */
        void setCommunityBean(CommunityBean communityBean);
    }
}
