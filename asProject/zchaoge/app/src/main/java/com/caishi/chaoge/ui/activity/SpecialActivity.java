package com.caishi.chaoge.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caishi.chaoge.R;
import com.caishi.chaoge.base.BaseActivity;
import com.caishi.chaoge.bean.ModuleMaterialBean;
import com.caishi.chaoge.bean.SpecialBean;
import com.caishi.chaoge.manager.VideoEditManager;
import com.caishi.chaoge.ui.adapter.SpecialAdapter;
import com.caishi.chaoge.ui.widget.CaptionDrawRect;
import com.caishi.chaoge.ui.widget.MyCountDownTimer;
import com.caishi.chaoge.utils.AEModuleUtils;
import com.caishi.chaoge.utils.DisplayMetricsUtil;
import com.caishi.chaoge.utils.GlideUtil;
import com.caishi.chaoge.utils.LogUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gyf.barlibrary.ImmersionBar;
import com.rd.vecore.VirtualVideoView;
import com.rd.vecore.exception.InvalidArgumentException;
import com.rd.vecore.models.caption.CaptionObject;

import java.util.ArrayList;
import java.util.List;

public class SpecialActivity extends BaseActivity {


    private VideoEditManager videoEditManager;
    private RecyclerView rv_special_list;
    private ImageView img_special_cover;
    private VirtualVideoView vvv_special_video;
    private ArrayList<SpecialBean> specialList = new ArrayList<>();//字效的list


    int[] selected = new int[]{R.drawable.im_ziyoufanzhuan_selected, R.drawable.im_duoheng_selected,
            R.drawable.im_duoshu_selected, R.drawable.im_danheng_selected, R.drawable.im_danshu_selected};
    int[] unSelected = new int[]{R.drawable.im_ziyoufanzhuan, R.drawable.im_duoheng,
            R.drawable.im_duoshu, R.drawable.im_danheng, R.drawable.im_danshu};
    private ModuleMaterialBean moduleMaterialBean;
    private SpecialAdapter specialAdapter;
    private CaptionDrawRect cdr_special_rect;
    private boolean updateUI;//是否显示控制框
    private int specialFlag;//字效
    private String snapshot;//封面图
    boolean isBack = false;

    @Override
    public void initBundle(Bundle bundle) {
        super.initBundle(bundle);
        specialFlag = bundle.getInt("specialFlag");
        snapshot = bundle.getString("snapshot");
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_special;
    }

    @Override
    public void initView(View view) {
        ImmersionBar.with(this).titleBar(R.id.view_special)
                .init();
        videoEditManager = VideoEditManager.newInstance();
        moduleMaterialBean = videoEditManager.getModuleMaterialBean();
        moduleMaterialBean.specialFlag = 0;
        moduleMaterialBean.aeModulePath = AEModuleUtils.getAEPath(0);
        for (int i = 0; i < selected.length; i++) {
            SpecialBean specialBean = new SpecialBean();
            specialBean.isSelect = i == moduleMaterialBean.specialFlag;
            specialBean.selected = selected[i];
            specialBean.unSelected = unSelected[i];
            specialList.add(specialBean);
        }

        updateUI = moduleMaterialBean.specialFlag >= 3;
        ((TextView) $(R.id.tv_baseDialogTitle_title)).setText("字效");
        rv_special_list = $(R.id.rv_special_list);
        img_special_cover = $(R.id.img_special_cover);
//        img_special_cover.setVisibility(View.VISIBLE);
//        GlideUtil.loadImg(snapshot, img_special_cover);
        cdr_special_rect = $(R.id.cdr_special_rect);
        vvv_special_video = $(R.id.vvv_special_video);
        cdr_special_rect.initbmp();
        vvv_special_video.setSWDecoderSize(3840 * 3840);
        //设置当OnPause后是否释放播放资源
        vvv_special_video.setReleasePlaybackOnPause(false);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!isBack)
            videoEditManager.init(mContext, vvv_special_video, moduleMaterialBean,1);
    }

    @Override
    public void doBusiness() {
        specialAdapter = new SpecialAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        rv_special_list.setLayoutManager(linearLayoutManager);
        rv_special_list.setAdapter(specialAdapter);
        specialAdapter.setNewData(specialList);

    }

    @Override
    public void setListener() {
        registerDrawRectListener();
        vvv_special_video.setOnPlaybackListener(new VirtualVideoView.VideoViewListener() {
            @Override
            public void onPlayerPrepared() {
                img_special_cover.setVisibility(View.GONE);
                if (updateUI) {
                    if (moduleMaterialBean.specialFlag < 3) {
                        videoEditManager.showRectF = null;
                        videoEditManager.scale = -1;
                        videoEditManager.captionPosition = -1;
                    }
                    cdr_special_rect.setVisibility(View.VISIBLE);
                    showRect();
                } else {
                    cdr_special_rect.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPlayerCompletion() {

            }

            @Override
            public void onGetCurrentPosition(float v) {

            }
        });

        $(R.id.rl_special_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vvv_special_video.isPlaying()) {
                    vvv_special_video.pause();
                    if (moduleMaterialBean.specialFlag >= 3) {
                        cdr_special_rect.setVisibility(View.VISIBLE);
                        showRect();
                    }
                } else {
                    vvv_special_video.start();
                    quitEditCaptionMode();
                }
            }
        });

        $(R.id.ll_baseDialogTitle_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        specialAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener()

        {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                for (int i = 0; i < specialList.size(); i++) {
                    specialList.get(i).isSelect = i == position;
                }
                moduleMaterialBean.specialFlag = position;
                specialAdapter.setNewData(specialList);
                updateUI = position >= 3;
                if (position==3){
                    moduleMaterialBean.leftLocation = 0.5f;
                    moduleMaterialBean.topLocation = 0.5f;
                }
                if (position==4){
                    moduleMaterialBean.leftLocation = 0.5f;
                    moduleMaterialBean.topLocation = 0.3f;
                }
                if (updateUI) {
                    videoEditManager.showRectF = null;
                    videoEditManager.scale = -1;
                    videoEditManager.captionPosition = -1;
                }
                if (position < 3) {
                    moduleMaterialBean.aeModulePath = AEModuleUtils.getAEPath(position);
                }
                videoEditManager.updateVirtualVideo(vvv_special_video);

            }
        });

        $(R.id.tv_baseDialogTitle_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < specialList.size(); i++) {
                    if (specialList.get(i).isSelect) {
                        moduleMaterialBean.specialFlag = i;
                        if (i < 3)
                            moduleMaterialBean.aeModulePath = AEModuleUtils.getAEPath(i);
                    }
                }
                onFinish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        for (int i = 0; i < specialList.size(); i++) {
            specialList.get(i).isSelect = i == specialFlag;
        }
        moduleMaterialBean.specialFlag = specialFlag;
        specialAdapter.setNewData(specialList);
        onFinish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isBack = true;
    }

    private void onFinish() {
        isBack = true;
        if (null != vvv_special_video) {
            vvv_special_video.reset();
        }
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(0, R.anim.push_bottom_out);
    }

    @Override
    public void widgetClick(View v) {

    }


    public void showRect() {
        CaptionObject editingCaption = videoEditManager.getCaptionObject();
        if (editingCaption != null) {
            if (!editingCaption.isEditing()) {
                //因为新增时：mVirtualVideo.addCaption(editingCaption);-> 已经被添加到播放器中，此刻编辑需要remove 播放器中的此对象
                editingCaption.removeCaption(); //否则界面会出现两个（一个view对象，一个播放器中的seo对象）
                vvv_special_video.refresh();
                try {
                    editingCaption.editCaptionMode();
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                }
            }
            List<PointF> listPoint =
                    editingCaption.getListPoint();
//            Log.e(TAG, "showRect: " + listPoint);
            cdr_special_rect.SetDrawRect(listPoint);

        }
    }

    final int MSG_SHOWRECT = 456;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case MSG_SHOWRECT: {
                    showRect();
                }
                break;
            }
        }
    };


    RectF dst = null;
    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            LogUtil.e("dst===" + dst);
            videoEditManager.updateRectF(dst, scale);
        }
    };
    float scale = -1;

    /***
     * 编辑状态下，给控制器设置回调并显示
     */
    public void registerDrawRectListener() {
        cdr_special_rect.SetOnTouchListener(new CaptionDrawRect.OnTouchListener() {
            @Override
            public void onDrag(PointF prePointF, PointF nowPointF) {
                if (null != nowPointF && null != prePointF) {
                    CaptionObject editingCaption = videoEditManager.getCaptionObject();
                    Log.e(TAG, "onDrag: " + prePointF + ">>" + nowPointF);
                    if (editingCaption != null) {
                        editingCaption.offSetCenter((nowPointF.x - prePointF.x) / DisplayMetricsUtil.getScreenWidth(mContext),
                                (nowPointF.y - prePointF.y) / DisplayMetricsUtil.getScreenHeight(mContext));
                        showRect();
                        //指定其余的字幕位置
                        List<PointF> listPoint = editingCaption.getListPoint();
                        if (null != listPoint) {
                            PointF plt = listPoint.get(0), prb = listPoint.get(2);
                            int sw = vvv_special_video.getWordLayout().getWidth();
                            int sh = vvv_special_video.getWordLayout().getHeight();
                            dst = new RectF(plt.x / sw, plt.y / sh, prb.x / sw, prb.y / sh);
                            scale = editingCaption.getScale();
                            //防止频繁更新位置（耗时）
                            mHandler.removeCallbacks(mRunnable);
                            mHandler.postDelayed(mRunnable, 200);
                        }
                    }

                }
            }

            @Override
            public void onScaleAndRotate(float offsetScale, float rotation) {
//                if (Math.abs(rotation) != 0) {
//                    mCurrentInfo.getCaptionObject().rotateCaption(mCurrentInfo.getRotateAngle() - rotation);
//                    onResetDrawRect();
//                }
                CaptionObject editingCaption = videoEditManager.getCaptionObject();
                if (editingCaption == null)
                    return;
                if (offsetScale != 0) {
                    //缩放字幕，缩放变化量
                    editingCaption.offSetScale(offsetScale);
                    showRect();
                    scale = editingCaption.getScale();
                    List<PointF> listPoint = editingCaption.getListPoint();
                    if (null != listPoint) {
                        //防止dst==null ,造成 EditManger->updateRectF->      if (null != showRectF) {}
                        PointF plt = listPoint.get(0), prb = listPoint.get(2);
                        int sw = vvv_special_video.getWordLayout().getWidth();
                        int sh = vvv_special_video.getWordLayout().getHeight();
                        dst = new RectF(plt.x / sw, plt.y / sh, prb.x / sw, prb.y / sh);
                    }
                    Log.e(TAG, "onScaleAndRotate: " + scale + "   dst:" + dst);
                    mHandler.removeCallbacks(mRunnable);
                    mHandler.postDelayed(mRunnable, 200);

                }
            }
        });
        cdr_special_rect.setOnCheckedListener(new CaptionDrawRect.onCheckedListener() {
            @Override
            public void onCheckItem(float x, float y) {
//                if (vvv_special_video.isPlaying()) {
//                    cdr_special_rect.setVisibility(View.VISIBLE);
//                    showRect();
//                    vvv_special_video.pause();
//                } else {
//                    vvv_special_video.start();
//                    quitEditCaptionMode();
//                }
            }
        });
    }

    /**
     * 指定的字幕退出编辑模式（移除view，字幕交由播放器核心驱动）
     */
    public void quitEditCaptionMode() {
        cdr_special_rect.setVisibility(View.GONE);
        CaptionObject editingCaption = videoEditManager.getCaptionObject();
        if (editingCaption != null && editingCaption.isEditing()) {
            Log.e(TAG, "quitEditCaptionMode: " + editingCaption);
            //退出编辑模式
            editingCaption.quitEditCaptionMode(true);
        }
    }

}
