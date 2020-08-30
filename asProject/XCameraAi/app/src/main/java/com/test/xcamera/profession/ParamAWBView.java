package com.test.xcamera.profession;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.test.xcamera.R;
import com.test.xcamera.bean.EventMessage;
import com.test.xcamera.bean.MoSettingModel;
import com.test.xcamera.enumbean.ScreenOrientationType;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.mointerface.AwbModeCallback;
import com.test.xcamera.mointerface.MoRequestCallback;
import com.test.xcamera.utils.StringUtil;
import com.test.xcamera.view.GalleryLayoutManager;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by smz on 2020/1/19.
 * <p>
 * 白平衡的设置
 */
public class ParamAWBView extends LinearLayout {
    private MoSettingModel mModel;
    private View mPortraitView, mLandView;
    private View mLine;
    private int autoIndex, cusIndex = Constant.awbValuesReve.size() - 1;
    private List<Constant.ParamMap> mAwbValues;

    private SelectButton mSelectButton;
    private RecyclerView mAuto, mCustom;
    private ScreenOrientationType mScreenType = ScreenOrientationType.LANDSCAPE;

    private Handler handler = new Handler();

    public ParamAWBView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(mScreenType);
    }

    private void init(ScreenOrientationType type) {
        mScreenType = type;

        this.removeAllViews();
        View view = null;
        if (type == ScreenOrientationType.LANDSCAPE) {
            if (mLandView == null)
                mLandView = LayoutInflater.from(getContext()).inflate(R.layout.layout_awb_param_land, this, false);
            view = mLandView;
        } else if (type == ScreenOrientationType.PORTRAIT) {
            if (mPortraitView == null)
                mPortraitView = LayoutInflater.from(getContext()).inflate(R.layout.layout_awb_param_portrait, this, false);
            view = mPortraitView;
        }
        this.addView(view);

        view.findViewById(R.id.down).setOnClickListener((v) -> {
            if (v.isPressed()) {
                EventBus.getDefault().post(new EventMessage(EventMessage.SHOW_SETTING_VIEW));
            }
        });

        //根据方向 调整顺序
        if (Constant.settingsAwb.get(0).rotation != type) {
            for (IconAdapter.SettingItem item : Constant.settingsAwb)
                item.rotation = type;
        }

        if (type == ScreenOrientationType.LANDSCAPE) {
            mAwbValues = Constant.awbValuesReve;
        } else if (type == ScreenOrientationType.PORTRAIT) {
            mAwbValues = Constant.awbValues;
        }

        mSelectButton = view.findViewById(R.id.select_btn);
        mSelectButton.setText(StringUtil.getStr(R.string.fpv_setting_scene), StringUtil.getStr(R.string.fpv_set_custom));
        mSelectButton.setSelectListener((status) -> {
            final int mode = status == SelectButton.LEFT ? 0 : 1;

            ConnectionManager.getInstance().setAwbModel(mode, new AwbModeCallback() {
                @Override
                public void onSuccess(int awb) {
                    post(() -> {
                        if (mModel.isVideo) {
                            mModel.videoModel.awb = awb;
                            mModel.videoModel.awb_mode = mode;
                        } else {
                            mModel.snapshotModel.awb = awb;
                            mModel.snapshotModel.awb_mode = mode;
                        }
                        syncMode(mModel);
                    });
                }
            });
            return false;
        });

        mAuto = view.findViewById(R.id.params_auto);
        mCustom = view.findViewById(R.id.params_custom);
        mLine = view.findViewById(R.id.awb_line);

        initAdapter();

        if (mModel == null)
            syncView(false);
    }

    /**
     * 横竖屏时 修正列表index的顺序
     */
    private int fixValue(int value, List list) {
        if (mScreenType == ScreenOrientationType.PORTRAIT) {
            return value;
        } else if (mScreenType == ScreenOrientationType.LANDSCAPE) {
            return list.size() - 1 - value;
        }

        return -1;
    }

    public void orientation(ScreenOrientationType type) {
        init(type);
        syncMode(mModel);
    }

    private void initAdapter() {
        IconAdapter adapter = new IconAdapter();
        adapter.setNewData(Constant.settingsAwb);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mAuto.setLayoutManager(llm);
        mAuto.setAdapter(adapter);
        adapter.setOnItemClickListener((ad, view, position) -> {
            if (mSelectButton.status == 1)
                return;

            autoIndex = fixValue(position, Constant.settingsAwb);

            ConnectionManager.getInstance().setAwb(Constant.settingsAwb.get(position).extra, new MoRequestCallback() {
                @Override
                public void onSuccess() {
                    if (mModel.isVideo) {
                        mModel.videoModel.awb = Constant.settingsAwb.get(position).extra;
                    } else {
                        mModel.snapshotModel.awb = Constant.settingsAwb.get(position).extra;
                    }
                    for (int i = 0; i < Constant.settingsAwb.size(); i++)
                        Constant.settingsAwb.get(i).selected = position == i;

                    handler.post(() -> {
                        adapter.notifyDataSetChanged();
                    });
                }
            });
        });
        mAuto.scrollToPosition(fixValue(autoIndex, Constant.settingsAwb));

        initItems(mCustom, mAwbValues, fixValue(cusIndex, mAwbValues)).setOnItemSelectedListener((rv, item, position) -> {
            if (mSelectButton.status == 0)
                return;

            cusIndex = fixValue(position, mAwbValues);

            if (mModel == null) return;

            int awb = mAwbValues.get(position).map;

            ConnectionManager.getInstance().setAwb(awb, new MoRequestCallback() {
                @Override
                public void onSuccess() {
                    if (mModel.isVideo) {
                        mModel.videoModel.awb = mAwbValues.get(position).map;
                    } else {
                        mModel.snapshotModel.awb = mAwbValues.get(position).map;
                    }
                    for (int i = 0; i < mAwbValues.size(); i++)
                        mAwbValues.get(i).selected = position == i;
                    post(() -> {
                        mCustom.getAdapter().notifyDataSetChanged();
                    });
                }
            });

            for (int i = 0; i < mAwbValues.size(); i++)
                mAwbValues.get(i).selected = position == i;

            mCustom.getAdapter().notifyDataSetChanged();
        });
    }

    /**
     * 同步UI
     *
     * @param auto true 场景  false 自定义
     */
    private void syncView(boolean auto) {
        mSelectButton.setStatus(auto ? SelectButton.LEFT : SelectButton.RIGHT);
        mAuto.setVisibility(auto ? View.VISIBLE : View.GONE);
        mCustom.setVisibility(!auto ? View.VISIBLE : View.GONE);
        mLine.setVisibility(!auto ? View.VISIBLE : View.GONE);
    }

    private GalleryLayoutManager initItems(RecyclerView recyclerView, List<Constant.ParamMap> params, int index) {
        //清除 防止反复横竖屏添加过多的回调
        recyclerView.clearOnScrollListeners();
        recyclerView.setOnFlingListener(null);

        GalleryLayoutManager layoutManager = new GalleryLayoutManager(GalleryLayoutManager.VERTICAL, true);
        layoutManager.attach(recyclerView, index);
        layoutManager.setCallbackInFling(true);
        ParamAdapter adapter = new ParamAdapter();
        adapter.setNewData(params);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((ad, view, position) -> {
            recyclerView.smoothScrollToPosition(position);
        });
        return layoutManager;
    }

    public void syncMode(MoSettingModel model) {
        syncMode(model, true);
    }

    public void syncMode(MoSettingModel model, boolean sync) {
        this.mModel = model;
        if (model == null) return;

        int awbType = model.isVideo ? model.videoModel.awb_mode : model.snapshotModel.awb_mode;
        syncView(awbType == 0);

        if (model.isVideo) {
            for (Constant.ParamMap item : mAwbValues) {
                item.selected = model.videoModel.awb == item.map;
                if (item.selected)
                    cusIndex = fixValue(mAwbValues.indexOf(item), mAwbValues);
            }

            for (IconAdapter.SettingItem item : Constant.settingsAwb) {
                item.selected = model.videoModel.awb == item.extra;
                if (item.selected)
                    autoIndex = Constant.settingsAwb.indexOf(item);
            }
        } else {
            for (Constant.ParamMap item : mAwbValues) {
                item.selected = model.snapshotModel.awb == item.map;
                if (item.selected)
                    cusIndex = fixValue(mAwbValues.indexOf(item), mAwbValues);
            }
            for (IconAdapter.SettingItem item : Constant.settingsAwb) {
                item.selected = model.snapshotModel.awb == item.extra;
                if (item.selected)
                    autoIndex = Constant.settingsAwb.indexOf(item);
            }
        }
        mCustom.getAdapter().notifyDataSetChanged();

        mAuto.getAdapter().notifyDataSetChanged();
        mAuto.scrollToPosition(autoIndex);

        mCustom.smoothScrollToPosition(fixValue(cusIndex, mAwbValues));
    }
}
