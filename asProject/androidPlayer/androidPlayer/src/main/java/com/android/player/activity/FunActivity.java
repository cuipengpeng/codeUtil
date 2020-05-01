package com.android.player.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.android.player.R;
import com.android.player.adapter.ContentFragmentPagerAdapter;

public class FunActivity extends FragmentActivity implements View.OnClickListener{
	private ViewPager mViewPager;
	private static final String[] titles = {"One","Two","Three","Four","Five"};
	private List<String> list = new ArrayList<String>();
	private ContentFragmentPagerAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fun_activity);
		for (int i=0;i<titles.length;i++){
			list.add(titles[i]);
		}
		
		mViewPager = (ViewPager)findViewById(R.id.mViewPager);
		
		PagerTabStrip mPagerTabStrip = (PagerTabStrip) findViewById(R.id.mPagerTabStrip);
		mPagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.select_text_color)); 
		
		mAdapter = new ContentFragmentPagerAdapter(getSupportFragmentManager(),list);
		mViewPager.setAdapter(mAdapter);
	}

	@Override
	public void onClick(View v) {

	}
	
}
