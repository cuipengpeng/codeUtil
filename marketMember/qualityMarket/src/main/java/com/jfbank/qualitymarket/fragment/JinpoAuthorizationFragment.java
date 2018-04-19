//package com.jfbank.qualitymarket.fragment;
//
//import org.apache.http.Header;
//
//import com.alibaba.fastjson.JSON;
//import com.jfbank.qualitymarket.AppContext;
//import com.jfbank.qualitymarket.R;
//import com.jfbank.qualitymarket.activity.ApplyAmountActivity;
//import com.jfbank.qualitymarket.activity.LoginActivity;
//import com.jfbank.qualitymarket.base.BaseFragment;
//import com.jfbank.qualitymarket.dao.StoreService;
//import com.jfbank.qualitymarket.model.JinpoAuthorizationInitBean;
//import com.jfbank.qualitymarket.model.JinpoAuthorizationInitBean.JinpoBean;
//import com.jfbank.qualitymarket.model.SaveBaseInfoBean;
//import com.jfbank.qualitymarket.net.HttpRequest;
//import com.jfbank.qualitymarket.util.ConstantsUtil;
//import com.jfbank.qualitymarket.widget.LoadingAlertDialog;
//import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
// import java.util.HashMap; import java.util.Map;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.RelativeLayout;
//import android.widget.Toast;
//
///**
// * 社保授权页面
// * @author 彭爱军
// * @date 2016年8月18日
// */
//public class JinpoAuthorizationFragment extends BaseFragment{
//	private View view;
//	/**社保卡号*/
//	private EditText mJinpo_authorization_et_code;
//	/**社保密码*/
//    private EditText mJinpo_authorization_et_password;
//    /**下一步*/
//    private Button mJinpo_authorization_btn_next;
//    /**社保号*/
//    private RelativeLayout mJinpo_authorization_rl_code;
//    /**密码*/
//    private RelativeLayout mJinpo_authorization_rl_passwrord;
//    /**个人编号*/
//    private RelativeLayout mJinpo_authorization_rl_number;
//    /**个人编号*/
//    private EditText mJinpo_authorization_et_number;
//	private OnClickBtnListen mOnClickBtnListen;
//	protected ApplyAmountActivity mContext;
//	private JinpoBean mJinpoBean;
//	/**网编请求时加载框*/
//	private LoadingAlertDialog mDialog;
//	private boolean isSecurity;		//社保号
//	private boolean isNeedPwd;			//社保密码
//	private boolean isPersonNo;		//个人编号
//    private class MyTextWatcher implements TextWatcher{
//
//		private String mCode;
//		private String mPassword;
//		private String mNumber;
//
//		@Override
//		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//			// TODO Auto-generated method stub
//
//		}
//
//		@Override
//		public void onTextChanged(CharSequence s, int start, int before, int count) {
//			mCode = mJinpo_authorization_et_code.getText().toString().trim();
//			mPassword = mJinpo_authorization_et_password.getText().toString().trim();
//			mNumber = mJinpo_authorization_et_number.getText().toString().trim();
//			if (isSecurity) {
//				mCode = "122222222222232232323";
//			}
//			if (isNeedPwd) {
//				mPassword = "232332323";
//			}
//			if (isPersonNo) {
//				mNumber = "232323232";
//			}
//			if (mCode.length() < 12 || mPassword.length() < 6 || mNumber.length() < 6) {
//				mJinpo_authorization_btn_next.setEnabled(false);
//				mJinpo_authorization_btn_next.setBackgroundResource(R.drawable.login_page_button_disabled);
//			}else{
//				mJinpo_authorization_btn_next.setEnabled(true);
//				mJinpo_authorization_btn_next.setBackgroundResource(R.drawable.button_selector);
//			}
//		}
//
//		@Override
//		public void afterTextChanged(Editable s) {
//			// TODO Auto-generated method stub
//
//		}
//
//    }
//
//    /** 添加一个接口,设置点击监听的回调 */
//	public interface OnClickBtnListen {
//
//		public void next(int step); // 进入下一个流程
//	}
//
//	public void setOnClickBtnListen(OnClickBtnListen mOnClickBtnListen) {
//		this.mOnClickBtnListen = mOnClickBtnListen;
//	}
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		mContext = (ApplyAmountActivity) getActivity();
//	}
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		 view = inflater.inflate(R.layout.fragment_jinpo_authorization, container, false);
//		 bindViews();
//		return view;
//	}
//
//	/**
//	 * 初始化View以及设置监听
//	 */
//    private void bindViews() {
//        mJinpo_authorization_et_code = (EditText) view.findViewById(R.id.jinpo_authorization_et_code);
//        mJinpo_authorization_et_password = (EditText) view.findViewById(R.id.jinpo_authorization_et_password);
//        mJinpo_authorization_btn_next = (Button) view.findViewById(R.id.jinpo_authorization_btn_next);
//        mJinpo_authorization_rl_code = (RelativeLayout) view.findViewById(R.id.jinpo_authorization_rl_code);
//        mJinpo_authorization_rl_passwrord = (RelativeLayout) view.findViewById(R.id.jinpo_authorization_rl_passwrord);
//        mJinpo_authorization_rl_number = (RelativeLayout) view.findViewById(R.id.jinpo_authorization_rl_number);
//        mJinpo_authorization_et_number = (EditText) view.findViewById(R.id.jinpo_authorization_et_number);
//
//        mJinpo_authorization_btn_next.setEnabled(false);
//        mJinpo_authorization_et_code.addTextChangedListener(new MyTextWatcher());
//        mJinpo_authorization_et_password.addTextChangedListener(new MyTextWatcher());
//        mJinpo_authorization_et_number.addTextChangedListener(new MyTextWatcher());
//        mJinpo_authorization_btn_next.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO 首先需要进行网络请求。需要设置回调监听
//				requestCheckSecurity4creditline();
//			}
//		});
//    }
//
//    /**
//     * 请求提交社保数据
//     */
//	protected void requestCheckSecurity4creditline() {
//		if (null == mDialog) {
//    		mDialog = new LoadingAlertDialog(mContext);
//		}
//    	mDialog.show(ConstantsUtil.NETWORK_REQUEST_IN);
//
//		Map<String,String> params = new HashMap<>();
//
//		params.put("ver", AppContext.getAppVersionName(mContext));
//		params.put("Plat", ConstantsUtil.PLAT);
//
//		params.put("uid", AppContext.user.getUid());
//		params.put("token", AppContext.user.getToken());
//
//		params.put("areaCode", mJinpoBean.getAreaCode());
//		params.put("name", mJinpoBean.getName());
//		params.put("idNumber", mJinpoBean.getIdNumber());
//
//		params.put("personNo", mJinpo_authorization_et_number.getText().toString());
//		params.put("insuranceNo", mJinpo_authorization_et_code.getText().toString());
//		params.put("password", mJinpo_authorization_et_password.getText().toString());
//
//		Log.e("TAG", params.toString());
//
//		HttpRequest.checkSecurity4creditline(mContext,params, new AsyncResponseCallBack() {
//
//			@Override
//			public void onResult(String arg2) {
//				if (mDialog.isShowing()) {
//					mDialog.dismiss();
//				}
//				if (null != arg2 &&  arg2.length()> 0) {
//					Log.e("TAG", new String(arg2));
//					 explainJson(new String(arg2));
//				}
//
//			}
//
//			@Override
//			public void onFailed(String path, String msg) {
//				if (mDialog.isShowing()) {
//					mDialog.dismiss();
//				}
//				Toast.makeText(mContext, ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER, Toast.LENGTH_SHORT).show();
//
//			}
//		});
//	}
//
//	/**
//	 * 解释社保提交的json的数据
//	 * @param json
//	 */
//	protected void explainJson(String json) {
//		SaveBaseInfoBean bean = JSON.parseObject(json, SaveBaseInfoBean.class);
//		if (null != bean && ConstantsUtil.RESPONSE_SUCCEED == Integer.parseInt(bean.getStatus())) {
//			mOnClickBtnListen.next(Integer.parseInt(bean.getData().getStep()));
//		}else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == Integer.parseInt(bean.getStatus())) {
//			showDialog(bean.getStatusDetail());
//		}else{
//			Toast.makeText(mContext, bean.getStatusDetail(), Toast.LENGTH_SHORT).show();
//		}
//	}
//	/**
//	 * token失效时弹出对话框
//	 * @param content
//	 */
//	private void showDialog(String content) {
//		final PopDialogFragment dialog = PopDialogFragment.newDialog(false, false,null, content, null,null, "确定");
//		dialog.setOnClickListen(new PopDialogFragment.OnClickListen() {
//
//			@Override
//			public void rightClick() {
//				new StoreService(mContext).clearUserInfo();
//				Intent loginIntent = new Intent(mContext.getApplication(), LoginActivity.class);
//				loginIntent.putExtra(LoginActivity.KEY_OF_COME_FROM, LoginActivity.TOKEN_FAIL_TAG);
//				startActivity(loginIntent);
//				dialog.dismiss();
//			}
//
//			@Override
//			public void leftClick() {
//				dialog.dismiss();
//			}
//		});
//		dialog.show(mContext.getSupportFragmentManager(), "TAG");
//	}
//	@Override
//	public void onResume() {
//		super.onResume();
//		requestJinpoAuthorization();
//	}
//
//	@Override
//	public String getPageName() {
//		return getString(R.string.str_pagename_jinpoauth_frag);
//	}
//
//	/**
//	 * 请求为社保验证准备数据
//	 */
//	private void requestJinpoAuthorization() {
//		if (null == mDialog) {
//    		mDialog = new LoadingAlertDialog(mContext);
//		}
//    	mDialog.show(ConstantsUtil.NETWORK_REQUEST_IN);
//
//		Map<String,String> params = new HashMap<>();
//
//		params.put("ver", AppContext.getAppVersionName(mContext));
//		params.put("Plat", ConstantsUtil.PLAT);
//
//		params.put("uid", AppContext.user.getUid());
//		params.put("token", AppContext.user.getToken());
//
//		Log.e("TAG", params.toString());
//
//		HttpRequest.preCheckSecurity4creditline(mContext,params, new AsyncResponseCallBack() {
//
//			@Override
//			public void onResult(String arg2) {
//				if (mDialog.isShowing()) {
//					mDialog.dismiss();
//				}
//				if (null != arg2 &&  arg2.length()> 0) {
//					Log.e("TAG", new String(arg2));
//					 explainJinpoInitJson(new String(arg2));
//				}
//
//			}
//
//			@Override
//			public void onFailed(String path, String msg) {
//				if (mDialog.isShowing()) {
//					mDialog.dismiss();
//				}
//				Toast.makeText(mContext, ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER, Toast.LENGTH_SHORT).show();
//
//			}
//		});
//	}
//
//	/**
//	 * 解释社保初始化数据的json
//	 * @param json
//	 */
//	protected void explainJinpoInitJson(String json) {
//		JinpoAuthorizationInitBean bean = JSON.parseObject(json, JinpoAuthorizationInitBean.class);
//		if (null != bean && ConstantsUtil.RESPONSE_SUCCEED == Integer.parseInt(bean.getStatus())) {
//			if (null != bean.getData()) {
//				 mJinpoBean = bean.getData();
//				 setShowView();
//			}else{
//				Toast.makeText(mContext, bean.getStatusDetail(), Toast.LENGTH_SHORT).show();
//			}
//		}else{
//			Toast.makeText(mContext, bean.getStatusDetail(), Toast.LENGTH_SHORT).show();
//		}
//	}
//
//	/**
//	 * 设置显示相应的View
//	 */
//	private void setShowView() {
//		if (null != mJinpoBean) {
//			if (mJinpoBean.isNeedSecurityNo()) {		//是否显示社保卡号
//				mJinpo_authorization_rl_code.setVisibility(View.VISIBLE);
//			}else{
//				mJinpo_authorization_rl_code.setVisibility(View.GONE);
//				isSecurity = true;
//			}
//			if (mJinpoBean.isNeedPwd()) {			//是否显示密码
//				mJinpo_authorization_rl_passwrord.setVisibility(View.VISIBLE);
//			}else{
//				mJinpo_authorization_rl_passwrord.setVisibility(View.GONE);
//				isNeedPwd = true;
//			}
//			if (mJinpoBean.isNeedPersonNo()) {	//是否需要显示个人编号
//				mJinpo_authorization_rl_number.setVisibility(View.VISIBLE);
//			}else{
//				mJinpo_authorization_rl_number.setVisibility(View.GONE);
//				isPersonNo = true;
//			}
//		}
//	}
//
//	@Override
//	public void onDestroy() {
//		// TODO Auto-generated method stub
//		super.onDestroy();
//	}
//
//}
