package com.test.xcamera.cameraclip.adapter;

import android.app.Activity;
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
import com.test.xcamera.bean.SideKeyBean;
import com.test.xcamera.picasso.Picasso;
import com.test.xcamera.utils.DateUtils;
import com.test.xcamera.utils.DisplayUtils;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SideKeyAdapter extends BaseRecyclerAdapter<SideKeyBean, SideKeyAdapter.ViewHolder> {

    public SideKeyAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = View.inflate(mContext, R.layout.item_camera_clip_now, null);
        return new SideKeyAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(viewHolder.itemView, i);
            }
        });
        int itemWidth = DisplayUtils.getwidth((Activity) mContext) / 2;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewHolder.rlL.getLayoutParams();
        layoutParams.width = itemWidth;
        layoutParams.height = itemWidth;
        viewHolder.rlL.setLayoutParams(layoutParams);
        SideKeyBean movideo = mData.get(i);
        if (!TextUtils.isEmpty(movideo.getVideo_thumbnail())) {
//            VideoFrameUtils.loadVideoThumbnail(mContext,movideo.getVideo_thumbnail(),viewHolder.ivImg,0);
            Picasso.with(mContext).load(movideo.getVideo_thumbnail()).placeholder(R.mipmap.bank_thumbnail_local)
                    .error(R.mipmap.bank_thumbnail_local).into(viewHolder.ivImg);
        } else {
            viewHolder.ivImg.setImageResource(R.mipmap.bank_thumbnail_local);
        }

        if (movideo.isSelected()) {
            viewHolder.ivImg.setAlpha(0.5f);
        } else {
            viewHolder.ivImg.setAlpha(1.0f);
        }
//        String time = TimeUtil.secToTime((int) (movideo.getmDate() / 1000) < 1 ? 1 : (int) (movideo.getmDate() / 1000));
        String year = DateUtils.DateFormat("yyyy年", movideo.getDate());
        String date = DateUtils.DateFormat("MM月dd日", movideo.getDate());
        String time = DateUtils.DateFormat("HH:mm", movideo.getDate());
        viewHolder.tvTime.setText(time);
        viewHolder.tvDate.setText(year+"\n"+date);

    }


    static class ViewHolder extends RecyclerView.ViewHolder {
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
            ButterKnife.bind(this, itemView);
        }
    }

}
