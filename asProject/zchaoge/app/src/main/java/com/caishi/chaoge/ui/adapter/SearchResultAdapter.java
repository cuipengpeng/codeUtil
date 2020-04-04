package com.caishi.chaoge.ui.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.caishi.chaoge.R;
import com.caishi.chaoge.bean.ScenarioBean;
import com.caishi.chaoge.http.Product;
import com.caishi.chaoge.utils.GlideUtil;
import com.caishi.chaoge.utils.Utils;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static com.caishi.chaoge.ui.activity.ScenarioActivity.KEY_OF_SCENERIO_RESULT_DATA;

/**
 */

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {
    public static final String TAG = SearchResultAdapter.class.getName();
    FragmentActivity mContext;
    public List<ScenarioBean> mDataList = new ArrayList<>();

    public SearchResultAdapter(FragmentActivity context) {
        this.mContext = context;
        this.mDataList.clear();
    }

    public void upateData(boolean isRefresh, List<ScenarioBean> data) {
        if (isRefresh) {
            this.mDataList.clear();
        }
        this.mDataList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_scenario_activity_serch_result, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        GlideUtil.loadImg(Utils.isUrl(mDataList.get(position).imageUrl), holder.ivScenarioActivitySearchResultItemProfile);
        holder.tvScenarioActivitySearchResultItemTitle.getPaint().setFakeBoldText(true);
        holder.tvScenarioActivitySearchResultItemTitle.setText(mDataList.get(position).title);
        holder.tvScenarioActivitySearchResultItemInfo.setText(mDataList.get(position).substance);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(KEY_OF_SCENERIO_RESULT_DATA, mDataList.get(position));
                mContext.setResult(RESULT_OK, intent);
                mContext.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_scenarioActivity_searchResultItem_profile)
        ImageView ivScenarioActivitySearchResultItemProfile;
        @BindView(R.id.tv_scenarioActivity_searchResultItem_title)
        TextView tvScenarioActivitySearchResultItemTitle;
        @BindView(R.id.tv_scenarioActivity_searchResultItem_info)
        TextView tvScenarioActivitySearchResultItemInfo;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
