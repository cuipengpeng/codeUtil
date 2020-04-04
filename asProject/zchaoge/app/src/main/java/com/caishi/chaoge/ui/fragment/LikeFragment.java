package com.caishi.chaoge.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.caishi.chaoge.R;
import com.caishi.chaoge.base.BaseUILocalDataFragment;
import com.caishi.chaoge.bean.HomeDataBean;
import com.caishi.chaoge.http.HttpRequest;
import com.caishi.chaoge.http.RequestURL;
import com.caishi.chaoge.ui.activity.VideoActivity;
import com.caishi.chaoge.ui.adapter.LikeAdapter;
import com.caishi.chaoge.ui.widget.dialog.DialogUtil;
import com.caishi.chaoge.ui.widget.dialog.IDialog;
import com.caishi.chaoge.http.RequestURL;
import com.caishi.chaoge.utils.SPUtils;
import com.caishi.chaoge.utils.StringUtil;
import com.caishi.chaoge.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class LikeFragment extends BaseUILocalDataFragment {

    @BindView(R.id.ll_data_null)
    LinearLayout ll_data_null;
    @BindView(R.id.rv_like_list)
    RecyclerView rv_like_list;

    int pageSize = 10;
    long since = -1;
    final String UP = "UP";
    final String DOWN = "DOWN";
    public LikeAdapter likeAdapter;
    public List<HomeDataBean> homeDataBeanList = new ArrayList<>();
    public String userID;
    private OnDisLikeListener onDisLikeListener;

    public static LikeFragment newInstance(String userId) {
        LikeFragment likeFragment = new LikeFragment();
        likeFragment.userID = userId;
        return likeFragment;
    }

    @Override
    protected String getPageTitle() {
        return null;
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.fragment_like;
    }

    @Override
    protected void initPageData() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3);
        rv_like_list.setLayoutManager(gridLayoutManager);
        likeAdapter = new LikeAdapter();
        rv_like_list.setAdapter(likeAdapter);
        getData(true, -1, "DOWN");
        likeAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, final int position) {
                if (Utils.isFastClick())
                    return;
                final ArrayList<HomeDataBean> resultsArrayList = (ArrayList<HomeDataBean>) likeAdapter.getData();
                if (StringUtil.notEmpty(resultsArrayList.get(position).videoUrl)) {
                    VideoActivity.open(getActivity(), VideoActivity.FROM_LIKE_FRAGMENT, position, userID, likeAdapter.getData());
                }else{
                    if (userID.equals(SPUtils.readCurrentLoginUserInfo(getActivity()).userId)) {
                        DialogUtil.createDefaultDialog(getActivity(), "提示", "该作品不存在或已删除", "取消收藏", new IDialog.OnClickListener() {
                            @Override
                            public void onClick(IDialog dialog) {
                                Map<String, String> paramsMap = new HashMap<String, String>();
                                paramsMap.put("userId", SPUtils.readCurrentLoginUserInfo(mContext).userId);
                                paramsMap.put("momentId", resultsArrayList.get(position).momentId);
                                paramsMap.put("desUserId", "");
                                paramsMap.put("likeStatus", "1");

                                HttpRequest.post(false , HttpRequest.APP_INTERFACE_WEB_URL + RequestURL.LIKE, paramsMap, new HttpRequest.HttpResponseCallBank() {
                                    @Override
                                    public void onSuccess(String response) {
                                    }

                                    @Override
                                    public void onFailure(String t) {
                                    }
                                });

                                dialog.dismiss();
                                if (onDisLikeListener != null){
                                    onDisLikeListener.disLike();
                                }
                                resultsArrayList.remove(position);
                                likeAdapter.notifyDataSetChanged();
                            }
                        }, "知道了", new IDialog.OnClickListener() {
                            @Override
                            public void onClick(IDialog dialog) {
                                dialog.dismiss();
                            }
                        });

                    } else {
                        Toast.makeText(getActivity(), "视频不存在", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        likeAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                ArrayList<HomeDataBean> resultsArrayList = (ArrayList<HomeDataBean>) likeAdapter.getData();
                getData(false, resultsArrayList.get(resultsArrayList.size() - 1).targetTime, UP);
            }
        }, rv_like_list);
    }

    public void getData(final boolean refresh, long since, String slipType) {
        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("pageSize", pageSize + "");
        paramsMap.put("since", since + "");
        paramsMap.put("slipType", slipType);
        paramsMap.put("userId", userID);

        HttpRequest.post(true, HttpRequest.APP_INTERFACE_WEB_URL + RequestURL.LIKES, paramsMap, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onSuccess(String response) {
                Gson gson = new Gson();
                homeDataBeanList = gson.fromJson(response, new TypeToken<List<HomeDataBean>>(){}.getType());
                likeAdapter.setEnableLoadMore(true);
                if (refresh) {
                    likeAdapter.setNewData(homeDataBeanList);
                } else {
                    likeAdapter.addData(homeDataBeanList);
                }

                if (likeAdapter.getData().size() == 0) {
                    ll_data_null.setVisibility(View.VISIBLE);
                    rv_like_list.setVisibility(View.GONE);
                }else {
                    ll_data_null.setVisibility(View.GONE);
                    rv_like_list.setVisibility(View.VISIBLE);
                }

                if (homeDataBeanList.size() < pageSize) {
                    //第一页如果不够一页就不显示没有更多数据布局
                    likeAdapter.loadMoreEnd(false);
                } else {
                    likeAdapter.loadMoreComplete();
                }

            }

            @Override
            public void onFailure(String t) {
            }
        });
    }

    public void setOnDisLikeListener(OnDisLikeListener onDisLikeListener) {
        this.onDisLikeListener = onDisLikeListener;
    }

    public interface OnDisLikeListener {
        void disLike();
    }
}
