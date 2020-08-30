package com.test.xcamera.home;

import android.widget.RelativeLayout;

import com.framwork.base.view.BaseViewInterface;
import com.test.xcamera.bean.FeedList;
import com.test.xcamera.widget.RingProgressBar;

import java.util.List;

/**
 * Created by smz on 2019/11/13.
 */

public interface RecommendFragmentView extends BaseViewInterface{

    void addView(List<FeedList.Feed> list, boolean isLoadMore, List<FeedList.Feed> loadMoreFeed);


    void  Refresh();

    void  setCurrentLikeAndShare();

    void  setCurrentLikeAndShare(List<FeedList.Feed> mList);

    RingProgressBar getProgressBar();

    RelativeLayout  getRelativeLayout();

}
