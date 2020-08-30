package com.test.xcamera.cameraclip.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.framwork.base.adapter.BaseRecyclerAdapter;
import com.test.xcamera.R;
import com.test.xcamera.cameraclip.bean.VideoSegment;
import com.test.xcamera.constants.LogcatConstants;
import com.test.xcamera.utils.GlideUtils;
import com.test.xcamera.utils.LoggerUtils;
import com.moxiang.common.logging.Logcat;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author: mz
 * Time:  2019/10/30
 */
public class CameraClipEditAdapter extends BaseRecyclerAdapter<VideoSegment, CameraClipEditAdapter.ViewHolder> {

    public CameraClipEditAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = View.inflate(mContext, R.layout.item_camera_clip_edit, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        VideoSegment videoSegment=mData.get(i);
        LoggerUtils.d("获取的url="+videoSegment.getVideoSegmentFilePath());
        if (videoSegment.isSelected()) {
            viewHolder.relativeLayout.setBackgroundResource(R.drawable.video_eidt_item_border_bg);
        } else {
            viewHolder.relativeLayout.setBackgroundResource(R.drawable.circle_corner_transparent_bg);
        }
        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("load down file icon with glide "+ videoSegment.getVideoSegmentFilePath()).out();
        GlideUtils.GlideLoader(mContext,videoSegment.getVideoSegmentFilePath(),viewHolder.ivImage);
//        long duration = videoSegment.getEnd_time()-videoSegment.getStart_time()+500;//+500 是为了让1600ms 变成2s
//        String time = TimeUtil.secToTime((int) (duration / 1000) < 1 ? 1 : (int) (duration / 1000));
        float duration = (videoSegment.getEnd_time()-videoSegment.getStart_time())*1.0f/1000;
        viewHolder.tvTime.setText(duration+"S");
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(viewHolder.itemView, i);
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rl_item_cameraClipEditActivity_item)
        RelativeLayout relativeLayout;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.iv_image)
        ImageView ivImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

}
