/*
 * Copyright (C) 2015 Paul Burke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.test.xcamera.phonealbum.widget.drag;

import android.animation.ObjectAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.utils.DensityUtils;
import com.test.xcamera.utils.ItemTouchHelperCallBack;


/**
 * An implementation of {@link ItemTouchHelper.Callback} that enables basic drag & drop and
 * swipe-to-dismiss. Drag events are automatically started by an item long-press.<br/>
 * </br/>
 * Expects the <code>RecyclerView.Adapter</code> to listen for {@link
 * ItemTouchHelperAdapter} callbacks and the <code>RecyclerView.ViewHolder</code> to implement
 * {@link ItemTouchHelperViewHolder}.
 *
 * @author Paul Burke (ipaulpro)
 */
public class ItemVideoTouchHelperCallback extends ItemTouchHelper.Callback {
    private LinearLayout delItemLinearLayout;

    public static final float ALPHA_FULL = 1.0f;
    private float mScale = 1.0f;
    private float mMoveScale = mScale;

    private final ItemTouchHelperAdapter mAdapter;
    private boolean mIsInside = false;
    private boolean mDragStatus = false;
    private int selectedPosition = -1;
    private float mMotionEventActionX=0;

    private ItemTouchHelperCallBack.DragListener mDragListener;
    public ItemVideoTouchHelperCallback(LinearLayout delItemLinearLayout, ItemTouchHelperAdapter adapter) {
        this.delItemLinearLayout = delItemLinearLayout;
        mAdapter = adapter;
    }

    public void setDragListener(ItemTouchHelperCallBack.DragListener mDragListener) {
        this.mDragListener = mDragListener;
    }

    public void setMotionEventActionX(float mMotionEventActionX) {
        this.mMotionEventActionX = mMotionEventActionX;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        selectedPosition = viewHolder.getAdapterPosition();

        Log.i("tag","getMovementFlags---------------------------");
        // Set movement flags based on the layout manager
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            final int dragFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            final int swipeFlags =  ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        }else if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            final int swipeFlags = 0;
            return makeMovementFlags(dragFlags, swipeFlags);
        } else {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        }
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        if (source.getItemViewType() != target.getItemViewType()) {
            return false;
        }

        // Notify the adapter of the move
        mAdapter.onItemMove(source.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
        // Notify the adapter of the dismissal
        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
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
        int viewHeight=DensityUtils.dp2px(AiCameraApplication.getContext(),80);
        if(viewHolder.itemView.getHeight()>viewHeight){
            int h=(viewHolder.itemView.getHeight()-viewHeight);
            itemY=itemY-h/2;
        }

        int scaleItemWidth = (int) (itemWidth * mMoveScale);
        int scaleItemHeight = (int) (itemHeight * mMoveScale);
        int centerX = itemX + scaleItemWidth / 2;
        int centerY = itemY+ scaleItemHeight / 2;

        boolean isInside = false;

        isInside = centerY > delAreaY && centerY < delAreaY + delAreaHeight * 2 && centerX > delAreaX && centerX < delAreaX + delAreaWidth;
        Log.i("club","club:onChildDraw_centerY"+centerY+
                " delAreaY:"+delAreaY+
                " isInside:"+isInside+
                " Height:"+viewHolder.itemView.getHeight()+
                " viewHeight:"+viewHeight+
                " delAreaHeight:"+delAreaHeight);
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

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, true);
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        // We only want the active item to change
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof ItemTouchHelperViewHolder) {
                // Let the view holder know that this item is being moved or dragged
                ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
                itemViewHolder.onItemSelected();
            }
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
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);

        viewHolder.itemView.setAlpha(ALPHA_FULL);

//        if (viewHolder instanceof ItemTouchHelperViewHolder) {
//            // Tell the view holder it's time to restore the idle state
//            ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
//            itemViewHolder.onItemClear();
//        }
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
}
