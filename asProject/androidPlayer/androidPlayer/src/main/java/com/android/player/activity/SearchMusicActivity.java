package com.android.player.activity;

import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.player.R;
import com.android.player.dao.AudioDao;
import com.android.player.dao.impl.AudioDaoImpl;
import com.android.player.adapter.MusicListAdapter;
import com.android.player.utils.Constants.PopupMenu;

public class SearchMusicActivity extends Activity {
	private AudioDao audioDao = new AudioDaoImpl(this);
	ImageButton search_btn;
	Context context;
	Set<Integer> popUpMenu = new HashSet<Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.search_main_activity);
		context = this;
		initPopupMenu();
		final AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.search_edit);
		search_btn = (ImageButton) findViewById(R.id.search_button);
		final ListView list = (ListView) findViewById(R.id.lv_musicList);
		search_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if ("".equals(autoCompleteTextView.getText().toString().trim())) {
					Toast
							.makeText(
									context,
									getResources().getString(
											R.string.search_edit_hint),
									Toast.LENGTH_LONG).show();
				} else {
					list.setAdapter(new MusicListAdapter(context, audioDao
							.getLocalAudioListByName(autoCompleteTextView
									.getText().toString()), null));
				}
			}
		});

		super.onCreate(savedInstanceState);
	}

	private void initPopupMenu() {
		popUpMenu.add(PopupMenu.ADD_ALL_TO.getMenu());
		popUpMenu.add(PopupMenu.CREATE_LIST.getMenu());
		popUpMenu.add(PopupMenu.EXIT.getMenu());
		popUpMenu.add(PopupMenu.HELP.getMenu());
		popUpMenu.add(PopupMenu.SETTING.getMenu());
	}
}
