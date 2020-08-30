/*
 * Copyright (C) 2015 Tomás Ruiz-López.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.test.xcamera.phonealbum.adapter.hardwareadapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.test.xcamera.R;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.picasso.Picasso;
import com.editvideo.MediaConstant;
import com.editvideo.MediaData;
import com.editvideo.OnItemClick;
import com.editvideo.ScreenUtils;
import com.editvideo.TimeUtil;

import java.io.File;


public class AgendaItemViewHolder extends RecyclerView.ViewHolder {

    TextView textView;
    ImageView iv_item_image;
    RelativeLayout item_media_hideLayout;
    TextView tv_selected_num;
    private Activity  activity;
    private int mClickType;

    private ImageView is_alpha;
    private  ImageView  is_check;

    public AgendaItemViewHolder(View itemView, int type, Activity activity) {
        super(itemView);
        this.mClickType = type;
        this.activity=activity;
        textView = itemView.findViewById(R.id.tv_media_type);
        tv_selected_num = itemView.findViewById(R.id.tv_selected_num);
        iv_item_image = itemView.findViewById(R.id.iv_item_image);
        item_media_hideLayout = itemView.findViewById(R.id.item_media_hideLayout);

        is_check=itemView.findViewById(R.id.is_check);

        is_alpha=itemView.findViewById(R.id.is_alpha);
    }

    public void render(MediaData mediaData, final int se, final int position, final OnItemClick onItemClick) {
        //设置当前的图片为正方形
        int marginSizeLeftAndRight = (int) AiCameraApplication.mApplication.getResources().getDimension(R.dimen.select_recycle_marginLeftAndRight);
        int width = ScreenUtils.getWindowWidth(AiCameraApplication.mApplication) ;//- marginSizeLeftAndRight * 2;
//        int marginSizeStart = (int) AiCameraApplication.mApplication.getResources().getDimension(R.dimen.select_item_start_end);
        int marginSizeMiddle = (int) AiCameraApplication.mApplication.getResources().getDimension(R.dimen.select_item_between);
        int marginledf=(int) AiCameraApplication.mApplication.getResources().getDimension(R.dimen.select_item_start_end);
//        int itemWidth = (width - marginSizeStart * 2 - marginSizeMiddle * (GRIDITEMCOUNT - 1)) / GRIDITEMCOUNT;
        int itemWidth=(width-marginledf/2)/3;

        RecyclerView.LayoutParams param = new RecyclerView.LayoutParams(itemWidth, itemWidth);
        int columnMarginStartAndEnd = 0;
        int columnMarginMiddle = 0;
        int marginSizeTopAndEnd = ScreenUtils.dip2px(AiCameraApplication.mApplication, 4) / 2;
        if (position < 3) {
            marginSizeTopAndEnd = 0;
        }


        param.setMargins(0, 0, marginledf, 0);
        iv_item_image.setPadding(marginSizeMiddle,marginSizeMiddle,marginSizeMiddle,0);
        itemView.setLayoutParams(param);
        if (mediaData.getType() == MediaConstant.VIDEO) {
            textView.setVisibility(View.VISIBLE);
//            textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
//            textView.getPaint().setAntiAlias(true);//抗锯齿
            textView.setText(TimeUtil.secToTime((int) (mediaData.getDuration() / 1000) < 1 ? 1 : (int) (mediaData.getDuration() / 1000)));
        } else {
            textView.setVisibility(View.GONE);
        }


        item_media_hideLayout.setVisibility(mediaData.isState() ? View.VISIBLE : View.GONE);
//        if (mClickType == MediaConstant.TYPE_ITEMCLICK_SINGLE) {
//            tv_selected_num.setVisibility(View.GONE);
//        } else {
//            tv_selected_num.setText(mediaData.getPosition() + "");
//        }
        if(mediaData.isState()){
            is_alpha.setVisibility(View.VISIBLE);
//            is_alpha.setAlpha(0.5f);
            is_check.setImageResource(R.mipmap.picture_selected);
        }else {
            is_alpha.setVisibility(View.GONE);
        }



            Picasso.with(activity).load(mediaData.getThumbPath()).placeholder(R.mipmap.bank_thumbnail_local)
                    .error(R.mipmap.bank_thumbnail_local).into(iv_item_image);
//          else{
//
//          }
//        setImageByFile(mediaData.getPath(), itemWidth);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.OnItemClick(itemView, se, position);
            }
        });
    }

    private void setImageByFile(String iamgeFile, int width) {
        File file = new File(iamgeFile);
        RequestOptions options = new RequestOptions().centerCrop()
                .placeholder(R.mipmap.bank_thumbnail_local)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .override(width, width);
        Glide.with(AiCameraApplication.mApplication)
                .asBitmap()
                .load(file)
                .apply(options)
                .into(iv_item_image);
    }
}
