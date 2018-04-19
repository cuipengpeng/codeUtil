package com.jfbank.qualitymarket.fragment;

import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.base.BaseFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
/**
 * 申请额度申请提交完成页面
 * @author 彭爱军
 * @date 2016年8月18日
 */
public class ApplySubmitfinishFragment extends BaseFragment{

	private View view;
	/**我知道啦*/
	private Button mBtnKnow;
	
	private OnClickBtnListen mOnClickBtnListen;
	
	/** 添加一个接口,设置点击监听的回调 */
	public interface OnClickBtnListen {

		public void next(); // 进入下一个流程
	}

	public void setOnClickBtnListen(OnClickBtnListen mOnClickBtnListen) {
		this.mOnClickBtnListen = mOnClickBtnListen;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 view = inflater.inflate(R.layout.fragment_apply_submit_finish, container, false); 
		 bindViews();
		return view;
	}

	/**
	 * 初始化View以及设置监听
	 */
	private void bindViews() {
		mBtnKnow = (Button) view.findViewById(R.id.apply_finish_btn_know);
		mBtnKnow.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//TODO 需要设置回调监听 mOnClickBtnListen.next();
				//Toast.makeText(getActivity(), "点击了我知道啦", Toast.LENGTH_SHORT).show();
				mOnClickBtnListen.next();
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public String getPageName() {
		return getString(R.string.str_pagename_applysubmitfinish);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
