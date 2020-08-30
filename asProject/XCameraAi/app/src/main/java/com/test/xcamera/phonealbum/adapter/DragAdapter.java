package com.test.xcamera.phonealbum.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.framwork.base.adapter.BaseRecyclerAdapter;
import com.test.xcamera.phonealbum.MyVideoEditActivity;

import com.test.xcamera.picasso.Picasso;
import com.test.xcamera.utils.GlideUtils;
import com.test.xcamera.R;
import com.editvideo.MediaConstant;
import com.editvideo.MediaData;
import com.editvideo.TimeUtil;
import com.test.xcamera.utils.glide.VideoFrameUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by DELL on 2019/7/4.
 */

public class DragAdapter extends BaseRecyclerAdapter<MediaData, DragAdapter.ViewHolder> {

    public DragAdapter(Context context) {
        super(context);
    }

    @Override
    public final DragAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = View.inflate(mContext, R.layout.item_drop, null);
        return new ViewHolder(view);
    }

    @Override
    public final void onBindViewHolder(final DragAdapter.ViewHolder viewHolder, final int position) {
        MediaData mediaData = mData.get(position);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(viewHolder.itemView, position);
            }
        });
        viewHolder.editItemImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mContext instanceof MyVideoEditActivity) {
                    if (mOnDragItemCallBack != null) {
                        mOnDragItemCallBack.OnEditButtonCallBack(viewHolder.getAdapterPosition());
                    }

                }
            }
        });

//        if (mediaData.isShowingDragItem()) {
//            viewHolder.ivImage.setBackgroundResource(R.drawable.circle_corner_red_border_bg);
//        } else {
//            viewHolder.ivImage.setBackgroundResource(R.drawable.circle_corner_transparent_bg);
//        }
        if (mediaData.isShowingDragItem()) {
            viewHolder.iv_image_bg.setVisibility(View.VISIBLE);
        } else {
            viewHolder.iv_image_bg.setVisibility(View.GONE);
        }

        if (mediaData.isShowEditItemIcon()) {
            viewHolder.editItemImageView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.editItemImageView.setVisibility(View.GONE);
        }
        if (mediaData.isRemoteData()) {
            Picasso.with(mContext).load(mediaData.getRemotePath()).placeholder(R.mipmap.bank_thumbnail_local)
                    .error(R.mipmap.bank_thumbnail_local).into(viewHolder.ivImage);
        } else {
            if(mediaData.getType()==MediaConstant.VIDEO){
                VideoFrameUtils.loadVideoThumbnail(mContext,mediaData.getPath(),viewHolder.ivImage,100);
            }else {
                GlideUtils.GlideLoader(mContext, mediaData.getPath(), viewHolder.ivImage);

            }        }
        if (mediaData.getType() == MediaConstant.VIDEO) {
            viewHolder.tvRightTime.setVisibility(View.VISIBLE);
            String time = TimeUtil.secToTime((int) (mediaData.getDuration() / 1000) < 1 ? 1 : (int) (mediaData.getDuration() / 1000));
            viewHolder.tvRightTime.setText(time);
        } else {
            viewHolder.tvRightTime.setVisibility(View.GONE);
        }


    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_image)
        ImageView ivImage;
        @BindView(R.id.iv_image_bg)
        ImageView iv_image_bg;

        @BindView(R.id.iv_editItem)
        ImageView editItemImageView;
        @BindView(R.id.tv_right_time1)
        TextView tvRightTime;
        @BindView(R.id.rl_albumActivity_dragItem_itemView)
        RelativeLayout itemViewRelativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    private OnDragItemCallBack mOnDragItemCallBack;


    public void setOnDragItemCallBack(OnDragItemCallBack mOnDragItemCallBack) {
        this.mOnDragItemCallBack = mOnDragItemCallBack;
    }

    public interface OnDragItemCallBack {
        void OnEditButtonCallBack(int position);
    }

}
