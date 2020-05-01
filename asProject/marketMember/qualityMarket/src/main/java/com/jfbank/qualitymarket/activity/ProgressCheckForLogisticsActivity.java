package com.jfbank.qualitymarket.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.adapter.LogisticsDetailseAdapter;
import com.jfbank.qualitymarket.base.BaseActivity;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
import com.jfbank.qualitymarket.model.GoodsRejectedBean;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.DisplayBigImageUtil;
import com.jfbank.qualitymarket.util.LogUtil;
import com.jfbank.qualitymarket.util.UserUtils;
import com.jfbank.qualitymarket.widget.LoadingAlertDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/*
* 描    述：<br>
*   本页面适用于以下这些状态：
    如果identification为1即退货: 3-退货受理中 4-退货完成 5-退款完成, 如果identification为2即换货:  3-待收取换货, 4-换货完成,  6-待商户发货,7-待买家收货
* 作    者：崔朋朋<br>
* 时    间：2017/4/20<br>
*
    如果identification为1（即退货）
    订单状态 0-审核退货中, 1-待寄送商品, 2-拒绝退货  3-退货受理中 4-退货受理中 5-退款完成，6-已取消
    如果identification为2（即换货）
    订单状态 0-换货审核中, 1-待寄送换货, 2-拒绝换货, 3-待收取换货, 4-换货完成,5-已取消,6-待商户发货,7-待买家收货
*/
public class ProgressCheckForLogisticsActivity extends BaseActivity {
    public static final String TAG = ProgressCheckForLogisticsActivity.class.getName();

    @InjectView(R.id.rl_title)
    RelativeLayout rlTitle;
    @InjectView(R.id.tv_title)
    TextView tvTitle;
    @InjectView(R.id.iv_back)
    ImageView ivBack;

    //收货人信息详情
    @InjectView(R.id.v_progressCheckForLogisticsActivity_section01Border)
    View section01BorderView;
    @InjectView(R.id.v_progressCheckForLogisticsActivity_section03Border)
    View section03BorderView;
    @InjectView(R.id.tv_progressCheckForLogisticsActivity_recipientNameValue)
    TextView recipientNameTextView;
    @InjectView(R.id.tv_progressCheckForLogisticsActivity_phoneNumValue)
    TextView phoneNumTextView;
    @InjectView(R.id.tv_progressCheckForLogisticsActivity_postcodeKey)
    TextView postcodeKeyTextView;
    @InjectView(R.id.tv_progressCheckForLogisticsActivity_postcodeValue)
    TextView postcodeTextView;
    @InjectView(R.id.tv_progressCheckForLogisticsActivity_addressValue)
    TextView addressTextView;

    //物流公司详情
    @InjectView(R.id.tv_progressCheckForLogisticsActivity_logisticsCompanyValue)
    TextView logisticsCompanyTextView;
    @InjectView(R.id.tv_progressCheckForLogisticsActivity_trackingNumValue)
    TextView trackingNumTextView;
    @InjectView(R.id.tv_progressCheckForLogisticsActivity_transportationCostKey)
    TextView transportationCostKeyInLogisticsPageTextView;
    @InjectView(R.id.tv_progressCheckForLogisticsActivity_transportationCostValue)
    TextView transportationCostValueInLogisticsPageTextView;
    @InjectView(R.id.tv_progressCheckForLogisticsActivity_deliveryTimeKey)
    TextView deliveryTimeKeyTextView;
    @InjectView(R.id.tv_progressCheckForLogisticsActivity_deliveryTimeValue)
    TextView deliveryTimeTextView;
    @InjectView(R.id.tv_progressCheckForGoodsRejectedActivity_applyTimeValue)
    TextView applyTimeValueTextView;
    @InjectView(R.id.tv_progressCheckForLogisticsActivity_section01TitleKey)
    TextView section01TitleKeyTextView;
    @InjectView(R.id.tv_progressCheckForLogisticsActivity_section02TitleKey)
    TextView section02TitleKeyTextView;

    //退货商品详情
    @InjectView(R.id.tv_progressCheckForGoodsRejectedActivity_afterSaleTypeValue)
    TextView afterSaleTypeValueTextView;
    @InjectView(R.id.tv_progressCheckForGoodsRejectedActivity_orderNumKey)
    TextView orderNumKeyTextView;
    @InjectView(R.id.tv_progressCheckForGoodsRejectedActivity_orderNumValue)
    TextView orderNumValueTextView;
    @InjectView(R.id.tv_progressCheckForGoodsRejectedActivity_transportationCostValue)
    TextView transportationCostValueInGoodsRejectedPageTextView;
    @InjectView(R.id.tv_progressCheckForGoodsRejectedActivity_returnedMoneyKey)
    TextView returnedMoneyKeyTextView;
    @InjectView(R.id.tv_progressCheckForGoodsRejectedActivity_returnedMoneyValue)
    TextView returnedMoneyValueTextView;
    @InjectView(R.id.tv_progressCheckForLogisticsActivity_returnedMoneyAmountInSection1Value)
    TextView returnedMoneyAmountInSection1ValueTextView;
    @InjectView(R.id.tv_progressCheckForLogisticsActivity_timeOfReturnedMoneyValue)
    TextView timeOfReturnedMoneyValueTextView;
    @InjectView(R.id.tv_progressCheckForGoodsRejectedActivity_commentOfGoodsRejectedKey)
    TextView commentOfGoodsRejectedKeyTextView;
    @InjectView(R.id.tv_progressCheckForGoodsRejectedActivity_commentOfGoodsRejectedValue)
    TextView commentOfGoodsRejectedValueTextView;
    @InjectView(R.id.tv_progressCheckForGoodsRejectedActivity_reasonOfGoodsRejectedKey)
    TextView reasonOfGoodsRejectedKeyTextView;
    @InjectView(R.id.tv_progressCheckForGoodsRejectedActivity_reasonOfGoodsRejectedValue)
    TextView reasonOfGoodsRejectedValueTextView;
    @InjectView(R.id.iv_progressCheckForGoodsRejectedActivity_imageOfGoodsRejected01)
    ImageView imageOfGoodsRejected01ImageView;
    @InjectView(R.id.iv_progressCheckForGoodsRejectedActivity_imageOfGoodsRejected02)
    ImageView imageOfGoodsRejected02ImageView;
    @InjectView(R.id.iv_progressCheckForGoodsRejectedActivity_imageOfGoodsRejected03)
    ImageView imageOfGoodsRejected03ImageView;
    @InjectView(R.id.ll_progressCheckForGoodsRejectedActivity_threeImagesOfGoodsRejected)
    LinearLayout threeImagesOfGoodsRejectedLinearLayout;
    @InjectView(R.id.rl_progressCheckForGoodsRejectedActivity_threeImagesAndApplyTime)
    RelativeLayout threeImagesAndApplyTimeRelativeLayout;

    //物流信息详情
    @InjectView(R.id.tv_progressCheckForLogisticsActivity_logisticsInfoKey)
    TextView logisticsInfoKeyTextView;
    @InjectView(R.id.lv_progressCheckForLogisticsActivity_logisticsInfoValue)
    ListView logisticsInfoValueListView;
    private LogisticsDetailseAdapter mAdapter;

    @InjectView(R.id.sv_progressCheckForLogisticsActivity_allDataView)
    ScrollView allDataViewScrollView;
    @InjectView(R.id.ll_progressCheckForLogisticsActivity_netwrokErrorView)
    LinearLayout netwrokErrorViewLinearLayout;

    @InjectView(R.id.rl_progressCheckForLogisticsActivity_recipientInfo)
    RelativeLayout recipientInfoRelativeLayout;
    @InjectView(R.id.rl_progressCheckForLogisticsActivity_returnMoneyInfo)
    RelativeLayout returnMoneyInfoRelativeLayout;
    @InjectView(R.id.rl_progressCheckForGoodsRejectedActivity_goodsRejectedDetailInfo)
    RelativeLayout goodsRejectedDetailInfoRelativeLayout;
    @InjectView(R.id.rl_progressCheckForLogisticsActivity_logisticsDetailInfo)
    RelativeLayout logisticsDetailInfoRelativeLayout;
    @InjectView(R.id.ll_progressCheckForLogisticsActivity_logisticsInfo)
    LinearLayout logisticsInfoLinearLayout;

    /**网编请求时加载框*/
    private LoadingAlertDialog mDialog;

    private  GoodsRejectedBean goodsRejectedBean = new GoodsRejectedBean();
    private String identification = "";
    private String orderId = "";
    private String returnedGoodsOrderId = "";

    @Override
    protected String getPageName() {
        return "退换货进度查询-查看物流";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_check_for_logistics);
        ButterKnife.inject(this);

        CommonUtils.setTitle(this,rlTitle);
        tvTitle.setText("进度查询");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            orderId = bundle.getString(ConstantsUtil.EXTRA_ORDERID, "");
            identification = bundle.getString(ConstantsUtil.EXTRA_IDENTIFICATION, "");
            returnedGoodsOrderId = bundle.getString(ConstantsUtil.EXTRA_RETURNEDGOODSORDERID, "");
        }

        //默认隐藏部分view
        returnMoneyInfoRelativeLayout.setVisibility(View.GONE);
        goodsRejectedDetailInfoRelativeLayout.setVisibility(View.GONE);
        logisticsInfoLinearLayout.setVisibility(View.GONE);
        section03BorderView.setVisibility(View.GONE);
        threeImagesOfGoodsRejectedLinearLayout.setVisibility(View.INVISIBLE);

        allDataViewScrollView.setVisibility(View.GONE);
        netwrokErrorViewLinearLayout.setVisibility(View.GONE);

        mAdapter= new LogisticsDetailseAdapter<GoodsRejectedBean.Data>(this, new ArrayList<GoodsRejectedBean.Data>());
        logisticsInfoValueListView.setAdapter(mAdapter);
        queryGoodsRejectedProgress(identification, orderId, returnedGoodsOrderId);

    }

    /**
     * 退换货进度查询
     */
    public void queryGoodsRejectedProgress(String identification, String orderId, String returnedGoodsOrderId) {
        if (null == mDialog) {
            mDialog = new LoadingAlertDialog(this);
        }
        mDialog.show(ConstantsUtil.NETWORK_REQUEST_IN);


        Map<String,String> params = new HashMap<>();
        params.put("uid", AppContext.user.getUid());
        params.put("token", AppContext.user.getToken());
        params.put("identification", identification);
        params.put("orderId", orderId);
        params.put("returnedGoodsOrderId", returnedGoodsOrderId);

        HttpRequest.post(mContext, HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.GOODS_REJECTED_QUERY_SCHEDULE_DETAIL, params,
                new AsyncResponseCallBack() {

                    @Override
                    public void onResult(String arg2) {
                        if (mDialog.isShowing()) {
                            mDialog.dismiss();
                        }


                        String jsonStr = new String(arg2);
                        LogUtil.printLog("退换货进度查询：" + jsonStr);
                        JSONObject jsonObject = JSON.parseObject(jsonStr);

                        if (ConstantsUtil.RESPONSE_SUCCEED == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                            goodsRejectedBean = JSON.parseObject(jsonObject.getJSONObject(ConstantsUtil.RESPONSE_DATA_JSON_ARRAY_FIELD_NAME).toString(), GoodsRejectedBean.class);
                            GoodsRejectedBean.QualityRefundOrderMap qualityRefundOrderMap = goodsRejectedBean.getQualityRefundOrderMap();

                            allDataViewScrollView.setVisibility(View.VISIBLE);
                            netwrokErrorViewLinearLayout.setVisibility(View.GONE);
                            ArrayList logisticsList = qualityRefundOrderMap.getData();
                            if(logisticsList != null && logisticsList.size()>0){
                                mAdapter.updateData(logisticsList);
                                logisticsInfoLinearLayout.setVisibility(View.VISIBLE);
                                section03BorderView.setVisibility(View.VISIBLE);
                            }


//                            如果identification为1（即退货）
//                            订单状态 0-审核退货中, 1-待寄送商品, 2-拒绝退货  3-退货受理中 4-退货受理中 5-退款完成，6-已取消
//                            如果identification为2（即换货）
//                            订单状态 0-换货审核中, 1-待寄送换货, 2-拒绝换货, 3-待收取换货, 4-换货完成,5-已取消,6-待商户发货,7-待买家收货

                            String typeOfOrder = "";
                            if (ProgressCheckForGoodsRejectedActivity.TYPE_OF_GOODS_REJECTED == qualityRefundOrderMap.getIdentification()) {
                                typeOfOrder = "退货";
                                adjustrRejectedOrExchangeGoodsText(typeOfOrder);
                                switch (qualityRefundOrderMap.getOrderStatus()) {
                                    case ProgressCheckForGoodsRejectedActivity.GOODS_REJECTED_STATUS_WAIT_FOR_VERIFYING:
                                        break;
                                    case ProgressCheckForGoodsRejectedActivity.GOODS_REJECTED_STATUS_WAIT_FOR_SEND_GOODS:
                                        break;
                                    case ProgressCheckForGoodsRejectedActivity.GOODS_REJECTED_STATUS_REFUSED:
                                        break;
                                    case ProgressCheckForGoodsRejectedActivity.GOODS_REJECTED_STATUS_ACCEPTING:
                                    case ProgressCheckForGoodsRejectedActivity.GOODS_REJECTED_STATUS_RETURNED_GOODS_FINISHED:
                                        showInAcceptingForGoodsRejectedOrExchangeGoods(qualityRefundOrderMap);
                                        break;
                                    case ProgressCheckForGoodsRejectedActivity.GOODS_REJECTED_STATUS_RETURNED_MONEY_FINISHED:
                                        showSuccessForGoodsRejectedOrExchangeView(qualityRefundOrderMap, typeOfOrder);

                                        section01BorderView.setVisibility(View.VISIBLE);
                                        section01TitleKeyTextView.setVisibility(View.VISIBLE);
                                        section01TitleKeyTextView.setText("退货成功");
                                        section02TitleKeyTextView.setVisibility(View.GONE);
                                        returnMoneyInfoRelativeLayout.setVisibility(View.VISIBLE);
                                        returnedMoneyAmountInSection1ValueTextView.setText(qualityRefundOrderMap.getRefundAmount() + "元");
                                        returnedMoneyValueTextView.setText(qualityRefundOrderMap.getRefundAmount() + "元");
                                        timeOfReturnedMoneyValueTextView.setText(qualityRefundOrderMap.getRefundTime());
                                        break;
                                    case ProgressCheckForGoodsRejectedActivity.GOODS_REJECTED_STATUS_CANCEL_APPLY:
                                        break;
                                }
                            } else if (ProgressCheckForGoodsRejectedActivity.TYPE_OF_EXCHANGE_GOODS == qualityRefundOrderMap.getIdentification()) {
                                typeOfOrder = "换货";
                                returnedMoneyKeyTextView.setVisibility(View.GONE);
                                returnedMoneyValueTextView.setVisibility(View.GONE);
                                adjustrRejectedOrExchangeGoodsText(typeOfOrder);

                                switch (qualityRefundOrderMap.getOrderStatus()) {
                                    case ProgressCheckForGoodsRejectedActivity.EXCHANGE_GOODS_STATUS_WAIT_FOR_VERIFYING:
                                        break;
                                    case ProgressCheckForGoodsRejectedActivity.EXCHANGE_GOODS_STATUS_WAIT_FOR_SEND_GOODS:
                                        break;
                                    case ProgressCheckForGoodsRejectedActivity.EXCHANGE_GOODS_STATUS_REFUSED:
                                        break;
                                    case ProgressCheckForGoodsRejectedActivity.EXCHANGE_GOODS_STATUS_WAIT_FOR_SELLER_RECEIVE_GOODS:
                                        showInAcceptingForGoodsRejectedOrExchangeGoods(qualityRefundOrderMap);
                                        break;
                                    case ProgressCheckForGoodsRejectedActivity.EXCHANGE_GOODS_STATUS_FINISHED:
                                    case ProgressCheckForGoodsRejectedActivity.EXCHANGE_GOODS_STATUS_WAIT_FOR_BUYER_RECEIVE_GOODS:
                                        showSellerSendGoodsView(qualityRefundOrderMap);

                                        section01TitleKeyTextView.setText("发新品给买家");
                                        postcodeKeyTextView.setVisibility(View.GONE);
                                        postcodeTextView.setVisibility(View.GONE);
                                        transportationCostKeyInLogisticsPageTextView.setVisibility(View.GONE);
                                        transportationCostValueInLogisticsPageTextView.setVisibility(View.GONE);
                                        deliveryTimeKeyTextView.setVisibility(View.VISIBLE);
                                        deliveryTimeTextView.setVisibility(View.VISIBLE);
                                        deliveryTimeTextView.setText(qualityRefundOrderMap.getDeliveryDate());
                                        threeImagesAndApplyTimeRelativeLayout.setVisibility(View.GONE);

                                        break;
                                    case ProgressCheckForGoodsRejectedActivity.EXCHANGE_GOODS_STATUS_CANCEL_APPLY:
                                        break;
                                    case ProgressCheckForGoodsRejectedActivity.EXCHANGE_GOODS_STATUS_WAIT_FOR_SELLER_SEND_GOODS:
                                        showSuccessForGoodsRejectedOrExchangeView(qualityRefundOrderMap,typeOfOrder);
                                        section02TitleKeyTextView.setVisibility(View.VISIBLE);
                                        section01TitleKeyTextView.setVisibility(View.GONE);
                                        section01BorderView.setVisibility(View.GONE);
                                        returnMoneyInfoRelativeLayout.setVisibility(View.GONE);
                                        break;
                                }
                            }


                            if (qualityRefundOrderMap.getPictureArray() != null &&
                                    (qualityRefundOrderMap.getOrderStatus() == ProgressCheckForGoodsRejectedActivity.GOODS_REJECTED_STATUS_ACCEPTING ||
                                            qualityRefundOrderMap.getOrderStatus() == ProgressCheckForGoodsRejectedActivity.GOODS_REJECTED_STATUS_RETURNED_GOODS_FINISHED ||
                                            qualityRefundOrderMap.getOrderStatus() == ProgressCheckForGoodsRejectedActivity.GOODS_REJECTED_STATUS_RETURNED_MONEY_FINISHED ||
                                            qualityRefundOrderMap.getOrderStatus() == ProgressCheckForGoodsRejectedActivity.EXCHANGE_GOODS_STATUS_WAIT_FOR_SELLER_RECEIVE_GOODS ||
                                            qualityRefundOrderMap.getOrderStatus() == ProgressCheckForGoodsRejectedActivity.EXCHANGE_GOODS_STATUS_WAIT_FOR_SELLER_SEND_GOODS)) {
                                switch (qualityRefundOrderMap.getPictureArray().size()) {
                                    case 1:
                                        showAllThreeGoodsImages();
                                        imageOfGoodsRejected02ImageView.setVisibility(View.GONE);
                                        imageOfGoodsRejected03ImageView.setVisibility(View.GONE);
                                        Picasso.with(ProgressCheckForLogisticsActivity.this).load(qualityRefundOrderMap.getPictureArray().get(0)).into(imageOfGoodsRejected01ImageView);
                                        break;
                                    case 2:
                                        showAllThreeGoodsImages();
                                        imageOfGoodsRejected03ImageView.setVisibility(View.GONE);
                                        Picasso.with(ProgressCheckForLogisticsActivity.this).load(qualityRefundOrderMap.getPictureArray().get(0)).into(imageOfGoodsRejected01ImageView);
                                        Picasso.with(ProgressCheckForLogisticsActivity.this).load(qualityRefundOrderMap.getPictureArray().get(1)).into(imageOfGoodsRejected02ImageView);
                                        break;
                                    case 3:
                                        showAllThreeGoodsImages();
                                        Picasso.with(ProgressCheckForLogisticsActivity.this).load(qualityRefundOrderMap.getPictureArray().get(0)).into(imageOfGoodsRejected01ImageView);
                                        Picasso.with(ProgressCheckForLogisticsActivity.this).load(qualityRefundOrderMap.getPictureArray().get(1)).into(imageOfGoodsRejected02ImageView);
                                        Picasso.with(ProgressCheckForLogisticsActivity.this).load(qualityRefundOrderMap.getPictureArray().get(2)).into(imageOfGoodsRejected03ImageView);
                                        break;
                                }
                            }


                        } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                            UserUtils.tokenFailDialog(mContext, jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), TAG);
                        } else {
                            Toast.makeText(ProgressCheckForLogisticsActivity.this, jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailed(String path, String msg) {
                        if (mDialog.isShowing()) {
                            mDialog.dismiss();
                        }

                        allDataViewScrollView.setVisibility(View.GONE);
                        netwrokErrorViewLinearLayout.setVisibility(View.VISIBLE);

                        // Toast.makeText(getActivity(),
                        // ConstantsUtil.FAIL_TO_CONNECT_SERVER+"查询信用额度失败",
                        // Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 显示退货受理中或者待收取换货的view
     * @param qualityRefundOrderMap
     */
    private void showInAcceptingForGoodsRejectedOrExchangeGoods(GoodsRejectedBean.QualityRefundOrderMap qualityRefundOrderMap) {
        showSellerSendGoodsView(qualityRefundOrderMap);

        postcodeKeyTextView.setVisibility(View.VISIBLE);
        postcodeTextView.setVisibility(View.VISIBLE);
        postcodeTextView.setText(qualityRefundOrderMap.getZipCode());
        section01BorderView.setVisibility(View.VISIBLE);
        section01TitleKeyTextView.setVisibility(View.VISIBLE);
        section01TitleKeyTextView.setText("退货给卖家");
        section02TitleKeyTextView.setVisibility(View.GONE);
        transportationCostKeyInLogisticsPageTextView.setVisibility(View.VISIBLE);
        transportationCostValueInLogisticsPageTextView.setVisibility(View.VISIBLE);
        transportationCostValueInLogisticsPageTextView.setText(qualityRefundOrderMap.getFreight()+"元");
        deliveryTimeKeyTextView.setVisibility(View.GONE);
        deliveryTimeTextView.setVisibility(View.GONE);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) threeImagesAndApplyTimeRelativeLayout.getLayoutParams();
        layoutParams.setMargins(CommonUtils.dipToPx(this,15),0,0,0);
        threeImagesAndApplyTimeRelativeLayout.setVisibility(View.VISIBLE);
        threeImagesAndApplyTimeRelativeLayout.setLayoutParams(layoutParams);
        applyTimeValueTextView.setText(qualityRefundOrderMap.getApplyTime());
    }

    /**
     * 显示卖家发送商品的view
     * @param qualityRefundOrderMap
     */
    private void showSellerSendGoodsView(GoodsRejectedBean.QualityRefundOrderMap qualityRefundOrderMap) {
        returnMoneyInfoRelativeLayout.setVisibility(View.GONE);
        goodsRejectedDetailInfoRelativeLayout.setVisibility(View.GONE);
        section01BorderView.setVisibility(View.VISIBLE);
        section01TitleKeyTextView.setVisibility(View.VISIBLE);
        section02TitleKeyTextView.setVisibility(View.GONE);
        recipientInfoRelativeLayout.setVisibility(View.VISIBLE);
        logisticsDetailInfoRelativeLayout.setVisibility(View.VISIBLE);
        recipientNameTextView.setText(qualityRefundOrderMap.getRecipientName());
        phoneNumTextView.setText(qualityRefundOrderMap.getRecipientPhone());
        addressTextView.setText(qualityRefundOrderMap.getAddressInfo());
        logisticsCompanyTextView.setText(qualityRefundOrderMap.getExpress());
        trackingNumTextView.setText(qualityRefundOrderMap.getLogisticCode());
    }

    /**
     * 显示退货成功或者换货成功的view
     * @param qualityRefundOrderMap
     * @param typeOfOrder
     */
    private void showSuccessForGoodsRejectedOrExchangeView(GoodsRejectedBean.QualityRefundOrderMap qualityRefundOrderMap, String typeOfOrder) {
        recipientInfoRelativeLayout.setVisibility(View.GONE);
        logisticsDetailInfoRelativeLayout.setVisibility(View.GONE);
        goodsRejectedDetailInfoRelativeLayout.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) threeImagesAndApplyTimeRelativeLayout.getLayoutParams();
        layoutParams.setMargins(CommonUtils.dipToPx(this,27),0,0,0);
        threeImagesAndApplyTimeRelativeLayout.setLayoutParams(layoutParams);
        threeImagesAndApplyTimeRelativeLayout.setVisibility(View.VISIBLE);
        afterSaleTypeValueTextView.setText(typeOfOrder);
        orderNumValueTextView.setText(qualityRefundOrderMap.getReturnOrderId());
        reasonOfGoodsRejectedValueTextView.setText(qualityRefundOrderMap.getReason());
        transportationCostValueInGoodsRejectedPageTextView.setText(qualityRefundOrderMap.getFreight()+"元");
        commentOfGoodsRejectedValueTextView.setText(qualityRefundOrderMap.getExplain());
        applyTimeValueTextView.setText(qualityRefundOrderMap.getApplyTime());
    }


    /**
     * 显示三张凭证照片的view
     */
    private void showAllThreeGoodsImages() {
        threeImagesOfGoodsRejectedLinearLayout.setVisibility(View.VISIBLE);
        imageOfGoodsRejected01ImageView.setVisibility(View.VISIBLE);
        imageOfGoodsRejected02ImageView.setVisibility(View.VISIBLE);
        imageOfGoodsRejected03ImageView.setVisibility(View.VISIBLE);
    }

    /**
     * 调整退货或者换货页面的文本信息
     *
     * @param typeOfOrder "退货"  “换货”
     */
    private void adjustrRejectedOrExchangeGoodsText(String typeOfOrder) {
        orderNumKeyTextView.setText(typeOfOrder + "订单号:");
        reasonOfGoodsRejectedKeyTextView.setText(typeOfOrder + "原因:");
        commentOfGoodsRejectedKeyTextView.setText(typeOfOrder + "说明:");
    }


    @OnClick({R.id.iv_back, R.id.iv_progressCheckForGoodsRejectedActivity_imageOfGoodsRejected01, R.id.iv_progressCheckForGoodsRejectedActivity_imageOfGoodsRejected02,
            R.id.iv_progressCheckForGoodsRejectedActivity_imageOfGoodsRejected03, R.id.btn_error_retry})
    public void onClick(View view) {
        GoodsRejectedBean.QualityRefundOrderMap qualityRefundOrderMap = goodsRejectedBean.getQualityRefundOrderMap();

        switch (view.getId()) {
            case R.id.btn_error_retry:
                queryGoodsRejectedProgress(identification, orderId, returnedGoodsOrderId);
                break;
            case R.id.iv_progressCheckForGoodsRejectedActivity_imageOfGoodsRejected01:
                DisplayBigImageUtil.displayBigImageView(this, qualityRefundOrderMap.getPictureArray().get(0));
                break;
            case R.id.iv_progressCheckForGoodsRejectedActivity_imageOfGoodsRejected02:
                DisplayBigImageUtil.displayBigImageView(this, qualityRefundOrderMap.getPictureArray().get(1));
                break;
            case R.id.iv_progressCheckForGoodsRejectedActivity_imageOfGoodsRejected03:
                DisplayBigImageUtil.displayBigImageView(this, qualityRefundOrderMap.getPictureArray().get(2));
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }
}