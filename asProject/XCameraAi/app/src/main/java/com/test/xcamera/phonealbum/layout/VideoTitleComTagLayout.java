package com.test.xcamera.phonealbum.layout;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.test.xcamera.R;

/**
 * 组合字幕
 */
public class VideoTitleComTagLayout extends LinearLayout{
    private OnVideoTitlePanelCallBack mOnVideoTitlePanelCallBack;
    private ImageView mApplyImage;
    private RecyclerView mRecyclerView;
    public VideoTitleComTagLayout(Context context) {
        this(context, null);
        init(context);
    }

    public VideoTitleComTagLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private void init(Context context) {
        View root = inflate(context, R.layout.view_video_title_com_tag, this);
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
    }
    public void showVideoEditPanel(int tag){

    }
    public void setOnVideoTitlePanelCallBack(OnVideoTitlePanelCallBack mOnVideoTitlePanelCallBack) {
        this.mOnVideoTitlePanelCallBack = mOnVideoTitlePanelCallBack;
    }





}
