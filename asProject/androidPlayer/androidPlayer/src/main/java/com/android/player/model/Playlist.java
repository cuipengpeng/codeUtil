package com.android.player.model;

import com.android.player.db.DataBaseHelper;
import com.android.player.orm.annotation.Column;
import com.android.player.orm.annotation.Table;

import java.util.Date;

@Table(name = DataBaseHelper.PLAYLIST_TABLE_NAME)
public class Playlist extends BaseDomain {

	private Long id;
	@Column(name = "name")
	private String name;
	@Column(name = "add_date")
	private Date addDate;
	@Column(name = "modified_date")
	private Date updateDate;

	private Integer countAudio;

	public Long getId() {
		return id;
	}

	public Integer getCountAudio() {
		return countAudio;
	}

	public void setCountAudio(Integer countAudio) {
		this.countAudio = countAudio;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getAddDate() {
		return addDate;
	}

	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

}
