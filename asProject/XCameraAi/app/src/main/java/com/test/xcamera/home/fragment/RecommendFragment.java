package com.test.xcamera.home.fragment;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.player.PlayerConfig;
import com.test.xcamera.R;
import com.test.xcamera.bean.FeedList;
import com.test.xcamera.download.DownloadContract;
import com.test.xcamera.download.DownloadPresenter;
import com.test.xcamera.home.DouYinAdapter;
import com.test.xcamera.home.presenter.RecommendFragmentPresenter;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.Constants;
import com.test.xcamera.utils.NetworkUtil;
import com.test.xcamera.utils.SPUtils;
import com.test.xcamera.view.ConstomSwipeRefreshView;
import com.test.xcamera.widget.DouYinController;
import com.test.xcamera.widget.RingProgressBar;
import com.test.xcamera.widget.VerticalViewPager;
import com.test.xcamera.zoom.library.uk.co.senab.photoview.PhotoView;
import com.moxiang.common.base.BaseFragment;
import com.moxiang.common.share.IShare;
import com.moxiang.common.share.ShareManager;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Author: mz
 * Time:  2019/10/15
 * 推荐页面
 */
public class RecommendFragment extends BaseFragment<DownloadPresenter> implements DownloadContract.DownloadView{
    @BindView(R.id.verticalviewpager)
    VerticalViewPager mVerticalViewpager;

    @BindView(R.id.tv_check)
    TextView tvCheck;
    @BindView(R.id.iv_delete)
    ImageView ivDelete;
    @BindView(R.id.lin_hardware)
    RelativeLayout linHardware;
    @BindView(R.id.tv_hardware)
    TextView tvHardware;

    @BindView(R.id.progress)
    RingProgressBar progress;
    @BindView(R.id.rl_progress)
    RelativeLayout rlProgress;
    @BindView(R.id.mSwipeRefreshView)
    ConstomSwipeRefreshView mSwipeRefreshView;

//    private List<MyTestBean> mlist2 = new ArrayList<>();
//    private List<LevideoData> mList = new ArrayList<>();

    private IjkVideoView mIjkVideoView;
    public DouYinController mDouYinController;
    private DouYinAdapter mDouYinAdapter;

    private List<View> mViews = null;
    private List<FeedList.Feed> feeds = new ArrayList();

    public int page = 1;
    public static final int PAGE_COUNT = 5;
    private TextView mTvVideoTitle;
    private int mPlayingPosition;
    private RecommendFragmentPresenter recommendFragmentPresenter;
    private boolean isLoadMore = false;

//    @Override
//    public int initView() {
//        return R.layout.fragment_recomend;
//    }


    RequestOptions options = new RequestOptions();
//            .placeholder(R.mipmap.myfirst)//图片加载出来前，显示的图片
//            .fallback(R.mipmap.myfirst) //url为空的时候,显示的图片
//            .error(R.mipmap.myfirst);//图片加载失败后，显示的图片;

    @Override
    protected void initPresenter() {
        mPresenter = new DownloadPresenter();
    }

    @Override
    protected int bindLayout() {
        return R.layout.fragment_recomend;
    }

    @Override
    public void initData() {
//        recommendFragmentPresenter = new RecommendFragmentPresenter(this, feeds);
        recommendFragmentPresenter.getFeedList(page, PAGE_COUNT, false);
        tvHardware.setSelected(true);

//        initjSON();
//        getImageData();
        initplayer();
        initViewPagerListener();
    }

    @Override
    protected void initView() {

    }

    public void initplayer() {
        mIjkVideoView = new IjkVideoView(getActivity());
        PlayerConfig config = new PlayerConfig.Builder().setLooping().build();
        mIjkVideoView.setPlayerConfig(config);
        mDouYinController = new DouYinController(getActivity());
        mIjkVideoView.setVideoController(mDouYinController);
    }

    private void initViewPagerListener() {
        mVerticalViewpager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mSwipeRefreshView.setInterceptFlag(position);
                mIjkVideoView.pause();
                if (mViews != null && mViews.size() > 0 && position == mViews.size() - 1)
                    loadMoreFeedData();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (mPlayingPosition == mVerticalViewpager.getCurrentItem()) return;
                if (state == VerticalViewPager.SCROLL_STATE_IDLE) {
                    mIjkVideoView.release();
                    ViewParent parent = mIjkVideoView.getParent();
                    if (parent != null && parent instanceof FrameLayout) {
                        ((FrameLayout) parent).removeView(mIjkVideoView);
                    }
                    startPlay();
                }
            }
        });

        mSwipeRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isLoadMore = false;
                page = 1;
                recommendFragmentPresenter.getFeedList(page, PAGE_COUNT, false);
            }
        });
    }


    private void startPlay() {
        if (mViews.size() == 0) return;
        View view = mViews.get(mVerticalViewpager.getCurrentItem());

        FrameLayout frameLayout = view.findViewById(R.id.container);
        PhotoView mCover = view.findViewById(R.id.cover_img);
        mDouYinController.setSelect(false);

        if (mCover != null && mCover.getDrawable() != null) {
            mDouYinController.getThumb().setImageDrawable(mCover.getDrawable());
        }

        ViewGroup parent = (ViewGroup) mIjkVideoView.getParent();

        if (parent != null) {
            parent.removeAllViews();
        }
        frameLayout.addView(mIjkVideoView);

        if (feeds.size() != 0) {
            String url = Constants.getFileIdToUrl(feeds.get(mVerticalViewpager.getCurrentItem()).getVideoFileId() + "");
            mIjkVideoView.setUrl(url);
            mIjkVideoView.setScreenScale(IjkVideoView.SCREEN_SCALE_DEFAULT); //播放的设置的屏幕参数问题
            mIjkVideoView.start();

            mPlayingPosition = mVerticalViewpager.getCurrentItem();
        }
    }

    private void loadMoreFeedData() {
        if (isLoadMore) {
            return;
        }
        isLoadMore = true;
        page += 1;
        recommendFragmentPresenter.getFeedList(page, PAGE_COUNT, true);
    }


    @OnClick({R.id.tv_check, R.id.iv_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_check:
//                startAct(UploadHardWareActivity.class);
                break;
            case R.id.iv_delete:
                linHardware.setVisibility(View.GONE);
                break;
        }
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (hidden && mDouYinController.getIvPlay().getVisibility() == View.GONE) {
            pauseVideo();
        } else if (!hidden && mDouYinController.getIvPlay().getVisibility() != View.GONE) { //按暂停

        } else if (!hidden && mDouYinController.getIvPlay().getVisibility() == View.GONE) { //没按暂停  播放时
            restartVideo();
//            pauseVideo();
        }

    }


    public void restartVideo() {
        mIjkVideoView.resume();
        if (mDouYinController != null) {
            mDouYinController.setSelect(false);
        }
    }

    public void pauseVideo() {
        mIjkVideoView.pause();
        if (mDouYinController != null) {
            mDouYinController.getIvPlay().setVisibility(View.GONE);
            mDouYinController.setSelect(false);
        }

    }

    public void stopVideo() {
        mIjkVideoView.pause();
        if (mDouYinController != null) {
            mDouYinController.getIvPlay().setVisibility(View.VISIBLE);
            mDouYinController.setSelect(true);
        }

    }

    public IjkVideoView getmIjkVideoView() {
        if (mIjkVideoView == null) {
            mIjkVideoView = new IjkVideoView(getActivity());
        }

        return mIjkVideoView;
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mIjkVideoView.isPlaying() && mDouYinController.getIvPlay().getVisibility() == View.GONE)
            pauseVideo();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        destoryVideoPlay();
    }

    private void destoryVideoPlay() {
        if (mIjkVideoView != null) {
            mIjkVideoView.release();
        }
    }

//    @Override
//    public void IsVisbilityUploadHardWare() {
//        //TODO linHardware.setVisibility(View.VISIBLE);
//    }
//
//    @Override
//    public void setProgresss(int progress) {
//
//    }
//
//    @Override
//    public void setTitleAndContent(String title, String Content) {
//
//    }
//
//    @Override
//    public void setVisable(int Visable1, int Visable2) {
//
//    }
//
//    @Override
//    public UploadDialog getDialog() {
//        return null;
//    }
//
//    @Override
//    public void setContent(int size, long time, String content) {
//
//    }
//
//    @Override
//    public void StartAct(Class clazz) {
//
//    }
//
//    @Override
//    public void isClickable() {
//
//    }

//    @Override
    public void addView(List<FeedList.Feed> list, boolean isLoadMore, List<FeedList.Feed> loadMoreFeedList) {
        if (isLoadMore) {
            this.isLoadMore = false;
            for (int i = 0; i < loadMoreFeedList.size(); i++) {
                FeedList.Feed item = loadMoreFeedList.get(i);
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_video_item, null);
                PhotoView videoCover = view.findViewById(R.id.cover_img);
                String url = Constants.getFileIdToUrl(item.getCoverFileId() + "");
                Glide.with(getActivity()).load(url).apply(options).into(videoCover);
                mViews.add(view);
            }
        } else {
            destoryVideoPlay();

            initAdapter(list);

            mVerticalViewpager.post(new Runnable() {
                @Override
                public void run() {
                    startPlay();
                }
            });
        }
    }

    private void initAdapter(List<FeedList.Feed> list) {
        if (mViews != null) {
            mViews.clear();
            mViews = null;
        }
        mViews = new ArrayList<>();
        mSwipeRefreshView.setRefreshing(false);
        if (mDouYinAdapter != null) {
            mDouYinAdapter = null;
        }
        mDouYinAdapter = new DouYinAdapter();

        for (int i = 0; i < list.size(); i++) {
            FeedList.Feed item = list.get(i);
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_video_item, null);
            PhotoView videoCover = view.findViewById(R.id.cover_img);
            String url = Constants.getFileIdToUrl(item.getCoverFileId() + "");
            Glide.with(getActivity()).load(url).apply(options).into(videoCover);
            mViews.add(view);
        }
        mVerticalViewpager.setAdapter(mDouYinAdapter);
        mDouYinAdapter.setmViews(mViews);
    }

//    @Override
    public void Refresh() {
        mDouYinAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
//        marqueeView.startFlipping();
    }

    @Override
    public void onProgress(int progress) {

    }

    @Override
    public void onFinish(File path) {

    }

    @Override
    public void onFail(String errorInfo) {

    }

    @Override
    public void onStop() {
        super.onStop();
//        marqueeView.stopFlipping();
    }


    /**
     * 分享
     */
    public void share() {
        ShareManager.ShareEntity shareEntity = new ShareManager.ShareEntity();
        shareEntity.setBackHandle(true);
        ShareManager.getInstance().showMoSharePlatform(RecommendFragment.this.getActivity(), shareEntity, new IShare() {
            @Override
            public void goHome() {

            }

            @Override
            public void goPublishPage() {

            }

            @Override
            public void onItemClick(ShareManager.ShareChooser shareChooser) {
//                recommendFragmentPresenter.downloadFeedFlie(mHanlder, mPlayingPosition, tv_share);
            }

            @Override
            public void onStart(SHARE_MEDIA share_media) {

            }

            @Override
            public void onResult(SHARE_MEDIA share_media) {

            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {

            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {

            }
        },null);

    }

//    @Override
    public void setCurrentLikeAndShare(List<FeedList.Feed> mList) {
        for (int i = 0; i < mList.size(); i++) {

            View view = mViews.get(i);
            FeedList.Feed feed = mList.get(i);

            final TextView tv_like = view.findViewById(R.id.tv_like);
            final TextView tv_share = view.findViewById(R.id.tv_share);

            TextView tv_name = view.findViewById(R.id.tv_name);
            TextView tv_content = view.findViewById(R.id.tv_content);
            TextView tv_music_title = view.findViewById(R.id.tv_music_title);
            if (feed.isLike()) {
                setTextImage(tv_like, R.mipmap.icon_feed_like_press);
            } else {
                setTextImage(tv_like, R.mipmap.icon_fees_like);
            }
            tv_like.setText((feed.getLikeNum() >= 10000) ? parseNumber(feed.getLikeNum()) + "w" : feed.getLikeNum() + "");
            tv_share.setText((feed.getShareNum() >= 10000) ? parseNumber(feed.getShareNum()) + "w" : feed.getShareNum() + "");
            tv_name.setText(feed.getAuthorName());
            tv_content.setText(feed.getDescription());
            tv_music_title.setText("安全，迅速，~~~~~~~                   高效，稳定 ！！！！~~~~~~~ ");
            tv_music_title.setSelected(true); //跑马灯效果

            setLikeAndShareClick(feed, tv_like, tv_share);
        }
    }

    private float parseNumber(int number) {
        if (number >= 10000) {
            int a = number / 10000;
            int b = number % 10000;
            float b1 = b / 10000;
            return a + b1;
        }
        return number;
    }

    private void setLikeAndShareClick(FeedList.Feed feed, TextView tv_like, TextView tv_share) {
        tv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                   checkStatus();
                if (!NetworkUtil.isNetworkConnected(getActivity())) {
                    CameraToastUtil.show("暂无网络", getActivity());
                    return;
                }
                if (!SPUtils.isLogin(getActivity())) {
//                    startAct(LoginActivty.class);
                    return;
                }
                if (feed.isLike()) {
                    return;
                }
                setTextImage(tv_like, R.mipmap.icon_feed_like_press);
                int likesunm = feed.getLikeNum() + 1;
                feed.setLikeNum(likesunm);
                feed.setLike(true);
                tv_like.setText(likesunm + "");
                recommendFragmentPresenter.clickLike(mPlayingPosition, feed, tv_like);
            }
        });

        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                   checkStatus();
                if (!NetworkUtil.isNetworkConnected(getActivity())) {
                    CameraToastUtil.show("暂无网络", getActivity());
                    return;
                }
//                if (!SPUtils.isLogin(getActivity())) {
//                    CameraToastUtil.show("请先登陆", getActivity());
//                    return;
//                }
                //下载此视频

                share();
            }
        });
    }


    public void setCurrentLikeAndShare() {
        View view = mViews.get(mPlayingPosition);

        TextView tv_like = view.findViewById(R.id.tv_like);
        TextView tv_share = view.findViewById(R.id.tv_share);

        tv_like.setText(feeds.get(mPlayingPosition).getLikeNum() + "");
        tv_share.setText(feeds.get(mPlayingPosition).getShareNum() + "");
    }


//    @Override
//    public RingProgressBar getProgressBar() {
//        return progress;
//    }
//
//    @Override
//    public RelativeLayout getRelativeLayout() {
//        return rlProgress;
//    }


    public void setTextImage(TextView textImage, int imagid) {
        Drawable drawable = getResources().getDrawable(imagid);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        textImage.setCompoundDrawables(null, drawable, null, null);
    }

    public void checkStatus() {
        if (!NetworkUtil.isNetworkConnected(getActivity())) {
            CameraToastUtil.show("暂无网络", getActivity());
            return;
        }
        if (!SPUtils.isLogin(getActivity())) {
            CameraToastUtil.show("请先登陆", getActivity());
            return;
        }
    }

    private Handler mHanlder = new Handler();

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showNetWorkErrorView() {

    }

    @Override
    public void showNoDataView() {

    }
}
