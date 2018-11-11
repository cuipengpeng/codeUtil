package com.test.bank.view.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.test.bank.R;
import com.test.bank.adapter.SevenDayYieldListAdapter;
import com.test.bank.base.BaseUIActivity;
import com.test.bank.bean.WanfenIncomeBean;
import com.test.bank.http.HttpRequest;
import com.test.bank.utils.ConstantsUtil;
import com.test.bank.utils.DensityUtil;
import com.test.bank.utils.SPUtil;
import com.test.bank.utils.StringUtil;
import com.test.bank.weight.refreshlayout.AutoLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
*/
public class SevenDayYieldListActivity extends BaseUIActivity {

    @BindView(R.id.tv_sevenDayYieldListActivity_yieldKey)
    TextView yieldKeyTextView;
    @BindView(R.id.tv_sevenDayYieldListActivity_yieldValue)
    TextView yieldValueTextView;
    @BindView(R.id.tv_sevenDayYieldListActivity_yieldDesc)
    TextView yieldDescTextView;
    @BindView(R.id.rv_sevenDayYieldListActivity_yieldList)
    AutoLoadMoreRecyclerView yieldListRecyclerView;
    @BindView(R.id.rv_sevenDayYieldListActivity_wanfenIncomeList)
    AutoLoadMoreRecyclerView wanfenIncomeListRecyclerView;
    @BindView(R.id.rv_sevenDayYieldListActivity_accumulatedList)
    AutoLoadMoreRecyclerView accumulatedListRecyclerView;
    @BindView(R.id.ll_list_no_data_view)
    LinearLayout noDataViewLinearLayout;
    @BindView(R.id.tv_list_no_data_view)
    TextView noDataTextView;

    private String fundCode;

    private int sevenDayYieldPageNo = 1;
    private int wanfenIncomePageNo = 1;
    private int accumulatedIncomePageNo = 1;

    private SevenDayYieldListAdapter sevenDayYieldAdapter;
    private List<WanfenIncomeBean.List> mSevenDayYieldList = new ArrayList<WanfenIncomeBean.List>();
    private SevenDayYieldListAdapter wanfenIncomeAdapter;
    private List<WanfenIncomeBean.List> mWanfenIncomeList = new ArrayList<WanfenIncomeBean.List>();
    private SevenDayYieldListAdapter accumulatedIncomeAdapter;
    private List<WanfenIncomeBean.List> mAccumulatedIncomeList = new ArrayList<WanfenIncomeBean.List>();

    public static final  int SEVEN_DAY_YIELD = 501;
    public static final  int WAN_FEN_INCOME = 502;
    public static final  int ACCUMULATED_INCOME = 503;
    private int currentIncomeType = SEVEN_DAY_YIELD;
    private PopupWindow popupWindow;

    @OnClick({R.id.tv_base_title})
    public void onItemClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.tv_base_title:
                showPopUp(baseTitleTextView);
                break;
        }
    }

    @Override
    protected String getPageTitle() {
        return "";
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_seven_day_yield_list;
    }

    @Override
    protected void initPageData() {
        fundCode = getIntent().getStringExtra(SingleFundDetailActivity.KEY_OF_FUND_CODE);

        Drawable drawable = getResources().getDrawable(R.mipmap.seven_day_yield_list_activity_arrow_down);
        baseTitleTextView.setCompoundDrawablePadding(((int)DensityUtil.px2dip(55)));
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        baseTitleTextView.setCompoundDrawables(null, null, drawable, null);


        sevenDayYieldAdapter = new SevenDayYieldListAdapter(this, SEVEN_DAY_YIELD);
        wanfenIncomeAdapter = new SevenDayYieldListAdapter(this, WAN_FEN_INCOME);
        accumulatedIncomeAdapter = new SevenDayYieldListAdapter(this, ACCUMULATED_INCOME);
        yieldListRecyclerView.setAdapter(sevenDayYieldAdapter);
        yieldListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        wanfenIncomeListRecyclerView.setAdapter(wanfenIncomeAdapter);
        wanfenIncomeListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        accumulatedListRecyclerView.setAdapter(accumulatedIncomeAdapter);
        accumulatedListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        yieldListRecyclerView.setOnRefreshListener(new AutoLoadMoreRecyclerView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshCorrespondingList(true);
            }
        });
        yieldListRecyclerView.setOnLoadMoreListener(new AutoLoadMoreRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                refreshCorrespondingList(false);
            }
        });
        wanfenIncomeListRecyclerView.setOnRefreshListener(new AutoLoadMoreRecyclerView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshCorrespondingList(true);
            }
        });
        wanfenIncomeListRecyclerView.setOnLoadMoreListener(new AutoLoadMoreRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                refreshCorrespondingList(false);
            }
        });
        accumulatedListRecyclerView.setOnRefreshListener(new AutoLoadMoreRecyclerView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshCorrespondingList(true);
            }
        });
        accumulatedListRecyclerView.setOnLoadMoreListener(new AutoLoadMoreRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                refreshCorrespondingList(false);
            }
        });

        defaultTitleSevenDayYield();
        baseTitleTextView.setTextColor(getResources().getColor(R.color.currentPlusActivityGold));

        getSevenDayYield(true);
    }


    /**
     * 刷新相对应的列表数据
     * @param initRequest
     */
    private void refreshCorrespondingList(boolean initRequest) {
        switch (currentIncomeType){
            case SEVEN_DAY_YIELD:
                getSevenDayYield(initRequest);
                break;
            case WAN_FEN_INCOME:
                getWanfenIncome(initRequest);
                break;
            case ACCUMULATED_INCOME:
                getAccumulatedIncome(initRequest);
                break;
        }
    }


    /**
     *  获取七日年化收益率
     * @param initRequest
     */
    private void getSevenDayYield(final boolean initRequest) {
        currentIncomeType = SEVEN_DAY_YIELD;

        if(initRequest){
            sevenDayYieldPageNo = 1;
        }else {
            sevenDayYieldPageNo+=1;
        }

        Map<String, String> params =  new HashMap<String, String>();
        params.put("token", SPUtil.getInstance().getToken());
        params.put("pageNo", sevenDayYieldPageNo+"");
        params.put("rowsSize", ConstantsUtil.PAGE_SIZE+"");

        HttpRequest.post(this, HttpRequest.APP_INTERFACE_WEB_URL + HttpRequest.BAOBAO_SEVEN_DAY_YIELD, params, new HttpRequest.HttpResponseCallBack() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                WanfenIncomeBean wanfenIncomeBean = JSON.parseObject(response.body(), WanfenIncomeBean.class);
                yieldValueTextView.setText(StringUtil.moneyDecimalFormat4(wanfenIncomeBean.getSummary()+"")+"%");
                if (wanfenIncomeBean.getList() != null && !wanfenIncomeBean.getList().isEmpty()) {
                    showListView();
                    yieldListRecyclerView.refreshComplete();
                    sevenDayYieldAdapter.upateData(initRequest, wanfenIncomeBean.getList());

                    if (wanfenIncomeBean.getList().size()<ConstantsUtil.PAGE_SIZE ) {
                        yieldListRecyclerView.noMoreLoading();
                    }
                } else {
                    showNoDataView() ;
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                yieldListRecyclerView.refreshComplete();
            }
        });
    }

    /**
     * 获取万份收益列表
     * @param initRequest
     */
    private void getWanfenIncome(final boolean initRequest) {
        currentIncomeType = WAN_FEN_INCOME;
        if(initRequest){
            wanfenIncomePageNo = 1;
        }else {
            wanfenIncomePageNo+=1;
        }

        Map<String, String> params =  new HashMap<String, String>();
        params.put("fundcode", "");
        params.put("pageNo", wanfenIncomePageNo+"");
        params.put("rowsSize", ConstantsUtil.PAGE_SIZE+"");

        HttpRequest.post( this, HttpRequest.APP_INTERFACE_WEB_URL + HttpRequest.BAOBAO_WAN_FEN_INCOME, params, new HttpRequest.HttpResponseCallBack() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                WanfenIncomeBean wanfenIncomeBean = JSON.parseObject(response.body(), WanfenIncomeBean.class);

                yieldValueTextView.setText(StringUtil.moneyDecimalFormat4(wanfenIncomeBean.getSummary()+""));
                if (wanfenIncomeBean.getList() != null && !wanfenIncomeBean.getList().isEmpty()) {
                    showListView();
                    wanfenIncomeListRecyclerView.refreshComplete();
                    wanfenIncomeAdapter.upateData(initRequest, wanfenIncomeBean.getList());

                    if (wanfenIncomeBean.getList().size()<ConstantsUtil.PAGE_SIZE ) {
                        wanfenIncomeListRecyclerView.noMoreLoading();
                    }
                } else {
                    showNoDataView() ;
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                wanfenIncomeListRecyclerView.refreshComplete();
            }
        });
    }

    /**
     * 获取累计收益列表
     * @param initRequest
     */
    private void getAccumulatedIncome(final boolean initRequest) {
        currentIncomeType = ACCUMULATED_INCOME;
        if(initRequest){
            accumulatedIncomePageNo = 1;
        }else {
            accumulatedIncomePageNo+=1;
        }

        Map<String, String> params =  new HashMap<String, String>();
        params.put("token", SPUtil.getInstance().getToken());
        params.put("pageNo", accumulatedIncomePageNo+"");
        params.put("rowsSize", ConstantsUtil.PAGE_SIZE+"");

        HttpRequest.post(this, HttpRequest.APP_INTERFACE_WEB_URL + HttpRequest.BAOBAO_ACCUMULATED_INCOME, params, new HttpRequest.HttpResponseCallBack() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                WanfenIncomeBean wanfenIncomeBean = JSON.parseObject(response.body(), WanfenIncomeBean.class);

                yieldValueTextView.setText(StringUtil.moneyDecimalFormat2(wanfenIncomeBean.getSummary()+""));
                if (wanfenIncomeBean.getList() != null && !wanfenIncomeBean.getList().isEmpty()) {
                    showListView();
                    accumulatedListRecyclerView.refreshComplete();
                    accumulatedIncomeAdapter.upateData(initRequest, wanfenIncomeBean.getList());

                    if (wanfenIncomeBean.getList().size()<ConstantsUtil.PAGE_SIZE ) {
                        accumulatedListRecyclerView.noMoreLoading();
                    }
                } else {
                    showNoDataView();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                accumulatedListRecyclerView.refreshComplete();
            }
        });
    }

    private void showListView() {
        noDataViewLinearLayout.setVisibility(View.GONE);
        yieldListRecyclerView.setVisibility(View.GONE);
        wanfenIncomeListRecyclerView.setVisibility(View.GONE);
        accumulatedListRecyclerView.setVisibility(View.GONE);
        switch (currentIncomeType){
            case SEVEN_DAY_YIELD:
                yieldListRecyclerView.setVisibility(View.VISIBLE);
                break;
            case WAN_FEN_INCOME:
                wanfenIncomeListRecyclerView.setVisibility(View.VISIBLE);
                break;
            case ACCUMULATED_INCOME:
                accumulatedListRecyclerView.setVisibility(View.VISIBLE);
                break;
        }

    }

    @Override
    public void showNoDataView() {
        noDataViewLinearLayout.setVisibility(View.VISIBLE);
        noDataTextView.setText("暂无收益记录");
        yieldListRecyclerView.setVisibility(View.GONE);
        wanfenIncomeListRecyclerView.setVisibility(View.GONE);
        accumulatedListRecyclerView.setVisibility(View.GONE);
    }

    private void showPopUp(View v) {
         View dropDownView = LayoutInflater.from(this).inflate(R.layout.popup_window_in_seven_day_yield_list, null);
        TextView sevenDayYieldTextView = dropDownView.findViewById(R.id.tv_sevenDayYieldListActivity_popupWindow_sevenDayYield);
        TextView wanfenIncomeTextView = dropDownView.findViewById(R.id.tv_sevenDayYieldListActivity_popupWindow_wanfenIncome);
        TextView accumulatedIncomeTextView = dropDownView.findViewById(R.id.tv_sevenDayYieldListActivity_popupWindow_accumulatedIncome);
        sevenDayYieldTextView.setOnClickListener(popupWindowOnClickListener);
        wanfenIncomeTextView.setOnClickListener(popupWindowOnClickListener);
        accumulatedIncomeTextView.setOnClickListener(popupWindowOnClickListener);

        popupWindow = new PopupWindow(dropDownView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        int[] location = new int[2];
        v.getLocationOnScreen(location);

//        popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1]-popupWindow.getHeight());
        popupWindow.showAsDropDown(v, 0, 0);
    }

    private  View.OnClickListener popupWindowOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            popupWindow.dismiss();
            yieldValueTextView.setText(ConstantsUtil.REPLACE_BLANK_SYMBOL);
            switch (v.getId()){
                case R.id.tv_sevenDayYieldListActivity_popupWindow_sevenDayYield:
                    currentIncomeType = SEVEN_DAY_YIELD;
                    defaultTitleSevenDayYield();
                    if(mSevenDayYieldList.size()>0){
                        sevenDayYieldAdapter.notifyDataSetChanged();
                    }else {
                        getSevenDayYield(true);
                    }
                    break;
                case R.id.tv_sevenDayYieldListActivity_popupWindow_wanfenIncome:
                    currentIncomeType = WAN_FEN_INCOME;
                    baseTitleTextView.setText("万份收益");
                    yieldKeyTextView.setText("近一月平均万份收益(元)");
                    if(mWanfenIncomeList.size()>0){
                        wanfenIncomeAdapter.notifyDataSetChanged();
                    }else {
                        getWanfenIncome(true);
                    }

                    break;
                case R.id.tv_sevenDayYieldListActivity_popupWindow_accumulatedIncome:
                    currentIncomeType = ACCUMULATED_INCOME;
                    baseTitleTextView.setText("收益");
                    yieldKeyTextView.setText("累计收益(元)");
                    if(mAccumulatedIncomeList.size()>0){
                        accumulatedIncomeAdapter.notifyDataSetChanged();
                    }else {
                        getAccumulatedIncome(true);
                    }
                    break;
            }
        }
    };

    private void defaultTitleSevenDayYield() {
        baseTitleTextView.setText("七日年化");
        yieldKeyTextView.setText("近一月平均七日年化(%)");
    }


}
