package com.test.xcamera.phonealbum;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.editvideo.Constants;
import com.editvideo.ConvertFiles;
import com.editvideo.ScreenUtils;
import com.editvideo.TimelineUtil;
import com.editvideo.ToastUtil;
import com.editvideo.dataInfo.ClipInfo;
import com.editvideo.dataInfo.RecordClipsInfo;
import com.editvideo.dataInfo.TimelineData;
import com.framwork.base.adapter.BaseRecyclerAdapter;
import com.framwork.base.view.MOBaseActivity;
import com.test.xcamera.R;
import com.test.xcamera.accrssory.UsbDispose;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.bean.MoErrorData;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.mointerface.MoErrorCallback;
import com.test.xcamera.phonealbum.adapter.BeautyAdapter;
import com.test.xcamera.phonealbum.adapter.VideoMenuAdapter;
import com.test.xcamera.phonealbum.bean.MusicBean;
import com.test.xcamera.phonealbum.layout.EditSubtitleLayout;
import com.test.xcamera.phonealbum.player.VideoPlayLayout;
import com.test.xcamera.phonealbum.presenter.VideoEditDetailContract;
import com.test.xcamera.phonealbum.presenter.VideoEditDetailPresenter;
import com.test.xcamera.phonealbum.usecase.VideoImportCheck;
import com.test.xcamera.phonealbum.widget.VideoCutView;
import com.test.xcamera.phonealbum.widget.VideoEditManger;
import com.test.xcamera.phonealbum.widget.VideoTransitionDialog;
import com.test.xcamera.phonealbum.widget.subtitle.bean.MovieSubtitleTimeline;
import com.test.xcamera.util.MusicUtils;
import com.test.xcamera.utils.CenterLayoutManager;
import com.test.xcamera.utils.DensityUtils;
import com.test.xcamera.utils.FileUtils;
import com.test.xcamera.utils.FormatUtils;
import com.test.xcamera.utils.LoggerUtils;
import com.test.xcamera.utils.PUtil;
import com.test.xcamera.utils.glide.VideoFrameThumb;
import com.test.xcamera.utils.proxy.Perform;
import com.test.xcamera.utils.proxy.click.NonDuplicateFactory;
import com.test.xcamera.view.basedialog.dialog.CommonDownloadDialog;
import com.meicam.sdk.NvsLiveWindow;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoFx;
import com.meicam.sdk.NvsVideoTrack;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.editvideo.Constants.VIDEOVOLUME_MAXSEEKBAR_VALUE;
import static com.editvideo.Constants.VIDEOVOLUME_MAXVOLUMEVALUE;
import static com.test.xcamera.phonealbum.MyVideoEditActivity.REQUEST_CODE;
import static com.test.xcamera.phonealbum.MyVideoEditActivity.RESULT_OK_IN_ALBUM;

public class MyVideoEditDetailActivity extends MOBaseActivity implements VideoEditDetailContract.View {
    @BindView(R.id.left_iv_title)
    ImageView leftIvTitle;
    @BindView(R.id.left_tv_title)
    TextView leftTvTitle;
    @BindView(R.id.right_tv_titlee)
    TextView rightTvTitle;
    @BindView(R.id.tv_middle_title)
    TextView tvMiddleTitle;
    @BindView(R.id.ll_subtitle_layout)
    LinearLayout subtitleLinearLayout;
    NvsLiveWindow mLiveWindow;
    @BindView(R.id.iv_myVideoEditActivity_pause)
    ImageView pauseImageView;
    @BindView(R.id.tv_myVideoEditActivity_currentPlayPosition)
    TextView currentPlayPositionTextView;
    @BindView(R.id.tv_myVideoEditActivity_duration)
    TextView durationTextView;

    @BindView(R.id.ll_myVideoEditActivity_editItem)
    LinearLayout editMusicLinearLayout;
    @BindView(R.id.ll_myVideoEditActivity_editBeautyDetail)
    LinearLayout editBeautyLinearLayout;
    @BindView(R.id.mEditItemImage)
    ImageView mEditItemImage;
    @BindView(R.id.mEditBeautyImage)
    ImageView mEditBeautyImage;
    @BindView(R.id.mEditCaptionImage)
    ImageView mEditCaptionImage;

    @BindView(R.id.mEditItemText)
    TextView mEditItemText;
    @BindView(R.id.mEditBeautyText)
    TextView mEditBeautyText;
    @BindView(R.id.mEditCaptionText)
    TextView mEditCaptionText;

    @BindView(R.id.rv_myVideoEditDetailActivity_itemEdit)
    RecyclerView itemEditRecyclerView;
    @BindView(R.id.ll_myVideoEditDetailActivity_seekBar)
    LinearLayout seekBarLinearLayout;
    @BindView(R.id.tv_myVideoEditDetailActivity_seekBarKey)
    TextView seekBarKeyTextView;
    @BindView(R.id.sb_myVideoEditDetailActivity_beautySeekBar)
    SeekBar beautySeekBar;
    @BindView(R.id.tv_myVideoEditDetailActivity_seekBarValue)
    TextView seekBarValueTextView;
    @BindView(R.id.ll_myVideoEditDetailActivity_adjust)
    LinearLayout adjustLinearLayout;
    @BindView(R.id.rl_myVideoEditDetailActivity_adjustBack)
    RelativeLayout adjustBackRelativeLayout;
    @BindView(R.id.rv_myVideoEditDetailActivity_adjust)
    RecyclerView adjustRecyclerView;
    @BindView(R.id.ll_myVideoEditActivity_changeSpeed)
    LinearLayout changeSpeedLinearLayout;
    @BindView(R.id.tv_myVideoEditDetailActivity_speedQuarter)
    TextView speedQuarterTextView;
    @BindView(R.id.tv_myVideoEditDetailActivity_speedHalf)
    TextView speedHalfTextView;
    @BindView(R.id.tv_myVideoEditDetailActivity_speedOne)
    TextView speedOneTextView;
    @BindView(R.id.tv_myVideoEditDetailActivity_speedTwo)
    TextView speedTwoTextView;
    @BindView(R.id.tv_myVideoEditDetailActivity_speedFour)
    TextView speedFourTextView;
    @BindView(R.id.rv_myVideoEditDetailActivity_beauty)
    RecyclerView beautyRecyclerView;
    @BindView(R.id.mVideoCutView)
    VideoCutView mVideoCutView;
    @BindView(R.id.player_layout)
    FrameLayout mPlayerLayout;
    private VideoPlayLayout mVideoPlayLayout;

    private NvsStreamingContext mStreamingContext;
    private NvsTimeline mTimeline;
    private NvsVideoTrack mVideoTrack;
    public static final int TAB_EDIT_ITEM = 1001;
    public static final int TAB_BEAUTY = 1002;
    public static final int TAB_CAPTION = 1003;
    private NvsVideoFx mColorVideoFx, mVignetteVideoFx, mSharpenVideoFx, mMopiVideoFx;
    public static final int RESULT_OK_IN_SUBTITLE = 303;

    private static final int SEEK_BAR_MODE_ADJUST_ANJIAO = 401;
    private static final int SEEK_BAR_MODE_ADJUST_BAOGUANG = 402;
    private static final int SEEK_BAR_MODE_ADJUST_BAOHEDU = 403;
    private static final int SEEK_BAR_MODE_ADJUST_DUIBIDU = 404;
    private static final int SEEK_BAR_MODE_ADJUST_RUIDU = 405;
    private static final int SEEK_BAR_MODE_VOICE = 201;
    private int CURRENT_SEEK_BAR_MODE = SEEK_BAR_MODE_VOICE;
    private String currentSeekBarString = AiCameraApplication.getContext().getString(R.string.video_edit_vlomue);
    private boolean showSeekBar = false;
    private static final int SEEK_BAR_MODE_BEAUTY_RED = 501;
    private static final int SEEK_BAR_MODE_BEAUTY_MOPI = 502;
    private static final int SEEK_BAR_MODE_BEAUTY_WHITE = 503;
    private static final int SEEK_BAR_MODE_BEAUTY_THINFACE = 504;
    private static final int SEEK_BAR_MODE_BEAUTY_BIGEYE = 505;
    private static final int SEEK_BAR_MODE_BEAUTY_XIABA = 506;
    private static final int SEEK_BAR_MODE_BEAUTY_NOSE = 507;
    private static final int SEEK_BAR_MODE_BEAUTY_HEAD = 508;
    private static final int SEEK_BAR_MODE_BEAUTY_MOUTH_SIZE = 509;
    private static final int SEEK_BAR_MODE_BEAUTY_SMALL_FACE = 510;
    private static final int SEEK_BAR_MODE_BEAUTY_EYE_CORNER = 511;
    private static final int SEEK_BAR_MODE_BEAUTY_FACE_WIDTH = 512;
    private static final int SEEK_BAR_MODE_BEAUTY_NOSE_LENGTH = 513;
    private static final int SEEK_BAR_MODE_BEAUTY_MOUTH_CORNER = 514;
    public static final int MESSAGE_ALL_VIDEO_CONVERT_FINISHED = 2;
    public static final int MESSAGE_ALL_VIDEO_CONVERT_FINISHED_DISS = 3;

    private VideoMenuAdapter itemAdapter;
    private VideoMenuAdapter adjustAdapter;
    private BeautyAdapter beautyAdapter;
    public static final int SPEED_MODE_QUARTER = 14;
    public static final int SPEED_MODE_HALF = 12;
    public static final int SPEED_MODE_ONE = 10;
    public static final int SPEED_MODE_TWO = 20;
    public static final int SPEED_MODE_FOUR = 40;
    private int mSpeedMode = SPEED_MODE_ONE;
    private int mEditItemPosition = -1;
    private float mCurrentSpeed = -1.0f;
    private int mSelectedItemPosition = 0;
    public double pixelMicrosecond;
    private ConvertFiles mConvertFiles;
    private long mCurTime = 0;
    private VideoEditDetailPresenter mPresenter;
    private CommonDownloadDialog downProgressDialog;
    private boolean mIsDraging = false;
    private boolean mIsPlayIng = false;

    @OnClick({R.id.right_tv_titlee, R.id.left_tv_title, R.id.frame_pause, R.id.rl_myVideoEditDetailActivity_adjustBack,
            R.id.tv_use_all_seekBarValue,
            R.id.tv_myVideoEditDetailActivity_speedQuarter, R.id.tv_myVideoEditDetailActivity_speedHalf, R.id.tv_myVideoEditDetailActivity_speedOne,
            R.id.tv_myVideoEditDetailActivity_speedTwo, R.id.tv_myVideoEditDetailActivity_speedFour,
            R.id.ll_myVideoEditActivity_editItem, R.id.ll_myVideoEditActivity_editBeautyDetail, R.id.ll_myVideoEditActivity_editCaptionDetail})
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.rl_myVideoEditDetailActivity_adjustBack:
            case R.id.ll_myVideoEditActivity_editItem:
                showBottomMenuTab(TAB_EDIT_ITEM);
                break;
            case R.id.ll_myVideoEditActivity_editBeautyDetail:
                showBottomMenuTab(TAB_BEAUTY);
                break;
            case R.id.ll_myVideoEditActivity_editCaptionDetail:
                showBottomMenuTab(TAB_CAPTION);
                break;
            case R.id.tv_use_all_seekBarValue:/*应用全部*/
                NonDuplicateFactory.proxy(new Perform() {
                    @Override
                    public void perform() {
                        VideoTransitionDialog dialog = new VideoTransitionDialog(MyVideoEditDetailActivity.this, 0);
                        dialog.show();
                        dialog.setOnTransitionDialog(new VideoTransitionDialog.OnTransitionDialog() {
                            @Override
                            public void onTransitionConfirm() {
                                useSeekAllValue();
                            }
                        });

                    }
                });
                break;
            case R.id.tv_myVideoEditDetailActivity_speedQuarter:
                changeSpeed(0.25f);
                break;
            case R.id.tv_myVideoEditDetailActivity_speedHalf:
                changeSpeed(0.5f);
                break;
            case R.id.tv_myVideoEditDetailActivity_speedOne:
                changeSpeed(1.0f);
                break;
            case R.id.tv_myVideoEditDetailActivity_speedTwo:
                changeSpeed(2.0f);
                break;
            case R.id.tv_myVideoEditDetailActivity_speedFour:
                changeSpeed(4.0f);
                break;
            case R.id.right_tv_titlee:
                NonDuplicateFactory.proxy(new Perform() {
                    @Override
                    public void perform() {
                        stopPlay();
                        if (mStreamingContext != null) {
                            VideoEditManger.mCurrentTime = mStreamingContext.getTimelineCurrentPosition(mTimeline);
                        }
                        removeTimeline();
                        finish();
                    }
                });

                break;

            case R.id.left_tv_title:
                NonDuplicateFactory.proxy(new Perform() {
                    @Override
                    public void perform() {
                        stopPlay();
                        if (mStreamingContext != null) {
                            VideoEditManger.mCurrentTime = mStreamingContext.getTimelineCurrentPosition(mTimeline);
                        }
                        removeTimeline();
                        finish();
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
        Constants.EDIT_VIDEO_DETAIL_FINISH = false;
        return R.layout.activity_my_video_edit_detail;
    }

    @Override
    public void initData() {
        noStatusBar();
        setStatusBarColor(getResources().getColor(R.color.c050505), 0);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mPlayerLayout.getLayoutParams();
        params.height = PUtil.getScreenH(this) / 2 - DensityUtils.dp2px(this, 60);
        mPlayerLayout.setLayoutParams(params);
        mPresenter = VideoEditDetailPresenter.getInstance(this, this);
        leftIvTitle.setVisibility(View.GONE);
        leftTvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        rightTvTitle.setVisibility(View.VISIBLE);
        rightTvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        leftTvTitle.setText(R.string.video_edit_cancel);
        rightTvTitle.setText(R.string.video_edit_apply);
        rightTvTitle.setTextColor(getResources().getColorStateList(R.color.appThemeColor));
        mSelectedItemPosition = getIntent().getIntExtra("selectedPosition", 0);
        mCurTime = getIntent().getLongExtra("currentTime", 0);
        mVideoCutView.setSelectedItemPosition(mSelectedItemPosition);
        mPresenter.initEditDetail();
        mVideoPlayLayout = new VideoPlayLayout(this);
        mStreamingContext = mVideoPlayLayout.getStreamingContext();
        mTimeline = mVideoPlayLayout.getTimeline();
        mLiveWindow = mVideoPlayLayout.getmLiveWindow();
        mPlayerLayout.addView(mVideoPlayLayout);
        if (mTimeline == null) {
            show(getString(R.string.video_edit_create_error));
            this.finish();
            return;
        }
        mVideoTrack = mTimeline.getVideoTrackByIndex(0);
        if (mVideoTrack == null) {
            return;
        }

        pixelMicrosecond = ScreenUtils.getScreenWidth(this) / mTimeline.getDuration();
        itemEditRecyclerView.setVisibility(View.VISIBLE);
        updateVideoClipFx(mSelectedItemPosition);
        setDefaultColorForSpeedTextView();
        if (mSelectedItemPosition < 0) {
            mSelectedItemPosition = 0;
        }
        showSpeedView(TimelineData.instance().getClipInfoData().get(mSelectedItemPosition).getSpeed());

        initRecycleView();
        showBottomMenuTab(TAB_EDIT_ITEM);
        initSeekBar();
        connectTimelineWithLiveWindow();
        durationTextView.setText("/  " + MusicUtils.formatTimeStrWithUs(mTimeline.getDuration()));
        mVideoCutView.setOnViewFinishInflate(() -> {

        });
        mVideoCutView.setOnVideoCutCallBack(new VideoCutView.OnVideoCutCallBack() {
            @Override
            public void OnVideoPreview(int selectPosition) {
                if (selectPosition < 0 || selectPosition > TimelineData.instance().getClipCount() - 1) {
                    return;
                }
                mSelectedItemPosition = selectPosition;
                play(mVideoTrack.getClipByIndex(selectPosition).getInPoint());
            }

            @Override
            public void onVideoImport(int selectPosition) {
                if (selectPosition < 0) {
                    return;
                }
                stopPlay();
                mSelectedItemPosition = selectPosition;
                Intent intent = new Intent(MyVideoEditDetailActivity.this, AlbumActivity.class);
                intent.putExtra("mSelectClipPosition", selectPosition);
                startActivityForResult(intent, REQUEST_CODE);
            }

            @Override
            public void onVideoCutSelectCallBack(int selectPosition) {
                if (selectPosition < 0 || selectPosition > TimelineData.instance().getClipCount() - 1) {
                    return;
                }
                mSelectedItemPosition = selectPosition;
                updateVideoClipFx(mSelectedItemPosition);
                if (showSeekBar) {
                    showSeekBarMode(CURRENT_SEEK_BAR_MODE, currentSeekBarString);
                }
            }

            @Override
            public void onVideoCutDurationCallBack(long curTime, int selectPosition, boolean isUser) {
                if (selectPosition < 0 || selectPosition > TimelineData.instance().getClipCount() - 1) {
                    return;
                }
                if (TimelineData.instance().getClipInfoData().size() <= selectPosition) {
                    selectPosition = TimelineData.instance().getClipInfoData().size() - 1;
                }
                mSelectedItemPosition = selectPosition;
                showSpeedView(TimelineData.instance().getClipInfoData().get(mSelectedItemPosition).getSpeed());
                mCurTime = curTime;
                if (isUser) {
                    if (mEditSubtitleLayout != null) {
                        mEditSubtitleLayout.setSubtitleTimeLine((float) curTime / VideoEditManger.VIDEO_microsecond_TIME);
                    }
                    seekVideoDelayed(curTime);
                }

            }

            @Override
            public void onVideoStop() {
                mStreamingContext.stop();
            }

            @Override
            public void onDragStart() {
                mIsDraging = true;
            }

            @Override
            public void onDragFinish(boolean delete, int selectedPosition) {
                mIsDraging = false;

                if (delete) {
                    setDurationTextView(0);
                    if (mEditSubtitleLayout != null) {
                        mEditSubtitleLayout.setScaleList((int) (mTimeline.getDuration() / (float) VideoEditManger.VIDEO_microsecond_TIME * 10));
                    }

                }
            }

            @Override
            public void closeFinish() {
                MyVideoEditDetailActivity.this.finish();
                Constants.EDIT_VIDEO_FINISH = true;
            }
        });
        mVideoCutView.setInitVideoData(mTimeline, mSelectedItemPosition, mCurTime, mVideoTrack);
    }

    private void seekVideoDelayed(long time) {
        mSeekHandler.removeMessages(0);
        Message msg = mSeekHandler.obtainMessage();
        msg.what = 0;
        msg.obj = time;
        mSeekHandler.sendMessageDelayed(msg, 20);
    }

    private Handler mSeekHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    seek((long) msg.obj);
                    break;
            }
        }
    };


    private void onScrollingSubtitle(float value) {
        mCurTime = (long) (value * VideoEditManger.VIDEO_microsecond_TIME / 10);
        seek(mCurTime);
        mVideoCutView.scrollThumbToPosition(mCurTime, true);
        setDurationTextView((int) mCurTime);
    }

    private EditSubtitleLayout mEditSubtitleLayout;

    private void showEditSubtitle() {
        if (mEditSubtitleLayout == null) {
            mEditSubtitleLayout = new EditSubtitleLayout(this, mTimeline, mStreamingContext, mPresenter);
            mEditSubtitleLayout.setOnSubTitleCallBack(new EditSubtitleLayout.OnSubTitleCallBack() {
                @Override
                public void subtitleSeek(long time) {
                    seek(time);
                }

                @Override
                public void setSubtitleTimeLine(float time) {
                    mVideoPlayLayout.showRunes(MovieSubtitleTimeline.getHookedMarkList(time * 10), (long) (time * VideoEditManger.VIDEO_microsecond_TIME));
                }

                @Override
                public void onSubtitleAdd(String markId) {
                    VideoSubtitleEditActivity.startVideoSubtitleEditActivity(MyVideoEditDetailActivity.this, markId, RESULT_OK_IN_SUBTITLE);
                }

                @Override
                public void onScrollingSubtitle(float value) {
                    MyVideoEditDetailActivity.this.onScrollingSubtitle(value);
                }
            });
            subtitleLinearLayout.removeAllViews();
            subtitleLinearLayout.addView(mEditSubtitleLayout);
        }
        subtitleLinearLayout.setVisibility(View.VISIBLE);
        mEditSubtitleLayout.setSubtitleTimeLine((float) getCurPlayTime() / VideoEditManger.VIDEO_microsecond_TIME);

    }

    public boolean isIsAddSubtitle() {
        if (mEditSubtitleLayout != null) {
            return mEditSubtitleLayout.isIsAddSubtitle();
        }
        return false;
    }

    public boolean isIsLongPress() {
        if (mEditSubtitleLayout != null) {
            return mEditSubtitleLayout.isIsLongPress();
        }
        return false;
    }

    private long getCurPlayTime() {
        return mStreamingContext.getTimelineCurrentPosition(mTimeline);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        stopPlay();
        intent.putExtra(AlbumActivity.KEY_OF_REQUEST_CODE, requestCode);
        super.startActivityForResult(intent, requestCode);
    }

    private void seek(long time) {
        mStreamingContext.seekTimeline(mTimeline, time, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
        setDurationTextView(time);

    }

    private void play() {
        play(0);
    }

    private void play(long time) {
        play(time, mTimeline.getDuration());
    }

    private void play(long time, long endTime) {
        mStreamingContext.playbackTimeline(mTimeline, time, endTime, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, true, NvsStreamingContext.STREAMING_ENGINE_PLAYBACK_FLAG_BUDDY_HOST_VIDEO_FRAME);

    }

    EditHandler mEditHandler;

    /**
     * 开始生成倒放视频
     */
    public void reverseVideo() {
        stopPlay();
        RecordClipsInfo recordClipsInfo = new RecordClipsInfo();
        recordClipsInfo.addClip(TimelineData.instance().getClipInfoData().get(mSelectedItemPosition));
        if (mEditHandler == null) {
            mEditHandler = new EditHandler(this);
        } else {
            mEditHandler.removeCallbacksAndMessages(null);
        }

        mConvertFiles = new ConvertFiles(mSelectedItemPosition, recordClipsInfo, FileUtils.getProjectPath(), mEditHandler, MESSAGE_ALL_VIDEO_CONVERT_FINISHED);
        mConvertFiles.sendConvertFileMsg();
    }

    /**
     * 更改视频倍速播放
     *
     * @param newSpeed 0.125--0.25--0.5--1.0--2.0--4.0
     */
    private void changeSpeed(float newSpeed) {
        showSpeedView(newSpeed);

        NvsVideoClip videoClip = mVideoTrack.getClipByIndex(mSelectedItemPosition);
        if (videoClip != null) {
            LoggerUtils.printLog("speed=" + newSpeed);
            videoClip.changeSpeed(newSpeed);
            TimelineData.instance().getClipInfoData().get(mSelectedItemPosition).setSpeed(newSpeed);
            //当前片段是图片
        }
        seek(mVideoTrack.getClipByIndex(mSelectedItemPosition).getInPoint());
        mVideoCutView.setInitVideoData(mTimeline, mSelectedItemPosition, mVideoTrack.getClipByIndex(mSelectedItemPosition).getInPoint(), mVideoTrack);
        if (TimelineData.init().getMusicData() != null && TimelineData.init().getMusicData().size() > 0) {
            TimelineData.init().getMusicData().get(0).setTrimOut(mTimeline.getDuration());
            TimelineUtil.buildTimelineMusic(mTimeline, TimelineData.init().getMusicData());
        }
        if (mEditSubtitleLayout != null) {
            mEditSubtitleLayout.setScaleList((int) (mTimeline.getDuration() / (float) VideoEditManger.VIDEO_microsecond_TIME * 10));
        }
    }

    public void setDurationTextView(long curTime) {
        if (currentPlayPositionTextView == null) {
            return;
        }
        currentPlayPositionTextView.setText(MusicUtils.formatTimeStrWithUs(curTime));
        if (mTimeline != null) {
            durationTextView.setText("/  " + MusicUtils.formatTimeStrWithUs(mTimeline.getDuration()));
        }
    }

    /**
     * 显示对应的倍速的view
     *
     * @param newSpeed
     */
    private void showSpeedView(float newSpeed) {
        if (mCurrentSpeed == newSpeed) {
            return;
        }
        setDefaultColorForSpeedTextView();

        mCurrentSpeed = newSpeed;
        if (newSpeed == 0.25) {
            mSpeedMode = SPEED_MODE_QUARTER;
        } else if (newSpeed == 0.5) {
            mSpeedMode = SPEED_MODE_HALF;
        } else if (newSpeed == 1.0) {
            mSpeedMode = SPEED_MODE_ONE;
        } else if (newSpeed == 2.0) {
            mSpeedMode = SPEED_MODE_TWO;
        } else if (newSpeed == 4.0) {
            mSpeedMode = SPEED_MODE_FOUR;
        }

        switch (mSpeedMode) {
            case SPEED_MODE_QUARTER:
                speedQuarterTextView.setTextColor(getResources().getColor(R.color.selectTextViewColor));
                speedQuarterTextView.setBackgroundResource(R.drawable.circle_corner_red_border_bg);
                break;
            case SPEED_MODE_HALF:
                speedHalfTextView.setTextColor(getResources().getColor(R.color.selectTextViewColor));
                speedHalfTextView.setBackgroundResource(R.drawable.circle_corner_red_border_bg);
                break;
            case SPEED_MODE_ONE:
                speedOneTextView.setTextColor(getResources().getColor(R.color.selectTextViewColor));
                speedOneTextView.setBackgroundResource(R.drawable.circle_corner_red_border_bg);
                break;
            case SPEED_MODE_TWO:
                speedTwoTextView.setTextColor(getResources().getColor(R.color.selectTextViewColor));
                speedTwoTextView.setBackgroundResource(R.drawable.circle_corner_red_border_bg);
                break;
            case SPEED_MODE_FOUR:
                speedFourTextView.setTextColor(getResources().getColor(R.color.selectTextViewColor));
                speedFourTextView.setBackgroundResource(R.drawable.circle_corner_red_border_bg);
                break;
        }
    }

    private void setDefaultColorForSpeedTextView() {
        speedQuarterTextView.setTextColor(Color.WHITE);
        speedQuarterTextView.setBackgroundResource(R.drawable.circle_corner_gray_border_normal_2dp);
        speedHalfTextView.setTextColor(Color.WHITE);
        speedHalfTextView.setBackgroundResource(R.drawable.circle_corner_gray_border_normal_2dp);
        speedOneTextView.setTextColor(Color.WHITE);
        speedOneTextView.setBackgroundResource(R.drawable.circle_corner_gray_border_normal_2dp);
        speedTwoTextView.setTextColor(Color.WHITE);
        speedTwoTextView.setBackgroundResource(R.drawable.circle_corner_gray_border_normal_2dp);
        speedFourTextView.setTextColor(Color.WHITE);
        speedFourTextView.setBackgroundResource(R.drawable.circle_corner_gray_border_normal_2dp);
    }


    public static class EditHandler extends Handler {
        WeakReference<MyVideoEditDetailActivity> mWeakReference;

        public EditHandler(MyVideoEditDetailActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final MyVideoEditDetailActivity activity = mWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case MESSAGE_ALL_VIDEO_CONVERT_FINISHED_DISS:
                        if (activity.downProgressDialog != null) {
                            activity.downProgressDialog.dismissDialog();
                        }
                        break;
                    case MESSAGE_ALL_VIDEO_CONVERT_FINISHED:
                        if (activity.mTimeline == null || activity.mVideoCutView == null) {
                            return;
                        }
                        ClipInfo clipInfo = TimelineData.instance().getClipInfoData().get(activity.mSelectedItemPosition);
                        clipInfo.setIsReverseConvert(true);
                        clipInfo.setFileConvertPath((String) msg.obj);
                        VideoEditManger.reConvertTrimInAndTrimOut(clipInfo);
                        TimelineUtil.reBuildVideoTrack(activity.mTimeline);
                        activity.mVideoCutView.setInitVideoData(activity.mTimeline, activity.mSelectedItemPosition, activity.mCurTime, activity.mVideoTrack);
                        if (activity.downProgressDialog != null) {
                            activity.downProgressDialog.setProgress(activity.getString(R.string.video_edit_reverse));
                            sendEmptyMessageDelayed(MESSAGE_ALL_VIDEO_CONVERT_FINISHED_DISS, 1000);
                        }

                        activity.updateVideoClipFx(activity.mSelectedItemPosition);
                        break;
                    case ConvertFiles.mProgressCode:
                        if (activity.downProgressDialog != null) {
                            activity.downProgressDialog.setProgress(activity.getString(R.string.video_edit_reverse_ing) + msg.arg1 + "%");
                        }
                        activity.mConvertFiles.onConvertDestroy();
                        break;
                    case ConvertFiles.MESSAGE_CONVERT_Error:
                        if (activity.downProgressDialog != null) {
                            activity.downProgressDialog.setProgress(activity.getString(R.string.video_edit_reverse_error));
                            sendEmptyMessageDelayed(MESSAGE_ALL_VIDEO_CONVERT_FINISHED_DISS, 1000);
                        }
                        activity.show(activity.getString(R.string.video_edit_reverse_error));
                        activity.mConvertFiles.sendCancelConvertMsg();
                        activity.mConvertFiles.sendFinishConvertMsg();
                        break;
                }

            }
        }
    }


    private void setTabDefaultBgColor() {

        mEditItemImage.setImageResource(R.mipmap.video_edit_item_normal);
        mEditBeautyImage.setImageResource(R.mipmap.video_edit_beauty_normal);
        mEditCaptionImage.setImageResource(R.mipmap.video_edit_caption_detail_normal);

        mEditItemText.setTextColor(this.getResources().getColorStateList(R.color.ffffff66));
        mEditBeautyText.setTextColor(this.getResources().getColorStateList(R.color.ffffff66));
        mEditCaptionText.setTextColor(this.getResources().getColorStateList(R.color.ffffff66));
    }

    private void updateVideoVoiceSeekBar(float volumeVal) {
        beautySeekBar.setProgress((int) (volumeVal + 0.5f));
        seekBarValueTextView.setText(String.valueOf((int) (volumeVal + 0.5f)) + "%");
    }

    private void updateVideoVoiceSeekBarOther(float volumeVal) {
        Log.i("club", "club——VoiceSeekBar：" + volumeVal);
        beautySeekBar.setProgress((int) (volumeVal + 0.5f));
        seekBarValueTextView.setText(FormatUtils.percentageToOther(volumeVal));
    }

    /**
     * 美型SeekBar计算公式
     * 美型取值范围 -1--1， 对应seekBar为0--100
     *
     * @param volumeVal
     * @return
     */
    private float calculateBeautySeekBarValue(float volumeVal) {
        return (volumeVal + 1) * 50;
    }

    /**
     * 美型计算公式
     *
     * @param progress
     * @return
     */
    private float calculateBeautyFloatValue(float progress) {
        return progress / 50f - 1;
    }

    private void initRecycleView() {
        CenterLayoutManager itemEditLayoutManager = new CenterLayoutManager(this, RecyclerView.HORIZONTAL, false);
        itemEditRecyclerView.setLayoutManager(itemEditLayoutManager);
        itemAdapter = new VideoMenuAdapter(this);
        itemEditRecyclerView.setAdapter(itemAdapter);
        itemAdapter.setOnRecyclerItemClickListener(new BaseRecyclerAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, final int pos) {
                NonDuplicateFactory.proxy(new Perform() {
                    @Override
                    public void perform() {
                        itemEditLayoutManager.smoothScrollToPosition(itemEditRecyclerView, new RecyclerView.State(), pos);
                        int position = pos;
                        if (pos != 4) {
                            changeSpeedLinearLayout.setVisibility(View.GONE);
                        }
                        if (pos != 0) {
                            seekBarLinearLayout.setVisibility(View.GONE);
                        }
                        showSeekBar = false;
                        switch (pos) {
                            case 0://音量
                                if (seekBarLinearLayout.getVisibility() == View.VISIBLE) {
                                    position = -1;
                                    showSeekBar = false;
                                    seekBarLinearLayout.setVisibility(View.GONE);

                                } else {
                                    showSeekBar = true;
                                    showSeekBarMode(SEEK_BAR_MODE_VOICE, getString(R.string.video_edit_vlomue));
                                }

                                break;
                            case 1://调节
                                itemEditRecyclerView.setVisibility(View.GONE);
                                seekBarLinearLayout.setVisibility(View.VISIBLE);
                                adjustLinearLayout.setVisibility(View.VISIBLE);
                                selectItemPosition(0, adjustAdapter);
                                showSeekBar = true;
                                showSeekBarMode(SEEK_BAR_MODE_ADJUST_ANJIAO, getString(R.string.video_edit_detail_an_jiao));
                                break;
                            case 2://分割
                                //只有视频做分割，图片不做分割
                                boolean split = VideoEditManger.setVideoSplit(mSelectedItemPosition, mCurTime, mTimeline, mVideoTrack);
                                if (split) {
                                    NvsVideoClip clip = mVideoTrack.getClipByTimelinePosition(getCurPlayTime());
                                    if (clip != null) {
                                        mSelectedItemPosition = clip.getIndex();
                                    }
                                    updateVideoClipFx(mSelectedItemPosition);
                                    mVideoCutView.setInitVideoData(mTimeline, mSelectedItemPosition, getCurPlayTime(), mVideoTrack);
                                    durationTextView.setText("/  " + MusicUtils.formatTimeStrWithUs(mTimeline.getDuration()));
                                } else {
                                    ToastUtil.showToast(MyVideoEditDetailActivity.this.getApplicationContext(), getString(R.string.video_edit_detail_split_min_time_tip));
                                }
                                break;
                            case 3://复制
                                if (!VideoImportCheck.getInstance().checkVideoClipCount()) {
                                    return;
                                }
                                VideoEditManger.setVideoCopy(mSelectedItemPosition, mTimeline);
                                mVideoCutView.setInitVideoData(mTimeline, mSelectedItemPosition, getCurPlayTime(), mVideoTrack);


                                NvsVideoClip videoClip = mVideoTrack.getClipByIndex(mSelectedItemPosition + 1);

                                if (videoClip != null) {
                                    seek(videoClip.getInPoint());
                                    mVideoCutView.setInitVideoData(mTimeline, mSelectedItemPosition + 1, videoClip.getInPoint(), mVideoTrack);
                                } else {
                                    mVideoCutView.setVideoData(mTimeline, mVideoTrack);
                                }
                                if (mEditSubtitleLayout != null) {
                                    mEditSubtitleLayout.setScaleList((int) (mTimeline.getDuration() / (float) VideoEditManger.VIDEO_microsecond_TIME * 10));
                                }
//                        changeSpeed(mCurrentSpeed);
                                durationTextView.setText("/  " + MusicUtils.formatTimeStrWithUs(mTimeline.getDuration()));
                                if (videoClip != null) {
                                    mSelectedItemPosition = videoClip.getIndex();
                                }
                                updateVideoClipFx(mSelectedItemPosition);
                                break;
                            case 4://变速
                                if (changeSpeedLinearLayout.getVisibility() == View.VISIBLE) {
                                    position = -1;
                                }
                                changeSpeedLinearLayout.setVisibility(changeSpeedLinearLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                                break;
                            case 5://倒放
                                ClipInfo clipInfo = TimelineData.instance().getClipInfoData().get(mSelectedItemPosition);
                                if (clipInfo.getDuration() > 0) {
                                    if (clipInfo.isIsReverseConvert()) {
                                        clipInfo.setIsReverseConvert(false);
                                        VideoEditManger.reConvertTrimInAndTrimOut(clipInfo);
                                        TimelineUtil.reBuildVideoTrack(mTimeline);
                                        mVideoCutView.setInitVideoData(mTimeline, mSelectedItemPosition, getCurPlayTime(), mVideoTrack);
                                        seek(getCurPlayTime());
                                        updateVideoClipFx(mSelectedItemPosition);
                                    } else {

                                        if (!TextUtils.isEmpty(clipInfo.getFileConvertPath()) && new File(clipInfo.getFileConvertPath()).exists()) {
                                            clipInfo.setIsReverseConvert(true);
                                            VideoEditManger.reConvertTrimInAndTrimOut(clipInfo);
                                            TimelineUtil.reBuildVideoTrack(mTimeline);
                                            mVideoCutView.setInitVideoData(mTimeline, mSelectedItemPosition, getCurPlayTime(), mVideoTrack);
                                            seek(getCurPlayTime());
                                            updateVideoClipFx(mSelectedItemPosition);
                                        } else {
                                            if (mStreamingContext != null) {
                                                mStreamingContext.stop();
                                            }
                                            reverseVideo();
                                            if (downProgressDialog == null) {
                                                downProgressDialog = new CommonDownloadDialog(new WeakReference<>(MyVideoEditDetailActivity.this).get());
                                            }
                                            downProgressDialog.setCancelable(false);
                                            downProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                @Override
                                                public void onDismiss(DialogInterface dialogInterface) {
                                                    if (mConvertFiles != null) {
                                                        mConvertFiles.sendCancelConvertMsg();
                                                    }
                                                }
                                            });
                                            downProgressDialog.showDialog(false);
                                            if (downProgressDialog != null) {
                                                downProgressDialog.setProgress(getString(R.string.video_edit_reverse_ing) + "0%");
                                            }
                                        }

                                        return;
                                    }
                                } else {
                                    ToastUtil.showToast(MyVideoEditDetailActivity.this.getApplicationContext(), getString(R.string.video_edit_reverse_picture_tip));
                                }

                                break;
                        }
                        mEditItemPosition = position;
                    }
                });

//                mStreamingContext.playbackTimeline(mTimeline, 0, mTimeline.getDuration(), NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, true, NvsStreamingContext.STREAMING_ENGINE_PLAYBACK_FLAG_BUDDY_HOST_VIDEO_FRAME);
            }
        });
        itemAdapter.updateData(true, mPresenter.getEditSetMenu());

        CenterLayoutManager adjustLinearLayoutManager = new CenterLayoutManager(this, RecyclerView.HORIZONTAL, false);
        adjustRecyclerView.setLayoutManager(adjustLinearLayoutManager);
        adjustAdapter = new VideoMenuAdapter(this);
        adjustRecyclerView.setAdapter(adjustAdapter);
        adjustAdapter.setOnRecyclerItemClickListener(new BaseRecyclerAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                NonDuplicateFactory.proxy(new Perform() {
                    @Override
                    public void perform() {
                        adjustLinearLayoutManager.smoothScrollToPosition(adjustRecyclerView, new RecyclerView.State(), pos);
                        selectItemPosition(pos, adjustAdapter);
                        switch (pos) {
                            case 0:
                                showSeekBarMode(SEEK_BAR_MODE_ADJUST_ANJIAO, adjustAdapter.mData.get(pos).getName());
                                break;
                            case 1:
                                showSeekBarMode(SEEK_BAR_MODE_ADJUST_BAOGUANG, adjustAdapter.mData.get(pos).getName());
                                break;
                            case 2:
                                showSeekBarMode(SEEK_BAR_MODE_ADJUST_BAOHEDU, adjustAdapter.mData.get(pos).getName());
                                break;
                            case 3:
                                showSeekBarMode(SEEK_BAR_MODE_ADJUST_DUIBIDU, adjustAdapter.mData.get(pos).getName());
                                break;
                            case 4:
                                showSeekBarMode(SEEK_BAR_MODE_ADJUST_RUIDU, adjustAdapter.mData.get(pos).getName());
                                break;
                        }
                        mStreamingContext.playbackTimeline(mTimeline, mStreamingContext.getTimelineCurrentPosition(mTimeline), mTimeline.getDuration(), NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, true, NvsStreamingContext.STREAMING_ENGINE_PLAYBACK_FLAG_BUDDY_HOST_VIDEO_FRAME);

                    }
                });
            }
        });
        adjustAdapter.updateData(true, mPresenter.getEditSetAdjust());

        CenterLayoutManager beautyLayoutManager = new CenterLayoutManager(this, RecyclerView.HORIZONTAL, false);
        beautyRecyclerView.setLayoutManager(beautyLayoutManager);
        beautyAdapter = new BeautyAdapter(this);
        beautyRecyclerView.setAdapter(beautyAdapter);
        beautyAdapter.setOnRecyclerItemClickListener(new BaseRecyclerAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                NonDuplicateFactory.proxy(new Perform() {
                    @Override
                    public void perform() {
                        beautyLayoutManager.smoothScrollToPosition(beautyRecyclerView, new RecyclerView.State(), pos);
                        selectItemPosition(pos, beautyAdapter);
                        switch (pos) {
                            case 0:
                                showSeekBarMode(SEEK_BAR_MODE_BEAUTY_RED, beautyAdapter.mData.get(pos).getName());
                                break;
                            case 1:
                                showSeekBarMode(SEEK_BAR_MODE_BEAUTY_MOPI, beautyAdapter.mData.get(pos).getName());
                                break;
                            case 2:
                                showSeekBarMode(SEEK_BAR_MODE_BEAUTY_WHITE, beautyAdapter.mData.get(pos).getName());
                                break;
                            case 3:
                                showSeekBarMode(SEEK_BAR_MODE_BEAUTY_THINFACE, beautyAdapter.mData.get(pos).getName());
                                break;
                            case 4:
                                showSeekBarMode(SEEK_BAR_MODE_BEAUTY_BIGEYE, beautyAdapter.mData.get(pos).getName());
                                break;
                            case 5:
                                showSeekBarMode(SEEK_BAR_MODE_BEAUTY_XIABA, beautyAdapter.mData.get(pos).getName());
                                break;
                            case 6:
                                showSeekBarMode(SEEK_BAR_MODE_BEAUTY_NOSE, beautyAdapter.mData.get(pos).getName());
                                break;
                            case 7:
                                showSeekBarMode(SEEK_BAR_MODE_BEAUTY_HEAD, beautyAdapter.mData.get(pos).getName());
                                break;
                            case 8:
                                showSeekBarMode(SEEK_BAR_MODE_BEAUTY_MOUTH_SIZE, beautyAdapter.mData.get(pos).getName());
                                break;
                            case 9:
                                showSeekBarMode(SEEK_BAR_MODE_BEAUTY_SMALL_FACE, beautyAdapter.mData.get(pos).getName());
                                break;
                            case 10:
                                showSeekBarMode(SEEK_BAR_MODE_BEAUTY_EYE_CORNER, beautyAdapter.mData.get(pos).getName());
                                break;
                            case 11:
                                showSeekBarMode(SEEK_BAR_MODE_BEAUTY_FACE_WIDTH, beautyAdapter.mData.get(pos).getName());
                                break;
                            case 12:
                                showSeekBarMode(SEEK_BAR_MODE_BEAUTY_NOSE_LENGTH, beautyAdapter.mData.get(pos).getName());
                                break;
                            case 13:
                                showSeekBarMode(SEEK_BAR_MODE_BEAUTY_MOUTH_CORNER, beautyAdapter.mData.get(pos).getName());
                                break;
                        }
//                        mStreamingContext.playbackTimeline(mTimeline, mStreamingContext.getTimelineCurrentPosition(mTimeline), mTimeline.getDuration(), NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, true, NvsStreamingContext.STREAMING_ENGINE_PLAYBACK_FLAG_BUDDY_HOST_VIDEO_FRAME);

                    }
                });
            }
        });
        beautyAdapter.updateData(true, mPresenter.getEditSetBeauty());

    }

    private void selectItemPosition(int pos, VideoMenuAdapter adjustAdapter) {
        setSelectedItemPosition(pos, adjustAdapter.mData);

        adjustAdapter.notifyDataSetChanged();
    }

    private void selectItemPosition(int pos, BeautyAdapter adjustAdapter) {
        setSelectedItemPosition(pos, adjustAdapter.mData);
        adjustAdapter.notifyDataSetChanged();
    }

    private void setSelectedItemPosition(int pos, List<MusicBean> list) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setSelected(false);
        }
        if (pos != -1) {
            list.get(pos).setSelected(true);
        }
    }


    /**
     * 显示相应的seekbar 视图
     *
     * @param seekBarMode
     */
    private void showSeekBarMode(final int seekBarMode, String text) {
        CURRENT_SEEK_BAR_MODE = seekBarMode;
        currentSeekBarString = text;
        seekBarKeyTextView.setText(text);
        seekBarLinearLayout.setVisibility(View.VISIBLE);
        beautySeekBar.setMax(VIDEOVOLUME_MAXSEEKBAR_VALUE);
        float volumeVal;
        switch (seekBarMode) {
            case SEEK_BAR_MODE_VOICE://音量
                NvsVideoClip videoClip=mVideoTrack.getClipByIndex(mSelectedItemPosition);
                if(videoClip!=null){
                    volumeVal = (float) Math.floor(videoClip.getVolumeGain().leftVolume / VIDEOVOLUME_MAXVOLUMEVALUE * VIDEOVOLUME_MAXSEEKBAR_VALUE + 0.5D);
                    updateVideoVoiceSeekBar(volumeVal);
                }
                break;
            case SEEK_BAR_MODE_ADJUST_ANJIAO://暗角
                volumeVal = (float) (mVignetteVideoFx.getFloatVal(Constants.FX_VIGNETTE_DEGREE) * 100);
                updateVideoVoiceSeekBar(volumeVal);
                break;
            case SEEK_BAR_MODE_ADJUST_RUIDU://锐度
                volumeVal = (float) (mSharpenVideoFx.getFloatVal(Constants.FX_SHARPEN_AMOUNT) / 5 * 100);
                updateVideoVoiceSeekBar((volumeVal));
                break;
            case SEEK_BAR_MODE_ADJUST_BAOGUANG://曝光
                volumeVal = getCurProgress((float) mColorVideoFx.getFloatVal(Constants.FX_COLOR_PROPERTY_BRIGHTNESS));
                updateVideoVoiceSeekBarOther(volumeVal);
                break;
            case SEEK_BAR_MODE_ADJUST_DUIBIDU://对比度
                volumeVal = getCurProgress((float) mColorVideoFx.getFloatVal(Constants.FX_COLOR_PROPERTY_CONTRAST));
                updateVideoVoiceSeekBarOther(volumeVal);
                break;
            case SEEK_BAR_MODE_ADJUST_BAOHEDU://饱和度
                volumeVal = getCurProgress((float) mColorVideoFx.getFloatVal(Constants.FX_COLOR_PROPERTY_SATURATION));
                updateVideoVoiceSeekBarOther(volumeVal);
                break;
            case SEEK_BAR_MODE_BEAUTY_MOPI://磨皮
                volumeVal = (float) mMopiVideoFx.getFloatVal(Constants.FX_BEAUTY_MOPI) * 100f;
                updateVideoVoiceSeekBar(volumeVal);
                break;
            case SEEK_BAR_MODE_BEAUTY_WHITE://美白
                volumeVal = (float) mMopiVideoFx.getFloatVal(Constants.FX_BEAUTY_WHITE) * 100f;
                updateVideoVoiceSeekBar(volumeVal);
                break;
            case SEEK_BAR_MODE_BEAUTY_RED://红润
                volumeVal = (float) mMopiVideoFx.getFloatVal(Constants.FX_BEAUTY_RED) * 100f;
                updateVideoVoiceSeekBar(volumeVal);
                break;
            case SEEK_BAR_MODE_BEAUTY_THINFACE://瘦脸
                volumeVal = (float) mMopiVideoFx.getFloatVal(Constants.FX_BEAUTY_THIN_FACE);
                updateVideoVoiceSeekBar(calculateBeautySeekBarValue(volumeVal));
                break;
            case SEEK_BAR_MODE_BEAUTY_BIGEYE://大眼
                volumeVal = (float) mMopiVideoFx.getFloatVal(Constants.FX_BEAUTY_BIG_EYE);
                updateVideoVoiceSeekBar(calculateBeautySeekBarValue(volumeVal));
                break;
            case SEEK_BAR_MODE_BEAUTY_XIABA://下巴
                volumeVal = (float) mMopiVideoFx.getFloatVal(Constants.FX_BEAUTY_XIABA);
                updateVideoVoiceSeekBar(calculateBeautySeekBarValue(volumeVal));
                break;
            case SEEK_BAR_MODE_BEAUTY_NOSE://鼻子
                volumeVal = (float) mMopiVideoFx.getFloatVal(Constants.FX_BEAUTY_NOSE);
                updateVideoVoiceSeekBar(calculateBeautySeekBarValue(volumeVal));
                break;
            case SEEK_BAR_MODE_BEAUTY_HEAD://额头
                volumeVal = (float) mMopiVideoFx.getFloatVal(Constants.FX_BEAUTY_HEAD);
                updateVideoVoiceSeekBar(calculateBeautySeekBarValue(volumeVal));
                break;
            case SEEK_BAR_MODE_BEAUTY_MOUTH_SIZE://嘴部
                volumeVal = (float) mMopiVideoFx.getFloatVal(Constants.FX_BEAUTY_MOUTH_SIZE);
                updateVideoVoiceSeekBar(calculateBeautySeekBarValue(volumeVal));
                break;
            case SEEK_BAR_MODE_BEAUTY_SMALL_FACE://小脸
                volumeVal = (float) mMopiVideoFx.getFloatVal(Constants.FX_BEAUTY_SMALL_FACE);
                updateVideoVoiceSeekBar(calculateBeautySeekBarValue(volumeVal));
                break;
            case SEEK_BAR_MODE_BEAUTY_EYE_CORNER://眼角
                volumeVal = (float) mMopiVideoFx.getFloatVal(Constants.FX_BEAUTY_EYE_CORNER);
                updateVideoVoiceSeekBar(calculateBeautySeekBarValue(volumeVal));
                break;
            case SEEK_BAR_MODE_BEAUTY_FACE_WIDTH://窄脸
                volumeVal = (float) mMopiVideoFx.getFloatVal(Constants.FX_BEAUTY_FACE_WIDTH);
                updateVideoVoiceSeekBar(calculateBeautySeekBarValue(volumeVal));
                break;
            case SEEK_BAR_MODE_BEAUTY_NOSE_LENGTH://长鼻
                volumeVal = (float) mMopiVideoFx.getFloatVal(Constants.FX_BEAUTY_NOSE_LENGTH);
                updateVideoVoiceSeekBar(calculateBeautySeekBarValue(volumeVal));
                break;
            case SEEK_BAR_MODE_BEAUTY_MOUTH_CORNER://嘴角
                volumeVal = (float) mMopiVideoFx.getFloatVal(Constants.FX_BEAUTY_MOUTH_CORNER);
                updateVideoVoiceSeekBar(calculateBeautySeekBarValue(volumeVal));
                break;
        }
    }


    private float getFloatColorVal(int progress) {
        float val = progress < 50 ? progress / 50.f : progress * 9 / 50.0f - 8;
        Log.i("club", "club:FloatColorVal:" + val);
        return val;
    }

    private int getCurProgress(float colVal) {
        /*0-1 和 1-50  中间值是1*/
        int curProgress = 50;
        if (colVal < 0) {
            return curProgress;
        }
        if (colVal <= 1) {
            curProgress = (int) (colVal * 50);
        } else {
            curProgress = 50 + (int) (colVal * 5);
        }
        return curProgress;
    }

    /**
     * 更新指定位置item的画面调整参数
     *
     * @param itemPosition
     */
    private void updateVideoClipFx(int itemPosition) {
        Log.i("club", "club_updateVideoClipFx:" + itemPosition);
        NvsVideoClip videoClip = mVideoTrack.getClipByIndex(itemPosition);
        if (videoClip == null) {
            return;
        }
        NvsVideoFx colorVideoFx = null, vignetteVideoFx = null, sharpenVideoFx = null, mopiVideoFx = null;
        int fxCount = videoClip.getFxCount();
        for (int index = 0; index < fxCount; ++index) {
            NvsVideoFx videoFx = videoClip.getFxByIndex(index);
            if (videoFx == null) {
                continue;
            }
            String fxName = videoFx.getBuiltinVideoFxName();
            if (fxName == null || TextUtils.isEmpty(fxName)) {
                continue;
            }
            if (fxName.equals(Constants.FX_COLOR_PROPERTY)) {
                colorVideoFx = videoFx;
            } else if (fxName.equals(Constants.FX_VIGNETTE)) {
                vignetteVideoFx = videoFx;
            } else if (fxName.equals(Constants.FX_SHARPEN)) {
                sharpenVideoFx = videoFx;
            } else if (fxName.equals(Constants.AR_SCENE)) {
                mopiVideoFx = videoFx;
                mopiVideoFx.setBooleanVal("Beauty Effect", true);
                mopiVideoFx.setBooleanVal("Beauty Shape", true);
//                mopiVideoFx.setBooleanVal("Single Buffer Mode", false);
            }
        }

        if (mopiVideoFx == null) {
            mopiVideoFx = videoClip.appendBuiltinFx(Constants.AR_SCENE);
            mopiVideoFx.setBooleanVal("Beauty Effect", true);
            mopiVideoFx.setBooleanVal("Beauty Shape", true);
//            mopiVideoFx.setBooleanVal("Single Buffer Mode", false);
            //切记： 滤镜特效一定要加在美颜美型的后边
            TimelineUtil.buildTimelineFilter(mTimeline, TimelineData.instance().getVideoClipFxData());
        }
        if (colorVideoFx == null) {
            colorVideoFx = videoClip.appendBuiltinFx(Constants.FX_COLOR_PROPERTY);
        }
        if (vignetteVideoFx == null) {
            vignetteVideoFx = videoClip.appendBuiltinFx(Constants.FX_VIGNETTE);
        }
        if (sharpenVideoFx == null) {
            sharpenVideoFx = videoClip.appendBuiltinFx(Constants.FX_SHARPEN);
        }

        mColorVideoFx = colorVideoFx;
        mVignetteVideoFx = vignetteVideoFx;
        mSharpenVideoFx = sharpenVideoFx;
        mMopiVideoFx = mopiVideoFx;
    }

    UsbDispose mUsbDispose;

    public void setConnectedUSBError() {
        ConnectionManager.getInstance().setErrorI(new MoErrorCallback() {
            @Override
            public void onError(MoErrorData data) {
                if (mUsbDispose != null) {
                    mUsbDispose.syncLE(data.event);
                }

            }
        });
    }

    @Override
    public void connectedUSB() {
        super.connectedUSB();
        mIsDisconnectedUSB = true;
        show(getString(R.string.connectCameraSucc));
        mUsbDispose = new UsbDispose();
        mUsbDispose.dispose(new UsbDispose.SyncStatus() {
            @Override
            public void onSyncSucc() {

            }

            @Override
            public void onSyncFailed() {

            }

            @Override
            public void onSyncStart() {

            }
        });
    }

    private boolean mIsDisconnectedUSB = true;

    @Override
    public void disconnectedUSB() {
        super.disconnectedUSB();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mIsDisconnectedUSB) {
                    mIsDisconnectedUSB = false;
                    show(getString(R.string.disconenct_usb));
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (VideoEditManger.checkVideoLose(mTimeline)) {
            if (TimelineData.instance().getClipCount() <= 0) {
                finish();
                Constants.EDIT_VIDEO_FINISH = true;
                return;
            } else {
                mSelectedItemPosition = 0;
                if (mVideoCutView != null) {
                    mVideoCutView.initData(mSelectedItemPosition);
                }
                seek(0);
                setDurationTextView(mVideoTrack.getDuration());

            }
        }
        if (mStreamingContext != null) {
            seek(mStreamingContext.getTimelineCurrentPosition(mTimeline));
            currentPlayPositionTextView.setText(MusicUtils.formatTimeStrWithUs(mStreamingContext.getTimelineCurrentPosition(mTimeline)));
        }
        setConnectedUSBError();
    }

    @Override
    public void onPause() {
        super.onPause();
        mSeekHandler.removeMessages(0);
        stopPlay();
    }

    private void stopPlay() {
        if (mStreamingContext == null) {
            ToastUtil.showToast(MyVideoEditDetailActivity.this.getApplicationContext(), getString(R.string.video_edit_error));
            finish();
            return;
        }
        if (getStreamingEngineState() == NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
            mStreamingContext.stop();
        }
    }

    private int getStreamingEngineState() {
        if (mStreamingContext == null) {
            ToastUtil.showToast(MyVideoEditDetailActivity.this.getApplicationContext(), getString(R.string.video_edit_error));
            finish();
            return 0;
        }
        return mStreamingContext.getStreamingEngineState();
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
                if (mSelectedItemPosition == mVideoTrack.getClipCount() - 1 && !isIsAddSubtitle()) {
                    currentPlayPositionTextView.setText(MusicUtils.formatTimeStrWithUs(0));
                    seek(0);
                    mVideoCutView.setInitVideoData(mTimeline, 0, 0, mVideoTrack);
                }

                LoggerUtils.d("club Streaming+onPlaybackEOF " + mSelectedItemPosition + ":" + mVideoTrack.getClipCount());

            }
        });

        mStreamingContext.setPlaybackCallback2(new NvsStreamingContext.PlaybackCallback2() {
            @Override
            public void onPlaybackTimelinePosition(NvsTimeline nvsTimeline, long cur_position) {
                if (mEditSubtitleLayout != null) {
                    mEditSubtitleLayout.setSubtitleTimeLine((float) cur_position / VideoEditManger.VIDEO_microsecond_TIME);
                }
                currentPlayPositionTextView.setText(MusicUtils.formatTimeStrWithUs(cur_position));
                mVideoCutView.scrollThumbToPosition(cur_position, true);
            }
        });

        mStreamingContext.setStreamingEngineCallback(new NvsStreamingContext.StreamingEngineCallback() {
            @Override
            public void onStreamingEngineStateChanged(int i) {
                if (i == NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                    pauseImageView.setBackgroundResource(R.mipmap.play);
                    mIsPlayIng = true;
                } else {
                    pauseImageView.setBackgroundResource(R.mipmap.pause);
                    mIsPlayIng = false;
                }
                LoggerUtils.d("club StreamingEngineStateChanged:i=" + i);
//                if (i == NvsStreamingContext.S){
//
//                }
            }

            @Override
            public void onFirstVideoFramePresented(NvsTimeline nvsTimeline) {
            }
        });

        mStreamingContext.connectTimelineWithLiveWindow(mTimeline, mLiveWindow);
    }


    private void showBottomMenuTab(int tab) {
        hideAllVideoEditView();

        setTabDefaultBgColor();
        switch (tab) {
            case TAB_EDIT_ITEM:
                mEditItemText.setTextColor(this.getResources().getColorStateList(R.color.white));
                mEditItemImage.setImageResource(R.mipmap.video_edit_item);
                itemEditRecyclerView.setVisibility(View.VISIBLE);
                showSeekBar = false;
                switch (mEditItemPosition) {
                    case 0:
                        showSeekBar = true;
                        showSeekBarMode(SEEK_BAR_MODE_VOICE, getString(R.string.video_edit_vlomue));
                        break;
                    case 4:
                    case 5:
                        changeSpeedLinearLayout.setVisibility(View.VISIBLE);
                        break;
                }
                break;
            case TAB_BEAUTY:
                mEditBeautyText.setTextColor(this.getResources().getColorStateList(R.color.white));
                mEditBeautyImage.setImageResource(R.mipmap.video_edit_beauty);
                seekBarLinearLayout.setVisibility(View.VISIBLE);
                beautyRecyclerView.setVisibility(View.VISIBLE);
                selectItemPosition(0, beautyAdapter);
                showSeekBar = true;
                showSeekBarMode(SEEK_BAR_MODE_BEAUTY_RED, getString(R.string.video_edit_detail_beauty_red));
                break;
            case TAB_CAPTION:
                showSeekBar = false;
                showEditSubtitle();
                mEditCaptionText.setTextColor(this.getResources().getColorStateList(R.color.white));
                mEditCaptionImage.setImageResource(R.mipmap.video_edit_caption_detail);

                break;
        }
    }

    private void hideAllVideoEditView() {
        seekBarLinearLayout.setVisibility(View.GONE);
        changeSpeedLinearLayout.setVisibility(View.GONE);
        itemEditRecyclerView.setVisibility(View.GONE);
        adjustLinearLayout.setVisibility(View.GONE);
        beautyRecyclerView.setVisibility(View.GONE);
        subtitleLinearLayout.setVisibility(View.GONE);
//        captionLinearLayout.setVisibility(View.GONE);
    }

    private boolean mSeekBeforePlayStatus;

    /**
     * 初始化SeekBar， 整个细分编辑页面只有一个SeekBar
     */
    private void initSeekBar() {
        beautySeekBar.setMax(VIDEOVOLUME_MAXSEEKBAR_VALUE);
        int volumeVal = (int) Math.floor(mVideoTrack.getVolumeGain().leftVolume / VIDEOVOLUME_MAXVOLUMEVALUE * VIDEOVOLUME_MAXSEEKBAR_VALUE + 0.5D);
        updateVideoVoiceSeekBar(volumeVal);

        beautySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    updateVideoVoiceSeekBar(progress);
                    setSeekValue(progress,mSelectedItemPosition);
                    seek(mStreamingContext.getTimelineCurrentPosition(mTimeline));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mSeekBeforePlayStatus = mIsPlayIng;
                mStreamingContext.stop();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mSeekBeforePlayStatus) {
                    play(mStreamingContext.getTimelineCurrentPosition(mTimeline), mVideoTrack.getClipByIndex(mSelectedItemPosition).getOutPoint());
                } else {
                    seek(mStreamingContext.getTimelineCurrentPosition(mTimeline));
                }
                adjustAdapter.notifyDataSetChanged();
                beautyAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 应用到全部
     */
    private void useSeekAllValue() {
        for (int i = 0; i < TimelineData.init().getClipCount(); i++) {
            updateVideoClipFx(i);
            setSeekValue(beautySeekBar.getProgress(),i);
        }
        updateVideoClipFx(mSelectedItemPosition);
        show(getString(R.string.video_edit_detail_use_all_succ));
    }

    private void setSeekValue(int progress,int mSelectedItemPosition) {
        float colorVal = 0;
        switch (CURRENT_SEEK_BAR_MODE) {
            case SEEK_BAR_MODE_ADJUST_ANJIAO:
                colorVal = progress / 100.0f;
                mVignetteVideoFx.setFloatVal(Constants.FX_VIGNETTE_DEGREE, colorVal);
                TimelineData.instance().getClipInfoData().get(mSelectedItemPosition).setVignetteVal(colorVal);
                break;
            case SEEK_BAR_MODE_ADJUST_RUIDU:

                colorVal = progress / 100.0f * 5;
                mSharpenVideoFx.setFloatVal(Constants.FX_SHARPEN_AMOUNT, colorVal);
                TimelineData.instance().getClipInfoData().get(mSelectedItemPosition).setSharpenVal(colorVal);
                break;
            case SEEK_BAR_MODE_ADJUST_BAOGUANG:
                updateVideoVoiceSeekBarOther(progress);
                colorVal = getFloatColorVal(progress);
                mColorVideoFx.setFloatVal(Constants.FX_COLOR_PROPERTY_BRIGHTNESS, colorVal);
                TimelineData.instance().getClipInfoData().get(mSelectedItemPosition).setBrightnessVal(colorVal);
                break;
            case SEEK_BAR_MODE_ADJUST_DUIBIDU:
                updateVideoVoiceSeekBarOther(progress);
                colorVal = getFloatColorVal(progress);
                mColorVideoFx.setFloatVal(Constants.FX_COLOR_PROPERTY_CONTRAST, colorVal);
                TimelineData.instance().getClipInfoData().get(mSelectedItemPosition).setContrastVal(colorVal);
                break;
            case SEEK_BAR_MODE_ADJUST_BAOHEDU:
                updateVideoVoiceSeekBarOther(progress);
                colorVal = getFloatColorVal(progress);
                mColorVideoFx.setFloatVal(Constants.FX_COLOR_PROPERTY_SATURATION, colorVal);
                TimelineData.instance().getClipInfoData().get(mSelectedItemPosition).setSaturationVal(colorVal);
                break;
            case SEEK_BAR_MODE_VOICE://音量调节
                float volumeVal = progress * VIDEOVOLUME_MAXVOLUMEVALUE / VIDEOVOLUME_MAXSEEKBAR_VALUE;
//                            mVideoTrack.setVolumeGain(volumeVal, volumeVal);
//                            TimelineData.instance().setOriginVideoVolume(volumeVal);
                NvsVideoClip videoClip = mVideoTrack.getClipByIndex(mSelectedItemPosition);
                videoClip.setVolumeGain(volumeVal, volumeVal);
                videoClip.getVolumeGain();
                TimelineData.instance().getClipInfoData().get(mSelectedItemPosition).setVolume(volumeVal);

                break;
            case SEEK_BAR_MODE_BEAUTY_MOPI:
                colorVal = progress / 100.0f;
                mMopiVideoFx.setFloatVal(Constants.FX_BEAUTY_MOPI, colorVal);
                TimelineData.instance().getClipInfoData().get(mSelectedItemPosition).setStrength(colorVal);
                beautyAdapter.beautyValueMap.put(Constants.FX_BEAUTY_MOPI, colorVal);
                break;
            case SEEK_BAR_MODE_BEAUTY_RED:
                colorVal = progress / 100.0f;
                mMopiVideoFx.setFloatVal(Constants.FX_BEAUTY_RED, colorVal);
                TimelineData.instance().getClipInfoData().get(mSelectedItemPosition).setRed(colorVal);
                beautyAdapter.beautyValueMap.put(Constants.FX_BEAUTY_RED, colorVal);
                break;
            case SEEK_BAR_MODE_BEAUTY_WHITE:
                colorVal = progress / 100.0f;
                mMopiVideoFx.setFloatVal(Constants.FX_BEAUTY_WHITE, colorVal);
                TimelineData.instance().getClipInfoData().get(mSelectedItemPosition).setWhite(colorVal);
                beautyAdapter.beautyValueMap.put(Constants.FX_BEAUTY_WHITE, colorVal);
                break;
            case SEEK_BAR_MODE_BEAUTY_THINFACE:
                colorVal = calculateBeautyFloatValue(progress);
                mMopiVideoFx.setFloatVal(Constants.FX_BEAUTY_THIN_FACE, colorVal);
                TimelineData.instance().getClipInfoData().get(mSelectedItemPosition).setThinface(colorVal);
                beautyAdapter.beautyValueMap.put(Constants.FX_BEAUTY_THIN_FACE, colorVal);
                break;
            case SEEK_BAR_MODE_BEAUTY_BIGEYE:
                colorVal = calculateBeautyFloatValue(progress);
                mMopiVideoFx.setFloatVal(Constants.FX_BEAUTY_BIG_EYE, colorVal);
                TimelineData.instance().getClipInfoData().get(mSelectedItemPosition).setBigeye(colorVal);
                beautyAdapter.beautyValueMap.put(Constants.FX_BEAUTY_BIG_EYE, colorVal);
                break;
            case SEEK_BAR_MODE_BEAUTY_XIABA:
                colorVal = calculateBeautyFloatValue(progress);
                mMopiVideoFx.setFloatVal(Constants.FX_BEAUTY_XIABA, colorVal);
                TimelineData.instance().getClipInfoData().get(mSelectedItemPosition).setXiaba(colorVal);
                beautyAdapter.beautyValueMap.put(Constants.FX_BEAUTY_XIABA, colorVal);
                break;
            case SEEK_BAR_MODE_BEAUTY_NOSE:
                colorVal = calculateBeautyFloatValue(progress);
                mMopiVideoFx.setFloatVal(Constants.FX_BEAUTY_NOSE, colorVal);
                TimelineData.instance().getClipInfoData().get(mSelectedItemPosition).setNose(colorVal);
                beautyAdapter.beautyValueMap.put(Constants.FX_BEAUTY_NOSE, colorVal);
                break;
            case SEEK_BAR_MODE_BEAUTY_HEAD:
                colorVal = calculateBeautyFloatValue(progress);
                mMopiVideoFx.setFloatVal(Constants.FX_BEAUTY_HEAD, colorVal);
                TimelineData.instance().getClipInfoData().get(mSelectedItemPosition).setHead(colorVal);
                beautyAdapter.beautyValueMap.put(Constants.FX_BEAUTY_HEAD, colorVal);
                break;
            case SEEK_BAR_MODE_BEAUTY_MOUTH_SIZE:
                colorVal = calculateBeautyFloatValue(progress);
                mMopiVideoFx.setFloatVal(Constants.FX_BEAUTY_MOUTH_SIZE, colorVal);
                TimelineData.instance().getClipInfoData().get(mSelectedItemPosition).setMouthSize(colorVal);
                beautyAdapter.beautyValueMap.put(Constants.FX_BEAUTY_MOUTH_SIZE, colorVal);
                break;
            case SEEK_BAR_MODE_BEAUTY_SMALL_FACE:
                colorVal = calculateBeautyFloatValue(progress);
                mMopiVideoFx.setFloatVal(Constants.FX_BEAUTY_SMALL_FACE, colorVal);
                TimelineData.instance().getClipInfoData().get(mSelectedItemPosition).setSmallFace(colorVal);
                beautyAdapter.beautyValueMap.put(Constants.FX_BEAUTY_SMALL_FACE, colorVal);
                break;
            case SEEK_BAR_MODE_BEAUTY_EYE_CORNER:
                colorVal = calculateBeautyFloatValue(progress);
                mMopiVideoFx.setFloatVal(Constants.FX_BEAUTY_EYE_CORNER, colorVal);
                TimelineData.instance().getClipInfoData().get(mSelectedItemPosition).setEyeCorner(colorVal);
                beautyAdapter.beautyValueMap.put(Constants.FX_BEAUTY_EYE_CORNER, colorVal);
                break;
            case SEEK_BAR_MODE_BEAUTY_FACE_WIDTH:
                colorVal = calculateBeautyFloatValue(progress);
                mMopiVideoFx.setFloatVal(Constants.FX_BEAUTY_FACE_WIDTH, colorVal);
                TimelineData.instance().getClipInfoData().get(mSelectedItemPosition).setFaceWidth(colorVal);
                beautyAdapter.beautyValueMap.put(Constants.FX_BEAUTY_FACE_WIDTH, colorVal);
                break;
            case SEEK_BAR_MODE_BEAUTY_NOSE_LENGTH:
                colorVal = calculateBeautyFloatValue(progress);
                mMopiVideoFx.setFloatVal(Constants.FX_BEAUTY_NOSE_LENGTH, colorVal);
                TimelineData.instance().getClipInfoData().get(mSelectedItemPosition).setNoseLength(colorVal);
                beautyAdapter.beautyValueMap.put(Constants.FX_BEAUTY_NOSE_LENGTH, colorVal);
                break;
            case SEEK_BAR_MODE_BEAUTY_MOUTH_CORNER:
                colorVal = calculateBeautyFloatValue(progress);
                mMopiVideoFx.setFloatVal(Constants.FX_BEAUTY_MOUTH_CORNER, colorVal);
                TimelineData.instance().getClipInfoData().get(mSelectedItemPosition).setMouthCorner(colorVal);
                beautyAdapter.beautyValueMap.put(Constants.FX_BEAUTY_MOUTH_CORNER, colorVal);
                break;
        }
        Log.d("#########", "colorVal=" + colorVal);

    }

    @Override
    public void onBackPressed() {
        /*if (mIsDraging || isIsAddSubtitle()) {
            return;
        }
        if (mStreamingContext != null) {
            VideoEditManger.mCurrentTime = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        }
        removeTimeline();
        super.onBackPressed();*/
    }

    @Override
    protected void onDestroy() {
        if (downProgressDialog != null) {
            downProgressDialog.destroy();
            downProgressDialog = null;
        }
        if (subtitleLinearLayout != null) {
            subtitleLinearLayout.removeAllViews();
            subtitleLinearLayout = null;
        }
        if (mEditSubtitleLayout != null) {
            mEditSubtitleLayout = null;
        }
        if (mEditHandler != null) {
            mEditHandler.removeCallbacksAndMessages(null);
            mEditHandler = null;
        }
        removeTimeline();
        super.onDestroy();

    }

    private void removeTimeline() {
        if (mStreamingContext == null) {
            ToastUtil.showToast(MyVideoEditDetailActivity.this.getApplicationContext(), getString(R.string.video_edit_error));
            finish();
        }
        VideoFrameThumb.getInstance().onDetached();
        if (mStreamingContext != null && mTimeline != null) {
            mStreamingContext.removeTimeline(mTimeline);
            TimelineUtil.removeTimeline(mTimeline);
        }
        mTimeline = null;
    }

    public static void open(Context context, int selectedPosition, long currentTime) {
        Intent intent = new Intent(context, MyVideoEditDetailActivity.class);
        intent.putExtra("selectedPosition", selectedPosition);
        intent.putExtra("currentTime", currentTime);
        context.startActivity(intent);
    }

    private static class ResultHandler extends Handler {
        private final WeakReference<MyVideoEditDetailActivity> mActivity;

        public ResultHandler(MyVideoEditDetailActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MyVideoEditDetailActivity activity = mActivity.get();
            if (activity != null) {
                activity.seek(activity.mVideoTrack.getClipByIndex(activity.mSelectedItemPosition).getInPoint());
                activity.mVideoCutView.setInitVideoData(activity.mTimeline, activity.mSelectedItemPosition, activity.mVideoTrack.getClipByIndex(activity.mSelectedItemPosition).getInPoint(), activity.mVideoTrack);
                if (activity.mEditSubtitleLayout != null) {
                    activity.mEditSubtitleLayout.setScaleList((int) (activity.mTimeline.getDuration() / (float) VideoEditManger.VIDEO_microsecond_TIME * 10));
                }
            }
        }
    }

    private ResultHandler mResultHandler = new ResultHandler(this);

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data != null) {
            switch (resultCode) {

                case RESULT_OK_IN_ALBUM:
                    TimelineUtil.reBuildVideoTrack(mTimeline);
                    if (mSelectedItemPosition + 1 < TimelineData.instance().getClipCount()) {
                        mSelectedItemPosition = mSelectedItemPosition + 1;
                    }
                    mResultHandler.sendEmptyMessageAtTime(0, 200);

                    break;
                case RESULT_OK_IN_SUBTITLE:
                    if (mEditSubtitleLayout != null) {
                        mEditSubtitleLayout.updateTopTimeline();
                        mEditSubtitleLayout.setScrollingSubtitle();
                    }
                    break;
            }
        }
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN || ev.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
            Log.i("club", "club-dispatchTouchEvent:" + +ev.getActionMasked());
            if (mIsDraging || isIsAddSubtitle() || isIsLongPress()||ev.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
                return false;
            }
            return super.dispatchTouchEvent(ev);

        }
        if (ev.getActionMasked() == MotionEvent.ACTION_UP || ev.getActionMasked() == MotionEvent.ACTION_POINTER_UP) {
            Log.i("club", "club-dispatchTouchEvent:" +ev.getActionMasked() );
            if (mIsDraging) {
                mIsDraging = false;
            }
            if (isIsAddSubtitle()) {
                mIsDraging = false;
            }
        }

        return super.dispatchTouchEvent(ev);
    }


}
