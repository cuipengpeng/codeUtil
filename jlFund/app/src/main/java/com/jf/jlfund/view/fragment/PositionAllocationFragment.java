package com.jf.jlfund.view.fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.jf.jlfund.R;
import com.jf.jlfund.adapter.PositionAllocationBondAdapter;
import com.jf.jlfund.adapter.PositionAllocationStockAdapter;
import com.jf.jlfund.base.BaseUIFragment;
import com.jf.jlfund.bean.PositionAllocationBean;
import com.jf.jlfund.http.HttpRequest;
import com.jf.jlfund.utils.DensityUtil;
import com.jf.jlfund.utils.StringUtil;
import com.jf.jlfund.view.activity.PositionAllocationActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

/**
 * 收益率走势图Fragment
 */
public class PositionAllocationFragment extends BaseUIFragment {
    @BindView(R.id.tv_positionAllocationFragment_stockPercentOfPieChart)
    TextView stockPercentOfPieChartTextView;
    @BindView(R.id.tv_positionAllocationFragment_bondPercentOfPieChart)
    TextView bondPercentOfPieChartTextView;
    @BindView(R.id.tv_positionAllocationFragment_cashPercentOfPieChart)
    TextView cashPercentOfPieChartTextView;
    @BindView(R.id.tv_positionAllocationFragment_otherPercentOfPieChart)
    TextView otherPercentOfPieChartTextView;

    @BindView(R.id.pieChart)
    PieChart pieChart;
    @BindView(R.id.rl_positionAllocationFragment_pieChartAndPercent)
    RelativeLayout pieChartAndPercentRelativeLayout;
    @BindView(R.id.tv_positionAllocationFragment_stockKey)
    TextView stockKeyTextView;
    @BindView(R.id.tv_positionAllocationFragment_stockPercentValue)
    TextView stockPercentValueTextView;
    @BindView(R.id.tv_positionAllocationFragment_bondKey)
    TextView bondKeyTextView;
    @BindView(R.id.tv_positionAllocationFragment_bondPercentValue)
    TextView bondPercentValueTextView;
    @BindView(R.id.tv_positionAllocationFragment_cashKey)
    TextView cashKeyTextView;
    @BindView(R.id.tv_positionAllocationFragment_cashPercentValue)
    TextView cashPercentValueTextView;
    @BindView(R.id.tv_positionAllocationFragment_otherKey)
    TextView otherKeyTextView;
    @BindView(R.id.tv_positionAllocationFragment_otherPercentValue)
    TextView otherPercentValueTextView;

    @BindView(R.id.rl_positionAllocationFragment_bondTitle)
    RelativeLayout bondTitleRelativeLayout;
    @BindView(R.id.rl_positionAllocationFragment_stockTitle)
    RelativeLayout stockTitleRelativeLayout;
    @BindView(R.id.ll_positionAllocationFragment_stockKey)
    LinearLayout stockKeyLinearLayout;
    @BindView(R.id.ll_positionAllocationFragment_bondKey)
    LinearLayout bondKeyLinearLayout;
    @BindView(R.id.v_positionAllocationFragment_stockDivideLine)
    View stockDivideLineView;
    @BindView(R.id.v_positionAllocationFragment_bondDivideLine)
    View bondDivideLineView;
    @BindView(R.id.rv_positionAllocationFragment_stock)
    RecyclerView stockRecyclerView;
    @BindView(R.id.rv_positionAllocationFragment_bond)
    RecyclerView bondRecyclerView;
    @BindView(R.id.iv_positionAllocationFragment_bondArrow)
    ImageView bondArrowImageView;
    @BindView(R.id.iv_positionAllocationFragment_stockArrow)
    ImageView stockArrowImageView;
    @BindView(R.id.ll_list_no_data_view)
    LinearLayout noDataLinearLayout;
    @BindView(R.id.sv_positionAllocationFragment_all)
    ScrollView allScrollView;

    private boolean showStockList = true;
    private boolean showBondList = true;
    private final float DP8 = 8f;
    private final float DP14 = 14f;
    private List<PositionAllocationBean.Scalelist> scalelist = new ArrayList<PositionAllocationBean.Scalelist>();

    @OnClick({R.id.rl_positionAllocationFragment_bondTitle, R.id.rl_positionAllocationFragment_stockTitle})
    public void onItemClick(View v) {
        Intent intent = new Intent();
        RelativeLayout.LayoutParams layoutParams = null;
        switch (v.getId()) {
            case R.id.rl_positionAllocationFragment_stockTitle:
                layoutParams = (RelativeLayout.LayoutParams) stockArrowImageView.getLayoutParams();
                if (showStockList) {
                    stockKeyLinearLayout.setVisibility(View.GONE);
                    stockDivideLineView.setVisibility(View.GONE);
                    stockRecyclerView.setVisibility(View.GONE);
                    layoutParams.width = DensityUtil.dip2px(DP8);
                    layoutParams.height = DensityUtil.dip2px(DP14);
                    stockArrowImageView.setLayoutParams(layoutParams);
                    stockArrowImageView.setBackgroundResource(R.mipmap.arrow_up);
                    showStockList = false;

                } else {
                    stockKeyLinearLayout.setVisibility(View.VISIBLE);
                    stockDivideLineView.setVisibility(View.VISIBLE);
                    stockRecyclerView.setVisibility(View.VISIBLE);
                    layoutParams.width = DensityUtil.dip2px(DP14);
                    layoutParams.height = DensityUtil.dip2px(DP8);
                    stockArrowImageView.setLayoutParams(layoutParams);
                    stockArrowImageView.setBackgroundResource(R.mipmap.arrow_down);
                    showStockList = true;

                }
                break;
            case R.id.rl_positionAllocationFragment_bondTitle:
                layoutParams = (RelativeLayout.LayoutParams) bondArrowImageView.getLayoutParams();
                if (showBondList) {
                    bondKeyLinearLayout.setVisibility(View.GONE);
                    bondDivideLineView.setVisibility(View.GONE);
                    bondRecyclerView.setVisibility(View.GONE);
                    layoutParams.width = DensityUtil.dip2px(DP8);
                    layoutParams.height = DensityUtil.dip2px(DP14);
                    bondArrowImageView.setLayoutParams(layoutParams);
                    bondArrowImageView.setBackgroundResource(R.mipmap.arrow_up);
                    showBondList = false;
                } else {
                    bondKeyLinearLayout.setVisibility(View.VISIBLE);
                    bondDivideLineView.setVisibility(View.VISIBLE);
                    bondRecyclerView.setVisibility(View.VISIBLE);
                    layoutParams.width = DensityUtil.dip2px(DP14);
                    layoutParams.height = DensityUtil.dip2px(DP8);
                    bondArrowImageView.setLayoutParams(layoutParams);
                    bondArrowImageView.setBackgroundResource(R.mipmap.arrow_down);
                    showBondList = true;
                }
                break;
        }
    }

    Unbinder unbinder;
    public static final float pieChartTextSize = 14f;
    public static final int STOCK_INDEX = 0;
    public static final int BOND_INDEX = 1;
    public static final int CASH_INDEX = 2;
    public static final int OTHER_INDEX = 3;
    public static final int[] PIE_COLORS = {Color.parseColor("#f53857"), Color.parseColor("#0084ff"),
            Color.parseColor("#ff963b"), Color.parseColor("#ae87fa")};
    PositionAllocationStockAdapter stockAdapter;
    PositionAllocationBondAdapter bondAdapter;
    List<PositionAllocationBean.StockAssetConf> mStockList = new ArrayList<PositionAllocationBean.StockAssetConf>();
    List<PositionAllocationBean.BndAssetConf> mBondList = new ArrayList<PositionAllocationBean.BndAssetConf>();

    @Override
    protected String getPageTitle() {
        return null;
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.fragment_position_allocation;
    }

    @Override
    protected void initPageData() {
        pieChartAndPercentRelativeLayout.setVisibility(View.INVISIBLE);
        stockAdapter = new PositionAllocationStockAdapter(getActivity(), mStockList);
        stockRecyclerView.setAdapter(stockAdapter);
        stockRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        bondAdapter = new PositionAllocationBondAdapter(getActivity(), mBondList);
        bondRecyclerView.setAdapter(bondAdapter);
        bondRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        getFundCompanyInfo();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    //设置中间文字
    private SpannableString generateCenterSpannableText(String title) {
        SpannableString s = new SpannableString(title);
        //s.setSpan(new RelativeSizeSpan(1.7f), 0, 14, 0);
        //s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
        Resources resources = getActivity().getResources();
        s.setSpan(new ForegroundColorSpan(resources.getColor(R.color.appTitleColor)), 0, 4, 0);
        s.setSpan(new ForegroundColorSpan(resources.getColor(R.color.appContentColor)), 4, s.length(), 0);
        //s.setSpan(new RelativeSizeSpan(.8f), 14, s.length() - 15, 0);
        // s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
        // s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);
//        s.setSpan(new TextAppearanceSpan(activity, R.style.style_themeRed_22sp), 0, (money.length() - 1),
//                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        s.setSpan(new TextAppearanceSpan(activity, R.style.style_themeRed_12sp), (money.length() - 1),
//                money.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return s;
    }

    /**
     * 设置饼图样式
     *
     * @param pieChart
     * @param pieValues
     * @param showLegend 是否显示图例
     */
    public void setPieChart(PieChart pieChart, List<PieEntry> pieValues, boolean showLegend) {
        pieChart.setUsePercentValues(false);//设置使用百分比
        pieChart.getDescription().setEnabled(false);//设置描述
        pieChart.setExtraOffsets(15, 15, 8, 15);//设置pieChart图表上下左右的偏移，类似于外边距
        pieChart.setRotationEnabled(false);//设置pieChart图表是否可以手动旋转
        pieChart.setRotationAngle(270f);//设置旋转角度
        pieChart.setDrawEntryLabels(false);//圆形饼图上绘制lable
//        pieChart.setDragDecelerationFrictionCoef(0.95f);//设置pieChart图表转动阻力摩擦系数[0,1]
//        pieChart.setHighlightPerTapEnabled(true);       //设置piecahrt图表点击Item高亮是否可用

// 设置 pieChart 图表Item文本属性
//        pieChart.setDrawEntryLabels(true);              //设置pieChart是否只显示饼图上百分比不显示文字（true：下面属性才有效果）
//        pieChart.setEntryLabelColor(Color.WHITE);       //设置pieChart图表文本字体颜色
//        pieChart.setEntryLabelTypeface(mTfRegular);     //设置pieChart图表文本字体样式
//        pieChart.setEntryLabelTextSize(10f);            //设置pieChart图表文本字体大小
//
// 设置 pieChart 内部圆环属性
        pieChart.setDrawHoleEnabled(true);              //是否显示PieChart内部圆环(true:下面属性才有意义)
        pieChart.setHoleRadius(90f);                    //设置PieChart内部圆的半径(这里设置28.0f)
//        pieChart.setTransparentCircleRadius(2f);       //设置PieChart内部透明圆的半径(这里设置31.0f)
//        pieChart.setTransparentCircleColor(Color.BLACK);//设置PieChart内部透明圆与内部圆间距(31f-28f)填充颜色
//        pieChart.setTransparentCircleAlpha(50);         //设置PieChart内部透明圆与内部圆间距(31f-28f)透明度[0~255]数值越小越透明
//        pieChart.setHoleColor(Color.WHITE);             //设置PieChart内部圆的颜色

        pieChart.setDrawCenterText(true);               //是否绘制PieChart内部中心文本（true：下面属性才有意义）
        pieChart.setCenterTextSize(pieChartTextSize);//设置环中文字的大小
//        pieChart.setCenterTextTypeface(mTfLight);       //设置PieChart内部圆文字的字体样式
//        pieChart.setCenterText("Test");                 //设置PieChart内部圆文字的内容
//        pieChart.setCenterTextColor(Color.RED);         //设置PieChart内部圆文字的颜色

        //图例设置
        Legend legend = pieChart.getLegend();
        if (showLegend) {
            legend.setEnabled(true);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
            legend.setOrientation(Legend.LegendOrientation.VERTICAL);
            legend.setDrawInside(false);
            legend.setTextColor(getActivity().getResources().getColor(R.color.appContentColor));
            legend.setTextSize(pieChartTextSize);
            legend.setFormToTextSpace(15);
            legend.setForm(Legend.LegendForm.CIRCLE);//正方形，圆形或线
//            legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
        } else {
            legend.setEnabled(false);
        }

        //设置饼图数据
        setPieChartData(pieChart, pieValues);

        pieChart.animateX(1500, Easing.EasingOption.EaseInOutQuad);//数据显示动画
    }

    /**
     * 设置饼图数据源
     */
    private static void setPieChartData(PieChart pieChart, List<PieEntry> dataList) {
        PieDataSet dataSet = new PieDataSet(dataList, "");
        dataSet.setColors(PIE_COLORS);//设置饼块的颜色
        dataSet.setSliceSpace(0f);//设置饼块之间的间隔
        dataSet.setSelectionShift(5f);//设置饼块选中时偏离饼图中心的距离

//        dataSet.setValueLinePart1OffsetPercentage(80f);//数据连接线距图形片内部边界的距离，为百分数
//        dataSet.setValueLinePart1Length(0.3f);
//        dataSet.setValueLinePart2Length(0.4f);
//        dataSet.setValueLineColor(Color.BLUE);//设置连接线的颜色
//        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData pieData = new PieData(dataSet);
        pieData.setDrawValues(false);
//        pieData.setValueFormatter(null);
//        pieData.setValueFormatter(new PercentFormatter());
//        pieData.setValueTextSize(11f);
//        pieData.setValueTextColor(Color.GREEN);

        pieChart.setData(pieData);
        pieChart.highlightValues(null);
        pieChart.invalidate();
    }

    private void getFundCompanyInfo() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("fundcode", ((PositionAllocationActivity) getActivity()).mFundCode);

        HttpRequest.post(HttpRequest.JL_FUND_TEST_ENV_WEB_URL + HttpRequest.POSITION_ALLOCATION, params, this, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                PositionAllocationBean positionAllocationBean = JSON.parseObject(response.body(), PositionAllocationBean.class);
                pieChart.setCenterText(generateCenterSpannableText("资产分布\n" + positionAllocationBean.getPerioddate() + positionAllocationBean.getRpt_cls()));//设置环中的文字
                List<PieEntry> dataList = new ArrayList<PieEntry>();
                 scalelist = positionAllocationBean.getScalelist();

                if (scalelist.size() > 0) {
                    allScrollView.setVisibility(View.VISIBLE);
                    noDataLinearLayout.setVisibility(View.GONE);

                    dataList.add(STOCK_INDEX, new PieEntry(scalelist.get(STOCK_INDEX).getVal_pct(), "股票"));
                    dataList.add(BOND_INDEX, new PieEntry(scalelist.get(BOND_INDEX).getVal_pct(), "债券"));
                    dataList.add(CASH_INDEX, new PieEntry(scalelist.get(CASH_INDEX).getVal_pct(), "现金"));
                    dataList.add(OTHER_INDEX, new PieEntry(scalelist.get(OTHER_INDEX).getVal_pct(), "其他"));
                    setPieChart(pieChart, dataList, false);

                    stockPercentOfPieChartTextView.setText(StringUtil.moneyDecimalFormat2(scalelist.get(STOCK_INDEX).getVal_pct()+"") + "%");
                    bondPercentOfPieChartTextView.setText(StringUtil.moneyDecimalFormat2(scalelist.get(BOND_INDEX).getVal_pct()+"") + "%");
                    cashPercentOfPieChartTextView.setText(StringUtil.moneyDecimalFormat2(scalelist.get(CASH_INDEX).getVal_pct()+"") + "%");
                    otherPercentOfPieChartTextView.setText(StringUtil.moneyDecimalFormat2(scalelist.get(OTHER_INDEX).getVal_pct()+"") + "%");
                    pieChartAndPercentRelativeLayout.setVisibility(View.VISIBLE);

                    stockPercentValueTextView.setText(StringUtil.moneyDecimalFormat2(scalelist.get(STOCK_INDEX).getVal_pct()+"") + "%");
                    bondPercentValueTextView.setText(StringUtil.moneyDecimalFormat2(scalelist.get(BOND_INDEX).getVal_pct()+"") + "%");
                    cashPercentValueTextView.setText(StringUtil.moneyDecimalFormat2(scalelist.get(CASH_INDEX).getVal_pct()+"") + "%");
                    otherPercentValueTextView.setText(StringUtil.moneyDecimalFormat2(scalelist.get(OTHER_INDEX).getVal_pct()+"") + "%");

                    if(scalelist.get(STOCK_INDEX).getStockAssetConf().size()>0){
                        stockDivideLineView.setVisibility(View.VISIBLE);
                    }else {
                        stockDivideLineView.setVisibility(View.GONE);
                    }
                    if(scalelist.get(BOND_INDEX).getBndAssetConf().size()>0){
                        bondDivideLineView.setVisibility(View.VISIBLE);
                    }else {
                        bondDivideLineView.setVisibility(View.GONE);
                    }
                    stockAdapter.upateData(true, scalelist.get(STOCK_INDEX).getStockAssetConf());
                    bondAdapter.upateData(true, scalelist.get(BOND_INDEX).getBndAssetConf());
                } else {
                    allScrollView.setVisibility(View.GONE);
                    noDataLinearLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

}

