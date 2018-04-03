//package com.jfbank.qualitymarket.activity;
//
//
//import android.content.Intent;
//import android.graphics.drawable.Drawable;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.jfbank.qualitymarket.R;
//
//import com.jfbank.qualitymarket.base.BaseActivity;
//import com.jfbank.qualitymarket.fragment.MyAccountFragment;
//import com.jfbank.qualitymarket.util.CommonUtils;
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
////授权信息提交成功
//public class AuthorizationCommitSuccessActivity extends BaseActivity implements View.OnClickListener{
//    private Button known,more;
//    RelativeLayout rlTitle;
//    /**
//     * 显示标题内容
//     */
//    TextView tvTitle;
//    /**
//     * 返回
//     */
//    ImageView ivBack;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_authorization_commit_success);
//        initView();
//    }
//
//    private void initView()
//    {
//        rlTitle = (RelativeLayout) findViewById(R.id.rl_title);
//        ivBack = (ImageView) findViewById(R.id.iv_back);
//        tvTitle = (TextView) findViewById(R.id.tv_title);
//        CommonUtils.setTitle(this, rlTitle);
//        tvTitle.setText("提交成功");
//        Drawable drawable = getResources().getDrawable(R.mipmap.ic_delete_gray);
//        ivBack.setImageDrawable(drawable);
//        ivBack.setOnClickListener(this);
//        known = (Button) findViewById(R.id.btn_know);
//        known.setOnClickListener(this);
//        more = (Button) findViewById(R.id.btn_more_authorization);
//        more.setOnClickListener(this);
//        ivBack.setOnClickListener(this);
//    }
//
//    @Override
//    public void onClick(View v)
//    {
//        switch(v.getId()){
////            跳转到我的
//            case R.id.btn_know:
//                Intent intentAccount = new Intent(AuthorizationCommitSuccessActivity.this,MainActivity.class);
//                intentAccount.putExtra(MainActivity.KEY_OF_BOTTOM_MENU,MyAccountFragment.TAG);
//                startActivity(intentAccount);
//                finish();
//                break;
//            //跳转到激活白条界面
//            case R.id.btn_more_authorization:
//                Intent intent = new Intent();
//                intent.setClass(this, MyAuthenticationActivity.class);
//                intent.putExtra(MyAuthenticationActivity.KEY_OF_ENTER_MYAUTH_ORJHBT, false);//true 进入我的认证页面，false进入激活白条页面
//                startActivity(intent);
//                finish();
//                break;
//            case R.id.iv_back:
//            	if(MyAuthenticationActivity.activeBorrow){
//                    Intent intentMain = new Intent(AuthorizationCommitSuccessActivity.this,MainActivity.class);
//                    intentMain.putExtra(MainActivity.KEY_OF_BOTTOM_MENU,MyAccountFragment.TAG);
//                    startActivity(intentMain);
//            	}
//                finish();
//                break;
//        }
//    }
//
//    @Override
//    protected String getPageName() {
//        return getString(R.string.str_pagename_authcommitsuccess);
//    }
//
//
//
//}
