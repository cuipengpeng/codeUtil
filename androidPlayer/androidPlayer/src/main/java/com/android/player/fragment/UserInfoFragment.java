package com.android.player.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.android.player.R;
import com.android.player.activity.MoreActivity;

public class UserInfoFragment extends Fragment implements View.OnClickListener {
	private ImageButton settingImageButton;
	private Button myMessageButton;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_right_menu, container,false);
		settingImageButton = (ImageButton) view.findViewById(R.id.ibtn_user_info_fragment_setting);
		settingImageButton.setOnClickListener(this);
		myMessageButton = (Button) view.findViewById(R.id.btn_user_info_fragment_my_messages);
		myMessageButton.setOnClickListener(this);
		
		return view;
	}
	
	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.ibtn_user_info_fragment_setting:
			intent = new Intent(getActivity(), MoreActivity.class);
			getActivity().startActivity(intent);
			break;
		case R.id.btn_user_info_fragment_my_messages:
			intent = new Intent(getActivity(), MoreActivity.class);
			getActivity().startActivity(intent);
			break;
		}
	}

}
