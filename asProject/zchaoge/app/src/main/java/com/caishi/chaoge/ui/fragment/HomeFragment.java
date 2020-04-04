package com.caishi.chaoge.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.caishi.chaoge.R;
import com.caishi.chaoge.base.BaseUILocalDataFragment;
import com.caishi.chaoge.bean.HomeDataBean;
import com.caishi.chaoge.http.HttpRequest;
import com.caishi.chaoge.http.RequestURL;
import com.caishi.chaoge.ui.activity.VideoActivity;
import com.caishi.chaoge.ui.adapter.HomeRecycleViewAdapter;
import com.caishi.chaoge.utils.DisplayMetricsUtil;
import com.caishi.chaoge.utils.LogUtil;
import com.caishi.chaoge.utils.SPUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.barlibrary.ImmersionBar;
import com.rd.lib.utils.FileUtils;
import com.xiao.nicevideoplayer.NiceVideoPlayer;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeFragment extends Fragment {

    @BindView(R.id.rv_home_dataList)
    RecyclerView rv_home_dataList;
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.ll_homeFragment_netwrokErrorView)
    LinearLayout networkErrorLinearLayout;

    public ArrayList<HomeDataBean> homeDataList = new ArrayList<>();
    private HomeRecycleViewAdapter homeRecycleViewAdapter;
    private PagerSnapHelper mPagerSnapHelper = new PagerSnapHelper();
    final String UP = "UP";
    final String DOWN = "DOWN";
    int pageSize = 10;
    private int currentPlayPosition = 0;
    private boolean mHidden;


    @OnClick({R.id.btn_networkError_refresh})
    public void OnClickView(View v) {
        switch (v.getId()) {
            case R.id.btn_networkError_refresh:
                getHomeData(true, -1, DOWN);
                break;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View subScreenView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home, null);
        ButterKnife.bind(this, subScreenView);
        return  subScreenView;
    }

        @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        rv_home_dataList.setLayoutManager(linearLayoutManager);
        homeRecycleViewAdapter = new HomeRecycleViewAdapter(getActivity());
        rv_home_dataList.setAdapter(homeRecycleViewAdapter);
        mPagerSnapHelper.attachToRecyclerView(rv_home_dataList);

        rv_home_dataList.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                int position = rv_home_dataList.getLayoutManager().getPosition(view);
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
        rv_home_dataList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    LinearLayoutManager mLineManager = (LinearLayoutManager) rv_home_dataList.getLayoutManager();
                    int scrollPosition = VideoActivity.getCurrentViewIndex(getActivity(), mLineManager);
                    if(scrollPosition!= currentPlayPosition){
                        currentPlayPosition = scrollPosition;
                        playVideo(currentPlayPosition);
                    }

                    int screenHeight = DisplayMetricsUtil.getScreenHeight(getActivity());
                    boolean bottom = recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange()-screenHeight;
                    if(bottom && homeRecycleViewAdapter.homeDataList.size()>0){
                        long targetTime = homeRecycleViewAdapter.homeDataList.get(homeRecycleViewAdapter.homeDataList.size() - 1).targetTime;
                        getHomeData(false, targetTime, "UP");
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        homeRecycleViewAdapter.setOnItemClickListener(new HomeRecycleViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(HomeRecycleViewAdapter.ViewHolder viewHolder, int position) {
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
                getHomeData(true, -1, DOWN);
            }
        });

        getHomeData(true, -1, DOWN);
    }

    private void playVideo(final int position) {
        View  itemView = rv_home_dataList.getLayoutManager().findViewByPosition(position);
        if (itemView != null) {
            NiceVideoPlayer videoView = itemView.findViewById(R.id.video_itemHome_play);
//            videoView.setLooping(true);
            ImageView iv_itemHome_pause = itemView.findViewById(R.id.iv_itemHome_pause);
            iv_itemHome_pause.setVisibility(View.GONE);

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
                        if (SPUtils.isLogin(getActivity())){
                            userId = SPUtils.readCurrentLoginUserInfo(getActivity()).userId;
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
                    if (!homeRecycleViewAdapter.showCommentDialog) {
                        rv_home_dataList.smoothScrollToPosition(position + 1);
                    }
                }
            });
        }
    }

    public void pauseVideo() {
        if (rv_home_dataList != null) {
            View itemView = rv_home_dataList.getChildAt(0);
            if (itemView != null) {
                final NiceVideoPlayer videoView = itemView.findViewById(R.id.video_itemHome_play);
                final ImageView iv_itemHome_pause = itemView.findViewById(R.id.iv_itemHome_pause);
                iv_itemHome_pause.setVisibility(View.VISIBLE);
                LogUtil.i("videoView. videoView.isPreparing()====" + (videoView.isPreparing()));
                LogUtil.i("videoView. videoView.isPrepared()====" + (videoView.isPrepared()));
                if (videoView.isPreparing() || videoView.isPrepared())
                    videoView.release();
                if (videoView.isPlaying()) {
                    videoView.pause();
                }
            }
        }
    }

    private void resumeVideo() {
        if (rv_home_dataList != null) {
            View itemView = rv_home_dataList.getChildAt(0);
            if (itemView != null) {
                final NiceVideoPlayer videoView = itemView.findViewById(R.id.video_itemHome_play);
                final ImageView iv_itemHome_pause = itemView.findViewById(R.id.iv_itemHome_pause);
                iv_itemHome_pause.setVisibility(View.GONE);
                if (videoView.isIdle()) {
                    videoView.start(false);
                }else {
                    videoView.restart();
                }
            }
        }
    }

    public void getHomeData(final boolean refresh, long since, String state) {
        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("pageSize", pageSize + "");
        paramsMap.put("since", since + "");
        paramsMap.put("slipType", state);

        HttpRequest.post(true, HttpRequest.APP_INTERFACE_WEB_URL + RequestURL.HOME_DATA, paramsMap, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onSuccess(String response) {
                refreshLayout.setVisibility(View.VISIBLE);
                networkErrorLinearLayout.setVisibility(View.GONE);
                if (refreshLayout.isRefreshing()) {
                    refreshLayout.setRefreshing(false);//取消刷新
                }

                Gson gson = new Gson();
                List<HomeDataBean> homeDataBeanList = gson.fromJson(response, new TypeToken<List<HomeDataBean>>() {}.getType());
                if (homeDataBeanList.size() > 0) {
                    if(refresh){
                        homeRecycleViewAdapter.updateData(refresh, homeDataBeanList);
                    }else {
                        //首页流数据因弱网时重复加载数据的问题
                        if(homeRecycleViewAdapter.homeDataList.get(homeRecycleViewAdapter.homeDataList.size()-1).targetTime >= homeDataBeanList.get(0).targetTime){
                            homeRecycleViewAdapter.updateData(refresh, homeDataBeanList);
                        }
                    }
                }else {
                    Toast.makeText(getActivity(), "没有内容了", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String t) {
                networkErrorLinearLayout.setVisibility(View.VISIBLE);
                refreshLayout.setVisibility(View.GONE);
                pauseVideo();
                if (refreshLayout.isRefreshing()) {
                    refreshLayout.setRefreshing(false);//取消刷新
                }
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.mHidden = hidden;
        if(hidden){
            pauseVideo();
        }else {
            resumeVideo();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!mHidden){
            playVideo(currentPlayPosition);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseVideo();
    }

    @Override
    public void onStop() {
        super.onStop();
        pauseVideo();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
    }
}