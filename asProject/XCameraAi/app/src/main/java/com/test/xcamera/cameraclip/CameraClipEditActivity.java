package com.test.xcamera.cameraclip;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.editvideo.Constants;
import com.editvideo.LiveWindow;
import com.editvideo.TimelineUtil;
import com.editvideo.VideoClipFxInfo;
import com.editvideo.VideoCompileUtil;
import com.editvideo.dataInfo.ClipInfo;
import com.editvideo.dataInfo.CompoundCaptionInfo;
import com.editvideo.dataInfo.TimelineData;
import com.editvideo.dataInfo.TransitionInfo;
import com.framwork.base.adapter.BaseRecyclerAdapter;
import com.framwork.base.view.MOBaseActivity;
import com.test.xcamera.R;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.bean.VideoTemplete;
import com.test.xcamera.cameraclip.adapter.CameraClipEditAdapter;
import com.test.xcamera.cameraclip.adapter.VideoTempleteFragmentPagetAdapter;
import com.test.xcamera.cameraclip.bean.VideoSegment;
import com.test.xcamera.constants.LogcatConstants;
import com.test.xcamera.phonealbum.MyVideoEditActivity;
import com.test.xcamera.phonealbum.bean.MusicBean;
import com.test.xcamera.phonealbum.bean.VideoParamClipData;
import com.test.xcamera.phonealbum.bean.VideoParamData;
import com.test.xcamera.phonealbum.bean.VideoParamMusicData;
import com.test.xcamera.phonealbum.usecase.VideoCompoundCaption;
import com.test.xcamera.phonealbum.usecase.VideoFilerData;
import com.test.xcamera.phonealbum.usecase.VideoTransitionData;
import com.test.xcamera.phonealbum.widget.VideoEditManger;
import com.test.xcamera.statistic.StatisticOneKeyMakeVideo;
import com.test.xcamera.statistic.StatisticShare;
import com.test.xcamera.utils.AssetFile;
import com.test.xcamera.utils.CameraSpaceItemDecoration;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.DensityUtils;
import com.test.xcamera.utils.FileUtil;
import com.test.xcamera.utils.FileUtils;
import com.test.xcamera.utils.LoggerUtils;
import com.test.xcamera.utils.StringUtil;
import com.test.xcamera.view.CircleProgressBar;
import com.test.xcamera.view.basedialog.dialog.CommonDownloadDialog;
import com.test.xcamera.widget.ActivityContainer;
import com.meicam.sdk.NvsAudioTrack;
import com.meicam.sdk.NvsRational;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoFx;
import com.meicam.sdk.NvsVideoResolution;
import com.meicam.sdk.NvsVideoTrack;
import com.moxiang.common.logging.Logcat;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Author: mz
 * Time:  2019/10/29
 */
public class CameraClipEditActivity extends MOBaseActivity {
    @BindView(R.id.left_tv_title)
    TextView leftTvTitle;
    @BindView(R.id.tv_middle_title)
    TextView tvMiddleTitle;
    @BindView(R.id.right_tv_titlee)
    TextView rightTvTitlee;
    @BindView(R.id.lv_window)
    LiveWindow lvWindow;
    @BindView(R.id.iv_paly)
    ImageView ivPalys;
    @BindView(R.id.rl_paly)
    RelativeLayout rlPaly;
    @BindView(R.id.progress)
    CircleProgressBar progress;
    @BindView(R.id.rv_view)
    RecyclerView horizontalRecyclerView;
    @BindView(R.id.tl_cameraClipEditActivity_templeteTitle)
    TabLayout templeteTitleTabLayout;
    @BindView(R.id.vp_cameraClipEditActivity_templeteList)
    ViewPager templeteListViewPager;

    private VideoTempleteFragmentPagetAdapter videoTempletePagetAdapter;
    private List<Fragment> mFragmentList = new ArrayList<>();
    CommonDownloadDialog rlProgress;
    private VideoTempleteFragment shortVideoFragment;
    private VideoTempleteFragment longVideoFragment;
    private int currentShowPage=0;
    private final int SHORT_VIDEO_PAGE=0;
    private final int LONG_VIDEO_PAGE=1;
    private CameraClipEditAdapter horizontalAdapter;
    private String path;
    private NvsAudioTrack mMusicAudioTrack;
    private VideoTemplete selectedVideoTemplete;
    public static final String KEY_OF_TEMPLETE = "templeteKey";
    public static final String KEY_OF_CLICK_TEMPLETE = "clickTempleteKey";
    public static final int MAX_TEMPLETE_COUNT = 8;
    public static final String KEY_OF_NET_TEMPLETE_LIST = "netTempleteListKey";
    private List<VideoSegment> mAllVideoSegmentList = new ArrayList<>();
    private NvsStreamingContext mStreamingContext;
    private NvsTimeline mTimeline;
    private NvsVideoTrack mVideoTrack;
    private NvsVideoFx mColorVideoFx, mVignetteVideoFx, mSharpenVideoFx;
    public static final int DEFAULT_VIDEOFX_VALUE_FOR_COLOR = 1;
    public static final int DEFAULT_VIDEOFX_VALUE_FOR_SHARP_AND_VIGNETTE = 0;

    private boolean needConnectLiveWindow = false;
    private VideoClipFxInfo fxInfo;
    String parent_path;
    String temp_path = "";
    private List<VideoTemplete> mNetVideoTempleteList = new ArrayList<>();
    private List<VideoTemplete> mLocalVideoTempleteList;
    private List<MusicBean> filerList = new ArrayList<>();
    private List<MusicBean> transitionList = new ArrayList<>();
    private int currentPlayItem=-1;

    @OnClick({R.id.left_iv_title, R.id.right_tv_titlee, R.id.rl_paly,
            R.id.iv_cameraClipEditActivity_editVideo, R.id.rl_progress})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left_iv_title:
                onBackPressed();
                break;
            case R.id.iv_cameraClipEditActivity_editVideo:
                needConnectLiveWindow = true;
                StatisticOneKeyMakeVideo.getInstance().setOnEvent(StatisticOneKeyMakeVideo.SIDE_KEY_EDIT);
                VideoParamData paramData = getTestVideoParamData();
                MyVideoEditActivity.startMyVideoEditActivity(this, paramData);
                break;
            case R.id.right_tv_titlee:
                StatisticOneKeyMakeVideo.getInstance().setOnEvent(StatisticOneKeyMakeVideo.SIDE_KEY_GENERATE);
                rightTvTitlee.setClickable(false);
                complidVideo();
                break;
            case R.id.rl_paly:
                setPlayOrStop();
                break;
        }
    }

    @Override
    public int initView() {
        TimelineUtil.initStreamingContext();
        mStreamingContext = NvsStreamingContext.getInstance();
        return R.layout.activity_camera_clip_edit;
    }

    @Override
    public void initData() {
        mAllVideoSegmentList = AiCameraApplication.mApplication.mAllSortedVideoSegmentList;
        selectedVideoTemplete = getIntent().getParcelableExtra(KEY_OF_TEMPLETE);
        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("selectedVideoTemplete id="+selectedVideoTemplete.getId()
                +" localIcon="+selectedVideoTemplete.getIcon()
                +" name="+selectedVideoTemplete.getName()
                +" content="+selectedVideoTemplete.getContent()
                +" desc="+selectedVideoTemplete.getDescription()).out();

//        if (selectedVideoTemplete.isNetTemplete()) {
            mNetVideoTempleteList = getIntent().getParcelableArrayListExtra(KEY_OF_NET_TEMPLETE_LIST);
            for (int i = 0; i < mNetVideoTempleteList.size(); i++) {
                mNetVideoTempleteList.get(i).setNetTemplete(true);
            }
//        }

        rlProgress = new CommonDownloadDialog(this);
        rlProgress.setDialogCanceledOnTouchOutside(false);
        VideoCompoundCaption.getInstance();//切记不能删除，用于组合字幕搜索资源。
        generateTimeLineData(selectedVideoTemplete.getVideoSegmentList()); //生成Timeline信息
        initTitle();
        initRecycleView();

        if (createTimelineAndSetCallback()){
            return;
        }

        transitionList= VideoTransitionData.getInstance().getTransitionDataList(null);
        filerList= VideoFilerData.getInstance().getVideoFilerList();
    }

    public boolean createTimelineAndSetCallback() {
        NvsVideoResolution videoResolution = new NvsVideoResolution();
        videoResolution.imageWidth = selectedVideoTemplete.getVideoWidth();
        videoResolution.imageHeight = selectedVideoTemplete.getVideoHeight();
//        videoResolution.imagePAR = new NvsRational(1, 1);
        NvsRational videoFps = new NvsRational(30, 1);
        mTimeline = TimelineUtil.createTimeline(videoResolution, videoFps);
        if (mTimeline == null) {
            return true;
        }
        mVideoTrack = mTimeline.getVideoTrackByIndex(0);
        if (mVideoTrack == null) {
            return true;
        }
        mMusicAudioTrack = mTimeline.getAudioTrackByIndex(0);
        connectTimelineWithLiveWindow();
        return false;
    }


    public void initTitle() {
        leftTvTitle.setBackgroundResource(R.mipmap.icon_video_edit_back);
        tvMiddleTitle.setText(getResources().getString(R.string.wondelfulTime));
        rightTvTitlee.setVisibility(View.VISIBLE);
        rightTvTitlee.setBackgroundResource(R.drawable.circle_corner_green_bg_normal_2dp);
        rightTvTitlee.setTextColor(Color.WHITE);
        rightTvTitlee.setText(getResources().getString(R.string.generate));
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rightTvTitlee.getLayoutParams();
        layoutParams.width = DensityUtils.dp2px(this, 56);
        layoutParams.height = DensityUtils.dp2px(this, 32);
        rightTvTitlee.setLayoutParams(layoutParams);
    }

    public void initRecycleView() {
        horizontalAdapter = new CameraClipEditAdapter(mContext);
        horizontalRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
        //设置item间距，30dp
        horizontalRecyclerView.addItemDecoration(new CameraSpaceItemDecoration(10, 0));
        horizontalRecyclerView.setAdapter(horizontalAdapter);
        horizontalAdapter.updateData(true, selectedVideoTemplete.getVideoSegmentList());
        horizontalAdapter.setOnRecyclerItemClickListener(new BaseRecyclerAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                horizontalRecyclerView.getLayoutManager().smoothScrollToPosition(horizontalRecyclerView, new RecyclerView.State(), position);
                selectHorizontalThumbnailView(position);
                long curTime = 0;
                if (mVideoTrack.getClipByIndex(position) == null) {
                    return;
                } else {
                    curTime = mVideoTrack.getClipByIndex(position).getInPoint() + 10000;
                }
                ivPalys.setVisibility(View.GONE);
                mStreamingContext.playbackTimeline(mTimeline, curTime, mTimeline.getDuration(), NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, true, 0);
            }
        });

        mLocalVideoTempleteList = OneKeyMakeVideoHelper.convertTempleteJsonToBean(this);

        List<VideoTemplete> shortTempleteList = new ArrayList<>();
        List<VideoTemplete> tmpLongTempleteList = new ArrayList<>();
        List<VideoTemplete> longTempleteList = new ArrayList<>();
        if (selectedVideoTemplete.isNetTemplete()) {
            boolean hasSelectedTemplete = false;
            for (int i = 0; i < mNetVideoTempleteList.size(); i++) {
                VideoTemplete videoTemplete = mNetVideoTempleteList.get(i);
                if (videoTemplete.isShortTemplete()) {
                    shortTempleteList.add(videoTemplete);
                } else {
                    tmpLongTempleteList.add(videoTemplete);
                }

                if (!hasSelectedTemplete && selectedVideoTemplete.getId() == videoTemplete.getId()) {
                    hasSelectedTemplete=true;
                    videoTemplete.setCheck(true);
                    if (videoTemplete.isShortTemplete()) {
                        currentShowPage = SHORT_VIDEO_PAGE;
                    } else {
                        currentShowPage = LONG_VIDEO_PAGE;
                    }
                }
            }
            for (int i = 0; i < mLocalVideoTempleteList.size(); i++) {
                VideoTemplete videoTemplete = mLocalVideoTempleteList.get(i);
                if (videoTemplete.isShortTemplete()) {
                    shortTempleteList.add(videoTemplete);
                } else {
                    tmpLongTempleteList.add(videoTemplete);
                }
            }
        } else {
            boolean hasSelectedTemplete = false;
            for (int i = 0; i < mLocalVideoTempleteList.size(); i++) {
                VideoTemplete videoTemplete = mLocalVideoTempleteList.get(i);
                if (videoTemplete.isShortTemplete()) {
                    shortTempleteList.add(videoTemplete);
                } else {
                    tmpLongTempleteList.add(videoTemplete);
                }

                if (!hasSelectedTemplete && selectedVideoTemplete.getIcon().equalsIgnoreCase(videoTemplete.getIcon())) {
                    hasSelectedTemplete = true;
                    videoTemplete.setCheck(true);
                    if (videoTemplete.isShortTemplete()) {
                        currentShowPage = SHORT_VIDEO_PAGE;
                    } else {
                        currentShowPage = LONG_VIDEO_PAGE;
                    }
                }
            }
        }
        for(int i=0; i<tmpLongTempleteList.size();i++){
            if(OneKeyMakeVideoXmlParser.MIN_FILE_DURATION*3*tmpLongTempleteList.get(i).getVideo_segment().size()<=OneKeyMakeVideoXmlParser.TOTAL_DURATION){
                longTempleteList.add(tmpLongTempleteList.get(i));
            }
        }

        videoTempletePagetAdapter = new VideoTempleteFragmentPagetAdapter(getSupportFragmentManager());
        templeteListViewPager.setAdapter(videoTempletePagetAdapter);
        shortVideoFragment = (VideoTempleteFragment) VideoTempleteFragment.newInstance(shortTempleteList, rlProgress);
        longVideoFragment = (VideoTempleteFragment) VideoTempleteFragment.newInstance(longTempleteList, rlProgress);
        shortVideoFragment.setTempleteDownloadFinishCallback(new VideoTempleteFragment.TempleteDownloadFinishCallback() {
            @Override
            public void onDownloadFinish(VideoTemplete videoTemplete) {
                reGenerateTempleteVideo(videoTemplete);
            }
        });
        longVideoFragment.setTempleteDownloadFinishCallback(new VideoTempleteFragment.TempleteDownloadFinishCallback() {
            @Override
            public void onDownloadFinish(VideoTemplete videoTemplete) {
                reGenerateTempleteVideo(videoTemplete);
            }
        });
        mFragmentList.add(shortVideoFragment);
        if(OneKeyMakeVideoHelper.makeVideoType == OneKeyMakeVideoHelper.MakeVideoType.SIDE_KEY){
            templeteListViewPager.setCurrentItem(SHORT_VIDEO_PAGE);
            templeteTitleTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }else {
            templeteListViewPager.setCurrentItem(currentShowPage);
            mFragmentList.add(longVideoFragment);
        }
        videoTempletePagetAdapter.updateData(true, mFragmentList);
        templeteListViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                currentShowPage =  i;
                if(currentShowPage==SHORT_VIDEO_PAGE){
                    StatisticOneKeyMakeVideo.getInstance().setOnEvent(StatisticOneKeyMakeVideo.TODAY_WONDERFUL_SHORT_TEMPLETE);
                }else if(currentShowPage==LONG_VIDEO_PAGE){
                    StatisticOneKeyMakeVideo.getInstance().setOnEvent(StatisticOneKeyMakeVideo.TODAY_WONDERFUL_LONG_TEMPLETE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        int[] tablayoutTitles = {R.string.shortVideo, R.string.longVideo};
        templeteTitleTabLayout.setupWithViewPager(templeteListViewPager);
        for(int i=0; i<videoTempletePagetAdapter.mFragmentList.size();i++){
            templeteTitleTabLayout.getTabAt(i).setText(tablayoutTitles[i]);
        }
    }

    public void selectHorizontalThumbnailView(int position) {
        currentPlayItem=position;
        for (int i = 0; i < horizontalAdapter.mData.size(); i++) {
            horizontalAdapter.mData.get(i).setSelected(false);
        }
        horizontalAdapter.mData.get(position).setSelected(true);
        horizontalAdapter.notifyDataSetChanged();
    }

    /**
     * 重新生成模板视频
     * @param videoTemplete
     */
    private void reGenerateTempleteVideo(VideoTemplete videoTemplete) {
        selectedVideoTemplete = videoTemplete;
        generateTimeLineData(selectedVideoTemplete.getVideoSegmentList());
        TimelineUtil.reBuildVideoTrack(mTimeline);
        horizontalAdapter.updateData(true, selectedVideoTemplete.getVideoSegmentList());
        playTemplateVideo(selectedVideoTemplete);

        if (currentShowPage==LONG_VIDEO_PAGE) {
            for (int i = 0; i < shortVideoFragment.videoTempleteAdapter.mData.size(); i++) {
                shortVideoFragment.videoTempleteAdapter.mData.get(i).setCheck(false);
            }
            shortVideoFragment.videoTempleteAdapter.notifyDataSetChanged();
        } else {
            //侧面键没有长视频tab
            if(longVideoFragment.videoTempleteAdapter!=null){
                for (int i = 0; i < longVideoFragment.videoTempleteAdapter.mData.size(); i++) {
                    longVideoFragment.videoTempleteAdapter.mData.get(i).setCheck(false);
                }
                longVideoFragment.videoTempleteAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!rlProgress.isShowing()){
            if (selectedVideoTemplete != null) {
                playTemplateVideo(selectedVideoTemplete);
            } else {
                play();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mStreamingContext != null && !rlProgress.isShowing())
            mStreamingContext.stop();
    }

    //连接时间线跟liveWindow
    public void connectTimelineWithLiveWindow() {
        if (mStreamingContext == null || mTimeline == null || lvWindow == null)
            return;
        mStreamingContext.setPlaybackCallback(new NvsStreamingContext.PlaybackCallback() {
            @Override
            public void onPlaybackPreloadingCompletion(NvsTimeline nvsTimeline) {
            }

            @Override
            public void onPlaybackStopped(NvsTimeline nvsTimeline) {
            }

            @Override
            public void onPlaybackEOF(NvsTimeline nvsTimeline) {
                mStreamingContext.playbackTimeline(mTimeline, 0, mTimeline.getDuration(), NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, true, 0);
            }
        });
        mStreamingContext.setPlaybackCallback2(new NvsStreamingContext.PlaybackCallback2() {
            @Override
            public void onPlaybackTimelinePosition(NvsTimeline nvsTimeline, long cur_position) {
                NvsVideoClip clip = mVideoTrack.getClipByTimelinePosition(cur_position);
                if (currentPlayItem != clip.getIndex()) {
                    currentPlayItem = clip.getIndex();
                    selectHorizontalThumbnailView(currentPlayItem);
                    horizontalRecyclerView.getLayoutManager().smoothScrollToPosition(horizontalRecyclerView, new RecyclerView.State(), currentPlayItem);
                }
            }
        });
        mStreamingContext.setCompileCallback2(new NvsStreamingContext.CompileCallback2() {
            @Override
            public void onCompileCompleted(NvsTimeline nvsTimeline, boolean isCanceled) {
                enableGeneVideoButton();
                Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("meishe compile video  Completed").out();
                FileUtil.sendScanBroadcast(mContext, path);
                AiCameraApplication.mApplication.mAllSortedVideoSegmentList.clear();
                StatisticShare.SHARE_FROM_KEY=StatisticShare.One_key_Video;
                CompleteVideoActivity.open(mContext, path, mTimeline.getDuration());
                finish();
            }
        });
        mStreamingContext.setCompileCallback(new NvsStreamingContext.CompileCallback() {
            @Override
            public void onCompileProgress(NvsTimeline nvsTimeline, int i) {
                String format = String.format(mContext.getResources().getString(R.string.download_save_progress), i) + "%";
                rlProgress.setProgress(format);
            }

            @Override
            public void onCompileFinished(NvsTimeline nvsTimeline) {
                Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("meishe compile video  finished").out();
                enableGeneVideoButton();
            }

            @Override
            public void onCompileFailed(NvsTimeline nvsTimeline) {
                enableGeneVideoButton();
                Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("meishe compile video  fail").out();
                CameraToastUtil.show(getResourceToString(R.string.complete_fail), mContext);
            }
        });
        mStreamingContext.connectTimelineWithLiveWindow(mTimeline, lvWindow);
        needConnectLiveWindow = false;
    }

    private void enableGeneVideoButton() {
        rightTvTitlee.setClickable(true);
        rlProgress.dismissDialog();
    }

    /**
     * 生成TimeLineData
     */
    public List<ClipInfo> generateTimeLineData(List<VideoSegment> segmentBeanList) {
        TimelineData.instance().clear();
        ArrayList<ClipInfo> pathList = new ArrayList<>();
        for (int i = 0; (i<segmentBeanList.size() && i<selectedVideoTemplete.getVideo_segment().size()); i++) {
            VideoSegment segmentBean = segmentBeanList.get(i);
            LoggerUtils.printLog("mediaData.getPath()=" + segmentBean.getVideoSegmentFilePath());
            //剪辑信息只要了 path
            ClipInfo clipInfo = new ClipInfo();
            Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg(" segment duration  "+segmentBean.getVideoSegmentDuration()).out();
            clipInfo.setFilePath(segmentBean.getVideoSegmentFilePath());
            clipInfo.setDuration(segmentBean.getVideoSegmentDuration());
            pathList.add(clipInfo);
        }
        TimelineData.instance().setClipInfoData(pathList);
        return pathList;
    }

    /**
     * 合成视频
     */
    public void complidVideo() {
        path = com.test.xcamera.utils.Constants.myGalleryLocalPath + "/" + System.currentTimeMillis() + ".mp4";
        VideoCompileUtil.compileVideo(mStreamingContext, mTimeline, path, 0, mTimeline.getDuration(), false);
        if (!rlProgress.isShowing()) {
            rlProgress.showDialog(false, false);
        }
    }

    /**
     *
     */
    private void playTemplateVideo(VideoTemplete template) {
        if (mTimeline==null || mTimeline.getVideoTrackByIndex(0)==null || mTimeline.getAudioTrackByIndex(0)==null ){
            TimelineUtil.initStreamingContext();
            mStreamingContext = NvsStreamingContext.getInstance();
            generateTimeLineData(selectedVideoTemplete.getVideoSegmentList()); //生成Timeline信息
            createTimelineAndSetCallback();
        }
        if(needConnectLiveWindow){
            connectTimelineWithLiveWindow();
        }
        //倍速
        changeSpeedAndAdjustVideo();

        //bg音乐
        if (!selectedVideoTemplete.isNetTemplete()) {
            toSdCard(template.getBgm().getAudio_url());
        } else {
            parent_path = selectedVideoTemplete.getBgm().getAudio_url();
        }
        mMusicAudioTrack.addClip(parent_path, 0);
        String volumn  = template.getBgm().getVolume();
        if(StringUtil.notEmpty(volumn)){
            float vol = Float.valueOf(volumn);
            if(vol>Constants.VIDEOVOLUME_MAXVOLUMEVALUE||vol<Constants.VIDEOVOLUME_MIN_VALUE_VOLUME){
                mMusicAudioTrack.setVolumeGain(1,1);
            }else {
                mMusicAudioTrack.setVolumeGain(vol,vol);
            }
        }

        //设置滤镜
        fxInfo = new VideoClipFxInfo();
        fxInfo.setFxId(null);
        for(int m=0; m<filerList.size();m++){
            if(filerList.get(m).getName().equalsIgnoreCase(template.getFilter().getName())){
                fxInfo.setFxMode(VideoClipFxInfo.FXMODE_PACKAGE);
                fxInfo.setFxId(filerList.get(m).getId());
                break;
            }
        }
        fxInfo.setFxIntensity((float) template.getFilter().getIntensity());
        TimelineData.instance().setVideoClipFxData(fxInfo);
        TimelineUtil.buildTimelineFilter(mTimeline, fxInfo);

        //设置转场
        for (int i = 0; (i < mTimeline.getVideoTrackByIndex(0).getClipCount() - 1) && (i < (template.getVideo_segment().size()-1) ); i++) {
            VideoTemplete.VideoSegmentBean detail = template.getVideo_segment().get(i);
            TransitionInfo transitionInfo = new TransitionInfo();
            transitionInfo.setTransitionId(detail.getTransition().getName());
            for(int m=0; m<transitionList.size();m++){
                if(transitionList.get(m).getName().equalsIgnoreCase(detail.getTransition().getName())){
                    if(m<4){
                        transitionInfo.setTransitionMode(TransitionInfo.TRANSITIONMODE_BUILTIN);
                        mTimeline.getVideoTrackByIndex(0).setBuiltinTransition(i, transitionList.get(m).getId());
                    }else {
                        transitionInfo.setTransitionMode(TransitionInfo.TRANSITIONMODE_PACKAGE);
                        mTimeline.getVideoTrackByIndex(0).setPackagedTransition(i, transitionList.get(m).getId());
                    }
                    break;
                }
            }
        }

        //组合字幕
        TimelineUtil.setCompoundCaption(mTimeline, createCompoundCaption(template));
        float yUnitHeight = 0.2f;
        //                    TimelineUtil.addCaption(mTimeline, lvWindow, captionListBean.getCaption_text(), (float) captionListBean.getIn_point(),
//                            (float) captionListBean.getDuration(), new PointF(0.5f, (i + 1) * yUnitHeight), 50);
//        //贴纸
//        TimelineUtil.addSticker(mTimeline, 0, (int) (mTimeline.getDuration() / (2*1000 * 1000)),
//                "87482E23-C297-4227-85E4-5ACD5B42CAF8", "");
        play();
    }

    /**
     * 组合字幕
     * @param template
     * @return
     */
    private ArrayList<CompoundCaptionInfo> createCompoundCaption(VideoTemplete template) {
        ArrayList<CompoundCaptionInfo> compoundCaptionList = new ArrayList<>();
        for (int i = 0; i < template.getCaption_list().size(); i++) {
            VideoTemplete.CaptionListBean captionListBean = template.getCaption_list().get(i);
            CompoundCaptionInfo info=new CompoundCaptionInfo();
            info.setCaptionStyleUuid(captionListBean.getCaption_name());
            info.setInPoint((long) (captionListBean.getIn_point()*VideoEditManger.VIDEO_microsecond_TIME));
            info.setOutPoint((long) ((captionListBean.getIn_point()+captionListBean.getDuration())* VideoEditManger.VIDEO_microsecond_TIME));
            info.setCaptionZVal((int) VideoCompoundCaption.getInstanceOther().getCurCaptionZVal(mTimeline));
            compoundCaptionList.add(info);
            for (int j=0;j<captionListBean.getCaption().size();j++){
                CompoundCaptionInfo.CompoundCaptionAttr captionAttr = new CompoundCaptionInfo.CompoundCaptionAttr();
                VideoTemplete.CaptionListBean.CaptionBean captionBean = captionListBean.getCaption().get(j);
                captionAttr.setCaptionColor(captionBean.getCaption_text_color());
                captionAttr.setCaptionText(captionBean.getCaption_text());
                 String font = captionBean.getCaption_Typeface();
                String fontAssetPath = "";
                if(font.endsWith("ttf")){
                    fontAssetPath = "assets:/font/" + font;
                }else {
                    fontAssetPath = "assets:/font/" + font+".ttf";
                }
                captionAttr.setCaptionFontName(mStreamingContext.registerFontByFilePath(fontAssetPath));
                info.getCaptionAttributeList().add(captionAttr);
            }
        }
        return compoundCaptionList;
    }

    /**
     * 调整视频画面的亮度，对比度，饱和度，暗角等
     * @param clipInfo
     */
    private void adjustVideo(VideoParamClipData clipInfo) {
        float vignette = selectedVideoTemplete.getVignette();
        if(vignette>Constants.MAX_VALUE_FOR_VIGNETTE||vignette<Constants.MIN_VALUE_FOR_VIGNETTE){
            clipInfo.setM_vignetteVal(DEFAULT_VIDEOFX_VALUE_FOR_SHARP_AND_VIGNETTE);
        }else {
            clipInfo.setM_vignetteVal(vignette);
        }
        float sharpen = selectedVideoTemplete.getSharpen();
        if(sharpen>Constants.MAX_VALUE_FOR_SHARPEN||sharpen<Constants.MIN_VALUE_FOR_SHARPEN){
            clipInfo.setM_sharpenVal(DEFAULT_VIDEOFX_VALUE_FOR_SHARP_AND_VIGNETTE);
        }else {
            clipInfo.setM_sharpenVal(sharpen);
        }
        float exposure = selectedVideoTemplete.getExposure();
        if(exposure>Constants.MAX_VALUE_FOR_BRIGHTNESS||exposure<Constants.MIN_VALUE_FOR_BRIGHTNESS){
            clipInfo.setM_brightnessVal(DEFAULT_VIDEOFX_VALUE_FOR_COLOR);
        }else {
            clipInfo.setM_brightnessVal(exposure);
        }
        float contrastRatio = selectedVideoTemplete.getContrast_ratio();
        if(contrastRatio>Constants.MAX_VALUE_FOR_CONTRAST||contrastRatio<Constants.MIN_VALUE_FOR_CONTRAST){
            clipInfo.setM_contrastVal(DEFAULT_VIDEOFX_VALUE_FOR_COLOR);
        }else {
            clipInfo.setM_contrastVal(contrastRatio);
        }
        float saturation = selectedVideoTemplete.getSaturation();
        if(saturation>Constants.MAX_VALUE_FOR_SATURATION||saturation<Constants.MIN_VALUE_FOR_SATURATION){
            clipInfo.setM_saturationVal(DEFAULT_VIDEOFX_VALUE_FOR_COLOR);
        }else {
            clipInfo.setM_saturationVal(saturation);
        }
    }


    /**
     * change video spee
     * 0.125--0.25--0.5--1.0--2.0--4.0
     */
    private void changeSpeedAndAdjustVideo() {
        for (int i = 0; i < mVideoTrack.getClipCount(); i++) {
            NvsVideoClip videoClip = mVideoTrack.getClipByIndex(i);
            if (videoClip == null) {
                continue;
            }
            String speed = selectedVideoTemplete.getVideo_segment().get(i).getVideo_speed();
            LoggerUtils.printLog("speed=" + speed);
            if(StringUtil.notEmpty(speed)){
                videoClip.changeSpeed(Double.valueOf(speed));
            }else {
                videoClip.changeSpeed(1d);
            }

            //画面调节
            updateVideoClipFx(videoClip);
            float vignette = selectedVideoTemplete.getVignette();
            if(vignette>Constants.MAX_VALUE_FOR_VIGNETTE || vignette<Constants.MIN_VALUE_FOR_VIGNETTE){
                mVignetteVideoFx.setFloatVal(Constants.FX_VIGNETTE_DEGREE, DEFAULT_VIDEOFX_VALUE_FOR_SHARP_AND_VIGNETTE);
            }else {
                mVignetteVideoFx.setFloatVal(Constants.FX_VIGNETTE_DEGREE, vignette);
            }
            float sharpen = selectedVideoTemplete.getSharpen();
            if(sharpen>Constants.MAX_VALUE_FOR_SHARPEN || sharpen<Constants.MIN_VALUE_FOR_SHARPEN){
                mSharpenVideoFx.setFloatVal(Constants.FX_SHARPEN_AMOUNT, DEFAULT_VIDEOFX_VALUE_FOR_SHARP_AND_VIGNETTE);
            }else {
                mSharpenVideoFx.setFloatVal(Constants.FX_SHARPEN_AMOUNT, sharpen);
            }
            float exposure = selectedVideoTemplete.getExposure();
            if(exposure>Constants.MAX_VALUE_FOR_BRIGHTNESS||exposure<Constants.MIN_VALUE_FOR_BRIGHTNESS){
                mColorVideoFx.setFloatVal(Constants.FX_COLOR_PROPERTY_BRIGHTNESS, DEFAULT_VIDEOFX_VALUE_FOR_COLOR);
            }else {
                mColorVideoFx.setFloatVal(Constants.FX_COLOR_PROPERTY_BRIGHTNESS, exposure);
            }
            float contrastRatio = selectedVideoTemplete.getContrast_ratio();
            if(contrastRatio>Constants.MAX_VALUE_FOR_CONTRAST||contrastRatio<Constants.MIN_VALUE_FOR_CONTRAST){
                mColorVideoFx.setFloatVal(Constants.FX_COLOR_PROPERTY_CONTRAST, DEFAULT_VIDEOFX_VALUE_FOR_COLOR);
            }else {
                mColorVideoFx.setFloatVal(Constants.FX_COLOR_PROPERTY_CONTRAST, contrastRatio);
            }
            float saturation = selectedVideoTemplete.getSaturation();
            if(saturation>Constants.MAX_VALUE_FOR_SATURATION||saturation<Constants.MIN_VALUE_FOR_SATURATION){
                mColorVideoFx.setFloatVal(Constants.FX_COLOR_PROPERTY_SATURATION, DEFAULT_VIDEOFX_VALUE_FOR_COLOR);
            }else {
                mColorVideoFx.setFloatVal(Constants.FX_COLOR_PROPERTY_SATURATION, saturation);
            }
        }
    }

    /**
     * 更新指定位置item的画面调整参数
     */
    private void updateVideoClipFx(NvsVideoClip videoClip) {
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
            }
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
    }


//    private float getCurAnimateStickerZVal() {
//        float zVal = 0.0f;
//        NvsTimelineAnimatedSticker animatedSticker = mTimeline.getFirstAnimatedSticker();
//        while (animatedSticker != null) {
//            float tmpZVal = animatedSticker.getZValue();
//            if (tmpZVal > zVal)
//                zVal = tmpZVal;
//            animatedSticker = mTimeline.getNextAnimatedSticker(animatedSticker);
//        }
//        zVal += 1.0;
//        return zVal;
//    }

    /**
     * start play video
     */
    public void play() {
        ivPalys.setVisibility(View.GONE);
        mStreamingContext.playbackTimeline(mTimeline, 0,
                mTimeline.getDuration(), NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, true, 0);
    }

    public void setPlayOrStop() {
        if (mStreamingContext.getStreamingEngineState() == NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
            mStreamingContext.stop();
            ivPalys.setVisibility(View.VISIBLE);
        } else {
            mStreamingContext.playbackTimeline(mTimeline, mStreamingContext.getTimelineCurrentPosition(mTimeline), mTimeline.getDuration(), NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, true, 0);
            ivPalys.setVisibility(View.GONE);
        }
    }

    public void toSdCard(String file_childerpath) {
        String[] str = file_childerpath.split("/");
        temp_path = str[str.length - 1];
        parent_path = com.test.xcamera.utils.Constants.cacheLocalPath + "/" + temp_path;
        FileUtils.creatFile(com.test.xcamera.utils.Constants.cacheLocalPath, temp_path);
        File file = new File(com.test.xcamera.utils.Constants.cacheLocalPath, temp_path);
        if (!file.exists()) {
            AssetFile assetFile = new AssetFile(mContext);
            assetFile.toSdcard(parent_path, com.test.xcamera.utils.Constants.template_path + file_childerpath);
        }
    }


    @Override
    protected void onDestroy() {
        rlProgress.destroy();
        super.onDestroy();
        if (lvWindow != null) {
            lvWindow.setOnClickListener(null);
        }
        if(mStreamingContext!=null){
            mStreamingContext.removeTimeline(mTimeline);
            mStreamingContext.clearCachedResources(true);
        }
        NvsStreamingContext.close();
        TimelineData.instance().clear();
    }

    @Override
    public void disconnectedUSB() {
        super.disconnectedUSB();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(rlProgress!=null ){
                    rlProgress.dismissDialog();
                }
                CameraToastUtil.show(getResourceToString(R.string.disconenct_usb), mContext);
                finishActivity();
            }
        });
    }

    public static void finishActivity() {
        List<WeakReference<Activity>> activityStack = ActivityContainer.getInstance().getActivityStack();
        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("disconnected usb "+activityStack.size()).out();
        for (int i = 0; i < activityStack.size(); i++) {
            if(activityStack.get(i)==null||activityStack.get(i).get()==null){
                continue;
            }
            if (activityStack.get(i).get() instanceof TodayVideoListActivity
                    ||activityStack.get(i).get() instanceof TodayFileListActivity) {
                activityStack.get(i).get().finish();
                activityStack.remove(i);
            }
        }
    }

    public static void open(Context context, VideoTemplete videoTemplete, List<VideoTemplete> netVideoTempleteList) {
        Intent intent = new Intent(context, CameraClipEditActivity.class);
        intent.putExtra(CameraClipEditActivity.KEY_OF_TEMPLETE, videoTemplete);
        intent.putParcelableArrayListExtra(CameraClipEditActivity.KEY_OF_NET_TEMPLETE_LIST, (ArrayList<? extends Parcelable>) netVideoTempleteList);
        context.startActivity(intent);
    }


    private VideoParamData getTestVideoParamData() {
        VideoParamData paramData = new VideoParamData();
        paramData.setCompoundCaptionArray(createCompoundCaption(selectedVideoTemplete));
        ArrayList<VideoParamClipData> clipInfos = new ArrayList<>();
        List<VideoSegment> videoSegmentList = selectedVideoTemplete.getVideoSegmentList();
        List<VideoTemplete.VideoSegmentBean> video_segment = selectedVideoTemplete.getVideo_segment();
        float totalTime = 0;
        for (int i = 0; (i<videoSegmentList.size() && i<video_segment.size()); i++) {
            VideoSegment videoSegment = videoSegmentList.get(i);
            VideoTemplete.VideoSegmentBean videoSegmentBean = video_segment.get(i);
            TransitionInfo transitionInfo;
            totalTime += (videoSegment.getEnd_time() - videoSegment.getStart_time());
            VideoParamClipData clipInfo = new VideoParamClipData();
            clipInfo.setFilePath(videoSegment.getVideoSegmentFilePath());
            clipInfo.setDuration((videoSegment.getEnd_time() - videoSegment.getStart_time()) / 1000f);
            String speed = selectedVideoTemplete.getVideo_segment().get(i).getVideo_speed();
            if(StringUtil.notEmpty(speed)){
                clipInfo.setSpeed(Float.valueOf(speed));
            }
            transitionInfo = new TransitionInfo();
            for(int m=0; m<transitionList.size();m++){
                if(transitionList.get(m).getName().equalsIgnoreCase(videoSegmentBean.getTransition().getName())){
                    if(m<4){
                        transitionInfo.setTransitionMode(TransitionInfo.TRANSITIONMODE_BUILTIN);
                    }else {
                        transitionInfo.setTransitionMode(TransitionInfo.TRANSITIONMODE_PACKAGE);
                    }
                    transitionInfo.setTransitionId(transitionList.get(m).getId());
                    break;
                }
            }
            clipInfo.setTransitionInfo(transitionInfo);
            adjustVideo(clipInfo);
            clipInfos.add(clipInfo);
        }

        paramData.setVideoParamClipData(clipInfos);

        VideoClipFxInfo m_videoClipFxInfo = new VideoClipFxInfo();
        for(int m=0; m<filerList.size();m++){
            if(filerList.get(m).getName().equalsIgnoreCase(selectedVideoTemplete.getFilter().getName())){
                m_videoClipFxInfo.setFxMode(VideoClipFxInfo.FXMODE_PACKAGE);
                m_videoClipFxInfo.setFxId(filerList.get(m).getId());
                break;
            }
        }
        paramData.setM_videoClipFxInfo(m_videoClipFxInfo);

        VideoParamMusicData videoParamMusicData = new VideoParamMusicData();
        videoParamMusicData.setFilePath(parent_path);
        videoParamMusicData.setImagePath("");
        int lastIndexOf = selectedVideoTemplete.getBgm().getAudio_url().lastIndexOf("/");
        int lastIndexOf1 = selectedVideoTemplete.getBgm().getAudio_url().lastIndexOf(".");
        String name = selectedVideoTemplete.getBgm().getAudio_url().substring(lastIndexOf + 1, lastIndexOf1);
        videoParamMusicData.setName(name);
        videoParamMusicData.setInPoint(0);
        videoParamMusicData.setTrimIn(0);
        float vTime = totalTime / 1000;
        videoParamMusicData.setOutPoint(vTime);
        videoParamMusicData.setTrimOut(vTime);
        videoParamMusicData.setDuration(vTime);
        float vol =Float.parseFloat(selectedVideoTemplete.getBgm().getVolume());
        if(vol>=4||vol <=0){
            videoParamMusicData.setVolume(1);
        }else {
            videoParamMusicData.setVolume(vol);
        }
        paramData.setVideoParamMusicData(videoParamMusicData);
        return paramData;
    }
}
