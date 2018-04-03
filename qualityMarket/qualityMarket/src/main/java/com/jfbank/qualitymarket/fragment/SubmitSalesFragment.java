package com.jfbank.qualitymarket.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.dialog.entity.DialogMenuItem;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.adapter.TakePicAdapter;
import com.jfbank.qualitymarket.base.BaseMvpFragment;
import com.jfbank.qualitymarket.listener.DialogListener;
import com.jfbank.qualitymarket.model.AfterSalesReasonBean;
import com.jfbank.qualitymarket.model.GoodsRejectedBean;
import com.jfbank.qualitymarket.model.SaleApplyOrderBean;
import com.jfbank.qualitymarket.mvp.SubmitSalesMVP;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.DialogUtils;
import com.jfbank.qualitymarket.util.PhotoUtils;
import com.jfbank.qualitymarket.widget.NoScrollGridView;
import com.jph.takephoto.model.TResult;

import org.w3c.dom.Text;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 功能：提交退换货售后申请<br>
 * 作者：赵海<br>
 * 时间： 2017/4/24 0024<br>.
 * 版本：1.5.1
 */

public class SubmitSalesFragment extends BaseMvpFragment<SubmitSalesMVP.Presenter, SubmitSalesMVP.Model> implements SubmitSalesMVP.View {
    @InjectView(R.id.ll_salesreturn_reason)
    LinearLayout llSalesreturnReason;
    @InjectView(R.id.tv_aftersalesr_reason_title)
    TextView tvAftersalesrReasonTitle;
    @InjectView(R.id.tv_select_aftersalesr_reason)
    TextView tvSelectAftersalesrReason;//退换货原因
    @InjectView(R.id.tv_salesreturn_money)
    TextView tvSalesreturnMoney;//退换货金额
    @InjectView(R.id.ll_salesreturn_money)
    LinearLayout llSalesreturnMoney;
    @InjectView(R.id.tv_aftersales_title)
    TextView tvAftersalesTitle;
    @InjectView(R.id.et_aftersales_des)
    EditText etAftersalesDes;//退换货说明
    @InjectView(R.id.nsgv_item_add_pic)
    NoScrollGridView nsgvItemAddPic;//退换货凭证
    @InjectView(R.id.btn_submit_aftersales)
    Button btnSubmitAftersales;//提交
    private TakePicAdapter takePicAdapter;
    private int aftersales_type = 0;//退换货类型
    private AfterSalesReasonBean afterSalesReasonBean;//退还原因
    private String orderId, firstPayment;//订单号和退款金额（首付）
    private String deafultResonMenu = null;//选中退款原因
    private ArrayList<DialogMenuItem> mMenuReasonItems = new ArrayList<>();//选择退款原因菜单栏
    private String reasonSelectTitle, reasonExplainTitle;//退换货原因和说明
    private GoodsRejectedBean.QualityRefundOrderMap qualityRefundOrderMap;

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_submit_sales;
    }

    @Override
    protected void initView() {
        takePicAdapter = new TakePicAdapter(mContext, mPresenter, "上传凭证 选择图片", 3);
        nsgvItemAddPic.setAdapter(takePicAdapter);
        aftersales_type = getArguments().getInt(ConstantsUtil.EXTRA_AFTERSALES_TYPE, 1);
        afterSalesReasonBean = (AfterSalesReasonBean) getArguments().getSerializable(ConstantsUtil.EXTRA_AFTERSALESREASONBEAN);
        qualityRefundOrderMap = (GoodsRejectedBean.QualityRefundOrderMap) mContext.getIntent().getSerializableExtra(ConstantsUtil.EXTRA_GOODS_REJECTED_BEAN);
        firstPayment = getArguments().getString(ConstantsUtil.EXTRA_FIRSTPAYMENT);
        orderId = getArguments().getString(ConstantsUtil.EXTRA_ORDERID);
        if (aftersales_type == 1) {//退货
            llSalesreturnReason.setVisibility(View.VISIBLE);
            llSalesreturnMoney.setVisibility(View.VISIBLE);
            reasonSelectTitle = String.format(getResources().getString(R.string.sales_return_select_reason), "退货");
            reasonExplainTitle = String.format(getResources().getString(R.string.sales_return_reason_des), "退货");
            if (afterSalesReasonBean.getRefundLca() != null && afterSalesReasonBean.getRefundLca().size() > 0) {
                for (int i = 0; i < afterSalesReasonBean.getRefundLca().size(); i++) {
                    mMenuReasonItems.add(i, new DialogMenuItem(afterSalesReasonBean.getRefundLca().get(i).getParameterDes(), 0));
                }
                deafultResonMenu = afterSalesReasonBean.getRefundLca().get(0).getParameterDes();
            } else {

            }
            if (qualityRefundOrderMap != null && qualityRefundOrderMap.getIdentification() == aftersales_type) {
                tvSalesreturnMoney.setText(qualityRefundOrderMap.getRefundAmount() + "元");
                deafultResonMenu = qualityRefundOrderMap.getReason();
                etAftersalesDes.setText(qualityRefundOrderMap.getExplain());
                etAftersalesDes.setSelection( etAftersalesDes.getText().length());
                takePicAdapter.addPic(qualityRefundOrderMap.getPictureArray());
            } else {
                tvSalesreturnMoney.setText(firstPayment + "元");
            }

        } else {//换货
            llSalesreturnReason.setVisibility(View.GONE);
            llSalesreturnMoney.setVisibility(View.GONE);
            reasonSelectTitle = String.format(getResources().getString(R.string.sales_return_select_reason), "换货");
            reasonExplainTitle = String.format(getResources().getString(R.string.sales_return_reason_des), "换货");
            if (afterSalesReasonBean.getExchangeLca() != null && afterSalesReasonBean.getExchangeLca().size() > 0) {
                for (int i = 0; i < afterSalesReasonBean.getExchangeLca().size(); i++) {
                    mMenuReasonItems.add(i, new DialogMenuItem(afterSalesReasonBean.getExchangeLca().get(i).getParameterDes(), 0));
                }
                deafultResonMenu = afterSalesReasonBean.getExchangeLca().get(0).getParameterDes();
            }
            if (qualityRefundOrderMap != null && qualityRefundOrderMap.getIdentification() == aftersales_type) {
                deafultResonMenu = qualityRefundOrderMap.getReason();
                etAftersalesDes.setText(qualityRefundOrderMap.getExplain());
                etAftersalesDes.setSelection( etAftersalesDes.getText().length());
                takePicAdapter.addPic(qualityRefundOrderMap.getPictureArray());
            }
        }
        tvAftersalesrReasonTitle.setText(reasonSelectTitle + "：");
        tvAftersalesTitle.setText(reasonExplainTitle + "：");
        if (deafultResonMenu != null) {//设置默认原因
            tvSelectAftersalesrReason.setText(deafultResonMenu);
        }

    }

    @OnClick({R.id.tv_select_aftersalesr_reason, R.id.btn_submit_aftersales})
    public void OnClick(View v) {
        switch (v.getId()) {
            case R.id.tv_select_aftersalesr_reason://选择退换货原因
                DialogUtils.showListDialog(mContext, reasonSelectTitle, mMenuReasonItems, new DialogListener.DialogItemLisenter() {
                    @Override
                    public void onDialogClick(int position) {
                        deafultResonMenu = mMenuReasonItems.get(position).mOperName;
                        if (deafultResonMenu != null) {//设置原因
                            tvSelectAftersalesrReason.setText(deafultResonMenu);
                        }
                    }
                });
                break;
            case R.id.btn_submit_aftersales://提交退换货
                mPresenter.applyAfterSales(reasonSelectTitle, reasonExplainTitle, aftersales_type + "", orderId, deafultResonMenu == null ? null : deafultResonMenu, etAftersalesDes.getText().toString().trim(), takePicAdapter.getDatas());
                break;
        }
    }

    @Override
    public void takeCancel() {//取消选择图片
        super.takeCancel();
        msgToast("取消选择图片");
    }

    @Override
    public void takeFail(TResult result, String msg) {//照相失败
        super.takeFail(result, msg);
        msgToast(msg);
    }

    @Override
    public void takeSuccess(final TResult result) {//获取图片成功
        super.takeSuccess(result);
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String picUrl = result.getImage().getOriginalPath();
                if (picUrl.endsWith(".jpg") || picUrl.endsWith(".JPG") || picUrl.endsWith(".png") || picUrl.endsWith(".PNG"))
                    takePicAdapter.addPic(result.getImage().getCompressPath());
                else
                    msgToast("请选择jpg或png图片");
            }
        });

    }

    @Override
    protected void initData() {
    }

    @Override
    public String getPageName() {
        return getString(R.string.str_submit_salesreturn);
    }
}