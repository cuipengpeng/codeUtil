package com.jfbank.qualitymarket.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.model.BottomFunctionMenu;
import com.jfbank.qualitymarket.util.DensityUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * 作者：Rainbean on 2016/11/14 0014 11:04
 * <p>
 * 邮箱：rainbean@126.com
 */

public class BottomFunctionMenuAdapter extends BaseAdapter
{
    private  List<BottomFunctionMenu> allData;
    private  Context context;

    public BottomFunctionMenuAdapter(List<BottomFunctionMenu> allData, Context context) {
        this.allData=allData;
        this.context=context;
    }

    public void updateData(List<BottomFunctionMenu> allData){
              this.allData=allData;
              notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return allData.size();
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
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder;
        if(convertView==null){
            convertView=View.inflate(context, R.layout.bottom_function_menu_item,null);
            viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }


        viewHolder.bottomFunctionMenuBadgeCountTextView.setVisibility(View.INVISIBLE);
        if(allData.get(position).getIntegral()>0 && AppContext.isLogin && AppContext.user != null){
            viewHolder.bottomFunctionMenuBadgeCountTextView.setText(allData.get(position).getIntegral()+"");
            viewHolder.bottomFunctionMenuBadgeCountTextView.setVisibility(View.VISIBLE);
        }

        viewHolder.bottomFunctionMenuNameTextView.setText(allData.get(position).getIconName()+"");
        Picasso.with(context).load(allData.get(position).getIconImage()).resize(DensityUtil.dip2px(context,35),DensityUtil.dip2px(context,35))
                 .placeholder(R.drawable.icon_default_recommend_type)
                 .into(viewHolder.categoryDescIcon);
        return convertView;
    }


    class ViewHolder
    {
        @InjectView(R.id.iv_myaccountFragment_bottomFunctionMenuImage)
        ImageView categoryDescIcon;
        @InjectView(R.id.tv_myaccountFragment_bottomFunctionMenuBadgeCount)
        TextView bottomFunctionMenuBadgeCountTextView;
        @InjectView(R.id.tv_myaccountFragment_bottomFunctionMenuName)
        TextView bottomFunctionMenuNameTextView;

        public ViewHolder(View v) {
            super();
            ButterKnife.inject(this, v);
        }
    }

}
