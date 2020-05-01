package com.android.player.widget.refreshlayout;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.player.R;


public class LoadingMoreFooter extends LinearLayout {

    public final static int STATE_LOADING = 0;
    public final static int STATE_COMPLETE = 1;
    public final static int STATE_NOMORE = 2;
    private TextView mText;
    private AnimationDrawable mAnimationDrawable;
    private ImageView mIvProgress;
    private LinearLayout llRootView;

    public LoadingMoreFooter(Context context) {
        super(context);
        initView(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public LoadingMoreFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.xrecyclerview_refresh_footer, this);
        mText = (TextView) findViewById(R.id.msg);
        mIvProgress = (ImageView) findViewById(R.id.iv_progress);
        llRootView = (LinearLayout) findViewById(R.id.ll_loadMoreFooter);
        mAnimationDrawable = (AnimationDrawable) mIvProgress.getDrawable();
        if (!mAnimationDrawable.isRunning()) {
            mAnimationDrawable.start();
        }
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void setBgColor(String bgColor){
        llRootView.setBackgroundColor(Color.parseColor(bgColor));
    }

    public void setState(int state) {
        setBackgroundResource(R.color.white);
        switch (state) {
            case STATE_LOADING:
                if (!mAnimationDrawable.isRunning()) {
                    mAnimationDrawable.start();
                }
                mIvProgress.setVisibility(View.VISIBLE);
                mText.setText("努力加载中...");
                this.setVisibility(View.VISIBLE);
                break;
            case STATE_COMPLETE:
                if (mAnimationDrawable.isRunning()) {
                    mAnimationDrawable.stop();
                }
                mText.setText("努力加载中...");
                this.setVisibility(View.GONE);
                break;
            case STATE_NOMORE:
                setBackgroundResource(R.color.appBackgroundColor);
                if (mAnimationDrawable.isRunning()) {
                    mAnimationDrawable.stop();
                }
                mText.setText("没有更多了~");
                mIvProgress.setVisibility(View.GONE);
                this.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void reSet() {
        this.setVisibility(GONE);
    }
}
