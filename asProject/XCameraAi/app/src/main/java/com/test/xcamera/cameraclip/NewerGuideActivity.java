package com.test.xcamera.cameraclip;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.framwork.base.view.MOBaseActivity;
import com.test.xcamera.R;
import com.test.xcamera.accrssory.AccessoryManager;
import com.test.xcamera.activity.DyFPVActivity;
import com.test.xcamera.utils.SPUtils;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class NewerGuideActivity extends MOBaseActivity {

    @BindView(R.id.iv_newerGuideActivity_img)
    GifImageView contentImageView;
    @BindView(R.id.tv_newerGuideActivity_skip)
    TextView skipTextView;
    @BindView(R.id.tv_newerGuideActivity_skipBottom)
    TextView skipBottomTextView;

    RequestOptions options = new RequestOptions()
            .placeholder(R.mipmap.bj_net)//图片加载出来前，显示的图片
            .fallback(R.mipmap.bj_net) //url为空的时候,显示的图片
            .error(R.mipmap.bj_net);//图片加载失败后，显示的图片;
    public static final String KEY_OF_GOUP = "goupGuideKey";
    public static final String KEY_OF_TODAY_WONDERFUL = "todayWonderfulGuideKey";
    public static final String KEY_OF_DOU_YIN = "douYinGuideKey";
    public static final String KEY_OF_ALBUM = "AlbumGuideKey";

    private int currentGuideType = -1;
    private int guideStep = 1;
    public static final String KEY_OF_GUIDE_TYPE = "guideTypeKey";
    public static final int NEWER_GUIDE_GOUP = 1001;
    public static final int NEWER_GUIDE_TODAY_WONDERFUL = 1002;
    public static final int NEWER_GUIDE_DOU_YIN = 1003;
    public static final int NEWER_GUIDE_ALBUM = 1004;


    @OnClick({R.id.iv_newerGuideActivity_img, R.id.tv_newerGuideActivity_skip, R.id.tv_newerGuideActivity_skipBottom})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_newerGuideActivity_skip:
            case R.id.tv_newerGuideActivity_skipBottom:
                switch (currentGuideType){
                    case NEWER_GUIDE_TODAY_WONDERFUL:
                        getSharedPreferences(SPUtils.FILE_NAME, Context.MODE_PRIVATE).edit().putBoolean(KEY_OF_TODAY_WONDERFUL, true).commit();
                        break;
                    case NEWER_GUIDE_DOU_YIN:
                        getSharedPreferences(SPUtils.FILE_NAME, Context.MODE_PRIVATE).edit().putBoolean(KEY_OF_DOU_YIN, true).commit();
                        break;
                    case NEWER_GUIDE_ALBUM:
                        getSharedPreferences(SPUtils.FILE_NAME, Context.MODE_PRIVATE).edit().putBoolean(KEY_OF_ALBUM, true).commit();
                        break;
                }
                finishPage();
                break;
            case R.id.iv_newerGuideActivity_img:
                guideStep += 1;
                if(guideStep>1){
                    skipTextView.setVisibility(View.GONE);
                    skipBottomTextView.setVisibility(View.GONE);
                }
                switch (currentGuideType) {
                    case NEWER_GUIDE_GOUP:
                        switch (guideStep) {
                            case 1:
                                updateImageView(R.mipmap.guide_goup_1);
                                break;
                            case 2:
                                updateImageView(R.mipmap.guide_goup_2);
                                break;
                            case 3:
                                updateImageView(R.mipmap.guide_goup_3);
                                break;
                            case 4:
                                updateImageView(R.mipmap.guide_goup_4);
                                break;
                            case 5:
                                updateImageView(R.mipmap.guide_feed);
                                getSharedPreferences(SPUtils.FILE_NAME, Context.MODE_PRIVATE).edit().putBoolean(KEY_OF_GOUP, true).commit();
                                break;
                            case 6:
                                finishPage();
                                break;
                        }
                        break;
                    case NEWER_GUIDE_TODAY_WONDERFUL:
                        switch (guideStep) {
                            case 1:
                                updateImageView(R.mipmap.guide_today_wonderful_1);
                                break;
                            case 2:
                                updateImageView(R.mipmap.guide_today_wonderful_2);
                                break;
                            case 3:
                                updateImageView(R.mipmap.guide_today_wonderful_3);
                                break;
                            case 4:
                                updateImageView(R.mipmap.guide_today_wonderful_4);
                                break;
                            case 5:
                                updateImageView(R.mipmap.guide_today_wonderful_5);
                                getSharedPreferences(SPUtils.FILE_NAME, Context.MODE_PRIVATE).edit().putBoolean(KEY_OF_TODAY_WONDERFUL, true).commit();
                                break;
                            case 6:
                                finishPage();
                                break;
                        }
                        break;
                    case NEWER_GUIDE_DOU_YIN:
                        switch (guideStep) {
                            case 1:
                                updateImageView(R.mipmap.douyin_1);
                                break;
                            case 2:
                                updateImageView(R.mipmap.douyin_2);
                                break;
                            case 3:
                                updateImageView(R.mipmap.douyin_3);
                                getSharedPreferences(SPUtils.FILE_NAME, Context.MODE_PRIVATE).edit().putBoolean(KEY_OF_DOU_YIN, true).commit();
                                break;
                            case 4:
                                finishPage();
                                break;
                        }
                        break;
                    case NEWER_GUIDE_ALBUM:
                        switch (guideStep) {
                            case 1:
                                updateImageView(R.mipmap.album_1);
                                break;
                            case 2:
                                updateImageView(R.mipmap.album_2);
                                break;
                            case 3:
                                updateImageView(R.mipmap.album_3);
                                break;
                            case 4:
                                updateImageView(R.mipmap.album_4);
                                getSharedPreferences(SPUtils.FILE_NAME, Context.MODE_PRIVATE).edit().putBoolean(KEY_OF_ALBUM, true).commit();
                                break;
                            case 5:
                                finishPage();
                                break;
                        }
                        break;
                }
                break;
        }
    }

    private void finishPage() {
        if ((currentGuideType == NEWER_GUIDE_DOU_YIN) && AccessoryManager.getInstance().mIsRunning) {
            Intent intentDouYin = new Intent(this, DyFPVActivity.class);
            startActivity(intentDouYin);
        }
        finish();
        overridePendingTransition(R.anim.push_slient, R.anim.push_bottom_out);
    }

    @Override
    public int initView() {
        return R.layout.activity_newer_guide;
    }

    @Override
    public void initData() {
        currentGuideType = getIntent().getIntExtra(KEY_OF_GUIDE_TYPE, -1);
        guideStep = 1;
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(skipBottomTextView, "rotation", 0f, 180f);
        objectAnimator.setRepeatCount(1);
        objectAnimator.setDuration(10);
        objectAnimator.start();
        skipBottomTextView.setVisibility(View.GONE);
        skipTextView.setVisibility(View.GONE);
        switch (currentGuideType) {
            case NEWER_GUIDE_GOUP:
                updateImageView(R.mipmap.guide_goup_1);
                break;
            case NEWER_GUIDE_TODAY_WONDERFUL:
                skipTextView.setVisibility(View.VISIBLE);
                updateImageView(R.mipmap.guide_today_wonderful_1);
                break;
            case NEWER_GUIDE_DOU_YIN:
                skipBottomTextView.setVisibility(View.VISIBLE);
                updateImageView(R.mipmap.douyin_1);
                break;
            case NEWER_GUIDE_ALBUM:
                skipTextView.setVisibility(View.VISIBLE);
                updateImageView(R.mipmap.album_1);
                break;
        }
    }

    private void updateImageView(int resourceID) {
        GifDrawable gifDrawable = null;
        try {
            gifDrawable = new GifDrawable(getResources(), resourceID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        gifDrawable.setLoopCount(Character.MAX_VALUE);
        contentImageView.setImageDrawable(gifDrawable);
    }

    public static void open(FragmentActivity context, int guideType) {
        Intent intent = new Intent(context, NewerGuideActivity.class);
        intent.putExtra(KEY_OF_GUIDE_TYPE, guideType);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.push_slient, R.anim.push_bottom_out);
    }
}
