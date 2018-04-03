package com.jfbank.qualitymarket.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.bean.CategoryLevel1Bean;

import java.util.List;


/**
 * 作者：Rainbean on 2016/11/14 0014 11:04
 * 邮箱：rainbean@126.com
 */

public class CategoryLevel1Adapter extends BaseAdapter
{
    private  List<CategoryLevel1Bean> mMenus;
    private final Context context;
    //用于指定那个item显示选中的红色
    private int selectIndex;

    public CategoryLevel1Adapter(List<CategoryLevel1Bean> mMenus, Context context, int selectIndex){
        this.mMenus=mMenus;
        this.context=context;
        this.selectIndex=selectIndex;
    }

    public void updateData(List<CategoryLevel1Bean> allData, int selectIndex){
        this.mMenus=allData;
        this.selectIndex=selectIndex;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return mMenus.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView==null){
            convertView=View.inflate(context, R.layout.category_title_item,null);
            vh=new ViewHolder();
            vh.tv= (TextView) convertView.findViewById(R.id.tv_category_title);
            vh.iv= (ImageView) convertView.findViewById(R.id.iv_category_indicator);
            convertView.setTag(vh);
        }else {
            vh= (ViewHolder) convertView.getTag();
        }

        LinearLayout.LayoutParams selectParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //左上右下
        selectParams.setMargins(1,1,0,0);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(1,1,1,0);

        if(position==selectIndex){
            vh.tv.setTextColor(Color.RED);
            vh.tv.setBackgroundColor(context.getResources().getColor(R.color.categoryFragment_bgColor));
            vh.iv.setVisibility(View.VISIBLE);
            vh.tv.setLayoutParams(selectParams);
        }else {
            vh.tv.setTextColor(Color.BLACK);
            vh.tv.setBackgroundColor(Color.WHITE);
            vh.iv.setVisibility(View.INVISIBLE);
            vh.tv.setLayoutParams(params);
        }
        vh.tv.setText(mMenus.get(position).getUpCategoryName());
        return convertView;
    }

    class ViewHolder{
        TextView tv;
        ImageView iv;
    }
}
