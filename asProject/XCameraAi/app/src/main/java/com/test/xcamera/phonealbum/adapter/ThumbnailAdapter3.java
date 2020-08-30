package com.test.xcamera.phonealbum.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.editvideo.MediaConstant;
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

public class ThumbnailAdapter3 extends BaseRecyclerAdapter<ThumbnailBean, ThumbnailAdapter3.ViewHolder>
        implements ItemTouchHelperAdapter {

    public List<ThumbnailBean> mData = new ArrayList<>();
    private Context mContext;
    private TimeSpan.OnTrimChangeListener mOnTrimChangeListener;
    private boolean mIsDragIng = false;
    private OnStartDragListener mDragStartListener;
    private int mPositionFrom = 0;
    private int mPositionTo = 0;
    private int mDragWidth;
    private int mWidth;

    public void setDragStartListener(OnStartDragListener mDragStartListener) {
        this.mDragStartListener = mDragStartListener;
    }

    public ThumbnailAdapter3(Context context) {
        super(context);
        mContext = context;
        mDragWidth= PUtil.dip2px(mContext, VideoEditManger.VIDEO_FRAME_WIDTH);
        mWidth= PUtil.dip2px(mContext, VideoEditManger.VIDEO_FRAME_WIDTH);


    }

    public int getPositionFrom() {
        return mPositionFrom;
    }

    public int getPositionTo() {
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
//        notifyDataSetChanged();
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
        View view = View.inflate(mContext, R.layout.item_my_video_edit_thumbnail3, null);
        return new ViewHolder(view);
    }

    @SuppressLint("NewApi")
    @Override
    public final void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        ViewGroup.LayoutParams lp = viewHolder.rl_dragItem_itemView.getLayoutParams();
         if (mIsDragIng){
             lp.width =mDragWidth;
         }else {
             lp.width =mData.get(viewHolder.getAdapterPosition()).getItemWidth();
         }
         viewHolder.rl_root_itemView.setLayoutParams(lp);
         setThumbImage(viewHolder);
//        viewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
//        ImageAdapter adapter;
//        adapter = new ImageAdapter(mContext, mData.get(position).isEmptyView());
//
//        viewHolder.recyclerView.setAdapter(adapter);
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

        if (mData.get(position).isSelected()&&position!=0&&position!=mData.size()-1) {
            viewHolder.timelineTimeSpan.setBackgroundResource(R.drawable.circle_corner_transparent_bg);

//            viewHolder.timelineTimeSpan.setBackgroundResource(R.drawable.circle_corner_red_border_bg);
//             viewHolder.timelineTimeSpan.setVisibility(View.VISIBLE);
//             viewHolder.clickShowTimeSpan.setVisibility(View.GONE);
        } else {
            viewHolder.timelineTimeSpan.setBackgroundResource(R.drawable.circle_corner_transparent_bg);
//             viewHolder.timelineTimeSpan.setVisibility(View.GONE);
//             viewHolder.clickShowTimeSpan.setVisibility(View.VISIBLE);
        }
//         if(mData.get(position).getzOrder()>0){
//             viewHolder.itemView.setZ(mData.get(position).getzOrder());
//         }
        viewHolder.timelineTimeSpan.setCurrentPosition(position);
        viewHolder.timelineTimeSpan.setOnTrimChangeListener(new TimeSpan.OnTrimChangeListener() {

            @Override
            public void onChange(int currentPosition, boolean dragLeft, int distance) {
//                 if (dragLeft){
//                     RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) viewHolder.leftBlackView.getLayoutParams();
//                     lp.width +=distance;
//                     viewHolder.leftBlackView.setLayoutParams(lp);
//                 }else {
//                     RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) viewHolder.rightBlackView.getLayoutParams();
//                     lp.width +=distance;
//                     viewHolder.rightBlackView.setLayoutParams(lp);
//                 }
                if (mOnTrimChangeListener != null) {
                    mOnTrimChangeListener.onChange(currentPosition, dragLeft, distance);
                }
            }
        });
        viewHolder.clickShowTimeSpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectPosition(viewHolder.getAdapterPosition());
            }
        });
//        viewHolder.clickShowTimeSpan.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//
//                mSelectLongPosition = viewHolder.getAdapterPosition();
//                if (mSelectLongPosition == 0 || mSelectLongPosition == mData.size() - 1) {
//                    return true;
//                }
//                mIsDragIng = true;
//                setSelectLongPosition(mSelectLongPosition);
//                if (mDragStartListener != null) {
//                    mDragStartListener.onStartDrag(viewHolder);
//                }
//                return false;
//            }
//        });

    }
    private void setThumbImage(final ViewHolder viewHolder ){
        ThumbnailBean bean=  mData.get(viewHolder.getAdapterPosition());
        FrameLayout.LayoutParams mLayoutParams;
        if(bean.isEmptyView() ){
            mLayoutParams  = (FrameLayout.LayoutParams) viewHolder.iv_thumb.getLayoutParams();
            if(mIsDragIng){
                mLayoutParams.width = mWidth;

            }else {
                mLayoutParams.width =PUtil.getScreenW(mContext)/2;

            }
            viewHolder.iv_thumb.setImageResource(0);
            viewHolder.iv_thumb.setVisibility(View.INVISIBLE);
            viewHolder.iv_thumb.setLayoutParams(mLayoutParams);
            viewHolder.split_line.setVisibility(View.GONE);
        } else {
            mLayoutParams  = (FrameLayout.LayoutParams) viewHolder.iv_thumb.getLayoutParams();
            if(mIsDragIng){
                mLayoutParams.width=mWidth;
            }else {
                mLayoutParams.width=bean.getFrameWidth();
            }
            viewHolder.iv_thumb.setLayoutParams(mLayoutParams);
            viewHolder.iv_thumb.setVisibility(View.VISIBLE);
            if(bean.getType()== MediaConstant.IMAGE){
                viewHolder.iv_thumb.setTag(null);
//                Glide.with(mContext).load( bean.getMediaPath()).into(viewHolder.iv_thumb);
                VideoFrameThumb.getInstance().loadVideoThumbnail(mContext,bean.getMediaPath(),viewHolder.iv_thumb,bean.getTime());

            }else {
//                VideoFrameUtils.loadVideoThumbnail(mContext,bean.getMediaPath(),viewHolder.iv_thumb,bean.getTime());
                VideoFrameThumb.getInstance().loadVideoThumbnail(mContext,bean.getMediaPath(),viewHolder.iv_thumb,bean.getTime());
            }
            viewHolder.split_line.bringToFront();

            if(bean.isClipEnd()){
                if(mData.size()-2==viewHolder.getAdapterPosition()){
                    viewHolder.split_line.setVisibility(View.GONE);

                }else {
                    viewHolder.split_line.setVisibility(View.VISIBLE);
                    viewHolder.split_line.bringToFront();

                }
            }else {
                viewHolder.split_line.setVisibility(View.GONE);
            }
        }
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
    public void setSelectLongPosition(int position) {
        if (position == 0 || position == mData.size() - 1) {
            mData.get(position).setSelected(false);
            notifyItemChanged(position);
            return;
        }
        List<ThumbnailBean> thumbnailBeans=new ArrayList<>();
        for (int i = 0; i < mData.size(); i++) {
            ThumbnailBean bean=mData.get(i);
            if(bean.isClipStart()){
                if(mData.get(position).getClipPosition()==mData.get(i).getClipPosition()){
                    bean.setSelected(true);
                }else {
                    bean.setSelected(false);
                }
                thumbnailBeans.add(bean);
            }
        }
        mData.clear();
        mData.addAll(thumbnailBeans);
        notifyDataSetChanged();
    }
    /**
     * 选中坐标
     *
     * @param position
     */
    public void setSelectPosition(int position) {
        if (position == 0 || position == mData.size() - 1) {
            mData.get(position).setSelected(false);
            notifyItemChanged(position);
            return;
        }
        for (int i = 0; i < mData.size(); i++) {
            if(mData.get(position).getClipPosition()==mData.get(i).getClipPosition()){
                mData.get(i).setSelected(true);
            }else {
                mData.get(i).setSelected(false);
            }
        }
        notifyDataSetChanged();
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
            ThumbnailBean bean=mData.get(viewHolder.getAdapterPosition());

            if (viewHolder.getAdapterPosition() == 1) {
                if (TimelineData.instance().getCaptionData().size()==1) {
                    setTransitionViewStatus(viewHolder, View.GONE, View.GONE);
                }else {
                    if(bean.isClipEnd()){
                        setTransitionViewStatus(viewHolder, View.VISIBLE, View.GONE);
                    }else {
                        setTransitionViewStatus(viewHolder, View.GONE, View.GONE);
                    }
                }
            } else if (viewHolder.getAdapterPosition() == mData.size() - 2) {
                if(bean.isClipStart()){
                    setTransitionViewStatus(viewHolder, View.VISIBLE, View.GONE);

                }else {
                    setTransitionViewStatus(viewHolder, View.GONE, View.GONE);
                }
            } else {
                if(bean.isClipStart()){
                    setTransitionViewStatus(viewHolder, View.VISIBLE, View.GONE);
                }else
                if(bean.isClipEnd()){
                    setTransitionViewStatus(viewHolder, View.GONE, View.VISIBLE);

                }else {
                    setTransitionViewStatus(viewHolder, View.GONE, View.GONE);

                }

            }
        }
        setTransitionViewStatus(viewHolder, View.GONE, View.GONE);

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
        public ImageView iv_thumb;
        public TimeSpan timelineTimeSpan;
        public View clickShowTimeSpan;
        public View leftBlackView;
        public View rightBlackView;
        public View split_line;
        public FrameLayout rl_root_itemView;
        public FrameLayout rl_dragItem_itemView;
        public FrameLayout ivTransitionLeft;
        public FrameLayout ivTransitionRight;


        public ViewHolder(View itemView) {
            super(itemView);
            iv_thumb = itemView.findViewById(R.id.iv_thumb);
            timelineTimeSpan = itemView.findViewById(R.id.ts_myVideoEditActivity_timeSpan);
            clickShowTimeSpan = itemView.findViewById(R.id.v_myVideoEditActivity_clickShowTimeSpan);
            leftBlackView = itemView.findViewById(R.id.v_myVideoEditActivity_leftBlackView);
            rightBlackView = itemView.findViewById(R.id.v_myVideoEditActivity_rightBlackView);
            rl_root_itemView = itemView.findViewById(R.id.rl_root_itemView);
            rl_dragItem_itemView = itemView.findViewById(R.id.rl_dragItem_itemView);
            ivTransitionLeft = itemView.findViewById(R.id.ivTransitionLeft);
            ivTransitionRight = itemView.findViewById(R.id.ivTransitionRight);
            split_line = itemView.findViewById(R.id.split_line);
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
    }
}
