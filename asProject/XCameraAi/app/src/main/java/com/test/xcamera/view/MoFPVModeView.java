package com.test.xcamera.view;

import android.animation.Animator;
import android.content.Context;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.dueeeke.videoplayer.util.L;
import com.test.xcamera.R;
import com.test.xcamera.activity.CameraMode;
import com.test.xcamera.adapter.MoSelectModeAdapter;
import com.test.xcamera.bean.EventMessage;
import com.test.xcamera.bean.MoSettingModel;
import com.test.xcamera.bean.MoSystemInfo;
import com.test.xcamera.enumbean.ScreenOrientationType;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.mointerface.MoRequestCallback;
import com.test.xcamera.mointerface.MoSyncSettingCallback;
import com.test.xcamera.mointerface.MoSystemInfoCallback;
import com.test.xcamera.profession.ProfessionView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by smz on 2019/12/17.
 */

public class MoFPVModeView extends LinearLayout implements View.OnClickListener {
    private Context mContext;
    private RecyclerViewEx mRecyclerView;
    private MoSelectModeAdapter mSelectModeAdapter;
    private MoFPVModeBase mFpvMode;
    private boolean mGalleryFirstFlag = false;
    private List<View> mRienView = new ArrayList<>();
    private CameraMode mCurrectMode;
    private ScreenOrientationType mOrientationType = ScreenOrientationType.PORTRAIT;
    private ProfessionView mProfessionView;
    private boolean mShowHeader = true;
    private Handler mHandler = new Handler();

    private ConstraintLayout mAspectContent;
    private LinearLayout mVisualNormal, mVisualWidth;
    private View mAspectRatioView, mAspectRatioViewPortrait, mAspectRatioViewLand;
    private ConstraintLayoutEx mModeHeadPortrait, mModeHeadLand;
    private View mAspectRatio;
    private View mProfession;
    private View mBack;

    private View mBatteryIcon;
    private TextView mBatteryValue;
    private View mSdcardIcon;
    private TextView mSdcardValue;
    private View mBatteryContent;
    private View mSdcardContent;

    public TextView mCameraZoom;
    private ImageView mTailAfter;
    private ImageView mCloudReversal;
    private ImageView mControlLeft;

    private View mRightContent;
    private LottieAnimationView mRightLottie;

    private FinishCB mFinishCB;

    class ModeViews {
        //拍照按钮
        FrameLayout recordContent;
        ImageView recordView;
        LottieAnimationView recordLottie;

        //用于放参数选项
        FrameLayout paramView;
        //回放图标
        ImageView rightImg;
        //模式图标 每种模式各不相同
        View customView;
        ImageView customView24;
        ImageView customView32;
        TextView customViewValue;

        //倒计时动画 只有拍照用到
        LottieAnimationView delayTimeLottie;
    }

    public ModeViews mModeViews = new ModeViews();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera_zoom:
                int zoom = (int) mCameraZoom.getTag() / 10;
                zoom++;
                zoom %= 6;
                if (zoom == 0)
                    zoom = 1;

                final int value = zoom;
                ConnectionManager.getInstance().digitalZoom(value * 10, () -> {
                    mCameraZoom.setText(value + "X");
                    mCameraZoom.setTag(value * 10);
                });
                break;
            case R.id.tail_after:   //跟踪

                break;
            case R.id.cloud_reversal:   //画面翻转
                ConnectionManager.getInstance().setSelfie(0, null);
                break;
            case R.id.back:
                if (this.mFinishCB != null)
                    this.mFinishCB.onFinish();
                break;
            case R.id.profession:   //专业模式
                mProfessionView.setVisibility(View.VISIBLE);
                mProfessionView.reset();
                break;
            case R.id.control_left: //云台回中按钮

                break;
            case R.id.aspect_ratio:
                mAspectRatioView.setVisibility(View.VISIBLE);
                break;
            case R.id.custom_view:
                mFpvMode.showParamView();
                break;
            case R.id.visual_normal:
            case R.id.visual_width:
                distortion(v.getId());
                break;
            case R.id.scale_16_9:
            case R.id.scale_19_9:
            case R.id.scale_1_1:
            case R.id.scale_4_3:
                for (int i = 0; i < mAspectContent.getChildCount(); i++)
                    mAspectContent.getChildAt(i).setSelected(mAspectContent.getChildAt(i).getId() == v.getId());
                break;
        }
    }

    /**
     * 设置视角和长宽比
     */
    private void distortion(int id) {
        for (int i = 0; i < mVisualNormal.getChildCount(); i++)
            mVisualNormal.getChildAt(i).setSelected(R.id.visual_normal == id);
        for (int i = 0; i < mVisualWidth.getChildCount(); i++)
            mVisualWidth.getChildAt(i).setSelected(R.id.visual_width == id);
    }

    public MoFPVModeView(Context context) {
        super(context);
        init(context);
    }

    public MoFPVModeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;

        View view = LayoutInflater.from(context).inflate(R.layout.layout_fpv_mode, this, false);
        this.addView(view);

        initModeView();
        initModeSelect();
        initAspect(mAspectRatioView);

        mCameraZoom.setTag(10);
        mCameraZoom.setText("1X");

        //初始为VIDEO
        switchMode(CameraMode.VIDEO);
    }

    /**
     * 同步相机参数
     *
     * @param isSwitchMode 同步数据时是否同步模式  true 切换模式
     */
    public void syncMode(boolean isSwitchMode) {
        ConnectionManager.getInstance().syncSetting(new MoSyncSettingCallback() {
            @Override
            public void onSuccess(MoSettingModel model) {
                syncViews(model, isSwitchMode);
                startPreview();
            }

            @Override
            public void onFailed() {
                Log.e("=====", "syncMode syncSetting==>onFailed");
                startPreview();
            }
        });
    }

    private void startPreview() {
        ConnectionManager.getInstance().startPreview(new MoRequestCallback() {
            @Override
            public void onSuccess() {
                Log.e("=====", "startPreview==>onSuccess");
                ConnectionManager.getInstance().getSystemInfo(new MoSystemInfoCallback() {
                    @Override
                    public void onSuccess(MoSystemInfo systemInfo) {
                        Log.e("=====", "getSystemInfo==>" + systemInfo.toString());
                        mSdcardValue.setText(CameraMode.isVideo(mCurrectMode) ? systemInfo.getmVideoCount() + "" : systemInfo.getmImageCount() + "");
                        mBatteryValue.setText(systemInfo.getmBattery() + "");
                    }

                    @Override
                    public void onFailed() {
                        Log.e("=====", "getSystemInfo==>onFailed");
                    }
                });
            }

            @Override
            public void onFailed() {
                Toast.makeText(getContext(), "开启预览流失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void syncViews(MoSettingModel model, boolean isSwitchMode) {
        if (isSwitchMode)
            switchMode(CameraMode.getMode(model.mode));
        mFpvMode.syncParams(model);

        int dis = 0;
        int zoom = 1;
        if (model.isVideo) {
            dis = model.videoModel.dis;
            zoom = Math.round(model.videoModel.zoom / 10f);
        } else {
            dis = model.snapshotModel.dis;
            zoom = Math.round(model.snapshotModel.zoom / 10f);
        }
        distortion(dis == 0 ? R.id.visual_normal : R.id.visual_width);

        mCameraZoom.setTag(zoom);
        mCameraZoom.setText(zoom + "X");
    }

    public void switchMode(CameraMode mode) {
        //长曝光模式下 需要隐藏
        this.mTailAfter.setVisibility(View.VISIBLE);

        switch (mode) {
            case LONG_EXPLORE:
                mFpvMode = new MoFPVModePhotoLE();
                this.mTailAfter.setVisibility(View.INVISIBLE);
                break;
            case PHOTO:
                mFpvMode = new MoFPVModePhoto();
                break;
            case VIDEO:

                break;
            case SLOW_MOTION:

                break;
            case LAPSE:

                break;
            case BEAUTY_PHOTO:

                break;
            case BEAUTY_VIDEO:

                break;
            default:

                break;
        }

        mFpvMode = new MoFPVModePhoto();
        mFpvMode.initMode(getContext(), this);
        mProfessionView.setMode(mode);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(EventMessage msg) {
        if (msg.code == EventMessage.SCREEN_CLICKED && mFpvMode != null) {
            mAspectRatioView.setVisibility(View.GONE);
            mFpvMode.hideParamView();
        }
        if (msg.code == EventMessage.PROFESSION_HIDE) {
            mProfessionView.setVisibility(View.GONE);
        }
    }

    /**
     * 同步横竖屏
     */
    public void orientation(ScreenOrientationType type) {
        mOrientationType = type;
        if (type == ScreenOrientationType.PORTRAIT) {
            mModeHeadPortrait.setVisibility(mShowHeader ? View.VISIBLE : View.GONE);
            mModeHeadLand.setVisibility(View.GONE);

            initHeaderView(mModeHeadPortrait);

            if (mAspectRatioView.getVisibility() == View.VISIBLE) {
                mAspectRatioViewPortrait.setVisibility(View.VISIBLE);
                mAspectRatioViewLand.setVisibility(View.GONE);
            }
            mAspectRatioView = mAspectRatioViewPortrait;

            for (View v : mRienView)
                v.animate().rotation(0);
        } else if (type == ScreenOrientationType.LANDSCAPE) {
            mModeHeadPortrait.setVisibility(View.GONE);
            mModeHeadLand.setVisibility(mShowHeader ? View.VISIBLE : View.GONE);

            initHeaderView(mModeHeadLand);

            if (mAspectRatioView.getVisibility() == View.VISIBLE) {
                mAspectRatioViewPortrait.setVisibility(View.GONE);
                mAspectRatioViewLand.setVisibility(View.VISIBLE);
            }
            mAspectRatioView = mAspectRatioViewLand;

            for (View v : mRienView)
                v.animate().rotation(90);
        }

        if (mFpvMode != null) {
            mFpvMode.syncHeaderView(mModeViews);
            mFpvMode.orientation(type);
        }

        initAspect(mAspectRatioView);
        mProfessionView.orientation(type);
    }

    private void initModeView() {
        mProfessionView = findViewById(R.id.profession_view);
        mAspectRatioView = mAspectRatioViewPortrait = findViewById(R.id.fpv_aspect_ratio_view_portrait);
        mAspectRatioViewLand = findViewById(R.id.fpv_aspect_ratio_view_land);
        mModeHeadPortrait = findViewById(R.id.mode_head_portrait);
        mModeHeadLand = findViewById(R.id.mode_head_land);
        mModeViews.paramView = findViewById(R.id.param_view);

        mBatteryIcon = findViewById(R.id.battery_icon);
        mBatteryValue = findViewById(R.id.battery_value);
        mSdcardIcon = findViewById(R.id.sdcard_icon);
        mSdcardValue = findViewById(R.id.sdcard_value);
        mBatteryContent = findViewById(R.id.battery_content);
        mSdcardContent = findViewById(R.id.sdcard_content);
        mRienView.add(mBatteryContent);
        mRienView.add(mSdcardContent);

        mModeViews.delayTimeLottie = findViewById(R.id.delaytime_lottie);
        mRienView.add(mModeViews.delayTimeLottie);

        mCameraZoom = findViewById(R.id.camera_zoom);
        mTailAfter = findViewById(R.id.tail_after);
        mCloudReversal = findViewById(R.id.cloud_reversal);
        mControlLeft = findViewById(R.id.control_left);
        mRienView.add(mCameraZoom);
        mRienView.add(mTailAfter);
        mRienView.add(mCloudReversal);
        mRienView.add(mControlLeft);
        mCloudReversal.setOnClickListener(this);

        mModeViews.recordContent = findViewById(R.id.record_content);
        mModeViews.recordView = findViewById(R.id.record_view);
        mModeViews.recordLottie = findViewById(R.id.shot_lottie);

        mRightContent = findViewById(R.id.control_right);
        mModeViews.rightImg = findViewById(R.id.control_right_img);
        mRightLottie = findViewById(R.id.control_right_lottie);

        mRienView.add(mModeViews.recordContent);
        mRienView.add(mRightContent);
        //TODO
        initHeaderView(mModeHeadPortrait);

        mControlLeft.setOnClickListener(this);
        mCameraZoom.setOnClickListener(this);
        mTailAfter.setOnClickListener(this);

    }

    private void initHeaderView(View content) {
        mBack = content.findViewById(R.id.back);
        mProfession = content.findViewById(R.id.profession);
        mAspectRatio = content.findViewById(R.id.aspect_ratio);

        mModeViews.customView = content.findViewById(R.id.custom_view);
        mModeViews.customView24 = content.findViewById(R.id.custom_view_24);
        mModeViews.customView32 = content.findViewById(R.id.custom_view_32);
        mModeViews.customViewValue = content.findViewById(R.id.custom_view_value);

        mBack.setOnClickListener(this);
        mProfession.setOnClickListener(this);
        mAspectRatio.setOnClickListener(this);
        mModeViews.customView.setOnClickListener(this);
    }

    /**
     * 初始化模式
     */
    private void initModeSelect() {
        mRecyclerView = findViewById(R.id.mode_select);
        mGalleryFirstFlag = false;
        final GalleryLayoutManager layoutManager = new GalleryLayoutManager(GalleryLayoutManager.HORIZONTAL, false);
        layoutManager.attach(mRecyclerView);
        layoutManager.setCallbackInFling(false);
        layoutManager.setOnItemSelectedListener((recyclerView, item, position) -> {
            Log.e("=====", "setOnItemSelectedListener==>" + position);

            CameraMode mode = CameraMode.mCameraMode.get(position);
            L.e(mCurrectMode + "==>" + mode);
            if (mode != mCurrectMode) {
                ConnectionManager.getInstance().stopPreview(new MoRequestCallback() {
                    @Override
                    public void onSuccess() {
                        L.e("stopPreview==>onSuccess");
                        ConnectionManager.getInstance().switchMode(mode.cmd, new MoRequestCallback() {
                            @Override
                            public void onSuccess() {
                                mCurrectMode = mode;
                                mHandler.post(() -> {
                                    switchMode(mode);
                                });

                                syncMode(false);
                            }

                            @Override
                            public void onFailed() {
                                Toast.makeText(mContext, "切换模式失败", Toast.LENGTH_SHORT).show();
                                startPreview();
                            }
                        });
                    }

                    @Override
                    public void onFailed() {
                        Toast.makeText(mContext, "关闭预览流失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        mSelectModeAdapter = new MoSelectModeAdapter();
        mSelectModeAdapter.setOnItemClickListener((view, item) -> {
            Log.e("=====", "index==>" + CameraMode.mCameraMode.indexOf(item));
            mRecyclerView.smoothScrollToPosition(CameraMode.mCameraMode.indexOf(item));
        });
        mRecyclerView.setAdapter(mSelectModeAdapter);
    }

    /**
     * 初始化视角
     */
    private void initAspect(View view) {
        mAspectContent = view.findViewById(R.id.aspect_content);
        for (int i = 0; i < mAspectContent.getChildCount(); i++)
            mAspectContent.getChildAt(i).setOnClickListener(this);

        mVisualNormal = view.findViewById(R.id.visual_normal);
        mVisualWidth = view.findViewById(R.id.visual_width);
        mVisualNormal.setOnClickListener(this);
        mVisualWidth.setOnClickListener(this);
    }

    /**
     * 设置拍照时 需要禁止的操作
     *
     * @param status false 禁止操作
     */
    public void setPhotoViews(boolean status) {
        mFpvMode.hideParamView();
        mAspectRatioView.setVisibility(View.GONE);

        mModeHeadLand.setIntercept(!status);
        mModeHeadPortrait.setIntercept(!status);

        mControlLeft.setClickable(status);
        mTailAfter.setClickable(status);
        mModeViews.recordContent.setClickable(status);
        mCloudReversal.setClickable(status);
        mRightContent.setClickable(status);
        mRecyclerView.setIntercept(!status);
    }

    /**
     * 设置长曝光时 需要禁止的操作
     *
     * @param status false 禁止操作
     */
    public void setLongexpotimeViews(boolean status) {
        setHideViews(status);
    }

    /**
     * 拍视频时 需要禁止的操作
     *
     * @param status false 禁止操作
     */
    public void setVideoViews(boolean status, String url) {
        setHideViews(status);
        if (status) {
            mModeViews.rightImg.setImageResource(R.mipmap.icon_fpv_playback);
            mRightContent.setOnClickListener((view) -> {

            });
        } else {
            mModeViews.rightImg.setImageResource(R.mipmap.icon_record_mark);
            mRightContent.setOnClickListener((view) -> {
                markAnimation();

                ConnectionManager.getInstance().markVideo(url, System.currentTimeMillis(), new MoRequestCallback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailed() {

                    }
                });
            });
        }
    }

    private void setHideViews(boolean status) {
        mFpvMode.hideParamView();
        mAspectRatioView.setVisibility(View.GONE);

        mShowHeader = status;

        if (!status) {
            mModeHeadLand.setVisibility(View.GONE);
            mModeHeadPortrait.setVisibility(View.GONE);
        } else {
            mModeHeadLand.setVisibility(mOrientationType == ScreenOrientationType.LANDSCAPE ? View.VISIBLE : View.GONE);
            mModeHeadPortrait.setVisibility(mOrientationType == ScreenOrientationType.PORTRAIT ? View.VISIBLE : View.GONE);
        }

        mRecyclerView.setVisibility(status ? View.VISIBLE : View.INVISIBLE);
        mBatteryContent.setVisibility(status ? View.VISIBLE : View.INVISIBLE);
        mSdcardContent.setVisibility(status ? View.VISIBLE : View.INVISIBLE);
    }

    private void markAnimation() {
        mRightLottie.setVisibility(View.VISIBLE);
        mModeViews.rightImg.setVisibility(View.GONE);

        mRightLottie.playAnimation();
        mRightLottie.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mRightLottie.setVisibility(View.GONE);
                mModeViews.rightImg.setVisibility(View.VISIBLE);

                mRightLottie.removeAllAnimatorListeners();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    public void setFinishCB(FinishCB finish) {
        this.mFinishCB = finish;
    }

    public interface FinishCB {
        void onFinish();
    }
}
