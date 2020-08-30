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
import com.test.xcamera.bean.MoVideoByDate;
import com.test.xcamera.picasso.Picasso;
import com.test.xcamera.utils.DateUtils;
import com.test.xcamera.utils.DisplayUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TodayVideoListAdapter extends BaseRecyclerAdapter<MoVideoByDate, TodayVideoListAdapter.ViewHolder> {

    public TodayVideoListAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = View.inflate(mContext, R.layout.item_today_video_list_activity, null);
        return new TodayVideoListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        int itemWidth = DisplayUtils.getwidth((Activity) mContext) / 2;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewHolder.rlL.getLayoutParams();
        layoutParams.width = itemWidth;
        layoutParams.height = itemWidth;
        viewHolder.rlL.setLayoutParams(layoutParams);

        MoVideoByDate movideo = mData.get(i);
        if (!TextUtils.isEmpty(movideo.getmVideoThumbnail())) {
            Picasso.with(mContext).load(movideo.getmVideoThumbnail()).placeholder(R.mipmap.bank_thumbnail_local)
                    .error(R.mipmap.bank_thumbnail_local).into(viewHolder.ivImg);
        } else {
            viewHolder.ivImg.setImageResource(R.mipmap.bank_thumbnail_local);
        }
        if (movideo.isCheck()) {
            viewHolder.ivImg.setAlpha(0.5f);
        } else {
            viewHolder.ivImg.setAlpha(1.0f);
        }
//        String time = TimeUtil.secToTime((int) (movideo.getmDate() / 1000) < 1 ? 1 : (int) (movideo.getmDate() / 1000));
        String year = DateUtils.DateFormat("yyyy年", movideo.getmDate());
        String date = DateUtils.DateFormat("MM月dd日", movideo.getmDate());
        viewHolder.tvDate.setText(year+"\n"+date);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(viewHolder.itemView, i);
            }
        });
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rl1)
        RelativeLayout rlL;
        @BindView(R.id.iv_img)
        ImageView ivImg;
        @BindView(R.id.tv_date)
        TextView tvDate;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
