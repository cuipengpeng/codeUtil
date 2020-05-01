//package com.jfbank.qualitymarket.fragment;
//
//import java.util.HashMap;
//import java.util.Map;
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
//import com.jfbank.qualitymarket.bean.Next;
//import com.jfbank.qualitymarket.bean.Param;
//import com.jfbank.qualitymarket.bean.PhoneData;
//import com.jfbank.qualitymarket.bean.RefreshGraphBean;
//import com.jfbank.qualitymarket.bean.RefreshParam;
//import com.jfbank.qualitymarket.dao.StoreService;
//import com.jfbank.qualitymarket.model.PhoneLoginBean;
//import com.jfbank.qualitymarket.model.PhoneVerificationBean;
//import com.jfbank.qualitymarket.net.HttpRequest;
//import com.jfbank.qualitymarket.util.Base64;
//import com.jfbank.qualitymarket.util.ConstantsUtil;
//import com.jfbank.qualitymarket.widget.LoadingAlertDialog;
//import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
// import java.util.HashMap; import java.util.Map;
//
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.drawable.BitmapDrawable;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.v4.app.Fragment;
//import android.telephony.TelephonyManager;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.inputmethod.InputMethodManager;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.Toast;
///**
// * 手机验证对应的页面
// * @author 彭爱军
// * @date 2016年8月10日
// */
//public class PhoneVerificationFragment extends BaseFragment implements OnClickListener{
//	private ApplyAmountActivity mContext;
//	/**手机号码*/
//    private EditText mPhone_verifcation_et_phone;
//    /**服务密码*/
//    private RelativeLayout mPhone_verifcation_rl_server_password;
//    /**服务密码*/
//    private EditText mPhone_verifcation_et_server_password;
//    /**提交*/
//    private Button mPhone_verifcation_btn_next;
//    /**点击图形验证码*/
//    private ImageView mPhone_verifcation_iv_code;
//    /**图形验证码*/
//    private EditText mPhone_verifcation_et_code;
//    /**图形验证码*/
//    private RelativeLayout mPhone_verifcation_rl_code;
//    /**短信验证码*/
//    private RelativeLayout mPhone_verifcation_rl_message_code;
//    /**短信验证码*/
//    private EditText mPhone_verifcation_et_message_code;
//    /**获取短信验证码*/
//    private Button mPhone_verifcation_btn_code;
//    private View view ;
//
//    private String mPhone;				//手机号
//    private String mServerPassword;		//服务密码
//	private OnClickBtnListen mOnClickBtnListen;
//	private String mCode;				//验证码
//	private String mMessageCode;		//短信验证码
//	/**网编请求时加载框*/
//	private LoadingAlertDialog mDialog;
//
//	private TelephonyManager tm;
//	/**下一步的数据*/
//	private Next mNext;
//	private HashMap<String, String> map;
//	private String mTaskNo;				//任务编码
//	private String method = "login";
//	private boolean isPicCodeLogin = false;		//是否picCode true有。
//
//	/**倒计时时间*/
//	private int mTime = 60;
//	/**处理消息*/
//	private Handler mHandler = new Handler(){
//		public void handleMessage(android.os.Message msg) {
//			if (0 == mTime) {
//				mTime = 60;
//				showState(true);
//				mPhone_verifcation_btn_code.setText("获取验证码");
//			}else{
//				mTime--;
//				mPhone_verifcation_btn_code.setText(mTime+"S");
//				showState(false);
//			}
//		};
//	};
//
//    /**添加一个接口,设置点击监听的回调*/
//	public interface OnClickBtnListen{
//
//		public void next(boolean isHasNext,int step,PhoneData data);		//进入下一个流程
//	}
//
//
//	public void setmOnClickBtnListen(OnClickBtnListen mOnClickBtnListen) {
//		this.mOnClickBtnListen = mOnClickBtnListen;
//	}
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		mContext = (ApplyAmountActivity) getActivity();
//		tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);		//初始化电话管理类
//		/*InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
//		imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,InputMethodManager.SHOW_FORCED);  */
//	}
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		 view = inflater.inflate(R.layout.fragment_phone_verifcation, container, false);
//		 bindViews();
//		return view;
//	}
//
//	/**
//	 * 初始化View以及设置监听
//	 */
//    private void bindViews() {
//        mPhone_verifcation_et_phone = (EditText) view.findViewById(R.id.phone_verifcation_et_phone);
//        mPhone_verifcation_rl_server_password = (RelativeLayout) view.findViewById(R.id.phone_verifcation_rl_server_password);
//        mPhone_verifcation_et_server_password = (EditText) view.findViewById(R.id.phone_verifcation_et_server_password);
//        mPhone_verifcation_btn_next = (Button) view.findViewById(R.id.phone_verifcation_btn_next);
//        mPhone_verifcation_iv_code = (ImageView) view.findViewById(R.id.phone_verifcation_iv_code);
//        mPhone_verifcation_et_code = (EditText) view.findViewById(R.id.phone_verifcation_et_code);
//        mPhone_verifcation_rl_code = (RelativeLayout) view.findViewById(R.id.phone_verifcation_rl_code);
//        mPhone_verifcation_rl_message_code = (RelativeLayout) view.findViewById(R.id.phone_verifcation_rl_message_code);
//        mPhone_verifcation_et_message_code = (EditText)view.findViewById(R.id.phone_verifcation_et_message_code);
//        mPhone_verifcation_btn_code = (Button) view.findViewById(R.id.phone_verifcation_btn_code);
//
//        mPhone_verifcation_iv_code.setOnClickListener(this);
//        mPhone_verifcation_btn_next.setOnClickListener(this);
//        mPhone_verifcation_btn_code.setOnClickListener(this);
//
//        mPhone = AppContext.user.getMobile();
//        mPhone_verifcation_et_phone.setText(mPhone.substring(0, 3)+"****"+mPhone.substring(7, mPhone.length()));
//        mPhone_verifcation_et_phone.setEnabled(false);
//        mPhone_verifcation_et_phone.requestFocus();
//
//   	 	map = new HashMap<String, String>();
//    }
//
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.phone_verifcation_iv_code:		//获取或刷新图形验证
//			requestRefreshOrGetGraphCode(getJsonRefreshGraphCode(),method);
//			mPhone_verifcation_et_code.setText("");
//			break;
//		case R.id.phone_verifcation_btn_code:	//获取或刷新短信验证
//			requestRefreshOrGetGraphCode(getRefreshCodeJson(),method);
//			mPhone_verifcation_et_code.setText("");
//			break;
//		case R.id.phone_verifcation_btn_next:	//下一步
//			if (!submitBeforeExamine()) {
//				break;
//			}
//			if (null != map) {		//每次清空上次map里面的值得
//				map.clear();
//			}
//			if (null != mNext) {	//判断mNext是否有值，即可能存在输入正确的服务密码之后就OK了
//				setParamsJson();
//			}
//			requestPhoneCheck();
//			break;
//
//		default:
//			break;
//		}
//	}
//	/**
//	 * 获取或刷新短信验证码的json数据
//	 * @return
//	 */
//	private String getRefreshCodeJson(){
//		if (null == mNext) {
//			return null;
//		}
//		if (null != map) {
//			map.clear();
//		}
//		method = mNext.getMethod();
//		Param[] param = mNext.getParam();
//		for (int i = 0; i < param.length; i++) {
//			if ("message_code".equals(param[i].getKey())) {
//				if ("getMessageCode".equals(method)) {				//如果method不等于getMessageCode则method是getRefresh_method即refreshMessageCode
//					continue;
//				}
//				method = param[i].getRefresh_method();
//				RefreshParam[] refreshParam = param[i].getRefresh_param();
//				for (int j = 0; j < refreshParam.length; j++) {
//					map.put(refreshParam[j].getKey(),refreshParam[j].getValue());
//				}
//			}else{
//				map.put(param[i].getKey(), param[i].getValue());
//			}
//		}
//		if ("getMessageCode".equals(method)) {				//如果method等于getMessageCode则需要Hidden
//			Hidden[] hidden = mNext.getHidden();
//			for (int i = 0; i < hidden.length; i++) {
//				map.put(hidden[i].getKey(), hidden[i].getValue());
//			}
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
//			mPhone_verifcation_btn_code.setBackgroundResource(R.drawable.button_selector);
//			mPhone_verifcation_btn_code.setEnabled(true);
//			mPhone_verifcation_btn_code.setText("获取验证码");
//		}else{
//			mPhone_verifcation_btn_code.setEnabled(false);
//			mPhone_verifcation_btn_code.setBackgroundResource(R.drawable.login_page_button_disabled);
//			//发送一个消息
//			mHandler.sendEmptyMessageDelayed(0, 1000);
//		}
//	}
//
//	/**
//	 * 提交数据前检测
//	 * @return
//	 */
//    private boolean submitBeforeExamine() {
//    	mServerPassword = mPhone_verifcation_et_server_password.getText().toString().trim();
//    	mCode = mPhone_verifcation_et_code.getText().toString().trim();
//    	mMessageCode = mPhone_verifcation_et_message_code.getText().toString().trim();
//    	if (mServerPassword.length() < 6) {
//			Toast.makeText(mContext, "请输入正确的服务密码", Toast.LENGTH_SHORT).show();
//			return false;
//		}
//    	if (View.VISIBLE == mPhone_verifcation_rl_code.getVisibility()) {
//    		isPicCodeLogin = true;
//    		if (mCode.length() < 1) {
//    			Toast.makeText(mContext, "请输入正确的图形验证码", Toast.LENGTH_SHORT).show();
//    			return false;
//    		}
//		}else{
//			isPicCodeLogin = false;
//		}
//
//    	if (View.VISIBLE == mPhone_verifcation_rl_message_code.getVisibility()) {
//    		if ( mMessageCode.length() < 1) {
//    			Toast.makeText(mContext, "请输入正确的验证码", Toast.LENGTH_SHORT).show();
//    			return false;
//    		}
//    	}
//
//		return true;
//	}
//
//	/**
//     * 设置传给后台的params的数据,可能是图形也有可能是短信，还有可能两者都存在
//     */
//    protected void setParamsJson() {
//		Param[] param = mNext.getParam();
//		method = mNext.getMethod();
//		for (int i = 0; i < param.length; i++) {
//			if ("pic_code".equals(param[i].getKey())) {
//	    		map.put(param[i].getKey(), mCode);			//图形验证
//			}else if("message_code".equals(param[i].getKey())){
//				map.put(param[i].getKey(), mMessageCode);	//短信验证
//			}else{
//				map.put(param[i].getKey(), param[i].getValue());
//			}
//		}
//	}
//
//	/**
//     * 网络请求直接登录手机或提交图形验证码即：method可能为：login、login、getMessageCode
//     */
//    protected void requestPhoneCheck() {
//    	if (null == mDialog) {
//    		mDialog = new LoadingAlertDialog(mContext);
//		}
//    	mDialog.show("正在验证手机号");
//
//    	setBtnShow(false);
//
//    	Map<String,String> params = new HashMap<>();
//
//		mServerPassword = mPhone_verifcation_et_server_password.getText().toString().trim();
//
//		params.put("method", method );
//		if (null != mTaskNo) {
//			params.put("taskNo", mTaskNo);
//		}
//		params.put("paramsJson", getJsonString());
//
//		params.put("uid", AppContext.user.getUid());
//		params.put("token", AppContext.user.getToken());
//
//		params.put("ver", AppContext.getAppVersionName(mContext));
//		params.put("Plat", ConstantsUtil.PLAT);
//
//		Log.e("TAG", "TaskNo11:"+mTaskNo);
//		Log.e("TAG", params.toString());
//
//		HttpRequest.checkPhone(mContext,params, new AsyncResponseCallBack() {
//
//			@Override
//			public void onResult(String arg2) {
//				if (mDialog.isShowing()) {
//					mDialog.dismiss();
//				}
//				setBtnShow(true);
//				if (null != arg2 &&  arg2.length() > 0) {
//					Log.e("TAG", new String(arg2));
//					explainJson(new String(arg2));
//				}
//
//			}
//
//			@Override
//			public void onFailed(String path, String msg) {
//				setBtnShow(true);
//				Toast.makeText(mContext, ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER, Toast.LENGTH_SHORT).show();
//				if (mDialog.isShowing()) {
//					mDialog.dismiss();
//				}
//			}
//		});
//	}
//
//    /**
//     * 设置提交按钮的状态
//     */
//    private void setBtnShow(boolean isOnClick) {
//    	if (isOnClick) {
//        	mPhone_verifcation_btn_next.setEnabled(true);
//        	mPhone_verifcation_btn_next.setBackgroundResource(R.drawable.button_selector);
//		}else{
//	    	mPhone_verifcation_btn_next.setEnabled(false);
//	    	mPhone_verifcation_btn_next.setBackgroundResource(R.drawable.login_page_button_disabled);
//		}
//	}
//
//	/**
//     * 解释手机验证的json数据
//     */
//	protected void explainJson(String json) {
//		PhoneVerificationBean bean = JSON.parseObject(json, PhoneVerificationBean.class);
//		if (null != bean && ConstantsUtil.RESPONSE_SUCCEED == Integer.parseInt(bean.getStatus())) {
//			PhoneData data = bean.getData();
//			if (null != data && "".equals(data.getOpt())) {
//				if (data.isHasNext()) {		//为true。则说明有下一步
//					mTaskNo = data.getTaskNo();
//					Log.e("TAG", "TaskNo:"+data.getTaskNo());
//					mNext = data.getNext();
//					if (isShowGraphCode(mNext.getParam())) {			//需要显示图形验证
//						showGraphCode();
//					}else{
//						mPhone_verifcation_rl_code.setVisibility(View.GONE);
//					}
//					if(isShowMessageCode(mNext.getParam())){			//需要显示短信验证
//						mHandler.removeCallbacksAndMessages(null);
//						mTime = 60;
//						mPhone_verifcation_btn_code.setText(mTime+"S");
//						showState(false);
//						showMessageCode();
//					}else{
//						mPhone_verifcation_rl_message_code.setVisibility(View.GONE);
//					}
//
//					if (isShowPassword()) {								//需要显示服务密码
//						mPhone_verifcation_rl_server_password.setVisibility(View.VISIBLE);
//						mPhone_verifcation_et_server_password.setSelection(mServerPassword.length());
//					}else{
//						mPhone_verifcation_rl_server_password.setVisibility(View.GONE);
//					}
//				}else{						//即基本资料成功提交
//					mOnClickBtnListen.next(false, Integer.parseInt(data.getStep()), null);
//				}
//			}else if (null != data && "jump2LoginPage".equals(data.getOpt())) {		//即opt有值 jump2LoginPage 停在当前页面
//				mPhone_verifcation_et_server_password.setText("");
//				mPhone_verifcation_et_code.setText("");
//				mPhone_verifcation_et_message_code.setText("");
//				mPhone_verifcation_rl_code.setVisibility(View.GONE);
//				mPhone_verifcation_rl_message_code.setVisibility(View.GONE);
//				Toast.makeText(mContext, "请输入正确的服务密码", Toast.LENGTH_SHORT).show();
//			}else{
//				Toast.makeText(mContext, bean.getStatusDetail(), Toast.LENGTH_SHORT).show();
//			}
//		}else if(null != bean && "2".equals(bean.getStatus())){		//图片验证码、服务密码以及短信验证码错误等
//			if (null == bean.getData()) {
//				Toast.makeText(mContext, bean.getStatusDetail(), Toast.LENGTH_SHORT).show();
//				return;
//			}
//			if ("".equals(bean.getData().getOpt())) {		//图片或者短信验证码错误
//				mPhone_verifcation_et_code.setText("");
//				mPhone_verifcation_et_message_code.setText("");
//				if ("refreshMessageCode".equals(method) || "getMessageCode".equals(method)) {		//提交错误的短信验证码,当图形和短信同时存在用isMessageCodeLogin存在问题，只能手动点击刷新或者获取图形验证码
//					Toast.makeText(mContext, bean.getStatusDetail(), Toast.LENGTH_SHORT).show();
//					return;
//				}
//				if (isPicCodeLogin) {		//说明是图片验证码提交错误
//					Toast.makeText(mContext, bean.getStatusDetail(), Toast.LENGTH_SHORT).show();
//					requestRefreshOrGetGraphCode(getJsonRefreshGraphCode(),method);
//					return;
//				}
//				Toast.makeText(mContext, bean.getStatusDetail(), Toast.LENGTH_SHORT).show();
//			}else if("jump2LoginPage".equals(bean.getData().getOpt())){		//服务密码错误
//				Toast.makeText(mContext, bean.getStatusDetail(), Toast.LENGTH_SHORT).show();
//				mTaskNo = null;
//				mNext = null;
//				mPhone_verifcation_rl_code.setVisibility(View.GONE);
//				mPhone_verifcation_rl_message_code.setVisibility(View.GONE);
//				mPhone_verifcation_et_server_password.setText("");
//				mPhone_verifcation_et_code.setText("");
//				mPhone_verifcation_et_message_code.setText("");
//			}else{
//				Toast.makeText(mContext, bean.getStatusDetail(), Toast.LENGTH_SHORT).show();
//			}
//		}else if (null != bean && ConstantsUtil.RESPONSE_TOKEN_FAIL == Integer.parseInt(bean.getStatus())) {
//			showDialog(bean.getStatusDetail());
//		}else{
//			Toast.makeText(mContext, bean.getStatusDetail(), Toast.LENGTH_SHORT).show();
//		}
//	}
//
//	/**
//	 * 刷新图片或者获取图片验证码
//	 * @param method
//	 * @param paramsJson
//	 */
//	private void requestRefreshOrGetGraphCode(String paramsJson,String method){
//		if (null == mDialog) {
//    		mDialog = new LoadingAlertDialog(mContext);
//		}
//    	mDialog.show(ConstantsUtil.NETWORK_REQUEST_IN);
//
//    	Map<String,String> params = new HashMap<>();
//
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
//				if (null != arg2 &&  arg2.length()> 0) {
//					Log.e("TAG", new String(arg2));
//					explainRefreshOrGetGraph(new String(arg2));
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
//	 * 解释刷新和获取图片的json
//	 * @param json
//	 */
//	protected void explainRefreshOrGetGraph(String json) {
//		RefreshGraphBean bean = JSON.parseObject(json, RefreshGraphBean.class);
//		if (null != bean && ConstantsUtil.RESPONSE_SUCCEED == Integer.parseInt(bean.getStatus())) {
//			if ("refreshMessageCode".equals(method) || "getMessageCode".equals(method)) {		//说明不是图片相关数据,即获取或者刷新短信
//				mPhone_verifcation_et_message_code.setText("");
//				mTime = 60;
//				mPhone_verifcation_btn_code.setText(mTime+"S");
//				showState(false);
//				return;
//			}
//			if (null != bean.getData()) {
//				Bitmap bitmap = getBase64Bitmap(bean.getData().getPic_code());
//				if (null != bitmap) {
//					mPhone_verifcation_iv_code.setBackgroundDrawable(new BitmapDrawable(bitmap));
//				}else{
//					Toast.makeText(mContext, "获取图形验证码失败", Toast.LENGTH_SHORT).show();
//				}
//			}
//		}else if (null != bean && ConstantsUtil.RESPONSE_TOKEN_FAIL == Integer.parseInt(bean.getStatus())) {
//			showDialog(bean.getStatusDetail());
//		}else{
//			Toast.makeText(mContext, bean.getStatusDetail(), Toast.LENGTH_SHORT).show();
//		}
//	}
//
//	/**
//	 * 获取刷新图片的json数据
//	 * @return
//	 */
//	private String getJsonRefreshGraphCode(){
//		if (null == mNext) {
//			return null;
//		}
//		if (null != map) {
//			map.clear();
//		}
//		method = mNext.getMethod();
//		Param[] param = mNext.getParam();
//		for (int i = 0; i < param.length; i++) {
//			if ("pic_code".equals(param[i].getKey())) {
//				if ("getPicCode".equals(method)) {				//默认情况下。获取比刷新图片验证码的优先级高
//					continue;
//				}
//				method = param[i].getRefresh_method();
//				RefreshParam[] refreshParam = param[i].getRefresh_param();
//				for (int j = 0; j < refreshParam.length; j++) {
//					map.put(refreshParam[j].getKey(), refreshParam[j].getValue());
//				}
//			}else{
//				map.put(param[i].getKey(), param[i].getValue());
//			}
//		}
//		if ("getPicCode".equals(method)) {						//是获取图片验证码
//			Hidden[] hidden = mNext.getHidden();
//			for (int i = 0; i < hidden.length; i++) {
//				map.put(hidden[i].getKey(), hidden[i].getValue());
//			}
//		}
//		return JSONObject.toJSONString(map);
//	}
//
//	/**
//	 * 显示短信验证码
//	 */
//	private void showMessageCode() {
//		mPhone_verifcation_rl_message_code.setVisibility(View.VISIBLE);
//		mPhone_verifcation_et_message_code.setText("");
//	}
//
//	/**
//	 * 显示图片相关信息
//	 */
//	private void showGraphCode() {
//		mPhone_verifcation_rl_code.setVisibility(View.VISIBLE);
//		mPhone_verifcation_et_code.setText("");
//		Bitmap bitmap = getBase64Bitmap(getBase64Str());
//		if (null != bitmap) {
//			//mPhone_verifcation_iv_code.setImageBitmap(bitmap);
//			mPhone_verifcation_iv_code.setBackgroundDrawable(new BitmapDrawable(bitmap));
//		}else{
//			Toast.makeText(mContext, "获取图形验证码失败", Toast.LENGTH_SHORT).show();
//		}
//
//	}
//
//	/**
//	 * 获取base64编码
//	 * @return
//	 */
//	private String getBase64Str(){
//		Param[] param = mNext.getParam();
//		for (int i = 0; i < param.length; i++) {
//			if ("pic_code".equals(param[i].getKey())) {
//				return param[i].getValue();
//			}
//		}
//		return null;
//	}
//
//	/**
//	 * 是否显示图形验证码
//	 * @param param
//	 * @return
//	 */
//	private boolean isShowGraphCode(Param[] param) {
//		for (int i = 0; i < param.length; i++) {
//			if ("pic_code".equals(param[i].getKey())) {		//需要显示图形验证码
//				return true;
//			}
//		}
//		return false;
//	}
//
//	/**
//	 * 是否显示短信验证码
//	 * @param param
//	 * @return
//	 */
//	private boolean isShowMessageCode(Param[] param) {
//		for (int i = 0; i < param.length; i++) {
//			if ("message_code".equals(param[i].getKey())) {		//需要显示短信验证码
//				return true;
//			}
//		}
//		return false;
//	}
//
//	/**
//	 * 是否显示服务密码
//	 * @return
//	 */
//	private boolean isShowPassword() {
//		Hidden[] hidden = mNext.getHidden();
//		for (int i = 0; i < hidden.length; i++) {
//			if ("password".equals(hidden[i].getKey())) {
//				mPhone_verifcation_et_server_password.setText(hidden[i].getValue());
//				return true;
//			}
//		}
//		Param[] param = mNext.getParam();
//		for (int i = 0; i < param.length; i++) {
//			if ("password".equals(param[i].getKey())) {
//				mPhone_verifcation_et_server_password.setText("");
//				return true;
//			}
//		}
//		return false;
//	}
//
//
//	/**
//	 * 返回json字符串
//	 * @return
//	 */
//	private String getJsonString(){//TODO	 有待完善 param数组里面的key和对应的值. 即指手机上输入的值
//		if (null == mNext) {				//第一次进入此页面点击下一步时会出现这种情况
//			map.put("cellphone", mPhone);
//			map.put("password", mServerPassword);
//
//			return JSONObject.toJSONString(map);
//		}
//
//		Hidden[] hidden = mNext.getHidden();
//		for (int i = 0; i < hidden.length; i++) {
//			if ("password".equals(hidden[i].getKey())) {
//				map.put(hidden[i].getKey(), mServerPassword);		//动态改变了服务密码,即修改服务密码会有影响
//			}else{
//				map.put(hidden[i].getKey(), hidden[i].getValue());
//			}
//		}
//
//		return JSONObject.toJSONString(map);
//	}
//
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
//
//	@Override
//	public void onResume() {
//		// TODO Auto-generated method stub
//		super.onResume();
//	}
//
//	@Override
//	public String getPageName() {
//		return getString(R.string.str_pagename_phoneverification_frag);
//	}
//
//	/**
//	 * 根据base64转为bitmap
//	 * @param bitmapBase64
//	 * @return
//	 */
//	private Bitmap getBase64Bitmap(String bitmapBase64){
////		com.alibaba.fastjson.util.Base64 base = new com.alibaba.fastjson.util.Base64();
//		byte[] bt = com.alibaba.fastjson.util.Base64.decodeFast(bitmapBase64);
//		if (0 != bt.length) {
//			return BitmapFactory.decodeByteArray(bt, 0, bt.length);
//		}
//		return null;
//	}
//
//	@Override
//	public void onDestroy() {
//		// TODO Auto-generated method stub
//		super.onDestroy();
//	}
//
//}
