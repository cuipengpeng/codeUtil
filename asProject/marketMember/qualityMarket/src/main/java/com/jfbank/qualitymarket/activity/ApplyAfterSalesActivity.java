package com.jfbank.qualitymarket.activity;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentTransaction;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.base.BaseMvpActivity;
import com.jfbank.qualitymarket.fragment.SubmitSalesFragment;
import com.jfbank.qualitymarket.model.AfterSalesReasonBean;
import com.jfbank.qualitymarket.mvp.ApplyAfterSalesMVP;
import com.jfbank.qualitymarket.util.ConstantsUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 功能：申请退换货<br>
 * 作者：赵海<br>
 * 时间： 2017/4/20 0020<br>.
 * 版本：1.2.0
 */

public class ApplyAfterSalesActivity extends BaseMvpActivity<ApplyAfterSalesMVP.Presenter, ApplyAfterSalesMVP.Model> implements RadioGroup.OnCheckedChangeListener, ApplyAfterSalesMVP.View {

    @InjectView(R.id.rg_select_aftersales_type)
    RadioGroup rgSelectAftersalesType;
    @InjectView(R.id.ll_content)
    LinearLayout llContent;
    SubmitSalesFragment salesReturnFrag;
    SubmitSalesFragment salesChangeFrag;
    @InjectView(R.id.rb_type_salesreturn)
    RadioButton rbTypeSalesreturn;
    @InjectView(R.id.rb_type_changesales)
    RadioButton rbTypeChangesales;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_apply_after_sales;
    }

    @Override
    protected void initView() {


    }

    @Override
    protected void initData() {

        int typeOfSupport = ((AfterSalesReasonBean) getIntent().getSerializableExtra(ConstantsUtil.EXTRA_AFTERSALESREASONBEAN)).getTypeOfSupport();
        int afterSalesType = getIntent().getIntExtra(ConstantsUtil.EXTRA_AFTERSALES_TYPE, 1);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        salesReturnFrag = new SubmitSalesFragment();
        Bundle salesReturnBundle = new Bundle();
        salesReturnBundle.putInt(ConstantsUtil.EXTRA_AFTERSALES_TYPE, 1);
        salesReturnBundle.putSerializable(ConstantsUtil.EXTRA_AFTERSALESREASONBEAN, getIntent().getSerializableExtra(ConstantsUtil.EXTRA_AFTERSALESREASONBEAN));
        salesReturnBundle.putString(ConstantsUtil.EXTRA_ORDERID, getIntent().getStringExtra(ConstantsUtil.EXTRA_ORDERID));
        salesReturnBundle.putString(ConstantsUtil.EXTRA_FIRSTPAYMENT, getIntent().getStringExtra(ConstantsUtil.EXTRA_FIRSTPAYMENT));
        salesReturnFrag.setArguments(salesReturnBundle);
        transaction.add(R.id.ll_content, salesReturnFrag);


        salesChangeFrag = new SubmitSalesFragment();
        Bundle salesChangeBundle = new Bundle();
        salesChangeBundle.putInt(ConstantsUtil.EXTRA_AFTERSALES_TYPE, 2);
        salesChangeBundle.putSerializable(ConstantsUtil.EXTRA_AFTERSALESREASONBEAN, getIntent().getSerializableExtra(ConstantsUtil.EXTRA_AFTERSALESREASONBEAN));
        salesChangeBundle.putString(ConstantsUtil.EXTRA_ORDERID, getIntent().getStringExtra(ConstantsUtil.EXTRA_ORDERID));
        salesChangeBundle.putString(ConstantsUtil.EXTRA_FIRSTPAYMENT, getIntent().getStringExtra(ConstantsUtil.EXTRA_FIRSTPAYMENT));
        salesChangeFrag.setArguments(salesChangeBundle);
        transaction.add(R.id.ll_content, salesChangeFrag);
        if (typeOfSupport == 1 || typeOfSupport == 2) {
            rbTypeSalesreturn.setEnabled(true);
        } else {
            rbTypeSalesreturn.setEnabled(false);
        }
        if (typeOfSupport == 1 || typeOfSupport == 3) {
            rbTypeChangesales.setEnabled(true);
        } else {
            rbTypeChangesales.setEnabled(false);
        }
        transaction.commit();
        if (typeOfSupport == 1) {
            initSelect(afterSalesType);
        } else if (typeOfSupport == 2) {
            initSelect(1);
        } else if (typeOfSupport == 3) {
            initSelect(2);
        } else {
            initSelect(-1);
        }
        tvTitle.setText(getResources().getString(R.string.str_pagename_afersales));
        rgSelectAftersalesType.setOnCheckedChangeListener(this);
    }

    private void initSelect(int afterSalesType) {
        if (afterSalesType == 2) {//售后换货申请
            seletAfterSales(1);
            rgSelectAftersalesType.check(R.id.rb_type_changesales);
        } else if (afterSalesType == 1) {
            seletAfterSales(0);
            rgSelectAftersalesType.check(R.id.rb_type_salesreturn);
        } else {
            seletAfterSales(2);
        }
    }

    @Override
    protected String getPageName() {
        return getString(R.string.str_pagename_submit_salesapply);
    }

    public void seletAfterSales(int position) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (position == 0) {
            transaction.hide(salesChangeFrag).show(salesReturnFrag);
        } else if (position == 1) {
            transaction.hide(salesReturnFrag).show(salesChangeFrag);
        } else {
            transaction.hide(salesReturnFrag).hide(salesChangeFrag);
        }
        transaction.commit();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        if (group.getCheckedRadioButtonId() == R.id.rb_type_salesreturn) {//退货
            seletAfterSales(0);
        } else {//换货
            seletAfterSales(1);
        }
    }
}