package com.android.player.fragment;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.player.R;
import com.android.player.dao.AudioDao;
import com.android.player.dao.impl.AudioDaoImpl;
import com.android.player.model.Audio;
import com.android.player.activity.HomeContainerActivity;
import com.android.player.activity.PlayingActivity;
import com.android.player.adapter.MusicListAdapter;

public class LocalMusicListFragment extends Fragment implements View.OnClickListener{
	private AudioDao audioDao ;
	private List<Audio> musicList;
	private MusicListAdapter adapter;
	private ListView musicListView;
	private RelativeLayout mPlayingNowRelativeLayout;
	private RelativeLayout leftMenuRelativeLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.local_audio_list,container, false);
		mPlayingNowRelativeLayout = (RelativeLayout) view.findViewById(R.id.rl_localMusicActivity_playingNow);
		mPlayingNowRelativeLayout.setOnClickListener(this);
		leftMenuRelativeLayout = (RelativeLayout) view.findViewById(R.id.rl_main_fragment_sliding_menu_left);
		leftMenuRelativeLayout.setOnClickListener(this);
		musicListView = (ListView) view.findViewById(R.id.lv_localMusicActivity_musicList);
		audioDao = new AudioDaoImpl(getActivity());
		musicList = audioDao.getLocalAudioList();
		adapter = new MusicListAdapter(getActivity(), musicList, null);
		musicListView.setAdapter(adapter);
		return view;
	}
		
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_localMusicActivity_playingNow:
			startActivity(new Intent(getActivity(), PlayingActivity.class));
			break;
		case R.id.rl_main_fragment_sliding_menu_left:
			HomeContainerActivity.mSlidingMenu.showMenu();
//          toggle(); //动态判断自动关闭或开启SlidingMenu
//          getSlidingMenu().showMenu();//显示SlidingMenu
//          getSlidingMenu().showSecondaryMenu() 显式第二个menu
//          getSlidingMenu().showContent();//显示主页面内容
			break;
		}
	}
}
