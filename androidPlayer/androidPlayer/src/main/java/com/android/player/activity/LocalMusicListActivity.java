package com.android.player.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.player.R;
import com.android.player.dao.AudioDao;
import com.android.player.dao.impl.AudioDaoImpl;
import com.android.player.model.Audio;
import com.android.player.adapter.MusicListAdapter;

public class LocalMusicListActivity extends Activity implements OnClickListener{
	private AudioDao audioDao = new AudioDaoImpl(this);
	private List<Audio> musicList;
	private MusicListAdapter adapter;
	private ListView musicListView;
	private RelativeLayout mPlayingNowRelativeLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.local_audio_list);
		mPlayingNowRelativeLayout = (RelativeLayout) findViewById(R.id.rl_localMusicActivity_playingNow);
		mPlayingNowRelativeLayout.setOnClickListener(this);
		musicListView = (ListView) findViewById(R.id.lv_localMusicActivity_musicList);
		musicList = audioDao.getLocalAudioList();
		adapter = new MusicListAdapter(this, musicList, null);
		musicListView.setAdapter(adapter);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_localMusicActivity_playingNow:
			startActivity(new Intent(LocalMusicListActivity.this, PlayingActivity.class));
			break;
		}
	}
}
