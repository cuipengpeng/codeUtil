package com.test.xcamera.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.effect_opengl.EffectManager;
import com.test.xcamera.R;
import com.test.xcamera.adapter.MoSelectShotModeAdapter;
import com.test.xcamera.enumbean.WorkState;
import com.test.xcamera.managers.WorkStateManager;

import java.util.ArrayList;

/**
 * Created by zll on 2019/10/21.
 */

public class MoSelectShotModeView extends RelativeLayout {
    private static final String TAG = "MoSelectShotModeView";
    public static final int LONG_EXPLORE = 0;
    public static final int PHOTO = 1;
    public static final int VIDEO = 2;
    public static final int SLOW_MOTION = 3;
    public static final int LAPSE = 4;
    public static final int BEAUTY = 5;
    private FlingRecycleView mRecyclerView;
    private Context mContext;
    private MoSelectShotModeAdapter mSelectShotModeAdapter;
    private CurrentShotModeListener mCurrentIndexListener;
    private long delayTime;
    private int initPosition = -1;

    public MoSelectShotModeView(Context context) {
        super(context);

        initView(context);
    }

    public MoSelectShotModeView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView(context);
    }

    public void setBottomSelectorCurrentIndexListener(CurrentShotModeListener listener) {
        mCurrentIndexListener = listener;
    }

    private void initView(Context context) {
        mContext = context;

        LayoutInflater.from(context).inflate(R.layout.select_shot_mode_layout, this, true);


        mRecyclerView = findViewById(R.id.select_shot_mode_recycler_view);
        mRecyclerView.setFlingAble(false);
        /**
         * 配合LONG_EXPLORE、PHOTO等 确定模式的位置
         * */
        ArrayList<Integer> list = new ArrayList<>();
        list.add(R.string.fpv_shot_mode_longexplore);
        list.add(R.string.fpv_shot_mode_photo);
        list.add(R.string.fpv_shot_mode_video);
        list.add(R.string.fpv_shot_mode_slowmotion);
        list.add(R.string.fpv_shot_mode_lapse);
        int initPos = list.indexOf(R.string.fpv_shot_mode_video);
        lastChangeFlag = initPos;
//        list.add(R.string.fpv_shot_mode_beauty);

        final GalleryLayoutManager layoutManager2 = new GalleryLayoutManager(GalleryLayoutManager.VERTICAL, false);
        //默认为视频模式
        layoutManager2.attach(mRecyclerView, initPos);
        layoutManager2.setCallBackOnce(true);
        layoutManager2.setCallbackInFling(true);
        layoutManager2.setOnItemSelectedListener(new GalleryLayoutManager.OnItemSelectedListener() {
            @Override
            public void onItemSelected(RecyclerView recyclerView, View item, int position) {
//                Log.e("=====", String.format("currentShotMode: %d  %b", position, changeFlag) + mCurrentIndexListener);
                if (mCurrentIndexListener != null && !changeFlag) {
                    WorkStateManager.getInstance().preSwitchTime = System.currentTimeMillis();
                    if (position != 5) {
                        EffectManager.instance().clearFilter();
                    }
                    mCurrentIndexListener.currentShotMode(position);
                }
                changeFlag = false;
            }
        });

        mSelectShotModeAdapter = new MoSelectShotModeAdapter(list);
        mSelectShotModeAdapter.setOnItemClickListener(new MoSelectShotModeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                WorkStateManager.getInstance().preSwitchTime = System.currentTimeMillis();
                changeFlag = false;
                //拍照模式不可点击
                if (canChange()) {
                    mRecyclerView.smoothScrollToPosition(position);
                }
            }
        });
        mRecyclerView.setAdapter(mSelectShotModeAdapter);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = !canChange();
//        Logcat.v().tag(LogcatConstants.FPV_PREVIEW).msg("onInterceptTouchEvent  intercept==>" + intercept).out();
        return intercept;
    }

    private boolean canChange() {
//        Logcat.v().tag(LogcatConstants.FPV_PREVIEW).msg("MoSelectShotModeView canChange()==>Standby:"
//                + WorkStateManager.getInstance().getmWorkState() + "   switchMode:" + !WorkStateManager.getInstance().switchMode).out();
        return WorkStateManager.getInstance().getmWorkState() == WorkState.STANDBY
                && !WorkStateManager.getInstance().enableSwitch()
                && !WorkStateManager.getInstance().isSetting();
    }

    private boolean delayTime() {
        if (System.currentTimeMillis() - delayTime < 500)
            return false;
        delayTime = System.currentTimeMillis();
        return true;
    }

    private boolean changeFlag = false;
    private int lastChangeFlag = -1;

    public void changeMode(int position) {
        if (mRecyclerView != null && lastChangeFlag != position) {
            changeFlag = true;
            mRecyclerView.smoothScrollToPosition(position);
        }
        lastChangeFlag = position;
    }

}
