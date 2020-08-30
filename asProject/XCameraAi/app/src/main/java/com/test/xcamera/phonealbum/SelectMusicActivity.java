package com.test.xcamera.phonealbum;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.editvideo.ToastUtil;
import com.framwork.base.adapter.BaseRecyclerAdapter;
import com.framwork.base.view.MOBaseActivity;
import com.test.xcamera.R;
import com.test.xcamera.phonealbum.adapter.SelectMusicAdapter;
import com.test.xcamera.phonealbum.bean.MusicBean;
import com.test.xcamera.phonealbum.bean.MusicResult;
import com.test.xcamera.phonealbum.bean.MusicTypeResult;
import com.test.xcamera.phonealbum.presenter.SelectMusicContract;
import com.test.xcamera.phonealbum.presenter.SelectMusicPresenter;
import com.test.xcamera.phonealbum.widget.MediaDataLoading;
import com.test.xcamera.phonealbum.widget.MediaNotworkEmpty;
import com.test.xcamera.util.MediaPlayerUtil;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.NetworkUtil;

import butterknife.BindView;
import butterknife.OnClick;

public class SelectMusicActivity extends MOBaseActivity implements SelectMusicContract.View {
    public static final String FILE_PROTOCAL = "file://";

    @BindView(R.id.left_tv_title)
    TextView leftTvTitle;
    @BindView(R.id.left_iv_title)
    ImageView leftIvTitle;
    @BindView(R.id.tv_middle_title)
    TextView tvMiddleTitle;
    @BindView(R.id.tv_selectMusicActivity_noMusic)
    TextView noMusicTextView;
    @BindView(R.id.tl_tab)
    TabLayout mTabLayout;
    @BindView(R.id.root_layout)
    RelativeLayout root_layout;
    @BindView(R.id.line)
    View line;
    @BindView(R.id.search_action)
    View search_action;


    @BindView(R.id.rv_selectMusicActivity_musicList)
    RecyclerView musicListRecyclerView;
    @BindView(R.id.mFrameMedia)
    FrameLayout mFrameMedia;
    private final int MEDIA_DATA_LOADING = 0;
    private final int MEDIA_DATA_EMPTY = 1;
    private MediaDataLoading mMediaDataLoading;
    private MediaNotworkEmpty mMediaNotworkEmpty;
    public SelectMusicAdapter selectMusicListAdapter;
    public static final String KEY_OF_MUSIC_RESULT = "selectMusicResult";
    MediaPlayerUtil mediaPlayerUtil;
    private SelectMusicPresenter mPresenter;
    private int mCurPage = 1;
    private MusicResult mMusicResult;
    private boolean mIsLoading = false;
    private boolean mIsRefresh = true;
    private MusicTypeResult.DataBean mMusicTypeData;
    private float mRotation=0;
    private MusicBean mSelectMusic;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @OnClick({R.id.left_iv_title,R.id.search_action})
    public void onClickView(View view) {
        switch (view.getId()) {
            case R.id.left_iv_title:
                back();
                break;
            case R.id.search_action:
                SelectSearchMusicActivity.openForResult(this, 1,mRotation);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        back();
    }
    private boolean  mDisconnectedUSBStatus=true;
    private void back(){
        Intent intent = new Intent();
        intent.putExtra("isCancelMusic", mIsCancelMusic);
        intent.putExtra("DisconnectedUSBStatus", mDisconnectedUSBStatus);
        ((FragmentActivity) mContext).setResult(MyVideoEditActivity.RESULT_OK_IN_MUSIC, intent);
        finish();
    }
    @Override
    public int initView() {
        return R.layout.activity_select_music_list;
    }

    @Override
    public void initData() {
        mRotation=getIntent().getFloatExtra("mRotation",0f);
        mSelectMusic= (MusicBean) getIntent().getSerializableExtra("musicBean");
        showMusicCancelFrame(mSelectMusic);
        if(mRotation!=0){
            search_action.setVisibility(View.GONE);
        }
        ObjectAnimator anim = ObjectAnimator.ofFloat(root_layout, "rotation", 0f, mRotation);
        anim.setDuration(1);
        anim.start();
        noStatusBar();
        setStatusBarColor(getResources().getColor(R.color.c050505), 0);
        mPresenter = SelectMusicPresenter.getInstance(this, this);
        leftIvTitle.setImageResource(R.mipmap.back);
        musicListRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        selectMusicListAdapter = new SelectMusicAdapter(this,mRotation);
        musicListRecyclerView.setAdapter(selectMusicListAdapter);
        /* 滚动加载更多*/
        musicListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!mIsLoading &&
                        !recyclerView.canScrollVertically(1) ) {
                    mCurPage++;
                    mIsLoading = true;
                    mIsRefresh = false;
                    loadMusicData(mCurPage);
                }
            }
        });
        selectMusicListAdapter.setOnRecyclerItemClickListener(new BaseRecyclerAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (selectMusicListAdapter.mData.get(position).isSelected()) {
                    selectMusicListAdapter.mData.get(position).setSelected(false);
                    mediaPlayerUtil.pause();
                    selectMusicListAdapter.notifyDataSetChanged();
                } else {
                    if(NetworkUtil.isNetworkConnected(mContext)||selectMusicListAdapter.mData.get(position).ismIsLocal()){
                        mediaPlayerUtil.start(selectMusicListAdapter.mData.get(position).getPath(), true);
                        selectMusicListAdapter.setSelectPosition(position);
                    }else {
                        mediaPlayerUtil.pause();
                        MediaPlayerUtil.mIsLoading = false;
                        if(mRotation==0){
                            ToastUtil.showToast(mContext, mContext.getResources().getString(R.string.network_error_reset));
                        }else {
                            CameraToastUtil.show180(mContext.getResources().getString(R.string.network_error_reset),mContext);
                        }
                        selectMusicListAdapter.setSelectPosition(-1);
                    }
                }
            }
        });

        mediaPlayerUtil = new MediaPlayerUtil(this);
        mediaPlayerUtil.setOnPreparedCallBack(new MediaPlayerUtil.OnPreparedCallBack() {
            @Override
            public void onPrepared() {
                MediaPlayerUtil.mIsLoading=false;
                selectMusicListAdapter.setNotifyItemChanged();
            }

            @Override
            public void onError() {
                selectMusicListAdapter.setNotifyItemPlayError();
            }
        });
        mPresenter.loadMusicTypeData();
        showMediaFrame(MEDIA_DATA_LOADING);
    }

    @Override
    public void disconnectedUSB() {
        super.disconnectedUSB();
        mDisconnectedUSBStatus=false;
        if(selectMusicListAdapter!=null){
            selectMusicListAdapter.setDisconnectedUSBStatus(mDisconnectedUSBStatus);
        }
    }


    private void loadMusicData(int page) {
        if(mMusicTypeData==null){
            showMediaFrame(MEDIA_DATA_EMPTY);
            return;
        }
        mPresenter.loadMusicData(page, mMusicTypeData.getId());
        // 添加 tab item

    }
    private boolean mIsCancelMusic=false;
    private void showMusicCancelFrame(MusicBean musicBean) {
        if(musicBean==null){
            return;
        }
        View view=findViewById(R.id.rel_music_cancel);
        view.setVisibility(View.VISIBLE);
        TextView cancel=findViewById(R.id.tv_music_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setVisibility(View.GONE);
                mIsCancelMusic=true;
            }
        });
        TextView cancelName=findViewById(R.id.tv_music_cancel_name);
        cancelName.setText(getString(R.string.video_edit_music_use)+musicBean.getName());

    }
    private void showMediaFrame(int status) {
        if (mFrameMedia == null) {
            return;
        }
        mFrameMedia.removeAllViews();
        mFrameMedia.setVisibility(View.VISIBLE);
        if (status == MEDIA_DATA_LOADING) {
            if (mMediaDataLoading == null) {
                mMediaDataLoading = new MediaDataLoading(this);
            }
            mFrameMedia.addView(mMediaDataLoading);

        } else if (status == MEDIA_DATA_EMPTY) {
            if (mMediaNotworkEmpty == null) {
                mMediaNotworkEmpty = new MediaNotworkEmpty(this);
                mMediaNotworkEmpty.setOnRestClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showMediaFrame(MEDIA_DATA_LOADING);
                        mPresenter.loadMusicTypeData();
                    }
                });
            }
            mFrameMedia.addView(mMediaNotworkEmpty);
        }
    }

    private void hideMediaFrame() {
        if (mFrameMedia == null) {
            return;
        }
        mFrameMedia.removeAllViews();
        mFrameMedia.setVisibility(View.GONE);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (mMusicIsPlay) {
            mediaPlayerUtil.resumePlay();
        }
    }
    boolean mMusicIsPlay=false;
    @Override
    protected void onPause() {
        super.onPause();
        mMusicIsPlay=mediaPlayerUtil.isPlay();
        mediaPlayerUtil.pause();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==MyVideoEditActivity.RESULT_OK_IN_MUSIC){
            setResult(MyVideoEditActivity.RESULT_OK_IN_MUSIC, data);
            finish();
        }

    }



    @Override
    protected void onDestroy() {
        mediaPlayerUtil.releaseMediaPlayer();
        super.onDestroy();
    }

    public static void openForResult(Activity activity, int requestCode) {
         openForResult(activity,requestCode,0,null);
    }
    public static void openForResult(Activity activity, int requestCode,float mRotation,MusicBean musicBean) {
        Intent intent = new Intent(activity, SelectMusicActivity.class);
        intent.putExtra("mRotation",mRotation);
        intent.putExtra("musicBean",musicBean);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void showMusicTypeData(MusicTypeResult dataResult) {
        if (noMusicTextView == null) {
            return;
        }
        if (dataResult == null || mTabLayout == null) {
            noMusicTextView.setVisibility(View.VISIBLE);
            musicListRecyclerView.setVisibility(View.GONE);
            line.setVisibility(View.INVISIBLE);
            return;
        }
        for (MusicTypeResult.DataBean bean : dataResult.getData()) {
            if (mTabLayout.newTab() == null) {
                return;
            }
            TabLayout.Tab tab = mTabLayout.newTab().setText(bean.getName());
            tab.setTag(bean.getId());
            mTabLayout.addTab(tab);
        }
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mMusicTypeData = new MusicTypeResult.DataBean();
                mMusicTypeData.setId((int) tab.getTag());
                mCurPage = 1;
                mIsRefresh = true;
                loadMusicData(mCurPage);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if (dataResult.getData().size() > 0) {
            mMusicTypeData = dataResult.getData().get(0);
            loadMusicData(mCurPage);
        }

    }

    @Override
    public void showMusicTypeError(String msg) {

    }

    @Override
    public void showMusicData(MusicResult dataResult) {
        if (noMusicTextView == null || musicListRecyclerView == null) {
            return;
        }
        hideMediaFrame();
        mMusicResult = dataResult;
        selectMusicListAdapter.upateData(mIsRefresh, dataResult.getData());
        if(dataResult.getData()!=null&&dataResult.getData().size()>0){
            mIsLoading = false;
        }
        if (selectMusicListAdapter.getItemCount() == 0) {
            noMusicTextView.setVisibility(View.VISIBLE);
            musicListRecyclerView.setVisibility(View.GONE);
            line.setVisibility(View.INVISIBLE);
        } else {
            noMusicTextView.setVisibility(View.GONE);
            musicListRecyclerView.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showMusicError(String msg) {
        if (noMusicTextView == null || musicListRecyclerView == null) {
            return;
        }
        showMediaFrame(MEDIA_DATA_EMPTY);
        noMusicTextView.setVisibility(View.GONE);
        musicListRecyclerView.setVisibility(View.GONE);
        line.setVisibility(View.GONE);
    }
}