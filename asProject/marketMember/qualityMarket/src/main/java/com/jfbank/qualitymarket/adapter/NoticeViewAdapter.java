package com.jfbank.qualitymarket.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.model.QualityNewsBean;
import com.jfbank.qualitymarket.model.QualityNewsBean;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.widget.NoticeAdverView;

import java.util.List;

/**
 * Created by Administrator on 2016/3/20.
 * 京东广告栏数据适配器
 */

public class NoticeViewAdapter {
    private List<QualityNewsBean> mDatas;
    Context mContext;

    public NoticeViewAdapter(Context context, List<QualityNewsBean> mDatas) {
        this.mDatas = mDatas;
        this.mContext = context;
    }

    /**
     * 获取数据的条数
     *
     * @return
     */
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    /**
     * 获取摸个数据
     *
     * @param position
     * @return
     */
    public QualityNewsBean getItem(int position) {
        return mDatas.get(position);
    }

    /**
     * 获取条目布局
     *
     * @param parent
     * @return
     */
    public View getView(NoticeAdverView parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notice, null);
    }

    /**
     * 条目数据适配
     *
     * @param view
     * @param data
     */
    public void setItem(final View view, final QualityNewsBean data) {
        TextView tv = (TextView) view.findViewById(R.id.tv_notice);
        tv.setText(data.getTitle());
        //你可以增加点击事件
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.startWebViewActivity(mContext, data.getUrl(), false, false);
            }
        });
    }
}
