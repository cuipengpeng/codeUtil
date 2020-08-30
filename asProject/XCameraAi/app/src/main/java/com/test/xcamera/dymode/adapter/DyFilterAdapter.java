package com.test.xcamera.dymode.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.test.xcamera.R;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.utils.GlideUtils;
import com.ss.android.ugc.effectmanager.effect.model.Effect;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zll on 2020/2/26.
 */

public class DyFilterAdapter extends RecyclerView.Adapter<DyFilterAdapter.ViewHolder> {
    private List<Effect> mEffectList;
    private OnItemClickListener mItemClickListener;
    private View.OnClickListener mOnClickListener;
    private int lastSelectPos = -1;
    private View lastSelectedView = null;
    private Animation mAnimation;

    public DyFilterAdapter(OnItemClickListener listener, List<Effect> effects) {
        mItemClickListener = listener;
        if (mEffectList == null) {
            mEffectList = new ArrayList<>();
        }
        if (effects != null)
            mEffectList.addAll(effects);
        initClickListener();
        mAnimation = AnimationUtils.loadAnimation(AiCameraApplication.getContext(), R.anim.dy_prop_download_anim);
    }

    public void setData(List<Effect> effects, int selection) {
        lastSelectPos = selection;
        mEffectList.addAll(effects);
        notifyDataSetChanged();
    }

    public void clearData() {
        lastSelectPos = -1;
        if (mEffectList != null)
            mEffectList.clear();
        notifyDataSetChanged();
    }

    public void resetSelect() {
        lastSelectPos = -1;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dy_filter_recyclerview_item_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.setIsRecyclable(false);
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
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        if (mEffectList == null) return;
        Effect effect = mEffectList.get(position);
        Glide.with(AiCameraApplication.getContext()).load(effect.getIconUrl().getUrlList().get(0))
                .apply(GlideUtils.DY_FILTER_OPTIONS)
                .thumbnail(0.5f)
                .into(viewHolder.background);
        viewHolder.name.setText(effect.getName());

        if (effect.isDownloaded()) {
            viewHolder.state.setVisibility(View.GONE);
            Log.d("", "onItemClick onBindViewHolder: lastSelectPos: " + lastSelectPos);
            if (lastSelectPos == position) {
                viewHolder.select.setSelected(true);
                viewHolder.name.setTextColor(0XFFFF7700);
                lastSelectedView = viewHolder.select;
            } else {
                viewHolder.select.setSelected(false);
                viewHolder.name.setTextColor(0XFFE7E7E7);
            }
        } else {
            viewHolder.state.clearAnimation();
            viewHolder.state.setBackgroundResource(R.mipmap.icon_dy_props_download);
            viewHolder.state.setVisibility(View.VISIBLE);
        }
//        if (lastSelectPos == position) {
//            viewHolder.select.setSelected(true);
//            viewHolder.name.setTextColor(0XFFFF7700);
//            lastSelectedView = viewHolder.select;
//        } else {
//            viewHolder.select.setSelected(false);
//            viewHolder.name.setTextColor(0XFFE7E7E7);
//        }
        if (mOnClickListener != null) {
            viewHolder.select.setTag(position);
            viewHolder.itemView.setFocusable(true);
            viewHolder.select.setOnClickListener(mOnClickListener);
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

                lastSelectPos = selectIndex;
                lastSelectedView = v;
                Effect effect = mEffectList.get(selectIndex);
                if (effect.isDownloaded()) {
                    notifyDataSetChanged();
                }
//                    mItemClickListener.onItemClick(selectIndex, mEffectList.get(selectIndex));
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
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            select = itemView.findViewById(R.id.dy_filter_item_select_bg);
            background = itemView.findViewById(R.id.dy_filter_item_bg);
            state = itemView.findViewById(R.id.dy_filter_item_state_image);
            name = itemView.findViewById(R.id.dy_filter_item_name);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, Effect effect);
    }
}
