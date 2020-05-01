package com.jfbank.qualitymarket.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huoyanzhengxin.zhengxin.print.FingerPrint;
import com.huoyanzhengxin.zhengxin.print.FingerPrintHelp;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.base.BaseActivity;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.LogUtil;
import com.jfbank.qualitymarket.util.StringUtil;
import com.jfbank.qualitymarket.util.ToastUtil;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


/**
* 忘记密码页面
* @author 崔朋朋
*/

public class FindPasswordActivity extends BaseActivity implements TextWatcher {

	@InjectView(R.id.rl_title)
	RelativeLayout rlTitle;
	@InjectView(R.id.tv_title)
	TextView tvTitle;

	@InjectView(R.id.et_findPasswordActivity_phoneNumber)
	EditText phoneNumberEditText;
	@InjectView(R.id.et_findPasswordActivity_verifyCode)
	EditText verifyCodeEditText;
	@InjectView(R.id.et_findPasswordActivity_passwd)
	EditText passwdEditText;
	@InjectView(R.id.iv_findPasswordActivity_showPasswd)
	ImageView showPasswd;

	@InjectView(R.id.btn_findPasswordActivity_getVerifyCode)
	Button getVerifyCodeButton;
	@InjectView(R.id.btn_findPasswordActivity_register)
	Button registerButton;

	private String phoneNumber;
	private String verifyCode;
	private String passwd;
	private boolean hidePasswd = true;//标记当前是显示还是隐藏密码
	public static final String KEY_OF_COME_FROM = "comeFrom";
	private String comeFrom = "";
	
	private int countDownTime = ConstantsUtil.COUNT_DOWN;
	private final int COUNT_DOWN_TASK = 20001;
	private final int COUNT_DOWN_OVER = 10001;
	private FingerPrint fingerPrint;

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
				enableGetVerifyButton();
				break;

			}
		};
	};



	public interface ImageVerifyCodeHandler {
		void handlerImageVerifyCode(String imageVerifyCode);
	}

	@OnClick({ R.id.iv_back, R.id.btn_findPasswordActivity_getVerifyCode,
			R.id.btn_findPasswordActivity_register, R.id.iv_findPasswordActivity_showPasswd })
	void onViewClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			launchActivity();
			break;
		case R.id.btn_findPasswordActivity_getVerifyCode:
			if (phoneNumber==null || "".equals(phoneNumber)){
				Toast.makeText(this, "手机号不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			if (!CommonUtils.isMobilePhoneVerify(phoneNumber)){
				Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
				return;
			}

			showImageVerifyCodeDialog(this, phoneNumber, new ImageVerifyCodeHandler() {
				@Override
				public void handlerImageVerifyCode(String imageVerifyCode) {
					getVerifyCode(imageVerifyCode);
				}
			});
//			getVerifyCode();
			break;
		case R.id.btn_findPasswordActivity_register:
			disableSubmitButton();
			findPassword();
			break;
		case R.id.iv_findPasswordActivity_showPasswd:
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
                Selection.setSelection(spanText, charSequence.length());
            }
			break;
		}
	}

	/**
	 * 显示图形验证码对话框
	 * @param context
	 * @param phoneNumber
	 * @param imageVerifyCodeHandler
     */
	public static void showImageVerifyCodeDialog(final Context context, final String phoneNumber, final ImageVerifyCodeHandler imageVerifyCodeHandler) {
		if (phoneNumber==null || "".equals(phoneNumber)){
			Toast.makeText(context, "手机号不能为空", Toast.LENGTH_SHORT).show();
			return;
		}

		final Dialog dialog = new Dialog(context, R.style.protocalDialog);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);
		View dialogView = View.inflate(context, R.layout.image_verify_code_view, null);
		final EditText imageVerifyCodeEditText = (EditText) dialogView.findViewById(R.id.et_findPasswordActivity_inputImageVerifyCodeText);
		final ImageView imageVerifyCodeImageView = (ImageView) dialogView.findViewById(R.id.iv_findPasswordActivity_imageVerifyCode);
		imageVerifyCodeImageView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                showImageVerifyCode(context, phoneNumber, imageVerifyCodeImageView);
            }
        });

		showImageVerifyCode(context, phoneNumber, imageVerifyCodeImageView);
		Button imageVerifyCodeButton = (Button) dialogView.findViewById(R.id.btn_findPasswordActivity_submitImageVerifyCode);
		imageVerifyCodeButton.setOnClickListener(new View.OnClickListener(){		//我知道了按钮

            @Override
            public void onClick(View v) {
                String imageverifyCode	= imageVerifyCodeEditText.getText().toString();
                if(StringUtil.isNull(imageverifyCode)){
                    Toast.makeText(context, "验证码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }else{
					imageVerifyCodeHandler.handlerImageVerifyCode(imageverifyCode);
                    dialog.dismiss();
                }
            }

        });
		dialog.setContentView(dialogView);
		dialog.setTitle("");
		dialog.show();
	}

	/**
	 * 显示图形验证码
	 * @param imageVerifyCodeImageView
     */
	private static void showImageVerifyCode(Context context, String phoneNumber, ImageView imageVerifyCodeImageView) {
		String imageVerifyCodeUrl = HttpRequest.QUALITY_MARKET_WEB_URL+HttpRequest.GET_IMAGE_VERIFY_CODE+"?mobile="+phoneNumber+"&timestamp="+System.currentTimeMillis();
		Picasso.with(context).load(imageVerifyCodeUrl).placeholder(R.drawable.load_image_place_holder)
                .into(imageVerifyCodeImageView);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_find_password);
		ButterKnife.inject(this);

		fingerPrint = FingerPrintHelp.getFingerPrint(getApplicationContext());
		fingerPrint.startTime();

		CommonUtils.setTitle(this,rlTitle);
		Intent intent = getIntent();
		comeFrom = intent.getStringExtra(KEY_OF_COME_FROM);
		if(comeFrom.equals(LoginActivity.TAG)){
			tvTitle.setText(R.string.str_pagenanme_findpsd);
		}else if(comeFrom.equals(AccountInfoActivity.TAG)){
			tvTitle.setText(R.string.str_pagename_editpsd);
			phoneNumberEditText.setText(AppContext.user.getMobile());
		}
		phoneNumber = phoneNumberEditText.getText().toString();
		
		phoneNumberEditText.addTextChangedListener(this);
		verifyCodeEditText.addTextChangedListener(this);
		passwdEditText.addTextChangedListener(this);
		disableSubmitButton();
	}

	@Override
	protected String getPageName() {
		if (getIntent().getStringExtra(KEY_OF_COME_FROM).equals(LoginActivity.TAG)){
			return  getString(R.string.str_pagenanme_findpsd);
		}else{
			return  getString(R.string.str_pagename_editpsd);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			launchActivity();
			return true;
		}else {
			return super.onKeyDown(keyCode, event);
		}
	}

/**
 * 启动上个页面过来的activity
 */
	private void launchActivity() {
		Intent intent = new Intent();
		if(comeFrom.equals(LoginActivity.TAG)){
//			intent.setClass(this, LoginActivity.class);
			
		}else if(comeFrom.equals(AccountInfoActivity.TAG)){
			intent.setClass(this, AccountInfoActivity.class);
			startActivity(intent);
		}
		finish();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}

	/**
	 * 找回密码
	 */
	private void findPassword() {
		if(!RegisterActivity.checkPassword(passwd)){
			Toast.makeText(this, "密码只能为数字和字母", Toast.LENGTH_SHORT).show();
			enableSubmitButton();
			return;
		}


		Map<String,String> params = new HashMap<>();
		 params.put("mobile", phoneNumber);
//		 params.put("password", Md5.md5(passwd));
		 params.put("password", passwd);
		 params.put("mobileValidCode", verifyCode);

		 HttpRequest.post(mContext, HttpRequest.QUALITY_MARKET_WEB_URL+HttpRequest.FIND_PASSWORD,params, new AsyncResponseCallBack() {

			@Override
			public void onResult(String arg2) {
				int inputLength = fingerPrint.getStringLength(phoneNumber, passwd, verifyCode);
				fingerPrint.endTime(phoneNumber, "android找回密码页面", inputLength);

				String jsonStr = new String(arg2);
				LogUtil.printLog("找回密码：" + jsonStr);
				Log.e("TAG", jsonStr);
				JSONObject jsonObject = JSON.parseObject(jsonStr);

				if(ConstantsUtil.RESPONSE_SUCCEED == jsonObject.getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)){
					Toast.makeText(FindPasswordActivity.this, "修改密码成功", Toast.LENGTH_SHORT).show();
//					Intent intent = new Intent(FindPasswordActivity.this, LoginActivity.class);
//					startActivity(intent);
					finish();
					overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
				}else {
//					String errorMsg = jsonObject.getJSONObject(ConstantsUtil.RESPONSE_DETAIL_FAILED_REASON_FIELD_NAME).getString(ConstantsUtil.RESPONSE_ERROR_MESSAGE_FIELD_NAME);
					String errorMsg = jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME);
					ToastUtil.show(errorMsg);
				}
				enableSubmitButton();
			}

			@Override
			public void onFailed(String path, String msg) {
				Toast.makeText(FindPasswordActivity.this, "用户找回密码失败", Toast.LENGTH_SHORT).show();
				enableSubmitButton();
			}
		});

	}

	/**
	 * 获取短信验证码
	 */
	private void getVerifyCode(String imageVerifyCode) {
		if (phoneNumber==null || "".equals(phoneNumber)){
			Toast.makeText(this, "手机号不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!CommonUtils.isMobilePhoneVerify(phoneNumber)){
			Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
			return;
		}

		
		getVerifyCodeButton.setEnabled(false);
		getVerifyCodeButton.setBackgroundResource(R.drawable.login_page_button_disabled);
		Map<String,String> params = new HashMap<>();
		params.put("mobile", phoneNumber);
//		发送手机验证码,1注册,2修改密码
		params.put("type", "2");
		params.put("verifyCode", imageVerifyCode);

		HttpRequest.post(mContext, HttpRequest.QUALITY_MARKET_WEB_URL+HttpRequest.SEND_VERIFY_CODE,params, new AsyncResponseCallBack() {

			@Override
			public void onFailed(String path, String msg) {
				Toast.makeText(FindPasswordActivity.this, "获取验证码失败", Toast.LENGTH_SHORT).show();
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
					Toast.makeText(FindPasswordActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
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
			enableSubmitButton();
		}else {
			disableSubmitButton();
		}

	}

	/**
	 * 禁用提交 button
	 */
	private void disableSubmitButton() {
		registerButton.setEnabled(false);
		registerButton.setBackgroundResource(R.drawable.login_page_button_disabled);
	}

	/**
	 * 启用提交 button
	 */
	private void enableSubmitButton() {
		registerButton.setEnabled(true);
		registerButton.setBackgroundResource(R.drawable.button_selector);
	}

	@Override
	public void afterTextChanged(Editable s) {
	}
	
	/**
	 * 启用短信验证码button
	 */
	private void enableGetVerifyButton() {
		getVerifyCodeButton.setEnabled(true);
		getVerifyCodeButton.setBackgroundResource(R.drawable.button_selector);
		getVerifyCodeButton.setText("获取验证码");
	}

}
