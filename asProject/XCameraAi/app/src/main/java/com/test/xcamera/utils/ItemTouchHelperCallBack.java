package com.test.xcamera.utils;

import android.animation.ObjectAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Property;
import android.view.View;
import android.widget.LinearLayout;

import com.framwork.base.adapter.BaseRecyclerAdapter;

import java.util.Collections;

/**
 * Created by DELL on 2019/7/4.
 */

public class ItemTouchHelperCallBack extends ItemTouchHelper.Callback {

    private BaseRecyclerAdapter adapter;
    private LinearLayout delItemLinearLayout;
    private int selectedPosition = -1;

    private float mScale = 1.0f;
    private float mAlpha = 1.0f;
    private float mInsideScale = 0.8f;
    private float mInsideAlpha = 0.4f;
    private float mMoveScale = mScale;

    private boolean mIsInside = false;
    private boolean mDragStatus = false;
    private DragListener mDragListener;

    public ItemTouchHelperCallBack(BaseRecyclerAdapter  adapter){
        this.adapter = adapter;
    }

    public ItemTouchHelperCallBack(LinearLayout delItemLinearLayout, BaseRecyclerAdapter  adapter){
        this.delItemLinearLayout = delItemLinearLayout;
        this.adapter = adapter;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        selectedPosition = recyclerView.getLayoutManager().getPosition(viewHolder.itemView);
        int dragFrlg = 0;
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager || recyclerView.getLayoutManager() instanceof LinearLayoutManager){
            if(delItemLinearLayout!=null){
                dragFrlg = ItemTouchHelper.DOWN|ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT;
            }else {
                dragFrlg = ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT;
            }
        }
        return makeMovementFlags(dragFrlg,0);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        //得到当拖拽的viewHolder的Position
        int fromPosition = viewHolder.getAdapterPosition();
        //拿到当前拖拽到的item的viewHolder
        int toPosition = target.getAdapterPosition();
        //滑动事件  下面注释的代码，滑动后数据和条目错乱，被舍弃
        Collections.swap(adapter.mData,fromPosition, toPosition);

//        if (fromPosition < toPosition) {
//            for (int i = fromPosition; i < toPosition; i++) {
//                Collections.swap(list, i, i + 1);
//            }
//        } else {
//            for (int i = fromPosition; i > toPosition; i--) {
//                Collections.swap(list, i, i - 1);
//            }
//        }
        adapter.notifyItemMoved(fromPosition, toPosition);
        selectedPosition = toPosition;
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        //侧滑删除可以使用；
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }
    /**
     * 长按选中Item的时候开始调用
     * 长按高亮
     * @param viewHolder
     * @param actionState
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
//            viewHolder.itemView.setBackgroundColor(Color.GRAY);
            //获取系统震动服务//震动70毫秒
//                    Vibrator vib = (Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);
//                    vib.vibrate(70);
            if (mDragListener != null) {
                mDragListener.onDragStart();
            }
            mDragStatus = true;
        }else {
            if (mDragListener != null) {
                mDragListener.onDragFinish(mIsInside, selectedPosition);
            }
            mDragStatus = false;
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (delItemLinearLayout == null || isActivatingAniming(viewHolder.itemView)) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            return;
        }
        int delAreaWidth = delItemLinearLayout.getWidth();
        int delAreaHeight = delItemLinearLayout.getHeight();
        int[] delLocation = new int[2];
        delItemLinearLayout.getLocationInWindow(delLocation);
        int delAreaX = delLocation[0];
        int delAreaY = delLocation[1];

        int itemWidth = viewHolder.itemView.getWidth();
        int itemHeight = viewHolder.itemView.getHeight();
        int[] itemLocation = new int[2];
        viewHolder.itemView.getLocationInWindow(itemLocation);
        int itemX = itemLocation[0];
        int itemY = itemLocation[1];


        int scaleItemWidth = (int) (itemWidth * mMoveScale);
        int scaleItemHeight = (int) (itemHeight * mMoveScale);
        int centerX = itemX + scaleItemWidth / 2;
        int centerY = itemY + scaleItemHeight / 2;

        boolean isInside = false;
        isInside = centerY > delAreaY && centerY < delAreaY + delAreaHeight && centerX > delAreaX && centerX < delAreaX + delAreaWidth;
        if (isInside != mIsInside && mDragStatus) {
//            clearActivatingAnim(viewHolder.itemView);
//            if (isInside) {
//                mMoveScale = mInsideScale;
//                startActivatingAnim(viewHolder.itemView, mScale, mInsideScale, 150);
//                viewHolder.itemView.setAlpha(mInsideAlpha);
//            } else {
//                mMoveScale = mScale;
//                startActivatingAnim(viewHolder.itemView, mInsideScale, mScale, 150);
//                viewHolder.itemView.setAlpha(mAlpha);
//            }
            if (mDragListener != null) {
                mDragListener.onDragAreaChange(isInside);
            }
        }
        mIsInside = isInside;

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    /**
     * 手指松开的时候还原高亮
     * @param recyclerView
     * @param viewHolder
     */
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
    }

    private void startActivatingAnim(View view, float from, float to, long duration) {
        Object tag = view.getTag();
        if (tag instanceof ObjectAnimator) {
            return;
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, scaleProperty, from, to);
        animator.setDuration(duration);
        animator.start();
        view.setTag(animator);
    }

    private boolean isActivatingAniming(View view) {
        Object tag = view.getTag();
        if (tag instanceof ObjectAnimator) {
            ObjectAnimator animator = (ObjectAnimator) tag;
            if (animator.isRunning()) {
                return true;
            }
        }
        return false;
    }

    private void clearActivatingAnim(View view) {
        Object tag = view.getTag();
        if (tag instanceof ObjectAnimator) {
            ObjectAnimator animator = (ObjectAnimator) tag;
            animator.cancel();
            view.setTag(null);
        }
    }

    private ScaleProperty scaleProperty = new ScaleProperty("scale");

    public static class ScaleProperty extends Property<View, Float> {
        public ScaleProperty(String name) {
            super(Float.class, name);
        }

        @Override
        public Float get(View object) {
            return object.getScaleX();
        }

        @Override
        public void set(View object, Float value) {
            object.setScaleX(value);
            object.setScaleY(value);
        }
    }

    public interface DragListener {
        void onDragStart();

        void onDragFinish(boolean delete, int selectedPosition);

        void onDragAreaChange(boolean isInside);
    }

    public void setDragListener(DragListener listener) {
        mDragListener = listener;
    }

}
