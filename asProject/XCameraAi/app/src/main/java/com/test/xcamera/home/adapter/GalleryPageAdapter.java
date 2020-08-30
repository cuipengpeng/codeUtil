package com.test.xcamera.home.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.framwork.base.adapter.BaseRecyclerAdapter;
import com.test.xcamera.R;
import com.test.xcamera.bean.SideKeyBean;
import com.test.xcamera.picasso.Picasso;
import com.test.xcamera.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by smz on 2019/11/22.
 */

public class GalleryPageAdapter  extends PagerAdapter{

    private Context mContext;
    public List<SideKeyBean> mDataList = new ArrayList<>();
    private BaseRecyclerAdapter.OnRecyclerItemClickListener mItemClickListener;
    public static final int MAX_ITEM_COUNT = 6;

    public GalleryPageAdapter(Context mcontext) {
        this.mContext = mcontext;
    }

    public void updateData(boolean refresh, List<SideKeyBean> dataList){
        if(refresh){
            mDataList.clear();
        }
        if (dataList.size() >= MAX_ITEM_COUNT) {
            dataList = dataList.subList(0, MAX_ITEM_COUNT);
        }
        mDataList.addAll(dataList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }
    @Override public Object instantiateItem(ViewGroup container, int position) {
        View itemView = View.inflate(mContext, R.layout.item_gallery_for_sidekey, null);
        ImageView imageView = itemView.findViewById(R.id.iv_img);
        TextView tv_date = itemView.findViewById(R.id.tv_date);
        TextView tv_time = itemView.findViewById(R.id.tv_time);

        SideKeyBean sideKeyBean = mDataList.get(position);
        if (sideKeyBean.isSelected()) {
            imageView.setAlpha(0.5f);
        } else {
            imageView.setAlpha(1.0f);
        }
        String time = DateUtils.DateFormat("HH:mm", sideKeyBean.getDate());
        tv_time.setText(time);

        if(position < (MAX_ITEM_COUNT-1)){
            tv_time.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(sideKeyBean.getVideo_thumbnail())) {
                Picasso.with(mContext).load(sideKeyBean.getVideo_thumbnail()).placeholder(R.mipmap.bank_thumbnail_local)
                        .error(R.mipmap.bank_thumbnail_local).into(imageView);
            } else {
                imageView.setImageResource(R.mipmap.bank_thumbnail_local);
            }
            String date = DateUtils.DateFormat("MM月dd日", sideKeyBean.getDate());
            tv_date.setText(date);
        }else{
            tv_time.setVisibility(View.GONE);
            //最后一个时 默认一张图片和数据
            imageView.setImageResource(R.mipmap.more_data_in_sidekey);
            tv_date.setText("");
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(itemView, position);
            }
        });

        container.addView(itemView);
        return itemView;
    }

    @Override public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void setOnRecyclerItemClickListener(BaseRecyclerAdapter.OnRecyclerItemClickListener clickListener) {
        this.mItemClickListener = clickListener;
    }
}
