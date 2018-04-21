package com.android.player.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.player.R;

public class GameFragment extends Fragment implements View.OnClickListener{
	private TextView tv;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.game_fragment, container, false);
		return view;
	}

	@Override
	public void onClick(View v) {
		
	}

}
