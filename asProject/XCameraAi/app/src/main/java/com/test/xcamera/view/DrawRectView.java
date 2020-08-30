package com.test.xcamera.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.test.xcamera.bean.FrameRectInfo;
import com.test.xcamera.utils.PUtil;

import java.util.ArrayList;

public class DrawRectView extends View {
    public static final String TAG = "DrawRectView";
    private static final int FRAME_WIDTH = 640;
    private static final int FRAME_HEIGHT = 480;
    private Rect mHumanRect;
    private Paint humanpaint1, humanpaint2, humanpaint3;
    private boolean mShowRect = false;
    private ArrayList<Rect> mRects;
    private ArrayList<Rect> mDefaultRects;
    private int mScreenWidth, mScreenHeight;
    private ArrayList<Paint> mPaints;
    private boolean mIsClear;

    public DrawRectView(Context context) {
        super(context);
    }

    public DrawRectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        humanpaint1 = new Paint();
        humanpaint1.setStrokeWidth(3);
        humanpaint1.setColor(Color.RED);
        humanpaint1.setStyle(Paint.Style.STROKE);
        humanpaint1.setAntiAlias(true);

        humanpaint2 = new Paint();
        humanpaint2.setStrokeWidth(3);
        humanpaint2.setColor(Color.YELLOW);
        humanpaint2.setStyle(Paint.Style.STROKE);
        humanpaint2.setAntiAlias(true);

        humanpaint3 = new Paint();
        humanpaint3.setStrokeWidth(3);
        humanpaint3.setColor(Color.BLUE);
        humanpaint3.setStyle(Paint.Style.STROKE);
        humanpaint3.setAntiAlias(true);

        mPaints = new ArrayList<>();
        mDefaultRects = new ArrayList<>();
        mPaints.add(humanpaint1);
        mPaints.add(humanpaint2);
        mPaints.add(humanpaint3);

        mRects = new ArrayList<>();
        mScreenWidth = PUtil.getScreenW(context);
        mScreenHeight = PUtil.getScreenH(context);
    }

    public DrawRectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DrawRectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setShowRect(boolean b) {
        mShowRect = b;
    }

    public void setHumanPainColor(int value) {
//        if (value == 0) {
//            humanpaint.setColor(Color.GREEN);
//        } else if (value == 1) {
//            humanpaint.setColor(Color.RED);
//        }
    }

    public void setHumanRect(ArrayList<FrameRectInfo> frameRectInfos) {
        Log.d(TAG, "setHumanRect: " + frameRectInfos.size());
        if (mRects != null) {
            mRects.clear();
        }
        for (int i = 0; i < frameRectInfos.size(); i++) {
            FrameRectInfo frameRectInfo = frameRectInfos.get(i);
            Rect rect = new Rect();
//            rect.left = frameRectInfo.getmStartX() * mScreenWidth / FRAME_WIDTH;
//            rect.top = frameRectInfo.getmStatrY() * mScreenHeight / FRAME_HEIGHT;
//            rect.right = frameRectInfo.getmEndX() * mScreenWidth / FRAME_WIDTH;
//            rect.bottom = frameRectInfo.getmEndY() * mScreenHeight / FRAME_HEIGHT;
            mRects.add(rect);
        }
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d("draw view rect = ", "" + mRects.size());

        if (mShowRect) {
//            int size = mRects.size();
//            if (size == 0) {
//                for (int i = 0; i < 3; i++) {
//                    canvas.drawRect(new Rect(0, 0, 0, 0), mPaints.get(i));
//                }
//            } else {
                for (int i = 0; i < mRects.size(); i++) {
                    canvas.drawRect(mRects.get(i), mPaints.get(i));
                }
//            }

        }
        super.onDraw(canvas);
    }
}
