package com.jfbank.qualitymarket.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jfbank.qualitymarket.R;
/**
 * 弹出一个或两个对话框的按钮的类
 * @author 彭爱军
 * @date 2016年10月12日
 */
public class MyPopupDialog extends Dialog implements android.view.View.OnClickListener{
	private LayoutInflater inflater;
	private Context mContext;
	private View view;
	/**标识是否为两个按钮。*/
	private boolean isTwoButton;
	/**标题*/
	private TextView mPop_dialog_tv_title;
	/**显示内容*/
    private TextView mPop_dialog_tv_content;
    /**显示两对话按钮*/
    private RelativeLayout mPop_dialog_rl_two_button;
    /**左边按钮显示的内容*/
    private TextView mPop_dialog_tv_left_content;
    /**右边按钮*/
    private TextView mPop_dialog_tv_right_content;
    /**一个按钮*/
    private TextView mPop_dialog_tv_one_button;
	
	private OnClickListen mOnClickListen;
	private String mTitle;
	private String mContent;
	private String mLeftBtn;
	private String mRightBtn;
	private String mOneBtn;
	public MyPopupDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}

	protected MyPopupDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// TODO Auto-generated constructor stub
	}

	public MyPopupDialog(Context context) {
		super(context);
		inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.dialog_fragment_pop, null);
		bindViews();
	}
	/**
	 * 
	 * @param context
	 * @param mTitle		标题
	 * @param mContent		内容
	 * @param mLeftBtn		
	 * @param mRightBtn
	 * @param mOneBtn
	 * @param isTwoButton	是否显示两个按钮
	 */
	public MyPopupDialog(Context context,String mTitle,String mContent,String mLeftBtn, String mRightBtn,String mOneBtn,boolean isTwoButton) {
		super(context);
		inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.dialog_fragment_pop, null);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(view);
		this.mTitle = mTitle;
		this.mContent = mContent;
		this.mLeftBtn = mLeftBtn;
		this.mRightBtn = mRightBtn;
		this.mOneBtn = mOneBtn;
		this.isTwoButton = isTwoButton;
		bindViews();
	}
	
	/**
	 * 设置回调监听接口
	 * @author 彭爱军
	 * @date 2016年8月15日
	 */
	public interface OnClickListen{
		public void leftClick();  
		
		public void rightClick();
	}
	
	/**
	 * 设置点击监听
	 * @param mOnClickListen
	 */
	public void setOnClickListen(OnClickListen mOnClickListen) {
		this.mOnClickListen = mOnClickListen;
	}

	/**
	 * 初始化View以及设置监听
	 */
    private void bindViews() {
        mPop_dialog_tv_title = (TextView) view.findViewById(R.id.pop_dialog_tv_title);
        mPop_dialog_tv_content = (TextView) view.findViewById(R.id.pop_dialog_tv_content);
        mPop_dialog_rl_two_button = (RelativeLayout) view.findViewById(R.id.pop_dialog_rl_two_button);
        mPop_dialog_tv_left_content = (TextView) view.findViewById(R.id.pop_dialog_tv_left_content);
        mPop_dialog_tv_right_content = (TextView) view.findViewById(R.id.pop_dialog_tv_right_content);
        mPop_dialog_tv_one_button = (TextView) view.findViewById(R.id.pop_dialog_tv_one_button);
        
        if (null == mTitle) {
        	mPop_dialog_tv_title.setVisibility(View.GONE);
		}else {
			mPop_dialog_tv_title.setVisibility(View.VISIBLE);
			mPop_dialog_tv_title.setText(mTitle);
		}

    	mPop_dialog_tv_content.setText(mContent);
        if (isTwoButton) {
        	mPop_dialog_tv_one_button.setVisibility(View.GONE);
        	mPop_dialog_tv_left_content.setText(mLeftBtn);
        	mPop_dialog_tv_right_content.setText(mRightBtn);
		}else{
			mPop_dialog_rl_two_button.setVisibility(View.GONE);
			mPop_dialog_tv_one_button.setText(mOneBtn);	//79c5f9
		}
        
        mPop_dialog_tv_left_content.setTextColor(0xff79c5f9);		//设置字体颜色
        mPop_dialog_tv_right_content.setTextColor(0xff79c5f9);
        mPop_dialog_tv_one_button.setTextColor(0xff79c5f9);
        
        mPop_dialog_tv_left_content.setOnClickListener(this);
        mPop_dialog_tv_right_content.setOnClickListener(this);
        mPop_dialog_tv_one_button.setOnClickListener(this);
        
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pop_dialog_tv_left_content:		//点击左边按钮
			mOnClickListen.leftClick();
			break;
		case R.id.pop_dialog_tv_right_content:		//右边按钮
			mOnClickListen.rightClick();
			break;
		case R.id.pop_dialog_tv_one_button:			//点击一个按钮时
			mOnClickListen.rightClick();
			break;

		default:
			break;
		}
	}
}
