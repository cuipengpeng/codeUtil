package com.test.xcamera.view;

import android.animation.Animator;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.test.xcamera.R;
import com.test.xcamera.bean.MoImage;
import com.test.xcamera.bean.MoSettingModel;
import com.test.xcamera.enumbean.ScreenOrientationType;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.mointerface.MoTaklePhotoCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by smz on 2019/12/19.
 */
public class MoFPVModePhoto extends MoFPVModeBase {
    private static final List<Entity> list = new ArrayList<>();
    private static final int time[] = {0/*关闭*/, 3, 5, 7};
    private SingleParamsView mParamsWindow;
    private int delayTime;

    static {
        list.add(new Entity(Entity.PORTRAIT, "关闭", time[0], true));
        for (int i = 1; i < time.length; i++)
            list.add(new Entity(Entity.PORTRAIT, time[i] + "秒", time[i]));
    }

    @Override
    void initMode(Context context, MoFPVModeView modeViews) {
        super.initMode(context, modeViews);

        mParamsWindow = null;
        syncHeaderView(modeViews.mModeViews);
        modeViews.mModeViews.recordView.setImageResource(R.mipmap.icon_shot);
        modeViews.mModeViews.recordContent.setOnClickListener((view) -> {
//            if (!checkConn()) {
//                setShotState(MoFPVModeBase.CANCEL);
//                return;
//            }
            setShotState(MoFPVModeBase.PHOTO_NORMAL);
            delayAnimation(modeViews.mModeViews);

            ConnectionManager.getInstance().takePhoto(new MoTaklePhotoCallback() {
                @Override
                public void onSuccess(MoImage image) {
                    if (image == null || TextUtils.isEmpty(image.getmUri()))
                        modeViews.mModeViews.rightImg.setImageResource(R.mipmap.icon_fpv_playback);
                    else
                        Glide.with(mContext)
                                .load(image.getmUri())
                                .apply(new RequestOptions()
                                        .error(R.mipmap.icon_fpv_playback))
                                .into(modeViews.mModeViews.rightImg);
                }

                @Override
                public void onFailed(int reason) {
                    modeViews.mModeViews.rightImg.setImageResource(R.mipmap.icon_fpv_playback);
                }
            });
        });
    }

    @Override
    void syncHeaderView(MoFPVModeView.ModeViews modeViews) {
        super.syncHeaderView(modeViews);
        modeViews.customView24.setVisibility(View.VISIBLE);
        modeViews.customView24.setImageResource(R.mipmap.icon_fpv_mode_count_down);
    }

    @Override
    void showParamView() {
        if (mParamsWindow == null) {
            this.mFpvModeView.mModeViews.paramView.removeAllViews();
            mParamsWindow = getSingleLineParams(mContext, list, "倒计时");
            this.mFpvModeView.mModeViews.paramView.addView(mParamsWindow);
            mParamsWindow.mAdapter.setOnItemChildClickListener((adapter1, view1, position) -> {
                mParamsWindow.changeItem(position);
                int delayTime = (int) ((Entity) adapter1.getData().get(position)).source;
                ConnectionManager.getInstance().setDelayTime(delayTime, () -> {
                    syncDelayTime(position);
                });
            });
        } else if (mParamsWindow.getVisibility() != View.VISIBLE)
            mParamsWindow.setVisibility(View.VISIBLE);
    }

    @Override
    void hideParamView() {
        if (mParamsWindow != null)
            mParamsWindow.setVisibility(View.GONE);
    }

    @Override
    void syncParams(MoSettingModel shotSetting) {
        if (shotSetting == null || shotSetting.snapshotModel == null)
            return;

        int delayTime = shotSetting.snapshotModel.delaytime / 100;
        int index = list.indexOf(delayTime);
        syncDelayTime(index == -1 ? 0 : index);
    }

    @Override
    void orientation(ScreenOrientationType type) {
        if (mParamsWindow != null)
            mParamsWindow.orientation(type);
    }

    private void syncDelayTime(int position) {
        for (int i = 0; i < list.size(); i++)
            list.get(i).selectedFlag = i == position;
        mFpvModeView.mModeViews.customViewValue.setText(position == 0 ? "" : list.get(position).text);
        delayTime = time[position];
    }

    private void delayAnimation(MoFPVModeView.ModeViews modeViews) {
        if (modeViews.delayTimeLottie != null && delayTime > 0) {
            modeViews.delayTimeLottie.setVisibility(View.VISIBLE);
            modeViews.delayTimeLottie.loop(false);
            modeViews.delayTimeLottie.setAnimation(String.format(Locale.ENGLISH, "animation/%d_seconds.json.json", delayTime));
            modeViews.delayTimeLottie.playAnimation();

            modeViews.delayTimeLottie.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    modeViews.delayTimeLottie.removeAllAnimatorListeners();
                    modeViews.delayTimeLottie.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }
}
