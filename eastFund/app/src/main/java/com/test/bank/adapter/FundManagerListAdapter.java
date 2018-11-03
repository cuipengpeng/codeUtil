package com.test.bank.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.bean.FundManagerDetailBean;
import com.test.bank.view.activity.FundManagerDetailActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 */

public class FundManagerListAdapter extends RecyclerView.Adapter<FundManagerListAdapter.ViewHolder> {
    public static final String KEY_OF_FUND_MANAGER_MODEL = "fundManagerModelKey";
    Context mContext;
    List<FundManagerDetailBean.FundManager> mDataList = new ArrayList<FundManagerDetailBean.FundManager>();

    public FundManagerListAdapter(Context context) {
        this.mContext = context;
        this.mDataList.clear();
    }

    public void upateData(boolean isRefresh, List<FundManagerDetailBean.FundManager> data) {
        if (isRefresh) {
            this.mDataList.clear();
        }
        this.mDataList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fund_manager_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if(position == mDataList.size()-1){
            holder.dividerLineView.setVisibility(View.GONE);
        }else {
            holder.dividerLineView.setVisibility(View.VISIBLE);
        }
        holder.managerNameTextView.setText(mDataList.get(position).getIndi_name());
        holder.managerWorkingDateTextView.setText(mDataList.get(position).getDeclaredate()+"");
        holder.managerRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FundManagerDetailActivity.class);
                intent.putExtra(KEY_OF_FUND_MANAGER_MODEL, mDataList.get(position));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rl_fundManagerListActivity_item_manager)
        RelativeLayout managerRelativeLayout ;
        @BindView(R.id.tv_fundManagerListActivity_item_managerName)
        TextView managerNameTextView ;
        @BindView(R.id.tv_fundManagerListActivity_item_managerWorkingDate)
        TextView managerWorkingDateTextView ;
        @BindView(R.id.v_fundManagerListActivity_item_dividerLine)
        View dividerLineView;

        ImageView managerImageView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            managerImageView = view.findViewById(R.id.iv_fundManagerListActivity_item_managerImage);
        }
    }
}
