package com.jfbank.qualitymarket.test;

import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.net.HttpRequest;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class CustomServerIpActivity extends Activity implements OnClickListener, OnItemSelectedListener, TextWatcher{
	private EditText serverIpEditText;	
	private EditText portEditText;	
	private Button confirmButton;
	private Spinner chooseEnvironmentSpinner;	
    private static final String[] environment={"请选择环境","测试环境","准生产环境"};
    private String selectedEnvironment = "";
    private ArrayAdapter<String> adapter;
    private final String FINANCE_TEST_IP = "101.200.87.124";  
    private final String FINANCE_PRE_IP = "123.56.167.11";  
    
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_custom_server_ip);
			
			serverIpEditText = (EditText) findViewById(R.id.et_customServerIpActivity_serverIp);
			serverIpEditText.addTextChangedListener(this);
			portEditText = (EditText) findViewById(R.id.et_customServerIpActivity_port);
			confirmButton = (Button) findViewById(R.id.btn_customServerIpActivity_confirm);
			confirmButton.setOnClickListener(this);
			chooseEnvironmentSpinner = (Spinner) findViewById(R.id.spinner_customServerIpActivity_chooseEnvironment);
	        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,environment);
	        //设置下拉列表的风格
	        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        chooseEnvironmentSpinner.setAdapter(adapter);
	        chooseEnvironmentSpinner.setOnItemSelectedListener(this);
	        chooseEnvironmentSpinner.setEnabled(false);
	    }
	     
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_customServerIpActivity_confirm:
				String serverIp = serverIpEditText.getText().toString().trim();
				String port = portEditText.getText().toString().trim();
				
				if(("".equals(port) || port==null) && ("".equals(serverIp) || serverIp==null)){
					//两个输入框都为空， 则使用默认服务器地址
					HttpRequest.initEnvironment();
					Toast.makeText(getApplication(), "默认服务器地址："+HttpRequest.QUALITY_MARKET_WEB_URL, Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(!"".equals(serverIp) && serverIp!=null){
					//ip地址不为空， 则使用自定义服务器地址
					String[] strArr1 = serverIp.split("\\.");
					if(strArr1.length != 4){
						Toast.makeText(getApplication(), "服务器IP地址不合法，请重新输入。", Toast.LENGTH_SHORT).show();
						return;
					}

					if("".equals(port) || port==null ){
						//端口默认使用8080
						port="8080";
					}

					if(Integer.valueOf(port)>65535 || Integer.valueOf(port)<0){
						Toast.makeText(getApplication(), "端口地址不合法，请重新输入。", Toast.LENGTH_SHORT).show();
						return;
					}
					
//					if(FINANCE_TEST_IP.equals(serverIp) || selectedEnvironment.equals(environment[1])){
//						//测试环境IP
//						HttpRequest.currentEnv =HttpRequest.Environment.TEST;
//						HttpRequest.initEnvironment();
//					}else if(FINANCE_PRE_IP.equals(serverIp) || selectedEnvironment.equals(environment[2])){
//						//预发环境IP
//						HttpRequest.currentEnv =HttpRequest.Environment.PRE;
//						HttpRequest.initEnvironment();
//					}
					
					HttpRequest.QUALITY_MARKET_WEB_URL = "http://"+serverIp+":"+port+"/quality-mall-api/";
					Toast.makeText(getApplication(), "新服务器地址："+HttpRequest.QUALITY_MARKET_WEB_URL, Toast.LENGTH_SHORT).show();
				}else {
					//ip地址为空
					Toast.makeText(getApplication(), "请输入ip地址。", Toast.LENGTH_SHORT).show();
					return;
				}
				break;
			}
		}

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
	        selectedEnvironment = environment[position];
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
//				if(!FINANCE_PRE_IP.equals(s.toString()) && !FINANCE_TEST_IP.equals(s.toString()) && s.toString().split("\\.").length==4){
//					chooseEnvironmentSpinner.setEnabled(true);
//					Toast.makeText(getApplication(), "请选择本地服务器连接环境！", Toast.LENGTH_SHORT).show();
//
//				}else{
//					chooseEnvironmentSpinner.setEnabled(false);
//				}
				chooseEnvironmentSpinner.setEnabled(false);
		}

		@Override
		public void afterTextChanged(Editable s) {
			
		}
}
