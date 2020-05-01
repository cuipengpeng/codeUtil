package com.jfbank.qualitymarket.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.adapter.SearchGoodsHistoryAdapter;
import com.jfbank.qualitymarket.base.BaseActivity;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
import com.jfbank.qualitymarket.fragment.MyAccountFragment;
import com.jfbank.qualitymarket.js.JsRequstInterface;
import com.jfbank.qualitymarket.model.Product;
import com.jfbank.qualitymarket.model.SearchTextBean;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.util.ActivityManager;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.LogUtil;
import com.jfbank.qualitymarket.util.StringUtil;
import com.jfbank.qualitymarket.util.TDUtils;
import com.jfbank.qualitymarket.widget.LoadingAlertDialog;
import com.jfbank.qualitymarket.widget.NoScrollGridView;
import com.jfbank.qualitymarket.widget.TwinklingRefreshLayoutView;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


/**
 * 商品搜索类
 *
 * @author 崔朋朋
 */
public class SearchGoodsActivity extends BaseActivity {
    @InjectView(R.id.tv_searchGoodsActivity_back)
    TextView backTextView;
    @InjectView(R.id.tv_searchGoodsActivity_search)
    TextView searchTextView;
    @InjectView(R.id.et_searchGoodsActivity_searchText)
    EditText searchTextEditText;
    //显示热门搜索和搜索历史
    @InjectView(R.id.ll_searchGoodsActivity_hotSearchAndsearchHistory)
    LinearLayout hotSearchAndsearchHistoryLinearLayout;
    @InjectView(R.id.v_searchGoodsActivity_borderSearchRecommend)
    View borderSearchRecommendView;

    @InjectView(R.id.ll_searchGoodsActivity_searchHistory)
    LinearLayout searchHistoryLinearLayout;
    @InjectView(R.id.gv_searchGoodsActivity_hotSearch)
    NoScrollGridView hotSearchGridView;

    @InjectView(R.id.lv_searchGoodsActivity_searchHistory)
    ListView searchHistoryListView;
    @InjectView(R.id.refreshLayout)
    TwinklingRefreshLayoutView refreshLayout;
    //搜索推荐
    @InjectView(R.id.sv_searchGoodsActivity_hotSearchAndsearchHistory)
    ScrollView sv_hotSearchAndsearchHistoryScrollView;
    @InjectView(R.id.lv_searchGoodsActivity_searchRecommend)
    ListView searchRecommendListView;

    private HotSearchGridViewAdapter hotSearchGridViewAdapter;
    private SearchGoodsHistoryAdapter searchGoodsHistoryAdapter;
    private SearchGoodsHistoryAdapter searchRecommendAdapter;

    private List<String> hotSearchList = new ArrayList<String>();
    private List<String> searchHistoryList = new ArrayList<String>();
    private List<String> searchRecommendList = new ArrayList<String>();
    private String keyOfSearchHistory = "searchHistoryLocalKey";
    private final String SEPERATOR_OF_SEARCH_HISTORY = ",";

    @InjectView(R.id.tv_searchGoodsActivity_sortByGeneral)
    TextView sortByGeneralTextView;
    @InjectView(R.id.iv_searchGoodsActivity_sortByGeneral)
    ImageView sortByGeneralImageView;
    @InjectView(R.id.tv_searchGoodsActivity_sortBySaleAmount)
    TextView sortBySaleAmountTextView;
    @InjectView(R.id.iv_searchGoodsActivity_sortBySaleAmount)
    ImageView sortBySaleAmountImageView;
    @InjectView(R.id.tv_searchGoodsActivity_sortByPrice)
    TextView sortByPriceTextView;
    @InjectView(R.id.iv_searchGoodsActivity_sortByPrice)
    ImageView sortByPriceImageView;
    @InjectView(R.id.rl_searchGoodsActivity_sortByGeneral)
    RelativeLayout sortByGeneralRelativeLayout;
    @InjectView(R.id.rl_searchGoodsActivity_sortBySaleAmount)
    RelativeLayout sortBySaleAmountRelativeLayout;
    @InjectView(R.id.rl_searchGoodsActivity_sortByPrice)
    RelativeLayout sortByPriceRelativeLayout;

    //商品列表
    @InjectView(R.id.fl_list_content)
    FrameLayout flListContent;
    //搜索无数据显示
    @InjectView(R.id.rl_searchGoodsActivity_noProductData)
    RelativeLayout noProductDataRelativeLayout;

    //索索商品列表表头
    @InjectView(R.id.ll_searchGoodsActivity_sort)
    LinearLayout sortLinearLayout;
    @InjectView(R.id.btn_home)            //去首页
            Button btn_home;
    @InjectView(R.id.rl_title)
    RelativeLayout rlTitle;
    private String searchText = "";
    private String categoryLevel1Id = "";
    private boolean searchCategoryProduct = false;
    private boolean comeFromCategory = false;

    private List<Product> allProductList = new ArrayList<Product>();
    private MyOrderAdapter productAdapter;
    private String orderStatus;
    private Drawable descDrawable;
    private Drawable generalDrawable;
    private int defaultColor;
    private int selectedColor;
    private int pageNo = 1;
    /**
     * list
     */
    private ListView productListView;
    /**
     * 自定义一个布局
     */
    public static final String KEY_OF_ORDER_ID = "orderIdKey";
    private final int SORT_BY_GENERAL = 0;  //综合搜索
    private final int SORT_BY_SALE_AMOUNT = 1;  //按销量搜索
    private final int SORT_BY_PRICE = 2;  //按价格搜索
    private int SORT_BY_PRICE_ASC_DESC = 1; // 1降序、其他升序，只适用于价格排序
    private int selectedSortBy = 0;  //当前选中的搜索方式
    private SearchTextBean searchTextBean = new SearchTextBean();
    private boolean isSearchingGoods = false;
    /**
     * 网编请求时加载框
     */
    private LoadingAlertDialog mDialog;

    @OnClick({R.id.tv_searchGoodsActivity_search, R.id.tv_searchGoodsActivity_back,
            R.id.rl_searchGoodsActivity_sortByGeneral, R.id.rl_searchGoodsActivity_sortBySaleAmount,
            R.id.rl_searchGoodsActivity_sortByPrice, R.id.btn_home, R.id.et_searchGoodsActivity_searchText,
            R.id.ll_searchGoodsActivity_clearSearchHistory})
    public void OnViewClick(View v) {
        descDrawable.setBounds(0, 0, descDrawable.getMinimumWidth(), descDrawable.getMinimumHeight());
        generalDrawable.setBounds(0, 0, generalDrawable.getMinimumWidth(), generalDrawable.getMinimumHeight());
        switch (v.getId()) {
            case R.id.tv_searchGoodsActivity_back:
                finish();
                break;
            case R.id.ll_searchGoodsActivity_clearSearchHistory:
                searchHistoryList.clear();
                getSharedPreferences(ConstantsUtil.GLOBAL_CONFIG_FILE_NAME, MODE_PRIVATE).edit()
                        .putString(keyOfSearchHistory, "").commit();

                hideAllView();
                showHotSearchAndRefreshSearchHistory();
                break;
            case R.id.rl_searchGoodsActivity_sortByGeneral:
                selectedSortBy = SORT_BY_GENERAL;
                setDefaultTextView();
                sortByGeneralTextView.setTextColor(selectedColor);
                if (searchCategoryProduct) {
                    pageNo = 1;
                    searchProductCategory(selectedSortBy, true, SORT_BY_PRICE_ASC_DESC,  true);
                } else {
                    // pageNo = 0;
                    pageNo = 1;
                    searchGoods(selectedSortBy, true, SORT_BY_PRICE_ASC_DESC, true);
                }
                break;
            case R.id.rl_searchGoodsActivity_sortBySaleAmount:
                selectedSortBy = SORT_BY_SALE_AMOUNT;
                setDefaultTextView();
                sortBySaleAmountTextView.setTextColor(selectedColor);
                sortBySaleAmountImageView.setBackgroundResource(R.mipmap.ic_goods_sort_down);
                if (searchCategoryProduct) {
                    pageNo = 1;
                    searchProductCategory(selectedSortBy, true, SORT_BY_PRICE_ASC_DESC,  true);
                } else {
                    // pageNo = 0;
                    pageNo = 1;
                    searchGoods(selectedSortBy, true, SORT_BY_PRICE_ASC_DESC, true);
                }
                break;
            case R.id.rl_searchGoodsActivity_sortByPrice:
                selectedSortBy = SORT_BY_PRICE;
                setDefaultTextView();
                sortByPriceTextView.setTextColor(selectedColor);
                if (SORT_BY_PRICE_ASC_DESC == 1) {
                    SORT_BY_PRICE_ASC_DESC = 0;
                    sortByPriceImageView.setImageResource(R.mipmap.ic_goods_sort_up);
                } else {
                    sortByPriceImageView.setImageResource(R.mipmap.ic_goods_sort_down);
                    SORT_BY_PRICE_ASC_DESC = 1;
                }
                if (searchCategoryProduct) {
                    pageNo = 1;
                    searchProductCategory(selectedSortBy, true, SORT_BY_PRICE_ASC_DESC, true);
                } else {
                    // pageNo = 0;
                    pageNo = 1;
                    searchGoods(selectedSortBy, true, SORT_BY_PRICE_ASC_DESC,  true);
                }
                break;
            case R.id.tv_searchGoodsActivity_search://点击搜索
                searchText = searchTextEditText.getText().toString();
                if (searchText == null || "".equals(searchText)) {//内容为空
                    CharSequence hintString = searchTextEditText.getHint();
                    if (TextUtils.isEmpty(hintString)) {//默认hint为空
                        Toast.makeText(this, "输入商品关键字", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        searchText = hintString.toString();
                    }
                } else {
                    String searchTextTrim = searchTextEditText.getText().toString().trim();
                    if (TextUtils.isEmpty(searchTextTrim)) {
                        return;
                    }
                }
                searchStringText(searchText);
                break;
            case R.id.btn_home:            //去首页
                ActivityManager.getInstance().finishActivity(CategoryActivity.class);//如果是品类界面进入该搜索界面，去首页，则关闭该品类界面
                Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);
                finish();
                break;
            case R.id.et_searchGoodsActivity_searchText://shou
                productListView.setVisibility(View.GONE);
                sortLinearLayout.setVisibility(View.GONE);
                //搜索按钮
                searchTextView.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * 添加搜索文字到搜索列表中
     */
    private void addStringToSearchHistoryList() {
        //若搜索列表中已存在，则移除后添加到列表的起始位置
        for (int i = 0; i < searchHistoryList.size(); i++) {
            if (searchHistoryList.get(i).equals(searchText)) {
                searchHistoryList.remove(i);
                break;
            }
        }

        if (searchHistoryList.size() >= 20) {
            searchHistoryList.remove(searchHistoryList.size() - 1);
        }

        searchHistoryList.add(0, searchText);
        //保存到本地
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < searchHistoryList.size(); i++) {
            stringBuffer.append(searchHistoryList.get(i)).append(SEPERATOR_OF_SEARCH_HISTORY);
        }
        //去除掉最后一个 ","
        String searchHistory = stringBuffer.toString();
        searchHistory = searchHistory.substring(0, searchHistory.length() - 1);
        getSharedPreferences(ConstantsUtil.GLOBAL_CONFIG_FILE_NAME, MODE_PRIVATE).edit()
                .putString(keyOfSearchHistory, searchHistory).commit();

        searchGoodsHistoryAdapter.setData(searchHistoryList);
        searchGoodsHistoryAdapter.notifyDataSetChanged();
    }


    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.inject(this);
        hotSearchList.clear();
        searchHistoryList.clear();
        searchRecommendList.clear();

        hotSearchGridViewAdapter = new HotSearchGridViewAdapter(this, hotSearchList);
        searchGoodsHistoryAdapter = new SearchGoodsHistoryAdapter(this, searchHistoryList, false);
        searchRecommendAdapter = new SearchGoodsHistoryAdapter(this, searchRecommendList, true);

        hotSearchGridView.setAdapter(hotSearchGridViewAdapter);
        searchHistoryListView.setAdapter(searchGoodsHistoryAdapter);
        searchRecommendListView.setAdapter(searchRecommendAdapter);

        hotSearchGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchStringText(hotSearchList.get(position));
            }
        });
        searchHistoryListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchStringText(searchHistoryList.get(position));
            }
        });
        searchRecommendListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchStringText(searchRecommendList.get(position));
            }
        });

        //取出本地的搜索历史记录
        String searchHistory = getSharedPreferences(ConstantsUtil.GLOBAL_CONFIG_FILE_NAME, MODE_PRIVATE).getString(keyOfSearchHistory, null);
        if (StringUtil.notEmpty(searchHistory)) {
            String[] searchHistoryArray = searchHistory.split(SEPERATOR_OF_SEARCH_HISTORY);
            for (int i = 0; i < searchHistoryArray.length; i++) {
                searchHistoryList.add(searchHistoryArray[i]);
                searchGoodsHistoryAdapter.setData(searchHistoryList);
                searchGoodsHistoryAdapter.notifyDataSetChanged();
            }
        }


        searchTextEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchText = s.toString();
                if (StringUtil.isNull(searchText)) {
                    hideAllView();
                    showHotSearchAndRefreshSearchHistory();
                } else {
                    if (!isSearchingGoods) {//搜索商品过程中，不调用自动补全功能
                        searchRecommendAdapter.setSearchText(searchText);
                        getAutomaticCompletion(searchText);
                    }
                }
            }
        });

        CommonUtils.setTitle(this, rlTitle);
        allProductList.clear();
        productListView = (ListView) findViewById(R.id.lv_searchGoodsActivity_productList);
        productAdapter = new MyOrderAdapter(allProductList);
        productListView.setAdapter(productAdapter);

        refreshLayout.setEnableRefresh(false);
        refreshLayout.setAutoLoadMore(true);
        refreshLayout.setOverScrollTopShow(false);
        refreshLayout.setOverScrollBottomShow(true);
        refreshLayout.setEnableOverScroll(true);
        //下拉刷新和上啦加载更多
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {//下拉刷新
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {//加载更多
                pageNo += 1;
                if (searchCategoryProduct) {
                    searchProductCategory(selectedSortBy, false, SORT_BY_PRICE_ASC_DESC,  true);
                } else {
                    searchGoods(selectedSortBy, false, SORT_BY_PRICE_ASC_DESC,  true);
                }
            }
        });
        productListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(SearchGoodsActivity.this, WebViewActivity.class);
//                intent.putExtra(JsRequstInterface.PRODUCT_ID, allProductList.get(position).getProductNo());
//                startActivity(intent);
//                AppContext.extraActivityList.add(SearchGoodsActivity.this);
                CommonUtils.startGoodsDeteail(SearchGoodsActivity.this, allProductList.get(position).getProductNo());
            }
        });



        descDrawable = getResources().getDrawable(R.mipmap.ic_goods_sort_down);
        generalDrawable = getResources().getDrawable(R.mipmap.ic_goods_sort_up);
        defaultColor = getResources().getColor(R.color.c_666666);
        selectedColor = getResources().getColor(R.color.themeRed);
        sortByGeneralTextView.setTextColor(selectedColor);

        hideAllView();
        Intent intent = getIntent();
        searchText = intent.getStringExtra(JsRequstInterface.CLASSIFY_ID);
        categoryLevel1Id = intent.getStringExtra(JsRequstInterface.CATEGORY_LEVEL1_ID);
        if (StringUtil.notEmpty(searchText)) {
            comeFromCategory = true;
            pageNo = 1;
            searchCategoryProduct = true;
            searchTextView.setVisibility(View.INVISIBLE);
            hideSoftInput();
            searchProductCategory(selectedSortBy, true, SORT_BY_PRICE_ASC_DESC, true);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        } else {
            showSoftInput();
            sv_hotSearchAndsearchHistoryScrollView.setVisibility(View.VISIBLE);
            borderSearchRecommendView.setVisibility(View.VISIBLE);
        }

        searchTextEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && StringUtil.isNull(searchTextEditText.getText().toString())) {
                    searchCategoryProduct = false;
                    searchTextEditText.setHint(searchTextBean.getSearchfordefault());
                    hideAllView();
                    showHotSearchAndRefreshSearchHistory();
                    if (sv_hotSearchAndsearchHistoryScrollView.getVisibility() == View.VISIBLE && flListContent.getVisibility() == View.GONE) {
                        searchTextView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        getHotSearchWords();
    }

    /**
     * 点击人们关键字、历史记录、搜索
     */
    private void searchStringText(String searchText) {
        pageNo = 1;
        selectedSortBy = SORT_BY_GENERAL;
        setDefaultTextView();
        this.searchText = searchText;
        sortByGeneralTextView.setTextColor(selectedColor);
        addStringToSearchHistoryList();
        hideAllView();
        TDUtils.onEvent(mContext, "100002", "搜索框点击", TDUtils.getInstance().putUserid().putParams("搜索内容", searchText).buildParams());
        searchGoods(selectedSortBy, true, selectedSortBy, true);
        searchTextEditText.setText(searchText);
        searchTextEditText.setSelection(searchTextEditText.getText().toString().length());
    }

    /**
     * 隐藏所有的view
     */
    private void hideAllView() {
        //隐藏热门搜索和搜索历史
        hideHotSearchViewAndsearchHistoryView();

        //搜索推荐
        searchRecommendListView.setVisibility(View.GONE);

        //商品列表
        sortLinearLayout.setVisibility(View.GONE);
        flListContent.setVisibility(View.GONE);

        //搜索无数据显示
        noProductDataRelativeLayout.setVisibility(View.GONE);
    }

    /**
     * 隐藏热门搜索和搜索历史
     */
    private void hideHotSearchViewAndsearchHistoryView() {
        sv_hotSearchAndsearchHistoryScrollView.setVisibility(View.GONE);
        borderSearchRecommendView.setVisibility(View.GONE);
    }

    /**
     * 显示热门搜索和刷新搜索历史
     */
    private void showHotSearchAndRefreshSearchHistory() {
        sv_hotSearchAndsearchHistoryScrollView.setVisibility(View.VISIBLE);
        borderSearchRecommendView.setVisibility(View.VISIBLE);

        searchGoodsHistoryAdapter.notifyDataSetChanged();
        if (searchHistoryList.size() > 0) {
            searchHistoryLinearLayout.setVisibility(View.VISIBLE);
        } else {
            searchHistoryLinearLayout.setVisibility(View.GONE);
        }

    }

    @Override
    protected String getPageName() {
        return getString(R.string.str_pagename_searchgoods);
    }


    /**
     * @param initRequest         是否是初始化请求
     * @param priceAscOrDesc
     * @param dialog              是否显示网络加载框架
     */
    public void searchProductCategory(int orderBy, final boolean initRequest, int priceAscOrDesc, boolean dialog) {
        if (mDialog == null) {
            mDialog = new LoadingAlertDialog(this);
        }
        if (dialog && initRequest)
            mDialog.show(ConstantsUtil.NETWORK_REQUEST_IN);

        Map<String,String> params = new HashMap<>();
        params.put("upCategoryType", searchText);
        params.put("categoryType", categoryLevel1Id);
        params.put("pageNo", pageNo+"");
        params.put("orderNo", orderBy+"");
        params.put("deAc", priceAscOrDesc+"");

        HttpRequest.post(mContext, HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.PRODUCT_CATEGORY, params,
                new AsyncResponseCallBack() {

                    @Override
                    public void onResult(String arg2) {
                        if (mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        refreshLayout.finishLoadmore();
                        String jsonStr = new String(arg2);
                        LogUtil.printLog("品类查看: " + jsonStr);

                        JSONObject jsonObject = JSON.parseObject(jsonStr);
                        hideAllView();

                        flListContent.setVisibility(View.VISIBLE);
                        productListView.setVisibility(View.VISIBLE);
                        sortLinearLayout.setVisibility(View.VISIBLE);

                        if (initRequest) {
                            allProductList.clear();
                            productAdapter.notifyDataSetChanged();
                        }

                        if ((ConstantsUtil.RESPONSE_SUCCEED == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) || allProductList.size() > 0) {
                            if (jsonObject.containsKey(ConstantsUtil.RESPONSE_DATA_JSON_ARRAY_FIELD_NAME)) {
                                JSONArray productJsonArray = jsonObject
                                        .getJSONArray(ConstantsUtil.RESPONSE_DATA_JSON_ARRAY_FIELD_NAME);
                                Product product = null;
                                for (int i = 0; i < productJsonArray.size(); i++) {
                                    product = JSON.parseObject(productJsonArray.get(i).toString(), Product.class);
                                    allProductList.add(product);
                                }
                                if (productJsonArray==null||productJsonArray.size()==0){
                                    refreshLayout.setEnableLoadmore(false);
                                }else{
                                    refreshLayout.setEnableLoadmore(true);
                                }
                            } else {
                                Toast.makeText(SearchGoodsActivity.this, ConstantsUtil.NO_MORE_DATA_PROMPT,
                                        Toast.LENGTH_SHORT).show();
                            }
                            productAdapter.notifyDataSetChanged();
                            if (initRequest) {
                                productListView.setSelection(0);
                            }
                        } else if ((ConstantsUtil.RESPONSE_NO_DATA == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME) && allProductList.size() <= 0)) {
                            showNoDataView();
                        } else {
                            showNoDataView();
                        }

                        hideSoftInput();
                    }

                    @Override
                    public void onFailed(String path, String msg) {
                        if (mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        Toast.makeText(SearchGoodsActivity.this, ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER,
                                Toast.LENGTH_SHORT).show();
                    }
                });

    }

    /**
     * @param orderBy             排序: 0.综合 1.销量 2.价格
     * @param initRequest         是否是初始化请求
     * @param priceAscOrDesc
     * @param dialog              是否显示网络加载框
     */
    public void searchGoods(int orderBy, final boolean initRequest, int priceAscOrDesc,  boolean dialog) {
        isSearchingGoods = true;
        if (mDialog == null) {
            mDialog = new LoadingAlertDialog(this);
        }
        if (dialog && initRequest)
            mDialog.show(ConstantsUtil.NETWORK_REQUEST_IN);

        Map<String,String> params = new HashMap<>();
        params.put("productName", searchText);
        params.put("orderNo", orderBy+"");
        params.put("deAc", priceAscOrDesc+"");
        params.put("pageNo", pageNo+"");

        HttpRequest.post(mContext, HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.SEARCH_GOODS, params,
                new AsyncResponseCallBack() {

                    @Override
                    public void onResult(String arg2) {
                        isSearchingGoods = false;
                        if (mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                            refreshLayout.finishLoadmore();        //必须加
                        String jsonStr = new String(arg2);
                        LogUtil.printLog("搜索商品: " + jsonStr);

                        JSONObject jsonObject = JSON.parseObject(jsonStr);
                        hideAllView();

                        flListContent.setVisibility(View.VISIBLE);

                        productListView.setVisibility(View.VISIBLE);
                        sortLinearLayout.setVisibility(View.VISIBLE);

                        if (initRequest) {
                            allProductList.clear();
                            productAdapter.notifyDataSetChanged();
                        }

                        if ((ConstantsUtil.RESPONSE_SUCCEED == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) || allProductList.size() > 0) {
                            if (jsonObject.containsKey(ConstantsUtil.RESPONSE_DATA_JSON_ARRAY_FIELD_NAME)) {
                                JSONArray productJsonArray = jsonObject
                                        .getJSONArray(ConstantsUtil.RESPONSE_DATA_JSON_ARRAY_FIELD_NAME);
                                Product product = null;
                                for (int i = 0; i < productJsonArray.size(); i++) {
                                    product = JSON.parseObject(productJsonArray.get(i).toString(), Product.class);
                                    allProductList.add(product);
                                }
                                if (productJsonArray==null||productJsonArray.size()==0){
                                    refreshLayout.setEnableLoadmore(false);
                                }else{
                                    refreshLayout.setEnableLoadmore(true);
                                }
                            } else {
                                Toast.makeText(SearchGoodsActivity.this, ConstantsUtil.NO_MORE_DATA_PROMPT,
                                        Toast.LENGTH_SHORT).show();
                            }
                            productAdapter.notifyDataSetChanged();
                            if (initRequest) {
                                productListView.setSelection(0);
                            }
                        } else if ((ConstantsUtil.RESPONSE_NO_DATA == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME) && allProductList.size() <= 0)) {
                            showNoDataView();
                        } else {
                            showNoDataView();
                        }

                        hideSoftInput();
                    }

                    @Override
                    public void onFailed(String path, String msg) {
                        isSearchingGoods = false;
                        if (mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        Toast.makeText(SearchGoodsActivity.this, ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER + "搜索商品失败",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /**
     * 热门推荐词汇
     */
    public void getHotSearchWords() {
        Map<String,String> params = new HashMap<>();
        HttpRequest.post(mContext, HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.GET_SEARCH_HOT_WORD, params,
                new AsyncResponseCallBack() {

                    @Override
                    public void onResult(String arg2) {
                        String jsonStr = new String(arg2);
                        LogUtil.printLog("热门推荐词汇: " + jsonStr);

                        JSONObject jsonObject = JSON.parseObject(jsonStr);
//                        hideAllView();


                        if ((ConstantsUtil.RESPONSE_SUCCEED == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) || allProductList.size() > 0) {
                            if (jsonObject.containsKey(ConstantsUtil.RESPONSE_DATA_JSON_ARRAY_FIELD_NAME)) {
                                hotSearchList.clear();

                                searchTextBean = JSON.parseObject(jsonObject.getString(ConstantsUtil.RESPONSE_DATA_JSON_ARRAY_FIELD_NAME), SearchTextBean.class);
                                if (!comeFromCategory) {
                                    searchText = searchTextBean.getSearchfordefault();
                                    searchTextEditText.setHint(searchTextBean.getSearchfordefault());
                                }
                                hotSearchList = searchTextBean.getSearchHotWord();
                                hotSearchGridViewAdapter.setData(searchTextBean.getSearchHotWord());
                                hotSearchGridViewAdapter.notifyDataSetChanged();
                                if (!comeFromCategory) {
                                    hideAllView();
                                    showHotSearchAndRefreshSearchHistory();
                                }

                            } else {
                                Toast.makeText(SearchGoodsActivity.this, ConstantsUtil.NO_MORE_DATA_PROMPT,
                                        Toast.LENGTH_SHORT).show();
                            }
                            productAdapter.notifyDataSetChanged();
                        } else if ((ConstantsUtil.RESPONSE_NO_DATA == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME) && allProductList.size() <= 0)) {
                            hideHotSearchViewAndsearchHistoryView();
                        } else {
                            hideHotSearchViewAndsearchHistoryView();
                        }
                    }

                    @Override
                    public void onFailed(String path, String msg) {
                    }
                });

    }

    /**
     * 热词自动补全
     */
    public void getAutomaticCompletion(String automaticSearchText) {
        final LoadingAlertDialog mDialog = new LoadingAlertDialog(this);
        mDialog.show(ConstantsUtil.NETWORK_REQUEST_IN);

        Map<String,String> params = new HashMap<>();
        params.put("searchword", automaticSearchText);

        HttpRequest.post(mContext, HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.GET_AUTOMATIC_COMPLETION, params,
                new AsyncResponseCallBack() {

                    @Override
                    public void onResult(String arg2) {
                        if (mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        String jsonStr = new String(arg2);
                        LogUtil.printLog("热词自动补全: " + jsonStr);

                        JSONObject jsonObject = JSON.parseObject(jsonStr);
                        hideAllView();

                        if ((ConstantsUtil.RESPONSE_SUCCEED == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) || allProductList.size() > 0) {
                            if (jsonObject.containsKey(ConstantsUtil.RESPONSE_DATA_JSON_ARRAY_FIELD_NAME)) {
                                searchRecommendList.clear();

                                SearchTextBean searchTextBean = JSON.parseObject(jsonObject.toString(), SearchTextBean.class);
                                searchRecommendList = searchTextBean.getData();
                                searchRecommendAdapter.setData(searchTextBean.getData());
                                searchRecommendAdapter.notifyDataSetChanged();
                                hideAllView();
                                searchRecommendListView.setVisibility(View.VISIBLE);
                                borderSearchRecommendView.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(SearchGoodsActivity.this, ConstantsUtil.NO_MORE_DATA_PROMPT,
                                        Toast.LENGTH_SHORT).show();
                            }
                            productAdapter.notifyDataSetChanged();
                        } else if ((ConstantsUtil.RESPONSE_NO_DATA == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME) && allProductList.size() <= 0)) {
//                            showNoDataView();
                        } else {
//                            showNoDataView();
                        }
                    }

                    @Override
                    public void onFailed(String path, String msg) {
                        if (mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        Toast.makeText(SearchGoodsActivity.this, ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER + "搜索商品失败",
                                Toast.LENGTH_SHORT).show();
                    }
                });

    }

    /**
     * 隐藏软键盘
     */
    private void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);

        //禁止页面初始化时， edittext获取焦点，弹出软键盘
        backTextView.setFocusable(true);
        backTextView.setFocusableInTouchMode(true);
        backTextView.requestFocus();  // 初始不让EditText得焦点
        backTextView.requestFocusFromTouch();
    }

    /**
     * 显示输入法
     */
    private void showSoftInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchTextEditText, InputMethodManager.SHOW_FORCED);
        searchTextEditText.setFocusable(true);
        searchTextEditText.setFocusableInTouchMode(true);
    }

    public void setDefaultTextView() {
        sortByGeneralTextView.setTextColor(defaultColor);
        sortByGeneralImageView.setBackgroundColor(0xffffff);
        sortBySaleAmountTextView.setTextColor(defaultColor);
        sortBySaleAmountImageView.setBackgroundColor(0xffffff);
        sortByPriceTextView.setTextColor(defaultColor);
        sortByPriceImageView.setImageResource(R.mipmap.ic_goods_sort_deafult);
    }

    private void showNoDataView() {
        hideAllView();
        noProductDataRelativeLayout.setVisibility(View.VISIBLE);
    }

    class MyOrderAdapter extends BaseAdapter {
        private List<Product> orderList;

        public MyOrderAdapter(List<Product> orderList) {
            super();
            this.orderList = orderList;
        }

        @Override
        public int getCount() {
            return orderList.size();
        }

        @Override
        public Object getItem(int position) {
            return orderList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(SearchGoodsActivity.this, R.layout.search_goods_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Product product = orderList.get(position);

            LogUtil.printLog("ProductStock="+product.getProductStock());
            //抢光了默认隐藏
            viewHolder.zeroProductStockImageView.setVisibility(View.GONE);
            if(product.getProductStock()==0){
                viewHolder.zeroProductStockImageView.setVisibility(View.VISIBLE);
            }

            Picasso.with(SearchGoodsActivity.this).load(product.getProImage()).placeholder(R.drawable.load_image_place_holder)
                    .resize(ConstantsUtil.PRODUCT_IMAGE_WIDTH, ConstantsUtil.PRODUCT_IMAGE_WIDTH)
                    .into(viewHolder.productImageView);
            viewHolder.productNameTextView.setText(product.getProductName());
            int monthAmount = new BigDecimal(Double.parseDouble(String.valueOf(product.getMonthAmount()))).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();        //(int)product.getMonthAmount()
            if (product.getMonthAmount() > monthAmount) {
                monthAmount = monthAmount + 1;
            }

            viewHolder.monthlyPaymentTextView.setText("售价： " + MyAccountFragment.moneyDecimalFormat.format(Double.valueOf(product.getPrice())) + "元");
            viewHolder.productPriceTextView.setText(CommonUtils.getTextStyle(SearchGoodsActivity.this, new String[]{"月付：", monthAmount + "", " 元"}, new int[]{R.style.style_gray_13sp, R.style.style_themeRed_18sp, R.style.style_themeRed_13sp}), TextView.BufferType.SPANNABLE);
            viewHolder.monthNumTextView.setText(" x " + product.getMonthnum() + "期");
            return convertView;
        }

    }

    static class ViewHolder {
        @InjectView(R.id.iv_searchGoodsActivity_orderItem_productImage)
        ImageView productImageView;
        @InjectView(R.id.iv_searchGoodsActivity_orderItem_zeroProductStock)
        ImageView zeroProductStockImageView;
        @InjectView(R.id.tv_searchGoodsActivity_orderItem_productName)
        TextView productNameTextView;
        @InjectView(R.id.tv_searchGoodsActivity_orderItem_productPrice)
        TextView productPriceTextView;
        @InjectView(R.id.tv_searchGoodsActivity_orderItem_monthlyPayment)
        TextView monthlyPaymentTextView;
        @InjectView(R.id.tv_searchGoodsActivity_orderItem_monthNum)
        TextView monthNumTextView;

        public ViewHolder(View v) {
            super();
            ButterKnife.inject(this, v);
        }
    }

    class HotSearchGridViewAdapter extends BaseAdapter {
        private List<String> orderList;
        private Activity activity;

        public HotSearchGridViewAdapter(Activity activity, List<String> orderList) {
            super();
            this.orderList = orderList;
            this.activity = activity;
        }

        public void setData(List<String> orderList) {
            this.orderList = orderList;
        }

        @Override
        public int getCount() {
            return orderList.size();
        }

        @Override
        public Object getItem(int position) {
            return orderList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            HotSearchViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(activity, R.layout.search_goods_search_recommend_item, null);
                viewHolder = new HotSearchViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (HotSearchViewHolder) convertView.getTag();
            }
            viewHolder.searchNameTextView.setText(orderList.get(position));
            return convertView;
        }

    }

    class HotSearchViewHolder {
        @InjectView(R.id.iv_searchGoodsActivity_searchRecommendItem_searchName)
        TextView searchNameTextView;

        public HotSearchViewHolder(View v) {
            super();
            ButterKnife.inject(this, v);
        }
    }

}
