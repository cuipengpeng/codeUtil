package com.test.xcamera.personal.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.editvideo.MediaData;
import com.editvideo.TimeUtil;
import com.framwork.base.adapter.BaseRecyclerAdapter;
import com.test.xcamera.R;
import com.test.xcamera.utils.Constants;
import com.test.xcamera.utils.DisplayUtils;
import com.test.xcamera.utils.GlideUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by SMZ on 2019/7/3.
 */

public class MyAdapter extends BaseRecyclerAdapter<MediaData, MyAdapter.ViewHolder> {

    public MyAdapter(Context context) {
        super(context);
    }



    @Override
    public final MyAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = View.inflate(mContext, R.layout.item_my_fragment , null);
        return new MyAdapter.ViewHolder(view);
    }

    @Override
    public final void onBindViewHolder(final MyAdapter.ViewHolder viewHolder, final int position) {
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(viewHolder.itemView, position);
            }
        });
        int itemWidth = DisplayUtils.getwidth((Activity) mContext)/3;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewHolder.itemViewRelativeLayout.getLayoutParams();
        layoutParams.width = itemWidth;
        layoutParams.height = itemWidth;
        viewHolder.itemViewRelativeLayout.setLayoutParams(layoutParams);

        MediaData mediaData = mData.get(position);
        if(mediaData.isRemoteData()){
            GlideUtils.GlideLoader(mContext, Constants.getFileIdToUrl(mediaData.getPath()+""), viewHolder.ivImage);
        }else {
            GlideUtils.GlideLoader(mContext, mediaData.getPath(), viewHolder.ivImage);
        }
        viewHolder.tvMediaTime.setVisibility(View.VISIBLE);
        String time = TimeUtil.secToTime((int) (mediaData.getDuration()/1000) < 1 ? 1 : (int) (mediaData.getDuration()/1000));
        viewHolder.tvMediaTime.setText(time);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_image)
        ImageView ivImage;
        @BindView(R.id.tv_media_time)
        TextView tvMediaTime;
        @BindView(R.id.rl_albumActivity_galleryItem_itemView)
        RelativeLayout itemViewRelativeLayout;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }




}
