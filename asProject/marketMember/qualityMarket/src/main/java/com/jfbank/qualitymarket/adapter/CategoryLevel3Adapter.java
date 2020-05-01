package com.jfbank.qualitymarket.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.bean.CategoryLevel3Bean;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.LogUtil;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * 作者：Rainbean on 2016/11/14 0014 11:04
 * <p>
 * 邮箱：rainbean@126.com
 */

public class CategoryLevel3Adapter extends BaseAdapter {
    private List<CategoryLevel3Bean> allData;
    private Context context;
    private int with = 188;

    public CategoryLevel3Adapter(Context context, int with, List<CategoryLevel3Bean> allData) {
        this.context = context;
        this.with = with;
        this.allData = allData;
    }

    public void updateData(List<CategoryLevel3Bean> allData) {
        this.allData = allData;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return CommonUtils.isEmptyList(allData) ? 0 : allData.size();
    }

    @Override
    public Object getItem(int position) {
        return allData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.category_desc_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        LogUtil.printLog("CategoryLevel3-----------imageid=" + allData.get(position).getImage() + "-------tagName=" + allData.get(position).getName() + "-----position=" + position);
        viewHolder.categoryDescName.setText(allData.get(position).getName());
        CommonUtils.loadCacheImage((Activity) context, viewHolder.categoryDescIcon, allData.get(position).getImage(), R.drawable.icon_default_recommend_type);
        return convertView;
    }


    class ViewHolder {
        @InjectView(R.id.tv_category_desc_name)
        TextView categoryDescName;
        @InjectView(R.id.iv_category_desc_icon)
        ImageView categoryDescIcon;

        public ViewHolder(View v) {
            super();
            ButterKnife.inject(this, v);
        }

    }

}
