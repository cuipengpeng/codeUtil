package com.jf.jlfund.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jf.jlfund.R;
import com.jf.jlfund.bean.OptionalFundBean;
import com.jf.jlfund.utils.CommonUtil;
import com.jf.jlfund.utils.StringUtil;
import com.jf.jlfund.view.activity.SingleFundDetailActivity;
import com.jf.jlfund.view.fragment.OptionalFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 */

public class OptionalFundAdapter extends RecyclerView.Adapter<OptionalFundAdapter.ViewHolder> {
    private Context mContext;
    public List<OptionalFundBean> mDataList = new ArrayList<OptionalFundBean>();
    private OptionalFragment mOptionalFragment;
    public  StringBuffer delFundCodeStr =new StringBuffer("");

    public OptionalFundAdapter(Context context, List<OptionalFundBean> dataList, OptionalFragment optionalFragment) {
        this.mContext = context;
        if(dataList!=null && dataList.size()>0){
            this.mDataList.clear();
            this.mDataList.addAll(dataList);
        }
        this.mOptionalFragment = optionalFragment;
    }

    public void upateData(boolean isRefresh, List<OptionalFundBean> data) {
        if (isRefresh) {
            this.mDataList.clear();
        }
        if(data!=null && data.size()>0){
            this.mDataList.addAll(data);
        }
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_optional_fund, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if(position%2 == 1){
            holder.itemLinearLayout.setBackgroundResource(R.color.appGrayTitleBackgroundColor);
        }else  {
            holder.itemLinearLayout.setBackgroundResource(R.color.color_ffffff);
        }

        holder.fundNameTextView.setText(mDataList.get(position).getFundName());
        holder.fundTypeTextView.setText(mDataList.get(position).getNav());
        StringUtil.setMoneyTextView(mContext, mDataList.get(position).getDaygrowth()+"", holder.fundYieldTextView);
        holder.fundYieldTextView.setText(holder.fundYieldTextView.getText().toString().trim()+"%");


        if(mOptionalFragment.currentText == mOptionalFragment.FINISH_TEXT){
            holder.delImageView.setVisibility(View.VISIBLE);
            holder.itemLinearLayout.setOnClickListener(null);
        }else {
            holder.delImageView.setVisibility(View.GONE);
            holder.itemLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonUtil.startActivity(mContext, SingleFundDetailActivity.class, mDataList.get(position).getFundCode());
                }
            });
        }

        holder.delImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delFundCodeStr.append(mDataList.get(position).getFundCode()).append(";");
                mDataList.remove(position);
                notifyDataSetChanged();
                if(mDataList.size()<=0){
                    mOptionalFragment.showNoDataView();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_optionalFragment_item_del)
        ImageView delImageView;
        @BindView(R.id.tv_optionalFragment_item_fundName)
        TextView fundNameTextView ;
        @BindView(R.id.tv_optionalFragment_item_fundType)
        TextView fundTypeTextView ;
        @BindView(R.id.tv_optionalFragment_item_fundYield)
        TextView fundYieldTextView ;
        @BindView(R.id.ll_optionalFragment_item)
        LinearLayout itemLinearLayout;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
