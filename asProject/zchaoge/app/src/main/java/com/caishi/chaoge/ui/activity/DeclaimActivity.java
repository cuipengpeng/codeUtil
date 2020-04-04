package com.caishi.chaoge.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.caishi.chaoge.R;
import com.caishi.chaoge.base.BaseActivity;
import com.caishi.chaoge.bean.EventBusBean.AudioVolumeBean;
import com.caishi.chaoge.bean.LrcBean;
import com.caishi.chaoge.bean.ModuleMaterialBean;
import com.caishi.chaoge.bean.PagerItemBean;
import com.caishi.chaoge.manager.ModelManager;
import com.caishi.chaoge.manager.UploadManager;
import com.caishi.chaoge.manager.VideoEditManager;
import com.caishi.chaoge.ui.adapter.MainTabAdapter;
import com.caishi.chaoge.ui.fragment.DeclaimFragment;
import com.caishi.chaoge.ui.widget.AutoLocateHorizontalView;
import com.caishi.chaoge.ui.widget.CaptionDrawRect;
import com.caishi.chaoge.ui.widget.MoveRelativeLayout;
import com.caishi.chaoge.utils.BitmapUtils;
import com.caishi.chaoge.utils.DisplayMetricsUtil;
import com.caishi.chaoge.utils.FileUtils;
import com.caishi.chaoge.utils.LogUtil;
import com.caishi.chaoge.utils.Utils;
import com.gyf.barlibrary.ImmersionBar;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.rd.vecore.VirtualVideoView;
import com.rd.vecore.exception.InvalidArgumentException;
import com.rd.vecore.models.caption.CaptionObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.caishi.chaoge.utils.ConstantUtils.ISFIRSTLAUNCH;
import static com.caishi.chaoge.utils.ConstantUtils.IS_NEW_VERSION;

public class DeclaimActivity extends BaseActivity {

    static List<PagerItemBean> mPagerItems = new ArrayList<>();
    public DeclaimFragment fragment;
    public MoveRelativeLayout mouthPositionImageView;

    static {
        mPagerItems.add(new PagerItemBean("背景"));
        mPagerItems.add(new PagerItemBean("字效"));
        mPagerItems.add(new PagerItemBean("字体"));
        mPagerItems.add(new PagerItemBean("音乐"));
        mPagerItems.add(new PagerItemBean("音量"));
    }

    private AutoLocateHorizontalView alrv_main_tab;
    private RelativeLayout rl_main_layout;
    private ProgressBar pb_main_progress;
    private LinearLayout ll_main_layout;
    public TextView tv_main_title;
    private ImageView img_main_preview;
    private LottieAnimationView lav_main_hintPlay;
    private ImageView img_main_mine, img_main_indicate;
    public int lastPos = -1;
    private boolean isPreview = false;//是否预览
    public VirtualVideoView vvv_main_video;
    public VideoEditManager videoEditManager;
    private Button btn_main_save;
    public CaptionDrawRect mCaptionDrawRect;
    public AudioVolumeBean volume;
    private MainTabAdapter maintabAdapter;
    public String musicId;
    public String scriptId;
    public String backGroundId;
    private String personMusicPath;
    private ArrayList<LrcBean> lrcList;
    public ModelManager modelManager;

    @Override
    public void initBundle(Bundle bundle) {
        super.initBundle(bundle);
        personMusicPath = bundle.getString("personMusicPath");
        scriptId = bundle.getString("scriptId", "");
        lrcList = (ArrayList<LrcBean>) bundle.getSerializable("lrcList");

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_declaim;
    }

    @Override
    public void initView(final View view) {
//        ImmersionBar.with(this)
//                .titleBarMarginTop(R.id.ll_main_layout)
//                .transparentStatusBar().init();
        ImmersionBar.with(this).init();
        ImmersionBar.hideStatusBar(getWindow());
        mouthPositionImageView = $(R.id.iv_declaimActivity_mouthPosition);
        mouthPositionImageView.setY(DisplayMetricsUtil.getScreenHeight(this) / 2);
        alrv_main_tab = $(R.id.alrv_main_tab);
        ll_main_layout = $(R.id.ll_main_layout);
        rl_main_layout = $(R.id.rl_main_layout);
        pb_main_progress = $(R.id.pb_main_progress);
        img_main_preview = $(R.id.img_main_preview);
        img_main_mine = $(R.id.img_main_mine);
        lav_main_hintPlay = $(R.id.lav_main_hintPlay);
        img_main_indicate = $(R.id.img_main_indicate);
        tv_main_title = $(R.id.tv_main_title);
        vvv_main_video = $(R.id.vvv_main_video);
        btn_main_save = $(R.id.btn_main_save);
        mCaptionDrawRect = $(R.id.cdr_main_rect);
        //媒体宽高小于这个面积的使用强制软解
        vvv_main_video.setSWDecoderSize(3840 * 3840);

        int screenWidth = DisplayMetricsUtil.getScreenWidth(this);
        int screenHeight = DisplayMetricsUtil.getScreenHeight(this);
        vvv_main_video.setPreviewAspectRatio(screenWidth, screenWidth / screenHeight);
        //设置当OnPause后是否释放播放资源
        vvv_main_video.setReleasePlaybackOnPause(false);
        mCaptionDrawRect.initbmp();
        modelManager = ModelManager.newInstance();
        modelManager.init(personMusicPath, lrcList);
        modelManager.setOnModelInitSucceed(new ModelManager.OnModelInitSucceed() {
            @Override
            public void onSucceed(ModuleMaterialBean moduleMaterialBean) {
                videoEditManager.init(mContext, vvv_main_video, moduleMaterialBean, 0);
                fragment.updateEffectAdapter(moduleMaterialBean.specialFlag);
            }
        });
        for (int i = 0; i < 4; i++) {
            modelManager.getAeModulePath(i);
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void setListener() {
        img_main_preview.setOnClickListener(this);
        btn_main_save.setOnClickListener(this);
        img_main_mine.setOnClickListener(this);
        img_main_preview.setOnClickListener(this);
        vvv_main_video.setOnPlaybackListener(new VirtualVideoView.VideoViewListener() {
            @Override
            public void onPlayerPrepared() {
                Log.e(TAG, "onPlayerPrepared: " + this);
                fragment.updateSeekBar();
                registerDrawRectListener();
                volume = videoEditManager.getVolume();
                fragment.updateSeekBarProgress(volume);
                if (fragment.showMouthText) {
                    mouthPositionImageView.setVisibility(View.VISIBLE);
                } else {
                    mouthPositionImageView.setVisibility(View.GONE);
                }
                if (fragment.updateUI) {
                    videoEditManager.showRectF = null;
                    videoEditManager.scale = -1;
                    videoEditManager.captionPosition = -1;
                    mCaptionDrawRect.setVisibility(View.VISIBLE);
                    showRect();
                } else {
                    mCaptionDrawRect.setVisibility(View.GONE);
                }
                String title = ((int) videoEditManager.getDuration()) + "s";
                tv_main_title.setText(title);
                pb_main_progress.setMax((int) (videoEditManager.getDuration() * 1000));
            }

            @Override
            public void onPlayerCompletion() {
            }

            @Override
            public void onGetCurrentPosition(float v) {
                int progress = (int) (v * 1000);
                pb_main_progress.setProgress(progress);
            }

            @Override
            public boolean onPlayerError(int i, int i1, String s) {
                LogUtil.d(TAG, "播放错误===" + s);
                return super.onPlayerError(i, i1, s);
            }
        });

        alrv_main_tab.setOnSelectedPositionChangedListener(new AutoLocateHorizontalView.OnSelectedPositionChangedListener() {
            @Override
            public void selectedPositionChanged(int pos) {
                LogUtil.d("pos===" + pos);
                if (lastPos != pos) {
                    if (pos != 0)
                        hideHint();
                    mouthPositionImageView.setVisibility(View.GONE);
                    mCaptionDrawRect.setVisibility(View.GONE);
                    fragment.setViewVisibility(pos);
                    String title;
                    title = ((int) videoEditManager.getDuration()) + "s";
                    tv_main_title.setText(title);
                    String flag = "";
                    switch (pos) {
                        case 0://背景
                            flag = "lz0016";
                            break;
                        case 1://字效
                            flag = "lz0014";
                            break;
                        case 2://字体
                            flag = "lz0015";
                            break;
                        case 3://音乐
                            flag = "lz0017";
                            break;
                        case 4://音量
                            flag = "lz0018";
                            break;
                    }
                    Utils.umengStatistics(mContext, flag);
                }
                lastPos = pos;
            }
        });
    }

    private void upDataUi(boolean isPreview) {
        ll_main_layout.setVisibility(isPreview ? View.INVISIBLE : View.VISIBLE);
        rl_main_layout.setVisibility(isPreview ? View.INVISIBLE : View.VISIBLE);
        img_main_indicate.setVisibility(isPreview ? View.INVISIBLE : View.VISIBLE);
        pb_main_progress.setVisibility(isPreview ? View.VISIBLE : View.GONE);
        img_main_preview.setImageResource(isPreview ? R.drawable.im_suspended_click : R.drawable.im_preview);
    }

    @Override
    public void doBusiness() {
        mouthPositionImageView.setTouchCallBack(new MoveRelativeLayout.TouchCallBack() {
            @Override
            public void onTouchUp(float height) {
                for (int i = 0; i < videoEditManager.myDrawTextHandler.mLrcBeanList.size(); i++) {
                    videoEditManager.myDrawTextHandler.mLrcBeanList.get(i).setDeltaY((int) height);
                }
            }
        });
        VideoEditManager.clearVideoEditManager();
        videoEditManager = VideoEditManager.newInstance();
        fragment = DeclaimFragment.newInstance();
        switchFragment(fragment, R.id.fl_main_layout);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        alrv_main_tab.setLayoutManager(linearLayoutManager);
        maintabAdapter = new MainTabAdapter(this);
        maintabAdapter.setData(mPagerItems);
        alrv_main_tab.setItemCount(5);
        alrv_main_tab.setInitPos(0);
        alrv_main_tab.setData(mPagerItems);
        alrv_main_tab.setAdapter(maintabAdapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            XXPermissions.with(this)
                    .permission(Permission.WRITE_EXTERNAL_STORAGE)
                    .request(new OnPermission() {
                        @Override
                        public void hasPermission(List<String> granted, boolean isAll) {
                            if (isAll) {
//                                modelManager.upModel(0, null);
                            } else {
                                showToast("部分权限未正常授予");
                                XXPermissions.gotoPermissionSettings(mContext);
                            }
                        }

                        @Override
                        public void noPermission(List<String> denied, boolean quick) {
                            showToast("被永久拒绝授权，请手动授予权限");
                            XXPermissions.gotoPermissionSettings(mContext);
                        }
                    });
        } else {
//            modelManager.upModel(0, null);

        }


    }

    @Override
    public void widgetClick(View v) {
        hideHint();
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.btn_main_save:
                String snapshot = "";
                String bgPath = videoEditManager.getModuleMaterialBean().bgPath;
                if (!Utils.isEmpty(bgPath))
                    if (bgPath.contains(".mp4")) {
                        snapshot = FileUtils.saveBitmap(BitmapUtils.createVideoShot(bgPath, 1));
                    } else {
                        snapshot = bgPath;
                    }
                bundle.putString("firstSnapshot", snapshot);
                bundle.putString("musicId", musicId);
                bundle.putString("scriptId", scriptId);
                bundle.putString("backGroundId", backGroundId);
                bundle.putInt("classFlag", 0);
                Utils.umengStatistics(mContext, "lz0004"); //文字朗读
                startActivity(IssueActivity.class, bundle);
                break;

            case R.id.img_main_mine:
                onBackPressed();
            case R.id.img_main_preview:
                hideHint();
                if (isPreview) {//停止预览
                    isPreview = false;
                    vvv_main_video.pause();
                } else {//开始预览
                    quitEditCaptionMode(true);
                    isPreview = true;
                    vvv_main_video.start();
                    vvv_main_video.setAutoRepeat(true);
                }
                upDataUi(isPreview);
                if (fragment.showMouthText) {
                    mouthPositionImageView.setVisibility(isPreview ? View.GONE : View.VISIBLE);//根据是否预览来判断是否显示控制框
                } else {
                    mouthPositionImageView.setVisibility(View.GONE);
                }
                if (fragment.updateUI) {//在编辑状态
                    mCaptionDrawRect.setVisibility(isPreview ? View.GONE : View.VISIBLE);//根据是否预览来判断是否显示控制框
                    if (!isPreview)//不在预览 显示控制框
                    {
                        showRect();
                    }
                } else {
                    mCaptionDrawRect.setVisibility(View.GONE);
                }


                break;
        }

    }


    public void showRect() {
        CaptionObject editingCaption = videoEditManager.getCaptionObject();
        if (editingCaption != null) {
            if (!editingCaption.isEditing()) {
                //因为新增时：mVirtualVideo.addCaption(editingCaption);-> 已经被添加到播放器中，此刻编辑需要remove 播放器中的此对象
                editingCaption.removeCaption(); //否则界面会出现两个（一个view对象，一个播放器中的seo对象）
                vvv_main_video.refresh();
                try {
                    editingCaption.editCaptionMode();
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                }
            }
            List<PointF> listPoint =
                    editingCaption.getListPoint();
            mCaptionDrawRect.SetDrawRect(listPoint);

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
                    mCaptionDrawRect.setVisibility(View.VISIBLE);
                    showRect();
                }
                break;
                default: {
                }
                break;
            }
        }
    };


    RectF dst = null;
    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            videoEditManager.updateRectF(dst, scale);
        }
    };
    float scale = -1;

    /***
     * 编辑状态下，给控制器设置回调并显示
     */
    public void registerDrawRectListener() {
        mCaptionDrawRect.SetOnTouchListener(new CaptionDrawRect.OnTouchListener() {
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
                            int sw = vvv_main_video.getWordLayout().getWidth();
                            int sh = vvv_main_video.getWordLayout().getHeight();
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
                        int sw = vvv_main_video.getWordLayout().getWidth();
                        int sh = vvv_main_video.getWordLayout().getHeight();
                        dst = new RectF(plt.x / sw, plt.y / sh, prb.x / sw, prb.y / sh);
                    }
                    Log.e(TAG, "onScaleAndRotate: " + scale + "   dst:" + dst);
                    mHandler.removeCallbacks(mRunnable);
                    mHandler.postDelayed(mRunnable, 200);

                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCaptionDrawRect.recycle();
        if (null != vvv_main_video) {
            vvv_main_video.cleanUp();
            vvv_main_video = null;
        }
    }


    /**
     * 隐藏提示
     */
    private void hideHint() {
        lav_main_hintPlay.setVisibility(View.GONE);
    }


    /**
     * 指定的字幕退出编辑模式（移除view，字幕交由播放器核心驱动）
     */
    public void quitEditCaptionMode(boolean isSave) {
        CaptionObject editingCaption = videoEditManager.getCaptionObject();
        if (editingCaption != null && editingCaption.isEditing()) {
            Log.e(TAG, "quitEditCaptionMode: " + editingCaption);
            //退出编辑模式
            editingCaption.quitEditCaptionMode(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (vvv_main_video.isPlaying()) {
            vvv_main_video.pause();
            isPreview = false;
        }

    }

    public void showStatusBar() {
//        ImmersionBar.with(this)
//                .statusBarColor(R.color.white)
//                .init();

    }

    public void hideStatusBar() {
//        ImmersionBar.hideStatusBar(getWindow());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        upDataUi(isPreview);

    }

}
