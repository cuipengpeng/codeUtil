package com.jfbank.qualitymarket.fragment;

import com.jfbank.qualitymarket.R;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 弹出对话框的Dialog
 * @author 彭爱军
 * @date 2016年8月15日
 */
public class PopDialogFragment extends DialogFragment implements OnClickListener{
	private Context mContext;
	/**标识是否为两个按钮。*/
	private boolean isTwoButton;
	private View view;
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
    
	private String mTitle;
	private String mContent;
	private String mLeftBtn;
	private String mRightBtn;
	private String mOneBtn;
	
	private OnClickListen mOnClickListen;
	private boolean isCancel;	//true不消失，false消失
    
	/**
	 * 设置回调监听接口
	 * @author 彭爱军
	 * @date 2016年8月15日
	 */
	public interface OnClickListen{
		public void leftClick();  
		
		public void rightClick();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		 view = inflater.inflate(R.layout.dialog_fragment_pop, container, false);
		 bindViews();
		return view;
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
        
        Bundle args = getArguments();
        if (null != args) {
        	isTwoButton = args.getBoolean("FLAG", false);
        	isCancel = args.getBoolean("CANCEL", false);
        	mTitle = args.getString("TITLE");
        	mContent = args.getString("CONTENT");
        	mLeftBtn = args.getString("LEFT_BTN");
        	mRightBtn = args.getString("RIGHT_BTN");
        	mOneBtn = args.getString("ONE_BTN");
        	
        	mPop_dialog_tv_title.setText(mTitle);
        	mPop_dialog_tv_content.setText(mContent);
		}
        
        if (null == mTitle) {
        	mPop_dialog_tv_title.setVisibility(View.GONE);
		}
        
        if (isTwoButton) {
        	mPop_dialog_tv_one_button.setVisibility(View.GONE);
        	mPop_dialog_tv_left_content.setText(mLeftBtn);
        	mPop_dialog_tv_right_content.setText(mRightBtn);
		}else{
			mPop_dialog_rl_two_button.setVisibility(View.GONE);
			mPop_dialog_tv_one_button.setText(mOneBtn);
		}
        
        mPop_dialog_tv_left_content.setOnClickListener(this);
        mPop_dialog_tv_right_content.setOnClickListener(this);
        mPop_dialog_tv_one_button.setOnClickListener(this);
        
        if (!isCancel) {
        	getDialog().setCancelable(false);
            getDialog().setCanceledOnTouchOutside(false);
            getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        return true;
                    }
                    return false;
                }
            });
		}
        
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
    
    
    /**
     * 创建一个对话框
     * @param flag				标识是一个按钮还是两个按钮 true 两个按钮， false一个按钮
     * @param cancel			点击空白处或者返回键不消失。  true不消失，false消失
     * @param title				标题
     * @param content			内容
     * @param leftBtn
     * @param rightBtn
     * @param oneBtn
     * @return
     */
    public static PopDialogFragment newDialog(boolean flag,boolean cancel,String title,String content,String leftBtn,String rightBtn,String oneBtn) {
    	PopDialogFragment dialog = new PopDialogFragment();
    	Bundle args = new Bundle();
    	
    	args.putBoolean("FLAG", flag);
    	args.putBoolean("CANCEL", cancel);
    	args.putString("TITLE", title);
    	args.putString("CONTENT", content);
    	args.putString("LEFT_BTN", leftBtn);
    	args.putString("RIGHT_BTN", rightBtn);
    	args.putString("ONE_BTN", oneBtn);
    	
		dialog.setArguments(args );
    	
    	return dialog;
	}
    
    @Override
    public void onResume() {
    	super.onResume();
    }
    
	@Override
	public void onDestroy() {
		super.onDestroy();
	}


	
}
