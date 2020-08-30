package com.test.xcamera.phonealbum.adapter;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.editvideo.MediaConstant;
import com.editvideo.MediaData;
import com.editvideo.TimeUtil;
import com.editvideo.dataInfo.ClipInfo;
import com.editvideo.dataInfo.TimelineData;
import com.framwork.base.adapter.BaseRecyclerAdapter;
import com.test.xcamera.R;
import com.test.xcamera.phonealbum.MyVideoEditActivity;
import com.test.xcamera.phonealbum.widget.VideoEditManger;
import com.test.xcamera.phonealbum.widget.drag.ItemTouchHelperAdapter;
import com.test.xcamera.phonealbum.widget.drag.ItemTouchHelperViewHolder;
import com.test.xcamera.phonealbum.widget.drag.OnStartDragListener;
import com.test.xcamera.picasso.Picasso;
import com.test.xcamera.utils.GlideUtils;
import com.test.xcamera.utils.LoggerUtils;
import com.test.xcamera.utils.glide.VideoFrameThumb;
import com.test.xcamera.utils.proxy.Perform;
import com.test.xcamera.utils.proxy.click.NonDuplicateFactory;

import butterknife.BindView;
import butterknife.ButterKnife;


public class VideoEditAdapter extends BaseRecyclerAdapter<MediaData, VideoEditAdapter.ViewHolder> implements ItemTouchHelperAdapter {
    private OnStartDragListener mDragStartListener;
    private int mPositionFrom = 0;
    private int mPositionTo = 0;
    private int mSelectLongPosition = 0;
    private boolean isVideoEdit = true;

    public void setVideoEdit(boolean videoEdit) {
        isVideoEdit = videoEdit;
    }

    public int getSelectLongPosition() {
        mSelectLongPosition = VideoEditManger.selectPosition(mSelectLongPosition);
        return mSelectLongPosition;
    }

    public int getPositionFrom() {
        mPositionFrom = VideoEditManger.selectPosition(mPositionFrom);
        return mPositionFrom;
    }

    public int getPositionTo() {
        mPositionTo = VideoEditManger.selectPosition(mPositionTo);
        return mPositionTo;
    }

    public int getPositionToAlbum() {
        LoggerUtils.printLog("club:getPositionToAlbum:mPositionTo:" + mPositionTo);

        return mPositionTo;
    }

    public void setDragStartListener(OnStartDragListener mDragStartListener) {
        this.mDragStartListener = mDragStartListener;
    }

    public VideoEditAdapter(Context context) {
        super(context);
    }

    @Override
    public final VideoEditAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = View.inflate(mContext, R.layout.item_video_edit_drop, null);
        return new ViewHolder(view);
    }

    @Override
    public final void onBindViewHolder(final VideoEditAdapter.ViewHolder viewHolder, final int position) {
        MediaData mediaData = mData.get(position);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NonDuplicateFactory.proxy(new Perform() {
                    @Override
                    public void perform() {
                        mItemClickListener.onItemClick(viewHolder.itemView, position);
                    }
                });
            }
        });
        viewHolder.frame_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NonDuplicateFactory.proxy(new Perform() {
                    @Override
                    public void perform() {
                        if (mContext instanceof MyVideoEditActivity) {
                            if (mOnDragItemCallBack != null) {
                                mOnDragItemCallBack.OnEditButtonCallBack(viewHolder.getAdapterPosition());
                            }

                        }
                    }
                });

            }
        });
        ValueAnimator anim = ValueAnimator.ofFloat(1f, 1.2f);

        viewHolder.itemView.setOnLongClickListener(v -> {
            if (mSelectLongPosition != viewHolder.getAdapterPosition()) {
                setIsSelectPosition(mSelectLongPosition, false);
                viewHolder.frame_edit.setBackgroundResource(R.drawable.video_eidt_item_border_bg);
                viewHolder.frame_edit.setVisibility(View.VISIBLE);
                mSelectLongPosition = viewHolder.getAdapterPosition();
                mPositionTo = mSelectLongPosition;

            }
            anim.addUpdateListener(animation -> {
                viewHolder.itemView.setScaleX((float) animation.getAnimatedValue());
                viewHolder.itemView.setScaleY((float) animation.getAnimatedValue());
            });
            anim.setDuration(200);
            anim.start();
            if (mDragStartListener != null) {
                mDragStartListener.onStartDrag(viewHolder);
            }
            return false;
        });
        viewHolder.frame_edit.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        viewHolder.itemView.setScaleX((float) animation.getAnimatedValue());
                        viewHolder.itemView.setScaleY((float) animation.getAnimatedValue());
                    }
                });
                anim.setDuration(200);
                anim.start();
                mSelectLongPosition = viewHolder.getAdapterPosition();
                mPositionTo = mSelectLongPosition;
                if (mDragStartListener != null) {
                    mDragStartListener.onStartDrag(viewHolder);
                }

                return false;
            }
        });
        viewHolder.itemView.setScaleX(1);
        viewHolder.itemView.setScaleY(1);
        if (mediaData.isShowingDragItem()) {
            mSelectLongPosition = viewHolder.getAdapterPosition();
            viewHolder.frame_edit.setBackgroundResource(R.drawable.video_eidt_item_border_bg);
            viewHolder.frame_edit.setVisibility(View.VISIBLE);

        } else {
            viewHolder.frame_edit.setBackgroundResource(R.drawable.circle_corner_transparent_bg);
            viewHolder.frame_edit.setVisibility(View.GONE);

        }
        if (mediaData.isShowEditItemIcon()) {
            viewHolder.editItemImageView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.editItemImageView.setVisibility(View.GONE);
        }
        if (mediaData.isRemoteData()) {
            Picasso.with(mContext).load(mediaData.getRemotePath()).resize(100, 100)
                    .placeholder(R.mipmap.zhanweitu)
                    .error(R.mipmap.zhanweitu).into(viewHolder.ivImage);
        } else {
            if(isVideoEdit){
                VideoFrameThumb.getInstance().loadVideoThumbnail(mContext,mediaData.getPath(),viewHolder.ivImage,mediaData.getTrimIn());
            }else {
                if (mediaData.getType() == MediaConstant.VIDEO) {
                    GlideUtils.GlideLoader(mContext, mediaData.getPath(), viewHolder.ivImage);
                } else {
                    GlideUtils.GlideLoader(mContext, mediaData.getPath(), viewHolder.ivImage);
                }
            }

        }
        if (mediaData.getType() == MediaConstant.VIDEO) {
            viewHolder.tvRightTime.setVisibility(View.VISIBLE);
            String time = TimeUtil.secToTime((int) (mediaData.getDuration() / 1000) < 1 ? 1 : (int) (mediaData.getDuration() / 1000));
            viewHolder.tvRightTime.setText(time);
        } else {
            viewHolder.tvRightTime.setVisibility(View.GONE);
        }


    }

    public void setIsSelectPosition(int position, boolean isSelect) {
        mData.get(position).setShowingDragItem(isSelect);
        mData.get(position).setShowEditItemIcon(false);
        notifyItemChanged(position);
    }

    /**
     * 选中坐标
     *
     * @param position
     */
    public void setSelectPosition(int position, boolean isShowEditIcon) {
        if (mData.size() == 0) {
            return;
        }
        for (int i = 0; i < mData.size(); i++) {
            mData.get(i).setShowingDragItem(false);
            mData.get(i).setShowEditItemIcon(false);
        }

        mData.get(position).setShowingDragItem(true);
        mData.get(position).setShowEditItemIcon(isShowEditIcon);
        notifyDataSetChanged();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        mPositionFrom = fromPosition;
        mPositionTo = toPosition;
        LoggerUtils.printLog("club:onItemMove:mPositionTo:" + mPositionTo + " mPositionFrom:" + mPositionFrom);
        if (isVideoEdit) {
            ClipInfo info = TimelineData.instance().getClipInfoData().remove(mPositionFrom);
            TimelineData.instance().getClipInfoData().add(mPositionTo, info);
        }
        MediaData data = mData.remove(fromPosition);
        mData.add(toPosition, data);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {

    }

    /**
     * 删除
     *
     * @param position
     */
    public void setRemovePosition(int position) {
        Log.i("club", "club_setRemovePosition:" + position + " size:" + mData.size());
        if (position < 0 || position >= mData.size()) {
            return;
        }
        mData.remove(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {
        @BindView(R.id.iv_image)
        ImageView ivImage;
        @BindView(R.id.iv_editItem)
        ImageView editItemImageView;
        @BindView(R.id.tv_right_time1)
        TextView tvRightTime;
        @BindView(R.id.rl_albumActivity_dragItem_itemView)
        RelativeLayout itemViewRelativeLayout;
        @BindView(R.id.frame_edit)
        FrameLayout frame_edit;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onItemSelected() {

        }

        @Override
        public void onItemClear() {

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
