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
import com.test.xcamera.bean.MoShotSetting;
import com.test.xcamera.enumbean.ScreenOrientationType;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.mointerface.MoRequestCallback;
import com.test.xcamera.viewcontrol.MoFPVShotSettingControl;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by zll on 2019/9/29.
 * 延时摄影 参数view
 */

public class PreviewLapseView extends RelativeLayout {
    private static final String ITEMS[] = {"0.2", "0.3", "0.5", "1.0", "2.0", "3.0", "5.0", "10.0", "15.0", "20.0", "25.0", "30.0", "60.0"};

    @BindView(R.id.preivew_longexplore_view_recyclerview)
    RecyclerView mRecyclerview;
    private LapseAdpter mAdpter;
    private Unbinder mUnbinder;
    private ArrayList<LapseValue> mValues;
    private TextView mLandText, mPortraitText;
    private ScreenOrientationType mScreenOrientationType;
    private String mTime;
    private LapseValueChange mLapseValueChange;

    public PreviewLapseView(Context context) {
        super(context);

        initView(context);
    }

    public PreviewLapseView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.preview_lapse_head_layout, this, true);

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

        mAdpter = new LapseAdpter();
        mAdpter.setData(mValues);
        mRecyclerview.setAdapter(mAdpter);
        mAdpter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                setValue(position);
                MoFPVShotSettingControl.getInstance().setLapseValue(mTime);
            }
        });
    }

    private void initLandData() {
        mValues = new ArrayList<>();

        for (String item : ITEMS)
            mValues.add(new LapseValue(item, false));
        mRecyclerview.smoothScrollToPosition(0);
    }

    private void initPortraitData() {
        mValues = new ArrayList<>();
        for (String item : ITEMS)
            mValues.add(new LapseValue(item, false));
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

        MoFPVShotSettingControl.getInstance().setLapseValue(mTime);
    }

    private void setValue(final int position) {
        for (int i = 0; i < mValues.size(); i++) {
            if (i == position) {
                mValues.get(i).isSelected = true;
                mTime = mValues.get(i).duration;
            } else {
                mValues.get(i).isSelected = false;
            }
        }
        mAdpter.notifyDataSetChanged();

        final String duration = mValues.get(position).duration;
        if (mLapseValueChange != null)
            mLapseValueChange.onValue(mTime);
        //参数为秒
        setLapseValue(duration);
    }

    public void unBindButterKnife() {
        mUnbinder.unbind();
    }

    public void syncSetting(MoShotSetting shotSetting) {
        if (shotSetting.getmMoRecordSetting() != null) {
            int duration = shotSetting.getmMoRecordSetting().getInterval();
            mTime = String.format("%.1f", duration / 1000f);
            setViewState(mTime);
        }

        MoFPVShotSettingControl.getInstance().setLapseValue(mTime);
    }

    private void setViewState(String time) {
        for (int i = 0; i < mValues.size(); i++) {
            mValues.get(i).isSelected = mValues.get(i).duration.equals(time);
        }

        if (mLapseValueChange != null)
            mLapseValueChange.onValue(mTime);

        mAdpter.notifyDataSetChanged();
    }

    private void setLapseValue(final String value) {
        ConnectionManager.getInstance().stopPreview(new MoRequestCallback() {
            @Override
            public void onSuccess() {
                ConnectionManager.getInstance().laspeRecordSetting((int) (Float.parseFloat(value) * 1000), new MoRequestCallback() {
                    @Override
                    public void onSuccess() {
//                        EventBus.getDefault().post(new EventMessage(EventMessage.RESET_ZOOM));

                        ConnectionManager.getInstance().startPreview(null);
                    }

                    @Override
                    public void onFailed() {
                        ConnectionManager.getInstance().startPreview(null);
                    }
                });
            }

            @Override
            public void onFailed() {

            }
        });

    }

    class LapseAdpter extends RecyclerView.Adapter<LapseAdpter.ViewHolder> {
        private ArrayList<LapseValue> mData;
        private OnItemClickListener itemClickListener;

        public void setItemClickListener(OnItemClickListener listener) {
            itemClickListener = listener;
        }

        public void setData(ArrayList<LapseValue> values) {
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
            LapseValue value = mData.get(position);

            viewHolder.textView.setText(value.duration);

            viewHolder.textView.setSelected(value.isSelected);

            if (mScreenOrientationType == ScreenOrientationType.PORTRAIT) {
                viewHolder.textView.setRotation(-90);
//                viewHolder.textView.animate().rotation(-90);
            } else if (mScreenOrientationType == ScreenOrientationType.LANDSCAPE) {
                viewHolder.textView.setRotation(0);
//                viewHolder.textView.animate().rotation(0);
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

    class LapseValue {
        String duration;
        boolean isSelected = false;

        public LapseValue(String duration, boolean isSelected) {
            this.duration = duration;
            this.isSelected = isSelected;
        }
    }

    public void setLapseValueChange(LapseValueChange lapseValueChange) {
        this.mLapseValueChange = lapseValueChange;
    }

    public interface LapseValueChange {
        void onValue(String value);
    }
}

