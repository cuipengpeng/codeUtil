package com.test.bank.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.bean.ChaseHotBean;
import com.test.bank.utils.ImageUtils;
import com.test.bank.utils.UIUtils;
import com.test.bank.view.activity.BuyGoodFundDetailActivity;

import java.util.List;

/**
 * Created by 55 on 2017/12/8.
 */

public class ChaseHotAdapter extends RecyclerView.Adapter<ChaseHotAdapter.ChaseHotViewHolder> {

    private Context context;
    private List<ChaseHotBean> mData;

    public ChaseHotAdapter(Context context, List<ChaseHotBean> chaseHotList) {
        this.context = context;
        this.mData = chaseHotList;
    }

    @Override
    public ChaseHotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChaseHotViewHolder(LayoutInflater.from(context).inflate(R.layout.item_chase_hot_list, parent, false));
    }

    @Override
    public void onBindViewHolder(ChaseHotViewHolder holder, final int position) {
        UIUtils.setText(holder.tvTitle, mData.get(position).getTitle(), "");
        UIUtils.setText(holder.tvCode, mData.get(position).getSecondTitle(), "");
        ImageUtils.displayImage(context, mData.get(position).getImgUrl(), holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuyGoodFundDetailActivity.open(context, mData.get(position).getId(), false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class ChaseHotViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tvTitle;
        TextView tvCode;

        public ChaseHotViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_item_chase_hot_bg);
            tvTitle = itemView.findViewById(R.id.tv_item_chase_hot_title);
            tvCode = itemView.findViewById(R.id.tv_item_chase_hot_code);
        }
    }
}
