package com.test.xcamera.dymode.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.test.xcamera.R;
import com.test.xcamera.activity.DyFPVActivity;
import com.test.xcamera.dymode.utils.FileUtils;
import com.test.xcamera.view.VerticalTextView;
import com.ss.android.vesdk.VEBeautyParam;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 抖音拍摄--美化
 * Created by zll on 2020/2/21.
 */

public class DyFPVBeautyView extends RelativeLayout {
    private static final String TAG = "DyFPVBeautyView";
    private static final int SMOOTH_DEFAULT_VALUE = 60;
    private static final int FACE_DEFAULT_VALUE = 30;
    private static final int EYES_DEFAULT_VALUE = 30;
    private static final int LIPSTICK_DEFAULT_VALUE = 30;
    private static final int BLUSHER_DEFAULT_VALUE = 20;

    @BindView(R.id.dy_fpv_beauty_reset_layout)
    LinearLayout mResetLayout;
    @BindView(R.id.dy_fpv_beauty_skin_img)
    RelativeLayout mSkinImg;
    @BindView(R.id.dy_fpv_beauty_skin_text)
    TextView mSkinText;
    @BindView(R.id.dy_fpv_beauty_face_img)
    RelativeLayout mFaceImg;
    @BindView(R.id.dy_fpv_beauty_face_text)
    TextView mFaceText;
    @BindView(R.id.dy_fpv_beauty_eye_img)
    RelativeLayout mEyeImg;
    @BindView(R.id.dy_fpv_beauty_eye_text)
    TextView mEyeText;
    @BindView(R.id.dy_fpv_beauty_rouge_img)
    RelativeLayout mRougeImg;
    @BindView(R.id.dy_fpv_beauty_rouge_text)
    TextView mRougeText;
    @BindView(R.id.dy_fpv_beauty_blush_img)
    RelativeLayout mBlushImg;
    @BindView(R.id.dy_fpv_beauty_blush_text)
    TextView mBlushText;
    @BindView(R.id.dy_fpv_beauty_seekbar)
    VerticalSeekBar mSeekBar;
    @BindView(R.id.dy_fpv_beauty_seekbar_container)
    VerticalSeekBarWrapper mSeekBarWrapper;
    //    DyFPVSeekBar mSeekBar;
    @BindView(R.id.fy_fpv_beauty_seekbar_layout)
    RelativeLayout mBeautySeekBarLayout;
    @BindView(R.id.dy_fpv_beauty_value)
    VerticalTextView mBeautyValue;
    @BindView(R.id.dy_fpv_beauty_parent_layout)
    RelativeLayout mParentLayout;
    @BindView(R.id.fy_fpv_beauty_empty_view)
    View mEmptyView;
    @BindView(R.id.dy_fpv_beauty_clear)
    ImageView mClearBtn;

    private WeakReference<Context> mContext;
    private WeakReference<DyFPVShotView> mDyFPVShotView;

    private ArrayList<RelativeLayout> mViews;
    private ArrayList<TextView> mTextViews;
    private DyFPVActivity mActivity;

    /**
     * 当前美颜选择，默认磨皮
     * VEBeautyParam.BEAUTY_SMOOTH = 2;  磨皮
     * VEBeautyParam.BEAUTY_RESHAPE_CHEEK = 5; 瘦脸
     * VEBeautyParam.BEAUTY_RESHAPE_EYE = 4; 大眼
     * VEBeautyParam.BEAUTY_LIPSTICK = 17; 口红
     * VEBeautyParam.BEAUTY_BLUSHER = 18; 腮红
     */
    private int mCurrentType = VEBeautyParam.BEAUTY_SMOOTH;
    private int[] mTypes =
            new int[]{VEBeautyParam.BEAUTY_SMOOTH, VEBeautyParam.BEAUTY_RESHAPE_CHEEK,
                    VEBeautyParam.BEAUTY_RESHAPE_EYE, VEBeautyParam.BEAUTY_LIPSTICK,
                    VEBeautyParam.BEAUTY_BLUSHER};

    private int mSmoothLastValue = -1;
    private int mReshapeCheekLatValue = -1;
    private int mReshapeEyeLastValue = -1;
    private int mLipstickLastValue = -1;
    private int mBlusherLastValue = -1;

    private float mSmoothLastValueY = -1;
    private float mReshapeCheekLatValueY = -1;
    private float mReshapeEyeLastValueY = -1;
    private float mLipstickLastValueY = -1;
    private float mBlusherLastValueY = -1;

    private DisplayMetrics displaysMetrics;
    private int mTmpProgress;
    private boolean isClearBeauty = true;

    public DyFPVBeautyView(Context context) {
        super(context);

//        init(context);
    }

    public DyFPVBeautyView(Context context, AttributeSet attrs) {
        super(context, attrs);

//        init(context);
    }

    public void init(Context context, DyFPVShotView shotView) {
        mContext = new WeakReference<>(context);
        mDyFPVShotView = new WeakReference<>(shotView);
        LayoutInflater.from(context).inflate(R.layout.dy_fpv_beauty, this, true);

        ButterKnife.bind(this);

        mViews = new ArrayList<>();
        mViews.add(mSkinImg);
        mViews.add(mFaceImg);
        mViews.add(mEyeImg);
        mViews.add(mRougeImg);
        mViews.add(mBlushImg);

        mTextViews = new ArrayList<>();
        mTextViews.add(mSkinText);
        mTextViews.add(mFaceText);
        mTextViews.add(mEyeText);
        mTextViews.add(mRougeText);
        mTextViews.add(mBlushText);

        mSmoothLastValue = SMOOTH_DEFAULT_VALUE;
        mReshapeCheekLatValue = FACE_DEFAULT_VALUE;
        mReshapeEyeLastValue = EYES_DEFAULT_VALUE;
        mLipstickLastValue = LIPSTICK_DEFAULT_VALUE;
        mBlusherLastValue = BLUSHER_DEFAULT_VALUE;

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTmpProgress = progress;
                if (progress < 0 || progress > 100) return;
                mBeautyValue.setText(progress + "");
                float value = 1.0f * progress / 100;
                Log.d(TAG, "onProgressChanged: " + progress + ", value = " + value);
//                mActivity.setIntensityByType(mCurrentType, value);
                if (mCurrentType == VEBeautyParam.BEAUTY_SMOOTH) {
                    mActivity.setBeauty(true, value);
                } else if (mCurrentType == VEBeautyParam.BEAUTY_RESHAPE_EYE) {
                    float face = (float) (1.0 * mReshapeCheekLatValue / 100);
                    mActivity.setFaceReshape(FileUtils.getDyResourceFaceShapDir(mContext.get()), value, face);
                } else if (mCurrentType == VEBeautyParam.BEAUTY_RESHAPE_CHEEK) {
                    float eye = (float) (1.0 * mReshapeEyeLastValue / 100);
                    mActivity.setFaceReshape(FileUtils.getDyResourceFaceShapDir(mContext.get()), eye, value);
                } else {
                    mActivity.setIntensityByType(mCurrentType, value);
                }
                setLastValue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mSeekBar.setSeekBarLocationCallback(new VerticalSeekBar.SeekBarLocationCallback() {
            @Override
            public void location(float value) {
                Log.d(TAG, "location: " + value);
                if (mTmpProgress <= 0 || mTmpProgress >= 100) return;
                if (mCurrentType == VEBeautyParam.BEAUTY_SMOOTH) {
                    mSmoothLastValueY = value;
                } else if (mCurrentType == VEBeautyParam.BEAUTY_RESHAPE_CHEEK) {
                    mReshapeCheekLatValueY = value;
                } else if (mCurrentType == VEBeautyParam.BEAUTY_RESHAPE_EYE) {
                    mReshapeEyeLastValueY = value;
                } else if (mCurrentType == VEBeautyParam.BEAUTY_LIPSTICK) {
                    mLipstickLastValueY = value;
                } else if (mCurrentType == VEBeautyParam.BEAUTY_BLUSHER) {
                    mBlusherLastValueY = value;
                }
                mBeautyValue.setTranslationY(value);
            }
        });


        mSeekBarWrapper.setSeekBarSizeChangedListener(new VerticalSeekBarWrapper.SeekBarSizeChangedListener() {
            @Override
            public void onSizeChanged(int width, int height) {
                float value = mTmpProgress * height / 100 - 90;
                Log.d(TAG, "onSizeChanged: width: " + width + ", height: " + height + ", value: " + value);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBeautyValue.setTranslationY(value);
                    }
                });
            }
        });

        setEnable(false);
    }

    @OnClick({R.id.dy_fpv_beauty_reset_layout, R.id.dy_fpv_beauty_skin_img,
            R.id.dy_fpv_beauty_face_img, R.id.dy_fpv_beauty_eye_img, R.id.dy_fpv_beauty_clear,
            R.id.dy_fpv_beauty_rouge_img, R.id.dy_fpv_beauty_blush_img, R.id.fy_fpv_beauty_empty_view})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.dy_fpv_beauty_reset_layout) {
            resetValue();
        } else if (view.getId() == R.id.fy_fpv_beauty_empty_view) {
            hide();
        } else if (view.getId() == R.id.dy_fpv_beauty_clear) {
            if (isClearBeauty) {
                isClearBeauty = false;
                mClearBtn.setBackgroundResource(R.mipmap.icon_dy_props_clear);
                setDefaultBeauty();
                setEnable(true);
            } else {
                isClearBeauty = true;
                mClearBtn.setBackgroundResource(R.mipmap.icon_dy_props_clear_light);
                clearBeauty();
                setEnable(false);
                mBeautySeekBarLayout.setVisibility(GONE);
            }
        } else {
            setSelectState(view.getId());
        }
    }

    private void setSelectState(int viewID) {
        if (isClearBeauty) {
            isClearBeauty = false;
            mClearBtn.setBackgroundResource(R.mipmap.icon_dy_props_clear);
            setDefaultBeauty();
            setEnable(true);
        }
        for (int i = 0; i < mViews.size(); i++) {
            View view = mViews.get(i);
            TextView textView = mTextViews.get(i);
            if (view.getId() == viewID) {
                view.setSelected(true);
                textView.setTextColor(0XFFDD6700);
                mCurrentType = mTypes[i];
            } else {
                view.setSelected(false);
                textView.setTextColor(0XFFFFFFFF);
            }
        }
        Log.d(TAG, "setSelectState: current type = " + mCurrentType);
        setLastProgress();
        mBeautySeekBarLayout.setVisibility(VISIBLE);
    }

    private void setLastProgress() {
        int progress = 0;
        float value = -1;
        switch (mCurrentType) {
            case VEBeautyParam.BEAUTY_SMOOTH:
                if (mSmoothLastValue == -1) {
                    progress = SMOOTH_DEFAULT_VALUE;
                } else {
                    progress = mSmoothLastValue;
                }
                value = mSmoothLastValueY;
                break;
            case VEBeautyParam.BEAUTY_RESHAPE_CHEEK:
                if (mReshapeCheekLatValue == -1) {
                    progress = FACE_DEFAULT_VALUE;
                } else {
                    progress = mReshapeCheekLatValue;
                }
                value = mReshapeCheekLatValueY;
                break;
            case VEBeautyParam.BEAUTY_RESHAPE_EYE:
                if (mReshapeEyeLastValue == -1) {
                    progress = EYES_DEFAULT_VALUE;
                } else {
                    progress = mReshapeEyeLastValue;
                }
                value = mReshapeEyeLastValueY;
                break;
            case VEBeautyParam.BEAUTY_LIPSTICK:
                if (mLipstickLastValue == -1) {
                    progress = LIPSTICK_DEFAULT_VALUE;
                } else {
                    progress = mLipstickLastValue;
                }
                value = mLipstickLastValueY;
                break;
            case VEBeautyParam.BEAUTY_BLUSHER:
                if (mBlusherLastValue == -1) {
                    progress = BLUSHER_DEFAULT_VALUE;
                } else {
                    progress = mBlusherLastValue;
                }
                value = mBlusherLastValueY;
                break;
        }
        mTmpProgress = progress;
        mSeekBar.setProgress(progress);
        if (value < 0)
            value = progress * mSeekBarWrapper.getmHeight() / 100 - 50;
        Log.d(TAG, "setLastProgress: " + value);
//        if (mTmpProgress >= 95) {
//            mBeautyValue.setTranslationY(value - 90);
//        } else if (mTmpProgress <= 10) {
//            mBeautyValue.setTranslationY(value + 50);
//        } else {
        mBeautyValue.setTranslationY(value);
//        }
    }

    private void setLastValue(int value) {
        switch (mCurrentType) {
            case VEBeautyParam.BEAUTY_SMOOTH:
                mSmoothLastValue = value;
                break;
            case VEBeautyParam.BEAUTY_RESHAPE_CHEEK:
                mReshapeCheekLatValue = value;
                break;
            case VEBeautyParam.BEAUTY_RESHAPE_EYE:
                mReshapeEyeLastValue = value;
                break;
            case VEBeautyParam.BEAUTY_LIPSTICK:
                mLipstickLastValue = value;
                break;
            case VEBeautyParam.BEAUTY_BLUSHER:
                mBlusherLastValue = value;
                break;
        }
    }

    private void resetValue() {
        int progress = 0;
        switch (mCurrentType) {
            case VEBeautyParam.BEAUTY_SMOOTH:
                mSmoothLastValue = SMOOTH_DEFAULT_VALUE;
                progress = SMOOTH_DEFAULT_VALUE;
                break;
            case VEBeautyParam.BEAUTY_RESHAPE_CHEEK:
                mReshapeCheekLatValue = FACE_DEFAULT_VALUE;
                progress = FACE_DEFAULT_VALUE;
                break;
            case VEBeautyParam.BEAUTY_RESHAPE_EYE:
                mReshapeEyeLastValue = EYES_DEFAULT_VALUE;
                progress = EYES_DEFAULT_VALUE;
                break;
            case VEBeautyParam.BEAUTY_LIPSTICK:
                mLipstickLastValue = LIPSTICK_DEFAULT_VALUE;
                progress = LIPSTICK_DEFAULT_VALUE;
                break;
            case VEBeautyParam.BEAUTY_BLUSHER:
                mBlusherLastValue = BLUSHER_DEFAULT_VALUE;
                progress = BLUSHER_DEFAULT_VALUE;
                break;
        }
        mSeekBar.setProgress(progress);
        float value = progress * mSeekBarWrapper.getmHeight() / 100 - 90;
        mBeautyValue.setTranslationY(value);
    }

    public void attachActivity(DyFPVActivity activity) {
        mActivity = activity;
    }

    private void hide() {
        if (mDyFPVShotView.get() != null)
            mDyFPVShotView.get().clickBeautyLayout();
    }

    public void setDefaultBeauty() {
        float smoothValue = 1.0f * mSmoothLastValue / 100;
        float eyeValue = 1.0f * mReshapeEyeLastValue / 100;
        float faceValue = 1.0f * mReshapeCheekLatValue / 100;
        float lipstickValue = 1.0f * mLipstickLastValue / 100;
        float blusherValue = 1.0f * mBlusherLastValue / 100;

        mActivity.setBeauty(true, smoothValue);
        mActivity.setFaceReshape(FileUtils.getDyResourceFaceShapDir(mContext.get()), eyeValue, faceValue);
        mActivity.setFaceReshape(FileUtils.getDyResourceFaceShapDir(mContext.get()), eyeValue, faceValue);
        mActivity.setIntensityByType(VEBeautyParam.BEAUTY_LIPSTICK, lipstickValue);
        mActivity.setIntensityByType(VEBeautyParam.BEAUTY_BLUSHER, blusherValue);
    }

    private void clearBeauty() {
        mActivity.setBeauty(false, 0);
        mActivity.setFaceReshape("", 0, 0);
        mActivity.setIntensityByType(VEBeautyParam.BEAUTY_LIPSTICK, 0);
        mActivity.setIntensityByType(VEBeautyParam.BEAUTY_BLUSHER, 0);
    }

    private void setEnable(boolean enable) {
        if (enable) {
            mSkinImg.setAlpha(1f);
            mFaceImg.setAlpha(1f);
            mEyeImg.setAlpha(1f);
            mRougeImg.setAlpha(1f);
            mBlushImg.setAlpha(1f);

            mSkinText.setAlpha(1f);
            mFaceText.setAlpha(1f);
            mEyeText.setAlpha(1f);
            mRougeText.setAlpha(1f);
            mBlushText.setAlpha(1f);
        } else {
            mSkinImg.setAlpha(0.4f);
            mFaceImg.setAlpha(0.4f);
            mEyeImg.setAlpha(0.4f);
            mRougeImg.setAlpha(0.4f);
            mBlushImg.setAlpha(0.4f);

            mSkinText.setAlpha(0.4f);
            mFaceText.setAlpha(0.4f);
            mEyeText.setAlpha(0.4f);
            mRougeText.setAlpha(0.4f);
            mBlushText.setAlpha(0.4f);

            for (int i = 0; i < mViews.size(); i++) {
                mViews.get(i).setSelected(false);
            }

            for (int i =0; i < mTextViews.size(); i++) {
                mTextViews.get(i).setTextColor(0XFFFFFFFF);
            }
        }
    }
}
