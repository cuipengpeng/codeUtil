package com.test.xcamera.phonealbum;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.editvideo.Constants;
import com.editvideo.MediaData;
import com.editvideo.TimelineUtil;
import com.editvideo.ToastUtil;
import com.editvideo.VideoClipFxInfo;
import com.editvideo.dataInfo.ClipInfo;
import com.editvideo.dataInfo.CompoundCaptionInfo;
import com.editvideo.dataInfo.TimelineData;
import com.editvideo.dataInfo.TransitionInfo;
import com.framwork.base.adapter.BaseRecyclerAdapter;
import com.framwork.base.view.MOBaseActivity;
import com.test.xcamera.R;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.cameraclip.CompleteVideoActivity;
import com.test.xcamera.home.HomeActivity;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.phonealbum.adapter.MusicAdapter;
import com.test.xcamera.phonealbum.adapter.VideoEditAdapter;
import com.test.xcamera.phonealbum.bean.BackupData;
import com.test.xcamera.phonealbum.adapter.VideoFilterAdapter;
import com.test.xcamera.phonealbum.bean.MusicBean;
import com.test.xcamera.phonealbum.bean.VideoParamData;
import com.test.xcamera.phonealbum.editInterface.EditCallBack;
import com.test.xcamera.phonealbum.layout.VideoTitleComCaptionLayout;
import com.test.xcamera.phonealbum.player.VideoPlayLayout;
import com.test.xcamera.phonealbum.presenter.VideoEditContract;
import com.test.xcamera.phonealbum.presenter.VideoEditPresenter;
import com.test.xcamera.phonealbum.usecase.MediaClipDataTran;
import com.test.xcamera.phonealbum.usecase.VideoCompoundCaption;
import com.test.xcamera.phonealbum.usecase.VideoImportCheck;
import com.test.xcamera.phonealbum.usecase.VideoTransitionData;
import com.test.xcamera.phonealbum.widget.BackDialog;
import com.test.xcamera.phonealbum.widget.RecyclerChangCallBack;
import com.test.xcamera.phonealbum.widget.VideoEditManger;
import com.test.xcamera.phonealbum.widget.drag.ItemVideoTouchHelperCallback;
import com.test.xcamera.phonealbum.widget.drag.OnStartDragListener;
import com.test.xcamera.phonealbum.widget.subtitle.bean.MovieSubtitleTimeline;
import com.test.xcamera.statistic.StatisticMaterialEdit;
import com.test.xcamera.statistic.StatisticShare;
import com.test.xcamera.util.MusicUtils;
import com.test.xcamera.utils.CenterLayoutManager;
import com.test.xcamera.utils.DensityUtils;
import com.test.xcamera.utils.ItemTouchHelperCallBack;
import com.test.xcamera.utils.MediaScannerUtil;
import com.test.xcamera.utils.PUtil;
import com.test.xcamera.utils.glide.VideoFrameThumb;
import com.test.xcamera.utils.proxy.Perform;
import com.test.xcamera.utils.proxy.click.NonDuplicateFactory;
import com.test.xcamera.view.basedialog.dialog.CommonDownloadDialog;
import com.test.xcamera.widget.ActivityContainer;
import com.meicam.sdk.NvsAudioTrack;
import com.meicam.sdk.NvsLiveWindow;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineCompoundCaption;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoTrack;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.editvideo.Constants.EDIT_MODE_COMPOUND_CAPTION;
import static com.editvideo.Constants.VIDEOVOLUME_MAXSEEKBAR_VALUE;
import static com.editvideo.Constants.VIDEOVOLUME_MAXVOLUMEVALUE;
import static com.editvideo.Constants.VIDEOVOLUME_MUSIC_MAXVOLUMEVALUE;
import static com.test.xcamera.phonealbum.VideoCompoundCaptionStyleActivity.select_caption_index;
import static com.test.xcamera.phonealbum.VideoCompoundCaptionStyleActivity.select_caption_text;

public class MyVideoEditActivity extends MOBaseActivity implements VideoEditContract.View, OnStartDragListener {
    @BindView(R.id.tv_middle_title)
    TextView tvMiddleTitle;
    @BindView(R.id.right_tv_titlee)
    TextView rightTvTitlee;
    NvsLiveWindow mLiveWindow;
    @BindView(R.id.player_layout)
    FrameLayout mPlayerLayout;
    @BindView(R.id.rv_myVideoEditActivity_videoClip)
    RecyclerView dragRecycleView;
    @BindView(R.id.iv_myVideoEditActivity_pause)
    ImageView pauseImageView;
    @BindView(R.id.tv_myVideoEditActivity_currentPlayPosition)
    TextView currentPlayPositionTextView;
    @BindView(R.id.sb_myVideoEditActivity_playSeekBar)
    SeekBar mPlaySeekBar;
    @BindView(R.id.tv_myVideoEditActivity_duration)
    TextView durationTextView;
    @BindView(R.id.sb_myVideoEditActivity_videoVolumnSeekBar)
    SeekBar videoVolumnSeekBar;
    @BindView(R.id.tv_myVideoEditActivity_videoVolumnValue)
    TextView videoVolumnValueTextView;
    @BindView(R.id.sb_myVideoEditActivity_musicVolumnSeekBar)
    SeekBar musicVolumnSeekBar;
    @BindView(R.id.tv_myVideoEditActivity_musicVolumnValue)
    TextView musicVolumnValueTextView;
    @BindView(R.id.tv_myVideoEditActivity_musicVolumnValueIn)
    TextView musicVolumnValueIn;
    @BindView(R.id.tv_myVideoEditActivity_musicVolumnValueOut)
    TextView musicVolumnValueOut;
    @BindView(R.id.ll_myVideoEditActivity_music)
    LinearLayout musicLinearLayout;
    @BindView(R.id.ll_myVideoEditActivity_originalVolumeAdjust)
    LinearLayout originalVolumeAdjustLinearLayout;
    @BindView(R.id.ll_myVideoEditActivity_bgMusicVolumeAdjust)
    LinearLayout bgMusicVolumeAdjustLinearLayout;

    @BindView(R.id.ll_myVideoEditActivity_editMusic)
    LinearLayout editMusicLinearLayout;
    @BindView(R.id.ll_myVideoEditActivity_editBeauty)
    LinearLayout editBeautyLinearLayout;
    @BindView(R.id.ll_myVideoEditActivity_editCaption)
    LinearLayout editCaptionLinearLayout;
    @BindView(R.id.ll_myVideoEditActivity_editTransition)
    LinearLayout editTransitionLinearLayout;

    @BindView(R.id.mEditMusicText)
    TextView mEditMusicText;
    @BindView(R.id.mEditBeautyText)
    TextView mEditBeautyText;
    @BindView(R.id.mEditCaptionText)
    TextView mEditCaptionText;
    @BindView(R.id.mEditTransitionText)
    TextView mEditTransitionText;

    @BindView(R.id.mEditMusicImage)
    ImageView mEditMusicImage;
    @BindView(R.id.mEditBeautyImage)
    ImageView mEditBeautyImage;
    @BindView(R.id.mEditCaptionImage)
    ImageView mEditCaptionImage;
    @BindView(R.id.mEditTransitionImage)
    ImageView mEditTransitionImage;

    @BindView(R.id.rl_myVideoEditActivity_addMusic)
    RelativeLayout addMusicRelativeLayout;
    @BindView(R.id.tv_myVideoEditActivity_musicTab)
    TextView musicTabTextView;
    @BindView(R.id.tv_myVideoEditActivity_volumnTab)
    TextView volumnTabTextView;
    @BindView(R.id.ll_myVideoEditActivity_twoTabs)
    LinearLayout twoTabsLinearLayout;
    @BindView(R.id.rv_myVideoEditActivity_music)
    RecyclerView musicRecyclerView;
    @BindView(R.id.sb_myVideoEditActivity_filter)
    SeekBar filterSeekBar;
    @BindView(R.id.tv_myVideoEditActivity_filterValue)
    TextView filterValueTextView;
    @BindView(R.id.iv_myVideoEditActivity_addNewItem)
    ImageView addNewItemImageView;
    @BindView(R.id.iv_myVideoEditActivity_filterIcon)
    ImageView filterIconImageView;
    @BindView(R.id.ll_myVideoEditActivity_filter)
    LinearLayout filterLinearLayout;
    @BindView(R.id.ll_myVideoEditActivity_filterIntensity)
    LinearLayout filterIntensityLinearLayout;
    @BindView(R.id.rv_myVideoEditActivity_filter)
    RecyclerView filterRecyclerView;
    @BindView(R.id.linear_myVideoEditActivity_caption)
    LinearLayout myVideoEditActivity_caption;
    @BindView(R.id.rv_myVideoEditActivity_sticker)
    RecyclerView stickerRecyclerView;
    @BindView(R.id.rv_myVideoEditActivity_transition)
    RecyclerView transitionRecyclerView;
    @BindView(R.id.ll_myVideoEditActivity_compileLoading)
    LinearLayout compileLoadingLinearLayout;
    @BindView(R.id.rl_myVideoEditActivity_halfBottom)
    RelativeLayout halfBottomRelativeLayout;
    @BindView(R.id.ll_myVideoEditActivity_delItem)
    LinearLayout delItemLinearLayout;
    @BindView(R.id.iv_myVideoEditActivity_deleteIcon)
    ImageView deleteIconImageView;
    @BindView(R.id.iv_myVideoEditActivity_deleteText)
    TextView textIconImageView;
    @BindView(R.id.mVideoEditContentFrame)
    FrameLayout mVideoEditContentFrame;

    private NvsStreamingContext mStreamingContext;
    private NvsTimeline mTimeline;
    private NvsVideoTrack mVideoTrack;
    private NvsAudioTrack mMusicAudioTrack;
    private final long BASE_VALUE = 100000;
    private VideoClipFxInfo mVideoClipFxInfo;
    private TransitionInfo mTransitionInfo;
    public static final int TAB_MUSIC_ADD = 1000;
    public static final int TAB_MUSIC_LIST = 1001;
    public static final int TAB_FILTER = 1002;
    public static final int TAB_CAPTION = 1003;
    public static final int TAB_TRANSITION = 1005;
    public static final int REQUEST_CODE = 101;
    public static final int RESULT_OK_IN_ALBUM = 301;
    public static final int RESULT_OK_IN_MUSIC = 302;
    public static final int RESULT_OK_IN_CAPTION_STYLE = 303;
    public boolean needConnectLiveWindow = false;
    public boolean mIsGoHome = false;
    private VideoEditAdapter mVideoEditAdapter;
    private MusicAdapter musicAdapter;
    private List<MusicBean> musicBeanList = new ArrayList<>();
    private VideoFilterAdapter filterAdapter;
    private List<MusicBean> filterBeanList = new ArrayList<>();
    private VideoFilterAdapter transitionAdapter;
    private List<MusicBean> transitionBeanList = new ArrayList<>();
    private String compileVideoPath = com.test.xcamera.utils.Constants.myGalleryLocalPath + "/" + "VID_" + System.currentTimeMillis() + ".mp4";
    private int currentPlayItem = -1;
    private VideoPlayLayout mVideoPlayLayout;
    private CommonDownloadDialog downProgressDialog;
    VideoTitleComCaptionLayout mTitlePanelLayout;
    private int mSelectPosition = 0;
    //是否正在移动
    private boolean mIsDraging = false;
    private boolean mIsSelect = false;
    private ItemTouchHelper mItemTouchHelper;
    private boolean misPlayIng = false;
    private boolean mIsCreate = true;
    private VideoEditPresenter mVideoEditPresenter;
    /*一建成片*/
    private static final int SOURCE_TYPE_CameraClipEdit = 1;
    private static final int SOURCE_TYPE_Other = 0;
    private static final String SOURCE = "SOURCE";
    private int mSourceType;
    private boolean mIsCompileIng = false;

    public static void startMyVideoEditActivity(Context context, List<MediaData> list) {
        ArrayList<ClipInfo> pathList = MediaClipDataTran.mediaData2ClipInfo(list);
        VideoImportCheck.getInstance().checkVideoIsConvertImport(pathList, new WeakReference<>(context), new VideoImportCheck.OnImportCheckCallBack() {
            @Override
            public void onCheckFinish() {
                VideoImportCheck.mVideoImportCheck.setOnImportCheckCallBack(null);
                VideoImportCheck.reset();
                if (MediaClipDataTran.videoParamToTimelineData(pathList)) {
                    Intent intent = new Intent(context, MyVideoEditActivity.class);
                    intent.putExtra(SOURCE, SOURCE_TYPE_Other);
                    context.startActivity(intent);
                }
            }
        });

    }

    public static void startMyVideoEditActivity(Context context, VideoParamData paramData) {
        ArrayList<ClipInfo> pathList = MediaClipDataTran.videoParamClipToClipInfo(paramData);
        VideoImportCheck.getInstance().checkVideoIsConvertImport(pathList, new WeakReference<>(context), new VideoImportCheck.OnImportCheckCallBack() {
            @Override
            public void onCheckFinish() {
                VideoImportCheck.mVideoImportCheck.setOnImportCheckCallBack(null);
                VideoImportCheck.reset();
                if (MediaClipDataTran.videoParamToTimelineData(paramData, pathList)) {
                    Intent intent = new Intent(context, MyVideoEditActivity.class);
                    intent.putExtra(SOURCE, SOURCE_TYPE_CameraClipEdit);
                    context.startActivity(intent);
                }
            }
        });
    }

    @OnClick({R.id.left_iv_title, R.id.frame_pause, R.id.right_tv_titlee, R.id.tv_myVideoEditActivity_musicTab, R.id.tv_myVideoEditActivity_volumnTab,
            R.id.ll_myVideoEditActivity_editMusic, R.id.ll_myVideoEditActivity_editBeauty, R.id.ll_myVideoEditActivity_editCaption,
            R.id.ll_myVideoEditActivity_editTransition, R.id.rl_myVideoEditActivity_addMusic,
            R.id.iv_myVideoEditActivity_addNewItem})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_myVideoEditActivity_addMusic:
                NonDuplicateFactory.proxy(new Perform() {
                    @Override
                    public void perform() {
                        stopPlay();
                        mIsSelect = true;
                        SelectMusicActivity.openForResult(MyVideoEditActivity.this, REQUEST_CODE);
                    }
                });

                break;
            case R.id.ll_myVideoEditActivity_editMusic:
                showMusicContext();
                break;
            case R.id.ll_myVideoEditActivity_editBeauty:
                StatisticMaterialEdit.getInstance().setOnEvent(StatisticMaterialEdit.MaterialEdit_Filter);
                showBottomMenuTab(TAB_FILTER);
                break;
            case R.id.ll_myVideoEditActivity_editCaption:
                StatisticMaterialEdit.getInstance().setOnEvent(StatisticMaterialEdit.MaterialEdit_Transition);
                showBottomMenuTab(TAB_CAPTION);
                break;
            case R.id.ll_myVideoEditActivity_editTransition:
                StatisticMaterialEdit.getInstance().setOnEvent(StatisticMaterialEdit.MaterialEdit_Title);
                showBottomMenuTab(TAB_TRANSITION);
                break;
            case R.id.tv_myVideoEditActivity_musicTab:
                showMusicTab();
                break;
            case R.id.iv_myVideoEditActivity_addNewItem:
                NonDuplicateFactory.proxy(new Perform() {
                    @Override
                    public void perform() {
                        stopPlay();
                        Intent intent = new Intent(MyVideoEditActivity.this, AlbumActivity.class);
                        intent.putExtra("mSelectClipPosition", mSelectPosition + 1);
                        startActivityForResult(intent, REQUEST_CODE);
                    }
                });

                break;
            case R.id.tv_myVideoEditActivity_volumnTab:
                musicTabTextView.setTextColor(getResources().getColor(R.color.defaultTextViewColor));
                volumnTabTextView.setTextColor(getResources().getColor(R.color.selectTextViewColor));
                musicRecyclerView.setVisibility(View.GONE);
                originalVolumeAdjustLinearLayout.setVisibility(View.VISIBLE);
                bgMusicVolumeAdjustLinearLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.left_iv_title:
                onBackPressed();
                break;
            case R.id.right_tv_titlee:
                if(mIsDraging){
                    return;
                }
                NonDuplicateFactory.proxy(new Perform() {
                    @Override
                    public void perform() {
                        StatisticMaterialEdit.getInstance().setOnEvent(StatisticMaterialEdit.MaterialEdit_Compose);
                        if (mIsCompileIng) {
                            return;
                        }
                        stopPlay();
                        mIsCompileIng = true;
                        if(mVideoEditPresenter!=null){
                            mVideoEditPresenter.compileVideo(new WeakReference<>(MyVideoEditActivity.this).get(),mStreamingContext, mTimeline, compileVideoPath, 0, mTimeline.getDuration());
                        }
                        StatisticShare.SHARE_FROM_KEY=StatisticShare.Video_Edit;
                    }
                });

                break;
            case R.id.frame_pause:
                if (mStreamingContext.getStreamingEngineState() == NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                    mStreamingContext.stop();
                } else {
                    mStreamingContext.playbackTimeline(mTimeline, mStreamingContext.getTimelineCurrentPosition(mTimeline), mTimeline.getDuration(), NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, true, NvsStreamingContext.STREAMING_ENGINE_PLAYBACK_FLAG_BUDDY_HOST_VIDEO_FRAME);
                }

                break;
        }
    }

    @Override
    public int initView() {
        return R.layout.activity_my_video_edit;
    }

    @Override
    public void initData() {
        mSourceType = getIntent().getIntExtra(SOURCE, 0);
        ActivityContainer.getInstance().addActivity(this);
        noStatusBar();
        setStatusBarColor(getResources().getColor(R.color.c050505), 0);
        mVideoEditPresenter = VideoEditPresenter.getInstance(this, this);
        VideoEditManger.mCurrentTime = 0;
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mPlayerLayout.getLayoutParams();
        params.height = PUtil.getScreenH(this) / 2 - DensityUtils.dp2px(this, 60);
        mPlayerLayout.setLayoutParams(params);
        //设置组合字幕模式
        mVideoPlayLayout = new VideoPlayLayout(this);
        mStreamingContext = mVideoPlayLayout.getStreamingContext();

        mTimeline = mVideoPlayLayout.getTimeline();
        mLiveWindow = mVideoPlayLayout.getmLiveWindow();
        mPlayerLayout.addView(mVideoPlayLayout);
        if (mTimeline == null) {
            show(getString(R.string.video_edit_create_error));
            finishVideoEdit(false);
            return;
        }
        VideoEditManger.mVideoWidth = mTimeline.getVideoRes().imageWidth;
        VideoEditManger.mVideoHeight = mTimeline.getVideoRes().imageHeight;
        mVideoTrack = mTimeline.getVideoTrackByIndex(0);
        if (mVideoTrack == null) {
            return;
        }
        mMusicAudioTrack = mTimeline.getAudioTrackByIndex(0);
        mTransitionInfo = TimelineData.instance().cloneTransitionData();
        if (mTransitionInfo == null) {
            mTransitionInfo = new TransitionInfo();
        }
        mVideoClipFxInfo = TimelineData.instance().getVideoClipFxData();
        if (mVideoClipFxInfo == null) {
            mVideoClipFxInfo = new VideoClipFxInfo();
        }
        tvMiddleTitle.setText("");
        RelativeLayout.LayoutParams rightTvTitleeParams = (RelativeLayout.LayoutParams) rightTvTitlee.getLayoutParams();
        rightTvTitleeParams.height = DensityUtils.dp2px(this, 32);
        rightTvTitleeParams.width = DensityUtils.dp2px(this, 56);
        rightTvTitlee.setLayoutParams(rightTvTitleeParams);
        rightTvTitlee.setVisibility(View.VISIBLE);
        rightTvTitlee.setText(getString(R.string.video_edit_complie));
        rightTvTitlee.setTextColor(Color.WHITE);
        rightTvTitlee.setBackgroundResource(R.drawable.circle_corner_green_bg_normal_2dp);
        compileLoadingLinearLayout.setVisibility(View.GONE);
        showBottomMenuTab(TAB_MUSIC_ADD);
        showMusicTab();
        initRecycleView();
        initSeekBar();
        connectTimelineWithLiveWindow();
        setTotalDuaration();
        initBackDialog();
        mIsCreate = true;
        initEditCompoundCaption();
    }

    private NvsTimelineCompoundCaption mCurCaption;
    private boolean mIsInnerDrawRect = false;
    private ArrayList<CompoundCaptionInfo> mCaptionDataListClone;

    public void initEditCompoundCaption() {
        mCaptionDataListClone = TimelineData.instance().cloneCompoundCaptionData();
        mVideoPlayLayout.setEditMode(Constants.EDIT_MODE_COMPOUND_CAPTION);
        mVideoPlayLayout.setAssetEditListener(new EditCallBack.AssetEditListener() {
            @Override
            public void onAssetDelete() {/*删除*/
                if (mCurCaption != null) {
                    VideoCompoundCaption.getInstanceOther().removeCurCompoundCaption(mTimeline, mCurCaption);
                    long cur_position = mStreamingContext.getTimelineCurrentPosition(mTimeline);
                    seekTimeline(cur_position);
                    selectCurCompoundCaption();
                    mVideoPlayLayout.showRunes(MovieSubtitleTimeline.getHookedMarkList((float) cur_position / VideoEditManger.VIDEO_microsecond_TIME * 10), cur_position);
                    updateCurrentPlayPosition(cur_position);
                    mTitlePanelLayout.setSelectPosition("-1");
                }
            }

            @Override
            public void onAssetSelected(PointF curPoint) {/*选中*/
                //判断若没有选中当前字幕框则选中，选中则不处理
                mIsInnerDrawRect = mVideoPlayLayout.curPointIsInnerDrawRect((int) curPoint.x, (int) curPoint.y);
                if (!mIsInnerDrawRect) {
                    mVideoPlayLayout.selectCompoundCaptionByHandClick(curPoint);
                    mCurCaption = mVideoPlayLayout.getCurrCompoundCaption();
                }
            }

            @Override
            public void onAssetTranstion() {/*移动*/
                if (mCurCaption == null)
                    return;
                PointF pointF = mCurCaption.getCaptionTranslation();
                int zVal = (int) mCurCaption.getZValue();
                int index = getCaptionIndex(zVal);
                if (index >= 0) {
                    TimelineData.instance().getCompoundCaptionArray().get(index).setTranslation(pointF);
                }
            }

            @Override
            public void onAssetScale() {/*缩放*/
                if (mCurCaption == null)
                    return;
                int zVal = (int) mCurCaption.getZValue();
                int index = getCaptionIndex(zVal);
                if (index >= 0) {
                    TimelineData.instance().getCompoundCaptionArray().get(index).setScaleFactorX(mCurCaption.getScaleX());
                    TimelineData.instance().getCompoundCaptionArray().get(index).setScaleFactorY(mCurCaption.getScaleY());
                    TimelineData.instance().getCompoundCaptionArray().get(index).setAnchor(mCurCaption.getAnchorPoint());
                    TimelineData.instance().getCompoundCaptionArray().get(index).setRotation(mCurCaption.getRotationZ());
                    PointF pointF = mCurCaption.getCaptionTranslation();
                    TimelineData.instance().getCompoundCaptionArray().get(index).setTranslation(pointF);
                }
            }

            @Override
            public void onAssetAlign(int alignVal) {
            }

            @Override
            public void onAssetHorizFlip(boolean isHorizFlip) {
            }

            @Override
            public void onTouchDown() {
                mIsDraging = true;
            }

            @Override
            public void onTouchUp() {
                mIsDraging = false;
                long cur_position = mStreamingContext.getTimelineCurrentPosition(mTimeline);
                seekTimeline(cur_position);
                selectCurCompoundCaption();
            }
        });
        mVideoPlayLayout.setCompoundCaptionListener(new VideoPlayLayout.OnCompoundCaptionListener() {
            @Override
            public void onCaptionIndex(int captionIndex) {
                if (mCurCaption == null) {
                    return;
                }
                mCaptionDataListClone = TimelineData.instance().cloneCompoundCaptionData();
                int captionCount = mCurCaption.getCaptionCount();
                if (captionIndex < 0 || captionIndex >= captionCount) {
                    return;
                }
                mIsInnerDrawRect = false;

                int zVal = (int) mCurCaption.getZValue();
                BackupData.instance().setCaptionZVal(zVal);
                BackupData.instance().setCompoundCaptionList(mCaptionDataListClone);
                BackupData.instance().setCurSeekTimelinePos(mStreamingContext.getTimelineCurrentPosition(mTimeline));
                Log.i("club", "club_selectCurCompoundCaption ListClone:" + mCaptionDataListClone.size());

                String captionText = mCurCaption.getText(captionIndex);
                Bundle bundle = new Bundle();
                bundle.putInt(select_caption_index, captionIndex);
                bundle.putString(select_caption_text, captionText);
                Intent intent = new Intent(MyVideoEditActivity.this, VideoCompoundCaptionStyleActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, RESULT_OK_IN_CAPTION_STYLE);
            }
        });
    }

    private int getCaptionIndex(int curZValue) {
        if(mVideoEditPresenter!=null){
            mVideoEditPresenter.getCompoundCaptionIndex(curZValue);
        }
        return 0;
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        intent.putExtra(AlbumActivity.KEY_OF_REQUEST_CODE, requestCode);
        super.startActivityForResult(intent, requestCode);
    }

    private void showMusicTab() {
        musicTabTextView.setTextColor(getResources().getColor(R.color.selectTextViewColor));
        volumnTabTextView.setTextColor(getResources().getColor(R.color.defaultTextViewColor));
        originalVolumeAdjustLinearLayout.setVisibility(View.GONE);
        bgMusicVolumeAdjustLinearLayout.setVisibility(View.GONE);
        musicRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showMusicContext() {
        if (musicAdapter != null && musicAdapter.mData != null) {
            if (musicAdapter.mData.size() > 1) {
                showBottomMenuTab(TAB_MUSIC_LIST);
            } else {
                showBottomMenuTab(TAB_MUSIC_ADD);
            }
        }
    }

    private void setTabDefaultBgColor() {
        mEditMusicImage.setImageResource(R.mipmap.video_edit_music_normal);
        mEditBeautyImage.setImageResource(R.mipmap.video_edit_filter_normal);
        mEditCaptionImage.setImageResource(R.mipmap.video_edit_caption_normal);
        mEditTransitionImage.setImageResource(R.mipmap.video_edit_transition_normal);
        mEditMusicText.setTextColor(this.getResources().getColorStateList(R.color.ffffff66));
        mEditBeautyText.setTextColor(this.getResources().getColorStateList(R.color.ffffff66));
        mEditCaptionText.setTextColor(this.getResources().getColorStateList(R.color.ffffff66));
        mEditTransitionText.setTextColor(this.getResources().getColorStateList(R.color.ffffff66));
    }

    CenterLayoutManager mDragLayoutManager;

    private void initRecycleView() {
        mDragLayoutManager = new CenterLayoutManager(this, RecyclerView.HORIZONTAL, false);
        dragRecycleView.setLayoutManager(mDragLayoutManager);
        mVideoEditAdapter = new VideoEditAdapter(this);
        mVideoEditAdapter.setOnDragItemCallBack(new VideoEditAdapter.OnDragItemCallBack() {
            @Override
            public void OnEditButtonCallBack(int position) {
                mDragLayoutManager.smoothScrollToPosition(dragRecycleView, new RecyclerView.State(), position);
                stopPlay();
                MyVideoEditDetailActivity.open(MyVideoEditActivity.this, position, mStreamingContext.getTimelineCurrentPosition(mTimeline));
                needConnectLiveWindow = true;
            }
        });
        dragRecycleView.setAdapter(mVideoEditAdapter);
        mVideoEditAdapter.setOnRecyclerItemClickListener(new BaseRecyclerAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                mDragLayoutManager.smoothScrollToPosition(dragRecycleView, new RecyclerView.State(), pos);
                mVideoEditAdapter.setSelectPosition(pos, true);
                mSelectPosition = pos;
                long curTime = 0;
                if (mVideoTrack.getClipByIndex(pos) == null) {
                    return;
                } else {
                    curTime = mVideoTrack.getClipByIndex(pos).getInPoint() + 10000;
                }
                updateCurrentPlayPosition(curTime);
                mStreamingContext.seekTimeline(mTimeline, curTime, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_BUDDY_HOST_VIDEO_FRAME);
                selectCurCompoundCaption();
                mVideoPlayLayout.showRunes(MovieSubtitleTimeline.getHookedMarkList((float) curTime / VideoEditManger.VIDEO_microsecond_TIME * 10), curTime);
            }
        });
        deleteIconImageView.setImageResource(R.drawable.ic_edit_no_delete);
        textIconImageView.setText(R.string.touch_text);

        ItemVideoTouchHelperCallback itemTouchHelperCallBack = new ItemVideoTouchHelperCallback(delItemLinearLayout, mVideoEditAdapter);
        itemTouchHelperCallBack.setDragListener(new ItemTouchHelperCallBack.DragListener() {
            @Override
            public void onDragStart() {
                mIsDraging = true;
                setRecyclerViewWidthAndHeight(mIsDraging);
                stopPlay();
                halfBottomRelativeLayout.setClipChildren(false);
                delItemLinearLayout.setAnimation(AnimationUtils.loadAnimation(MyVideoEditActivity.this, R.anim.tranlate_dialog_out));
                delItemLinearLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onDragFinish(boolean delete, int selectedPosition) {
                mIsDraging = false;
                setRecyclerViewWidthAndHeight(mIsDraging);
                halfBottomRelativeLayout.setClipChildren(true);
                delItemLinearLayout.setAnimation(AnimationUtils.loadAnimation(MyVideoEditActivity.this, R.anim.tranlate_dialog_in));
                delItemLinearLayout.setVisibility(View.GONE);

                if (delete) {
                    if (TimelineData.instance().getClipInfoData().size() == 1) {
                        finishVideoEdit(false);
                        return;
                    }
                    int delPosition=mVideoEditAdapter.getPositionTo();
                    mVideoEditAdapter.setRemovePosition(delPosition);
                    VideoEditManger.setVideoDel(delPosition, mTimeline);
                } else {
                    VideoEditManger.setVideoMove(mVideoEditAdapter.getPositionFrom(), mVideoEditAdapter.getPositionTo(), mTimeline);
                }
                RecyclerChangCallBack.getInstance(dragRecycleView).setOnChangCallBack(isSucc -> {
                    mVideoEditAdapter.setSelectPosition(mVideoEditAdapter.getPositionTo(), true);

                });
                setTotalDuaration();
                currentPlayItem = -1;
                long time = 0;
                if (mVideoTrack.getClipByIndex(mVideoEditAdapter.getPositionTo()) != null) {
                    time = mVideoTrack.getClipByIndex(mVideoEditAdapter.getPositionTo()).getInPoint();

                }
                mStreamingContext.playbackTimeline(mTimeline, time, mTimeline.getDuration(), NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, true, NvsStreamingContext.STREAMING_ENGINE_PLAYBACK_FLAG_BUDDY_HOST_VIDEO_FRAME);
            }

            @Override
            public void onDragAreaChange(boolean isInside) {
                if (isInside) {
                    deleteIconImageView.setImageResource(R.drawable.ic_edit_deleted);
                    textIconImageView.setText(R.string.remove);
                } else {
                    deleteIconImageView.setImageResource(R.drawable.ic_edit_no_delete);
                    textIconImageView.setText(R.string.touch_text);
                }
            }
        });
        mVideoEditAdapter.setDragStartListener(this);
        mItemTouchHelper = new ItemTouchHelper(itemTouchHelperCallBack);
        mItemTouchHelper.attachToRecyclerView(dragRecycleView);
        mVideoEditAdapter.updateData(true, MediaClipDataTran.clipInfo2MediaData(TimelineData.instance().getClipInfoData()));
        CenterLayoutManager musicLayoutManager = new CenterLayoutManager(this, RecyclerView.HORIZONTAL, false);
        musicRecyclerView.setLayoutManager(musicLayoutManager);
        musicAdapter = new MusicAdapter(this);
        musicRecyclerView.setAdapter(musicAdapter);
        musicAdapter.setOnRecyclerItemClickListener(new BaseRecyclerAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                NonDuplicateFactory.proxy(new Perform() {
                    @Override
                    public void perform() {
                        musicLayoutManager.smoothScrollToPosition(musicRecyclerView, new RecyclerView.State(), pos);
                        if (pos == 0) {
                            stopPlay();
                            mIsSelect = true;
                            SelectMusicActivity.openForResult(MyVideoEditActivity.this, REQUEST_CODE);
                            return;
                        }
                        if (musicAdapter.mData.get(pos).isSelected()) {
                            musicAdapter.mData.get(pos).setSelected(false);
                            musicAdapter.notifyItemChanged(pos);
                            mMusicAudioTrack.removeAllClips();
                            if (TimelineData.instance().getMusicData() != null) {
                                TimelineData.instance().getMusicData().clear();
                            }
                        } else {
                            playPositionItemMusic(pos);

                        }
                        mStreamingContext.playbackTimeline(mTimeline, 0, mTimeline.getDuration(), NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, true, NvsStreamingContext.STREAMING_ENGINE_PLAYBACK_FLAG_BUDDY_HOST_VIDEO_FRAME);

                    }
                });
            }
        });
        CenterLayoutManager filterLayoutManager = new CenterLayoutManager(this, RecyclerView.HORIZONTAL, false);
        filterRecyclerView.setLayoutManager(filterLayoutManager);
        filterAdapter = new VideoFilterAdapter(this);
        filterRecyclerView.setAdapter(filterAdapter);
        filterAdapter.setOnRecyclerItemClickListener(new BaseRecyclerAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                NonDuplicateFactory.proxy(new Perform() {
                    @Override
                    public void perform() {
                        filterLayoutManager.smoothScrollToPosition(filterRecyclerView, new RecyclerView.State(), pos);
                        filterAdapter.selectPosition(pos);
                        if (pos == 0) {
                            filterIntensityLinearLayout.setVisibility(View.INVISIBLE);
                            mVideoClipFxInfo.setFxId(null);
                        } else if (pos > 0) {
                            filterIntensityLinearLayout.setVisibility(View.VISIBLE);
                            mVideoClipFxInfo.setFxMode(VideoClipFxInfo.FXMODE_PACKAGE);
                            mVideoClipFxInfo.setFxId(filterAdapter.mData.get(pos).getId());
                        } else {
                            filterIntensityLinearLayout.setVisibility(View.VISIBLE);
                            mVideoClipFxInfo.setFxMode(VideoClipFxInfo.FXMODE_BUILTIN);
                            mVideoClipFxInfo.setFxId(filterAdapter.mData.get(pos).getId());
                        }
                        long curTime = mStreamingContext.getTimelineCurrentPosition(mTimeline);
                        updateFilterSeekBar(100);
                        TimelineData.instance().setVideoClipFxData(mVideoClipFxInfo);
                        TimelineUtil.buildTimelineFilter(mTimeline, mVideoClipFxInfo);
                        if (!misPlayIng) {
                            mStreamingContext.seekTimeline(mTimeline, curTime, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_BUDDY_HOST_VIDEO_FRAME);
                        }
                    }
                });
            }
        });

        transitionBeanList.addAll(VideoTransitionData.getInstance().getTransitionDataList(TimelineData.instance().getTransitionData()));
        CenterLayoutManager transitionLayoutManager = new CenterLayoutManager(this, RecyclerView.HORIZONTAL, false);
        transitionRecyclerView.setLayoutManager(transitionLayoutManager);
        transitionAdapter = new VideoFilterAdapter(this);
        transitionRecyclerView.setAdapter(transitionAdapter);
        transitionAdapter.setOnRecyclerItemClickListener(new BaseRecyclerAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                NonDuplicateFactory.proxy(new Perform() {
                    @Override
                    public void perform() {
                        currentPlayItem = -1;
                        transitionLayoutManager.smoothScrollToPosition(transitionRecyclerView, new RecyclerView.State(), pos);
                        for (int i = 0; i < transitionAdapter.mData.size(); i++) {
                            transitionAdapter.mData.get(i).setSelected(false);
                        }
                        transitionAdapter.mData.get(pos).setSelected(true);
                        transitionAdapter.notifyDataSetChanged();
                        if (pos == 0) {
                            mTransitionInfo.setTransitionId(null);
                        } else if (pos > 0 && pos < 4) {
                            mTransitionInfo.setTransitionMode(TransitionInfo.TRANSITIONMODE_BUILTIN);
                            mTransitionInfo.setTransitionId(transitionAdapter.mData.get(pos).getId());
                        } else {
                            mTransitionInfo.setTransitionMode(TransitionInfo.TRANSITIONMODE_PACKAGE);
                            mTransitionInfo.setTransitionId(transitionAdapter.mData.get(pos).getId());

                        }
                        VideoEditManger.setVideoTransition(-1, mTimeline, mTransitionInfo);
                        long time = mVideoTrack.getClipByIndex(0).getOutPoint();
                        if (time > VideoEditManger.VIDEO_microsecond_TIME * 1) {
                            time = time - VideoEditManger.VIDEO_microsecond_TIME * 1;
                        }
                        mStreamingContext.playbackTimeline(mTimeline, time, mTimeline.getDuration(), NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, true, NvsStreamingContext.STREAMING_ENGINE_PLAYBACK_FLAG_BUDDY_HOST_VIDEO_FRAME);

                    }
                });
            }
        });
        transitionAdapter.updateData(true, transitionBeanList);
        mVideoEditPresenter.getMusicBGBeanList();
        mVideoEditPresenter.getVideoFilerBeanList();
    }

    public void setRecyclerViewWidthAndHeight(boolean mIsDragIng) {
        int height;
        if (mIsDragIng) {
            addNewItemImageView.setVisibility(View.GONE);
            height=FrameLayout.LayoutParams.MATCH_PARENT;
        } else {
            addNewItemImageView.setVisibility(View.VISIBLE);
            height= PUtil.dip2px(this, 80);
        }
        RelativeLayout.LayoutParams mLayoutParams = (RelativeLayout.LayoutParams) dragRecycleView.getLayoutParams();
        mLayoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT;
        mLayoutParams.height = height;
        dragRecycleView.setLayoutParams(mLayoutParams);
    }

    private void stopPlay() {
        if (mStreamingContext == null) {
            showErrorMassage();
            return;
        }
        if (getStreamingEngineState() == NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
            mStreamingContext.stop();
        }
    }

    private int getStreamingEngineState() {
        if (mStreamingContext == null) {
            showErrorMassage();
            return -1;
        }
        return mStreamingContext.getStreamingEngineState();
    }

    private void showErrorMassage() {
        ToastUtil.showToast(AiCameraApplication.getContext(), getResourceToString(R.string.video_edit_error));
        finishVideoEdit(false);
    }

    /**
     * 关闭当前编辑activity
     * @param isGoHome 是否去首页
     */
    private void finishVideoEdit(boolean isGoHome){
        mIsGoHome = isGoHome;
        if(mSourceType!=SOURCE_TYPE_CameraClipEdit||isGoHome){
             VideoEditManger.releaseNvsStreamingContext();
        }
        finish();
    }

    private void playPositionItemMusic(int pos) {
        if(mVideoEditPresenter!=null){
            mVideoEditPresenter.playPositionItemMusic(mTimeline,mMusicAudioTrack,pos,musicAdapter.mData.get(pos).getPath());
        }
        musicAdapter.selectPosition(pos);
        int volumeMusicVal = (int) Math.floor(mMusicAudioTrack.getVolumeGain().leftVolume / VIDEOVOLUME_MUSIC_MAXVOLUMEVALUE * VIDEOVOLUME_MAXSEEKBAR_VALUE + 0.5D);
        updateMusicVoiceSeekBar(volumeMusicVal);
    }

    public void setConnectedUSBError() {
        if(mVideoEditPresenter!=null){
            mVideoEditPresenter.setConnectedUSBError();
        }
    }

    @Override
    public void connectedUSB() {
        super.connectedUSB();
        mIsDisconnectedUSB=true;
        show(getString(R.string.connectCameraSucc));
        if(mVideoEditPresenter!=null){
            mVideoEditPresenter.setUsbDispose();
        }
    }
    private boolean mIsDisconnectedUSB=true;
    @Override
    public void disconnectedUSB() {
        super.disconnectedUSB();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mIsDisconnectedUSB){
                    mIsDisconnectedUSB=false;
                    show(getString(R.string.disconenct_usb));
                }
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (Constants.EDIT_VIDEO_FINISH) {
            Constants.EDIT_VIDEO_FINISH = false;
            finishVideoEdit(false);
        }
        if (mIsCompileIng) {
            return;
        }
        if (needConnectLiveWindow) {
            currentPlayItem = -1;
            TimelineUtil.reBuildVideoTrack(mTimeline);
            setTotalDuaration();
            mVideoEditAdapter.updateData(true, MediaClipDataTran.clipInfo2MediaData(TimelineData.instance().getClipInfoData()));
            connectTimelineWithLiveWindow();
        }
        if (musicAdapter != null) {
            if (VideoEditManger.checkMusicLose(musicAdapter.mData, mTimeline)) {
                musicAdapter.notifyDataSetChanged();
                if (musicAdapter.mData.size() <= 1) {
                    addMusicRelativeLayout.setVisibility(View.VISIBLE);
                    musicLinearLayout.setVisibility(View.GONE);
                }
            }
        }
        if (VideoEditManger.checkVideoLose(mTimeline)) {
            if (TimelineData.instance().getClipCount() <= 0) {
                startAct(HomeActivity.class);
                finishVideoEdit(true);
                return;
            } else {
                mIsSelect = true;
                mSelectPosition = 0;
                updateCurrentPlayPosition(0);
                setTotalDuaration();
                mVideoEditAdapter.updateData(true, MediaClipDataTran.clipInfo2MediaData(TimelineData.instance().getClipInfoData()));
            }
        }
        if (mTimeline != null) {
            long time = mStreamingContext.getTimelineCurrentPosition(mTimeline);
            if (mIsSelect) {
                time = 0;
                mIsSelect = false;
            } else if (needConnectLiveWindow) {
                time = VideoEditManger.mCurrentTime;
            }
            if (mIsCreate) {
                mIsCreate = false;
                mStreamingContext.playbackTimeline(mTimeline, time, mTimeline.getDuration(), NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, true, NvsStreamingContext.STREAMING_ENGINE_PLAYBACK_FLAG_BUDDY_HOST_VIDEO_FRAME);
            } else {
                mStreamingContext.seekTimeline(mTimeline, time, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_BUDDY_HOST_VIDEO_FRAME);
            }
            NvsVideoClip clip = mVideoTrack.getClipByTimelinePosition(time);
            if (clip != null && mDragLayoutManager != null && mVideoEditAdapter != null) {
                currentPlayItem = clip.getIndex();
                mSelectPosition = currentPlayItem;
                mDragLayoutManager.smoothScrollToPosition(dragRecycleView, new RecyclerView.State(), currentPlayItem);
                mVideoEditAdapter.setSelectPosition(currentPlayItem, true);
            }
            updateCurrentPlayPosition(time);
        }
        needConnectLiveWindow = false;
        setConnectedUSBError();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void playDelayed(long time, int delay) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                play(time);
            }
        }, delay);
    }

    private void play(long time) {
        mStreamingContext.playbackTimeline(mTimeline, time, mTimeline.getDuration(), NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, true, NvsStreamingContext.STREAMING_ENGINE_PLAYBACK_FLAG_BUDDY_HOST_VIDEO_FRAME);

    }

    @Override
    public void onPause() {
        super.onPause();
        ConnectionManager.getInstance().setErrorI(null);
        if (mIsCompileIng) {
            return;
        }
        stopPlay();
    }

    //连接时间线跟liveWindow
    public void connectTimelineWithLiveWindow() {
        mStreamingContext.setPlaybackCallback(new NvsStreamingContext.PlaybackCallback() {
            @Override
            public void onPlaybackPreloadingCompletion(NvsTimeline nvsTimeline) {
            }

            @Override
            public void onPlaybackStopped(NvsTimeline nvsTimeline) {
            }

            @Override
            public void onPlaybackEOF(NvsTimeline nvsTimeline) {
                updateCurrentPlayPosition(0);
                mStreamingContext.playbackTimeline(mTimeline, 0, mTimeline.getDuration(), NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, true, NvsStreamingContext.STREAMING_ENGINE_PLAYBACK_FLAG_BUDDY_HOST_VIDEO_FRAME);
            }
        });

        mStreamingContext.setPlaybackCallback2(new NvsStreamingContext.PlaybackCallback2() {
            @Override
            public void onPlaybackTimelinePosition(NvsTimeline nvsTimeline, long cur_position) {
                NvsVideoClip clip = mVideoTrack.getClipByTimelinePosition(cur_position);
                if (currentPlayItem != clip.getIndex()) {
                    currentPlayItem = clip.getIndex();
                    mSelectPosition = currentPlayItem;
                    mVideoEditAdapter.setSelectPosition(currentPlayItem, true);
                    mDragLayoutManager.smoothScrollToPosition(dragRecycleView, new RecyclerView.State(), currentPlayItem);

                }
                selectCurCompoundCaption();
                mVideoPlayLayout.showRunes(MovieSubtitleTimeline.getHookedMarkList((float) cur_position / VideoEditManger.VIDEO_microsecond_TIME * 10), cur_position);
                updateCurrentPlayPosition(cur_position);
            }
        });

        mStreamingContext.setStreamingEngineCallback(new NvsStreamingContext.StreamingEngineCallback() {
            @Override
            public void onStreamingEngineStateChanged(int i) {
                if (i == NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                    pauseImageView.setBackgroundResource(R.mipmap.play);
                    misPlayIng = true;
                } else {
                    pauseImageView.setBackgroundResource(R.mipmap.pause);
                    misPlayIng = false;

                }
            }

            @Override
            public void onFirstVideoFramePresented(NvsTimeline nvsTimeline) {
            }
        });
        mStreamingContext.connectTimelineWithLiveWindow(mTimeline, mLiveWindow);
    }

    private void selectCurCompoundCaption() {
        long curPos = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        mCurCaption=mVideoEditPresenter.getCurCompoundCaption(mTimeline,curPos);
        updateComCaptionBoundingRect();
    }

    private void updateComCaptionBoundingRect() {
        mVideoPlayLayout.setCurCompoundCaption(mCurCaption);
        mVideoPlayLayout.updateCompoundCaptionCoordinate(mCurCaption);
        mVideoPlayLayout.changeCompoundCaptionRectVisible();

    }

    private void showBottomMenuTab(int tab) {
        hideAllVideoEditView();
        setTabDefaultBgColor();
        mVideoPlayLayout.setIsShowEdit(false);
        selectCurCompoundCaption();
        switch (tab) {
            case TAB_MUSIC_ADD:
                mEditMusicText.setTextColor(this.getResources().getColorStateList(R.color.white));
                mEditMusicImage.setImageResource(R.mipmap.video_edit_music);
                addMusicRelativeLayout.setVisibility(View.VISIBLE);
                break;
            case TAB_MUSIC_LIST:
                mEditMusicText.setTextColor(this.getResources().getColorStateList(R.color.white));
                mEditMusicImage.setImageResource(R.mipmap.video_edit_music);
                musicLinearLayout.setVisibility(View.VISIBLE);
                break;
            case TAB_FILTER:
                mEditBeautyText.setTextColor(this.getResources().getColorStateList(R.color.white));
                mEditBeautyImage.setImageResource(R.mipmap.video_edit_filter);
                filterLinearLayout.setVisibility(View.VISIBLE);
                break;
            case TAB_CAPTION:
                mVideoPlayLayout.setIsShowEdit(true);
                mVideoPlayLayout.setEditMode(EDIT_MODE_COMPOUND_CAPTION);
                selectCurCompoundCaption();
                mEditCaptionText.setTextColor(this.getResources().getColorStateList(R.color.white));
                mEditCaptionImage.setImageResource(R.mipmap.video_edit_caption);
                mTitlePanelLayout = new VideoTitleComCaptionLayout(this, mTimeline, mStreamingContext);
                mVideoEditContentFrame.addView(mTitlePanelLayout);
                mVideoEditContentFrame.setVisibility(View.VISIBLE);
                break;
            case TAB_TRANSITION:
                mEditTransitionText.setTextColor(this.getResources().getColorStateList(R.color.white));
                mEditTransitionImage.setImageResource(R.mipmap.video_edit_transition);
                transitionRecyclerView.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void hideAllVideoEditView() {
        addMusicRelativeLayout.setVisibility(View.GONE);
        musicLinearLayout.setVisibility(View.GONE);
        filterLinearLayout.setVisibility(View.GONE);
        mVideoEditContentFrame.setVisibility(View.GONE);
        mVideoEditContentFrame.removeAllViews();
        stickerRecyclerView.setVisibility(View.GONE);
        transitionRecyclerView.setVisibility(View.GONE);
    }

    public void updateCurrentPlayPosition(long time) {
        currentPlayPositionTextView.setText(MusicUtils.formatTimeStrWithUs(time));
        mPlaySeekBar.setProgress((int) (time / BASE_VALUE));
    }

    public void setTotalDuaration() {
        durationTextView.setText("/  " + MusicUtils.formatTimeStrWithUs(mTimeline.getDuration()));
        mPlaySeekBar.setMax((int) (mTimeline.getDuration() / BASE_VALUE));
    }

    private void initSeekBar() {
        videoVolumnSeekBar.setMax(VIDEOVOLUME_MAXSEEKBAR_VALUE);
        musicVolumnSeekBar.setMax(VIDEOVOLUME_MAXSEEKBAR_VALUE);
        filterSeekBar.setMax(VIDEOVOLUME_MAXSEEKBAR_VALUE);
        int volumeVal = (int) Math.floor(mVideoTrack.getVolumeGain().leftVolume / VIDEOVOLUME_MAXVOLUMEVALUE * VIDEOVOLUME_MAXSEEKBAR_VALUE + 0.5D);
        int volumeMusicVal = (int) Math.floor(mMusicAudioTrack.getVolumeGain().leftVolume / VIDEOVOLUME_MUSIC_MAXVOLUMEVALUE * VIDEOVOLUME_MAXSEEKBAR_VALUE + 0.5D);

        updateVideoVoiceSeekBar(volumeVal);
        updateMusicVoiceSeekBar(volumeMusicVal);
        if(mVideoClipFxInfo!=null&&mVideoClipFxInfo.getFxIntensity()<=1){
            updateFilterSeekBar((int)(100*mVideoClipFxInfo.getFxIntensity()));
        }else {
            updateFilterSeekBar(100);
        }
        if(mVideoClipFxInfo!=null&&mVideoClipFxInfo.getFxId()!=null){
            filterIntensityLinearLayout.setVisibility(View.VISIBLE);
        }else {
            filterIntensityLinearLayout.setVisibility(View.INVISIBLE);
        }
        musicVolumnValueIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateMusicFadeInStatus();
            }
        });
        updateMusicFadeInStatus();
        musicVolumnValueOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateMusicFadeOutStatus();
            }
        });
        updateMusicFadeOutStatus();
        mPlaySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mStreamingContext.seekTimeline(mTimeline, progress * BASE_VALUE, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_BUDDY_HOST_VIDEO_FRAME);
                    updateCurrentPlayPosition(progress * BASE_VALUE);
                    mStreamingContext.playbackTimeline(mTimeline, mStreamingContext.getTimelineCurrentPosition(mTimeline), mTimeline.getDuration(), NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, true, NvsStreamingContext.STREAMING_ENGINE_PLAYBACK_FLAG_BUDDY_HOST_VIDEO_FRAME);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        videoVolumnSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    updateVideoVoiceSeekBar(progress);
                    float volumeVal = progress * VIDEOVOLUME_MAXVOLUMEVALUE / VIDEOVOLUME_MAXSEEKBAR_VALUE;
                    mVideoTrack.setVolumeGain(volumeVal, volumeVal);
                    TimelineData.instance().setOriginVideoVolume(volumeVal);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        musicVolumnSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    updateMusicVoiceSeekBar(progress);
                    float volumeVal = progress * VIDEOVOLUME_MUSIC_MAXVOLUMEVALUE / VIDEOVOLUME_MAXSEEKBAR_VALUE;
                    mMusicAudioTrack.setVolumeGain(volumeVal, volumeVal);
                    TimelineData.instance().setMusicVolume(volumeVal);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        filterSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    updateFilterSeekBar(progress);
                    if(mVideoEditPresenter!=null){
                        mVideoEditPresenter.setFilterIntensity(mTimeline,mVideoClipFxInfo);
                    }
                    if (!misPlayIng) {
                        long curTime = mStreamingContext.getTimelineCurrentPosition(mTimeline);
                        mStreamingContext.seekTimeline(mTimeline, curTime, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_BUDDY_HOST_VIDEO_FRAME);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void updateMusicFadeInStatus() {
        if(mVideoEditPresenter!=null){
            mVideoEditPresenter.updateMusicFadeInStatus(mTimeline,mMusicAudioTrack);
        }
    }

    public void updateMusicFadeInStatus(boolean isOpen) {
        Drawable top;
        if (isOpen) {
            top = getResources().getDrawable(R.mipmap.icon_volume_fade_in_select);
        } else {
            top = getResources().getDrawable(R.mipmap.icon_volume_fade_in_);
        }
        musicVolumnValueIn.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
    }

    private void updateMusicFadeOutStatus() {
        if(mVideoEditPresenter!=null){
            mVideoEditPresenter.updateMusicFadeOutStatus(mTimeline,mMusicAudioTrack);
        }
    }

    public void updateMusicFadeOutStatus(boolean isOpen) {
        Drawable top;
        if (isOpen) {
            top = getResources().getDrawable(R.mipmap.icon_volume_fade_out_select);
        } else {
            top = getResources().getDrawable(R.mipmap.icon_volume_fade_out);
        }
        musicVolumnValueOut.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
    }

    @Override
    public void videoCompileResult(boolean isStatus) {
        mIsCompileIng = false;
        if(isStatus){
            MediaScannerUtil.scanFile(compileVideoPath, "video/mp4");
            Intent intent = new Intent(MyVideoEditActivity.this, CompleteVideoActivity.class);
            intent.putExtra(CompleteVideoActivity.KEY_OF_VIDEO_PATH, compileVideoPath);
            intent.putExtra(CompleteVideoActivity.KEY_OF_VIDEO_DURATION, 0);
            MyVideoEditActivity.this.startActivity(intent);
            finish();
            ActivityContainer.getInstance().finishAllActivity();
        }

    }

    private void updateVideoVoiceSeekBar(int volumeVal) {
        videoVolumnSeekBar.setProgress(volumeVal);
        videoVolumnValueTextView.setText(String.valueOf(volumeVal) + "%");
    }

    private void updateMusicVoiceSeekBar(int volumeVal) {
        musicVolumnSeekBar.setProgress(volumeVal);
        musicVolumnValueTextView.setText(String.valueOf(volumeVal) + "%");
    }

    private void updateFilterSeekBar(int progress) {
        mVideoClipFxInfo.setFxIntensity(progress / (float) 100);
        filterSeekBar.setProgress(progress);
        filterValueTextView.setText(String.valueOf(progress) + "%");
    }

    private void seekTimeline(long timeStamp) {
        mStreamingContext.seekTimeline(mTimeline, timeStamp, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data != null) {
            switch (resultCode) {
                case RESULT_OK_IN_MUSIC:
                    MusicBean musicBean = (MusicBean) data.getSerializableExtra(SelectMusicActivity.KEY_OF_MUSIC_RESULT);
                    if (musicBean == null) {
                        return;
                    }
                    int position = -1;
                    for (int i = 0; i < musicAdapter.mData.size(); i++) {
                        if (musicBean.getPath().equals(musicAdapter.mData.get(i).getPath())) {
                            //音乐列表中已存在
                            position = i;
                            break;
                        }
                    }
                    if (position >= 0) {
                        musicAdapter.mData.remove(musicAdapter.mData.get(position));
                    }
                    musicAdapter.mData.add(1, musicBean);
                    if (musicAdapter.mData.size() > 15) {
                        musicAdapter.mData.remove(musicAdapter.mData.get(15));
                    }
                    playPositionItemMusic(1);
                    showBottomMenuTab(TAB_MUSIC_LIST);
                    playDelayed(0, 400);
                    break;
                case RESULT_OK_IN_ALBUM:
                    mVideoEditAdapter.updateData(true, MediaClipDataTran.clipInfo2MediaData(TimelineData.instance().getClipInfoData()));
                    dragRecycleView.smoothScrollToPosition(mSelectPosition);
                    TimelineUtil.reBuildVideoTrack(mTimeline);
                    setTotalDuaration();
                    currentPlayItem = -1;
                    break;
                case RESULT_OK_IN_CAPTION_STYLE:
                    mCaptionDataListClone = BackupData.instance().getCompoundCaptionList();
                    TimelineUtil.setCompoundCaption(mTimeline, mCaptionDataListClone);
                    TimelineData.instance().setCompoundCaptionArray(mCaptionDataListClone);
                    long curSeekPos = BackupData.instance().getCurSeekTimelinePos();
                    seekTimeline(curSeekPos);
                    selectCurCompoundCaption();
                    mVideoPlayLayout.showRunes(MovieSubtitleTimeline.getHookedMarkList((float) curSeekPos * 10), curSeekPos);
                    break;

            }
        }
    }

    BackDialog mBackDialog;
    boolean mIsCancel = false;
    private void initBackDialog() {
        mBackDialog = new BackDialog(this, R.style.ActionSheetDialogStyle);
        mBackDialog.setOnClickListener(v -> {
            mIsCancel = true;
            if(mSourceType==SOURCE_TYPE_CameraClipEdit){//回退到一建成片
                finishVideoEdit(false);
            }else {//回退到首页
                startAct(HomeActivity.class);
                finishVideoEdit(true);
                ActivityContainer.getInstance().finishAllActivity();
                VideoEditManger.releaseNvsStreamingContext();
            }

        });
    }

    @Override
    public void onBackPressed() {
        if (mIsDraging) {
            return;
        }
        if (mBackDialog != null && !this.isFinishing() && !mBackDialog.isShowing() && !mIsCancel) {
            mBackDialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        if(mVideoEditPresenter!=null){
            mVideoEditPresenter.destroy();
        }
        if(mSourceType!=SOURCE_TYPE_CameraClipEdit ||mIsGoHome){
            if (mTimeline != null) {
                mStreamingContext.removeTimeline(mTimeline);
            }
            TimelineUtil.removeTimeline(mTimeline);
            if (mStreamingContext != null) {
                mStreamingContext.setStreamingEngineCallback(null);
                mStreamingContext.setPlaybackCallback2(null);
                mStreamingContext.setPlaybackCallback(null);
                mStreamingContext.setCompileCallback(null);
                mStreamingContext.setCompileCallback2(null);
                mStreamingContext.setCompileCallback2(null);
                mStreamingContext.setHardwareErrorCallback(null);
                mStreamingContext.clearCachedResources(false);

            }
        }
        VideoFrameThumb.getInstance().onDetached();
        if (downProgressDialog != null) {
            downProgressDialog.destroy();
            downProgressDialog = null;
        }
        if (mLiveWindow != null) {
            mLiveWindow.setOnClickListener(null);
        }
        VideoEditManger.releaseProject();
        super.onDestroy();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN || ev.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
            if (mIsDraging||ev.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
                return false;
            }
            return super.dispatchTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }


    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        if (mStreamingContext != null) {
            mStreamingContext.stop();
        }
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void showMusicBGBeanList(List<MusicBean> list) {
        if (musicBeanList == null || musicAdapter == null) {
            return;
        }
        musicBeanList.clear();
        musicBeanList.addAll(list);
        musicAdapter.updateData(true, musicBeanList);
        showMusicContext();
    }

    @Override
    public void showFilerBeanList(List<MusicBean> list) {
        if (musicBeanList == null || filterAdapter == null) {
            return;
        }
        filterBeanList.clear();
        filterBeanList.addAll(list);
        filterAdapter.updateData(true, filterBeanList);
    }
}
