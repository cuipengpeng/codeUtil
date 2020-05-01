package com.jfbank.qualitymarket.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.model.PrudouctBean;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.widget.ForegroundLinearLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by 赵海 on 2016/11/12.
 */

public class HomeGoodsInfoAdapter extends BaseAdapter {
    Context mContext;
    List<PrudouctBean> data = new ArrayList<>();
    private int type = -1;

    public void updateData(List<PrudouctBean> data) {
        this.data.clear();
        this.data = data;

        notifyDataSetChanged();
    }

    public HomeGoodsInfoAdapter(Context context, int type) {
        mContext = context;
        this.type = type;
    }

    @Override
    public int getCount() {
        return CommonUtils.isEmptyList(data) ? 0 : data.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemViewType(int position) {
        return type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            if (getItemViewType(position) == 1) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_goods_info_one,viewGroup, false);
            } else if (getItemViewType(position) == 2) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_goods_info_two,viewGroup, false);
            } else if (getItemViewType(position) == 3) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_goods_info_three,viewGroup, false);
            } else {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_goods_info_three,viewGroup, false);
            }
            viewHolder = new ViewHolder(convertView, getItemViewType(position));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (TextUtils.isEmpty(data.get(position).getProImage())) {
            Picasso.with(mContext).load(R.mipmap.icon_default_product_style1).into(viewHolder.ivGoodsPic);
        } else {
            Picasso.with(mContext).load(data.get(position).getProImage()).placeholder(R.mipmap.icon_default_product_style1).into(viewHolder.ivGoodsPic);
        }
        viewHolder.tvGoodsName.setText(CommonUtils.toDBC(data.get(position).getProName()));
        if (getItemViewType(position) == 1 || getItemViewType(position) == 2) {
            viewHolder.tvPrice.setText("￥" + data.get(position).getPrice());
        }
        if (getItemViewType(position)==3){//板式3
            viewHolder.tvMinprice.setText("月付：￥" + data.get(position).getMonthAmount());
        }else{
            viewHolder.tvMinprice.setText("月付：￥" + data.get(position).getMonthAmount() + "起");
        }
        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.iv_goods_pic)
        ImageView ivGoodsPic;
        @InjectView(R.id.tv_goods_name)
        TextView tvGoodsName;
        TextView tvPrice;
        @InjectView(R.id.tv_minprice)
        TextView tvMinprice;
        @InjectView(R.id.fll_goodinfo)
        ForegroundLinearLayout fllGoodinfo;

        ViewHolder(View view, int type) {
            ButterKnife.inject(this, view);
            if (type == 2 || type == 1)
                tvPrice = (TextView) view.findViewById(R.id.tv_price);
        }
    }
}
