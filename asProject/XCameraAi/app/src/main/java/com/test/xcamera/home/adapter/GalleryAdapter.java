package com.test.xcamera.home.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.framwork.base.adapter.BaseRecyclerAdapter;
import com.test.xcamera.R;
import com.test.xcamera.bean.MoVideoByDate;
import com.test.xcamera.picasso.Picasso;
import com.test.xcamera.utils.DateUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by smz on 2019/11/15.
 */

public class GalleryAdapter extends BaseRecyclerAdapter<MoVideoByDate, GalleryAdapter.ViewHolder> {


    public GalleryAdapter(Context context) {
        super(context);
    }


    @Override
    public int getItemCount() {
        return mData.size()>=5?6:mData.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = View.inflate(mContext, R.layout.item_camera_clip_now, null);
        return new GalleryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(viewHolder.itemView, i);
            }
        });
//     if(i%2==0){
//         viewHolder.ivImg.setBackgroundResource(R.mipmap.pic5);
//     }else{
//         viewHolder.ivImg.setBackgroundResource(R.mipmap.pic6);
//     }

        if(mData.size()>0&&mData.size()-1==5&&i==5){
            viewHolder.tvTime.setVisibility(View.GONE);
            //最后一个时 默认一张图片和数据
            viewHolder.ivImg.setImageResource(R.mipmap.icon_slide_moren);
            viewHolder.tvDate.setText("更多...");
        }else if(mData.size()>0){
            viewHolder.tvTime.setVisibility(View.VISIBLE);
            MoVideoByDate  movideo=mData.get(i);
            if(!TextUtils.isEmpty(movideo.getmVideoThumbnail())){
                Picasso.with(mContext).load(movideo.getmVideoThumbnail()).placeholder(R.mipmap.bank_thumbnail_local)
                        .error(R.mipmap.bank_thumbnail_local).into(viewHolder.ivImg);
            }else{
                viewHolder.ivImg.setImageResource(R.mipmap.bank_thumbnail_local);
            }

            if(movideo.isCheck()){
                viewHolder.ivImg.setAlpha(0.5f);
            }else{
                viewHolder.ivImg.setAlpha(1.0f);
            }
            String date= DateUtils.DateFormat("MM月dd日",movideo.getmDate());
            viewHolder.tvDate.setText(date);
            String time=DateUtils.DateFormat("HH:mm",movideo.getmDate());
            viewHolder.tvTime.setText(time);
        }


    }

    static class ViewHolder extends  RecyclerView.ViewHolder {
        @BindView(R.id.rl1)
        RelativeLayout rlL;
        @BindView(R.id.iv_img)
        ImageView ivImg;
        @BindView(R.id.tv_date)
        TextView tvDate;
        @BindView(R.id.tv_time)
        TextView tvTime;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
