package com.test.xcamera.home.fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dueeeke.videoplayer.controller.BaseVideoController;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.player.VideoViewManager;
import com.editvideo.ScreenUtils;
import com.jaeger.library.StatusBarUtil;
import com.test.xcamera.R;
import com.test.xcamera.accrssory.AccessoryManager;
import com.test.xcamera.activity.MoFPVActivity;
import com.test.xcamera.application.AppContext;
import com.test.xcamera.bean.CommunityBean;
import com.test.xcamera.bean.FeedList;
import com.test.xcamera.h5_page.CommunityActivity;
import com.test.xcamera.h5_page.H5BasePageActivity;
import com.test.xcamera.login.LoginActivty;
import com.test.xcamera.home.adapter.FeedRecycleViewAdapter;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.NetworkUtil;
import com.test.xcamera.widget.DouYinController;
import com.test.xcamera.widget.LoveLayout;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeedFragment extends Fragment implements FeedInterface.PresenterToView {

    @BindView(R.id.rv_feedFragment_feed)
    RecyclerView feedRecycleView;
    @BindView(R.id.srl_feedFragment_refresh)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.ll_feedFragment_netwrokErrorView)
    RelativeLayout networkErrorLinearLayout;
    @BindView(R.id.tv_feedFragment_recommend)
    TextView recommendTextView;
    @BindView(R.id.tv_feedFragment_activity)
    TextView activityTextView;

    private FeedRecycleViewAdapter feedRecycleViewAdapter;
    private PagerSnapHelper mPagerSnapHelper = new PagerSnapHelper();
    int pageSize = 10;
    int mPageNumber = 1;
    private int currentPlayPosition = 0;
    private boolean useMobileData = false;
    private CommunityBean communityBean;
    private FeedPresenter feedPresenter;
    private boolean refresh;
    private int index;
    private Handler handler = new Handler();

    @OnClick({R.id.iv_feedFragment_camera, R.id.tv_feedFragment_recommend, R.id.tv_feedFragment_activity,
            R.id.btn_networkError_refresh})
    public void OnClickView(View v) {
        switch (v.getId()) {
            case R.id.btn_networkError_refresh:
                feedPresenter.getCommunity();
                getFeedList(false, 1);
                break;
            case R.id.iv_feedFragment_camera:
                if (!AccessoryManager.getInstance().mIsRunning) {
                    CameraToastUtil.show(getResources().getString(R.string.connectCamera), getActivity());
                    return;
                }
                openActivity(MoFPVActivity.class);
                break;
            case R.id.tv_feedFragment_recommend:
                recommendTextView.setTextColor(getResources().getColor(R.color.white));
                activityTextView.setTextColor(getResources().getColor(R.color.feedFragment_gray));
                break;
            case R.id.tv_feedFragment_activity:
                activityTextView.setTextColor(getResources().getColor(R.color.white));
                recommendTextView.setTextColor(getResources().getColor(R.color.feedFragment_gray));
                break;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View subScreenView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_feed, null);
        ButterKnife.bind(this, subScreenView);
        feedPresenter = new FeedPresenter(this);
        return subScreenView;
    }

    @Override
    public void onResume() {
        super.onResume();
        feedPresenter.getCommunity();
    }

    public CommunityBean getCommunityBean() {
        return communityBean;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        StatusBarUtil.setTranslucentForImageViewInFragment(getActivity(), 50, null);

        initRecycleView();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFeedList(true, 1);
            }
        });

        getFeedList(true, 1);
    }

    public void openActivity(Class clazz) {
        Intent intent = new Intent(getActivity(), clazz);
        startActivity(intent);
    }

    private void initRecycleView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        feedRecycleView.setLayoutManager(linearLayoutManager);
        feedRecycleViewAdapter = new FeedRecycleViewAdapter(getActivity(), this);
        feedRecycleView.setAdapter(feedRecycleViewAdapter);
        mPagerSnapHelper.attachToRecyclerView(feedRecycleView);

        feedRecycleView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                int position = feedRecycleView.getLayoutManager().getPosition(view);
                if (position == 0) {
                    playVideo(0);
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                LoveLayout loveLayout=view.findViewById(R.id.mLoveLayout);
                if(loveLayout!=null){
                    loveLayout.onPause();
                }
                IjkVideoView ijkVideoView = view.findViewById(R.id.ijk_feedAdapter_videoPlayer);
                if (ijkVideoView != null) {
                    ijkVideoView.release();
                }
            }
        });

        feedRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    LinearLayoutManager mLineManager = (LinearLayoutManager) feedRecycleView.getLayoutManager();
                    int scrollPosition = getCurrentViewIndex(getActivity(), mLineManager);
                    if (scrollPosition != currentPlayPosition) {
                        currentPlayPosition = scrollPosition;
                        playVideo(currentPlayPosition);
                    }
                    index = recyclerView.getLayoutManager().getItemCount() - 1;
                    int screenHeight = ScreenUtils.getScreenHeight(getActivity());
                    boolean bottom = recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange() - screenHeight;
                    if (bottom && feedRecycleViewAdapter.mData.size() > 0) {
                        mPageNumber += 1;
                        getFeedList(false, mPageNumber);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        feedRecycleViewAdapter.setOnRecyclerItemClickListener(new FeedRecycleViewAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                IjkVideoView ijkVideoView = view.findViewById(R.id.ijk_feedAdapter_videoPlayer);
                if (ijkVideoView.isPlaying()) {
                    pauseVideo(true);
                } else {
                    pauseVideo(false);
                }
            }
        });
    }

    private void playVideo(final int position) {
        View itemView = feedRecycleView.getLayoutManager().findViewByPosition(position);
        if (itemView != null) {
            if (NetworkUtil.isMobileConnected(getActivity()) && !useMobileData) {
                useMobileData = true;
                Toast.makeText(getActivity(), getResources().getString(R.string.attentionForNotWifi), Toast.LENGTH_LONG).show();
            }
            WeakReference<IjkVideoView> ijkVideoView = new WeakReference<>(itemView.findViewById(R.id.ijk_feedAdapter_videoPlayer));
            ijkVideoView.get().start();
        }
    }

    public void pauseVideo(boolean pause) {
        if (feedRecycleView == null) {
            return;
        }
        View itemView = feedRecycleView.getChildAt(0);

        if (itemView != null) {

            WeakReference<IjkVideoView> ijkVideoView = new WeakReference<>(itemView.findViewById(R.id.ijk_feedAdapter_videoPlayer));
            if (pause) {
                ijkVideoView.get().release();
                ijkVideoView.get().pause();
                ijkVideoView.get().pauseStatus();
                BaseVideoController baseVideoController = ijkVideoView.get().getmVideoController();
                if (baseVideoController instanceof DouYinController) {
                    DouYinController controller = (DouYinController) baseVideoController;
                    controller.setSelect(true);
                }
            } else {
                ijkVideoView.get().resume();
            }
        }
    }

    public void getFeedList(boolean refresh, int pageNumber) {
        this.refresh = refresh;
        if (refresh) {
            feedPresenter.getCommunity();
            mPageNumber = 1;
        }
        feedPresenter.getFeedList(pageNumber, pageSize);
    }

    public static int getCurrentViewIndex(Context context, LinearLayoutManager mLineManager) {
        int firstVisibleItem = mLineManager.findFirstVisibleItemPosition();
        int lastVisibleItem = mLineManager.findLastVisibleItemPosition();
        int currentIndex = firstVisibleItem;
        for (int i = firstVisibleItem; i <= lastVisibleItem; i++) {
            View view = mLineManager.findViewByPosition(i);
            if (null == view) {
                continue;
            }
            Rect localRect = new Rect();
            view.getLocalVisibleRect(localRect);
            int showHeight = localRect.bottom - localRect.top;
            int middleContentHeight = ScreenUtils.getScreenHeight(context);
            if (showHeight > middleContentHeight / 2) {
                currentIndex = i;
            }
        }

        if (currentIndex < 0) {
            currentIndex = 0;
        }
        return currentIndex;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        pauseVideo(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseVideo(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        pauseVideo(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        VideoViewManager.instance().releaseVideoPlayer();
        if (handler != null) {
            handler.removeCallbacks(null);
            handler = null;
        }
    }

    public void login() {
        //跳转登录页面
        startActivity(new Intent(getActivity(), LoginActivty.class));
    }

    public void loginCommunity() {
        //跳转社区页面
        if (this.communityBean == null) {
            return;
        }
        H5BasePageActivity.openActivity(getActivity(), communityBean.getData().getTargetUrl(), CommunityActivity.class);
    }

    @Override
    public void feedVideoData(List<FeedList.Feed> feeds, int pageNumber) {
        if (pageNumber > 1 && feeds.size() == 0 && currentPlayPosition == index) {
            CameraToastUtil.show(AppContext.getInstance().
                            getResources().getString(R.string.feed_data_no_more),
                    AppContext.getInstance());
        } else {
            networkErrorLinearLayout.setVisibility(View.GONE);
            refreshLayout.setVisibility(View.VISIBLE);
            pauseVideo(false);
            refreshLayout.setRefreshing(false);//取消刷新
        }
    }

    @Override
    public void refreshView(List<FeedList.Feed> feeds) {
        feedRecycleViewAdapter.updateData(refresh, feeds);
    }

    @Override
    public void dataError(String errorInfo) {
        CameraToastUtil.show(AppContext.getInstance().
                        getResources().getString(R.string.net_enable),
                AppContext.getInstance());
        networkErrorLinearLayout.setVisibility(View.VISIBLE);
        refreshLayout.setVisibility(View.GONE);
        pauseVideo(true);
        if (refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);//取消刷新
        }
    }

    @Override
    public void setCommunityBean(CommunityBean communityBean) {
        this.communityBean = communityBean;
        boolean isHide = communityBean != null && communityBean.getData().getStatus() == 1 &&
                !TextUtils.isEmpty(communityBean.getData().getTargetUrl());
        if (feedRecycleViewAdapter != null) {
            feedRecycleViewAdapter.setCommunityIconHide(isHide);
        }
    }
}