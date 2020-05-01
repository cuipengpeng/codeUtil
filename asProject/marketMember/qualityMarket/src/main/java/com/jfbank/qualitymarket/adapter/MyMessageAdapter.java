package com.jfbank.qualitymarket.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.model.MessageBean;
import com.jfbank.qualitymarket.mvp.MyMessageMVP;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.TimeUtils;
import com.jfbank.qualitymarket.widget.ForegroundLinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 功能：我的消息适配器<br>
 * 作者：赵海
 * 时间： 2016/12/5 0005<br>.
 * 版本：1.2.0
 */

public class MyMessageAdapter extends RecyclerSwipeAdapter<MyMessageAdapter.ViewHolder> {
    Context mContext;
    MyMessageMVP.Presenter mPresenter;

    public List<MessageBean> getData() {
        return data;
    }

    List<MessageBean> data = new ArrayList<>();

    /**
     * 刷新界面数据
     *
     * @param isRefresh
     * @param list
     */
    public void updateData(boolean isRefresh, List<MessageBean> list) {
        if (!CommonUtils.isEmptyList(list)) {
            if (isRefresh) {
                data.clear();
            }
            data.addAll(list);
            notifyDataSetChanged();
        }
    }

    public MyMessageAdapter(Context context, MyMessageMVP.Presenter mPresenter) {
        this.mContext = context;
        this.mPresenter = mPresenter;
    }


    @Override
    public int getItemCount() {
        return CommonUtils.isEmptyList(data) ? 0 : data.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_mymessage, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        String title = data.get(position).getTitle();
        if (!TextUtils.isEmpty(title) && title.length() > 10) {
            title = title.substring(0, 10) + "...";
        }
        viewHolder.tvMsgTitle.setText(title);
        viewHolder.tvMsgContent.setText(data.get(position).getContent());
        viewHolder.tvMsgTime.setText(TimeUtils.formatWithString(data.get(position).getCreateTime(), TimeUtils.HOUR_TIME_FORMAT));
        if (TextUtils.equals(data.get(position).getReadStatus(), "1")) {
            viewHolder.ivNoreadFlag.setVisibility(View.GONE);
        } else {
            viewHolder.ivNoreadFlag.setVisibility(View.VISIBLE);
        }
        viewHolder.flDeleteMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//点击删除
                viewHolder.swipe.close(true);
                mPresenter.deleteMsg(position, getData().get(position));
            }
        });
        viewHolder.flItemMmsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//点击整条
                mPresenter.updateMsgStatus(position, getData().get(position));
            }
        });
        mItemManger.bindView(viewHolder.itemView, position);
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.swipe)
        SwipeLayout swipe;
        @InjectView(R.id.fl_item_msg)
        ForegroundLinearLayout flItemMmsg;
        @InjectView(R.id.fl_delete_msg)
        ForegroundLinearLayout flDeleteMsg;
        @InjectView(R.id.tv_msg_title)
        TextView tvMsgTitle;
        @InjectView(R.id.tv_msg_time)
        TextView tvMsgTime;
        @InjectView(R.id.tv_msg_content)
        TextView tvMsgContent;
        @InjectView(R.id.iv_noread_flag)
        ImageView ivNoreadFlag;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}
