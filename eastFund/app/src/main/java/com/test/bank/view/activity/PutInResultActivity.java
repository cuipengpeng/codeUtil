package com.test.bank.view.activity;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.test.bank.R;
import com.test.bank.adapter.TradeRecordListAdapter;
import com.test.bank.base.BaseUIActivity;
import com.test.bank.bean.BaobaoTradeRecordDetailBean;
import com.test.bank.http.HttpRequest;
import com.test.bank.utils.Aes;
import com.test.bank.utils.SPUtil;
import com.test.bank.utils.StringUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Response;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
*/
public class PutInResultActivity extends BaseUIActivity {

    @BindView(R.id.tv_putInResultActivity_tradeType)
    TextView tradeTypeTextView;
    @BindView(R.id.tv_putInResultActivity_tradeDate)
    TextView tradeDateTextView;
    @BindView(R.id.tv_putInResultActivity_tradeAmount)
    TextView tradeAmountTextView;

    @BindView(R.id.rl_putInResultActivity_flowStep1)
    RelativeLayout flowStep1RelativeLayout;
    @BindView(R.id.rl_putInResultActivity_flowStep2)
    RelativeLayout flowStep2RelativeLayout;
    @BindView(R.id.rl_putInResultActivity_flowStep3)
    RelativeLayout flowStep3RelativeLayout;

    @BindView(R.id.tv_putInResultActivity_flowLineUp1)
    View flowLineUp1View;
    @BindView(R.id.tv_putInResultActivity_flowIcon1)
    ImageView flowIcon1ImageView;
    @BindView(R.id.tv_putInResultActivity_flowLineDown1)
    View flowLineDown1View;
    @BindView(R.id.tv_putInResultActivity_flowDesc1)
    TextView flowDesc1TextView;
    @BindView(R.id.tv_putInResultActivity_flowDate1)
    TextView flowDate1TextView;
    @BindView(R.id.tv_putInResultActivity_flowLineUp2)
    View flowLineUp2View;
    @BindView(R.id.tv_putInResultActivity_flowIcon2)
    ImageView flowIcon2ImageView;
    @BindView(R.id.tv_putInResultActivity_flowLineDown2)
    View flowLineDown2View;
    @BindView(R.id.tv_putInResultActivity_flowDesc2)
    TextView flowDesc2TextView;
    @BindView(R.id.tv_putInResultActivity_flowDate2)
    TextView flowDate2TextView;
    @BindView(R.id.tv_putInResultActivity_flowLineUp3)
    View flowLineUp3View;
    @BindView(R.id.tv_putInResultActivity_flowIcon3)
    ImageView flowIcon3ImageView;
    @BindView(R.id.tv_putInResultActivity_flowLineDown3)
    View flowLineDown3View;
    @BindView(R.id.tv_putInResultActivity_flowDesc3)
    TextView flowDesc3TextView;
    @BindView(R.id.tv_putInResultActivity_flowDate3)
    TextView flowDate3TextView;

    @BindView(R.id.tv_putInResultActivity_contactCustomerService)
    TextView contactCustomerServiceTextView;
    @BindView(R.id.v_putInResultActivity_bottomDivider)
    View bottomDividerView;


    private String tradeRecordNumber;
    public static final int PUT_IN_RESULT_SUCCESS = 2001;
    public static final int PUT_IN_RESULT_FAIL = 2002;
    public static final int GET_OUT_RESULT_QUICK_GET_OUT = 2003;
    public static final int GET_OUT_RESULT_COMMON_GET_OUT = 2004;
    public static final int TRADE_RECORD_STATUS_PUT_IN_SUCCESS = 2005;
    public static final int TRADE_RECORD_STATUS_PUT_IN_FAIL = 2006;
    public static final int TRADE_RECORD_STATUS_QUICK_GET_OUT = 2007;
    public static final int TRADE_RECORD_STATUS_COMMON_GET_OUT = 2008;
    public static final int TRADE_RECORD_STATUS_COMMON_GET_OUT_FAIL = 2009;
    public static final int TRADE_RECORD_STATUS_COMMON_GET_OUT_CLOSE = 2010;
    public static final String KEY_OF_TRADE_TYPE_AND_STATUS = "tradeTypeAndStatusKey";
    private int currentTradeTypeOrStatus = -1;
    private BaobaoTradeRecordDetailBean  baobaoTradeRecordDetailBean = new BaobaoTradeRecordDetailBean();

//    @OnClick({R.id.})
//    public void onItemClick(View v) {
//        Intent intent = new Intent();
//        switch (v.getId()) {
//            case R.id.tv_:
//                break;
//        }
//    }


    @Override
    protected String getPageTitle() {
        return "";
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_putin_result;
    }

    @Override
    protected void initPageData() {
        tradeRecordNumber = getIntent().getStringExtra(TradeRecordListAdapter.KEY_OF_TRADE_RECORD_NUMBER);
        currentTradeTypeOrStatus = getIntent().getIntExtra(KEY_OF_TRADE_TYPE_AND_STATUS, -1);

        flowStep1RelativeLayout.setVisibility(View.GONE);
        flowStep2RelativeLayout.setVisibility(View.GONE);
        flowStep3RelativeLayout.setVisibility(View.GONE);
        bottomDividerView.setVisibility(View.GONE);
        contactCustomerServiceTextView.setVisibility(View.GONE);
        switchCorrespondingView(currentTradeTypeOrStatus);

        getBaobaoBuyRecordDetail();
    }

    private void switchCorrespondingView(int currentStatus) {
        switch (currentStatus){
            case PUT_IN_RESULT_FAIL:
            case TRADE_RECORD_STATUS_COMMON_GET_OUT_CLOSE:
            case GET_OUT_RESULT_QUICK_GET_OUT:
            case TRADE_RECORD_STATUS_QUICK_GET_OUT:
                flowStep1RelativeLayout.setVisibility(View.VISIBLE);
                flowStep2RelativeLayout.setVisibility(View.GONE);
                flowStep3RelativeLayout.setVisibility(View.GONE);
                flowLineUp1View.setVisibility(View.INVISIBLE);
                flowLineDown1View.setVisibility(View.INVISIBLE);
                break;
            case GET_OUT_RESULT_COMMON_GET_OUT:
            case TRADE_RECORD_STATUS_COMMON_GET_OUT:
            case TRADE_RECORD_STATUS_COMMON_GET_OUT_FAIL:
                flowStep1RelativeLayout.setVisibility(View.VISIBLE);
                flowStep2RelativeLayout.setVisibility(View.GONE);
                flowStep3RelativeLayout.setVisibility(View.VISIBLE);
                flowLineUp1View.setVisibility(View.INVISIBLE);
                flowLineDown1View.setVisibility(View.VISIBLE);
                flowLineDown1View.setBackgroundColor(getResources().getColor(R.color.appViewFullTextColor));
                flowLineUp3View.setVisibility(View.VISIBLE);
                flowLineDown3View.setVisibility(View.INVISIBLE);
                break;
            case PUT_IN_RESULT_SUCCESS:
            case TRADE_RECORD_STATUS_PUT_IN_SUCCESS:
                flowStep1RelativeLayout.setVisibility(View.VISIBLE);
                flowStep2RelativeLayout.setVisibility(View.VISIBLE);
                flowStep3RelativeLayout.setVisibility(View.VISIBLE);
                flowLineUp1View.setVisibility(View.INVISIBLE);
                flowLineDown1View.setVisibility(View.VISIBLE);
                flowLineDown1View.setBackgroundColor(getResources().getColor(R.color.appViewFullTextColor));
                flowLineUp3View.setVisibility(View.VISIBLE);
                flowLineDown3View.setVisibility(View.INVISIBLE);
                break;
            case TRADE_RECORD_STATUS_PUT_IN_FAIL:
                flowStep1RelativeLayout.setVisibility(View.VISIBLE);
                flowStep2RelativeLayout.setVisibility(View.VISIBLE);
                flowStep3RelativeLayout.setVisibility(View.VISIBLE);
                flowLineUp1View.setVisibility(View.INVISIBLE);
                flowLineDown1View.setVisibility(View.VISIBLE);
                flowLineDown1View.setBackgroundColor(getResources().getColor(R.color.appViewFullTextColor));
                flowLineUp3View.setVisibility(View.VISIBLE);
                flowLineDown3View.setVisibility(View.INVISIBLE);
                flowDate3TextView.setVisibility(View.GONE);
                contactCustomerServiceTextView.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void setTitleRightText() {
        baseBackRelativeLayout.setVisibility(View.GONE);
        baseRightMenuTextView.setVisibility(View.VISIBLE);
        baseRightMenuTextView.setText("完成");
        baseRightMenuTextView.setTextColor(getResources().getColor(R.color.appViewFullTextColor));
        baseRightMenuTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getBaobaoBuyRecordDetail() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", SPUtil.getInstance().getToken());
        params.put("tradeno", Aes.encryptAES(tradeRecordNumber));

        HttpRequest.post(false, this, HttpRequest.APP_INTERFACE_WEB_URL + HttpRequest.BAOBAO_BUY_RECORD_DETAIL, params, new HttpRequest.HttpResponseCallBack() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                baobaoTradeRecordDetailBean = JSON.parseObject(response.body(), BaobaoTradeRecordDetailBean.class);

                //转换交易记录状态
                if(currentTradeTypeOrStatus == -1){
                    switch (baobaoTradeRecordDetailBean.getTradetype()) {
                        case 1:
                            if (baobaoTradeRecordDetailBean.getTradestatus() == 0 || baobaoTradeRecordDetailBean.getTradestatus() == 1) {
                                currentTradeTypeOrStatus  = TRADE_RECORD_STATUS_COMMON_GET_OUT;
                            } else if (baobaoTradeRecordDetailBean.getTradestatus() == 3) {
                                currentTradeTypeOrStatus  = TRADE_RECORD_STATUS_COMMON_GET_OUT_FAIL;
                            } else if (baobaoTradeRecordDetailBean.getTradestatus() == 4) {
                                currentTradeTypeOrStatus  = TRADE_RECORD_STATUS_COMMON_GET_OUT_CLOSE;
                            }
                            break;
                        case 2:
                            currentTradeTypeOrStatus  = TRADE_RECORD_STATUS_QUICK_GET_OUT;
                            break;
                        case 3:
                            if (baobaoTradeRecordDetailBean.getPayStatus() == 0 || baobaoTradeRecordDetailBean.getPayStatus() == 2) {
                                currentTradeTypeOrStatus  = TRADE_RECORD_STATUS_PUT_IN_SUCCESS;
                            } else if (baobaoTradeRecordDetailBean.getPayStatus() == 1) {
                                currentTradeTypeOrStatus  = TRADE_RECORD_STATUS_PUT_IN_FAIL;
                            }
                            break;
                    }

                    //显示相对应的view
                    switchCorrespondingView(currentTradeTypeOrStatus);
                }

                //填充顶部item数据
                tradeTypeTextView.setText(getTradeStatusText(baobaoTradeRecordDetailBean.getTradetype(), baobaoTradeRecordDetailBean.getPayStatus()));
                tradeDateTextView.setText(baobaoTradeRecordDetailBean.getTradedate());
                switch (currentTradeTypeOrStatus){
                    case PUT_IN_RESULT_FAIL:
                    case PUT_IN_RESULT_SUCCESS:
                    case TRADE_RECORD_STATUS_PUT_IN_SUCCESS:
                    case TRADE_RECORD_STATUS_PUT_IN_FAIL:
                        tradeAmountTextView.setText("+"+ StringUtil.moneyDecimalFormat2(baobaoTradeRecordDetailBean.getTrademoney()+""));
                        break;
                    case GET_OUT_RESULT_COMMON_GET_OUT:
                    case GET_OUT_RESULT_QUICK_GET_OUT:
                    case TRADE_RECORD_STATUS_COMMON_GET_OUT:
                    case TRADE_RECORD_STATUS_COMMON_GET_OUT_FAIL:
                    case TRADE_RECORD_STATUS_COMMON_GET_OUT_CLOSE:
                    case TRADE_RECORD_STATUS_QUICK_GET_OUT:
                        tradeAmountTextView.setText("-"+ StringUtil.moneyDecimalFormat2(baobaoTradeRecordDetailBean.getTradeshare()+""));
                        break;
                }

                //设置进度条直线及进度条图标数据
                if(baobaoTradeRecordDetailBean.getTradeincomedate_flag()){
                    flowLineUp2View.setBackgroundColor(getResources().getColor(R.color.appViewFullTextColor));
                    flowLineDown2View.setBackgroundColor(getResources().getColor(R.color.appViewFullTextColor));
                    flowDesc2TextView.setTextColor(getResources().getColor(R.color.appViewFullTextColor));
                    flowIcon2ImageView.setBackgroundResource(R.mipmap.put_in_result_activity_flow_step2_selected);
                }else {
                    flowLineUp2View.setBackgroundColor(getResources().getColor(R.color.appDividerLineColor_1dp));
                    flowLineDown2View.setBackgroundColor(getResources().getColor(R.color.appDividerLineColor_1dp));
                    flowDesc2TextView.setTextColor(getResources().getColor(R.color.appTitleColor));
                    flowIcon2ImageView.setBackgroundResource(R.mipmap.put_in_result_activity_flow_step2_unselected);
                }

                if(baobaoTradeRecordDetailBean.getTradetoacctdate_flag()){
                    flowLineUp3View.setBackgroundColor(getResources().getColor(R.color.appViewFullTextColor));
                    flowDesc3TextView.setTextColor(getResources().getColor(R.color.appViewFullTextColor));
                    flowIcon3ImageView.setBackgroundResource(R.mipmap.put_in_result_activity_flow_step3_selected);
                }else {
                    flowLineUp3View.setBackgroundColor(getResources().getColor(R.color.appDividerLineColor_1dp));
                    flowDesc3TextView.setTextColor(getResources().getColor(R.color.appTitleColor));
                    flowIcon3ImageView.setBackgroundResource(R.mipmap.put_in_result_activity_flow_step3_unselected);
                }

                if(currentTradeTypeOrStatus == TRADE_RECORD_STATUS_PUT_IN_FAIL){
                    flowLineUp2View.setBackgroundColor(getResources().getColor(R.color.appViewFullTextColor));
                    flowLineDown2View.setBackgroundColor(getResources().getColor(R.color.appDividerLineColor_1dp));
                    flowDesc2TextView.setTextColor(getResources().getColor(R.color.appRedColor));
                    flowIcon2ImageView.setBackgroundResource(R.mipmap.put_in_result_activity_fail);

                    flowLineUp3View.setBackgroundColor(getResources().getColor(R.color.appDividerLineColor_1dp));
                    flowDesc3TextView.setTextColor(getResources().getColor(R.color.appTitleColor));
                    flowIcon3ImageView.setBackgroundResource(R.mipmap.put_in_result_activity_flow_step3_unselected);
                }

                //设置进度条文本数据
                bottomDividerView.setVisibility(View.VISIBLE);
//                flowDate1TextView.setText(baobaoTradeRecordDetailBean.getTradedate().substring(5));
                flowDate1TextView.setText(baobaoTradeRecordDetailBean.getTradedate_short());
                switch (currentTradeTypeOrStatus){
                    case PUT_IN_RESULT_FAIL:
                        setPutInFailTextView();
                        break;
                    case TRADE_RECORD_STATUS_COMMON_GET_OUT_CLOSE:
                        baseTitleTextView.setText("取出结果");
                        flowDesc1TextView.setText("普通取出失败");
                        flowDesc1TextView.setTextColor(getResources().getColor(R.color.appRedColor));
                        flowIcon1ImageView.setBackgroundResource(R.mipmap.put_in_result_activity_fail);
                        break;
                    case GET_OUT_RESULT_QUICK_GET_OUT:
                        setTitleRightText();
                        setQuickGetOutlTextView();
                        break;
                    case TRADE_RECORD_STATUS_PUT_IN_FAIL:
                        baseTitleTextView.setText("存入结果");
                        flowDesc1TextView.setText("提交申请");
                        flowDesc2TextView.setText("确认失败");
                        flowDate2TextView.setText(baobaoTradeRecordDetailBean.getTradeaffirmdate());
                        flowDesc3TextView.setText("无收益");
                        bottomDividerView.setVisibility(View.GONE);
                        break;
                    case TRADE_RECORD_STATUS_QUICK_GET_OUT:
                        setQuickGetOutlTextView();
                        break;
                    case TRADE_RECORD_STATUS_COMMON_GET_OUT:
                        setCommonGetOutTextView();
                        break;
                    case TRADE_RECORD_STATUS_COMMON_GET_OUT_FAIL:
                        baseTitleTextView.setText("取出结果");
                        flowDesc1TextView.setText("提交申请");
                        flowLineUp3View.setBackgroundColor(getResources().getColor(R.color.appViewFullTextColor));
                        flowIcon3ImageView.setBackgroundResource(R.mipmap.put_in_result_activity_fail);
                        flowDesc3TextView.setText("确认失败，未取出");
                        flowDesc3TextView.setTextColor(getResources().getColor(R.color.appRedColor));
                        break;
                    case GET_OUT_RESULT_COMMON_GET_OUT:
                        setTitleRightText();
                        setCommonGetOutTextView();
                        break;
                    case TRADE_RECORD_STATUS_PUT_IN_SUCCESS:
                        setPutInSuccessTextView();
                        break;
                    case PUT_IN_RESULT_SUCCESS:
                        setTitleRightText();
                        setPutInSuccessTextView();
                        break;
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void setPutInSuccessTextView() {
        baseTitleTextView.setText("存入结果");
        flowDesc1TextView.setText("提交申请");
        flowDesc2TextView.setText("开始计算收益");
        flowDesc3TextView.setText("收益到账");
        flowDate2TextView.setText(baobaoTradeRecordDetailBean.getTradeaffirmdate());
        flowDate3TextView.setText("预计"+baobaoTradeRecordDetailBean.getTradeincomedate());
    }

    private void setCommonGetOutTextView() {
        baseTitleTextView.setText("取出结果");
        flowDesc1TextView.setText("提交申请");
        flowDesc3TextView.setText("到账");
        flowDate3TextView.setText("预计"+baobaoTradeRecordDetailBean.getTradeaffirmdate());
    }

    private void setQuickGetOutlTextView() {
        if(baobaoTradeRecordDetailBean.getFastpayment_flag()){
            flowDesc1TextView.setText("到账");
            flowIcon1ImageView.setBackgroundResource(R.mipmap.put_in_result_activity_flow_step1_selected);
        }else {
            flowDesc1TextView.setText("预计两小时内到账");
            flowIcon1ImageView.setBackgroundResource(R.mipmap.put_in_result_activity_flow_step3_selected);
        }
        baseTitleTextView.setText("取出结果");
        flowDate1TextView.setVisibility(View.GONE);
        bottomDividerView.setVisibility(View.GONE);
    }

    private void setPutInFailTextView() {
        baseTitleTextView.setText("存入结果");
        flowDesc1TextView.setText("存入失败");
        flowDate1TextView.setText("扣款失败");
        flowDesc1TextView.setTextColor(getResources().getColor(R.color.appRedColor));
        flowIcon1ImageView.setBackgroundResource(R.mipmap.put_in_result_activity_fail);
    }


    public  String getTradeStatusText(int  tradeType, int payStatus){
        String tradeStatus = "";
        switch (tradeType){
            case 1:
                tradeStatus = "普通取出";
                break;
            case 2:
                tradeStatus = "快速取出";
                break;
            case 3:
                if(payStatus==0|| payStatus == 2){
                    tradeStatus = "存入";
                }else if(payStatus == 1){
                     tradeStatus = "存入失败";
                }
                break;
        }

        return tradeStatus;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean haveBackArrow = true;
        switch (currentTradeTypeOrStatus){
            case PUT_IN_RESULT_FAIL:
            case PUT_IN_RESULT_SUCCESS:
            case GET_OUT_RESULT_COMMON_GET_OUT:
            case GET_OUT_RESULT_QUICK_GET_OUT:
                haveBackArrow= false;
                break;
        }

        if(haveBackArrow){
            return super.onKeyDown(keyCode, event);
        }else {
            return true ;
        }
    }

    public static void open(Context context, String tradeNumber){
        Intent intent = new Intent(context, PutInResultActivity.class);
        intent.putExtra(TradeRecordListAdapter.KEY_OF_TRADE_RECORD_NUMBER, tradeNumber);
        context.startActivity(intent);
    }
}
