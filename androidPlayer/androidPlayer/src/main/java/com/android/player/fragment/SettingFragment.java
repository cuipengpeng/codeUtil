package com.android.player.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.android.player.R;
import com.android.player.ui.activity.ContactListActivity;
import com.android.player.ui.activity.xiyou3;

public class SettingFragment extends Fragment implements View.OnClickListener{
	private RelativeLayout userInfoRelativeLayout;
	private RelativeLayout contactListRelativeLayout;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main_tab_settings, container, false);
		userInfoRelativeLayout = (RelativeLayout) view.findViewById(R.id.rl_settingFragment_userInfo);
		userInfoRelativeLayout.setOnClickListener(this);
		contactListRelativeLayout = (RelativeLayout) view.findViewById(R.id.rl_settingFragment_contactList);
		contactListRelativeLayout.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_settingFragment_userInfo:
			startActivity(new Intent(getActivity(), xiyou3.class));
			break;
		case R.id.rl_settingFragment_contactList:
			startActivity(new Intent(getActivity(), ContactListActivity.class));
			break;
		}
	}
	
}
