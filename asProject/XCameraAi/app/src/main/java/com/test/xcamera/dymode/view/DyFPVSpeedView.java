package com.test.xcamera.dymode.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.test.xcamera.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2020/2/7.
 */

public class DyFPVSpeedView extends RelativeLayout {
    private static final String TAG = "DyFPVSpeedView";
    @BindView(R.id.dy_fpv_speed_super_slow)
    TextView mSuperSlow;
    @BindView(R.id.dy_fpv_speed_super_slow_layout)
    RelativeLayout mSuperSlowLayout;
    @BindView(R.id.dy_fpv_speed_slow)
    TextView mSlow;
    @BindView(R.id.dy_fpv_speed_slow_layout)
    RelativeLayout mSlowLayout;
    @BindView(R.id.dy_fpv_speed_normal)
    TextView mNormal;
    @BindView(R.id.dy_fpv_speed_normal_layout)
    RelativeLayout mNormalLayout;
    @BindView(R.id.dy_fpv_speed_fast)
    TextView mFast;
    @BindView(R.id.dy_fpv_speed_fast_layout)
    RelativeLayout mFastLayout;
    @BindView(R.id.dy_fpv_speed_super_fast)
    TextView mSuperFast;
    @BindView(R.id.dy_fpv_speed_super_fast_layout)
    RelativeLayout mSuperFastLayout;
    @BindView(R.id.dy_fpv_speed_empty_view)
    View mEmptyLayout;

//    private WeakReference<Context> mContext;
    private ArrayList<View> mViews;
    private ArrayList<TextView> mTextViews;

    private float mRecordSpeed = 1.0f;
    private final float[] mSpeeds = {0.33f, 0.5f, 1.0f, 2f, 3f};

    private WeakReference<DyFPVShotView> mDyFPVShotView;

    public DyFPVSpeedView(Context context) {
        super(context);

//        init(context);
    }

    public DyFPVSpeedView(Context context, AttributeSet attrs) {
        super(context, attrs);

//        init(context);
    }

    public void init(Context context, DyFPVShotView shotView) {
//        mContext = new WeakReference<>(context);
        mDyFPVShotView = new WeakReference<>(shotView);
        LayoutInflater.from(context).inflate(R.layout.dy_fpv_speed_view, this, true);

        ButterKnife.bind(this);

        mNormalLayout.setSelected(true);
        mNormal.setTextColor(0XFF000000);

        mViews = new ArrayList<>();
        mViews.add(mSuperSlowLayout);
        mViews.add(mSlowLayout);
        mViews.add(mNormalLayout);
        mViews.add(mFastLayout);
        mViews.add(mSuperFastLayout);

        mTextViews = new ArrayList<>();
        mTextViews.add(mSuperSlow);
        mTextViews.add(mSlow);
        mTextViews.add(mNormal);
        mTextViews.add(mFast);
        mTextViews.add(mSuperFast);
    }

    @OnClick({R.id.dy_fpv_speed_super_slow_layout, R.id.dy_fpv_speed_slow_layout,
            R.id.dy_fpv_speed_normal_layout, R.id.dy_fpv_speed_fast_layout,
            R.id.dy_fpv_speed_super_fast_layout})
    public void onViewClicked(View view) {
//        if (view.getId() == R.id.dy_fpv_speed_empty_view) {
//            hide();
//        } else {
            selectItem(view.getId());
//        }
    }

    private void selectItem(int resID) {
        for (int i = 0; i < mViews.size(); i++) {
            View view = mViews.get(i);
            TextView textView = mTextViews.get(i);
            if (view.getId() == resID) {
                view.setSelected(true);
                textView.setTextColor(0XFF000000);
                setRecordSpeed(i);
            } else {
                view.setSelected(false);
                textView.setTextColor(0XFFFFFFFF);
            }
        }
    }

    private void setRecordSpeed(int position) {
        mRecordSpeed = mSpeeds[position];
        Log.d(TAG, "setRecordSpeed: " + mRecordSpeed);
    }

    public float getRecordSpeed() {
        return mRecordSpeed;
    }

    private void hide() {
        if (mDyFPVShotView.get() != null)
            mDyFPVShotView.get().clickSpeedLayout();
    }
}
