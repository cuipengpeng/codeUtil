package com.jfbank.qualitymarket.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.base.BaseActivity;
import com.jfbank.qualitymarket.util.CommonUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import qiu.niorgai.StatusBarCompat;

/**
 * 发票信息页面
 * @author 崔朋朋
 */
public class InvoiceInfoActivity extends BaseActivity {
	@InjectView(R.id.rl_title)
	RelativeLayout rlTitle;
	@InjectView(R.id.tv_title)
	TextView tvTitle;
	@InjectView(R.id.tv_right_menu)
	TextView tvRightMmenu;

	@InjectView(R.id.tv_invoiceInfoActivity_invoiceTypeNoInvoice)
	TextView invoiceTypeNoInvoiceTextView;
	@InjectView(R.id.tv_invoiceInfoActivity_invoiceTypePager)
	TextView invoiceTypePagerTextView;
	@InjectView(R.id.rl_invoiceInfoActivity_personInvoice)
	RelativeLayout personInvoiceRelativeLayout;
	@InjectView(R.id.rl_invoiceInfoActivity_companyInvoice)
	RelativeLayout companyInvoiceRelativeLayout;
	@InjectView(R.id.tv_invoiceInfoActivity_personInvoice)
	TextView personInvoiceTextView;
	@InjectView(R.id.tv_invoiceInfoActivity_companyInvoice)
	TextView companyInvoiceTextView;
	@InjectView(R.id.et_invoiceInfoActivity_companyName)
	EditText companyNameEditText;
	@InjectView(R.id.tv_invoiceInfoActivity_invoiceDetail)
	TextView invoiceDetailTextView;
	@InjectView(R.id.tv_invoiceInfoActivity_invoiceConsumerGoods)
	TextView invoiceConsumerGoodsTextView;
	@InjectView(R.id.tv_invoiceInfoActivity_invoiceOfficeSupply)
	TextView invoiceOfficeSupplyTextView;
	@InjectView(R.id.tv_invoiceInfoActivity_invoicePCComponents)
	TextView invoicePCComponentsTextView;

	@InjectView(R.id.btn_invoiceInfoActivity_confirm)
	Button confirmButton;

	private Resources resources;
	private Drawable selectedCheckBoxDrawable;
	private Drawable normalCheckBoxDrawable;

	
	private String selectedInvoiceType = ConfirmOrderActivity.NO_INVOICE_INFO;
	private String selectedInvoiceTitle;
	private String selectedInvoiceContent;
	public static final String INVOICE_INFO_SEPARATOR = ",";
	public static final String INVOICE_TITLE_PERSON = "个人";
	public static final String INVOICE_TITLE_COMPANY = "单位";
	public static final String INVOICE_CONTENT_DETAIL = "明细";
	public static final String INVOICE_CONTENT_CONSUMER_GOODS = "耗材";
	public static final String INVOICE_CONTENT_OFFICE_SUPPLY = "办公用品";
	public static final String INVOICE_CONTENT_PC_COMPONENTS = "电脑配件";

	@OnClick({ R.id.rl_invoiceInfoActivity_companyInvoice, R.id.rl_invoiceInfoActivity_personInvoice,
			R.id.tv_invoiceInfoActivity_invoiceDetail, R.id.tv_invoiceInfoActivity_invoiceConsumerGoods,
			R.id.tv_invoiceInfoActivity_invoiceOfficeSupply, R.id.tv_invoiceInfoActivity_invoicePCComponents,
			R.id.tv_right_menu, R.id.btn_invoiceInfoActivity_confirm,
			R.id.iv_back,R.id.tv_invoiceInfoActivity_invoiceTypeNoInvoice,
			R.id.tv_invoiceInfoActivity_invoiceTypePager})
	public void onViewClick(View v) {
		switch (v.getId()) {
		case R.id.tv_invoiceInfoActivity_invoiceTypeNoInvoice:
			//没有发票信息
			selectedInvoiceType = ConfirmOrderActivity.NO_INVOICE_INFO;
			setDefaultInvoiceTitleDrawable();
			setDefaultInvoiceContentDrawable();
			setNoInvoiceView();
			break;
		case R.id.tv_invoiceInfoActivity_invoiceTypePager:
			//纸质发票
			selectedInvoiceType = ConfirmOrderActivity.PAGER_INVOICE_INFO;
			setPagerInvoice();
			break;
		case R.id.rl_invoiceInfoActivity_companyInvoice:
			//公司发票
			setDefaultInvoiceTitleDrawable();
			companyInvoiceTextView.setCompoundDrawables(selectedCheckBoxDrawable, null, null, null);
			companyNameEditText.setVisibility(View.VISIBLE);
			selectedInvoiceTitle = INVOICE_TITLE_COMPANY;
			break;
		case R.id.rl_invoiceInfoActivity_personInvoice:
			//个人发票
			setDefaultInvoiceTitleDrawable();
			personInvoiceTextView.setCompoundDrawables(selectedCheckBoxDrawable, null, null, null);
			companyNameEditText.setVisibility(View.GONE);
			selectedInvoiceTitle = INVOICE_TITLE_PERSON;
			break;
		case R.id.tv_invoiceInfoActivity_invoiceDetail:
			//发票详情
			setDefaultInvoiceContentDrawable();
			invoiceDetailTextView.setCompoundDrawables(selectedCheckBoxDrawable, null, null, null);
			selectedInvoiceContent = INVOICE_CONTENT_DETAIL;
			break;
		case R.id.tv_invoiceInfoActivity_invoiceConsumerGoods:
			//发票-电脑耗材
			setDefaultInvoiceContentDrawable();
			invoiceConsumerGoodsTextView.setCompoundDrawables(selectedCheckBoxDrawable, null, null, null);
			selectedInvoiceContent = INVOICE_CONTENT_CONSUMER_GOODS;

			break;
		case R.id.tv_invoiceInfoActivity_invoiceOfficeSupply:
			//发票-办公用品
			setDefaultInvoiceContentDrawable();
			invoiceOfficeSupplyTextView.setCompoundDrawables(selectedCheckBoxDrawable, null, null, null);
			selectedInvoiceContent = INVOICE_CONTENT_OFFICE_SUPPLY;

			break;
		case R.id.tv_invoiceInfoActivity_invoicePCComponents:
			//发票-电脑周边
			setDefaultInvoiceContentDrawable();
			invoicePCComponentsTextView.setCompoundDrawables(selectedCheckBoxDrawable, null, null, null); 
			selectedInvoiceContent = INVOICE_CONTENT_PC_COMPONENTS;

			break;
		case R.id.tv_right_menu:
			//发票协议
			final Dialog dialog = new Dialog(this, R.style.protocalDialog);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(true); 
			View view = View.inflate(this, R.layout.protocal_text_view, null);
			view.findViewById(R.id.tv_invoiceInfoActivity_know).setOnClickListener(new View.OnClickListener(){		//我知道了按钮

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
				
			});;
			TextView textView = (TextView) view.findViewById(R.id.tv_invoiceInfoActivity_invoiceProtocalContent);
			textView.setMovementMethod(ScrollingMovementMethod.getInstance());
			dialog.setContentView(view);
			dialog.setTitle("");
			dialog.show();
			break;
		case R.id.iv_back:
			finish();  
			break;
		case R.id.btn_invoiceInfoActivity_confirm:
			//确认
			String companyName = companyNameEditText.getText().toString();
			if (selectedInvoiceTitle.equals(INVOICE_TITLE_COMPANY) && ConfirmOrderActivity.PAGER_INVOICE_INFO.equals(selectedInvoiceType)) {
				if (companyName == null || "".equals(companyName)) {
					Toast.makeText(this, "请填写发票公司名称", Toast.LENGTH_SHORT).show();
					return;
				}
				selectedInvoiceTitle = selectedInvoiceTitle + INVOICE_INFO_SEPARATOR + companyName;
			}

			Intent intent = new Intent();
			if(ConfirmOrderActivity.NO_INVOICE_INFO.equals(selectedInvoiceType)){
				intent.putExtra(ConfirmOrderActivity.KEY_OF_INVOICE_INFO,
						ConfirmOrderActivity.NO_INVOICE_INFO);
				
			}else {
				intent.putExtra(ConfirmOrderActivity.KEY_OF_INVOICE_INFO,
						selectedInvoiceTitle + INVOICE_INFO_SEPARATOR + selectedInvoiceContent);
			}
			setResult(102, intent);
			finish();

			break;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invoice_info);
		ButterKnife.inject(this);
		tvTitle.setText(R.string.str_pagename_invoiceinfo);
		tvRightMmenu.setText("发票须知");
		tvRightMmenu.setTextSize(14f);
		tvRightMmenu.setVisibility(View.VISIBLE);
		tvRightMmenu.setCompoundDrawables(null, null, null, null);
		CommonUtils.setTitle(this,rlTitle);
		resources = getResources();
		selectedCheckBoxDrawable = resources.getDrawable(R.drawable.invoice_info_activity_checkbox_selected);
		normalCheckBoxDrawable = resources.getDrawable(R.drawable.invoice_info_activity_checkbox_normal);
		setDefaultInvoiceTitleDrawable();
		setDefaultInvoiceContentDrawable();
		invoiceTypeNoInvoiceTextView.setText(ConfirmOrderActivity.NO_INVOICE_INFO);
		invoiceTypePagerTextView.setText(ConfirmOrderActivity.PAGER_INVOICE_INFO);
		
		Intent intent = getIntent();
		String invoiceInfo = intent.getStringExtra(ConfirmOrderActivity.KEY_OF_INVOICE_INFO);
		String[] invoiceInfoArray = invoiceInfo.split("\\" + INVOICE_INFO_SEPARATOR);
		selectedCheckBoxDrawable.setBounds(0, 0, selectedCheckBoxDrawable.getMinimumWidth(),
				selectedCheckBoxDrawable.getMinimumHeight());
		if (invoiceInfo.equals(ConfirmOrderActivity.NO_INVOICE_INFO)) {
			//没有发票信息
			selectedInvoiceType = ConfirmOrderActivity.NO_INVOICE_INFO;
			setNoInvoiceView();
		} else if (invoiceInfo.contains(INVOICE_INFO_SEPARATOR)) {
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			
			selectedInvoiceType = ConfirmOrderActivity.PAGER_INVOICE_INFO;
			setPagerInvoice();
			selectedInvoiceTitle = invoiceInfoArray[0];
			selectedInvoiceContent = invoiceInfoArray[invoiceInfoArray.length - 1];
			
			//设置发票title
			if (INVOICE_TITLE_PERSON.equals(selectedInvoiceTitle)) {
				personInvoiceTextView.setCompoundDrawables(selectedCheckBoxDrawable, null, null, null);
				companyNameEditText.setVisibility(View.GONE);
			} else if (INVOICE_TITLE_COMPANY.equals(selectedInvoiceTitle)) {
				companyInvoiceTextView.setCompoundDrawables(selectedCheckBoxDrawable, null, null, null);
				companyNameEditText.setVisibility(View.VISIBLE);
				companyNameEditText.setText(invoiceInfoArray[1]);
			}

			//设置发票内容
			if (INVOICE_CONTENT_DETAIL.equals(selectedInvoiceContent)) {
				invoiceDetailTextView.setCompoundDrawables(selectedCheckBoxDrawable, null, null, null);
			} else if (INVOICE_CONTENT_CONSUMER_GOODS.equals(selectedInvoiceContent)) {
				invoiceConsumerGoodsTextView.setCompoundDrawables(selectedCheckBoxDrawable, null, null, null);
			} else if (INVOICE_CONTENT_OFFICE_SUPPLY.equals(selectedInvoiceContent)) {
				invoiceOfficeSupplyTextView.setCompoundDrawables(selectedCheckBoxDrawable, null, null, null);
			} else if (INVOICE_CONTENT_PC_COMPONENTS.equals(selectedInvoiceContent)) {
				invoicePCComponentsTextView.setCompoundDrawables(selectedCheckBoxDrawable, null, null, null);
			}
		}

	}

	@Override
	protected String getPageName() {
		return getString(R.string.str_pagename_invoiceinfo);
	}

	/**
	 * 设置纸质发票
	 */
	private void setPagerInvoice() {
		invoiceTypePagerTextView.setBackgroundResource(R.drawable.invoice_info_activity_red_border);
		invoiceTypeNoInvoiceTextView.setBackgroundColor(Color.WHITE);
	}

	/**
	 * 设置“不开发票”的view
	 */
	private void setNoInvoiceView() {
		invoiceTypeNoInvoiceTextView.setBackgroundResource(R.drawable.invoice_info_activity_red_border);
		invoiceTypePagerTextView.setBackgroundColor(Color.WHITE);
		personInvoiceTextView.setCompoundDrawables(selectedCheckBoxDrawable, null, null, null);
		companyNameEditText.setVisibility(View.GONE);
		invoiceDetailTextView.setCompoundDrawables(selectedCheckBoxDrawable, null, null, null);
		selectedInvoiceTitle = INVOICE_TITLE_PERSON;
		selectedInvoiceContent = INVOICE_CONTENT_DETAIL;
	}

	/**
	 * 设置默认的title的drawable
	 */
	public void setDefaultInvoiceTitleDrawable() {
		normalCheckBoxDrawable.setBounds(0, 0, normalCheckBoxDrawable.getMinimumWidth(),
				normalCheckBoxDrawable.getMinimumHeight());
		personInvoiceTextView.setCompoundDrawables(normalCheckBoxDrawable, null, null, null);
		companyInvoiceTextView.setCompoundDrawables(normalCheckBoxDrawable, null, null, null);
	}

	/**
	 * 设置默认的content的drawable
	 */
	public void setDefaultInvoiceContentDrawable() {
		normalCheckBoxDrawable.setBounds(0, 0, normalCheckBoxDrawable.getMinimumWidth(),
				normalCheckBoxDrawable.getMinimumHeight());
		invoiceDetailTextView.setCompoundDrawables(normalCheckBoxDrawable, null, null, null);
		invoiceConsumerGoodsTextView.setCompoundDrawables(normalCheckBoxDrawable, null, null, null);
		invoiceOfficeSupplyTextView.setCompoundDrawables(normalCheckBoxDrawable, null, null, null);
		invoicePCComponentsTextView.setCompoundDrawables(normalCheckBoxDrawable, null, null, null);
	}

}
