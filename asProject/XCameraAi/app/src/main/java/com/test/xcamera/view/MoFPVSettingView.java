package com.test.xcamera.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.test.xcamera.MoPresenters.MoFPVPresenter;
import com.test.xcamera.R;
import com.test.xcamera.adapter.MoFPVAWBTypeAdapter;
import com.test.xcamera.adapter.MoFPVSettingValueAdapter;
import com.test.xcamera.adapter.MoFPVSettingAdapter;
import com.test.xcamera.bean.AWBType;
import com.test.xcamera.bean.MoFPVSetting;
import com.test.xcamera.bean.MoShotSetting;
import com.test.xcamera.enumbean.ScreenOrientationType;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.managers.MoFPVParametersManager;
import com.test.xcamera.managers.ShotModeManager;
import com.test.xcamera.mointerface.MoFPVCallback;
import com.test.xcamera.utils.MoUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 设置拍摄参数的View
 * Created by zll on 2019/10/14.
 */

public class MoFPVSettingView extends RelativeLayout {
    private static final String TAG = "MoFPVSettingView";
    private static final int REFRESH_UI = 10000001;
    @BindView(R.id.shot_setting_all_lock_text)
    VerticalTextView mAllLockText;
    @BindView(R.id.shot_setting_all_lock_layout)
    RelativeLayout mAllLockLayout;
    @BindView(R.id.shot_setting_fpv_text)
    VerticalTextView mFPVText;
    @BindView(R.id.shot_setting_fpv_layout)
    RelativeLayout mFPVLayout;
    @BindView(R.id.shot_setting_lock_pitch_text)
    VerticalTextView mLockPitchText;
    @BindView(R.id.shot_setting_lock_pitch_layout)
    RelativeLayout mLockPitchLayout;
    @BindView(R.id.shot_setting_lock_roll_text)
    VerticalTextView mLockRollText;
    @BindView(R.id.shot_setting_lock_roll_layout)
    RelativeLayout mLockRollLayout;
    @BindView(R.id.shot_setting_check)
    ImageView mFirstCheck;
    @BindView(R.id.shot_setting_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.shot_setting_first_setting_layout)
    LinearLayout mFirstSettingLayout;
    @BindView(R.id.shot_setting_second_setting_layout)
    LinearLayout mSecondSettingLayout;
    @BindView(R.id.shot_setting_second_check)
    ImageView mExploreCheck;
    @BindView(R.id.shot_setting_shutter_text)
    VerticalTextView mShutterText;
    @BindView(R.id.shot_setting_shutter_layout)
    RelativeLayout mShutterLayout;
    @BindView(R.id.shot_setting_shutter_value)
    TextView mShutterValue;
    @BindView(R.id.shot_setting_ev_text)
    VerticalTextView mEVText;
    @BindView(R.id.shot_setting_ev_layout)
    RelativeLayout mEVLayout;
    @BindView(R.id.shot_setting_ev_value)
    TextView mEVValue;
    @BindView(R.id.shot_setting_iso_text)
    VerticalTextView mISOText;
    @BindView(R.id.shot_setting_iso_layout)
    RelativeLayout mISOLayout;
    @BindView(R.id.shot_setting_iso_value)
    TextView mISOValue;
    @BindView(R.id.shot_setting_second_setting_layout_hide)
    ImageView mHideImg;
    @BindView(R.id.shot_setting_second_setting_layout_recyclerview)
    RecyclerView mSecondRecyclerView;
    @BindView(R.id.shot_setting_second_mode_select_layout)
    LinearLayout mSecondChooseModeLayout;
    @BindView(R.id.shot_setting_second_mode_default_layout)
    LinearLayout mSecondDefaultValueLayout;
    @BindView(R.id.shot_setting_second_layout_one_layout)
    LinearLayout mSecondOneLayout;
    @BindView(R.id.shot_setting_second_awb_layout)
    LinearLayout mSecondAWBLayout;
    @BindView(R.id.shot_setting_second_awb_auto_check)
    ImageView mAWBAutoCheck;
    @BindView(R.id.shot_setting_second_awb_type_recycler_view)
    RecyclerView mAWBTypeRecyclerView;
    @BindView(R.id.shot_setting_second_awb_value_recycler_view)
    RecyclerView mAWBValueRecyclerView;

    private Context mContext;
    private ArrayList<View> mViews;
    private ArrayList<VerticalTextView> mTexts;
    private MoFPVSettingAdapter mSettingAdapter;
    private ArrayList<MoFPVSetting> moFPVSettings;
    private MoFPVSettingValueAdapter mFPVSettingValueAdapter;
    private MoFPVAWBTypeAdapter mFPVAWBTypeAdapter;
    private ArrayList<AWBType> mAWBTypesList;

    private Unbinder mUnBinder;
    private boolean mExploreAuto = true;
    private boolean mAWBAuto = true;

    private MoFPVCallback mFPVCallback;
    private MoFPVPresenter mFPVPresenter;
    private SyncDataThread mSyncDataThread;

    private int mExploreType = -1; // 0:ISO  1:EV  2:Shutter

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case REFRESH_UI:
                    moFPVSettings = MoFPVParametersManager.getInstance(mContext).getMoFPVSettings();
                    mSettingAdapter.setData(moFPVSettings);

                    mExploreCheck.setSelected(MoFPVParametersManager.getInstance(mContext).isExploreAuto());
                    mAWBAutoCheck.setSelected(MoFPVParametersManager.getInstance(mContext).isAWBAuto());

                    mISOValue.setText(MoFPVParametersManager.getInstance(mContext).getSyncISOValue() + "");
                    mEVValue.setText(MoFPVParametersManager.getInstance(mContext).getSyncEVValue() + "");
                    mShutterValue.setText(MoFPVParametersManager.getInstance(mContext).getSyncShutterValue() + "");

                    mFPVCallback.setExploreValue(mEVValue.getText().toString());
                    break;
            }
        }
    };

    public MoFPVSettingView(Context context) {
        super(context);

        initView(context);
    }

    public MoFPVSettingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView(context);
    }

    public void unBind() {
        mUnBinder.unbind();
    }

    public void setMoFPVCallback(MoFPVCallback callback) {
        mFPVCallback = callback;
    }

    public void setMoFPVPresenter(MoFPVPresenter presenter) {
        mFPVPresenter = presenter;
    }

    private void initView(Context context) {
        mContext = context;

        LayoutInflater.from(context).inflate(R.layout.shot_setting_layout, this, true);
        mUnBinder = ButterKnife.bind(this);

        mViews = new ArrayList<>();
        mViews.add(mLockRollLayout);
        mViews.add(mLockPitchLayout);
        mViews.add(mFPVLayout);
        mViews.add(mAllLockLayout);

        mTexts = new ArrayList<>();
        mTexts.add(mLockRollText);
        mTexts.add(mLockPitchText);
        mTexts.add(mFPVText);
        mTexts.add(mAllLockText);

        mExploreCheck.setSelected(true);
        mAWBAutoCheck.setSelected(true);

        initSettingTypeRecyclerView();
        initExploreRecyclerView();
        initAWBTypeRecyclerView();
        initAWBValueRecyclerView();

        setSecondViewState(true);

//        mSyncDataThread = new SyncDataThread();
//        mSyncDataThread.startThread();
//        mSyncDataThread.start();
    }

    @OnClick({R.id.shot_setting_all_lock_layout, R.id.shot_setting_fpv_layout,
            R.id.shot_setting_lock_pitch_layout, R.id.shot_setting_lock_roll_layout,
            R.id.shot_setting_check, R.id.shot_setting_second_check,
            R.id.shot_setting_shutter_layout, R.id.shot_setting_ev_layout,
            R.id.shot_setting_iso_layout, R.id.shot_setting_second_setting_layout_hide,
            R.id.shot_setting_second_awb_auto_check})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.shot_setting_all_lock_layout:
                setSelectState(R.id.shot_setting_all_lock_layout);
                ConnectionManager.getInstance().setModel(3, null);
                break;
            case R.id.shot_setting_fpv_layout:
                setSelectState(R.id.shot_setting_fpv_layout);
                ConnectionManager.getInstance().setModel(2, null);
                break;
            case R.id.shot_setting_lock_pitch_layout:
                setSelectState(R.id.shot_setting_lock_pitch_layout);
                ConnectionManager.getInstance().setModel(1, null);
                break;
            case R.id.shot_setting_lock_roll_layout:
                setSelectState(R.id.shot_setting_lock_roll_layout);
                ConnectionManager.getInstance().setModel(0, null);
                break;
            case R.id.shot_setting_check:
                boolean isChecked = mFirstCheck.isSelected();
                if (isChecked) {
                    mFirstCheck.setSelected(false);
                    mFPVCallback.changeExploreVisibility(View.GONE);
                } else {
                    mFirstCheck.setSelected(true);
                    mFPVCallback.changeExploreVisibility(View.VISIBLE);
                }
                if (moFPVSettings != null && moFPVSettings.size() > 0) {
                    moFPVSettings.clear();
                }
                moFPVSettings = MoFPVParametersManager.getInstance(mContext).getMoFPVSettings(!isChecked, mExploreAuto, mAWBAuto);
                mSettingAdapter.setData(moFPVSettings);
                mRecyclerView.smoothScrollToPosition(moFPVSettings.size() - 1);
                break;
            case R.id.shot_setting_second_check:
                if (mExploreCheck.isSelected()) {
                    mExploreCheck.setSelected(false);
                    setSecondViewState(false);
                    mExploreAuto = false;
                    selectISOLayout();
                } else {
                    mExploreCheck.setSelected(true);
                    setSecondViewState(true);
                    mExploreAuto = true;
                }
                moFPVSettings = MoFPVParametersManager.getInstance(mContext).getMoFPVSettings(mFirstCheck.isSelected(), mExploreAuto, mAWBAuto);
                mSettingAdapter.setData(moFPVSettings);
                break;
            case R.id.shot_setting_shutter_layout:
                selectShutterLayout();
                break;
            case R.id.shot_setting_ev_layout:
                selectEVLayout();
                break;
            case R.id.shot_setting_iso_layout:
                selectISOLayout();
                break;
            case R.id.shot_setting_second_setting_layout_hide:
                mFirstSettingLayout.setVisibility(VISIBLE);
                mSecondSettingLayout.setVisibility(GONE);
                break;
            case R.id.shot_setting_second_awb_auto_check:
                boolean isCheck = mAWBAutoCheck.isSelected();
                if (isCheck) {
                    mAWBAutoCheck.setSelected(false);
                    mAWBAuto = false;
                } else {
                    mAWBAutoCheck.setSelected(true);
                    mAWBAuto = true;
                }
                moFPVSettings = MoFPVParametersManager.getInstance(mContext).getMoFPVSettings(mFirstCheck.isSelected(), mExploreAuto, mAWBAuto);
                mSettingAdapter.setData(moFPVSettings);
                break;
        }
    }

    private void selectISOLayout() {
        mExploreType = 0;
        mISOLayout.setSelected(true);
        mEVLayout.setSelected(false);
        mShutterLayout.setSelected(false);

        mEVText.setTextColor(0XFF676A72);
        mISOText.setTextColor(0XFFFFFFFF);
        mShutterText.setTextColor(0XFF676A72);

        notifyAdapter(MoFPVParametersManager.getInstance(mContext).getISOValue());
    }

    private void selectEVLayout() {
        mExploreType = 1;
        mEVLayout.setSelected(true);
        mISOLayout.setSelected(false);
        mShutterLayout.setSelected(false);

        mEVText.setTextColor(0XFFFFFFFF);
        mISOText.setTextColor(0XFF676A72);
        mShutterText.setTextColor(0XFF676A72);

        notifyAdapter(MoFPVParametersManager.getInstance(mContext).getEVValue());
    }

    private void selectShutterLayout() {
        mExploreType = 2;
        mShutterLayout.setSelected(true);
        mISOLayout.setSelected(false);
        mEVLayout.setSelected(false);

        mEVText.setTextColor(0XFF676A72);
        mISOText.setTextColor(0XFF676A72);
        mShutterText.setTextColor(0XFFFFFFFF);

        notifyAdapter(MoFPVParametersManager.getInstance(mContext).getShutterValue());
    }

    private void setSelectState(int viewID) {
        for (int i = 0; i < mViews.size(); i++) {
            View view = mViews.get(i);
            VerticalTextView textView = mTexts.get(i);
            if (view.getId() == viewID) {
                textView.setTextColor(getResources().getColor(R.color.color_ff7700));
            } else {
                textView.setTextColor(getResources().getColor(R.color.color_666666));
            }
        }
    }

    private void initSettingTypeRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);

        moFPVSettings = MoFPVParametersManager.getInstance(mContext).getMoFPVSettings(false, mExploreAuto, mAWBAuto);

        mSettingAdapter = new MoFPVSettingAdapter();
        mRecyclerView.setAdapter(mSettingAdapter);
        mSettingAdapter.setData(moFPVSettings);
        mRecyclerView.scrollToPosition(moFPVSettings.size() - 1);

        mSettingAdapter.setOnItemClickListener(new MoFPVSettingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, ArrayList<MoFPVSetting> shotSettings) {
                MoFPVSetting setting = shotSettings.get(position);
//                if (!mFirstCheck.isSelected()) {
//                    if (setting.getmSelect() == 1) {
//                        setting.setmSelect(0);
//                    } else {
//                        setting.setmSelect(1);
//                    }
//                }
                if (setting.getmTitle().equals(MoUtil.getString(mContext, R.string.fpv_setting_type_explore))) {
                    mFirstSettingLayout.setVisibility(GONE);
                    mSecondSettingLayout.setVisibility(VISIBLE);
                    mSecondOneLayout.setVisibility(VISIBLE);
                    mSecondAWBLayout.setVisibility(GONE);
                    return;
                } else if (setting.getmTitle().equals(MoUtil.getString(mContext, R.string.fpv_setting_type_awb))) {
                    mFirstSettingLayout.setVisibility(GONE);
                    mSecondSettingLayout.setVisibility(VISIBLE);
                    mSecondAWBLayout.setVisibility(VISIBLE);
                    mSecondOneLayout.setVisibility(GONE);
                    return;
                } else if (setting.getmTitle().equals(MoUtil.getString(mContext, R.string.fpv_setting_type_color))) {
                    mFPVPresenter.setColor(setting.getmSelect() == 1 ? 0 : 1);
                } else if (setting.getmTitle().equals(MoUtil.getString(mContext, R.string.fpv_setting_type_track))) {
                    mFPVPresenter.setTrackSpeed(setting.getmSelect() == 1 ? 0 : 1);
                } else if (setting.getmTitle().equals(MoUtil.getString(mContext, R.string.fpv_setting_type_dis))) {
                    mFPVPresenter.setDis(setting.getmSelect() == 1 ? 0 : 1);
                } else if (setting.getmTitle().equals(MoUtil.getString(mContext, R.string.fpv_setting_type_wdr))) {
                    mFPVPresenter.setDynamicRange(setting.getmSelect() == 1 ? 0 : 1);
                } else if (setting.getmTitle().equals(MoUtil.getString(mContext, R.string.fpv_setting_type_super_high_quality))) {
                    mFPVPresenter.setSuperHighQuality(setting.getmSelect() == 1 ? 0 : 1);
                } else if (setting.getmTitle().equals(MoUtil.getString(mContext, R.string.fpv_setting_type_format))) {
                    int flag = setting.getmSelect() == 0 ? 0 : 1;
                    //拍照模式 0和2  录像模式 0和1
                    if (!ShotModeManager.getInstance().isVideo() && flag == 1)
                        flag = 2;
                    mFPVPresenter.setFormat(flag);
                } else if (setting.getmTitle().equals(MoUtil.getString(mContext, R.string.fpv_setting_type_more))) {
                    mFPVCallback.startMoreSettingActivity();
                }
                if (setting.getmSelect() == 1) {
                    setting.setmSelect(0);
                } else {
                    setting.setmSelect(1);
                }
                mSettingAdapter.notifyItemChanged(position, 1);
            }
        });
    }

    private void initExploreRecyclerView() {
        final GalleryLayoutManager layoutManager2 = new GalleryLayoutManager(GalleryLayoutManager.VERTICAL, true);
        layoutManager2.attach(mSecondRecyclerView, 3);
        layoutManager2.setCallbackInFling(true);
        layoutManager2.setCallBackOnce(false);
        layoutManager2.setOnItemSelectedListener(new GalleryLayoutManager.OnItemSelectedListener() {
            @Override
            public void onItemSelected(RecyclerView recyclerView, View item, int position) {
                String value;
                if (mExploreType == 0) {
                    value = MoFPVParametersManager.getInstance(mContext).getISOValue().get(position).getValue();
//                    value = value.substring(0, value.length() - 1);
                    mFPVPresenter.setISO(Integer.valueOf(value));
                } else if (mExploreType == 1) {
                    value = MoFPVParametersManager.getInstance(mContext).getEVValue().get(position).getValue();
//                    value = value.substring(0, value.length() - 1);
                    mFPVPresenter.setEV(Integer.valueOf(value));
                } else if (mExploreType == 2) {
                    value = MoFPVParametersManager.getInstance(mContext).getShutterValue().get(position).getValue();
                    value = value.substring(2);
                    mFPVPresenter.setShutter(Integer.valueOf(value));
                }
            }
        });

        mFPVSettingValueAdapter = new MoFPVSettingValueAdapter(mContext);
        mSecondRecyclerView.setAdapter(mFPVSettingValueAdapter);
        mFPVSettingValueAdapter.setData(MoFPVParametersManager.getInstance(mContext).getISOValue());
    }

    private void initAWBTypeRecyclerView() {
        LinearLayoutManager awbTypeManager = new LinearLayoutManager(mContext);
        awbTypeManager.setOrientation(LinearLayoutManager.VERTICAL);
        mAWBTypeRecyclerView.setLayoutManager(awbTypeManager);

        mFPVAWBTypeAdapter = new MoFPVAWBTypeAdapter();
        mAWBTypeRecyclerView.setAdapter(mFPVAWBTypeAdapter);
        mAWBTypesList = MoFPVParametersManager.getInstance(mContext).getAWBTypeValues();
        mFPVAWBTypeAdapter.setData(mAWBTypesList);
        mAWBTypeRecyclerView.smoothScrollToPosition(mAWBTypesList.size() - 1);

        mFPVAWBTypeAdapter.setOnItemClickListener(new MoFPVAWBTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, ArrayList<AWBType> awbTypes) {
                AWBType type = awbTypes.get(position);
                if (type.isSelected()) {
                    type.setSelected(false);
                } else {
                    type.setSelected(true);
                }
                for (int i = 0; i < awbTypes.size(); i++) {
                    if (i != position) {
                        awbTypes.get(i).setSelected(false);
                    }
                }
                if (position == awbTypes.size() - 1) {
                    mAWBAutoCheck.setSelected(false);
                    mAWBValueRecyclerView.setVisibility(View.VISIBLE);
                    mAWBAuto = false;
                    mFPVSettingValueAdapter.setData(MoFPVParametersManager.getInstance(mContext).getAWBValue());
                } else {
                    mAWBValueRecyclerView.setVisibility(View.INVISIBLE);
                    mAWBAutoCheck.setSelected(true);
                    mAWBAuto = true;
                }
                mFPVAWBTypeAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initAWBValueRecyclerView() {
        final GalleryLayoutManager awbValueManager = new GalleryLayoutManager(GalleryLayoutManager.VERTICAL, true);
        awbValueManager.attach(mAWBValueRecyclerView, 3);
        awbValueManager.setCallbackInFling(true);
        awbValueManager.setCallBackOnce(false);
        awbValueManager.setOnItemSelectedListener(new GalleryLayoutManager.OnItemSelectedListener() {
            @Override
            public void onItemSelected(RecyclerView recyclerView, View item, int position) {
                String value = MoFPVParametersManager.getInstance(mContext).getAWBValue().get(position).getValue();
                value = value.substring(0, value.length() - 1);
                mFPVPresenter.setAWB(Integer.valueOf(value));
            }
        });
        mAWBValueRecyclerView.setLayoutManager(awbValueManager);
        mAWBValueRecyclerView.setAdapter(mFPVSettingValueAdapter);
        mFPVSettingValueAdapter.setData(MoFPVParametersManager.getInstance(mContext).getAWBValue());

        mAWBValueRecyclerView.setVisibility(VISIBLE);
    }

    public void notifySetting() {
        if (moFPVSettings != null && moFPVSettings.size() > 0) {
            moFPVSettings.clear();
        }
        moFPVSettings = MoFPVParametersManager.getInstance(mContext).getMoFPVSettings(false, mExploreAuto, mAWBAuto);
        mSettingAdapter.setData(moFPVSettings);
        mRecyclerView.scrollToPosition(moFPVSettings.size() - 1);
    }

    private void setSecondViewState(boolean b) {
        if (b) {
            mSecondDefaultValueLayout.setVisibility(View.VISIBLE);
            mSecondRecyclerView.setVisibility(View.GONE);
            setSecondModeState(false);
        } else {
            mSecondDefaultValueLayout.setVisibility(View.GONE);
            mSecondRecyclerView.setVisibility(View.VISIBLE);
            setSecondModeState(true);
        }
    }

    private void setSecondModeState(boolean b) {
        mShutterLayout.setEnabled(b);
        mISOLayout.setEnabled(b);
        mEVLayout.setEnabled(b);
    }

    public void syncSetting(MoShotSetting shotSetting) {
        MoFPVParametersManager.getInstance(mContext).syncFPVSettings(shotSetting, mFirstCheck.isSelected());
        mHandler.sendEmptyMessage(REFRESH_UI);
    }

    private void notifyAdapter(ArrayList<MoFPVParametersManager.ValueData> data) {
        if (mFPVSettingValueAdapter != null) {
            mSecondRecyclerView.setVisibility(VISIBLE);
            mFPVSettingValueAdapter.setData(data);
            mSecondRecyclerView.smoothScrollToPosition(3);
        }
    }

    public void changeOrientation(ScreenOrientationType orientation) {
        if (mSettingAdapter != null) {
            mSettingAdapter.setScreenOrientation(orientation);
        }
        if (mFPVSettingValueAdapter != null) {
            mFPVSettingValueAdapter.setScreenOrientation(orientation);
        }
        if (mFPVAWBTypeAdapter != null) {
            mFPVAWBTypeAdapter.setScreenOrientation(orientation);
        }
    }

    public void release() {
        unBind();
        if (mSyncDataThread != null) {
            mSyncDataThread.stopThread();
        }
    }

    private class SyncDataThread extends Thread {
        private boolean isRunning = false;

        public void startThread() {
            isRunning = true;
        }

        public void stopThread() {
            isRunning = false;
        }

        @Override
        public void run() {
            while (isRunning) {
                if (mFPVPresenter != null) {
                    mFPVPresenter.syncShotSettings();
                }
                try {
                    Thread.sleep(3 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
