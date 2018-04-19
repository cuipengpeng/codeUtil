package com.jfbank.qualitymarket.activity;

import com.jfbank.qualitymarket.ActivitysManage;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.base.BaseActivity;
import com.jfbank.qualitymarket.util.CommonUtils;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import qiu.niorgai.StatusBarCompat;

/**
 * 备注对应的页面
 * @author 彭爱军
 * @date 2016年8月10日
 */
public class RemarkActivity extends BaseActivity implements OnClickListener {

	RelativeLayout rlTitle;
	/**
	 * 显示标题内容
	 */
	TextView tvTitle;
	/**
	 * 返回
	 */
	ImageView ivBack;
	TextView tvRightMmenu;
    /**备注的内容*/
    private EditText mRemark_et_content;
    /**备注内容提示的信息*/
	private String mIntentComment;
	private final int resultCode = 200;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_remark);
		ActivitysManage.getActivitysManager().addActivity(this);
		bindViews();
	}
	
	
	/**
	 * 初始化View以及设置监听
	 */
    private void bindViews() {
		rlTitle = (RelativeLayout) findViewById(R.id.rl_title);
		ivBack = (ImageView) findViewById(R.id.iv_back);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvRightMmenu= (TextView) findViewById(R.id.tv_right_menu);
		CommonUtils.setTitle(this, rlTitle);
		mRemark_et_content = (EditText) findViewById(R.id.remark_et_content);
        tvTitle.setText(R.string.str_pagename_remark);
		tvRightMmenu.setVisibility(View.VISIBLE);
		tvRightMmenu.setCompoundDrawables(null,null,null,null);
		tvRightMmenu.setTextSize(14f);
		tvRightMmenu.setText("保存");
        ivBack.setOnClickListener(this);
		tvRightMmenu.setOnClickListener(this);
        Intent intent = getIntent();
        if (null != intent) {
        	 mIntentComment = intent.getStringExtra(ConfirmOrderActivity.KEY_OF_COMMENT);
			
		}
        if (!ConfirmOrderActivity.COMMENT_NO_INFO.equals(mIntentComment) && null != mIntentComment) {
        	mRemark_et_content.setText(mIntentComment);
		}
        
    }
    
    @Override
    public void onClick(View v) {
    	switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.tv_right_menu:		//保存
			//Toast.makeText(this, "正在建设中。。。", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(this, ConfirmOrderActivity.class);
			intent.putExtra(ConfirmOrderActivity.KEY_OF_COMMENT, mRemark_et_content.getText().toString().trim());
			setResult(resultCode, intent );
			finish();
			break;

		default:
			break;
		}
    	
    }
    
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected String getPageName() {
		return getString(R.string.str_pagename_remark);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivitysManage.getActivitysManager().finishActivity(this);
	}


}
