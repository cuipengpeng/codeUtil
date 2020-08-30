package com.test.xcamera.dymode.view;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.test.xcamera.R;
import com.test.xcamera.activity.DyFPVActivity;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.dymode.adapter.DyPropsAdapter;
import com.test.xcamera.dymode.managers.EffectPlatform;
import com.test.xcamera.utils.AppExecutors;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.DensityUtils;
import com.ss.android.ugc.effectmanager.common.task.ExceptionResult;
import com.ss.android.ugc.effectmanager.effect.model.Effect;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 抖音拍摄--道具选择
 * Created by zll on 2020/2/21.
 */

public class DyFPVPropsView extends RelativeLayout implements DyPropsAdapter.OnItemClickListener {
    private static final String TAG = "DyFPVPropsView";
    @BindView(R.id.dy_fpv_props_clear)
    ImageView mPropsClearBtn;
    @BindView(R.id.dy_fpv_props_recycle_view)
    RecyclerView mRecycleView;
    @BindView(R.id.dy_fpv_props_parent_layout)
    LinearLayout mParentLayout;
    @BindView(R.id.dy_fpv_props_hint)
    TextView mPropsHint;
    @BindView(R.id.dy_fpv_props_empty_view)
    View mEmptyView;

    private WeakReference<Context> mContext;
    private DyPropsAdapter mAdapter;
    private WeakReference<DyFPVActivity> mActivity;
    private WeakReference<DyFPVShotView> mDyFPVShotView;

    private List<Effect> mEffectList;
    private Handler mHandler = new Handler();

    public DyFPVPropsView(Context context) {
        super(context);

//        init(context);
    }

    public DyFPVPropsView(Context context, AttributeSet attrs) {
        super(context, attrs);

//        init(context);
    }

    public void attachActivity(DyFPVActivity activity) {
        mActivity =new WeakReference<>( activity);
    }

    public void init(Context context, DyFPVShotView shotView) {
        mContext = new WeakReference<>(context);
        mDyFPVShotView = new WeakReference<>(shotView);
        LayoutInflater.from(context).inflate(R.layout.dy_fpv_props, this, true);

        ButterKnife.bind(this);

        // 心动爱心 693488 没特效   https://lf3-effectcdn-tos.pstatp.com/obj/ies.fe.effect/8c79e1d023a7d1a738f2b931fe0b19fb  demo无效果
        // 毛绒耳饰 693480 位置错误 https://lf6-effectcdn-tos.pstatp.com/obj/ies.fe.effect/9ad5d4c98f0a332e429290e841094963  demo有效
        // 化妆段位 693476 中途中断 https://lf6-effectcdn-tos.pstatp.com/obj/ies.fe.effect/dcfd0c5b84efb090429dfbc92fe70194  demo正常
        // 出水芙蓉 693472 卡住     https://lf9-effectcdn-tos.pstatp.com/obj/ies.fe.effect/18a6adaf62bb1bde009b91c3f1ed9e17  demo正常
        // 是       693466 没特效   https://lf6-effectcdn-tos.pstatp.com/obj/ies.fe.effect/456203261036b492b04f8873a0599fb6  demo无特效
        // 嘻哈眼镜 531272 没特效   https://lf9-effectcdn-tos.pstatp.com/obj/ies.fe.effect/e0d0419628f6c70f8468444c4b6b85c8  demo无特效

        mRecycleView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.left = DensityUtils.dp2px(mContext.get(), 2);
                outRect.right = DensityUtils.dp2px(mContext.get(), 2);
                outRect.bottom = DensityUtils.dp2px(mContext.get(), 8);
                outRect.top = DensityUtils.dp2px(mContext.get(), 8);
            }
        });

        GridLayoutManager manager = new GridLayoutManager(mContext.get(), 5, GridLayoutManager.HORIZONTAL, true);
        mRecycleView.setLayoutManager(manager);

        mAdapter = new DyPropsAdapter(this, EffectPlatform.getInstance().getEffectList(EffectPlatform.PANEL_DEFAULT));
        mRecycleView.setAdapter(mAdapter);
    }

    @OnClick({R.id.dy_fpv_props_clear, R.id.dy_fpv_props_empty_view})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dy_fpv_props_clear:
                if(mActivity!=null&&mActivity.get()!=null){
                    mActivity.get().switchEffect("");
                }
                mAdapter.resetSelect();
                mPropsClearBtn.setBackgroundResource(R.mipmap.icon_dy_props_clear_light);
                mPropsHint.setVisibility(GONE);
                break;
            case R.id.dy_fpv_props_empty_view:
                hide();
                break;
        }
    }

    @Override
    public void onItemClick(int position, Effect effect) {
        Log.d(TAG, "onItemClick: position = " + position);
        if (!effect.isDownloaded()) {
            EffectPlatform.getInstance().fetchEffect(effect, new EffectPlatform.EffectDownloadListener() {
                @Override
                public void onStart(Effect effect) {
                    Log.d(TAG, "fetchEffect onStart: ");
                    AppExecutors.getInstance().mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyItemChanged(position, 1);
                        }
                    });
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
                            EffectPlatform.getInstance().syncData(true, EffectPlatform.PANEL_DEFAULT, position, effect);
                            mAdapter.notifyDataSetChanged();
                            switchEffect(effect.getUnzipPath());
                            mPropsClearBtn.setBackgroundResource(R.mipmap.icon_dy_props_clear);
                            setHint(effect);
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
                                mAdapter.notifyItemChanged(position, 2);
                            }
                        });
                    }
                }
            });
        } else {
            switchEffect(effect.getUnzipPath());
            mPropsClearBtn.setBackgroundResource(R.mipmap.icon_dy_props_clear);
            setHint(effect);
        }
    }
    private void switchEffect(String effectPath){
        if(mActivity!=null&&mActivity.get()!=null){
            mActivity.get().switchEffect(effectPath);
        }
    }

    private void setHint(Effect effect) {
        mPropsHint.setVisibility(GONE);
        mHandler.removeCallbacks(mHideHintRunnable);
        if (!TextUtils.isEmpty(effect.getHint())) {
            mPropsHint.setText(effect.getHint());
            mPropsHint.setVisibility(VISIBLE);
            mHandler.postDelayed(mHideHintRunnable, 5 * 1000);
        }
    }

    private Runnable mHideHintRunnable = new Runnable() {
        @Override
        public void run() {
            mPropsHint.setVisibility(GONE);
        }
    };

    public void notifyPropsAdapter() {
        if (mAdapter != null) {
            mAdapter.setData(EffectPlatform.getInstance().getEffectList(EffectPlatform.PANEL_DEFAULT));
        }
    }

    private void hide() {
        if (mDyFPVShotView.get() != null)
            mDyFPVShotView.get().clickPropLayout();
    }
}
