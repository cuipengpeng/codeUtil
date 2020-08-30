package com.test.xcamera.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

import com.test.xcamera.R;
import com.test.xcamera.accrssory.AccessoryManager;
import com.test.xcamera.bean.EventMessage;
import com.test.xcamera.bean.FrameRectInfo;
import com.test.xcamera.bean.VideoFrameInfo;
import com.test.xcamera.dymode.view.AutoFitTextureView;
import com.test.xcamera.dymode.view.DyFPVShotView;
import com.test.xcamera.enumbean.ShootMode;
import com.test.xcamera.glview.PreviewGLSurfaceView;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.managers.ShotModeManager;
import com.test.xcamera.mointerface.MoRequestCallback;
import com.test.xcamera.profession.ProfessionSettingView;
import com.test.xcamera.utils.BitmapUtils;
import com.test.xcamera.utils.DlgUtils;
import com.test.xcamera.utils.ViewUitls;
import com.test.xcamera.viewcontrol.MoFPVScaleControl;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * surfaceview  画框
 * <p>
 * 1、双击跟踪
 * 2、画框跟踪
 * <p>
 * 3、双指缩放数码变焦
 */
public class AutoTrackingRectViewFix extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private static final String TAG = "AutoTrackingRectView";
    private final Object AREA_FLAG = new Object();
    private final static int AREA_WIDTH = 200;  //双击跟踪框大小
    private final static int RECT_OFFSET = 30;  //跟踪框抖动的范围
    private final static boolean DEBUG = false;

    private final static int DISCERN = 0;
    public final static int TRACE = 1;
    public final static int TRACE_FIND = 2;

    //双指缩放 需要同步数码变焦
    private MoFPVModeView mFPVModeView;
    MoFPVShotControlView mShotControlLayout;
    //画框跟踪 需要获取图片 上传固件
    private PreviewGLSurfaceView mGLSurfaceView;
    private AutoFitTextureView mTextureView;
    private SurfaceHolder holder;
    private int clearFlag = 0;
    private Thread mRenderThread = null;
    private final Object LOCK = new Object();
    private Paint mPaint, mSelectedPaint, mAreaPaint;

    //view的宽高
    private int mHeight, mWidth;
    private float mRatio;
    //视频在view中的宽高
    private volatile int videoWidth, videoHeight;
    public volatile int videoWidthFix = 0, videoHeightFix = 0;

    //橙色跟踪框叉号宽高
    private int px16 = 0;
    private float pxOff25, pxOff75;
    //双击跟踪框大小
    private int mDoubleViewSize;

    //当前图像选择的角度
    public volatile int mRotate;
    public int mViewRotate;

    //数码变焦相关
    private float oldDist = 1f;
    private int zoom = 10;

    private volatile boolean mirror = false;
    //页面是否整体翻转
    private volatile int mOrientation;
    /**
     * 判断是否可以跟踪
     */
    private volatile boolean enable = true;
    /**
     * 跟踪不可用时 画框跟踪的标记
     */
    private volatile boolean enableScrollFlag = true;

    private volatile long mStartTime = 0;
    /**
     * 判断是否可以缩放
     */
    private volatile boolean enableZoom = true;
    //TODO 测试用 标记跟踪是否停止
    public boolean isStopFlag = false;
    //判断是否是跟踪状态
    public volatile boolean isTracingMode = false;
    //是否允许跟踪状态
    public volatile int enableTracingMode = 0;

    private long mLastToastTime;

    //存放画框的数据
    private LinkedList<VideoFrameInfo> mInfos = new LinkedList<>();
    //存放双击及画框的数据 left=-1表示无效的数据
    private Rect mAreaRect = new Rect();
    private Map<Integer, RectInfo> mViewRects = Collections.synchronizedMap(new HashMap<>());
    //用于处理跟踪框抖动
    private Map<Integer, Rect> mViewRectsTmp = Collections.synchronizedMap(new HashMap<>());

    private AtomicBoolean mIsStop = new AtomicBoolean(true);
    public volatile boolean mIsDraw;
    private Mode mMode = Mode.IDLE;

    //平滑双指缩放
    private float distTotal = 0f;
    private int distCount = 0;

    private GestureDetector mGestureDetector;
    private TrackInterface mTrackInterface;
    //画框跟踪 如果超过0.8秒没有完成 则清屏
    private static final int DELAY = 800;
    private long drawTime;
    private long sendTime;
    private Handler mDelayHandler = new Handler();
    private Runnable mRun = (() -> {
        mAreaRect.left = -1;
        mAreaRect.top = 0;
        mAreaRect.right = 0;
        mAreaRect.bottom = 0;
        unLock();
    });

    //500毫秒以内 防止双指缩放冲突
    private long mZoomFlag;
    private Runnable mSendArea = (() -> {
        sendAreaMessage(mAreaRect, Mode.AREA);
    });

    // 抖音
    DyFPVShotView mDyFPVShotView;
    private SurfaceView mSurfaceView;
    private boolean mIsDyMode = false;

    public AutoTrackingRectViewFix(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.setZOrderOnTop(true);
        this.getHolder().setFormat(PixelFormat.TRANSLUCENT);

        px16 = ViewUitls.dp2px(context, 16);
        pxOff25 = px16 * 0.25f;
        pxOff75 = px16 * 0.75f;
        mDoubleViewSize = AREA_WIDTH / 2;
        mAreaRect.left = -1;
        enable = true;
        enableZoom = true;

        this.holder = getHolder();
        this.holder.addCallback(this);
        holder.setFormat(PixelFormat.TRANSPARENT);//设置surface为透明

        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                EventBus.getDefault().post(new EventMessage(EventMessage.SCREEN_CLICKED));
                //单击需要有框的时候才能操作
                if (mIsDyMode && mDyFPVShotView != null) {
                    mDyFPVShotView.onTouch(e.getX(), e.getY());
                }
                if (mIsDraw) {
                    for (Map.Entry<Integer, RectInfo> item : mViewRects.entrySet()) {
                        if (item.getValue().status == TRACE || item.getValue().status == TRACE_FIND) {
                            //跟踪框的叉号 关闭跟踪。。。
                            RectF rectF = null;
                            if (mirror) {
                                if (mOrientation == 180) {
                                    rectF = createRectF(item.getValue().r.left, item.getValue().r.bottom - px16, item.getValue().r.left + px16, item.getValue().r.bottom, 1.5f);
                                } else {
                                    if (mViewRotate == -90) {
                                        rectF = createRectF(item.getValue().r.right - px16, item.getValue().r.top, item.getValue().r.right, item.getValue().r.top + px16 * 1f, 1.5f);
                                    } else {
                                        rectF = createRectF(item.getValue().r.left, item.getValue().r.top, item.getValue().r.left + px16, item.getValue().r.top + px16, 1.5f);
                                    }
                                }
                            } else {
                                if (mOrientation == 180) {
                                    rectF = createRectF(item.getValue().r.right - px16, item.getValue().r.bottom - px16, item.getValue().r.right, item.getValue().r.bottom, 1.5f);
                                } else {
                                    if (mViewRotate == -90) {
                                        rectF = createRectF(item.getValue().r.left, item.getValue().r.top, item.getValue().r.left + px16, item.getValue().r.top + px16, 1.5f);
                                    } else {
                                        rectF = createRectF(item.getValue().r.right - px16, item.getValue().r.top, item.getValue().r.right, item.getValue().r.top + px16, 1.5f);
                                    }
                                }
                            }
                            if (rectF.contains(getRealX(e.getX()), getRealY(e.getY()))) {
                                ConnectionManager.getInstance().stopTrack(new MoRequestCallback() {
                                    @Override
                                    public void onSuccess() {
                                        //在子线程中调用
                                        setNormal();
                                        if (mTrackInterface != null) {
                                            post(() -> {
                                                mTrackInterface.onStopTrackSingle();
                                            });
                                        }
                                    }

                                    @Override
                                    public void onFailed() {

                                    }
                                });
                                return true;
                            }
                        } else {
                            //单击白色跟踪框 选择跟踪
                            if (isDrawWhite()) {
//                            Log.e("=====", String.format("onSingleTapConfirmed==>%s  %d  %d %b %d"
//                                    , item.getValue().toString(), (int) e.getX(), (int) e.getY(), mirror, getWidth()));
                                if (item.getValue().r.contains((int) getRealX(e.getX()), (int) getRealY(e.getY()))) {
                                    if (!checkTraceEnable())
                                        return true;

                                    ConnectionManager.getInstance().startLocationObject(item.getKey(), new MoRequestCallback() {
                                        @Override
                                        public void onSuccess() {
//                                            setMode(Mode.TRACE);
                                            if (mTrackInterface != null)
                                                post(() -> {
                                                    mTrackInterface.onStartTrackSingle();
                                                });
                                        }

                                        @Override
                                        public void onFailed() {

                                        }
                                    });
                                    return true;
                                }
                            }
                        }
                    }
                }
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
//                if (mIsDyMode) return true;
                if (!enable) {
                    //长曝光模式 不会提示
                    if (ShotModeManager.getInstance().getmShootMode() != ShootMode.LONG_EXPLORE)
                        checkEnable();
                    return true;
                }

                if (!checkTraceEnable())
                    return true;

                /**
                 * 1、如果双击白色跟踪框 则选择跟踪单个对象状态
                 * 2、如果双击空白处 执行双击跟踪
                 * */
                boolean reactFlag = false;
                for (Map.Entry<Integer, RectInfo> item : mViewRects.entrySet()) {
                    if (item.getValue().r.contains((int) getRealX(e.getX()), (int) getRealY(e.getY()))) {
                        reactFlag = true;

                        ConnectionManager.getInstance().startLocationObject(item.getKey(), new MoRequestCallback() {
                            @Override
                            public void onSuccess() {
                                mIsDraw = true;
                                if (mTrackInterface != null)
                                    post(() -> {
                                        mTrackInterface.onStartTrackSingle();
                                    });
                            }

                            @Override
                            public void onFailed() {

                            }
                        });
                        return true;
                    }
                }

                //双击跟踪
                if (!reactFlag) {
                    //防止越界
                    float x = getRealX(e.getX());
                    float y = getRealY(e.getY());
                    if (x - mDoubleViewSize < 0)
                        x = mDoubleViewSize;
                    else if (x + mDoubleViewSize > videoWidthFix)
                        x = videoWidthFix - mDoubleViewSize;
                    if (y - mDoubleViewSize < 0)
                        y = mDoubleViewSize;
                    else if (y + mDoubleViewSize > videoHeightFix)
                        y = videoHeightFix - mDoubleViewSize;

                    synchronized (AREA_FLAG) {
                        mAreaRect.left = (int) (x - mDoubleViewSize);
                        mAreaRect.top = (int) (y - mDoubleViewSize);
                        mAreaRect.right = (int) (x + mDoubleViewSize);
                        mAreaRect.bottom = (int) (y + mDoubleViewSize);
                        unLock();

                        sendAreaMessage(mAreaRect, Mode.DOUBLE_AREA);
                    }
                }
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                //去除画框跟踪
//                if ((getMode() == Mode.IDLE || getMode() == Mode.AREAING)) {
//                    synchronized (AREA_FLAG) {
//                        mAreaRect.left = getX(e1.getX());
//                        mAreaRect.top = (int) (e1.getY());
//                        mAreaRect.right = getX(e2.getX());
//                        mAreaRect.bottom = (int) (e2.getY());
//
//                        if (!enable)
//                            mAreaRect.left = -1;
//
//                        setMode(Mode.AREAING);
//                    }
//                    synchronized (LOCK) {
//                        LOCK.notify();
//                    }
//                }
                return false;
            }
        });
    }

    private float getRealX(float x) {
        float mirrX = mirror ? (getWidth() - x) : x;
        if (mOrientation == 180) {
            return this.getWidth() - mirrX;
        }
        return mirrX;
    }

    private float getRealY(float y) {
        if (mOrientation == 180) {
            return this.getHeight() - y;
        }
        return y;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setEnableZoom(boolean enableZoom) {
        this.enableZoom = enableZoom;
    }

    private void checkEnable() {
        if (ShotModeManager.getInstance().getmShootMode() == ShootMode.SLOW_MOTION)
            DlgUtils.toast(getContext(), getResources().getString(R.string.trace_err_8x), mViewRotate);
        else if (ShotModeManager.getInstance().getmShootMode() == ShootMode.VIDEO)
            DlgUtils.toast(getContext(), getResources().getString(R.string.trace_err_4k60fps), mViewRotate);
        else
            DlgUtils.toast(getContext(), getResources().getString(R.string.trace_err_mode), mViewRotate);
    }

    private boolean checkTraceEnable() {
        if (enableTracingMode == ProfessionSettingView.FPV) {
            DlgUtils.toast(getContext(), getResources().getString(R.string.trace_unsupport_fpv), mViewRotate);
            return false;
        }
        return true;
    }

    public void setMirror(boolean mirror) {
        this.mirror = mirror;
    }

    private void sendAreaMessage(final Rect rect, Mode mode) {
        if (!enable)
            checkEnable();

        if ((mGLSurfaceView != null || mTextureView != null) && AccessoryManager.getInstance().mIsRunning) {
            if (getMode() == Mode.AREAING || getMode() == Mode.DOUBLE_AREA)
                mDelayHandler.postDelayed(mRun, DELAY);

            if (!mIsDyMode) {
                glSurfaceViewTrack(rect, mode);
            } else {
                textureViewTrack(rect, mode);
            }
        }
    }

    private void glSurfaceViewTrack(Rect rect, Mode mode) {
        mGLSurfaceView.setGetGLBitmap((bitmap) -> {
            if (bitmap == null) {
                resetMode();
                unLock();
                return;
            }
            trackBitmap(bitmap, rect, mode);
        });
    }

    private void textureViewTrack(Rect rect, Mode mode) {
        Bitmap bitmap = mTextureView.getBitmap();
        if (bitmap == null) {
            resetMode();
            unLock();
            return;
        }
        trackBitmap(bitmap, rect, mode);
    }

    private void dySurfaceViewTrack(Rect rect, Mode mode) {
//        Bitmap bitmap = mSurfaceView.getHolder().getSurface()
    }

    private void trackBitmap(Bitmap bitmap, Rect rect, Mode mode) {
        Rect trackRect = new Rect(rect);
        //获取的图片宽高不定
        int bmW = 0, bmH = 0;
        if (mIsDyMode) {
            bmW = 768;
            bmH = 432;
        } else if (ShotModeManager.getInstance().isVideo()) {
            bmW = 768;
            bmH = 432;
        } else {
            switch (MoFPVScaleControl.getInstance().mScale) {
                case MoFPVScaleControl.SCALE_1_1:
                    bmW = 640;
                    bmH = 640;
                    break;
                case MoFPVScaleControl.SCALE_4_3:
                    bmW = 640;
                    bmH = 480;
                    break;
                case MoFPVScaleControl.SCALE_16_9:
                    bmW = 768;
                    bmH = 432;
                    break;
                case MoFPVScaleControl.SCALE_195_9:
                    bmW = 896;
                    bmH = 412;
                    break;
            }
        }

        if (bmW == 0 || bmH == 0) {
            bmW = 768;
            bmH = 432;
        }

        if (DEBUG) {
            BitmapUtils.saveImg(bitmap, "pristine.jpg");
            Log.e("=====", "trackRect  b==>" + trackRect.toString());
        }
        //根据跟踪区域的大小 获取gles图片中实际的图片信息
        //再进行缩放
        if (!mIsDyMode) {
            int x = (bitmap.getWidth() - this.getWidth()) / 2;
            int y = (bitmap.getHeight() - this.getHeight()) / 2;
            bitmap = BitmapUtils.cropImg(bitmap, x, y, this.getWidth(), this.getHeight());
        }
        Rect rectRes = scale(trackRect, (float) bmW / bitmap.getWidth(), (float) bmH / bitmap.getHeight());
        if (DEBUG) {
            Log.e("=====", "rect  a==>" + rectRes.toString());
            BitmapUtils.saveImg(bitmap, "scale.jpg");
        }
        Bitmap newBitmap = BitmapUtils.scaleBitmap(bitmap, bmW, bmH);
        //传给算法的数据 都是未镜像的数据
        if (mirror) {
            Matrix matrix = new Matrix();
            matrix.postScale(-1, 1);
            newBitmap = Bitmap.createBitmap(newBitmap, 0, 0, newBitmap.getWidth(), newBitmap.getHeight(), matrix, true);
        }

        if (DEBUG) {
            BitmapUtils.saveImg(newBitmap, "save.jpg");
            BitmapUtils.saveImg(BitmapUtils.cropImg(newBitmap, rectRes.left, rectRes.top
                    , (rectRes.right - rectRes.left), (rectRes.bottom - rectRes.top)), "save_crop.jpg");
        }

        //根据相机角度选择图像
        if (mRotate != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(-mRotate);
            newBitmap = Bitmap.createBitmap(newBitmap, 0, 0, newBitmap.getWidth(), newBitmap.getHeight(), matrix, true);
        }

        if (DEBUG) {
            BitmapUtils.saveImg(newBitmap, "rotate.jpg");
        }

        byte[] bgr = BitmapUtils.bitmap2BGR(newBitmap);

        if (DEBUG) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(new File("/sdcard/test222/bgr.bgr"));
                fileOutputStream.write(bgr);
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //防止越界
        rectRes.right -= 1;
        rectRes.bottom -= 1;

        ConnectionManager.getInstance().trackRect(bmW, bmH, rectRes, bgr, new MoRequestCallback() {
            @Override
            public void onSuccess() {
                isStopFlag = false;

                mDelayHandler.removeCallbacks(mRun);

                mIsDraw = true;
                setMode(mode);
                mAreaRect.left = -1;
                unLock();
                if (mTrackInterface != null)
                    post(() -> {
                        mTrackInterface.onStartTrackSingle();
                    });
            }

            @Override
            public void onFailed() {
                mDelayHandler.removeCallbacks(mRun);

                resetMode();
                unLock();
                if (mTrackInterface != null)
                    post(() -> {
                        mTrackInterface.onFailed(Mode.AREA);
                    });
            }
        });
    }

    private Rect scale(Rect rect, float wScale, float hScale) {
        Rect res = new Rect(rect);
        res.left *= wScale;
        res.right *= wScale;
        res.top *= hScale;
        res.bottom *= hScale;

        return res;
    }

    private void resetMode() {
        setMode(Mode.IDLE);
        mAreaRect.left = -1;
        mAreaRect.top = 0;
        mAreaRect.right = 0;
        mAreaRect.bottom = 0;
    }

    //获取视频数据时 确保框的方向正确
    private void verifyRect(Rect rect) {
        if (rect.left > rect.right) {
            int tmp = rect.left;
            rect.left = rect.right;
            rect.right = tmp;
        }
        if (rect.top > rect.bottom) {
            int tmp = rect.top;
            rect.top = rect.bottom;
            rect.bottom = tmp;
        }
    }

    private synchronized void setMode(Mode mode) {
        this.mMode = mode;
    }

    public synchronized Mode getMode() {
        return this.mMode;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.getWidth() == 0 || this.getHeight() == 0)
            return true;

        //单指操作跟踪相关
        if (event.getPointerCount() == 1) {
            if (System.currentTimeMillis() - mZoomFlag < 500) {
                return false;
            }

            mGestureDetector.onTouchEvent(event);

            //双指操作 数码变焦相关
        } else if (event.getPointerCount() == 2) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_POINTER_DOWN:
                    if (!enableZoom && (System.currentTimeMillis() - mLastToastTime > 3000)) {
                        mLastToastTime = System.currentTimeMillis();
                        DlgUtils.toast(getContext(), getResources().getString(R.string.zoom_err_mode), mViewRotate);
                        return true;
                    }

                    oldDist = 0f;
                    distTotal = 0f;
                    distCount = 0;
                    if (mIsDyMode) {
                        if (mDyFPVShotView != null && mDyFPVShotView.mShotZoomText.getTag() != null) {
                            zoom = (int) mDyFPVShotView.mShotZoomText.getTag();
                        }
                    } else {
                        if (mFPVModeView != null && mFPVModeView.mCameraZoom.getTag() != null)
                            zoom = (int) mFPVModeView.mCameraZoom.getTag();
                        else if (mShotControlLayout != null && mShotControlLayout.mZoomText.getTag() != null)
                            zoom = (int) mShotControlLayout.mZoomText.getTag();
                    }

                    areaErr();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (!enableZoom) {
                        return true;
                    }

                    areaErr();

                    float newDist = getFingerSpacing(event);
                    if (oldDist < 0.01f) {
                        oldDist = newDist;
                        break;
                    }

                    float diff = newDist - oldDist;

                    distTotal += diff;
                    if (distCount != 0)
                        distTotal /= 2f;

//                    Log.e("=====", "distTotal==>" + distTotal + "   distCount==>" + distCount + "   diff==>" + diff);
                    if (distCount++ > 2) {
                        diff = distTotal;
                        distTotal = 0f;
                        distCount = 0;

                        diff /= 20f;
                        if (Math.abs(diff) < 1f)        //根据缩放的快慢 改变数据
                            diff = diff < 0 ? -1f : 1f;
                        else if (Math.abs(diff) > 5f)
                            diff = diff < 0 ? -5f : 5f;
                        long currentTime = System.currentTimeMillis();
                        if (currentTime - sendTime > 50) {      //防止变焦指令发送过多
                            handleZoom((int) diff);
                            sendTime = currentTime;
                        }
                    }
                    oldDist = newDist;
                    break;
            }
        }
        return true;
    }

    /**
     * 防止两个手指不同步 造成画框跟踪误操作
     */
    private void areaErr() {
        mZoomFlag = System.currentTimeMillis();

        if (getMode() == Mode.AREAING) {
            mDelayHandler.removeCallbacks(mSendArea);
            synchronized (AREA_FLAG) {
                resetMode();
            }
        }
    }

    private void handleZoom(int z) {
        int oldZoom = zoom;
        zoom += z;
        if (zoom < 10) {
            zoom = 10;
        } else if (zoom > 50) {
            zoom = 50;
        }

        if (oldZoom == zoom)
            return;

        float value = zoom / 10f;
        ConnectionManager.getInstance().digitalZoom(zoom, () -> {
            post(() -> {
                if (mIsDyMode) {
                    if (mDyFPVShotView != null) {
                        mDyFPVShotView.setZoom(value, zoom);
//                    mDyFPVShotView.mShotZoomText.setText(value + "X");
//                    mDyFPVShotView.mShotZoomText.setTag(zoom);
                    }
                } else {
                    if (mFPVModeView != null) {
                        mFPVModeView.mCameraZoom.setText(value + "X");
                        mFPVModeView.mCameraZoom.setTag(zoom);
                    }
                    if (mShotControlLayout != null) {
                        mShotControlLayout.mZoomText.setText(value + "X");
                        mShotControlLayout.mZoomText.setTag(zoom);
                        mShotControlLayout.mZoomLevel = zoom;
                    }
                }
            });
        });
    }

    private float getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    public void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5f);

        mSelectedPaint = new Paint();
        mSelectedPaint.setColor(getResources().getColor(R.color.color_55FF8A));
        mSelectedPaint.setAntiAlias(true);
        mSelectedPaint.setStyle(Paint.Style.STROKE);
        mSelectedPaint.setStrokeWidth(5f);

        mAreaPaint = new Paint();
        mAreaPaint.setColor(getResources().getColor(R.color.color_red));
        mAreaPaint.setAntiAlias(true);
        mAreaPaint.setStyle(Paint.Style.STROKE);
        mAreaPaint.setStrokeWidth(5f);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mStartTime = System.currentTimeMillis();
        initPaint();
        start();
        EventBus.getDefault().register(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.mHeight = ((RelativeLayout) getParent()).getHeight();
        this.mWidth = ((RelativeLayout) getParent()).getWidth();
        this.mRatio = this.mWidth / (float) this.mHeight;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stop();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 判断线程是否还在运行
     *
     * @return true 正在运行
     */
    public boolean isRunning() {
        return this.mRenderThread != null && this.mRenderThread.isAlive();
    }

    public void setNormal() {
        mIsDraw = true;
        mInfos.clear();
        setMode(Mode.NORMAL);
    }

    /**
     * 开始绘制
     */
    public void startDraw() {
        if (!this.enable) {
            checkEnable();
            return;
        }

        start();

        ConnectionManager.getInstance().getTracking(1, new MoRequestCallback() {
            @Override
            public void onSuccess() {
                isStopFlag = false;

                setMode(Mode.NORMAL);
                mIsDraw = true;

                if (mTrackInterface != null)
                    post(() -> {
                        mTrackInterface.onStartAutoTrack();
                    });
            }

            @Override
            public void onFailed() {
                resetMode();
                if (mTrackInterface != null)
                    post(() -> {
                        mTrackInterface.onFailed(Mode.NORMAL);
                    });
            }
        });
    }

    /**
     * 结束绘制
     */
    public void stopDraw() {
        //如果没有连接相机 则UI直接结束绘制
        if (!AccessoryManager.getInstance().mIsRunning) {
            stopTrack();
            return;
        }
        //如果有跟踪 则停跟踪 否则直接停止识别
        ConnectionManager.getInstance().getTracking(0, new MoRequestCallback() {
            @Override
            public void onSuccess() {
                isStopFlag = true;
                stopTrack();
            }
        });
    }

    public void stopTrack() {
        mInfos.clear();
        mViewRects.clear();
        mIsDraw = false;
        resetMode();
        unLock();

        if (mTrackInterface != null)
            post(() -> {
                if (mTrackInterface != null) {
                    mTrackInterface.onStopTrack();

                }
            });
    }

    public void start() {
        mInfos.clear();
        mViewRects.clear();
        mViewRectsTmp.clear();
        mIsDraw = false;
        resetMode();

        if (this.mIsStop.get()) {
            if (mRenderThread == null) {
                mRenderThread = new Thread(this);
                mRenderThread.setName("track thread");
            }
            mRenderThread.start();
        }
    }

    private void stop() {
        stopTrack();
        mIsStop.set(true);

        unLock();
        this.mRenderThread = null;
    }

    @Override
    public void run() {
        Canvas canvas = null;

        this.mIsStop.set(false);
        while (!this.mIsStop.get()) {
            try {
//                Log.e("=====", String.format("track==>%d  %d", mInfos.size(), mAreaRect.left));
                mViewRects.clear();
                if (mInfos.isEmpty() && mAreaRect.left == -1) {
                    //如果没有数据了 则先清除屏幕 再wait
                    if (++clearFlag > 3) {
                        synchronized (LOCK) {
                            LOCK.wait();
                        }
                        clearFlag = 0;
                    }
                } else {
                    clearFlag = 0;
                }

                VideoFrameInfo info = null;
                boolean isTrack = false;
                if (mInfos.size() > 0) {
                    info = mInfos.pollFirst();
                    isTrack = info != null && info.getmRectCount() > 0 && enableTracingMode != ProfessionSettingView.FPV;
                }

                drawTime = System.currentTimeMillis();
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

                    canvas.scale(this.mirror ? -1f : 1f, 1f, getWidth() / 2, getHeight() / 2);

//                Log.e("=====", String.format("track==>%d  %d   %d  %b", mInfos.size(), mAreaRect.left, info == null ? 0 : info.getmFrameRectInfos().size(), isTrack));
                    if (isTrack) {
                        //如果相机为跟踪单个对象的状态 则第一次插入 也要同步
                        boolean isTrace = false;
                        for (FrameRectInfo rect : info.getmFrameRectInfos()) {
                            if (rect.getStatus() == TRACE || rect.getStatus() == TRACE_FIND) {
                                isTrace = true;
//                                mIsDraw = true;
                                break;
                            }
                        }

                        isTracingMode = isTrace && mIsDraw;

                        for (int i = 0; i < info.getmFrameRectInfos().size(); i++) {
                            FrameRectInfo rects = info.getmFrameRectInfos().get(i);
                            int left = (int) (Float.parseFloat(rects.getmStartX()) * videoWidthFix);
                            int top = (int) (Float.parseFloat(rects.getmStartY()) * videoHeightFix);
                            int right = (int) (Float.parseFloat(rects.getmEndX()) * videoWidthFix);
                            int bottom = (int) (Float.parseFloat(rects.getmEndY()) * videoHeightFix);
                            Rect r = new Rect(left, top, right, bottom);

                            //处理跟踪框抖动
                            Rect rectTmp = mViewRectsTmp.get(rects.getmID());
                            if (rectTmp != null
                                    && Math.abs(r.left - rectTmp.left) < RECT_OFFSET && Math.abs(r.right - rectTmp.right) < RECT_OFFSET
                                    && Math.abs(r.top - rectTmp.top) < RECT_OFFSET && Math.abs(r.bottom - rectTmp.bottom) < RECT_OFFSET) {
                                r = rectTmp;
                            }

                            mViewRects.put(rects.getmID(), new RectInfo(r, rects.getStatus()));
                            mViewRectsTmp.put(rects.getmID(), r);

                            if (mIsDraw) {
                                if (!isTrace)
                                    drawWhiteRect(canvas, r);
                                else if (isTrace && (rects.getStatus() == TRACE || rects.getStatus() == TRACE_FIND))
                                    drawSelectedRect(canvas, r);
                            }
                        }

                        //清除过期的数据
                        Iterator<Map.Entry<Integer, Rect>> it = mViewRectsTmp.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry<Integer, Rect> entry = it.next();
                            if (!mViewRects.keySet().contains(entry.getKey()))
                                it.remove();
                        }
                    } else
                        isTracingMode = false;

                    //画区域框
//                    synchronized (AREA_FLAG) {
//                        if (mAreaRect.left != -1) {
//                            canvas.drawRect(mAreaRect, mAreaPaint);
//                        }
//                    }

                    //跟踪框绘制过快 没有数据后 会闪
                    if (mInfos.size() < 5 && mAreaRect.left == -1) {
                        //如果只有一个跟踪框 可能是由于
                        int delay = 99;
                        if (mInfos.size() > 0) {
                            delay = (int) (33 - (System.currentTimeMillis() - drawTime));
                            delay = Math.min(Math.max(0, delay), 33);
                        }
                        Thread.currentThread().sleep(delay);
                    }
                }
            } catch (Exception e) {
                Log.e("=====", "e==>" + e.getMessage());
                e.printStackTrace();
            } finally {
                if (holder != null && canvas != null && holder.getSurface() != null && holder.getSurface().isValid())
                    try {
                        holder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        Log.e("=====", "unlockCanvasAndPost err==>" + e.getMessage());
                    }

            }
        }
        Log.e("=====", "trace ==>end");
        clearFlag = 0;
    }

    /***
     * 设置View的宽高 与预览视频同步
     *
     * @param width 视频的宽度
     * @param height 视频的高度
     */
    public void changeSize(int width, int height) {
        if (videoHeight != height || videoWidth != width
                || videoHeightFix == 0 || videoWidthFix == 0) {
            videoWidth = width;
            videoHeight = height;

            //如果视频的长宽比比view的长宽比大 则视频宽度填满view宽度。以view的宽度为准
            float videoRatio = width / height;
            if (videoRatio > mRatio) {
                videoHeightFix = (int) ((float) mWidth / width * height);
                videoWidthFix = mWidth;
            } else {
                videoHeightFix = mHeight;
                videoWidthFix = (int) ((float) mHeight / height * width);
            }

            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) this.getLayoutParams();
            lp.width = videoWidthFix;
            lp.height = videoHeightFix;

            this.setLayoutParams(lp);
        }
    }

    /**
     * 判断是否需要画白色的跟踪框
     */
    private boolean isDrawWhite() {
        return getMode() == Mode.NORMAL || getMode() == Mode.AREA || getMode() == Mode.DOUBLE_AREA;
    }

    private void drawWhiteRect(Canvas canvas, Rect rect) {
        canvas.drawRect(rect, mPaint);
    }

    /**
     * 选中状态的跟踪框
     */
    private void drawSelectedRect(Canvas canvas, Rect rect) {
        mSelectedPaint.setColor(getResources().getColor(R.color.color_55FF8A));
        mSelectedPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(rect, mSelectedPaint);
        //画叉号外框
        mSelectedPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        if (mirror) {
            if (mOrientation == 180) {
                canvas.drawRect(rect.left, rect.bottom, rect.left + px16, rect.bottom - px16, mSelectedPaint);
            } else {
                if (mViewRotate == -90)
                    canvas.drawRect(rect.right, rect.top, rect.right - px16, rect.top + px16, mSelectedPaint);
                else
                    canvas.drawRect(rect.left, rect.top, rect.left + px16, rect.top + px16, mSelectedPaint);
            }
        } else {
            if (mOrientation == 180) {
                canvas.drawRect(rect.right - px16, rect.bottom, rect.right, rect.bottom - px16, mSelectedPaint);
            } else {
                if (mViewRotate == -90)
                    canvas.drawRect(rect.left + px16, rect.top, rect.left, rect.top + px16, mSelectedPaint);
                else
                    canvas.drawRect(rect.right - px16, rect.top, rect.right, rect.top + px16, mSelectedPaint);
            }
        }
        //画叉号
        mSelectedPaint.setColor(Color.WHITE);
        mSelectedPaint.setStyle(Paint.Style.STROKE);
        if (mirror) {
            if (mOrientation == 180) {
                canvas.drawLine(rect.left + pxOff75, rect.bottom - pxOff25
                        , rect.left + pxOff25, rect.bottom - pxOff75
                        , mSelectedPaint);
                canvas.drawLine(rect.left + pxOff25, rect.bottom - pxOff25
                        , rect.left + pxOff75, rect.bottom - pxOff75
                        , mSelectedPaint);
            } else {
                if (mViewRotate == -90) {
                    canvas.drawLine(rect.right - pxOff75, rect.top + pxOff25
                            , rect.right - pxOff25, rect.top + pxOff75
                            , mSelectedPaint);
                    canvas.drawLine(rect.right - pxOff25, rect.top + pxOff25
                            , rect.right - pxOff75, rect.top + pxOff75
                            , mSelectedPaint);
                } else {
                    canvas.drawLine(rect.left + pxOff75, rect.top + pxOff25
                            , rect.left + pxOff25, rect.top + pxOff75
                            , mSelectedPaint);
                    canvas.drawLine(rect.left + pxOff25, rect.top + pxOff25
                            , rect.left + pxOff75, rect.top + pxOff75
                            , mSelectedPaint);
                }
            }
        } else {
            if (mOrientation == 180) {
                canvas.drawLine(rect.right - pxOff75, rect.bottom - pxOff25
                        , rect.right - pxOff25, rect.bottom - pxOff75
                        , mSelectedPaint);
                canvas.drawLine(rect.right - pxOff25, rect.bottom - pxOff25
                        , rect.right - pxOff75, rect.bottom - pxOff75
                        , mSelectedPaint);
            } else {
                if (mViewRotate == -90) {
                    canvas.drawLine(rect.left + pxOff75, rect.top + pxOff25
                            , rect.left + pxOff25, rect.top + pxOff75
                            , mSelectedPaint);
                    canvas.drawLine(rect.left + pxOff25, rect.top + pxOff25
                            , rect.left + pxOff75, rect.top + pxOff75
                            , mSelectedPaint);
                } else {
                    canvas.drawLine(rect.right - pxOff75, rect.top + pxOff25
                            , rect.right - pxOff25, rect.top + pxOff75
                            , mSelectedPaint);
                    canvas.drawLine(rect.right - pxOff25, rect.top + pxOff25
                            , rect.right - pxOff75, rect.top + pxOff75
                            , mSelectedPaint);
                }
            }
        }
    }

    public void setData(VideoFrameInfo videoFrameInfo) {
        mInfos.add(videoFrameInfo);
        unLock();

        if (mInfos.size() > 30) {
            mInfos.clear();
        }
    }

    private void unLock() {
        synchronized (LOCK) {
            LOCK.notify();
        }
    }

    /**
     * 画面整体倒置时 修正点击区域及x号
     *
     * @param orien 180为倒置
     */
    public void setOrien(int orien) {
        this.mOrientation = orien;
    }

    private RectF createRectF(float l, float t, float r, float b, float scale) {
        float off = (r - l) * scale / 2;
        l -= off;
        if (l < 0)
            l = 0;
        r += off;
        if (r > getWidth())
            r = getWidth();
        t -= off;
        if (t < 0)
            t = 0;
        b += off;
        if (b > getHeight())
            b = getHeight();

        return new RectF(l, t, r, b);
    }

    public void setTrackInterface(TrackInterface trackInterface) {
        this.mTrackInterface = trackInterface;
    }

    public void setGLSurfaceView(PreviewGLSurfaceView glSurfaceView) {
        this.mGLSurfaceView = glSurfaceView;
    }

    public void setTextureView(AutoFitTextureView textureView) {
        this.mTextureView = textureView;
    }

    public void setFPVModeView(MoFPVModeView moFPVModeView) {
        this.mFPVModeView = moFPVModeView;
    }

    public void setShotControlView(MoFPVShotControlView mShotControlLayout) {
        this.mShotControlLayout = mShotControlLayout;
    }

    /**
     * 跟踪回调
     */
    public interface TrackInterface {
        /**
         * 开始自动跟踪
         */
        void onStartAutoTrack();

        /**
         * 开始跟踪单个对象
         */
        void onStartTrackSingle();

        /**
         * 停止跟踪单个对象
         */
        void onStopTrackSingle();

        /**
         * 关闭跟踪
         */
        void onStopTrack();

        /**
         * 开始画框跟踪
         */
        void onStartAreaTrack();

        void onFailed(Mode mode);
    }

    /**
     * 跟踪框的模式
     */
    public enum Mode {
        IDLE, NORMAL, AREA/*画框跟踪*/, AREAING, DOUBLE_AREA/*双击跟踪*/
    }

    class RectInfo {
        Rect r;
        int status;

        public RectInfo(Rect r, int status) {
            this.r = r;
            this.status = status;
        }

        @Override
        public String toString() {
            return "RectInfo{" +
                    "r=" + r +
                    ", status=" + status +
                    '}';
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(EventMessage msg) {
        if (msg.code == EventMessage.ENABLE_TRACING) {
            enableTracingMode = msg.extra;
        }
    }

    public void setDyMode(boolean isDouyin) {
        this.mIsDyMode = isDouyin;
    }

    public void setDyShotLayout(DyFPVShotView shotLayout) {
        this.mDyFPVShotView = shotLayout;
    }

    public void setDySurfaceView(SurfaceView surfaceView) {
        this.mSurfaceView = surfaceView;
    }
}
