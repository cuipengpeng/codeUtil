package com.test.xcamera.cameraclip.adapter;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.dueeeke.videoplayer.player.IjkVideoView;
import com.test.xcamera.R;
import com.test.xcamera.utils.LoggerUtils;

import java.util.List;

public class SampleVideoItemTouchCallBack extends ItemTouchHelper.SimpleCallback {

    private   int mHorizontalDeviation;
    protected RecyclerView mRv;
    protected List mDatas;
    protected RecyclerView.Adapter mAdapter;
    //屏幕上最多同时显示几个Item
    public static int MAX_SHOW_COUNT=4;
    //每一级Scale相差0.05f，translationY相差7dp左右
    public static float SCALE_GAP=0.05f;
    public static int TRANS_Y_GAP =50;
    public static final int MAX_ROTATION = 15;
    //一个flag 判断左右滑
    private boolean isLeftSwipe;

    public SampleVideoItemTouchCallBack(RecyclerView rv, RecyclerView.Adapter adapter, List datas) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        mRv = rv;
        mAdapter = adapter;
        mDatas = datas;
        mHorizontalDeviation = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, mRv.getContext().getResources().getDisplayMetrics());
    }

    //水平方向是否可以被回收掉的阈值
    public float getThreshold(RecyclerView.ViewHolder viewHolder) {
        return mRv.getWidth() * /*getSwipeThreshold(viewHolder)*/ 0.5f;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
/*        Log.d("TAG", "getSwipeThreshold() called with: viewHolder.itemView.getX() = [" + viewHolder.itemView.getX() + "]");
        Log.d("TAG", "getSwipeThreshold() called with:  viewHolder.itemView.getWidth() / 2  = [" + viewHolder.itemView.getWidth() / 2 + "]");
        Log.d("TAG", "getSwipeThreshold() called with:  mRv.getX() = [" + mRv.getX() + "]");
        Log.d("TAG", "getSwipeThreshold() called with:  mRv.getWidth() / 2 = [" + mRv.getWidth() / 2 + "]");*/
        LoggerUtils.printLog("position="+viewHolder.getAdapterPosition()+"--"+viewHolder.getLayoutPosition());
        if (isTopViewCenterInHorizontal(viewHolder.itemView)) {
            return Float.MAX_VALUE;
        }
        return super.getSwipeThreshold(viewHolder);
    }

    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
        LoggerUtils.printLog("position=");
        View topView = mRv.getChildAt(mRv.getChildCount() - 1);
        if (isTopViewCenterInHorizontal(topView)) {
            return Float.MAX_VALUE;
        }
        return super.getSwipeEscapeVelocity(defaultValue);
    }

    @Override
    public float getSwipeVelocityThreshold(float defaultValue) {
        LoggerUtils.printLog("position=");
        View topView = mRv.getChildAt(mRv.getChildCount() - 1);
        if (isTopViewCenterInHorizontal(topView)) {
            return Float.MAX_VALUE;
        }
        return super.getSwipeVelocityThreshold(defaultValue);
    }

    /**
     * 返回TopView此时在水平方向上是否是居中的
     *
     * @return
     */
    public boolean isTopViewCenterInHorizontal(View topView) {
        Log.d("TAG", "getSwipeThreshold() called with: viewHolder.itemView.getX() = [" + topView.getX() + "]");
        Log.d("TAG", "getSwipeThreshold() called with:  viewHolder.itemView.getWidth() / 2  = [" + topView.getWidth() / 2 + "]");
        Log.d("TAG", "getSwipeThreshold() called with:  mRv.getX() = [" + mRv.getX() + "]");
        Log.d("TAG", "getSwipeThreshold() called with:  mRv.getWidth() / 2 = [" + mRv.getWidth() / 2 + "]");
        return Math.abs(mRv.getWidth() / 2 - topView.getX() - (topView.getWidth() / 2)) < mHorizontalDeviation;
    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        //Log.e("swipecard", "onSwiped() called with: viewHolder = [" + viewHolder + "], direction = [" + direction + "]");
        //rollBack(viewHolder);
        View itemView = viewHolder.itemView;
        if (itemView != null) {
            itemView.findViewById(R.id.iv_sampleVideoctivity_item_image).setVisibility(View.VISIBLE);
            IjkVideoView ijkVideoView = itemView.findViewById(R.id.ijk_sampleVideoctivity_item_ijkPlayer);
            ijkVideoView.release();
        }
        //★实现循环的要点
        Object remove = mDatas.remove(viewHolder.getLayoutPosition());
        mDatas.add(0, remove);
        mAdapter.notifyDataSetChanged();
        mOnScrollListener.onScrollOver(viewHolder.getLayoutPosition());
        LoggerUtils.printLog("position="+viewHolder.getAdapterPosition()+"--"+viewHolder.getLayoutPosition());
        //对rotate进行复位
        viewHolder.itemView.setRotation(0);

    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        Log.e("swipecard", "onChildDraw()  viewHolder = [" + viewHolder + "], dX = [" + dX + "], dY = [" + dY + "], actionState = [" + actionState + "], isCurrentlyActive = [" + isCurrentlyActive + "]");
        LoggerUtils.printLog("position="+viewHolder.getAdapterPosition()+"--"+viewHolder.getLayoutPosition());
        double swipValue = Math.sqrt(dX * dX + dY * dY);
        double fraction = swipValue / getThreshold(viewHolder);
        //边界修正 最大为1
        if (fraction > 1) {
            fraction = 1;
        }
        int childCount = recyclerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = recyclerView.getChildAt(i);
            //第几层,举例子，count =7， 最后一个TopView（6）是第0层，
            int level = childCount - i - 1;
            if (level > 0) {
                child.setScaleX((float) (1 - SCALE_GAP * level + fraction * SCALE_GAP));
                if (level < MAX_SHOW_COUNT - 1) {
                    child.setScaleY((float) (1 - SCALE_GAP * level + fraction * SCALE_GAP));
                    child.setTranslationY((float) (TRANS_Y_GAP * level - fraction * TRANS_Y_GAP));
                } else {
                    //child.setTranslationY((float) (mTranslationYGap * (level - 1) - fraction * mTranslationYGap));
                }
            } else {
                //第一层加了rotate & alpha的操作
                //不过他区分左右
                float xFraction = dX / getThreshold(viewHolder);
                //边界修正 最大为1
                if (xFraction > 1) {
                    xFraction = 1;
                } else if (xFraction < -1) {
                    xFraction = -1;
                }
                //rotate
                child.setRotation(xFraction * MAX_ROTATION);
            }
        }


        //可以在此判断左右滑：
        float v = mRv.getWidth() / 2 - viewHolder.itemView.getX() - (viewHolder.itemView.getWidth() / 2);
        if (v > 0) {
            isLeftSwipe = true;
        } else if (v < 0) {
            isLeftSwipe = false;
        }
    }

    private OnScrollListener mOnScrollListener;
    public void setOnScrollListener(OnScrollListener onScrollListener){
        this.mOnScrollListener = onScrollListener;
    }

    public interface OnScrollListener{
        void onScrollOver(int position);
    }
}
