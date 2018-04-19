package com.jfbank.qualitymarket.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.model.PrudouctBean;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.widget.ForceClickImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 赵海 on 2016/11/12.
 */

public class HomeGoodsImageAdapter extends BaseAdapter {
    Context mContext;
    List<PrudouctBean> data = new ArrayList<>();

    public void updateData(List<PrudouctBean> data) {
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public HomeGoodsImageAdapter(Context context) {
        mContext = context;
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
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_goods_info_six, viewGroup, false);
            viewHolder.fivGoodsImginfo = (ForceClickImageView) convertView.findViewById(R.id.fiv_goods_imginfo);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (TextUtils.isEmpty(data.get(position).getPicture())) {
            Picasso.with(mContext).load(R.mipmap.ic_default_goodsimg).into(viewHolder.fivGoodsImginfo);
        } else {
            Picasso.with(mContext).load(data.get(position).getPicture()).placeholder(R.mipmap.ic_default_goodsimg).into(viewHolder.fivGoodsImginfo);
        }
        return convertView;
    }

    private class ViewHolder {
        ForceClickImageView fivGoodsImginfo;
    }
}
