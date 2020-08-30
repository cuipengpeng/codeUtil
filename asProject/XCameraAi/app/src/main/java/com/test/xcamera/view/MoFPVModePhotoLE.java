package com.test.xcamera.view;

import android.content.Context;
import android.view.View;

import com.test.xcamera.R;
import com.test.xcamera.bean.MoSettingModel;
import com.test.xcamera.enumbean.ScreenOrientationType;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.mointerface.MoRequestCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by smz on 2019/12/19.
 * <p>
 * 长曝光模式
 */

public class MoFPVModePhotoLE extends MoFPVModeBase {
    private static final List<Entity> list = new ArrayList<>();
    private static final int time[] = {0/*关闭*/, 2, 3, 4, 5};
    private SingleParamsView mParamsWindow;

    static {
        list.add(new Entity(Entity.PORTRAIT, "关闭", time[0], true));
        for (int i = 1; i < time.length; i++) {
            list.add(new Entity(Entity.PORTRAIT, time[i] + "秒", time[i]));
        }
    }

    @Override
    void initMode(Context context, MoFPVModeView modeViews) {
        super.initMode(context, modeViews);
        mParamsWindow = null;

        modeViews.mModeViews.customView24.setImageResource(R.mipmap.icon_fpv_long_explore);
    }

    @Override
    void syncHeaderView(MoFPVModeView.ModeViews modeViews) {

    }

    @Override
    void showParamView() {
        if (mParamsWindow == null) {
            mFpvModeView.mModeViews.paramView.removeAllViews();
            mParamsWindow = getSingleLineParams(mContext, list, "曝光时长");
            mFpvModeView.mModeViews.paramView.addView(mParamsWindow);
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

        int longExpotime = shotSetting.snapshotModel.longexpotime;
        int index = list.indexOf(longExpotime);
        syncLongExpotime(index == -1 ? 0 : index);
    }

    @Override
    void orientation(ScreenOrientationType type) {
        if (mParamsWindow != null)
            mParamsWindow.orientation(type);
    }

    private void syncLongExpotime(int position) {
        for (int i = 0; i < list.size(); i++)
            list.get(i).selectedFlag = i == position;
        mFpvModeView.mModeViews.customViewValue.setText(position == 0 ? "" : list.get(position).text);
    }

    protected boolean selectedParams(int position) {
        ConnectionManager.getInstance().setLongExplore(time[position], new MoRequestCallback() {
            @Override
            public void onSuccess() {
                syncLongExpotime(position);
            }

            @Override
            public void onFailed() {
            }
        });

        return true;
    }
}
