package com.test.xcamera.phonealbum;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.framwork.base.view.MOBaseActivity;
import com.test.xcamera.widget.DouYinController;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.player.PlayerConfig;
import com.test.xcamera.R;

import butterknife.BindView;
import butterknife.OnClick;

public class VideoPlayActivity extends MOBaseActivity {
    @BindView(R.id.cover_img)
    ImageView coverImg;
    @BindView(R.id.container)
    FrameLayout container;
    private String video;

    private IjkVideoView mIjkVideoView;
    public DouYinController mDouYinController;

    @Override
    public int initView() {
        return R.layout.view_video_item;
    }

    @Override
    public void initClick() {

    }

    @Override
    public void initData() {
        video = getIntent().getStringExtra("url");

        mIjkVideoView = new IjkVideoView(mContext);
        PlayerConfig config = new PlayerConfig.Builder().setLooping().build();
        mIjkVideoView.setPlayerConfig(config);
        mDouYinController = new DouYinController(mContext);
        mIjkVideoView.setVideoController(mDouYinController);

        startPlay();
    }



    @OnClick({R.id.cover_img, R.id.container})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cover_img:
                break;
            case R.id.container:
                break;
        }
    }

    private void startPlay() {


//        FrameLayout frameLayout = view.findViewById(R.id.container);
//        mCover = view.findViewById(R.id.cover_img);

        mDouYinController.setSelect(false);

        if (coverImg != null && coverImg.getDrawable() != null) {
            mDouYinController.getThumb().setImageDrawable(coverImg.getDrawable());
        }

        ViewGroup parent = (ViewGroup) mIjkVideoView.getParent();

        if (parent != null) {
            parent.removeAllViews();
        }

        container.addView(mIjkVideoView);
        mIjkVideoView.setUrl(video);
        mIjkVideoView.setScreenScale(IjkVideoView.SCREEN_SCALE_DEFAULT);
        mIjkVideoView.start();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mIjkVideoView!=null){
            mIjkVideoView.release();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if(mIjkVideoView.isPlaying()&&mDouYinController.getIvPlay().getVisibility()==View.GONE)
            stopVideo();
    }

    public  void   stopVideo(){
        mIjkVideoView.pause();
        if (mDouYinController != null) {
            mDouYinController.getIvPlay().setVisibility(View.VISIBLE);
        }
    }



}
