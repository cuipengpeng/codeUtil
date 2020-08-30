package com.test.xcamera.profession;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.test.xcamera.R;
import com.test.xcamera.activity.CameraMode;
import com.test.xcamera.bean.EventMessage;
import com.test.xcamera.bean.MoSettingModel;
import com.test.xcamera.enumbean.ScreenOrientationType;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.mointerface.ExposModeCallback;
import com.test.xcamera.mointerface.MoRequestCallback;
import com.test.xcamera.utils.DlgUtils;
import com.test.xcamera.utils.ViewUitls;
import com.test.xcamera.view.GalleryLayoutManager;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by smz on 2020/1/15.
 * 曝光值参数调整页面
 */

public class ParamCustomView extends LinearLayout {
    private SelectButton mSelectButton;
    private RecyclerView mExposure, mShut, mIso;
    private TextView mShutValue, mIsoValue;
    private TextView mExposureValue;
    private GalleryLayoutManager mExposureLM, mShutLM, mIsoLM;
    private ParamAdapter mShutAdapter = new ParamAdapter();
    private ParamAdapter mIsoAdapter = new ParamAdapter();
    private List<Constant.ParamMap> mShutList = Constant.shutValues;
    private List<Constant.ParamMap> mIsoValues = Constant.isoValues;
    private int mShutFpsValue = -1, mSpeed = -1;
    private int mMode = -1;
    private int evIndex, shutIndex, isoIndex;

    private ScreenOrientationType mScreenType = ScreenOrientationType.LANDSCAPE;
    private MoSettingModel mModel;
    private View mPortraitView, mLandView;

    public ParamCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(mScreenType);
    }

    private void init(ScreenOrientationType type) {
        mScreenType = type;
        this.removeAllViews();

        View view = null;
        if (type == ScreenOrientationType.LANDSCAPE) {
            if (mLandView == null)
                mLandView = LayoutInflater.from(getContext()).inflate(R.layout.layout_custom_param_land, this, false);
            view = mLandView;
        } else if (type == ScreenOrientationType.PORTRAIT) {
            if (mPortraitView == null)
                mPortraitView = LayoutInflater.from(getContext()).inflate(R.layout.layout_custom_param_portrait, this, false);
            view = mPortraitView;
        }
        this.addView(view);

        mSelectButton = view.findViewById(R.id.select_btn);
        mSelectButton.setText(" 自动 ", "自定义");
        mSelectButton.setSelectListener((status) -> {
            if (mModel == null)
                return false;

            final int mode = status == SelectButton.LEFT ? 0 : 1;
            ConnectionManager.getInstance().setExposureModel(mode, new ExposModeCallback() {
                @Override
                public void onSuccess(int iso, int ev, int shut) {
                    post(() -> {
                        syncCB(mode, iso, ev, shut);
                    });
                }
            });
            return false;
        });

        view.findViewById(R.id.down).setOnClickListener((v) -> {
            if (v.isPressed()) {
                EventBus.getDefault().post(new EventMessage(EventMessage.SHOW_SETTING_VIEW));
            }
        });

        mExposure = view.findViewById(R.id.exposure);
        mShut = view.findViewById(R.id.shut);
        mIso = view.findViewById(R.id.iso);
        mExposureValue = view.findViewById(R.id.exposure_value);
        mShutValue = view.findViewById(R.id.shut_value);
        mIsoValue = view.findViewById(R.id.iso_value);

        //根据方向 调整顺序
        if (Constant.evValues.get(0).rotation != type)
            for (List<Constant.ParamMap> list : Constant.values) {
                for (Constant.ParamMap item : list)
                    item.rotation = type;
            }

        mExposureLM = initItems(mExposure, Constant.evValues, evIndex);
        mExposureLM.setOnItemSelectedListener((rv, item, position) -> {
            evIndex = position;
            int value = Constant.evValues.get(position).map;
            ConnectionManager.getInstance().setEV(value, new MoRequestCallback() {
                @Override
                public void onSuccess() {
                    if (mModel.isVideo) {
                        mModel.videoModel.ev = value;
                    } else {
                        mModel.snapshotModel.ev = value;
                    }
                }

                @Override
                public void onFailed() {
                    DlgUtils.toast(getContext(), "设置失败");
                }
            });
        });
        mShutLM = initItems(mShut, mShutList, shutIndex, mShutAdapter);
        mShutLM.setOnItemSelectedListener((rv, item, position) -> {
            shutIndex = position;
            int value = mShutList.get(position).map;
            ConnectionManager.getInstance().setShutter(value, new MoRequestCallback() {
                @Override
                public void onSuccess() {
                    if (mModel.isVideo) {
                        mModel.videoModel.shutter = value;
                    } else {
                        mModel.snapshotModel.shutter = value;
                    }
                }

                @Override
                public void onFailed() {
                    DlgUtils.toast(getContext(), "设置失败");
                }
            });
        });
        mIsoLM = initItems(mIso, mIsoValues, isoIndex, mIsoAdapter);
        mIsoLM.setOnItemSelectedListener((rv, item, position) -> {
            isoIndex = position;
            int value = mIsoValues.get(position).map;
            ConnectionManager.getInstance().setISOValue(value, new MoRequestCallback() {
                @Override
                public void onSuccess() {
                    if (mModel.isVideo) {
                        mModel.videoModel.iso = value;
                    } else {
                        mModel.snapshotModel.iso = value;
                    }
                }

                @Override
                public void onFailed() {
                    DlgUtils.toast(getContext(), "设置失败");
                }
            });
        });
    }

    private void syncCB(int mode, int iso, int ev, int shut) {
        syncView(mode == 0);

        if (mModel.isVideo) {
            mModel.videoModel.exposure_mode = mode;
            mModel.videoModel.ev = ev;
            mModel.videoModel.shutter = shut;
            mModel.videoModel.iso = iso;
        } else {
            mModel.snapshotModel.exposure_mode = mode;
            mModel.snapshotModel.ev = ev;
            mModel.snapshotModel.shutter = shut;
            mModel.snapshotModel.iso = iso;
        }

        syncMode(mModel);
    }

    public void orientation(ScreenOrientationType type) {
        init(type);
        syncMode(mModel);
    }

    /**
     * 同步UI
     *
     * @param auto true 自动  false 自定义
     */
    private void syncView(boolean auto) {
        mSelectButton.setStatus(auto ? SelectButton.LEFT : SelectButton.RIGHT);
        ViewUitls.isViewGone(mExposure, !auto);
        ViewUitls.isViewGone(mShut, auto);
        ViewUitls.isViewGone(mIso, auto);
        ViewUitls.isViewGone(mExposureValue, auto);
        ViewUitls.isViewGone(mShutValue, !auto);
        ViewUitls.isViewGone(mIsoValue, !auto);
    }

    public void syncMode(MoSettingModel model) {
        this.mModel = model;
        if (model == null) return;
//        L.e("ParamCustomView  syncMode==>" + model.toString());

        int exposure_mode = 0;
        int ev = 0;
        int shutter = 0;
        int iso = 0;
        int fps;
        int speed = 0;
        if (model.isVideo) {
            exposure_mode = model.videoModel.exposure_mode;
            ev = model.videoModel.ev;
            shutter = model.videoModel.shutter;
            iso = model.videoModel.iso;
            fps = model.videoModel.frame_rate;
            speed = model.videoModel.speed;
        } else {
            exposure_mode = model.snapshotModel.exposure_mode;
            ev = model.snapshotModel.ev;
            shutter = model.snapshotModel.shutter;
            iso = model.snapshotModel.iso;
            fps = model.snapshotModel.frame_rate;
        }
        if (fps != mShutFpsValue || mMode != model.mode || mSpeed != speed) {

            mShutFpsValue = fps;
            mSpeed = speed;
            mShutList = Constant.getShutValues(CameraMode.SLOW_MOTION == CameraMode.getMode(model.mode) ? speed : fps);

            if (mShutList.get(mShutList.size() - 1).rotation != mScreenType)
                for (Constant.ParamMap paramMap : mShutList)
                    paramMap.rotation = mScreenType;

            mShutAdapter.setNewData(mShutList);
            mShutAdapter.notifyDataSetChanged();
        }

        if (mShutList.get(mShutList.size() - 1).rotation != mScreenType || mShutList.get(0).rotation != mScreenType) {
            for (Constant.ParamMap paramMap : mShutList) {
                paramMap.rotation = mScreenType;
            }
            mShutAdapter.notifyDataSetChanged();
        }

        if (mMode != model.mode) {
            mIsoValues = Constant.getIsoValues(model.mode);
            mIsoAdapter.setNewData(mIsoValues);
        }

        syncView(exposure_mode == 0);

        if (this.getVisibility() == View.VISIBLE) {
            if (exposure_mode == 0) {
                int position = 0;
                for (int i = 0; i < Constant.evValues.size(); i++)
                    if (Constant.evValues.get(i).map == ev) {
                        position = i;
                        break;
                    }
                mExposure.smoothScrollToPosition(position);
            } else {
                int posShut = 0, posIso = 0;
                for (int i = 0; i < mShutList.size(); i++)
                    if (mShutList.get(i).map == shutter) {
                        posShut = i;
                        break;
                    }

                for (int i = 0; i < mIsoValues.size(); i++)
                    if (mIsoValues.get(i).map == iso) {
                        posIso = i;
                        break;
                    }

                mShut.smoothScrollToPosition(posShut);
                mIso.smoothScrollToPosition(posIso);
            }
        }

        mMode = model.mode;
    }

    private GalleryLayoutManager initItems(RecyclerView recyclerView, List<Constant.ParamMap> params, int index, ParamAdapter adapter) {
        //清除 防止反复横竖屏添加过多的回调
        recyclerView.clearOnScrollListeners();
        recyclerView.setOnFlingListener(null);

        GalleryLayoutManager layoutManager = new GalleryLayoutManager(GalleryLayoutManager.VERTICAL, true);
        layoutManager.attach(recyclerView, index);
        layoutManager.setCallbackInFling(true);
        if (adapter == null)
            adapter = new ParamAdapter();
        adapter.setNewData(params);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((ad, view, position) -> {
            recyclerView.smoothScrollToPosition(position);
        });
        return layoutManager;
    }

    private GalleryLayoutManager initItems(RecyclerView recyclerView, List<Constant.ParamMap> params, int index) {
        return initItems(recyclerView, params, index, null);
    }
}
