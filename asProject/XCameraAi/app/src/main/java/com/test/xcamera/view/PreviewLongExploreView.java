package com.test.xcamera.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.test.xcamera.R;
import com.test.xcamera.bean.EventMessage;
import com.test.xcamera.bean.MoShotSetting;
import com.test.xcamera.enumbean.ScreenOrientationType;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.mointerface.MoRequestCallback;
import com.test.xcamera.statistic.StatisticFPVLayer;
import com.test.xcamera.utils.StringUtil;
import com.test.xcamera.viewcontrol.MoFPVShotSettingControl;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * FPV页长曝光子模式设置
 * Created by zll on 2019/10/24.
 */

public class PreviewLongExploreView extends RelativeLayout {
    @BindView(R.id.preivew_longexplore_view_recyclerview)
    RecyclerView mRecyclerview;
    private PreviewLongExploreAdpter mAdpter;
    private Unbinder mUnbinder;
    private ArrayList<LongExploreValue> mValues;
    private TextView mLandText, mPortraitText;
    private ScreenOrientationType mScreenOrientationType;
    private float mTime;

    private float shuts[] = {
            1f, 1.3f, 1.6f, 2f, 2.5f, 3.2f, 4f, 5f, 6f, 8f, 10f, 13f, 15f, 20f, 25f, 30f
    };

    public PreviewLongExploreView(Context context) {
        super(context);

        initView(context);
    }

    public PreviewLongExploreView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.preview_longexplore_layout, this, true);

        mLandText = findViewById(R.id.preivew_longexplore_view_land_text);
        mPortraitText = findViewById(R.id.preivew_longexplore_view_portrait_text);
        mScreenOrientationType = ScreenOrientationType.LANDSCAPE;

        mLandText.setVisibility(View.VISIBLE);
        mPortraitText.setVisibility(View.GONE);

        mUnbinder = ButterKnife.bind(this);

        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerview.setLayoutManager(manager);

        initLandData();

        mAdpter = new PreviewLongExploreAdpter();
        mAdpter.setData(mValues);
        mRecyclerview.setAdapter(mAdpter);
        mAdpter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                setValue(position);
            }
        });
    }

    private void initLandData() {
        mValues = new ArrayList<>();
        for (float v : shuts) {
            LongExploreValue value = new LongExploreValue(v, false);
            mValues.add(value);
        }
        mRecyclerview.smoothScrollToPosition(0);
    }

    private void initPortraitData() {
        mValues = new ArrayList<>();
        for (float v : shuts) {
            LongExploreValue value = new LongExploreValue(v, false);
            mValues.add(value);
        }
        Collections.reverse(mValues);
        mRecyclerview.smoothScrollToPosition(mValues.size());
    }

    public void changeOrientation(ScreenOrientationType orientationType) {
        if (orientationType == ScreenOrientationType.LANDSCAPE) {
            mScreenOrientationType = ScreenOrientationType.LANDSCAPE;
            mLandText.setVisibility(View.VISIBLE);
            mPortraitText.setVisibility(View.GONE);
            initLandData();
            mAdpter.setData(mValues);
            mAdpter.notifyDataSetChanged();
            setViewState(mTime);
        } else if (orientationType == ScreenOrientationType.PORTRAIT) {
            mScreenOrientationType = ScreenOrientationType.PORTRAIT;
            mPortraitText.setVisibility(View.VISIBLE);
            mLandText.setVisibility(View.GONE);
            initPortraitData();
            mAdpter.setData(mValues);
            mAdpter.notifyDataSetChanged();
            setViewState(mTime);
        }

        MoFPVShotSettingControl.getInstance().setLongExploreValue(mTime);
    }

    private void setValue(final int position) {
        for (int i = 0; i < mValues.size(); i++) {
            if (i == position) {
                mValues.get(i).setSelected(true);
                mTime = mValues.get(i).getDuration();
            } else {
                mValues.get(i).setSelected(false);
            }
        }
        mAdpter.setData(mValues);
        mAdpter.notifyDataSetChanged();

        final float duration = mValues.get(position).getDuration();
        new android.os.Handler().post(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new EventMessage(EventMessage.HIDE_PARAMS_VIEW));
                MoFPVShotSettingControl.getInstance().setLongExploreValue(mTime);
            }
        });

        StatisticFPVLayer.getInstance().setOnEvent(StatisticFPVLayer.FloatLayer_Capture_ExposureParameterSelection, "time:" + mTime);

        //参数为秒
        ConnectionManager.getInstance().setLongExplore(duration, new MoRequestCallback() {
            @Override
            public void onSuccess() {
                new android.os.Handler().post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }

            @Override
            public void onFailed() {

            }
        });
    }

    public void unBindButterKnife() {
        mUnbinder.unbind();
    }

    public void syncSetting(MoShotSetting shotSetting) {
        if (shotSetting.getmMoSnapShotSetting() != null) {
            int time = shotSetting.getmMoSnapShotSetting().getmLongExploreTime();
            mTime = time / 1000f / 1000f;
            setViewState(mTime);

            MoFPVShotSettingControl.getInstance().setLongExploreValue(mTime);
        }
    }

    private void setViewState(float time) {
        for (int i = 0; i < mValues.size(); i++) {
            LongExploreValue longExploreValue = mValues.get(i);
            if (Math.abs(longExploreValue.getDuration() - time) < 0.01f) {
                mValues.get(i).setSelected(true);
            } else {
                mValues.get(i).setSelected(false);
            }
        }
        mAdpter.setData(mValues);
        mAdpter.notifyDataSetChanged();
    }

    class PreviewLongExploreAdpter extends RecyclerView.Adapter<PreviewLongExploreAdpter.ViewHolder> {
        private ArrayList<LongExploreValue> mData;
        private OnItemClickListener itemClickListener;

        public void setItemClickListener(OnItemClickListener listener) {
            itemClickListener = listener;
        }

        public void setData(ArrayList<LongExploreValue> values) {
            mData = values;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.prview_longexplore_recyclerview_item_view, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
            LongExploreValue value = mData.get(position);
            if (value.getDuration() == 0)
                viewHolder.textView.setText("关闭");
            else {
                float v = value.getDuration();
                //如果是整数 则不保留小数部分
                if (v - (int) v < 0.01)
                    viewHolder.textView.setText(((int) value.getDuration()) + StringUtil.getStr(R.string.second));
                else
                    viewHolder.textView.setText(value.getDuration() + StringUtil.getStr(R.string.second));
            }
            viewHolder.textView.setSelected(value.isSelected());

            if (mScreenOrientationType == ScreenOrientationType.PORTRAIT) {
                viewHolder.textView.setRotation(-90);
            } else if (mScreenOrientationType == ScreenOrientationType.LANDSCAPE) {
                viewHolder.textView.setRotation(0);
            }

            viewHolder.textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(position);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                textView = itemView.findViewById(R.id.prview_longexplore_recyclerview_item_text);
            }
        }
    }

    interface OnItemClickListener {
        void onItemClick(int position);
    }

    class LongExploreValue {
        float duration;
        boolean isSelected = false;

        public LongExploreValue(float duration, boolean isSelected) {
            this.duration = duration;
            this.isSelected = isSelected;
        }

        public float getDuration() {
            return duration;
        }

        public void setDuration(float duration) {
            this.duration = duration;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }
    }
}
