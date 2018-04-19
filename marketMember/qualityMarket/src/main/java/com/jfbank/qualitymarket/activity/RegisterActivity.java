package com.jfbank.qualitymarket.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huoyanzhengxin.zhengxin.print.FingerPrint;
import com.huoyanzhengxin.zhengxin.print.FingerPrintHelp;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.base.BaseActivity;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.LogUtil;
import com.jfbank.qualitymarket.util.TDUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
* 注册页面
* @author 崔朋朋
*/

public class RegisterActivity extends BaseActivity implements TextWatcher, OnCheckedChangeListener {
	@InjectView(R.id.rl_title)
	RelativeLayout rlTitle;
	@InjectView(R.id.tv_title)
	TextView tvTitle;
	@InjectView(R.id.et_registerActivity_phoneNumber)
	EditText phoneNumberEditText;
	@InjectView(R.id.et_registerActivity_verifyCode)
	EditText verifyCodeEditText;
	@InjectView(R.id.et_registerActivity_passwd)
	EditText passwdEditText;
	@InjectView(R.id.et_registerActivity_inviteCode)
	EditText inviteCodeEditText;
	@InjectView(R.id.iv_registerActivity_showPasswd)
	ImageView showPasswd;

	@InjectView(R.id.btn_registerActivity_getVerifyCode)
	Button getVerifyCodeButton;
	@InjectView(R.id.cb_registerActivity_userProtocal)
	CheckBox userProtocalCheckBox;
	@InjectView(R.id.btn_registerActivity_register)
	Button registerButton;
	@InjectView(R.id.tv_registerActivity_userProtocal)
	TextView userProtocalTextView;

	private String phoneNumber;
	private String verifyCode;
	private String passwd;
	private boolean hidePasswd = true; //标记当前是显示还是隐藏密码
	private boolean textValidate = false;//三个edittext文本框是否校验通过
	private boolean checkBoxValidate = false;//是否勾选checkbox同意协议书

	private FingerPrint fingerPrint;
	private int countDownTime = ConstantsUtil.COUNT_DOWN;
	private final int COUNT_DOWN_TASK = 20001;
	private final int COUNT_DOWN_OVER = 10001;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case COUNT_DOWN_TASK:
				//点击获取验证码后的倒计时任务
				getVerifyCodeButton.setText("剩余" + msg.arg1 + "s");
				if(countDownTime>0){
					Message message = new Message();
					countDownTime -= 1;
					message.what = COUNT_DOWN_TASK;
					message.arg1 = countDownTime;
					handler.sendMessageDelayed(message, 1000);
				}else {
					Message message2 = new Message();
					message2.what = COUNT_DOWN_OVER;
					handler.sendMessageDelayed(message2, 0);
				}
				break;
			case COUNT_DOWN_OVER:
				//倒计时结束，启用获取验证码按钮
				enableGetVerifyButton();
				break;

			}
		};
	};

	@OnClick({ R.id.iv_back, R.id.btn_registerActivity_getVerifyCode,
			R.id.btn_registerActivity_register, R.id.iv_registerActivity_showPasswd, R.id.tv_registerActivity_userProtocal })
	void onViewClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			launchLoginActivity();
			break;
		case R.id.tv_registerActivity_userProtocal:
			String commonIssueUrl =  HttpRequest.QUALITY_MARKET_WEB_URL+HttpRequest.H5_PAGE_SUB_URL+"index.html#/pzsczcxy";
			CommonUtils.startWebViewActivity(this,commonIssueUrl,false,false);
			break;
		case R.id.btn_registerActivity_getVerifyCode:
			if (phoneNumber==null || "".equals(phoneNumber)){
				Toast.makeText(this, "手机号不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			if (!CommonUtils.isMobilePhoneVerify(phoneNumber) || "147".equals(phoneNumber.substring(0,3))|| "145".equals(phoneNumber.substring(0,3))){		//添加对147和145手机号的限制
				Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
				return;
			}

			FindPasswordActivity.showImageVerifyCodeDialog(this, phoneNumber, new FindPasswordActivity.ImageVerifyCodeHandler() {
				@Override
				public void handlerImageVerifyCode(String imageVerifyCode) {
					getVerifyCode(imageVerifyCode);
				}
			});
			TDUtils.onEvent(mContext, "100009", "点击获取验证码");
			break;
		case R.id.btn_registerActivity_register:
			disableRegisterButton();
			register();
			break;
		case R.id.iv_registerActivity_showPasswd:
			if (hidePasswd) {
				showPasswd.setImageResource(R.mipmap.login_page_password_on);
				hidePasswd = false;
				passwdEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
			} else {
				passwdEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				showPasswd.setImageResource(R.mipmap.login_page_password_off);
				hidePasswd = true;
			}
	          //切换后将EditText光标置于末尾
            CharSequence charSequence = passwdEditText.getText();
            if (charSequence instanceof Spannable) {
                Spannable spanText = (Spannable) charSequence;
                Selection.setSelection(spanText, charSequence.length());
            }
			break;

		}
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);
		ButterKnife.inject(this);

		fingerPrint = FingerPrintHelp.getFingerPrint(getApplicationContext());
		fingerPrint.startTime();

		tvTitle.setText(R.string.str_pagename_register);
		userProtocalCheckBox.setOnCheckedChangeListener(this);
		checkBoxValidate = true;
		userProtocalCheckBox.setChecked(true);
		phoneNumberEditText.addTextChangedListener(this);
		verifyCodeEditText.addTextChangedListener(this);
		passwdEditText.addTextChangedListener(this);
		disableRegisterButton();
		tvTitle.setText(R.string.str_pagename_register);
		CommonUtils.setTitle(this,rlTitle);
	}

	@Override
	protected String getPageName() {
		return getString(R.string.str_pagename_register);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			launchLoginActivity();
			return true;
		}else {
			return super.onKeyDown(keyCode, event);
		}
	}

	private void launchLoginActivity() {
		finish();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}
	
	private void register() {
		if(!checkPassword(passwd)){
			Toast.makeText(this, "密码只能为数字和字母", Toast.LENGTH_SHORT).show();
			enableRegisterButton();
			return;
		}

		if(!userProtocalCheckBox.isChecked()){
			Toast.makeText(this, "请先同意用户协议", Toast.LENGTH_SHORT).show();
			enableRegisterButton();
			return;
		}
		TDUtils.onEvent(mContext, "100010", "点击完成注册");
		Map<String,String> params = new HashMap<>();
		 params.put("mobile", phoneNumber);
		 params.put("password", passwd);
		 params.put("channel", CommonUtils.getAppChannelId(this)+"");
//		 params.put("password", EncryptUtil.desEncrypt(passwd, ConstantsUtil.KEY_OF_DES));
		 params.put("mobileValidCode", verifyCode);
		 params.put("inviteCode", inviteCodeEditText.getText().toString());

		 HttpRequest.post(mContext,HttpRequest.QUALITY_MARKET_WEB_URL+HttpRequest.REGISTER_USER,params, new AsyncResponseCallBack() {

			@Override
			public void onResult(String arg2) {
				String jsonStr = new String(arg2);
				LogUtil.printLog("注册：" + jsonStr);

				int inputLength = fingerPrint.getStringLength(phoneNumber, passwd, verifyCode, inviteCodeEditText.getText().toString());
				fingerPrint.endTime(phoneNumber, "android注册页面", inputLength);

				JSONObject jsonObject = JSON.parseObject(jsonStr);
				if(ConstantsUtil.RESPONSE_SUCCEED == jsonObject.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)){
					Toast.makeText(RegisterActivity.this, "用户注册成功", Toast.LENGTH_SHORT).show();
					//LoginActivity.login(RegisterActivity.this, phoneNumber, passwd);
					TDUtils.onEvent(mContext, "100011", "注册成功");
					finish();
				}else{
					TDUtils.onEvent(mContext, "100011", "注册失败");
					String errorMsg = jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME);
					Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
				}
				enableRegisterButton();
			}

			@Override
			public void onFailed(String path, String msg) {
				Toast.makeText(RegisterActivity.this, "用户注册失败", Toast.LENGTH_SHORT).show();
				enableRegisterButton();
			}
		});

	}

	private void getVerifyCode(String imageVerifyCode) {
		if (phoneNumber==null || "".equals(phoneNumber)){
			Toast.makeText(this, "手机号不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!CommonUtils.isMobilePhoneVerify(phoneNumber) || "147".equals(phoneNumber.substring(0,3))|| "145".equals(phoneNumber.substring(0,3))){		//添加对147和145手机号的限制
			Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
			return;
		}
		
		getVerifyCodeButton.setEnabled(false);
		getVerifyCodeButton.setBackgroundResource(R.drawable.login_page_button_disabled);
		Map<String,String> params = new HashMap<>();
		params.put("mobile", phoneNumber);
//		发送手机验证码,1注册,2修改密码
		params.put("type", "1");
		params.put("verifyCode", imageVerifyCode);
		
		HttpRequest.post(mContext,HttpRequest.QUALITY_MARKET_WEB_URL+HttpRequest.SEND_VERIFY_CODE,params, new AsyncResponseCallBack() {

			@Override
			public void onFailed(String path, String msg) {
				Toast.makeText(RegisterActivity.this, "获取验证码失败", Toast.LENGTH_SHORT).show();
				enableGetVerifyButton();
			}


			@Override
			public void onResult(String arg2) {
				String jsonStr = new String(arg2);
				LogUtil.printLog("获取验证码：" + jsonStr);

				Log.e("TAG", jsonStr);
				JSONObject jsonObject = JSON.parseObject(jsonStr);
				if(ConstantsUtil.RESPONSE_SUCCEED == jsonObject.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)){

					// 倒计时60s
					countDownTime = ConstantsUtil.COUNT_DOWN;
					Message message = new Message();
					countDownTime -= 1;
					message.what = COUNT_DOWN_TASK;
					message.arg1 = countDownTime;
					handler.sendMessageDelayed(message, 0);
				}else{
					enableGetVerifyButton();
					String errorMsg = jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME);
					Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
				}
				
			}
		});

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		phoneNumber = phoneNumberEditText.getText().toString();
		verifyCode = verifyCodeEditText.getText().toString();
		passwd = passwdEditText.getText().toString();

		if (CommonUtils.isMobilePhoneVerify(phoneNumber) && verifyCode.length() == ConstantsUtil.VERIFY_CODE_LENGTH && passwd.length() > 5) {
			textValidate = true;
		}else {
			textValidate = false;
		}
		checkEditTextAndCheckbox();
	}

	@Override
	public void afterTextChanged(Editable s) {
	}
	
	/**
	 * 禁用注册button
	 */
	private void disableRegisterButton() {
		registerButton.setEnabled(false);
		registerButton.setBackgroundResource(R.drawable.login_page_button_disabled);
	}

	/**
	 * 启用注册button
	 */
	private void enableRegisterButton() {
		registerButton.setEnabled(true);
		registerButton.setBackgroundResource(R.drawable.button_selector);
	}
	
	/**
	 * 启用获取短信验证码button
	 */
	private void enableGetVerifyButton() {
		getVerifyCodeButton.setEnabled(true);
		getVerifyCodeButton.setBackgroundResource(R.drawable.button_selector);
		getVerifyCodeButton.setText("获取验证码");
	}
	
	/**
	 * 校验密码
	 * @param password
	 * @return
	 */
	public static boolean checkPassword(String password){
		boolean isPassword = false;
		Pattern pattern = Pattern.compile("^[0-9a-zA-Z]{6,12}$");
		Matcher matcher = pattern.matcher(password);
		isPassword = matcher.matches();
		return isPassword;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(isChecked){
			checkBoxValidate = true;
		}else{
			checkBoxValidate = false;
		}
		checkEditTextAndCheckbox();
	}

	/**
	 * 校验三个exittext和同意协议书的checkbox
	 */
	private void checkEditTextAndCheckbox() {
		if(textValidate && checkBoxValidate){
			enableRegisterButton();
		}else{
			disableRegisterButton();
		}
	}
}
