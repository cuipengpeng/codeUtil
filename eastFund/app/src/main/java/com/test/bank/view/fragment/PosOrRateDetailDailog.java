package com.test.bank.view.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.bean.GetOutBankCardInfoBean;
import com.test.bank.weight.holder.RateItemHolder;

import java.util.ArrayList;

/**
 * Created by 55 on 2018/1/17.
 * 持仓详情弹框或费率详情弹框
 */

public class PosOrRateDetailDailog extends DialogFragment {
    private static final String ARG_IS_RATE_DETAIL = "ARG_IS_RATE_DETAIL";

    private int type;   //是否展示费率详情
    private GetOutBankCardInfoBean data;
    RelativeLayout rlRootView;
    TextView tvTitle;
    TextView tvLeftTitle;
    TextView tvDescOfPos;
    TextView tvTitleReduceRate;
    LinearLayout llSheetRateDetail;

    TextView tvSaleOutRateDesc;

    LinearLayout llRateContainer;

    ImageView ivClose;

    public PosOrRateDetailDailog() {
    }

    /**
     * 持仓详情或者费率详情弹框
     *
     * @param type -1： 持仓详情页面  1：申购费率详情  2：卖出费率详情
     * @param bean type为-1时为null.
     * @return
     */
    public static PosOrRateDetailDailog getInstance(int type, GetOutBankCardInfoBean bean) {
        PosOrRateDetailDailog dialog = new PosOrRateDetailDailog();
        Bundle args = new Bundle();
        args.putInt(ARG_IS_RATE_DETAIL, type);
        args.putSerializable("bean", bean);
        dialog.setArguments(args);
        return dialog;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt(ARG_IS_RATE_DETAIL, -1);
            data = (GetOutBankCardInfoBean) getArguments().getSerializable("bean");
        }
    }

    DismissListener dismissListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.PosOrRateDetailDailog);
        dialog.setContentView(R.layout.dialog_pos_or_rate_detail);
        init(dialog);
        dialog.setCanceledOnTouchOutside(true);
        dismissListener = new DismissListener();
        rlRootView.setOnClickListener(dismissListener);
        ivClose.setOnClickListener(dismissListener);
        tvTitle.setOnClickListener(dismissListener);
        tvDescOfPos.setOnClickListener(dismissListener);
        llSheetRateDetail.setOnClickListener(dismissListener);

        return dialog;
    }

    class DismissListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            dismiss();
        }
    }

    private void init(Dialog dialog) {
        initView(dialog);
    }

    private void initView(Dialog dialog) {
        if (dialog != null && dialog.getWindow() != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout(dm.widthPixels, dm.heightPixels);
        }

        rlRootView = dialog.findViewById(R.id.rl_pord_rootView);
        tvTitle = dialog.findViewById(R.id.tv_posOrRateDialog_title);
        tvLeftTitle = dialog.findViewById(R.id.tv_dialog_titleLeft);
        tvTitleReduceRate = dialog.findViewById(R.id.tv_titleReduceRate);
        tvDescOfPos = dialog.findViewById(R.id.tv_posOrRateDialog_desc);
        llSheetRateDetail = dialog.findViewById(R.id.ll_sheet_rate_detail);
        llRateContainer = dialog.findViewById(R.id.ll_rateContainer);
        tvSaleOutRateDesc = dialog.findViewById(R.id.tv_saleOutRateDesc);
        ivClose = dialog.findViewById(R.id.iv_posOrRateDetail_close);

        if (type == -1) {
            tvTitle.setText("持仓成本价");
            tvDescOfPos.setVisibility(View.VISIBLE);
            llSheetRateDetail.setVisibility(View.GONE);
        } else {
            String[] lineData = new String[3];
            if (type == 1) {
                tvTitle.setText("费率详情（前端申购）");
                tvDescOfPos.setVisibility(View.GONE);
                llSheetRateDetail.setVisibility(View.VISIBLE);
                ArrayList<GetOutBankCardInfoBean.Chag_rate_list> list = data.getChag_rate_list();
                for (int i = 0; i < list.size(); i++) {
                    lineData[0] = list.get(i).getPurchase_amount_interval();
                    lineData[1] = list.get(i).getRatio();
                    lineData[2] = list.get(i).getRiscountRatio();
                    llRateContainer.addView(new RateItemHolder(i == list.size() - 1).inflateData(lineData));
                }
            } else if (type == 2) {
                tvTitle.setText("卖出费率");
                tvLeftTitle.setText("持有期限");
                tvDescOfPos.setVisibility(View.GONE);
                llSheetRateDetail.setVisibility(View.VISIBLE);
                tvTitleReduceRate.setVisibility(View.GONE);
                tvSaleOutRateDesc.setVisibility(View.VISIBLE);
                ArrayList<GetOutBankCardInfoBean.Call_rate_list> list = data.getCall_rate_list();
                for (int i = 0; i < list.size(); i++) {
                    lineData[0] = list.get(i).getPurchase_amount_interval();
                    lineData[1] = list.get(i).getRatio();
                    lineData[2] = RateItemHolder.TYPE_SALE_OUT_HIDE_DISCOUNT_RATE;
                    llRateContainer.addView(new RateItemHolder(i == list.size() - 1).inflateData(lineData));
                }
            }
        }
    }
}
