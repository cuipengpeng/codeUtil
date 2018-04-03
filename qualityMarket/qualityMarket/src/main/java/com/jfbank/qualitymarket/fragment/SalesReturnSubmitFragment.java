package com.jfbank.qualitymarket.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
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
import com.jfbank.qualitymarket.helper.CashierInputFilter;
import com.jfbank.qualitymarket.listener.DialogListener;
import com.jfbank.qualitymarket.model.AfterSalesReasonBean;
import com.jfbank.qualitymarket.model.GoodsRejectedBean;
import com.jfbank.qualitymarket.model.SaleApplyOrderBean;
import com.jfbank.qualitymarket.mvp.SalesReturnSubmitMVP;
import com.jfbank.qualitymarket.util.DialogUtils;
import com.jfbank.qualitymarket.util.PhotoUtils;
import com.jfbank.qualitymarket.widget.NoScrollGridView;
import com.jph.takephoto.model.TResult;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 功能：退货信息提交<br>
 * 作者：赵海<br>
 * 时间： 2017/4/25 0025<br>.
 * 版本：1.5.2
 */

public class SalesReturnSubmitFragment extends BaseMvpFragment<SalesReturnSubmitMVP.Presenter, SalesReturnSubmitMVP.Model> implements SalesReturnSubmitMVP.View {
    @InjectView(R.id.tv_cancel_order)
    TextView tvCancelOrder;
    @InjectView(R.id.tv_salesreturn_time)
    TextView tvSalesreturnTime;
    @InjectView(R.id.tv_recipients_name)
    TextView tvRecipientsName;
    @InjectView(R.id.tv_recipients_phone)
    TextView tvRecipientsPhone;
    @InjectView(R.id.tv_salesreturn_recipients_postcode)
    TextView tvSalesreturnRecipientsPostcode;
    @InjectView(R.id.tv_recipients_address)
    TextView tvRecipientsAddress;
    @InjectView(R.id.ll_salesreturn_reason)
    LinearLayout llSalesreturnReason;
    @InjectView(R.id.tv_aftersalesr_reason_title)
    TextView tvAftersalesrReasonTitle;
    @InjectView(R.id.tv_select_logistics_name)
    TextView tvSelectLogisticsName;
    @InjectView(R.id.et_select_logistics_money)
    EditText etSelectLogisticsMoney;
    @InjectView(R.id.tv_salesreturn_money)
    TextView tvSalesreturnMoney;
    @InjectView(R.id.ll_salesreturn_money)
    LinearLayout llSalesreturnMoney;
    @InjectView(R.id.tv_aftersales_title)
    TextView tvAftersalesTitle;
    @InjectView(R.id.et_select_logistics_id)
    EditText etSelectLogisticsId;
    @InjectView(R.id.nsgv_item_add_pic)
    NoScrollGridView nsgvItemAddPic;
    @InjectView(R.id.btn_submit_salesreturn)
    Button btnSubmitSalesreturn;
    GoodsRejectedBean goodsRejectedBean;
    private TakePicAdapter takePicAdapter;
    DialogMenuItem deafultResonMenu = null;
    ArrayList<DialogMenuItem> mMenuReasonItems = new ArrayList<>();
    String expressCode = null;

    /**
     * @param activity
     * @param resId
     * @param goodsRejectedBean
     * @return
     */
    public static SalesReturnSubmitFragment getInstance(FragmentActivity activity, int resId, GoodsRejectedBean goodsRejectedBean) {
        SalesReturnSubmitFragment returnSubmitFragment = new SalesReturnSubmitFragment();
        Bundle returnSubmitBundle = new Bundle();
        returnSubmitBundle.putSerializable("goodsRejectedBean", goodsRejectedBean);
        returnSubmitFragment.setArguments(returnSubmitBundle);
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.replace(resId, returnSubmitFragment);
        transaction.commit();
        return returnSubmitFragment;
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_sales_return_submit;
    }

    @Override
    protected void initView() {
    }

    @OnClick({R.id.tv_select_logistics_name, R.id.tv_cancel_order, R.id.btn_submit_salesreturn})
    public void OnClick(View v) {
        switch (v.getId()) {
            case R.id.tv_select_logistics_name://选择物流公司
                DialogUtils.showListDialog(mContext, "物流公司", mMenuReasonItems, new DialogListener.DialogItemLisenter() {
                    @Override
                    public void onDialogClick(int position) {
                        setExpressCode(position);
                    }
                });
                break;
            case R.id.btn_submit_salesreturn://提交退换货
                if (goodsRejectedBean != null)
                    mPresenter.applyAfterLogistics(goodsRejectedBean.getQualityRefundOrderMap().getIdentification() + "", goodsRejectedBean.getQualityRefundOrderMap().getReturnOrderId(), expressCode, etSelectLogisticsId.getText().toString().trim(), etSelectLogisticsMoney.getText().toString().trim(), takePicAdapter.getDatas());
                break;
            case R.id.tv_cancel_order://取消申请
                DialogUtils.showTwoBtnDialog(mContext, null, "撤销申请后您将不能重新发起售后申请，是否确认撤销？", null, "确认", new DialogListener.DialogClickLisenter() {
                    @Override
                    public void onDialogClick(int type) {
                        if (type == CLICK_SURE) {//确定
                            if (goodsRejectedBean != null)
                                mPresenter.cancelOrderId(goodsRejectedBean.getQualityRefundOrderMap().getReturnOrderId(), goodsRejectedBean.getQualityRefundOrderMap().getIdentification() + "");
                        }
                    }

                });
                break;
        }
    }

    /**
     * 选择物流公司
     *
     * @param position
     */
    private void setExpressCode(int position) {//
        deafultResonMenu = mMenuReasonItems.get(position);
        tvSelectLogisticsName.setText(deafultResonMenu.mOperName);
        expressCode = goodsRejectedBean.getLogisticsLca().get(position).getParameterName();
    }

    @Override
    public void takeCancel() {
        super.takeCancel();
        msgToast("取消选择图片");
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
        msgToast(msg);
    }

    @Override
    public void takeSuccess(final TResult result) {
        super.takeSuccess(result);
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String picUrl = result.getImage().getOriginalPath();
                Log.e("ssssssss", picUrl + "   ss");
                if (picUrl.endsWith(".jpg") || picUrl.endsWith(".JPG") || picUrl.endsWith(".png") || picUrl.endsWith(".PNG"))
                    takePicAdapter.addPic(result.getImage().getCompressPath());
                else
                    msgToast("请选择jpg或png图片");
            }
        });

    }

    @Override
    protected void initData() {
        Bundle bundle = getArguments();
        if (bundle != null)
            goodsRejectedBean = (GoodsRejectedBean) getArguments().getSerializable("goodsRejectedBean");
        if (goodsRejectedBean != null) {
            if (goodsRejectedBean.getLogisticsLca() != null && goodsRejectedBean.getLogisticsLca().size() > 0) {//有物流信息
                for (int i = 0; i < goodsRejectedBean.getLogisticsLca().size(); i++) {//对话框菜单
                    mMenuReasonItems.add(i, new DialogMenuItem(goodsRejectedBean.getLogisticsLca().get(i).getParameterDes(), 0));
                }
                setExpressCode(0);
            }
            tvSalesreturnRecipientsPostcode.setText(goodsRejectedBean.getQualityRefundOrderMap().getZipCode());
            tvRecipientsAddress.setText(goodsRejectedBean.getQualityRefundOrderMap().getAddressInfo());
            tvRecipientsPhone.setText(goodsRejectedBean.getQualityRefundOrderMap().getRecipientPhone());
            tvRecipientsName.setText(goodsRejectedBean.getQualityRefundOrderMap().getRecipientName());
            tvSalesreturnTime.setText(Html.fromHtml("剩余<font color='#fe4c40'>" + goodsRejectedBean.getQualityRefundOrderMap().getCountDownDays() + "</font>天<font color='#fe4c40'>" + goodsRejectedBean.getQualityRefundOrderMap().getCountDownHours() + "</font>时申请未达成。当前申请会自动关闭。"));
        } else {

        }
        takePicAdapter = new TakePicAdapter(mContext, mPresenter, "上传凭证 选择图片", 3);
        nsgvItemAddPic.setAdapter(takePicAdapter);
        etSelectLogisticsMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable edt) {
                String temp = edt.toString();
                int posDot = temp.indexOf(".");
                if (posDot == -1) {
                    return;
                } else if (posDot == 0) {
                    edt.delete(0, 1);
                    return;
                }
                int lastIndex=temp.lastIndexOf(".");
                if (lastIndex!=posDot){
                    edt.delete(lastIndex,lastIndex+1);
                    return;
                }
                if (temp.length() - posDot - 1 > 2) {
                    edt.delete(posDot + 3, posDot + 4);
                }
            }
        });
    }

    @Override
    public String getPageName() {
        return "退货给卖家";
    }
}