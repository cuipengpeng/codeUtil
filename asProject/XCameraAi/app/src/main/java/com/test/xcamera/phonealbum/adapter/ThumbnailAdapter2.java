package com.test.xcamera.phonealbum.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.editvideo.dataInfo.TimelineData;
import com.framwork.base.adapter.BaseRecyclerAdapter;
import com.test.xcamera.R;
import com.test.xcamera.phonealbum.bean.BaseThumbBean;
import com.test.xcamera.phonealbum.bean.ThumbnailBean;
import com.test.xcamera.phonealbum.widget.VideoEditManger;
import com.test.xcamera.phonealbum.widget.drag.ItemTouchHelperAdapter;
import com.test.xcamera.phonealbum.widget.drag.ItemTouchHelperViewHolder;
import com.test.xcamera.phonealbum.widget.drag.OnStartDragListener;
import com.test.xcamera.utils.PUtil;
import com.test.xcamera.utils.glide.VideoFrameThumb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by DELL on 2019/7/4.
 */

public class ThumbnailAdapter2 extends BaseRecyclerAdapter<ThumbnailBean, ThumbnailAdapter2.ViewHolder>
        implements ItemTouchHelperAdapter {

    public List<ThumbnailBean> mData = new ArrayList<>();
    private Context mContext;
    private TimeSpan.OnTrimChangeListener mOnTrimChangeListener;
    private boolean mIsDragIng = false;
    private OnStartDragListener mDragStartListener;
    private int mPositionFrom = 0;
    private int mPositionTo = 0;
    private int mDragWidth;
    public void setDragStartListener(OnStartDragListener mDragStartListener) {
        this.mDragStartListener = mDragStartListener;
    }

    public ThumbnailAdapter2(Context context) {
        super(context);
        mContext = context;
        mDragWidth= PUtil.dip2px(mContext, VideoEditManger.VIDEO_FRAME_WIDTH);

    }

    public int getPositionFrom() {
        return mPositionFrom;
    }

    public int getPositionTo() {
        if(mPositionTo<=0||mPositionTo>=TimelineData.instance().getClipCount()){
            mPositionTo=0;
        }
        return mPositionTo;
    }
    public int getPositionDel() {
        if(mPositionTo<=0){
            mPositionTo=0;
        }else  if(mPositionTo>=TimelineData.instance().getClipCount()){
            mPositionTo=TimelineData.instance().getClipCount();
        }
        return mPositionTo;
    }

    public void updateData(boolean refresh, List<ThumbnailBean> mediaDataList) {
        if (refresh) {
            mData.clear();
        }
        mData.addAll(mediaDataList);
        notifyDataSetChanged();
    }

    public void setIsDragIng(boolean isDragIng) {
        mIsDragIng = isDragIng;
    }

    public List<BaseThumbBean> getmBeans() {
        List<BaseThumbBean> baseThumbBeans = new ArrayList<>();
        for (BaseThumbBean base : mData) {
            baseThumbBeans.add(base);
        }
        return baseThumbBeans;
    }

    @Override
    public final ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = View.inflate(mContext, R.layout.item_my_video_edit_thumbnail, null);
        return new ViewHolder(view);
    }

    @SuppressLint("NewApi")
    @Override
    public final void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        ViewGroup.LayoutParams lp = viewHolder.rl_dragItem_itemView.getLayoutParams();
         if (mIsDragIng){
             lp.width =mDragWidth;
             viewHolder.image_thumb.setVisibility(View.VISIBLE);

             if (position == 0 || position == mData.size() - 1) {
                 viewHolder.image_thumb.setImageResource(0);
             }else {
                 long time=mData.get(position).getTrimInTime();
                 VideoFrameThumb.getInstance().loadVideoThumbnail(mContext,mData.get(position).getMediaPath(),viewHolder.image_thumb,time);
             }
         }else {
             lp.width =mData.get(viewHolder.getAdapterPosition()).getItemWidth();
             viewHolder.image_thumb.setVisibility(View.GONE);

         }
         viewHolder.rl_root_itemView.setLayoutParams(lp);
        setTransitionViewStatus(viewHolder);
        viewHolder.ivTransitionLeft.setOnClickListener(view -> {
            if (mOnVideoCutThumbCallBack != null) {
                mOnVideoCutThumbCallBack.onVideoTransition(viewHolder.getAdapterPosition() - 1);
            }
        });
        viewHolder.ivTransitionRight.setOnClickListener(view -> {
            if (mOnVideoCutThumbCallBack != null) {
                mOnVideoCutThumbCallBack.onVideoTransition(viewHolder.getAdapterPosition());
            }
        });
        if (mData.get(position).isSelected()) {
            if (viewHolder.getAdapterPosition() == mData.size() - 2&&!mIsDragIng){
                viewHolder.rl_time_line.setPadding(0,0,0,0);

            }else {
                viewHolder.rl_time_line.setPadding(0,0,PUtil.dip2px(mContext,5),0);

            }
            viewHolder.timelineTimeSpan.setBackgroundResource(R.drawable.circle_corner_white_border_bg);
        } else {
            viewHolder.timelineTimeSpan.setBackgroundResource(R.drawable.circle_corner_transparent_bg);
        }
        viewHolder.timelineTimeSpan.setCurrentPosition(position);
        viewHolder.timelineTimeSpan.setOnTrimChangeListener(new TimeSpan.OnTrimChangeListener() {

            @Override
            public void onChange(int currentPosition, boolean dragLeft, int distance) {
/*                 if (dragLeft){
                     RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) viewHolder.leftBlackView.getLayoutParams();
                     lp.width +=distance;
                     viewHolder.leftBlackView.setLayoutParams(lp);
                 }else {
                     RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) viewHolder.rightBlackView.getLayoutParams();
                     lp.width +=distance;
                     viewHolder.rightBlackView.setLayoutParams(lp);
                 }*/
                if (mOnTrimChangeListener != null) {
                    mOnTrimChangeListener.onChange(currentPosition, dragLeft, distance);
                }
            }
        });
        viewHolder.clickShowTimeSpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectPosition(position);
                if(mOnVideoCutThumbCallBack!=null){
                    mOnVideoCutThumbCallBack.onVideoOnClick(position);
                }
            }
        });
        viewHolder.clickShowTimeSpan.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                mSelectLongPosition = viewHolder.getAdapterPosition();
                mPositionTo = mSelectLongPosition - 1 < 0 ? 0 : mSelectLongPosition - 1;
                if (mSelectLongPosition <= 0 || mSelectLongPosition == mData.size() - 1) {
                    return true;
                }
                mIsDragIng = true;
                setSelectPosition(mSelectLongPosition);
                if (mDragStartListener != null) {
                    mDragStartListener.onStartDrag(viewHolder);
                }
                return false;
            }
        });

    }

    private int mSelectLongPosition = 0;

    public int getSelectLongPosition() {
        return mSelectLongPosition;
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

    /**
     * 选中坐标
     *
     * @param position
     */
    public void setSelectPosition(int position) {
        if (position <= 0 || position == mData.size() - 1) {
            return;
        }
        for (int i = 0; i < mData.size(); i++) {
            mData.get(i).setSelected(false);
        }
        mData.get(position).setSelected(true);
        notifyDataSetChanged();
    }
    public int getScrollPosition(int x) {
        int position=1;
        int totalWidth=0;
        for (int i = 1; i < mData.size()-1; i++) {
            if(x>totalWidth&&x<=totalWidth+mData.get(i).getViewStartIndex()){
                position=i;
                break;
            }
            totalWidth=totalWidth+mData.get(i).getViewStartIndex();
        }
        return position;
    }

    /**
     * 设置转场按钮状态显示
     *
     * @param viewHolder
     */
    public void setTransitionViewStatus(ViewHolder viewHolder) {
        //长按移动全部隐藏
        if (mIsDragIng) {
            setTransitionViewStatus(viewHolder, View.GONE, View.GONE);
            return;
        }
        if (mData.get(viewHolder.getAdapterPosition()).isEmptyView()) {
            setTransitionViewStatus(viewHolder, View.GONE, View.GONE);
        } else {
            if (viewHolder.getAdapterPosition() == 1) {
                if (mData.size() == 3) {
                    setTransitionViewStatus(viewHolder, View.GONE, View.GONE);
                }else {
                    setTransitionViewStatus(viewHolder, View.GONE, View.VISIBLE);
                }
            } else if (viewHolder.getAdapterPosition() == mData.size() - 2) {
                setTransitionViewStatus(viewHolder, View.VISIBLE, View.GONE);
            } else {
                setTransitionViewStatus(viewHolder, View.VISIBLE, View.VISIBLE);

            }
        }
    }

    public void setTransitionViewStatus(ViewHolder viewHolder, int left, int right) {
        if (viewHolder.ivTransitionLeft.getVisibility() != left) {
            viewHolder.ivTransitionLeft.setVisibility(left);
        }
        if (viewHolder.ivTransitionRight.getVisibility() != right) {
            viewHolder.ivTransitionRight.setVisibility(right);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Log.i("Drag", "Drag_onItemMove---------------");

        if (fromPosition != 0 && toPosition != 0 && fromPosition != mData.size() - 1 && toPosition != mData.size() - 1) {
            mPositionFrom = fromPosition - 1 < 0 ? 0 : fromPosition - 1;
            mPositionTo = toPosition - 1 < 0 ? 0 : toPosition - 1;
            Log.i("club","club_onItemMove:fromPosition:"+mPositionFrom+" toPosition:"+mPositionTo);
            Collections.swap(TimelineData.instance().getClipInfoData(), mPositionFrom, mPositionTo);

            Collections.swap(mData, fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);


        }
        return true;
    }

    @Override
    public void onItemDismiss(int position) {

    }


    class ViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {
        public ImageView image_thumb;
        public TimeSpan timelineTimeSpan;
        public View clickShowTimeSpan;
        public View leftBlackView;
        public View rightBlackView;
        public View rl_time_line;
        public FrameLayout rl_root_itemView;
        public FrameLayout rl_dragItem_itemView;
        public FrameLayout ivTransitionLeft;
        public FrameLayout ivTransitionRight;


        public ViewHolder(View itemView) {
            super(itemView);
            image_thumb =  itemView.findViewById(R.id.image_thumb);
            timelineTimeSpan = itemView.findViewById(R.id.ts_myVideoEditActivity_timeSpan);
            clickShowTimeSpan = itemView.findViewById(R.id.v_myVideoEditActivity_clickShowTimeSpan);
            leftBlackView = itemView.findViewById(R.id.v_myVideoEditActivity_leftBlackView);
            rightBlackView = itemView.findViewById(R.id.v_myVideoEditActivity_rightBlackView);
            rl_root_itemView = itemView.findViewById(R.id.rl_root_itemView);
            rl_dragItem_itemView = itemView.findViewById(R.id.rl_dragItem_itemView);
            ivTransitionLeft = itemView.findViewById(R.id.ivTransitionLeft);
            ivTransitionRight = itemView.findViewById(R.id.ivTransitionRight);
            rl_time_line = itemView.findViewById(R.id.rl_time_line);
        }

        @Override
        public void onItemSelected() {
            Log.i("Drag", "Drag_onItemSelected---------------");

        }

        @Override
        public void onItemClear() {
            Log.i("Drag", "Drag_onItemClear---------------");

        }
    }

    public void setOnTrimChangeListener(TimeSpan.OnTrimChangeListener onTrimChangeListener) {
        this.mOnTrimChangeListener = onTrimChangeListener;
    }

    private OnVideoCutThumbCallBack mOnVideoCutThumbCallBack;

    public void setOnVideoCutThumbCallBack(OnVideoCutThumbCallBack onVideoCutThumbCallBack) {
        this.mOnVideoCutThumbCallBack = onVideoCutThumbCallBack;
    }

    public interface OnVideoCutThumbCallBack {
        void onVideoTransition(int position);
        void onVideoOnClick(int position);
    }
}
