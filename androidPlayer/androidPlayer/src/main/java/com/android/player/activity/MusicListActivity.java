package com.android.player.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.player.R;
import com.android.player.builder.ContentValuesBuilder;
import com.android.player.dao.AudioDao;
import com.android.player.dao.impl.AudioDaoImpl;
import com.android.player.model.Audio;
import com.android.player.model.BaseDomain;
import com.android.player.utils.MusicFolderUtil;
import com.android.player.adapter.MusicListAdapter;
import com.android.player.widget.CommonAlertDialogBuilder;
import com.android.player.utils.Constants;
import com.android.player.utils.DirectoryUtil;

public class MusicListActivity extends Activity implements OnClickListener{
	private AudioDao audioDao = new AudioDaoImpl(this);
	private ProgressDialog progress_dialog;
	private List<Audio> musicList;
	private List<Integer> checkedItem = new ArrayList<Integer>();
	private String[] choices;
	private MusicListAdapter adapter;
	Set<Integer> popUpMenu = new HashSet<Integer>();
	private ListView musicListView;
	
	private Handler musicListHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0: 
				progress_dialog.dismiss();
				break;
			}
		}
	};

	private Runnable mRunnable = new Runnable() {
		public void run() {
			for (int i = 0; i < checkedItem.size(); i++) {
				String[] s = DirectoryUtil
						.MediaScan(choices[checkedItem.get(i)]);
			}
			musicListHandler.sendEmptyMessage(0);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.audio_list);
		LinearLayout addAudioBtn = (LinearLayout) findViewById(R.id.create_audio_list_header);
		addAudioBtn.setOnClickListener(this);

		musicListView = (ListView) findViewById(R.id.lv_musicList);
//		musicList = audioDao.getAudioListByPlaylistId(this.getIntent()
//				.getExtras().get("com.android.player.pid").toString());
//
//		adapter = new MusicListAdapter(this, musicList, this.getIntent()
//				.getExtras().get("com.android.player.pid").toString());
		musicListView.setAdapter(adapter);
		
		TextView count_audio = (TextView) findViewById(R.id.count_audio);
//		count_audio.setText("共" + String.valueOf(musicList.size()) + "首");
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		AlertDialog alertDialog = (AlertDialog) dialog;
		ListView lv = alertDialog.getListView();
		for (int i = 0; i < choices.length; i++) {
			lv.setItemChecked(i, false);
		}
		super.onPrepareDialog(id, dialog);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		final MusicFolderUtil musicFolderUtil = new MusicFolderUtil(this);
		Set<String> folderList = musicFolderUtil.getFolderContainMedia();
		choices = folderList.toArray(new String[folderList.size()]);
		// 选项数组
		// String[] choices = { "Facebook", "Twitter" };
		// Check判断数组，与选项对应
		// boolean[] chsBool = { true, false };
		AlertDialog dialog = CommonAlertDialogBuilder.getInstance(this)
				.setIcon(R.drawable.ic_menu_scan).setTitle("请选择扫描目录")
				.setMultiChoiceItems(choices, null,
						new OnMultiChoiceClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which, boolean isChecked) {
								if (isChecked) {
									checkedItem.add(which);
								} else {
									checkedItem.remove((Object) which);
								}
							}

						}).setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								for (int i = 0; i < checkedItem.size(); i++) {
									addMediaToPlaylist(musicFolderUtil
											.getMediasByFolder(choices[checkedItem
													.get(i)]));
									System.out
											.println(musicFolderUtil
													.getMediasByFolder(
															choices[checkedItem
																	.get(i)])
													.toString());

								}
								showProcessDialog();
								mRunnable.run();
								checkedItem.clear();
							}

						}).setNegativeButton("No",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								checkedItem.clear();
							}
						}).create();
		return dialog;
	}

	protected void showProcessDialog() {
		progress_dialog = ProgressDialog.show(this, null, "正在扫描");
	}
	
	private String getMediaName(String path) {
		String mediaName = path.substring(path.lastIndexOf("/") + 1, path
				.length());
		return mediaName;
	}

	// 添加音乐到播放列表
	private final void addMediaToPlaylist(Set<String> medias) {
		String pId = this.getIntent().getExtras().get(
				"com.android.player.pid").toString();
		ContentValues values = new ContentValues();
		for (String path : medias) {
			if (getCountPlaylistMediaByName(getMediaName(path)) == 0) {
				Audio audio = new Audio();
				audio.setPlaylistId(pId);
				audio.setName(getMediaName(path));
				audio.setPath(path);
				audio.setAddDate(new Date());
				audio.setUpdateDate(new Date());
				values = bulid(audio);
				audioDao.addMediaToPlaylist(values);
			}

		}
		Toast.makeText(this, "添加音乐成功", Toast.LENGTH_LONG).show();
	}
	
	protected <T extends BaseDomain> ContentValues bulid(T domain) {
		ContentValues cv = null;
		try {
			cv = ContentValuesBuilder.getInstance().bulid(domain);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cv;
	}

	private final int getCountPlaylistMediaByName(String name) {
		ContentResolver resolver = getContentResolver();
		Uri uri = Uri
				.parse("content://" + Constants.AUTHORITY + "/audiolist");
		String[] proj = { "id" };
		String selection = "playlist_id = ? AND audio_name = ?";
		String[] selectionArgs = {
				this.getIntent().getExtras().get("com.hrw.android.player.pid")
						.toString(), name };
		Cursor cursor = resolver.query(uri, proj, selection, selectionArgs,
				null);
		return cursor.getCount();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.create_audio_list_header:
			showDialog(1);
			break;
		}
	}
}
