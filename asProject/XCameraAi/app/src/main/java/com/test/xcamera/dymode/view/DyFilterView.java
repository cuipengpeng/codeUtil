package com.test.xcamera.dymode.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
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
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.dymode.adapter.DyFilterAdapter;
import com.test.xcamera.dymode.adapter.DyFilterTypeAdapter;
import com.test.xcamera.dymode.managers.EffectPlatform;
import com.test.xcamera.utils.AppExecutors;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.DensityUtils;
import com.test.xcamera.view.VerticalTextView;
import com.ss.android.ugc.effectmanager.common.task.ExceptionResult;
import com.ss.android.ugc.effectmanager.effect.model.Effect;
import com.ss.android.ugc.effectmanager.effect.model.EffectCategoryResponse;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 滤镜选择
 * Created by zll on 2020/2/26.
 */

public class DyFilterView extends RelativeLayout implements DyFilterAdapter.OnItemClickListener,
        DyFilterTypeAdapter.OnTypeClickListener {
    private static final String TAG = "DyFilterView";

    @BindView(R.id.dy_fpv_filter_name)
    TextView mFilterName;
    @BindView(R.id.dy_fpv_filter_type)
    TextView mFilterType;
    @BindView(R.id.dy_fpv_filter_clear)
    ImageView mFilterClear;
    @BindView(R.id.dy_fpv_filter_people)
    VerticalTextView mFilterPeople;
    @BindView(R.id.dy_fpv_filter_scenery)
    VerticalTextView mFilterScenery;
    @BindView(R.id.dy_fpv_filter_beautiful_scenery)
    VerticalTextView mFilterBeautifulScenery;
    @BindView(R.id.dy_fpv_filter_new)
    VerticalTextView mFilterNew;
    @BindView(R.id.dy_fpv_filter_recycler_view)
    RecyclerView mFiltersRecyclerView;
    @BindView(R.id.dy_fpv_filter_type_recyclerview)
    RecyclerView mFilterTypeRecyclerView;
    @BindView(R.id.dy_fpv_filter_seek_bar)
    VerticalSeekBar mSeekBar;
    @BindView(R.id.dy_fpv_filter_seek_bar_container)
    VerticalSeekBarWrapper mSeekBarWrapper;
    @BindView(R.id.dy_fpv_filter_name_layout)
    LinearLayout mFilterNameLayout;
    @BindView(R.id.dy_fpv_filter_detail_layout)
    LinearLayout mFilterLayout;
    @BindView(R.id.dy_fpv_filter_value)
    VerticalTextView mValue;
    @BindView(R.id.dy_fpv_filter_empty_view)
    View mEmptyView;

    private WeakReference<Context> mContext;
    private DyFilterAdapter mFilterAdapter;
    private WeakReference<DyFPVActivity> mActivity;
    private int mCurrentProgress;
    private Effect mCurrentEffect;
    private int mCurrentPosition;
    private DyFilterTypeAdapter mFilterTypeAdapter;
    private android.os.Handler mHandler = new android.os.Handler();
    private String mCurrentName;
    private int mTmpProgress;
    private int mType1, mType2, mType3;
    private WeakReference<DyFPVShotView> mDyFPVShotView;

    public DyFilterView(Context context) {
        super(context);

//        init(context);
    }

    public DyFilterView(Context context, AttributeSet attrs) {
        super(context, attrs);

//        init(context);
    }

    public void attachActivity(DyFPVActivity activity) {
        mActivity = new WeakReference<>(activity);
    }

    public void init(Context context, DyFPVShotView shotView) {
        mContext = new WeakReference<>(context);
        mDyFPVShotView = new WeakReference<>(shotView);
        LayoutInflater.from(context).inflate(R.layout.dy_fpv_filter, this, true);

        ButterKnife.bind(this);

        mType1 = mType2 = mType3 = -1;

        mCurrentName = EffectPlatform.getInstance().getFirtTypeName();
        mFilterClear.setBackgroundResource(R.mipmap.icon_dy_props_clear);
        mFiltersRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = DensityUtils.dp2px(mContext.get(), 5);
                outRect.bottom = DensityUtils.dp2px(mContext.get(), 5);
            }
        });


        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext.get(), 4, GridLayoutManager.HORIZONTAL, false);
        mFilterTypeRecyclerView.setLayoutManager(gridLayoutManager);

        mFilterTypeAdapter = new DyFilterTypeAdapter(this, EffectPlatform.getInstance().getFilterTypes());
        mFilterTypeRecyclerView.setAdapter(mFilterTypeAdapter);

        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mFiltersRecyclerView.setLayoutManager(manager);

        mFilterAdapter = new DyFilterAdapter(this, EffectPlatform.getInstance().getFirstFilters());
        mFiltersRecyclerView.setAdapter(mFilterAdapter);

        mSeekBar.setVisibility(GONE);
        mValue.setVisibility(GONE);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTmpProgress = progress;
                if (progress < 0 || progress > 100) return;
                Log.d(TAG, "onProgressChanged: " + progress);
                mValue.setText(progress + "");
                if (mCurrentEffect != null) {
                    float intensity = 1.0f * progress / 100;
                    if (checkActivity()) {
                        mActivity.get().setFilter(mCurrentEffect.getUnzipPath(), intensity);

                    }
                    mCurrentEffect.setSource(progress);
                    EffectPlatform.getInstance().syncData(true, EffectPlatform.PANEL_FILTER, mCurrentPosition, mCurrentEffect);
                }
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
                if (mTmpProgress <= 0 || mTmpProgress >= 100) return;
                mValue.setTranslationY(value);
            }
        });

        mSeekBarWrapper.setSeekBarSizeChangedListener(new VerticalSeekBarWrapper.SeekBarSizeChangedListener() {
            @Override
            public void onSizeChanged(int width, int height) {
                float value = mTmpProgress * height / 100 - 90;
                AppExecutors.getInstance().mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        mValue.setTranslationY(value);
                    }
                });
            }
        });

//        mSeekBar.setProgressChangedListener(new DyVerticalSeekBar.ProgessChangedListener() {
//            @Override
//            public void onProgressChanged(int progress) {
//                if (progress < 0 || progress > 100) return;
//                if (mCurrentEffect != null) {
//                    float intensity = 1.0f * progress / 100;
//                    mActivity.setFilter(mCurrentEffect.getUnzipPath(), intensity);
//                    mCurrentEffect.setEffectType(progress);
//                    EffectPlatform.getInstance().syncData(EffectPlatform.PANEL_FILTER, mCurrentPosition, mCurrentEffect);
//                }
//            }
//        });
//        mSeekBar.init(ScreenUtils.getScreenWidth(mContext));
//        mSeekBar.setSeekBarChaneListener(new DyFPVSeekBar.DyFPVSeekBarChangedListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                // TODO set filter value
//                if (mCurrentEffect != null) {
//                    float intensity = 1.0f * progress / 100;
//                    mActivity.setFilter(mCurrentEffect.getUnzipPath(), intensity);
//                    mCurrentEffect.setEffectType(progress);
//                    EffectPlatform.getInstance().syncData(EffectPlatform.PANEL_FILTER, mCurrentPosition, mCurrentEffect);
//                }
//            }
//        });
    }

    private boolean checkActivity() {
        return mActivity != null && mActivity.get() != null;
    }

    @OnClick({R.id.dy_fpv_filter_clear, R.id.dy_fpv_filter_people, R.id.dy_fpv_filter_scenery,
            R.id.dy_fpv_filter_beautiful_scenery, R.id.dy_fpv_filter_new, R.id.dy_fpv_filter_detail_layout
            , R.id.dy_fpv_filter_empty_view})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dy_fpv_filter_clear:
                mType1 = mType2 = mType3 = -1;
                mSeekBar.setVisibility(GONE);
                mValue.setVisibility(GONE);
                if (mFilterAdapter != null) {
                    if (checkActivity()) {
                        mActivity.get().setFilter("", 0);
                    }
                    mFilterAdapter.resetSelect();
                    mFilterTypeAdapter.resetSelect();
                    mFilterClear.setBackgroundResource(R.mipmap.icon_dy_props_clear_light);
                }
                break;
            case R.id.dy_fpv_filter_people:
                selectPeople();
                break;
            case R.id.dy_fpv_filter_scenery:
                selectScenery();
                break;
            case R.id.dy_fpv_filter_beautiful_scenery:
                selectBeautifulScenery();
                break;
            case R.id.dy_fpv_filter_new:
                selectNew();
                break;
            case R.id.dy_fpv_filter_detail_layout:

                break;
            case R.id.dy_fpv_filter_empty_view:
                hide();
                break;
        }
    }

    private void selectPeople() {
        Log.d(TAG, "selectPeople: ");
        mFilterPeople.setTextColor(0XFFFFFFFF);
        setUnSelect(mFilterScenery, mFilterBeautifulScenery, mFilterNew);
    }

    private void selectScenery() {
        mFilterScenery.setTextColor(0XFFFFFFFF);
        setUnSelect(mFilterPeople, mFilterBeautifulScenery, mFilterNew);
    }

    private void selectBeautifulScenery() {
        mFilterBeautifulScenery.setTextColor(0XFFFFFFFF);
        setUnSelect(mFilterScenery, mFilterPeople, mFilterNew);
    }

    private void selectNew() {
        mFilterNew.setTextColor(0XFFFFFFFF);
        setUnSelect(mFilterScenery, mFilterBeautifulScenery, mFilterPeople);
    }

    private void setUnSelect(TextView... views) {
        for (TextView textView : views) {
            textView.setTextColor(0XFF7A7A7A);
        }
    }

    @Override
    public void onItemClick(int position, Effect effect) {
        Log.d(TAG, "onItemClick: position = " + position);
        mCurrentPosition = position;
        setSelectedPostion(position);
        if (!effect.isDownloaded()) {
            mSeekBar.setVisibility(GONE);
            mValue.setVisibility(GONE);
            EffectPlatform.getInstance().fetchEffect(effect, new EffectPlatform.EffectDownloadListener() {
                @Override
                public void onStart(Effect effect) {
                    Log.d(TAG, "fetchEffect onStart: ");
                    mFilterAdapter.notifyItemChanged(position, 1);
                }

                @Override
                public void onProgress(Effect effect, int progress, long totalSize) {
                    Log.d(TAG, "fetchEffect onProgress: " + progress);
                }

                @Override
                public void onSuccess(Effect effect) {
                    Log.d(TAG, "fetchEffect onSuccess: path = " + effect.getUnzipPath());
                    AppExecutors.getInstance().mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            EffectPlatform.getInstance().syncData(true, EffectPlatform.PANEL_FILTER, position, effect);
                            mFilterAdapter.notifyDataSetChanged();
                            mCurrentEffect = effect;
                            mTmpProgress = effect.getSource();
                            mSeekBar.setProgress(effect.getSource());
                            if (checkActivity()) {
                                mActivity.get().setFilter(effect.getUnzipPath(), 1.0f);
                            }
                            mFilterClear.setBackgroundResource(R.mipmap.icon_dy_props_clear);
                            float value = mTmpProgress * mSeekBarWrapper.getmHeight() / 100 - 140;
                            mValue.setTranslationY(value);
                            showFilterName(effect.getName(), mCurrentName);
                            mSeekBar.setVisibility(VISIBLE);
                            mValue.setVisibility(VISIBLE);
                        }
                    });
                }

                @Override
                public void onFailed(Effect failedEffect, ExceptionResult e) {
                    Log.d(TAG, "fetchEffect onFailed: ");
                    if (e.getErrorCode() == 10011) {
                        AppExecutors.getInstance().mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                CameraToastUtil.show90(AiCameraApplication.getContext().getString(R.string.dy_download_error_net), mContext.get());
                                EffectPlatform.getInstance().syncData(false, EffectPlatform.PANEL_DEFAULT, position, effect);
                                mFilterAdapter.notifyItemChanged(position, 2);
                            }
                        });
                    }
                }
            });
        } else {
            mFilterClear.setBackgroundResource(R.mipmap.icon_dy_props_clear);
            float intensity = 1.0f * effect.getSource() / 100;
            if (checkActivity()) {
                mActivity.get().setFilter(effect.getUnzipPath(), intensity);
            }
            mCurrentEffect = effect;
            mCurrentProgress = effect.getSource();
            mTmpProgress = effect.getSource();
            mSeekBar.setProgress(mCurrentProgress);
            mCurrentEffect = effect;
            int offset;
            if (mTmpProgress == 100) offset = 140;
            else offset = 90;
            float value = mTmpProgress * mSeekBarWrapper.getmHeight() / 100 - offset;
            mValue.setTranslationY(value);
            showFilterName(effect.getName(), mCurrentName);
            mSeekBar.setVisibility(VISIBLE);
            mValue.setVisibility(VISIBLE);
        }
    }

    private void showFilterName(String name, String type) {
        mHandler.removeCallbacks(hideNameRunnable);
        mFilterNameLayout.setVisibility(VISIBLE);
        mFilterType.setText(type);
        mFilterName.setText(name);
        mHandler.postDelayed(hideNameRunnable, 2000);
    }

    private Runnable hideNameRunnable = new Runnable() {
        @Override
        public void run() {
            mFilterNameLayout.setVisibility(GONE);
        }
    };

    @Override
    public void onItemClick(int position, EffectCategoryResponse response) {
        mFilterClear.setBackgroundResource(R.mipmap.icon_dy_props_clear);
        mCurrentName = response.getName();
        Log.d(TAG, "onItemClick: mCurrentName = " + mCurrentName);

        if (mFilterAdapter != null) {
            mFilterAdapter.clearData();
            Log.d(TAG, "onItemClick: postion: " + getSelectionPostin());
            mFilterAdapter.setData(response.getTotalEffects(), getSelectionPostin());
        }
    }

    public void notifyFilterAdapter() {
        if (mFilterTypeAdapter != null)
            mFilterTypeAdapter.setData(EffectPlatform.getInstance().getFilterTypes());
        if (mFilterAdapter != null) {
            mFilterAdapter.clearData();
            mFilterAdapter.setData(EffectPlatform.getInstance().getFirstFilters(), getSelectionPostin());
        }
    }

    private void setSelectedPostion(int postion) {
        switch (mCurrentName) {
            case "人像":
                mType1 = postion;
                mType2 = -1;
                mType3 = -1;
                break;
            case "风景":
                mType2 = postion;
                mType1 = -1;
                mType3 = -1;
                break;
            case "新锐":
                mType3 = postion;
                mType1 = -1;
                mType2 = -1;
                break;
        }
    }

    private int getSelectionPostin() {
        int postion = -1;
        switch (mCurrentName) {
            case "人像":
                postion = mType1;
                break;
            case "风景":
                postion = mType2;
                break;
            case "新锐":
                postion = mType3;
                break;
        }
        return postion;
    }

    private void hide() {
        if (mDyFPVShotView.get() != null)
            mDyFPVShotView.get().clickFilterLayout();
    }
}
