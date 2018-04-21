package com.android.player.ui.activity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;

import com.android.player.R;
import com.android.player.dao.PlaylistDao;
import com.android.player.dao.impl.PlaylistDaoImpl;
import com.android.player.domain.Playlist;
import com.android.player.ui.adapter.PlaylistAdapter;
import com.android.player.ui.component.dialog.CreatePlaylistDialog;
import com.android.player.utils.Constants;
import com.android.player.utils.Constants.PopupMenu;

public class PlaylistActivity extends Activity {

	private PlaylistDao playlistDao = new PlaylistDaoImpl(this);
	private ImageButton back_btn;
	private TabActivity tabActivity;
	private LinearLayout createNewPlaylistBtn;
	private ListView musicListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.playlist_list);
		musicListView = (ListView) findViewById(R.id.lv_musicList);
		tabActivity = (TabActivity) getParent();
		initCreateNewPlaylistButton();
		initListAdapter();
		super.onCreate(savedInstanceState);
	}

	private void initCreateNewPlaylistButton() {
		createNewPlaylistBtn = (LinearLayout) findViewById(R.id.create_playlist_header);
		createNewPlaylistBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog dialog = CreatePlaylistDialog
						.getCreatePlaylistDialog(PlaylistActivity.this)
						.create();
				dialog.setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						initListAdapter();
					}
				});
				dialog.show();
			}
		});
	}

	private void initListAdapter() {
		List<Playlist> playlist = getAllPlaylist();
		musicListView.setAdapter(new PlaylistAdapter(this, playlist));
		musicListView.setOnItemLongClickListener(
				new OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						showItemLongClickDialog(id);
						return false;
					}
				});
		
		musicListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> l, View v, int position,
					long id) {
				Intent toMusicListActivity = new Intent(PlaylistActivity.this, MusicListActivity.class);
				toMusicListActivity.putExtra("com.hrw.android.player.pid", id);
				TabHost.TabSpec tab_spec_menu_list = tabActivity.getTabHost()
						.newTabSpec("music_list" + id).setIndicator("music_list" + id);
				tab_spec_menu_list.setContent(toMusicListActivity);
				tabActivity.getTabHost().addTab(tab_spec_menu_list);
				tabActivity.getTabHost().setCurrentTabByTag("music_list" + id);
			}
		});
	}

	private void showItemLongClickDialog(final long id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final CharSequence[] items = { "重命名", "删除" };
		// TODO setMessage is something different with kugou's
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				switch (which) {
				// TODO 0,1 to constant
				case 0:
					break;
				case 1:
					playlistDao.removePlaylist(String.valueOf(id));
					initListAdapter();
					break;
				default:
					break;
				}

			}
		}).setTitle("id:" + id);
		AlertDialog alert = builder.create();
		alert.show();
	}

	protected List<Playlist> getAllPlaylist() {
		List<Playlist> allPlaylist = playlistDao.getAllPlaylist();
		for (Playlist playlst : allPlaylist) {
			playlst.setCountAudio(countAudio(playlst.getId().toString()));
		}
		return allPlaylist;
	}

	private Integer countAudio(String pId) {
		ContentResolver cr = getContentResolver();
		Uri uri = Uri
				.parse("content://" + Constants.AUTHORITY + "/audiolist");
		String[] projection = { "id" };
		String selection = "playlist_id = ?";
		String[] selectionArgs = { pId };
		Cursor c = cr.query(uri, projection, selection, selectionArgs, null);
		c.close();
		return c.getCount();

	}
}
