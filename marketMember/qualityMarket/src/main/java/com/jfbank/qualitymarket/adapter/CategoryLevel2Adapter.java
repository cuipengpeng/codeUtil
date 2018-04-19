package com.jfbank.qualitymarket.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.activity.SearchGoodsActivity;
import com.jfbank.qualitymarket.bean.CategoryLevel2Bean;
import com.jfbank.qualitymarket.bean.CategoryLevel3Bean;
import com.jfbank.qualitymarket.js.JsRequstInterface;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.DensityUtil;
import com.jfbank.qualitymarket.util.LogUtil;
import com.jfbank.qualitymarket.widget.NoScrollGridView;

import java.util.List;
import java.util.concurrent.ExecutorService;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * 作者：Rainbean on 2016/11/14 0014 11:04
 * <p>
 * 邮箱：rainbean@126.com
 */

public class CategoryLevel2Adapter extends BaseAdapter {
    private List<CategoryLevel2Bean> allData;
    private Context context;
    private String categoryLable1Id;
    int with = 99;
    CategoryLevel3Adapter categoryLevel3Adapter;
    public CategoryLevel2Adapter(List<CategoryLevel2Bean> allData, Context context) {
        this.allData = allData;
        this.context = context;
        with = (CommonUtils.getDisplayMetrics(context).widthPixels - CommonUtils.dipToPx(context, 93)) / 3;
    }

    public void updateData(String categoryLable1Id, List<CategoryLevel2Bean> allData, int updatePosition) {
        this.categoryLable1Id = categoryLable1Id;
        this.allData = allData;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return allData.size();
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
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_category_level2, parent, false);
            viewHolder = new ViewHolder(convertView);
            viewHolder.categoryLevel2GridView = (NoScrollGridView) convertView.findViewById(R.id.gv_categoryFragment_categoryLevel2);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final List<CategoryLevel3Bean> categoryLevel3BeanList = allData.get(position).getTaglistData();
        LogUtil.printLog("position=" + position + "---CategoryLevel2--categoryLevel3BeanList.size()=" + categoryLevel3BeanList.size() + "-------tagName=" + allData.get(position).getTagName());
        categoryLevel3Adapter=new CategoryLevel3Adapter(context,with,categoryLevel3BeanList);
        viewHolder.categoryLevel2GridView.setAdapter(categoryLevel3Adapter);
        final String lableType = allData.get(position).getType();
        if ("1".equals(lableType)) {
            viewHolder.categoryLevel2GridView.setHorizontalSpacing(0);
            viewHolder.categoryLevel2GridView.setVerticalSpacing(0);
        } else {
            viewHolder.categoryLevel2GridView.setHorizontalSpacing(DensityUtil.dip2px(context, 1f));
            viewHolder.categoryLevel2GridView.setVerticalSpacing(DensityUtil.dip2px(context, 1f));
        }
        viewHolder.categoryLevel2LableNameTextView.setText(allData.get(position).getTagName());
        viewHolder.categoryLevel2GridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                CategoryLevel3Bean categoryDesc = categoryLevel3BeanList.get(position);
                Intent intent = new Intent(context, SearchGoodsActivity.class);
                if ("0".equals(lableType)) {
                    intent.putExtra(JsRequstInterface.CLASSIFY_ID, categoryDesc.getKeyWord());
                } else if ("1".equals(lableType)) {
                    intent.putExtra(JsRequstInterface.CLASSIFY_ID, categoryDesc.getModelType());
                }
                intent.putExtra(JsRequstInterface.CATEGORY_LEVEL1_ID, categoryLable1Id);
                context.startActivity(intent);
            }
        });

        return convertView;
    }


    class ViewHolder {
        @InjectView(R.id.tv_categoryFragment_categoryLevel2LableName)
        TextView categoryLevel2LableNameTextView;

        NoScrollGridView categoryLevel2GridView;
        public ViewHolder(View v) {
            super();
            ButterKnife.inject(this, v);
        }


    }

}
