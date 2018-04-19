package com.jfbank.qualitymarket.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.base.BaseActivity;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
import com.jfbank.qualitymarket.fragment.SalesReturnSubmitFragment;
import com.jfbank.qualitymarket.listener.DialogListener;
import com.jfbank.qualitymarket.model.AfterSalesReasonBean;
import com.jfbank.qualitymarket.model.GoodsRejectedBean;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.DialogUtils;
import com.jfbank.qualitymarket.util.DisplayBigImageUtil;
import com.jfbank.qualitymarket.util.LogUtil;
import com.jfbank.qualitymarket.util.UserUtils;
import com.jfbank.qualitymarket.widget.LoadingAlertDialog;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


/*
* 描    述：<br>
*     本页面适用于以下这些状态：
*     如果identification为1即退货: 0-审核退货中, 1-待寄送商品, 2-拒绝退货 ，6-已取消，如果identification为2即换货: 0-换货审核中, 1-待寄送换货, 2-拒绝换货,5-已取消 这些状态
* 作    者：崔朋朋<br>
* 时    间：2017/4/20<br>
*
    如果identification为1（即退货）
        订单状态 0-审核退货中, 1-待寄送商品, 2-拒绝退货  3-退货受理中 4-退货受理中 5-退款完成，6-已取消
        如果identification为2（即换货）
        订单状态 0-换货审核中, 1-待寄送换货, 2-拒绝换货, 3-待收取换货, 4-换货完成,5-已取消,6-待商户发货,7-待买家收货
        */
public class ProgressCheckForGoodsRejectedActivity extends BaseActivity {
    public static final String TAG = ProgressCheckForGoodsRejectedActivity.class.getName();

    public static final String KEY_OF_SELECT_APPLY_AFTER_SALE_TAB = "selectApplyAfterSaleTabKey";
    public static final int PROGRESS_CHECK_TAB_OF_APPLY_AFTER_SALE = 2;//1申请售后   2进度查询

    //如果identification为1（即退货） 如果identification为2（即换货）
    public static final int TYPE_OF_GOODS_REJECTED = 1;
    public static final int TYPE_OF_EXCHANGE_GOODS = 2;

    public static final int GOODS_REJECTED_STATUS_WAIT_FOR_VERIFYING = 0;
    public static final int GOODS_REJECTED_STATUS_WAIT_FOR_SEND_GOODS = 1;
    public static final int GOODS_REJECTED_STATUS_REFUSED = 2;
    public static final int GOODS_REJECTED_STATUS_ACCEPTING = 3;
    public static final int GOODS_REJECTED_STATUS_RETURNED_GOODS_FINISHED = 4;
    public static final int GOODS_REJECTED_STATUS_RETURNED_MONEY_FINISHED = 5;
    public static final int GOODS_REJECTED_STATUS_CANCEL_APPLY = 6;

    public static final int EXCHANGE_GOODS_STATUS_WAIT_FOR_VERIFYING = GOODS_REJECTED_STATUS_WAIT_FOR_VERIFYING;
    public static final int EXCHANGE_GOODS_STATUS_WAIT_FOR_SEND_GOODS = GOODS_REJECTED_STATUS_WAIT_FOR_SEND_GOODS;
    public static final int EXCHANGE_GOODS_STATUS_REFUSED = GOODS_REJECTED_STATUS_REFUSED;
    public static final int EXCHANGE_GOODS_STATUS_WAIT_FOR_SELLER_RECEIVE_GOODS = 3;
    public static final int EXCHANGE_GOODS_STATUS_FINISHED = 4;
    public static final int EXCHANGE_GOODS_STATUS_CANCEL_APPLY = 5;
    public static final int EXCHANGE_GOODS_STATUS_WAIT_FOR_SELLER_SEND_GOODS = 6;
    public static final int EXCHANGE_GOODS_STATUS_WAIT_FOR_BUYER_RECEIVE_GOODS = 7;

    @InjectView(R.id.rl_title)
    RelativeLayout rlTitle;
    @InjectView(R.id.tv_title)
    TextView tvTitle;
    @InjectView(R.id.iv_back)
    ImageView ivBack;

    @InjectView(R.id.tv_progressCheckForGoodsRejectedActivity_cancelApply)
    TextView cancelApplyTextView;
    @InjectView(R.id.tv_progressCheckForGoodsRejectedActivity_sellerunHandleCountDown)
    TextView sellerunHandleCountDownTextView;
    @InjectView(R.id.tv_progressCheckForGoodsRejectedActivity_afterSaleTypeValue)
    TextView afterSaleTypeTextView;
    @InjectView(R.id.tv_progressCheckForGoodsRejectedActivity_orderNumValue)
    TextView orderNumTextView;
    @InjectView(R.id.tv_progressCheckForGoodsRejectedActivity_reasonOfGoodsRejectedValue)
    TextView reasonOfGoodsRejectedValueTextView;
    @InjectView(R.id.tv_progressCheckForGoodsRejectedActivity_returnedMoneyValue)
    TextView returnedMoneyTextView;
    @InjectView(R.id.tv_progressCheckForGoodsRejectedActivity_commentOfGoodsRejectedValue)
    TextView commentOfGoodsRejectedTextView;
    @InjectView(R.id.iv_progressCheckForGoodsRejectedActivity_imageOfGoodsRejected01)
    ImageView imageOfGoodsRejected01ImageView;
    @InjectView(R.id.iv_progressCheckForGoodsRejectedActivity_imageOfGoodsRejected02)
    ImageView imageOfGoodsRejected02ImageView;
    @InjectView(R.id.iv_progressCheckForGoodsRejectedActivity_imageOfGoodsRejected03)
    ImageView imageOfGoodsRejected03ImageView;
    @InjectView(R.id.tv_progressCheckForGoodsRejectedActivity_applyTimeValue)
    TextView applyTimeTextView;
    @InjectView(R.id.btn_progressCheckForGoodsRejectedActivity_modifyApplyOfExchangeGoods)
    Button modifyApplyOfExchangeGoodsButton;

    @InjectView(R.id.tv_progressCheckForGoodsRejectedActivity_waitForSellerHandleApplyKey)
    TextView waitForSellerHandleApplyKeyTextView;
    @InjectView(R.id.rl_progressCheckForGoodsRejectedActivity_waitForSellerHandleApply)
    RelativeLayout waitForSellerHandleApplyRelativeLayout;
    @InjectView(R.id.rl_progressCheckForGoodsRejectedActivity_sellerRefuseApply)
    RelativeLayout sellerRefuseApplyRelativeLayout;
    @InjectView(R.id.rl_progressCheckForGoodsRejectedActivity_applyClosed)
    RelativeLayout applyClosedRelativeLayout;
    @InjectView(R.id.tv_progressCheckForGoodsRejectedActivity_orderNumKey)
    TextView orderNumKeyTextView;
    @InjectView(R.id.tv_progressCheckForGoodsRejectedActivity_reasonOfGoodsRejectedKey)
    TextView reasonOfGoodsRejectedKeyTextView;
    @InjectView(R.id.tv_progressCheckForGoodsRejectedActivity_returnedMoneyKey)
    TextView returnedMoneyKeyTextView;
    @InjectView(R.id.tv_progressCheckForGoodsRejectedActivity_commentOfGoodsRejectedKey)
    TextView commentOfGoodsRejectedKeyTextView;
    @InjectView(R.id.ll_progressCheckForGoodsRejectedActivity_threeImagesOfGoodsRejected)
    LinearLayout threeImagesOfGoodsRejectedLinearLayout;
    @InjectView(R.id.rl_progressCheckForGoodsRejectedActivity_threeImagesAndApplyTime)
    RelativeLayout threeImagesAndApplyTimeRelativeLayout;

    @InjectView(R.id.sl_progressCheckForGoodsRejectedActivity_returnGoodsToSeller)
    ScrollView returnGoodsToSellerScrollView;
    @InjectView(R.id.sl_progressCheckForGoodsRejectedActivity_allViewsExceptReturnGoodsToSeller)
    ScrollView allViewsExceptReturnGoodsToSellerScrollView;
    @InjectView(R.id.ll_progressCheckForGoodsRejectedActivity_allDataView)
    LinearLayout allDataViewLinearLayout;
    @InjectView(R.id.ll_progressCheckForGoodsRejectedActivity_netwrokErrorView)
    LinearLayout netwrokErrorViewLinearLayout;

    @InjectView(R.id.tv_progressCheckForGoodsRejectedActivity_transportationCostKey)
    TextView transportationCostKeyTextView;
    @InjectView(R.id.tv_progressCheckForGoodsRejectedActivity_transportationCostValue)
    TextView transportationCostValueTextView;

    @InjectView(R.id.tv_progressCheckForGoodsRejectedActivity_timeOfSellerRefuseApplyValue)
    TextView timeOfSellerRefuseApplyValueTextView;
    @InjectView(R.id.tv_progressCheckForGoodsRejectedActivity_reasonOfSellerRefuseApplyValue)
    TextView reasonOfSellerRefuseApplyValueTextView;
    @InjectView(R.id.tv_progressCheckForGoodsRejectedActivity_reasonOfClosedApplyValue)
    TextView reasonOfClosedApplyValueTextView;
    @InjectView(R.id.tv_progressCheckForGoodsRejectedActivity_timeOfClosedApplyValue)
    TextView timeOfClosedApplyValueTextView;

    /**网编请求时加载框*/
    private LoadingAlertDialog mDialog;

    private  GoodsRejectedBean goodsRejectedBean = new GoodsRejectedBean();
    private  String typeOfOrder = "";
    public String identification = "";
    public String orderId = "";
    public String returnedGoodsOrderId = "";

    @Override
    protected String getPageName() {
        return "退换货进度查询-商品拒绝";
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_check_for_goods_rejected);
        ButterKnife.inject(this);

        CommonUtils.setTitle(this,rlTitle);
        tvTitle.setText("进度查询");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            orderId = bundle.getString(ConstantsUtil.EXTRA_ORDERID, "");
            identification = bundle.getString(ConstantsUtil.EXTRA_IDENTIFICATION, "");
            returnedGoodsOrderId = bundle.getString(ConstantsUtil.EXTRA_RETURNEDGOODSORDERID, "");
        }

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) threeImagesAndApplyTimeRelativeLayout.getLayoutParams();
        layoutParams.setMargins(CommonUtils.dipToPx(this,27),0,0,0);
        threeImagesAndApplyTimeRelativeLayout.setLayoutParams(layoutParams);

        //默认隐藏部分view
        hideAllTitleComment();
        waitForSellerHandleApplyRelativeLayout.setVisibility(View.VISIBLE);
        threeImagesOfGoodsRejectedLinearLayout.setVisibility(View.INVISIBLE);

        transportationCostKeyTextView.setVisibility(View.GONE);
        transportationCostValueTextView.setVisibility(View.GONE);


        //默认隐藏所有的view   避免跳转进度查询页面时会闪一个页面
        netwrokErrorViewLinearLayout.setVisibility(View.GONE);
        allViewsExceptReturnGoodsToSellerScrollView.setVisibility(View.GONE);
        returnGoodsToSellerScrollView.setVisibility(View.GONE);
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
                            showDataView();
                            goodsRejectedBean = JSON.parseObject(jsonObject.getJSONObject(ConstantsUtil.RESPONSE_DATA_JSON_ARRAY_FIELD_NAME).toString(), GoodsRejectedBean.class);
                            GoodsRejectedBean.QualityRefundOrderMap qualityRefundOrderMap = goodsRejectedBean.getQualityRefundOrderMap();
//                            如果identification为1（即退货）
//                            订单状态 0-审核退货中, 1-待寄送商品, 2-拒绝退货  3-退货受理中 4-退货受理中 5-退款完成，6-已取消
//                            如果identification为2（即换货）
//                            订单状态 0-换货审核中, 1-待寄送换货, 2-拒绝换货, 3-待收取换货, 4-换货完成,5-已取消,6-待商户发货,7-待买家收货

                            boolean methodOver = false;
                            if (TYPE_OF_GOODS_REJECTED == qualityRefundOrderMap.getIdentification()) {
                                typeOfOrder = "退货";
                                adjustrRejectedOrExchangeGoodsText(typeOfOrder);
                                returnedMoneyTextView.setText(qualityRefundOrderMap.getRefundAmount() + "元");
                                switch (qualityRefundOrderMap.getOrderStatus()) {
                                    case GOODS_REJECTED_STATUS_WAIT_FOR_VERIFYING:
                                        showWaitForSellerHandleApplyTitleView(qualityRefundOrderMap, typeOfOrder);
                                        break;
                                    case GOODS_REJECTED_STATUS_WAIT_FOR_SEND_GOODS:
                                        methodOver = waitForBuyerSendGoods();
                                        break;
                                    case GOODS_REJECTED_STATUS_REFUSED:
                                        showSellerRefuseApplyTitleView(qualityRefundOrderMap, typeOfOrder);
                                        break;
                                    case GOODS_REJECTED_STATUS_ACCEPTING:
                                        break;
                                    case GOODS_REJECTED_STATUS_RETURNED_GOODS_FINISHED:
                                        break;
                                    case GOODS_REJECTED_STATUS_RETURNED_MONEY_FINISHED:
                                        break;
                                    case GOODS_REJECTED_STATUS_CANCEL_APPLY:
                                        showApplyClosedTitleView(qualityRefundOrderMap, typeOfOrder);
                                        break;
                                }
                            } else if (TYPE_OF_EXCHANGE_GOODS == qualityRefundOrderMap.getIdentification()) {
                                typeOfOrder = "换货";
                                returnedMoneyKeyTextView.setVisibility(View.GONE);
                                returnedMoneyTextView.setVisibility(View.GONE);
                                adjustrRejectedOrExchangeGoodsText(typeOfOrder);

                                switch (qualityRefundOrderMap.getOrderStatus()) {
                                    case EXCHANGE_GOODS_STATUS_WAIT_FOR_VERIFYING:
                                        showWaitForSellerHandleApplyTitleView(qualityRefundOrderMap, typeOfOrder);
                                        break;
                                    case EXCHANGE_GOODS_STATUS_WAIT_FOR_SEND_GOODS:
                                        methodOver = waitForBuyerSendGoods();
                                        break;
                                    case EXCHANGE_GOODS_STATUS_REFUSED:
                                        showSellerRefuseApplyTitleView(qualityRefundOrderMap, typeOfOrder);
                                        break;
                                    case EXCHANGE_GOODS_STATUS_WAIT_FOR_SELLER_RECEIVE_GOODS:
                                        break;
                                    case EXCHANGE_GOODS_STATUS_FINISHED:
                                        break;
                                    case EXCHANGE_GOODS_STATUS_CANCEL_APPLY:
                                        showApplyClosedTitleView(qualityRefundOrderMap, typeOfOrder);
                                        break;
                                    case EXCHANGE_GOODS_STATUS_WAIT_FOR_SELLER_SEND_GOODS:
                                        break;
                                    case EXCHANGE_GOODS_STATUS_WAIT_FOR_BUYER_RECEIVE_GOODS:
                                        break;
                                }
                            }

                            if (methodOver) {
                                return;
                            }

                            if (qualityRefundOrderMap.getPictureArray() != null) {
                                switch (qualityRefundOrderMap.getPictureArray().size()) {
                                    case 1:
                                        showAllThreeGoodsImages();
                                        imageOfGoodsRejected02ImageView.setVisibility(View.GONE);
                                        imageOfGoodsRejected03ImageView.setVisibility(View.GONE);
                                        Picasso.with(ProgressCheckForGoodsRejectedActivity.this).load(qualityRefundOrderMap.getPictureArray().get(0)).into(imageOfGoodsRejected01ImageView);
                                        break;
                                    case 2:
                                        showAllThreeGoodsImages();
                                        imageOfGoodsRejected03ImageView.setVisibility(View.GONE);
                                        Picasso.with(ProgressCheckForGoodsRejectedActivity.this).load(qualityRefundOrderMap.getPictureArray().get(0)).into(imageOfGoodsRejected01ImageView);
                                        Picasso.with(ProgressCheckForGoodsRejectedActivity.this).load(qualityRefundOrderMap.getPictureArray().get(1)).into(imageOfGoodsRejected02ImageView);
                                        break;
                                    case 3:
                                        showAllThreeGoodsImages();
                                        Picasso.with(ProgressCheckForGoodsRejectedActivity.this).load(qualityRefundOrderMap.getPictureArray().get(0)).into(imageOfGoodsRejected01ImageView);
                                        Picasso.with(ProgressCheckForGoodsRejectedActivity.this).load(qualityRefundOrderMap.getPictureArray().get(1)).into(imageOfGoodsRejected02ImageView);
                                        Picasso.with(ProgressCheckForGoodsRejectedActivity.this).load(qualityRefundOrderMap.getPictureArray().get(2)).into(imageOfGoodsRejected03ImageView);
                                        break;
                                }
                            }

                            afterSaleTypeTextView.setText(typeOfOrder);
                            orderNumTextView.setText(qualityRefundOrderMap.getReturnOrderId());
                            reasonOfGoodsRejectedValueTextView.setText(qualityRefundOrderMap.getReason());
                            commentOfGoodsRejectedTextView.setText(qualityRefundOrderMap.getExplain());
                            applyTimeTextView.setText(qualityRefundOrderMap.getApplyTime());


                        } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                            UserUtils.tokenFailDialog(mContext, jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), TAG);
                        } else {
                            Toast.makeText(ProgressCheckForGoodsRejectedActivity.this, jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailed(String path, String msg) {
                        if (mDialog.isShowing()) {
                            mDialog.dismiss();
                        }

                        showNetworkErrorView();
                        // Toast.makeText(getActivity(),
                        // ConstantsUtil.FAIL_TO_CONNECT_SERVER+"查询信用额度失败",
                        // Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 显示待买家发货的view
     * @return
     */
    private boolean waitForBuyerSendGoods() {
        netwrokErrorViewLinearLayout.setVisibility(View.GONE);
        allViewsExceptReturnGoodsToSellerScrollView.setVisibility(View.GONE);
        returnGoodsToSellerScrollView.setVisibility(View.VISIBLE);
        //TODO
        SalesReturnSubmitFragment.getInstance(ProgressCheckForGoodsRejectedActivity.this, R.id.ll_progressCheckForGoodsRejectedActivity_returnGoodsToSeller, goodsRejectedBean);
        boolean methodOver = true;
        return methodOver;
    }

    /**
     * 显示数据的view
     */
    private void showDataView() {
        returnGoodsToSellerScrollView.setVisibility(View.GONE);
        allViewsExceptReturnGoodsToSellerScrollView.setVisibility(View.VISIBLE);
        netwrokErrorViewLinearLayout.setVisibility(View.GONE);
    }

    /**
     * 显示网络异常的view
     */
    private void showNetworkErrorView() {
        returnGoodsToSellerScrollView.setVisibility(View.GONE);
        allViewsExceptReturnGoodsToSellerScrollView.setVisibility(View.GONE);
        netwrokErrorViewLinearLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 显示三张凭证照片
     */
    private void showAllThreeGoodsImages() {
        threeImagesOfGoodsRejectedLinearLayout.setVisibility(View.VISIBLE);
        imageOfGoodsRejected01ImageView.setVisibility(View.VISIBLE);
        imageOfGoodsRejected02ImageView.setVisibility(View.VISIBLE);
        imageOfGoodsRejected03ImageView.setVisibility(View.VISIBLE);
    }


    /**
     *
     * 显示申请关闭的view
     * @param qualityRefundOrderMap
     * @param typeOfOrder
     */
    private void showApplyClosedTitleView(GoodsRejectedBean.QualityRefundOrderMap qualityRefundOrderMap, String typeOfOrder) {
        hideAllTitleComment();
        applyClosedRelativeLayout.setVisibility(View.VISIBLE);

        waitForSellerHandleApplyKeyTextView.setText(typeOfOrder + "关闭");
        cancelApplyTextView.setVisibility(View.GONE);
        modifyApplyOfExchangeGoodsButton.setVisibility(View.GONE);

        timeOfClosedApplyValueTextView.setText(qualityRefundOrderMap.getFinishTime());
        reasonOfClosedApplyValueTextView.setText(qualityRefundOrderMap.getCancelDesc());
    }

    /**
     * 显示商家待处理申请的view
     * @param qualityRefundOrderMap
     * @param typeOfOrder
     */
    private void showWaitForSellerHandleApplyTitleView(GoodsRejectedBean.QualityRefundOrderMap qualityRefundOrderMap, String typeOfOrder) {
        hideAllTitleComment();
        waitForSellerHandleApplyRelativeLayout.setVisibility(View.VISIBLE);

        waitForSellerHandleApplyKeyTextView.setText("等待商家处理" + typeOfOrder + "申请");
        cancelApplyTextView.setVisibility(View.VISIBLE);
        modifyApplyOfExchangeGoodsButton.setVisibility(View.GONE);

        String str = "申请未达成，请联系客服人员";
        String[] countDownDayAndHour =formatCountDownDayAndHour(qualityRefundOrderMap);
        if(Integer.valueOf(countDownDayAndHour[0])==0 && Integer.valueOf(countDownDayAndHour[1])==0){
            sellerunHandleCountDownTextView.setText(str);
        }else {
            str = "剩余"+countDownDayAndHour[0]+"天"+countDownDayAndHour[1]+"时"+str;
            sellerunHandleCountDownTextView.setText(getRedRepaymentTextStyle(this, str), TextView.BufferType.SPANNABLE);
        }
    }

    private String[] formatCountDownDayAndHour(GoodsRejectedBean.QualityRefundOrderMap qualityRefundOrderMap){

        String[] countDownDayAndHour = new String[2];
        countDownDayAndHour[0] =qualityRefundOrderMap.getCountDownDays();
        countDownDayAndHour[1] =qualityRefundOrderMap.getCountDownHours();
        if(countDownDayAndHour[0].length() < 2){
            countDownDayAndHour[0] = "0"+countDownDayAndHour[0];
        }
        if(countDownDayAndHour[1].length() < 2){
            countDownDayAndHour[1] = "0"+countDownDayAndHour[1];
        }

        return countDownDayAndHour;
    }

    /**
     * 显示商家拒绝申请的view
     * @param qualityRefundOrderMap
     * @param typeOfOrder
     */
    private void showSellerRefuseApplyTitleView(GoodsRejectedBean.QualityRefundOrderMap qualityRefundOrderMap, String typeOfOrder) {
        hideAllTitleComment();
        sellerRefuseApplyRelativeLayout.setVisibility(View.VISIBLE);

        waitForSellerHandleApplyKeyTextView.setText("请修改" + typeOfOrder + "申请");
        cancelApplyTextView.setVisibility(View.VISIBLE);
        modifyApplyOfExchangeGoodsButton.setVisibility(View.VISIBLE);
        modifyApplyOfExchangeGoodsButton.setText("修改" + typeOfOrder + "申请");

        String[] countDownDayAndHour =formatCountDownDayAndHour(qualityRefundOrderMap);
        String str = "剩余"+countDownDayAndHour[0]+"天"+countDownDayAndHour[1]+"时，逾期未修改申请将自动关闭";
        timeOfSellerRefuseApplyValueTextView.setText(getRedRepaymentTextStyle(this, str), TextView.BufferType.SPANNABLE);
        reasonOfSellerRefuseApplyValueTextView.setText(qualityRefundOrderMap.getRefusedReason());
    }

    /**
     * 隐藏所有的顶部的用户提示view
     */
    private void hideAllTitleComment() {
        waitForSellerHandleApplyRelativeLayout.setVisibility(View.GONE);
        sellerRefuseApplyRelativeLayout.setVisibility(View.GONE);
        applyClosedRelativeLayout.setVisibility(View.GONE);
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

    /**
     * 修改退换货申请
     */
    public void modifyGoodsRejectedApply() {

        Map<String,String> params = new HashMap<>();
        params.put("uid", AppContext.user.getUid());
        params.put("token", AppContext.user.getToken());
        params.put("identification", identification);
        params.put("returnOrderId", returnedGoodsOrderId);

        HttpRequest.post(mContext,HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.MODIFY_GOODS_REJECTED_APPLY, params,
                new AsyncResponseCallBack() {

                    @Override
                    public void onResult(String arg2) {
                        String jsonStr = new String(arg2);
                        LogUtil.printLog("修改退换货申请：" + jsonStr);
                        JSONObject jsonObject = JSON.parseObject(jsonStr);

                        if (ConstantsUtil.RESPONSE_SUCCEED == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                            AfterSalesReasonBean afterSalesReasonBean = JSON.parseObject(jsonObject.getJSONObject(ConstantsUtil.RESPONSE_DATA_JSON_ARRAY_FIELD_NAME).toString(), AfterSalesReasonBean.class);

                            LogUtil.printLog(afterSalesReasonBean.toString());
                            Intent intent = new Intent(ProgressCheckForGoodsRejectedActivity.this, ApplyAfterSalesActivity.class);
                            Bundle extraParams = new Bundle();
                            extraParams.putInt(ConstantsUtil.EXTRA_AFTERSALES_TYPE, goodsRejectedBean.getQualityRefundOrderMap().getIdentification());
                            extraParams.putSerializable(ConstantsUtil.EXTRA_AFTERSALESREASONBEAN, afterSalesReasonBean);
                            extraParams.putSerializable(ConstantsUtil.EXTRA_GOODS_REJECTED_BEAN, goodsRejectedBean.getQualityRefundOrderMap());
                            extraParams.putString(ConstantsUtil.EXTRA_ORDERID, orderId);
                            extraParams.putString(ConstantsUtil.EXTRA_FIRSTPAYMENT, goodsRejectedBean.getQualityRefundOrderMap().getRefundAmount());
                            intent.putExtras(extraParams);
                            startActivity(intent);

                        } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                            UserUtils.tokenFailDialog(mContext, jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), TAG);
                        } else if (ConstantsUtil.RESPONSE_NO_DATA == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                            DialogUtils.showOneBtnDialog(ProgressCheckForGoodsRejectedActivity.this, false, "", jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), "", new DialogListener.DialogClickLisenter() {
                                @Override
                                public void onDialogClick(int type) {
                                }
                            });
                        } else {
                                Toast.makeText(ProgressCheckForGoodsRejectedActivity.this, jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailed(String path, String msg) {
                        // Toast.makeText(getActivity(),
                        // ConstantsUtil.FAIL_TO_CONNECT_SERVER+"查询信用额度失败",
                        // Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 取消退换货申请
     */
    public void cancelGoodsRejectedApply() {
        Map<String,String> params = new HashMap<>();
        params.put("uid", AppContext.user.getUid());
        params.put("token", AppContext.user.getToken());
        params.put("identification", identification);
//        params.put("orderId", returnedGoodsOrderId);
        params.put("returnOrderId", returnedGoodsOrderId);

        HttpRequest.post(mContext,HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.CANCEL_GOODS_REJECTED_APPLY, params,
                new AsyncResponseCallBack() {

                    @Override
                    public void onResult(String arg2) {
                        String jsonStr = new String(arg2);
                        LogUtil.printLog("取消退换货申请：" + jsonStr);
                        JSONObject jsonObject = JSON.parseObject(jsonStr);

                        if (ConstantsUtil.RESPONSE_SUCCEED == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                            queryGoodsRejectedProgress(identification, orderId, returnedGoodsOrderId);
                        } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                            UserUtils.tokenFailDialog(mContext, jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), TAG);
                        } else if (ConstantsUtil.RESPONSE_NO_DATA == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                            Toast.makeText(ProgressCheckForGoodsRejectedActivity.this, jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), Toast.LENGTH_LONG).show();
                        } else{
                                Toast.makeText(ProgressCheckForGoodsRejectedActivity.this, jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailed(String path, String msg) {
                        // Toast.makeText(getActivity(),
                        // ConstantsUtil.FAIL_TO_CONNECT_SERVER+"查询信用额度失败",
                        // Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /**
     * 商家未处理的倒计时的文本变成红色
     *
     * @param activity ooncreate
     * @param money
     * @return
     */
    public static SpannableString getRedRepaymentTextStyle(Activity activity, String money) {
        SpannableString styledText = new SpannableString(money);
        styledText.setSpan(new TextAppearanceSpan(activity, R.style.style_themeRed_14sp), 2, 4,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledText.setSpan(new TextAppearanceSpan(activity, R.style.style_themeRed_14sp), 5, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return styledText;
    }

    @OnClick({R.id.tv_progressCheckForGoodsRejectedActivity_cancelApply, R.id.iv_progressCheckForGoodsRejectedActivity_imageOfGoodsRejected01,
            R.id.iv_progressCheckForGoodsRejectedActivity_imageOfGoodsRejected02, R.id.iv_progressCheckForGoodsRejectedActivity_imageOfGoodsRejected03,
            R.id.btn_progressCheckForGoodsRejectedActivity_modifyApplyOfExchangeGoods, R.id.iv_back, R.id.btn_error_retry})
    public void onClick(View view) {
        GoodsRejectedBean.QualityRefundOrderMap qualityRefundOrderMap = goodsRejectedBean.getQualityRefundOrderMap();

        switch (view.getId()) {
            case R.id.iv_back:
                launchAfterSalesActivity();
                break;
            case R.id.btn_error_retry:
                queryGoodsRejectedProgress(identification, orderId, returnedGoodsOrderId);
                break;
            case R.id.tv_progressCheckForGoodsRejectedActivity_cancelApply:
                String msg = "撤销申请后您将不能重新发起售后申请，是否确认撤销？";
                DialogUtils.showTwoBtnDialog(this, "", msg, "", "", new DialogListener.DialogClickLisenter() {
                    @Override
                    public void onDialogClick(int type) {
                        if(type == CLICK_SURE){
                            cancelGoodsRejectedApply();
                        }
                    }
                });

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
            case R.id.btn_progressCheckForGoodsRejectedActivity_modifyApplyOfExchangeGoods:
                modifyGoodsRejectedApply();
                break;
        }
    }

    private void launchAfterSalesActivity() {
        Intent intent = new Intent(this, AfterSalesActivity.class);
        intent.putExtra(KEY_OF_SELECT_APPLY_AFTER_SALE_TAB,PROGRESS_CHECK_TAB_OF_APPLY_AFTER_SALE);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            launchAfterSalesActivity();
            return true;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }

}