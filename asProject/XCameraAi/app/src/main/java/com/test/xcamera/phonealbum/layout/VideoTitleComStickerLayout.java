package com.test.xcamera.phonealbum.layout;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.editvideo.dataInfo.StickerInfo;
import com.editvideo.dataInfo.TimelineData;
import com.test.xcamera.R;
import com.test.xcamera.phonealbum.adapter.EditStickerAdapter;
import com.test.xcamera.phonealbum.usecase.VideoCompoundCaption;
import com.test.xcamera.phonealbum.usecase.VideoCompoundSticker;
import com.test.xcamera.phonealbum.widget.OnItemClickListener;
import com.test.xcamera.phonealbum.widget.VideoEditManger;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineAnimatedSticker;

import java.util.ArrayList;

/**
 * 贴纸
 */
public class VideoTitleComStickerLayout extends LinearLayout implements OnItemClickListener {
    private OnVideoTitlePanelCallBack mOnVideoTitlePanelCallBack;
    private ImageView mApplyImage;
    private RecyclerView mRecyclerView;
    private ArrayList<StickerInfo> mStickerInfos = new ArrayList<>();
    private EditStickerAdapter mAdapter;
    private NvsTimeline mTimeline;
    public VideoTitleComStickerLayout(Context context,NvsTimeline timeline,int type) {
        this(context, null);
        mTimeline=timeline;
        init(context);
    }

    public VideoTitleComStickerLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private void init(Context context) {
        View root = inflate(context, R.layout.view_video_title_sticker, this);
        mApplyImage=root.findViewById(R.id.tv_apply);
        mRecyclerView=root.findViewById(R.id.mRecyclerView);
        mApplyImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnVideoTitlePanelCallBack!=null){
                    mOnVideoTitlePanelCallBack.OnConfirmBack(VideoTitlePanelLayout.VIDEO_TITLE_COM_CAPTION);
                }
            }
        });
        mAdapter=new EditStickerAdapter(getContext());
        mAdapter.setOnItemClickListener(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        VideoCompoundSticker.getInstance().getComStickerList(new VideoCompoundSticker.VideoStickerCallaBack() {
            @Override
            public void OnStickerArrayList(ArrayList<StickerInfo> list) {
                mStickerInfos.clear();
                mStickerInfos.addAll(list);
                mAdapter.setAssetList(list);
            }
        });
    }

    public void setOnVideoTitlePanelCallBack(OnVideoTitlePanelCallBack mOnVideoTitlePanelCallBack) {
        this.mOnVideoTitlePanelCallBack = mOnVideoTitlePanelCallBack;
    }


    @Override
    public void onItemClick(View view, int pos,boolean isAdd) {
        VideoCompoundCaption.getInstance().removeTimeCompoundCaption(mTimeline,VideoCompoundCaption.COMPOUND_CAPTION_START_TIME,VideoCompoundCaption.COMPOUND_CAPTION_END_TIME);
        VideoCompoundSticker.getInstance().removeAllSticker(mTimeline);
        StickerInfo stickerInfo=mStickerInfos.get(pos);
        stickerInfo.setAnimateStickerZVal((int)VideoCompoundSticker.getInstance().getCurStickerZVal(mTimeline));
        NvsTimelineAnimatedSticker sticker=VideoEditManger.addComSticker(mTimeline,stickerInfo);
        if(sticker!=null){
            ArrayList<StickerInfo> list=new ArrayList<>();
            list.add(stickerInfo);
            TimelineData.instance().setStickerData(list);
            if(mOnVideoTitlePanelCallBack!=null){
                mOnVideoTitlePanelCallBack.onReviewVideo(0);
            }
        }

    }
}
