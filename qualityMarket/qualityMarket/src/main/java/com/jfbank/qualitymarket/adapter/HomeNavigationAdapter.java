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
import com.jfbank.qualitymarket.helper.PicassoRoundTransform;
import com.jfbank.qualitymarket.model.NavigationHomeBean;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.TDUtils;
import com.jfbank.qualitymarket.widget.ForegroundLinearLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 功能：<br>
 * 作者：赵海<br>
 * 时间： 2017/4/18 0018<br>.
 * 版本：1.2.0
 */

public class HomeNavigationAdapter extends BaseAdapter {
    Context mContext;
    List<NavigationHomeBean> data = new ArrayList<>();

    public void updateData(List<NavigationHomeBean> data) {
        this.data.clear();
        this.data = data;
        notifyDataSetChanged();
    }

    public HomeNavigationAdapter(Context context) {
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
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_home_navigation, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (TextUtils.isEmpty(data.get(position).getPicUrl())) {
            Picasso.with(mContext).load(R.drawable.icon_default_recommend_type).resize(CommonUtils.dipToPx(mContext, 80), CommonUtils.dipToPx(mContext, 80)).into(viewHolder.ivHomeType);
        } else {
            //
            Picasso.with(mContext).load(data.get(position).getPicUrl()).placeholder(R.drawable.icon_default_recommend_type).resize(CommonUtils.dipToPx(mContext, 80), CommonUtils.dipToPx(mContext, 80)).transform(new PicassoRoundTransform(CommonUtils.dipToPx(mContext, 4), CommonUtils.dipToPx(mContext, 4))).into(viewHolder.ivHomeType);
        }
        viewHolder.fllRecommendGoodstype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonUtils.startIntent(mContext, data.get(position).getAppPage(), data.get(position).getAppParams());
                TDUtils.onEvent(mContext, "100004", "10个Icon区", TDUtils.getInstance().putParams("分类名称", data.get(position).getName()).buildParams());
                TDUtils.onEventNavigation(mContext, position, data.get(position).getName(), TDUtils.getInstance().putParams("分类名称", data.get(position).getName()).buildParams());
            }
        });
        viewHolder.tvHomeType.setText(data.get(position).getName());
        return convertView;
    }


    class ViewHolder {
        @InjectView(R.id.iv_home_type)
        ImageView ivHomeType;
        @InjectView(R.id.tv_home_type)
        TextView tvHomeType;
        @InjectView(R.id.fll_recommend_goodstype)
        ForegroundLinearLayout fllRecommendGoodstype;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
