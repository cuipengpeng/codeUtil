package com.android.player.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.android.player.R;
import com.android.player.fragment.UserInfoFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class HomeContainerActivity extends SlidingFragmentActivity implements OnClickListener{
	private Fragment mLocalMusicFragments;
	private Fragment mGameFragments;
	private Fragment mSettingFragments;
	private Fragment mMoreFragments;
	private Button localMusicButton,gameButton,settingButton,moreButton,contactButton;

	private android.support.v4.app.FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;
	public static SlidingMenu mSlidingMenu;
	private UserInfoFragment userInfoFragment;
	
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSlidingMenu = getSlidingMenu();
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		//设置主界面布局
		setContentView(R.layout.activity_main);
		
		//设置第一个滑动菜单的布局
		setBehindContentView(R.layout.sliding_menu_first);
		
		//设置第二个滑动菜单的布局
//		mSlidingMenu.setSecondaryMenu(R.layout.sliding_menu_second);
		
		fragmentManager = getSupportFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();
		
		userInfoFragment = new UserInfoFragment();
		fragmentTransaction.replace(R.id.fl_home_activity_sliding_menu_left, userInfoFragment);
		fragmentTransaction.commit();
		
		//设置滑动菜单出现的位置
		mSlidingMenu.setMode(SlidingMenu.RIGHT);//SlidingMenu.LEFT_RIGHT左右都可以划出SlidingMenu菜单
		//设置滑动菜单触发时主页面的剩余宽度
		mSlidingMenu.setBehindOffset(50);
		//设置菜单滑动的触发方式
        //TOUCHMODE_FULLSCREEN 全屏模式.在整个content页面中，滑动，可以打开SlidingMenu
        //TOUCHMODE_MARGIN 边缘模式.在content页面中，如果想打开SlidingMenu,你需要在屏幕边缘滑动才可以打开SlidingMenu
        //TOUCHMODE_NONE 不能通过手势打开SlidingMenu
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
        //使用左上方icon可点，这样在onOptionsItemSelected里面才可以监听到R.id.home
//        getActionBar().setDisplayHomeAsUpEnabled(true);
//        getActionBar().setLogo(R.drawable.ic_logo);
        
		mLocalMusicFragments = fragmentManager.findFragmentById(R.id.fragment_local_music);
		mGameFragments = fragmentManager.findFragmentById(R.id.fragment_game);
		mSettingFragments = fragmentManager.findFragmentById(R.id.fragment_setting);
		mMoreFragments = fragmentManager.findFragmentById(R.id.fragment_more);
		
		fragmentTransaction = fragmentManager.beginTransaction().hide(mLocalMusicFragments).hide(mGameFragments).hide(mSettingFragments).hide(mMoreFragments);
		fragmentTransaction.show(mLocalMusicFragments).commit();

		//BottomButton
		localMusicButton = (Button)findViewById(R.id.btn_homeActivity_localMusic);
		localMusicButton.setOnClickListener(this);
		gameButton = (Button)findViewById(R.id.btn_homeActivity_game);
		gameButton.setOnClickListener(this);
		contactButton =(Button)findViewById(R.id.btn_homeActivity_contact);
		contactButton.setOnClickListener(this);
		settingButton =(Button)findViewById(R.id.btn_homeActivity_setting);
		settingButton.setOnClickListener(this);
		moreButton =(Button)findViewById(R.id.btn_homeActivity_more);
		moreButton.setOnClickListener(this);
		
		localMusicButton.setSelected(true);
		localMusicButton.setTextColor(Color.WHITE);
	}

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
    
	@Override
	public void onClick(View v) {
		
		fragmentTransaction = fragmentManager.beginTransaction().hide(mLocalMusicFragments).hide(mGameFragments).hide(mSettingFragments).hide(mMoreFragments);
		localMusicButton.setTextColor(Color.WHITE);
		gameButton.setTextColor(Color.WHITE);
		contactButton.setTextColor(Color.WHITE);
		settingButton.setTextColor(Color.WHITE);
		moreButton.setTextColor(Color.WHITE);
		
		localMusicButton.setSelected(false);
		gameButton.setSelected(false);
		settingButton.setSelected(false);
		moreButton.setSelected(false);
		contactButton.setSelected(false);
		
		switch(v.getId()){
		case R.id.btn_homeActivity_localMusic:
			fragmentTransaction.show(mLocalMusicFragments).commit();
			localMusicButton.setTextColor(Color.GRAY);
			localMusicButton.setSelected(true);
			break;
		case R.id.btn_homeActivity_game:
			fragmentTransaction.show(mGameFragments).commit();
			gameButton.setTextColor(Color.GRAY);
			gameButton.setSelected(true);
			break;
		case R.id.btn_homeActivity_contact:
			startActivity(new Intent(this, CameraActivity.class));
			contactButton.setTextColor(Color.GRAY);
			contactButton.setSelected(true);
			break;
		case R.id.btn_homeActivity_setting:
			fragmentTransaction.show(mSettingFragments).commit();
			settingButton.setTextColor(Color.GRAY);
			settingButton.setSelected(true);
			break;
		case R.id.btn_homeActivity_more:
			fragmentTransaction.show(mMoreFragments).commit();
			moreButton.setTextColor(Color.GRAY);
			moreButton.setSelected(true);
			break;
		}
	}
}


