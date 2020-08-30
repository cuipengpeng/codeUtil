package com.test.xcamera.cameraclip.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.framwork.base.adapter.BaseRecyclerAdapter;
import com.test.xcamera.R;
import com.test.xcamera.bean.VideoTemplete;
import com.test.xcamera.utils.Constants;
import com.test.xcamera.utils.DensityUtils;
import com.test.xcamera.utils.GlideUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by DELL on 2019/11/4.
 */

public class CameraTemplateAdater extends BaseRecyclerAdapter<VideoTemplete, CameraTemplateAdater.ViewHolder> {


    public CameraTemplateAdater(Context context) {
        super(context);
    }


    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_template, viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        VideoTemplete template = mData.get(i);
        String section = mContext.getResources().getString(R.string.section);
        if(template.isNetTemplete()){
            GlideUtils.GlideLoader(mContext, Constants.getFileIdToUrl(template.getCoverFileId()+""), viewHolder.ivHead);
            viewHolder.tvTemplateContent.setText(template.getDescription());
            viewHolder.tvTime.setText(template.getDuration()+"s");
            viewHolder.tvPart.setText(template.getPartNum()+section);
        }else {
            GlideUtils.GlideLoader(mContext, Constants.template_img_url + Constants.template_path + template.getIcon(), viewHolder.ivHead);
            viewHolder.tvTemplateContent.setText(template.getContent());
            viewHolder.tvTime.setText(template.getVideo_length()+"s");
            viewHolder.tvPart.setText(template.getSegment_num()+section);
        }
        viewHolder.tvTemplateTitle.setText(template.getName());
        if (template.isCheck()) {
            viewHolder.useTextView.setText(R.string.using);
            viewHolder.useTextView.setTextColor(mContext.getResources().getColor(R.color.color_4a4a4a));
            viewHolder.useTextView.setBackgroundResource(R.drawable.circle_corner_gray_border_normal_3dp);
        } else {
            viewHolder.useTextView.setText(R.string.use);
            viewHolder.useTextView.setTextColor(mContext.getResources().getColor(R.color.appThemeColor));
            viewHolder.useTextView.setBackgroundResource(R.drawable.circle_corner_red_border_bg);
        }
        viewHolder.useTextView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(viewHolder.itemView, i);
            }
        });
        ViewGroup.LayoutParams rootLayoutParams= viewHolder.itemRootRelativeLayout.getLayoutParams();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewHolder.ivHead.getLayoutParams();
        if(!template.isCheck() && template.isHasClickOnce()){
            layoutParams.height = DensityUtils.dp2px(mContext,70);
            layoutParams.width = DensityUtils.dp2px(mContext,70);
            viewHolder.tvTime.setTextSize(15);
            viewHolder.tvPart.setTextSize(15);
            rootLayoutParams.height = DensityUtils.dp2px(mContext,80);
        }else {
            layoutParams.height = DensityUtils.dp2px(mContext,58);
            layoutParams.width = DensityUtils.dp2px(mContext,58);
            viewHolder.tvTime.setTextSize(12);
            viewHolder.tvPart.setTextSize(12);
            rootLayoutParams.height = DensityUtils.dp2px(mContext,70);
        }
        viewHolder.ivHead.setLayoutParams(layoutParams);
        viewHolder.itemRootRelativeLayout.setLayoutParams(rootLayoutParams);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!template.isCheck() && template.isHasClickOnce()){
                    return;
                }
                mItemClickListener.onItemClick(viewHolder.itemView, i);
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_item_videoTemplete_head)
        ImageView ivHead;
        @BindView(R.id.tv_template_title)
        TextView tvTemplateTitle;
        @BindView(R.id.tv_template_content)
        TextView tvTemplateContent;
        @BindView(R.id.tv_item_videoTemplete_time)
        TextView tvTime;
        @BindView(R.id.tv_item_videoTemplete_part)
        TextView tvPart;
        @BindView(R.id.tv_item_videoTemplete_use)
        TextView useTextView;
        @BindView(R.id.rl_item_videoTemplete_itemRoot)
        RelativeLayout itemRootRelativeLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
