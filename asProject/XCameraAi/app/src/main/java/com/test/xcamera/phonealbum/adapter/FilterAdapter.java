 package com.test.xcamera.phonealbum.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.framwork.base.adapter.BaseRecyclerAdapter;
import com.test.xcamera.phonealbum.bean.MusicBean;
import com.test.xcamera.R;
import com.test.xcamera.widget.NiceImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by DELL on 2019/7/4.
 */

public class FilterAdapter extends BaseRecyclerAdapter<MusicBean, FilterAdapter.ViewHolder> {

    public FilterAdapter(Context context) {
        super(context);
    }

    public void updateData(boolean refresh, List<MusicBean> mediaDataList){
        if(refresh){
            mData.clear();
        }
        mData.addAll(mediaDataList);
        notifyDataSetChanged();
    }

    @Override
    public final FilterAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = View.inflate(mContext, R.layout.my_video_edit_item_filter, null);
        return new ViewHolder(view);
    }

    @Override
    public final void onBindViewHolder(final FilterAdapter.ViewHolder viewHolder, final int position) {
        Glide.with(mContext).load( mData.get(position).getImgRes()).into(viewHolder.ivImage);
        viewHolder.tvRightTime.setText(mData.get(position).getName());
        if(mData.get(position).isSelected()){
            viewHolder.tvRightTime.setTextColor(mContext.getResources().getColor(R.color.appThemeColor));
            viewHolder.ivImage.setBackgroundResource(R.drawable.circle_corner_4dp_border_bg);
        }else {
            viewHolder.tvRightTime.setTextColor(mContext.getResources().getColorStateList(R.color.color_666666));
            viewHolder.ivImage.setBackgroundResource(R.drawable.circle_corner_transparent_bg);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(viewHolder.itemView, position);
            }
        });

    }
    class ViewHolder extends  RecyclerView.ViewHolder
     {
        @BindView(R.id.iv_myVideoEditActivity_item_img)
        NiceImageView ivImage;
        @BindView(R.id.tv_myVideoEditActivity_item_name)
        TextView tvRightTime;
        @BindView(R.id.rl_albumActivity_dragItem_itemView)
        LinearLayout itemViewRelativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
