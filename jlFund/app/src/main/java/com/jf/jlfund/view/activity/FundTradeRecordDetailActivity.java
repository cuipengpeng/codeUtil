package com.jf.jlfund.view.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jf.jlfund.R;
import com.jf.jlfund.base.BaseActivity;
import com.jf.jlfund.base.BaseBean;
import com.jf.jlfund.bean.FundTradeRecordDetailBean;
import com.jf.jlfund.bean.FundTradeResultBean;
import com.jf.jlfund.http.NetService;
import com.jf.jlfund.http.ParamMap;
import com.jf.jlfund.inter.OnResponseListener;
import com.jf.jlfund.utils.Aes;
import com.jf.jlfund.utils.CommonUtil;
import com.jf.jlfund.utils.ImageUtils;
import com.jf.jlfund.utils.LogUtils;
import com.jf.jlfund.utils.SPUtil;
import com.jf.jlfund.utils.ToastUtils;
import com.jf.jlfund.utils.UIUtils;
import com.jf.jlfund.weight.CommonTitleBar;
import com.jf.jlfund.weight.PayPasswordView;
import com.jf.jlfund.weight.errorview.DefaultErrorPageBean;
import com.jf.jlfund.weight.errorview.ErrorBean;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;

public class FundTradeRecordDetailActivity extends BaseActivity {
    @BindView(R.id.ll_fundRecordDetail_rootView)
    LinearLayout llRootView;

    @BindView(R.id.commonTitleBar_recordDetail)
    CommonTitleBar commonTitleBar;

    @BindView(R.id.iv_fundRecordDetail_saleOrBuy)
    ImageView ivSaleOrBuy;
    @BindView(R.id.tv_fundRecordDetail_fundName)
    TextView tvFundName;
    @BindView(R.id.tv_fundRecordDetail_amount)
    TextView tvAmount;

    @BindView(R.id.iv_fundRecordDetail_ok)
    ImageView ivOk;
    @BindView(R.id.iv_fundRecordDetail_cal)
    ImageView ivCal;
    @BindView(R.id.iv_fundRecordDetail_rmb)
    ImageView ivRmb;

    @BindView(R.id.tv_fundRecordDetail_titleTop)
    TextView tvTitleTop;
    @BindView(R.id.tv_fundRecordDetail_titleCenter)
    TextView tvTitleCenter;
    @BindView(R.id.tv_fundRecordDetail_titleBottom)
    TextView tvTitleBottom;

    @BindView(R.id.tv_fundRecordDetail_descTop)
    TextView tvDescTop;
    @BindView(R.id.tv_fundRecordDetail_descCenter)
    TextView tvDescCenter;
    @BindView(R.id.tv_fundRecordDetail_descBottom)
    TextView tvDescBottom;
    @BindView(R.id.tv_fundRecordDetail_descBottom2)
    TextView tvDescBottom2;
    @BindView(R.id.tv_fundRecordDetail_fundBuyInFailedTip)
    TextView tvBuyInFailedBottomTip;

    @BindView(R.id.v_fundRecordDetail_v1)
    View vP1;
    @BindView(R.id.v_fundRecordDetail_v2)
    View vP2;
    @BindView(R.id.v_fundRecordDetail_v3)
    View vP3;
    @BindView(R.id.v_fundRecordDetail_v4)
    View vP4;

    @BindView(R.id.rl_fundRecordDetail_sheet)
    RelativeLayout rlSheet;

    @BindView(R.id.tv_fundRecordDetail_ensureAmount)
    TextView tvEnsureAmount;
    @BindView(R.id.tv_fundRecordDetail_ensureLot)
    TextView tvEnsureLot;
    @BindView(R.id.tv_fundRecordDetail_ensureNetValue)
    TextView tvEnsureNetValue;
    @BindView(R.id.tv_fundRecordDetail_serviceCost)
    TextView tvEnsureServiceCost;

    @BindView(R.id.tv_fundRecordDetail_orderNo)
    TextView tvOrderNo;

    private String tradeNo;
    private boolean isFromTradeRecordList;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_fund_record_detail;
    }

    @Override
    protected void init() {
        if (getIntent() != null) {
            tradeNo = getIntent().getStringExtra(PARAM_TRADE_NO);
            isFromTradeRecordList = getIntent().getBooleanExtra(PARAM_IS_AFTER_BUY_IN, false);
        }
        if (!isFromTradeRecordList) {  //买卖完成进入该页面，点击完成进入基金首页
            commonTitleBar.setPattern(CommonTitleBar.PATTERN_TITLE_WITH_RIGHT_TXT);
            commonTitleBar.setRightText("完成");
            commonTitleBar.setOnRightClickListener(new CommonTitleBar.onRightClickListener() {
                @Override
                public void onClickRight() {
                    FundActivity.open(FundTradeRecordDetailActivity.this, true);
                    finish();
                }
            });
        }
        rlSheet.setVisibility(View.GONE);
    }


    @Override
    protected void doBusiness() {
        llRootView.setVisibility(View.INVISIBLE);
        postRequest(new OnResponseListener<FundTradeRecordDetailBean>() {
            @Override
            public Observable<BaseBean<FundTradeRecordDetailBean>> createObservalbe() {
                ParamMap paramMap = new ParamMap();
                paramMap.put("token", SPUtil.getInstance().getToken());
                paramMap.putLast("tradeno", Aes.encryptAES(tradeNo));      //20180122000223
                return NetService.getNetService().getFundTradeRecordDetail(paramMap);
            }

            @Override
            public void onResponse(BaseBean<FundTradeRecordDetailBean> result) {
                llRootView.setVisibility(View.VISIBLE);
                if (result.isSuccess()) {
                    inflateData(result.getData());
                }
            }

            @Override
            public void onError(String errorMsg) {
                llRootView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showTradePwdInputDialog() {
        inputPayPasswd(this, new PayPasswordView.OnPayListener() {
            @Override
            public void onCancelPay() {

            }

            @Override
            public void onSurePay(String password, final PayPasswordView payPasswordView) {
//                buyFund(password);
                //撤单成功
                cancelTradeOrder(password);
            }
        });
    }

    //撤单
    private void cancelTradeOrder(final String tradePwd) {
        postRequest(new OnResponseListener<FundTradeResultBean>() {
            @Override
            public Observable<BaseBean<FundTradeResultBean>> createObservalbe() {
                ParamMap paramMap = new ParamMap();
                paramMap.put("token", SPUtil.getInstance().getToken());
                paramMap.put("tradeId", tradeNo);
                paramMap.putLast("pwd", Aes.encryptAES(tradePwd));
                return NetService.getNetService().cancelFundTrade(paramMap);
            }

            @Override
            public void onResponse(final BaseBean<FundTradeResultBean> result) {
                if (result.isSuccess()) {       //撤单成功
                    if (payPasswordView != null) {
                        payPasswordView.onCheckPayPwdSuccess(new ImageUtils.OnGifPlayListener() {
                            @Override
                            public void onGifPlayFinish() {
                                commonTitleBar.setPattern(CommonTitleBar.PATTERN_TITLE_WITH_LEFT_IMG);
                                refreshProgressUIStatus(0);
                                refreshTitleTextColor(1, 0, 0);
                                ivOk.setSelected(true);
                                UIUtils.setText(tvTitleTop, "交易已撤销");
                                if (bean.getTradetype() == 3) {
                                    tvDescTop.setVisibility(View.VISIBLE);
                                    tvDescTop.setText("资金将在两个工作日退回到银行卡");
                                } else {
                                    tvDescTop.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                } else {
                    ToastUtils.showShort("服务异常，请重试~");
                    if (payPasswordView != null) {
                        payPasswordView.onCheckPayPwdFailed(new ImageUtils.OnGifPlayListener() {
                            @Override
                            public void onGifPlayFinish() {
                                ToastUtils.showShort(result.getResCode() + " : " + result.getResMsg());
                            }
                        });
                    }
                }
            }

            @Override
            public void onError(final String errorMsg) {
                if (payPasswordView != null) {
                    payPasswordView.onCheckPayPwdFailed(new ImageUtils.OnGifPlayListener() {
                        @Override
                        public void onGifPlayFinish() {
                            ToastUtils.showShort(errorMsg);
                        }
                    });
                }
            }
        });
    }


    FundTradeRecordDetailBean bean;

    private void inflateData(FundTradeRecordDetailBean result) {
        bean = result;

        if (isFromTradeRecordList) {
            if (bean.getIscancellations()) {   //从交易列表过来并且显示可撤单
                commonTitleBar.setPattern(CommonTitleBar.PATTERN_TITLE_WITH_LEFT_IMG_AND_RIGHT_TXT);
                commonTitleBar.setRightText("撤单");
                commonTitleBar.setOnRightClickListener(new CommonTitleBar.onRightClickListener() {
                    @Override
                    public void onClickRight() {
                        showTradePwdInputDialog();
                    }
                });
            } else {
                commonTitleBar.setPattern(CommonTitleBar.PATTERN_TITLE_WITH_LEFT_IMG);
            }
        }

        UIUtils.setText(tvFundName, bean.getFundname());

        if (bean.getTradetype() == 3) { //申购
            ivSaleOrBuy.setSelected(true);
            UIUtils.setText(tvAmount, bean.getTrademoney() + "元");      //买入金额
            //交易状态
            if (bean.getTradestatus() == 0) {     //-----------------------------------------------------待确认
                //支付状态
                if (bean.getPaystatus().equals("0")) {  //支付成功，待确认（1）【买入受理成功】
                    setStatusOfBuyInAndAccept();
                } else if (bean.getPaystatus().equals("1")) {  //支付失败  【买入受理失败】
                    refreshProgressUIStatus(0);
                    refreshTitleTextColor(-1, 0, 0);
                    ivOk.setSelected(false);
                    UIUtils.setText(tvTitleTop, "支付失败");
                    tvTitleTop.setTextColor(ContextCompat.getColor(this, R.color.color_f35857));
                    UIUtils.setText(tvDescTop, "扣款失败");
                } else if (bean.getPaystatus().equals("2")) {  //未支付
                    setStatusOfBuyInAndAccept();
                } else if (bean.getPaystatus().equals("3")) {  //已到账，无该状态
                }
            } else if (bean.getTradestatus() == 1) {       //----------------------------------------------------已确认
                //为了容错接口数据异常
                if (TextUtils.isEmpty(bean.getPaystatus()) || bean.getPaystatus().equals("0")) {  //买入 已确定 支付成功
                    ivOk.setSelected(true);
                    ivCal.setSelected(true);
                    if (bean.getTradeincomedate_flag()) {   //第三个节点亮
                        refreshProgressUIStatus(4);
                        refreshTitleTextColor(1, 1, 1);
                        ivRmb.setSelected(true);
                    } else {
                        refreshProgressUIStatus(3);
                        refreshTitleTextColor(1, 1, 0);
                        ivRmb.setSelected(false);
                    }
                    UIUtils.setText(tvTitleTop, "申请已受理");
                    UIUtils.setText(tvDescTop, bean.getTradedate());

                    UIUtils.setText(tvTitleCenter, "份额确认，开始计算收益");
                    UIUtils.setText(tvTitleCenter, bean.getTradeaffirmdate());

                    UIUtils.setText(tvTitleBottom, "查看收益");
                    UIUtils.setText(tvDescBottom, bean.getTradeincomedate());

                    inflateSheetData();     //填充底部表格
                } else {//基金买入已确认 目前只有支付成功的状态
                }
            } else if (bean.getTradestatus() == 2) { //------------------------------------------------------已撤单
                if (bean.getPaystatus().equals("0")) {    //撤单--支付成功
                    refreshProgressUIStatus(0);
                    ivOk.setSelected(true);
                    UIUtils.setText(tvTitleTop, "交易已撤销");
                    if (bean.getIsBuyWithdrawnAccount()) {
                        UIUtils.setText(tvDescTop, "资金已退回到银行卡");
                    } else {
                        UIUtils.setText(tvDescTop, "资金将在两个工作日退回到银行卡");
                    }
                    refreshTitleTextColor(1, 0, 0);
                } else {      //已撤单只有支付成功
                }
            } else if (bean.getTradestatus() == 3) {     //-----------------------------------------------------异常
                if (bean.getPaystatus().equals("0")) {    //买入确认失败
                    refreshProgressUIStatus(2);
                    refreshTitleTextColor(1, -1, 0);

                    ivOk.setSelected(true);
                    UIUtils.setText(tvTitleTop, "申请已受理");
                    UIUtils.setText(tvDescTop, bean.getTradedate());

                    ivCal.setImageResource(R.drawable.icon_fund_record_detail_failed);
                    UIUtils.setText(tvTitleCenter, "确认失败");
                    UIUtils.setText(tvDescCenter, bean.getTradeaffirmdate());

                    UIUtils.setText(tvTitleBottom, "无收益");
                    tvDescBottom.setVisibility(View.GONE);
                    tvBuyInFailedBottomTip.setVisibility(View.VISIBLE);
                } else if (bean.getPaystatus().equals("1")) {  //买入受理失败
                    refreshProgressUIStatus(0);
                    refreshTitleTextColor(-1, 0, 0);
                    ivOk.setSelected(false);
                    UIUtils.setText(tvDescTop, "扣款失败");
                } else if (bean.getPaystatus().equals("2")) {      //异常未支付
                    setStatusOfBuyInAndAccept();
                }
            } else if (bean.getTradestatus() == 4) {    //交易关闭，暂不处理
            }
        } else {                        //1:普赎 2：快赎
            ivSaleOrBuy.setSelected(false);
            UIUtils.setText(tvAmount, bean.getTradeshare() + "份");      //赎回份额

            if (bean.getTradestatus() == 0) {           //-------------------------------待确认
                refreshProgressUIStatus(1);
                refreshTitleTextColor(1, 0, 0);

                ivOk.setSelected(true);
                ivCal.setSelected(false);
                ivRmb.setSelected(false);

                UIUtils.setText(tvTitleTop, "申请已受理");
                UIUtils.setText(tvDescTop, bean.getTradedate());        //交易申请时间

                UIUtils.setText(tvTitleCenter, "卖出份额确认");
                UIUtils.setText(tvDescCenter, bean.getTradeaffirmdate());

                UIUtils.setText(tvTitleBottom, "卖出金额到账");
                UIUtils.setText(tvDescBottom, bean.getBankInfo());
                tvDescBottom2.setVisibility(View.VISIBLE);
                UIUtils.setText(tvDescBottom2, bean.getTradetoacctdate());
            } else if (bean.getTradestatus() == 1) {    //-------------------------------已确认
                if (bean.getTradetoacctdate_flag()) {
                    refreshProgressUIStatus(5);
                    refreshTitleTextColor(1, 1, 1);
                } else {
                    refreshProgressUIStatus(3);
                    refreshTitleTextColor(1, 1, 0);
                }

                ivOk.setSelected(true);
                ivCal.setSelected(true);
                ivRmb.setSelected(true);

                UIUtils.setText(tvTitleTop, "申请已受理");
                UIUtils.setText(tvDescTop, bean.getTradedate());

                UIUtils.setText(tvTitleCenter, "卖出份额确认");
                UIUtils.setText(tvDescCenter, bean.getTradeaffirmdate());

                UIUtils.setText(tvTitleBottom, "卖出金额到账");
                UIUtils.setText(tvDescBottom, bean.getBankInfo());
                tvDescBottom2.setVisibility(View.VISIBLE);
                UIUtils.setText(tvDescBottom2, "预计" + bean.getTradetoacctdate());

                inflateSheetData();
            } else if (bean.getTradestatus() == 2) {    //------------------------------已撤单
                refreshProgressUIStatus(0);
                refreshTitleTextColor(1, 0, 0);
                ivOk.setSelected(true);
                UIUtils.setText(tvTitleTop, "交易已撤销");
                tvDescTop.setVisibility(View.GONE);
            } else if (bean.getTradestatus() == 3) {    //-------------------------------异常
                refreshTitleTextColor(1, -1, 0);
                refreshProgressUIStatus(2);
                ivOk.setSelected(true);
                ivCal.setImageResource(R.drawable.icon_fund_record_detail_failed);
                ivRmb.setSelected(false);

                UIUtils.setText(tvTitleTop, "申请已受理");
                UIUtils.setText(tvDescTop, bean.getTradedate());
                UIUtils.setText(tvTitleCenter, "确认失败");
                UIUtils.setText(tvDescCenter, bean.getTradetoacctdate());
                UIUtils.setText(tvTitleBottom, "未卖出");
                tvDescBottom.setVisibility(View.GONE);
                tvDescBottom2.setVisibility(View.GONE);
            } else if (bean.getTradestatus() == 4) { //交易关闭，暂不处理
            }
        }
    }

    /**
     * 买入已受理【状态复用：买入未支付（0,2），异常为支付（3,2）
     */
    private void setStatusOfBuyInAndAccept() {
        refreshProgressUIStatus(1);
        refreshTitleTextColor(1, 0, 0);
        ivOk.setImageResource(R.drawable.selector_fund_record_detail_ok);
        ivOk.setSelected(true);
        ivCal.setImageResource(R.drawable.selector_fund_record_detail_cal);
        if (bean.getTradeaffirmdate_flag()) {   //第二个节点
            ivCal.setSelected(true);
        } else {
            ivCal.setSelected(false);
        }
        ivRmb.setImageResource(R.drawable.selector_fund_record_detail_rmb);
        if (bean.getTradeincomedate_flag()) {
            ivRmb.setSelected(true);
        } else {
            ivRmb.setSelected(false);
        }
        UIUtils.setText(tvTitleTop, "申请已受理");
        UIUtils.setText(tvDescTop, bean.getTradedate());        //交易申请时间
        UIUtils.setText(tvTitleBottom, "查看收益");
        if (isNormalFund()) {
            UIUtils.setText(tvTitleCenter, "份额确认，开始计算收益");
            UIUtils.setText(tvDescCenter, bean.getTradeaffirmdate());    //交易确认时间   (在第二个节点展示)

            UIUtils.setText(tvDescBottom, "预计" + bean.getTradeincomedate());       //交易查看收益时间   (购买第三个节点)
        } else {
            UIUtils.setText(tvTitleCenter, "基金成立，确认份额");
            UIUtils.setText(tvDescCenter, "以基金公司公告为准");    //交易确认时间   (在第二个节点展示)

            UIUtils.setText(tvDescBottom, "基金成立次日");       //交易查看收益时间   (购买第三个节点)
        }
    }


    /**
     * 填充底部表格数据
     */
    private void inflateSheetData() {
        rlSheet.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(bean.getComfirmmoney())) {
            UIUtils.setText(tvEnsureAmount, bean.getComfirmmoney() + "元"); //确认金额
        }
        if (!TextUtils.isEmpty(bean.getConfirmshare())) {
            UIUtils.setText(tvEnsureLot, bean.getConfirmshare() + "份");    //确认份额
        }
        String netValue = "";
        if (!TextUtils.isEmpty(bean.getConfirmnav())) {
            netValue = bean.getConfirmnav();
        }
        if (!TextUtils.isEmpty(bean.getConfirmnavdate())) {
            netValue = netValue + "(" + bean.getConfirmnavdate() + ")";
        }
        UIUtils.setText(tvEnsureNetValue, netValue);     //确认净值
        if (!TextUtils.isEmpty(bean.getFactorage())) {
            UIUtils.setText(tvEnsureServiceCost, bean.getFactorage() + "元");       //手续费
        }
    }

    @OnClick({R.id.tv_fundRecordDetail_fundName})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_fundRecordDetail_fundName:     //点击基金名称 跳转至该只基金产品详情页
                CommonUtil.startActivity(this, SingleFundDetailActivity.class, bean.getFundcode());
                break;
        }
    }

    /**
     * 基金状态为1、2、3、6、7则为正常发布基金，否则为认购期基金
     *
     * @return
     */
    private boolean isNormalFund() {
//        1 2 3 6 7 位正常基金
        String normalFundStatus = "12367";
        return normalFundStatus.contains(bean.getFundstate());
    }


    /**
     * 刷新进度条的显示状态。Note:只控制进度条显示隐藏，不控制显示状态【对勾或叉号】。
     * 分为5个等级
     * 0:只显示节点1.一下节点及进度条以及对应描述tv均隐藏
     * 1：全部显示，只vP1亮起
     * 2：全部显示，vP2及以上全亮，下边置灰，tvDescBottom2隐
     * 3：全部显示，vP3及以上全亮，下边置灰，tvDescBottom2隐
     * 4: 全部显示，全亮，tvDescBottom2隐藏
     * 5：全部显示，全亮，tvDescBottom2显示
     *
     * @param progress
     */
    private void refreshProgressUIStatus(int progress) {
        LogUtils.e("showProgress: " + progress);
        if (progress == 0) {  //终结状态：支付成功、支付失败、撤单成功。。。
            vP1.setVisibility(View.GONE);
            vP2.setVisibility(View.GONE);
            vP3.setVisibility(View.GONE);
            vP4.setVisibility(View.GONE);

            ivCal.setVisibility(View.GONE);
            ivRmb.setVisibility(View.GONE);

            tvTitleCenter.setVisibility(View.GONE);
            tvDescCenter.setVisibility(View.GONE);
            tvTitleBottom.setVisibility(View.GONE);
            tvDescBottom.setVisibility(View.GONE);
            tvDescBottom2.setVisibility(View.GONE);
        } else {  //进行状态：...已受理...

            ivCal.setVisibility(View.VISIBLE);
            ivRmb.setVisibility(View.VISIBLE);

            vP1.setVisibility(View.VISIBLE);
            vP2.setVisibility(View.VISIBLE);
            vP3.setVisibility(View.VISIBLE);
            vP4.setVisibility(View.VISIBLE);

            tvTitleCenter.setVisibility(View.VISIBLE);
            tvDescCenter.setVisibility(View.VISIBLE);
            tvTitleBottom.setVisibility(View.VISIBLE);
            tvDescBottom.setVisibility(View.VISIBLE);

            tvDescBottom2.setVisibility(progress == 5 ? View.VISIBLE : View.GONE);

            vP1.setBackgroundResource(progress >= 1 ? R.color.color_0084ff : R.color.color_b9bbca);
            vP2.setBackgroundResource(progress >= 2 ? R.color.color_0084ff : R.color.color_b9bbca);
            vP3.setBackgroundResource(progress >= 3 ? R.color.color_0084ff : R.color.color_b9bbca);
            vP4.setBackgroundResource(progress >= 4 ? R.color.color_0084ff : R.color.color_b9bbca);
        }
    }

    /**
     * 设置时间轴标题字体颜色
     *
     * @param titleTop    【0:393b51 1:0084ff  -1:f35857】
     * @param titleCenter 【0:393b51 1:0084ff  -1:f35857】
     * @param titleBottom 【0:393b51 1:0084ff  -1:f35857】
     */
    private void refreshTitleTextColor(int titleTop, int titleCenter, int titleBottom) {
        if (0 == titleTop) {
            tvTitleTop.setTextColor(ContextCompat.getColor(this, R.color.color_393b51));
        } else if (1 == titleTop) {
            tvTitleTop.setTextColor(ContextCompat.getColor(this, R.color.color_0084ff));
        } else if (-1 == titleTop) {
            tvTitleTop.setTextColor(ContextCompat.getColor(this, R.color.color_f35857));
        }

        if (0 == titleCenter) {
            tvTitleCenter.setTextColor(ContextCompat.getColor(this, R.color.color_393b51));
        } else if (1 == titleCenter) {
            tvTitleCenter.setTextColor(ContextCompat.getColor(this, R.color.color_0084ff));
        } else if (-1 == titleCenter) {
            tvTitleCenter.setTextColor(ContextCompat.getColor(this, R.color.color_f35857));
        }

        if (0 == titleBottom) {
            tvTitleBottom.setTextColor(ContextCompat.getColor(this, R.color.color_393b51));
        } else if (1 == titleBottom) {
            tvTitleBottom.setTextColor(ContextCompat.getColor(this, R.color.color_0084ff));
        } else if (-1 == titleBottom) {
            tvTitleBottom.setTextColor(ContextCompat.getColor(this, R.color.color_f35857));
        }
    }

    private PayPasswordView payPasswordView;

    /**
     * 弹出交易密码对话框
     *
     * @param activity
     */
    public void inputPayPasswd(final Activity activity, PayPasswordView.OnPayListener onPayListener) {
        try {
            Dialog tradePasswordDialog = new Dialog(activity, R.style.payBillInstalmentDialog);
            payPasswordView = new PayPasswordView(activity, tradePasswordDialog, onPayListener);
            final View dialogView = payPasswordView.getView();
            tradePasswordDialog.setCancelable(true);
            tradePasswordDialog.setCanceledOnTouchOutside(true);
            tradePasswordDialog.setContentView(dialogView);
            tradePasswordDialog.setTitle("");

            Window dialogWindow = tradePasswordDialog.getWindow();
            dialogWindow.setGravity(Gravity.BOTTOM);
//		dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
            WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            dialogWindow.setBackgroundDrawable(activity.getResources().getDrawable(R.color.transparent));
            lp.x = 0; // 新位置X坐标
            lp.y = -20; // 新位置Y坐标
            lp.width = (int) activity.getResources().getDisplayMetrics().widthPixels; // 宽度
//		lp.height = WindowManager.LayoutParams.WRAP_CONTENT; // 高度
//		dialogView.measure(0,0);
            lp.height = (int) activity.getResources().getDisplayMetrics().heightPixels;
            lp.alpha = 1f; // 透明度
            dialogWindow.setAttributes(lp);
            tradePasswordDialog.show();
        } catch (Exception e) {
            LogUtils.e("!!!!!!!!!!!!!!: " + e.getMessage());
        }
    }

    @Override
    protected ErrorBean getErrorBean() {
        return new DefaultErrorPageBean(llRootView);
    }

    public static String PARAM_TRADE_NO = "tradeNo";
    public static String PARAM_IS_AFTER_BUY_IN = "isAfterBuyIn"; //是否刚刚买入完成【买入完成没有返回按钮】

    public static void open(Context context, String tradeNo) {
        open(context, tradeNo, true);
    }

    public static void open(Context context, String tradeNo, boolean isAfterBuyIn) {
        Intent intent = new Intent(context, FundTradeRecordDetailActivity.class);
        intent.putExtra(PARAM_TRADE_NO, tradeNo);
        intent.putExtra(PARAM_IS_AFTER_BUY_IN, isAfterBuyIn);
        context.startActivity(intent);
    }
}
