package com.test.bank.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.bean.FundAnnouncementListBean;
import com.test.bank.view.activity.WebViewActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 */

public class FundAnnouncementListAdapter extends RecyclerView.Adapter<FundAnnouncementListAdapter.ViewHolder> {
    Context mContext;
    List<FundAnnouncementListBean.NoticeList> mDataList = new ArrayList<FundAnnouncementListBean.NoticeList>();

    public FundAnnouncementListAdapter(Context context) {
        this.mContext = context;
        this.mDataList.clear();
    }

    public void upateData(boolean isRefresh, List<FundAnnouncementListBean.NoticeList> data) {
        if (isRefresh) {
            this.mDataList.clear();
        }
        this.mDataList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fund_announcement_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if(position == mDataList.size()-1){
            holder.dividerLineView.setVisibility(View.GONE);
        }else {
            holder.dividerLineView.setVisibility(View.VISIBLE);
        }

        holder.announcementContentTextView.setText(mDataList.get(position).getTitle());
        holder.announcementDateTextView.setText(mDataList.get(position).getDeclaredate());
        holder.announcementLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent starter = new Intent(mContext, WebViewActivity.class);
                starter.putExtra(WebViewActivity.KEY_OF_HTML_URL, mDataList.get(position).getH5_url());
                mContext.startActivity(starter);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ll_fundAnnouncementListActivity_item_announcement)
        LinearLayout announcementLinearLayout;
        @BindView(R.id.tv_fundAnnouncementListActivity_item_announcementContent)
        TextView announcementContentTextView ;
        @BindView(R.id.tv_fundAnnouncementListActivity_item_announcementDate)
        TextView announcementDateTextView ;
        @BindView(R.id.v_fundAnnouncementListActivity_item_dividerLine)
        View dividerLineView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
