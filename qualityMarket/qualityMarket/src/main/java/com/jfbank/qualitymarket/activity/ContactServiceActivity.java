package com.jfbank.qualitymarket.activity;

import com.jfbank.qualitymarket.ActivitysManage;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.fragment.PopDialogFragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
/**
 * 联系客服页面 
 * @author 彭爱军
 * @date 2016年8月22日
 */
public class ContactServiceActivity extends FragmentActivity implements OnClickListener { 
	/** 返回 */ 
	private TextView mTitle_tv_back;  
	/** 显示标题内容 */
	private TextView mTitle_tv_content;
	/** 电话号码 */ 
	private TextView mContact_service_tv_phone;
	/**拨打电话*/
	private Button mContact_server_btn_call;
	@Override 
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_contact_service); 
		ActivitysManage.getActivitysManager().addActivity(this); 
		bindViews(); 
	}
	
	/**
	 * 初始化View以及设置监听 
	 */
	private void bindViews() {
		mTitle_tv_back = (TextView) findViewById(R.id.title_tv_back);
		mTitle_tv_content = (TextView) findViewById(R.id.title_tv_content);
		findViewById(R.id.title_tv_right_content).setVisibility(View.GONE);
		
		mContact_service_tv_phone = (TextView) findViewById(R.id.contact_service_tv_phone);
		mContact_server_btn_call = (Button) findViewById(R.id.contact_server_btn_call);
		
		mTitle_tv_content.setText("联系客服");
		
		mContact_server_btn_call.setOnClickListener(this);
		mTitle_tv_back.setOnClickListener(this);
		
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_tv_back:
			finish();
			break;
		case R.id.contact_server_btn_call:
			final PopDialogFragment dialog = PopDialogFragment.newDialog(true, true,null, "确定拨打客服电话吗？", "取消", "确定", null);
			dialog.setOnClickListen(new PopDialogFragment.OnClickListen() {
				
				@Override
				public void rightClick() {
					dialog.dismiss();
					call();
				}
				
				@Override
				public void leftClick() {
					dialog.dismiss();
				}
			});
			dialog.show(getSupportFragmentManager(), "TAG");
			break;

		default:
			break;
		}
	}
	
	/**
	 * 拨打电话
	 */
	protected void call() {
		String phoneNumber =  "400-810-8818";  
        //意图：想干什么事  
        Intent intent = new Intent();  
        intent.setAction(Intent.ACTION_CALL);  
        //url:统一资源定位符  
        //uri:统一资源标示符（更广）  
        intent.setData(Uri.parse("tel:" + phoneNumber));  
        //开启系统拨号器  
        startActivity(intent);  
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivitysManage.getActivitysManager().finishActivity(this);
	}

	
}
