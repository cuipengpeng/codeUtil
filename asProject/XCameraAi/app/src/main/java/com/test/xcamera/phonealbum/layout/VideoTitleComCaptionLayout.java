package com.test.xcamera.phonealbum.layout;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.editvideo.dataInfo.CompoundCaptionInfo;
import com.editvideo.dataInfo.TimelineData;
import com.test.xcamera.R;
import com.test.xcamera.phonealbum.adapter.EditCompoundCaptionAdapter;
import com.test.xcamera.phonealbum.bean.AssetItem;
import com.test.xcamera.phonealbum.usecase.VideoCompoundCaption;
import com.test.xcamera.phonealbum.usecase.VideoCompoundSticker;
import com.test.xcamera.phonealbum.widget.OnItemClickListener;
import com.test.xcamera.phonealbum.widget.VideoEditManger;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineCompoundCaption;

import java.util.ArrayList;

/**
 * 组合字幕
 */
public class VideoTitleComCaptionLayout extends LinearLayout implements OnItemClickListener {
    private OnVideoTitlePanelCallBack mOnVideoTitlePanelCallBack;
    private ImageView mApplyImage;
    private RecyclerView mRecyclerView;
    private ArrayList<AssetItem> mComCaptionStyleList = new ArrayList<>();
    private EditCompoundCaptionAdapter mCompoundCaptionAdapter;
    private NvsTimelineCompoundCaption mCurCaption;
    private NvsTimelineCompoundCaption mAddComCaption;

    private NvsTimeline mTimeline;
    private NvsStreamingContext mStreamingContext;

    private VideoCompoundCaption mVideoCompoundCaption;

    public VideoTitleComCaptionLayout(Context context, NvsTimeline timeline, NvsStreamingContext streamingContext) {
        this(context, null);
        mTimeline = timeline;
        mStreamingContext = streamingContext;
        init(context);
    }

    public VideoTitleComCaptionLayout(Context context, NvsTimeline timeline, int type) {
        this(context, null);
        mTimeline = timeline;
        init(context);
    }

    public VideoTitleComCaptionLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private void init(Context context) {
        View root = inflate(context, R.layout.view_video_title_com_caption, this);
        mApplyImage = root.findViewById(R.id.tv_apply);
        mRecyclerView = root.findViewById(R.id.mRecyclerView);
        mCompoundCaptionAdapter = new EditCompoundCaptionAdapter(getContext());
        mCompoundCaptionAdapter.setOnItemClickListener(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mCompoundCaptionAdapter);
        mApplyImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnVideoTitlePanelCallBack != null) {
                    mOnVideoTitlePanelCallBack.OnConfirmBack(VideoTitlePanelLayout.VIDEO_TITLE_COM_CAPTION);
                }
            }
        });
        if (mVideoCompoundCaption == null) {
            mVideoCompoundCaption = VideoCompoundCaption.getInstance();
        }
        mVideoCompoundCaption.getComCaptionList(list -> {
            showVideoEditPanel(list);
        });
    }

    public void showVideoEditPanel(ArrayList<AssetItem> list) {
        mComCaptionStyleList.clear();
        mComCaptionStyleList.addAll(list);
        String uuid = "";
        if (TimelineData.init().getCompoundCaptionArray() != null && TimelineData.init().getCompoundCaptionArray().size() > 0) {
            uuid = TimelineData.init().getCompoundCaptionArray().get(0).getCaptionStyleUuid();
        }
        mCompoundCaptionAdapter.setAssetList(list, uuid);
        mCompoundCaptionAdapter.notifyDataSetChanged();
    }

    public void setOnVideoTitlePanelCallBack(OnVideoTitlePanelCallBack mOnVideoTitlePanelCallBack) {
        this.mOnVideoTitlePanelCallBack = mOnVideoTitlePanelCallBack;
    }

    public void setSelectPosition(String uuid) {
        mCompoundCaptionAdapter.setSelectedPos(uuid);
    }


    @Override
    public void onItemClick(View view, int pos, boolean isAdd) {
        if (isAdd) {
            VideoCompoundSticker.getInstance().removeAllSticker(mTimeline);
            mVideoCompoundCaption.removeTimeCompoundCaption(mTimeline, VideoCompoundCaption.COMPOUND_CAPTION_START_TIME, VideoCompoundCaption.COMPOUND_CAPTION_END_TIME);
            mVideoCompoundCaption.removeTimeAllCompoundCaption(mTimeline, VideoCompoundCaption.COMPOUND_CAPTION_END_TIME / 2);
            String captionUuid = mComCaptionStyleList.get(pos).getAsset().uuid;
            CompoundCaptionInfo info = new CompoundCaptionInfo();
            info.setCaptionStyleUuid(captionUuid);
            info.setInPoint(VideoCompoundCaption.COMPOUND_CAPTION_START_TIME);
            info.setOutPoint(VideoCompoundCaption.COMPOUND_CAPTION_END_TIME);
            info.setCaptionZVal((int) mVideoCompoundCaption.getCurCaptionZVal(mTimeline));

            mCurCaption = VideoEditManger.addCompoundCaption(mTimeline, info);
            if (mCurCaption != null) {
                CompoundCaptionInfo captionInfo = VideoCompoundCaption.saveCompoundCaptionData(mCurCaption);
                if (captionInfo != null) {
                    captionInfo.setCaptionStyleUuid(captionUuid);
                    TimelineData.instance().getCompoundCaptionArray().add(captionInfo);
                    if (mOnVideoTitlePanelCallBack != null) {
                        mOnVideoTitlePanelCallBack.onReviewVideo(0);
                    }
                    onReviewVideo(0);
                }

            }
        } else {
            if (mOnVideoTitlePanelCallBack != null) {
                mOnVideoTitlePanelCallBack.onReviewVideo(0);
            }
            onReviewVideo(0);
        }


    }

    public void onReviewVideo(long time) {
        if (mStreamingContext != null) {
            mStreamingContext.playbackTimeline(mTimeline, time, mTimeline.getDuration(), NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, true, NvsStreamingContext.STREAMING_ENGINE_PLAYBACK_FLAG_BUDDY_HOST_VIDEO_FRAME);
        }
    }
}
