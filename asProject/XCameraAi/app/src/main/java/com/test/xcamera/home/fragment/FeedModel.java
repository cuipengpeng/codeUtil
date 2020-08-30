package com.test.xcamera.home.fragment;

import com.test.xcamera.api.ApiImpl;
import com.test.xcamera.api.CallBack;
import com.test.xcamera.bean.CommunityBean;
import com.test.xcamera.bean.FeedList;
import com.test.xcamera.bean.SqueezeBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouxuecheng on
 * Create Time 2020/7/22
 * e-mail zhouxuecheng1991@163.com
 */

public class FeedModel {
    private final FeedInterface.ModelToPresenter modelToPresenter;

    public FeedModel(FeedInterface.ModelToPresenter modelToPresenter) {
        this.modelToPresenter = modelToPresenter;
    }

    /**
     * feed流页面社区状态
     */
    public void getCommunity() {
        ApiImpl.getInstance().community(new CallBack<CommunityBean>() {
            @Override
            public void onSuccess(CommunityBean communityBean) {
                modelToPresenter.community(communityBean);
            }

            @Override
            public void onFailure(Throwable e) {

            }

            @Override
            public void onCompleted() {

            }
        });
    }

    /**
     * 获取feed视频数据
     *
     * @param pageNumber
     * @param pageSize
     */
    public void getFeedVideoListData(int pageNumber, int pageSize) {
        ApiImpl.getInstance().getFeedList(pageNumber, pageSize, new CallBack<FeedList>() {
            @Override
            public void onSuccess(FeedList feedList) {
                if (feedList.isSucess()) {
                    //先返回feed流的视频数据,在请求点赞和分享,还有落地页面
                    List<FeedList.Feed> data = feedList.getData();
                    modelToPresenter.feedVideoList(data, pageNumber);

                    if (data.size() > 0) {
                        getFeedLikeAndShareCount(getFeedId(data), data);
                    }
                }
            }

            @Override
            public void onFailure(Throwable e) {
                modelToPresenter.interfaceError("getFeedVideoListData : " + e.toString());
            }

            @Override
            public void onCompleted() {

            }
        });
    }

    /**
     * 获取点赞数和分享数
     *
     * @param idList
     * @param beforeData
     */
    private void getFeedLikeAndShareCount(String idList, List<FeedList.Feed> beforeData) {
        ApiImpl.getInstance().getLikeAndShareNum(idList, new CallBack<FeedList>() {
            @Override
            public void onSuccess(FeedList feedlist) {
                List<FeedList.Feed> laterData = feedlist.getData();
                if (feedlist.getData().size() > 0) {
                    //数据组装,完成点赞数和分享数的组装
                    List<FeedList.Feed> feeds = dataLikeAndSharePackage(beforeData, laterData);
                    //请求落地页
                    getFeedSqueeze(getFeedId(feedlist.getData()), feeds);
                }
            }

            @Override
            public void onFailure(Throwable e) {
                modelToPresenter.interfaceError("getFeedLikeAndShareCount : " + e.toString());
            }

            @Override
            public void onCompleted() {

            }
        });
    }

    private void getFeedSqueeze(String idList, List<FeedList.Feed> feeds) {
        ApiImpl.getInstance().getFeedSqueeze(idList, new CallBack<SqueezeBean>() {
            @Override
            public void onSuccess(SqueezeBean squeezeBean) {
                ArrayList<SqueezeBean.Squeeze> data = squeezeBean.getData();
                //将最终的数据返回出去使用
                List<FeedList.Feed> resultFeeds = dataSqueezePackage(feeds, data);
                modelToPresenter.refreshFeed(resultFeeds);
            }

            @Override
            public void onFailure(Throwable e) {
                modelToPresenter.interfaceError("getFeedSqueeze : " + e.toString());
            }

            @Override
            public void onCompleted() {

            }
        });
    }

    private String getFeedId(List<FeedList.Feed> data) {
        StringBuilder builder = new StringBuilder();
        int length = data.size();
        for (int i = 0; i < length; i++) {
            FeedList.Feed feed = data.get(i);
            if (length > 0 && i == length - 1) {
                builder.append(feed.getOpusId());
            } else {
                builder.append(feed.getOpusId() + ",");
            }
        }
        return builder.toString();
    }

    /**
     * 对数据进行封装
     *
     * @param beforeData
     * @param laterData
     */
    private List<FeedList.Feed> dataLikeAndSharePackage(List<FeedList.Feed> beforeData, List<FeedList.Feed> laterData) {
        for (int i = 0; i < beforeData.size(); i++) {
            FeedList.Feed feedItem = beforeData.get(i);
            for (int j = 0; j < laterData.size(); j++) {
                FeedList.Feed feedLike = laterData.get(j);
                if (feedItem.getOpusId() == feedLike.getOpusId()) {
                    feedItem.setLikeNum(feedLike.getLikeNum());
                    feedItem.setShareNum(feedLike.getShareNum());
                    feedItem.setLike(feedLike.isLike());
                }
                if (j == laterData.size() - 1) {
                    break;
                }
            }
        }
        return beforeData;
    }

    /**
     * 组装落地页面的数据
     *
     * @param feeds
     * @param data
     */
    private List<FeedList.Feed> dataSqueezePackage(List<FeedList.Feed> feeds, ArrayList<SqueezeBean.Squeeze> data) {
        for (int i = 0; i < feeds.size(); i++) {
            FeedList.Feed feedItem = feeds.get(i);
            for (int j = 0; j < data.size(); j++) {
                SqueezeBean.Squeeze squeeze = data.get(j);
                if (feedItem.getOpusId() == squeeze.getOpusId()) {
                    feedItem.setIconUrl(squeeze.getIconUrl());
                    feedItem.setContent(squeeze.getContent());
                    feedItem.setLink(squeeze.getLink());
                }
                if (j == data.size() - 1) {
                    break;
                }
            }
        }
        return feeds;
    }
}
