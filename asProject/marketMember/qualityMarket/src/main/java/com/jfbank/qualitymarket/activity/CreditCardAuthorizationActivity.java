//package com.jfbank.qualitymarket.activity;
//
//import com.jfbank.qualitymarket.R;
//import com.jfbank.qualitymarket.base.BaseActivity;
//import com.jfbank.qualitymarket.util.CommonUtils;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.graphics.drawable.Drawable;
//import android.os.Bundle;
//import android.text.Editable;
//import android.text.Selection;
//import android.text.TextWatcher;
//import android.view.View;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import qiu.niorgai.StatusBarCompat;
//
//
///**
// * 作者：Rainbean on 2016/10/19 0019 11:33
// * <p>
// * 邮箱：rainbean@126.com
// */
//
//public class CreditCardAuthorizationActivity extends BaseActivity implements View.OnClickListener {
//    RelativeLayout rlTitle;
//    /**
//     * 显示标题内容
//     */
//    TextView tvTitle;
//    /**
//     * 返回
//     */
//    ImageView ivBack;
//    private EditText credit_number, credit_password, credit_phone;
//    private CheckBox agree_credit_authorization;
//    private Button commit_credit_authorization;
//
//    private static final char space = ' ';
//
//    private String creditNumber, queryPassword, phoneVerificationCode;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_credit_card_authorization);
//        initView();
//    }
//
//    @Override
//    protected String getPageName() {
//        return getString(R.string.str_pagename_criditcardauth);
//    }
//
//    private void initView() {
//        rlTitle = (RelativeLayout) findViewById(R.id.rl_title);
//        ivBack = (ImageView) findViewById(R.id.iv_back);
//        tvTitle = (TextView) findViewById(R.id.tv_title);
//        Drawable drawable = getResources().getDrawable(R.mipmap.ic_delete_gray);
//        ivBack.setImageDrawable(drawable);
//        ivBack.setOnClickListener(this);
//        CommonUtils.setTitle(this,rlTitle);
//        commit_credit_authorization = (Button) findViewById(R.id.btn_credit_authorization);
//        commit_credit_authorization.setOnClickListener(this);
//
//        credit_number = (EditText) findViewById(R.id.et_credit_card_number);//信用卡号
//        credit_password = (EditText) findViewById(R.id.et_credit_card_password);//信用卡查询密码
//        credit_phone = (EditText) findViewById(R.id.et_credit_authorization_phone);//手机验证码
//
//        agree_credit_authorization = (CheckBox) findViewById(R.id.cb_agree_credit_authorization);//同意协议
//
//        creditNumber = credit_number.getText().toString().replaceAll(" ", "");
//        queryPassword = credit_password.getText().toString().replaceAll(" ", "");
//
////        信用卡号自动加空格
//        credit_number.addTextChangedListener(new TextWatcher() {
//
//            //改变之前text长度
//            int beforeTextLength = 0;
//            //改变之前的文字
//            private CharSequence beforeChar;
//            //改变之后text长度
//            int onTextLength = 0;
//            //是否改变空格或光标
//            boolean isChanged = false;
//            // 记录光标的位置
//            int location = 0;
//            private char[] tempChar;
//            private StringBuffer buffer = new StringBuffer();
//            //已有空格数量
//            int spaceNumber = 0;
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                beforeTextLength = s.length();
//                if (buffer.length() > 0) {
//                    buffer.delete(0, buffer.length());
//                }
//                spaceNumber = 0;
//                for (int i = 0; i < s.length(); i++) {
//                    if (s.charAt(i) == ' ') {
//                        spaceNumber++;
//                    }
//                }
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                onTextLength = s.length();
//                buffer.append(s.toString());
//                if (onTextLength == beforeTextLength || onTextLength <= 3
//                        || isChanged) {
//                    isChanged = false;
//                    return;
//                }
//                isChanged = true;
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (isChanged) {
//                    location = credit_number.getSelectionEnd();
//                    int index = 0;
//                    while (index < buffer.length()) {
//                        if (buffer.charAt(index) == space) {
//                            buffer.deleteCharAt(index);
//                        } else {
//                            index++;
//                        }
//                    }
//                    index = 0;
//                    int konggeNumberC = 0;
//                    while (index < buffer.length()) {
//                        if ((index == 4 || index == 9 || index == 14 || index == 19)) {
//                            buffer.insert(index, space);
//                            konggeNumberC++;
//                        }
//                        index++;
//                    }
//
//                    if (konggeNumberC > spaceNumber) {
//                        location += (konggeNumberC - spaceNumber);
//                    }
//
//                    tempChar = new char[buffer.length()];
//                    buffer.getChars(0, buffer.length(), tempChar, 0);
//                    String str = buffer.toString();
//                    if (location > str.length()) {
//                        location = str.length();
//                    } else if (location < 0) {
//                        location = 0;
//                    }
//
//                    credit_number.setText(str);
//                    Editable etable = credit_number.getText();
//                    Selection.setSelection(etable, location);
//                    isChanged = false;
//                }
//            }
//        });
//        tvTitle.setText(R.string.str_pagename_criditcardauth);
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.iv_back:
//                finish();
//                break;
//            //查看数据服务授权协议
//            case R.id.tv_agree_credit_authorization_link:
//
//                break;
//            //确定进行信用卡授权
//            case R.id.btn_credit_authorization:
//
////                验证码为空时，
//
//
////                if (isValid()){
////
////                }
//                startActivity(new Intent(CreditCardAuthorizationActivity.this, AuthorizationCommitSuccessActivity.class));
//                break;
//        }
//    }
//
//    //需要填写的信息是否有效
//    private boolean isValid() {
//        if (agree_credit_authorization.isChecked()) {
//
//        }
//        return false;
//    }
//
//
//}
