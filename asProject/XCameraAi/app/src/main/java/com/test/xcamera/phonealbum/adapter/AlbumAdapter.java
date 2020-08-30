package com.test.xcamera.phonealbum.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.framwork.base.adapter.BaseRecyclerAdapter;
import com.test.xcamera.utils.DateUtils;
import com.test.xcamera.utils.DisplayUtils;
import com.test.xcamera.utils.GlideUtils;
import com.test.xcamera.R;
import com.editvideo.MediaConstant;
import com.editvideo.MediaData;
import com.test.xcamera.utils.glide.VideoFrameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by DELL on 2019/7/3.
 */

public class AlbumAdapter extends BaseRecyclerAdapter<MediaData, RecyclerView.ViewHolder> {
    //item类型
    public static final int ITEM_TYPE_HEADER = 0;
    public static final int ITEM_TYPE_CONTENT = 1;
    public static final int ITEM_TYPE_BOTTOM = 2;
    private int mHeaderCount = 0;//头部View个数
    private int mBottomCount = 1;//底部View个数
    private LayoutInflater mLayoutInflater;

    //内容长度
    public int getContentItemCount() {
        return mData.size();
    }

    //判断当前item是否是HeadView
    public boolean isHeaderView(int position) {
        return mHeaderCount != 0 && position < mHeaderCount;
    }

    //判断当前item是否是FooterView
    public boolean isBottomView(int position) {
        return mBottomCount != 0 && position >= (mHeaderCount + getContentItemCount());
    }

    public AlbumAdapter(Context context) {
        super(context);
        mLayoutInflater = LayoutInflater.from(context);

    }

    @Override
    public void updateData(boolean refresh, List<MediaData> mediaDataList) {
        MediaData mediaData = null;
        if (mediaDataList.size() > 0) {
            //这个句话主要是用来获取当前设置进来的数据是不是相机的数据
            mediaData = mediaDataList.get(0);
        }

        if (refresh) {
            mData.clear();
        }
        if (mediaData != null && mediaData.getRemotePath().startsWith("mox")) {
            ArrayList<MediaData> tempList = new ArrayList<>();
            for (int i = 0; i < mediaDataList.size(); i++) {
                tempList.add(mediaDataList.get(i));
            }
            mData.addAll(tempList);
        } else {
            ArrayList<MediaData> tempList = new ArrayList<>();
            for (int i = 0; i < mediaDataList.size(); i++) {
                String path = mediaDataList.get(i).getPath();
                File file = new File(path);
                if (!file.exists()) {
                } else {
                    tempList.add(mediaDataList.get(i));
                }
            }
            mData.addAll(tempList);
        }
        notifyDataSetChanged();
    }

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        if (viewType == ITEM_TYPE_CONTENT) {
            return new AlbumAdapter.ViewHolder(View.inflate(mContext, R.layout.item_ablum, null));
        } else if (viewType == ITEM_TYPE_BOTTOM) {
            return new AlbumAdapter.BottomViewHolder(mLayoutInflater.inflate(R.layout.item_ablum_bottom, viewGroup, false));
        }
        return null;
    }

    boolean IsShowBottom = true;

    public void setIsShowBottom(boolean isShow) {
        IsShowBottom = isShow;
    }

    @Override
    public final void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof BottomViewHolder) {
            BottomViewHolder holder = (BottomViewHolder) viewHolder;
            if (IsShowBottom) {
                holder.tvBottomTip.setText(mContext.getString(R.string.video_import_ready_bottom));
                holder.tvBottomTip.setVisibility(View.VISIBLE);
            } else {
                holder.tvBottomTip.setText(mContext.getString(R.string.video_import_loading));
                holder.tvBottomTip.setVisibility(View.VISIBLE);
            }

        } else if (viewHolder instanceof ViewHolder) {
            ViewHolder contentHolder = (ViewHolder) viewHolder;
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                setStatus(viewHolder,mData.get(position));

                    mItemClickListener.onItemClick(viewHolder.itemView, position);
                }
            });
            int itemWidth = DisplayUtils.getwidth((Activity) mContext) / 3;
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) contentHolder.itemViewRelativeLayout.getLayoutParams();
            layoutParams.width = itemWidth;
            layoutParams.height = itemWidth;
            contentHolder.itemViewRelativeLayout.setLayoutParams(layoutParams);

            MediaData mediaData = mData.get(position);

            if (mediaData.isRemoteData()) {
                contentHolder.ivImage.setImageResource(R.mipmap.bank_thumbnail_local);
//                Picasso.with(mContext).load(mediaData.getPath()).placeholder(R.mipmap.bank_thumbnail_local)
//                        .error(R.mipmap.bank_thumbnail_local).into(contentHolder.ivImage);
                VideoFrameUtils.loadVideoThumbnail(mContext, mediaData.getRemotePath(), contentHolder.ivImage, 0);

            } else {
                if (mediaData.getType() == MediaConstant.VIDEO) {
                    Glide.with(mContext).load(Uri.fromFile(new File(mediaData.getPath())))
                            .apply(GlideUtils.options).into(contentHolder.ivImage);
//                VideoFrameUtils.loadVideoThumbnail(mContext,mediaData.getPath(),viewHolder.ivImage,100);
                } else {
                    GlideUtils.GlideLoader(mContext, mediaData.getPath(), contentHolder.ivImage);

                }

            }
            setStatus(contentHolder, mediaData);
            if (mediaData.getType() == MediaConstant.VIDEO) {
                contentHolder.tvMediaTime.setVisibility(View.VISIBLE);
//                String time = TimeUtil.secToTime((int) (mediaData.getDuration() / 1000) < 1 ? 1 : (int) (mediaData.getDuration() / 1000));
                String time = DateUtils.stringForTime(mediaData.getDuration());
                contentHolder.tvMediaTime.setText(time);
            } else {
                contentHolder.tvMediaTime.setVisibility(View.GONE);
            }
        }


    }

    public void setStatus(AlbumAdapter.ViewHolder viewHolder, MediaData mediaData) {
        if (mediaData.isState()) {
            viewHolder.ivIscheck.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ivIscheck.setVisibility(View.GONE);
        }
    }

    //判断当前item类型
    @Override
    public int getItemViewType(int position) {
        int dataItemCount = getContentItemCount();
        if (mHeaderCount != 0 && position < mHeaderCount) {
            //头部View
            return ITEM_TYPE_HEADER;
        } else if (mBottomCount != 0 && position >= (mHeaderCount + dataItemCount)) {
            //底部View
            return ITEM_TYPE_BOTTOM;
        } else {
            //内容View
            return ITEM_TYPE_CONTENT;
        }
    }

    @Override
    public int getItemCount() {
        return mHeaderCount + getContentItemCount() + mBottomCount;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_image)
        ImageView ivImage;
        @BindView(R.id.iv_ischeck)
        ImageView ivIscheck;
        @BindView(R.id.tv_media_time)
        TextView tvMediaTime;
        @BindView(R.id.rl_albumActivity_galleryItem_itemView)
        RelativeLayout itemViewRelativeLayout;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    //底部 ViewHolder
    static class BottomViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_bottom_tip)
        TextView tvBottomTip;

        public BottomViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
