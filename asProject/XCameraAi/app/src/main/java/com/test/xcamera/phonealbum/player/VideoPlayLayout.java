package com.test.xcamera.phonealbum.player;

import android.content.Context;
import android.graphics.PointF;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.editvideo.Constants;
import com.editvideo.TimelineUtil;
import com.editvideo.dataInfo.TimelineData;
import com.test.xcamera.R;
import com.test.xcamera.phonealbum.editInterface.EditCallBack;
import com.test.xcamera.phonealbum.player.mvp.Player;
import com.test.xcamera.phonealbum.player.mvp.VideoPlayContract;
import com.test.xcamera.phonealbum.player.mvp.VideoPlayPresenter;
import com.test.xcamera.phonealbum.widget.VideoEditManger;
import com.test.xcamera.phonealbum.widget.subtitle.bean.Mark;
import com.test.xcamera.utils.MoStreamingContext;
import com.test.xcamera.view.DrawRect;
import com.meicam.sdk.NvsLiveWindow;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineAnimatedSticker;
import com.meicam.sdk.NvsTimelineCaption;
import com.meicam.sdk.NvsTimelineCompoundCaption;
import com.meicam.sdk.NvsTimelineVideoFx;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoTrack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class VideoPlayLayout extends FrameLayout implements VideoPlayContract.View, Player, View.OnClickListener {
    private final String TAG = "VideoPlayLayout";
    private static final int RESETPLATBACKSTATE = 100;
    private static final float DEFAULT_SCALE_VALUE = 1.0f;

    private RelativeLayout mPlayerLayout;
    private NvsLiveWindow mLiveWindow;
    private DrawRect mDrawRect;
    private TextView mSubtitleView;

    private NvsStreamingContext mStreamingContext;
    private NvsTimeline mTimeline;
    private boolean mPlayBarVisibleState = true;

    // 播放开始标识
    private long mPlayStartFlag = -1;
    private VideoPlayPresenter mPresenter;
    private PlayController mPlayController;
    private boolean mIsVisible = false;
    private boolean mIsOnClickListener = false;


    private boolean isLocked = false;
    private boolean isDownStopPlayVideo = false;
    private boolean isShowPlayButton = true;

    private int mEditMode = 0;
    /*普通字幕*/
    private NvsTimelineCaption mCurCaption;
    /*组合字幕*/
    private NvsTimelineCompoundCaption mCurCompoundCaption;
    private OnCompoundCaptionListener mCompoundCaptionListener;
    private EditCallBack.VideoCaptionTextEditListener mCaptionTextEditListener;
    private OnStickerMuteListener mStickerMuteListener;

    private NvsTimelineVideoFx mTimelineVideoFx;
    /*动态帖子*/
    private NvsTimelineAnimatedSticker mCurAnimateSticker;
    private int mStickerMuteIndex = 0;

    private WaterMarkChangeListener waterMarkChangeListener;

    //liveWindow 实际view中坐标点
    private List<PointF> pointFListLiveWindow;

    public NvsLiveWindow getmLiveWindow() {
        return mLiveWindow;
    }

    public RelativeLayout getmPlayerLayout() {
        return mPlayerLayout;
    }

    private EditCallBack.AssetEditListener mAssetEditListener;


    public void setPlayController(PlayController mPlayController) {
        this.mPlayController = mPlayController;
    }

    public VideoPlayLayout(Context context) {
        this(context, null);
    }

    public VideoPlayLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoPlayLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        this.setKeepScreenOn(true);
        mStreamingContext = MoStreamingContext.getInstance().getNvsStreamingContext();
        mTimeline = TimelineUtil.createTimeline();
        if (mTimeline == null)
            return;
        mPresenter = VideoPlayPresenter.create(this);
        updateAudioClip();
        inflate(context, R.layout.video_play_layout, this);
        mDrawRect = findViewById(R.id.draw_rect);
        mPlayerLayout = findViewById(R.id.player_layout);
        mPlayerLayout = findViewById(R.id.player_layout);
        mPlayerLayout.setOnClickListener(this);
        mLiveWindow = findViewById(R.id.liveWindow);
        mSubtitleView = findViewById(R.id.mSubtitleView);
        controllerOperation();
        initDrawRectListener();
    }

    private void initDrawRectListener() {
        mDrawRect.setOnTouchListener(new DrawRect.OnTouchListener() {
            @Override
            public void onDrag(PointF prePointF, PointF nowPointF) {
                 if(!ismIsShowEdit()){
                     return;
                 }
                /* 坐标转换
                 *
                 * SDK接口所使用的坐标均是Canonical坐标系内的坐标，而我们在程序中所用是的
                 * 一般是Android View 坐标系里面的坐标，所以在使用接口的时候需要使用SDK所
                 * 提供的mapViewToCanonical函数将View坐标转换为Canonical坐标，相反的，
                 * 如果想要将Canonical坐标转换为View坐标，则可以使用mapCanonicalToView
                 * 函数进行转换。
                 * */
                PointF pre = mLiveWindow.mapViewToCanonical(prePointF);
                PointF p = mLiveWindow.mapViewToCanonical(nowPointF);
                PointF timeLinePointF = new PointF(p.x - pre.x, p.y - pre.y);
                if (mEditMode == Constants.EDIT_MODE_CAPTION) {
                    // 移动字幕
                    if (mCurCaption != null) {
                        //mTimeline.setupInputCacheForCaption(mCurCaption,mStreamingContext.getTimelineCurrentPosition(mTimeline));//解决拖拽字幕跟不上拖拽框的问题
                        mCurCaption.translateCaption(timeLinePointF);
                        updateCaptionCoordinate(mCurCaption);
                        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
                    }
                } else if (mEditMode == Constants.EDIT_MODE_STICKER) { // 贴纸编辑
                    // 移动贴纸
                    if (mCurAnimateSticker != null) {
                        mCurAnimateSticker.translateAnimatedSticker(timeLinePointF);
                        updateAnimateStickerCoordinate(mCurAnimateSticker);
                        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_ANIMATED_STICKER_POSTER);
                    }
                } else if (mEditMode == Constants.EDIT_MODE_WATERMARK) {
                    updateWaterMarkPositionOnDrag(timeLinePointF.x, timeLinePointF.y, mDrawRect.getDrawRect());
                } else if (mEditMode == Constants.EDIT_MODE_COMPOUND_CAPTION) {
                    if (mCurCompoundCaption != null) {
                        mCurCompoundCaption.translateCaption(timeLinePointF);
                        updateCompoundCaptionCoordinate(mCurCompoundCaption);
                        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
                    }
                }
                if (mAssetEditListener != null) {
                    mAssetEditListener.onAssetTranstion();
                }
            }

            @Override
            public void onScaleAndRotate(float scaleFactor, PointF anchor, float angle) {
                if(!ismIsShowEdit()){
                    return;
                }
                /* 坐标转换
                 *
                 * SDK接口所使用的坐标均是Canonical坐标系内的坐标，而我们在程序中所用是的
                 * 一般是Android View 坐标系里面的坐标，所以在使用接口的时候需要使用SDK所
                 * 提供的mapViewToCanonical函数将View坐标转换为Canonical坐标，相反的，
                 *如果想要将Canonical坐标转换为View坐标，则可以使用mapCanonicalToView
                 * 函数进行转换。
                 * */
                PointF assetAnchor = mLiveWindow.mapViewToCanonical(anchor);
                if (mEditMode == Constants.EDIT_MODE_CAPTION) {
                    if (mCurCaption != null) {
                        //mTimeline.setupInputCacheForCaption(mCurCaption,mStreamingContext.getTimelineCurrentPosition(mTimeline));//解决拖拽字幕跟不上拖拽框的问题
                        // 放缩字幕
                        mCurCaption.scaleCaption(scaleFactor, assetAnchor);
                        // 旋转字幕
                        mCurCaption.rotateCaption(angle);
                        updateCaptionCoordinate(mCurCaption);
                        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
                    }
                } else if (mEditMode == Constants.EDIT_MODE_STICKER) { // 贴纸编辑
                    // 放缩贴纸
                    if (mCurAnimateSticker != null) {
                        //缩放贴纸
                        mCurAnimateSticker.scaleAnimatedSticker(scaleFactor, assetAnchor);
                        //旋转贴纸
                        mCurAnimateSticker.rotateAnimatedSticker(angle);
                        updateAnimateStickerCoordinate(mCurAnimateSticker);
                        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_ANIMATED_STICKER_POSTER);
                    }
                } else if (mEditMode == Constants.EDIT_MODE_WATERMARK) {
                    updateWaterMarkPositionOnScaleAndRotate(scaleFactor, anchor, angle, mDrawRect.getDrawRect());
                } else if (mEditMode == Constants.EDIT_MODE_COMPOUND_CAPTION) {
                    if (mCurCompoundCaption != null) {
                        mCurCompoundCaption.scaleCaption(scaleFactor, assetAnchor);
                        // 旋转字幕
                        mCurCompoundCaption.rotateCaption(angle, assetAnchor);
                        float scaleX = mCurCompoundCaption.getScaleX();
                        float scaleY = mCurCompoundCaption.getScaleY();
                        if (scaleX <= DEFAULT_SCALE_VALUE && scaleY <= DEFAULT_SCALE_VALUE) {
                            mCurCompoundCaption.setScaleX(DEFAULT_SCALE_VALUE);
                            mCurCompoundCaption.setScaleY(DEFAULT_SCALE_VALUE);
                        }
                        updateCompoundCaptionCoordinate(mCurCompoundCaption);
                        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
                    }
                }
                if (mAssetEditListener != null) {
                    mAssetEditListener.onAssetScale();
                }
            }

            @Override
            public void onDel() {
                if(!ismIsShowEdit()){
                    return;
                }
                if (mAssetEditListener != null) {
                    mAssetEditListener.onAssetDelete();
                }
            }

            @Override
            public void onTouchDown(PointF curPoint) {
                if(!ismIsShowEdit()){
                    return;
                }
                if (mAssetEditListener != null) {
                    mAssetEditListener.onTouchDown();
                }
                if (mAssetEditListener != null) {
                    mStreamingContext.stop();
                    mAssetEditListener.onAssetSelected(curPoint);
                }
            }

            @Override
            public void onTouchUp() {
                if(!ismIsShowEdit()){
                    return;
                }
                if (mAssetEditListener != null) {
                    mAssetEditListener.onTouchUp();
                }
            }

            @Override
            public void onAlignClick() {
                if(!ismIsShowEdit()){
                    return;
                }
                if (mEditMode == Constants.EDIT_MODE_CAPTION
                        && mCurCaption != null) {
                    switch (mCurCaption.getTextAlignment()) {
                        case NvsTimelineCaption.TEXT_ALIGNMENT_LEFT:
                            mCurCaption.setTextAlignment(NvsTimelineCaption.TEXT_ALIGNMENT_CENTER);  //居中对齐
                            setAlignIndex(1);
                            break;
                        case NvsTimelineCaption.TEXT_ALIGNMENT_CENTER:
                            mCurCaption.setTextAlignment(NvsTimelineCaption.TEXT_ALIGNMENT_RIGHT);  //居右对齐
                            setAlignIndex(2);
                            break;

                        case NvsTimelineCaption.TEXT_ALIGNMENT_RIGHT:
                            mCurCaption.setTextAlignment(NvsTimelineCaption.TEXT_ALIGNMENT_LEFT);  //左对齐
                            setAlignIndex(0);
                            break;
                    }
                    seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
                    if (mAssetEditListener != null) {
                        mAssetEditListener.onAssetAlign(mCurCaption.getTextAlignment());
                    }
                }
            }

            @Override
            public void onHorizFlipClick() {
                if(!ismIsShowEdit()){
                    return;
                }
                if (mEditMode == Constants.EDIT_MODE_STICKER) {
                    if (mCurAnimateSticker == null)
                        return;
                    // 贴纸水平翻转
                    boolean isHorizFlip = !mCurAnimateSticker.getHorizontalFlip();
                    mCurAnimateSticker.setHorizontalFlip(isHorizFlip);
                    updateAnimateStickerCoordinate(mCurAnimateSticker);
                    seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_ANIMATED_STICKER_POSTER);
                    if (mAssetEditListener != null) {
                        mAssetEditListener.onAssetHorizFlip(isHorizFlip);
                    }
                }
            }

            @Override
            public void onBeyondDrawRectClick() {
//                mPlayButton.callOnClick();
            }
        });

        mDrawRect.setDrawRectClickListener(new DrawRect.onDrawRectClickListener() {
            @Override
            public void onDrawRectClick(int captionIndex) {
                if(!ismIsShowEdit()){
                    return;
                }
                if (mEditMode == Constants.EDIT_MODE_CAPTION) {
                    if (mCaptionTextEditListener != null) {
                        mCaptionTextEditListener.onCaptionTextEdit();
                    }
                } else if (mEditMode == Constants.EDIT_MODE_COMPOUND_CAPTION) {
                    if (mCompoundCaptionListener != null) {
                        mCompoundCaptionListener.onCaptionIndex(captionIndex);
                    }
                }
            }
        });

        mDrawRect.setStickerMuteListenser(new DrawRect.onStickerMuteListenser() {
            @Override
            public void onStickerMute() {
                if(!ismIsShowEdit()){
                    return;
                }
                if (mCurAnimateSticker == null)
                    return;
                mStickerMuteIndex = mStickerMuteIndex == 0 ? 1 : 0;
                float volumeGain = mStickerMuteIndex == 0 ? 1.0f : 0.0f;
                mCurAnimateSticker.setVolumeGain(volumeGain, volumeGain);
                setStickerMuteIndex(mStickerMuteIndex);
                if (mStickerMuteListener != null)
                    mStickerMuteListener.onStickerMute();
            }
        });
    }

    public void setEditMode(int mode) {
        mEditMode = mode;
    }

    public void setAlignIndex(int index) {
        mDrawRect.setAlignIndex(index);
    }

    //字幕API
    public void setCurCaption(NvsTimelineCaption caption) {
        mCurCaption = caption;
    }

    // 更新字幕在视图上的坐标
    public void updateCaptionCoordinate(NvsTimelineCaption caption) {
        if (caption != null) {
            // 获取字幕的原始包围矩形框变换后的顶点位置
            List<PointF> list = caption.getBoundingRectangleVertices();
            if (list == null || list.size() < 4)
                return;

            List<PointF> newList = getAssetViewVerticesList(list);
            mDrawRect.setDrawRect(newList, Constants.EDIT_MODE_CAPTION);
        }
    }

    public NvsTimelineCaption getCurCaption() {
        return mCurCaption;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //贴纸API
    public void setCurAnimateSticker(NvsTimelineAnimatedSticker animateSticker) {
        mCurAnimateSticker = animateSticker;
    }

    public void setStickerMuteIndex(int index) {
        mStickerMuteIndex = index;
        mDrawRect.setStickerMuteIndex(index);
    }

    public NvsTimelineAnimatedSticker getCurAnimateSticker() {
        return mCurAnimateSticker;
    }

    // 更新贴纸在视图上的坐标
    public void updateAnimateStickerCoordinate(NvsTimelineAnimatedSticker animateSticker) {
        if (animateSticker != null) {
            // 获取贴纸的原始包围矩形框变换后的顶点位置
            List<PointF> list = animateSticker.getBoundingRectangleVertices();
            if (list == null || list.size() < 4)
                return;
            boolean isHorizonFlip = animateSticker.getHorizontalFlip();
            if (isHorizonFlip) {//如果已水平翻转，需要对顶点数据进行处理
                Collections.swap(list, 0, 3);
                Collections.swap(list, 1, 2);
            }
            List<PointF> newList = getAssetViewVerticesList(list);
            mDrawRect.setDrawRect(newList, Constants.EDIT_MODE_STICKER);
        }
    }

    //设置贴纸选择框显隐
    public void changeStickerRectVisible() {
        if (mEditMode == Constants.EDIT_MODE_STICKER) {
            setDrawRectVisible(isSelectedAnimateSticker() ? View.VISIBLE : View.GONE);
        }
    }

    //在liveWindow上手动选择贴纸
    public void selectAnimateStickerByHandClick(PointF curPoint) {
        if (mTimeline == null) {
            return;
        }
        List<NvsTimelineAnimatedSticker> stickerList = mTimeline.getAnimatedStickersByTimelinePosition(mStreamingContext.getTimelineCurrentPosition(mTimeline));
        if (stickerList.size() <= 1)
            return;

        for (int j = 0; j < stickerList.size(); j++) {
            NvsTimelineAnimatedSticker sticker = stickerList.get(j);
            List<PointF> list = sticker.getBoundingRectangleVertices();
            List<PointF> newList = getAssetViewVerticesList(list);
            boolean isSelected = mDrawRect.clickPointIsInnerDrawRect(newList, (int) curPoint.x, (int) curPoint.y);
            if (isSelected) {
                mDrawRect.setDrawRect(newList, Constants.EDIT_MODE_STICKER);
                mCurAnimateSticker = sticker;
                break;
            }
        }
    }

    public void setMuteVisible(boolean hasAudio) {
        mDrawRect.setMuteVisible(hasAudio);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //组合字幕API
    public void setCurCompoundCaption(NvsTimelineCompoundCaption caption) {
        mCurCompoundCaption = caption;
    }

    public NvsTimelineCompoundCaption getCurrCompoundCaption() {
        return mCurCompoundCaption;
    }

    // 更新组合字幕在视图上的坐标
    public void updateCompoundCaptionCoordinate(NvsTimelineCompoundCaption caption) {
        if (caption != null) {
            // 获取字幕的原始包围矩形框变换后的顶点位置
            List<PointF> list = caption.getCompoundBoundingVertices(NvsTimelineCompoundCaption.BOUNDING_TYPE_FRAME);
            if (list == null || list.size() < 4) {
                return;
            }

            List<PointF> newList = getAssetViewVerticesList(list);
            List<List<PointF>> newSubCaptionList = new ArrayList<>();
            int subCaptionCount = caption.getCaptionCount();
            for (int index = 0; index < subCaptionCount; index++) {
                List<PointF> subList = caption.getCaptionBoundingVertices(index, NvsTimelineCompoundCaption.BOUNDING_TYPE_TEXT);
                if (subList == null || subList.size() < 4) {
                    continue;
                }
                List<PointF> newSubList = getAssetViewVerticesList(subList);
                newSubCaptionList.add(newSubList);
            }
            mDrawRect.setCompoundDrawRect(newList, newSubCaptionList, Constants.EDIT_MODE_COMPOUND_CAPTION);
        }
    }

    public void changeCompoundCaptionRectVisible() {
        if (mEditMode == Constants.EDIT_MODE_COMPOUND_CAPTION) {
            setDrawRectVisible(isSelectedCompoundCaption() ? View.VISIBLE : View.GONE);
        }
    }

    public void setIsShowEdit(boolean isShow) {
        mDrawRect.setIsShowEdit(isShow);
        if(isShow){
            mDrawRect.setVisibility(VISIBLE);
        }else {
            mDrawRect.setVisibility(GONE);
        }
    }
    public boolean ismIsShowEdit() {
        return mDrawRect.ismIsShowEdit();
    }


    public void setDrawRectVisible(int visibility) {
        mDrawRect.setVisibility(visibility);
    }

    private boolean isSelectedCompoundCaption() {
        long curPosition = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        return mCurCompoundCaption != null
                && curPosition >= mCurCompoundCaption.getInPoint()
                && curPosition <= mCurCompoundCaption.getOutPoint();
    }

    private boolean isSelectedCaption() {
        long curPosition = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        return mCurCaption != null
                && curPosition >= mCurCaption.getInPoint()
                && curPosition <= mCurCaption.getOutPoint();
    }

    private boolean isSelectedAnimateSticker() {
        long curPosition = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        return mCurAnimateSticker != null
                && curPosition >= mCurAnimateSticker.getInPoint()
                && curPosition <= mCurAnimateSticker.getOutPoint();
    }

    //在liveWindow上手动选择字幕
    public void selectCompoundCaptionByHandClick(PointF curPoint) {
        if (mTimeline == null) {
            return;
        }
        List<NvsTimelineCompoundCaption> captionList = mTimeline.getCompoundCaptionsByTimelinePosition(mStreamingContext.getTimelineCurrentPosition(mTimeline));
        if (captionList.size() <= 1)
            return;

        for (int j = 0; j < captionList.size(); j++) {
            NvsTimelineCompoundCaption caption = captionList.get(j);
            List<PointF> list = caption.getCompoundBoundingVertices(NvsTimelineCompoundCaption.BOUNDING_TYPE_FRAME);
            List<PointF> newList = getAssetViewVerticesList(list);
            boolean isSelected = mDrawRect.clickPointIsInnerDrawRect(newList, (int) curPoint.x, (int) curPoint.y);
            if (isSelected) {
                mDrawRect.setDrawRect(newList, Constants.EDIT_MODE_COMPOUND_CAPTION);
                mCurCompoundCaption = caption;
                break;
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 根据宽高获取livewindow的四个角坐标
     */
    private void setPointFListLiveWindow(int w, int h) {
//        int x0 = Math.abs(w - h) / 2;
        int x0 = 0;
        int x1 = w;
        int y0 = 0;
        int y1 = h;
        Log.e(TAG, "liveWindow的四个角坐标  " + x0 + "  " + x1 + "  " + y0 + "  " + y1);
        pointFListLiveWindow = setFourPointToList(x0, x1, y0, y1);
    }

    private List<PointF> getAssetViewVerticesList(List<PointF> verticesList) {
        List<PointF> newList = new ArrayList<>();
        for (int i = 0; i < verticesList.size(); i++) {
            PointF pointF = mLiveWindow.mapCanonicalToView(verticesList.get(i));
            newList.add(pointF);
        }
        return newList;
    }

    public void setDrawRect(List<PointF> newList) {
        mDrawRect.setDrawRect(newList, Constants.EDIT_MODE_WATERMARK);
    }

    public List<PointF> getDrawRect() {
        return mDrawRect.getDrawRect();
    }

    /**
     * 四个点坐标转化到list，从左上逆时针为0123
     */
    private List<PointF> setFourPointToList(float x0, float x1, float y0, float y1) {
        List<PointF> newList = new ArrayList<>();
        newList.add(new PointF(x0, y0));
        newList.add(new PointF(x0, y1));
        newList.add(new PointF(x1, y1));
        newList.add(new PointF(x1, y0));
        return newList;
    }

    private boolean checkInLiveWindow(List<PointF> newList) {
        if (pointFListLiveWindow != null) {
            float minX = pointFListLiveWindow.get(0).x;
            float maxX = pointFListLiveWindow.get(2).x;
            float minY = pointFListLiveWindow.get(0).y;
            float maxY = pointFListLiveWindow.get(2).y;
            for (PointF pointF : newList) {
                if (pointF.x < minX || pointF.x > maxX || pointF.y < minY || pointF.y > maxY) {
                    Log.e(TAG, "checkInLiveWindow " + minX + "       " + pointF.x + "      " + maxX);
                    Log.e(TAG, "checkInLiveWindow " + minY + "       " + pointF.y + "      " + maxY);
                    return false;
                }
            }
        }
        return true;
    }

    private void updateWaterMarkPositionOnDrag(float x, float y, List<PointF> list) {
        List<PointF> newList = new ArrayList<>();
        for (PointF pointF : list) {
            newList.add(new PointF(pointF.x + x, pointF.y - y));
        }
        if (checkInLiveWindow(newList)) {
            mDrawRect.setDrawRect(newList, Constants.EDIT_MODE_WATERMARK);
            if (waterMarkChangeListener != null) {
                waterMarkChangeListener.onDrag(newList);
            }
        }
    }

    /**
     * @param scaleDregree 缩放比例
     * @param centerPoint  中心坐标
     * @param angle        旋转角度
     * @param list         方框坐标点
     */
    private void updateWaterMarkPositionOnScaleAndRotate(float scaleDregree, PointF centerPoint, float angle, List<PointF> list) {
        float width = Math.abs(list.get(0).x - list.get(3).x);
        float height = Math.abs(list.get(0).y - list.get(1).y);
        float x0 = centerPoint.x - width / 2 * scaleDregree;
        float x1 = centerPoint.x + width / 2 * scaleDregree;
        float y0 = centerPoint.y - height / 2 * scaleDregree;
        float y1 = centerPoint.y + height / 2 * scaleDregree;
        refreshWaterMarkByFourPoint(x0, x1, y0, y1);
    }

    /**
     * 四个点就能确定一个矩形 (x0,y0) (x0,y1) (x1,y1) (x1,y0)
     */
    private void refreshWaterMarkByFourPoint(float x0, float x1, float y0, float y1) {
        List<PointF> newList = setFourPointToList(x0, x1, y0, y1);
        if (checkInLiveWindow(newList)) {
            mDrawRect.setDrawRect(newList, Constants.EDIT_MODE_WATERMARK);
            if (waterMarkChangeListener != null) {
                waterMarkChangeListener.onScaleAndRotate(newList);
            }
        }
    }

    public boolean curPointIsInnerDrawRect(int xPos, int yPos) {
        return mDrawRect.curPointIsInnerDrawRect(xPos, yPos);
    }

    public void changeCaptionRectVisible() {
        if (mEditMode == Constants.EDIT_MODE_CAPTION) {
            setDrawRectVisible(isSelectedCaption() ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initData();
        if (mLiveWindow == null) {
            return;
        }
        //
        mLiveWindow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowPlayButton) {
                    pause();
                }
            }
        });

        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mPlayController != null) {
                    mPlayController.onPrepared();
                }

            }
        }, 100L);

    }

    public void updateAudioClip() {

    }

    private void initData() {
        updateLiveWindow(0, 0);
    }

    public void setDownStopPlayVideo(boolean downStopPlayVideo) {
        isDownStopPlayVideo = downStopPlayVideo;
    }


    public void updateLiveWindow(int width, int height) {
        if (mPlayerLayout == null) {
            return;
        }
        int ratio = TimelineData.instance().getMakeRatio();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mPlayerLayout.getLayoutParams();
        if (mPresenter != null) {
            mPresenter.setLiveWindowRatio(layoutParams, ratio, width, height);
        }
//        connectTimelineWithLiveWindow();
    }

    public void doLiveWindowRatio(RelativeLayout.LayoutParams layoutParams) {
        mPlayerLayout.setLayoutParams(layoutParams);
        mLiveWindow.setFillMode(NvsLiveWindow.FILLMODE_PRESERVEASPECTFIT);
    }

    @Override
    public void showRunes(List<? extends Mark> properties, long time) {

//        if(properties.size()>0){
//            TitleSubtitleMark mark=(TitleSubtitleMark) properties.get(0);
//            mSubtitleView.setText(mark.getText());
//            mSubtitleView.setVisibility(VISIBLE);
//        }else {
//            mSubtitleView.setVisibility(GONE);
//        }
//            if (getCurrentEngineState() == NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
//                mRuneView.setLocked(true);
//            } else {
//                mRuneView.setLocked(false);
//            }


    }

    @Override
    public void showEffectView(Mark info, long time, boolean locked) {
        isLocked = locked;
        mPresenter.setSticker(time, locked);
    }

    public void updateCurPlayTime(long time) {

        if (mPlayController != null) {
            mPlayController.onPlayProgress(time / (float) VideoEditManger.VIDEO_microsecond_TIME);
        }
    }


    public NvsStreamingContext getStreamingContext() {
        return mStreamingContext;
    }

    public NvsTimeline getTimeline() {
        return mTimeline;
    }

    public void setTimeline(NvsTimeline timeline) {
        mTimeline = timeline;
    }


    //连接时间线跟liveWindow
    public void connectTimelineWithLiveWindow() {
        if (mStreamingContext == null || mTimeline == null || mLiveWindow == null) {
            return;
        }
        mStreamingContext.setCompileCallback(new NvsStreamingContext.CompileCallback() {
            @Override
            public void onCompileProgress(NvsTimeline nvsTimeline, int i) {

            }

            @Override
            public void onCompileFinished(NvsTimeline nvsTimeline) {
            }

            @Override
            public void onCompileFailed(NvsTimeline nvsTimeline) {

            }
        });
        mStreamingContext.setPlaybackCallback(new NvsStreamingContext.PlaybackCallback() {
            @Override
            public void onPlaybackPreloadingCompletion(NvsTimeline nvsTimeline) {

            }

            @Override
            public void onPlaybackStopped(NvsTimeline nvsTimeline) {
//                if (mVideoFragmentCallBack != null) {
//                    mVideoFragmentCallBack.playStopped(nvsTimeline);
//                }

            }

            @Override
            public void onPlaybackEOF(NvsTimeline nvsTimeline) {
                if (mPlayBarVisibleState) {
                    m_handler.sendEmptyMessage(RESETPLATBACKSTATE);
                }
//                if (mVideoFragmentCallBack != null) {
//                    mVideoFragmentCallBack.playBackEOF(nvsTimeline);
//                }
            }
        });

        mStreamingContext.setPlaybackCallback2(new NvsStreamingContext.PlaybackCallback2() {
            @Override
            public void onPlaybackTimelinePosition(NvsTimeline nvsTimeline, long cur_position) {
                setPlayButton(GONE);
                if (mPlayBarVisibleState) {
                    updateCurPlayTime(cur_position);
                }
//                if (mVideoFragmentCallBack != null) {
//                    mVideoFragmentCallBack.playbackTimelinePosition(nvsTimeline, cur_position);
//                }

            }
        });

        mStreamingContext.setStreamingEngineCallback(new NvsStreamingContext.StreamingEngineCallback() {
            @Override
            public void onStreamingEngineStateChanged(int i) {

                if (i == NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
//                    seekTimeline(0, 0);

                    startHidePlayBarTimer(false);
                } else if (i == NvsStreamingContext.STREAMING_ENGINE_STATE_SEEKING) {
//                    seekTimeline(0, 0);
                    if (mPlayController != null) {
                        mPlayController.onComplete();
                    }
                } else {
                    startHidePlayBarTimer(true);
                }
//                if (mVideoFragmentCallBack != null) {
//                    mVideoFragmentCallBack.streamingEngineStateChanged(i);
//                }
            }

            @Override
            public void onFirstVideoFramePresented(NvsTimeline nvsTimeline) {

            }
        });
        mStreamingContext.connectTimelineWithLiveWindow(mTimeline, mLiveWindow);
        updateCurPlayTime(0);
    }


    @Override
    public void onDestroy() {
        m_handler.removeCallbacksAndMessages(null);
        TimelineUtil.removeTimeline(mTimeline);
        mTimeline = null;

    }


    @Override
    public void setVideoIndex(int index) {

    }

    @Override
    public void start() {
//        connectTimelineWithLiveWindow();
        if (mStreamingContext == null) {
            return;
        }
        long stamp = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        updateCurPlayTime(stamp);
    }

    public void playVideo(long startTime, long endTime) {

        // 播放视频
        mStreamingContext.playbackTimeline(mTimeline, startTime, endTime, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, true, NvsStreamingContext.STREAMING_ENGINE_PLAYBACK_FLAG_LOW_PIPELINE_SIZE);
    }

    public void playVideo(int position) {
        if (mTimeline == null) return;
        NvsVideoTrack videoTrackByIndex = mTimeline.getVideoTrackByIndex(0);
        if (videoTrackByIndex == null)
            return;
        int videoCount = videoTrackByIndex.getClipCount();

        if (videoCount < position)
            return;
        NvsVideoClip clip = videoTrackByIndex.getClipByIndex(position);
        if (clip == null)
            return;
        long inPoint = clip.getInPoint();
        seekTimeline(inPoint, 0);
        playVideo(inPoint, clip.getOutPoint());
    }


    public void playVideo() {
        playVideo(0, mTimeline.getDuration());
    }

    public void playVideo(long curTime) {
        playVideo(curTime, mTimeline.getDuration());
    }

    @Override
    public void pause() {
        stopEngine();
        setPlayButton(View.VISIBLE);
        if (mPlayController != null) {
            mPlayController.onPauseVideo();
        }
    }

    @Override
    public long getDuration() {
        return mTimeline.getDuration();
    }

    @Override
    public long getCurrentPosition() {
        if (mStreamingContext == null) {
            return 0;
        }
        long curTime = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        return curTime;
    }

    //预览
    public void seekTimeline(long timestamp, int seekShowMode) {

        /* seekTimeline
         * param1: 当前时间线
         * param2: 时间戳 取值范围为  [0, timeLine.getDuration()) (左闭右开区间)
         * param3: 图像预览模式
         * param4: 引擎定位的特殊标志
         * */
        if (mStreamingContext == null) {
            return;
        }
        mStreamingContext.seekTimeline(mTimeline, timestamp, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, seekShowMode);
        setPlayButton(VISIBLE);
    }

    //预览
    public void seekTimeline(long timestamp) {
        seekTimeline(timestamp, 0);
    }

    //预览
    public void seekTimelineDelay(final long timestamp) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                seekTimeline(timestamp, 0);

            }
        }, 400L);

    }


    @Override
    public boolean isPlaying() {
        return false;
    }

    // 获取当前引擎状态
    public int getCurrentEngineState() {
        return mStreamingContext.getStreamingEngineState();
    }

    //停止引擎
    public void stopEngine() {
        if (mStreamingContext != null) {
            mStreamingContext.stop();//停止播放
        }
    }

    public void setShowBottom(boolean isVisible) {

        mIsVisible = isVisible;
    }

    public void setIsOnClickListener(boolean mIsOnClickListener) {
        this.mIsOnClickListener = mIsOnClickListener;
    }

    public void setShowPlayButton(boolean showPlayButton) {
        isShowPlayButton = showPlayButton;
    }

    private void controllerOperation() {

    }

    private void setPlayButton(int visibility) {

    }

    public void setPlayBarVisibleState(boolean mPlayBarVisibleState) {
        this.mPlayBarVisibleState = mPlayBarVisibleState;
    }

    public void startHidePlayBarTimer(boolean start) {
        if (mPlayBarVisibleState) {
            m_hidePlayBarTimer.cancel();
            if (start) {
                m_hidePlayBarTimer.start();
            }
        }
    }

    private Handler m_handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case RESETPLATBACKSTATE:

                    updateCurPlayTime(0);
                    seekTimeline(0, 0);
                    // 播放进度条显示
                    if (mPlayBarVisibleState) {
                        mPlayStartFlag = -1;
                        startHidePlayBarTimer(true);
                        setPlayButton(View.VISIBLE);
                    }
                    if (mPlayController != null) {
                        mPlayController.onComplete();
                    }
                    break;
            }
            return false;
        }
    });

    private CountDownTimer m_hidePlayBarTimer = new CountDownTimer(3000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            // 播放进度条显示
            if (mPlayBarVisibleState && mIsVisible) {
                mPlayStartFlag = -1;
            }
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
            if (mOnLayoutClick != null) {
                mOnLayoutClick.onLayoutDownClick();
            }
            if (isDownStopPlayVideo) {
                pause();
            }
            return super.dispatchTouchEvent(ev);

        }
        if (ev.getActionMasked() == MotionEvent.ACTION_UP) {
            Log.d(TAG, "===ACTION_UP");
            if (mOnLayoutClick != null) {
                mOnLayoutClick.onLayoutClick();
            }
            if (isDownStopPlayVideo) {

                seekTimeline(getCurrentPosition());
            }
            return super.dispatchTouchEvent(ev);

        }
        return super.dispatchTouchEvent(ev);
    }


    @Override
    public void onClick(View v) {
        Log.d(TAG, "==onClick=" + v.toString());
    }

    public interface OnLayoutClick {
        void onLayoutClick();

        void onLayoutDownClick();
    }

    OnLayoutClick mOnLayoutClick;

    public void setmOnLayoutClick(OnLayoutClick mOnLayoutClick) {
        this.mOnLayoutClick = mOnLayoutClick;
    }

    public void setCompoundCaptionListener(OnCompoundCaptionListener compoundCaptionListener) {
        this.mCompoundCaptionListener = compoundCaptionListener;
    }

    //字幕文本修改回调
    public interface OnCompoundCaptionListener {
        void onCaptionIndex(int captionIndex);
    }

    public interface WaterMarkChangeListener {
        void onDrag(List<PointF> list);

        void onScaleAndRotate(List<PointF> curPoint);
    }

    //LiveWindowd点击回调
    public interface OnStickerMuteListener {
        void onStickerMute();
    }

    public void setAssetEditListener(EditCallBack.AssetEditListener assetEditListener) {
        this.mAssetEditListener = assetEditListener;
    }

    public void setCaptionTextEditListener(EditCallBack.VideoCaptionTextEditListener captionTextEditListener) {
        this.mCaptionTextEditListener = captionTextEditListener;
    }

    public void setStickerMuteListener(OnStickerMuteListener stickerMuteListener) {
        this.mStickerMuteListener = stickerMuteListener;
    }
}
