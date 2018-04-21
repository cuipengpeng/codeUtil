package com.android.player.fragment;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.android.player.R;
import com.android.player.dao.AudioDao;
import com.android.player.dao.impl.AudioDaoImpl;
import com.android.player.ui.activity.FunActivity;

public class SearchMusicFragment extends Fragment implements View.OnClickListener{
	private AudioDao audioDao = new AudioDaoImpl(getActivity());
	ImageButton search_btn;
	Context context;
	Set<Integer> popUpMenu = new HashSet<Integer>();
	private RelativeLayout funRelativelayout;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.search_main_activity, container, false);
		funRelativelayout = (RelativeLayout) view.findViewById(R.id.rl_toolsFragment_fun);
		funRelativelayout.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_toolsFragment_fun:
			getActivity().startActivity(new Intent(getActivity(), FunActivity.class));
			break;
		}
	}
}
