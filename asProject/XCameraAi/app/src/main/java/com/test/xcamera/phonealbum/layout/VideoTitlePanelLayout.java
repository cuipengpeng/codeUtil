package com.test.xcamera.phonealbum.layout;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.test.xcamera.R;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;


/**
 * 标题
 */
public class VideoTitlePanelLayout extends LinearLayout implements OnVideoTitlePanelCallBack {
    public static final int VIDEO_TITLE_COM_CAPTION = 0;
    public static final int VIDEO_TITLE_TAG = 1;
    public static final int VIDEO_TITLE_STICKER = 2;
    FrameLayout mVideoEditContentFrame;
    NvsTimeline mTimeline;
    private NvsStreamingContext mStreamingContext;
    private VideoTitleComCaptionLayout mComCaptionLayout;
    private VideoTitleComTagLayout  mComTagLayout;
    private VideoTitleComStickerLayout  mComStickerLayout;
    public VideoTitlePanelLayout(Context context, FrameLayout contentFrame, NvsTimeline timeline,NvsStreamingContext streamingContext) {
        this(context, null);
        mVideoEditContentFrame = contentFrame;
        mTimeline = timeline;
        mStreamingContext=streamingContext;
        init(context);
    }

    public VideoTitlePanelLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private void init(Context context) {
        View root = inflate(context, R.layout.view_video_title_panel, this);
        TextView mVideoComCaption = root.findViewById(R.id.mVideoComCaption);
        mVideoComCaption.setOnClickListener(view -> showVideoEditPanel(VIDEO_TITLE_COM_CAPTION));
        TextView mVideoTag = root.findViewById(R.id.mVideoTag);
        mVideoTag.setVisibility(GONE);
        mVideoTag.setOnClickListener(view -> showVideoEditPanel(VIDEO_TITLE_TAG));
        TextView mVideoAnimatedSticker = root.findViewById(R.id.mVideoAnimatedSticker);
        mVideoAnimatedSticker.setOnClickListener(view -> showVideoEditPanel(VIDEO_TITLE_STICKER));
    }

    public void selectPosition(int tag,int position) {
        if(tag==VIDEO_TITLE_COM_CAPTION){
            if(mComCaptionLayout!=null){
                mComCaptionLayout.setSelectPosition("");
            }
        }
    }
    public void showVideoEditPanel(int tag) {
        if (mVideoEditContentFrame == null) {
            return;
        }
        mVideoEditContentFrame.setOnClickListener(view -> {});
        mVideoEditContentFrame.removeAllViews();
        if(tag==VIDEO_TITLE_COM_CAPTION){
            mComCaptionLayout=new VideoTitleComCaptionLayout(getContext(),mTimeline,tag);
            mComCaptionLayout.setOnVideoTitlePanelCallBack(this);
            mVideoEditContentFrame.addView(mComCaptionLayout);
        }else  if(tag==VIDEO_TITLE_TAG){
            mComTagLayout=new VideoTitleComTagLayout(getContext());
            mComTagLayout.setOnVideoTitlePanelCallBack(this);
            mVideoEditContentFrame.addView(mComTagLayout);
        }else  if(tag==VIDEO_TITLE_STICKER){
            VideoTitleComStickerLayout layout=new VideoTitleComStickerLayout(getContext(),mTimeline,tag);
            layout.setOnVideoTitlePanelCallBack(this);
            mVideoEditContentFrame.addView(layout);
        }
        mVideoEditContentFrame.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.tranlate_dialog_out));
        mVideoEditContentFrame.setVisibility(VISIBLE);

    }

    @Override
    public void OnConfirmBack(int tag) {
        mVideoEditContentFrame.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.tranlate_dialog_in));
        mVideoEditContentFrame.setVisibility(GONE);

    }

    @Override
    public void onReviewVideo(long time) {
        mStreamingContext.playbackTimeline(mTimeline,0, mTimeline.getDuration(), NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, true, NvsStreamingContext.STREAMING_ENGINE_PLAYBACK_FLAG_BUDDY_HOST_VIDEO_FRAME);
    }

}
