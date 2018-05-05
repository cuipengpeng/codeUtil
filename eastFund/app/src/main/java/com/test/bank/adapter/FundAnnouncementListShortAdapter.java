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
import com.test.bank.bean.FundArchivesWithAnnouncementBean;
import com.test.bank.view.activity.WebViewActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 */

public class FundAnnouncementListShortAdapter extends RecyclerView.Adapter<FundAnnouncementListShortAdapter.ViewHolder> {
    Context mContext;
    List<FundArchivesWithAnnouncementBean.Disc_main_list> mDataList = new ArrayList<FundArchivesWithAnnouncementBean.Disc_main_list>();
    private int count = 3;

    public FundAnnouncementListShortAdapter(Context context, List<FundArchivesWithAnnouncementBean.Disc_main_list> dataList) {
        this.mContext = context;
        this.mDataList.clear();
        this.mDataList.addAll(dataList);
    }

    public void upateData(boolean isRefresh, List<FundArchivesWithAnnouncementBean.Disc_main_list> data) {
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

    public void setCount(int count) {
        this.count = count;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
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
        if(mDataList.size()<=count){
            count = mDataList.size();
        }
        return count;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ll_fundAnnouncementListActivity_item_announcement)
        LinearLayout announcementLinearLayout;
        @BindView(R.id.tv_fundAnnouncementListActivity_item_announcementContent)
        TextView announcementContentTextView ;
        @BindView(R.id.tv_fundAnnouncementListActivity_item_announcementDate)
        TextView announcementDateTextView ;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
