package com.test.xcamera.cameraclip.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.editvideo.TimeUtil;
import com.framwork.base.adapter.BaseRecyclerAdapter;
import com.test.xcamera.R;
import com.test.xcamera.cameraclip.bean.VideoFile;
import com.test.xcamera.cameraclip.bean.VideoScoreType;
import com.test.xcamera.picasso.Picasso;
import com.test.xcamera.utils.DisplayUtils;

import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by SMZ on 2019/7/3.
 */

public class TodayFileListAdapter extends BaseRecyclerAdapter<VideoFile, TodayFileListAdapter.ViewHolder> {

    public TodayFileListAdapter(Context context) {
        super(context);
    }



    @Override
    public final TodayFileListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = View.inflate(mContext, R.layout.item_today_file_list , null);
        return new TodayFileListAdapter.ViewHolder(view);
    }

    @Override
    public final void onBindViewHolder(final TodayFileListAdapter.ViewHolder viewHolder, final int position) {
        int itemWidth = DisplayUtils.getwidth((Activity) mContext)/3;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewHolder.itemViewRelativeLayout.getLayoutParams();
        layoutParams.width = itemWidth;
        layoutParams.height = itemWidth;
        viewHolder.itemViewRelativeLayout.setLayoutParams(layoutParams);

        VideoFile mediaData = mData.get(position);
        boolean hasMark = false;
        for(int i=0; i<mediaData.getVideoScoreTypeList().size(); i++){
            if(mediaData.getVideoScoreTypeList().get(i).getScoreType()==VideoScoreType.ScoreType.MARK){
                hasMark = true;
                break;
            }
        }
        if(hasMark){
            viewHolder.markImageView.setVisibility(View.VISIBLE);
        }else {
            viewHolder.markImageView.setVisibility(View.GONE);
        }
//        String filePath = "mox://resourceitem?uri=" + mediaData.getName() + "&size=0&type=video";
        String filePath = "mox://resourceitem?uri=" + URLEncoder.encode(mediaData.getName()) + ".THM&size=406490&type=image";
        Picasso.with(mContext).load(filePath).placeholder(R.mipmap.bank_thumbnail_local)
                .error(R.mipmap.bank_thumbnail_local).into(viewHolder.ivImage);
        if(mediaData.isSelected()){
            viewHolder.selectedImageView.setVisibility(View.VISIBLE);
        }else{
            viewHolder.selectedImageView.setVisibility(View.GONE);
        }
        viewHolder.tvMediaTime.setVisibility(View.VISIBLE);
        long duration = mediaData.getClose_time()- mediaData.getCreate_time();
        String time = TimeUtil.secToTime((int) (duration/1000) < 1 ? 1 : (int) (duration/1000));
        viewHolder.tvMediaTime.setText(time);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(viewHolder.itemView, position);
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_image)
        ImageView ivImage;
        @BindView(R.id.iv_todayFileListActivity_item_selected)
        ImageView selectedImageView;
        @BindView(R.id.iv_todayFileListActivity_item_mark)
        ImageView markImageView;
        @BindView(R.id.tv_media_time)
        TextView tvMediaTime;
        @BindView(R.id.rl_todayFileListActivity_itemView)
        RelativeLayout itemViewRelativeLayout;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }




}
