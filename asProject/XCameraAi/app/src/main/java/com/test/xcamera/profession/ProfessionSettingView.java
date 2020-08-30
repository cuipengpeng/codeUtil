package com.test.xcamera.profession;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.widget.FrameLayout;

import com.test.xcamera.R;
import com.test.xcamera.accrssory.AccessoryManager;
import com.test.xcamera.activity.CameraMode;
import com.test.xcamera.bean.EventMessage;
import com.test.xcamera.bean.MoSettingModel;
import com.test.xcamera.enumbean.ScreenOrientationType;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.mointerface.MoRequestCallback;
import com.test.xcamera.utils.DlgUtils;
import com.test.xcamera.utils.StringUtil;
import com.test.xcamera.view.AutoTrackingRectViewFix;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.test.xcamera.profession.Constant.EXPOSURE;

/**
 * Created by smz on 2020/1/15.
 * 总设置
 * <p>
 * 设置页面与参数页面关联太紧密
 * 显示子参数页面 使用ProfessionView的实例
 * 关闭子参数页面 使用Eventbus
 */

public class ProfessionSettingView extends FrameLayout implements View.OnClickListener {
    private static final int FOLLOW = 1;    //跟随
    private static final int LOCK_P_R = 2;  //锁定俯仰角
    public static final int FPV = 3;       //FPV模式

    public int mViewRotate;
    private RecyclerView mRecyclerView;
    private View mFollow, mLock, mPtzFollow, mPtzLock;
    private IconAdapter mAdapter;
    private MoSettingModel mSettingModel;
    private List<View> mList = new ArrayList<>();
    private SparseArray<View> ptzStatus = new SparseArray<>(3);
    private int hdrIcon[] = {R.mipmap.icon_hdr_auto, R.mipmap.icon_hdr, R.mipmap.icon_hdr_light, R.mipmap.icon_hdr_off};
    //    private int hdrText[] = {R.string.hdr_auto, R.string.hdr_open, R.string.hdr_night, R.string.hdr_close};
    private int ptzMode, ptzSensitivity;

    //切换云台模式时 如果在跟踪状态 则不能切换、
    //跟踪状态需要实时获取 且跟踪模块和设置模块没有联系  传对象最方便
    public AutoTrackingRectViewFix mTrackView;
    private CameraMode mCameraMode;
    private ProfessionView mProfessionView;
    private Handler handler = new Handler();

    public ProfessionSettingView(@NonNull Context context) {
        super(context);
        init();
    }

    public ProfessionSettingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        View.inflate(getContext(), R.layout.layout_profession_setting, this);

        mRecyclerView = findViewById(R.id.items);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);
        mAdapter = new IconAdapter();
        mAdapter.mEnableSele = false;
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            //TODO 测试
            if (mSettingModel == null || !AccessoryManager.getInstance().mIsRunning) {
                DlgUtils.toast(getContext(), getResources().getString(R.string.fpv_setting_connect), mViewRotate);
                return;
            }

            /**
             * 将所有item的状态值保存在MoSettingModel中 如曝光参数、白平衡
             * 如果在子页面有改变 通过MoSettingModel中的值同步本页面的状态
             * */
            IconAdapter.SettingItem settingsVideo = mAdapter.getData().get(position);

            switch (settingsVideo.extra) {
                case Constant.HDR:
                    int value = 1;
                    if (!this.mSettingModel.isVideo) {
                        int index = this.mSettingModel.snapshotModel.HDR;
                        value = ++index;
                        if (value > hdrIcon.length - 1)
                            value = 1;
                    }
                    final int v = value;
                    ConnectionManager.getInstance().setDynamicRange(v, new MoRequestCallback() {
                        @Override
                        public void onSuccess() {
                            if (!mSettingModel.isVideo)
                                mSettingModel.snapshotModel.HDR = v;

                            handler.post(() -> {
                                syncMode(mSettingModel);
                            });
                        }
                    });
                    break;
                case Constant.FORMAT:
                    int type = -1;
                    if (this.mSettingModel.isVideo) {
                        type = this.mSettingModel.videoModel.type == 0 ? 1 : 0;
                    } else
                        type = this.mSettingModel.snapshotModel.type == 0 ? 2 : 0;

                    final int valueT = type;
                    ConnectionManager.getInstance().setFormat(valueT, new MoRequestCallback() {
                        @Override
                        public void onSuccess() {
                            if (mSettingModel.isVideo)
                                mSettingModel.videoModel.type = valueT;
                            else
                                mSettingModel.snapshotModel.type = valueT;

                            handler.post(() -> {
                                syncMode(mSettingModel);
                            });
                        }
                    });
                    break;
                case EXPOSURE:    //曝光参数
                    if (mCameraMode == CameraMode.LONG_EXPLORE) {
                        return;     //长曝光模式 无法调整曝光值
                    }
                    //拍照模式 如果hdr未关闭 则曝光不可用
                    if (mCameraMode == CameraMode.PHOTO) {
                        if (mSettingModel.snapshotModel.HDR == 1 || mSettingModel.snapshotModel.HDR == 2)
                            return;
                    }

                    this.setVisibility(View.GONE);
                    mProfessionView.mParamCustomView.setVisibility(View.VISIBLE);
                    mProfessionView.mParamCustomView.syncMode(mSettingModel);
                    break;
                case Constant.AWB:     //白平衡
                    this.setVisibility(View.GONE);
                    mProfessionView.mParamAWBView.setVisibility(View.VISIBLE);
                    mProfessionView.mParamAWBView.syncMode(mSettingModel);
                    break;
                case Constant.MORE:
                    break;
            }
        });

        mFollow = findViewById(R.id.follow);
        mLock = findViewById(R.id.lock);
        mPtzFollow = findViewById(R.id.ptz_follow);
        mPtzLock = findViewById(R.id.ptz_lock);
        ptzStatus.put(FOLLOW, mPtzFollow);   //3.3.39.模式（云台）
        ptzStatus.put(LOCK_P_R, mPtzLock);
        ptzStatus.put(FPV, mLock);

        mList.add(mFollow);
        mList.add(mLock);
        mList.add(mPtzFollow);
        mList.add(mPtzLock);
        for (View view : mList)
            view.setOnClickListener(this);

        setMode(CameraMode.VIDEO);
    }

    public void setProfessionView(ProfessionView professionView) {
        this.mProfessionView = professionView;
    }

    public void orientation(ScreenOrientationType type) {
        if (type == ScreenOrientationType.PORTRAIT) {
            for (View view : mList)
                view.setRotation(-90);
        } else if (type == ScreenOrientationType.LANDSCAPE) {
            for (View view : mList)
                view.setRotation(0);
        }

        if (mAdapter.getData().get(0).rotation != type) {
            for (IconAdapter.SettingItem item : mAdapter.getData())
                item.rotation = type;
            mAdapter.notifyDataSetChanged();
        }
    }

    public void syncPtzMode(int ptzMode, int ptzSensitivity) {
        if (ptzMode > 0) {
            this.ptzMode = ptzMode;

            for (int i = 0; i < ptzStatus.size(); i++) {
                int key = ptzStatus.keyAt(i);
                ptzStatus.get(key).setSelected(key == ptzMode);
            }
        }
        if (ptzSensitivity > 0) {
            this.ptzSensitivity = ptzSensitivity;

            mFollow.setSelected(ptzSensitivity == 1);
        }
    }

    public void syncMode(MoSettingModel model) {
        this.mSettingModel = model;
        if (model == null) return;

        setMode(CameraMode.getMode(model.mode));

        if (this.mSettingModel.isVideo) {
            for (IconAdapter.SettingItem item : Constant.settingsVideo) {
                if (item.extra == EXPOSURE) {
                    item.selected = model.videoModel.exposure_mode == 1;
                    item.text = model.videoModel.exposure_mode == 0 ? getResources().getString(R.string.fpv_set_auto)
                            : getResources().getString(R.string.fpv_set_custom);
                }
                if (item.extra == Constant.AWB) {
                    item.icon = syncAwbItem(model.videoModel.awb_mode, model.videoModel.awb);
                    item.text = model.videoModel.awb_mode == 0 ? getResources().getString(R.string.fpv_set_auto)
                            : getResources().getString(R.string.fpv_set_custom);
                }
                if (item.extra == Constant.FORMAT)
                    item.selected = model.videoModel.type == 1;
            }
        } else {
            List<IconAdapter.SettingItem> settings = Constant.settingsImage;

            for (IconAdapter.SettingItem item : settings) {
                if (item.extra == EXPOSURE) {
                    item.selected = model.snapshotModel.exposure_mode == 1;
                    item.text = model.snapshotModel.exposure_mode == 0 ? getResources().getString(R.string.fpv_set_auto)
                            : getResources().getString(R.string.fpv_set_custom);

                    if (CameraMode.getMode(this.mSettingModel.mode) == CameraMode.LONG_EXPLORE) {
//                            || (CameraMode.getMode(this.mSettingModel.mode) == CameraMode.PHOTO
//                                    && (model.snapshotModel.HDR == 1 || model.snapshotModel.HDR == 2))) {
                        item.icon = R.mipmap.profession_exposure_disable;
                        item.flag = 1;
                    } else {
                        item.flag = 0;
                        item.icon = R.mipmap.icon_exposure;
                    }
                }
                if (item.extra == Constant.AWB) {
                    item.icon = syncAwbItem(model.snapshotModel.awb_mode, model.snapshotModel.awb);
                    item.text = model.snapshotModel.awb_mode == 0 ? getResources().getString(R.string.fpv_set_auto)
                            : getResources().getString(R.string.fpv_set_custom);
                }
                if (item.extra == Constant.HDR) {
                    item.icon = hdrIcon[model.snapshotModel.HDR];
                    item.text = StringUtil.getStr(R.string.hdr_hdr);
                }
                if (item.extra == Constant.FORMAT)
                    item.selected = model.snapshotModel.type != 0; //0:jpg  2:jpg+raw
            }
        }

    }

    //适配白平衡图标  白平衡为场景时 使用对应的图标
    //否则使用自定义图标
    private int syncAwbItem(int mode, int value) {
        if (mode == 0) {
            for (IconAdapter.SettingItem item : Constant.settingsAwb)
                if (item.extra == value)
                    return item.icon;
        }
        return R.mipmap.icon_awb;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.follow:
                int value = mFollow.isSelected() ? 2 : 1;
                ConnectionManager.getInstance().setSpeed(value, new MoRequestCallback() {
                    @Override
                    public void onSuccess() {
                        post(() -> {
                            mFollow.setSelected(!mFollow.isSelected());
                            DlgUtils.toast(getContext(), getResources().getString(mFollow.isSelected()
                                    ? R.string.dlg_follow_sens : R.string.dlg_follow), mViewRotate);
                        });
                    }

                    @Override
                    public void onFailed() {
                        post(() -> {
                            DlgUtils.toast(getContext(), getResources().getString(R.string.fpv_setting_trace_set_err), mViewRotate);
                        });
                    }
                });
                break;
            case R.id.lock:
                if (!enableChangePtz())
                    break;
                setPtzMode(FPV, getResources().getString(R.string.ptz_lock));
                EventBus.getDefault().post(new EventMessage(EventMessage.ENABLE_TRACING, FPV));
                break;
            case R.id.ptz_follow:
                if (!enableChangePtz())
                    break;
                setPtzMode(FOLLOW, getResources().getString(R.string.ptz_follow));
                EventBus.getDefault().post(new EventMessage(EventMessage.ENABLE_TRACING, FOLLOW));
                break;
            case R.id.ptz_lock:
                if (!enableChangePtz())
                    break;
                setPtzMode(LOCK_P_R, getResources().getString(R.string.ptz_ptz_lock));
                EventBus.getDefault().post(new EventMessage(EventMessage.ENABLE_TRACING, LOCK_P_R));
                break;
        }
    }

    //跟踪状态下 不能切换模式
    private boolean enableChangePtz() {
        if (mTrackView != null && mTrackView.isTracingMode) {
            DlgUtils.toast(getContext(), getResources().getString(R.string.trace_ptz_unsupport), mViewRotate);
            return false;
        }
        return true;
    }

    private void setPtzMode(int ptzMode, String text) {
        ConnectionManager.getInstance().setModel(ptzMode, new MoRequestCallback() {
            @Override
            public void onSuccess() {
                post(() -> {
                    for (int i = 0; i < ptzStatus.size(); i++) {
                        int key = ptzStatus.keyAt(i);
                        ptzStatus.get(key).setSelected(key == ptzMode);
                    }

                    DlgUtils.toast(getContext(), text, mViewRotate);
                });
            }

            @Override
            public void onFailed() {
                post(() -> {
                    DlgUtils.toast(getContext(), getResources().getString(R.string.fpv_setting_ptz_mode_err), mViewRotate);
                });
            }
        });
    }

    public void setMode(CameraMode mode) {
        this.mCameraMode = mode;

        List<IconAdapter.SettingItem> settings = null;

        if (mode == CameraMode.PHOTO || mode == CameraMode.LONG_EXPLORE) {  //如果是拍照模式 且不是长曝光 则重置profession_exposure图标
            settings = Constant.settingsImage;
        } else {
            settings = Constant.settingsVideo;
        }

        mAdapter.setNewData(settings);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(EventMessage msg) {
        if (msg.code == EventMessage.SCREEN_CLICKED) {
            if (mProfessionView.mParamCustomView.getVisibility() == View.VISIBLE
                    || mProfessionView.mParamAWBView.getVisibility() == View.VISIBLE) {
                showSettingView();
            } else {
                mProfessionView.setVisibility(View.GONE);
            }
        } else if (msg.code == EventMessage.SHOW_SETTING_VIEW) {
            showSettingView();
        }
    }

    private void showSettingView() {
        mProfessionView.mParamCustomView.setVisibility(View.GONE);
        mProfessionView.mParamAWBView.setVisibility(View.GONE);

        this.setVisibility(View.VISIBLE);
        syncMode(mSettingModel);
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
}
