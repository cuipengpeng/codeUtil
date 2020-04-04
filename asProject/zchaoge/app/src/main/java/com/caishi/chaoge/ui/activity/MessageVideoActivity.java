package com.caishi.chaoge.ui.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.caishi.chaoge.R;
import com.caishi.chaoge.base.BaseRequestInterface;
import com.caishi.chaoge.base.BaseUILocalDataActivity;
import com.caishi.chaoge.bean.HomeDataBean;
import com.caishi.chaoge.http.HttpRequest;
import com.caishi.chaoge.http.RequestURL;
import com.caishi.chaoge.request.FindMomentRequest;
import com.caishi.chaoge.ui.adapter.VideoRecycleViewAdapter;
import com.caishi.chaoge.ui.dialog.HomeDialog;
import com.caishi.chaoge.utils.SPUtils;
import com.xiao.nicevideoplayer.NiceVideoPlayer;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class MessageVideoActivity extends BaseUILocalDataActivity {
    @BindView(R.id.rv_video_list)
    public RecyclerView videoRecyclerView;

    private VideoRecycleViewAdapter videoRecycleViewAdapter;
    private static ArrayList<HomeDataBean> homeDataList = new ArrayList<>();
    private PagerSnapHelper mPagerSnapHelper = new PagerSnapHelper();
    private String momentId;
    private int pageFlag;

    @OnClick({R.id.ll_baseTitle_back})
    public void onClickView(View v) {
        switch (v.getId()) {
            case R.id.ll_baseTitle_back:
                NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
                finish();
                break;
        }
    }

    @Override
    protected String getPageTitle() {
        return null;
    }

    @Override
    public int getSubLayoutId() {
        return R.layout.activity_message_video;
    }

    @Override
    public void initPageData() {
        Intent intent = getIntent();
        momentId = intent.getStringExtra("momentId");
        pageFlag = intent.getIntExtra("pageFlag",-1);
        showBaseUITitle = false;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        videoRecyclerView.setLayoutManager(linearLayoutManager);
        videoRecycleViewAdapter = new VideoRecycleViewAdapter(mContext);
        videoRecyclerView.setAdapter(videoRecycleViewAdapter);
        videoRecycleViewAdapter.updateData(true, homeDataList);
        mPagerSnapHelper.attachToRecyclerView(videoRecyclerView);
        getDataList();
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

    }


    private void playVideo(final int position) {
        View itemView = videoRecyclerView.getLayoutManager().findViewByPosition(position);
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
                    if (homeDataList != null && homeDataList.size() > 0) {
                        Map<String, String> paramsMap = new HashMap<String, String>();

                        String userId;
                        if (SPUtils.isLogin(mContext)) {
                            userId = SPUtils.readCurrentLoginUserInfo(mContext).userId;
                        } else {
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

    public void getDataList() {

        FindMomentRequest.newInstance(this).getMessageNum(momentId, new BaseRequestInterface<HomeDataBean>() {
            @Override
            public void success(int state, String msg, HomeDataBean homeDataBean) {
                homeDataList.clear();
                homeDataList.add(homeDataBean);
                videoRecycleViewAdapter.updateData(true, homeDataList);
                if (pageFlag == 2)
                    HomeDialog.newInstance().showCommentDialog(mContext, videoRecycleViewAdapter, homeDataBean.momentId,
                            homeDataBean.commentCount, null);

            }

            @Override
            public void error(int state, String msg) {

            }
        });


    }


    private void pauseVideo() {
        if (videoRecyclerView != null) {
            View itemView = videoRecyclerView.getChildAt(0);
            if (itemView != null) {
                NiceVideoPlayer videoView = itemView.findViewById(R.id.video_itemHome_play);
                ImageView iv_itemVideo_pause = itemView.findViewById(R.id.iv_itemVideo_pause);
                iv_itemVideo_pause.setVisibility(View.VISIBLE);
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

}
