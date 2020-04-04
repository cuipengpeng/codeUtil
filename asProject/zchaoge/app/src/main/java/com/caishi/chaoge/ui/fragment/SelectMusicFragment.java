package com.caishi.chaoge.ui.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.caishi.chaoge.R;
import com.caishi.chaoge.base.BaseUILocalDataFragment;
import com.caishi.chaoge.bean.MusicBean;
import com.caishi.chaoge.http.HttpRequest;
import com.caishi.chaoge.ui.activity.SelectMusicActivity;
import com.caishi.chaoge.ui.adapter.SelectMusicAdapter;
import com.caishi.chaoge.ui.widget.refreshlayout.AutoLoadMoreRecyclerView;
import com.caishi.chaoge.http.RequestURL;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class SelectMusicFragment extends BaseUILocalDataFragment {

    @BindView(R.id.rv_selectTempletFragment_templetList)
    AutoLoadMoreRecyclerView musicListRecyclerView;
    @BindView(R.id.ll_selectTempletFragment_netwrokError)
    LinearLayout netwrokErrorLinearLayout;
    @BindView(R.id.btn_networkError_refresh)
    Button networkErrorRefreshButon;
    @BindView(R.id.srl_selectTempletFragment_noData)
    SwipeRefreshLayout noDataSwipeRefreshLayout;


    public SelectMusicAdapter selectMusicListAdapter;
    private String mTempletType = "-1";
    private final int PAGE_SIZE = 20;

    @OnClick({R.id.btn_networkError_refresh})
    public void onClickView(View view) {
        switch (view.getId()) {
            case R.id.btn_networkError_refresh:
                getMusicList(true, mTempletType, -1, "UP");
                break;
        }
    }

    public static Fragment newInstance(String templetType){
        SelectMusicFragment selectTempletFragment = new SelectMusicFragment();
        selectTempletFragment.mTempletType = templetType;
        return selectTempletFragment;
    }

    @Override
    protected String getPageTitle() {
        return "";
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.fragment_select_templet;
    }

    @Override
    protected void initPageData() {
        noDataSwipeRefreshLayout.setVisibility(View.GONE);
        netwrokErrorLinearLayout.setVisibility(View.GONE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        musicListRecyclerView.setLayoutManager(linearLayoutManager);
        selectMusicListAdapter = new SelectMusicAdapter(getActivity());
        musicListRecyclerView.setAdapter(selectMusicListAdapter);

        musicListRecyclerView.setOnLoadMoreListener(new AutoLoadMoreRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                long targetTime = selectMusicListAdapter.mDataList.get(selectMusicListAdapter.mDataList.size() - 1).targetTime;
                getMusicList(false, mTempletType, targetTime, "UP");
            }
        });
        noDataSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMusicList(true, mTempletType, -1, "UP");
            }
        });

        getMusicList(true, mTempletType, -1, "UP");
    }


    /**
     * 获取音乐分类列表
     * @param refresh
     * @param musicType
     * @param startTime
     * @param direction
     */
    public void getMusicList(final boolean refresh, String musicType, long startTime, String direction) {
        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("pageSize", PAGE_SIZE + "");
        paramsMap.put("since", startTime + "");
        paramsMap.put("slipType", direction);
        paramsMap.put("op", "0"); //0为最火  1 为最新
        paramsMap.put("audioType", musicType);

        HttpRequest.post(false, HttpRequest.APP_INTERFACE_WEB_URL + RequestURL.GET_MUSIC, paramsMap, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onSuccess(String response) {
                refreshComplete();
                if (refresh) {
                    ((SelectMusicActivity)getActivity()).mediaPlayerManager.pause();
                    musicListRecyclerView.smoothScrollToPosition(0);
                    musicListRecyclerView.reset();
                }

                Gson gson = new Gson();
                List<MusicBean> musicBeanList = gson.fromJson(response, new TypeToken<List<MusicBean>>(){}.getType());
                if (refresh && (musicBeanList == null || musicBeanList.size() <= 0)) {
                    noDataSwipeRefreshLayout.setVisibility(View.VISIBLE);
                    netwrokErrorLinearLayout.setVisibility(View.GONE);
                    musicListRecyclerView.setVisibility(View.GONE);
                } else {
                    musicListRecyclerView.setVisibility(View.VISIBLE);
                    noDataSwipeRefreshLayout.setVisibility(View.GONE);
                    netwrokErrorLinearLayout.setVisibility(View.GONE);
                    if (musicBeanList.size() > 0) {
                        selectMusicListAdapter.upateData(refresh, musicBeanList);
                    } else {
                        musicListRecyclerView.noMoreLoading();
                    }
                }
            }

            @Override
            public void onFailure(String t) {
                ((SelectMusicActivity)getActivity()).mediaPlayerManager.pause();
                refreshComplete();
                netwrokErrorLinearLayout.setVisibility(View.VISIBLE);
                noDataSwipeRefreshLayout.setVisibility(View.GONE);
                musicListRecyclerView.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 隐藏加载更多的动画
     */
    private void refreshComplete() {
        if (noDataSwipeRefreshLayout.isRefreshing()) {
            noDataSwipeRefreshLayout.setRefreshing(false);//取消刷新
        }
        musicListRecyclerView.loadMoreComplete();
    }

}