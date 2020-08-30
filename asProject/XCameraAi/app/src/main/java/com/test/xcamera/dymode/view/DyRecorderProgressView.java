//package com.meetvr.aicamera.dymode.view;
//
//import android.content.Context;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.os.Handler;
//import android.os.Message;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.View;
//
//import com.meetvr.aicamera.R;
//import com.meetvr.aicamera.dymode.utils.DisplayUtil;
//
//import java.lang.ref.WeakReference;
//import java.util.Iterator;
//
//public class DyRecorderProgressView extends View {
//    private static final String TAG = "DyRecorderProgressView";
//    /**
//     * 进度条
//     */
//    private Paint mProgressPaint;
//    /**
//     * 闪
//     */
//    private Paint mActivePaint;
//    /**
//     * 暂停/中断色块
//     */
//    private Paint mPausePaint;
//    /**
//     * 回删
//     */
//    private Paint mRemovePaint;
//    /**
//     * 三秒
//     */
//    private Paint mThreePaint;
//    /**
//     * 超时
//     */
//    private boolean mStop, mProgressChanged;
//    private boolean mActiveState = false;
//    private MediaObject mMediaObject;
//    /**
//     * 最长时长
//     */
//    private int mMaxDuration, mVLineWidth;
//    private boolean isShowEnoughTime = false;
//
//    public DyRecorderProgressView(Context paramContext) {
//        super(paramContext);
//        init();
//    }
//
//    public DyRecorderProgressView(Context paramContext, AttributeSet paramAttributeSet) {
//        super(paramContext, paramAttributeSet);
//        init();
//    }
//
//    public DyRecorderProgressView(Context paramContext, AttributeSet paramAttributeSet,
//                                  int paramInt) {
//        super(paramContext, paramAttributeSet, paramInt);
//        init();
//    }
//
//    private void init() {
//        Log.d(TAG, "init: ");
//        mProgressPaint = new Paint();
//        mActivePaint = new Paint();
//        mPausePaint = new Paint();
//        mRemovePaint = new Paint();
//        mThreePaint = new Paint();
//
//        mVLineWidth = DisplayUtil.dipToPx(getContext(), 1);
//
//        setBackgroundColor(getResources().getColor(R.color.middle_gray));
//        mProgressPaint.setColor(Color.parseColor("#F6CB14"));
//        mProgressPaint.setStyle(Paint.Style.FILL);
//
//        mActivePaint.setColor(getResources().getColor(R.color.white));
//        mActivePaint.setStyle(Paint.Style.FILL);
//
//        mPausePaint.setColor(getResources().getColor(
//                R.color.white));
//        mPausePaint.setStyle(Paint.Style.FILL);
//
//        mRemovePaint.setColor(getResources().getColor(
//                R.color.red));
//        mRemovePaint.setStyle(Paint.Style.FILL);
//
//        mThreePaint.setColor(getResources().getColor(
//                R.color.middle_gray));
//        mThreePaint.setStyle(Paint.Style.FILL);
//    }
//
//    /**
//     * 闪动
//     */
//    private final static int HANDLER_INVALIDATE_ACTIVE = 0;
//    /**
//     * 录制中
//     */
//    private final static int HANDLER_INVALIDATE_RECORDING = 1;
//
//    private static class MyHandler extends Handler {
//        WeakReference<DyRecorderProgressView> mWeakReference;
//
//        public MyHandler(WeakReference<DyRecorderProgressView> weakReference) {
//            mWeakReference = weakReference;
//        }
//
//        @Override
//        public void dispatchMessage(Message msg) {
//            DyRecorderProgressView mRecorderProgressView = mWeakReference.get();
//            switch (msg.what) {
//                case HANDLER_INVALIDATE_ACTIVE:
//                    if (!mRecorderProgressView.mStop)
//                        sendEmptyMessageDelayed(HANDLER_INVALIDATE_ACTIVE, 50);
//                    mRecorderProgressView.invalidate();
//                    if (!mRecorderProgressView.mProgressChanged) {
//                        mRecorderProgressView.mActiveState = !mRecorderProgressView.mActiveState;
//                    } else {
//                        mRecorderProgressView.mActiveState = true;
//                    }
//                    break;
//                case HANDLER_INVALIDATE_RECORDING:
//                    mRecorderProgressView.invalidate();
//                    if (mRecorderProgressView.mProgressChanged)
//                        sendEmptyMessageDelayed(HANDLER_INVALIDATE_ACTIVE, 50);
//                    break;
//            }
//        }
//    }
//
//    private Handler mHandler = new MyHandler(new WeakReference<DyRecorderProgressView>(this));
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        Log.d(TAG, "MediaObject onDraw: ");
//        super.onDraw(canvas);
//        final int width = getMeasuredWidth(), height = getMeasuredHeight();
//        int left = 0, right = 0, duration = 0;
////		if (mMediaObject != null && mMediaObject.getMedaParts() != null) {
//        if (mMediaObject != null && mMediaObject.getMedaParts().size() > 0) {
//            Log.d(TAG, "onDraw: mMediaObject != null, parts size = " + mMediaObject.getMedaParts().size());
//            left = right = 0;
//            Iterator<MediaObject.MediaPart> iterator = mMediaObject
//                    .getMedaParts().iterator();
//            boolean hasNext = iterator.hasNext();
//            int maxDuration = mMaxDuration;
//            boolean hasOutDuration = false;
//            int currentDuration = mMediaObject.getDuration();
//            hasOutDuration = currentDuration > mMaxDuration;
//            if (isNeedCountDown) {
//                Log.d(TAG, "onDraw: 11111");
//                if (currentDuration > mCountDownTime && mOverTimeClickListener != null) {
//                    mOverTimeClickListener.isArriveCountDown();
//                    isNeedCountDown = false;
//                }
//            }
//            if (hasOutDuration) {
//                Log.e(TAG, "onDraw:      " + currentDuration + "mMaxduration" + mMaxDuration);
//                maxDuration = currentDuration;
//                if (mOverTimeClickListener != null) {
//                    mOverTimeClickListener.overTime();
////					mProgressChanged = false;
////					mStop = true;
////					mHandler.removeMessages(HANDLER_INVALIDATE_ACTIVE);
//                }
//            }
//            while (hasNext) {
//                Log.d(TAG, "onDraw: 22222");
//                MediaObject.MediaPart vp = iterator.next();
//                final int partDuration = vp.getDuration();
//                Log.d(TAG, "partDuration: " + partDuration);
//                left = right;
//                right = left
//                        + (int) (partDuration * 1.0F / maxDuration * width);
////				if (vp.remove) {
////					canvas.drawRect(left, 0.0F, right, height, mRemovePaint);
////				} else {
//                // 画进度
//                // 回删
//                canvas.drawRect(left, 0.0F, right, height,
//                        mProgressPaint);
////				}
//                hasNext = iterator.hasNext();
//                if (hasNext) {
//                    // left = right - mVLineWidth;
//                    canvas.drawRect(right - mVLineWidth, 0.0F, right, height,
//                            mPausePaint);
//                }
//
//                duration += partDuration;
//                // progress = vp.progress;
//            }
//        }
//        Log.e(TAG, "onDraw:  duration == " + duration);
//        // 画三秒
//        if (duration < 5300) {
//            left = (int) (5300F / mMaxDuration * width);
////			canvas.drawRect(left, 0.0F, left + mVLineWidth, height, mThreePaint);
//        } else if (!isShowEnoughTime) {
//            if (mOverTimeClickListener != null) {
//                mOverTimeClickListener.noEnoughTime();
//                isShowEnoughTime = true;
//            }
//        }
//        // 闪
//        if (mActiveState) {
//        }
//        if (right + 8 >= width)
//            right = width - 8;
//        canvas.drawRect(right, 0.0F, right + 8, getMeasuredHeight(),
//                mActivePaint);
//
//    }
//
//    private float mCountDownTime;
//    private boolean isNeedCountDown = false;
//
//    public void setCountDownTime(float countTime) {
//        mCountDownTime = countTime;
//        isNeedCountDown = true;
//    }
//
//    public void setShowEnouchTime(boolean is) {
//        isShowEnoughTime = is;
//    }
//
//    public void startRecording() {
//        mStop = false;
//        isShowEnoughTime = false;
//        mHandler.sendEmptyMessage(HANDLER_INVALIDATE_ACTIVE);
//    }
//
//    @Override
//    protected void onAttachedToWindow() {
//        Log.d(TAG, "onAttachedToWindow: ");
//        super.onAttachedToWindow();
//        attach();
//    }
//
//    @Override
//    protected void onDetachedFromWindow() {
//        Log.d(TAG, "onDetachedFromWindow: ");
//        super.onDetachedFromWindow();
//        detached();
//    }
//
//    public void detached() {
//        mStop = true;
//        mHandler.removeMessages(HANDLER_INVALIDATE_ACTIVE);
//    }
//
//    public void attach() {
//        mStop = false;
//        mHandler.sendEmptyMessage(HANDLER_INVALIDATE_ACTIVE);
//    }
//
////	public void release() {
////		mStop = true;
////		mProgressChanged = false;
////		mHandler.removeMessages(HANDLER_INVALIDATE_ACTIVE);
////	}
//
//    public void setData(MediaObject mMediaObject) {
//        this.mMediaObject = mMediaObject;
//    }
//
//    public void setMaxDuration(int duration) {
//        Log.d(TAG, "setMaxDuration: " + duration);
//        this.mMaxDuration = duration;
//    }
//
//    public void start() {
//        mProgressChanged = true;
////		mHandler.sendEmptyMessage(HANDLER_INVALIDATE_ACTIVE);
//    }
//
//    public void stop() {
//        mProgressChanged = false;
////		mHandler.removeMessages(HANDLER_INVALIDATE_ACTIVE);
//    }
//
//    public OverTimeClickListener mOverTimeClickListener;
//
//    public void setOverTimeClickListener(OverTimeClickListener overTimeClickListener) {
//        mOverTimeClickListener = overTimeClickListener;
//    }
//
//    public interface OverTimeClickListener {
//        void overTime();
//
//        void noEnoughTime();
//
//        void isArriveCountDown();
//    }
//}
