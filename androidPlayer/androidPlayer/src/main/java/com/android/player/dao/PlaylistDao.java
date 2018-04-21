package com.android.player.dao;

import java.util.List;

import com.android.player.domain.Playlist;

public interface PlaylistDao {
	void createPlaylist(String name);

	void removePlaylist(String id);

	List<Playlist> getAllPlaylist();

}
