package com.test.xcamera.dymode.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.test.xcamera.R;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.utils.GlideUtils;
import com.ss.android.ugc.effectmanager.effect.model.Effect;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zll on 2020/2/25.
 */

public class DyPropsAdapter extends RecyclerView.Adapter<DyPropsAdapter.ViewHolder> {
    private List<Effect> mEffectList;
    private OnItemClickListener mItemClickListener;
    private View.OnClickListener mOnClickListener;
    private int lastSelectPos = -1;
    private View lastSelectedView = null;
    private Animation mAnimation;

    public DyPropsAdapter(OnItemClickListener listener, List<Effect> effects) {
        mItemClickListener = listener;
        if (mEffectList == null)
            mEffectList = new ArrayList<>();
        if (effects != null)
            mEffectList.addAll(effects);
        initClickListener();
        mAnimation = AnimationUtils.loadAnimation(AiCameraApplication.getContext(), R.anim.dy_prop_download_anim);
    }

    public void setData(List<Effect> effects) {
        if (mEffectList != null) {
            mEffectList.clear();
            mEffectList.addAll(effects);
        }
        lastSelectPos = -1;
        notifyDataSetChanged();
    }

    public void resetSelect() {
        lastSelectPos = -1;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dy_props_recyclerview_item_view, viewGroup, false);
        DyPropsAdapter.ViewHolder viewHolder = new DyPropsAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            if (payloads.contains(1)) {
                holder.state.setBackgroundResource(R.mipmap.icon_dy_download);
                holder.state.startAnimation(mAnimation);
            } else if (payloads.contains(2)) {
                holder.state.clearAnimation();
                holder.state.setBackgroundResource(R.mipmap.icon_dy_props_download);
                holder.state.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder dyPropsViewHolder, final int position) {
        if (mEffectList == null) return;
        Effect effect = mEffectList.get(position);
        Glide.with(AiCameraApplication.getContext()).load(effect.getIconUrl().getUrlList().get(0)).apply(GlideUtils.DY_PROP_OPTIONS)
                .thumbnail(0.5f)
                .into(dyPropsViewHolder.background);
        if (effect.isDownloaded()) {
            dyPropsViewHolder.state.setVisibility(View.GONE);
            if (lastSelectPos == position) {
                dyPropsViewHolder.select.setSelected(true);
                lastSelectedView = dyPropsViewHolder.select;
            } else {
                dyPropsViewHolder.select.setSelected(false);
            }
        } else {
            dyPropsViewHolder.state.clearAnimation();
            dyPropsViewHolder.state.setBackgroundResource(R.mipmap.icon_dy_props_download);
            dyPropsViewHolder.state.setVisibility(View.VISIBLE);
        }
        if (mOnClickListener != null) {
            dyPropsViewHolder.select.setTag(position);
            dyPropsViewHolder.itemView.setFocusable(true);
            dyPropsViewHolder.select.setOnClickListener(mOnClickListener);
        }
    }

    @Override
    public int getItemCount() {
        if (mEffectList != null && mEffectList.size() > 0)
            return mEffectList.size();
        return 0;
    }

    public void initClickListener() {
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectIndex = Integer.parseInt(v.getTag().toString());
//                if (selectIndex != lastSelectPos) {
//                    if (lastSelectedView != null && (int) lastSelectedView.getTag() == lastSelectPos) {
//                        lastSelectedView.setSelected(false);
//                    }
//                    v.setSelected(true);
                Effect effect = mEffectList.get(selectIndex);
                lastSelectPos = selectIndex;
                lastSelectedView = v;
                if (effect.isDownloaded()) {
                    notifyDataSetChanged();
                }
                mItemClickListener.onItemClick(selectIndex, effect);
//                }
//                else{
//                    lastSelectedView = null;
//                    lastSelectPos = -1;
//                    v.setSelected(false);
//                    mItemClickListener.onItemClick(v);
//                }
            }
        };
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView background;
        ImageView state;
        RelativeLayout select;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            select = itemView.findViewById(R.id.dy_props_item_select_bg);
            background = itemView.findViewById(R.id.dy_props_item_bg);
            state = itemView.findViewById(R.id.dy_props_item_state_image);
        }
    }

    public void selectItem(int position, ViewHolder viewHolder) {
        if (lastSelectPos == position) {
            viewHolder.select.setSelected(true);
            lastSelectedView = viewHolder.select;
        } else {
            viewHolder.select.setSelected(false);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, Effect effect);
    }
}
