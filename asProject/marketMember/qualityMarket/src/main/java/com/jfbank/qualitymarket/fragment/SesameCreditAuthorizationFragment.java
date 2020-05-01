//package com.jfbank.qualitymarket.fragment;
//
//import java.util.HashMap;
//
//import org.apache.http.Header;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.jfbank.qualitymarket.AppContext;
//import com.jfbank.qualitymarket.R;
//import com.jfbank.qualitymarket.activity.ApplyAmountActivity;
//import com.jfbank.qualitymarket.activity.LoginActivity;
//import com.jfbank.qualitymarket.base.BaseFragment;
//import com.jfbank.qualitymarket.bean.Hidden;
//import com.jfbank.qualitymarket.bean.Param;
//import com.jfbank.qualitymarket.bean.PhoneData;
//import com.jfbank.qualitymarket.bean.RefreshParam;
//import com.jfbank.qualitymarket.dao.StoreService;
//import com.jfbank.qualitymarket.model.PhoneVerificationBean;
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
//import android.os.Handler;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.inputmethod.InputMethodManager;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
///**
// * 手机验证对应的页面 2/2
// * @author 彭爱军
// * @date 2016年8月10日
// */
//public class SesameCreditAuthorizationFragment extends BaseFragment implements OnClickListener {
//	private ApplyAmountActivity mContext;
//	/**手机号码*/
//	private EditText mSesame_credit_et_phone;
//	/**获取验证码*/
//	private Button mSesame_credit_btn_code;
//	/**提交*/
//	private Button mSesame_credit_btn_submit;
//	/**短信验证码*/
//	private EditText mSesame_credit_et_code;
//	private View view ;
//
//	private String mPhone;
//	private String mCode;
//	private OnClickBtnListen mOnClickBtnListen;
//
//	/**任务编码*/
//	private String mTaskNo ;
//	/**短信刷新或提交需要的数据*/
//	private PhoneData mData;
//	/**网编请求时加载框*/
//	private LoadingAlertDialog mDialog;
//	/**刷新或者提交短信验证的method*/
//	private String method;
//	private HashMap<String, String> map;
//
//	/**添加一个接口,设置点击监听的回调*/
//	public interface OnClickBtnListen{
//
//		public void next(int step);		//进入下一个流程
//	}
//
//	public void setmOnClickBtnListen(OnClickBtnListen mOnClickBtnListen) {
//		this.mOnClickBtnListen = mOnClickBtnListen;
//	}
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		mContext = (ApplyAmountActivity) getActivity();
//		InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
//		imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,InputMethodManager.SHOW_FORCED);
//	}
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		 view = inflater.inflate(R.layout.fragment_sesame_credit_authorization, container,false);
//		 bindViews();
//		return view;
//	}
//
//	/**
//	 * 初始化View以及设置监听
//	 */
//	private void bindViews() {
//		mSesame_credit_et_phone = (EditText) view.findViewById(R.id.sesame_credit_et_phone);
//		mSesame_credit_btn_code = (Button) view.findViewById(R.id.sesame_credit_btn_code);
//		mSesame_credit_btn_submit = (Button) view.findViewById(R.id.sesame_credit_btn_submit);
//		mSesame_credit_et_code = (EditText) view.findViewById(R.id.sesame_credit_et_code);
//
//		mSesame_credit_btn_code.setOnClickListener(this);
//		mSesame_credit_btn_submit.setOnClickListener(this);
//
//		mPhone = AppContext.user.getMobile();
//		if (null != mPhone) {
//			mSesame_credit_et_phone.setText(mPhone.subSequence(0, 3)+"****"+mPhone.substring(7, mPhone.length()));
//		}
//		mSesame_credit_et_phone.setEnabled(false);
//
//		Bundle bundle = getArguments();
//		if (null != bundle) {
//			mData = bundle.getParcelable("DATA");
//			mTaskNo = mData.getTaskNo();
//			method = mData.getNext().getMethod();
//		}
//		if ("getMessageCode".equals(method)) {
//			showState(true);
//		}else{
//			mTime--;
//			showState(false);
//		}
//	 	map = new HashMap<String, String>();
//	}
//
//	/**
//	 * 获取或刷新短信验证码的json数据
//	 * @return
//	 */
//	private String getRefreshCodeJson(){
//		if (null != map) {
//			map.clear();
//		}
//		Param[] param = mData.getNext().getParam();
//		for (int i = 0; i < param.length; i++) {
//			if ("message_code".equals(param[i].getKey())) {
//				if (!"getMessageCode".equals(method)) {				//如果method不等于getMessageCode则method是getRefresh_method即refreshMessageCode
//					method = param[i].getRefresh_method();
//				}
//				RefreshParam[] refreshParam = param[i].getRefresh_param();
//				for (int j = 0; j < refreshParam.length; j++) {
//					map.put(refreshParam[j].getKey(),refreshParam[j].getValue());
//				}
//			}else{
//				map.put(param[i].getKey(), param[i].getValue());
//			}
//		}
//		return JSONObject.toJSONString(map);
//	}
//
//	/**
//	 * 获取提交短信验证的json
//	 * @return
//	 */
//	private String getSubmitCodeJson(){
//		if (null != map) {
//			map.clear();
//		}
//		method = mData.getNext().getMethod();
//		Log.e("TAG", "methodmethod:"+method);
//	/*	if (!"submitMessageCode".equals(method)) {		//如果不等于submitMessageCode即要先获取验证码
//			Toast.makeText(mContext, "请先获取验证码", Toast.LENGTH_SHORT).show();
//			return null;
//		}*/
//		Param[] param = mData.getNext().getParam();
//		for (int i = 0; i < param.length; i++) {
//			if ("message_code".equals(param[i].getKey())) {
//				map.put(param[i].getKey(),mCode);
//			}else{
//				map.put(param[i].getKey(), param[i].getValue());
//			}
//		}
//		Hidden[] hidden = mData.getNext().getHidden();
//		for (int i = 0; i < hidden.length; i++) {
//			map.put(hidden[i].getKey(), hidden[i].getValue());
//		}
//		return JSONObject.toJSONString(map);
//	}
//
//	/**
//	 * 根据state显示不同的状态
//	 * @param isState
//	 */
//	private void showState(boolean isState) {
//		if (isState) {		//如果是直接进入此页面，而不是由上个页面进入的。
//			mSesame_credit_btn_code.setBackgroundResource(R.drawable.button_selector);
//			mSesame_credit_btn_code.setEnabled(true);
//			mSesame_credit_btn_code.setText("获取验证码");
//		}else{
//			mSesame_credit_btn_code.setEnabled(false);
//			mSesame_credit_btn_code.setBackgroundResource(R.drawable.login_page_button_disabled);
//			//发送一个消息
//			mHandler.sendEmptyMessageDelayed(0, 1000);
//		}
//	}
//
//	/**
//	 * 网络请求获取验证码
//	 */
//	private void requestCheckMobileGetCode4creditline(String paramsJson,String method) {
//		if (null == mDialog) {
//    		mDialog = new LoadingAlertDialog(mContext);
//		}
//    	mDialog.show(ConstantsUtil.NETWORK_REQUEST_IN);
//
//    	Map<String,String> params = new HashMap<>();
//		params.put("method", method);
//
//		params.put("paramsJson", paramsJson);
//		params.put("taskNo", mTaskNo);
//
//		params.put("uid", AppContext.user.getUid());
//		params.put("token", AppContext.user.getToken());
//
//		params.put("ver", AppContext.getAppVersionName(mContext));
//		params.put("Plat", ConstantsUtil.PLAT);
//
//		Log.e("TAG", params.toString());
//
//		HttpRequest.checkPhone(mContext,params, new AsyncResponseCallBack() {
//
//			@Override
//			public void onResult(String arg2) {
//				if (mDialog.isShowing()) {
//					mDialog.dismiss();
//				}
//				if (null != arg2 &&  arg2.length() > 0) {
//					Log.e("TAG", new String(arg2));
//					explainJson(new String(arg2));
//				}
//
//			}
//
//			@Override
//			public void onFailed(String path, String msg) {
//				Toast.makeText(mContext, ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER, Toast.LENGTH_SHORT).show();
//				if (mDialog.isShowing()) {
//					mDialog.dismiss();
//				}
//			}
//		});
//	}
//
//	/**
//	 * 解释获取验证码的json
//	 * @param json
//	 */
//	protected void explainJson(String json) {
//		if ("refreshMessageCode".equals(method) || "submitMessageCode".equals(method)) {
//			explainRefreshOrSubmitCode(json);
//		}else{
//			explainGetCodeJson(json);
//		}
//	}
//
//	/**
//	 * 解释刷新短信验证码或者提交短信验证码
//	 * @param json
//	 */
//	private void explainRefreshOrSubmitCode(String json) {
//		SaveBaseInfoBean bean = JSON.parseObject(json, SaveBaseInfoBean.class);
//		if (null != bean && ConstantsUtil.RESPONSE_SUCCEED == Integer.parseInt(bean.getStatus())) {
//			if ("0".equals(bean.getData().getStep())) {		//操作成功,即提交验证码
//				mOnClickBtnListen.next(Integer.parseInt(bean.getData().getStep()));
//			}else {											//刷新验证码
//				showState(false);
//			}
//		}else if (null != bean && "2".equals(bean.getStatus())) {
//			mSesame_credit_et_code.setText("");
//			Toast.makeText(mContext, bean.getStatusDetail(), Toast.LENGTH_SHORT).show();
//			String opt = null;
//			if (null != bean.getData()) {
//				 opt = bean.getData().getOpt();
//			}
//			if ("jump2LoginPage".equals(opt)) {
//				mOnClickBtnListen.next(3);
//			}
//		}else if (null != bean && ConstantsUtil.RESPONSE_TOKEN_FAIL == Integer.parseInt(bean.getStatus())) {
//			showDialog(bean.getStatusDetail());
//		}else{
//			Toast.makeText(mContext, bean.getStatusDetail(), Toast.LENGTH_SHORT).show();
//		}
//	}
//
//	/**
//	 * 解释获取短信验证码
//	 * @param json
//	 */
//	private void explainGetCodeJson(String json) {
//		PhoneVerificationBean bean = JSON.parseObject(json, PhoneVerificationBean.class);
//		if (null != bean && ConstantsUtil.RESPONSE_SUCCEED == Integer.parseInt(bean.getStatus())) {
//			mData = bean.getData();
//			showState(false);
//		}else if (null != bean && ConstantsUtil.RESPONSE_TOKEN_FAIL == Integer.parseInt(bean.getStatus())) {
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
//
//	/**倒计时时间*/
//	private int mTime = 60;
//	/**处理消息*/
//	private Handler mHandler = new Handler(){
//		public void handleMessage(android.os.Message msg) {
//			if (0 == mTime) {
//				mTime = 60;
//				showState(true);
//				mSesame_credit_btn_code.setText("获取验证码");
//			}else{
//				mTime--;
//				showState(false);
//				mSesame_credit_btn_code.setText(mTime+"S");
//			}
//		};
//	};
//
//	@Override
//	public void onClick(View v) {		//login_page_button_disabled
//		switch (v.getId()) {
//		case R.id.sesame_credit_btn_code:		//获取验证码
//			requestCheckMobileGetCode4creditline(getRefreshCodeJson(),method);
//			break;
//		case R.id.sesame_credit_btn_submit:		//提交		进入手机验证
//			//Toast.makeText(mContext, "正在建设中。。。", Toast.LENGTH_SHORT).show();
//			mCode = mSesame_credit_et_code.getText().toString().trim();
//			if (6 != mCode.length()) {
//				Toast.makeText(mContext, "请输入正确的验证码", Toast.LENGTH_SHORT).show();
//				break;
//			}
//			requestCheckMobileGetCode4creditline(getSubmitCodeJson(),method);
//			break;
//
//		default:
//			break;
//		}
//
//	}
//
//
//	/**
//	 * 设置重新获取验证码的状态
//	 */
//	private void setAgainCodeState(){
//		mHandler.removeCallbacksAndMessages(null);
//		mTime = 60;
//		mSesame_credit_btn_code.setBackgroundResource(R.drawable.button_selector);
//		mSesame_credit_btn_code.setEnabled(true);
//		mSesame_credit_btn_code.setText("获取验证码");
//	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//		mSesame_credit_et_code.setText("");
//
//	}
//
//	@Override
//	public String getPageName() {
//		return getString(R.string.str_pagename_sesamecreditauth_frag);
//	}
//
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//		mHandler.removeCallbacksAndMessages(null);
//	}
//
//
//
//}
