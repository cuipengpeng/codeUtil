package com.caishi.chaoge.ui.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.caishi.chaoge.R;
import com.caishi.chaoge.bean.FollowBean;
import com.caishi.chaoge.bean.MyFansBean;
import com.caishi.chaoge.http.HttpRequest;
import com.caishi.chaoge.http.RequestURL;
import com.caishi.chaoge.ui.activity.UserDetailsActivity;
import com.caishi.chaoge.ui.widget.CircleImageView;
import com.caishi.chaoge.utils.Utils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 */

public class MyFollowsAdapter extends RecyclerView.Adapter<MyFollowsAdapter.ViewHolder> {
    public static final String TAG = MyFollowsAdapter.class.getName();
    FragmentActivity mContext;
   public List<MyFansBean> mDataList = new ArrayList<>();

    public MyFollowsAdapter(FragmentActivity context) {
        this.mContext = context;
        this.mDataList.clear();
    }

    public void upateData(boolean isRefresh, List<MyFansBean> data) {
        if (isRefresh) {
            this.mDataList.clear();
        }
        this.mDataList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_fans, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Glide.with(mContext).load(Utils.isUrl((String)mDataList.get(position).getAvatar())).into(holder.ivMyFansActivityItemProfile);
        holder.tvMyFansActivityItemUsername.setText(mDataList.get(position).getNickname());
        holder.tvMyFansActivityItemSignature.setText(mDataList.get(position).getRemark());
        if (mDataList.get(position).getFollowStatus() == 1) {//0 取消关注 1互相关注
            holder.btnMyFansActivityItemFollow.setText("互相关注");
        } else {
            holder.btnMyFansActivityItemFollow.setText("已关注");
        }

        final MyFansBean resultBean = mDataList.get(position);
        resultBean.setHasFollow(1); //只在关注列表使用 // 1 关注 0 取消关注
        holder.btnMyFansActivityItemFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> paramsMap = new HashMap<String, String>();
                paramsMap.put("userId",  mDataList.get(position).getUserId());
                if (resultBean.getHasFollow() == 1) {
                    paramsMap.put("status", "0");//0 取消关注 1关注
                } else {
                    paramsMap.put("status", "1");//0 取消关注 1关注
                }

                HttpRequest.post(false, HttpRequest.APP_INTERFACE_WEB_URL + RequestURL.FOLLOW, paramsMap, new HttpRequest.HttpResponseCallBank() {
                    @Override
                    public void onSuccess(String response) {
                        if (resultBean.getHasFollow() == 1) {
                            resultBean.setHasFollow(0);
                            holder.btnMyFansActivityItemFollow.setText("关注");
                        } else {
                            resultBean.setHasFollow(1);

                            Gson gson = new Gson();
                            FollowBean followBean = gson.fromJson(response, FollowBean.class);
                            if(followBean.getAttached() != null && followBean.getAttached().isIsFriend()){
                                holder.btnMyFansActivityItemFollow.setText("互相关注");
                            }else {
                                holder.btnMyFansActivityItemFollow.setText("已关注");
                            }
                        }
                    }

                    @Override
                    public void onFailure(String t) {
                    }
                });
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDetailsActivity.open(mContext, mDataList.get(position).getUserId());
                mContext.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_myFansActivity_item_profile)
        CircleImageView ivMyFansActivityItemProfile;
        @BindView(R.id.tv_myFansActivity_item_username)
        TextView tvMyFansActivityItemUsername;
        @BindView(R.id.tv_myFansActivity_item_signature)
        TextView tvMyFansActivityItemSignature;
        @BindView(R.id.btn_myFansActivity_item_follow)
        Button btnMyFansActivityItemFollow;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
