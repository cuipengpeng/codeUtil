package com.caishi.chaoge.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.caishi.chaoge.R;
import com.caishi.chaoge.base.BaseUILocalDataActivity;
import com.caishi.chaoge.bean.HomeDataBean;
import com.caishi.chaoge.http.HttpRequest;
import com.caishi.chaoge.http.RequestURL;
import com.caishi.chaoge.ui.adapter.VideoRecycleViewAdapter;
import com.caishi.chaoge.utils.DisplayMetricsUtil;
import com.caishi.chaoge.utils.LogUtil;
import com.caishi.chaoge.utils.SPUtils;
import com.caishi.chaoge.utils.StatusBarUtil;
import com.caishi.chaoge.utils.VideoScreenUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.util.DensityUtil;
import com.xiao.nicevideoplayer.NiceVideoPlayer;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class VideoActivity extends BaseUILocalDataActivity{
    @BindView(R.id.rv_video_list)
    public RecyclerView videoRecyclerView;
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;

    final String UP = "UP";
    final String DOWN = "DOWN";
    private VideoRecycleViewAdapter videoRecycleViewAdapter;
    private String userID;
    private int pageFalg;//用来区分请求喜欢还是作品
    private final int pageSize = 10;
    private int mCurrentPlayPosition;
    private static ArrayList<HomeDataBean> homeDataList = new ArrayList<>();
    private PagerSnapHelper mPagerSnapHelper = new PagerSnapHelper();
    private boolean firstInit = true;//暂停状态，压后台后返回，避免再次执行
    public static final String KEY_OF_PLAY_POSITION = "playPositionKey";
    public static final String KEY_OF_USER_ID = "userIDKey";
    public static final String KEY_OF_COME_FROM = "comeFromKey";
    public static final int FROM_LIKE_FRAGMENT = 301;
    public static final int FROM_PRODUCT_FRAGMENT = 302;
    private NiceVideoPlayer mNiceVideoPlayer;

    @OnClick({R.id.ll_baseTitle_back})
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.ll_baseTitle_back:
                onBackPressed();
                break;
        }
    }

    @Override
    protected String getPageTitle() {
        return null;
    }

    @Override
    public int getSubLayoutId() {
        return R.layout.activity_video;
    }

    @Override
    public void initPageData() {
        showBaseUITitle = false;
//        Bundle bundle = getIntent().getExtras();
        Intent bundle = getIntent();
        mCurrentPlayPosition = bundle.getIntExtra(KEY_OF_PLAY_POSITION,-1);
        userID = bundle.getStringExtra(KEY_OF_USER_ID);
        pageFalg = bundle.getIntExtra(KEY_OF_COME_FROM, -1);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        videoRecyclerView.setLayoutManager(linearLayoutManager);
        videoRecycleViewAdapter = new VideoRecycleViewAdapter(mContext);
        videoRecyclerView.setAdapter(videoRecycleViewAdapter);
        videoRecycleViewAdapter.updateData(true, homeDataList);
        mPagerSnapHelper.attachToRecyclerView(videoRecyclerView);

        videoRecyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                int position = videoRecyclerView.getLayoutManager().getPosition(view);
                if (position == 0) {
                    playVideo(0);
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                NiceVideoPlayer niceVideoPlayer = view.findViewById(R.id.video_itemHome_play);
                if (niceVideoPlayer != null) {
                    niceVideoPlayer.release();
                }
            }
        });

        videoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    LinearLayoutManager mLineManager = (LinearLayoutManager) VideoActivity.this.videoRecyclerView.getLayoutManager();
                    int scrollPosition = getCurrentViewIndex(VideoActivity.this, mLineManager);
                    if(scrollPosition!= mCurrentPlayPosition){
                        mCurrentPlayPosition = scrollPosition;
                        playVideo(mCurrentPlayPosition);
                    }

                    int screenHeight = DisplayMetricsUtil.getScreenHeight(VideoActivity.this);
                    boolean bottom = recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange()-screenHeight/2;
                    if(bottom){
                        long targetTime = videoRecycleViewAdapter.homeDataList.get(videoRecycleViewAdapter.homeDataList.size() - 1).targetTime;
                        getDataList(false, targetTime, "UP");
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        videoRecycleViewAdapter.setOnItemClickListener(new VideoRecycleViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(VideoRecycleViewAdapter.ViewHolder viewHolder, int position) {
                if (!viewHolder.video_itemHome_play.isPlaying()) {
                    resumeVideo();
                } else {
                    pauseVideo();
                }
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDataList(true,-1, DOWN);
            }
        });

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
            int middleContentHeight = VideoScreenUtil.getScreenHeight(context) - StatusBarUtil.getStatusBarHeight(context)
                    - DensityUtil.dp2px(49);
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
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus){
            if(firstInit){
                int itemHeight = DisplayMetricsUtil.getScreenHeight(this);
                videoRecyclerView.scrollBy(0, itemHeight*mCurrentPlayPosition);
                firstInit = false;//暂停状态，压后台后返回，避免再次执行
            }
            playVideo(mCurrentPlayPosition);
        }
    }

    private void playVideo(final int position) {
        View  itemView = videoRecyclerView.getLayoutManager().findViewByPosition(position);
        if (itemView != null) {
            NiceVideoPlayer videoView = itemView.findViewById(R.id.video_itemHome_play);
            videoView.setLooping(true);
            ImageView iv_itemVideo_pause = itemView.findViewById(R.id.iv_itemVideo_pause);
            iv_itemVideo_pause.setVisibility(View.GONE);

            if (videoView.isPlaying()) {
                NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
            }
            if (videoView.isIdle()) {
                videoView.start(false);
            } else if (videoView.isPaused() || videoView.isBufferingPaused()) {
                videoView.restart();
            }

            videoView.setOnPlayCompletionListener(new NiceVideoPlayer.OnPlayCompletionListener() {
                @Override
                public void onPlayCompletion(int playCount) {
                    if (homeDataList != null && homeDataList.size() > 0){
                        Map<String, String> paramsMap = new HashMap<String, String>();

                        String userId ;
                        if (SPUtils.isLogin(mContext)){
                            userId = SPUtils.readCurrentLoginUserInfo(mContext).userId;
                        }else {
                            //TODO 传入设备号登陆的userId
                            userId = "123456";
                        }
                        paramsMap.put("userId", userId);
                        paramsMap.put("momentId", homeDataList.get(position).getMomentId());
                        paramsMap.put("desUserId", homeDataList.get(position).getUserId());
                        paramsMap.put("likeStatus", "3");
                        HttpRequest.post(false, HttpRequest.APP_INTERFACE_WEB_URL + RequestURL.FORWARD, paramsMap, new HttpRequest.HttpResponseCallBank() {
                            @Override
                            public void onSuccess(String response) {
                            }

                            @Override
                            public void onFailure(String t) {
                            }
                        });
                    }
                }
            });
        }
    }

    public void getDataList(final boolean refresh, long since, String slipType) {
        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("pageSize", pageSize+"");
        paramsMap.put("since", since+"");
        paramsMap.put("slipType", slipType);
        paramsMap.put("userId", userID);

        String url = HttpRequest.APP_INTERFACE_WEB_URL;
        if (pageFalg == FROM_PRODUCT_FRAGMENT){
            url += RequestURL.PERSONAL;
        }
        if (pageFalg == FROM_LIKE_FRAGMENT){
            url += RequestURL.LIKES;
        }
        HttpRequest.post(true, url, paramsMap, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onSuccess(String response) {
                refreshComplete();

                Gson gson = new Gson();
                List<HomeDataBean> homeDataBeanList = gson.fromJson(response, new TypeToken<List<HomeDataBean>>(){}.getType());
                if (homeDataBeanList.size() > 0) {
                    ArrayList<HomeDataBean> arrayList = new ArrayList<>();
                    for (int i = 0; i < homeDataBeanList.size(); i++) {
                        if (homeDataBeanList.get(i).getVideoUrl() != null){
                            arrayList.add(homeDataBeanList.get(i));
                        }
                    }
                    videoRecycleViewAdapter.updateData(refresh, arrayList);
                }else {
                    Toast.makeText(VideoActivity.this, "没有内容了", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String t) {
                refreshComplete();
            }
        });
    }


    private void refreshComplete() {
        if (refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);//取消刷新
        }
    }

    private void pauseVideo() {
        if (videoRecyclerView != null) {
            View itemView = videoRecyclerView.getChildAt(0);
            if (itemView != null) {
               NiceVideoPlayer videoView = itemView.findViewById(R.id.video_itemHome_play);
               ImageView iv_itemVideo_pause = itemView.findViewById(R.id.iv_itemVideo_pause);
                iv_itemVideo_pause.setVisibility(View.VISIBLE);
                LogUtil.i("videoView.isPlaying()====" + (videoView.isPlaying()));
                if (videoView.isPlaying()) {
                    videoView.pause();
                }
            }
        }
    }

    private void resumeVideo() {
        if (videoRecyclerView != null) {
            View itemView = videoRecyclerView.getChildAt(0);
            if (itemView != null) {
               NiceVideoPlayer videoView = itemView.findViewById(R.id.video_itemHome_play);
               ImageView iv_itemVideo_pause = itemView.findViewById(R.id.iv_itemVideo_pause);
                iv_itemVideo_pause.setVisibility(View.GONE);
                videoView.restart();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
                finish();
                return false;//拦截事件
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onResume() {
        super.onResume();
        resumeVideo();
    }

    @Override
    protected void onPause() {
        super.onPause();
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (homeDataList != null) {
            homeDataList.clear();
        }
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
    }

    public static void open(Context context, int type, int playPosition, String userID, List<HomeDataBean> homeList){
//    public static void open(Context context, int type, int playPosition, String userID,){
        Intent intent = new Intent(context, VideoActivity.class);
        homeDataList.clear();
        homeDataList.addAll(homeList);
        intent.putExtra(KEY_OF_COME_FROM, type);
        intent.putExtra(KEY_OF_PLAY_POSITION, playPosition);
        intent.putExtra(KEY_OF_USER_ID, userID);
        context.startActivity(intent);
    }
}
