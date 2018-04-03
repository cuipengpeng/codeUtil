package com.jfbank.qualitymarket.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * 功能：首页板式4适配器<br>
 * 作者：赵海<br>
 * 时间： 2017/2/27 0027<br>.
 * 版本：1.2.0
 */

public class HomeGoodsInfoHorAdapter extends RecyclerView.Adapter<HomeGoodsInfoHorAdapter.ViewHolder> {
    Context mContext;
    List<PrudouctBean> data = new ArrayList<>();
    int itemWith = 200;

    public HomeGoodsInfoHorAdapter(Context context) {
        this.mContext = context;
        itemWith = CommonUtils.getDisplayMetrics(mContext).widthPixels * 3/ 8;
    }

    /**
     * 更新数据
     *
     * @param data
     */
    public void updateData(List<PrudouctBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_goods_info_four,
                parent, false);
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(itemWith, -2);
        view.setLayoutParams(params);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (TextUtils.isEmpty(data.get(position).getProImage())) {
            Picasso.with(mContext).load(R.mipmap.icon_default_product_style1).into(holder.ivGoodsPic);
        } else {
            Picasso.with(mContext).load(data.get(position).getProImage()).placeholder(R.mipmap.icon_default_product_style1).into(holder.ivGoodsPic);
        }
        holder.tvGoodsName.setText(CommonUtils.toDBC(data.get(position).getProName()));
        holder.tvPrice.setText("￥" + data.get(position).getPrice());
        holder.tvMinprice.setText("月付：￥" + data.get(position).getMonthAmount() + "起");
        holder.fllGoodinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.startGoodsDeteail(mContext, data.get(position).getProductNo());
            }
        });
    }

    @Override
    public int getItemCount() {
        return CommonUtils.isEmptyList(data) ? 0 : data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.iv_goods_pic)
        ImageView ivGoodsPic;
        @InjectView(R.id.tv_goods_name)
        TextView tvGoodsName;
        @InjectView(R.id.tv_price)
        TextView tvPrice;
        @InjectView(R.id.tv_minprice)
        TextView tvMinprice;
        @InjectView(R.id.fll_goodinfo)
        ForegroundLinearLayout fllGoodinfo;

        ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}
