package com.caishi.chaoge.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.caishi.chaoge.R;
import com.caishi.chaoge.base.BaseUILocalDataFragment;
import com.caishi.chaoge.bean.ScenarioBean;
import com.caishi.chaoge.http.HttpRequest;
import com.caishi.chaoge.http.Product;
import com.caishi.chaoge.ui.activity.ScenarioActivity;
import com.caishi.chaoge.utils.CustomLoadMoreView;
import com.caishi.chaoge.http.RequestURL;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

import static android.app.Activity.RESULT_OK;

public class ScenarioFragment extends BaseUILocalDataFragment {
    @BindView(R.id.rv_scenarioFrag_list)
    RecyclerView rv_scenarioFrag_list;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    ArrayList<ScenarioBean> scenarioList = new ArrayList<>();
    private ScenarioListAdapter scenarioListAdapter;
    final String UP = "UP";
    final String DOWN = "DOWN";
    int pageSize = 10;
    long since = -1;
    boolean isAddData = false;
    int op = 0;
    String scriptType = "1";//区分具体的剧本分类
    public static final String KEY_OF_SCENARIO_CONTENT = "scenarioData";

    public static ScenarioFragment newInstance(int op, String scriptType) {
        ScenarioFragment fragment = new ScenarioFragment();
        Bundle args = new Bundle();
        args.putInt("op", op);
        args.putString("scriptType", scriptType);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected String getPageTitle() {
        return null;
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.fragment_scenario;
    }

    @Override
    protected void initPageData() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            op = arguments.getInt("op", 0);
            scriptType = arguments.getString("scriptType", "1");
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_scenarioFrag_list.setLayoutManager(linearLayoutManager);
        scenarioListAdapter = new ScenarioListAdapter();
        scenarioListAdapter.setLoadMoreView(new CustomLoadMoreView());
        rv_scenarioFrag_list.setAdapter(scenarioListAdapter);
        if (scenarioListAdapter.getData().size() == 0) {
            getData(-1, DOWN);
        }
        scenarioListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                isAddData = true;
                getData(scenarioList.get(scenarioList.size() - 1).targetTime, UP);
            }
        }, rv_scenarioFrag_list);

        scenarioListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent();
                intent.putExtra(ScenarioActivity.KEY_OF_SCENERIO_RESULT_DATA, scenarioList.get(position));
                mContext.setResult(RESULT_OK, intent);
                mContext.finish();
            }
        });
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                scenarioList.clear();
                isAddData = false;
                getData(-1, DOWN);
            }
        });
    }


    public void getData(long since, String slipType) {
        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("pageSize", pageSize + "");
        paramsMap.put("since", since + "");
        paramsMap.put("slipType", slipType + "");
        paramsMap.put("op", op + "");
        paramsMap.put("scriptType", scriptType);

        HttpRequest.post(false, HttpRequest.APP_INTERFACE_WEB_URL + RequestURL.GET_SCRIPT, paramsMap, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onSuccess(String response) {
                Gson gson = new Gson();
                List<ScenarioBean> scenarioBeanList = gson.fromJson(response, new TypeToken<List<ScenarioBean>>() {
                }.getType());
                refreshLayout.finishRefresh();
                scenarioListAdapter.setEnableLoadMore(true);
                scenarioList.addAll(scenarioBeanList);
                if (!isAddData) {
                    scenarioListAdapter.setNewData(scenarioBeanList);
                } else {
                    if (scenarioBeanList.size() > 0) {
                        scenarioListAdapter.addData(scenarioBeanList);
                    }
                }
                if (scenarioBeanList.size() < pageSize) {
                    //第一页如果不够一页就不显示没有更多数据布局
                    scenarioListAdapter.loadMoreEnd(false);
                } else {
                    scenarioListAdapter.loadMoreComplete();
                }

            }

            @Override
            public void onFailure(String t) {
                scenarioListAdapter.loadMoreEnd();
                refreshLayout.finishRefresh();//传入false表示刷新失败
            }
        });
    }

    /**
     * 剧本的adapter
     */
    class ScenarioListAdapter extends BaseQuickAdapter<ScenarioBean, BaseViewHolder> {
        public ScenarioListAdapter() {
            super(R.layout.item_scenario_list);
        }

        @Override
        protected void convert(BaseViewHolder helper, ScenarioBean item) {
            helper.setText(R.id.tv_scenario_title, "《" + item.title + "》");
            helper.setText(R.id.tv_scenario_info, item.substance);
        }
    }


}
