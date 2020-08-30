package com.test.xcamera.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.test.xcamera.R;
import com.test.xcamera.mointerface.MoFPVCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 画框跟踪
 * Created by zll on 2019/10/25.
 */

public class MoFPVDrawRectTrackView extends LinearLayout {
    @BindView(R.id.finger_rect_view)
    FingerTrackView mFingerRectView;

    private Unbinder mUnbinder;
    private MoFPVCallback mFPVCallback;
    private boolean mNeedDraw = false;

    public MoFPVDrawRectTrackView(Context context) {
        super(context);

        init(context);
    }

    public MoFPVDrawRectTrackView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public void setMoFPVCallback(MoFPVCallback callback) {
        mFPVCallback = callback;
    }

    public void setNeedDraw(boolean needDraw) {
        mNeedDraw = needDraw;
        mFingerRectView.setNeedDraw(mNeedDraw);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.draw_rect_track_view, this, true);

        mUnbinder = ButterKnife.bind(this);

        mFingerRectView.setRectLocationListener(new FingerTrackView.RectLocationListener() {
            @Override
            public void location(int x, int y, int width, int height) {
                int[] values = new int[]{x, y, width, height};
                mFPVCallback.startFingerTrack(values);
            }
        });
    }

    public boolean onTouch(MotionEvent event) {
        if (mFingerRectView != null)
            return mFingerRectView.onTouch(event);
        return false;
    }

    public void unBindButterKnife() {
        mUnbinder.unbind();
    }

    public void hide() {
        mFingerRectView.hide();
    }

    public boolean isHide() {
        return mFingerRectView.isHide();
    }
}
