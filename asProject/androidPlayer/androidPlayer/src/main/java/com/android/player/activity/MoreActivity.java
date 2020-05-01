package com.android.player.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.android.player.R;

public class MoreActivity extends Activity implements View.OnClickListener{
	private RelativeLayout userInfoRelativeLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tab_settings);
		userInfoRelativeLayout = (RelativeLayout)findViewById(R.id.rl_settingFragment_userInfo);
		userInfoRelativeLayout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_settingFragment_userInfo:
			startActivity(new Intent(this, xiyou3.class));
			break;
		case R.id.rl_settingFragment_contactList:
			startActivity(new Intent(this, xiyou3.class));
			break;
		}
	}
	
}
