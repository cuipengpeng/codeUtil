package com.test.xcamera.phonealbum;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.test.xcamera.phonealbum.widget.MusicSearchView;
import com.test.xcamera.util.MediaPlayerUtil;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.KeyboardUtils;
import com.test.xcamera.utils.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SelectSearchMusicActivity extends MOBaseActivity implements SelectMusicContract.View{
    public static final String FILE_PROTOCAL = "file://";

    @BindView(R.id.left_tv_title)
    TextView leftTvTitle;
    @BindView(R.id.left_iv_title)
    ImageView leftIvTitle;
    @BindView(R.id.tv_middle_title)
    TextView tvMiddleTitle;
    @BindView(R.id.tv_selectMusicActivity_noMusic)
    TextView noMusicTextView;
    @BindView(R.id.root_layout)
    RelativeLayout root_layout;
    @BindView(R.id.rv_selectMusicActivity_musicList)
    RecyclerView musicListRecyclerView;
    @BindView(R.id.musicSearchView)
    MusicSearchView musicSearchView;
    @BindView(R.id.mFrameMedia)
    FrameLayout mFrameMedia;
    private final int MEDIA_DATA_LOADING = 0;
    private final int MEDIA_DATA_EMPTY = 1;
    private MediaDataLoading mMediaDataLoading;
    private MediaNotworkEmpty mMediaNotworkEmpty;
    public SelectMusicAdapter selectMusicListAdapter;
    private List<MusicBean> musicBeanList = new ArrayList<>();
    public static final String KEY_OF_MUSIC_RESULT = "selectMusicResult";
    MediaPlayerUtil mediaPlayerUtil;
    private SelectMusicPresenter mPresenter;
    private int mCurPage=1;
    private MusicResult mMusicResult;
    private boolean mIsLoading = false;
    private boolean mIsRefresh = true;
    private int mTotalCount = 0;
    private String mKeyWord="";
    private float mRotation=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_BEHIND);

    }

    @OnClick({R.id.left_iv_title,R.id.action_cancel})
    public void onClickView(View view) {
        switch (view.getId()) {
            case R.id.left_iv_title:
                finish();
                break;
                case R.id.action_cancel:
                finish();
                break;
        }
    }

    @Override
    public int initView() {
        return R.layout.activity_select_search_music_list;
    }

    @Override
    public void initData() {
        mRotation=getIntent().getFloatExtra("mRotation",0f);
//        ObjectAnimator anim = ObjectAnimator.ofFloat(root_layout, "rotation", 0f, mRotation);
//        anim.setDuration(1);
//        anim.start();
        noStatusBar();
        setStatusBarColor(getResources().getColor(R.color.c050505), 0);
        mPresenter=SelectMusicPresenter.getInstance(this,this);
        leftIvTitle.setImageResource(R.mipmap.back);
        musicListRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        selectMusicListAdapter = new SelectMusicAdapter(this,mRotation);
        musicListRecyclerView.setAdapter(selectMusicListAdapter);
        /* 滚动加载更多*/
        musicListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!mIsLoading && isVisBottom(musicListRecyclerView)){
//                        !recyclerView.canScrollVertically(1))
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
                if(selectMusicListAdapter.mData.get(position).isSelected()){
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
                        }                        selectMusicListAdapter.setSelectPosition(-1);
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
        KeyboardUtils.openSoftKeyboard(this,musicSearchView.getEt_search());
        musicSearchView.getSearchValue(new MusicSearchView.OnMusicSearchView() {
            @Override
            public void onOnMusicSearchView(String keyWord) {
                mediaPlayerUtil.pause();
                showMediaFrame(MEDIA_DATA_LOADING);
                mKeyWord=keyWord;
                mCurPage=1;
                mIsLoading = false;
                mIsRefresh = true;
                loadMusicData(mCurPage);
                KeyboardUtils.closeSoftKeyboard(SelectSearchMusicActivity.this,musicSearchView.getEt_search());

            }
        });
    }
    public static boolean isVisBottom(RecyclerView recyclerView){
        if(recyclerView==null){
            return false;
        }
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        //屏幕中最后一个可见子项的position
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        //当前屏幕所看到的子项个数
        int visibleItemCount = layoutManager.getChildCount();
        //当前RecyclerView的所有子项个数
        int totalItemCount = layoutManager.getItemCount();
        //RecyclerView的滑动状态
        int state = recyclerView.getScrollState();
        return visibleItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1;
    }

    private void loadMusicData(int page){
        mPresenter.loadSearchMusicData(page,mKeyWord);
        // 添加 tab item

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
                        loadMusicData(0);
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
        if(!mediaPlayerUtil.isPlay()){
            mediaPlayerUtil.resumePlay();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayerUtil.pause();
    }

    @Override
    protected void onDestroy() {
        mediaPlayerUtil.releaseMediaPlayer();
        overridePendingTransition(0, 0);
        super.onDestroy();
    }

    public static void openForResult(Activity activity, int requestCode,float mRotation) {
        Intent intent = new Intent(activity, SelectSearchMusicActivity.class);
        intent.putExtra("mRotation",mRotation);
        activity.startActivityForResult(intent, requestCode);
    }


    @Override
    public void showMusicTypeData(MusicTypeResult dataResult) {
    }

    @Override
    public void showMusicTypeError(String msg) {

    }

    @Override
    public void showMusicData(MusicResult dataResult) {
        if(noMusicTextView==null||musicListRecyclerView==null){
            return;
        }
        hideMediaFrame();
        mMusicResult=dataResult;
        mTotalCount=mMusicResult.getPagination().getTotal();
        selectMusicListAdapter.upateData(mIsRefresh, dataResult.getData());
        if(dataResult.getData()!=null&&dataResult.getData().size()>0){
            mIsLoading = false;
        }
        if(selectMusicListAdapter.getItemCount()==0){
            noMusicTextView.setVisibility(View.VISIBLE);
            musicListRecyclerView.setVisibility(View.GONE);
        }else {
            noMusicTextView.setVisibility(View.GONE);
            musicListRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showMusicError(String msg) {
        if(noMusicTextView==null||musicListRecyclerView==null){
            return;
        }
        showMediaFrame(MEDIA_DATA_EMPTY);
        noMusicTextView.setVisibility(View.GONE);
        musicListRecyclerView.setVisibility(View.GONE);
    }

}