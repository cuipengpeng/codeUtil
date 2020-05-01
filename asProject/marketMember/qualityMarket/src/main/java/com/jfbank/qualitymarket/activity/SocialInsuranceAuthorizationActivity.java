//package com.jfbank.qualitymarket.activity;
//
//import android.content.Intent;
//import android.graphics.drawable.Drawable;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.CompoundButton;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.alibaba.fastjson.JSONObject;
//import com.jfbank.qualitymarket.AppContext;
//import com.jfbank.qualitymarket.R;
//import com.jfbank.qualitymarket.base.BaseActivity;
//import com.jfbank.qualitymarket.model.AddressInfoBean;
//import com.jfbank.qualitymarket.net.HttpRequest;
//import com.jfbank.qualitymarket.util.CommonUtils;
//import com.jfbank.qualitymarket.util.ConstantsUtil;
//import com.jfbank.qualitymarket.util.LogUtil;
//import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
// import java.util.HashMap; import java.util.Map;
//
//import org.apache.http.Header;
//
//import static com.jfbank.qualitymarket.net.HttpRequest.CHECK_SECURITY;
//import static com.jfbank.qualitymarket.net.HttpRequest.QUALITY_MARKET_WEB_URL;
//
//
///**
// * 作者：Rainbean on 2016/10/19 0019 11:33
// * 邮箱：rainbean@126.com
// */
//
////社保授权
//public class SocialInsuranceAuthorizationActivity extends BaseActivity implements View.OnClickListener{
//    RelativeLayout rlTitle;
//    /**
//     * 显示标题内容
//     */
//    TextView tvTitle;
//    /**
//     * 返回
//     */
//    ImageView ivBack;
//    private TextView social_insurance_city,social_insurance__id_card_number;
//    private EditText social_insurance_number,social_insurance_person_number,social_insurance_password;
//    private ImageView iv_social_insurance_authorization_close;
//    private Button btn_social_insurance_authorization;
//    private RelativeLayout rl_social_insurance_address;
//    private CheckBox cb;
//    private String city;
//
//    private String socialInsuranceNumber=""; //社保号码
//    private String personNumber="";//个人编号
//    private String socialInsurancePassword="";//社保密码
//
//    private RelativeLayout rl_social_insurance_number,rl_social_insurance_person_number,rl_social_insurance_password;
//
//    private static final String  SOCIAL_INSURANCE = "SOCIAL_INSURANCE_CITY";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_social_insurance_authorization);
//        initView();
//    }
//
//    private void initView(){
//        rlTitle = (RelativeLayout) findViewById(R.id.rl_title);
//        ivBack = (ImageView) findViewById(R.id.iv_back);
//        tvTitle = (TextView) findViewById(R.id.tv_title);
//        tvTitle.setText(R.string.str_pagename_socialinsuranceauth);
//        Drawable drawable = getResources().getDrawable(R.mipmap.ic_delete_gray);
//        ivBack.setImageDrawable(drawable);
//        ivBack.setOnClickListener(this);
//        btn_social_insurance_authorization = (Button) findViewById(R.id.btn_social_insurance_authorization);
//        btn_social_insurance_authorization.setOnClickListener(this);
//        rl_social_insurance_address = (RelativeLayout) findViewById(R.id.rl_social_insurance_address);
//        rl_social_insurance_address.setOnClickListener(this);
//
//        social_insurance_city = (TextView) findViewById(R.id.tv_social_insurance_city);
////        social_insurance__id_card_number = (TextView) findViewById(R.id.tv_social_insurance_id_card_num);
////        social_insurance__id_card_number.setText(encryptIDCardNumber(AppContext.user.getIdNumber()));
//
//        social_insurance_number = (EditText) findViewById(R.id.et_social_insurance_number);
//        social_insurance_person_number = (EditText) findViewById(R.id.et_social_insurance_person_number);
//        social_insurance_password = (EditText) findViewById(R.id.et_social_insurance_password);
//        cb = (CheckBox) findViewById(R.id.cb_agree_social_insurance_authorization);
//        rl_social_insurance_number = (RelativeLayout) findViewById(R.id.rl_social_insurance_number);
//        rl_social_insurance_person_number = (RelativeLayout) findViewById(R.id.rl_social_insurance_person_number);
//        rl_social_insurance_password = (RelativeLayout) findViewById(R.id.rl_social_insurance_password);
//
//        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked){
//                            btn_social_insurance_authorization.setEnabled(true);
//                            btn_social_insurance_authorization.setBackgroundResource(R.drawable.button_selector);
//                }else{
//                            btn_social_insurance_authorization.setEnabled(false);
//                            btn_social_insurance_authorization.setBackgroundResource(R.drawable.login_page_button_disabled);
//                }
//            }
//        });
//    }
//
//	// 控制信息输入界面显示
//	private void disPlayInputView() {
//		if (AppContext.addressInfo != null) {
//			if (AppContext.addressInfo.isNeedSecurityNo()) {
//				rl_social_insurance_number.setVisibility(View.VISIBLE);
//				rl_social_insurance_number.invalidate();
//			} else {
//				rl_social_insurance_number.setVisibility(View.GONE);
//				rl_social_insurance_number.invalidate();
//			}
//			if (AppContext.addressInfo.isNeedPersonNo()) {
//				rl_social_insurance_person_number.setVisibility(View.VISIBLE);
//				rl_social_insurance_person_number.invalidate();
//			} else {
//				rl_social_insurance_person_number.setVisibility(View.GONE);
//				rl_social_insurance_person_number.invalidate();
//			}
//			if (AppContext.addressInfo.isNeedPwd()) {
//				rl_social_insurance_password.setVisibility(View.VISIBLE);
//				rl_social_insurance_password.invalidate();
//			} else {
//				rl_social_insurance_password.setVisibility(View.GONE);
//				rl_social_insurance_password.invalidate();
//			}
//		}
//	}
//
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        setCity();
//    }
//
//    @Override
//    protected String getPageName() {
//        return getString(R.string.str_pagename_socialinsuranceauth);
//    }
//
//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        disPlayInputView();
//    }
//
//    private void setCity() {
//        AddressInfoBean bean = AppContext.getAddressInfo();
//        if (city==null||city.equals("")||bean==null){
//            city = "请选择城市";
//        }else {
//            city = bean.getAddCity();
//        }
//        social_insurance_city.setText(city);
//
//        if (city==null){
//            social_insurance_city.setText( "请选择城市");
//        }
//    }
//
//    //    身份证号只显示后四位18位
//    private String encryptIDCardNumber(String idcardNumber){
//        int length = idcardNumber.length();
//        String startStr = idcardNumber.substring(0,3);
//        return startStr+"***********"+idcardNumber.subSequence(length-4,length).toString();
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch(v.getId()){
//            case R.id.iv_back:
//                finish();
//                break;
//            case R.id.btn_social_insurance_authorization:
//                if (city.equals("请选择城市")){
//                    Toast.makeText(this,"请选择城市",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (!cb.isChecked()){
//                    Toast.makeText(this,"请先同意社保账号授权协议",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (AppContext.addressInfo.isNeedPersonNo()&&
//                        TextUtils.isEmpty(social_insurance_person_number.getText().toString().trim())){
//                    Toast.makeText(this,"请填写正确的个人编号",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (AppContext.addressInfo.isNeedPersonNo()){
//                    personNumber = social_insurance_person_number.getText().toString().trim();
//                }
//
//                if (AppContext.addressInfo.isNeedSecurityNo()&&
//                        TextUtils.isEmpty(social_insurance_number.getText().toString().trim())){
//                    Toast.makeText(this,"请填写正确的社保编号",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (AppContext.addressInfo.isNeedSecurityNo()){
//                    socialInsuranceNumber = social_insurance_number.getText().toString().trim();
//                }
//
//                if (AppContext.addressInfo.isNeedPwd()&&
//                        social_insurance_password.getText().toString().trim().length()<6){
//                    Toast.makeText(this,"请填写正确的社保密码",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (AppContext.addressInfo.isNeedPwd()){
//                    socialInsurancePassword = social_insurance_password.getText().toString().trim();
//                }
//                commitSocilaInsuranceDatas();
//                break;
////            选择地址
//            case R.id.rl_social_insurance_address:
//                Intent intent = new Intent();
//                intent.setClass(mContext, ProvinceAddressActivity.class);		//选择城市
//                intent.putExtra("IS_BASE_INFO", true);
//                intent.putExtra("SOCIAL_INSURANCE", SOCIAL_INSURANCE);
//                startActivity(intent);
//                break;
//        }
//    }
//
//
//// 提交社保验证信息
//    private void commitSocilaInsuranceDatas(){
//
////        用户id			uid
////        区域编码		areaCode
////        姓名			name
////        身份证号		idNumber
////        个人编号		personNo
////        社保号码		insuranceNo
////        社保密码		password
////        额度申请id		creditlineApplyId     mCreditlineApplyId = AppContext.user.getCreditlineApplyId4show();
//
//        final Map<String,String> params = new HashMap<>();
//        params.put("uid", AppContext.user.getUid());
//        params.put("token", AppContext.user.getToken());
//        params.put("ver", AppContext.getAppVersionName(AppContext.mContext));
//        params.put("Plat", ConstantsUtil.PLAT);
//        params.put("areaCode", AppContext.addressInfo.getAddCityCode());
//        params.put("name", AppContext.user.getIdName());
//        params.put("idNumber", AppContext.user.getIdNumber());
//        params.put("insuranceNo",socialInsuranceNumber);
//        params.put("personNo",personNumber);
//        params.put("password", socialInsurancePassword);
//      //  params.put("creditlineApplyId", AppContext.user.getCreditlineApplyId4show());
//        Log.e("TAG", params.toString());
//
//        HttpRequest.post(mContext,QUALITY_MARKET_WEB_URL + CHECK_SECURITY, params, new AsyncResponseCallBack() {
//            @Override
//            public void onResult(String responseStr) {
//                String resultStr = new String(responseStr);
//                LogUtil.printLog("提交社保信息"+resultStr);
//                JSONObject jsonObject = JSONObject.parseObject(resultStr);
//                String code = jsonObject.getString("status");
//                if (code.equals("1")){
//                    Toast.makeText(mContext,"操作成功",Toast.LENGTH_SHORT).show();
//                    CommonUtils.startWebViewActivity(SocialInsuranceAuthorizationActivity.this, "https://sctest.9fbank.com/views/credit/authSucc.html", false, true);
////                    startActivity(new Intent(SocialInsuranceAuthorizationActivity.this,AuthorizationCommitSuccessActivity.class));
//                    finish();
//                }else if (code.equals("2")){
//                    Toast.makeText(SocialInsuranceAuthorizationActivity.this,jsonObject.getString("statusDetail"),Toast.LENGTH_SHORT).show();
//                }else {
//                    Toast.makeText(SocialInsuranceAuthorizationActivity.this,jsonObject.getString("statusDetail"),Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                Toast.makeText(SocialInsuranceAuthorizationActivity.this,"提交用户社保信息失败，请检查网络设置！",Toast.LENGTH_SHORT).show();
//            }
//        });
//
//    }
//
//
//}
