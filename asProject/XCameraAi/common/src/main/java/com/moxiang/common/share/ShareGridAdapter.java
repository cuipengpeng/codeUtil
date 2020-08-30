package com.moxiang.common.share;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.moxiang.common.R;

import java.util.List;

public class ShareGridAdapter extends BaseAdapter {
    public static final String TAG = "ShareGridAdapter";

    private Context context;
    private List<ShareGridItem> dataList;

    public ShareGridAdapter(Context context, List<ShareGridItem> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public ShareGridItem getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        ShareGridItem item = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item_share, null);
            holder = new ViewHolder();
            holder.tvLabel = convertView.findViewById(R.id.share_label);
            holder.ivLogo = convertView.findViewById(R.id.share_logo);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvLabel.setText(item.getLabel());
        holder.ivLogo.setImageResource(item.getLogoRes());

        return convertView;
    }


    class ViewHolder {
        TextView tvLabel;
        ImageView ivLogo;
    }

}