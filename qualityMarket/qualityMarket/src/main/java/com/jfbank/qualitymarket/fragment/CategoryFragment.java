package com.jfbank.qualitymarket.fragment;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.activity.MainActivity;
import com.jfbank.qualitymarket.activity.SearchGoodsActivity;
import com.jfbank.qualitymarket.adapter.CategoryLevel1Adapter;
import com.jfbank.qualitymarket.adapter.CategoryLevel2Adapter;
import com.jfbank.qualitymarket.base.BaseFragment;
import com.jfbank.qualitymarket.bean.CategoryLevel1Bean;
import com.jfbank.qualitymarket.bean.CategoryLevel2Bean;
import com.jfbank.qualitymarket.config.CacheKeyConfig;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.helper.DiskLruCacheHelper;
import com.jfbank.qualitymarket.util.LogUtil;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;

import java.util.HashMap;
import java.util.Map;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


/**
 * 分类tab页面
 *
 * @author 崔朋朋
 */
public class CategoryFragment extends BaseFragment {
    public static final String TAG = CategoryFragment.class.getName();

    @InjectView(R.id.iv_category_back)
    ImageView iv_category_back;
    @InjectView(R.id.ll_title)
    android.widget.LinearLayout rlTitle;

    @InjectView(R.id.tv_search)
    TextView tvSearch;

    @InjectView(R.id.rl_category_empty)
    RelativeLayout rl_category_empty;
    /**
     * 品类原生部分
     */
//	品类Banner
    @InjectView(R.id.iv_category_banner)
    ImageView banner_category;

    //	左侧品类列表
    @InjectView(R.id.lv_category_title)
    ListView categoryLevel1ListView;
    //	右侧品类列表
    @InjectView(R.id.lv_category_desc)
    ListView categoryLevel2ListView;
    // 左侧品类适配器
    private CategoryLevel1Adapter categoryLevel1Adapter = null;
    private CategoryLevel2Adapter categoryLevel2Adapter = null;
    //	左侧品类数据源
    private List<CategoryLevel1Bean> categoryLevel1List = new ArrayList<CategoryLevel1Bean>();
    //	右侧品类数据源
    private List<CategoryLevel2Bean> categoryLevel2List = new ArrayList<CategoryLevel2Bean>();

    private int selectIndex = 0;
    private int selectedPositionInRightPosition = 0;
    private String defaultUpCategoryType = null;
    private int fromType = 0;

    @OnClick({R.id.tv_search, R.id.iv_category_banner, R.id.iv_category_back})
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.tv_search:
                startActivity(new Intent(getActivity(), SearchGoodsActivity.class));
                break;
            case R.id.iv_category_back:
                getActivity().finish();
                break;
            case R.id.iv_category_banner:
                LogUtil.printLog("AppPage=" + categoryLevel1List.get(selectIndex).getAppPage() + "--AppParams=" + categoryLevel1List.get(selectIndex).getAppParams());
                CommonUtils.startIntent(getContext(), categoryLevel1List.get(selectIndex).getAppPage(), categoryLevel1List.get(selectIndex).getAppParams());
                break;
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!MainActivity.isNetworkAvailable(getActivity())) {
            Toast.makeText(getActivity(), ConstantsUtil.H5_PAGE_FAIL_TO_CONNECT_SERVER, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        ButterKnife.inject(this, view);

        categoryLevel1Adapter = new CategoryLevel1Adapter(categoryLevel1List, getActivity(), 0);
        categoryLevel1ListView.setAdapter(categoryLevel1Adapter);
        categoryLevel2Adapter = new CategoryLevel2Adapter(categoryLevel2List, getActivity());
        categoryLevel2ListView.setAdapter(categoryLevel2Adapter);
        CommonUtils.setStatusBarTitle(getActivity(), rlTitle);

        hideAllCategoryDataView();

        Bundle args = getArguments();
        if (args != null) {
            defaultUpCategoryType = getArguments().getString("upCategoroyType", "");
            fromType = getArguments().getInt("fromType", 0);
            if (fromType == 2) {
                Log.e("type=", fromType + "");
                iv_category_back.setVisibility(View.VISIBLE);
            }
        }

        //					点击切换到对应的品类显示目录
        categoryLevel1ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                selectIndex = position;//用于右侧的banner的点击
                updateCategoryLevel2Data(position);
            }
        });

        categoryLevel2ListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    selectedPositionInRightPosition = view.getVerticalScrollbarPosition();
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        categoryLevel1ListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        categoryLevel1List = DiskLruCacheHelper.getAsSerializableList(CacheKeyConfig.CACHE_CATEGORYFRAGMENT, CategoryLevel1Bean[].class);
        if (!CommonUtils.isEmptyList(categoryLevel1List)) {
            updateData();
        }
        getCategoryData();
        return view;
    }

    /**
     * 隐藏所有的分类数据的view
     */
    private void hideAllCategoryDataView() {
        categoryLevel1ListView.setVisibility(View.GONE);
        categoryLevel2ListView.setVisibility(View.GONE);
        banner_category.setVisibility(View.GONE);
        rl_category_empty.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        categoryLevel2ListView.setVerticalScrollbarPosition(selectedPositionInRightPosition);
//        categoryLevel2ListView.setSelection(selectedPositionInRightPosition);
    }

    @Override
    public String getPageName() {
        return getString(R.string.str_pagename_category);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getCategoryData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ConstantsUtil.PERMISSION_WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission Granted
            } else {
                // Permission Denied
            }
        }
    }

    /**
     * 获取品类页面数据
     */
    private void getCategoryData() {
        Map<String, String> params = new HashMap<>();
        params.put("ver", AppContext.getAppVersionName(AppContext.mContext));
        params.put("Plat", ConstantsUtil.PLAT);
        HttpRequest.post(mContext, HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.GET_CATEGORY_URL, params, new AsyncResponseCallBack() {
            @Override
            public void onResult(String bytes) {
                String resultSuccess = new String(bytes);
                LogUtil.printLog("获取分类页面数据：" + resultSuccess);

                JSONObject jsonObject = JSON.parseObject(resultSuccess);


                if (ConstantsUtil.RESPONSE_SUCCEED == jsonObject
                        .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                    categoryLevel1List = JSONObject.parseArray(jsonObject.getString("data"), CategoryLevel1Bean.class);
                    updateData();
                } else {
                    Toast.makeText(getActivity(), jsonObject.getString("statusDetail"), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed(String path, String msg) {
                Toast.makeText(getActivity(), "网络连接失败，请检查网络设置！", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 更新品类数据
     */
    private void updateData() {
        if (fromType == 0x2) {//来自CategoryActivity
            CategoryLevel1Bean categoryLevel1Bean = new CategoryLevel1Bean();
            categoryLevel1Bean.setUpCategoroyType(defaultUpCategoryType);
            int index = categoryLevel1List.indexOf(categoryLevel1Bean);
            if (index != -1) {//获取当前对象在list中的位置
                selectIndex = index;
            }
        }
        DiskLruCacheHelper.put(CacheKeyConfig.CACHE_CATEGORYFRAGMENT, categoryLevel1List);//设置缓存
        if (categoryLevel1List.size() > 0) {
            categoryLevel1ListView.setVisibility(View.VISIBLE);
            updateCategoryLevel2Data(selectIndex);
        } else {
            hideAllCategoryDataView();
        }
    }

    /**
     * 更新右侧的列表
     *
     * @param position
     */
    private void updateCategoryLevel2Data(int position) {
        categoryLevel1Adapter.updateData(categoryLevel1List, position);

        if (categoryLevel1List.get(position).getPicUrl() == null) {
            banner_category.setVisibility(View.GONE);
        } else {
            banner_category.setVisibility(View.VISIBLE);
            CommonUtils.loadCacheImage(mContext, banner_category, categoryLevel1List.get(position).getPicUrl(), R.drawable.bigbg_mor);
        }

        if (categoryLevel1List.get(position).getLabelList() != null) {
            categoryLevel2Adapter.updateData(categoryLevel1List.get(position).getUpCategoroyType(), categoryLevel1List.get(position).getLabelList(), position);
            rl_category_empty.setVisibility(View.GONE);
            categoryLevel2ListView.setVisibility(View.VISIBLE);
        } else {
            categoryLevel2ListView.setVisibility(View.GONE);
            rl_category_empty.setVisibility(View.VISIBLE);
        }
    }

}
