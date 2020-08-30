package com.test.xcamera.widget;

import com.test.xcamera.bean.AlbumDirectory;
import com.editvideo.MediaData;

import java.util.List;
import java.util.Map;

public class DialogItem {
	private String text;
	private int resourceId;
	private int mViewId;

	private List<MediaData> mlist;

	private  Map.Entry<String, AlbumDirectory> entry;

	public  DialogItem(String text, int id,int viewId){
		this.setText(text);
		this.setResourceId(id);
		this.mViewId = viewId;
	}

	public  DialogItem( int layout,   Map.Entry<String, AlbumDirectory> entry){
		this.mViewId=layout;
		this.entry=entry;

	}
	public  DialogItem(int layout, List<MediaData>  list){
		this.mViewId=layout;
		this.mlist=list;
	}




	//点击事件
	public void onClick(){
		
	}

	public int getViewId() {
		return mViewId;
	}

	public void setViewId(int mViewId) {
		this.mViewId = mViewId;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getResourceId() {
		return resourceId;
	}

	public void setResourceId(int resourceId) {
		this.resourceId = resourceId;
	}

	public Map.Entry<String, AlbumDirectory> getEntry() {
		return entry;
	}

	public void setEntry(Map.Entry<String, AlbumDirectory> entry) {
		this.entry = entry;
	}

	public List<MediaData> getMlist() {
		return mlist;
	}

	public void setMlist(List<MediaData> mlist) {
		this.mlist = mlist;
	}
}

