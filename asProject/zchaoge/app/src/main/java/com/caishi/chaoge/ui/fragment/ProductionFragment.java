package com.caishi.chaoge.ui.fragment;


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
import com.caishi.chaoge.ui.adapter.ProductionAdapter;
import com.caishi.chaoge.ui.widget.dialog.DialogUtil;
import com.caishi.chaoge.ui.widget.dialog.IDialog;
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

public class ProductionFragment extends BaseUILocalDataFragment {
    @BindView(R.id.ll_data_null)
    LinearLayout ll_data_null;
    @BindView(R.id.rv_production_list)
    RecyclerView rv_production_list;

    int pageSize = 10;
    long since = -1;
    final String UP = "UP";
    final String DOWN = "DOWN";
    public ProductionAdapter productionAdapter;
    public List<HomeDataBean> homeDataBeanList = new ArrayList<>();
    public String userID;
    OnDeleteProductionListener onDeleteProductionListener;

    public static ProductionFragment newInstance(String userId) {
        ProductionFragment productionFragment = new ProductionFragment();
        productionFragment.userID = userId;
        return productionFragment;
    }

    @Override
    protected String getPageTitle() {
        return null;
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.fragment_production;
    }

    @Override
    protected void initPageData() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3);
        rv_production_list.setLayoutManager(gridLayoutManager);
        productionAdapter = new ProductionAdapter();
        rv_production_list.setAdapter(productionAdapter);
        getData(true, -1, "DOWN");
        productionAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                ArrayList<HomeDataBean> resultsArrayList = (ArrayList<HomeDataBean>) productionAdapter.getData();
                getData(false, resultsArrayList.get(resultsArrayList.size() - 1).targetTime, UP);
            }
        }, rv_production_list);

        productionAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, final int position) {
                if (Utils.isFastClick())
                    return;
                final ArrayList<HomeDataBean> resultsArrayList = (ArrayList<HomeDataBean>) productionAdapter.getData();
                if (StringUtil.notEmpty(resultsArrayList.get(position).videoUrl)) {
                    VideoActivity.open(getActivity(), VideoActivity.FROM_PRODUCT_FRAGMENT, position, userID, productionAdapter.getData());
                }else{
                    if (userID.equals(SPUtils.readCurrentLoginUserInfo(getActivity()).userId)) {
                        DialogUtil.createDefaultDialog(getActivity(), "提示", "该作品不存在或已删除", "删除作品", new IDialog.OnClickListener() {
                            @Override
                            public void onClick(IDialog dialog) {
                                Map<String, String> paramsMap = new HashMap<String, String>();
                                paramsMap.put("momentId", resultsArrayList.get(position).momentId);

                                HttpRequest.post(false , HttpRequest.APP_INTERFACE_WEB_URL + RequestURL.DELETE, paramsMap, new HttpRequest.HttpResponseCallBank() {
                                    @Override
                                    public void onSuccess(String response) {
                                    }

                                    @Override
                                    public void onFailure(String t) {
                                    }
                                });

                                dialog.dismiss();
                                if (onDeleteProductionListener != null){
                                    onDeleteProductionListener.deleteProduction();
                                }
                                resultsArrayList.remove(position);
                                productionAdapter.notifyDataSetChanged();
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
    }

    public void getData(final boolean refresh, long since, String slipType) {
        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("pageSize", pageSize + "");
        paramsMap.put("since", since + "");
        paramsMap.put("slipType", slipType);
        paramsMap.put("userId", userID);

        HttpRequest.post(true, HttpRequest.APP_INTERFACE_WEB_URL + RequestURL.PERSONAL, paramsMap, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onSuccess(String response) {
                Gson gson = new Gson();
                homeDataBeanList = gson.fromJson(response, new TypeToken<List<HomeDataBean>>(){}.getType());
                productionAdapter.setEnableLoadMore(true);
                if (refresh) {
                    productionAdapter.setNewData(homeDataBeanList);
                } else {
                    productionAdapter.addData(homeDataBeanList);
                }

                if (productionAdapter.getData().size() == 0) {
                    ll_data_null.setVisibility(View.VISIBLE);
                    rv_production_list.setVisibility(View.GONE);
                } else {
                    ll_data_null.setVisibility(View.GONE);
                    rv_production_list.setVisibility(View.VISIBLE);
                }


                if (homeDataBeanList.size() < pageSize) {
                    //第一页如果不够一页就不显示没有更多数据布局
                    productionAdapter.loadMoreEnd(false);
                } else {
                    productionAdapter.loadMoreComplete();
                }
            }

            @Override
            public void onFailure(String t) {
            }
        });
    }


    public void setOnDeleteProduvtionListener(OnDeleteProductionListener onDeleteProductionListener) {
        this.onDeleteProductionListener = onDeleteProductionListener;
    }

    public interface OnDeleteProductionListener {
        void deleteProduction();
    }
}
