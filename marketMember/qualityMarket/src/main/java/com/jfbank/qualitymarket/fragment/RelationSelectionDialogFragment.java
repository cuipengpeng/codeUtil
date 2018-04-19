package com.jfbank.qualitymarket.fragment;

import java.util.Arrays;

import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.widget.WheelView;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * 关系选择对应的dialog.
 * 
 * @author 彭爱军
 * @date 2016年8月16日
 */
public class RelationSelectionDialogFragment extends DialogFragment implements OnClickListener {
	private TextView mRelation_selection_tv_cancel;
	private TextView mRelation_selection_tv_confirm;
	private com.jfbank.qualitymarket.widget.WheelView mRelation_selection_wheelview;
	private View view;
	private static  String[] PLANETS = new String[] { "Mercury", "Venus", "Earth", "Mars", "Jupiter", "Uranus",
			"Neptune", "Pluto" };

	private OnClickListen mOnClickListen;
	private int mSelectedIndex = 1;
	private String mItem;
	
	public interface OnClickListen{
		public void cancel();
		
		public void confirm(int selectedIndex, String item );
	}
	
	public void setOnClickListen(OnClickListen mOnClickListen) {
		this.mOnClickListen = mOnClickListen;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle args = getArguments(); 
		if (null != args) {
			PLANETS = args.getStringArray("DATA");
			if (null != PLANETS && PLANETS.length == 1) {
				mItem = PLANETS[0];
			}else if(PLANETS.length > 1){
				mItem = PLANETS[mSelectedIndex - 1];
			}
			
		}
		view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_relation_selection, null); 
		bindViews();

		Dialog dialog = new Dialog(getActivity());
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // must be called 
																// before set
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(true);

		// 设置宽度为屏宽、靠近屏幕底部。 
		Window window = dialog.getWindow();
		 window.getDecorView().setPadding(0, 0, 0, 0);		//设置没有内边距
		 window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));		//设置背景颜色为透明
		WindowManager.LayoutParams wlp = window.getAttributes();
		wlp.gravity = Gravity.BOTTOM;
		wlp.width = WindowManager.LayoutParams.MATCH_PARENT; // WindowManager.LayoutParams.MATCH_PARENT
		window.setAttributes(wlp);
		
		return dialog;
	}

	/**
	 * 初始化View并且设置监听
	 */
	private void bindViews() {

		mRelation_selection_tv_cancel = (TextView) view.findViewById(R.id.relation_selection_tv_cancel);
		mRelation_selection_tv_confirm = (TextView) view.findViewById(R.id.relation_selection_tv_confirm);
		mRelation_selection_wheelview = (com.jfbank.qualitymarket.widget.WheelView) view
				.findViewById(R.id.relation_selection_wheelview);

		mRelation_selection_tv_cancel.setOnClickListener(this);
		mRelation_selection_tv_confirm.setOnClickListener(this);

		mRelation_selection_wheelview.setOffset(1);
		if (null != PLANETS && PLANETS.length == 1) {
			mRelation_selection_wheelview.setSeletion(0);
		}else if(PLANETS.length > 1){
			mRelation_selection_wheelview.setSeletion(0);
		}
	
		mRelation_selection_wheelview.setItems(Arrays.asList(PLANETS));
		mRelation_selection_wheelview.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
			@Override
			public void onSelected(int selectedIndex, String item) {
				Log.e("TAG", "selectedIndex: " + selectedIndex + ", item: " + item);
				if (0 == selectedIndex) {
					item = PLANETS[0];
				}
				mSelectedIndex = selectedIndex;
				mItem = item;
			}
		});

	}
	/**
	 * 获取对话框dialog
	 * @param str
	 * @return
	 */
	public static RelationSelectionDialogFragment newDialog(String[] str){
		RelationSelectionDialogFragment dialog = new RelationSelectionDialogFragment();
		
		Bundle args = new Bundle();
		args.putStringArray("DATA", str);
		dialog.setArguments(args );
		
		return dialog;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.relation_selection_tv_cancel:
			//Toast.makeText(getActivity(), "点击了取消", Toast.LENGTH_SHORT).show();
			mOnClickListen.cancel();
			break;
		case R.id.relation_selection_tv_confirm:
			//Toast.makeText(getActivity(), "点击了确定", Toast.LENGTH_SHORT).show();
			mOnClickListen.confirm(mSelectedIndex, mItem);
			break;

		default:
			break;
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		Dialog dialog = getDialog();
		if (dialog != null) {
			int width = ViewGroup.LayoutParams.MATCH_PARENT;
			int height = ViewGroup.LayoutParams.MATCH_PARENT;
			dialog.getWindow().setLayout(width, height);
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

}
