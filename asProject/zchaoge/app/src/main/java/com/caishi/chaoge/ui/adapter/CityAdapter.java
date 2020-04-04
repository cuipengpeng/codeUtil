package com.caishi.chaoge.ui.adapter;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.caishi.chaoge.R;
import com.caishi.chaoge.ui.activity.SelectProvinceActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 */

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {
    private FragmentActivity mContext;
    public List<String> mDataList = new ArrayList<>();
    private String selectedProvince = "";
    public static final String KEY_OF_CITY = "cityKey";

    public CityAdapter(FragmentActivity context) {
        this.mContext = context;
        this.mDataList.clear();
    }

    public void upateData(boolean isRefresh, List<String> data) {
        if (isRefresh) {
            this.mDataList.clear();
        }
        this.mDataList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_city_select, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.cityName.setText(mDataList.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (((SelectProvinceActivity)mContext).mCurrentList){
                    case SelectProvinceActivity.PROVINCE_LIST:
                        selectedProvince = mDataList.get(position);
                        onProvinceItemClickListener.onClick(selectedProvince);
                        upateData(true, ((SelectProvinceActivity)mContext).cityBeanList.provinces.get(position).child);
                        ((SelectProvinceActivity)mContext).mCurrentList = SelectProvinceActivity.CITY_LIST;
                        break;
                    case SelectProvinceActivity.CITY_LIST:
                        Intent intent = new Intent();
                        intent.putExtra(KEY_OF_CITY, selectedProvince+" "+mDataList.get(position));
                        mContext.setResult(mContext.RESULT_OK, intent);
                        mContext.finish();
                        break;
                }
            }
        });
    }

    private OnProvinceItemClickListener onProvinceItemClickListener;
    public interface OnProvinceItemClickListener{
        void onClick(String provinceName);
    }

    public void setOnProvinceItemClickListener(OnProvinceItemClickListener provinceItemClickListener){
        this.onProvinceItemClickListener = provinceItemClickListener;
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_city_name)
        TextView cityName;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
