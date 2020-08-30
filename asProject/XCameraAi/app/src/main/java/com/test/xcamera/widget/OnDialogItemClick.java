package com.test.xcamera.widget;

import android.app.Dialog;
import android.view.View;
import android.view.View.OnClickListener;


public class OnDialogItemClick implements OnClickListener{
	private DialogItem mItem;
	private Dialog mDialog;
	public OnDialogItemClick(DialogItem item, Dialog dialog){
		mItem = item;
		mDialog = dialog;
	}
	@Override
	public void onClick(View v) {
		if(mItem != null)
			mItem.onClick();
		if(mDialog != null){
			mDialog.dismiss();
			mDialog = null;
		}
	}

}
