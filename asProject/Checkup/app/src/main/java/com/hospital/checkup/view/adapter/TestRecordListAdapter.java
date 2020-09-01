package com.hospital.checkup.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hospital.checkup.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TestRecordListAdapter extends BaseRecyclerAdapter {

    public TestRecordListAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tester_detail_test_record, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_item_testerDetailActivity_index)
        TextView indexTextView;
        @BindView(R.id.tv_item_testerDetailActivity_testName)
        TextView testNameTextView;
        @BindView(R.id.tv_item_testerDetailActivity_doctorName)
        TextView doctorNameTextView;
        @BindView(R.id.tv_item_testerDetailActivity_showChart)
        ImageView showChartImageView;
        @BindView(R.id.tv_item_testerDetailActivity_date)
        TextView dateTextView;
        @BindView(R.id.rl_item_testerDetailActivity_title)
        RelativeLayout titleRelativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
