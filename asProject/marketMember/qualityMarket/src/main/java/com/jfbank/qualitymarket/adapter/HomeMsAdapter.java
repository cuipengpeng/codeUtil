package com.jfbank.qualitymarket.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.helper.PicassoRoundTransform;
import com.jfbank.qualitymarket.model.MsProductBean;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.widget.ForegroundLinearLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能：首页秒杀适配器<br>
 * 作者：赵海<br>
 * 时间： 2017/2/27 0027<br>.
 * 版本：1.2.0
 */

public class HomeMsAdapter extends RecyclerView.Adapter<HomeMsAdapter.ViewHolder> {
    Context mContext;
    List<MsProductBean.ProductListModel> data = new ArrayList<>();
    int activityBox;
    int itemWith = 200;
    public HomeMsAdapter(Context context) {
        this.mContext = context;
        itemWith = CommonUtils.getDisplayMetrics(mContext).widthPixels * 3/ 8;
    }

    /**
     * 更新数据
     *
     * @param data
     */
    public void updateData(List<MsProductBean.ProductListModel> data, int activityBox) {
        this.activityBox = activityBox;
        if (data != null && data.size() > 10) {//
            this.data.clear();
            for (int i = 0; i < 10; i++) {
                this.data.add(data.get(i));
            }
        } else {
            this.data = data;
        }
        notifyDataSetChanged();
    }

    @Override
    public HomeMsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_home_ms,
                parent, false);
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(itemWith, -2);
        view.setLayoutParams(params);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HomeMsAdapter.ViewHolder holder, final int position) {
        holder.tvActivityYjprice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        holder.tvActivityYjprice.setText("￥" + data.get(position).getOriginPrice());
        holder.tvActivityPrice.setText(data.get(position).getBuyingPrice());
        holder.tvActivityYfprice.setText("月付￥" + data.get(position).getMonthAmount());
        Picasso.with(mContext).load(data.get(position).getProImage()).placeholder(R.drawable.icon_default_product).into(holder.ivActivityProduce);
        holder.llItemMs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//跳转到秒杀
                CommonUtils.startMsHtmlList(mContext,activityBox, data.get(position).getProductNo());
            }
        });
    }

    @Override
    public int getItemCount() {
        return CommonUtils.isEmptyList(data) ? 0 : data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivActivityProduce;
        TextView tvActivityPrice;
        TextView tvActivityYjprice;
        TextView tvActivityYfprice;
        ForegroundLinearLayout llItemMs;

        ViewHolder(View itemView) {
            super(itemView);
            ivActivityProduce = (ImageView) itemView.findViewById(R.id.iv_activity_produce);
            tvActivityPrice = (TextView) itemView.findViewById(R.id.tv_activity_price);
            tvActivityYjprice = (TextView) itemView.findViewById(R.id.tv_activity_yjprice);
            tvActivityYfprice = (TextView) itemView.findViewById(R.id.tv_activity_yfprice);
            llItemMs = (ForegroundLinearLayout) itemView.findViewById(R.id.ll_item_ms);
        }
    }
}
